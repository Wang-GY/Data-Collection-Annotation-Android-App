package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.*;
import com.weiju.springboot.repository.CommitDataRepository;
import com.weiju.springboot.repository.CommitRepository;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.CommitDataService;
import com.weiju.springboot.service.CommitService;
import com.weiju.springboot.service.FileService;

import com.weiju.springboot.service.TaskService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 处理commit相关api
 */
@Controller
@RequestMapping("api/commits")

public class CommitController {
    private static Logger logger = LoggerFactory.getLogger(CommitController.class);
    final CommitRepository commitRepository;
    final FileService fileService;
    final UserRepository userRepository;
    final TaskRepository taskRepository;
    final CommitDataRepository commitDataRepository;
    final CommitService commitService;
    final CommitDataService commitDataService;
    final TaskService taskService;
    static final int ANNOTATION = 0;
    static final int COLLECTION = 1;

    @Autowired
    public CommitController(CommitRepository commitRepository, FileService fileService, UserRepository userRepository,
                            TaskRepository taskRepository,
                            CommitDataRepository commitDataRepository,
                            CommitService commitService,
                            CommitDataService commitDataService,
                            TaskService taskService) {
        this.commitRepository = commitRepository;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commitDataRepository = commitDataRepository;
        this.commitService = commitService;
        this.commitDataService = commitDataService;
        this.taskService = taskService;
    }

