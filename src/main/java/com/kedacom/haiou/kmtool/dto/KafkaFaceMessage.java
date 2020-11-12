package com.kedacom.haiou.kmtool.dto;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString
@Data
public class KafkaFaceMessage {

    private String imageId;
    private String imageContent;
    private String imageFormat;
    private String repoID;
    @Expose(serialize = false, deserialize = false)
    private Map<String, String> algorithmRepoMapping;
}
