import { defineStore } from 'pinia'
import { ref } from 'vue'
import { notificationApi } from '@/api/notification'

export const useNotificationStore = defineStore('notifications', () => {
  const unreadCount = ref(0)

  async function fetchUnreadCount() {
    try {
      const res = await notificationApi.unreadCount()
      unreadCount.value = res.data.data ?? 0
    } catch {
      unreadCount.value = 0
    }
  }

  function setUnread(n: number) {
    unreadCount.value = n
  }

  return { unreadCount, fetchUnreadCount, setUnread }
})
