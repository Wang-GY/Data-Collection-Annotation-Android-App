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


    @GetMapping("/")
    public Iterable<Task> getTasks(@RequestParam Map<String, String> requestParams) {
        int offset = Integer.parseInt(requestParams.get("offset"));
        int limit = Integer.parseInt(requestParams.get("limit"));

        Iterable<Task> tasks = taskService.getTasks(offset, limit);
        return tasks;
    }

    @GetMapping("/{task_id}")
    public String getTaskById(@RequestParam(value = "task_id", required = true) String task_id) {
        return taskService.getTaskProfile(Integer.parseInt(task_id)).toString();
    }

    @PatchMapping("/{id}")
    public String updateTask(@PathVariable(value = "id") String id,
        @RequestBody Map<String, Map<String, Object>> payload) {
        JSONObject data = new JSONObject(payload.get("data"));
        int idI = Integer.parseInt(data.getString("id"));
        if (Integer.parseInt(id) != idI) {
            return null;
        }

        int size = Integer.parseInt(data.getString("size"));

        taskService.updateTaskProfile(idI, data.getString("name"),
            data.getString("description"), size);

        return taskService.getTaskProfile(idI).toString();
    }



}
