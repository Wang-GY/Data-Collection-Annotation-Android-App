package com.weiju.springboot.service.impl;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import java.util.*;

@Service("Task srevice")
public class TaskServiceImpl implements TaskService {

    private final JdbcTemplate jdbcTemplate;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static FileSystem fs = FileSystems.getDefault();
    private final FileService fileService;

    @Autowired
    private final Environment environment;

    public TaskServiceImpl(JdbcTemplate jdbcTemplate, TaskRepository taskRepository,
                           UserRepository userRepository, Environment environment, FileService fileService) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.environment = environment;
        this.fileService = fileService;
    }

    @Override
    public Task createTask(int user_id, String formater, String title, String start_time, String deadline,
                           String description, int type, String name) throws BaseException {
        if (!userRepository.existsById(user_id)) {
            throw new BaseException("user not exist", "can not find user by user_id", HttpStatus.NOT_FOUND);
        }
        Task task = new Task();
        task.setFormatter(formater);
        task.setName(title);
        task.setCreator(userRepository.findByUserid(user_id));
        task.setStart_time(start_time);
        task.setDeadline(deadline);
        task.setDescription(description);
        task.setType(type);
        task.setName(name);
        try {
            taskRepository.save(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("create task fail ", "MySQLIntegrityConstraintViolationException", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        return task;
    }

    @Override
    public Task getTaskProfile(int id) {
        return taskRepository.findByTaskid(id);
    }

    @Override
    public Task updateTaskProfile(Map<String, Object> task_info) throws BaseException {

        int taskid = (Integer) task_info.get("id");


        Task task = taskRepository.findByTaskid(taskid);
        if (task != null) {
            for (Map.Entry entry : task_info.entrySet()) {
                if (entry.getKey().equals("id")) {
                    continue;
                }
                //TODO update deadline and formatter
                switch ((String) entry.getKey()) {
                    case "name":
                        logger.info((String) entry.getValue());
                        task.setName((String) entry.getValue());
                        break;
                    case "description":
                        task.setDescription((String) entry.getValue());
                        break;
                    case "size":
                        task.setSize((Integer) entry.getValue());
                        break;
                    case "formatter":
                        logger.info("change formatter:");
                        logger.info(new JSONObject(entry.getValue()).toString());
                        Map<String, Object> map = (Map<String, Object>) entry.getValue();
                        JSONObject jsonObject = new JSONObject(map);
                        task.setFormatter(jsonObject.toString());
                        break;
                    case "deadline":
                        try {
                            Long.parseLong((String) entry.getValue());
                        } catch (NumberFormatException e) {
                            throw new BaseException("update fail", "start_time can not cast to long", HttpStatus.BAD_REQUEST);
                        }
                        if (new Date(Long.parseLong((String) entry.getValue())).before(new Date(Long.parseLong(task.getStart_time())))) {
                            throw new BaseException("update fail", "deadline before start_time", HttpStatus.BAD_REQUEST);
                        }
                        task.setDeadline((String) entry.getValue());
                        break;
                    case "type":
                        task.setType((int) entry.getValue());
                        break;
                    case "cover":
                        task.setCover((String) entry.getValue());
                        break;
                    case "start_time":
                        try {
                            Long.parseLong((String) entry.getValue());
                        } catch (NumberFormatException e) {
                            throw new BaseException("json error", "start_time can not cast to long", HttpStatus.BAD_REQUEST);
                        }

                        task.setStart_time((String) entry.getValue());
                        break;
//                    default:
//                        throw new BaseException("update fail", String.format("can not update this field: %s, you are not allowed or key error", (String) entry.getKey()), HttpStatus.BAD_REQUEST);

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

    /**
     * get all pictures
     *
     * @param task_id
     * @return
     */
    @Override
    public List<String> getPicsByTaskId(int task_id) {
        String basePath = Paths.get(".").toAbsolutePath().normalize().toString();


        String picPath = basePath + fs.getSeparator() + "data" + fs.getSeparator() + "tasks"
                + fs.getSeparator() + task_id + fs.getSeparator() + "pictures";

        File folder = new File(picPath);
        File[] files = folder.listFiles();
        List<String> fileURIs = new LinkedList<>();

        String port = environment.getProperty("local.server.port");
        String ip = environment.getProperty("myip");

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String relativePath = "/tasks/"
                            + task_id + "/pictures/" + file.getName();
                    fileURIs.add(fileService.relativePathToUrl(relativePath));
                }

            }
        }
        return fileURIs;
    }

    /**
     * get task cover by task id paged
     *
     * @param task_id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<String> getPicsByTaskId(int task_id, int pageNum, int pageSize) throws BaseException {
        List<String> all = getPicsByTaskId(task_id);
        if (pageNum * pageSize >= 0 && pageNum * pageSize <= all.size()) {
            try {
                return all.subList(pageNum * pageSize, (pageNum + 1) * pageSize);
            } catch (ArrayIndexOutOfBoundsException e) {
                return all.subList(pageNum * pageSize, all.size());
            }

        } else {
            throw new BaseException("", "", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get task cover by task id
     *
     * @param task_id
     * @return
     */
    @Override
    public String getCoverByTaskId(int task_id) throws BaseException {
        Task task = taskRepository.findByTaskid(task_id);
        if (task == null) {
            throw new BaseException("task not fount", String.format("can not find task by this id:%d", task_id), HttpStatus.NOT_FOUND);
        }
        return task.getCover();

    }

    /**
     * return true if the passed deadline
     *
     * @param task
     * @return
     * @throws BaseException
     */
    @Override
    public boolean isTaskPassDeadline(Task task) throws BaseException {
        try {
            Long.parseLong(task.getDeadline());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new BaseException("deadline format error", String.format("deadline %s can not be casted to string", task.getDeadline()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Date deadline = new Date(Long.parseLong(task.getDeadline()));
        return deadline.before(new Date());
    }
}
