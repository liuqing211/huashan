package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.viid.DataClassTab;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.CommonConstant;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import com.kedacom.haiou.kmtool.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/syncData")
public class SyncDataController {



    @Autowired
    HaiouRepositoryDao haiouRepositoryDao;

    @Autowired
    ViewlibFacade viewlibFacade;

    @GetMapping("/syncDataClassTab")
    public void syncDataClassTabToAlgorithm() {
        List<HaiouRepository> repositoryList = haiouRepositoryDao.queryRepoByBelongUnit("d675");
        log.info("需要同步算法创建 {} 个人员库", repositoryList.size());
        if (CollectionUtils.isEmpty(repositoryList)) {
            log.info("目前库中无人员库需要同步给算法");
            return;
        }

        for (HaiouRepository haiouRepository : repositoryList) {
            final String tabId = haiouRepository.getId();

            Map<String, String> params = new HashMap<>();
            params.put("repoName", haiouRepository.getName());
            params.put("repoDesc", StringUtils.isEmpty(haiouRepository.getDescription()) ? "" : haiouRepository.getDescription());

            for (int i = 1; i <= 3; i++) {
                final String algorithmId = String.valueOf(i + 3);
                String repoMapping = haiouRepositoryDao.queryRepoMappingByAlgIDAndRepoId(algorithmId, tabId);
                if (StringUtils.isNotEmpty(repoMapping)) {
                    log.info("人员库 {} 在算法 {} 中已存在，无需建库", algorithmId, tabId);
                    continue;
                }

                String algorithmServerUrl = CommonConstant.ALGORITHM_MAP.get("ALGORITHM_" + algorithmId);
                if (StringUtils.isEmpty(algorithmServerUrl)) {
                    log.info("算法 {} 未接入系统，无需建库", algorithmId);
                    continue;
                }

                List<DataClassTab> dataClassTab = viewlibFacade.getDataClassTab(tabId);
                if (CollectionUtils.isEmpty(dataClassTab)) {
                    log.info("人员库 {} 在视图库中已删除，无需建库", tabId);
                    continue;
                }

                String url = algorithmServerUrl + CommonConstant.CREATE_REPOSITORY;
                ResponseEntity<String> responseEntity = RestUtil.getRestTemplate().exchange(url, HttpMethod.POST, generateHttpEntity(GsonUtil.toJson(params)), String.class);
                Map result = GsonUtil.GsonToBean(responseEntity.getBody(), Map.class);
                if (new Double(0).equals(result.get("errorCode"))) {
                    String repoID = (String) result.get("repoID");
                    if (StringUtils.isEmpty(repoID)) {
                        log.error("算法接口调用成功，算法返回 repoID 为空", result.toString());
                        continue;
                    }

                    Boolean addResult = haiouRepositoryDao.insertRepoMapping(tabId, algorithmId, repoID);
                    if (addResult) {
                        log.info("给算法 {} 建库 {} 库映射 {} 成功", algorithmId, tabId, repoID);
                    } else {
                        log.info("给算法 {} 建库 {} 库映射 {} 失败", algorithmId, tabId, repoID);
                    }

                }


            }
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
