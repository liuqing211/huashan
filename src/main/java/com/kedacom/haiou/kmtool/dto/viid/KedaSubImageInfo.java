package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class KedaSubImageInfo {
    private String ImageID;
    private String StoragePath;
    private String Type;
    private String FileFormat;
    private String ShotTime;
    private int Width;
    private int Height;
    private int AlgorithmsStatus;
    private FeatureInfo FeatureInfoObject;
}
