package com.weiju.springboot.service;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommitService {
    Commit save(int task_id, int committer_id, int size);

    Page<Commit> findByCommitter(User committer, Integer limit, Integer offset);

    Page<Commit> findByCommitterAndAndTask(User committer, Task task, Integer limit, Integer offset);

    Page<Commit> findByTask(Task task, Integer limit, Integer offset);
}
