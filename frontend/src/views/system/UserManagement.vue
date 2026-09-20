<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索用户名/姓名/手机号" clearable style="width: 260px" :prefix-icon="Search" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="searchKeyword = ''">重置</el-button>
    </div>

    <el-table :data="filteredList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="departmentId" label="部门ID" width="100" />
      <el-table-column prop="roleId" label="角色ID" width="100" />
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
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
            {{ row.status === 1 ? '停用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 用户表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="form.departmentId" placeholder="请选择部门" clearable style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="请选择角色" clearable style="width: 100%">
            <el-option v-for="role in roles" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入初始密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getUsers, saveUser, getDepartments, getRoles } from '@/api/system'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const userList = ref([])
const departments = ref([])
const roles = ref([])
const searchKeyword = ref('')

const form = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  departmentId: null,
  roleId: null,
  status: 1,
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const filteredList = computed(() => {
  return userList.value.filter(item => {
    if (!searchKeyword.value) return true
    return (item.username && item.username.includes(searchKeyword.value)) ||
      (item.realName && item.realName.includes(searchKeyword.value)) ||
      (item.phone && item.phone.includes(searchKeyword.value))
  })
})

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function loadData() {
  loading.value = true
  try {
    const [userRes, deptRes, roleRes] = await Promise.all([getUsers(), getDepartments(), getRoles()])
    userList.value = userRes.data
    departments.value = deptRes.data
    roles.value = roleRes.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { id: null, username: '', realName: '', phone: '', departmentId: null, roleId: null, status: 1, password: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row, { password: '' })
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    saving.value = true
    const { password, ...saveData } = form
    if (!form.id) saveData.password = password
    await saveUser(saveData)
    ElMessage.success(form.id ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
  } finally {
    saving.value = false
  }
}

function toggleStatus(row) {
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确定要${action}用户「${row.username}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await saveUser({ ...row, status: row.status === 1 ? 0 : 1 })
      ElMessage.success(`${action}成功`)
      loadData()
    }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>
