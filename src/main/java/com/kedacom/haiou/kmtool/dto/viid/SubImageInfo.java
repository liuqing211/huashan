package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class SubImageInfo {
    private String ImageID;
    private String DeviceID;
    private String StoragePath;
    private String Type;
    private String FileFormat;
    private String Data;
    private String ShotTime;
    private int Width;
    private int Height;
    private FeatureInfo FeatureInfoObject;
    private Integer AlgorithmsStatus;
}