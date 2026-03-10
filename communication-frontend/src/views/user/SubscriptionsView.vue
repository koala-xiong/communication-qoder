<template>
  <div class="subscriptions-page">
    <h1>我的关注</h1>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="订阅动态" name="feed">
        <div v-if="feedLoading && feed.length === 0" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="feed.length === 0" class="empty">
          <el-empty description="关注的用户暂无更新">
            <el-button type="primary" @click="$router.push('/')">
              发现更多内容
            </el-button>
          </el-empty>
        </div>
        <template v-else>
          <ContentFeed
            :contents="feed"
            :loading="feedLoading"
            :is-last="feedIsLast"
            @load-more="loadMoreFeed"
          />
        </template>
      </el-tab-pane>
      
      <el-tab-pane label="关注列表" name="following">
        <div v-if="followingLoading && following.length === 0" class="loading">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="following.length === 0" class="empty">
          <el-empty description="还没有关注任何人">
            <el-button type="primary" @click="$router.push('/')">
              发现创作者
            </el-button>
          </el-empty>
        </div>
        <template v-else>
          <div class="user-grid">
            <div v-for="user in following" :key="user.id" class="user-card">
              <router-link :to="`/profile/${user.username}`" class="user-link">
                <el-avatar :size="64" :src="user.avatarUrl">
                  {{ user.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <span class="username">{{ user.username }}</span>
              </router-link>
              <el-button
                size="small"
                @click="handleUnsubscribe(user.id)"
                :loading="unsubscribing === user.id"
              >
                取消关注
              </el-button>
            </div>
          </div>
          <div v-if="!followingIsLast" class="load-more">
            <el-button :loading="followingLoading" @click="loadMoreFollowing">
              加载更多
            </el-button>
          </div>
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { subscriptionApi, type User } from '@/api/subscription'
import { type Content } from '@/api/content'
import ContentFeed from '@/components/content/ContentFeed.vue'

const activeTab = ref('feed')

// 订阅动态
const feed = ref<Content[]>([])
const feedLoading = ref(false)
const feedPage = ref(0)
const feedIsLast = ref(false)

// 关注列表
const following = ref<User[]>([])
const followingLoading = ref(false)
const followingPage = ref(0)
const followingIsLast = ref(false)
const unsubscribing = ref<number | null>(null)

const loadFeed = async (reset = false) => {
  if (reset) {
    feedPage.value = 0
    feed.value = []
  }
  
  feedLoading.value = true
  try {
    const res = await subscriptionApi.getSubscriptionFeed(feedPage.value)
    if (res) {
      if (reset) {
        feed.value = res.content
      } else {
        feed.value.push(...res.content)
      }
      feedIsLast.value = res.last
    }
  } catch (error) {
    ElMessage.error('加载动态失败')
  } finally {
    feedLoading.value = false
  }
}

const loadMoreFeed = () => {
  feedPage.value++
  loadFeed()
}

const loadFollowing = async (reset = false) => {
  if (reset) {
    followingPage.value = 0
    following.value = []
  }
  
  followingLoading.value = true
  try {
    const res = await subscriptionApi.getMySubscriptions(followingPage.value)
    if (res) {
      if (reset) {
        following.value = res.content
      } else {
        following.value.push(...res.content)
      }
      followingIsLast.value = res.last
    }
  } catch (error) {
    ElMessage.error('加载关注列表失败')
  } finally {
    followingLoading.value = false
  }
}

const loadMoreFollowing = () => {
  followingPage.value++
  loadFollowing()
}

const handleUnsubscribe = async (userId: number) => {
  unsubscribing.value = userId
  try {
    const success = await subscriptionApi.unsubscribe(userId)
    if (success) {
      following.value = following.value.filter(u => u.id !== userId)
      ElMessage.success('已取消关注')
    } else {
      ElMessage.error('取消关注失败')
    }
  } catch (error) {
    ElMessage.error('取消关注失败')
  } finally {
    unsubscribing.value = null
  }
}

watch(activeTab, (tab) => {
  if (tab === 'feed' && feed.value.length === 0) {
    loadFeed(true)
  } else if (tab === 'following' && following.value.length === 0) {
    loadFollowing(true)
  }
})

onMounted(() => {
  loadFeed(true)
})
</script>

<style scoped>
.subscriptions-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.subscriptions-page h1 {
  margin: 0 0 24px;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.loading,
.empty {
  padding: 60px 0;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.user-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.username {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.load-more {
  text-align: center;
  padding: 24px 0;
}
</style>
