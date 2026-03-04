<script setup lang="ts">
import { ref, computed } from 'vue'
import { contentApi, type MediaType } from '@/api/content'
import { ElMessage } from 'element-plus'
import { Plus, Delete, VideoPlay, Picture } from '@element-plus/icons-vue'

const props = defineProps<{
  modelValue?: string
  mediaType?: MediaType
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'update:mediaType': [value: MediaType]
}>()

const uploading = ref(false)
const uploadProgress = ref(0)

const previewUrl = computed(() => props.modelValue)

const isImage = computed(() => props.mediaType === 'IMAGE')
const isVideo = computed(() => props.mediaType === 'VIDEO')

const handleFileChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  const isImageFile = file.type.startsWith('image/')
  const isVideoFile = file.type.startsWith('video/')

  if (!isImageFile && !isVideoFile) {
    ElMessage.error('Only images and videos are allowed')
    return
  }

  uploading.value = true
  uploadProgress.value = 0

  try {
    const response = isImageFile
      ? await contentApi.uploadImage(file)
      : await contentApi.uploadVideo(file)

    const data = response.data.data
    emit('update:modelValue', data.url)
    emit('update:mediaType', data.mediaType)
    ElMessage.success('File uploaded successfully')
  } catch (error: any) {
    const message = error.response?.data?.message || 'Upload failed'
    ElMessage.error(message)
  } finally {
    uploading.value = false
    uploadProgress.value = 0
    input.value = ''
  }
}

const handleRemove = () => {
  emit('update:modelValue', '')
  emit('update:mediaType', 'TEXT')
}
</script>

<template>
  <div class="media-uploader">
    <div class="preview-area" v-if="previewUrl">
      <img v-if="isImage" :src="previewUrl" class="preview-image" />
      <video v-else-if="isVideo" :src="previewUrl" controls class="preview-video" />

      <div class="preview-overlay">
        <el-button type="danger" :icon="Delete" circle @click="handleRemove" />
      </div>

      <div class="media-badge">
        <el-icon v-if="isImage"><Picture /></el-icon>
        <el-icon v-else-if="isVideo"><VideoPlay /></el-icon>
        <span>{{ mediaType }}</span>
      </div>
    </div>

    <label class="upload-area" v-else>
      <input
        type="file"
        accept="image/*,video/*"
        @change="handleFileChange"
        :disabled="uploading"
        hidden
      />

      <div class="upload-content" v-if="!uploading">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <p class="upload-text">Click to upload image or video</p>
        <p class="upload-hint">Supports: JPEG, PNG, GIF, MP4, WebM</p>
      </div>

      <div class="upload-progress" v-else>
        <el-progress :percentage="uploadProgress" :stroke-width="8" />
        <p>Uploading...</p>
      </div>
    </label>
  </div>
</template>

<style scoped>
.media-uploader {
  width: 100%;
}

.preview-area {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-bg-tertiary);
}

.preview-image,
.preview-video {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  display: block;
}

.preview-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.preview-area:hover .preview-overlay {
  opacity: 1;
}

.media-badge {
  position: absolute;
  bottom: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: var(--radius-full);
  font-size: 12px;
}

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-lg);
  background: var(--color-bg-tertiary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.upload-area:hover {
  border-color: var(--color-primary);
  background: var(--color-bg-primary);
}

.upload-content {
  text-align: center;
}

.upload-icon {
  font-size: 48px;
  color: var(--color-text-muted);
  margin-bottom: 16px;
}

.upload-text {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 12px;
  color: var(--color-text-muted);
}

.upload-progress {
  width: 200px;
  text-align: center;
}

.upload-progress p {
  margin-top: 12px;
  color: var(--color-text-secondary);
}
</style>
