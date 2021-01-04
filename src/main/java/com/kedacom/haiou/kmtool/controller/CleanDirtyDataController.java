package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.CleanDirtyDataService;
import com.kedacom.haiou.kmtool.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Administrator on 2020/11/30.
 */
@Slf4j
@Controller
@RequestMapping("/cleanDirtyData")
public class CleanDirtyDataController {

    @Autowired
    private CleanDirtyDataService cleanDirtyDataService;

    @Value("${whitelist.repoId}")
    private String whiteRepeIds;

    @PostMapping("/staticface/cleanByTabCreatorId")
    @ResponseBody
    public String cleanByTabCreatorId(@RequestBody Map<String, Object> param) {
        final String creatorId = param.get("creatorId").toString();
        log.info("收到根据库创建人 {} 清理 staticface 中脏数据的请求", creatorId);
        List<HaiouRepository> haiouRepositoryList = cleanDirtyDataService.getTabByCreatorId(creatorId);
        if (CollectionUtils.isEmpty(haiouRepositoryList)) {
            return "This user doesn't created repository";
        }

        List<String> whiteRepoIdList = Arrays.asList(whiteRepeIds.split(","));
        log.info("根据库创建人 {} 查询到的人员库有 {}", creatorId, haiouRepositoryList.size());
        List<HaiouRepository> cleanRepositoryList = new ArrayList<>();
        haiouRepositoryList.forEach(haiouRepository -> {
            final String tabId = haiouRepository.getId();
            if (StringUtils.isNotBlank(tabId)) {
                boolean isNotEmpty = cleanDirtyDataService.confirmStaticfaceNum(tabId);
                if (!isNotEmpty || whiteRepoIdList.contains(tabId)) {
                    log.info("人员库 {}-{} 中无数据或禁止进行修改删除，无需清理", haiouRepository.getId(), haiouRepository.getName());
                } else {
                    cleanRepositoryList.add(haiouRepository);
                }
            }
        });

        try {
            cleanDirtyDataService.cleanStaticfaceByTab(cleanRepositoryList);
        } catch (Exception e) {
            log.error("清理脏数据异常：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("根据库创建人 {} 清理 staticface 中的脏数据完成", creatorId);


        return "success";
    }


}
