package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

/**
 * Created by litao on 2019/4/15.
 */
@Data
public class FaceVerificationResult {
    public int TaskID;
    public int Result;
    public Double Similarity;
}
