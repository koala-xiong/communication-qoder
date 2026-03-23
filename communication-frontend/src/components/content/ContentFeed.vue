<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useContentStore } from '@/stores/content'
import ContentCard from './ContentCard.vue'
import type { Content } from '@/api/content'

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
const loadingMore = ref(false)

/** 未传 contents 时为首页 Latest Content */
const useStoreFeed = computed(() => props.contents === undefined)

const displayContents = computed(() =>
  useStoreFeed.value ? contentStore.contents : props.contents!
)

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
})

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
