package com.kedacom.haiou.kmtool.consumer;

import com.kedacom.haiou.kmtool.dto.viid.Face;
import com.kedacom.haiou.kmtool.dto.viid.FaceList;
import com.kedacom.haiou.kmtool.dto.viid.Person;
import com.kedacom.haiou.kmtool.dto.viid.PersonList;
import com.kedacom.haiou.kmtool.entity.ImportantPerson;
import com.kedacom.haiou.kmtool.service.lib.ViewlibFacade;
import com.kedacom.haiou.kmtool.utils.ConvertUtil;
import com.kedacom.haiou.kmtool.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2021/1/28.
 */
@Slf4j
@Component
public class ImportantPersonConsumer {

    @Value("${tabId.xzrz.syncImportantPerson}")
    private String tabId;

    @Autowired
    private ViewlibFacade viewlibFacade;

    private String faceFields = "FaceID,RelativeID,IDNumber,Name,TabID,SubImageList.ImageID,SubImageList.StoragePath";
    private String personFields = "PersonID,IDNumber,Name,TabID,EntryTime,SubImageList.ImageID,SubImageList.StoragePath";

    // @KafkaListener(topics = "${topic.xzrz.syncImportantPerson}", containerFactory = "kafkaXzrzContainerFactory")
    public void listenerImportantPerson(ConsumerRecord<byte[], byte[]> record, Acknowledgment ack) {

        try {
            final String message = new String(record.value(), "UTF-8");
            final String topic = record.topic();
            final Long offset = record.offset();

            log.info("收到新智推送的重点人员数据: {}-{}-{}", topic, offset, message);

            final ImportantPerson importantPerson = GsonUtil.GsonToBean(message, ImportantPerson.class);
            final String gxlx = importantPerson.getGxlx();
            final String sfzh = importantPerson.getSfzh();
            switch (gxlx) {
                case "0":
                    log.info("该条数据为需新增人员: {}", importantPerson.toString());

                    String faceParam = "?Face.TabID=%s&Face.IDNumber=%s&Fields=(%s)";
                    faceParam = String.format(faceParam, tabId, sfzh, faceFields);
                    List<Face> existFace = viewlibFacade.getFaces(faceParam);
                    if (!CollectionUtils.isEmpty(existFace)) {
                        log.info("该条数据在 staticface 中已存在: {}", importantPerson.toString());
                        break;
                    }

                    log.info("该条数据在 staticface 中不存在: {}", importantPerson.toString());
                    String personParam = "?Person.TabID like .*&Person.IDNumber=%s&Fields=(%s)";
                    personParam = String.format(personParam, sfzh, personFields);
                    List<Person> existPerson = viewlibFacade.getPersons(personParam);
                    List<Person> addPerson = new ArrayList<>();
                    List<Face> addFace = new ArrayList<>();
                    if (CollectionUtils.isEmpty(existPerson)) {
                        log.info("该条数据在 staticperson 中不存在: {}", importantPerson.toString());
                        Person person = ConvertUtil.convertImportantPersonToPerson(importantPerson);
                        addPerson.add(person);
                        Face face = ConvertUtil.convertImportantPersonToFace(importantPerson, person, tabId);
                        addFace.add(face);
                    } else {
                        Person person = existPerson.stream().sorted(Comparator.comparing(Person::getEntryTime)).collect(Collectors.toList()).get(0);
                        Face face = ConvertUtil.convertImportantPersonToFace(importantPerson, person, tabId);
                        addFace.add(face);
                    }

                    PersonList personList = new PersonList();
                    FaceList faceList = new FaceList();
                    if (!CollectionUtils.isEmpty(addPerson)) {
                        personList.setPersonObject(addPerson);
                        boolean addPersonResult = viewlibFacade.addPersons(personList);
                        log.info("{} 录入视图库 staticperson 是否成功 {}", personList.toString(), addPersonResult);
                    }

                    if (!CollectionUtils.isEmpty(addFace)) {
                        faceList.setFaceObject(addFace);
                        boolean addFaceResult = viewlibFacade.addFaces(faceList);
                        log.info("{} 录入视图库 staticface 是否成功 {}", faceList.toString(), addFaceResult);
                    }

                    break;

                case "1":
                    log.info("该条数据为需修改人员: {}", importantPerson.toString());
                    String params = "?Face.TabID=%s&Fce.IDNumber=%s&Fields=%s";
                    params = String.format(params, tabId, sfzh, faceFields);
                    List<Face> existFaceList = viewlibFacade.getFaces(params);

                    if (!CollectionUtils.isEmpty(existFaceList)) {
                        List<String> faceIdList = existFaceList.stream().map(Face::getFaceID).distinct().collect(Collectors.toList());
                        String idList = String.join(",", faceIdList);
                        boolean delResult = viewlibFacade.delFace(idList, tabId);
                        log.info("删除FaceID: {} 是否成功: {}", idList, delResult);
                    }


            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ack.acknowledge();
    }
}
