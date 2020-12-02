package com.kedacom.haiou.kmtool.entity;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2020/12/2.
 */
@Data
@ToString
public class DeleteStaticfaceLog {

    private String id;
    private String faceId;
    private String relativeId;
    private String tabId;
    private String idNumber;
    private String name;
    private String storagepath;
    private String deleteFlag;
    private String deleteTime;
}
