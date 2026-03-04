<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useContentStore } from '@/stores/content'
import type { MediaType, ContentStatus } from '@/api/content'
import MediaUploader from '@/components/content/MediaUploader.vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const contentStore = useContentStore()

const formRef = ref<FormInstance>()
const tagInput = ref('')
const form = reactive({
  title: '',
  body: '',
  mediaUrl: '',
  mediaType: 'TEXT' as MediaType,
  status: 'PUBLISHED' as ContentStatus,
  tags: [] as string[]
})

const rules: FormRules = {
  title: [
    { required: true, message: '标题不能为空', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ]
}

const handleMediaUpdate = (url: string) => {
  form.mediaUrl = url
}

const handleMediaTypeUpdate = (type: MediaType) => {
  form.mediaType = type
}

const handleAddTag = () => {
  const tag = tagInput.value.trim().toLowerCase()
  if (tag && !form.tags.includes(tag) && form.tags.length < 10) {
    form.tags.push(tag)
    tagInput.value = ''
  }
}

const handleRemoveTag = (tag: string) => {
  const index = form.tags.indexOf(tag)
  if (index > -1) {
    form.tags.splice(index, 1)
  }
}

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (valid) {
      const content = await contentStore.createContent({
        title: form.title,
        body: form.body || undefined,
        mediaUrl: form.mediaUrl || undefined,
        mediaType: form.mediaType,
        status: form.status,
        tags: form.tags.length > 0 ? form.tags : undefined
      })

      if (content) {
        router.push(`/content/${content.id}`)
      }
    }
  })
}

const handleSaveDraft = async () => {
  form.status = 'DRAFT'
  await handleSubmit(formRef.value)
}
</script>

<template>
  <div class="create-content-page">
    <div class="container">
      <div class="page-header">
        <h1>发布内容</h1>
        <p>分享你的想法、图片或视频</p>
      </div>

      <div class="content-form">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleSubmit(formRef)"
        >
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="输入一个吸引人的标题..."
              size="large"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="媒体（可选）">
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
              placeholder="写下你的内容..."
              :rows="8"
              maxlength="10000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="标签（最多10个）">
            <div class="tags-input">
              <div class="tags-list" v-if="form.tags.length">
                <el-tag
                  v-for="tag in form.tags"
                  :key="tag"
                  closable
                  @close="handleRemoveTag(tag)"
                >
                  # {{ tag }}
                </el-tag>
              </div>
              <div class="tag-input-wrapper">
                <el-input
                  v-model="tagInput"
                  placeholder="输入标签后按回车添加"
                  @keyup.enter.prevent="handleAddTag"
                  :disabled="form.tags.length >= 10"
                />
                <el-button @click="handleAddTag" :disabled="form.tags.length >= 10">
                  添加
                </el-button>
              </div>
            </div>
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button @click="router.back()">取消</el-button>
              <el-button @click="handleSaveDraft" :loading="contentStore.loading">
                存为草稿
              </el-button>
              <el-button
                type="primary"
                native-type="submit"
                :loading="contentStore.loading"
              >
                发布
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create-content-page {
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

.tags-input {
  width: 100%;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-input-wrapper {
  display: flex;
  gap: 8px;
}

.tag-input-wrapper .el-input {
  flex: 1;
}
</style>
