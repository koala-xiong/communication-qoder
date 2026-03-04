import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: 'Home' }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { title: 'Login', guest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { title: 'Register', guest: true }
    },
    {
      path: '/profile/:username',
      name: 'profile',
      component: () => import('@/views/user/ProfileView.vue'),
      meta: { title: 'Profile' }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/user/DashboardView.vue'),
      meta: { title: 'Dashboard', requiresAuth: true }
    },
    {
      path: '/subscriptions',
      name: 'subscriptions',
      component: () => import('@/views/user/SubscriptionsView.vue'),
      meta: { title: 'Subscriptions', requiresAuth: true }
    },
    {
      path: '/create',
      name: 'create-content',
      component: () => import('@/views/content/CreateContentView.vue'),
      meta: { title: 'Create Content', requiresAuth: true }
    },
    {
      path: '/content/:id',
      name: 'content-detail',
      component: () => import('@/views/content/ContentDetailView.vue'),
      meta: { title: 'Content' }
    },
    {
      path: '/edit/:id',
      name: 'edit-content',
      component: () => import('@/views/content/EditContentView.vue'),
      meta: { title: 'Edit Content', requiresAuth: true }
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/search/SearchView.vue'),
      meta: { title: 'Search' }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { title: 'Not Found' }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Update page title
  document.title = `${to.meta.title || 'Page'} - Communication`

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
