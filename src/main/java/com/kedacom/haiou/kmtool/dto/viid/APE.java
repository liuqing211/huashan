package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

/**
 * @Author: lwsmilence
 * @Date: 2019/10/10 18:58
 * @Last Modified by: lwsmilence
 * @Last Modified time: 2019/10/10 18:58
 * @Description:
 */
@Data
public class APE {
    private String ApeID;
    private String Name;
    private String Longitude;
    private String Latitude;
    private String OrgCode;
    private String OwnerApsID;
    private String Place;    // 位置名 (暂时想用作设备所在分组的上级分组)
    private String IsOnline;  // 1 在线、2 离线、9 其他
}
