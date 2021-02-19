package com.kedacom.haiou.kmtool.service;

import com.google.common.collect.Lists;
import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.entity.ExcelExistFaceEntity;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class BatchSearchFaceService {

    @Autowired
    private ViewlibFacade viewlibFacade;

    @Autowired
    private HaiouRepositoryDao haiouRepositoryDao;

    @Value("${picturePath.cacheLocation}")
    private String downloadLocation;

    public boolean searchByIDNumbers(List<String> idNumberList) {

        List<ExcelExportUtil.CellMap> cellMapList = new ArrayList<ExcelExportUtil.CellMap>() {{
            add(new ExcelExportUtil.CellMap("序号", "xh"));
            add(new ExcelExportUtil.CellMap("身份证号", "idNumber"));
            add(new ExcelExportUtil.CellMap("姓名", "name"));
            add(new ExcelExportUtil.CellMap("所属人员库", "tabName"));
        }};

        try {
            List<List<String>> idNumberLists = new ArrayList<>();
            if (1000 >= idNumberList.size()) {
                idNumberLists.add(idNumberList);
            } else {
                idNumberLists = Lists.partition(idNumberList, 1000);
            }

            Map<String, PersonBaseInfo> existFaceMap = new HashMap<>();
            for (List<String> idNumbers : idNumberLists) {
                String param = "?Face.TabID like .*.*&Face.IDNumber in (%s)&Fields=%s&PageRecordNum=%s";
                param = String.format(param, String.join(",", idNumbers), "FaceID,Name,IDNumber,TabID,SubImageList.StoragePath", idNumbers.size() * 10);
                List<Face> existFaceList = viewlibFacade.getFaces(param);
                if (CollectionUtils.isEmpty(existFaceList)) {
                    continue;
                }

                existFaceList.forEach(face -> {
                    final String idNumber = face.getIDNumber();
                    final String tabId = face.getTabID();

                    if (existFaceMap.containsKey(idNumber)) {
                        PersonBaseInfo personBaseInfo = existFaceMap.get(idNumber);
                        List<String> tabIDList = personBaseInfo.getTabIDList();
                        if (!tabIDList.contains(tabId)) {
                            tabIDList.add(tabId);
                            personBaseInfo.setTabIDList(tabIDList);
                            existFaceMap.put(idNumber, personBaseInfo);
                        }
                    } else {
                        PersonBaseInfo personBaseInfo = new PersonBaseInfo();
                        personBaseInfo.setIdNumber(face.getIDNumber());
                        personBaseInfo.setName(face.getName());
                        personBaseInfo.setPicUrl(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath());
                        List<String> tabIDList = new ArrayList<>();
                        tabIDList.add(face.getTabID());
                        personBaseInfo.setTabIDList(tabIDList);
                        existFaceMap.put(idNumber, personBaseInfo);
                    }
                });
            }

            List<ExcelExistFaceEntity> resList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(existFaceMap)) {
                AtomicInteger i = new AtomicInteger();
                idNumberList.forEach(idNumber -> {
                    ExcelExistFaceEntity excelExistFaceEntity = new ExcelExistFaceEntity();
                    if (existFaceMap.containsKey(idNumber)) {
                        excelExistFaceEntity.setXh(i.incrementAndGet());
                        excelExistFaceEntity.setIdNumber(idNumber);
                        excelExistFaceEntity.setName(existFaceMap.get(idNumber).getName());
                        List<String> tabIdList = existFaceMap.get(idNumber).getTabIDList();
                        List<String> tabNameList = convertTabId2TabName(tabIdList);
                        if (!CollectionUtils.isEmpty(tabNameList)) {
                            excelExistFaceEntity.setTabName(String.join(",", tabNameList));
                        } else {
                            excelExistFaceEntity.setTabName("不存在于任何布控库");
                        }

                        String pictureName = "%s.%s-%s.jpg";
                        pictureName = String.format(pictureName, excelExistFaceEntity.getXh(), idNumber, excelExistFaceEntity.getName());
                        String filePath = PictureUtil.downloadPicByURL(existFaceMap.get(idNumber).getPicUrl(),
                                downloadLocation, pictureName);
                        log.info("图片下载成功，下载路径: {}", filePath);
                    } else {
                        excelExistFaceEntity.setXh(i.incrementAndGet());
                        excelExistFaceEntity.setIdNumber(idNumber);
                        excelExistFaceEntity.setName("未知");
                        excelExistFaceEntity.setTabName("不存在于平台");
                    }
                    resList.add(excelExistFaceEntity);
                });
            }

            ExcelExportUtil<ExcelExistFaceEntity> eeu = new ExcelExportUtil<>();
            String sheetName = "重点人入库排查";
            String savePath = downloadLocation + File.separator + "检索结果.xlsx";
            try {
                eeu.exportSXSSaFExcel(sheetName, cellMapList, resList, resList.size(), savePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("根据IDNumber批量检索Face异常: {}", ExceptionUtils.getStackTrace(e));
            return false;
        }

        return true;
    }


    /**
     * 将批量TabID转换为TabName
     *
     * @param tabIdList
     * @return
     */
    public List<String> convertTabId2TabName(List<String> tabIdList) {
        List<String> tabNameList = new ArrayList<>();
        tabIdList.forEach(tabId -> {
            if (StringUtils.isNotEmpty(tabId)) {
                HaiouRepository haiouRepository = haiouRepositoryDao.queryRepoById(tabId);
                if (null != haiouRepository) {
                    final String tabName = haiouRepository.getName();
                    tabNameList.add(tabName);
                }
            }
        });

        return tabNameList;
    }

}
