package com.company.asset.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产台账：库存、领用、维修、报废等状态均在本表维护。
 */
@Entity
@Table(name = "asset")
@Getter
@Setter
public class Asset extends BaseEntity {
    @Column(unique = true)
    private String assetCode;
    private String name;
    private String category;
    private String brand;
    private String model;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private String supplier;
    private String location;
    private String status = "IN_STOCK";
    private Long keeperId;
    private Long departmentId;
    private String remark;
}
