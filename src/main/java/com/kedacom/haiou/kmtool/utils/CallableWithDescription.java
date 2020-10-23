package com.kedacom.haiou.kmtool.utils;

import java.util.concurrent.Callable;

/**
 * Created by liulun on 2017/3/20.
 */
public interface CallableWithDescription<T> extends Callable<T> {
    String getDescription();
}
