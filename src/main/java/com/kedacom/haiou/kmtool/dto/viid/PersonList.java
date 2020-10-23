package com.kedacom.haiou.kmtool.dto.viid;

import lombok.ToString;

import java.util.List;

@ToString
public class PersonList {
    private int TotalNum;
    private List<Person> PersonObject;

    public List<Person> getPersonObject() {
        return PersonObject;
    }

    public void setPersonObject(List<Person> personObject) {
        PersonObject = personObject;
    }

    public int getTotalNum() {
        return TotalNum;
    }

    public void setTotalNum(int totalNum) {
        TotalNum = totalNum;
    }
}
