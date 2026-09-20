import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览', icon: 'Odometer' }
      },
      {
        path: 'assets',
        name: 'AssetList',
        component: () => import('@/views/asset/AssetList.vue'),
        meta: { title: '资产台账', icon: 'Box' }
      },
      {
        path: 'assets/purchase',
        name: 'AssetPurchase',
        component: () => import('@/views/asset/Purchase.vue'),
        meta: { title: '采购申请', icon: 'ShoppingCart' }
      },
      {
        path: 'assets/receive',
        name: 'AssetReceive',
        component: () => import('@/views/asset/Receive.vue'),
        meta: { title: '领用申请', icon: 'TakeawayBox' }
      },
      {
        path: 'assets/return',
        name: 'AssetReturn',
        component: () => import('@/views/asset/Return.vue'),
        meta: { title: '归还申请', icon: 'RefreshLeft' }
      },
      {
        path: 'assets/write-off',
        name: 'AssetWriteOff',
        component: () => import('@/views/asset/WriteOff.vue'),
        meta: { title: '核销申请', icon: 'Delete' }
      },
      {
        path: 'assets/operations',
        name: 'AssetOperations',
        component: () => import('@/views/asset/Operations.vue'),
        meta: { title: '流程记录', icon: 'List' }
      },
      {
        path: 'approvals/pending',
        name: 'ApprovalPending',
        component: () => import('@/views/approval/PendingList.vue'),
        meta: { title: '待我审批', icon: 'Clock' }
      },
      {
        path: 'approvals/done',
        name: 'ApprovalDone',
        component: () => import('@/views/approval/DoneList.vue'),
        meta: { title: '已办审批', icon: 'CircleCheck' }
      },
      {
        path: 'approvals/:id',
        name: 'ApprovalDetail',
        component: () => import('@/views/approval/ApprovalDetail.vue'),
        meta: { title: '审批详情', hidden: true }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/notification/NotificationList.vue'),
        meta: { title: '消息通知', icon: 'Bell' }
      },
      {
        path: 'system/users',
        name: 'SystemUsers',
        component: () => import('@/views/system/UserManagement.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/departments',
        name: 'SystemDepartments',
        component: () => import('@/views/system/DepartmentManagement.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'system/roles',
        name: 'SystemRoles',
        component: () => import('@/views/system/RoleManagement.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'system/permissions',
        name: 'SystemPermissions',
        component: () => import('@/views/system/PermissionManagement.vue'),
        meta: { title: '权限管理', icon: 'Key' }
      },
      {
        path: 'system/configs',
        name: 'SystemConfigs',
        component: () => import('@/views/system/ConfigManagement.vue'),
        meta: { title: '系统配置', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 公司资产管理系统` : '公司资产管理系统'
  const token = localStorage.getItem('accessToken')
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
