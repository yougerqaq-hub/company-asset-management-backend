package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 通用审批单，可关联资产申请、采购、领用、归还和核销。
 */
@Entity
@Table(name = "approval")
@Getter
@Setter
public class Approval extends BaseEntity {
    private String approvalNo;
    private String businessType;
    private Long businessId;
    private String title;
    private Long applicantId;
    private Long approverId;
    private String status = "PENDING";
    private Integer currentStep = 1;
    @Column(length = 1000)
    private String comment;
}
