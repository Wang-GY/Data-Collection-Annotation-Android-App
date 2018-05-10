package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.CommitRepository;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.FileService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
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
    @Autowired
    public CommitController(CommitRepository commitRepository,FileService fileService,UserRepository userRepository, TaskRepository taskRepository) {
        this.commitRepository = commitRepository;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.taskRepository =taskRepository;
    }

    /**
     * 用户上传一个提交
     * 分别处理采集任务和标注任务
     *
     * @param payload request body
     * @return
     */
    @PostMapping()
    public ResponseEntity<String> uploadCommit(@RequestBody Map<String,Map<String,Object>> payload) throws BaseException {
        logger.info("request body "+ payload.toString());
        Map<String, Object> commit_data =  payload.get("data");
        logger.info("commit data: "+ commit_data);

        int task_type = (int) commit_data.get("task_type");
        logger.info("commit type:"+ task_type);
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
    }

    /**
     * 提交采集任务的图片
     * @param commitid
     * @param multipartFiles 图片
     * @return 新文件路径（用作测试）
     * @throws BaseException
     */
    @PostMapping("/pictures/{commitid}")
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
        Map<String,String> url_list = fileService.uploadFiles(multipartFiles,"tasks/" + id + "/pictures");
        JSONObject response = new JSONObject();
        response.put("data",url_list);
        return new ResponseEntity<>(response.toString(),HttpStatus.CREATED);
    }

    /**
     * 处理采集任务的提交
     *
     * @param commit_data
     * @return
     */
    private ResponseEntity<String> uploadCollectionCommit(Map<String, Object> commit_data) throws BaseException {
        try {
            Commit commit = new Commit();
            commit.setSize((Integer) commit_data.get("size"));
            int taskid = (int) commit_data.get("task_id");
            // get task
            Task task = taskRepository.findByTaskid(taskid);
            if (task == null){
                throw  new BaseException("Task not found",HttpStatus.NOT_FOUND);
            }
            commit.setTask(task);

            // get creator
            User creator = userRepository.findByUserid((Integer) commit_data.get("committer_id") );
            if(creator == null){
                throw  new BaseException("creator not found",HttpStatus.NOT_FOUND);
            }
            commit.setCommitter(creator);
            commit.setCommitTime(Instant.now().toString());
            Commit commit1 = commitRepository.save(commit);

            // construct response data meta error format
            JSONObject response = new JSONObject();
            response.put("commitid",commit1.getCommitid());
            response.put("data", response);
            return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("exception",HttpStatus.INTERNAL_SERVER_ERROR,e);
        }

    }

    /**
     * 处理标注任务的提交
     *
     * @param commit_data
     * @return
     */
    private ResponseEntity<String> uploadAnnotationCommit(Map<String, Object> commit_data) {



        

        return null;
    }


}
