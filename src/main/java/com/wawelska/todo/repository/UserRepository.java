package com.wawelska.todo.repository;

import com.wawelska.todo.model.Team;
import com.wawelska.todo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {


    User findById(Long id);

    User findByNickName(String nickName);

    List<User> findByTeam(Team team);

    @Query("SELECT u FROM User u WHERE u.team=?1 ORDER BY u.level DESC")
    List<User> getAllUsersInLevelOrderFromTeam(Team team);

}
