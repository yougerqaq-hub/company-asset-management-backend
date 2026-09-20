import request from '@/utils/request'

// 部门管理
export function getDepartments() {
  return request({
    url: '/system/departments',
    method: 'get'
  })
}

export function saveDepartment(data) {
  return request({
    url: '/system/departments',
    method: 'post',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: `/system/departments/${id}`,
    method: 'delete'
  })
}

// 角色管理
export function getRoles() {
  return request({
    url: '/system/roles',
    method: 'get'
  })
}

export function saveRole(data) {
  return request({
    url: '/system/roles',
    method: 'post',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/system/roles/${id}`,
    method: 'delete'
  })
}

// 权限管理
export function getPermissions() {
  return request({
    url: '/system/permissions',
    method: 'get'
  })
}

export function savePermission(data) {
  return request({
    url: '/system/permissions',
    method: 'post',
    data
  })
}

// 角色权限关联
export function getRolePermissions() {
  return request({
    url: '/system/role-permissions',
    method: 'get'
  })
}

export function saveRolePermission(data) {
  return request({
    url: '/system/role-permissions',
    method: 'post',
    data
  })
}

export function deleteRolePermission(id) {
  return request({
    url: `/system/role-permissions/${id}`,
    method: 'delete'
  })
}

// 用户管理
export function getUsers() {
  return request({
    url: '/system/users',
    method: 'get'
  })
}

export function saveUser(data) {
  return request({
    url: '/system/users',
    method: 'post',
    data
  })
}

// 系统配置
export function getConfigs() {
  return request({
    url: '/system/configs',
    method: 'get'
  })
}

export function saveConfig(data) {
  return request({
    url: '/system/configs',
    method: 'post',
    data
  })
}
