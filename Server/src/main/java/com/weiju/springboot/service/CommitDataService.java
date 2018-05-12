package com.weiju.springboot.service;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.CommitData;
import org.springframework.stereotype.Service;


public interface CommitDataService {
    CommitData save(int commit_id, String itemPath);

    CommitData save(Commit commit, String itemPath);
}
