package com.company.asset.controller;

import com.company.asset.common.Result;
import com.company.asset.entity.*;
import com.company.asset.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统设置：部门、角色、权限、用户、参数配置的管理接口。
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {
    private final CrudService crud;

    /**
     * 部门列表（可供前端构建部门树）。
     */
    @GetMapping("/departments")
    public Result<List<Department>> departments() {
        return Result.ok(crud.list(Department.class));
    }

    /**
     * 新增或编辑部门，带 id 为编辑。
     */
    @PostMapping("/departments")
    public Result<Department> saveDepartment(@RequestBody Department x) {
        return Result.ok(crud.save(x));
    }

    /**
     * 删除部门。
     */
    @DeleteMapping("/departments/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        crud.delete(Department.class, id);
        return Result.ok(null);
    }

    /**
     * 角色列表。
     */
    @GetMapping("/roles")
    public Result<List<Role>> roles() {
        return Result.ok(crud.list(Role.class));
    }

    /**
     * 新增或编辑角色。
     */
    @PostMapping("/roles")
    public Result<Role> saveRole(@RequestBody Role x) {
        return Result.ok(crud.save(x));
    }

    /**
     * 删除角色。
     */
    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        crud.delete(Role.class, id);
        return Result.ok(null);
    }

    /**
     * 权限/菜单列表，resourcePath 可供前端路由或后端鉴权扩展。
     */
    @GetMapping("/permissions")
    public Result<List<Permission>> permissions() {
        return Result.ok(crud.list(Permission.class));
    }

    /**
     * 新增或编辑权限。
     */
    @PostMapping("/permissions")
    public Result<Permission> savePermission(@RequestBody Permission x) {
        return Result.ok(crud.save(x));
    }

    /**
     * 查询角色拥有的权限关联。
     */
    @GetMapping("/role-permissions")
    public Result<List<RolePermission>> rolePermissions() {
        return Result.ok(crud.list(RolePermission.class));
    }

    /**
     * 给角色新增一项权限；前端批量授权时循环调用即可。
     */
    @PostMapping("/role-permissions")
    public Result<RolePermission> saveRolePermission(@RequestBody RolePermission x) {
        return Result.ok(crud.save(x));
    }

    /**
     * 取消角色的一项权限。
     */
    @DeleteMapping("/role-permissions/{id}")
    public Result<Void> deleteRolePermission(@PathVariable Long id) {
        crud.delete(RolePermission.class, id);
        return Result.ok(null);
    }

    /**
     * 系统用户列表。
     */
    @GetMapping("/users")
    public Result<List<User>> users() {
        return Result.ok(crud.list(User.class));
    }

    /**
     * 管理员编辑用户资料、部门、角色、启停状态；密码应通过注册/密码重置接口处理。
     */
    @PostMapping("/users")
    public Result<User> saveUser(@RequestBody User x) {
        return Result.ok(crud.save(x));
    }

    /**
     * 系统参数列表。
     */
    @GetMapping("/configs")
    public Result<List<SystemConfig>> configs() {
        return Result.ok(crud.list(SystemConfig.class));
    }

    /**
     * 保存系统参数，如公司名称、低库存阈值。
     */
    @PostMapping("/configs")
    public Result<SystemConfig> saveConfig(@RequestBody SystemConfig x) {
        return Result.ok(crud.save(x));
    }
}
