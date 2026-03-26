<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { categoryApi, type Category } from '@/api/category'
import type { Content } from '@/api/content'
import ContentFeed from '@/components/content/ContentFeed.vue'

const route = useRoute()
const categoryId = computed(() => Number(route.params.id))

const category = ref<Category | null>(null)
const categories = ref<Category[]>([])
const categoriesLoaded = ref(false)
const contents = ref<Content[]>([])
const loading = ref(false)
const page = ref(0)
const last = ref(true)

async function loadCategories() {
  const res = await categoryApi.list()
  categories.value = res.data.data
  category.value = categories.value.find((c) => c.id === categoryId.value) ?? null
  categoriesLoaded.value = true
}

async function loadContents(reset = false) {
  loading.value = true
  try {
    if (reset) {
      page.value = 0
      last.value = true
    }
    const res = await categoryApi.getContents(categoryId.value, page.value, 10)
    const data = res.data.data
    contents.value = reset ? data.content : [...contents.value, ...data.content]
    last.value = data.last
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadCategories()
  await loadContents(true)
})

watch(categoryId, async () => {
  await loadCategories()
  await loadContents(true)
})

watch(category, (c) => {
  if (c?.name) document.title = `${c.name} - Communication`
})

function loadMore() {
  if (last.value || loading.value) return
  page.value++
  loadContents()
}
</script>

<template>
  <div class="page container">
    <div v-if="!categoriesLoaded" class="loading-hint">加载中…</div>
    <template v-else-if="category">
      <div class="header">
        <h1>{{ category.name }}</h1>
        <p v-if="category.description" class="desc">{{ category.description }}</p>
      </div>
      <ContentFeed
        :contents="contents"
        :loading="loading && contents.length === 0"
        :is-last="last"
        @load-more="loadMore"
      />
    </template>
    <el-empty v-else description="分类不存在" />
  </div>
</template>

<style scoped>
.page {
  padding: 24px 20px 48px;
  max-width: 1200px;
  margin: 0 auto;
}
.header {
  margin-bottom: 24px;
}
h1 {
  margin: 0 0 8px;
  font-size: 24px;
}
.desc {
  margin: 0;
  color: var(--color-text-secondary);
}
.loading-hint {
  padding: 40px;
  text-align: center;
  color: var(--color-text-muted);
}
</style>
