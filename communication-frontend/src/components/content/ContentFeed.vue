<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useContentStore } from '@/stores/content'
import { useAuthStore } from '@/stores/auth'
import ContentCard from './ContentCard.vue'
import type { Content } from '@/api/content'
import { likeApi } from '@/api/like'
import { favoriteApi } from '@/api/favorite'

const props = defineProps<{
  /** 传入时由父组件提供数据（如个人主页「Ta 的作品」），不再使用首页全局 feed */
  contents?: Content[]
  loading?: boolean
  isLast?: boolean
}>()

const emit = defineEmits<{
  'load-more': []
}>()

const contentStore = useContentStore()
const authStore = useAuthStore()
const loadingMore = ref(false)

/** 未传 contents 时为首页 Latest Content */
const useStoreFeed = computed(() => props.contents === undefined)

const displayContents = computed(() =>
  useStoreFeed.value ? contentStore.contents : props.contents!
)

const likeMap = ref<Record<number, boolean>>({})
const favMap = ref<Record<number, boolean>>({})

async function syncInteractionMaps() {
  if (!authStore.isAuthenticated || displayContents.value.length === 0) {
    likeMap.value = {}
    favMap.value = {}
    return
  }
  const ids = displayContents.value.map((c) => c.id)
  try {
    const [lr, fr] = await Promise.all([
      likeApi.batchCheck(ids),
      favoriteApi.batchCheck(ids)
    ])
    const lm: Record<number, boolean> = {}
    Object.entries(lr.data.data ?? {}).forEach(([k, v]) => {
      lm[Number(k)] = !!v
    })
    const fm: Record<number, boolean> = {}
    Object.entries(fr.data.data ?? {}).forEach(([k, v]) => {
      fm[Number(k)] = !!v
    })
    likeMap.value = lm
    favMap.value = fm
  } catch {
    likeMap.value = {}
    favMap.value = {}
  }
}

function patchContentLikeCount(contentId: number, delta: number) {
  if (useStoreFeed.value) {
    const list = contentStore.contents
    const i = list.findIndex((c) => c.id === contentId)
    if (i !== -1) {
      const c = list[i]!
      const next = (c.likeCount ?? 0) + delta
      list[i] = { ...c, likeCount: Math.max(0, next) }
    }
  }
}

async function handleToggleLike(contentId: number) {
  const was = likeMap.value[contentId]
  const res = await likeApi.toggle(contentId)
  const liked = res.data.data.liked
  likeMap.value = { ...likeMap.value, [contentId]: liked }
  if (was !== liked) {
    patchContentLikeCount(contentId, liked ? 1 : -1)
  }
}

async function handleToggleFavorite(contentId: number) {
  const res = await favoriteApi.toggle(contentId)
  favMap.value = { ...favMap.value, [contentId]: res.data.data.favorited }
}

const displayLoading = computed(() =>
  useStoreFeed.value ? contentStore.loading : (props.loading ?? false)
)

const showLoadMore = computed(() => {
  if (useStoreFeed.value) {
    return contentStore.pagination.hasMore && contentStore.contents.length > 0
  }
  if (props.loading) return false
  return props.isLast === false
})

const showEndMessage = computed(() => {
  if (useStoreFeed.value) {
    return (
      contentStore.contents.length > 0 && !contentStore.pagination.hasMore
    )
  }
  return props.isLast === true && (props.contents?.length ?? 0) > 0
})

onMounted(async () => {
  if (useStoreFeed.value) {
    await contentStore.fetchContents()
  }
  await syncInteractionMaps()
})

watch(
  () => [displayContents.value, authStore.isAuthenticated] as const,
  () => {
    syncInteractionMaps()
  },
  { deep: true }
)

const handleLoadMore = async () => {
  if (useStoreFeed.value) {
    if (loadingMore.value || !contentStore.pagination.hasMore) return
    loadingMore.value = true
    await contentStore.loadMore()
    loadingMore.value = false
  } else {
    emit('load-more')
  }
}
</script>

<template>
  <div class="content-feed">
    <div class="feed-grid" v-if="displayContents.length > 0">
      <ContentCard
        v-for="content in displayContents"
        :key="content.id"
        :content="content"
        :liked="authStore.isAuthenticated ? !!likeMap[content.id] : false"
        :favorited="authStore.isAuthenticated ? !!favMap[content.id] : false"
        :interactive="authStore.isAuthenticated"
        @toggle-like="handleToggleLike"
        @toggle-favorite="handleToggleFavorite"
      />
    </div>

    <div class="empty-state" v-else-if="!displayLoading">
      <el-empty
        :description="
          useStoreFeed
            ? '暂无内容，快来发布第一条吧'
            : '暂无作品'
        "
      />
    </div>

    <div
      class="loading-state"
      v-if="displayLoading && displayContents.length === 0"
    >
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated />
    </div>

    <div class="load-more" v-if="showLoadMore">
      <el-button
        :loading="useStoreFeed ? loadingMore : loading"
        @click="handleLoadMore"
      >
        加载更多
      </el-button>
    </div>

    <div class="end-message" v-else-if="showEndMessage">
      <span>已经到底啦</span>
    </div>
  </div>
</template>

<style scoped>
.content-feed {
  width: 100%;
}

.feed-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
}

.loading-state {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.end-message {
  text-align: center;
  margin-top: 32px;
  color: var(--color-text-muted);
  font-size: 14px;
}
</style>
