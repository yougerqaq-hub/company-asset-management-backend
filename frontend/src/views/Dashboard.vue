<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">首页概览</h2>
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新数据</el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="card-grid">
      <div class="stat-card" style="border-left: 4px solid #409eff">
        <el-icon class="stat-icon" color="#409eff"><Box /></el-icon>
        <div class="stat-label">资产总数</div>
        <div class="stat-value">{{ dashboard.assetTotal || 0 }}</div>
      </div>
      <div class="stat-card" style="border-left: 4px solid #67c23a">
        <el-icon class="stat-icon" color="#67c23a"><Goods /></el-icon>
        <div class="stat-label">在库资产</div>
        <div class="stat-value">{{ dashboard.inStock || 0 }}</div>
      </div>
      <div class="stat-card" style="border-left: 4px solid #e6a23c">
        <el-icon class="stat-icon" color="#e6a23c"><TakeawayBox /></el-icon>
        <div class="stat-label">在用资产</div>
        <div class="stat-value">{{ dashboard.inUse || 0 }}</div>
      </div>
      <div class="stat-card" style="border-left: 4px solid #f56c6c">
        <el-icon class="stat-icon" color="#f56c6c"><Clock /></el-icon>
        <div class="stat-label">待审批</div>
        <div class="stat-value">{{ dashboard.pendingApproval || 0 }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-row">
      <div class="chart-box">
        <h3>资产分类分布</h3>
        <div ref="categoryChartRef" style="width: 100%; height: 320px"></div>
      </div>
      <div class="chart-box">
        <h3>资产状态分布</h3>
        <div ref="statusChartRef" style="width: 100%; height: 320px"></div>
      </div>
    </div>

    <!-- 最近流程记录 -->
    <div class="chart-box" style="margin-top: 20px">
      <h3>最近资产流程</h3>
      <el-table :data="recentOperations" stripe style="width: 100%">
        <el-table-column prop="operationNo" label="流程编号" width="180" />
        <el-table-column prop="type" label="类型" width="140">
          <template #default="{ row }">
            <el-tag :type="operationTypeTag(row.type)" size="small">{{ operationTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assetName" label="资产名称" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { Refresh } from '@element-plus/icons-vue'
import { getDashboard } from '@/api/statistics'
import { getOperations } from '@/api/asset'

const dashboard = reactive({
  assetTotal: 0,
  inStock: 0,
  inUse: 0,
  pendingApproval: 0,
  categoryDistribution: []
})

const recentOperations = ref([])
const categoryChartRef = ref(null)
const statusChartRef = ref(null)
let categoryChart = null
let statusChart = null

function operationTypeLabel(type) {
  const map = {
    ASSET_APPLY: '资产申请',
    ASSET_PURCHASE: '采购申请',
    ASSET_RECEIVE: '领用申请',
    ASSET_RETURN: '归还申请',
    ASSET_WRITE_OFF: '核销申请'
  }
  return map[type] || type
}

function operationTypeTag(type) {
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

function initCategoryChart() {
  if (!categoryChartRef.value) return
  categoryChart = echarts.init(categoryChartRef.value)
  const data = (dashboard.categoryDistribution || []).map(item => ({
    name: item[0] || '未分类',
    value: item[1]
  }))
  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: data.length ? data : [{ name: '暂无数据', value: 1 }]
    }]
  })
}

function initStatusChart() {
  if (!statusChartRef.value) return
  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['在库', '在用', '已核销'], axisLine: { lineStyle: { color: '#dcdfe6' } } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [{
      type: 'bar',
      barWidth: '50%',
      data: [
        { value: dashboard.inStock || 0, itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] } },
        { value: dashboard.inUse || 0, itemStyle: { color: '#e6a23c', borderRadius: [4, 4, 0, 0] } },
        { value: 0, itemStyle: { color: '#909399', borderRadius: [4, 4, 0, 0] } }
      ]
    }]
  })
}

async function loadData() {
  try {
    const res = await getDashboard()
    Object.assign(dashboard, res.data)
    const opRes = await getOperations()
    recentOperations.value = opRes.data.slice(0, 8)
    await nextTick()
    initCategoryChart()
    initStatusChart()
  } catch (e) {
    // handled
  }
}

function handleResize() {
  categoryChart?.resize()
  statusChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  categoryChart?.dispose()
  statusChart?.dispose()
})
</script>
