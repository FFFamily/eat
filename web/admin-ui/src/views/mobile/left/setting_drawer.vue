<template>
    <!-- 抽屉 -->
    <el-drawer v-model="drawerVisible" title="设置" direction="ltr" size="80%">
      <!-- 这里可以添加抽屉里的设置内容，比如食物列表的编辑等 -->
      <template>
        <p>这里可以放设置相关的内容</p>
      </template>
      <template #footer>
        <el-button @click="drawerVisible = false">关闭</el-button>
      </template>
    </el-drawer>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
// 抽屉可见性
const drawerVisible = ref(false)
</script>
<style scoped></style>