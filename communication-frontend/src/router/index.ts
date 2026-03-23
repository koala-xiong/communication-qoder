import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: '首页' }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { title: '登录', guest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { title: '注册', guest: true }
    },
    {
      path: '/profile/:username',
      name: 'profile',
      component: () => import('@/views/user/ProfileView.vue'),
      meta: { title: '个人主页' }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/user/DashboardView.vue'),
      meta: { title: '控制台', requiresAuth: true }
    },
    {
      path: '/subscriptions',
      name: 'subscriptions',
      component: () => import('@/views/user/SubscriptionsView.vue'),
      meta: { title: '我的关注', requiresAuth: true }
    },
    {
      path: '/create',
      name: 'create-content',
      component: () => import('@/views/content/CreateContentView.vue'),
      meta: { title: '发布内容', requiresAuth: true }
    },
    {
      path: '/content/:id',
      name: 'content-detail',
      component: () => import('@/views/content/ContentDetailView.vue'),
      meta: { title: '内容详情' }
    },
    {
      path: '/edit/:id',
      name: 'edit-content',
      component: () => import('@/views/content/EditContentView.vue'),
      meta: { title: '编辑内容', requiresAuth: true }
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/search/SearchView.vue'),
      meta: { title: '搜索' }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { title: '页面不存在' }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Update page title
  document.title = `${to.meta.title || '页面'} - Communication`

  // Redirect authenticated users away from guest-only pages
  if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // Redirect unauthenticated users to login for protected routes
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  next()
})

export default router
