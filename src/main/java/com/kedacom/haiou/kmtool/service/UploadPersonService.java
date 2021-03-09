package com.kedacom.haiou.kmtool.service;

import com.google.gson.Gson;
import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.UploadFileRespVO;
import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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

    @Value("${haioumate.addr}")
    private String haioumateUrl;

    @Value("${prefix.pictureUrl}")
    private String urlPrefix;

    @Value("${location.picturePath}")
    private String picturePath;

    @Value("${needCheckIDNumber}")
    private Boolean isNeedCheckIDNumber;

    @Value("${viewlib.batchNum}")
    private String batchNum;

    @Value("${pictureName.format}")
    private String pictureNameFormat;

    @Value("${pictureName.split}")
    private String pictureNameSplit;

    @Autowired
    private ViewlibFacade viewlibFacade;

    public String uploadLocalPic(String tabID, String location) {

        final String[] nameFormat = pictureNameFormat.split(pictureNameSplit);
        log.info("根据配置解析后的图片名称数组为：{}",Arrays.toString(nameFormat));
        int nameIndex = -1;
        int idNumberIndex = -1;
        for (int i = 0; i < nameFormat.length; i++) {
            if ("NAME".equals(nameFormat[i])){
                log.info("解析离线图片命名后获取姓名的下标为：{}", i);
                nameIndex = i;
            }
            if ("ID".equals(nameFormat[i])){
                log.info("解析离线图片命名后获取身份证号的下标为：{}", i);
                idNumberIndex = i;
            }
        }
        if (-1 == nameIndex || -1 == idNumberIndex){
            return "配置文件的图片格式不正确";
        }

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

                final String[] names = picture.getName().replace(".jpg", "").replace("JPG", "").replace(".png", "")
                        .replace("jpeg", "").trim().split(pictureNameSplit);

                if (nameFormat.length != names.length) {
                    log.info("{} 该文件命名格式不符合格式，跳过", picture.getAbsolutePath());
                    continue;
                }

                final String idNumber = names[idNumberIndex];
                final String name = names[nameIndex].trim();
                PersonBaseInfo personBaseInfo = new PersonBaseInfo();

                if (name.contains("（") || name.contains("）") || name.contains("(") || name.contains(")")) {
                    log.info("{} 该文件命名带括号，需格式化", picture.getAbsolutePath());
                    continue;
                }

                if (isNeedCheckIDNumber) {
                    //校验身份证
                } else {
                    personBaseInfo.setIdNumber(idNumber);
                    personBaseInfo.setName(name);
                }


                personBaseInfo.setImageID(IdFactory.ImageIDType());
                personBaseInfo.setRelativeID(IdFactory.faceIdType());

                String picUrl = null;
                if (isUploadPicture) {
                    //上传图片
                    picUrl = uploadPicture(picture);
                    log.info("上传图片至haioumate获取到的图片路径: {}", picUrl);
                } else {
                    // personBaseInfo.setPicUrl(picture.getPath().replace(picturePath.replace("/", "\\"), urlPrefix).replace("\\", "/"));
                    String localPicPath = picture.getPath();
                    log.info("获取到的图片路径为:" + localPicPath);
                    if (localPicPath.contains(picturePath)){
                        picUrl = localPicPath.replace(picturePath, haioumateUrl);
                        log.info("转换图片路径获取到的代理地址: {}", picUrl);
                    }
                }

                if (StringUtils.isEmpty(picUrl)){
                    log.error("图片地址为空，获取图片地址出现异常");
                    continue;
                }
                personBaseInfo.setPicUrl(picUrl);

                personBaseInfoList.add(personBaseInfo);

                if (personBaseInfoList.size() >= Integer.valueOf(batchNum)) {
                    boolean batchUploadResult = batchUploadPerson(personBaseInfoList, tabID);
                    /*if (batchUploadResult) {
                        personBaseInfoList.clear();
                    } else {
                        return "Failed";
                    }*/
                    personBaseInfoList.clear();
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

    private String uploadPicture(File picture) {
        if (null != picture) {
            Map<String, ContentBody> reqParam = new HashMap<>();
            reqParam.put("file", new FileBody(picture));
            reqParam.put("filename", new StringBody(picture.getName(), ContentType.MULTIPART_FORM_DATA));
            UploadFileRespVO uploadFileRespVO = new Gson().fromJson(ImageUtil.getImageUrl(haioumateUrl, reqParam), UploadFileRespVO.class);
            if (uploadFileRespVO != null && uploadFileRespVO.getExt() != null) {
                if (!StringUtils.isEmpty(uploadFileRespVO.getExt().getUrl())) {
                    log.info("图片上传成功，图片URL {}", uploadFileRespVO.getExt().getUrl());
                    return uploadFileRespVO.getExt().getUrl();
                }
            } else {
                log.error("{} 上传图片失败，上传图片返回值 {}" + uploadFileRespVO.toString());
            }
        }

        return null;
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


    public boolean batchUploadPerson(List<PersonBaseInfo> personBaseInfoList, String tabID) {
        List<String> idNumerList = personBaseInfoList.stream().map(PersonBaseInfo::getIdNumber).collect(Collectors.toList());
        String person_params = "?Person.TabID like .* &Person.IDNumber in (%s)&Fields=(%s)&PageRecordNum=%s";
        person_params = String.format(person_params, String.join(",", idNumerList), CommonConstant.PERSON_FIELDS, idNumerList.size() * 10);
        String face_params = "?Face.TabID=%s&Face.IDNumber in (%s)&Fields=(%s)&PageRecordNum=%s";
        face_params = String.format(face_params, tabID, String.join(",", idNumerList), CommonConstant.FACE_FIELDS, idNumerList.size() * 10);

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

    public static void main(String[] args) {
        String location = "D:/opt/avatar/image/";
        String regex = "http://xxx:xx/image/";
        String filePath = "D:/opt/avatar/image/xxx.jpg";

        String fileUrl = filePath.replace(location,regex);
        System.out.println(fileUrl);

    }
}


