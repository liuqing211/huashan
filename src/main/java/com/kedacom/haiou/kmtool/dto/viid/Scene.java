package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class Scene {
    private String SourceID;
    private String SceneID;
    private String BeginTime;
    private int InfoKind;
    private String PlaceType;
    private String WeatherType;
    private String SceneDescribe;
    private String SceneType;
    private String RoadAlignmentType;
    private int RoadTerraintype;
    private String RoadSurfaceType;
    private String RoadCoditionType;
    private String RoadJunctionSectionType;
    private String RoadLightingType;
    private String Illustration;
    private String WindDirection;
    private String Illumination;
    private String FieldCondition;
    private int Temperature;
    private String Humidity;
    private String PopulationDensity;
    private String DenseDegree;
    private int Importance;
}
