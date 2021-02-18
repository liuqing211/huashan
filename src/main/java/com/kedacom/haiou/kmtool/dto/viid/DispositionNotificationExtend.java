package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

/**
 * 统一告警定义的告警消息体结构
 * 基于1400结构进行删减和扩展
 */
@Data
public class DispositionNotificationExtend {
    private String NotificationID;
    private String DispositionID;
    private String Title;
    private String TriggerTime;
    private String CntObjectID;
    private Person PersonObject;
    private String DispositionType; // 布控类型名称
    private String NotificationType;    // 告警类型（1:人员;2:车辆;0:其他）
    private String ReceiveAddr; // 告警信息接收地址URL
    private String ReceiveMobile;   // 告警信息接收手机号，多个号码间以英文半角分号;间隔
    private String ReceiveMail; // 告警邮件地址，多个以英文半角分号;间隔
    private Integer ReceiveType;    // 告警接收方类型（0:人; 1: 组织）
    private String ReceiveUserCode; // 告警接收人Code，多个以英文半角分号;间隔，长度256
    private String ReceiveUserName; // 告警接收人名称，多个以英文半角分号;间隔，长度256
    private String ReceiveOrgCode; // 告警接收组织Code，多个以英文半角分号;间隔，长度256
    private String ReceiveOrgName; // 告警接收组织名称，多个以英文半角分号;间隔，长度256
    private String Content; // 告警内容，长度1024
    private String sourceId;    // 告警来源ID
    private String Source;  // 告警来源
    private String Sound;   // 告警声音
    private String Url; // 告警url，点击告警后在浏览器打开该地址
    private String sourceInfo; // 告警来源信息，包含来源信息，业务系统自定义需要的信息，包含（坐标，地址等）
}
