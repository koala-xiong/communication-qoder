import http from './http'

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface User {
  id: number
  username: string
  email: string
  avatarUrl: string | null
  bio: string | null
  createdAt: string
}

export interface AuthResponse {
  token: string
  tokenType: string
  user: User
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

export const authApi = {
  register(data: RegisterRequest) {
    return http.post<ApiResponse<AuthResponse>>('/auth/register', data)
  },

  login(data: LoginRequest) {
    return http.post<ApiResponse<AuthResponse>>('/auth/login', data)
  },

  getCurrentUser() {
    return http.get<ApiResponse<User>>('/auth/me')
  }
}
