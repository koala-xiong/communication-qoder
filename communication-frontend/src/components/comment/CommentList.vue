<template>
  <div class="comment-list">
    <div class="comment-header">
      <h3>评论 ({{ totalComments }})</h3>
    </div>
    
    <!-- 发表评论 -->
    <div v-if="authStore.isAuthenticated" class="comment-form">
      <CommentInput
        :loading="submitting"
        @submit="handleSubmit"
      />
    </div>
    <div v-else class="login-prompt">
      <router-link to="/login">登录</router-link> 后发表评论
    </div>
    
    <!-- 评论列表 -->
    <div v-if="loading && comments.length === 0" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="comments.length === 0" class="empty">
      <el-empty description="暂无评论，来发表第一条评论吧" :image-size="100" />
    </div>
    
    <div v-else class="comments">
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :content-author-id="contentAuthorId"
        @reply="handleReply"
        @delete="handleDelete"
      />
      
      <!-- 加载更多 -->
      <div v-if="!isLast" class="load-more">
        <el-button :loading="loading" @click="loadMore">
          加载更多评论
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { commentApi, type Comment } from '@/api/comment'
import CommentInput from './CommentInput.vue'
import CommentItem from './CommentItem.vue'

const props = defineProps<{
  contentId: number
  contentAuthorId?: number
}>()

const authStore = useAuthStore()
const comments = ref<Comment[]>([])
const loading = ref(false)
const submitting = ref(false)
const page = ref(0)
const isLast = ref(false)
const totalComments = ref(0)

const fetchComments = async (reset = false) => {
  if (reset) {
    page.value = 0
    comments.value = []
  }
  
  loading.value = true
  try {
    const res = await commentApi.getComments(props.contentId, page.value)
    if (reset) {
      comments.value = res.content
    } else {
      comments.value.push(...res.content)
    }
    isLast.value = res.last
    totalComments.value = res.totalElements
  } catch (error) {
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  page.value++
  fetchComments()
}

const handleSubmit = async (text: string) => {
  submitting.value = true
  try {
    const newComment = await commentApi.createComment(props.contentId, { body: text })
    comments.value.unshift(newComment)
    totalComments.value++
    ElMessage.success('评论发表成功')
  } catch (error) {
    ElMessage.error('评论发表失败')
  } finally {
    submitting.value = false
  }
}

const handleReply = async (parentId: number, text: string) => {
  try {
    const reply = await commentApi.createComment(props.contentId, { body: text, parentId })
    const parent = comments.value.find(c => c.id === parentId)
    if (parent) {
      if (!parent.replies) parent.replies = []
      parent.replies.push(reply)
    }
    totalComments.value++
    ElMessage.success('回复成功')
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

const handleDelete = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '删除确认', {
      type: 'warning'
    })
    
    await commentApi.deleteComment(props.contentId, commentId)
    
    // 从列表中移除
    const index = comments.value.findIndex(c => c.id === commentId)
    if (index !== -1) {
      const deletedCount = 1 + (comments.value[index].replies?.length || 0)
      comments.value.splice(index, 1)
      totalComments.value -= deletedCount
    } else {
      // 可能是回复
      for (const comment of comments.value) {
        if (comment.replies) {
          const replyIndex = comment.replies.findIndex(r => r.id === commentId)
          if (replyIndex !== -1) {
            comment.replies.splice(replyIndex, 1)
            totalComments.value--
            break
          }
        }
      }
    }
    
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchComments(true)
})
</script>

<style scoped>
.comment-list {
  margin-top: 32px;
}

.comment-header h3 {
  margin: 0 0 20px;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.comment-form {
  margin-bottom: 24px;
  padding: 16px;
  background: var(--el-fill-color-lighter);
  border-radius: 12px;
}

.login-prompt {
  margin-bottom: 24px;
  padding: 20px;
  text-align: center;
  background: var(--el-fill-color-lighter);
  border-radius: 12px;
  color: var(--el-text-color-secondary);
}

.login-prompt a {
  color: var(--color-primary);
  font-weight: 600;
}

.loading,
.empty {
  padding: 40px 0;
}

.load-more {
  text-align: center;
  padding: 20px 0;
}
</style>
