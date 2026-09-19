# 前后端联调示例

所有请求使用 `Content-Type: application/json`，所有响应使用统一的 `Result`：`code`、`message`、`data`。

## 登录

`POST /api/auth/login`

```json
{"username":"admin","password":"123456"}
```

## 新增资产

`POST /api/assets`

```json
{"assetCode":"AST-20260004","name":"联想笔记本","category":"电脑","brand":"Lenovo","model":"ThinkPad X1","purchasePrice":8999,"purchaseDate":"2026-09-19","supplier":"联想","location":"技术部库房","status":"IN_STOCK","departmentId":2}
```

## 提交领用申请

`POST /api/assets/receive`

```json
{"assetId":2,"assetName":"戴尔显示器","quantity":1,"applicantId":2,"departmentId":2,"reason":"开发工作需要","expectedDate":"2026-09-20"}
```

## 审批通过

`POST /api/approvals/2/approve`

```json
{"approverId":1,"comment":"同意领用"}
```

审批通过领用单会将资产状态更新为 `IN_USE`；归还单更新为 `IN_STOCK`；核销单更新为 `WRITTEN_OFF`。采购单、普通申请单仍保留为业务记录，管理员可在资产台账页面创建对应实物资产。
