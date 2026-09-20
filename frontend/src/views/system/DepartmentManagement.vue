<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">部门管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd(null)">新增部门</el-button>
    </div>

    <el-table :data="departmentTree" row-key="id" border default-expand-all style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="部门名称" min-width="200" />
      <el-table-column prop="leader" label="负责人" width="120" />
      <el-table-column prop="phone" label="联系电话" width="150" />
      <el-table-column prop="sortNo" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleAdd(row)">添加子部门</el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 部门表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑部门' : '新增部门'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门">
          <el-input :value="parentName" disabled />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sortNo" :min="0" style="width: 100%" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDepartments, saveDepartment, deleteDepartment } from '@/api/system'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const departmentList = ref([])
const parentName = ref('')

const form = reactive({
  id: null,
  name: '',
  parentId: 0,
  leader: '',
  phone: '',
  sortNo: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const departmentTree = computed(() => {
  const map = {}
  const roots = []
  departmentList.value.forEach(d => { map[d.id] = { ...d, children: [] } })
  departmentList.value.forEach(d => {
    if (d.parentId && map[d.parentId]) {
      map[d.parentId].children.push(map[d.id])
    } else {
      roots.push(map[d.id])
    }
  })
  return roots
})

async function loadData() {
  loading.value = true
  try {
    const res = await getDepartments()
    departmentList.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd(parent) {
  Object.assign(form, { id: null, name: '', parentId: parent ? parent.id : 0, leader: '', phone: '', sortNo: 0, status: 1 })
  parentName.value = parent ? parent.name : '顶级部门'
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  const parent = departmentList.value.find(d => d.id === row.parentId)
  parentName.value = parent ? parent.name : '顶级部门'
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    saving.value = true
    await saveDepartment(form)
    ElMessage.success(form.id ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
  } finally {
    saving.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除部门「${row.name}」吗？子部门也将受到影响。`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDepartment(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>
