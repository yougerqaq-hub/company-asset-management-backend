package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_user")
@Getter
@Setter
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String realName;
    private String phone;
    private Long departmentId;
    private Long roleId;
    private Integer status = 1;
}
