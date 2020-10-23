package com.kedacom.haiou.kmtool.dto.viid;

public class SubImageInfo {
    public String ImageID;
    public String EventSort;
    public String DeviceID;
    public String StoragePath;
    public String Type;
    public String FileFormat;
    public String ShotTime;
    public int Width;
    public int Height;
    public String Data;

    public String getImageID() {
        return ImageID;
    }

    public void setImageID(String imageID) {
        ImageID = imageID;
    }

    public String getEventSort() {
        return EventSort;
    }

    public void setEventSort(String eventSort) {
        EventSort = eventSort;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getStoragePath() {
        return StoragePath;
    }

    public void setStoragePath(String storagePath) {
        StoragePath = storagePath;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getFileFormat() {
        return FileFormat;
    }

    public void setFileFormat(String fileFormat) {
        FileFormat = fileFormat;
    }

    public String getShotTime() {
        return ShotTime;
    }

    public void setShotTime(String shotTime) {
        ShotTime = shotTime;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}