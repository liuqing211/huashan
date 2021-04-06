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

    @GetMapping("/pushProfileToKfk")
    @ResponseBody
    public void pushProfileToKfk(){
        syncFaceToAlgorithmService.pushProfileToKfk();
    }

}
