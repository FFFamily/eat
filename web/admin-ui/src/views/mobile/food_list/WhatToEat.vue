<template>
  <Header></Header>
  <div class="what-to-eat-page">
    <!-- 巨大圆形按钮 -->
    <el-button v-loading.fullscreen.lock="isMatching" class="big-round-button" @click="openFoodSelection">今天吃什么</el-button>
    <!-- 食物选择对话框 -->
    <el-dialog v-model="dialogVisible" style="width: 90%;height: 90%;" title="选择食物">
      <div class="food-content">
          <h3 class="food-title">{{ selectedFood.name }}</h3>
          <p class="food-info">{{ selectedFood.info }}</p>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">就这个吧</el-button>
        <el-button @click="dialogVisible = false">再选一次</el-button>
      </template>
    </el-dialog>
    <!-- <el-loading :visible="isMatching" text="正在匹配食物..."></el-loading> -->
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Header from './EatWahtHeader.vue'
import {getRecommendFood} from '~/api/user/userFoodApi'
// 控制对话框显示状态
const dialogVisible = ref(false);
// 控制是否正在匹配食物
const isMatching = ref(false);
// 选择的食物
const selectedFood = ref({});
// 食物列表改为对象数组
const foodList = ref([]);

// 打开食物选择对话框
const openFoodSelection = () => {
  isMatching.value = true;
  getRecommendFood().then(res=>{
    console.log(res)
    selectedFood.value = res.data[0];
    isMatching.value = false;
    dialogVisible.value = true
  })
};

// 选择食物
const selectFood = (food) => {
  // 这里可以添加选择食物后的逻辑，比如显示提示信息等
  console.log(`你选择了: ${food}`);
  dialogVisible.value = false;
};
</script>

<style>
/* 新增食物内容样式 */
.food-content {
  text-align: center;
  /* padding: 1000-px; */
}

.food-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
}

.food-info {
  color: #666;
  font-size: 16px;
  line-height: 1.5;
}

body {
  margin: 0;
}
.example-showcase .el-loading-mask {
  z-index: 9;
}
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

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
</style>