package com.kedacom.haiou.kmtool.dto.vias;

import lombok.Data;

import java.util.List;

@Data
public class ImageObject {
    private String ImageID;
    private String StoragePath;
    private String Data;
    private List<FaceRect> FaceRect;
}
