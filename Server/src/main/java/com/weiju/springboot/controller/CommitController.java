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

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    public CommitController(CommitRepository commitRepository, FileService fileService, UserRepository userRepository,
                            TaskRepository taskRepository,
                            CommitDataRepository commitDataRepository,
                            CommitService commitService,
                            CommitDataService commitDataService) {
        this.commitRepository = commitRepository;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commitDataRepository = commitDataRepository;
        this.commitService = commitService;
        this.commitDataService = commitDataService;
    }

    /**
     * 用户上传一个提交
     * 分别处理采集任务和标注任务
     *
     * @param payload request body
     * @return
     */
    @PostMapping("/")
    public ResponseEntity<String> uploadCommit(@RequestBody Map<String, Map<String, Object>> payload) throws BaseException {

        try {
            logger.info("request body " + payload.toString());
            Map<String, Object> commit_data = payload.get("data");
            logger.info("commit data: " + commit_data);

            int task_type = (int) commit_data.get("task_type");
            logger.info("commit type:" + task_type);

            switch (task_type) {
                case 0: // Annotation
                    logger.info("Annotation");
                    return uploadAnnotationCommit(commit_data);
                case 1: // Collection
                    logger.info("Collection");
                    return uploadCollectionCommit(commit_data);
                default:
                    throw new BaseException("wrong task type", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (JSONException e) {
            throw new BaseException("JSONObject not found", HttpStatus.NOT_FOUND, e);
        }

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
    public ResponseEntity<String> uploadCollectionData(@PathVariable(value = "commitid") int commitid, @RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("commit picture with id : " + String.valueOf(commitid));
        // get commit
        // TODO: double check has already committed
        Commit commit = commitRepository.findByCommitid(commitid);
        if (commit == null) {
            throw new BaseException("can not find commit", HttpStatus.NOT_FOUND);
        }
        int taskid = commit.getTask().getTaskid();
        String id = String.valueOf(taskid);
        List<String> url_list = fileService.uploadFiles(multipartFiles, "/tasks/" + id + "/pictures");

        for (String item : url_list) {
            commitDataService.save(commit, fileService.getRelativePathByUrl(item));
        }
        JSONObject response = new JSONObject();
        response.put("data", url_list);
        return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);
    }

    /**
     * 处理采集任务的提交
     *
     * @param commit_data
     * @return
     */
    private ResponseEntity<String> uploadCollectionCommit(Map<String, Object> commit_data) throws BaseException {
        try {

            Integer task_id = (Integer) commit_data.get("task_id");
            Integer committer_id = (Integer) commit_data.get("user_id");
            Integer size = (Integer) commit_data.get("size");

            if (task_id == null || committer_id == null || size == null) {
                throw new BaseException("json key error: task_id or committer_id or size not found", HttpStatus.NOT_FOUND);
            }


            Commit commit = commitService.save(task_id, committer_id, size);

            // construct response data meta error format
            JSONObject response = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("commit_id", commit.getCommitid());
            response.put("data", data);

            return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("exception", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

    }

    /**
     * 处理标注任务的提交
     *
     * @param commit_data
     * @return
     */
    private ResponseEntity<String> uploadAnnotationCommit(Map<String, Object> commit_data) throws BaseException {
        try {
            Integer task_id = (Integer) commit_data.get("task_id");
            Integer committer_id = (Integer) commit_data.get("user_id");
            Integer size = (Integer) commit_data.get("size");
            List<Map<String, String>> results = (List<Map<String, String>>) commit_data.get("result");

            if (task_id == null || committer_id == null || size == null || results == null) {
                throw new BaseException("json key error: task_id or user_id or size or result not found", HttpStatus.NOT_FOUND);
            }

            Commit commit = commitService.save(task_id, committer_id, size);

            for (Map<String, String> result : results) {
                String picture_url = result.get("picture_url");
                String xml = result.get("xml");
                if (picture_url == null || xml == null) {
                    throw new BaseException("json key error: picture_url or xml not found", HttpStatus.NOT_FOUND);
                }
                String relativePath = fileService.getRelativePathByUrl(picture_url);
                logger.info("picture path : " + relativePath);
                String pictureName = fileService.getFilenameByRelativePath(relativePath);

                // construct xml path
                relativePath = fileService.getParentPath(relativePath);
                logger.info("picture parent path: " + relativePath);
                relativePath = relativePath.replace("pictures", "xmls");// change directory
                relativePath = relativePath + "/" + pictureName;

                logger.info(" xml  path :" + relativePath);
                String xmlName = fileService.getNewFilename("-" + String.valueOf(committer_id) + ".xml");
                logger.info("xmlName: " + xmlName);
                fileService.storeString(xml, xmlName, relativePath);
                // save commit data
                commitDataService.save(commit, relativePath + "/" + xmlName);

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("json object field not found", HttpStatus.NOT_FOUND, e);

        }

        return null;
    }

    /**
     * 给定userid,task_id,limit 返回
     * api: get user commits
     *
     * @param userid
     * @param task_id
     * @param limit
     * @return
     */
    @GetMapping()
    public ResponseEntity<String> getUserCommits(@RequestParam(name = "user", required = false, defaultValue = "") String userid,
                                                 @RequestParam(name = "task", required = false, defaultValue = "") String task_id,
                                                 @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
                                                 @RequestParam(name = "offset", required = false, defaultValue = "1") int offset
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
                commits = commitService.findByCommitterAndAndTask(user, task, limit, offset);
            } else if (user != null) {
                //commits = commitRepository.findByCommitter(user);
                logger.info("findByCommitter");
                commits = commitService.findByCommitter(user, limit, offset);
            } else {
                //commits = commitRepository.findByTask(task);
                logger.info("findByTask");
                commits = commitService.findByTask(task, limit, offset);

            }

            List<JSONObject> responseCommits = new LinkedList<>();

            // put commit info into response
            for (Commit commit : commits) {
                JSONObject comitInfo = new JSONObject();
                comitInfo.put("id", commit.getCommitid());
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

                return new ResponseEntity<>(response.toString(), HttpStatus.OK);

            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            throw new BaseException("Number format exception", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

    }


}
