package com.kedacom.haiou.kmtool.utils;

/**
 * Created by liulun on 2017/5/11.
 */
public class ExceptionWithKey extends Exception {
    private Object key;

    public ExceptionWithKey(Object key, Throwable cause) {
        super(cause);
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }
}
