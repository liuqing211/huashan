package com.kedacom.haiou.kmtool.dto.viid;

import java.util.List;

public class PersonListObject {
    private int TotalNum;
    private List<Person> PersonObject;

    public int getTotalNum() {
        return TotalNum;
    }

    public void setTotalNum(int totalNum) {
        TotalNum = totalNum;
    }

    public List<Person> getPersonObject() {
        return PersonObject;
    }

    public void setPersonObject(List<Person> personObject) {
        PersonObject = personObject;
    }
}
