<template>
  <div class="user-home-container">
    <!-- 用户头部信息 -->
    <div class="user-header">
      <el-avatar class="user-avatar" shape="square" :size="80"
        src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" />
      <!-- <img class="user-avatar" src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" alt="用户头像"> -->
      <div class="user-header-info">
        <el-text type="primary" class="mx-1" size="large">{{ userInfo.username }}</el-text>
        <el-tag type="primary">Tag 1</el-tag>
        <el-tag type="success">Tag 2</el-tag>
        <!-- <el-text class="mx-1" size="small">{{ userInfo.introduction }}</el-text> -->
      </div>
    </div>
    <!-- 用户信息表单 -->
    <div class="user-info-form" @click="goToFoodManagement" :class="{ 'active': isClicked }">
      食物管理
    </div>
    <div class="user-info-form" @click="goToAdminManagement" :class="{ 'active': isClicked }">
      后台管理
    </div>
    <!-- 退出登录按钮 -->
    <el-button @click="logout">退出登录</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '~/store/modules/user';
import { ElMessage } from 'element-plus';

// 初始化用户信息
const userInfo = ref({
  username: '默认用户名',
  email: 'default@example.com',
  phone: '1234567890',
  notification: true,
  introduction: '这是个人简介',
  score: 100,
  avatar: 'https://via.placeholder.com/100'
});

// 初始化程序配置信息
const appConfig = ref({
  theme: 'light',
  language: 'zh'
});

// 保存用户信息
const saveInfo = () => {
  // 这里可以添加保存到后端的逻辑
  console.log('保存的用户信息：', userInfo.value);
  alert('信息已保存');
};

// 重置用户信息
const resetInfo = () => {
  userInfo.value = {
    username: '默认用户名',
    email: 'default@example.com',
    phone: '1234567890',
    notification: true,
    introduction: '这是个人简介',
    score: 100,
    avatar: 'https://via.placeholder.com/100'
  };
};

// 保存程序配置信息
const saveAppConfig = () => {
  // 这里可以添加保存到后端的逻辑
  console.log('保存的程序配置信息：', appConfig.value);
  alert('配置已保存');
};

// 路由实例
const router = useRouter();

// 点击状态
const isClicked = ref(false);

// 跳转到食物管理页面
const goToFoodManagement = () => {
  isClicked.value = true;
  setTimeout(() => {
    isClicked.value = false;
    // 这里假设你要使用路径来跳转，需要确保 'FoodManage' 对应的路径是正确的
    router.push('/mobile/foodManage');
  }, 100);
};
const goToAdminManagement = () => {
  isClicked.value = true;
  setTimeout(() => {
    isClicked.value = false;
    router.push('/admin');
  }, 100);
};


// 退出登录函数
const logout = async () => {
  const store = useUserStore();
  store.userLogout().then(res => {
    debugger
    ElMessage({ message: '退出登录成功', type: 'success', })
    router.push('/login');
  });
};
</script>

<style scoped>
.user-home-container {
  padding: 5px;
}

.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-avatar {
  /* width: 80px;
  height: 80px;
  border-radius: 50%; */
  margin-right: 20px;
  margin-left: 10px;
}

.user-header-info {
  flex: 1;
}

.user-score {
  margin-left: 20px;
}

.user-info-form {
  box-shadow: 10px 10px 10px rgba(158, 106, 106, 0.1);
  padding: 10px;
  border: 0.5px solid #ccc;
  border-radius: 10px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.user-info-form.active {
  background-color: #e0e0e0;
}
</style>