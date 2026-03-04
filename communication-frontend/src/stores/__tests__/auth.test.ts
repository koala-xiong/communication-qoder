import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../auth'

// Mock element-plus
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

// Mock the API
vi.mock('@/api/auth', () => ({
  authApi: {
    register: vi.fn(),
    login: vi.fn(),
    getCurrentUser: vi.fn()
  }
}))

import { authApi } from '@/api/auth'

describe('Auth Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have null token and user when localStorage is empty', () => {
      const store = useAuthStore()
      expect(store.token).toBeNull()
      expect(store.user).toBeNull()
      expect(store.isAuthenticated).toBe(false)
    })

    it('should load token from localStorage', () => {
      localStorage.setItem('token', 'test-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, username: 'testuser' }))
      
      const store = useAuthStore()
      expect(store.token).toBe('test-token')
      expect(store.user?.username).toBe('testuser')
      expect(store.isAuthenticated).toBe(true)
    })
  })

  describe('register', () => {
    it('should register successfully and store token', async () => {
      const mockResponse = {
        data: {
          data: {
            token: 'new-token',
            user: { id: 1, username: 'newuser', email: 'new@test.com' }
          }
        }
      }
      vi.mocked(authApi.register).mockResolvedValue(mockResponse as any)

      const store = useAuthStore()
      const result = await store.register({
        username: 'newuser',
        email: 'new@test.com',
        password: 'password123'
      })

      expect(result).toBe(true)
      expect(store.token).toBe('new-token')
      expect(store.user?.username).toBe('newuser')
      expect(localStorage.getItem('token')).toBe('new-token')
    })

    it('should return false on register failure', async () => {
      vi.mocked(authApi.register).mockRejectedValue({
        response: { data: { message: 'Username already exists' } }
      })

      const store = useAuthStore()
      const result = await store.register({
        username: 'existinguser',
        email: 'existing@test.com',
        password: 'password123'
      })

      expect(result).toBe(false)
      expect(store.token).toBeNull()
    })
  })

  describe('login', () => {
    it('should login successfully and store token', async () => {
      const mockResponse = {
        data: {
          data: {
            token: 'login-token',
            user: { id: 1, username: 'testuser', email: 'test@test.com' }
          }
        }
      }
      vi.mocked(authApi.login).mockResolvedValue(mockResponse as any)

      const store = useAuthStore()
      const result = await store.login({
        usernameOrEmail: 'testuser',
        password: 'password123'
      })

      expect(result).toBe(true)
      expect(store.token).toBe('login-token')
      expect(store.user?.username).toBe('testuser')
    })

    it('should return false on login failure', async () => {
      vi.mocked(authApi.login).mockRejectedValue({
        response: { data: { message: 'Invalid credentials' } }
      })

      const store = useAuthStore()
      const result = await store.login({
        usernameOrEmail: 'wronguser',
        password: 'wrongpassword'
      })

      expect(result).toBe(false)
      expect(store.token).toBeNull()
    })
  })

  describe('logout', () => {
    it('should clear token and user on logout', () => {
      localStorage.setItem('token', 'test-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, username: 'testuser' }))

      const store = useAuthStore()
      store.logout()

      expect(store.token).toBeNull()
      expect(store.user).toBeNull()
      expect(store.isAuthenticated).toBe(false)
      expect(localStorage.getItem('token')).toBeNull()
      expect(localStorage.getItem('user')).toBeNull()
    })
  })

  describe('fetchCurrentUser', () => {
    it('should fetch and update current user', async () => {
      localStorage.setItem('token', 'test-token')
      
      const mockResponse = {
        data: {
          data: { id: 1, username: 'updateduser', email: 'updated@test.com' }
        }
      }
      vi.mocked(authApi.getCurrentUser).mockResolvedValue(mockResponse as any)

      const store = useAuthStore()
      await store.fetchCurrentUser()

      expect(store.user?.username).toBe('updateduser')
    })

    it('should logout on fetch failure', async () => {
      localStorage.setItem('token', 'expired-token')
      vi.mocked(authApi.getCurrentUser).mockRejectedValue(new Error('Unauthorized'))

      const store = useAuthStore()
      await store.fetchCurrentUser()

      expect(store.token).toBeNull()
      expect(store.user).toBeNull()
    })

    it('should not fetch if no token', async () => {
      const store = useAuthStore()
      await store.fetchCurrentUser()

      expect(authApi.getCurrentUser).not.toHaveBeenCalled()
    })
  })
})
