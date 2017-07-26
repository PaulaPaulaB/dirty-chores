package com.wawelska.todo.repository;

import com.wawelska.todo.model.Team;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long> {

    Team findById(Long id);
}
