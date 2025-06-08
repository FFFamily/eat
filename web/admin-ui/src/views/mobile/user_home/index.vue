<template>
  <div class="user-home-container">
    <!-- 用户头部信息 -->
    <div class="user-header">
      <el-avatar class="user-avatar" :size="72" src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" />
      <div class="user-header-info">
        <h3 class="username">{{ userInfo.username }}</h3>
        <div class="user-tags">
          <el-tag size="small" effect="plain">会员</el-tag>
        </div>
      </div>
    </div>

    <!-- 功能入口列表 - 单列布局 -->
    <div class="function-list">
      <div 
        v-for="(item, index) in menuItems" 
        :key="index" 
        class="function-item"
        @click="goToPage(item.path)"
      >
        <el-icon :size="20" class="item-icon">
          <component :is="item.icon" />
        </el-icon>
        <span class="item-label">{{ item.label }}</span>
      </div>
    </div>
     <HistoryCalender/>
    <!-- 退出按钮 -->
    <el-button class="logout-btn" type="primary" plain @click="logout">退出登录</el-button>
  </div>
</template>


<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '~/store/modules/user';
import { ElMessage } from 'element-plus';
import HistoryCalender from './HistoryCalender.vue';
import {
  SetUp,
  Menu ,
  Calendar
} from '@element-plus/icons-vue'
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

const goToPage = (path: string) => {
  isClicked.value = true;
  setTimeout(() => {
    isClicked.value = false;
    // 这里假设你要使用路径来跳转，需要确保 'FoodManage' 对应的路径是正确的
    router.push(path);
  }, 100);
}

// 退出登录函数
const logout = async () => {
  const store = useUserStore();
  store.userLogout().then(res => {
    ElMessage({ message: '退出登录成功', type: 'success', })
    router.push('/login');
  });
};

// 新增菜单项配置
const menuItems = ref([
  { label: '食物管理', path: '/mobile/foodManage', icon: 'Menu' },
  { label: '食物类型', path: '/mobile/foodTypeManage', icon: 'SetUp' },
  { label: '饮食历史', path: '/mobile/manage/foodHistory', icon: 'Calendar' }
]);
</script>

<style scoped>
.user-home-container {
  padding: 16px;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.function-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.function-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.2s;
}

.function-item:hover {
  background: #f5f5f5;
  transform: translateX(4px);
}

.item-icon {
  margin-right: 12px;
  color: #409eff;
}

.item-label {
  font-size: 16px;
  color: #333;
}

.logout-btn {
  width: 100%;
  margin-top: 24px;
  height: 48px;
  font-size: 16px;
}
</style>
