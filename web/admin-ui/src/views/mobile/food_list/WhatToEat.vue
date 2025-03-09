<template>
  <Header></Header>
  <div class="what-to-eat-page">
    <!-- 巨大圆形按钮 -->
    <el-button class="big-round-button" @click="openFoodSelection">今天吃什么</el-button>
    <!-- 食物选择对话框 -->
    <el-dialog v-model="dialogVisible" style="width: 90%;height: 80%;" title="选择食物">
      <ul>
        <li v-for="food in foodList" :key="food" @click="selectFood(food)">{{ food }}</li>
      </ul>
      <template #footer>
        <el-button @click="dialogVisible = false">就这个把</el-button>
        <el-button @click="dialogVisible = false">再选一次</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Header from './EatWahtHeader.vue'
// 控制对话框显示状态
const dialogVisible = ref(false);

// 控制抽屉显示状态
const drawerVisible = ref(false);

// 食物列表
const foodList = ref([
  '汉堡',
  '披萨',
  '寿司',
  '拉面',
  '火锅'
]);

// 打开食物选择对话框
const openFoodSelection = () => {
  dialogVisible.value = true;
};

// 选择食物
const selectFood = (food) => {
  // 这里可以添加选择食物后的逻辑，比如显示提示信息等
  console.log(`你选择了: ${food}`);
  dialogVisible.value = false;
};


</script>

<style scoped>
.what-to-eat-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
}

.big-round-button {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  font-size: 24px;
  background-color: #409EFF;
  color: white;
  border: none;
}


</style>