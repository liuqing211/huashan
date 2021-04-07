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
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessage;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessageRoot;
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

    @Value("${topic.receiverProfile}")
    private String receiverProfileTopic;

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


    /**
     * 轮询查询视图库，将档案写入到kfk中
     */
    public void pushProfileToKfk() {

        int loadedCount = 0;
        String scollID = "1";
        do {
            log.info("开始轮询查询视图库 {}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
            url = String.format(url, "334c0a72450a4bc089d9aea173046fe2", scollID, 2000, "FaceID, IDNumber, Name, RelativeID, TabID, SubImageList.StoragePath");
            log.info("遍历视图库查询人员库 334c0a72450a4bc089d9aea173046fe2 中 staticface 数据请求: {}", url);

            ResponseEntity<String> pageResponse = null;
            try {
                pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
            } catch (RestClientException e) {
                log.error("查询视图库请求 {} 请求失败：{}", url, ExceptionUtils.getMessage(e));
                break;
            }

            if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                log.info("查询视图库人员库 334c0a72450a4bc089d9aea173046fe2 中 staticface 数据结束");
                break;
            } else if (HttpStatus.OK.equals(pageResponse.getStatusCode())) {
                FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();

                ProfileFaceMessageRoot profileFaceMessageRoot = new ProfileFaceMessageRoot();
                List<ProfileFaceMessage> profileFaceMessageList = new ArrayList<>();
                for (Face face : faceList) {
                    if (null != face) {
                        ProfileFaceMessage profileFaceMessage = new ProfileFaceMessage();
                        profileFaceMessage.setFaceID(face.getFaceID());
                        profileFaceMessage.setRelativeID(face.getRelativeID());
                        profileFaceMessage.setIDNumber(face.getIDNumber());
                        profileFaceMessage.setName(face.getName());
                        profileFaceMessage.setTabID(face.getTabID());
                        profileFaceMessage.setStoragePath(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath());
                        profileFaceMessage.setEntryTime(face.getLocationMarkTime());
                        profileFaceMessage.setId(face.getFaceID() + face.getLocationMarkTime() + UUID.randomUUID());
                        profileFaceMessageList.add(profileFaceMessage);
                    }
                }
                profileFaceMessageRoot.setProfileFaceMessageList(profileFaceMessageList);

                ExecutorService executorService = Executors.newWorkStealingPool(12);
                List<Future<Boolean>> result = new ArrayList();
                AddProfileToKfk thread = new AddProfileToKfk(profileFaceMessageRoot);
                result.add(executorService.submit(thread));

                result.forEach(r -> {
                    try {
                        if (r.get()) {
                        }
                    } catch (Exception e) {
                        log.error("发送人脸图到Kafka失败");
                    }
                });

                log.info("本次轮询查询视图库结束: {}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

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

    public class AddProfileToKfk implements Callable<Boolean> {
        private ProfileFaceMessageRoot profileFaceMessageRoot;

        public AddProfileToKfk(ProfileFaceMessageRoot profileFaceMessageRoot) {
            this.profileFaceMessageRoot = profileFaceMessageRoot;
        }

        @Override
        public Boolean call() {
            if (null == profileFaceMessageRoot || CollectionUtil.isEmpty(profileFaceMessageRoot.getProfileFaceMessageList())) {
                return false;
            }

            kafkaTemplate.send(receiverProfileTopic, String.valueOf(profileFaceMessageRoot.hashCode()).getBytes(), GsonUtil.toJson(profileFaceMessageRoot).getBytes()).addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                @Override
                public void onFailure(Throwable e) {
                    log.error("人员信息发送kfk失败: {}", ExceptionUtils.getStackTrace(e));
                }

                @Override
                public void onSuccess(SendResult<byte[], byte[]> sendResult) {
                    log.info("人员信息发送kafka {} 成功", receiverProfileTopic);
                }
            });
            return true;
        }
    }
}
