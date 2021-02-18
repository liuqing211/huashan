package com.kedacom.haiou.kmtool.dto.viid;

import lombok.Data;

@Data
public class ResponseStatus {
    private String Id;
    private String returnId;
    private String RequestURL;
    private int StatusCode;
    private String StatusString;
    private String LocalTime;
}