package com.weiju.springboot.service;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface TaskService {

    public Task createTask(int user_id, String formater, String title, String start, String deadline, String description, int type) throws BaseException;

    public Task getTaskProfile(int id);

    public Task updateTaskProfile(Map<String, Object> task_info) throws BaseException;

    public Page<Task> getTasks(int offset, int limit);

    public List<String> getPicsByTaskId(int task_id);


    List<String> getPicsByTaskId(int task_id, int pageNum, int pageSize) throws BaseException;

    String getCoverByTaskId(int task_id) throws BaseException;
}
