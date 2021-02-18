package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class ImageSearchedByImage {
    private String SearchID;
    private int MaxNumRecordReturn;
    private int PageRecordNum;
    private int RecordStartNo;
    private SubImageInfo Image;
    private FeatureInfo Feature;
    private double Threshold;
    private String SearchType;
    private String QueryString;
    private String ResultImageDeclare;
    private int ResultFeatureDeclare;
    private String TabIDList;
    private String CityOwnerApsID;
}
