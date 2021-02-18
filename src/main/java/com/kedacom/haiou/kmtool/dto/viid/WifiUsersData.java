package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class WifiUsersData {
    private String SourceID;
    private String WIFIDeviceID;
    private String DeviceID;
    private String DiscoverMacAddr;
    private String DiscoverSsidInf;
    private String HistorySsid;
    private String ImeiMsg;
    private String ImsiMsg;
    private String OSVersion;
    private String DiscoverDeviceManuf ;
    private String StartTime;
    private String EndTime;
    private String PhoneNo;
    private String WireDisX;
    private String WireDisY;
    private String WIFIEncryptType;
    private String APWifiChannel;
    private String MacAppearTimes;
    private String Power;
}
