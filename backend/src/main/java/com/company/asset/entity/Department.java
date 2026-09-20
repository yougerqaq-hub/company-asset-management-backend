package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_department")
@Getter
@Setter
public class Department extends BaseEntity {
    private String name;
    private Long parentId = 0L;
    private String leader;
    private String phone;
    private Integer sortNo = 0;
    private Integer status = 1;
}
