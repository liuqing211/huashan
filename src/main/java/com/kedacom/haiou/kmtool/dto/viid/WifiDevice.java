package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class WifiDevice {
    private String WifiDeviceDataID;
    private String DeviceID;
    private String DeviceName;
    private String Model;
    private String IPAddr;
    private String IPV6Addr;
    private String GPSInfo;
    private Double Longitude;
    private Double Latitude;
    private String PlaceCode;
    private String Place;
    private String WifiProbeenable;
    private String CurrentTime;
    private String TimeZone;
    private String APScope;
    private String WorkPattern;
    private String WirelessDataFrame;
    private String NetworkAccessType;
    private String APSendCyc;
    private String DeviceMacAdress;
    private GPSData GPSData;
}
