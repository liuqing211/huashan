package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.viid.DataClassTab;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.SyncFaceToAlgorithmService;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/syncFace")
public class SyncFaceToAlgorithmController {

    @Autowired
    private SyncFaceToAlgorithmService syncFaceToAlgorithmService;

    @Autowired
    private HaiouRepositoryDao haiouRepositoryDao;

    @Autowired
    private ViewlibFacade viewlibFacade;

    @GetMapping("/pushProfileToAlgorithm")
    @ResponseBody
    public void pushProfileToAlgorithm(){
        List<HaiouRepository> haiouRepositoryList = haiouRepositoryDao.queryRepoByBelongUnit("d675");
        log.info("查询到市级布控库有{} 个", haiouRepositoryList.size());

        for (HaiouRepository haiouRepository : haiouRepositoryList) {
            if (haiouRepository.getFaceNum() < 0) {
                log.info("人员库 {} 中无 Face 数据，跳过", haiouRepository.getName());
                continue;
            }

            syncFaceToAlgorithmService.syncValidFaceToAlgorithm(haiouRepository.getId(), "1,2,3");
        }

    }

    @GetMapping("/pushResidentToAlgorithm")
    @ResponseBody
    public void pushResidentToAlgorithm(){
        syncFaceToAlgorithmService.syncResidentToAlgorithm("334c0a72450a4bc089d9aea173046fe2", "1,2,3");

    }
}
