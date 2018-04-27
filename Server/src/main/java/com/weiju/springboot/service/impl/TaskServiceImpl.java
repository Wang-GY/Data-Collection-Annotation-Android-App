package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.service.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("Task srevice")
public class TaskServiceImpl implements TaskService {

    private final JdbcTemplate jdbcTemplate;

    private final TaskRepository taskRepository;

    public TaskServiceImpl(JdbcTemplate jdbcTemplate, TaskRepository taskRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(int uuid, String formater, String title, String start_time, String deadline,
        String description, int type) {
        Task task = new Task();
        task.setFormater(formater);
        task.setName(title);
        task.setCreator(uuid);
        task.setStart_time(start_time);
        task.setDeadline(deadline);
        task.setDescription(description);
        task.setType(type);
        taskRepository.save(task);
        return task;
    }

    @Override
    public Task getTaskProfile(int id) {
        return taskRepository.findByTaskid(id);
    }

    @Override
    public Task updateTaskProfile(int taskid, String formater) {
        Task task = getTaskProfile(taskid);
        if (task == null) {
            return null;
        }
        task.setFormater(formater);
        taskRepository.save(task);
        return task;
    }

    @Override
    public Iterable<Task> getTasks(int offset, int limit) {
        Pageable pageable = new PageRequest(offset, limit);
        return taskRepository.findAll(pageable);
    }

    @Override
    public int applyTask(int taskid, int applyer) {
        Task task = getTaskProfile(taskid);
        if (task == null) {
            return -1;
        }
        return 0;
    }
}
