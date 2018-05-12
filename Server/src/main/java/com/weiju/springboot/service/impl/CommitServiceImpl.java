package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.CommitRepository;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;


/**
 * Commit 的 Service 类
 */
@Service
public class CommitServiceImpl implements CommitService {

    @Autowired
    private final UserRepository userRepository;
    private final CommitRepository commitRepository;
    private final TaskRepository taskRepository;

    public CommitServiceImpl(UserRepository userRepository, CommitRepository commitRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.commitRepository = commitRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * 存储一个commit 字段
     * @param task_id
     * @param committer_id
     * @param size
     * @return 存储之后在获得的Commit 对象
     */
    @Override
    public Commit save(int task_id, int committer_id, int size){
        User committer = userRepository.findByUserid(committer_id);
        Task task = taskRepository.findByTaskid(task_id);

        Commit commit = new Commit();
        commit.setCommitTime(Instant.now().toString());
        commit.setSize(size);
        commit.setCommitter(committer);
        commit.setTask(task);
        return commitRepository.save(commit);
    }
}
