import http from './http'

export interface UserProfile {
  id: number
  username: string
  email?: string
  avatarUrl?: string
  bio?: string
  createdAt?: string
}

interface ApiResponse<T> {
  data: T
}

export const userApi = {
  async getUserByUsername(username: string): Promise<UserProfile | null> {
    try {
      const response = await http.get<ApiResponse<UserProfile>>(`/users/${username}`)
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async getUserById(id: number): Promise<UserProfile | null> {
    try {
      const response = await http.get<ApiResponse<UserProfile>>(`/users/${id}`)
      return response.data.data
    } catch (error) {
      return null
    }
  }
}
