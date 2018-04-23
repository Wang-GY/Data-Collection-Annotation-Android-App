package com.weiju.springboot.util;

import com.weiju.springboot.exception.BaseException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * 描述数据存储位置
 */
@Service("file properties")
public class FileProperties {

    private static final String BASE_DATA_PATH = Paths.get(".").toAbsolutePath().toString();
    private static final String TASK_DATA_PATH = "Data/Tasks";
//    FileProperties() throws BaseException{
//        Path datapath = Paths.get(DATA_PATH);
//        Path taskpath = Paths.get(TASK_DATA_PATH);
//        try {
//            Files.createDirectory(datapath);
//            Files.createDirectory(taskpath);
//
//        }catch (FileAlreadyExistsException e){
//            throw new BaseException("File Already Exits", HttpStatus.INTERNAL_SERVER_ERROR,e);
//        }catch (IOException e){
//            throw new BaseException("IO exception", HttpStatus.INTERNAL_SERVER_ERROR,e);
//        }
//    }

    public Path getTaskDataPath(int taskid) {

        return Paths.get(BASE_DATA_PATH, TASK_DATA_PATH + "/" + String.valueOf(taskid));
    }

    public Path getTaskPicturePath(int taskid) {

        return getTaskDataPath(taskid).resolve("pictures");

    }




}
