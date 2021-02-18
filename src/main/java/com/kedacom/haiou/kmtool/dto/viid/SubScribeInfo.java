package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class SubScribeInfo {

    private String SubscribeID;
    private String Title;
    private String SubscribeDetail;
    private String ResourceURI;
    private String ApplicantName;
    private String ApplicantOrg;
    private String BeginTime;
    private String EndTime;
    private String ReceiveAddr;
    private Integer ReportInterval;
    private String Reason;
    private Integer OperateType;
    private Integer SubscribeStatus;
    private String SubscribeCancelOrg;
    private String SubscribeCancelPerson;
    private String CancelTime;
    private String CancelReason;
    private String ResultImageDeclare;
    private Integer ResultFeatureDeclare;
    private String TabID;
}
