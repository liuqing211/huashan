package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

import java.io.Serializable;

@Data
public class Face implements Serializable {
    private String FaceID;
    private Integer InfoKind;
    private String SourceID;
    private String DeviceID;
    private Integer LeftTopX;
    private Integer LeftTopY;
    private Integer RightBtmX;
    private Integer RightBtmY;
    private String LocationMarkTime;
    private String FaceAppearTime;
    private String FaceDisAppearTime;
    private String IDType;
    private String IDNumber;
    private String Name;
    private String UsedName;
    private String Alias;
    private String GenderCode;
    private Integer AgeUpLimit;
    private Integer AgeLowerLimit;
    private String EthicCode;
    private String NationalityCode;
    private String NativeCityCode;
    private String ResidenceAdminDivision;
    private String ChineseAccentCode;
    private String JobCategory;
    private Integer AccompanyNumber;
    private String SkinColor;
    private String HairStyle;
    private String HairColor;
    private String FaceStyle;
    private String FacialFeature;
    private String PhysicalFeature;
    private String RespiratorColor;
    private String CapStyle;
    private String CapColor;
    private String GlassStyle;
    private String GlassColor;
    private Integer IsDriver;
    private Integer IsForeigner;
    private String PassportType;
    private String ImmigrantTypeCode;
    private Integer IsSuspectedTerrorist;
    private String SuspectedTerroristNumber;
    private Integer IsCriminalInvolved;
    private String CriminalInvolvedSpecilisationCode;
    private String BodySpeciallMark;
    private String CrimeMethod;
    private String CrimeCharacterCode;
    private String EscapedCriminalNumber;
    private Integer IsDetainees;
    private String DetentionHouseCode;
    private String DetaineesIdentity;
    private String DetaineesSpecialIdentity;
    private String MemberTypeCode;
    private Integer IsVictim;
    private String VictimType;
    private String InjuredDegree;
    private String CorpseConditionCode;
    private Integer IsSuspiciousPerson;
    private Integer Attitude;
    private Double SimilarityDegree;
    private Double Similaritydegree;
    private String EyebrowStyle;
    private String NoseStyle;
    private String MustacheStyle;
    private String LipStyle;
    private String WrinklePouch;
    private String AcneStain;
    private String FreckleBirthmark;
    private String ScarDimple;
    private String OtherFeature;
    private SubImageInfoList SubImageList;
    private String ShotTime;
    //	private FeatureInfoList FeatureList;
    private String TabID;
    private Integer Maritalstatus;
    private String FamilyAddress;
    private Object SelfDefObject;
    private String RelativeTabId;
    private String RelativeID;
    private String CollectorOrg;
    private String CollectorID;
    private Integer DispTag;
    private String ManageOrgName;       // 人脸管控单位
    private String ManagePersonName;    // 人脸管控者姓名
    private String ManagePhoneNumber;   // 人脸管控者联系电话

    private String DeviceName;

    private String TabName;

    private String DateBirth;

    public Double getSimilarityDegree() {
        return SimilarityDegree;
    }

    public void setSimilarityDegree(Double similarityDegree) {
        SimilarityDegree = similarityDegree;
        Similaritydegree = similarityDegree;
    }

    public Double getSimilaritydegree() {
        return Similaritydegree;
    }

    public void setSimilaritydegree(Double similaritydegree) {
        Similaritydegree = similaritydegree;
        SimilarityDegree = similaritydegree;
    }
}