package com.kedacom.haiou.kmtool.dto.vias;

import lombok.Data;

import java.util.List;

@Data
public class FeatureExtrationResult {
    private String Content;
    private List<FeatureAreaInfo> FeatureObject;
}
