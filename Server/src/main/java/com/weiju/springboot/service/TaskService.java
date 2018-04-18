package com.weiju.springboot.service;

import com.weiju.springboot.model.Task;

import java.util.Map;

public interface TaskService {

    public Task createTask(String formater, String title, String start, String end, String description);

    public Task getTaskProfile(int id);

    public Task updateTaskProfile(Map<Integer, Object> task_info);

    public Iterable<Task> getTasks();

    public int applyTask(int taskid, int applyer);
}
