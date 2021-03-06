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
            log.error("?????? creatorId: {} ????????????????????????{}", creatorId, ExceptionUtils.getStackTrace(e));
        }

        return haiouRepositoryList;
    }

    public boolean confirmStaticfaceNum(String tabId) {
        int staticFaceNum = 0;

        try {
            staticFaceNum = viewlibFacade.getStaticFacesNum(tabId);
        } catch (Exception e) {
            log.error("?????????ID???{} ?????? staticface ???????????????{}", tabId, ExceptionUtils.getStackTrace(e));
        }

        if (staticFaceNum > 0) {
            return true;
        } else {
            return false;
        }

    }


    public void cleanStaticfaceByTabID(List<String> tabIdList) throws Exception{
        for (String tabId : tabIdList) {
            log.info("????????????????????? {} ???????????????", tabId);
            String scollID = "1";

            do {
                String url = viewlibAddr + VIID_FACE + "?TabID=%s and ScollID=%s and MustUnLimit=1 and PageRecordNum = %s and Fields=(%s)";
                url = String.format(url, tabId, scollID, 100, "FaceID, RelativeID, SubImageList.StoragePath, IDNumber, Name, TabID");
                log.info("?????????????????????????????? {} ??? staticface ???????????????{}", tabId, url);

                ResponseEntity<String> pageResponse = null;
                try {
                    pageResponse = RestUtil.getRestTemplate().getForEntity(url, String.class);
                } catch (RestClientException e) {
                    log.error("????????????????????? {} ?????????{}", url, ExceptionUtils.getMessage(e));
                    break;
                }

                if (HttpStatus.NOT_FOUND.equals(pageResponse.getStatusCode())) {
                    log.info("???????????????????????? {} ??? staticface ????????????,????????????????????????????????????????????????", tabId);
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
                            log.info("???????????? {} ??? RelativeID {} ??? staticperson ????????????????????????", face.getFaceID(), relativeID);
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
                                log.info("???????????? {} ??? staticface ???????????????", face.getFaceID());
                                deleteStaticfaceLog.setDeleteFlag("0");
                            } else {
                                log.info("???????????? {} ??? staticface ???????????????", face.getFaceID());
                                deleteStaticfaceLog.setDeleteFlag("1");
                            }
                            deleteStaticfaceLog.setDeleteTime(TimeUtil.getNowDateStr(2));

                            int saveDeleteLogResult = deleteStaticfaceDao.saveDeleteStaticfaceLog(deleteStaticfaceLog);
                            if (saveDeleteLogResult > 0){
                                log.info("?????????????????? mysql ?????????{}", deleteStaticfaceLog.toString());
                            } else {
                                throw new Exception("?????????????????? mysql ??????");
                            }
                        }
                    }

                    String returnedScrollId = faceListRoot.getFaceListObject().getScollID();
                    if (!"1".equals(scollID) && !scollID.equals(returnedScrollId)) {
                        log.info("????????? {} ?????? {} ??????????????????", scollID, returnedScrollId);
                    }
                    scollID = returnedScrollId;
                }
            } while (true);


        }
    }

    /**
     * ?????? staticface ????????? RelativeId ??? staticperson ???????????????
     *
     * @param relativeID
     * @return
     */
    private int getStaticpersonNumByRelativeID  (String relativeID) {
        int staticPersonsNum = 0;
        try {
            staticPersonsNum = viewlibFacade.getStaticPersonsNum(relativeID);
        } catch (Exception e) {
            log.error("??????RelativeID???{} ?????? staticperson ???????????????{}", relativeID, ExceptionUtils.getStackTrace(e));
            return -1;
        }

        return staticPersonsNum;
    }

    /**
     * ?????????????????????
     * @return
     */
    public List<HaiouRepository> getAllRepository() {

        return null;
    }
}
