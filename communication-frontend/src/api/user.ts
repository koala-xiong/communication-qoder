import http from './http'

export interface UserProfile {
  id: number
  username: string
  email?: string
  avatarUrl?: string
  bio?: string
  createdAt?: string
}

export const userApi = {
  getUserByUsername(username: string) {
    return http.get<UserProfile>(`/users/${username}`)
  },

  getUserById(id: number) {
    return http.get<UserProfile>(`/users/id/${id}`)
  }
}
