<template>
  <div class="mobile-menu-container">
    <nav class="bottom-nav">
      <router-link
        v-for="(item, index) in menus"
        :key="index"
        :to="item.path"
        class="nav-item"
        active-class="active"
      >
        <div class="nav-icon">
          <component 
            :is="item.icon"
            class="el-icon"
            :style="{ color: isActive(item) ? '#2563eb' : '#666' }"
          />
        </div>
        <span class="nav-label">{{ item.label }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { House, ShoppingCart, User, List } from '@element-plus/icons-vue';

const route = useRoute();

// 菜单配置项 - 使用Element Plus图标
const menus = [
  {
    path: '/mobile/eat',
    label: '首页',
    icon: House
  },
  {
    path: '/order',
    label: '点餐',
    icon: List
  },
  {
    path: '/cart',
    label: '购物车',
    icon: ShoppingCart
  },
  {
    path: '/mobile/user',
    label: '我的',
    icon: User
  }
];

// 判断当前激活状态
const isActive = (item: any) => {
  return route.path === item.path;
};
</script>

<style scoped lang="scss">
.mobile-menu-container {
  // 确保页面内容不会被底部菜单遮挡
  padding-bottom: 56px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 56px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  padding: 0 8px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 6px 0;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;

  &:active {
    transform: scale(0.95);
  }
}

.nav-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 32px;
  height: 32px;
  margin-bottom: 2px;
}

.el-icon {
  font-size: 22px;
  transition: color 0.2s ease;
}

.nav-label {
  font-size: 10px;
  font-weight: 500;
  color: #666;
  letter-spacing: 0.2px;
  transition: color 0.2s ease;
}

.active {
  .nav-label {
    color: #2563eb;
  }
}

/* 适配全面屏手机底部安全区域 */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
  .bottom-nav {
    padding-bottom: env(safe-area-inset-bottom);
  }

  .mobile-menu-container {
    padding-bottom: calc(56px + env(safe-area-inset-bottom));
  }
}

/* 小屏设备适配 */
@media (max-width: 320px) {
  .nav-label {
    font-size: 9px;
  }
}
</style>