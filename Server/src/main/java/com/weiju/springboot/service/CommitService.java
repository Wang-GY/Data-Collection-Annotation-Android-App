package com.weiju.springboot.service;

import com.weiju.springboot.model.Commit;

public interface CommitService {
    Commit save(int task_id, int committer_id, int size);
}
