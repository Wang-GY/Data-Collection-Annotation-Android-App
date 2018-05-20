package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.CommitData;
import com.weiju.springboot.repository.CommitDataRepository;
import com.weiju.springboot.repository.CommitRepository;
import com.weiju.springboot.service.CommitDataService;
import com.weiju.springboot.service.CommitService;
import com.weiju.springboot.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommitDataServiceImpl implements CommitDataService {

    @Autowired
    private final CommitDataRepository commitDataRepository;
    private final CommitRepository commitRepository;
    private final FileService fileService;

    public CommitDataServiceImpl(CommitDataRepository commitDataRepository, CommitRepository commitRepository,
                                 FileService fileService
    ) {
        this.commitDataRepository = commitDataRepository;
        this.commitRepository = commitRepository;
        this.fileService = fileService;
    }

    /**
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

    /**
     * get api/commit?task=&user=?
     * 用于获取用户提交情况时统计
     *
     * @param commitDataList
     */
    public List<Map<String, String>> getCommitDataResult(List<CommitData> commitDataList) throws IOException {
        if (commitDataList == null)
            return null;
        Map<String, String> result = new LinkedHashMap<>();
        for (CommitData commitData : commitDataList) {
            String itemPath = commitData.getItemPath(); // relative Path
            String fileName = fileService.getFilenameByRelativePath(itemPath);
            if (fileService.getFileTypeByFileName(fileName).equals("xml")) {
                result.put("xml", fileService.relativePathToUrl(itemPath));
                result.put("picture", "");
            } else {
                result.put("picture", fileService.relativePathToUrl(itemPath));
            }

        }

        return null;
    }

}
