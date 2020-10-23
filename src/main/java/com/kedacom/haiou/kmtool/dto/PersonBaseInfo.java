package com.kedacom.haiou.kmtool.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2020/10/22.
 */
@Data
@ToString
public class PersonBaseInfo {

    private String imageID;
    private String relativeID;
    private String idNumber;
    private String name;
    private String picUrl;
}
