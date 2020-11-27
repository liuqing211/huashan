package com.kedacom.haiou.kmtool.utils;


import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class CommonHelper {
    @Value("${haioumate.addr}")
    private String haioumateServer;
    @Value("${haioumateFilePath:/mnt/upload}")
    private String haioumateFilePath;


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
            log.error("图片 {} 转换Base64异常：{}", imgURL, ExceptionUtils.getStackTrace(e));
        }
        return new String(Base64.encodeBase64(data.toByteArray()));
    }

    public String base64ToImage(String imgStr) {
        if (imgStr == null) {
            return null;
        }
        try {
            byte[] b = Base64.decodeBase64(imgStr);
            for (int i = 0; i < b.length; i++) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            String randomName = UUID.randomUUID().toString();
            String dateStr = new SimpleDateFormat("yyyMMdd").format(new Date());
            String imgFilePath = haioumateFilePath + File.separator + dateStr + File.separator + randomName + ".jpg";
            File serverFile = new File(imgFilePath);
            File parentFile = serverFile.getParentFile();
            if (!parentFile.exists()) {
                FileUtils.forceMkdir(parentFile);
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return haioumateServer + "/" + "api/download/" + dateStr + "/" + randomName + ".jpg";
        } catch (Exception e) {
            return null;
        }
    }

    public static String convertToSex(String genderCode) {
        return "1".equals(genderCode) ? "男" : ("2".equals(genderCode) ? "女" : "未知");
    }

    public static String convertToAgeDivision(Integer ageLowerLimit, Integer ageUpLimit) {
        if (ageLowerLimit == null || ageUpLimit == null)
            return "未知";
        double aveAge = (ageLowerLimit + ageUpLimit) / 2.0;
        if (aveAge == 0)
            return "未知";
        else if (aveAge > 0 && aveAge < 20)
            return "少年";
        else if (aveAge >= 20 && aveAge <= 40)
            return "青年";
        else if (aveAge >= 41 && aveAge <= 60)
            return "中年";
        else
            return "老年";
    }

    public static String convertToCover(Integer hasHat, Integer hasMask, Integer hasGlass) {
        if (hasHat == null || hasMask == null || hasHat == null)
            return "--";
        StringBuffer sb = new StringBuffer();
        if (hasHat > 0)
            sb.append("帽子");
        if (hasMask > 0) {
            if (sb.length() > 0)
                sb.append("，");
            sb.append("口罩");
        }
        if (hasGlass > 0) {
            if (sb.length() > 0)
                sb.append("，");
            sb.append("眼镜");
        }
        if (sb.length() == 0)
            sb.append("--");
        return sb.toString();
    }

    public static String convertToAlarmStatus(Integer alarmStatus) {
        switch (alarmStatus) {
            case 0:
                return "未处理";
            case 1:
                return "已收到";

            case 3:
                return "已查证->属实";
            case 4:
                return "已查证->误报";
            case 5:
                return "未查证->前期核实->属实";
            case 6:
                return "未查证->前期核实->误告";
            case 7:
                return "未查证->不需核实";
            case 8:
                return "未查证->其他";
            case 9:
                return "已控制->属实";
            case 10:
                return "已控制->误报";
            case 11:
                return "未控制->不确定";

            default:
                return "未处理";
        }
    }

    public static String conertToTaskLevel(String taskLevel) {
        switch (taskLevel) {
            case "1":
                return "关注";
            case "2":
                return "核实";
            case "3":
                return "控制";
            default:
                return "";
        }
    }

    public static Double[] convertToPoint(Double lng, Double lat) {
        Double[] point = new Double[2];
        point[0] = lng;
        point[1] = lat;
        return point;
    }

    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> paramMap = new HashMap<>();

        try {
            String noBracketString = queryString.replaceAll("\\(", "").replaceAll("\\)", "");
            if (noBracketString.contains("&")) {
                String[] splitQueryString = noBracketString.split("&");
                Arrays.stream(splitQueryString).forEach(paramString -> parseParam(paramString, paramMap));
            } else {
                parseParam(noBracketString, paramMap);
            }
        } catch (Exception e) {
            log.error("parse queryString failed! {}", ExceptionUtils.getStackTrace(e));
        }

        return paramMap;
    }


    private static String toUpperFirstChar(String string) {
        char[] chars = string.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] -= 32;
            return String.valueOf(chars);
        }
        return string;
    }

    private static void parseParam(String paramString, Map<String, String> paramMap) {
        String[] splitParamString = paramString.split("=");
        if (splitParamString[0].contains(".")) {
            paramMap.put(splitParamString[0].split("\\.")[1], splitParamString[1]);
        } else {
            paramMap.put(splitParamString[0], splitParamString[1]);
        }
    }
}
