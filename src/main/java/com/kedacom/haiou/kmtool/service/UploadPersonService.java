package com.kedacom.haiou.kmtool.service;

import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.IdFactory;
import com.kedacom.haiou.kmtool.utils.PictureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2020/10/22.
 */
@Service
@Slf4j
public class UploadPersonService {

    @Value("${flag.uploadPicture}")
    private Boolean isUploadPicture;

    @Value("${prefix.pictureUrl}")
    private String urlPrefix;

    @Value("${location.picturePath}")
    private String picturePath;

    @Value("${needCheckIDNumber}")
    private Boolean isNeedCheckIDNumber;

    @Value("${viewlib.batchNum}")
    private String batchNum;

    @Autowired
    private ViewlibFacade viewlibFacade;

    public String uploadLocalPic(String tabID, String location) {

        final File picLocation = new File(location);
        if (!picLocation.isDirectory() || !picLocation.exists()) {
            return "输入的本地图片路径不合法";
        }

        final List<DataClassTab> dataClassTabList = viewlibFacade.getDataClassTab(tabID);
        if (CollectionUtils.isEmpty(dataClassTabList)) {
            return "输入的人员库ID不存在";
        }

        List<PersonBaseInfo> personBaseInfoList = new ArrayList<>();
        List<File> pictureList = new ArrayList<>();
        getPictures(picLocation, pictureList);
        log.info("本次共需上传图片 {} 张", pictureList.size());
        for (File picture : pictureList) {
            if (null != picture && picture.isFile()) {
                log.info("开始上传文件：{}", picture.getAbsolutePath());
                if (!picture.isFile() || !PictureUtil.isPicture(picture)) {
                    log.info("{} 该文件不是图片，跳过", picture.getAbsolutePath());
                    continue;
                }

                final String[] names = picture.getName().split("_");
                if (5 > names.length) {
                    log.info("{} 该文件命名格式不符合格式，跳过", picture.getAbsolutePath());
                    continue;
                }

                final String idNumber = names[3].trim();
                final String name = names[4].replace(".jpg", "").replace("JPG", "").replace(".png", "").trim();
                PersonBaseInfo personBaseInfo = new PersonBaseInfo();
                if (isNeedCheckIDNumber) {
                    //校验身份证
                } else {
                    personBaseInfo.setIdNumber(idNumber);
                    personBaseInfo.setName(name);
                }


                personBaseInfo.setImageID(IdFactory.ImageIDType());
                personBaseInfo.setRelativeID(IdFactory.faceIdType());

                if (isUploadPicture) {
                    //上传图片
                } else {
                    personBaseInfo.setPicUrl(picture.getPath().replace(picturePath.replace("/", "\\"), urlPrefix).replace("\\", "/"));
                }
                personBaseInfoList.add(personBaseInfo);

                if (personBaseInfoList.size() >= Integer.valueOf(batchNum)) {
                    boolean batchUploadResult = batchUploadPerson(personBaseInfoList, tabID);
                    if (batchUploadResult) {
                        personBaseInfoList.clear();
                    } else {
                        return "Failed";
                    }
                }
            }

        }

        boolean batchUploadResult = batchUploadPerson(personBaseInfoList, tabID);
        if (batchUploadResult) {
            return "Success";
        } else {
            return "Failed";
        }

    }

    private File getPicture(File picture) {
        if (null != picture) {
            File[] pictures = picture.listFiles();
            if (null != pictures && pictures.length > 0) {
                for (File picFile : pictures) {
                    if (picFile.isFile()) {
                        return picFile;
                    } else {
                        return getPicture(picFile);
                    }
                }
            }
        }

        return null;
    }


    private boolean batchUploadPerson(List<PersonBaseInfo> personBaseInfoList, String tabID) {
        List<String> idNumerList = personBaseInfoList.stream().map(PersonBaseInfo::getIdNumber).collect(Collectors.toList());
        String pageNum = "&PageRecordNum=" + idNumerList.size() * 10;
        String person_params = "?Person.TabID=0&Person.IDNumber in (" + String.join(",", idNumerList) + ")" + pageNum;
        String face_params = "?Face.TabID=" + tabID + "&Face.IDNumber in (" + String.join(",", idNumerList) + ")" + pageNum;

        List<Person> existPersons = viewlibFacade.getPersons(person_params);
        List<String> existPersonIDNumberList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(existPersons)) {
            existPersonIDNumberList = existPersons.stream().map(Person::getIDNumber).collect(Collectors.toList());
        }

        List<Face> existFaces = viewlibFacade.getFaces(face_params);//查询出在CDB中已有的Face集合
        List<String> existFaceIDNumberList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(existFaces)) {
            existFaceIDNumberList = existFaces.stream().map(Face::getIDNumber).collect(Collectors.toList());
        }


        List<Person> personList = new ArrayList<>();
        List<Face> faceList = new ArrayList<>();
        PersonList persons = new PersonList();
        FaceList faces = new FaceList();

        for (PersonBaseInfo personBaseInfo : personBaseInfoList) {
            final String idNumber = personBaseInfo.getIdNumber();
            if (CollectionUtils.isEmpty(existPersonIDNumberList) || !existPersonIDNumberList.contains(idNumber)) {
                Person person = ConvertUtil.convertBaseInfoToPerson(personBaseInfo);
                if (null != person) {
                    personList.add(person);
                }
            }

            if (CollectionUtils.isEmpty(existFaceIDNumberList) || !existFaceIDNumberList.contains(idNumber)) {
                if (!CollectionUtils.isEmpty(existPersonIDNumberList) && existPersonIDNumberList.contains(idNumber)) {
                    existPersons.forEach(Person -> {
                        if (idNumber.equals(Person.getIDNumber())) {
                            personBaseInfo.setRelativeID(Person.getPersonID());
                        }
                    });
                }
                Face face = ConvertUtil.convertBaseInfoToFace(personBaseInfo, tabID);
                if (null != face) {
                    faceList.add(face);
                }
            }

        }

        if (!CollectionUtils.isEmpty(personList)) {
            persons.setPersonObject(personList);
            boolean addPersonResult = viewlibFacade.addPersons(persons);
            log.info("{} 录入视图库是否成功 {}", persons.toString(), addPersonResult);
            if (!addPersonResult) {
                return false;
            }
        }

        if (!CollectionUtils.isEmpty(faceList)) {
            faces.setFaceObject(faceList);
            boolean addFaceResult = viewlibFacade.addFaces(faces);
            log.info("{} 录入视图库是否成功 {}", faces.toString(), addFaceResult);
            if (!addFaceResult) {
                return false;
            }
        }

        return true;
    }

    public void getPictures(File baseFile, List<File> pictureList) {
        if (baseFile.isFile() && PictureUtil.isPicture(baseFile)) {
            pictureList.add(baseFile);
        } else if (baseFile.isDirectory()) {
            File[] secFiles = baseFile.listFiles();
            for (File secFile : secFiles) {
                getPictures(secFile, pictureList);
            }
        }
    }

     /*public static void main(String[] args) {
       List<File> pictureList = new ArrayList<>();
        File baseDir = new File("E:\\soft\\image\\new");
        UploadPersonService uploadPersonService = new UploadPersonService();
        uploadPersonService.getPictures(baseDir, pictureList);

        System.out.println("size:" + pictureList.size());

        pictureList.forEach(File -> {
            System.out.println(File.getPath());
        });

        String name = "1___110102194909221131_李永中";
        System.out.println(name.split("_").length);

    }*/

}
