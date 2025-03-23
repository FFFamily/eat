<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
  Avatar
} from '@element-plus/icons-vue'
// import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ref } from 'vue'
// const locale = ref(zhCn)
const activeMenu = ref('MobileHome')
import { useRouter } from 'vue-router';
const router = useRouter();
const handleMenuSelect = (key:any) => {
  activeMenu.value = key;
  router.push({ name: key });
};
</script>

<template>
  <el-config-provider>
    <router-view></router-view>
    <el-menu :default-active="activeMenu" class="el-menu-demo" mode="horizontal" @select="handleMenuSelect">
      <el-menu-item index="MobileHome"><el-icon><location /></el-icon></el-menu-item>
      <el-menu-item index="History"><el-icon><setting /></el-icon></el-menu-item>
      <el-menu-item index="Stats"><el-icon><Avatar /></el-icon></el-menu-item>
      <el-menu-item index="UserHome"><el-icon><Avatar /></el-icon></el-menu-item>
    </el-menu>
  </el-config-provider>
</template>

<style scoped lang="scss">
.el-menu-demo {
  /* 将菜单固定在浏览器窗口底部 */
  position: fixed;
  bottom: 0;
  width: 100%;
  display: flex; /* 使用 flexbox 布局 */
  justify-content: space-around; /* 让菜单项均匀分布 */
}
.el-menu--horizontal {
  --el-menu-horizontal-height: 5%;
}
// .el-menu--horizontal .el-menu-item {
//   // flex: 1; /* 让每个菜单项平均分配宽度 */
//   // text-align: center; /* 菜单项内容居中 */
// }
</style>