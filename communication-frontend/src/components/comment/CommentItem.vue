<template>
  <div class="comment-item">
    <div class="comment-avatar">
      <el-avatar :size="40" :src="comment.user.avatarUrl">
        {{ comment.user.username.charAt(0).toUpperCase() }}
      </el-avatar>
    </div>
    <div class="comment-content">
      <div class="comment-header">
        <router-link :to="`/profile/${comment.user.username}`" class="comment-author">
          {{ comment.user.username }}
        </router-link>
        <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
      </div>
      <div class="comment-body">{{ comment.body }}</div>
      <div class="comment-actions">
        <el-button text size="small" @click="showReply = !showReply">
          <el-icon><ChatLineRound /></el-icon>
          回复
        </el-button>
        <el-button
          v-if="canDelete"
          text
          size="small"
          type="danger"
          @click="$emit('delete', comment.id)"
        >
          <el-icon><Delete /></el-icon>
          删除
        </el-button>
      </div>
      
      <!-- 回复输入框 -->
      <div v-if="showReply" class="reply-input">
        <CommentInput
          :placeholder="`回复 @${comment.user.username}...`"
          submit-text="回复"
          show-cancel
          :loading="replyLoading"
          @submit="handleReply"
          @cancel="showReply = false"
        />
      </div>
      
      <!-- 回复列表 -->
      <div v-if="comment.replies?.length" class="replies">
        <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
          <div class="reply-avatar">
            <el-avatar :size="32" :src="reply.user.avatarUrl">
              {{ reply.user.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </div>
          <div class="reply-content">
            <div class="comment-header">
              <router-link :to="`/profile/${reply.user.username}`" class="comment-author">
                {{ reply.user.username }}
              </router-link>
              <span class="comment-time">{{ formatTime(reply.createdAt) }}</span>
            </div>
            <div class="comment-body">{{ reply.body }}</div>
            <div class="comment-actions">
              <el-button
                v-if="canDeleteReply(reply)"
                text
                size="small"
                type="danger"
                @click="$emit('delete', reply.id)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChatLineRound, Delete } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import CommentInput from './CommentInput.vue'
import type { Comment } from '@/api/comment'

const props = defineProps<{
  comment: Comment
  contentAuthorId?: number
}>()

const emit = defineEmits<{
  reply: [parentId: number, text: string]
  delete: [commentId: number]
}>()

const authStore = useAuthStore()
const showReply = ref(false)
const replyLoading = ref(false)

const canDelete = computed(() => {
  if (!authStore.user) return false
  return authStore.user.id === props.comment.user.id ||
         authStore.user.id === props.contentAuthorId
})

const canDeleteReply = (reply: Comment) => {
  if (!authStore.user) return false
  return authStore.user.id === reply.user.id ||
         authStore.user.id === props.contentAuthorId
}

const handleReply = async (text: string) => {
  replyLoading.value = true
  try {
    emit('reply', props.comment.id, text)
    showReply.value = false
  } finally {
    replyLoading.value = false
  }
}

const formatTime = (dateStr: string) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: var(--el-text-color-primary);
  text-decoration: none;
}

.comment-author:hover {
  color: var(--color-primary);
}

.comment-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.comment-body {
  line-height: 1.6;
  color: var(--el-text-color-regular);
  word-break: break-word;
}

.comment-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}

.reply-input {
  margin-top: 12px;
  padding: 12px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
}

.replies {
  margin-top: 16px;
  padding-left: 12px;
  border-left: 2px solid var(--el-border-color-lighter);
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
}

.reply-item:first-child {
  padding-top: 0;
}

.reply-content {
  flex: 1;
  min-width: 0;
}
</style>
