package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_role")
@Getter
@Setter
public class Role extends BaseEntity {
    @Column(unique = true)
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status = 1;
}
