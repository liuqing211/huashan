package com.kedacom.haiou.kmtool.dto.vias;

import lombok.Data;

import java.util.List;

@Data
public class ImageSearchedByImageBatch {
    private String SearchID;
    private int SearchTopNumber;
    private double Threshold;
    private String SearchType;
    private String ResultImageDeclare;
    private int ResultFeatureDeclare;
    private String TabIDList;
    private String NotFoundLoadIntoTabID;
}
