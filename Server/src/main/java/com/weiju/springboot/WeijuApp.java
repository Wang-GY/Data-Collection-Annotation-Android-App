package com.weiju.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication(scanBasePackages={"com.weiju.springboot"})
public class WeijuApp {
    public static void main(String[] args){
        SpringApplication.run(WeijuApp.class,args);
    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("200MB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("200MB");
        return factory.createMultipartConfig();
    }
}
