# 公司资产管理系统

基于 Spring Boot + Vue 3 的全栈公司资产管理系统。

## 项目结构

```
company-asset-management/
├── backend/              # Spring Boot 后端（Java 17 + JPA + MySQL）
│   ├── src/              # 源码
│   ├── sql/              # 数据库初始化脚本
│   ├── docs/             # API 文档
│   ├── pom.xml           # Maven 配置
│   └── README.md         # 后端说明
├── frontend/             # Vue 3 前端（Vite + Element Plus）
│   ├── src/              # 源码
│   ├── package.json      # npm 配置
│   ├── vite.config.js    # Vite 配置（已配置 /api 代理到 8080）
│   └── README.md         # 前端说明
└── README.md             # 本文件
```

## 环境要求

| 软件 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Maven | 3.6+ |
| Node.js | 16+ |
| MySQL | 5.7+ / 8.0+ |

## 快速启动

### 第一步：准备数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE asset_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

exit;
```

> 后端默认数据库账号：`root` / 密码：`root`。如不同，请修改 `backend/src/main/resources/application.yml`。

### 第二步：启动后端

```bash
cd backend
mvn spring-boot:run
```

启动成功标志：看到 `Tomcat started on port(s): 8080` 和 `Started AssetManagementApplication`。

后端接口地址：`http://localhost:8080/api`

### 第三步：启动前端

**新开一个终端窗口**（不要关后端）：

```bash
cd frontend
npm install
npm run dev
```

启动成功后访问：`http://localhost:5173`

前端已配置代理，`/api` 请求自动转发到后端 `http://localhost:8080`，无需额外配置跨域。

## 功能模块

- **认证**：登录、注册、Token 鉴权
- **首页概览**：资产统计卡片、分类分布饼图、状态分布柱状图、最近流程
- **资产管理**：资产台账（增删改查）、采购申请、领用申请、归还申请、核销申请、流程记录
- **审批管理**：待我审批、已办审批、审批详情（通过/驳回）
- **消息通知**：通知列表、标记已读、全部已读
- **系统设置**：用户管理、部门管理（树形）、角色管理（权限分配）、权限管理（树形）、系统配置

## 技术栈

### 后端
- Spring Boot 3.3.5
- Spring Data JPA / Hibernate
- Spring Security Crypto（BCrypt 密码加密）
- MySQL
- Lombok

### 前端
- Vue 3（Composition API）
- Vite 5
- Element Plus
- Vue Router 4
- Pinia
- Axios
- ECharts 5

## 注意事项

1. **两个终端都要保持运行**：一个跑后端（8080），一个跑前端（5173）
2. 前端 `npm install` 只需首次执行，之后直接 `npm run dev` 即可
3. 后端修改代码后需要重启，前端修改保存后自动热更新
4. 生产部署：后端 `mvn package` 生成 jar，前端 `npm run build` 生成 dist 目录
