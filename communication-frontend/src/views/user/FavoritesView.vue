<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { favoriteApi } from '@/api/favorite'
import type { Content } from '@/api/content'
import ContentFeed from '@/components/content/ContentFeed.vue'

const contents = ref<Content[]>([])
const loading = ref(false)
const page = ref(0)
const last = ref(false)

async function load(reset = false) {
  loading.value = true
  try {
    if (reset) {
      page.value = 0
      last.value = false
    }
    const res = await favoriteApi.getMyFavorites(page.value, 10)
    const data = res.data.data
    contents.value = reset ? data.content : [...contents.value, ...data.content]
    last.value = data.last
  } finally {
    loading.value = false
  }
}

onMounted(() => load(true))

function loadMore() {
  if (last.value || loading.value) return
  page.value++
  load()
}
</script>

<template>
  <div class="page container">
    <h1>我的收藏</h1>
    <ContentFeed
      :contents="contents"
      :loading="loading && contents.length === 0"
      :is-last="last"
      @load-more="loadMore"
    />
  </div>
</template>

<style scoped>
.page {
  padding: 24px 20px 48px;
  max-width: 1200px;
  margin: 0 auto;
}
h1 {
  margin: 0 0 24px;
  font-size: 24px;
}
</style>
