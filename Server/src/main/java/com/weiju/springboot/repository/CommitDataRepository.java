package com.weiju.springboot.repository;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.CommitData;
import org.springframework.data.repository.CrudRepository;

public interface CommitDataRepository extends CrudRepository<CommitData, Integer> {
}
