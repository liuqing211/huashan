package com.kedacom.haiou.kmtool.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by Administrator on 2021/3/4.
 */
@Data
@ToString
public class SendProfileInfoLog {

    private String FaceID;
    private String IDNumber;
    private String Name;
    private String TabID;
    private String RelativeID;
    private String StoragePath;
    private String Id;
    private String AlgorithmId;
    private String Status;
    private String Reason;
    private String SendTime;
}
