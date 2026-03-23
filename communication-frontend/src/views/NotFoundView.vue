<script setup lang="ts">
</script>

<template>
  <div class="not-found-page">
    <div class="container">
      <el-result
        icon="warning"
        title="404"
        sub-title="页面不存在"
      >
        <template #extra>
          <router-link to="/">
            <el-button type="primary">返回首页</el-button>
          </router-link>
        </template>
      </el-result>
    </div>
  </div>
</template>

<style scoped>
.not-found-page {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
