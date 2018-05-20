package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("Task srevice")
public class TaskServiceImpl implements TaskService {

    private final JdbcTemplate jdbcTemplate;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;
    public TaskServiceImpl(JdbcTemplate jdbcTemplate, TaskRepository taskRepository, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
        this. userRepository = userRepository;
    }

    @Override
    public Task createTask(int uuid, String formater, String title, String start_time, String deadline,
        String description, int type) {
        Task task = new Task();
        task.setFormater(formater);
        task.setName(title);
        task.setCreator(userRepository.findByUserid(uuid));
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
    public Task updateTaskProfile(Map<String, Object> task_info) {

        int taskid = Integer.parseInt((String)task_info.get("id"));

        Task task = taskRepository.findByTaskid(taskid);
        if (task != null) {
            for (Map.Entry entry: task_info.entrySet()) {
                if (entry.getKey().equals("id")) {
                    continue;
                }

                switch ((String)entry.getKey()) {
                case "name":
                    task.setName(entry.getValue().toString());
                    break;
                case "description":
                    task.setDescription((String) entry.getValue());
                    break;
                case "size":
                    task.setSize((Integer)entry.getValue());
                    break;
                }
            }
            taskRepository.save(task);
        }
        return task;
    }

    @Override
    public Page<Task> getTasks(int offset, int limit) {
        //Pageable pageable = new PageRequest(offset, limit);
        Pageable pageable = PageRequest.of(offset, limit);
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
