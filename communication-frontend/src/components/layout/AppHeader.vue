<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Search, Plus, User, Star, Menu } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const searchQuery = ref('')
const mobileMenuVisible = ref(false)

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ name: 'search', query: { q: searchQuery.value } })
    mobileMenuVisible.value = false
  }
}

const debouncedSearch = () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    if (searchQuery.value.trim()) {
      handleSearch()
    }
  }, 500)
}

const handleLogout = () => {
  authStore.logout()
  router.push({ name: 'home' })
  mobileMenuVisible.value = false
}

const navigateTo = (path: string) => {
  router.push(path)
  mobileMenuVisible.value = false
}
</script>

<template>
  <header class="app-header">
    <div class="header-container">
      <router-link to="/" class="logo">
        <span class="logo-text">Communication</span>
        <span class="logo-text-short">Comm</span>
      </router-link>

      <div class="search-box desktop-only">
        <el-input
          v-model="searchQuery"
          placeholder="搜索内容..."
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
          @input="debouncedSearch"
          clearable
        />
      </div>

      <!-- Desktop Navigation -->
      <nav class="nav-actions desktop-only">
        <template v-if="authStore.isAuthenticated">
          <el-button text @click="router.push('/subscriptions')">
            <el-icon><Star /></el-icon>
            订阅
          </el-button>
          <el-button type="primary" :icon="Plus" @click="router.push('/create')">
            发布
          </el-button>
          
          <el-dropdown trigger="click">
            <el-avatar
              :size="36"
              :src="authStore.user?.avatarUrl || undefined"
              class="user-avatar"
            >
              {{ authStore.user?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push(`/profile/${authStore.user?.username}`)">
                  <el-icon><User /></el-icon>
                  个人主页
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/dashboard')">
                  控制台
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        
        <template v-else>
          <el-button @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </template>
      </nav>

      <!-- Mobile Menu Button -->
      <el-button class="mobile-menu-btn mobile-only" text @click="mobileMenuVisible = true">
        <el-icon :size="24"><Menu /></el-icon>
      </el-button>
    </div>

    <!-- Mobile Drawer -->
    <el-drawer
      v-model="mobileMenuVisible"
      direction="rtl"
      size="280px"
      :show-close="false"
    >
      <template #header>
        <div class="mobile-drawer-header">
          <span class="logo-text">Communication</span>
        </div>
      </template>
      
      <div class="mobile-menu">
        <div class="mobile-search">
          <el-input
            v-model="searchQuery"
            placeholder="搜索内容..."
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            clearable
          />
        </div>

        <div class="mobile-nav">
          <template v-if="authStore.isAuthenticated">
            <div class="mobile-user-info">
              <el-avatar :size="48" :src="authStore.user?.avatarUrl">
                {{ authStore.user?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="username">{{ authStore.user?.username }}</span>
            </div>
            
            <el-button text class="mobile-nav-item" @click="navigateTo('/')">
              首页
            </el-button>
            <el-button text class="mobile-nav-item" @click="navigateTo('/subscriptions')">
              <el-icon><Star /></el-icon> 订阅
            </el-button>
            <el-button text class="mobile-nav-item" @click="navigateTo(`/profile/${authStore.user?.username}`)">
              <el-icon><User /></el-icon> 个人主页
            </el-button>
            <el-button text class="mobile-nav-item" @click="navigateTo('/dashboard')">
              控制台
            </el-button>
            <el-button type="primary" class="mobile-create-btn" @click="navigateTo('/create')">
              <el-icon><Plus /></el-icon> 发布内容
            </el-button>
            <el-button text class="mobile-nav-item logout" @click="handleLogout">
              退出登录
            </el-button>
          </template>
          
          <template v-else>
            <el-button text class="mobile-nav-item" @click="navigateTo('/')">
              首页
            </el-button>
            <el-button text class="mobile-nav-item" @click="navigateTo('/search')">
              发现
            </el-button>
            <div class="mobile-auth-buttons">
              <el-button @click="navigateTo('/login')">登录</el-button>
              <el-button type="primary" @click="navigateTo('/register')">注册</el-button>
            </div>
          </template>
        </div>
      </div>
    </el-drawer>
  </header>
</template>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  text-decoration: none;
  flex-shrink: 0;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-text-short {
  display: none;
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  cursor: pointer;
  background: var(--color-primary);
  color: white;
  transition: transform var(--transition-fast);
}

.user-avatar:hover {
  transform: scale(1.05);
}

/* Mobile styles */
.mobile-only {
  display: none;
}

.mobile-menu-btn {
  padding: 8px;
}

.mobile-drawer-header {
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.mobile-menu {
  padding: 16px 0;
}

.mobile-search {
  margin-bottom: 24px;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.mobile-user-info .username {
  font-weight: 600;
  font-size: 16px;
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  justify-content: flex-start;
  padding: 12px 0;
  font-size: 16px;
}

.mobile-nav-item.logout {
  color: var(--color-error);
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.mobile-create-btn {
  width: 100%;
  margin-top: 16px;
}

.mobile-auth-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

.mobile-auth-buttons .el-button {
  width: 100%;
}

/* Responsive */
@media (max-width: 768px) {
  .desktop-only {
    display: none !important;
  }
  
  .mobile-only {
    display: flex !important;
  }
  
  .header-container {
    padding: 0 16px;
    gap: 12px;
  }
  
  .logo-text {
    display: none;
  }
  
  .logo-text-short {
    display: block;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 12px;
  }
}
</style>
