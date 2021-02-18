package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

import java.util.List;

@Data
public class PersonListObject {
    private int TotalNum;
    private List<Person> PersonObject;
}
