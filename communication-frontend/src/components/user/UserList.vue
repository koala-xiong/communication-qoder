<template>
  <div class="user-list">
    <div v-if="loading && users.length === 0" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="users.length === 0" class="empty">
      <el-empty :description="emptyText" :image-size="80" />
    </div>
    
    <template v-else>
      <div v-for="user in users" :key="user.id" class="user-item">
        <router-link :to="`/profile/${user.username}`" class="user-link">
          <el-avatar :size="48" :src="user.avatarUrl">
            {{ user.username.charAt(0).toUpperCase() }}
          </el-avatar>
          <div class="user-info">
            <span class="username">{{ user.username }}</span>
            <span v-if="user.bio" class="bio">{{ user.bio }}</span>
          </div>
        </router-link>
      </div>
      
      <div v-if="!isLast" class="load-more">
        <el-button :loading="loading" text @click="$emit('load-more')">
          加载更多
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { User } from '@/api/subscription'

defineProps<{
  users: User[]
  loading?: boolean
  isLast?: boolean
  emptyText?: string
}>()

defineEmits<{
  'load-more': []
}>()
</script>

<style scoped>
.user-list {
  max-height: 400px;
  overflow-y: auto;
}

.user-item {
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.user-item:last-child {
  border-bottom: none;
}

.user-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: inherit;
  transition: opacity 0.2s;
}

.user-link:hover {
  opacity: 0.8;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.bio {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 250px;
}

.loading,
.empty {
  padding: 20px 0;
}

.load-more {
  text-align: center;
  padding: 12px 0;
}
</style>
