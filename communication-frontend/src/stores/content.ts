import { defineStore } from 'pinia'
import { ref } from 'vue'
import { contentApi, type Content, type CreateContentRequest, type UpdateContentRequest } from '@/api/content'
import { ElMessage } from 'element-plus'

export const useContentStore = defineStore('content', () => {
  const contents = ref<Content[]>([])
  const currentContent = ref<Content | null>(null)
  const loading = ref(false)
  const pagination = ref({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    hasMore: true
  })

  async function fetchContents(page = 0, size = 10, append = false): Promise<void> {
    loading.value = true
    try {
      const response = await contentApi.getContents(page, size)
      const data = response.data.data

      if (append) {
        contents.value = [...contents.value, ...data.content]
      } else {
        contents.value = data.content
      }

      pagination.value = {
        page: data.page,
        size: data.size,
        totalElements: data.totalElements,
        totalPages: data.totalPages,
        hasMore: !data.last
      }
    } catch (error) {
      console.error('Failed to fetch contents:', error)
    } finally {
      loading.value = false
    }
  }

  async function fetchContentById(id: number): Promise<Content | null> {
    loading.value = true
    try {
      const response = await contentApi.getContentById(id)
      currentContent.value = response.data.data
      return currentContent.value
    } catch (error) {
      console.error('Failed to fetch content:', error)
      return null
    } finally {
      loading.value = false
    }
  }

  async function createContent(data: CreateContentRequest): Promise<Content | null> {
    loading.value = true
    try {
      const response = await contentApi.createContent(data)
      const newContent = response.data.data
      contents.value = [newContent, ...contents.value]
      ElMessage.success('发布成功')
      return newContent
    } catch (error: any) {
      const message = error.response?.data?.message || '发布失败'
      ElMessage.error(message)
      return null
    } finally {
      loading.value = false
    }
  }

  async function updateContent(id: number, data: UpdateContentRequest): Promise<Content | null> {
    loading.value = true
    try {
      const response = await contentApi.updateContent(id, data)
      const updatedContent = response.data.data

      const index = contents.value.findIndex(c => c.id === id)
      if (index !== -1) {
        contents.value[index] = updatedContent
      }
      if (currentContent.value?.id === id) {
        currentContent.value = updatedContent
      }

      ElMessage.success('保存成功')
      return updatedContent
    } catch (error: any) {
      const message = error.response?.data?.message || '保存失败'
      ElMessage.error(message)
      return null
    } finally {
      loading.value = false
    }
  }

  async function deleteContent(id: number): Promise<boolean> {
    loading.value = true
    try {
      await contentApi.deleteContent(id)
      contents.value = contents.value.filter(c => c.id !== id)
      if (currentContent.value?.id === id) {
        currentContent.value = null
      }
      ElMessage.success('删除成功')
      return true
    } catch (error: any) {
      const message = error.response?.data?.message || '删除失败'
      ElMessage.error(message)
      return false
    } finally {
      loading.value = false
    }
  }

  async function loadMore(): Promise<void> {
    if (!pagination.value.hasMore || loading.value) return
    await fetchContents(pagination.value.page + 1, pagination.value.size, true)
  }

  function clearContents(): void {
    contents.value = []
    currentContent.value = null
    pagination.value = {
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      hasMore: true
    }
  }

  return {
    contents,
    currentContent,
    loading,
    pagination,
    fetchContents,
    fetchContentById,
    createContent,
    updateContent,
    deleteContent,
    loadMore,
    clearContents
  }
})
