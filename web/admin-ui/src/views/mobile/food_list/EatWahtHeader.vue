<template>
  <div class="header">
    <!-- <div class="spacer"></div> -->
    <el-button class="button-style" size="large" :icon="MoreFilled" @click="openDrawer"></el-button>
    <!-- <el-button class="button-style" size="large" :icon="Plus" @click="handleSettingClick"></el-button> -->
    <el-popover placement="bottom" trigger="click" :width="200">
      <template #reference>
        <el-button class="button-style" size="large" :icon="Plus"></el-button>
      </template>
      <!-- 修改后的 el-menu 标签，添加自定义类名 -->
      <el-menu class="small-menu" @select="handleMenuSelect">
        <el-menu-item index="FoodType">管理食物</el-menu-item>
        <!-- 这里可以添加其他菜单项 -->
      </el-menu>
    </el-popover>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { MoreFilled, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
const router = useRouter();
// 菜单选择方法
const handleMenuSelect = (key) => {
  router.push({ name: key });
};

// 打开抽屉
const openDrawer = () => {
  drawerVisible.value = true;
};
</script>

<style scoped>
.header {
  width: 100%;
  height: 40px;
  display: flex;
  justify-content: space-between;
}

.el-menu--horizontal {
  --el-menu-horizontal-height: 20px;
}

.button-style {
  border: 0px;
}

.spacer {
  flex: 1;
}

/* 新增的样式，用于缩小菜单 */
.small-menu {
  padding: 0px 0; 
}

.small-menu .el-menu-item {
  height: 20px; /* 减小菜单项的高度 */
  line-height: 24px; /* 确保文字垂直居中 */
}
</style>