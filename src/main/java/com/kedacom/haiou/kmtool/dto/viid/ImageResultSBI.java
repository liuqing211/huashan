package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class ImageResultSBI {
    private String SearchID;
    private int RecordStartNo;
    private int ReturnNum;
    private int TotalNum;
    private PersonList PersonObjectList;
    private FaceList FaceObjectList;
    private MotorVehicleList MotorVehicleObjectList;
    private NonMotorVehicleList NonMotorVehicleObjectList;
    private ThingList ThingObjectList;
    private SceneList SceneObjectList;
}
