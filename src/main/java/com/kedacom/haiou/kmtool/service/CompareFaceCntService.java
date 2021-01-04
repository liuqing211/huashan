package com.kedacom.haiou.kmtool.service;

import com.google.gson.Gson;
import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.dto.UploadFileRespVO;
import com.kedacom.haiou.kmtool.dto.viid.*;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.IdFactory;
import com.kedacom.haiou.kmtool.utils.ImageUtil;
import com.kedacom.haiou.kmtool.utils.PictureUtil;
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
import java.util.stream.Stream;

/**
 * Created by Administrator on 2020/10/22.
 */
@Service
@Slf4j
public class CompareFaceCntService {

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


    public boolean batchUploadFace(List<PersonBaseInfo> personBaseInfoList, String tabId) {
        Set<String> idNumberList = personBaseInfoList.stream().map(PersonBaseInfo::getIdNumber).collect(Collectors.toSet());
        List<Set<String>> idNunmberLists = new ArrayList<>();
        if (idNumberList.size() > Integer.valueOf(batchNum)) {
            final Integer limit = (idNumberList.size() + Integer.valueOf(batchNum) - 1) / Integer.valueOf(batchNum);
            Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
                idNunmberLists.add(idNumberList.stream().skip(i * Integer.valueOf(batchNum)).limit(Integer.valueOf(batchNum)).collect(Collectors.toSet()));
            });
        } else {
            idNunmberLists.add(idNumberList);
        }

        List<Person> existPersonList = new ArrayList<>();
        for (Set<String> idNumbers : idNunmberLists) {
            String personParams = "?Person.IDNumber in (%s)&Person.TabID like .*.*&PageRecordNum=%s&Fields=%s";
            personParams = String.format(personParams, String.join(",", idNumbers), idNumberList.size() * 10, "IDNumber,Name,TabID,PersonID");
            List<Person> existPersons = viewlibFacade.getPersons(personParams);
            existPersonList.addAll(existPersons);
        }

        List<Person> addPerson = new ArrayList<>();
        List<Face> addFace = new ArrayList<>();
        for (PersonBaseInfo personBaseInfo : personBaseInfoList) {
            final String idNumber = personBaseInfo.getIdNumber();
            List<Person> idNumberExistList = new ArrayList<>();

            for (Person person : existPersonList) {
                if (idNumber.equals(person.getIDNumber())) {
                    idNumberExistList.add(person);
                }
            }

            if (CollectionUtils.isEmpty(idNumberExistList) && StringUtils.isNotEmpty(idNumber)) {
                log.info("该身份证号 {} 在 staticperson 中不存在，需新增", idNumber);
                Person person = ConvertUtil.convertBaseInfoToPerson(personBaseInfo);
                Face face = ConvertUtil.convertBaseInfoToFace(personBaseInfo, tabId);
                addPerson.add(person);
                addFace.add(face);
            } else {
                List<String> tabIdList = idNumberExistList.stream().map(Person::getTabID).collect(Collectors.toList());
                if (tabIdList.contains("334c")) {
                    idNumberExistList.forEach(person -> {
                        if (idNumber.equals(person.getIDNumber()) && tabId.equals(person.getTabID())) {
                            personBaseInfo.setRelativeID(person.getPersonID());
                        }
                    });
                } else {
                    personBaseInfo.setRelativeID(idNumberExistList.get(0).getPersonID());
                }

                Face face = ConvertUtil.convertBaseInfoToFace(personBaseInfo, tabId);
                addFace.add(face);
            }
        }

        PersonList persons = new PersonList();
        FaceList faces = new FaceList();
        if (!CollectionUtils.isEmpty(addPerson)) {
            persons.setPersonObject(addPerson);
            boolean addPersonResult = viewlibFacade.addPersons(persons);
            log.info("{} 录入视图库是否成功 {}", persons.toString(), addPersonResult);
            if (!addPersonResult) {
                return false;
            }
        }

        if (!CollectionUtils.isEmpty(addFace)) {
            faces.setFaceObject(addFace);
            boolean addFaceResult = viewlibFacade.addFaces(faces);
            log.info("{} 录入视图库是否成功 {}", faces.toString(), addFaceResult);
            if (!addFaceResult) {
                return false;
            }
        }

        return true;
    }
}


