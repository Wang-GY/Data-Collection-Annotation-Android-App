package com.weiju.springboot.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;


/**
 * 描述数据存储位置
 */

public class FileProperties {

    private static final String DATA_PATH = "/Data";
    private static final String TASK_DATA_PATH = "/Data/Tasks";

    public String getTaskDataPath(int taskid) {
        return TASK_DATA_PATH + "/" + String.valueOf(taskid);
    }

    public String getTaskPicturePath(int taskid) {
        return getTaskDataPath(taskid) + "/pictures";
    }


}
