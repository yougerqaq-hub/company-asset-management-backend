<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon :size="24" color="#fff"><Box /></el-icon>
        <span v-if="!isCollapse" class="logo-text">资产管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#1f2d3d"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>首页概览</template>
        </el-menu-item>

        <el-sub-menu index="assets">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>资产管理</span>
          </template>
          <el-menu-item index="/assets">资产台账</el-menu-item>
          <el-menu-item index="/assets/purchase">采购申请</el-menu-item>
          <el-menu-item index="/assets/receive">领用申请</el-menu-item>
          <el-menu-item index="/assets/return">归还申请</el-menu-item>
          <el-menu-item index="/assets/write-off">核销申请</el-menu-item>
          <el-menu-item index="/assets/operations">流程记录</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="approvals">
          <template #title>
            <el-icon><Checked /></el-icon>
            <span>审批管理</span>
          </template>
          <el-menu-item index="/approvals/pending">
            <el-badge :value="pendingCount" :hidden="pendingCount === 0" class="menu-badge">
              待我审批
            </el-badge>
          </el-menu-item>
          <el-menu-item index="/approvals/done">已办审批</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/notifications">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="menu-badge">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-badge>
        </el-menu-item>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </template>
          <el-menu-item index="/system/users">用户管理</el-menu-item>
          <el-menu-item index="/system/departments">部门管理</el-menu-item>
          <el-menu-item index="/system/roles">角色管理</el-menu-item>
          <el-menu-item index="/system/permissions">权限管理</el-menu-item>
          <el-menu-item index="/system/configs">系统配置</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title && $route.path !== '/dashboard'">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" style="background: #409eff">
                {{ realName ? realName.charAt(0).toUpperCase() : 'U' }}
              </el-avatar>
              <span class="username">{{ realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getNotificationList } from '@/api/notification'
import { getApprovalList } from '@/api/approval'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const unreadCount = ref(0)
const pendingCount = ref(0)

const activeMenu = computed(() => route.path)
const realName = computed(() => userStore.realName)

function toggleCollapse() {
  isCollapse.value = !isCollapse.value
}

async function loadUnreadCount() {
  try {
    const res = await getNotificationList()
    const userId = userStore.userId
    unreadCount.value = res.data.filter(n => n.userId === userId && n.readFlag === 0).length
  } catch (e) {
    // ignore
  }
}

async function loadPendingCount() {
  try {
    const res = await getApprovalList()
    const userId = userStore.userId
    pendingCount.value = res.data.filter(a => a.status === 'PENDING' && a.approverId === userId).length
  } catch (e) {
    // ignore
  }
}

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中')
  }
}

onMounted(() => {
  loadUnreadCount()
  loadPendingCount()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #1f2d3d;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #2d3e50;
}

.logo-text {
  white-space: nowrap;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}

.menu-badge {
  margin-right: 8px;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #303133;
}

.main-content {
  background: #f0f2f5;
  padding: 0;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
