package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

import java.util.List;

/**
 * @Author: lwsmilence
 * @Date: 2019/8/13 14:18
 * @Last Modified by: lwsmilence
 * @Last Modified time: 2019/8/13 14:18
 * @Description:
 */

@Data
public class KedaPersonListObject {
    private int TotalNum;
    private List<KedaPerson> KedaPersonObject;
}
