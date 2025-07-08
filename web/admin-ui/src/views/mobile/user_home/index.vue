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
  { label: '饮食历史', path: '/mobile/manage/foodHistory', icon: 'Calendar' },
  { label: '饮食习惯', path: '/mobile/manage/foodHabit', icon: 'FoodHabit' },
  { label: '食物标签', path: '/mobile/manage/foodTag', icon: 'FoodTag' }
]);
</script>

<style scoped>
.user-home-container {
  padding: 24px 12px 32px 12px;
  background: linear-gradient(135deg, #f7faff 0%, #f0f4ff 100%);
  min-height: 100vh;
}

.user-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
  padding: 32px 0 24px 0;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
}

.user-avatar {
  border: 4px solid #e0e7ff;
  box-shadow: 0 2px 12px #2563eb22;
  margin-bottom: 12px;
}

.user-header-info {
  text-align: center;
}

.username {
  font-size: 1.3rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 6px;
}

.user-tags {
  margin-top: 4px;
  .el-tag {
    background: #f0f4ff;
    color: #2563eb;
    border: none;
    font-size: 0.95em;
    border-radius: 8px;
    padding: 2px 12px;
  }
}

.function-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.function-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 18px 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(37,99,235,0.06);
  cursor: pointer;
  transition: all 0.18s cubic-bezier(.34,1.56,.64,1);
  border: 1.5px solid transparent;
  &:hover {
    background: #f0f4ff;
    transform: scale(1.03);
    border-color: #2563eb22;
    box-shadow: 0 6px 24px #2563eb11;
  }
  &:active {
    transform: scale(0.98);
  }
}

.item-icon {
  margin-right: 16px;
  color: #2563eb;
  font-size: 1.5em;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-label {
  font-size: 1.08rem;
  color: #222;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.logout-btn {
  width: 100%;
  margin-top: 32px;
  height: 48px;
  font-size: 1.08rem;
  border-radius: 12px;
  background: #2563eb;
  color: #fff;
  border: none;
  box-shadow: 0 2px 12px #2563eb22;
  transition: background 0.2s;
  &:hover {
    background: #1e40af;
  }
}
</style>
