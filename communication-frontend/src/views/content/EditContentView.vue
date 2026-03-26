<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { useAuthStore } from '@/stores/auth'
import type { MediaType, ContentStatus, UpdateContentRequest } from '@/api/content'
import { categoryApi, type Category } from '@/api/category'
import MediaUploader from '@/components/content/MediaUploader.vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const contentStore = useContentStore()
const authStore = useAuthStore()

const contentId = computed(() => Number(route.params.id))

const formRef = ref<FormInstance>()
const categories = ref<Category[]>([])
const originalCategoryId = ref<number | undefined>(undefined)

const form = reactive({
  title: '',
  body: '',
  mediaUrl: '',
  mediaType: 'TEXT' as MediaType,
  status: 'PUBLISHED' as ContentStatus,
  categoryId: undefined as number | undefined
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过 200 个字符', trigger: 'blur' }
  ]
}

onMounted(async () => {
  try {
    const cr = await categoryApi.list()
    categories.value = cr.data.data
  } catch {
    categories.value = []
  }

  const content = await contentStore.fetchContentById(contentId.value)
  if (content) {
    if (content.author.username !== authStore.user?.username) {
      ElMessage.error('只能编辑自己的内容')
      router.push(`/content/${contentId.value}`)
      return
    }

    form.title = content.title
    form.body = content.body || ''
    form.mediaUrl = content.mediaUrl || ''
    form.mediaType = content.mediaType
    form.status = content.status
    form.categoryId = content.categoryId ?? undefined
    originalCategoryId.value = content.categoryId ?? undefined
  }
})

const handleMediaUpdate = (url: string) => {
  form.mediaUrl = url
}

const handleMediaTypeUpdate = (type: MediaType) => {
  form.mediaType = type
}

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (valid) {
      const payload: UpdateContentRequest = {
        title: form.title,
        body: form.body || undefined,
        mediaUrl: form.mediaUrl || undefined,
        mediaType: form.mediaType,
        status: form.status
      }
      if (form.categoryId != null) {
        payload.categoryId = form.categoryId
      } else if (originalCategoryId.value != null) {
        payload.clearCategory = true
      }

      const content = await contentStore.updateContent(contentId.value, payload)

      if (content) {
        router.push(`/content/${content.id}`)
      }
    }
  })
}
</script>

<template>
  <div class="edit-content-page">
    <div class="container">
      <div class="page-header">
        <h1>编辑内容</h1>
        <p>修改并保存你的内容</p>
      </div>

      <div class="loading-state" v-if="contentStore.loading && !form.title">
        <el-skeleton :rows="8" animated />
      </div>

      <div class="content-form" v-else>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleSubmit(formRef)"
        >
          <el-form-item label="频道（可选）">
            <el-select
              v-model="form.categoryId"
              placeholder="选择频道"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="c in categories"
                :key="c.id"
                :label="c.name"
                :value="c.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="输入标题..."
              size="large"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="媒体">
            <MediaUploader
              :model-value="form.mediaUrl"
              :media-type="form.mediaType"
              @update:model-value="handleMediaUpdate"
              @update:media-type="handleMediaTypeUpdate"
            />
          </el-form-item>

          <el-form-item label="正文">
            <el-input
              v-model="form.body"
              type="textarea"
              placeholder="写下正文..."
              :rows="8"
              maxlength="10000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio value="PUBLISHED">已发布</el-radio>
              <el-radio value="DRAFT">草稿</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button @click="router.back()">取消</el-button>
              <el-button
                type="primary"
                native-type="submit"
                :loading="contentStore.loading"
              >
                保存修改
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.edit-content-page {
  padding: 40px 0;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.page-header p {
  color: var(--color-text-secondary);
}

.content-form {
  max-width: 800px;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  padding: 32px;
  box-shadow: var(--shadow-md);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}
</style>
