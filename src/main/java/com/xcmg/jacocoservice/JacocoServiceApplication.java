package com.xcmg.jacocoservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xcmg.jacocoservice.mapper")
public class JacocoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JacocoServiceApplication.class, args);
    }

}
