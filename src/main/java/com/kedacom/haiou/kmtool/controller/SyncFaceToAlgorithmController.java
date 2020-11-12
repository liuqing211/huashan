package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dto.viid.DataClassTab;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.SyncFaceToAlgorithmService;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/syncFace")
public class SyncFaceToAlgorithmController {

    @Autowired
    private SyncFaceToAlgorithmService syncFaceToAlgorithmService;

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
        }

        return "success";
    }
}
