package com.kedacom.haiou.kmtool.dto;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString
@Data
public class KafkaFaceMessage {


    private String imageID;
    private String imageContent;
    private String imageFormat;
    private String repoID;
    private String name;
    private String idNumber;

}
