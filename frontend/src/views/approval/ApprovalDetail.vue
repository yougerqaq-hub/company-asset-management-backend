<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">审批详情</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <!-- 审批单信息 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <span style="font-weight: 600">审批单信息</span>
          </template>
          <el-descriptions :column="2" border v-if="approval">
            <el-descriptions-item label="审批编号">{{ approval.approvalNo }}</el-descriptions-item>
            <el-descriptions-item label="业务类型">
              <el-tag :type="typeTag(approval.businessType)" size="small">{{ typeLabel(approval.businessType) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审批标题" :span="2">{{ approval.title }}</el-descriptions-item>
            <el-descriptions-item label="申请人ID">{{ approval.applicantId }}</el-descriptions-item>
            <el-descriptions-item label="审批人ID">{{ approval.approverId || '待分配' }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="statusTag(approval.status)" size="small">{{ statusLabel(approval.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审批步骤">第 {{ approval.currentStep }} 步</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatTime(approval.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatTime(approval.updateTime) }}</el-descriptions-item>
            <el-descriptions-item label="审批意见" :span="2">{{ approval.comment || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 关联业务单信息 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <span style="font-weight: 600">关联业务单</span>
          </template>
          <el-descriptions :column="1" border v-if="operation">
            <el-descriptions-item label="流程编号">{{ operation.operationNo }}</el-descriptions-item>
            <el-descriptions-item label="流程类型">{{ typeLabel(operation.type) }}</el-descriptions-item>
            <el-descriptions-item label="资产名称">{{ operation.assetName }}</el-descriptions-item>
            <el-descriptions-item label="数量">{{ operation.quantity }}</el-descriptions-item>
            <el-descriptions-item label="期望日期">{{ operation.expectedDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="申请原因">{{ operation.reason || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ operation.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="暂无关联业务单" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 审批操作区 -->
    <el-card v-if="approval && approval.status === 'PENDING'" style="margin-top: 20px">
      <template #header>
        <span style="font-weight: 600; color: #e6a23c">审批操作</span>
      </template>
      <el-form :model="decisionForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="decisionForm.comment" type="textarea" :rows="3" placeholder="请输入审批意见（通过时可选，驳回时必填）" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" :icon="Check" :loading="submitting" @click="handleApprove">审批通过</el-button>
          <el-button type="danger" :icon="Close" :loading="submitting" @click="handleReject">审批驳回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'
import { getApprovalDetail, approve, reject } from '@/api/approval'
import { getOperationDetail } from '@/api/asset'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const approval = ref(null)
const operation = ref(null)

const decisionForm = reactive({
  comment: ''
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

async function loadData() {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getApprovalDetail(id)
    approval.value = res.data
    if (res.data.businessId) {
      try {
        const opRes = await getOperationDetail(res.data.businessId)
        operation.value = opRes.data
      } catch (e) {}
    }
  } finally {
    loading.value = false
  }
}

async function handleApprove() {
  submitting.value = true
  try {
    await approve(approval.value.id, {
      approverId: userStore.userId,
      comment: decisionForm.comment || '同意'
    })
    ElMessage.success('审批通过')
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleReject() {
  if (!decisionForm.comment.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  submitting.value = true
  try {
    await reject(approval.value.id, {
      approverId: userStore.userId,
      comment: decisionForm.comment
    })
    ElMessage.success('已驳回')
    loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
