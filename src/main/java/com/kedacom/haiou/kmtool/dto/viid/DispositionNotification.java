package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class DispositionNotification {
    private String NotificationID;
    private String DispositionID;
    private String Title;
    private String TriggerTime;
    private Person PersonObject;
    private MotorVehicle MotorVehicleObject;
    private NonMotorVehicle NonMotorVehicleObject;
    private Face FaceObject;
    private String Algorithm;
    private int AlarmStatus;
    private String AlarmHitPicID;
    private DispositionListObject DispositionListObject;
    private Integer AlarmType;
    // 新增部分
    private String DispositionTargetID;
    private String TargetFeature;
    // private KedaMotorVehicle KedaMotorVehicleObject;
    private KedaFace KedaFaceObject;
    private KedaPerson KedaPersonObject;
    private String FeedbackPerson;
    private String FeedbackRemark;
    private String FeedbackTime;
}
