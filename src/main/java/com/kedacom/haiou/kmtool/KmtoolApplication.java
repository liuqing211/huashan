package com.kedacom.haiou.kmtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class KmtoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(KmtoolApplication.class, args);
    }

}
