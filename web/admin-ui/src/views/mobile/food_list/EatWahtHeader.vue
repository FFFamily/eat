<template>
  <div class="header">
    <!-- <div class="spacer"></div> -->
    <el-button text size="large" :icon="Operation"  @click="openDrawer"></el-button>
    <el-button icon="Setting" @click="handleSettingClick"></el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Setting } from '@element-plus/icons-vue';
import { Operation } from '@element-plus/icons-vue'
// 处理设置按钮点击事件
const handleSettingClick = () => {
  // 这里可以添加设置按钮点击后的逻辑，比如打开设置弹窗
  console.log('设置按钮被点击');
};

// 打开抽屉
const openDrawer = () => {
  drawerVisible.value = true;
};
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f0f0f0;
  /* 添加以下属性实现两端对齐 */
  justify-content: space-between; 
}

.spacer {
  flex: 1;
}

/* .setting-button {
  position: absolute;
  top: 1px;
  left: 1px;
} */
</style>