package com.fjdialect;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fjdialect.mapper")
public class FJDialectApplication {

    public static void main(String[] args) {
        SpringApplication.run(FJDialectApplication.class, args);
    }

}
