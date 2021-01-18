package com.kedacom.haiou.kmtool.utils;

import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2020/10/12.
 */
@Slf4j
public class PictureUtil {

    public static String downloadPicByURL(String url, String path, String fileName) {
        String pathStr = "%s" + File.separator + "%s";
        pathStr = String.format(pathStr, path, fileName);

        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;
        try {
            URL pictureUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) pictureUrl.openConnection();
            inputStream = new DataInputStream(urlConnection.getInputStream());
            outputStream = new DataOutputStream(new FileOutputStream(pathStr));

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }

            return pathStr;
        } catch (Exception e) {
            log.error("下载图片失败：{}", ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }

                if (null != inputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 该方法只适用于 bmp gif jpg png
     * @param picture
     * @return
     */
    public static boolean isPicture(File picture){
        try {
            BufferedImage image = ImageIO.read(picture);

            return image != null;
        } catch (Exception e) {
            log.error("判断文件 {} 是否是图片发生异常：{}", picture.getAbsolutePath(), ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    public static String ImageToBase64(String imgURL) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            URL url = new URL(imgURL);
            byte[] by = new byte[1024];

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();

            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data.toByteArray()));
    }


    public static void main(String[] args) {
        String imageBase64 = PictureUtil.ImageToBase64("http://86.81.131.45:8080/haioumate/api/download/phone/20210107/0a0ea0e2-c41d-471e-a5c7-b319878231a2.jpg");
        System.out.println("base64:{{" + imageBase64 + "}}");
    }

}
