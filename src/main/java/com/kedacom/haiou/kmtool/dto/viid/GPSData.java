package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class GPSData {
    private String GPSID;
    private String DeviceID;
    private Integer SatelliteNo;
    private Double Direction;
    private Integer Height;
    private Double Longitude;
    private Double Latitude;
    private Double Speed;
    private String CurrentTime;
    private String Positioning;
    private Integer ValidDataMark;
}
