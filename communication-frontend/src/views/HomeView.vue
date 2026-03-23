<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ContentFeed from '@/components/content/ContentFeed.vue'

const router = useRouter()
const authStore = useAuthStore()
</script>

<template>
  <div class="home-page">
    <div class="container">
      <div class="welcome-section" v-if="!authStore.isAuthenticated">
        <h1 class="welcome-title">欢迎来到 Communication</h1>
        <p class="welcome-subtitle">
          分享动态、关注创作者、发现精彩内容。
        </p>
        <div class="welcome-actions">
          <router-link to="/register">
            <el-button type="primary" size="large">立即注册</el-button>
          </router-link>
          <router-link to="/login">
            <el-button size="large">登录</el-button>
          </router-link>
        </div>
      </div>

      <div class="feed-section">
        <div class="feed-header">
          <h2>最新动态</h2>
          <el-button 
            v-if="authStore.isAuthenticated" 
            type="primary" 
            @click="router.push('/create')"
          >
            发布内容
          </el-button>
        </div>

        <ContentFeed />
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  padding: 40px 0;
}

.welcome-section {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, var(--color-secondary-light) 0%, var(--color-bg-secondary) 100%);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  margin-bottom: 40px;
}

.welcome-title {
  font-size: 42px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 16px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 18px;
  color: var(--color-text-secondary);
  max-width: 500px;
  margin: 0 auto 32px;
}

.welcome-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.feed-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-primary);
}
</style>
