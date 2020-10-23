package com.kedacom.haiou.kmtool.utils;

/**
 * Created by liulun on 2017/5/11.
 */
public class TaskResult<ResultType> {
    private ResultType result;
    private ExceptionWithKey exception;

    public ResultType getResult() {
        return result;
    }

    public void setResult(ResultType result) {
        this.result = result;
    }

    public ExceptionWithKey getException() {
        return exception;
    }

    public void setException(ExceptionWithKey exception) {
        this.exception = exception;
    }
}
