package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 所有业务表的公共主键与审计字段。
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected LocalDateTime createTime = LocalDateTime.now();
    protected LocalDateTime updateTime = LocalDateTime.now();
}
