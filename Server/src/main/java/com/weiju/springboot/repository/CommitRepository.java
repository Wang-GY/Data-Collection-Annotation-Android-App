package com.weiju.springboot.repository;

import com.weiju.springboot.model.Commit;
import org.springframework.data.repository.CrudRepository;

public interface CommitRepository extends CrudRepository<Commit, Integer> {
    Commit findByCommiterId(int commitid);

}
