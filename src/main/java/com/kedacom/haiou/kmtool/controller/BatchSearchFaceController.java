package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.service.BatchSearchFaceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2021/1/20.
 */
@Slf4j
@RestController
@RequestMapping("/searchFace")
public class BatchSearchFaceController {

    @Autowired
    private BatchSearchFaceService batchSearchFaceService;


    // 欺骗 IE 浏览器，返回值是 html 类型， 否则老版本 IE 会产生下载文件请求
    @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST, produces = {"text/html"})
    @ResponseBody
    public String batchSearchByIDNumbers(@RequestParam("file") MultipartFile[] files){

        try {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String content = new String(file.getBytes(), "UTF-8");
                if (StringUtils.isNotEmpty(content)) {
                    String[] idNumbers = content.split("\r\n");

                    if (0 == idNumbers.length) {
                        log.info("上传文本中无身份证");
                        return "success";
                    }

                    log.info("收到根据IDNumber批量查询Face的请求，身份证数量: {}", idNumbers.length);
                    batchSearchFaceService.searchByIDNumbers(Arrays.asList(idNumbers));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "success";
    }

    @PostMapping("/searchByIDNumbers")
    @ResponseBody
    public String searchByIDNumbers(@RequestBody Map<String, Object> param) {
        final List<String> idNumberList = new ArrayList<>();
        log.info("收到根据IDNumber批量查询Face的请求，身份证数量: {}", idNumberList.size());


        if (CollectionUtils.isEmpty(idNumberList)) {
            log.info("文本中无身份证信息");
            return "This File is Empty";
        }

        boolean result = batchSearchFaceService.searchByIDNumbers(idNumberList);

        if (result) {
            return "success";
        } else {
            return "failed";
        }

    }
}
