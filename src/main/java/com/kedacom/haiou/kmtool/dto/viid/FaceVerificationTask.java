package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

/**
 * Created by litao on 2019/4/15.
 */
@Data
public class FaceVerificationTask {
    public int TaskID;  //任务标识
    public SubImageInfo ImageA;   //图像A
    public FeatureInfo FeatureA;  //特征值A
    public SubImageInfo ImageB;   //图像B
    public FeatureInfo FeatureB;  //特征值B
}
