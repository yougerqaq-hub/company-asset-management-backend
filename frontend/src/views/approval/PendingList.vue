<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">待我审批</h2>
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新</el-button>
    </div>

    <el-table :data="pendingList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="approvalNo" label="审批编号" width="170" />
      <el-table-column prop="title" label="审批标题" min-width="200" />
      <el-table-column prop="businessType" label="业务类型" width="120">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.businessType)" size="small">{{ typeLabel(row.businessType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applicantId" label="申请人ID" width="100" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag type="warning" size="small">待审批</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="goDetail(row)">审批</el-button>
          <el-button link type="success" size="small" @click="quickApprove(row)">通过</el-button>
          <el-button link type="danger" size="small" @click="quickReject(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && pendingList.length === 0" description="暂无待审批事项" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getApprovalList, approve, reject } from '@/api/approval'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const pendingList = ref([])

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

function quickApprove(row) {
  ElMessageBox.prompt('请输入审批意见（可选）', '审批通过', {
    confirmButtonText: '确认通过',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入审批意见'
  }).then(async ({ value }) => {
    await approve(row.id, { approverId: userStore.userId, comment: value || '同意' })
    ElMessage.success('审批通过')
    loadData()
  }).catch(() => {})
}

function quickReject(row) {
  ElMessageBox.prompt('请输入驳回原因', '审批驳回', {
    confirmButtonText: '确认驳回',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入驳回原因',
    inputValidator: (val) => !!val || '请输入驳回原因'
  }).then(async ({ value }) => {
    await reject(row.id, { approverId: userStore.userId, comment: value })
    ElMessage.success('已驳回')
    loadData()
  }).catch(() => {})
}

async function loadData() {
  loading.value = true
  try {
    const res = await getApprovalList()
    const userId = userStore.userId
    pendingList.value = res.data.filter(a => a.status === 'PENDING' && (a.approverId === userId || a.approverId === null))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
