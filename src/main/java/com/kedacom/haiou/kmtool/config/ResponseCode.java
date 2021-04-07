package com.kedacom.haiou.kmtool.config;

import lombok.Getter;


@Getter
public enum ResponseCode {    //1000系列通用错误
    SUCCESS(0, "操作成功"),
    FAILED(1001, "接口错误"),
    VALIDATE_FAILED(1002, "参数校验失败"),
    ERROR(1003, "未知错误");

    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

