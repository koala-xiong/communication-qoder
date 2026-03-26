<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { notificationApi, type AppNotification } from '@/api/notification'
import { useNotificationStore } from '@/stores/notifications'

const router = useRouter()
const notificationStore = useNotificationStore()

const list = ref<AppNotification[]>([])
const loading = ref(false)
const page = ref(0)
const totalPages = ref(1)
const size = 20

async function load(reset = false) {
  loading.value = true
  try {
    if (reset) page.value = 0
    const res = await notificationApi.list(page.value, size)
    const data = res.data.data
    list.value = reset ? data.content : [...list.value, ...data.content]
    totalPages.value = data.totalPages
    await notificationStore.fetchUnreadCount()
  } finally {
    loading.value = false
  }
}

onMounted(() => load(true))

function typeLabel(t: string) {
  switch (t) {
    case 'LIKE':
      return '点赞'
    case 'COMMENT':
      return '评论'
    case 'FOLLOW':
      return '关注'
    default:
      return '系统'
  }
}

async function openItem(n: AppNotification) {
  if (!n.isRead) {
    await notificationApi.markRead(n.id)
    n.isRead = true
    await notificationStore.fetchUnreadCount()
  }
  if (n.contentId) router.push(`/content/${n.contentId}`)
  else if (n.type === 'FOLLOW') router.push(`/profile/${n.sender.username}`)
}

async function markAll() {
  await notificationApi.markAllRead()
  list.value = list.value.map((x) => ({ ...x, isRead: true }))
  notificationStore.setUnread(0)
}

function loadMore() {
  if (loading.value || page.value + 1 >= totalPages.value) return
  page.value++
  load(false)
}
</script>

<template>
  <div class="page container">
    <div class="toolbar">
      <h1>通知</h1>
      <el-button v-if="list.length" text type="primary" @click="markAll">全部已读</el-button>
    </div>
    <el-skeleton v-if="loading && !list.length" :rows="6" animated />
    <el-empty v-else-if="!list.length" description="暂无通知" />
    <div v-else class="list">
      <div
        v-for="n in list"
        :key="n.id"
        class="item"
        :class="{ unread: !n.isRead }"
        @click="openItem(n)"
      >
        <div class="row">
          <el-tag size="small">{{ typeLabel(n.type) }}</el-tag>
          <span class="time">{{ new Date(n.createdAt).toLocaleString() }}</span>
        </div>
        <p class="msg">{{ n.message }}</p>
      </div>
      <div class="more" v-if="page + 1 < totalPages">
        <el-button :loading="loading" @click="loadMore">加载更多</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 24px 20px 48px;
  max-width: 720px;
  margin: 0 auto;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar h1 {
  margin: 0;
  font-size: 24px;
}
.item {
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  background: var(--color-bg-secondary);
}
.item.unread {
  border-color: #b3d8ff;
  background: #f0f9ff;
}
.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.time {
  font-size: 12px;
  color: var(--color-text-muted);
}
.msg {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}
.more {
  text-align: center;
  margin-top: 16px;
}
</style>
