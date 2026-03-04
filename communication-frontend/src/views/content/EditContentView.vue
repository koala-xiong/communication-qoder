<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { useAuthStore } from '@/stores/auth'
import type { MediaType, ContentStatus } from '@/api/content'
import MediaUploader from '@/components/content/MediaUploader.vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const contentStore = useContentStore()
const authStore = useAuthStore()

const contentId = computed(() => Number(route.params.id))

const formRef = ref<FormInstance>()
const form = reactive({
  title: '',
  body: '',
  mediaUrl: '',
  mediaType: 'TEXT' as MediaType,
  status: 'PUBLISHED' as ContentStatus
})

const rules: FormRules = {
  title: [
    { required: true, message: 'Title is required', trigger: 'blur' },
    { max: 200, message: 'Title must be less than 200 characters', trigger: 'blur' }
  ]
}

onMounted(async () => {
  const content = await contentStore.fetchContentById(contentId.value)
  if (content) {
    if (content.author.username !== authStore.user?.username) {
      ElMessage.error('You can only edit your own content')
      router.push(`/content/${contentId.value}`)
      return
    }

    form.title = content.title
    form.body = content.body || ''
    form.mediaUrl = content.mediaUrl || ''
    form.mediaType = content.mediaType
    form.status = content.status
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
      const content = await contentStore.updateContent(contentId.value, {
        title: form.title,
        body: form.body || undefined,
        mediaUrl: form.mediaUrl || undefined,
        mediaType: form.mediaType,
        status: form.status
      })

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
        <h1>Edit Content</h1>
        <p>Update your content</p>
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
          <el-form-item label="Title" prop="title">
            <el-input
              v-model="form.title"
              placeholder="Enter a catchy title..."
              size="large"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Media">
            <MediaUploader
              :model-value="form.mediaUrl"
              :media-type="form.mediaType"
              @update:model-value="handleMediaUpdate"
              @update:media-type="handleMediaTypeUpdate"
            />
          </el-form-item>

          <el-form-item label="Content">
            <el-input
              v-model="form.body"
              type="textarea"
              placeholder="Write your content here..."
              :rows="8"
              maxlength="10000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Status">
            <el-radio-group v-model="form.status">
              <el-radio value="PUBLISHED">Published</el-radio>
              <el-radio value="DRAFT">Draft</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button @click="router.back()">Cancel</el-button>
              <el-button
                type="primary"
                native-type="submit"
                :loading="contentStore.loading"
              >
                Save Changes
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
