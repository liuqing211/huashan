package com.kedacom.haiou.kmtool.service;

import com.kedacom.haiou.kmtool.dao.DeleteStaticfaceDao;
import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.FaceListRoot;
import com.kedacom.haiou.kmtool.entity.DeleteStaticfaceLog;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CleanDirtyDataService {

    @Resource
    private HaiouRepositoryDao repositoryDao;
    @Resource
    private DeleteStaticfaceDao deleteStaticfaceDao;

    @Autowired
    private ViewlibFacade viewlibFacade;

    @Value("${viewlib.addr}")
    private String viewlibAddr;
    @Value("${picturePath.cacheLocation}")
    private String pictureCacheLocation;

    private static final String VIID_FACE = "/VIID/Faces";


    public List<HaiouRepository> getTabByCreatorId(String creatorId) {
        List<HaiouRepository> haiouRepositoryList = new ArrayList<>();
        try {
            haiouRepositoryList = repositoryDao.getRepoByCreatorId(creatorId);
        } catch (Exception e) {
            log.error("根据 creatorId: {} 查询人员库异常：{}", creatorId, ExceptionUtils.getStackTrace(e));
        }

        return haiouRepositoryList;
    }

    public boolean confirmStaticfaceNum(String tabId) {
        int staticFaceNum = 0;

        try {
            staticFaceNum = viewlibFacade.getStaticFacesNum(tabId);
        } catch (Exception e) {
            log.error("根据库ID：{} 查询 staticface 总数异常：{}", tabId, ExceptionUtils.getStackTrace(e));
        }

        if (staticFaceNum > 0) {
            return true;
        } else {
            return false;
        }

    }


    public void cleanStaticfaceByTab(List<HaiouRepository> haiouRepositoryList) throws Exception{
        for (HaiouRepository haiouRepository : haiouRepositoryList) {
            log.info("开始清理人员库 {}-{} 中的脏数据", haiouRepository.getName(), haiouRepository.getId());
            final String tabId = haiouRepository.getId();
            String scollID = "1";

            do {
                String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
                url = String.format(url, tabId, scollID, 100, "FaceID, RelativeID, SubImageList.StoragePath, IDNumber, Name, TabID");
                log.info("遍历视图库查询人员库 {}-{} 中 staticface 数据请求：{}", haiouRepository.getName(), haiouRepository.getId(), url);

                ResponseEntity<String> pageResponse = null;
                try {
                    pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
                } catch (RestClientException e) {
                    log.error("查询视图库请求 {} 异常：{}", url, ExceptionUtils.getMessage(e));
                    break;
                }

                if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                    log.info("查询视图库人员库 {} 中 staticface 数据结束,该人员库无脏数据或脏数据清理完成", tabId);
                    break;
                } else {
                    FaceListRoot faceListRoot = GsonUtil.GsonToBean(pageResponse.getBody(), FaceListRoot.class);
                    List<Face> faceList = faceListRoot.getFaceListObject().getFaceObject();
                    for (Face face : faceList) {
                        final String relativeID = face.getRelativeID();
                        final Integer staticpersonNums = getStaticpersonNumByRelativeID(relativeID);
                        // final String storagePath = face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath();  || StringUtils.isNotBlank(storagePath)
                        if ((StringUtils.isNotBlank(relativeID) && 0 >= staticpersonNums) &&
                                StringUtils.isNotEmpty(face.getFaceID()) && StringUtils.isNotEmpty(face.getTabID())) {
                            log.info("该条人脸 {} 的 RelativeID {} 在 staticperson 中不存在，需删除", face.getFaceID(), relativeID);
                            boolean delResult = viewlibFacade.delFace(face.getFaceID(), face.getTabID());

                            DeleteStaticfaceLog deleteStaticfaceLog = new DeleteStaticfaceLog();
                            deleteStaticfaceLog.setId(String.valueOf(face.getFaceID() + "503" + new Random().nextInt(999999999) % (999999999-100000000+1) + 100000000));
                            deleteStaticfaceLog.setFaceId(face.getFaceID());
                            deleteStaticfaceLog.setRelativeId(face.getRelativeID());
                            deleteStaticfaceLog.setTabId(face.getTabID());
                            deleteStaticfaceLog.setIdNumber(face.getIDNumber());
                            deleteStaticfaceLog.setName(face.getName());
                            deleteStaticfaceLog.setStoragepath(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath());
                            if (delResult) {
                                log.info("该条人脸 {} 从 staticface 软删除成功", face.getFaceID());
                                deleteStaticfaceLog.setDeleteFlag("0");
                            } else {
                                log.info("该条人脸 {} 从 staticface 软删除失败", face.getFaceID());
                                deleteStaticfaceLog.setDeleteFlag("1");
                            }
                            deleteStaticfaceLog.setDeleteTime(TimeUtil.getNowDateStr(2));

                            int saveDeleteLogResult = deleteStaticfaceDao.saveDeleteStaticfaceLog(deleteStaticfaceLog);
                            if (saveDeleteLogResult > 0){
                                log.info("删除日志保存 mysql 成功：{}", deleteStaticfaceLog.toString());
                            } else {
                                throw new Exception("删除日志保存 mysql 失败");
                            }
                        }
                    }

                    String returnedScrollId = faceListRoot.getFaceListObject().getScollID();
                    if (!"1".equals(scollID) && !scollID.equals(returnedScrollId)) {
                        log.info("游标从 {} 变为 {} ，又从头加载", scollID, returnedScrollId);
                    }
                    scollID = returnedScrollId;
                }
            } while (true);


        }
    }

    /**
     * 确认 staticface 数据的 RelativeId 在 staticperson 中是否存在
     *
     * @param relativeID
     * @return
     */
    private int getStaticpersonNumByRelativeID  (String relativeID) {
        int staticPersonsNum = 0;
        try {
            staticPersonsNum = viewlibFacade.getStaticPersonsNum(relativeID);
        } catch (Exception e) {
            log.error("根据RelativeID：{} 查询 staticperson 总数异常：{}", relativeID, ExceptionUtils.getStackTrace(e));
            return -1;
        }

        return staticPersonsNum;
    }

    /**
     * 查询所有人员库
     * @return
     */
    public List<HaiouRepository> getAllRepository() {

        return null;
    }
}
