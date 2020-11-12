package com.kedacom.haiou.kmtool.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Component
public class ImageUtil {

    public static String getImageUrl(String url, Map<String, ContentBody> reqParam){
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        try {
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (Map.Entry<String, ContentBody> entry : reqParam.entrySet()){
                multipartEntityBuilder.addPart(entry.getKey(), entry.getValue());
            }

            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                return EntityUtils.toString(entity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            System.out.println("图片上传haiou-mate异常" + ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
