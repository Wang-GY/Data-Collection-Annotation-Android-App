package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.CommitData;
import com.weiju.springboot.repository.CommitDataRepository;
import com.weiju.springboot.repository.CommitRepository;
import com.weiju.springboot.service.CommitDataService;
import com.weiju.springboot.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommitDataServiceImpl implements CommitDataService {

    @Autowired
    private final CommitDataRepository commitDataRepository;
    private final CommitRepository commitRepository;

    public CommitDataServiceImpl(CommitDataRepository commitDataRepository, CommitRepository commitRepository) {
        this.commitDataRepository = commitDataRepository;
        this.commitRepository = commitRepository;
    }

    /**
     *
     * @param commit_id
     * @param itemPath
     * @return
     */
    @Override
    public CommitData save(int commit_id, String itemPath) {
        Commit commit = commitRepository.findByCommitid(commit_id);
        return save(commit, itemPath);
    }

    /**
     *
     * @param commit
     * @param itemPath
     * @return
     */
    @Override
    public CommitData save(Commit commit, String itemPath) {
        CommitData commitData = new CommitData();
        commitData.setCommitId(commit);
        commitData.setItemPath(itemPath);
        return commitDataRepository.save(commitData);
    }

}
