package com.kedacom.haiou.kmtool.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/10/22.
 */
@Slf4j
public class TimeUtil {

    private static final SimpleDateFormat yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat yyyy_MM_dd_HH_mm_ss_SSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static Map<Integer, SimpleDateFormat> simpleDateFormatMap = new HashMap<>();

    static {
        simpleDateFormatMap.put(1, yyyy_MM_dd_HH_mm_ss);
        simpleDateFormatMap.put(2, yyyyMMddHHmmss);
        simpleDateFormatMap.put(3, yyyy_MM_dd);
        simpleDateFormatMap.put(4, yyyyMMdd);
        simpleDateFormatMap.put(5, yyyy_MM_dd_HH_mm_ss_SSS);
        simpleDateFormatMap.put(6, yyyyMMddHHmmssSSS);
    }

    public static String getNowDateStr(Integer type){
        SimpleDateFormat simpleDateFormat = simpleDateFormatMap.get(type);
        return simpleDateFormat.format(new Date());
    };

    public static Date parseDateStr(Integer type, String date) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatMap.get(type);
        try {
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            log.error("转换时间格式异常: {}", ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public static String formatDate(Integer type, Date date) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatMap.get(type);
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            log.error("转换时间格式异常: {}", ExceptionUtils.getStackTrace(e));
        }

        return null;
    }
}
