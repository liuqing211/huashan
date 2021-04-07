package com.kedacom.haiou.kmtool.config;

import com.kedacom.haiou.kmtool.utils.GsonUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResponseMessage<T> implements Serializable {

    private String statusMsg;
    private int statusCode;
    private T data;

    public ResponseMessage(String statusMsg, int statusCode, T data) {
        this.statusMsg = statusMsg;
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> ResponseMessage<T> failed(T data) {
        return new ResponseMessage(ResponseCode.FAILED.getMsg(), ResponseCode.FAILED.getCode(),
                (null == data) ? "" : GsonUtil.toJson(data));
    }

    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage(ResponseCode.SUCCESS.getMsg(), ResponseCode.SUCCESS.getCode(),
                (null == data) ? "" : GsonUtil.toJson(data));
    }
}
