package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

import java.util.List;

@Data
public class ZipPerson {
    private String IDNumber;
    private String Name;
    private String UsedName;
    private String GenderCode;
    private String EthicCode;
    private String NativeCityCode;
    private String FamilyAddress;
    private String DateBirth;
    private int Maritalstatus;
    private List<String> imageIds;
}
