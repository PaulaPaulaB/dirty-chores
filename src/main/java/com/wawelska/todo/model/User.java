package com.wawelska.todo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Setter
    @Getter
    private String nickName;

    @Getter
    @Setter
    @Column(name = "password_hash")
    private String passwordHash;

    @Email
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private int level;


    @ManyToOne
    @JoinColumn(name = "team_id")
    @Getter
    @Setter
    private Team team;


    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Task> assigneeTasks;


    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Task> creatorTasks;
}
