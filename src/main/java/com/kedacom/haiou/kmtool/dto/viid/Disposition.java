package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class Disposition implements Cloneable {
    private String DispositionID;
    private String Title;
    private String DispositionCategory;
    private String TargetName;
    private String TargetFeature;
    private String TargetImageURI;
    private int PriorityLevel;
    private String ApplicantName;
    private String ApplicantInfo;
    private String ApplicantOrg;
    private String BeginTime;
    private String EndTime;
    private String CreatTime;
    private int OperateType;
    private int DispositionStatus;
    private String DispositionRange;
    private String TollgateList;
    private String DispositionArea;
    private String DeviceList;
    private String ReceiveAddr;
    private String ReceiveMobile;
    private String Reason;
    private String DispositionRemoveOrg;
    private String DispositionRemovePerson;
    private String DispositionRemoveTime;
    private String DispositionRemoveReason;
    private String SelfDefData;
    private SubImageInfoList SubImageList;
    private FeatureInfo FeatureObject;
    private String ReturnImage;
    private int ReturnFeature;
    private String TabID;
    private int AlarmSensitivity;
    private Double Similaritydegree;
    private Double Tolerancedegree;
    private String IsWhite;
    private int ResultObj;
    private String DispositionAlgorithms;
    private String AreaExpands;
    private Integer AlarmSound;
    private String ResultImageDeclare;

    @Override
    public Disposition clone() {
        Disposition disposition = null;
        try {
            disposition = (Disposition) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return disposition;
    }
}