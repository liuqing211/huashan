package com.kedacom.haiou.kmtool.utils;

import com.kedacom.haiou.kmtool.dto.KafkaFaceMessage;
import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.SendProfileInfoLog;
import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.entity.ImportantPerson;
import com.kedacom.haiou.kmtool.entity.PasserbyPic2Algorithm;
import com.kedacom.haiou.kmtool.entity.ProfileFaceMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
                switch (importantPerson.getXb()) {
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
                switch (importantPerson.getXb()) {
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

    /*public static UpdateFaceLog convertFaceToUpdateFaceLog(Face face) {
        UpdateFaceLog updateFaceLog = new UpdateFaceLog();

        return updateFaceLog;
    }*/

    public static SendProfileInfoLog convertFaceToSendProfileInfoLog(Map params) {
        SendProfileInfoLog sendProfileInfoLog = new SendProfileInfoLog();

        Face face = (Face) params.get("FaceObject");
        sendProfileInfoLog.setFaceID(face.getFaceID());
        sendProfileInfoLog.setIDNumber(face.getIDNumber());
        sendProfileInfoLog.setName(face.getName());
        sendProfileInfoLog.setTabID(face.getTabID());
        sendProfileInfoLog.setRelativeID(face.getRelativeID());
        sendProfileInfoLog.setStoragePath(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath());

        sendProfileInfoLog.setId((String) params.get("Id"));
        sendProfileInfoLog.setAlgorithmId((String) params.get("AlgorithmId"));
        sendProfileInfoLog.setStatus((String) params.get("Status"));
        sendProfileInfoLog.setReason((String) params.get("Reason"));
        sendProfileInfoLog.setSendTime((String) params.get("SendTime"));

        return sendProfileInfoLog;
    }

    public static KafkaFaceMessage convertFaceToKafkaMessage(Face face) throws Exception {


        KafkaFaceMessage kafkaFaceMessage = new KafkaFaceMessage();
        kafkaFaceMessage.setImageID(face.getFaceID());
        // kafkaFaceMessage.setImageContent(CommonHelper.ImageToBase64(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath()));

        String storagePath = face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath();
        String filePath = storagePath.replace("http://86.81.131.48:8090/images/", "D:\\store\\images\\").replaceAll("/", "\\\\");
        File picFile = new File(filePath);
        if (!picFile.isFile() || !PictureUtil.isPicture(picFile)) {
            log.info("该文件 {} 不是图片", picFile.getAbsolutePath());
            return null;
        }
        String base64Str = PictureUtil.ImgToBase64(picFile);
        if (StringUtils.isEmpty(base64Str)) {
            log.info("图片base64为空");
            return null;
        }
        kafkaFaceMessage.setImageContent(base64Str);

        kafkaFaceMessage.setImageFormat("image/jpg");
        kafkaFaceMessage.setIdNumber(face.getIDNumber());
        kafkaFaceMessage.setName(face.getName());
        kafkaFaceMessage.setRepoID(face.getTabID());

        return kafkaFaceMessage;
    }

    public static KafkaFaceMessage convertFaceToKafkaMessage(Face face, String algRepoId) throws Exception {


        KafkaFaceMessage kafkaFaceMessage = new KafkaFaceMessage();
        kafkaFaceMessage.setImageID(face.getFaceID());
        kafkaFaceMessage.setImageContent(CommonHelper.ImageToBase64(face.getSubImageList().getSubImageInfoObject().get(0).getStoragePath()));
        kafkaFaceMessage.setImageFormat("image/jpg");
        kafkaFaceMessage.setIdNumber(face.getIDNumber());
        kafkaFaceMessage.setName(new String(face.getName().getBytes(), "UTF-8"));
        kafkaFaceMessage.setRepoID(algRepoId);

        return kafkaFaceMessage;
    }

    public static List<KafkaFaceMessage> converFaceListTokafkaFaceMessageList(List<Face> sendFaceList) {

        List<KafkaFaceMessage> kafkaFaceMessageList = new ArrayList<>();
        for (Face face : sendFaceList) {
            KafkaFaceMessage kafkaFaceMessage = null;
            try {
                kafkaFaceMessage = convertFaceToKafkaMessage(face);
            } catch (Exception e) {
                log.error("转换 KafkaFaceMessage 对象异常: {}", ExceptionUtils.getStackTrace(e));
                continue;
            }
            kafkaFaceMessageList.add(kafkaFaceMessage);
        }

        return kafkaFaceMessageList;
    }

    public static KafkaFaceMessage convertPFMToKafkaFaceMessage(ProfileFaceMessage profileFaceMessage, String algorithmRepoId) {
        KafkaFaceMessage kafkaFaceMessage = new KafkaFaceMessage();
        kafkaFaceMessage.setImageID(profileFaceMessage.getFaceID());

        String storagePath = profileFaceMessage.getStoragePath();
        String filePath = storagePath.replace("http://86.81.131.48:8090/images/", "D:\\store\\images\\").replaceAll("/", "\\\\");
        File picFile = new File(filePath);
        if (!picFile.isFile() || !PictureUtil.isPicture(picFile)) {
            log.info("该文件 {} 不是图片", picFile.getAbsolutePath());
            return null;
        }
        String base64Str = PictureUtil.ImgToBase64(picFile);
        if (StringUtils.isEmpty(base64Str)) {
            log.info("图片base64为空");
            return null;
        }
        kafkaFaceMessage.setImageContent(base64Str);

        kafkaFaceMessage.setImageFormat("image/jpg");
        kafkaFaceMessage.setIdNumber(profileFaceMessage.getIDNumber());
        kafkaFaceMessage.setName(profileFaceMessage.getName());
        kafkaFaceMessage.setRepoID(algorithmRepoId);

        return kafkaFaceMessage;
    }
}
