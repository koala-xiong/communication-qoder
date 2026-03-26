<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { trendingApi } from '@/api/trending'
import type { Content } from '@/api/content'
import ContentFeed from '@/components/content/ContentFeed.vue'

const contents = ref<Content[]>([])
const loading = ref(false)
const page = ref(0)
const last = ref(false)
const mode = ref<'all' | 'weekly'>('all')

async function load(reset = false) {
  loading.value = true
  try {
    if (reset) {
      page.value = 0
      last.value = false
    }
    const api = mode.value === 'weekly' ? trendingApi.weekly : trendingApi.list
    const res = await api(page.value, 10)
    const data = res.data.data
    contents.value = reset ? data.content : [...contents.value, ...data.content]
    last.value = data.last
  } finally {
    loading.value = false
  }
}

onMounted(() => load(true))

watch(mode, () => load(true))

function loadMore() {
  if (last.value || loading.value) return
  page.value++
  load()
}
</script>

<template>
  <div class="page container">
    <h1>热门排行</h1>
    <p class="sub">综合浏览、点赞与评论的热度排序</p>
    <el-radio-group v-model="mode" size="small">
      <el-radio-button value="all">全站热门</el-radio-button>
      <el-radio-button value="weekly">近 7 天</el-radio-button>
    </el-radio-group>
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
  margin: 0 0 8px;
  font-size: 24px;
}
.sub {
  margin: 0 0 16px;
  color: var(--color-text-secondary);
  font-size: 14px;
}
.el-radio-group {
  margin-bottom: 24px;
}
</style>
