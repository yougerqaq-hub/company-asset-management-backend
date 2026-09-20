<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">消息通知</h2>
      <div>
        <el-button type="primary" :icon="Check" @click="markAllRead">全部已读</el-button>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
      </div>
    </div>

    <div class="search-bar">
      <el-radio-group v-model="filterRead" @change="loadData">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="unread">未读</el-radio-button>
        <el-radio-button value="read">已读</el-radio-button>
      </el-radio-group>
    </div>

    <div v-loading="loading">
      <div v-for="item in filteredList" :key="item.id"
        class="notification-item"
        :class="{ unread: item.readFlag === 0 }"
        @click="handleRead(item)">
        <div class="notification-left">
          <el-avatar :size="40" :style="{ background: typeColor(item.type) }">
            <el-icon :size="20"><Bell /></el-icon>
          </el-avatar>
        </div>
        <div class="notification-content">
          <div class="notification-header">
            <span class="notification-title">{{ item.title }}</span>
            <el-tag v-if="item.readFlag === 0" type="danger" size="small" effect="dark">未读</el-tag>
            <span class="notification-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="notification-body">{{ item.content }}</div>
        </div>
      </div>

      <el-empty v-if="!loading && filteredList.length === 0" description="暂无消息通知" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, Check, Refresh } from '@element-plus/icons-vue'
import { getNotificationList, markAsRead } from '@/api/notification'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const notifications = ref([])
const filterRead = ref('')

const filteredList = computed(() => {
  const userId = userStore.userId
  let list = notifications.value.filter(n => n.userId === userId || n.userId === null)
  if (filterRead.value === 'unread') {
    list = list.filter(n => n.readFlag === 0)
  } else if (filterRead.value === 'read') {
    list = list.filter(n => n.readFlag === 1)
  }
  return list
})

function typeColor(type) {
  const map = {
    SYSTEM: '#909399',
    APPROVAL: '#409eff',
    WARNING: '#e6a23c'
  }
  return map[type] || '#409eff'
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function handleRead(item) {
  if (item.readFlag === 0) {
    await markAsRead(item.id)
    item.readFlag = 1
  }
}

async function markAllRead() {
  const unreadItems = filteredList.value.filter(n => n.readFlag === 0)
  for (const item of unreadItems) {
    await markAsRead(item.id)
    item.readFlag = 1
  }
  ElMessage.success('已全部标记为已读')
}

async function loadData() {
  loading.value = true
  try {
    const res = await getNotificationList()
    notifications.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.notification-item {
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.notification-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.notification-item.unread {
  border-left: 3px solid #f56c6c;
  background: #fef0f0;
}

.notification-content {
  flex: 1;
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.notification-time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}

.notification-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
</style>
