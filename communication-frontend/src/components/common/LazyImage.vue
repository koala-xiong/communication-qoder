<template>
  <div 
    class="lazy-image-container" 
    :class="{ loaded: isLoaded, error: hasError }"
    :style="containerStyle"
  >
    <img
      v-if="!hasError"
      ref="imgRef"
      :src="isLoaded ? src : placeholder"
      :alt="alt"
      class="lazy-image"
      @load="handleLoad"
      @error="handleError"
    />
    <div v-else class="error-placeholder">
      <el-icon :size="32"><Picture /></el-icon>
    </div>
    <div v-if="!isLoaded && !hasError" class="loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  src: string
  alt?: string
  placeholder?: string
  aspectRatio?: string
}>(), {
  alt: '',
  placeholder: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg"%3E%3C/svg%3E',
  aspectRatio: '16/9'
})

const imgRef = ref<HTMLImageElement | null>(null)
const isLoaded = ref(false)
const hasError = ref(false)

const containerStyle = computed(() => ({
  aspectRatio: props.aspectRatio
}))

let observer: IntersectionObserver | null = null

const handleLoad = () => {
  isLoaded.value = true
}

const handleError = () => {
  hasError.value = true
}

onMounted(() => {
  if ('IntersectionObserver' in window && imgRef.value) {
    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const img = entry.target as HTMLImageElement
            img.src = props.src
            observer?.unobserve(img)
          }
        })
      },
      { rootMargin: '100px' }
    )
    observer.observe(imgRef.value)
  } else if (imgRef.value) {
    imgRef.value.src = props.src
  }
})

onUnmounted(() => {
  observer?.disconnect()
})
</script>

<style scoped>
.lazy-image-container {
  position: relative;
  overflow: hidden;
  background: var(--color-bg-tertiary);
}

.lazy-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity var(--transition-normal);
  opacity: 0;
}

.lazy-image-container.loaded .lazy-image {
  opacity: 1;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(90deg, var(--color-bg-tertiary) 25%, var(--color-bg-primary) 50%, var(--color-bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.error-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-tertiary);
  color: var(--color-text-muted);
}
</style>
