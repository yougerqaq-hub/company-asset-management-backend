# 公司资产管理系统 - 前端

基于 Vue 3 + Vite + Element Plus 构建的公司资产管理系统前端，对接 Spring Boot 后端 API。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **HTTP 客户端**: Axios
- **图表**: ECharts 5
- **图标**: @element-plus/icons-vue

## 功能模块

### 1. 认证模块
- 用户登录 / 注册
- Token 存储与路由守卫
- 退出登录

### 2. 首页概览 (Dashboard)
- 资产总数、在库、在用、待审批统计卡片
- 资产分类分布饼图
- 资产状态分布柱状图
- 最近资产流程记录

### 3. 资产管理
- **资产台账**: 资产列表、新增/编辑/删除、搜索筛选、详情查看
- **采购申请**: 提交采购申请单
- **领用申请**: 选择在库资产提交领用申请
- **归还申请**: 选择在用资产提交归还申请
- **核销申请**: 提交资产核销申请
- **流程记录**: 查看所有资产流程记录及详情

### 4. 审批管理
- **待我审批**: 待审批列表、快捷通过/驳回、进入详情审批
- **已办审批**: 已审批记录查询
- **审批详情**: 审批单信息 + 关联业务单 + 审批操作

### 5. 消息通知
- 消息列表（未读/已读筛选）
- 点击标记已读
- 一键全部已读

### 6. 系统设置
- **用户管理**: 用户增删改查、启用/停用、分配部门和角色
- **部门管理**: 部门树形结构、增删改查
- **角色管理**: 角色增删改查、权限分配（树形勾选）
- **权限管理**: 权限树形结构、增删改查
- **系统配置**: 系统参数键值对管理

## 项目结构

```
company-asset-management-frontend/
├── index.html
├── package.json
├── vite.config.js
├── src/
│   ├── main.js                 # 应用入口
│   ├── App.vue                 # 根组件
│   ├── assets/
│   │   └── main.css            # 全局样式
│   ├── router/
│   │   └── index.js            # 路由配置
│   ├── stores/
│   │   └── user.js             # 用户状态管理
│   ├── utils/
│   │   └── request.js          # Axios 封装
│   ├── api/                    # API 接口层
│   │   ├── auth.js
│   │   ├── asset.js
│   │   ├── approval.js
│   │   ├── notification.js
│   │   ├── statistics.js
│   │   └── system.js
│   ├── layouts/
│   │   └── DefaultLayout.vue   # 主布局（侧边栏+顶栏）
│   └── views/                  # 页面组件
│       ├── Login.vue
│       ├── Register.vue
│       ├── Dashboard.vue
│       ├── asset/
│       │   ├── AssetList.vue
│       │   ├── Purchase.vue
│       │   ├── Receive.vue
│       │   ├── Return.vue
│       │   ├── WriteOff.vue
│       │   └── Operations.vue
│       ├── approval/
│       │   ├── PendingList.vue
│       │   ├── DoneList.vue
│       │   └── ApprovalDetail.vue
│       ├── notification/
│       │   └── NotificationList.vue
│       └── system/
│           ├── UserManagement.vue
│           ├── DepartmentManagement.vue
│           ├── RoleManagement.vue
│           ├── PermissionManagement.vue
│           └── ConfigManagement.vue
```

## 快速开始

### 环境要求
- Node.js >= 16
- npm 或 yarn

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```
访问 http://localhost:5173

### 构建生产版本
```bash
npm run build
```

### 预览生产构建
```bash
npm run preview
```

## 后端 API 对接

前端通过 Vite 代理将 `/api` 请求转发到后端 `http://localhost:8080`。

如需修改后端地址，编辑 `vite.config.js` 中的 `server.proxy` 配置。

### API 基础路径
所有接口统一前缀 `/api`，返回格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 主要接口
| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 认证 | POST | /api/auth/login | 用户登录 |
| 认证 | POST | /api/auth/register | 用户注册 |
| 资产 | GET | /api/assets | 资产列表 |
| 资产 | POST | /api/assets | 新增/更新资产 |
| 资产 | DELETE | /api/assets/{id} | 删除资产 |
| 资产 | POST | /api/assets/purchase | 采购申请 |
| 资产 | POST | /api/assets/receive | 领用申请 |
| 资产 | POST | /api/assets/return | 归还申请 |
| 资产 | POST | /api/assets/write-off | 核销申请 |
| 资产 | GET | /api/assets/operations | 流程记录 |
| 审批 | GET | /api/approvals | 审批列表 |
| 审批 | POST | /api/approvals/{id}/approve | 审批通过 |
| 审批 | POST | /api/approvals/{id}/reject | 审批驳回 |
| 通知 | GET | /api/notifications | 消息列表 |
| 通知 | PUT | /api/notifications/{id}/read | 标记已读 |
| 统计 | GET | /api/statistics/dashboard | 首页统计 |
| 系统 | GET/POST/DELETE | /api/system/users | 用户管理 |
| 系统 | GET/POST/DELETE | /api/system/departments | 部门管理 |
| 系统 | GET/POST/DELETE | /api/system/roles | 角色管理 |
| 系统 | GET/POST | /api/system/permissions | 权限管理 |
| 系统 | GET/POST | /api/system/configs | 系统配置 |

## 资产状态说明

| 状态值 | 说明 |
|--------|------|
| IN_STOCK | 在库 |
| IN_USE | 在用 |
| WRITTEN_OFF | 已核销 |

## 流程类型说明

| 类型值 | 说明 |
|--------|------|
| ASSET_APPLY | 资产申请 |
| ASSET_PURCHASE | 采购申请 |
| ASSET_RECEIVE | 领用申请 |
| ASSET_RETURN | 归还申请 |
| ASSET_WRITE_OFF | 核销申请 |

## 审批状态说明

| 状态值 | 说明 |
|--------|------|
| PENDING | 待审批 |
| APPROVED | 已通过 |
| REJECTED | 已驳回 |
