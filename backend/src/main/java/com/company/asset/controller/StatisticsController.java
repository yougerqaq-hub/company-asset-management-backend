package com.company.asset.controller;

import com.company.asset.common.Result;
import com.company.asset.entity.*;
import com.company.asset.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 统计分析：资产总览、分类分布、状态分布、待审批数；可直接用于首页图表。
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final AssetFlowService flow;
    private final CrudService crud;

    /**
     * 首页总览指标和资产分类统计。
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.ok(flow.dashboard());
    }

    /**
     * 原始资产数据，可由前端自由进行明细导出或筛选分析。
     */
    @GetMapping("/asset-details")
    public Result<List<Asset>> assetDetails() {
        return Result.ok(crud.list(Asset.class));
    }

    /**
     * 原始审批数据，可用于审批效率、申请类别报表。
     */
    @GetMapping("/approval-details")
    public Result<List<Approval>> approvals() {
        return Result.ok(crud.list(Approval.class));
    }
}
