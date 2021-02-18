package com.kedacom.haiou.kmtool.utils;

import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.Person;
import com.kedacom.haiou.kmtool.dto.viid.SubImageInfo;
import com.kedacom.haiou.kmtool.dto.viid.SubImageInfoList;
import com.kedacom.haiou.kmtool.entity.ImportantPerson;
import com.kedacom.haiou.kmtool.entity.PasserbyPic2Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Time;
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


    public static Person convertImportantPersonToPerson(ImportantPerson importantPerson) {
        Person person = new Person();
        try {
            person.setPersonID(IdFactory.RelativeIDType());
            person.setSourceID(IdFactory.sourceIdType());
            person.setName(importantPerson.getXm());
            person.setIDNumber(importantPerson.getSfzh());
            person.setTabID("0");

            if (StringUtils.isNotEmpty(importantPerson.getJg())) {
                person.setNativeCityCode(importantPerson.getJg());
            }

            if (StringUtils.isNotEmpty(importantPerson.getXb())) {
                switch (importantPerson.getXb()){
                    case "男":
                        person.setGenderCode("1");
                        break;

                    case "女":
                        person.setGenderCode("2");
                        break;
                }
            }

            person.setPhoneNo(importantPerson.getSjh());
            person.setFamilyAddress(importantPerson.getXzz());
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
            personSubImageInfo.setImageID(IdFactory.ImageIDType());
            personSubImageInfo.setStoragePath(importantPerson.getTx());
            personSubImageInfo.setShotTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            personSubImageInfo.setType("100");
            personSubImageInfo.setFileFormat("Jpeg");
            personSubImageInfoObject.add(personSubImageInfo);
            personSubImageInfoList.setSubImageInfoObject(personSubImageInfoObject);
            person.setSubImageList(personSubImageInfoList);
        } catch (Exception e) {
            log.error("Important 转换 Person 异常：{}", ExceptionUtils.getStackTrace(e));
        }

        return person;
    }

    public static Face convertImportantPersonToFace(ImportantPerson importantPerson, Person person, String tabId) {
        Face face = new Face();
        try {
            face.setRelativeID(person.getPersonID());
            face.setFaceID(IdFactory.faceIdType());
            face.setIDNumber(importantPerson.getSfzh());
            face.setName(importantPerson.getXm());
            face.setTabID(tabId);

            if (StringUtils.isNotEmpty(importantPerson.getJg())) {
                face.setNativeCityCode(importantPerson.getJg());
            }

            if (StringUtils.isNotEmpty(importantPerson.getXb())) {
                switch (importantPerson.getXb()){
                    case "男":
                        face.setGenderCode("1");
                        break;

                    case "女":
                        face.setGenderCode("2");
                        break;
                }
            }

            face.setFamilyAddress(importantPerson.getXzz());
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
            subImageInfo.setImageID(IdFactory.ImageIDType());
            subImageInfo.setStoragePath(importantPerson.getTx());
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

    public static PasserbyPic2Algorithm convertFaceToPasserbyPic2Algorithm(Face face) {
        PasserbyPic2Algorithm passerbyPic2Algorithm = new PasserbyPic2Algorithm();

        passerbyPic2Algorithm.setDeviceID(face.getDeviceID());
        passerbyPic2Algorithm.setImageID(face.getFaceID());
        Date shotTime = TimeUtil.parseDateStr(2, face.getShotTime());
        passerbyPic2Algorithm.setShotTime(TimeUtil.formatDate(1, shotTime));
        passerbyPic2Algorithm.setTop(10);

        List<SubImageInfo> subImageInfoObject = face.getSubImageList().getSubImageInfoObject();
        subImageInfoObject.forEach(subImageInfo -> {
            if (subImageInfo.getType().equals("11")) {
                passerbyPic2Algorithm.setData(PictureUtil.ImageToBase64(subImageInfo.getStoragePath()));
                passerbyPic2Algorithm.setFaceImageUrl(subImageInfo.getStoragePath());
                passerbyPic2Algorithm.setImageSize(passerbyPic2Algorithm.getData().length());
            } else if (subImageInfo.getType().equals("14")) {
                passerbyPic2Algorithm.setOverviewImageUrl(subImageInfo.getStoragePath());
            }
        });

        return passerbyPic2Algorithm;
    }
}
