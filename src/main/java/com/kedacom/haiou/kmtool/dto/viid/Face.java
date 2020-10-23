package com.kedacom.haiou.kmtool.dto.viid;

import java.io.Serializable;

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
	private int Maritalstatus;
	private String FamilyAddress;
	private Object SelfDefObject;
	private String RelativeTabId;
	private String RelativeID;
	private String CollectorOrg;
	private String CollectorID;
	private Integer DispTag;

	public String getFaceID() {
		return FaceID;
	}

	public void setFaceID(String faceID) {
		FaceID = faceID;
	}

	public Integer getInfoKind() {
		return InfoKind;
	}

	public void setInfoKind(Integer infoKind) {
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

	public Integer getLeftTopX() {
		return LeftTopX;
	}

	public void setLeftTopX(Integer leftTopX) {
		LeftTopX = leftTopX;
	}

	public Integer getLeftTopY() {
		return LeftTopY;
	}

	public void setLeftTopY(Integer leftTopY) {
		LeftTopY = leftTopY;
	}

	public Integer getRightBtmX() {
		return RightBtmX;
	}

	public void setRightBtmX(Integer rightBtmX) {
		RightBtmX = rightBtmX;
	}

	public Integer getRightBtmY() {
		return RightBtmY;
	}

	public void setRightBtmY(Integer rightBtmY) {
		RightBtmY = rightBtmY;
	}

	public String getLocationMarkTime() {
		return LocationMarkTime;
	}

	public void setLocationMarkTime(String locationMarkTime) {
		LocationMarkTime = locationMarkTime;
	}

	public String getFaceAppearTime() {
		return FaceAppearTime;
	}

	public void setFaceAppearTime(String faceAppearTime) {
		FaceAppearTime = faceAppearTime;
	}

	public String getFaceDisAppearTime() {
		return FaceDisAppearTime;
	}

	public void setFaceDisAppearTime(String faceDisAppearTime) {
		FaceDisAppearTime = faceDisAppearTime;
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

	public Integer getAttitude() {
		return Attitude;
	}

	public void setAttitude(Integer attitude) {
		Attitude = attitude;
	}

	public Double getSimilaritydegree() {
		return Similaritydegree;
	}

	public void setSimilaritydegree(Double similaritydegree) {
		Similaritydegree = similaritydegree;
	}

	public String getEyebrowStyle() {
		return EyebrowStyle;
	}

	public void setEyebrowStyle(String eyebrowStyle) {
		EyebrowStyle = eyebrowStyle;
	}

	public String getNoseStyle() {
		return NoseStyle;
	}

	public void setNoseStyle(String noseStyle) {
		NoseStyle = noseStyle;
	}

	public String getMustacheStyle() {
		return MustacheStyle;
	}

	public void setMustacheStyle(String mustacheStyle) {
		MustacheStyle = mustacheStyle;
	}

	public String getLipStyle() {
		return LipStyle;
	}

	public void setLipStyle(String lipStyle) {
		LipStyle = lipStyle;
	}

	public String getWrinklePouch() {
		return WrinklePouch;
	}

	public void setWrinklePouch(String wrinklePouch) {
		WrinklePouch = wrinklePouch;
	}

	public String getAcneStain() {
		return AcneStain;
	}

	public void setAcneStain(String acneStain) {
		AcneStain = acneStain;
	}

	public String getFreckleBirthmark() {
		return FreckleBirthmark;
	}

	public void setFreckleBirthmark(String freckleBirthmark) {
		FreckleBirthmark = freckleBirthmark;
	}

	public String getScarDimple() {
		return ScarDimple;
	}

	public void setScarDimple(String scarDimple) {
		ScarDimple = scarDimple;
	}

	public String getOtherFeature() {
		return OtherFeature;
	}

	public void setOtherFeature(String otherFeature) {
		OtherFeature = otherFeature;
	}

	public SubImageInfoList getSubImageList() {
		return SubImageList;
	}

	public void setSubImageList(SubImageInfoList subImageList) {
		SubImageList = subImageList;
	}

	public String getShotTime() {
		return ShotTime;
	}

	public void setShotTime(String shotTime) {
		ShotTime = shotTime;
	}

	public String getTabID() {
		return TabID;
	}

	public void setTabID(String tabID) {
		TabID = tabID;
	}

	public int getMaritalstatus() {
		return Maritalstatus;
	}

	public void setMaritalstatus(int maritalstatus) {
		Maritalstatus = maritalstatus;
	}

	public String getFamilyAddress() {
		return FamilyAddress;
	}

	public void setFamilyAddress(String familyAddress) {
		FamilyAddress = familyAddress;
	}

	public Object getSelfDefObject() {
		return SelfDefObject;
	}

	public void setSelfDefObject(Object selfDefObject) {
		SelfDefObject = selfDefObject;
	}

	public String getRelativeTabId() {
		return RelativeTabId;
	}

	public void setRelativeTabId(String relativeTabId) {
		RelativeTabId = relativeTabId;
	}

	public String getRelativeID() {
		return RelativeID;
	}

	public void setRelativeID(String relativeID) {
		RelativeID = relativeID;
	}

	public String getCollectorOrg() {
		return CollectorOrg;
	}

	public void setCollectorOrg(String collectorOrg) {
		CollectorOrg = collectorOrg;
	}

	public String getCollectorID() {
		return CollectorID;
	}

	public void setCollectorID(String collectorID) {
		CollectorID = collectorID;
	}

	public Integer getDispTag() {
		return DispTag;
	}

	public void setDispTag(Integer dispTag) {
		DispTag = dispTag;
	}
}