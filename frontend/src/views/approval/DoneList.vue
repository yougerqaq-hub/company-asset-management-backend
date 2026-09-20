<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">已办审批</h2>
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新</el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="filterStatus" placeholder="审批结果" clearable style="width: 140px">
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-input v-model="searchKeyword" placeholder="搜索审批编号/标题" clearable style="width: 240px" :prefix-icon="Search" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <el-table :data="filteredList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="approvalNo" label="审批编号" width="170" />
      <el-table-column prop="title" label="审批标题" min-width="200" />
      <el-table-column prop="businessType" label="业务类型" width="120">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.businessType)" size="small">{{ typeLabel(row.businessType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applicantId" label="申请人ID" width="100" />
      <el-table-column prop="status" label="结果" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : 'danger'" size="small">
            {{ row.status === 'APPROVED' ? '已通过' : '已驳回' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="comment" label="审批意见" min-width="160" show-overflow-tooltip />
      <el-table-column prop="createTime" label="提交时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="updateTime" label="审批时间" width="170">
        <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="goDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && filteredList.length === 0" description="暂无已办审批记录" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getApprovalList } from '@/api/approval'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const doneList = ref([])
const filterStatus = ref('')
const searchKeyword = ref('')

const filteredList = computed(() => {
  return doneList.value.filter(item => {
    const matchStatus = !filterStatus.value || item.status === filterStatus.value
    const matchKeyword = !searchKeyword.value ||
      (item.approvalNo && item.approvalNo.includes(searchKeyword.value)) ||
      (item.title && item.title.includes(searchKeyword.value))
    return matchStatus && matchKeyword
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

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

function goDetail(row) {
  router.push(`/approvals/${row.id}`)
}

function resetFilter() {
  filterStatus.value = ''
  searchKeyword.value = ''
}

async function loadData() {
  loading.value = true
  try {
    const res = await getApprovalList()
    const userId = userStore.userId
    doneList.value = res.data.filter(a => a.status !== 'PENDING' && a.approverId === userId)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
