package com.wawelska.todo.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Data
public class TaskFormDTO {

    private Long id;

    @NotBlank(message = "opisz zadanie do wykonania")
    private String specification;

    private Priority priority;

    @Pattern(regexp = "[0-9]{4}-[0-1]{1}[0-9]{1}-[0-3]{1}[0-9]{1}", message = "Oczekiwany format daty: YYYY-MM-DD")
    private String deadlineDate;

    private String creationDate;

    private Long assigneeId;

    private Long creatorId;

    private TaskStatus status;

    private int points;

}
