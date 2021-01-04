package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dto.HaiouBasicNewVO;
import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.CompareFaceCntService;
import com.kedacom.haiou.kmtool.service.FaceSearchService;
import com.kedacom.haiou.kmtool.service.SyncFaceToAlgorithmService;
import com.kedacom.haiou.kmtool.service.UploadPersonService;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.IdFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequestMapping("/compareFaceCnt")
public class CompareFaceCntController {

    @Autowired
    private FaceSearchService faceSearchService;
    @Autowired
    private CompareFaceCntService compareFaceCntService;
    @Autowired
    private ViewlibFacade viewlibFacade;

    @Value("${viewlib.batchNum}")
    private String viewlibBatchNum;

    @PostMapping("/compareByTabID")
    @ResponseBody
    public String compareByTabID(@RequestBody Map<String, Object> params){
        final String tabId = params.get("TabID").toString();
        log.info("收到比对新老平台人员库 {} 中人员数量的请求", tabId);
        List<PersonBaseInfo> personBaseInfoList = faceSearchService.searchEsFaceByTabID(tabId);

        if (CollectionUtils.isEmpty(personBaseInfoList)) {
            return "人员库" + tabId + "在老平台中无人脸数据";
        }
        log.info("老平台中人员库 {} 中有 {} 条数据", tabId, personBaseInfoList.size());

        List<PersonBaseInfo> addPersonList = new ArrayList<>();
        for (PersonBaseInfo personBaseInfo : personBaseInfoList) {
            final String idNumber = personBaseInfo.getIdNumber();
            String faceExistParam = "?Face.TabID=%s&Face.IDNumber=%s&PageRecordNum=20&Fields=IDNumber,Name,SubImageList.StoragePath";
            faceExistParam = String.format(faceExistParam, tabId, idNumber);
            List<Face> faceList = viewlibFacade.getFaces(faceExistParam);
            if (CollectionUtils.isEmpty(faceList)) {
                log.info("身份证号 {} 在新平台中的人员库 {} 中不存在", idNumber, tabId);
                personBaseInfo.setImageID(IdFactory.ImageIDType());
                personBaseInfo.setRelativeID(IdFactory.RelativeIDType());
                addPersonList.add(personBaseInfo);
                continue;
            }

            boolean exists = false;
            for (Face face : faceList) {
                final Integer storagePathHash = face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath().hashCode();
                if (storagePathHash == personBaseInfo.getPicUrl().hashCode()) {
                    log.info("身份证号 {} 图片地址 {} 在新平台中的人员库 {} 中已存在", idNumber, personBaseInfo.getPicUrl(), tabId);
                    exists = true;
                }
            }

            if (!exists && StringUtils.isNotEmpty(personBaseInfo.getPicUrl())) {
                log.info("身份证号 {} 在新平台人员库 {} 中已存在，但图片 {} 不存在", idNumber, tabId, personBaseInfo.getPicUrl());
                personBaseInfo.setImageID(IdFactory.ImageIDType());
                personBaseInfo.setRelativeID(IdFactory.RelativeIDType());
                addPersonList.add(personBaseInfo);
            }
        }

        boolean addResult = compareFaceCntService.batchUploadFace(addPersonList, tabId);
        if (addResult) {
            log.info("批量添加人员库 {} 遗漏照片成功");
        }
        return "success";
    }
}
