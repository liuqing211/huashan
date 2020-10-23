package com.kedacom.haiou.kmtool.utils;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

@Service
public class IdFactory {
    private static  String BasicObjectIdTypePIX = "12010000005030000000";    // 20位固定编码
    private static final String BusinessObjectIdTypePIX = "010000000050";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String generateBasicObjectIdType(String type){
        return BasicObjectIdTypePIX+type+getTimeString()+getLsString();
    }

    public static String faceIdType(){
        return BasicObjectIdTypePIX+"02"+getTimeString()+getLsString()+"06"+getLsString();
    }

    public static String sourceIdType(){
        return BasicObjectIdTypePIX+"02"+getTimeString()+getLsString();
    }

    public static String generateBusinessObjectIdType(String type){
        return BusinessObjectIdTypePIX+type+getTimeString()+getLsString();
    }

    public static String generateImageId(){
        return UUID.randomUUID().toString();
    }

    private static String getTimeString(){
        Calendar calendar = Calendar.getInstance();
        return  sdf.format(calendar.getTime());
    }




    private static String getLsString(){
        Random ra =new Random();
        int i = ra.nextInt(99999)%(99999-10000+1) + 10000;
        return  String.valueOf(i);
    }

    public static String getUUIDString(){
        String s = UUID.randomUUID().toString();
        return s.substring(0,8) + s.substring(9,13) + s.substring(14,18) + s.substring(19,23) + s.substring(24);
    }

    public static int getTaskId(){
        int a =  new Random().nextInt(999999999) % (999999999-100000000+1) + 100000000;
        return a;
    }

    public static String ImageIDType() {
        return BasicObjectIdTypePIX + "02" + getTimeString() + getLsString();
    }

    public static String RelativeIDType() {
        Random ra =new Random();
        int i = ra.nextInt(999999999) % (999999999-100000000+1) + 100000000;
        return BasicObjectIdTypePIX + "02" + getTimeString() + String.valueOf(i);
    }




}
