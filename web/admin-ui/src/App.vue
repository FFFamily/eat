<template>
  <el-config-provider namespace="ep">
    <BaseHeader />
    <div class="main-container flex">
      <BaseSide />
      <div w="full" py="4">
        <RouterView />
      </div>
    </div>
  </el-config-provider>
</template>
<script setup lang="ts">
import service from './utils/request'
import {onMounted} from 'vue'

onMounted(() => {
  console.log(import.meta.env)
  service({
    url: '/admin/auth/test',
    method: 'get',
  }).then(res => {
    console.log(res)
  })
})
</script>

<style>
#app {
  text-align: center;
  color: var(--ep-text-color-primary);
}

.main-container {
  height: calc(100vh - var(--ep-menu-item-height) - 4px);
}
</style>
