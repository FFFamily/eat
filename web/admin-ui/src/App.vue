<template>
  <el-config-provider>
    <router-view></router-view>
    <HomeMenu v-show="route.fullPath.startsWith('/mobile')"></HomeMenu>
  </el-config-provider>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';

import HomeMenu from '~/views/compoment/HomeMenu.vue'
const router = useRouter();
const route = useRoute();
</script>

<style scoped lang="scss"></style>