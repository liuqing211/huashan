package com.kedacom.haiou.kmtool.entity;

import lombok.Data;

@Data
public class ProfileFaceMessage {

    private String Id;
    private String FaceID;
    private String StoragePath;
    private String RelativeID;
    private String TabID;
    private String IDNumber;
    private String Name;
    private String EntryTime;
    private String Base64;
    private String AlgorithmId;
    private String AlgorithmRepositoryId;

}
