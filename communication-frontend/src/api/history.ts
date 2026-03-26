import http from './http'
import type { ApiResponse } from './auth'
import type { Content, PageResponse } from './content'

export const historyApi = {
  list(page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/history`, {
      params: { page, size }
    })
  },

  clear() {
    return http.delete<ApiResponse<void>>(`/history`)
  }
}
