import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'TaskCenter',
    component: () => import('../components/TaskCenter.vue'),
    meta: {
      title: '任务中心 - 学习平台',
      keepAlive: true
    }
  },
  {
    path: '/task/:id',
    name: 'TaskDetail',
    component: () => import('../components/TaskDetail.vue'),
    props: true,
    meta: {
      title: '任务详情 - 学习平台'
      // 移除了 requireAuth: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../components/NotFound.vue'),
    meta: {
      title: '页面未找到'
    }
  },
  {
    path: '/task/new',
    name: 'NewTask',
    component: () => import('../components/NewTask.vue'),
    meta: {
      title: '新建任务 - 学习平台'
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 只保留设置标题的功能，移除认证检查
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router