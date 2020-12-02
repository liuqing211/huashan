package com.kedacom.haiou.kmtool.entity;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class HaiouRepository {

    private String id;
    private String name;
    private String description;
    private String enable;
    private Integer sort;
    private Float defaultSimilarity;
    private String nameSpelling = "";
    private String shortName;
    private String type = "0";
    private String belongUnit;
    private String creatorName;
    private String creatorOrg;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String createTime;
    private String updateTime;
    private String startTime;
    private String endTime;
    private String unAlarmGroup;
    private String unAlarmDevice;
    private String status;
    private long faceNum;
    private long personNum;
    private String blackType;

}
