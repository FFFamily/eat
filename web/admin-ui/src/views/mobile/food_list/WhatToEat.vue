<template>
  <!-- <Header></Header> -->
  <div class="what-to-eat-page">
    <!-- 巨大圆形按钮 -->
    <el-button v-loading.fullscreen.lock="isMatching" class="big-round-button" @click="generateFood">今天吃什么</el-button>
    <el-button @click="openFoodConfig">配置</el-button>

    <div v-for="(food, index) in foodList" :key="index" class="temp-food">
      {{ food.name }}
    </div>
    <el-dialog v-model="chonseFoodConfigVisible" :show-close="false" style="width: 90%;height: 20%;" title="">
      <el-form :model="foodSelectConfig" label-width="auto" style="max-width: 600px">
        <el-form-item label="饮食习惯">
          <el-select v-model="foodSelectConfig.foodHabitId" multiple placeholder="Select" style="width: 240px">
            <el-option v-for="item in foodHabitList" :key="item.id" :label="item.name" :value="item.id" />
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
import { de } from 'element-plus/es/locales.mjs';
import { getAllFoodHabits } from '~/api/food/foodHabitApi'

// 控制对话框显示状态
const dialogVisible = ref(false);
// 控制选择食物配置
const chonseFoodConfigVisible = ref(false);
// 控制是否正在匹配食物
const isMatching = ref(false);
// 选择的食物
const selectedFood = ref({});
// 食物列表
const foodList = ref([]);
// 饮食习惯列表
const foodHabitList = ref([]);
// 表单数据
const foodSelectConfig = ref({
  foodNum: 1
});

// 打开食物选择对话框
const openFoodConfig = () => {
  chonseFoodConfigVisible.value = true;
  getAllFoodHabits().then(res => {
    foodHabitList.value = res.data;
  })
};
// 开始匹配食物
const generateFood = async () => {
  // isMatching.value = true;
  foodSelectConfig.value.foodNum = 10;
  const tempFoods = ref([]);
  await getRecommendFood(foodSelectConfig.value).then(async res => {
    // isMatching.value = false;
    if (res.data.length === 0) {
      ElMessage({
        message: '没有匹配的食物',
        type: 'error',
      });
      return
    }
    tempFoods.value = res.data;
    for (let i = 0; i < tempFoods.value.length; i++) {
      foodList.value.push(tempFoods.value[i])
      await new Promise(resolve => setTimeout(resolve, 500));
      const foods = document.getElementsByClassName('temp-food');
      if (foods[i]) {
        foods[i].classList.add('animate');
        const randomX = Math.random() * 100 + "%"; // 左右随机位置（0%~100%）
        const randomY = Math.random() * 100 + "%"; // 上下随机位置（0%~100%）
        foods[i].style.left = randomX
        foods[i].style.top = randomY
      }
    }
    selectedFood.value = tempFoods.value[0];
    foodList.value = []
    dialogVisible.value = true;
  })
}

// 修改后的随机定位函数
// const getTempFoodStyle = () => {
//   // 生成 0-100 之间的随机数（百分比），覆盖父容器的可见区域
//   const randomX = Math.random() * 50; // 左右随机位置（0%~100%）
//   const randomY = Math.random() * 50; // 上下随机位置（0%~100%）
//   return {
//     left: `${randomX}%`,
//     top: `${randomY}%`
//   };
// };

const start = () => {
  chonseFoodConfigVisible.value = false;
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
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.big-round-button {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  font-size: 24px;
  background-color: #409EFF;
  color: white;
  border: none;
  margin: 20px 20px 20px 30px;
}

.temp-food {
  position: absolute;
  font-size: 20px;
  color: #333;
  opacity: 0;
  /* 初始透明度为0 */
  transition: all 1s ease;
  /* 控制动画速度 */
  pointer-events: none;
  /* 防止遮挡按钮点击 */
}

.temp-food-back {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: aquamarine;
}

/* 动画类：淡入 + 移动 */
.temp-food.animate {
  opacity: 1;
  transform: translateY(-20px);
  /* 向上移动20px */
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