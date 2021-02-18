package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.service.BatchSearchFaceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
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
@Controller
@RequestMapping("/searchFace")
public class BatchSearchFaceController {

    @Autowired
    private BatchSearchFaceService batchSearchFaceService;

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
