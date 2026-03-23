<template>
  <div class="profile-page">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <template v-else-if="user">
      <!-- 用户信息卡片 -->
      <div class="profile-header">
        <div class="profile-avatar">
          <el-avatar :size="120" :src="user.avatarUrl">
            {{ user.username.charAt(0).toUpperCase() }}
          </el-avatar>
        </div>
        <div class="profile-info">
          <h1 class="username">{{ user.username }}</h1>
          <p v-if="user.bio" class="bio">{{ user.bio }}</p>
          <p v-else class="bio empty">这个人很懒，什么都没写</p>
          
          <div class="stats">
            <div class="stat-item" @click="showFollowersModal = true">
              <span class="stat-value">{{ followerCount }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item" @click="showSubscriptionsModal = true">
              <span class="stat-value">{{ subscriptionCount }}</span>
              <span class="stat-label">关注</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ contentCount }}</span>
              <span class="stat-label">作品</span>
            </div>
          </div>
          
          <div v-if="!isOwnProfile" class="profile-actions">
            <el-button
              v-if="isSubscribed"
              @click="handleUnsubscribe"
              :loading="subscribing"
            >
              已关注
            </el-button>
            <el-button
              v-else
              type="primary"
              @click="handleSubscribe"
              :loading="subscribing"
            >
              关注
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 用户内容 -->
      <div class="profile-content">
        <h2>Ta 的作品</h2>
        <ContentFeed
          :contents="contents"
          :loading="contentsLoading"
          :is-last="isLast"
          @load-more="loadMoreContents"
        />
      </div>
    </template>
    
    <div v-else class="not-found">
      <el-empty description="用户不存在" />
    </div>
    
    <!-- 粉丝列表弹窗 -->
    <el-dialog v-model="showFollowersModal" title="粉丝列表" width="400px">
      <UserList
        :users="followers"
        :loading="followersLoading"
        :is-last="followersIsLast"
        empty-text="暂无粉丝"
        @load-more="loadMoreFollowers"
      />
    </el-dialog>
    
    <!-- 关注列表弹窗 -->
    <el-dialog v-model="showSubscriptionsModal" title="关注列表" width="400px">
      <UserList
        :users="subscriptions"
        :loading="subscriptionsLoading"
        :is-last="subscriptionsIsLast"
        empty-text="暂无关注"
        @load-more="loadMoreSubscriptions"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userApi, type UserProfile } from '@/api/user'
import { subscriptionApi, type User } from '@/api/subscription'
import { contentApi, type Content } from '@/api/content'
import ContentFeed from '@/components/content/ContentFeed.vue'
import UserList from '@/components/user/UserList.vue'

const route = useRoute()
const authStore = useAuthStore()

const user = ref<UserProfile | null>(null)
const loading = ref(true)
const isSubscribed = ref(false)
const subscribing = ref(false)
const followerCount = ref(0)
const subscriptionCount = ref(0)

// 内容相关
const contents = ref<Content[]>([])
const contentsLoading = ref(false)
const contentPage = ref(0)
/** 首屏加载完成前为 true，避免 ContentFeed 误显示「加载更多」 */
const isLast = ref(true)
const contentCount = ref(0)

// 粉丝列表
const showFollowersModal = ref(false)
const followers = ref<User[]>([])
const followersLoading = ref(false)
const followersPage = ref(0)
const followersIsLast = ref(false)

// 关注列表
const showSubscriptionsModal = ref(false)
const subscriptions = ref<User[]>([])
const subscriptionsLoading = ref(false)
const subscriptionsPage = ref(0)
const subscriptionsIsLast = ref(false)

const isOwnProfile = computed(() => {
  return authStore.user?.username === route.params.username
})

const fetchUser = async () => {
  loading.value = true
  try {
    const username = route.params.username as string
    user.value = await userApi.getUserByUsername(username)
    
    if (user.value) {
      // 获取订阅统计
      const counts = await subscriptionApi.getSubscriptionCount(user.value.id)
      followerCount.value = counts.followers
      subscriptionCount.value = counts.subscriptions
      
      // 检查是否已订阅
      if (authStore.isAuthenticated && !isOwnProfile.value) {
        isSubscribed.value = await subscriptionApi.checkSubscription(user.value.id)
      }
      
      // 获取用户内容
      await fetchContents(true)
    }
  } catch (error) {
    user.value = null
  } finally {
    loading.value = false
  }
}

