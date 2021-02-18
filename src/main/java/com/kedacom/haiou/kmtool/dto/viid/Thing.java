package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class Thing {
    private String SourceID;
    private String ThingID;
    private int InfoKind;
    private int LeftTopX;
    private int LeftTopY;
    private int RightBtmX;
    private int RightBtmY;
    private String Shape;
    private String LocationMarkTime;
    private String AppearTime;
    private String DisappearTime;
    private String Color;
    private String Material;
    private String Characteristic;
    private String Propertiy;
    private String InvolvedObjType;
    private String FirearmsAmmunitionType;
    private String ToolTraceType;
    private String EvidenceType;
    private String CaseEvidenceType;
    private String Name;
    private String Size;
}
