package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dao.HaiouRepositoryDao;
import com.kedacom.haiou.kmtool.dto.viid.DataClassTab;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.SyncFaceToAlgorithmService;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.TimeUtil;
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

    @PostMapping("/syncAllBlackListFace")
    @ResponseBody
    public String syncAllBlackListFace(@RequestParam String algorithmIds){
        log.info("收到同步所有黑名单人员至算法的请求");
        List<HaiouRepository> allBlackListRepo = syncFaceToAlgorithmService.getAllBlackListRepo();
        if (CollectionUtils.isEmpty(allBlackListRepo)){
            return "目前库中无黑名单人员库";
        }

        for (HaiouRepository haiouRepository : allBlackListRepo) {
            final String repoId = haiouRepository.getId();
            List<DataClassTab> dataClassTab = viewlibFacade.getDataClassTab(repoId);
            if (CollectionUtils.isEmpty(dataClassTab)){
                log.error("人员库 {} 在视图库 DataClassTab 中不存在", repoId);
                continue;
            }

            int staticFacesNum = viewlibFacade.getStaticFacesNum(repoId);
            if (staticFacesNum <= 0){
                log.error("人员库 {} 在视图库 staticface 中无人员数据", repoId);
                continue;
            }


            boolean syncFaceToAlgorithmResult = syncFaceToAlgorithmService.syncFaceToAlgorithm(repoId, algorithmIds);

            if (syncFaceToAlgorithmResult) {
                log.info("人员库 {} 同步第三方算法完成", repoId);
            }
        }

        return "success";
    }


    @PostMapping("/syncByCreatorId")
    @ResponseBody
    public String syncByCreatorId(@RequestBody Map param){
        String creatorName = (String) param.get("CreatorName");
        String algorithmIds = (String) param.get("AlgorithmIds");
        log.info("收到根据 CreatorName: {} 同步所有黑名单人员至算法的请求", creatorName);
        List<HaiouRepository> allBlackListRepo = haiouRepositoryDao.getBlackRepoByCreatorName(creatorName);
        if (CollectionUtils.isEmpty(allBlackListRepo)){
            return "目前库中无黑名单人员库";
        }

        for (HaiouRepository haiouRepository : allBlackListRepo) {
            final String repoId = haiouRepository.getId();
            List<DataClassTab> dataClassTab = viewlibFacade.getDataClassTab(repoId);
            if (CollectionUtils.isEmpty(dataClassTab)){
                log.error("人员库 {} 在视图库 DataClassTab 中不存在", repoId);
                continue;
            }

            int staticFacesNum = viewlibFacade.getStaticFacesNum(repoId);
            if (staticFacesNum <= 0){
                log.error("人员库 {} 在视图库 staticface 中无人员数据", repoId);
                continue;
            }


            boolean syncFaceToAlgorithmResult = syncFaceToAlgorithmService.syncFaceToAlgorithm(repoId, algorithmIds);

            if (syncFaceToAlgorithmResult) {
                log.info("人员库 {} 同步第三方算法完成", repoId);
            }
        }

        return "success";
    }
}
