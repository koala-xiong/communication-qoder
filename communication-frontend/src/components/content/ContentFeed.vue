<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useContentStore } from '@/stores/content'
import ContentCard from './ContentCard.vue'

const contentStore = useContentStore()
const loadingMore = ref(false)

onMounted(async () => {
  await contentStore.fetchContents()
})

const handleLoadMore = async () => {
  if (loadingMore.value || !contentStore.pagination.hasMore) return
  loadingMore.value = true
  await contentStore.loadMore()
  loadingMore.value = false
}
</script>

<template>
  <div class="content-feed">
    <div class="feed-grid" v-if="contentStore.contents.length > 0">
      <ContentCard
        v-for="content in contentStore.contents"
        :key="content.id"
        :content="content"
      />
    </div>

    <div class="empty-state" v-else-if="!contentStore.loading">
      <el-empty description="No content yet. Be the first to share!" />
    </div>

    <div class="loading-state" v-if="contentStore.loading && contentStore.contents.length === 0">
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated />
    </div>

    <div class="load-more" v-if="contentStore.pagination.hasMore && contentStore.contents.length > 0">
      <el-button
        :loading="loadingMore"
        @click="handleLoadMore"
      >
        Load More
      </el-button>
    </div>

    <div class="end-message" v-else-if="contentStore.contents.length > 0">
      <span>You've reached the end</span>
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
