package com.weiju.springboot.controller;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.service.TaskService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    //TODO
    @PostMapping("/")
    public @ResponseBody String createTask(@RequestBody Map<String, Map<String, Object>> payload) {
        return null;
    }

    //TODO
    @GetMapping("/")
    public Iterable<Task> getTasks(@RequestParam Map<String, Integer> requestParams) {
        return null;
    }

    //TODO


}
