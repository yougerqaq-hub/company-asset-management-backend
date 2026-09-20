package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 角色与权限的关联，用于给角色批量授权。
 */
@Entity
@Table(name = "sys_role_permission", uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"}))
@Getter
@Setter
public class RolePermission extends BaseEntity {
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "permission_id")
    private Long permissionId;
}
