package com.kedacom.haiou.kmtool.dto.viid;

public class FaceVerificationResultRoot {
    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    private String Content;
    private FaceVerificationResult FaceVerificationResultObject;

    public FaceVerificationResult getFaceVerificationResultObject() {
        return FaceVerificationResultObject;
    }

    public void setFaceVerificationResultObject(FaceVerificationResult faceVerificationResultObject) {
        FaceVerificationResultObject = faceVerificationResultObject;
    }


}