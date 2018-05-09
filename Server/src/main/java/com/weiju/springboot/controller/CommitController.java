package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.DataMetaErr;
import com.weiju.springboot.repository.CommitRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 处理commit相关api
 */
@Controller
@RequestMapping("api/commits")
public class CommitController {
    final CommitRepository commitRepository;

    @Autowired
    public CommitController(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    /**
     * 用户上传一个提交
     * 分别处理采集任务和标注任务
     *
     * @param payload request body
     * @return
     */
    @PostMapping()
    public ResponseEntity<String> uploadCommit(@RequestBody DataMetaErr payload) throws BaseException {
        Map<String, Object> commit_data = (Map<String, Object>) payload.getData();
        Integer task_type = (Integer) commit_data.get("task_type");
        switch (task_type) {
            case 0: // Annotation
                return uploadAnnotationCommit(commit_data);
            case 1: // Collection
                return uploadCollectionCommit(commit_data);
            default:
                throw new BaseException("wrong task type", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pictures/{commitid}")
    public ResponseEntity<String> uploadCollectionData(@RequestParam(value = "commitid") int commitid, @RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        // get commit
        // TODO: double check has already committed
        Commit commit = commitRepository.findByCommitid(commitid);
        if (commit == null) {
            throw new BaseException("can not find commit", HttpStatus.NOT_FOUND);
        }
        int taskid = commit.getTaskId();

        return null;
    }

    /**
     * 处理采集任务的提交
     *
     * @param commit_data
     * @return
     */
    private ResponseEntity<String> uploadCollectionCommit(Map<String, Object> commit_data) {
        try {
            Commit commit = new Commit();
            commit.setSize((Integer) commit_data.get("size"));
            commit.setTaskId((Integer) commit_data.get("task_id"));
            commit.setCommitterId((Integer) commit_data.get("committer_id"));
            Commit commit1 = commitRepository.save(commit);

            // construct response data meta error format
            JSONObject response = new JSONObject();
            response.put("data", commit1.getCommitid());
            return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
