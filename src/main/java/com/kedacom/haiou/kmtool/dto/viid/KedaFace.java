package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class KedaFace {
    private String FaceID;
    private Integer InfoKind;
    private String SourceID;
    private String ShotTime;
    private String DeviceID;
    private Integer LeftTopX;
    private Integer LeftTopY;
    private Integer RightBtmX;
    private Integer RightBtmY;
    private String FaceAppearTime;
    private String FaceDisAppearTime;
    private Integer SourceType;
    private String LandMarks;
    private double Yaw;
    private double Pitch;
    private double Roll;
    private double Blur;
    private String RelativeTabId;
    private String RelativeID;
    private String Extend1;
    private String Extend2;
    private Integer HasHat;
    private Integer HasMask;
    private Integer HasGlass;
    private String GenderCode;
    private Integer AgeLowerLimit;
    private Integer AgeUpLimit;
    private KedaSubImageInfoList KedaSubImageList;
}

