package com.kedacom.haiou.kmtool.dto.vias;

import lombok.Data;

@Data
public class FeatureAreaInfo {
    private String AreaID;
    private Integer LeftTopX;
    private Integer LeftTopY;
    private Integer RightBtmX;
    private Integer RightBtmY;
    private Integer Result;
    private String ImageID;
    private String Content;
    private String FeatureData;
}
