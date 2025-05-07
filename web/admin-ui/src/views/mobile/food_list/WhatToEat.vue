<template>
  <!-- <Header></Header> -->
  <div class="what-to-eat-page">
    <!-- 巨大圆形按钮 -->
    <el-button v-loading.fullscreen.lock="isMatching" class="big-round-button" @click="openFoodConfig">今天吃什么</el-button>
    <el-button @click="openFoodConfig">配置</el-button>
    <el-dialog v-model="chonseFoodConfigVisible" :show-close="false" style="width: 90%;height: 20%;" title="">
      <el-form :model="foodSelectConfig" label-width="auto" style="max-width: 600px">
        <el-form-item label="食物类型">
          <el-select v-model="foodSelectConfig.foodDietStyleId" multiple placeholder="Select" style="width: 240px">
            <el-option v-for="item in dietStyleList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div>
          <el-button class="button-center" @click="start">确定好了</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 食物选择对话框 -->
    <el-dialog v-model="dialogVisible" style="width: 90%;height: 50%;" title="选择食物">
      <div class="food-content">
        <h3 class="food-title">{{ selectedFood.name }}</h3>
        <p class="food-info">{{ selectedFood.info }}</p>
      </div>
      <template #footer>
        <el-button @click="selectFood">就这个吧</el-button>
        <el-button @click="dialogVisible = false">再选一次</el-button>
      </template>
    </el-dialog>
    <el-loading :visible="isMatching" text="正在匹配食物..."></el-loading>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import Header from './EatWahtHeader.vue'
import { getRecommendFood, eatFood } from '~/api/user/userFoodApi'
import { getAllFoodDietStyleList } from '~/api/food/foodDietStyleApi'

// 控制对话框显示状态
const dialogVisible = ref(false);
// 控制选择食物配置
const chonseFoodConfigVisible = ref(false);
// 控制是否正在匹配食物
const isMatching = ref(false);
// 选择的食物
const selectedFood = ref({});
// 食物列表改为对象数组
const foodList = ref([]);
// 饮食方式列表
const dietStyleList = ref([]);
// 表单数据
const foodSelectConfig = ref({});

// 打开食物选择对话框
const openFoodConfig = () => {
  chonseFoodConfigVisible.value = true;
  getAllFoodDietStyleList().then(res => {
    dietStyleList.value = res.data;
  })
};

const start = () => {
  isMatching.value = true;
  getRecommendFood(foodSelectConfig.value).then(res => {
    isMatching.value = false;
    if (res.data.length === 0) {
      ElMessage({
        message: '没有匹配的食物',
        type: 'error',
      });
      return
    }
    selectedFood.value = res.data[0];
    dialogVisible.value = true
  })
}

// 选择食物
const selectFood = () => {
  // 这里可以添加选择食物后的逻辑，比如显示提示信息等
  eatFood(selectedFood.value).then(res => {
    dialogVisible.value = false;
  })

};
</script>

<style>
/* 新增食物内容样式 */
.food-content {
  text-align: center;
  /* padding: 1000-px; */
}

.button-center {
  display: block;
  margin-left: auto;
  margin-right: auto;
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
  background-color: #333;
  display: flex;
  justify-content: center;
  align-items: center;
  /* height: 90vh; */
  position: relative;
}

.big-round-button {
  width: 150px;
  height: 150px;
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

/* 添加对话框底部居中样式 */
.custom-dialog .el-dialog__footer {
  padding: 20px;
  display: flex;
  justify-content: center;
  position: absolute;
  bottom: 0;
  width: 100%;
}

/* 确保对话框内容高度 */
.custom-dialog .el-dialog__body {
  position: relative;
  min-height: 300px;
  padding-bottom: 70px;
  /* 给底部留出空间 */
}
</style>