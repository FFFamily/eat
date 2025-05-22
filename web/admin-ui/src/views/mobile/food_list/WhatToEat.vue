<template>
  <!-- <Header></Header> -->
  <div class="what-to-eat-page">
    <!-- 巨大圆形按钮 -->
    <el-button v-loading.fullscreen.lock="isMatching" class="big-round-button" @click="generateFood">今天吃什么</el-button>
    <el-button @click="openFoodConfig">配置</el-button>
    <div v-for="(food, index) in foodList" :key="index" :style="getTempFoodStyle(index)" class="temp-food">
      {{ food }}
    </div>
    <!-- <div :style="getTempFoodStyle(index)" class="temp-food">
      红烧牛肉
    </div> -->
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
// 食物列表
const foodList = ref([]);
// 饮食方式列表
const dietStyleList = ref([]);
// 表单数据
const foodSelectConfig = ref({
  foodNum: 1
});

// 打开食物选择对话框
const openFoodConfig = () => {
  chonseFoodConfigVisible.value = true;
  getAllFoodDietStyleList().then(res => {
    dietStyleList.value = res.data;
  })
};
// 开始匹配食物
const generateFood = async () => {
  // isMatching.value = true;
  foodSelectConfig.value.foodNum = 10;
  const tempFoods = ref([]);
  // const foods = document.getElementsByClassName('temp-food')
  // foods[0].style.left = '100px'
  // foods[0].style.top = '200px'
  // foods[0].classList.add('animate');
  
  console.log()
  getRecommendFood(foodSelectConfig.value).then(res => {
    // isMatching.value = false;
    if (res.data.length === 0) {
      ElMessage({
        message: '没有匹配的食物',
        type: 'error',
      });
      return
    }
    tempFoods.value = res.data;
    selectedFood.value = tempFoods.value[0];
    console.log(selectedFood.value)
    // foodList.value = res.data;
    
    for (let i = 0; i < tempFoods.value.length; i++) {

      foodList.value.push(tempFoods.value[i])
      setTimeout(500); // 等待1秒
      const foods = document.getElementsByClassName('temp-food');
      console.log(foods[i])
      if (foods[i]) {
        foods[i].style.left = '100px';
        foods[i].style.top = '100px';
        foods[i].classList.add('animate');
      }
    }
    // await new Promise(resolve => setTimeout(resolve, 2000));
    // setTimeout(resolve, 2000)
    // dialogVisible.value = true
  })
}

const getTempFoodStyle = (index) => {
  const randomX = Math.random() * 100 - 50; // 左右随机偏移（-50px ~ 50px）
  return {
    left: `calc(50% + ${randomX}px)`,
    top: `calc(50% + ${index * 40}px)` // 每个食物垂直间隔40px
  };
}

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
  /* left: 100px;
  top: 100px; */
  font-size: 20px;
  color: #333;
  opacity: 0;
  /* 初始透明度为0 */
  transition: all 1s ease;
  /* 控制动画速度 */
  pointer-events: none;
  /* 防止遮挡按钮点击 */
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