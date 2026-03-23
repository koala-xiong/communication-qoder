<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { useAuthStore } from '@/stores/auth'
import { View, Edit, Delete, ChatLineRound } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import CommentList from '@/components/comment/CommentList.vue'

const route = useRoute()
const router = useRouter()
const contentStore = useContentStore()
const authStore = useAuthStore()

const contentId = computed(() => Number(route.params.id))

onMounted(async () => {
  await contentStore.fetchContentById(contentId.value)
})

const content = computed(() => contentStore.currentContent)

const isAuthor = computed(() => {
  return authStore.user?.username === content.value?.author.username
})

const formattedDate = computed(() => {
  if (!content.value) return ''
  const date = new Date(content.value.createdAt)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
})

const handleEdit = () => {
  router.push(`/edit/${contentId.value}`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '删除后无法恢复，确定要删除这篇内容吗？',
      '删除内容',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const success = await contentStore.deleteContent(contentId.value)
    if (success) {
      router.push('/')
    }
  } catch {
    // User cancelled
  }
}

const goToAuthorProfile = () => {
  if (content.value) {
    router.push(`/profile/${content.value.author.username}`)
  }
}
</script>

<template>
  <div class="content-detail-page">
    <div class="container">
      <div class="loading-state" v-if="contentStore.loading">
        <el-skeleton :rows="10" animated />
      </div>

      <div class="content-wrapper" v-else-if="content">
        <div class="content-header">
          <h1 class="content-title">{{ content.title }}</h1>

          <div class="content-meta">
            <div class="author-info" @click="goToAuthorProfile">
              <el-avatar :size="40" :src="content.author.avatarUrl || undefined">
                {{ content.author.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="author-details">
                <span class="author-name">{{ content.author.username }}</span>
                <span class="publish-date">{{ formattedDate }}</span>
              </div>
            </div>

            <div class="content-stats">
              <span class="view-count">
                <el-icon><View /></el-icon>
                {{ content.viewCount }} 次浏览
              </span>
              <span class="comment-count">
                <el-icon><ChatLineRound /></el-icon>
                {{ content.commentCount || 0 }} 条评论
              </span>
            </div>
          </div>

          <div class="content-actions" v-if="isAuthor">
            <el-button :icon="Edit" @click="handleEdit">编辑</el-button>
            <el-button type="danger" :icon="Delete" @click="handleDelete">删除</el-button>
          </div>
        </div>

        <div class="content-media" v-if="content.mediaUrl">
          <img
            v-if="content.mediaType === 'IMAGE'"
            :src="content.mediaUrl"
            :alt="content.title"
            class="media-image"
          />
          <video
            v-else-if="content.mediaType === 'VIDEO'"
            :src="content.mediaUrl"
            controls
            class="media-video"
          />
        </div>

        <div class="content-body" v-if="content.body">
          <p>{{ content.body }}</p>
        </div>

        <!-- 评论区 -->
        <CommentList
          :content-id="contentId"
          :content-author-id="content.author.id"
        />
      </div>

      <div class="not-found" v-else>
        <el-empty description="内容不存在或已删除">
          <el-button type="primary" @click="router.push('/')">返回首页</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-detail-page {
  padding: 40px 0;
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.content-header {
  margin-bottom: 32px;
}

.content-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.3;
  margin-bottom: 24px;
}

.content-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.author-info:hover .author-name {
  color: var(--color-primary);
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: var(--color-text-primary);
  transition: color var(--transition-fast);
}

.publish-date {
  font-size: 14px;
  color: var(--color-text-muted);
}

.content-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-secondary);
}

.comment-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-secondary);
}

.content-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.content-media {
  margin-bottom: 32px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-bg-tertiary);
}

.media-image {
  width: 100%;
  max-height: 600px;
  object-fit: contain;
  display: block;
}

.media-video {
  width: 100%;
  max-height: 600px;
  display: block;
}

.content-body {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
}

.content-body p {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-secondary);
  white-space: pre-wrap;
}

.not-found {
  text-align: center;
  padding: 60px 20px;
}
</style>
