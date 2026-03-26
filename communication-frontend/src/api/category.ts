import http from './http'
import type { ApiResponse } from './auth'
import type { Content, PageResponse } from './content'

export interface Category {
  id: number
  name: string
  description: string | null
  icon: string | null
  sortOrder: number
}

export const categoryApi = {
  list() {
    return http.get<ApiResponse<Category[]>>(`/categories`)
  },

  getContents(categoryId: number, page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/categories/${categoryId}/contents`, {
      params: { page, size }
    })
  }
}