const fetchContents = async (reset = false) => {
  if (!user.value) return
  
  if (reset) {
    contentPage.value = 0
    contents.value = []
    isLast.value = true
  }
  
  contentsLoading.value = true
  try {
    const response = await contentApi.getContentsByAuthor(
      user.value.id,
      contentPage.value
    )
    const pageData = response.data.data
    if (reset) {
      contents.value = pageData.content
    } else {
      contents.value.push(...pageData.content)
    }
    isLast.value = pageData.last
    contentCount.value = pageData.totalElements
  } catch (error) {
    ElMessage.error('加载内容失败')
  } finally {
    contentsLoading.value = false
  }
}

const loadMoreContents = () => {
  contentPage.value++
  fetchContents()
}

const handleSubscribe = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (!user.value) return
  
  subscribing.value = true
  try {
    await subscriptionApi.subscribe(user.value.id)
    isSubscribed.value = true
    followerCount.value++
    ElMessage.success('关注成功')
  } catch (error: any) {
    ElMessage.error(error.message || '关注失败')
  } finally {
    subscribing.value = false
  }
}

const handleUnsubscribe = async () => {
  if (!user.value) return
  
  subscribing.value = true
  try {
    await subscriptionApi.unsubscribe(user.value.id)
    isSubscribed.value = false
    followerCount.value--
    ElMessage.success('已取消关注')
  } catch (error: any) {
    ElMessage.error(error.message || '取消关注失败')
  } finally {
    subscribing.value = false
  }
}

const loadMoreFollowers = async () => {
  if (!user.value) return
  
  followersLoading.value = true
  try {
    const res = await subscriptionApi.getFollowers(user.value.id, followersPage.value)
    if (followersPage.value === 0) {
      followers.value = res.content
    } else {
      followers.value.push(...res.content)
    }
    followersIsLast.value = res.last
    followersPage.value++
  } catch (error) {
    ElMessage.error('加载粉丝列表失败')
  } finally {
    followersLoading.value = false
  }
}

const loadMoreSubscriptions = async () => {
  if (!user.value) return
  
  subscriptionsLoading.value = true
  try {
    // 需要通过用户名获取关注列表，这里简化处理
    const res = await subscriptionApi.getMySubscriptions(subscriptionsPage.value)
    if (subscriptionsPage.value === 0) {
      subscriptions.value = res.content
    } else {
      subscriptions.value.push(...res.content)
    }
    subscriptionsIsLast.value = res.last
    subscriptionsPage.value++
  } catch (error) {
    ElMessage.error('加载关注列表失败')
  } finally {
    subscriptionsLoading.value = false
  }
}

watch(() => showFollowersModal.value, (val) => {
  if (val && followers.value.length === 0) {
    followersPage.value = 0
    loadMoreFollowers()
  }
})

watch(() => showSubscriptionsModal.value, (val) => {
  if (val && subscriptions.value.length === 0) {
    subscriptionsPage.value = 0
    loadMoreSubscriptions()
  }
})

watch(() => route.params.username, () => {
  fetchUser()
})

onMounted(() => {
  fetchUser()
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.loading,
.not-found {
  padding: 60px 0;
}

.profile-header {
  display: flex;
  gap: 32px;
  padding: 32px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 32px;
}

.profile-avatar {
  flex-shrink: 0;
}

.profile-avatar :deep(.el-avatar) {
  font-size: 48px;
  font-weight: bold;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
}

.profile-info {
  flex: 1;
}

.username {
  margin: 0 0 8px;
  font-size: 28px;
  color: var(--el-text-color-primary);
}

.bio {
  margin: 0 0 16px;
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.bio.empty {
  color: var(--el-text-color-placeholder);
  font-style: italic;
}

.stats {
  display: flex;
  gap: 32px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: color 0.2s;
}

.stat-item:hover {
  color: var(--color-primary);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.profile-actions {
  margin-top: 16px;
}

.profile-content h2 {
  margin: 0 0 20px;
  font-size: 20px;
  color: var(--el-text-color-primary);
}

@media (max-width: 640px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .stats {
    justify-content: center;
  }
}
</style>
