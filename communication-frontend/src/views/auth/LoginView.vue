<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const form = reactive({
  usernameOrEmail: '',
  password: ''
})

const rules: FormRules = {
  usernameOrEmail: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (valid) {
      const success = await authStore.login(form)
      if (success) {
        const redirect = route.query.redirect as string
        router.push(redirect || '/')
      }
    }
  })
}
</script>

<template>
  <div class="login-page">
    <div class="form-card">
      <h1 class="form-title">欢迎回来</h1>
      <p class="form-subtitle">登录以继续使用 Communication</p>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleSubmit(formRef)"
      >
        <el-form-item label="用户名或邮箱" prop="usernameOrEmail">
          <el-input
            v-model="form.usernameOrEmail"
            placeholder="请输入用户名或邮箱"
            size="large"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            size="large"
            :loading="authStore.loading"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="form-footer">
        <span>还没有账号？</span>
        <router-link to="/register">去注册</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--color-text-muted);
}

.form-footer a {
  margin-left: 8px;
  font-weight: 500;
}
</style>
