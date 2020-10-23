package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.service.UploadPersonService;
import com.kedacom.haiou.kmtool.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2020/10/22.
 */
@Slf4j
@Controller
@RequestMapping("/uploadPic")
public class UploadPersonController {

    @Autowired
    private UploadPersonService uploadPersonService;

    @PostMapping("/uploadLocalPic")
    @ResponseBody
    public String uploadLocalPic(@RequestParam String tabID, @RequestParam String location){
        log.info("{} 收到上传本地图片至人员库请求，目标库ID {} , 本地图片路径 {}",
                TimeUtil.getNowDateStr(1), tabID, location);

        return uploadPersonService.uploadLocalPic(tabID, location);
    }

}
