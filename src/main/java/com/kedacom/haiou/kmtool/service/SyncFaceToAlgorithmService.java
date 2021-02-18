package com.kedacom.haiou.kmtool.service;

import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.FaceListRoot;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.CommonHelper;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import com.kedacom.haiou.kmtool.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;

@Service
@Slf4j
public class SyncFaceToAlgorithmService {

    private Map<String, Object> procedureConfig = new HashMap();
    private StringSerializer serializer = new StringSerializer();
    private KafkaProducer<StringSerializer, StringSerializer> kafkaProducer = null;

    @Resource
    private HaiouRepositoryDao repositoryDao;

    @Autowired
    private ViewlibFacade viewlibFacade;

    @Qualifier("kafkaKedaTemplate")
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

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
        final List<String> algorithmIdList = Arrays.asList(algorithmIds.split(","));
        Map<String, String> algorithmRepoIds = new HashMap<>();
        algorithmIdList.forEach(algorithmId -> {
            String algorithmRepoMapping = repositoryDao.queryRepoMappingByAlgIDAndRepoId(algorithmId, repoId);
            if (StringUtils.isNotEmpty(algorithmRepoMapping)) {
                algorithmRepoIds.put(algorithmId, algorithmRepoMapping);
            }
        });

        do {
            String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
            url = String.format(url, repoId, scollID, 100, "FaceID, TabID, SubImageList.StoragePath");
            log.info("遍历视图库查询人员库 {} 中 staticface 数据请求：{}", repoId, url);

            ResponseEntity<String> pageResponse = null;

            try {
                pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
            } catch (RestClientException e) {
                log.error("查询视图库请求 {} 请求失败：{}", url, ExceptionUtils.getMessage(e));
                break;
            }

            if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                log.info("查询视图库人员库 {} 中 staticface 数据结束", repoId);
                break;
            } else if (HttpStatus.OK.equals(pageResponse.getStatusCode())) {
                FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();
                for (String algorithmId : algorithmIdList) {
                    //String algorithmRepoMapping = repositoryDao.queryRepoMappingByAlgIDAndRepoId(algorithmId, repoId);
                    if (StringUtils.isEmpty(algorithmRepoIds.get(algorithmId))) {
                        log.info("人员库 {} 在算法 {} 中无映射库", repoId, algorithmId);
                        break;
                    }

                    List<KafkaFaceMessage> kafkaFaceMessageList = new ArrayList<>();
                    faceList.forEach(Face -> {
                        if (Face != null && Face.getSubImageList() != null && !CollectionUtils.isEmpty(Face.getSubImageList().getSubImageInfoObject()) && StringUtils.isNotEmpty(Face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath()) &&
                                !Face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath().contains("10.168.4.41")) {
                            KafkaFaceMessage kafkaFaceMessage = convertFaceToKafkaMessage(Face);
                            kafkaFaceMessage.setRepoID(algorithmRepoIds.get(algorithmId));
                            if (null != kafkaFaceMessage) {
                                kafkaFaceMessageList.add(kafkaFaceMessage);
                            }
                        }
                    });

                    //addFacesToKafka(kafkaFaceMessageList, algorithmId);

                    addFaceToKafka(kafkaFaceMessageList, algorithmId);
                }

                String returnedScrollId = faceListRoot.getFaceListObject().getScollID();
                if (!"1".equals(scollID) && !scollID.equals(returnedScrollId)) {
                    log.info("游标从 {} 变为 {} ，又从头加载", scollID, returnedScrollId);
                    loadedCount = 0;
                }

                loadedCount += faceListRoot.getFaceListObject().getFaceObject().size();
                scollID = returnedScrollId;
            }


        } while (true);


        return true;
    }

    private void addFaceToKafka(List<KafkaFaceMessage> kafkaFaceMessageList, String algorithmId) {
        String topic = addFaceTopic + "_" + algorithmId;
        for (KafkaFaceMessage kafkaFaceMessage : kafkaFaceMessageList) {
            if (CollectionUtils.isEmpty(procedureConfig)) {
                procedureConfig.put("bootstrap.servers", "86.81.131.40:9092");
                kafkaProducer = new KafkaProducer(procedureConfig, serializer, serializer);
            }

            RecordMetadata recordMetadata = null;
            if (kafkaProducer != null) {
                try {
                    // 封装消息
                    ProducerRecord<StringSerializer, StringSerializer> record = new ProducerRecord(topic, kafkaFaceMessage.getImageID(), GsonUtil.GsonString(kafkaFaceMessage));
                    // 使用生产者对象发送封装好的消息（异步的）
                    Future<RecordMetadata> future = kafkaProducer.send(record);
                    // 获取发送结果（偏移量、时间戳等信息）
                    recordMetadata = future.get();
                } catch (Exception e) {
                    log.error("人员信息 {} 发送kafka {} 失败：{}", kafkaFaceMessage.getImageID(), topic, ExceptionUtils.getStackTrace(e));
                    continue;
                }

                log.info("人员信息 {} 发送kafka {} 成功，partition-{}, offset -{}", kafkaFaceMessage.getImageID(), topic, recordMetadata.partition(), recordMetadata.offset());

            }
        }

    }

    private void addFacesToKafka(List<KafkaFaceMessage> kafkaFaceMessageList, String algorithmId) {
        String topic = addFaceTopic + "_" + algorithmId;
        for (KafkaFaceMessage kafkaFaceMessage : kafkaFaceMessageList) {
            if (StringUtils.isNotEmpty(algorithmId)) {
                RecordMetadata recordMetadata = null;
                try {
                    SendResult<byte[], byte[]> sendResult = kafkaTemplate.send(topic, kafkaFaceMessage.getImageID().getBytes(), GsonUtil.GsonString(kafkaFaceMessage).getBytes()).get();
                    recordMetadata = sendResult.getRecordMetadata();
                } catch (Exception e) {
                    log.error("人员信息 {} 发送kafka {} 失败：{}", kafkaFaceMessage.toString(), topic, ExceptionUtils.getStackTrace(e));
                    continue;
                }

                log.info("人员信息 {} 发送kafka {} 成功，partition-{}, offset -{}", kafkaFaceMessage.getImageID(), topic, recordMetadata.partition(), recordMetadata.offset());
            }
        }

    }

    private KafkaFaceMessage convertFaceToKafkaMessage(Face face) {


        KafkaFaceMessage kafkaFaceMessage = new KafkaFaceMessage();
        kafkaFaceMessage.setImageID(face.getFaceID());
        kafkaFaceMessage.setImageContent(CommonHelper.ImageToBase64(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath()));
        kafkaFaceMessage.setImageFormat("image/jpg");

        return kafkaFaceMessage;
    }
}
