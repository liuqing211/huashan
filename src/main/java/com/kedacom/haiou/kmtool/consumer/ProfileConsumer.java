package com.kedacom.haiou.kmtool.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.entity.FaceCacheEntry;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessage;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessageRoot;
import com.kedacom.haiou.kmtool.service.AddFaceTask;
import com.kedacom.haiou.kmtool.service.SyncFaceToAlgorithmService;
import com.kedacom.haiou.kmtool.utils.CommonConstant;
import com.kedacom.haiou.kmtool.utils.CommonHelper;
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

    @Autowired
    AddFaceTask addFaceTask;

    /*@KafkaListener(topics = "profileReceiverTopic",
            containerFactory = "thirdPartyResultConsumer.containerFactory",
            groupId = "haiou_profile_consumer_2")*/
    public void consumePasserbyClusterResult(List<ConsumerRecord> records) {

        try {
            List<ProfileFaceMessageRoot> profileFaceMessageRootList = records.stream().map(record -> {
                byte[] value = (byte[]) record.value();
                String result = new String(value);
                log.info("?????????????????? [ReceiveProfileResult] Kafka??????: {}-{}@{}}",
                        record.topic(), record.partition(), record.offset());

                final ProfileFaceMessageRoot profileFaceMessageRoot = GsonUtil.GsonToBean(result, ProfileFaceMessageRoot.class);

                return profileFaceMessageRoot;

            }).filter(Objects::nonNull).collect(Collectors.toList());

            if (CollectionUtil.isEmpty(profileFaceMessageRootList)) {
                log.error("??????????????????????????? ProfileFaceMessage ?????????0");
                return;
            }

            for (ProfileFaceMessageRoot profileFaceMessageRoot : profileFaceMessageRootList) {
                List<ProfileFaceMessage> profileFaceMessageList = profileFaceMessageRoot.getProfileFaceMessageList();
                if (CollectionUtil.isEmpty(profileFaceMessageList)) {
                    continue;
                }

                log.info("?????????????????????redis: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                List<String> faceIdList = profileFaceMessageList.stream().map(ProfileFaceMessage::getFaceID).distinct().collect(Collectors.toList());
                List<String> vidPrefix = faceIdList.stream().map(this::wrapKey).distinct().collect(Collectors.toList());
                List<String> strings = redisTemplate.opsForValue().multiGet(vidPrefix);

                List<String> existFaceIDList = new ArrayList<>();
                if (strings != null) {
                    int i = 0;
                    for (String faceID : faceIdList) {
                        String entryString = strings.get(i++);
                        FaceCacheEntry entry = GsonUtil.GsonToBean(entryString, FaceCacheEntry.class);
                        if (null != entry && StringUtils.isNotEmpty(entry.getPersonID())) {
                            existFaceIDList.add(faceID);
                        }
                    }
                }
                log.info("???????????????redis??????: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                for (ProfileFaceMessage profileFaceMessage : profileFaceMessageList) {
                    if (StringUtils.isEmpty(profileFaceMessage.getFaceID()) || StringUtils.isEmpty(profileFaceMessage.getIDNumber())
                            || StringUtils.isEmpty(profileFaceMessage.getStoragePath())
                            || !existFaceIDList.contains(profileFaceMessage.getFaceID())) {
                        continue;
                    }

                    String storagePath = profileFaceMessage.getStoragePath();
                    String Base64 = CommonHelper.ImageToBase64(storagePath);
                    profileFaceMessage.setBase64(Base64);

                    for (int i = 1; i <= 3; i++) {
                        KafkaFaceMessage kafkaFaceMessage = ConvertUtil.convertPFMToKafkaFaceMessage(profileFaceMessage, CommonConstant.ALGORITHM_REPO_MAP.get("ALGORITHM_" + String.valueOf(i + 3)));
                        if (kafkaFaceMessage == null) {
                            return;
                        }

                        kafkaTemplate.send("deployPic2AlgorithmProfiles_" + String.valueOf(i + 3),
                                String.valueOf(UUID.randomUUID().toString() + kafkaFaceMessage.hashCode()).getBytes(), GsonUtil.toJson(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                            @Override
                            public void onFailure(Throwable e) {
                                log.error("??????????????????kfk??????: {}", ExceptionUtils.getStackTrace(e));
                            }

                            @Override
                            public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                                // log.info("??????????????????kafka {} ??????", "deployPic2AlgorithmProfile_" + String.valueOf(finalI + 3));
                            }
                        });
                    }
                }
            }


            //addFaceTask.startAddPersonAndFace(profileFaceMessageRootList);

            /*for (ProfileFaceMessageRoot profileFaceMessageRoot : profileFaceMessageRootList) {
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

                ExecutorService executorService = Executors.newWorkStealingPool(100);
                List<Future<Boolean>> result = new ArrayList();
                AddProfileThread thread = new AddProfileThread(profileFaceMessageList, cacheResult);
                result.add(executorService.submit(thread));

                result.forEach(r -> {
                    try {
                        if (r.get()) {
                        }
                    } catch (Exception e) {
                        log.error("??????????????????Kafka??????: {}", ExceptionUtils.getStackTrace(e));
                    }
                });

            }*/


        } catch (Exception e) {
            log.info("??????kafka????????????????????????");
        }

        log.info("??????????????????kafka??????");

    }

    private String wrapKey(String faceId) {
        if (StringUtils.isEmpty(faceId)) {
            return faceId;
        }
        return "V:" + faceId;
    }

    public class AddProfileThread implements Callable<Boolean> {
        private List<ProfileFaceMessage> profileFaceMessageList;
        private Map<String, FaceCacheEntry> cacheResult;

        public AddProfileThread(List<ProfileFaceMessage> profileFaceMessageList, Map<String, FaceCacheEntry> cacheResult) {
            this.profileFaceMessageList = profileFaceMessageList;
            this.cacheResult = cacheResult;
        }

        @Override
        public Boolean call() {
            if (CollectionUtil.isEmpty(profileFaceMessageList)) {
                return false;
            }

            for (ProfileFaceMessage profileFaceMessage : profileFaceMessageList) {
                if (StringUtils.isEmpty(profileFaceMessage.getFaceID()) || StringUtils.isEmpty(profileFaceMessage.getIDNumber())
                        || StringUtils.isEmpty(profileFaceMessage.getStoragePath())
                        || !cacheResult.containsKey(profileFaceMessage.getFaceID())) {
                    continue;
                }

                for (int i = 1; i <= 3; i++) {
                    KafkaFaceMessage kafkaFaceMessage = ConvertUtil.convertPFMToKafkaFaceMessage(profileFaceMessage,
                            CommonConstant.ALGORITHM_REPO_MAP.get("ALGORITHM_" + String.valueOf(i + 3)));
                    if (kafkaFaceMessage == null)
                        continue;
                    int finalI = i;
                    kafkaTemplate.send("deployPic2AlgorithmProfile_" + String.valueOf(i + 3),
                            String.valueOf(UUID.randomUUID().toString() + kafkaFaceMessage.hashCode()).getBytes(), GsonUtil.toJson(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                        @Override
                        public void onFailure(Throwable e) {
                            log.error("??????????????????kfk??????: {}", ExceptionUtils.getStackTrace(e));
                        }

                        @Override
                        public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                            // log.info("??????????????????kafka {} ??????", "deployPic2AlgorithmProfile_" + String.valueOf(finalI + 3));
                        }
                    });
                }
            }

            return true;
        }
    }
}


