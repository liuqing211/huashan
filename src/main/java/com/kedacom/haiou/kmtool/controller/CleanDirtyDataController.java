package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dao.CalarmMsgInfoDao;
import com.kedacom.haiou.kmtool.dto.viid.DispositionNotification;
import com.kedacom.haiou.kmtool.entity.CalarmMsgInfo;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.service.CleanDirtyDataService;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
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

    @Autowired
    private CalarmMsgInfoDao calarmMsgInfoDao;

    @Autowired
    private ViewlibFacade viewlibFacade;

    @Value("${whitelist.repoId}")
    private String whiteRepeIds;

    @GetMapping("/calarm/updateTriggerTime")
    @ResponseBody
    public String cleanFeatureMsgInfo() {
        List<CalarmMsgInfo> calarmMsgInfos = calarmMsgInfoDao.queryFeatureMsgInfo();
        if (CollectionUtils.isEmpty(calarmMsgInfos)) {
            log.info("统一告警中无未来数据的告警");
            return "Failed";
        }

        calarmMsgInfos.forEach(calarmMsgInfo -> {
            String param = "?DispositionID=%s&NotificationID=%s&Fields=(%s)";
            param = String.format(param, calarmMsgInfo.getDispositionId(), calarmMsgInfo.getNotificationId(), "NotificationID,DispositionID,TriggerTime");
            List<DispositionNotification> dispositionNotificationList = viewlibFacade.getDispositionNotifications(param);
            if (!CollectionUtils.isEmpty(dispositionNotificationList)) {

                DispositionNotification dispositionNotification = dispositionNotificationList.get(0);
                Date triggerTimeDate = TimeUtil.parseDateStr(2, dispositionNotification.getTriggerTime());
                String triggerTime = TimeUtil.formatDate(1, triggerTimeDate);
                String triggerTimeStr = TimeUtil.formatDate(3, triggerTimeDate);
                boolean result = calarmMsgInfoDao.updateTriggerTime(dispositionNotification.getDispositionID(), dispositionNotification.getNotificationID(), triggerTime, triggerTimeStr);
                if (result) {
                    log.info("修改告警: DispositionID-{} NotificationID-{} 触发时间 TriggerTime-{} TriggerTimeStr-{} 成功",
                            dispositionNotification.getDispositionID(), dispositionNotification.getNotificationID(), triggerTime, triggerTimeStr);
                } else {
                    log.error("修改告警: DispositionID-{} NotificationID-{} 触发时间 TriggerTime-{} TriggerTimeStr-{} 失败",
                            dispositionNotification.getDispositionID(), dispositionNotification.getNotificationID(), triggerTime, triggerTimeStr);
                }
            }
        });


        return "success";

    }

    @PostMapping("/staticface/cleanByTabID")
    @ResponseBody
    public String cleanByTabID(@RequestBody Map<String, Object> param) {
        final List<String> tabIdList = (List<String>) param.get("tabIds");
        log.info("收到根据库ID组 {} 清理staticface中脏数据的请求", tabIdList.toString());
        if (CollectionUtils.isEmpty(tabIdList)) {
            return "传入的库ID组为空";
        }

        List<String> whiteRepoIdList = Arrays.asList(whiteRepeIds.split(","));
        List<String> cleanTabIDList = new ArrayList<>();

        for (String tabId : tabIdList) {
            if (StringUtils.isEmpty(tabId) || whiteRepoIdList.contains(tabId)) {
                log.info("库ID {} 为空或者在白名单中", tabId);
            }

            boolean isNotEmpty = cleanDirtyDataService.confirmStaticfaceNum(tabId);
            if (!isNotEmpty || whiteRepoIdList.contains(tabId)) {
                log.info("人员库 {} 中无数据或禁止进行修改删除，无需清理", tabId);
            } else {
                cleanTabIDList.add(tabId);
            }
        }

        try {
            cleanDirtyDataService.cleanStaticfaceByTabID(cleanTabIDList);
        } catch (Exception e) {
            log.error("清理脏数据异常：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("根据库ID组 {} 清理 staticface 中的脏数据完成", tabIdList.toString());
        return "success";

    }

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
        List<String> tabIdList = new ArrayList<>();
        haiouRepositoryList.forEach(haiouRepository -> {
            final String tabId = haiouRepository.getId();
            if (StringUtils.isNotBlank(tabId)) {
                boolean isNotEmpty = cleanDirtyDataService.confirmStaticfaceNum(tabId);
                if (!isNotEmpty || whiteRepoIdList.contains(tabId)) {
                    log.info("人员库 {}-{} 中无数据或禁止进行修改删除，无需清理", haiouRepository.getId(), haiouRepository.getName());
                } else {
                    tabIdList.add(tabId);
                }
            }
        });

        try {
            cleanDirtyDataService.cleanStaticfaceByTabID(tabIdList);
        } catch (Exception e) {
            log.error("清理脏数据异常：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("根据库创建人 {} 清理 staticface 中的脏数据完成", creatorId);
        return "success";
    }

    @GetMapping ("/staticface/cleanAllTab")
    @ResponseBody
    public String cleanAllTab() {
        List<String> whiteRepoIdList = Arrays.asList(whiteRepeIds.split(","));
        log.info("配置文件中的白名单有 {}", String.join(",", whiteRepoIdList));
        List<HaiouRepository> cleanRepositoryList = new ArrayList<>();

        /*List<HaiouRepository> allRepositoryList = cleanDirtyDataService.getAllRepository();
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
        log.info("根据库创建人 {} 清理 staticface 中的脏数据完成", creatorId);*/
        return "success";
    }


}
