package com.weiju.springboot.service;

import com.weiju.springboot.model.Task;
import org.springframework.stereotype.Service;


public interface TaskService {

    public Task createTask(int uuid, String formater, String title, String start, String deadline, String description, int type);

    public Task getTaskProfile(int id);

    public Task updateTaskProfile(int taskid, String formater);

    public Iterable<Task> getTasks(int offset, int limit);

    public int applyTask(int taskid, int applyer);
}
