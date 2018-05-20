package com.weiju.springboot.repository;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommitRepository extends CrudRepository<Commit, Integer> {
    Commit findByCommitid(int commitid);

    List<Commit> findByCommitter(User committer);

    List<Commit> findByCommitterAndAndTask(User committer, Task task);

    List<Commit> findByTask(Task task);




}
