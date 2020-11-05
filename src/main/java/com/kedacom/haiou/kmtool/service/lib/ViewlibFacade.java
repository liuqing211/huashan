package com.kedacom.haiou.kmtool.service.lib;

import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import com.kedacom.haiou.kmtool.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/10/22.
 */
@Slf4j
@Service
public class ViewlibFacade {

    private static final String VIID_PERSON = "/VIID/Persons";
    private static final String VIID_FACE = "/VIID/Faces";
    private static final String VIID_DATACLASSTABS = "/VIID/DataClassTabs";

    @Value("${viewlib.addr}")
    private String viewlibAddr;

    @Value("${flag.uploadMsg}")
    private boolean isUploadMsg;

    public List<DataClassTab> getDataClassTab(String tabID) {
        String url = viewlibAddr + VIID_DATACLASSTABS + "?DataClassTab.TabID=" + tabID;
        log.info("查询视图库DataClassTab请求参数：{}", url);
        try {
            ResponseEntity<String> responseEntity = RestUtil.getRestTemplate().exchange(url, HttpMethod.GET, null, String.class);
            DataClassTabListRoot dataClassTabListRoot = GsonUtil.GsonToBean(responseEntity.getBody(), DataClassTabListRoot.class);
            if (null == dataClassTabListRoot || dataClassTabListRoot.getDataClassTabListObject() == null) {
                return new ArrayList<>();
            }

            return dataClassTabListRoot.getDataClassTabListObject().getDataClassTabObject();
        } catch (Exception e) {
            log.error("查询人员库数据异常！: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }

    }

    public List<Person> getPersons(String person_params) {
        String url = viewlibAddr + VIID_PERSON + person_params;
        log.info("查询视图库Person是否存在参数 {}", url);
        try {
            ResponseEntity<String> responseEntity = RestUtil.getRestTemplate().exchange(url, HttpMethod.GET, null, String.class);
            PersonListRoot personListRoot = GsonUtil.GsonToBean(responseEntity.getBody(), PersonListRoot.class);
            List<Person> list = personListRoot.getPersonListObject().getPersonObject();
            return list;
        } catch (Exception e) {
            log.error("查询视图库Person异常 {}, 参数为 {}", ExceptionUtils.getStackTrace(e), person_params);
            return new ArrayList<>();
        }
    }

    public List<Face> getFaces(String face_params) {
        String url = viewlibAddr + VIID_FACE + face_params;
        log.info("查询视图库Face是否存在参数 {}", face_params);
        try {
            ResponseEntity<String> responseEntity = RestUtil.getRestTemplate().exchange(url, HttpMethod.GET, null, String.class);
            FaceListRoot faceListRoot = GsonUtil.GsonToBean(responseEntity.getBody(), FaceListRoot.class);
            return faceListRoot.getFaceListObject().getFaceObject();
        } catch (Exception e) {
            log.error("查询视图库Face异常 {}, 参数为 {}", ExceptionUtils.getStackTrace(e), face_params);
            return new ArrayList<>();
        }
    }

    public boolean addPersons(PersonList personList) {
        String url = viewlibAddr + VIID_PERSON;
        PersonListRoot personListRoot = new PersonListRoot();
        personListRoot.setPersonListObject(personList);
        try {
            String params = GsonUtil.GsonString(personListRoot);
            log.info("即将录入视图库Person的参数 {}", params);
            if (isUploadMsg) {
                ResponseEntity<String> responseEntity = RestUtil.getRestTemplate().exchange(url, HttpMethod.POST, generateHttpEntity(params), String.class);
                ResponseStatusListRoot responseStatusListRoot = GsonUtil.GsonToBean(responseEntity.getBody(), ResponseStatusListRoot.class);
                if (new Integer(0).equals(responseStatusListRoot.getResponseStatusListObject().getResponseStatusObject().get(0).getStatusCode())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("录入视图库Face异常 {}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public boolean addFaces(FaceList faceList) {
        String url = viewlibAddr + VIID_FACE;
        FaceListRoot faceListRoot = new FaceListRoot();
        faceListRoot.setFaceListObject(faceList);
        try {
            String params = GsonUtil.GsonString(faceListRoot);
            log.info("即将录入视图库Face的参数 {}", params);
            if (isUploadMsg) {
                ResponseEntity<String> responseEntity = RestUtil.getRestTemplate().exchange(url, HttpMethod.POST, generateHttpEntity(params), String.class);
                ResponseStatusListRoot responseStatusListRoot = GsonUtil.GsonToBean(responseEntity.getBody(), ResponseStatusListRoot.class);
                if (new Integer(0).equals(responseStatusListRoot.getResponseStatusListObject().getResponseStatusObject().get(0).getStatusCode())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }


        } catch (Exception e) {
            log.error("录入视图库Face异常 {}", ExceptionUtils.getStackTrace(e));
            return false;
        }

    }

    public HttpEntity<String> generateHttpEntity(String params) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.setConnection("Keep-Alive");
        HttpEntity<String> requestEntity = new HttpEntity<String>(params, headers);
        return requestEntity;
    }
}
