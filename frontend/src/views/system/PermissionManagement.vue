<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">权限管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd(null)">新增权限</el-button>
    </div>

    <el-table :data="permissionTree" row-key="id" border default-expand-all style="width: 100%" v-loading="loading">
      <el-table-column prop="permissionCode" label="权限编码" width="200" />
      <el-table-column prop="permissionName" label="权限名称" min-width="160" />
      <el-table-column prop="resourcePath" label="资源路径" width="220" />
      <el-table-column prop="method" label="请求方法" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.method" size="small" :type="methodTag(row.method)">{{ row.method }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleAdd(row)">添加子权限</el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 权限表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑权限' : '新增权限'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级权限">
          <el-input :value="parentName" disabled />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="如：asset:view" />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="如：查看资产" />
        </el-form-item>
        <el-form-item label="资源路径">
          <el-input v-model="form.resourcePath" placeholder="如：/api/assets" />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-select v-model="form.method" placeholder="请选择" clearable style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
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
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPermissions, savePermission } from '@/api/system'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const permissionList = ref([])
const parentName = ref('')

const form = reactive({
  id: null,
  permissionCode: '',
  permissionName: '',
  resourcePath: '',
  method: '',
  parentId: 0
})

const rules = {
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }]
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

function methodTag(method) {
  const map = { GET: 'success', POST: 'primary', PUT: 'warning', DELETE: 'danger' }
  return map[method] || ''
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPermissions()
    permissionList.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd(parent) {
  Object.assign(form, { id: null, permissionCode: '', permissionName: '', resourcePath: '', method: '', parentId: parent ? parent.id : 0 })
  parentName.value = parent ? parent.permissionName : '顶级权限'
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  const parent = permissionList.value.find(p => p.id === row.parentId)
  parentName.value = parent ? parent.permissionName : '顶级权限'
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    saving.value = true
    await savePermission(form)
    ElMessage.success(form.id ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
