package com.kedacom.haiou.kmtool.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2021/1/28.
 */
@Data
@ToString
public class ImportantPerson {

    @SerializedName("create_time")
    private String createTime;

    @SerializedName("gxlx")
    private String gxlx; // 0-新增 1-修改 2-删除

    @SerializedName("hjd")
    private String hjd; // 户籍地

    @SerializedName("jg")
    private String jg; // 籍贯

    @SerializedName("mz")
    private String mz; // 民族

    @SerializedName("sfzh")
    private String sfzh; // 身份证

    @SerializedName("sjh")
    private String sjh; // 手机号

    @SerializedName("tx")
    private String tx; // 证件照

    @SerializedName("update_time")
    private String update_time; // 更新时间

    @SerializedName("xb")
    private String xb; // 性别

    @SerializedName("xm")
    private String xm; // 姓名

    @SerializedName("xzz")
    private String xzz; // 居住地
}
