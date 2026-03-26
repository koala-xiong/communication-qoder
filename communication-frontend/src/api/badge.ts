import http from './http'
import type { ApiResponse } from './auth'

export type BadgeType =
  | 'NEWCOMER'
  | 'ACTIVE_WRITER'
  | 'POPULAR_AUTHOR'
  | 'TOP_COMMENTER'
  | 'LOYAL_READER'
  | 'EARLY_ADOPTER'

export interface Badge {
  badgeType: BadgeType
  displayName: string
  description: string
  earnedAt: string
}

export const badgeApi = {
  getByUser(userId: number) {
    return http.get<ApiResponse<Badge[]>>(`/badges/user/${userId}`)
  }
}
