package com.kedacom.haiou.kmtool.dto.viid;

/**
 * @author : zhouyang
 * @date : 2019/3/19
 */
public class FacesNumRoot {

    private FaceListObjectEntity FaceListObject;

    public void setFaceListObject(FaceListObjectEntity FaceListObject) {
        this.FaceListObject = FaceListObject;
    }

    public FaceListObjectEntity getFaceListObject() {
        return FaceListObject;
    }

    public class FaceListObjectEntity {
        private int TotalNum;

        public void setTotalNum(int TotalNum) {
            this.TotalNum = TotalNum;
        }

        public int getTotalNum() {
            return TotalNum;
        }
    }
}
