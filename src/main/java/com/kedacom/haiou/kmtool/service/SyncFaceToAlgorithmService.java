package com.kedacom.haiou.kmtool.service;

import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.FaceListRoot;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.entity.HaiouRepositoryMapping;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.CommonHelper;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import com.kedacom.haiou.kmtool.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestClientException;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class SyncFaceToAlgorithmService {

    @Resource
    private HaiouRepositoryDao repositoryDao;

    @Autowired
    private ViewlibFacade viewlibFacade;

    private KafkaTemplate<Object, byte[]> kafkaTemplate;

    public KafkaTemplate<Object, byte[]> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public void setKafkaTemplate(KafkaTemplate<Object, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String VIID_FACE = "/VIID/Faces";

    @Value("${viewlib.addr}")
    private String viewlibAddr;

    @Value("${topic.prefix.addFaceToAlgorithm}")
    private String addFaceTopic;

    public List<HaiouRepository> getAllBlackListRepo() {
        List<HaiouRepository> allBlackListRepo = new ArrayList<>();
        try {
            allBlackListRepo = repositoryDao.getAllBlackListRepo();
            return allBlackListRepo;
        } catch (Exception e) {
            log.error("查询 mysql 中所有黑名单人员库失败：{}", ExceptionUtils.getStackTrace(e));
        }

        return allBlackListRepo;
    }

    /**
     * 同步库中所有人员给第三方算法
     *
     * @param repoId
     * @return
     */
    public boolean syncFaceToAlgorithm(String repoId, String algorithmIds) {
        int loadedCount = 0;
        String scollID = "1";
        Map<String, String> algorithmMaps = new HashMap<>();
        final List<String> algorithmIdList = Arrays.asList(algorithmIds.split(","));
        for (String algorithmId : algorithmIdList) {
            HaiouRepositoryMapping haiouRepositoryMapping = repositoryDao.queryRepoMappingByAlgIDAndRepoId(algorithmId, repoId);
            if (null == haiouRepositoryMapping || StringUtils.isEmpty(haiouRepositoryMapping.getAlgorithmRepositoryId())) {
                log.info("人员库 {} 在算法 {} 中无映射库", repoId, algorithmId);
            } else {
                algorithmMaps.put(algorithmId, haiouRepositoryMapping.getAlgorithmRepositoryId());
            }

            if (CollectionUtils.isEmpty(algorithmMaps)) {
                log.info("人员库 {} 在所有算法中都没有映射库", algorithmId);
                continue;
            }

            do {
                String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
                url = String.format(url, repoId, scollID, 100, "FaceID, TabID, SubImageList.StoragePath");
                log.info("遍历视图库查询人员库 {} 中 staticface 数据请求：{}", repoId, url);

                ResponseEntity<String> pageResponse = null;

                try {
                    pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
                } catch (RestClientException e) {
                    log.error("查询视图库请求 {} 请求失败：{}", url, ExceptionUtils.getMessage(e));
                    continue;
                }

                if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                    log.info("查询视图库人员库 {} 中 staticface 数据结束", repoId);
                    break;
                } else if (HttpStatus.OK.equals(pageResponse.getStatusCode())) {
                    FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                    List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();

                    List<KafkaFaceMessage> kafkaFaceMessageList = new ArrayList<>();
                    faceList.forEach(Face -> {
                        KafkaFaceMessage kafkaFaceMessage = convertFaceToKafkaMessage(Face);
                        kafkaFaceMessage.setAlgorithmRepoMapping(algorithmMaps);
                        if (null != kafkaFaceMessage) {
                            kafkaFaceMessageList.add(kafkaFaceMessage);
                        }
                    });

                    addFacesToKafka(kafkaFaceMessageList);

                    String returnedScrollId = faceListRoot.getFaceListObject().getScollID();
                    if (!"1".equals(scollID) && !scollID.equals(returnedScrollId)) {
                        log.info("游标从 {} 变为 {} ，又从头加载", scollID, returnedScrollId);
                        loadedCount = 0;
                    }

                    loadedCount += faceListRoot.getFaceListObject().getFaceObject().size();
                    scollID = returnedScrollId;
                }


            } while (true);


        }

        return true;
    }

    private void addFacesToKafka(List<KafkaFaceMessage> kafkaFaceMessageList) {
        for (KafkaFaceMessage kafkaFaceMessage : kafkaFaceMessageList) {
            Map<String, String> algorithmRepoMapping = kafkaFaceMessage.getAlgorithmRepoMapping();
            for (Map.Entry<String, String> entry : algorithmRepoMapping.entrySet()) {
                final String algorithmId = entry.getKey();
                final String algprithmRepoId = entry.getValue();
                if (StringUtils.isNotEmpty(algprithmRepoId)) {
                    kafkaFaceMessage.setRepoID(algprithmRepoId);
                }

                if (StringUtils.isNotEmpty(algorithmId)) {
                    String topic = addFaceTopic + "_" + algorithmId;
                    ListenableFuture<SendResult<Object, byte[]>> future = kafkaTemplate.send(topic, kafkaFaceMessage.getImageId().getBytes(), GsonUtil.GsonString(kafkaFaceMessage).getBytes());
                    RecordMetadata recordMetadata = null;
                    try {
                        recordMetadata = future.get().getRecordMetadata();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("人员信息 {} 发送kafka {} 失败：{}", kafkaFaceMessage.toString(), topic, ExceptionUtils.getStackTrace(e));
                        continue;
                    }

                    log.info("人员信息 {} 发送kafka {} 成功，分片-{}，下标-{}", kafkaFaceMessage.toString(), topic, recordMetadata.partition(), recordMetadata.offset());
                }
            }
        }
    }

    private KafkaFaceMessage convertFaceToKafkaMessage(Face face) {

        KafkaFaceMessage kafkaFaceMessage = new KafkaFaceMessage();
        kafkaFaceMessage.setImageId(face.getFaceID());
        kafkaFaceMessage.setImageContent(CommonHelper.ImageToBase64(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath()));
        kafkaFaceMessage.setImageFormat("image/jpg");

        return kafkaFaceMessage;
    }
}
