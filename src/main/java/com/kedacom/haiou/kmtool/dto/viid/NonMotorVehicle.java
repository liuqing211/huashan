package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class NonMotorVehicle {
    private String SourceID;
    private String NonMotorVehicleID;
    private int InfoKind;
    private int LeftTopX;
    private int LeftTopY;
    private int RightBtmX;
    private int RightBtmY;
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
    private String VehicleBrand;
    private int VehicleLength;
    private int VehicleWidth;
    private int VehicleHeight;
    private String VehicleColor;
    private String VehicleHood;
    private String VehicleTrunk;
    private String VehicleWheel;
    private String WheelPrintedPattern;
    private String VehicleWindow;
    private String VehicleRoof;
    private String VehicleDoor;
    private String VehicleType;
    private String SideOfVehicle;
    private String CarOfVehicle;
    private String RearviewMirror;
    private String VehicleChassis;
    private String VehicleShielding;
    private String FilmColor;
    private String IsModified;
}