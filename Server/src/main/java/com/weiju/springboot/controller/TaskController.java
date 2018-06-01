package com.weiju.springboot.controller;

import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.service.CommitService;
import com.weiju.springboot.service.FileService;
import com.weiju.springboot.service.TaskService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Payload;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.io.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final CommitService commitService;
    private static Logger logger = LoggerFactory.getLogger(FileService.class);
    private static FileSystem fs = FileSystems.getDefault();

    @Autowired
    private final Environment environment;


    @Autowired
    public TaskController(TaskRepository taskRepository, TaskService taskService,
                          Environment environment, CommitService commitService) {
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.environment = environment;
        this.commitService = commitService;
    }


    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody Map<String, Map<String, Object>> payload) {
        logger.info("try to create a task");
        JSONObject data = new JSONObject(payload.get("data"));
        int user_id = data.getInt("user_id");
        String formatter = data.getJSONObject("formatter").toString();
        String title = data.getString("title");
        String start_time = data.getString("start_time");
        String deadline = data.getString("deadline");
        String description = data.getString("description");
        int type = data.getInt("type");

        logger.info("finish extract task information");
        taskService.createTask(user_id, formatter, title, start_time, deadline, description, type);

        return new ResponseEntity(HttpStatus.CREATED);
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
        JSONObject response = new JSONObject();
        JSONObject taskData = new JSONObject();

        Pageable pageable = PageRequest.of(offset, limit);
        //System.out.println(pageable.next().toString());


        if (((PageRequest) pageable).previous() != null) {
            response.put("previous", "http://206.189.35.98:12000/api/tasks/?offset="
                    + ((PageRequest) pageable).previous().getPageNumber() +
                    "&limit=" + ((PageRequest) pageable).previous().getOffset());
        }
        if (pageable.next() != null) {
            response.put("next", "http://206.189.35.98:12000/api/tasks/?offset="
                    + pageable.next().getPageNumber() +
                    "&limit=" + pageable.next().getOffset());
        }

        Page<Task> tasks = taskRepository.findAll(pageable);
        List<JSONObject> tasksInfo = new LinkedList<>();
        for (Task task : tasks) {
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


        taskData.put("tasks", tasksInfo);
        response.put("data", taskData);
        //response.put("next", tasks.nextPageable().toString());

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }


    @GetMapping("/{task_id}")
    public ResponseEntity<String> getTaskById(@PathVariable(value = "task_id", required = true) String task_id) {
        JSONObject payload = new JSONObject();
        JSONObject taskJSON = new JSONObject();
        Task task = taskService.getTaskProfile(Integer.parseInt(task_id));
        if (task != null) {
            String basePath = Paths.get(".").toAbsolutePath().normalize().toString();
            String picPath = basePath + fs.getSeparator() + "data" + fs.getSeparator() + "tasks"
                    + fs.getSeparator() + task_id + fs.getSeparator() + "pictures";
            //logger.info(picPath);
            File folder = new File(picPath);
            File[] files = folder.listFiles();
            List<String> fileURIs = new LinkedList<>();
            String port = environment.getProperty("local.server.port");
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileURIs.add(
                                "http://" + "206.189.35.98" + ":" + port + "/api/file/tasks/" + task_id + "/pictures/" + file.getName());
                    }
                    logger.info(file.getName());
                }
            }
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
            taskJSON.put("pictures", fileURIs);
            //TODO formater to formatter
            taskJSON.put("formatter", task.getFormatter());

            payload.put("data", taskJSON);
            return new ResponseEntity<>(payload.toString(), HttpStatus.OK);
        }
        taskJSON.put("title", "No such task.");
        taskJSON.put("detail", "No such task.");

        payload.put("error", taskJSON);
        return new ResponseEntity<>(payload.toString(), HttpStatus.NOT_FOUND);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable(value = "id") String id,
                                             @RequestBody Map<String, Map<String, Object>> payload) {
        JSONObject data = new JSONObject();
        int idI = Integer.parseInt((String) payload.get("data").get("id"));
        if (Integer.parseInt(id) != idI) {
            return new ResponseEntity<>("{\"error\", \"Bad Request\"", HttpStatus.BAD_REQUEST);
        }
        Task task = taskService.updateTaskProfile(payload.get("data"));
        if (task != null) {
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
            taskJSON.put("formatter", task.getFormatter());
            data.put("data", taskJSON);
        } else {
            return new ResponseEntity<>("{\"error\": \"Data not Found\"}", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data.toString(), HttpStatus.OK);
    }

    @PostMapping("/apply")
    public ResponseEntity<String> applyTask(@RequestBody Map<String, Map<String, Object>> payload) {
        Map<String, Object> data = payload.get("data");
        int task_id = (Integer) data.get("task_id");
        int user_id = (Integer) data.get("user_id");

        Task task = taskService.getTaskProfile(task_id);

        JSONObject returnData = new JSONObject();


        if (task != null) {
            if (task.getType() == 0) {
                commitService.save(task_id, user_id, task.getSize());
                List<String> picsURI = taskService.getPicsByTaskId(task_id);
                returnData.put("commit_id", user_id);
                returnData.put("size", task.getSize());
                returnData.put("task_id", task.getTaskid());
                returnData.put("type", task.getType());
                returnData.put("pictures", picsURI);

            } else if (task.getType() == 1) {
                returnData.put("size", task.getSize());
                returnData.put("task_id", task.getTaskid());
                returnData.put("type", task.getType());
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(returnData.toString(), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


}
