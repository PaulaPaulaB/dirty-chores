package com.wawelska.todo.repository;

import com.wawelska.todo.model.Task;
import com.wawelska.todo.model.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;


public interface TaskRepository extends CrudRepository<Task,Long> {

    @Query("SELECT t FROM Task t INNER JOIN t.creator u WHERE u.team = ?1 ORDER BY t.priority,t.creationDate")
    List<Task> getAllTaskForTeamByPriority(Team team);

    @Query ("SELECT t FROM Task t INNER JOIN t.creator u WHERE u.team = ?1 ORDER BY t.points DESC,t.creationDate DESC")
    List<Task> getAllTaskForTeamByPoints(Team team);

    @Query ("SELECT t FROM Task t INNER JOIN t.creator u WHERE u.team = ?1 ORDER BY t.deadlineDate,t.creationDate DESC")
    List<Task> getAllTaskForTeamByDeadline(Team team);

    @Query ("SELECT t FROM Task t INNER JOIN t.creator u WHERE u.team = ?1 ORDER BY t.status,t.creationDate DESC")
    List<Task> getAllTaskForTeamByStatus(Team team);

    Task findById(Long id);

    @Transactional
    void deleteById(Long id);
}
