package com.kedacom.haiou.kmtool.utils;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by liulun on 2017/2/9.
 */
@Component
public class RestUtil {

    private static final int PROTECTED_LENGTH = 1024 * 1024;// 输入流保护 1M
    public static final Logger log = LoggerFactory.getLogger(RestUtil.class);
    private ExecutorService executor;

    public static String readInfoStreamToBytes(InputStream is) {
        if (is == null) {
            return null;
        }

        // 字节数组
        byte[] bCache = new byte[2048];
        int readSize = 0;// 每次读取的字节长度
        int totalSize = 0;// 总字节长度
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        try {
            // 一次性读取2048字节
            while ((readSize = is.read(bCache)) > 0) {
                totalSize += readSize;
                if (totalSize > PROTECTED_LENGTH) {
                    return null;
                }
                // 将bcache中读取的input数据写入infoStream
                infoStream.write(bCache, 0, readSize);
            }
        } catch (IOException e1) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(infoStream.toByteArray()).replaceAll("[\\t\\n\\r]","");
    }

    //@PostConstruct
    private void initExecutorService(){
        log.info("Initializing thread pool", "", "");
        executor = Executors.newFixedThreadPool(10);
    }

    public static RestTemplate getRestTemplate(int timeOut) {

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionReuseStrategy(new DefaultClientConnectionReuseStrategy())
                .build();

        RestTemplate mRestTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(timeOut);
        factory.setConnectTimeout(timeOut);
        factory.setHttpClient(httpClient);
        mRestTemplate.setRequestFactory(factory);
        mRestTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        mRestTemplate.getMessageConverters().add(1, new MappingJackson2HttpMessageConverter());

        return mRestTemplate;
    }

    public static RestTemplate getRestTemplate() {
        return getRestTemplate(120000);
    }

    public <InType,ReturnType> List<ReturnType> transformListAsyc(List<InType> fromList, final TransFunc<? super InType, ? extends ReturnType> function, int threadNum){
        List<ReturnType> result = null;
        try {
            result = transformListAsyc(fromList, function, threadNum, true);
        }
        catch (Exception e)
        {
            log.error("58000001", "transformListAsyc", e.getMessage(), e);
        }
        finally {
            return result;
        }
    }

    public <InType,ReturnType> List<ReturnType> transformListAsyc(List<InType> fromList, final TransFunc<? super InType, ? extends ReturnType> function, int threadNum, boolean ignoreException) throws Exception {
        List<CallableWithDescription<ReturnType>> tasks = new ArrayList<>();
        log.info("begin build tasks, current time:" + System.currentTimeMillis());
        for(final InType record:fromList)
        {
            CallableWithDescription<ReturnType> task = new CallableWithDescription<ReturnType>() {
                public String getDescription() {
                    return function.getErroMessage(record);
                }
                @Override
                public ReturnType call() throws Exception {
                    return function.doTransform(record);
                }
            };
            tasks.add(task);
        }
        if(tasks == null){
            return null;
        }
        if(tasks.size() == 0){
            return new ArrayList();
        }

        List<ReturnType> resultList = new ArrayList(tasks.size());
        log.info("begin call tasks, current time:" + System.currentTimeMillis());
        List<Future<ReturnType>> results = executor.invokeAll(tasks);
        for (int i = 0; i < results.size(); i++) {
            Future<ReturnType> r = results.get(i);
            ReturnType res = null;
            try {
                res = r.get();
            } catch (Exception ex) {
                CallableWithDescription<ReturnType> task = tasks.get(i);
                if(!ignoreException) {
                    throw new Exception(task.getDescription() + "遇到错误", ex);
                }
                else{
                    log.error(task.getDescription(), "遇到错误", ExceptionUtils.getStackTrace(ex));
                }
            }
            resultList.add(res);
        }
        log.info("transform finished, current time:" + System.currentTimeMillis());
        return resultList;
    }

    public <ReturnType> List<ReturnType> callInParallel(List<CallableWithDescription<ReturnType>> tasks) throws Exception {
        if(tasks == null){
            return null;
        }
        if(tasks.size() == 0){
            return new ArrayList();
        }
        int numThreads = tasks.size();

        List<ReturnType> resultList = new ArrayList<>(numThreads);
        try {
            List<Future<ReturnType>> results = executor.invokeAll(tasks);
            for (int i = 0; i < results.size(); i++) {
                Future<ReturnType> r = results.get(i);
                ReturnType res = null;
                try {
                    res = r.get();
                } catch (Exception ex) {
                    CallableWithDescription<ReturnType> task = tasks.get(i);
                    log.error(task.getDescription(), "遇到错误", ExceptionUtils.getStackTrace(ex));
                }
                resultList.add(res);
            }
        } catch (InterruptedException e) {
            log.error("并行调用被中断", "错误", ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }finally {

        }
        return resultList;
    }

    public <ReturnType> List<ReturnType> callInParallel(final String[] ulrArr,List<Callable<ReturnType>> tasks, final String taskName) throws Exception {
        List<CallableWithDescription<ReturnType>> tasksWithDescription = new ArrayList<>();
        for (int i = 0, tasksSize = tasks.size(); i < tasksSize; i++) {
            final int j = i;
            final Callable<ReturnType> t = tasks.get(i);
            tasksWithDescription.add(new CallableWithDescription<ReturnType>() {
                @Override
                public String getDescription() {
                    return "请求海鸥进行" + taskName + "url:" + ulrArr[j];
                }

                @Override
                public ReturnType call() throws Exception {
                    return t.call();
                }
            });
        }
        return callInParallel(tasksWithDescription);
    }



    public <ReturnType> List<TaskResult<ReturnType>> callInParallelWithTimeout(List<Callable<ReturnType>> tasks, final long timeout){

        if(tasks == null){
            return null;
        }

        List<TaskResult<ReturnType>> results = new ArrayList<>();

        if(tasks.size() == 0){
            return results;
        }
        ExecutorCompletionService<ReturnType> ecs = new ExecutorCompletionService<ReturnType>(executor);
        for (Callable<ReturnType> task : tasks) {
            ecs.submit(task);
        }


        for(int i = 0; i < tasks.size(); i++) {
            ReturnType result = null;
            try{
                result = ecs.take().get(timeout, TimeUnit.MILLISECONDS);
                // successful callable
                if(result != null) {
                    TaskResult<ReturnType> taskResult = new TaskResult<>();
                    taskResult.setResult(result);
                    results.add(taskResult);
                }
            }catch (Exception ee){
                Throwable cause = ee.getCause();
                if(ee instanceof TimeoutException){
                    // timeout thread finishes, stop collecting result
                    break;
                }else if(cause instanceof ExceptionWithKey){
                    // failed callable
                    ExceptionWithKey exceptionWithKey = (ExceptionWithKey) cause;
                    TaskResult<ReturnType> taskResult = new TaskResult<>();
                    taskResult.setException(exceptionWithKey);
                    results.add(taskResult);
                }
                else if(ee instanceof InterruptedException)
                {
                    log.error("restUtil callable interrupted", "which should not happen", "");
                    throw new IllegalStateException("restUtil callable interrupted");
                }
                else{
                    log.error("Callable passed to callInParallelWithTimeout should only throw ExceptionWithKey", "otherwise we do not know which task fails", "");
                    throw new IllegalArgumentException("bad argument for callInParallelWithTimeout");
                }
            }
        }
        return results;
    }

}
