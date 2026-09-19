package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_notification")
@Getter
@Setter
public class Notification extends BaseEntity {
    private Long userId;
    private String title;
    @Column(length = 1000)
    private String content;
    private String type = "SYSTEM";
    private Integer readFlag = 0;
}
