package com.kedacom.haiou.kmtool.service;

import com.google.common.collect.Lists;
import com.kedacom.haiou.kmtool.dao.DeleteStaticfaceDao;
import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.FaceListRoot;
import com.kedacom.haiou.kmtool.entity.DeleteStaticfaceLog;
import com.kedacom.haiou.kmtool.entity.ExcelExistFaceEntity;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class BatchSearchFaceService {

    @Autowired
    private ViewlibFacade viewlibFacade;

    @Autowired
    private HaiouRepositoryDao haiouRepositoryDao;

    public boolean searchByIDNumbers(List<String> idNumberList) {

        List<ExcelExportUtil.CellMap> cellMapList = new ArrayList<ExcelExportUtil.CellMap>(){{
            add(new ExcelExportUtil.CellMap("身份证号", "idNumber"));
            add(new ExcelExportUtil.CellMap("所属人员库", "tabName"));
        }};

        try {
            List<List<String>> idNumberLists = new ArrayList<>();
            if (50 >= idNumberList.size()){
                idNumberLists.add(idNumberList);
            } else {
                idNumberLists = Lists.partition(idNumberList, 1000);
            }

            Map<String, List<String>> existFaceMap = new HashMap<>();
            for (List<String> idNumbers : idNumberLists) {
                String param = "?Face.TabID like .*.*&Face.IDNumber in (%s)&Fields=%s&PageRecordNum=%s";
                param = String.format(param, String.join(",", idNumbers), "FaceID,Name,IDNumber,TabID", idNumbers.size() * 10);
                List<Face> existFaceList = viewlibFacade.getFaces(param);
                if (CollectionUtils.isEmpty(existFaceList)) {
                    continue;
                }

                existFaceList.forEach(face -> {
                    final String idNumber = face.getIDNumber();
                    final String tabId = face.getTabID();

                    if (existFaceMap.containsKey(idNumber)) {
                        List<String> tabIdList = existFaceMap.get(idNumber);
                        if (!tabIdList.contains(tabId)) {
                            tabIdList.add(tabId);
                            existFaceMap.put(idNumber, tabIdList);
                        }
                    } else {
                        List<String> tabIdList = new ArrayList<>();
                        tabIdList.add(tabId);
                        existFaceMap.put(idNumber, tabIdList);
                    }
                });
            }

            List<ExcelExistFaceEntity> resList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(existFaceMap)) {
                idNumberList.forEach(idNumber -> {
                    ExcelExistFaceEntity excelExistFaceEntity = new ExcelExistFaceEntity();
                    if (existFaceMap.containsKey(idNumber)) {
                        List<String> tabIdList = existFaceMap.get(idNumber);
                        List<String> tabNameList = convertTabId2TabName(tabIdList);
                        excelExistFaceEntity.setIdNumber(idNumber);
                        if (!CollectionUtils.isEmpty(tabNameList)) {
                            excelExistFaceEntity.setTabName(String.join(",", tabNameList));
                        } else {
                            excelExistFaceEntity.setTabName("不存在");
                        }
                        resList.add(excelExistFaceEntity);
                    } else {
                        excelExistFaceEntity.setIdNumber(idNumber);
                        excelExistFaceEntity.setTabName("不存在");
                    }
                });
            }

            ExcelExportUtil<ExcelExistFaceEntity> eeu = new ExcelExportUtil<>();
            String sheetName = "重点人入库排查";
            String savePath = "E:\\33333\\重点人入库排查.xlsx";
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
