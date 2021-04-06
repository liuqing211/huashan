package com.kedacom.haiou.kmtool.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.entity.FaceCacheEntry;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessage;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessageRoot;
import com.kedacom.haiou.kmtool.utils.CommonConstant;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AddFaceTask {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    KafkaTemplate kafkaTemplate;

    ExecutorService executorService = null;

    public void startAddPersonAndFace(List<ProfileFaceMessageRoot> profileFaceMessageRootList) {
        executorService = Executors.newFixedThreadPool(3);
        if (executorService != null) {
            executorService.execute(new AddPersonAndFaceThread(profileFaceMessageRootList));
        }
    }

    public void startAddPersonAndFace(ProfileFaceMessage profileFaceMessage) {
        executorService = Executors.newFixedThreadPool(3);
        if (executorService != null) {
            executorService.execute(new AddProfileThread(profileFaceMessage));
        }
    }

    public class AddProfileThread implements Runnable {

        private ProfileFaceMessage profileFaceMessage;

        AddProfileThread(ProfileFaceMessage profileFaceMessage) {
            this.profileFaceMessage = profileFaceMessage;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= 3; i++) {
                    KafkaFaceMessage kafkaFaceMessage = ConvertUtil.convertPFMToKafkaFaceMessage(profileFaceMessage, CommonConstant.ALGORITHM_REPO_MAP.get("ALGORITHM_" + String.valueOf(i + 3)));
                    if (kafkaFaceMessage == null){
                        return;
                    }

                    kafkaTemplate.send("deployPic2AlgorithmProfiles_" + String.valueOf(i + 3),
                            String.valueOf(UUID.randomUUID().toString() + kafkaFaceMessage.hashCode()).getBytes(), GsonUtil.toJson(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                        @Override
                        public void onFailure(Throwable e) {
                            log.error("人员信息发送kfk失败: {}", ExceptionUtils.getStackTrace(e));
                        }

                        @Override
                        public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                            // log.info("人员信息发送kafka {} 成功", "deployPic2AlgorithmProfile_" + String.valueOf(finalI + 3));
                        }
                    });
                }
            } catch (Exception e) {
                log.error("档案下发算法kafka异常:{}", ExceptionUtils.getStackTrace(e));
            }

        }

        private String wrapKey(String faceId) {
            if (StringUtils.isEmpty(faceId)) {
                return faceId;
            }
            return "V:" + faceId;
        }


    }

    public class AddPersonAndFaceThread implements Runnable {

        private List<ProfileFaceMessageRoot> profileFaceMessageRootList;

        AddPersonAndFaceThread(List<ProfileFaceMessageRoot> profileFaceMessageRootList) {
            this.profileFaceMessageRootList = profileFaceMessageRootList;
        }

        @Override
        public void run() {
            try {
                for (ProfileFaceMessageRoot profileFaceMessageRoot : profileFaceMessageRootList) {
                    log.info("开始处理数据: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    if (null == profileFaceMessageRoot || CollectionUtil.isEmpty(profileFaceMessageRoot.getProfileFaceMessageList())) {
                        continue;
                    }

                    log.info("开始查询数据的redis: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    List<ProfileFaceMessage> profileFaceMessageList = profileFaceMessageRoot.getProfileFaceMessageList();
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
                    log.info("查询数据的redis结束: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                    log.info("开始过滤数据: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    List<ProfileFaceMessage> finalPfcList = profileFaceMessageList.stream().filter(e -> StringUtils.isNotEmpty(e.getFaceID()) &&
                            StringUtils.isNotEmpty(e.getIDNumber()) && StringUtils.isNotEmpty(e.getStoragePath()) &&
                            StringUtils.isNotEmpty(e.getRelativeID()) && cacheResult.containsKey(e.getFaceID())).collect(Collectors.toList());
                    log.info("过滤数据结束: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                    log.info("开始发送数据: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                    for (ProfileFaceMessage profileFaceMessage : finalPfcList) {
                        KafkaFaceMessage kafkaFaceMessage = ConvertUtil.convertPFMToKafkaFaceMessage(profileFaceMessage, CommonConstant.ALGORITHM_REPO_MAP.get("ALGORITHM_5"));
                        if (kafkaFaceMessage == null)
                            continue;
                        kafkaTemplate.send("deployPic2AlgorithmProfiles_5",
                                String.valueOf(UUID.randomUUID().toString() + kafkaFaceMessage.hashCode()).getBytes(), GsonUtil.toJson(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                            @Override
                            public void onFailure(Throwable e) {
                                log.error("人员信息发送kfk失败: {}", ExceptionUtils.getStackTrace(e));
                            }

                            @Override
                            public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                                // log.info("人员信息发送kafka {} 成功", "deployPic2AlgorithmProfile_" + String.valueOf(finalI + 3));
                            }
                        });
                    }
                    //ExecutorService executorService = Executors.newFixedThreadPool(3);
                    /*if (executorService != null) {
                        AddFacesThread addFacesThread = new AddFacesThread(finalPfcList, "5", CommonConstant.ALGORITHM_REPO_MAP.get("ALGORITHM_5"));
                        executorService.submit(addFacesThread);
                    }*/
                    log.info("开始发送数据结束: {}-{}", String.valueOf(profileFaceMessageRoot.hashCode()), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                }
            } catch (Exception e) {
                log.error("档案下发算法kafka异常:{}", ExceptionUtils.getStackTrace(e));
            }

        }

        private String wrapKey(String faceId) {
            if (StringUtils.isEmpty(faceId)) {
                return faceId;
            }
            return "V:" + faceId;
        }


    }

    public class AddFacesThread implements Runnable {
        private List<ProfileFaceMessage> finalPfcList;
        private String algorithmId;
        private String algorithmRepoId;

        public AddFacesThread(List<ProfileFaceMessage> finalPfcList, String algorithmId, String algorithmRepoId) {
            this.finalPfcList = finalPfcList;
            this.algorithmId = algorithmId;
            this.algorithmRepoId = algorithmRepoId;
        }

        @Override
        public synchronized void run() {
            try {
                for (ProfileFaceMessage profileFaceMessage : finalPfcList) {
                    KafkaFaceMessage kafkaFaceMessage = ConvertUtil.convertPFMToKafkaFaceMessage(profileFaceMessage, algorithmRepoId);
                    if (kafkaFaceMessage == null)
                        continue;
                    kafkaTemplate.send("deployPic2AlgorithmProfiles_" + algorithmId,
                            String.valueOf(UUID.randomUUID().toString() + kafkaFaceMessage.hashCode()).getBytes(), GsonUtil.toJson(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                        @Override
                        public void onFailure(Throwable e) {
                            log.error("人员信息发送kfk失败: {}", ExceptionUtils.getStackTrace(e));
                        }

                        @Override
                        public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                            // log.info("人员信息发送kafka {} 成功", "deployPic2AlgorithmProfile_" + String.valueOf(finalI + 3));
                        }
                    });
                }

            } catch (Exception e) {
                log.error("第三方算法入库异常,信息: {}" + ExceptionUtils.getStackTrace(e));
            }
        }
    }


}
