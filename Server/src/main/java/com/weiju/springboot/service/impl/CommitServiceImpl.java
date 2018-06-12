package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.CommitPaginationAndSortingRepository;
import com.weiju.springboot.repository.CommitRepository;
import com.weiju.springboot.repository.TaskRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.CommitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Commit 的 Service 类
 */
@Service
public class CommitServiceImpl implements CommitService {

    private static Logger logger = LoggerFactory.getLogger(CommitServiceImpl.class);

    @Autowired
    private final UserRepository userRepository;
    private final CommitRepository commitRepository;
    private final TaskRepository taskRepository;
    private final CommitPaginationAndSortingRepository commitPaginationAndSortingRepository;
    private JdbcTemplate jdbcTemplate;


    public CommitServiceImpl(UserRepository userRepository, CommitRepository commitRepository, TaskRepository taskRepository,
                             CommitPaginationAndSortingRepository commitPaginationAndSortingRepository,
                             JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.commitRepository = commitRepository;
        this.taskRepository = taskRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.commitPaginationAndSortingRepository = commitPaginationAndSortingRepository;
    }

    /**
     * 存储一个commit 字段
     *
     * @param task_id
     * @param committer_id
     * @param size
     * @return 存储之后在获得的Commit 对象
     */
    @Override
    public Commit save(int task_id, int committer_id, int size) {
        User committer = userRepository.findByUserid(committer_id);
        Task task = taskRepository.findByTaskid(task_id);

        Commit commit = new Commit();
        commit.setCommitTime(Instant.now().toString());
        commit.setSize(size);
        commit.setCommitter(committer);
        commit.setTask(task);
        return commitRepository.save(commit);
    }

    /**
     * 用于构造limit语句
     *
     * @param limit
     * @return
     */
    private String getLimitSql(Integer limit) {
        if (limit != null && limit >= 0) {
            return " LIMIT " + String.valueOf(limit);
        } else return "";
    }

    /**
     * 用于构造offset语句
     *
     * @param offset
     * @return
     */
    private String getOffsetSql(Integer offset) {
        if (offset != null && offset >= 0) {
            return " OFFSET " + String.valueOf(offset);
        } else return null;
    }


    //https://www.mkyong.com/spring/spring-jdbctemplate-querying-examples/

    /**
     * find commits with limit and offset
     *
     * @param committer
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<Commit> findByCommitter(User committer, Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        return commitPaginationAndSortingRepository.findByCommitter(committer, pageable);
    }

    /**
     * find commits with limit and offset
     *
     * @param committer
     * @param task
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<Commit> findByCommitterAndAndTask(User committer, Task task, Integer pageNum, Integer pageSize) {

        Pageable pageable = new PageRequest(pageNum, pageSize);
        return commitPaginationAndSortingRepository.findByCommitterAndAndTask(committer, task, pageable);

    }

    /**
     * find commits with limit and offset
     *
     * @param task
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<Commit> findByTask(Task task, Integer pageNum, Integer pageSize) {
        Pageable pageRequest = new PageRequest(pageNum, pageSize);
        return commitPaginationAndSortingRepository.findByTask(task, pageRequest);
    }

    /**
     * return true if commit finish
     *
     * @param commit
     * @return
     */
    @Override
    public boolean check_commit_finish(Commit commit) {
        return commit.getCommitDataList().size() == commit.getSize();
    }

}
