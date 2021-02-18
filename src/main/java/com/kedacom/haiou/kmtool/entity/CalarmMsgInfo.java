package com.kedacom.haiou.kmtool.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by Administrator on 2021/1/22.
 */
@Data
@Setter
@Getter
public class CalarmMsgInfo {

    private Integer id;
    private String notificationId;
    private String dispositionId;
    private Date triggerTime;
    private String triggerTimeStr;

}
