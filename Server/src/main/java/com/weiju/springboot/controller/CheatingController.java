package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.FileService;
import com.weiju.springboot.service.TaskService;
import com.weiju.springboot.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * only for developer
 */
@RestController
@RequestMapping("/cheating/api")
public class CheatingController {

    @Autowired
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final FileService fileService;
    private final UserService userService;
    private final UserRepository userRepository;

    public CheatingController(TaskService taskService,
                              TaskRepository taskRepository,
                              FileService fileService,
                              UserService userService,
                              UserRepository userRepository
    ) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.fileService = fileService;
        this.userService = userService;
        this.userRepository = userRepository;

    }

    /**
     * upload pictures for a task by task id
     *
     * @param task_id
     * @param multipartFiles
     * @return
     * @throws BaseException
     */
    @PostMapping("/tasks/{task_id}/pictures")
    public ResponseEntity uploadTaskPic(@PathVariable(value = "task_id") int task_id, @RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        Task task = taskRepository.findByTaskid(task_id);
        if (task == null) {
            throw new BaseException("Task not found", String.format("can not find task by task_id %d", task_id), HttpStatus.NOT_FOUND);
        }
        List<String> url_list = fileService.uploadFiles(multipartFiles, "/tasks/" + String.valueOf(task_id) + "/pictures");
        JSONObject data = new JSONObject();
        data.put("uploaded file urls", url_list);

        JSONObject meta = new JSONObject();
        meta.put("total pictures number", taskService.getPicsByTaskId(task_id).size());

        JSONObject response = new JSONObject();
        response.put("data", data);
        response.put("meta", meta);
        return ResponseEntity.created(null).contentType(MediaType.APPLICATION_JSON).body(response.toString());
    }

    /**
     * get all users from database
     *
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity getAllUsers() {

        Iterable<User> all_user = userRepository.findAll();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", all_user);
        return ResponseEntity.ok().body(response);
    }
}
