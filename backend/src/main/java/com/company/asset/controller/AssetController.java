package com.company.asset.controller;

import com.company.asset.common.*;
import com.company.asset.entity.*;
import com.company.asset.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 资产管理：资产台账、申请、采购、库存、领用、归还、核销。
 */
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    private final CrudService crud;
    private final AssetFlowService flow;

    /**
     * 资产台账列表，可展示全部资产和状态。
     */
    @GetMapping
    public Result<List<Asset>> list() {
        return Result.ok(crud.list(Asset.class));
    }

    /**
     * 按 id 获取资产详情。
     */
    @GetMapping("/{id}")
    public Result<Asset> detail(@PathVariable Long id) {
        return Result.ok(crud.get(Asset.class, id));
    }

    /**
     * 创建或更新资产；创建库存资产时 status 使用 IN_STOCK。
     */
    @PostMapping
    public Result<Asset> save(@RequestBody Asset x) {
        return Result.ok(crud.save(x));
    }

    /**
     * 删除资产台账。
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        crud.delete(Asset.class, id);
        return Result.ok(null);
    }

    /**
     * 提交资产流程单。type: ASSET_APPLY/ASSET_PURCHASE/ASSET_RECEIVE/ASSET_RETURN/ASSET_WRITE_OFF。
     */
    @PostMapping("/operations")
    public Result<AssetOperation> submitOperation(@RequestBody AssetOperation op) {
        return Result.ok("已提交审批", flow.submit(op));
    }

    /**
     * 全部资产流程记录，前端可按 type 分别渲染各子页面。
     */
    @GetMapping("/operations")
    public Result<List<AssetOperation>> operations() {
        return Result.ok(crud.list(AssetOperation.class));
    }

    /**
     * 获取单个资产流程详情。
     */
    @GetMapping("/operations/{id}")
    public Result<AssetOperation> operation(@PathVariable Long id) {
        return Result.ok(crud.get(AssetOperation.class, id));
    }

    /**
     * 采购页快捷入口，自动填充采购类型。
     */
    @PostMapping("/purchase")
    public Result<AssetOperation> purchase(@RequestBody AssetOperation op) {
        op.setType("ASSET_PURCHASE");
        return Result.ok("采购申请已提交", flow.submit(op));
    }

    /**
     * 领用页快捷入口。
     */
    @PostMapping("/receive")
    public Result<AssetOperation> receive(@RequestBody AssetOperation op) {
        op.setType("ASSET_RECEIVE");
        return Result.ok("领用申请已提交", flow.submit(op));
    }

    /**
     * 归还页快捷入口。
     */
    @PostMapping("/return")
    public Result<AssetOperation> returns(@RequestBody AssetOperation op) {
        op.setType("ASSET_RETURN");
        return Result.ok("归还申请已提交", flow.submit(op));
    }

    /**
     * 核销页快捷入口。
     */
    @PostMapping("/write-off")
    public Result<AssetOperation> writeOff(@RequestBody AssetOperation op) {
        op.setType("ASSET_WRITE_OFF");
        return Result.ok("核销申请已提交", flow.submit(op));
    }
}
