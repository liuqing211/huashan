package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

import java.util.Date;

@Data
public class MotorVehicle {
    private String DeviceID;
    private String SourceID;
    private String MotorVehicleID;
    private String Direction;
    private String TollgateID;
    private int InfoKind;
    private int LeftTopX;
    private int LeftTopY;
    private int RightBtmX;
    private int RightBtmY;
    private String StorageUrl1;
    private String StorageUrl2;
    private String StorageUrl3;
    private String StorageUrl4;
    private String StorageUrl5;
    private String MarkTime;
    private String AppearTime;
    private String DisappearTime;
    private String HasPlate;
    private String PlateClass;
    private String PlateColor;
    private String PlateNo;
    private String PlateNoAttach;
    private String PlateDescribe;
    private String IsDecked;
    private String IsAltered;
    private String IsCovered;
    private int Speed;
    private String DrivingStatusCode;
    private String UsingPropertiesCode;
    private String VehicleClass;
    private String VehicleBrand;
    private String VehicleModel;
    private String VehicleStyles;
    private int VehicleLength;
    private int VehicleWidth;
    private int VehicleHeight;
    private String VehicleColor;
    private String VehicleColorDepth;
    private String VehicleHood;
    private String VehicleTrunk;
    private String VehicleWheel;
    private String WheelPrintedPattern;
    private String VehicleWindow;
    private String VehicleRoof;
    private String VehicleDoor;
    private int LaneNo;
    private String SideOfVehicle;
    private String CarOfVehicle;
    private String RearviewMirror;
    private String VehicleChassis;
    private String VehicleShielding;
    private String FilmColor;
    private String IsModified;
    private String HitMarkInfo;
    private String VehicleBodyDesc;
    private String VehicleFrontItem;
    private String DescOfFrontItem;
    private String VehicleRearItem;
    private String DescOfRearItem;
    private int NumOfPassenger;
    private String PassTime;
    private String NameOfPassedRoad;
    private String IsSuspicious;
    private int Sunvisor;
    private int SafetyBelt;
    private int Calling;
    private String PlateReliability;
    private Date PlateCharReliability;
    private String BrandReliability;
}