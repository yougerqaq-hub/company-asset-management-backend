package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 资产申请、采购、领用、归还、核销等全流程操作记录。
 */
@Entity
@Table(name = "asset_operation")
@Getter
@Setter
public class AssetOperation extends BaseEntity {
    private String operationNo;
    private String type;
    private Long assetId;
    private String assetName;
    private Integer quantity = 1;
    private Long applicantId;
    private Long departmentId;
    private Long handlerId;
    private String status = "PENDING";
    private LocalDate expectedDate;
    @Column(length = 1000)
    private String reason;
    @Column(length = 1000)
    private String remark;
}
