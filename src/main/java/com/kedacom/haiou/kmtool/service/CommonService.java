package com.kedacom.haiou.kmtool.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2021/3/4.
 */
@Slf4j
@Component
public class CommonService {

    public void waitAMoment() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.info("等待重试被中断");
        }

    }
}
