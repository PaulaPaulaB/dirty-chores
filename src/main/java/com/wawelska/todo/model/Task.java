package com.wawelska.todo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String specification;

    @Getter
    @Setter
    private Date creationDate;

    @Getter
    @Setter
    private Date deadlineDate;

    @Getter
    @Setter
    private Priority priority;

    @Getter
    @Setter
    @Transient
    private int daysToDeadline;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @Getter
    @Setter
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @Getter
    @Setter
    private User creator;

    @Getter
    @Setter
    private TaskStatus status = TaskStatus.New;

    @Getter
    @Setter
    private int points;

}
