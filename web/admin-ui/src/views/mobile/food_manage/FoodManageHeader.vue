<template>
  <div class="header">
    <el-popover placement="bottom" trigger="click" :width="200">
      <template #reference>
        <el-button class="button-style" size="large" :icon="Plus"></el-button>
      </template>
      <!-- 修改后的 el-menu 标签，添加自定义类名 -->
      <el-menu class="small-menu" @select="handleMenuSelect">
        <el-menu-item index="AddFood">添加食物</el-menu-item>
        <!-- 这里可以添加其他菜单项 -->
      </el-menu>
    </el-popover>
  </div>
   <!-- 使用 v-model 绑定 dialogVisible -->
   <FoodAddDialog v-model="dialogVisible"  class="add" @close="handleDialogClose" />
</template>

<script setup>
import { ref } from 'vue';
import { MoreFilled, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import FoodAddDialog from './foodAddDialog.vue';
const emit = defineEmits(['refresh'])
// 控制对话框显示状态
const dialogVisible = ref(false);
const router = useRouter();
// 菜单选择方法
const handleMenuSelect = (key) => {
  if (key === 'AddFood') {
    openAddDialog();
  }
};
const handleDialogClose = () => {
  dialogVisible.value = false;
  emit('refresh')
  console.log('Dialog closed');
};
// 打开添加食物对话框
const openAddDialog = () => {
  dialogVisible.value = true;
};
</script>

<style scoped>
.header {
  width: 100%;
  height: 60px;
  position: fixed; /* 修正定位语法 */
  top: 0; /* 固定在视口顶部 */
  left: 0; /* 左对齐 */
  right: 0; /* 右对齐，确保宽度占满 */
  z-index: 1000; /* 保持层级最高，避免被内容遮挡 */
  background: white; /* 可选：添加背景色避免内容穿透 */
}

/* .add {
  width: 100%;
} */

.el-menu--horizontal {
  --el-menu-horizontal-height: 20px;
}

.button-style {
  border: 0px;
  height: 60px;
}

.spacer {
  flex: 1;
}

/* 新增的样式，用于缩小菜单 */
.small-menu {
  padding: 0px 0;
}

.small-menu .el-menu-item {
  height: 20px;
  /* 减小菜单项的高度 */
  line-height: 24px;
  /* 确保文字垂直居中 */
}
</style>