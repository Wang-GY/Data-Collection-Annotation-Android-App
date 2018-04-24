package com.weiju.springboot.util;


import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * 描述数据存储位置
 */
@Service("file properties")
public class FileProperties {

    private static final String BASE_DATA_PATH = Paths.get(".").toAbsolutePath().toString();
    private static final String TASK_DATA_PATH = "Data/Tasks";


    public Path getTaskDataPath(int taskid) {

        return Paths.get(BASE_DATA_PATH, TASK_DATA_PATH + "/" + String.valueOf(taskid));
    }

    public Path getTaskPicturePath(int taskid) {

        return getTaskDataPath(taskid).resolve("pictures");

    }




}
