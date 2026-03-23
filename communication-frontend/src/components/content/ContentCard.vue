<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { Content } from '@/api/content'
import { View, VideoPlay, Picture, Document, ChatLineRound } from '@element-plus/icons-vue'

const props = defineProps<{
  content: Content
}>()

const router = useRouter()

const mediaIcon = computed(() => {
  switch (props.content.mediaType) {
    case 'VIDEO': return VideoPlay
    case 'IMAGE': return Picture
    default: return Document
  }
})

const formattedDate = computed(() => {
  const date = new Date(props.content.createdAt)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
})

const truncatedBody = computed(() => {
  if (!props.content.body) return ''
  return props.content.body.length > 150
    ? props.content.body.substring(0, 150) + '...'
    : props.content.body
})

const mediaTypeLabel = computed(() => {
  switch (props.content.mediaType) {
    case 'IMAGE':
      return '图片'
    case 'VIDEO':
      return '视频'
    default:
      return '文字'
  }
})

const handleClick = () => {
  router.push(`/content/${props.content.id}`)
}

const handleAuthorClick = (e: Event) => {
  e.stopPropagation()
  router.push(`/profile/${props.content.author.username}`)
}

const handleTagClick = (e: Event, tag: string) => {
  e.stopPropagation()
  router.push({ path: '/search', query: { tag } })
}
</script>

<template>
  <div class="content-card" @click="handleClick">
    <div class="card-media" v-if="content.mediaUrl">
      <img
        v-if="content.mediaType === 'IMAGE'"
        :src="content.mediaUrl"
        :alt="content.title"
        class="media-image"
      />
      <video
        v-else-if="content.mediaType === 'VIDEO'"
        :src="content.mediaUrl"
        class="media-video"
      />
    </div>

    <div class="card-content">
      <div class="card-header">
        <el-tag
          :type="content.mediaType === 'VIDEO' ? 'danger' : content.mediaType === 'IMAGE' ? 'success' : 'info'"
          size="small"
          class="media-type-tag"
        >
          <el-icon><component :is="mediaIcon" /></el-icon>
          {{ mediaTypeLabel }}
        </el-tag>
      </div>

      <h3 class="card-title">{{ content.title }}</h3>

      <p class="card-body" v-if="truncatedBody">{{ truncatedBody }}</p>

      <!-- 标签 -->
      <div v-if="content.tags?.length" class="card-tags">
        <el-tag
          v-for="tag in content.tags.slice(0, 3)"
          :key="tag"
          size="small"
          effect="plain"
          class="tag-item"
          @click="(e: Event) => handleTagClick(e, tag)"
        >
          # {{ tag }}
        </el-tag>
        <span v-if="content.tags.length > 3" class="more-tags">
          +{{ content.tags.length - 3 }}
        </span>
      </div>

      <div class="card-footer">
        <div class="author-info" @click="handleAuthorClick">
          <el-avatar :size="28" :src="content.author.avatarUrl || undefined">
            {{ content.author.username.charAt(0).toUpperCase() }}
          </el-avatar>
          <span class="author-name">{{ content.author.username }}</span>
        </div>

        <div class="card-meta">
          <span class="view-count">
            <el-icon><View /></el-icon>
            {{ content.viewCount }}
          </span>
          <span class="comment-count">
            <el-icon><ChatLineRound /></el-icon>
            {{ content.commentCount || 0 }}
          </span>
          <span class="date">{{ formattedDate }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-card {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.content-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
  border-color: #d2e9fb;
}

.card-media {
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: var(--color-bg-tertiary);
  border-bottom: 1px solid var(--color-border-light);
}

.media-image,
.media-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 18px;
}

.card-header {
  margin-bottom: 14px;
}

.media-type-tag {
  border-radius: 999px;
}

:deep(.media-type-tag .el-tag__content) {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}

:deep(.media-type-tag .el-icon) {
  font-size: 13px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-body {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.author-info:hover .author-name {
  color: var(--color-primary);
}

.author-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.tag-item {
  cursor: pointer;
  border-radius: 999px;
  transition: all var(--transition-fast);
}

:deep(.tag-item.el-tag) {
  border-color: #d2e9fb;
  background: #eff8ff;
  color: #1167b1;
}

:deep(.tag-item .el-tag__content) {
  font-size: 12px;
  font-weight: 600;
  line-height: 1.2;
}

:deep(.tag-item:hover) {
  transform: translateY(-1px);
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

.more-tags {
  font-size: 12px;
  color: var(--color-text-muted);
}
</style>
