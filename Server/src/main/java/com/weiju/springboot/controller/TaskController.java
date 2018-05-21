package com.weiju.springboot.controller;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.service.TaskService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
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
        String formatter = data.getJSONObject("formatter").toString();
        String title  = data.getString("title");
        String start_time = data.getString("start_time");
        String deadline = data.getString("deadline");
        String description = data.getString("description");
        int type = data.getInt("type");

        taskService.createTask(uuid, formatter, title, start_time, deadline, description, type);

        return HttpStatus.CREATED;
    }


    @GetMapping("/")
    public ResponseEntity<String> getTasks(@RequestParam Map<String, String> requestParams) {
        int offset = 0;
        int limit = 10;
        if (requestParams.get("offset") != null) {
            offset = Integer.parseInt(requestParams.get("offset"));
        }
        if (requestParams.get("limit") != null) {
            limit = Integer.parseInt(requestParams.get("limit"));
        }

        Page<Task> tasks = taskService.getTasks(offset, limit);
        List<JSONObject> tasksInfo = new LinkedList<>();
        for (Task task: tasks) {
            JSONObject taskJSON = new JSONObject();
            taskJSON.put("name", task.getName());
            taskJSON.put("id", task.getTaskid());
            taskJSON.put("start_time", task.getStart_time());
            taskJSON.put("type", task.getType());
            taskJSON.put("size", task.getSize());
            taskJSON.put("data_path", task.getData_path());
            taskJSON.put("creator", task.getCreator().getUserid());
            taskJSON.put("description", task.getDescription());
            taskJSON.put("progress", task.getProgress());
            taskJSON.put("deadline", task.getDeadline());
            taskJSON.put("formater", task.getFormatter());
            tasksInfo.add(taskJSON);
        }

        JSONObject taskData = new JSONObject();
        taskData.put("tasks", tasksInfo);
        JSONObject response = new JSONObject();

        response.put("data", taskData);

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }



    @GetMapping("/{task_id}")
    public String getTaskById(@PathVariable(value = "task_id", required = true) String task_id) {
        JSONObject payload = new JSONObject();
        Task task = taskService.getTaskProfile(Integer.parseInt(task_id));
        JSONObject taskJSON = new JSONObject();
        taskJSON.put("name", task.getName());
        taskJSON.put("id", task.getTaskid());
        taskJSON.put("start_time", task.getStart_time());
        taskJSON.put("type", task.getType());
        taskJSON.put("size", task.getSize());
        taskJSON.put("description", task.getDescription());
        taskJSON.put("data_path", task.getData_path());
        taskJSON.put("creator", task.getCreator().getUserid());
        taskJSON.put("progress", task.getProgress());
        taskJSON.put("deadline", task.getDeadline());
        //TODO formater to formatter
        taskJSON.put("formater", task.getFormatter());

        payload.put("data", taskJSON);
        return payload.toString();
    }

    @PatchMapping("/{id}")
    public String updateTask(@PathVariable(value = "id") String id,
        @RequestBody Map<String, Map<String, Object>> payload) {
        JSONObject data = new JSONObject();
        int idI = Integer.parseInt((String) payload.get("data").get("id"));
        if (Integer.parseInt(id) != idI) {
            return null;
        }
        taskService.updateTaskProfile(payload.get("data"));
        data.put("data", taskService.getTaskProfile(idI));

        return data.toString();
    }



}
