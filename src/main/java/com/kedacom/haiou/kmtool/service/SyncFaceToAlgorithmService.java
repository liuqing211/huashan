package com.kedacom.haiou.kmtool.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.SendProfileInfoLog;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.FaceListRoot;
import com.kedacom.haiou.kmtool.dto.viid.Person;
import com.kedacom.haiou.kmtool.entity.FaceCacheEntry;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestClientException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Value("${es.host}")
    private String esHost;

    @Value("${es.protocol}")
    private String esProtocol;

    // @Qualifier("kafkaKedaTemplate")
    @Autowired
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Autowired
    private RestHighLevelClient esClient;

    private static final String VIID_FACE = "/VIID/Faces";

    @Value("${viewlib.addr}")
    private String viewlibAddr;

    @Value("${topic.prefix.addFaceToAlgorithm}")
    private String addFaceTopic;

    @Autowired
    private CommonService commonService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    HaiouRepositoryDao haiouRepositoryDao;

    @Bean
    RestHighLevelClient esClient() {
        List<HttpHost> hosts = Stream.of(esHost.split(",")).map(p -> {
            String[] hostPort = p.split(":");
            String host = hostPort[0];
            int port = 39200;
            if (hostPort.length >= 2) {
                port = Integer.parseInt(hostPort[1]);
            }
            return new HttpHost(host, port, esProtocol);
        }).collect(Collectors.toList());

        RestClient restClient = RestClient.builder(hosts.toArray(new HttpHost[]{})).build();

        RestHighLevelClient client = new RestHighLevelClient(restClient);

        return client;
    }

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
                commonService.waitAMoment();
                break;
            }

            if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                log.info("查询视图库人员库 {} 中 staticface 数据结束", repoId);
                break;
            } else if (HttpStatus.OK.equals(pageResponse.getStatusCode())) {
                FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();
                for (String algorithmId : algorithmIdList) {
                    if (StringUtils.isEmpty(algorithmRepoIds.get(algorithmId))) {
                        log.info("人员库 {} 在算法 {} 中无映射库", repoId, algorithmId);
                        break;
                    }

                    List<KafkaFaceMessage> kafkaFaceMessageList = new ArrayList<>();
                    faceList.forEach(Face -> {
                        if (Face != null && Face.getSubImageList() != null && !CollectionUtils.isEmpty(Face.getSubImageList().getSubImageInfoObject()) && StringUtils.isNotEmpty(Face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath()) &&
                                !Face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath().contains("10.168.4.41")) {
                            KafkaFaceMessage kafkaFaceMessage = null;
                            try {
                                kafkaFaceMessage = ConvertUtil.convertFaceToKafkaMessage(Face);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void addFacesToKafka(Face face, String algorithmId, String algRepoId, BulkRequest bulkRequest) {
        String topic = addFaceTopic + "_" + algorithmId;

        KafkaFaceMessage kafkaFaceMessage = null;
        try {
            kafkaFaceMessage = ConvertUtil.convertFaceToKafkaMessage(face, algRepoId);
        } catch (Exception e) {
            log.info("转换 KafkaFaceMessage 对象异常: {}", ExceptionUtils.getStackTrace(e));
        }

        if (kafkaFaceMessage != null && StringUtils.isNotEmpty(algorithmId)) {

            Map logParam = new HashMap();
            logParam.put("FaceObject", face);
            logParam.put("Id", (StringUtils.isEmpty(face.getFaceID()) ? String.valueOf(face.hashCode()) :
                    face.getFaceID()) + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
            logParam.put("AlgorithmId", algorithmId);
            logParam.put("SendTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            KafkaFaceMessage finalKafkaFaceMessage = kafkaFaceMessage;
            kafkaTemplate.send(topic, kafkaFaceMessage.getImageID().getBytes(), GsonUtil.GsonString(kafkaFaceMessage).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                @Override
                public void onFailure(Throwable e) {
                    log.error("人员信息 {} 发送kafka {} 失败：{}", finalKafkaFaceMessage.toString(), topic, ExceptionUtils.getStackTrace(e));
                    logParam.put("Status", "failed");
                    batchSaveSendProfileInfoLog(logParam, bulkRequest);
                }

                @Override
                public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                    log.info("人员信息 {} 发送kafka {} 成功", finalKafkaFaceMessage.getImageID(),topic);
                }
            });

//            RecordMetadata recordMetadata = null;
//            try {
//                SendResult<byte[], byte[]> sendResult = kafkaTemplate.send(topic, kafkaFaceMessage.getImageID().getBytes(), GsonUtil.GsonString(kafkaFaceMessage).getBytes()).get();
//                recordMetadata = sendResult.getRecordMetadata();
//            } catch (Exception e) {
//                log.error("人员信息 {} 发送kafka {} 失败：{}", kafkaFaceMessage.toString(), topic, ExceptionUtils.getStackTrace(e));
//                logParam.put("Status", "failed");
//                batchSaveSendProfileInfoLog(logParam, bulkRequest);
//                return;
//            }

//            logParam.put("Status", "success");
//            batchSaveSendProfileInfoLog(logParam, bulkRequest);
//            log.info("人员信息 {} 发送kafka {} 成功，partition-{}, offset -{}", kafkaFaceMessage.getImageID(), topic, recordMetadata.partition(), recordMetadata.offset());

        }

    }

    private void addFacesToKafka(List<Face> faceList, String algorithmId) {
        String topic = addFaceTopic + "_" + algorithmId;
        for (Face face : faceList) {

            KafkaFaceMessage kafkaFaceMessage = null;
            try {
                kafkaFaceMessage = ConvertUtil.convertFaceToKafkaMessage(face);
            } catch (Exception e) {
                log.info("转换 KafkaFaceMessage 对象异常: {}", ExceptionUtils.getStackTrace(e));
            }

            if (kafkaFaceMessage != null && StringUtils.isNotEmpty(algorithmId)) {

                Map logParam = new HashMap();
                logParam.put("FaceObject", face);
                logParam.put("Id", (StringUtils.isEmpty(face.getFaceID()) ? String.valueOf(face.hashCode()) :
                        face.getFaceID()) + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                logParam.put("AlgorithmId", algorithmId);
                logParam.put("SendTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                RecordMetadata recordMetadata = null;
                try {
                    SendResult<byte[], byte[]> sendResult = kafkaTemplate.send(topic, kafkaFaceMessage.getImageID().getBytes(), GsonUtil.GsonString(kafkaFaceMessage).getBytes()).get();
                    recordMetadata = sendResult.getRecordMetadata();
                } catch (Exception e) {
                    log.error("人员信息 {} 发送kafka {} 失败：{}", kafkaFaceMessage.toString(), topic, ExceptionUtils.getStackTrace(e));
                    logParam.put("Status", "failed");
                    saveSendProfileInfoLog(logParam);
                    continue;
                }

                logParam.put("Status", "success");
                saveSendProfileInfoLog(logParam);
                log.info("人员信息 {} 发送kafka {} 成功，partition-{}, offset -{}", kafkaFaceMessage.getImageID(), topic, recordMetadata.partition(), recordMetadata.offset());
            }
        }

    }

    /**
     * 下发常驻人口库
     *
     * @param tabId
     * @param algorithmIds
     */
    public void syncResidentToAlgorithm(String tabId, String algorithmIds) {
        int loadedCount = 0;
        String scollID = "1";
        final List<String> algorithmIdList = Arrays.asList(algorithmIds.split(","));

        do {
            String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
            url = String.format(url, tabId, scollID, 1000, "FaceID, IDNumber, Name, RelativeID, TabID, SubImageList.StoragePath");
            log.info("遍历视图库查询人员库 {} 中 staticface 数据请求：{}", tabId, url);

            ResponseEntity<String> pageResponse = null;

            try {
                pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
            } catch (RestClientException e) {
                log.error("查询视图库请求 {} 请求失败：{}", url, ExceptionUtils.getMessage(e));
                break;
            }

            if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                log.info("查询视图库人员库 {} 中 staticface 数据结束", tabId);
                break;
            } else if (HttpStatus.OK.equals(pageResponse.getStatusCode())) {
                FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();

                List<Face> sendFaceList = new ArrayList<>();
                for (Face face : faceList) {

                    Map logParam = new HashMap();
                    logParam.put("FaceObject", face);
                    logParam.put("Id", (StringUtils.isEmpty(face.getFaceID()) ? String.valueOf(face.hashCode()) :
                            face.getFaceID()) + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                    logParam.put("AlgorithmId", "");
                    logParam.put("Status", "skip");
                    logParam.put("SendTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                    if (face == null) {
                        log.warn("查询出来的 Face 对象为空");
                        continue;
                    }

                    if (StringUtils.isEmpty(face.getFaceID())) {
                        log.warn("查询出来的 Face 对象 FaceID 为空，FaceID: {}", face.getFaceID());
                        logParam.put("Reason", "查询出来的 Face 对象 FaceID 为空");
                        saveSendProfileInfoLog(logParam);
                        continue;
                    }

                    if (StringUtils.isEmpty(face.getRelativeID())) {
                        log.warn("查询出来的 Face 对象 RelativeID 为空，FaceID: {}", face.getFaceID());
                        logParam.put("Reason", "查询出来的 Face 对象 RelativeID 为空");
                        saveSendProfileInfoLog(logParam);
                        continue;
                    }

                    FaceCacheEntry faceCacheEntry = new FaceCacheEntry();

                    try {
                        String faceCacheValue = redisTemplate.opsForValue().get("V:" + face.getFaceID());
                        faceCacheEntry = GsonUtil.GsonToBean(faceCacheValue, FaceCacheEntry.class);
                        log.info("JSONString: {}", faceCacheValue);
                    } catch (Exception e) {
                        log.error("查询redis缓存异常: {}", ExceptionUtils.getStackTrace(e));
                    }
                    if (null == faceCacheEntry) {
                        log.warn("查询出来的 Face 对象 RelativeID 在 缓存 中不存在，FaceID: {}, RelativeID：{}", face.getFaceID(), face.getRelativeID());
                        logParam.put("Reason", "查询出来的 Face 对象 RelativeID 在 Person 中不存在");
                        saveSendProfileInfoLog(logParam);
                        continue;
                    }

                    if (StringUtils.isEmpty(face.getIDNumber())) {
                        log.warn("查询出来的 Face 对象 IDNumber 为空，FaceID: {}", face.getFaceID());
                        logParam.put("Reason", "查询出来的 Face 对象 IDNumber 为空");
                        saveSendProfileInfoLog(logParam);
                        continue;
                    }

                    if (StringUtils.isEmpty(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath())) {
                        log.warn("查询出来的 Face 对象 StoragePath 为空，FaceID: {}", face.getFaceID());
                        logParam.put("Reason", "查询出来的 Face 对象 StoragePath 为空");
                        saveSendProfileInfoLog(logParam);
                        continue;
                    }

                    sendFaceList.add(face);

                }


                if (!CollectionUtils.isEmpty(sendFaceList)) {
                    List<List<Face>> faceMessages = dividFaceMessages(faceList, 100);
                    ExecutorService executorService = Executors.newWorkStealingPool();
                    List<Future<Boolean>> result = new ArrayList();
                    faceMessages.forEach(faceMessageGroup -> {
                        // 一批人脸图
                        AddFaceToKafkaThread thread = new AddFaceToKafkaThread(faceMessageGroup, algorithmIdList);
                        result.add(executorService.submit(thread));
                    });

                    result.forEach(r -> {
                        try {
                            if (r.get()) {
                                log.info("线程完成工作");
                            }
                        } catch (Exception e) {
                            log.error("发送人脸图到Kafka失败");
                        }
                    });
                    executorService.shutdown();
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

        log.info("人员库 {} 同步算法完成", tabId);
        repositoryDao.updateSyncStatus(tabId);


    }


    /**
     * 以新协议对接，交互以平台库ID为准
     *
     * @param repoId
     * @param algorithmIds
     */
    public void syncValidFaceToAlgorithm(String repoId, String algorithmIds) {

        int loadedCount = 0;
        String scollID = "1";
        final List<String> algorithmIdList = Arrays.asList(algorithmIds.split(","));

        do {
            String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
            url = String.format(url, repoId, scollID, 1000, "FaceID, IDNumber, Name, RelativeID, TabID, SubImageList.StoragePath");
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
                Set<String> relativeIdList = faceList.stream().distinct().map(Face::getRelativeID).collect(Collectors.toSet());

                String param = "?Person.TabID like .*&Person.PersonID in (%s)&Fields=(%s)&PageRecordNum=%s";
                param = String.format(param, String.join(",", relativeIdList), "PersonID, IDNumber, Name", relativeIdList.size() * 10);
                List<Person> existPersonList = viewlibFacade.getPersons(param);

                List<Face> sendFaceList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(existPersonList)) {
                    Set<String> existPersonIDSet = existPersonList.stream().distinct().map(Person::getPersonID).collect(Collectors.toSet());
                    for (Face face : faceList) {
                        if (face == null) {
                            log.warn("查询出来的 Face 对象为空");
                            continue;
                        }

                        Map logParam = new HashMap();
                        logParam.put("FaceObject", face);
                        logParam.put("Id", (StringUtils.isEmpty(face.getFaceID()) ? String.valueOf(face.hashCode()) :
                                face.getFaceID()) + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                        logParam.put("AlgorithmId", "");
                        logParam.put("Status", "skip");
                        logParam.put("SendTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

                        if (StringUtils.isEmpty(face.getFaceID())) {
                            log.warn("查询出来的 Face 对象 FaceID 为空，FaceID: {}", face.getFaceID());
                            logParam.put("Reason", "查询出来的 Face 对象 FaceID 为空");
                            saveSendProfileInfoLog(logParam);
                            continue;
                        }

                        if (StringUtils.isEmpty(face.getRelativeID())) {
                            log.warn("查询出来的 Face 对象 RelativeID 为空，FaceID: {}", face.getFaceID());
                            logParam.put("Reason", "查询出来的 Face 对象 RelativeID 为空");
                            saveSendProfileInfoLog(logParam);
                            continue;
                        }

                        if (!existPersonIDSet.contains(face.getRelativeID())) {
                            log.warn("查询出来的 Face 对象 RelativeID 在 Person 中不存在，FaceID: {}, RelativeID：{}", face.getFaceID(), face.getRelativeID());
                            logParam.put("Reason", "查询出来的 Face 对象 RelativeID 在 Person 中不存在");
                            saveSendProfileInfoLog(logParam);
                            continue;
                        }

                        if (StringUtils.isEmpty(face.getIDNumber())) {
                            log.warn("查询出来的 Face 对象 IDNumber 为空，FaceID: {}", face.getFaceID());
                            logParam.put("Reason", "查询出来的 Face 对象 IDNumber 为空");
                            saveSendProfileInfoLog(logParam);
                            continue;
                        }

                        if (StringUtils.isEmpty(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath())) {
                            log.warn("查询出来的 Face 对象 StoragePath 为空，FaceID: {}", face.getFaceID());
                            logParam.put("Reason", "查询出来的 Face 对象 StoragePath 为空");
                            saveSendProfileInfoLog(logParam);
                            continue;
                        }

                        sendFaceList.add(face);

                    }

                    if (!CollectionUtils.isEmpty(sendFaceList)) {
                        List<List<Face>> faceMessages = dividFaceMessages(faceList, 100);
                        ExecutorService executorService = Executors.newWorkStealingPool();
                        List<Future<Boolean>> result = new ArrayList();
                        faceMessages.forEach(faceMessageGroup -> {
                            // 一批人脸图
                            AddFaceToKafkaThread thread = new AddFaceToKafkaThread(faceMessageGroup, algorithmIdList);
                            result.add(executorService.submit(thread));
                        });

                        result.forEach(r -> {
                            try {
                                if (r.get()) {
                                    log.info("线程完成工作");
                                }
                            } catch (Exception e) {
                                log.error("发送人脸图到Kafka失败");
                            }
                        });
                        executorService.shutdown();
                    }

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
    }

    private void saveSendProfileInfoLog(Map logParam) {
        SendProfileInfoLog sendProfileInfoLog = null;
        try {
            sendProfileInfoLog = ConvertUtil.convertFaceToSendProfileInfoLog(logParam);
        } catch (Exception e) {
            log.error("转换 SendProfileInfoLog 异常: {}", ExceptionUtils.getStackTrace(e));
        }
        if (null == sendProfileInfoLog) return;

        final BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        final IndexRequest index = new IndexRequest(
                "a_haiou_profile_send_log",
                "a_haiou_profile_send_log",
                sendProfileInfoLog.getId()
        );
        index.source(GsonUtil.GsonString(sendProfileInfoLog));
        bulkRequest.add(index);


        do {
            try {
                final BulkResponse bulkResponse = esClient.bulk(bulkRequest);
                if (bulkResponse.hasFailures()) {
                    final BulkItemResponse[] items = bulkResponse.getItems();
                    for (int i = 0; i < items.length; i++) {
                        if (items[i].isFailed()) {
                            log.error("保存档案发送日志失败{}，FaceID: {}", items[i].getFailureMessage(), sendProfileInfoLog.getFaceID());
                        }
                    }
                }
                break;
            } catch (Exception e) {
                log.error("保存档案发送日志异常{}，FaceID: {}", ExceptionUtils.getStackTrace(e), sendProfileInfoLog.getFaceID());
                commonService.waitAMoment();
            }
        } while (true);


    }

    private void batchSaveSendProfileInfoLog(Map logParam, BulkRequest bulkRequest) {
        SendProfileInfoLog sendProfileInfoLog = null;
        try {
            sendProfileInfoLog = ConvertUtil.convertFaceToSendProfileInfoLog(logParam);
        } catch (Exception e) {
            log.error("转换 SendProfileInfoLog 异常: {}", ExceptionUtils.getStackTrace(e));
        }
        if (null == sendProfileInfoLog) return;


        final IndexRequest index = new IndexRequest(
                "a_haiou_profile_send_log",
                "a_haiou_profile_send_log",
                sendProfileInfoLog.getId()
        );
        index.source(GsonUtil.GsonString(sendProfileInfoLog));
        bulkRequest.add(index);


    }


    public List<List<KafkaFaceMessage>> divideKafkaFaceMessages(List<KafkaFaceMessage> kafkaFaceMessageList, int batchSize) {
        List<List<KafkaFaceMessage>> params = new ArrayList<>();

        int n = (kafkaFaceMessageList.size() % batchSize == 0) ? (kafkaFaceMessageList.size() / batchSize) : (kafkaFaceMessageList.size() / batchSize) + 1;
        for (int i = 0; i < n; i++) {
            List<KafkaFaceMessage> temp = new ArrayList<>();
            for (int j = i * batchSize; j < (i + 1) * batchSize; j++) {
                if (j < kafkaFaceMessageList.size()) {
                    temp.add(kafkaFaceMessageList.get(j));
                }
            }
            params.add(temp);
        }

        return params;
    }

    public List<List<Face>> dividFaceMessages(List<Face> faceList, int batchSize) {
        List<List<Face>> params = new ArrayList<>();

        int n = (faceList.size() % batchSize == 0) ? (faceList.size() / batchSize) : (faceList.size() / batchSize) + 1;
        for (int i = 0; i < n; i++) {
            List<Face> temp = new ArrayList<>();
            for (int j = i * batchSize; j < (i + 1) * batchSize; j++) {
                if (j < faceList.size()) {
                    temp.add(faceList.get(j));
                }
            }
            params.add(temp);
        }

        return params;
    }

    public void MultiPushResidentToAlgorithm(String tabId, String algorithmIds) {

        final BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        Map<String, String> algRepoMap= new HashMap<>();
        for (String algorithmId : algorithmIds.split(",")) {
            String algoRepo = haiouRepositoryDao.queryRepoMappingByAlgIDAndRepoId(algorithmId, tabId);
            if (StringUtils.isNotEmpty(algoRepo)) {
                algRepoMap.put(algorithmId, algoRepo);
            }
        }

        if (CollectionUtil.isEmpty(algRepoMap)) {
            log.info("人员库 {} 在三家算法中都无映射", tabId);
            return;
        }

        int loadedCount = 0;
        String scollID = "1";
        do {
            String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s) and Sort=EntryTime";
            url = String.format(url, tabId, scollID, 2000, "FaceID, IDNumber, Name, RelativeID, TabID, SubImageList.StoragePath");
            log.info("遍历视图库查询人员库 {} 中 staticface 数据请求：{}", tabId, url);

            ResponseEntity<String> pageResponse = null;

            try {
                pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
            } catch (RestClientException e) {
                log.error("查询视图库请求 {} 请求失败：{}", url, ExceptionUtils.getMessage(e));
                break;
            }

            if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                log.info("查询视图库人员库 {} 中 staticface 数据结束", tabId);
                break;
            } else if (HttpStatus.OK.equals(pageResponse.getStatusCode())) {
                FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();
                List<String> faceIdList = faceList.stream().distinct().map(Face::getFaceID).collect(Collectors.toList());
                List<String> vidPrefix = faceIdList.stream().map(this::wrapKey).collect(Collectors.toList());

                List<String> strings = redisTemplate.opsForValue().multiGet(vidPrefix);
                Map<String, FaceCacheEntry> cacheResult = new HashMap<>();
                if(strings != null){
                    int i = 0;
                    for (String faceID : faceIdList) {
                        String entryString = strings.get(i++);
                        FaceCacheEntry entry = GsonUtil.GsonToBean(entryString, FaceCacheEntry.class);
                        cacheResult.put(faceID, entry);
                    }
                }

                ExecutorService executorService = Executors.newWorkStealingPool(100);
                List<Future<Boolean>> result = new ArrayList();
                for (Face face : faceList) {
                    if (cacheResult.containsKey(face.getFaceID())) {
                        MultiAddFaceToKafkaThread thread = new MultiAddFaceToKafkaThread(face, algRepoMap, bulkRequest);
                        result.add(executorService.submit(thread));
                    }
                }

                result.forEach(r -> {
                    try {
                        if (r.get()) {
                        }
                    } catch (Exception e) {
                        log.error("发送人脸图到Kafka失败");
                    }
                });
                executorService.shutdown();

                if (bulkRequest.numberOfActions() > 0) {
                    do {
                        try {
                            final BulkResponse bulkResponse = esClient.bulk(bulkRequest);
                            if (bulkResponse.hasFailures()) {
                                final BulkItemResponse[] items = bulkResponse.getItems();
                                for (int i = 0; i < items.length; i++) {
                                    if (items[i].isFailed()) {
                                        log.error("保存档案发送日志失败{}", items[i].getFailureMessage());
                                    }
                                }
                            }
                            break;
                        } catch (Exception e) {
                            log.error("保存档案发送日志异常{}", ExceptionUtils.getStackTrace(e));
                            commonService.waitAMoment();
                        }
                    } while (true);
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

    }

    private String wrapKey(String faceId){
        if(StringUtils.isEmpty(faceId)){
            return faceId;
        }
        return "V:" + faceId;
    }


    public class AddFaceToKafkaThread implements Callable<Boolean> {
        private List<Face> faceList;
        private List<String> algorithmIdList;

        public AddFaceToKafkaThread(List<Face> faceList, List<String> algorithmIdList) {
            this.faceList = faceList;
            this.algorithmIdList = algorithmIdList;
        }

        @Override
        public Boolean call() {
            algorithmIdList.forEach(algorithmId -> {
                addFacesToKafka(faceList, algorithmId);
            });

            return true;
        }
    }

    public class MultiAddFaceToKafkaThread implements Callable<Boolean> {
        private Face face;
        private Map<String, String> algRepoMap;
        private BulkRequest bulkRequest;

        public MultiAddFaceToKafkaThread(Face face, Map<String, String> algRepoMap, BulkRequest bulkRequest) {
            this.face = face;
            this.algRepoMap = algRepoMap;
            this.bulkRequest = bulkRequest;
        }

        @Override
        public Boolean call() {
            if (face == null) {
                log.warn("查询出来的 Face 对象为空");
                return false;
            }

            Map logParam = new HashMap();
            logParam.put("FaceObject", face);
            logParam.put("Id", (StringUtils.isEmpty(face.getFaceID()) ? String.valueOf(face.hashCode()) :
                    face.getFaceID()) + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
            logParam.put("AlgorithmId", "");
            logParam.put("Status", "skip");
            logParam.put("SendTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            if (StringUtils.isEmpty(face.getFaceID())) {
                log.warn("查询出来的 Face 对象 FaceID 为空，FaceID: {}", face.getFaceID());
                logParam.put("Reason", "查询出来的 Face 对象 FaceID 为空");
                batchSaveSendProfileInfoLog(logParam, bulkRequest);
                return false;
            }

            if (StringUtils.isEmpty(face.getRelativeID())) {
                log.warn("查询出来的 Face 对象 RelativeID 为空，FaceID: {}", face.getFaceID());
                logParam.put("Reason", "查询出来的 Face 对象 RelativeID 为空");
                batchSaveSendProfileInfoLog(logParam, bulkRequest);
                return false;
            }

            if (StringUtils.isEmpty(face.getIDNumber())) {
                log.warn("查询出来的 Face 对象 IDNumber 为空，FaceID: {}", face.getFaceID());
                logParam.put("Reason", "查询出来的 Face 对象 IDNumber 为空");
                batchSaveSendProfileInfoLog(logParam, bulkRequest);
                return false;
            }

            if (StringUtils.isEmpty(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath())) {
                log.warn("查询出来的 Face 对象 StoragePath 为空，FaceID: {}", face.getFaceID());
                logParam.put("Reason", "查询出来的 Face 对象 StoragePath 为空");
                batchSaveSendProfileInfoLog(logParam, bulkRequest);
                return false;
            }

            for (Map.Entry<String, String> algRepoValue : algRepoMap.entrySet()) {
                addFacesToKafka(face, algRepoValue.getKey(), algRepoValue.getValue(), bulkRequest);
            }

            return true;
        }
    }
}
