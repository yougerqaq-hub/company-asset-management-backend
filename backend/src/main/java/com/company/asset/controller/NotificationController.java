package com.company.asset.controller;

import com.company.asset.common.Result;
import com.company.asset.entity.Notification;
import com.company.asset.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 提醒中心：审批结果、待办和系统公告读取状态管理。
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final CrudService crud;

    /**
     * 消息列表。
     */
    @GetMapping
    public Result<List<Notification>> list() {
        return Result.ok(crud.list(Notification.class));
    }

    /**
     * 标记单条消息为已读。
     */
    @PutMapping("/{id}/read")
    public Result<Notification> read(@PathVariable Long id) {
        Notification n = crud.get(Notification.class, id);
        n.setReadFlag(1);
        return Result.ok(crud.save(n));
    }

    /**
     * 新建系统提醒或公告。
     */
    @PostMapping
    public Result<Notification> save(@RequestBody Notification n) {
        return Result.ok(crud.save(n));
    }
}
