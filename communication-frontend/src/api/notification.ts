import http from './http'
import type { ApiResponse } from './auth'
import type { PageResponse } from './content'
import type { Author } from './content'

export type NotificationType = 'LIKE' | 'COMMENT' | 'FOLLOW' | 'SYSTEM'

export interface AppNotification {
  id: number
  sender: Author
  type: NotificationType
  contentId: number | null
  contentTitle: string | null
  commentId: number | null
  message: string | null
  isRead: boolean
  createdAt: string
}

export const notificationApi = {
  list(page = 0, size = 20) {
    return http.get<ApiResponse<PageResponse<AppNotification>>>(`/notifications`, {
      params: { page, size }
    })
  },

  unreadCount() {
    return http.get<ApiResponse<number>>(`/notifications/unread-count`)
  },

  markRead(id: number) {
    return http.put<ApiResponse<void>>(`/notifications/${id}/read`)
  },

  markAllRead() {
    return http.put<ApiResponse<void>>(`/notifications/read-all`)
  }
}
