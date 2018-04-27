package com.weiju.springboot.repository;

import com.weiju.springboot.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    Task findByTaskid(int taskid);

    Task findByName(String name);

    Page<Task> findAll(Pageable pageable);

    boolean existsById(Integer integer);

    boolean existsByName(String name);
}
