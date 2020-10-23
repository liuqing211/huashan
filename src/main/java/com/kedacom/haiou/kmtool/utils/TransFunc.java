package com.kedacom.haiou.kmtool.utils;

/**
 * Created by Administrator on 2017/4/25.
 */
public interface TransFunc<In, Out> {
    Out doTransform(In record)throws Exception;
    String getErroMessage(In record);
}
