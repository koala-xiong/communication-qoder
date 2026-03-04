<template>
  <div class="dashboard-page">
    <div class="dashboard-header">
      <h1>控制台</h1>
      <p>查看你的创作数据和管理内容</p>
    </div>

    <!-- 数据概览卡片 -->
    <div v-if="statsLoading" class="stats-loading">
      <el-skeleton :rows="2" animated />
    </div>
    <div v-else class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon contents">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats?.totalContents || 0 }}</span>
          <span class="stat-label">总内容数</span>
        </div>
        <div class="stat-detail">
          <span>已发布 {{ stats?.publishedContents || 0 }}</span>
          <span>草稿 {{ stats?.draftContents || 0 }}</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon views">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ formatNumber(stats?.totalViews || 0) }}</span>
          <span class="stat-label">总浏览量</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon comments">
          <el-icon><ChatLineRound /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ formatNumber(stats?.totalComments || 0) }}</span>
          <span class="stat-label">总评论数</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon followers">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats?.followerCount || 0 }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-detail">
          <span>关注 {{ stats?.followingCount || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 功能标签页 -->
    <el-tabs v-model="activeTab" class="dashboard-tabs">
      <el-tab-pane label="内容管理" name="contents">
        <div class="tab-header">
          <el-select v-model="contentStatus" placeholder="状态筛选" clearable @change="loadContents(true)">
            <el-option label="全部" value="" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="草稿" value="DRAFT" />
          </el-select>
          <el-button type="primary" @click="$router.push('/create')">
            <el-icon><Plus /></el-icon>
            发布新内容
          </el-button>
        </div>

        <el-table :data="contents" v-loading="contentsLoading" stripe>
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <router-link :to="`/content/${row.id}`" class="content-link">
                {{ row.title }}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
                {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="commentCount" label="评论" width="80" />
          <el-table-column prop="createdAt" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button text size="small" @click="$router.push(`/edit/${row.id}`)">
                编辑
              </el-button>
              <el-button text size="small" type="danger" @click="handleDelete(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="!contentsIsLast" class="load-more">
          <el-button :loading="contentsLoading" @click="loadMoreContents">
            加载更多
          </el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="个人资料" name="profile">
        <div class="profile-form">
          <div class="avatar-section">
            <el-avatar :size="100" :src="authStore.user?.avatarUrl">
              {{ authStore.user?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="avatar-upload">
              <input
                type="file"
                ref="avatarInput"
                accept="image/*"
                @change="handleAvatarChange"
                hidden
              />
              <el-button @click="($refs.avatarInput as HTMLInputElement).click()" :loading="avatarUploading">
                更换头像
              </el-button>
            </div>
          </div>

          <el-form :model="profileForm" label-position="top" class="profile-edit-form">
            <el-form-item label="用户名">
              <el-input :value="authStore.user?.username" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input :value="authStore.user?.email" disabled />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input
                v-model="profileForm.bio"
                type="textarea"
                :rows="4"
                placeholder="介绍一下自己..."
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveProfile" :loading="profileSaving">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, View, ChatLineRound, User, Plus } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useContentStore } from '@/stores/content'
import { dashboardApi, type DashboardStats } from '@/api/dashboard'
import type { Content, ContentStatus } from '@/api/content'

const authStore = useAuthStore()
const contentStore = useContentStore()

const activeTab = ref('contents')
const statsLoading = ref(false)
const stats = ref<DashboardStats | null>(null)

// 内容管理
const contents = ref<Content[]>([])
const contentsLoading = ref(false)
const contentStatus = ref<ContentStatus | ''>('')
const contentsPage = ref(0)
const contentsIsLast = ref(false)

// 个人资料
const profileForm = reactive({
  bio: authStore.user?.bio || ''
})
const profileSaving = ref(false)
const avatarUploading = ref(false)

const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadStats = async () => {
  statsLoading.value = true
  try {
    stats.value = await dashboardApi.getStats()
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  } finally {
    statsLoading.value = false
  }
}

const loadContents = async (reset = false) => {
  if (reset) {
    contentsPage.value = 0
    contents.value = []
  }

  contentsLoading.value = true
  try {
    const status = contentStatus.value || undefined
    const res = await dashboardApi.getMyContents(status as ContentStatus | undefined, contentsPage.value)
    if (reset) {
      contents.value = res.content
    } else {
      contents.value.push(...res.content)
    }
    contentsIsLast.value = res.last
  } catch (error) {
    ElMessage.error('加载内容列表失败')
  } finally {
    contentsLoading.value = false
  }
}

const loadMoreContents = () => {
  contentsPage.value++
  loadContents()
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇内容吗？此操作不可撤销。', '删除确认', {
      type: 'warning'
    })

    await contentStore.deleteContent(id)
    contents.value = contents.value.filter(c => c.id !== id)
    loadStats() // 刷新统计
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAvatarChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  avatarUploading.value = true
  try {
    const user = await dashboardApi.uploadAvatar(file)
    authStore.updateUser(user)
    ElMessage.success('头像更新成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarUploading.value = false
    input.value = ''
  }
}

const handleSaveProfile = async () => {
  profileSaving.value = true
  try {
    const user = await dashboardApi.updateProfile({ bio: profileForm.bio })
    authStore.updateUser(user)
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    profileSaving.value = false
  }
}

onMounted(() => {
  loadStats()
  loadContents(true)
})
</script>

<style scoped>
.dashboard-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.dashboard-header {
  margin-bottom: 32px;
}

.dashboard-header h1 {
  margin: 0 0 8px;
  font-size: 28px;
  color: var(--el-text-color-primary);
}

.dashboard-header p {
  margin: 0;
  color: var(--el-text-color-secondary);
}

.stats-loading {
  margin-bottom: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.contents {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-icon.views {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.stat-icon.comments {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-icon.followers {
  background: rgba(255, 107, 53, 0.1);
  color: var(--color-primary);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.stat-detail {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.dashboard-tabs {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content-link {
  color: var(--el-text-color-primary);
  text-decoration: none;
}

.content-link:hover {
  color: var(--color-primary);
}

.load-more {
  text-align: center;
  padding: 20px 0;
}

.profile-form {
  max-width: 500px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
}

.avatar-section :deep(.el-avatar) {
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  font-size: 36px;
}

.profile-edit-form {
  margin-top: 24px;
}
</style>
