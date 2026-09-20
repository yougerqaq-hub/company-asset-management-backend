<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
    </div>

    <el-table :data="roleList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="roleCode" label="角色编码" width="160" />
      <el-table-column prop="roleName" label="角色名称" width="160" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handlePermission(row)">分配权限</el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 角色表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑角色' : '新增角色'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="如：ROLE_ADMIN" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="如：系统管理员" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配弹窗 -->
    <el-dialog v-model="permDialogVisible" :title="`分配权限 - ${currentRole?.roleName}`" width="600px" destroy-on-close>
      <el-tree
        ref="permTreeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermIds"
        :props="{ label: 'permissionName', children: 'children' }"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permSaving" @click="savePermissions">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getRoles, saveRole, deleteRole, getPermissions, getRolePermissions, saveRolePermission, deleteRolePermission } from '@/api/system'

const loading = ref(false)
const saving = ref(false)
const permSaving = ref(false)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const formRef = ref(null)
const permTreeRef = ref(null)
const roleList = ref([])
const permissionList = ref([])
const rolePermList = ref([])
const currentRole = ref(null)
const checkedPermIds = ref([])

const form = reactive({
  id: null,
  roleCode: '',
  roleName: '',
  description: '',
  status: 1
})

const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const permissionTree = computed(() => {
  const map = {}
  const roots = []
  permissionList.value.forEach(p => { map[p.id] = { ...p, children: [] } })
  permissionList.value.forEach(p => {
    if (p.parentId && map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    } else {
      roots.push(map[p.id])
    }
  })
  return roots
})

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function loadData() {
  loading.value = true
  try {
    const [roleRes, permRes, rpRes] = await Promise.all([getRoles(), getPermissions(), getRolePermissions()])
    roleList.value = roleRes.data
    permissionList.value = permRes.data
    rolePermList.value = rpRes.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { id: null, roleCode: '', roleName: '', description: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    saving.value = true
    await saveRole(form)
    ElMessage.success(form.id ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
  } finally {
    saving.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除角色「${row.roleName}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

async function handlePermission(row) {
  currentRole.value = row
  checkedPermIds.value = rolePermList.value.filter(rp => rp.roleId === row.id).map(rp => rp.permissionId)
  permDialogVisible.value = true
}

async function savePermissions() {
  permSaving.value = true
  try {
    const checkedKeys = permTreeRef.value.getCheckedKeys()
    const currentRps = rolePermList.value.filter(rp => rp.roleId === currentRole.value.id)
    // 删除不在新列表中的
    for (const rp of currentRps) {
      if (!checkedKeys.includes(rp.permissionId)) {
        await deleteRolePermission(rp.id)
      }
    }
    // 添加新的
    const existingPermIds = currentRps.map(rp => rp.permissionId)
    for (const permId of checkedKeys) {
      if (!existingPermIds.includes(permId)) {
        await saveRolePermission({ roleId: currentRole.value.id, permissionId: permId })
      }
    }
    ElMessage.success('权限保存成功')
    permDialogVisible.value = false
    loadData()
  } finally {
    permSaving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
