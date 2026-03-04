<template>
  <div class="empty-state">
    <div class="empty-icon">
      <slot name="icon">
        <el-icon :size="64"><component :is="iconComponent" /></el-icon>
      </slot>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p class="empty-description" v-if="description">{{ description }}</p>
    <div class="empty-action" v-if="$slots.action">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Document, Search, User, ChatLineRound, Star } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  title?: string
  description?: string
  icon?: 'content' | 'search' | 'user' | 'comment' | 'subscription'
}>(), {
  title: '暂无数据',
  icon: 'content'
})

const iconComponent = computed(() => {
  const icons = {
    content: Document,
    search: Search,
    user: User,
    comment: ChatLineRound,
    subscription: Star
  }
  return icons[props.icon]
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 20px;
  color: var(--color-secondary);
  opacity: 0.8;
}

.empty-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.empty-description {
  margin: 0 0 24px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  max-width: 300px;
}

.empty-action {
  display: flex;
  gap: 12px;
}
</style>
