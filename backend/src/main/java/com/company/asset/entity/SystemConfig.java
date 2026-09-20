package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_config")
@Getter
@Setter
public class SystemConfig extends BaseEntity {
    @Column(unique = true)
    private String configKey;
    private String configValue;
    private String description;
}
