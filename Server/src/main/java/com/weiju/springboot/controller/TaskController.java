package com.weiju.springboot.controller;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.service.TaskService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public HttpStatus createTask(@RequestBody Map<String, Map<String, Object>> payload) {
        JSONObject data = new JSONObject(payload.get("data"));
        int uuid = Integer.parseInt(data.getString("uuid"));
        String formatter = data.getString("formatter");
        String title  = data.getString("title");
        String start_time = data.getString("start_time");
        String deadline = data.getString("deadline");
        String description = data.getString("description");
        int type = Integer.parseInt(data.getString("type"));

        taskService.createTask(uuid, formatter, title, start_time, deadline, description, type);

        return HttpStatus.OK;
    }

    //TODO
    @GetMapping("/")
    public Iterable<Task> getTasks(@RequestParam Map<String, Integer> requestParams) {
        return null;
    }

    //TODO
    @GetMapping("/")
    public String getTaskById(@RequestParam(value = "task_id", required = true) String task_id) {
        return null;
    }

    //TODO
    @PatchMapping("/{id}")
    public String updateTask(@PathVariable(value = "id") String id) {
        return null;
    }



}
