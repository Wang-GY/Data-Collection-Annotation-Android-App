package com.weiju.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.weiju.springboot"})
public class WeijuApp {
    public static void main(String[] args){
        SpringApplication.run(WeijuApp.class,args);
    }

}
