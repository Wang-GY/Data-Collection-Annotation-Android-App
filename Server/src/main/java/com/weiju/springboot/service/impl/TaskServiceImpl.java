package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.service.TaskService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public class TaskServiceImpl implements TaskService {

    private final JdbcTemplate jdbcTemplate;

    private final TaskRepository taskRepository;

    public TaskServiceImpl(JdbcTemplate jdbcTemplate, TaskRepository taskRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(String formater, String title, String start, String end, String description) {
        return null;
    }

    @Override
    public Task getTaskProfile(int id) {
        return null;
    }

    @Override
    public Task updateTaskProfile(Map<Integer, Object> task_info) {
        return null;
    }

    @Override
    public Iterable<Task> getTasks() {
        return null;
    }

    @Override
    public int applyTask(int taskid, int applyer) {
        return 0;
    }
}
