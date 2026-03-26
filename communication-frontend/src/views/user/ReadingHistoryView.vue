<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { historyApi } from '@/api/history'
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
    const res = await historyApi.list(page.value, 10)
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

async function clearAll() {
  try {
    await ElMessageBox.confirm('确定清空全部阅读历史？', '确认', { type: 'warning' })
    await historyApi.clear()
    contents.value = []
    last.value = true
    ElMessage.success('已清空')
  } catch {
    /* cancel */
  }
}
</script>

<template>
  <div class="page container">
    <div class="head">
      <h1>阅读历史</h1>
      <el-button v-if="contents.length" type="danger" text @click="clearAll">清空历史</el-button>
    </div>
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
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
h1 {
  margin: 0;
  font-size: 24px;
}
</style>
