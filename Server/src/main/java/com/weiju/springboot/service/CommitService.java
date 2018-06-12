package com.weiju.springboot.service;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommitService {
    Commit save(int task_id, int committer_id, int size);

    Page<Commit> findByCommitter(User committer, Integer pageNum, Integer pageSize);

    Page<Commit> findByCommitterAndAndTask(User committer, Task task, Integer pageNum, Integer pageSize);

    Page<Commit> findByTask(Task task, Integer pageNum, Integer pageSize);

    /**
     * return true if a commit is finished
     * @param commit
     * @return
     */
    boolean check_commit_finish(Commit commit);
}
