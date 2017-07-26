package com.wawelska.todo.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;


@Data
public class UserFormDTO {

    @NotEmpty
    @Column(unique=true)
    private String nickName;

    @NotEmpty
    @Email
    private String email;

}
