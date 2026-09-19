package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_permission")
@Getter
@Setter
public class Permission extends BaseEntity {
    private String permissionCode;
    private String permissionName;
    private String resourcePath;
    private String method;
    private Long parentId = 0L;
}
