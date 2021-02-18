package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

import java.util.List;

@Data
public class KedaFaceListObject {
    private int TotalNum;
    private List<KedaFace> KedaFaceObject;
}
