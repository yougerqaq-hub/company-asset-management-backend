package com.company.asset.service;

import com.company.asset.common.BusinessException;
import com.company.asset.entity.*;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * 负责资产全流程及其审批联动，防止 Controller 中出现业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class AssetFlowService {
    private final CrudService crud;
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public AssetOperation submit(AssetOperation operation) {
        if (operation.getType() == null) throw new BusinessException("操作类型不能为空");
        operation.setOperationNo("OP" + System.currentTimeMillis());
        operation.setStatus("PENDING");
        crud.save(operation);
        Approval approval = new Approval();
        approval.setApprovalNo("AP" + System.currentTimeMillis());
        approval.setBusinessType(operation.getType());
        approval.setBusinessId(operation.getId());
        approval.setApplicantId(operation.getApplicantId());
        approval.setTitle(operation.getType() + "：" + operation.getAssetName());
        crud.save(approval);
        notifyUser(operation.getApplicantId(), "审批已发起", "您的" + operation.getType() + "申请已提交，等待审批。");
        return operation;
    }

    @Transactional
    public Approval decide(Long approvalId, Long approverId, boolean pass, String comment) {
        Approval approval = crud.get(Approval.class, approvalId);
        if (!"PENDING".equals(approval.getStatus())) throw new BusinessException("该单据已审批");
        approval.setApproverId(approverId);
        approval.setComment(comment);
        approval.setStatus(pass ? "APPROVED" : "REJECTED");
        crud.save(approval);
        AssetOperation op = crud.get(AssetOperation.class, approval.getBusinessId());
        op.setStatus(approval.getStatus());
        if (pass && op.getAssetId() != null) updateAsset(op);
        crud.save(op);
        notifyUser(op.getApplicantId(), "审批结果", approval.getTitle() + "：" + (pass ? "已通过" : "已驳回"));
        return approval;
    }

    private void updateAsset(AssetOperation op) {
        Asset asset = crud.get(Asset.class, op.getAssetId());
        String t = op.getType();
        if ("ASSET_RECEIVE".equals(t)) {
            asset.setStatus("IN_USE");
            asset.setKeeperId(op.getApplicantId());
            asset.setDepartmentId(op.getDepartmentId());
        }
        if ("ASSET_RETURN".equals(t)) {
            asset.setStatus("IN_STOCK");
            asset.setKeeperId(null);
        }
        if ("ASSET_WRITE_OFF".equals(t)) asset.setStatus("WRITTEN_OFF");
        if ("ASSET_PURCHASE".equals(t)) asset.setStatus("IN_STOCK");
        crud.save(asset);
    }

    @Transactional
    public void notifyUser(Long userId, String title, String content) {
        if (userId == null) return;
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        crud.save(n);
    }

    public Map<String, Object> dashboard() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("assetTotal", crud.count(Asset.class));
        r.put("inStock", countAsset("IN_STOCK"));
        r.put("inUse", countAsset("IN_USE"));
        r.put("pendingApproval", countApproval("PENDING"));
        r.put("categoryDistribution", em.createQuery("select a.category,count(a) from Asset a group by a.category").getResultList());
        return r;
    }

    private Long countAsset(String status) {
        return em.createQuery("select count(a) from Asset a where a.status=:s", Long.class).setParameter("s", status).getSingleResult();
    }

    private Long countApproval(String status) {
        return em.createQuery("select count(a) from Approval a where a.status=:s", Long.class).setParameter("s", status).getSingleResult();
    }
}
