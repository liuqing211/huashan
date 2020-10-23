package com.kedacom.haiou.kmtool.utils;

import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.Person;
import com.kedacom.haiou.kmtool.dto.viid.SubImageInfo;
import com.kedacom.haiou.kmtool.dto.viid.SubImageInfoList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/10/22.
 */
@Slf4j
public class ConvertUtil {

    public static Person convertBaseInfoToPerson(PersonBaseInfo personBaseInfo) {
        Person person = new Person();
        try {
            person.setPersonID(personBaseInfo.getRelativeID());
            person.setSourceID(IdFactory.sourceIdType());
            person.setName(personBaseInfo.getName());
            person.setIDNumber(personBaseInfo.getIdNumber());
            person.setTabID("0");
            person.setLocationMarkTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            person.setInfoKind(0);
            person.setLeftTopX(1);
            person.setLeftTopY(1);
            person.setRightBtmX(1);
            person.setRightBtmY(1);
            person.setIsSuspectedTerrorist(0);
            person.setIsCriminalInvolved(0);
            person.setIsDetainees(0);
            person.setIsVictim(0);
            person.setIsSuspiciousPerson(0);

            SubImageInfoList personSubImageInfoList = new SubImageInfoList();
            List<SubImageInfo> personSubImageInfoObject = new ArrayList<>();
            SubImageInfo personSubImageInfo = new SubImageInfo();
            personSubImageInfo.setImageID(personBaseInfo.getImageID());
            personSubImageInfo.setStoragePath(personBaseInfo.getPicUrl());
            personSubImageInfo.setShotTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            personSubImageInfo.setType("100");
            personSubImageInfo.setFileFormat("Jpeg");
            personSubImageInfoObject.add(personSubImageInfo);
            personSubImageInfoList.setSubImageInfoObject(personSubImageInfoObject);
            person.setSubImageList(personSubImageInfoList);
        } catch (Exception e) {
            log.error("PersonBaseInfo 转换 Person 异常：{}", ExceptionUtils.getStackTrace(e));
        }

        return person;
    }

    public static Face convertBaseInfoToFace(PersonBaseInfo personBaseInfo, String tabID) {
        Face face = new Face();
        try {
            face.setRelativeID(personBaseInfo.getRelativeID());
            face.setFaceID(IdFactory.faceIdType());
            face.setIDNumber(personBaseInfo.getIdNumber());
            face.setName(personBaseInfo.getName());
            face.setTabID(tabID);
            face.setShotTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            face.setInfoKind(2);
            face.setLeftTopX(1);
            face.setLeftTopY(1);
            face.setRightBtmX(1);
            face.setRightBtmY(1);
            face.setIsSuspectedTerrorist(0);
            face.setIsCriminalInvolved(0);
            face.setIsDetainees(0);
            face.setIsVictim(0);
            face.setIsSuspiciousPerson(0);
            SubImageInfoList SubImageInfoList = new SubImageInfoList();
            List<SubImageInfo> SubImageInfoObject = new ArrayList<>();
            SubImageInfo subImageInfo = new SubImageInfo();
            subImageInfo.setImageID(personBaseInfo.getImageID());
            subImageInfo.setStoragePath(personBaseInfo.getPicUrl());
            subImageInfo.setShotTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            subImageInfo.setType("11");
            subImageInfo.setFileFormat("Jpeg");
            SubImageInfoObject.add(subImageInfo);
            SubImageInfoList.setSubImageInfoObject(SubImageInfoObject);
            face.setSubImageList(SubImageInfoList);
        } catch (Exception e) {
            log.error("PersonBaseInfo 转换 Face 异常：{}", ExceptionUtils.getStackTrace(e));
        }
        return face;
    }
}
