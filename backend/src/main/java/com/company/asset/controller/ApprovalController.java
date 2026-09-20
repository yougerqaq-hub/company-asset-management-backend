package com.company.asset.controller;

import com.company.asset.common.Result;
import com.company.asset.entity.*;
import com.company.asset.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 审批管理：待办、已办、审批详情、同意与驳回。
 */
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {
    private final CrudService crud;
    private final AssetFlowService flow;

    /**
     * 审批单列表，前端可按 status、businessType 区分待办/已办/各类审批页面。
     */
    @GetMapping
    public Result<List<Approval>> list() {
        return Result.ok(crud.list(Approval.class));
    }

    /**
     * 审批详情。
     */
    @GetMapping("/{id}")
    public Result<Approval> detail(@PathVariable Long id) {
        return Result.ok(crud.get(Approval.class, id));
    }

    /**
     * 审批通过。
     */
    @PostMapping("/{id}/approve")
    public Result<Approval> approve(@PathVariable Long id, @RequestBody Decision d) {
        return Result.ok(flow.decide(id, d.approverId, true, d.comment));
    }

    /**
     * 审批驳回。
     */
    @PostMapping("/{id}/reject")
    public Result<Approval> reject(@PathVariable Long id, @RequestBody Decision d) {
        return Result.ok(flow.decide(id, d.approverId, false, d.comment));
    }

    @Data
    public static class Decision {
        private Long approverId;
        private String comment;
    }
}
