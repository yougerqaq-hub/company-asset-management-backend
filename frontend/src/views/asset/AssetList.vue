<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">资产台账</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增资产</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索资产名称/编号/品牌" clearable style="width: 260px" :prefix-icon="Search" />
      <el-select v-model="filterStatus" placeholder="资产状态" clearable style="width: 140px">
        <el-option label="在库" value="IN_STOCK" />
        <el-option label="在用" value="IN_USE" />
        <el-option label="已核销" value="WRITTEN_OFF" />
      </el-select>
      <el-select v-model="filterCategory" placeholder="资产分类" clearable style="width: 160px">
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <!-- 资产表格 -->
    <el-table :data="filteredList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="assetCode" label="资产编号" width="140" />
      <el-table-column prop="name" label="资产名称" min-width="140" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="brand" label="品牌" width="100" />
      <el-table-column prop="model" label="型号" width="120" />
      <el-table-column prop="purchasePrice" label="采购价格" width="110">
        <template #default="{ row }">¥{{ row.purchasePrice || 0 }}</template>
      </el-table-column>
      <el-table-column prop="purchaseDate" label="采购日期" width="120" />
      <el-table-column prop="location" label="存放位置" width="120" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 资产表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑资产' : '新增资产'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产编号" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="如：AST-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产分类" prop="category">
              <el-input v-model="form.category" placeholder="如：电脑、办公家具" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input v-model="form.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购价格">
              <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购日期">
              <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-input v-model="form.supplier" placeholder="请输入供应商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置">
              <el-input v-model="form.location" placeholder="请输入存放位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="在库" value="IN_STOCK" />
                <el-option label="在用" value="IN_USE" />
                <el-option label="已核销" value="WRITTEN_OFF" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 资产详情弹窗 -->
    <el-dialog v-model="detailVisible" title="资产详情" width="600px">
      <el-descriptions :column="2" border v-if="currentAsset">
        <el-descriptions-item label="资产编号">{{ currentAsset.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentAsset.name }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentAsset.category }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ currentAsset.brand }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ currentAsset.model }}</el-descriptions-item>
        <el-descriptions-item label="采购价格">¥{{ currentAsset.purchasePrice || 0 }}</el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ currentAsset.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ currentAsset.supplier }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ currentAsset.location }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(currentAsset.status)" size="small">{{ statusLabel(currentAsset.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentAsset.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(currentAsset.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentAsset.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getAssetList, saveAsset, deleteAsset } from '@/api/asset'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const assetList = ref([])
const currentAsset = ref(null)
const searchKeyword = ref('')
const filterStatus = ref('')
const filterCategory = ref('')

const form = reactive({
  id: null,
  assetCode: '',
  name: '',
  category: '',
  brand: '',
  model: '',
  purchasePrice: null,
  purchaseDate: '',
  supplier: '',
  location: '',
  status: 'IN_STOCK',
  keeperId: null,
  departmentId: null,
  remark: ''
})

const rules = {
  assetCode: [{ required: true, message: '请输入资产编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入资产分类', trigger: 'blur' }],
  status: [{ required: true, message: '请选择资产状态', trigger: 'change' }]
}

const categories = computed(() => {
  const set = new Set(assetList.value.map(a => a.category).filter(Boolean))
  return Array.from(set)
})

const filteredList = computed(() => {
  return assetList.value.filter(item => {
    const matchKeyword = !searchKeyword.value ||
      (item.name && item.name.includes(searchKeyword.value)) ||
      (item.assetCode && item.assetCode.includes(searchKeyword.value)) ||
      (item.brand && item.brand.includes(searchKeyword.value))
    const matchStatus = !filterStatus.value || item.status === filterStatus.value
    const matchCategory = !filterCategory.value || item.category === filterCategory.value
    return matchKeyword && matchStatus && matchCategory
  })
})

function statusLabel(status) {
  const map = { IN_STOCK: '在库', IN_USE: '在用', WRITTEN_OFF: '已核销' }
  return map[status] || status
}

function statusTag(status) {
  const map = { IN_STOCK: 'success', IN_USE: 'warning', WRITTEN_OFF: 'info' }
  return map[status] || ''
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function loadData() {
  loading.value = true
  try {
    const res = await getAssetList()
    assetList.value = res.data
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  searchKeyword.value = ''
  filterStatus.value = ''
  filterCategory.value = ''
}

function handleAdd() {
  Object.assign(form, {
    id: null, assetCode: '', name: '', category: '', brand: '', model: '',
    purchasePrice: null, purchaseDate: '', supplier: '', location: '',
    status: 'IN_STOCK', keeperId: null, departmentId: null, remark: ''
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleView(row) {
  currentAsset.value = row
  detailVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    saving.value = true
    await saveAsset(form)
    ElMessage.success(form.id ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // handled
  } finally {
    saving.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除资产「${row.name}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteAsset(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>
