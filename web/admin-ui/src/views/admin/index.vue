<template>
  <h1>管理员页面</h1>
  <el-row class="tac">
    <el-col :span="2.5">
      <el-menu default-active="2" class="el-menu-vertical-demo" @select="handleSelect">
        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <location />
            </el-icon>
            <span>用户模块</span>
          </template>
          <el-menu-item index="1-1">用户管理</el-menu-item>
          <el-menu-item index="1-2">角色管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="2">
          <template #title>
            <el-icon>
              <Setting />
            </el-icon>
            <span>饮食管理</span>
          </template>
          <el-menu-item index="2-1">饮食方式管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-col>
    <el-col :span="21">
      <!-- <router-view></router-view> -->
      <UserManagement v-if="selectMenu === '1-1'"></UserManagement>
      <RoleManage v-if="selectMenu === '1-2'"></RoleManage>
      <DietManagement v-if="selectMenu === '2-1'"></DietManagement>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'
import UserManagement from '~/views/admin/user/UserManagement.vue'
import RoleManage from '~/views/admin/user/RoleManage.vue'
import DietManagement from '~/views/admin/food/DietManagement.vue'
import { ref } from 'vue'
const selectMenu = ref('')
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key)
  selectMenu.value = key
}
</script>

<style scoped></style>