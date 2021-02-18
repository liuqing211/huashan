package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class SubscribeNotification {
    private String NotificationID;
    private String SubscribeID;
    private String Title;
    private String TriggerTime;
    private String InfoIDs;
    private FaceList FaceObjectList;
    private PersonList PersonObjectList;
    private DataClassTabList DataClassTabObjectList;
    private KedaFaceList KedaFaceObjectList;
    private APEList DeviceList;
    private int ExecuteOperation;
    private WifiDataList WifiDataObjectList;
}
