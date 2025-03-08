<template>
  <div>
    <h1>个人信息与设置</h1>
    <el-form :model="userInfo" label-width="120px">
      <!-- 用户名 -->
      <el-form-item label="用户名">
        <el-input v-model="userInfo.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <!-- 邮箱 -->
      <el-form-item label="邮箱">
        <el-input v-model="userInfo.email" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <!-- 联系方式 -->
      <el-form-item label="联系方式">
        <el-input v-model="userInfo.phone" placeholder="请输入联系方式"></el-input>
      </el-form-item>
      <!-- 通知设置 -->
      <el-form-item label="通知设置">
        <el-switch v-model="userInfo.notification" active-text="开启" inactive-text="关闭"></el-switch>
      </el-form-item>
      <!-- 保存按钮 -->
      <el-form-item>
        <el-button type="primary" @click="saveInfo">保存</el-button>
        <el-button @click="resetInfo">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 初始化用户信息
const userInfo = ref({
  username: '默认用户名',
  email: 'default@example.com',
  phone: '1234567890',
  notification: true
});

// 保存信息
const saveInfo = () => {
  // 这里可以添加保存到后端的逻辑
  console.log('保存的用户信息：', userInfo.value);
  alert('信息已保存');
};

// 重置信息
const resetInfo = () => {
  userInfo.value = {
    username: '默认用户名',
    email: 'default@example.com',
    phone: '1234567890',
    notification: true
  };
};
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>