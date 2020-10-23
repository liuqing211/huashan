package com.kedacom.haiou.kmtool.dto.viid;


public class Person {
    private String PersonID;
    private int InfoKind;
    private String SourceID;
    private String DeviceID;
    private int LeftTopX;
    private int LeftTopY;
    private int RightBtmX;
    private int RightBtmY;
    private String LocationMarkTime;
    private String PersonAppearTime;
    private String PersonDisAppearTime;
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
    private String PersonOrg;
    private String JobCategory;
    private Integer AccompanyNumber;
    private Integer HeightUpLimit;
    private Integer HeightLowerLimit;
    private String BodyType;
    private String SkinColor;
    private String HairStyle;
    private String HairColor;
    private String Gesture;
    private String Status;
    private String FaceStyle;
    private String FacialFeature;
    private String PhysicalFeature;
    private String BodyFeature;
    private String HabitualMovement;
    private String Behavior;
    private String BehaviorDescription;
    private String Appendant;
    private String AppendantDescription;
    private String UmbrellaColor;
    private String RespiratorColor;
    private String CapStyle;
    private String CapColor;
    private String GlassStyle;
    private String GlassColor;
    private String ScarfColor;
    private String BagStyle;
    private String BagColor;
    private String CoatStyle;
    private String CoatLength;
    private String CoatColor;
    private String TrousersStyle;
    private String TrousersColor;
    private String TrousersLen;
    private String ShoesStyle;
    private String ShoesColor;
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
    private SubImageInfoList SubImageList;
    private String TabID;
    private String FamilyAddress;
    private int Maritalstatus;
    private String DateBirth;
    private Integer DispTag;

    public Person() {
    }

    public String getPersonID() {
        return PersonID;
    }

    public void setPersonID(String personID) {
        PersonID = personID;
    }

    public int getInfoKind() {
        return InfoKind;
    }

    public void setInfoKind(int infoKind) {
        InfoKind = infoKind;
    }

    public String getSourceID() {
        return SourceID;
    }

