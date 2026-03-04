import http from './http'
import type { Content, PageResponse } from './content'

export interface User {
  id: number
  username: string
  avatarUrl?: string
  bio?: string
}

export const searchApi = {
  searchContents(q?: string, tag?: string, page = 0, size = 10) {
    return http.get<PageResponse<Content>>('/search/contents', {
      params: { q, tag, page, size }
    })
  },

  searchUsers(q: string, page = 0, size = 10) {
    return http.get<PageResponse<User>>('/search/users', {
      params: { q, page, size }
    })
  },

  getPopularTags(limit = 20) {
    return http.get<string[]>('/search/tags/popular', {
      params: { limit }
    })
  },

  suggestTags(q: string) {
    return http.get<string[]>('/search/tags/suggest', {
      params: { q }
    })
  }
}
