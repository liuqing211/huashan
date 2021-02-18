package com.kedacom.haiou.kmtool.controller;

import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.entity.PasserbyPic2Algorithm;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import com.kedacom.haiou.kmtool.utils.RequestFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2021/1/28.
 */
@Slf4j
@Controller
@RequestMapping("/VIID")
public class SubscribeNotificationsController {

    private static KafkaTemplate kafkaKedaTemplate;

    @Autowired
    public SubscribeNotificationsController(@Qualifier("kafkaKedaTemplate") KafkaTemplate kafkaTemplate) {
        SubscribeNotificationsController.kafkaKedaTemplate = kafkaTemplate;
    }

    @PostMapping("/SubscribeNotifications")
    @ResponseBody
    public ResponseEntity subscribeNotifications(HttpServletRequest request) {
        ResponseStatusListRoot responseListRootBean = new ResponseStatusListRoot();
        ResponseStatusList responseStatusList = new ResponseStatusList();
        List<ResponseStatus> responseStatuses = new ArrayList<>();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setRequestURL(request.getRequestURI());

        try {
            String s = RequestFormatUtil.formatRequest(request);
            log.debug("接收到通知信息: {} ", s);
            SubscribeNotificationListRoot root = GsonUtil.GsonToBean(s, SubscribeNotificationListRoot.class);
            List<SubscribeNotification> subscribeNotificationList = root.getSubscribeNotificationListObject().getSubscribeNotificationObject();
            for (SubscribeNotification subscribeNotification : subscribeNotificationList) {

                FaceList faceList = subscribeNotification.getFaceObjectList();
                faceList.getFaceObject().forEach(face -> {
                    PasserbyPic2Algorithm passerbyPic2Algorithm = ConvertUtil.convertFaceToPasserbyPic2Algorithm(face);
                    log.info("接收到的抓拍数据: {}", passerbyPic2Algorithm.toString());
                    kafkaKedaTemplate.send("passerbyPic2AlgorithmVid", GsonUtil.GsonString(passerbyPic2Algorithm).getBytes()).addCallback(new ListenableFutureCallback<SendResult<Object, byte[]>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            log.error("发送抓拍到kafka失败: {}", ExceptionUtils.getStackTrace(throwable));
                        }

                        @Override
                        public void onSuccess(@Nullable SendResult<Object, byte[]> objectSendResult) {
                            log.info("发送抓拍到Kafka成功");
                        }
                    });
                });

            }
            responseStatus.setStatusCode(0);
            responseStatus.setStatusString("OK");
        } catch (Exception e) {
            responseStatus.setStatusCode(1);
            responseStatus.setStatusString(ExceptionUtils.getStackTrace(e));
            log.error("从视图库获取通知之后处理失败！{}", ExceptionUtils.getStackTrace(e));
        }
        responseStatuses.add(responseStatus);
        responseStatusList.setResponseStatusObject(responseStatuses);
        responseListRootBean.setResponseStatusListObject(responseStatusList);
        return new ResponseEntity<>(GsonUtil.GsonString(responseListRootBean), HttpStatus.OK);

    }
}
