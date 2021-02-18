package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

/**
 * @Author: lwsmilence
 * @Date: 2019/8/13 9:27
 * @Last Modified by: lwsmilence
 * @Last Modified time: 2019/8/13 9:27
 * @Description:
 */

@Data
public class KedaPerson {
    private String PersonID;
    private int InfoKind;
    private String SourceID;
    private String DeviceID;
    private String LocationMarkTime;
    private String PersonAppearTime;
    private String PersonDisAppearTime;
    private int SourceType;    // 0:单张图片分析，1：视频分析
    private String BestPersonImageID;
    private String BestFaceImageID;
    private String Extend1;
    private String Extend2;
    private int IsDrivingNonMotorVehicle;
    private int Riding;
    private int HasBag;
    private int HasGlass;
    private int HasHat;
    private int HasMask;
    private int RightBtmX;
    private int RightBtmY;
    private int LeftTopX;
    private int LeftTopY;
    private String AnalysisObjID;
    private String GenderCode;
    private String RaceCode;
    private String TrousersColor;
    private String CoatColor;
    private int AgeUpLimit;
    private int AgeLowerLimit;
    private String ShotTime;
    private int FaceQuality;
    private KedaSubImageInfoList KedaSubImageList;
}
