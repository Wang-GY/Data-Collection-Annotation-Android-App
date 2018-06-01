package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service("Task srevice")
public class TaskServiceImpl implements TaskService {

    private final JdbcTemplate jdbcTemplate;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private static FileSystem fs = FileSystems.getDefault();

    @Autowired
    private final Environment environment;

    public TaskServiceImpl(JdbcTemplate jdbcTemplate, TaskRepository taskRepository,
                           UserRepository userRepository, Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.environment = environment;
    }

    @Override
    public Task createTask(int user_id, String formater, String title, String start_time, String deadline,
                           String description, int type) {
        Task task = new Task();
        task.setFormatter(formater);
        task.setName(title);
        task.setCreator(userRepository.findByUserid(user_id));
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

        int taskid = Integer.parseInt((String) task_info.get("id"));

        Task task = taskRepository.findByTaskid(taskid);
        if (task != null) {
            for (Map.Entry entry : task_info.entrySet()) {
                if (entry.getKey().equals("id")) {
                    continue;
                }

                switch ((String) entry.getKey()) {
                    case "name":
                        task.setName(entry.getValue().toString());
                        break;
                    case "description":
                        task.setDescription((String) entry.getValue());
                        break;
                    case "size":
                        task.setSize((Integer) entry.getValue());
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
        System.out.println(pageable.next().toString());
        return taskRepository.findAll(pageable);
    }


    @Override
    public List<String> getPicsByTaskId(int task_id) {
        String basePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String picPath = basePath + fs.getSeparator() + "data" + fs.getSeparator() + "tasks"
                + fs.getSeparator() + task_id + fs.getSeparator() + "pictures";

        File folder = new File(picPath);
        File[] files = folder.listFiles();
        List<String> fileURIs = new LinkedList<>();

        String port = environment.getProperty("local.server.port");


        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileURIs.add("http://" + "206.189.35.98" + ":" + port + "/api/tasks/" + task_id
                            + "/pictures/" + file.getName()
                    );
                }

            }
        }
        return fileURIs;
    }

}
