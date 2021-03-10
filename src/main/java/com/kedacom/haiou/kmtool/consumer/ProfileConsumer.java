package com.kedacom.haiou.kmtool.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.entity.FaceCacheEntry;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessage;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessageRoot;
import com.kedacom.haiou.kmtool.service.SyncFaceToAlgorithmService;
import com.kedacom.haiou.kmtool.utils.CommonConstant;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProfileConsumer {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    HaiouRepositoryDao haiouRepositoryDao;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = "profileReceiverTopic",
            containerFactory = "thirdPartyResultConsumer.containerFactory",
            groupId = "haiou_profile_consumer_1")
    public void consumePasserbyClusterResult(List<ConsumerRecord> records) {

        try {
            List<ProfileFaceMessageRoot> profileFaceMessageRootList = records.stream().map(record -> {
                byte[] value = (byte[]) record.value();
                String result = new String(value);
                log.info("收到档案数据 [ReceiveProfileResult] Kafka下标: {}-{}@{}}",
                        record.topic(), record.partition(), record.offset());

                final ProfileFaceMessageRoot profileFaceMessageRoot = GsonUtil.GsonToBean(result, ProfileFaceMessageRoot.class);

                return profileFaceMessageRoot;

            }).filter(Objects::nonNull).collect(Collectors.toList());

            if (CollectionUtil.isEmpty(profileFaceMessageRootList)) {
                log.error("收到的档案数据转换 ProfileFaceMessage 数量为0");
                return;
            }

            for (ProfileFaceMessageRoot profileFaceMessageRoot : profileFaceMessageRootList) {
                List<ProfileFaceMessage> profileFaceMessageList = profileFaceMessageRoot.getProfileFaceMessageList();
                if (CollectionUtil.isEmpty(profileFaceMessageList)) {
                    return;
                }

                List<String> faceIdList = profileFaceMessageList.stream().map(ProfileFaceMessage::getFaceID).distinct().collect(Collectors.toList());
                List<String> vidPrefix = faceIdList.stream().map(this::wrapKey).distinct().collect(Collectors.toList());
                List<String> strings = redisTemplate.opsForValue().multiGet(vidPrefix);
                Map<String, FaceCacheEntry> cacheResult = new HashMap<>();
                if (strings != null) {
                    int i = 0;
                    for (String faceID : faceIdList) {
                        String entryString = strings.get(i++);
                        FaceCacheEntry entry = GsonUtil.GsonToBean(entryString, FaceCacheEntry.class);
                        cacheResult.put(faceID, entry);
                    }
                }

                for (int i = 1; i <= 3; i++) {
                    String algorithmRepoId = CommonConstant.ALGORITHM_REPO_MAP.get("ALGORITHM_" + String.valueOf(i + 3));
                    if (StringUtils.isEmpty(algorithmRepoId)) {
                        continue;
                    }

                    ExecutorService executorService = Executors.newWorkStealingPool(12);
                    List<Future<Boolean>> result = new ArrayList();
                    AddProfileThread thread = new AddProfileThread(profileFaceMessageList, String.valueOf(i + 3), algorithmRepoId);
                    result.add(executorService.submit(thread));

                    result.forEach(r -> {
                        try {
                            if (r.get()) {
                            }
                        } catch (Exception e) {
                            log.error("发送人脸图到Kafka失败: {}", ExceptionUtils.getStackTrace(e));
                        }
                    });
                }
            }


        } catch (Exception e) {
            log.info("消费kafka发送抓拍数据异常");
        }

        log.info("获取数据发送kafka结束");
    }

    private String wrapKey(String faceId) {
        if (StringUtils.isEmpty(faceId)) {
            return faceId;
        }
        return "V:" + faceId;
    }

    public class AddProfileThread implements Callable<Boolean> {
        private List<ProfileFaceMessage> profileFaceMessageList;
        private String algorithmId;
        private String algorithmRepoId;

        public AddProfileThread(List<ProfileFaceMessage> profileFaceMessageList, String algorithmId, String algorithmRepoId) {
            this.profileFaceMessageList = profileFaceMessageList;
            this.algorithmId = algorithmId;
            this.algorithmRepoId = algorithmRepoId;
        }

        @Override
        public Boolean call() {
            if (CollectionUtil.isEmpty(profileFaceMessageList)) {
                return false;
            }

            for (ProfileFaceMessage profileFaceMessage : profileFaceMessageList) {
                KafkaFaceMessage kafkaFaceMessage = ConvertUtil.convertPFMToKafkaFaceMessage(profileFaceMessage, algorithmRepoId);
                if (kafkaFaceMessage == null)
                    continue;
                kafkaTemplate.send("deployPic2AlgorithmProfile_" + String.valueOf(algorithmId),
                        String.valueOf(UUID.randomUUID().toString() + kafkaFaceMessage.hashCode()).getBytes(), GsonUtil.toJson(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                    @Override
                    public void onFailure(Throwable e) {
                        log.error("人员信息发送kfk失败: {}", ExceptionUtils.getStackTrace(e));
                    }

                    @Override
                    public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                        log.info("人员信息发送kafka {} 成功", "deployPic2AlgorithmProfile_" + String.valueOf(algorithmId));
                    }
                });

            }

            return true;
        }
    }
}


