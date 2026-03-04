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
        <h1 class="welcome-title">Welcome to Communication</h1>
        <p class="welcome-subtitle">
          Share your stories, connect with creators, and discover amazing content.
        </p>
        <div class="welcome-actions">
          <router-link to="/register">
            <el-button type="primary" size="large">Get Started</el-button>
          </router-link>
          <router-link to="/login">
            <el-button size="large">Sign In</el-button>
          </router-link>
        </div>
      </div>

      <div class="feed-section">
        <div class="feed-header">
          <h2>Latest Content</h2>
          <el-button 
            v-if="authStore.isAuthenticated" 
            type="primary" 
            @click="router.push('/create')"
          >
            Create Content
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
  background: linear-gradient(135deg, var(--color-bg-tertiary) 0%, var(--color-bg-secondary) 100%);
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
