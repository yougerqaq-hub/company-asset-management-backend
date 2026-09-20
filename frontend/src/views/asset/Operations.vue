<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">流程记录</h2>
    </div>

    <!-- 筛选栏 -->
    <div class="search-bar">
      <el-select v-model="filterType" placeholder="流程类型" clearable style="width: 160px">
        <el-option label="资产申请" value="ASSET_APPLY" />
        <el-option label="采购申请" value="ASSET_PURCHASE" />
        <el-option label="领用申请" value="ASSET_RECEIVE" />
        <el-option label="归还申请" value="ASSET_RETURN" />
        <el-option label="核销申请" value="ASSET_WRITE_OFF" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="审批状态" clearable style="width: 140px">
        <el-option label="待审批" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-input v-model="searchKeyword" placeholder="搜索资产名称/流程编号" clearable style="width: 240px" :prefix-icon="Search" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <!-- 流程记录表格 -->
    <el-table :data="filteredList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="operationNo" label="流程编号" width="170" />
      <el-table-column prop="type" label="类型" width="110">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assetName" label="资产名称" min-width="140" />
      <el-table-column prop="quantity" label="数量" width="70" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expectedDate" label="期望日期" width="120" />
      <el-table-column prop="reason" label="原因" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createTime" label="提交时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="流程详情" width="640px">
      <el-descriptions :column="2" border v-if="currentOp">
        <el-descriptions-item label="流程编号">{{ currentOp.operationNo }}</el-descriptions-item>
        <el-descriptions-item label="流程类型">
          <el-tag :type="typeTag(currentOp.type)" size="small">{{ typeLabel(currentOp.type) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentOp.assetName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentOp.quantity }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(currentOp.status)" size="small">{{ statusLabel(currentOp.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="期望日期">{{ currentOp.expectedDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人ID">{{ currentOp.applicantId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门ID">{{ currentOp.departmentId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatTime(currentOp.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(currentOp.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="原因" :span="2">{{ currentOp.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOp.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getOperations } from '@/api/asset'

const loading = ref(false)
const operations = ref([])
const detailVisible = ref(false)
const currentOp = ref(null)
const filterType = ref('')
const filterStatus = ref('')
const searchKeyword = ref('')

const filteredList = computed(() => {
  return operations.value.filter(item => {
    const matchType = !filterType.value || item.type === filterType.value
    const matchStatus = !filterStatus.value || item.status === filterStatus.value
    const matchKeyword = !searchKeyword.value ||
      (item.assetName && item.assetName.includes(searchKeyword.value)) ||
      (item.operationNo && item.operationNo.includes(searchKeyword.value))
    return matchType && matchStatus && matchKeyword
  })
})

function typeLabel(type) {
  const map = {
    ASSET_APPLY: '资产申请',
    ASSET_PURCHASE: '采购申请',
    ASSET_RECEIVE: '领用申请',
    ASSET_RETURN: '归还申请',
    ASSET_WRITE_OFF: '核销申请'
  }
  return map[type] || type
}

function typeTag(type) {
  const map = {
    ASSET_APPLY: '',
    ASSET_PURCHASE: 'warning',
    ASSET_RECEIVE: 'primary',
    ASSET_RETURN: 'success',
    ASSET_WRITE_OFF: 'danger'
  }
  return map[type] || ''
}

function statusLabel(status) {
  const map = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[status] || status
}

function statusTag(status) {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || ''
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

function resetFilter() {
  filterType.value = ''
  filterStatus.value = ''
  searchKeyword.value = ''
}

function handleView(row) {
  currentOp.value = row
  detailVisible.value = true
}

async function loadData() {
  loading.value = true
  try {
    const res = await getOperations()
    operations.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
