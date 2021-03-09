package com.kedacom.haiou.kmtool.service;

public class HandleDataThread implements Runnable {

    private String data;

    public HandleDataThread(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void run() {
        System.out.println("多线程" + Thread.currentThread().getName() + "已处理数据: " + data);
    }
}
