package com.kedacom.haiou.kmtool.dto.viid;

import java.util.List;

public class FaceList {

    private String scollID;

    private int RecordStartNo;

    private int PageRecordNum;

    private int TotalNum;

    private List<Face> FaceObject;

    public int getRecordStartNo() {
        return RecordStartNo;
    }

    public void setRecordStartNo(int recordStartNo) {
        RecordStartNo = recordStartNo;
    }

    public int getPageRecordNum() {
        return PageRecordNum;
    }

    public void setPageRecordNum(int pageRecordNum) {
        PageRecordNum = pageRecordNum;
    }

    public int getTotalNum() {
        return TotalNum;
    }

    public void setTotalNum(int totalNum) {
        TotalNum = totalNum;
    }

    public List<Face> getFaceObject() {
        return FaceObject;
    }

    public void setFaceObject(List<Face> faceObject) {
        FaceObject = faceObject;
    }

    public String getScollID() {
        return scollID;
    }

    public void setScollID(String scollID) {
        this.scollID = scollID;
    }
}