    public void setSourceID(String sourceID) {
        SourceID = sourceID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public int getLeftTopX() {
        return LeftTopX;
    }

    public void setLeftTopX(int leftTopX) {
        LeftTopX = leftTopX;
    }

    public int getLeftTopY() {
        return LeftTopY;
    }

    public void setLeftTopY(int leftTopY) {
        LeftTopY = leftTopY;
    }

    public int getRightBtmX() {
        return RightBtmX;
    }

    public void setRightBtmX(int rightBtmX) {
        RightBtmX = rightBtmX;
    }

    public int getRightBtmY() {
        return RightBtmY;
    }

    public void setRightBtmY(int rightBtmY) {
        RightBtmY = rightBtmY;
    }

    public String getLocationMarkTime() {
        return LocationMarkTime;
    }

    public void setLocationMarkTime(String locationMarkTime) {
        LocationMarkTime = locationMarkTime;
    }

    public String getPersonAppearTime() {
        return PersonAppearTime;
    }

    public void setPersonAppearTime(String personAppearTime) {
        PersonAppearTime = personAppearTime;
    }

    public String getPersonDisAppearTime() {
        return PersonDisAppearTime;
    }

    public void setPersonDisAppearTime(String personDisAppearTime) {
        PersonDisAppearTime = personDisAppearTime;
    }

    public String getIDType() {
        return IDType;
    }

    public void setIDType(String IDType) {
        this.IDType = IDType;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsedName() {
        return UsedName;
    }

    public void setUsedName(String usedName) {
        UsedName = usedName;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getGenderCode() {
        return GenderCode;
    }

    public void setGenderCode(String genderCode) {
        GenderCode = genderCode;
    }

    public Integer getAgeUpLimit() {
        return AgeUpLimit;
    }

    public void setAgeUpLimit(Integer ageUpLimit) {
        AgeUpLimit = ageUpLimit;
    }

    public Integer getAgeLowerLimit() {
        return AgeLowerLimit;
    }

    public void setAgeLowerLimit(Integer ageLowerLimit) {
        AgeLowerLimit = ageLowerLimit;
    }

    public String getEthicCode() {
        return EthicCode;
    }

    public void setEthicCode(String ethicCode) {
        EthicCode = ethicCode;
    }

    public String getNationalityCode() {
        return NationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        NationalityCode = nationalityCode;
    }

    public String getNativeCityCode() {
        return NativeCityCode;
    }

    public void setNativeCityCode(String nativeCityCode) {
        NativeCityCode = nativeCityCode;
    }

    public String getResidenceAdminDivision() {
        return ResidenceAdminDivision;
    }

    public void setResidenceAdminDivision(String residenceAdminDivision) {
        ResidenceAdminDivision = residenceAdminDivision;
    }

    public String getChineseAccentCode() {
        return ChineseAccentCode;
    }

    public void setChineseAccentCode(String chineseAccentCode) {
        ChineseAccentCode = chineseAccentCode;
    }

    public String getPersonOrg() {
        return PersonOrg;
    }

    public void setPersonOrg(String personOrg) {
        PersonOrg = personOrg;
    }

    public String getJobCategory() {
        return JobCategory;
    }

    public void setJobCategory(String jobCategory) {
        JobCategory = jobCategory;
    }

    public Integer getAccompanyNumber() {
        return AccompanyNumber;
    }

    public void setAccompanyNumber(Integer accompanyNumber) {
        AccompanyNumber = accompanyNumber;
    }

    public Integer getHeightUpLimit() {
        return HeightUpLimit;
    }

    public void setHeightUpLimit(Integer heightUpLimit) {
        HeightUpLimit = heightUpLimit;
    }

    public Integer getHeightLowerLimit() {
        return HeightLowerLimit;
    }

    public void setHeightLowerLimit(Integer heightLowerLimit) {
        HeightLowerLimit = heightLowerLimit;
    }

    public String getBodyType() {
        return BodyType;
    }

    public void setBodyType(String bodyType) {
        BodyType = bodyType;
    }

    public String getSkinColor() {
        return SkinColor;
    }

    public void setSkinColor(String skinColor) {
        SkinColor = skinColor;
    }

    public String getHairStyle() {
        return HairStyle;
    }

    public void setHairStyle(String hairStyle) {
        HairStyle = hairStyle;
    }

    public String getHairColor() {
        return HairColor;
    }

    public void setHairColor(String hairColor) {
        HairColor = hairColor;
    }

    public String getGesture() {
        return Gesture;
    }

    public void setGesture(String gesture) {
        Gesture = gesture;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getFaceStyle() {
        return FaceStyle;
    }

    public void setFaceStyle(String faceStyle) {
        FaceStyle = faceStyle;
    }

    public String getFacialFeature() {
        return FacialFeature;
    }

    public void setFacialFeature(String facialFeature) {
        FacialFeature = facialFeature;
    }

    public String getPhysicalFeature() {
        return PhysicalFeature;
    }

    public void setPhysicalFeature(String physicalFeature) {
        PhysicalFeature = physicalFeature;
    }

    public String getBodyFeature() {
        return BodyFeature;
    }

    public void setBodyFeature(String bodyFeature) {
        BodyFeature = bodyFeature;
    }

    public String getHabitualMovement() {
        return HabitualMovement;
    }

    public void setHabitualMovement(String habitualMovement) {
        HabitualMovement = habitualMovement;
    }

    public String getBehavior() {
        return Behavior;
    }

    public void setBehavior(String behavior) {
        Behavior = behavior;
    }

    public String getBehaviorDescription() {
        return BehaviorDescription;
    }

    public void setBehaviorDescription(String behaviorDescription) {
        BehaviorDescription = behaviorDescription;
    }

    public String getAppendant() {
        return Appendant;
    }

    public void setAppendant(String appendant) {
        Appendant = appendant;
    }

    public String getAppendantDescription() {
        return AppendantDescription;
    }

    public void setAppendantDescription(String appendantDescription) {
        AppendantDescription = appendantDescription;
    }

    public String getUmbrellaColor() {
        return UmbrellaColor;
    }

    public void setUmbrellaColor(String umbrellaColor) {
        UmbrellaColor = umbrellaColor;
    }

    public String getRespiratorColor() {
        return RespiratorColor;
    }

    public void setRespiratorColor(String respiratorColor) {
        RespiratorColor = respiratorColor;
    }

    public String getCapStyle() {
        return CapStyle;
    }

    public void setCapStyle(String capStyle) {
        CapStyle = capStyle;
    }

    public String getCapColor() {
        return CapColor;
    }

    public void setCapColor(String capColor) {
        CapColor = capColor;
    }

    public String getGlassStyle() {
        return GlassStyle;
    }

    public void setGlassStyle(String glassStyle) {
        GlassStyle = glassStyle;
    }

    public String getGlassColor() {
        return GlassColor;
    }

    public void setGlassColor(String glassColor) {
        GlassColor = glassColor;
    }

    public String getScarfColor() {
        return ScarfColor;
    }

    public void setScarfColor(String scarfColor) {
        ScarfColor = scarfColor;
    }

    public String getBagStyle() {
        return BagStyle;
    }

    public void setBagStyle(String bagStyle) {
        BagStyle = bagStyle;
    }

    public String getBagColor() {
        return BagColor;
    }

    public void setBagColor(String bagColor) {
        BagColor = bagColor;
    }

    public String getCoatStyle() {
        return CoatStyle;
    }

    public void setCoatStyle(String coatStyle) {
        CoatStyle = coatStyle;
    }

    public String getCoatLength() {
        return CoatLength;
    }

    public void setCoatLength(String coatLength) {
        CoatLength = coatLength;
    }

    public String getCoatColor() {
        return CoatColor;
    }

    public void setCoatColor(String coatColor) {
        CoatColor = coatColor;
    }

    public String getTrousersStyle() {
        return TrousersStyle;
    }

    public void setTrousersStyle(String trousersStyle) {
        TrousersStyle = trousersStyle;
    }

    public String getTrousersColor() {
        return TrousersColor;
    }

    public void setTrousersColor(String trousersColor) {
        TrousersColor = trousersColor;
    }

    public String getTrousersLen() {
        return TrousersLen;
    }

    public void setTrousersLen(String trousersLen) {
        TrousersLen = trousersLen;
    }

    public String getShoesStyle() {
        return ShoesStyle;
    }

    public void setShoesStyle(String shoesStyle) {
        ShoesStyle = shoesStyle;
    }

    public String getShoesColor() {
        return ShoesColor;
    }

    public void setShoesColor(String shoesColor) {
        ShoesColor = shoesColor;
    }

    public Integer getIsDriver() {
        return IsDriver;
    }

    public void setIsDriver(Integer isDriver) {
        IsDriver = isDriver;
    }

    public Integer getIsForeigner() {
        return IsForeigner;
    }

    public void setIsForeigner(Integer isForeigner) {
        IsForeigner = isForeigner;
    }

    public String getPassportType() {
        return PassportType;
    }

    public void setPassportType(String passportType) {
        PassportType = passportType;
    }

    public String getImmigrantTypeCode() {
        return ImmigrantTypeCode;
    }

    public void setImmigrantTypeCode(String immigrantTypeCode) {
        ImmigrantTypeCode = immigrantTypeCode;
    }

    public Integer getIsSuspectedTerrorist() {
        return IsSuspectedTerrorist;
    }

    public void setIsSuspectedTerrorist(Integer isSuspectedTerrorist) {
        IsSuspectedTerrorist = isSuspectedTerrorist;
    }

    public String getSuspectedTerroristNumber() {
        return SuspectedTerroristNumber;
    }

    public void setSuspectedTerroristNumber(String suspectedTerroristNumber) {
        SuspectedTerroristNumber = suspectedTerroristNumber;
    }

    public Integer getIsCriminalInvolved() {
        return IsCriminalInvolved;
    }

    public void setIsCriminalInvolved(Integer isCriminalInvolved) {
        IsCriminalInvolved = isCriminalInvolved;
    }

    public String getCriminalInvolvedSpecilisationCode() {
        return CriminalInvolvedSpecilisationCode;
    }

    public void setCriminalInvolvedSpecilisationCode(String criminalInvolvedSpecilisationCode) {
        CriminalInvolvedSpecilisationCode = criminalInvolvedSpecilisationCode;
    }

    public String getBodySpeciallMark() {
        return BodySpeciallMark;
    }

    public void setBodySpeciallMark(String bodySpeciallMark) {
        BodySpeciallMark = bodySpeciallMark;
    }

    public String getCrimeMethod() {
        return CrimeMethod;
    }

    public void setCrimeMethod(String crimeMethod) {
        CrimeMethod = crimeMethod;
    }

    public String getCrimeCharacterCode() {
        return CrimeCharacterCode;
    }

    public void setCrimeCharacterCode(String crimeCharacterCode) {
        CrimeCharacterCode = crimeCharacterCode;
    }

    public String getEscapedCriminalNumber() {
        return EscapedCriminalNumber;
    }

    public void setEscapedCriminalNumber(String escapedCriminalNumber) {
        EscapedCriminalNumber = escapedCriminalNumber;
    }

    public Integer getIsDetainees() {
        return IsDetainees;
    }

    public void setIsDetainees(Integer isDetainees) {
        IsDetainees = isDetainees;
    }

    public String getDetentionHouseCode() {
        return DetentionHouseCode;
    }

    public void setDetentionHouseCode(String detentionHouseCode) {
        DetentionHouseCode = detentionHouseCode;
    }

    public String getDetaineesIdentity() {
        return DetaineesIdentity;
    }

    public void setDetaineesIdentity(String detaineesIdentity) {
        DetaineesIdentity = detaineesIdentity;
    }

    public String getDetaineesSpecialIdentity() {
        return DetaineesSpecialIdentity;
    }

    public void setDetaineesSpecialIdentity(String detaineesSpecialIdentity) {
        DetaineesSpecialIdentity = detaineesSpecialIdentity;
    }

    public String getMemberTypeCode() {
        return MemberTypeCode;
    }

    public void setMemberTypeCode(String memberTypeCode) {
        MemberTypeCode = memberTypeCode;
    }

    public Integer getIsVictim() {
        return IsVictim;
    }

    public void setIsVictim(Integer isVictim) {
        IsVictim = isVictim;
    }

    public String getVictimType() {
        return VictimType;
    }

    public void setVictimType(String victimType) {
        VictimType = victimType;
    }

    public String getInjuredDegree() {
        return InjuredDegree;
    }

    public void setInjuredDegree(String injuredDegree) {
        InjuredDegree = injuredDegree;
    }

    public String getCorpseConditionCode() {
        return CorpseConditionCode;
    }

    public void setCorpseConditionCode(String corpseConditionCode) {
        CorpseConditionCode = corpseConditionCode;
    }

    public Integer getIsSuspiciousPerson() {
        return IsSuspiciousPerson;
    }

    public void setIsSuspiciousPerson(Integer isSuspiciousPerson) {
        IsSuspiciousPerson = isSuspiciousPerson;
    }

    public SubImageInfoList getSubImageList() {
        return SubImageList;
    }

    public void setSubImageList(SubImageInfoList subImageList) {
        SubImageList = subImageList;
    }

    public String getTabID() {
        return TabID;
    }

    public void setTabID(String tabID) {
        TabID = tabID;
    }

    public String getFamilyAddress() {
        return FamilyAddress;
    }

    public void setFamilyAddress(String familyAddress) {
        FamilyAddress = familyAddress;
    }

    public int getMaritalstatus() {
        return Maritalstatus;
    }

    public void setMaritalstatus(int maritalstatus) {
        Maritalstatus = maritalstatus;
    }

    public String getDateBirth() {
        return DateBirth;
    }

    public void setDateBirth(String dateBirth) {
        DateBirth = dateBirth;
    }

    public Integer getDispTag() {
        return DispTag;
    }

    public void setDispTag(Integer dispTag) {
        DispTag = dispTag;
    }
}