package com.weiju.springboot.repository;

import com.weiju.springboot.model.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Integer> {

    Task findByTaskid(int taskid);

    Task findByName(String name);

    boolean existsById(Integer integer);

    boolean existsByName(String name);
}
