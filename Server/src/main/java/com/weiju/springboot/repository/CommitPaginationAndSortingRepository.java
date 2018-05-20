package com.weiju.springboot.repository;

import com.weiju.springboot.model.Commit;
import com.weiju.springboot.model.Task;
import com.weiju.springboot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 可以实现分页机制
 */
public interface CommitPaginationAndSortingRepository extends PagingAndSortingRepository<Commit, Integer> {


    Page<Commit> findByCommitterAndAndTask(User user, Task task, Pageable pageable);

    Page<Commit> findByTask(Task task, Pageable pageable);

    Page<Commit> findByCommitter(User committer, Pageable pageable);


}