    /**
     * 用户上传一个提交
     * 处理标注任务
     *
     * @param payload request body
     * @return
     */
    @PostMapping("/")
    @PreAuthorize("hasAnyRole('USER_ANNOTATION_COLLECTION')")
    public ResponseEntity<String> uploadCommit(@RequestBody Map<String, Map<String, Object>> payload) throws BaseException {

        logger.info("request body " + payload.toString());
        Map<String, Object> commit_data = payload.get("data");
        if (commit_data == null) {
            throw new BaseException("json error", "con not find 'data'", HttpStatus.NOT_FOUND);
        }
        logger.info("commit data: " + commit_data);

        try {
            Integer commit_id = (Integer) commit_data.get("commit_id");
            List<Map<String, String>> results = (List<Map<String, String>>) commit_data.get("result");

            List<String> pictures = (List<String>) commit_data.get("pictures");
            /*
            * Map<String, Object> map = (Map<String, Object>) entry.getValue();
                        JSONObject jsonObject = new JSONObject(map);
                        task.setFormatter(jsonObject.toString());
            * */
            List<String> tags = (List<String>) commit_data.get("tags");
            if (commit_id == null || pictures == null || tags == null) {
                throw new BaseException("json key error: commit_id or pictures or tags  not found", HttpStatus.NOT_FOUND);
            }

            Commit commit = commitRepository.findByCommitid(commit_id);

            if (commit == null) {
                throw new BaseException("commit not found", String.format("can not find commit by commid_id:%d", commit_id), HttpStatus.NOT_FOUND);
            }

            if (commit.getTask().getType() != ANNOTATION) {
                throw new BaseException("task_type not match", "this not a annotation task", HttpStatus.BAD_REQUEST);
            }
            List<CommitData> commitDatas = commit.getCommitDataList();
            if (taskService.isTaskPassDeadline(commit.getTask())) {
                throw new BaseException("try to commit after deadline", "deadline: " + commit.getTask().getDeadline(), HttpStatus.BAD_REQUEST);
            }
            if (pictures.size() != tags.size()) {
                throw new BaseException("json error", "pictures and tags can not match", HttpStatus.BAD_REQUEST);
            }

            if (pictures.size() > commit.getSize() - commitDatas.size()) {
                throw new BaseException("Commit too many entries", String.format("you can upload at most %d entries but you are trying to upload %d entries", commit.getSize() - commit.getCommitDataList().size(), results.size()), HttpStatus.BAD_REQUEST);
            }

            for (int i = 0; i < pictures.size(); i++) {
                String picture_url = pictures.get(i);
                String tag = tags.get(i);

                if (picture_url == null || tag == null) {
                    throw new BaseException("json key error: picture_url or tag not found", HttpStatus.NOT_FOUND);
                }
                String relativePath = fileService.getRelativePathByUrl(picture_url);
                logger.info("picture path : " + relativePath);
                String pictureName = fileService.getFilenameByRelativePath(relativePath);

                // construct annotation_json path
                relativePath = fileService.getParentPath(relativePath);
                logger.info("picture parent path: " + relativePath);
                relativePath = relativePath.replace("pictures", "annotations");// change directory
                relativePath = relativePath + "/" + pictureName;

                logger.info(" annotation  path :" + relativePath);
                String jsonName = fileService.getNewFilename("-" + String.valueOf(commit.getCommitter().getUserid()) + ".json");
                logger.info("annotation_jsonName: " + jsonName);
                try {
                    fileService.storeString(tag, jsonName, relativePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new BaseException("file not found", String.format("can not find file at %s", relativePath), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                // save commit data
                commitDataService.save(commit, relativePath + "/" + jsonName);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            throw new BaseException("json object field not found", HttpStatus.NOT_FOUND, e);

        }

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    /**
     * 提交采集任务的图片
     *
     * @param commitid
     * @param multipartFiles 图片
     * @return 新文件路径（用作测试）
     * @throws BaseException
     */
    @PostMapping("/pictures/{commitid}/")
    @PreAuthorize("hasRole('USER_ANNOTATION_COLLECTION')")
    public ResponseEntity<String> uploadCollectionData(@PathVariable(value = "commitid") int commitid, @RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("commit picture with id : " + String.valueOf(commitid));
        // get commit
        // TODO: double check has already committed
        Commit commit = commitRepository.findByCommitid(commitid);
        if (commit == null) {
            throw new BaseException("commit fail", "can not find commit", HttpStatus.NOT_FOUND);
        }
        if (commit.getTask().getType() != COLLECTION) {
            throw new BaseException("commit fail", "try to upload pictures to a annotation job", HttpStatus.BAD_REQUEST);
        }

        if (taskService.isTaskPassDeadline(commit.getTask())) {
            throw new BaseException("try to commit after deadline", "deadline: " + commit.getTask().getDeadline(), HttpStatus.BAD_REQUEST);
        }

        if (multipartFiles.size() > commit.getSize() - commit.getCommitDataList().size()) {
            throw new BaseException("Commit too many entries", String.format("you can upload at most %d entries but you are trying to upload %d entries", commit.getSize() - commit.getCommitDataList().size(), multipartFiles.size()), HttpStatus.BAD_REQUEST);
        }
        int taskid = commit.getTask().getTaskid();
        String id = String.valueOf(taskid);
        List<String> url_list = fileService.uploadFiles(multipartFiles, "/tasks/" + id + "/pictures");

        for (String item : url_list) {
            commitDataService.save(commit, fileService.getRelativePathByUrl(item));
        }
        JSONObject response = new JSONObject();
        response.put("data", url_list);
        return ResponseEntity.created(null).contentType(MediaType.APPLICATION_JSON).body(response.toString());
        //return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);
    }


    /**
     * 给定userid,task_id,limit 返回
     * api: get user commits
     *
     * @param userid
     * @param task_id
     * @param pageNum
     * @return
     */
    @GetMapping()
    @PreAuthorize("hasAnyRole('USER_ANNOTATION_COLLECTION','ADMIN')")
    public ResponseEntity<String> getUserCommits(@RequestParam(name = "user", required = false, defaultValue = "") String userid,
                                                 @RequestParam(name = "task", required = false, defaultValue = "") String task_id,
                                                 @RequestParam(name = "pageNum", required = false, defaultValue = "0") int pageNum,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) throws BaseException {


        try {
            User user = null;
            Task task = null;
            Page<Commit> commits;
            if (!userid.equals(""))
                user = userRepository.findByUserid(Integer.parseInt(userid));
            if (!task_id.equals(""))
                task = taskRepository.findByTaskid(Integer.parseInt(task_id));
            if (user != null && task != null) {
                //commits = commitRepository.findByCommitterAndAndTask(user, task);
                logger.info("findByCommitterAndAndTask");
                commits = commitService.findByCommitterAndAndTask(user, task, pageNum, pageSize);
            } else if (user != null) {
                //commits = commitRepository.findByCommitter(user);
                logger.info("findByCommitter");
                logger.info(String.valueOf(user.getUserid()));
                commits = commitService.findByCommitter(user, pageNum, pageSize);
            } else {
                //commits = commitRepository.findByTask(task);
                logger.info("findByTask");
                commits = commitService.findByTask(task, pageNum, pageSize);

            }

            List<JSONObject> responseCommits = new LinkedList<>();

            // put commit info into response
            for (Commit commit : commits) {
                JSONObject comitInfo = new JSONObject();
                comitInfo.put("commit_id", commit.getCommitid());
                comitInfo.put("task_id", commit.getTask().getTaskid());
                comitInfo.put("user_id", commit.getCommitter().getUserid());
                responseCommits.add(comitInfo);

                // TODO get commit data

                logger.info(comitInfo.toString());
            }


            JSONObject responseData = new JSONObject();
            JSONObject response = new JSONObject();
            if (commits != null) { // find some commit
                logger.info("try to put commits");
                responseData.put("commits", responseCommits);
                logger.info("put commits into data");
                response.put("data", responseData);

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response.toString());
                //return new ResponseEntity<>(response.toString(), HttpStatus.OK);

            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            throw new BaseException("Number format exception", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

    }


}
