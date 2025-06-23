<template>
    <el-config-provider>
      <router-view></router-view>
      <!-- <HomeMenu/> -->
      <el-menu v-if="activeMenuShow" :default-active="activeMenu" class="el-menu-demo" mode="horizontal" @select="handleMenuSelect">
        <el-menu-item index="MobileHome"><el-icon><location /></el-icon></el-menu-item>
        <!-- <el-menu-item index="History"><el-icon><setting /></el-icon></el-menu-item> -->
        <el-menu-item index="Stats"><el-icon><Avatar /></el-icon></el-menu-item>
        <el-menu-item index="UserHome"><el-icon><Avatar /></el-icon></el-menu-item>
      </el-menu>
    </el-config-provider>
  </template>
  
  <script setup lang="ts">
  import { ElConfigProvider } from 'element-plus'
  import {
    Document,
    Menu as IconMenu,
    Location,
    Setting,
    Avatar
  } from '@element-plus/icons-vue'
  import HomeMenu from './views/compoment/HomeMenu.vue'
  import { ref,watch } from 'vue'
  import '~/styles/index.scss'
  const activeMenu = ref('MobileHome')
  import { useRouter,useRoute } from 'vue-router';
  const router = useRouter();
  const route = useRoute();
  const activeMenuShow = ref(true);
  // 管理界面的时候就进行隐藏
  watch(() => route.fullPath, (newName) => {
    activeMenuShow.value = !newName.startsWith('/admin')
  });
  // 切换菜单
  const handleMenuSelect = (key:any) => {
    activeMenu.value = key;
    router.push({ name: key });
    console.log(route.fullPath);
  };
  </script>
  
  <style scoped lang="scss">
  .el-menu-demo {
    /* 将菜单固定在浏览器窗口底部 */
    position: fixed;
    bottom: 0;
    width: 100%;
    display: flex; /* 使用 flexbox 布局 */
    justify-content: space-around; /* 让菜单项均匀分布 */
    z-index: 1000; /* 保持层级最高，避免被内容遮挡 */
    background-color: rgb(255, 255, 255); /* 可选：添加背景色避免内容穿透 */
  }
  .el-menu--horizontal {
    --el-menu-horizontal-height: 5%;
  }
  </style>