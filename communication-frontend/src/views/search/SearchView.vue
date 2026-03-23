<template>
  <div class="search-page">
    <div class="search-header">
      <el-input
        v-model="searchQuery"
        size="large"
        placeholder="搜索内容或用户..."
        :prefix-icon="Search"
        clearable
        @input="debouncedSearch"
        @keyup.enter="handleSearch"
      />
    </div>

    <!-- 热门标签 -->
    <div v-if="!searchQuery && popularTags.length" class="popular-tags">
      <h3>热门标签</h3>
      <div class="tags-list">
        <el-tag
          v-for="tag in popularTags"
          :key="tag"
          class="tag-item"
          effect="plain"
          @click="searchByTag(tag)"
        >
          # {{ tag }}
        </el-tag>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div v-if="searchQuery || route.query.tag" class="search-results">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="内容" name="contents">
          <div v-if="contentsLoading && contents.length === 0" class="loading">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else-if="contents.length === 0" class="empty">
            <el-empty description="未找到相关内容" />
          </div>
          <template v-else>
            <ContentFeed
              :contents="contents"
              :loading="contentsLoading"
              :is-last="contentsIsLast"
              @load-more="loadMoreContents"
            />
          </template>
        </el-tab-pane>

        <el-tab-pane label="用户" name="users">
          <div v-if="usersLoading && users.length === 0" class="loading">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="users.length === 0" class="empty">
            <el-empty description="未找到相关用户" />
          </div>
          <template v-else>
            <div class="users-list">
              <div v-for="user in users" :key="user.id" class="user-card">
                <router-link :to="`/profile/${user.username}`" class="user-link">
                  <el-avatar :size="56" :src="user.avatarUrl">
                    {{ user.username.charAt(0).toUpperCase() }}
                  </el-avatar>
                  <div class="user-info">
                    <span class="username">{{ user.username }}</span>
                    <span v-if="user.bio" class="bio">{{ user.bio }}</span>
                  </div>
                </router-link>
              </div>
            </div>
            <div v-if="!usersIsLast" class="load-more">
              <el-button :loading="usersLoading" @click="loadMoreUsers">
                加载更多
              </el-button>
            </div>
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 无搜索时显示推荐内容 -->
    <div v-if="!searchQuery && !route.query.tag" class="recommended">
      <h3>推荐内容</h3>
      <ContentFeed
        :contents="contents"
        :loading="contentsLoading"
        :is-last="contentsIsLast"
        @load-more="loadMoreContents"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { searchApi } from '@/api/search'
import type { Content } from '@/api/content'
import type { User } from '@/api/search'
import ContentFeed from '@/components/content/ContentFeed.vue'

const route = useRoute()
const router = useRouter()

const searchQuery = ref('')
const activeTab = ref('contents')
const popularTags = ref<string[]>([])

// 内容搜索结果
const contents = ref<Content[]>([])
const contentsLoading = ref(false)
const contentsPage = ref(0)
const contentsIsLast = ref(false)

// 用户搜索结果
const users = ref<User[]>([])
const usersLoading = ref(false)
const usersPage = ref(0)
const usersIsLast = ref(false)

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const debouncedSearch = () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

const handleSearch = () => {
  router.push({ query: { q: searchQuery.value || undefined } })
  searchContents(true)
  if (activeTab.value === 'users') {
    searchUsers(true)
  }
}

const handleTabChange = (tab: string | number) => {
  if (tab === 'users' && users.value.length === 0 && searchQuery.value) {
    searchUsers(true)
  }
}

const searchContents = async (reset = false) => {
  if (reset) {
    contentsPage.value = 0
    contents.value = []
  }

  contentsLoading.value = true
  try {
    const tag = route.query.tag as string | undefined
    const res = await searchApi.searchContents(searchQuery.value || undefined, tag, contentsPage.value)
    if (res) {
      if (reset) {
        contents.value = res.content
      } else {
        contents.value.push(...res.content)
      }
      contentsIsLast.value = res.last
    }
  } catch (error) {
    console.error('Search contents error:', error)
  } finally {
    contentsLoading.value = false
  }
}

const loadMoreContents = () => {
  contentsPage.value++
  searchContents()
}

const searchUsers = async (reset = false) => {
  if (!searchQuery.value) return

  if (reset) {
    usersPage.value = 0
    users.value = []
  }

  usersLoading.value = true
  try {
    const res = await searchApi.searchUsers(searchQuery.value, usersPage.value)
    if (res) {
      if (reset) {
        users.value = res.content
      } else {
        users.value.push(...res.content)
      }
      usersIsLast.value = res.last
    }
  } catch (error) {
    console.error('Search users error:', error)
  } finally {
    usersLoading.value = false
  }
}

const loadMoreUsers = () => {
  usersPage.value++
  searchUsers()
}

const searchByTag = (tag: string) => {
  router.push({ path: '/search', query: { tag } })
}

const loadPopularTags = async () => {
  try {
    popularTags.value = await searchApi.getPopularTags(20)
  } catch (error) {
    console.error('Load popular tags error:', error)
    popularTags.value = []
  }
}

watch(() => route.query, (query) => {
  if (query.q) {
    searchQuery.value = query.q as string
  }
  if (query.tag || query.q) {
    searchContents(true)
  }
}, { immediate: true })

onMounted(() => {
  loadPopularTags()
  if (!route.query.q && !route.query.tag) {
    searchContents(true)
  }
})
</script>

<style scoped>
.search-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.search-header {
  margin-bottom: 24px;
}

.search-header :deep(.el-input__wrapper) {
  border-radius: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
}

.popular-tags {
  margin-bottom: 32px;
}

.popular-tags h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.2s;
}

.tag-item:hover {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}

.search-results,
.recommended {
  margin-top: 24px;
}

.recommended h3 {
  margin: 0 0 20px;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.loading,
.empty {
  padding: 40px 0;
}

.users-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-card {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 16px;
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s;
}

.user-card:hover {
  box-shadow: var(--shadow-md);
}

.user-link {
  display: flex;
  align-items: center;
  gap: 16px;
  text-decoration: none;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.bio {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.load-more {
  text-align: center;
  padding: 24px 0;
}
</style>
