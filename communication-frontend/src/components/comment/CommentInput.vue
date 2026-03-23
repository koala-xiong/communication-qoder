<template>
  <div class="comment-input">
    <el-input
      v-model="commentText"
      type="textarea"
      :rows="3"
      :placeholder="placeholder"
      :maxlength="2000"
      show-word-limit
    />
    <div class="comment-input-actions">
      <el-button v-if="showCancel" text @click="$emit('cancel')">取消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        :disabled="!commentText.trim()"
        @click="handleSubmit"
      >
        {{ submitText }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = withDefaults(defineProps<{
  placeholder?: string
  submitText?: string
  showCancel?: boolean
  loading?: boolean
}>(), {
  placeholder: '写下你的评论...',
  submitText: '发表评论',
  showCancel: false,
  loading: false
})

const emit = defineEmits<{
  submit: [text: string]
  cancel: []
}>()

const commentText = ref('')

const handleSubmit = () => {
  if (commentText.value.trim()) {
    emit('submit', commentText.value.trim())
    commentText.value = ''
  }
}

defineExpose({
  clear: () => {
    commentText.value = ''
  }
})
</script>

<style scoped>
.comment-input {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px;
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(29, 155, 240, 0.12);
}
</style>
