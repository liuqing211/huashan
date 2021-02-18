package com.kedacom.haiou.kmtool.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2021/1/20.
 */
public class FileUtil {

    public static void WriteStringToFile2(String log) {
        String filePath = System.getProperty("catalina.home") + "/logs/ddu_sendInfoNum.log";
        File file = new File(filePath);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(filePath, true);
            bw = new BufferedWriter(fw);
            bw.write(log);// 往已有的文件上添加字符串
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
