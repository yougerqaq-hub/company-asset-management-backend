# 公司资产管理系统后端

基于 Spring Boot 3、JPA 和 MySQL 的资产管理 REST API。系统覆盖角色、部门、用户、权限、资产全流程、审批、提醒、统计和系统参数。

## 启动

1. 在 MySQL 执行 [sql/asset_management.sql](sql/asset_management.sql)。
2. 按实际情况修改 `src/main/resources/application.yml` 的 MySQL 用户名和密码。
3. 执行 `mvn spring-boot:run`，服务地址为 `http://localhost:8080`。

测试账号为 `admin` / `123456`。全部响应遵循：`{"code":200,"message":"操作成功","data":...}`。

## 主要接口

| 模块 | 方法与路径 | 功能 |
|---|---|---|
| 登录注册 | `POST /api/auth/register`、`POST /api/auth/login` | 注册、登录并返回临时 token |
| 资产台账 | `GET/POST /api/assets`、`GET/DELETE /api/assets/{id}` | 资产列表、创建修改、详情、删除 |
| 资产子页面 | `GET/POST /api/assets/operations` | 全部流程记录、通用资产申请 |
| 采购/领用/归还/核销 | `POST /api/assets/purchase`、`/receive`、`/return`、`/write-off` | 自动创建审批单 |
| 审批管理 | `GET /api/approvals`、`POST /api/approvals/{id}/approve`、`/reject` | 审批列表、通过、驳回 |
| 统计分析 | `GET /api/statistics/dashboard` | 总资产、库存、领用、待审批、分类分布 |
| 系统设置 | `/api/system/departments`、`roles`、`permissions`、`role-permissions`、`users`、`configs` | 对应资源的查询、新增/编辑，含角色授权 |
| 提醒中心 | `GET/POST /api/notifications`、`PUT /api/notifications/{id}/read` | 消息查询、新建、已读 |

所有 Controller 方法均有接口功能注释；资产流程的 `type` 枚举为 `ASSET_APPLY`、`ASSET_PURCHASE`、`ASSET_RECEIVE`、`ASSET_RETURN`、`ASSET_WRITE_OFF`。

可直接参考 [docs/API_EXAMPLES.md](docs/API_EXAMPLES.md) 与前端联调。
