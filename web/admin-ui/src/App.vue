<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ref } from 'vue'
const locale = ref(zhCn)
const activeMenu = ref('MobileHome')
import { useRouter } from 'vue-router';
const router = useRouter();
const handleMenuSelect = (key:any) => {
  activeMenu.value = key;
  router.push({ name: key });
};
</script>

<template>
  <el-config-provider :locale="locale">
    <router-view></router-view>
    <el-menu :default-active="activeMenu" class="el-menu-demo" mode="horizontal" @select="handleMenuSelect">
      <el-menu-item index="MobileHome">食</el-menu-item>
      <el-menu-item index="FoodType">Home</el-menu-item>
      <el-menu-item index="History">日历</el-menu-item>
    </el-menu>
  </el-config-provider>
</template>

<style scoped lang="scss">
.el-menu-demo {
  /* 将菜单固定在浏览器窗口底部 */
  position: fixed;
  bottom: 0;
  width: 100%;
}
</style>