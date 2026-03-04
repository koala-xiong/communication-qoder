import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type User, type RegisterRequest, type LoginRequest } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)

  const isAuthenticated = computed(() => !!token.value)

  async function register(data: RegisterRequest): Promise<boolean> {
    loading.value = true
    try {
      const response = await authApi.register(data)
      const authData = response.data.data

      token.value = authData.token
      user.value = authData.user

      localStorage.setItem('token', authData.token)
      localStorage.setItem('user', JSON.stringify(authData.user))

      ElMessage.success('Registration successful!')
      return true
    } catch (error: any) {
      const message = error.response?.data?.message || 'Registration failed'
      ElMessage.error(message)
      return false
    } finally {
      loading.value = false
    }
  }

  async function login(data: LoginRequest): Promise<boolean> {
    loading.value = true
    try {
      const response = await authApi.login(data)
      const authData = response.data.data

      token.value = authData.token
      user.value = authData.user

      localStorage.setItem('token', authData.token)
      localStorage.setItem('user', JSON.stringify(authData.user))

      ElMessage.success('Login successful!')
      return true
    } catch (error: any) {
      const message = error.response?.data?.message || 'Login failed'
      ElMessage.error(message)
      return false
    } finally {
      loading.value = false
    }
  }

  async function fetchCurrentUser(): Promise<void> {
    if (!token.value) return

    try {
      const response = await authApi.getCurrentUser()
      user.value = response.data.data
      localStorage.setItem('user', JSON.stringify(user.value))
    } catch {
      logout()
    }
  }

  function logout(): void {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('Logged out successfully')
  }

  function updateUser(newUser: User): void {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    register,
    login,
    fetchCurrentUser,
    logout,
    updateUser
  }
})
