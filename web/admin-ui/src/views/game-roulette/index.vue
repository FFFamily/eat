<template>
  <div class="roulette-container">
    <h1 class="title">游戏随机选择轮盘</h1>

    <!-- 轮盘容器 -->
    <div class="roulette-wrapper">
      <!-- 指针 -->
      <div class="pointer"></div>
      
      <!-- 轮盘 -->
      <div class="roulette" :style="rouletteStyle">
        <div class="roulette-item" v-for="(game, index) in games" :key="index" :style="getItemStyle(index)">
          <div class="game-item">
            {{ game }}
          </div>
        </div>
      </div>
    </div>

    <!-- 控制按钮 -->
    <el-button 
      class="start-button"
      :disabled="isSpinning"
      @click="spinRoulette"
      :icon="isSpinning ? Loading : Refresh"
    >
      {{ isSpinning ? '转动中...' : '开始转动' }}
    </el-button>

    <!-- 结果提示 -->
    <el-dialog v-model="resultVisible" title="选择结果" width="30%">
      <div class="result-content">
        <p>恭喜！你被选中游玩：</p>
        <h2>{{ selectedGame }}</h2>
      </div>
      <template #footer>
        <el-button @click="resultVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElDialog, ElButton } from 'element-plus';
import { Refresh, Loading } from '@element-plus/icons-vue';

// 游戏列表（可自定义修改）
const games = ref([
  '英雄联盟', '王者荣耀', '原神', 'CS:GO', 'DOTA2', 
  '塞尔达传说'
]);

// 轮盘状态
const isSpinning = ref(false);
const rotationAngle = ref(0);
const resultVisible = ref(false);
const selectedGame = ref('');

// 计算轮盘样式
const rouletteStyle = computed(() => ({
  transform: `rotate(${rotationAngle.value}deg)`,
  transition: isSpinning.value ? 'transform 5s cubic-bezier(0.2, 0.8, 0.2, 1)' : 'none'
}));

// 计算每个游戏项的样式
const getItemStyle = (index: number) => {
  const angle = 360 / games.value.length;
  console.log(index * angle);
  return {
    transform: `rotate(${index * angle}deg) skewY(-30deg)`,
    // 删除错误的height角度单位设置
  };
};

// 随机生成旋转角度
const generateRandomAngle = () => {
  // 最少旋转5圈，最多10圈，加上随机角度
  return 1800 + Math.floor(Math.random() * 1800) + Math.floor(Math.random() * 360);
};

// 计算选中的游戏
const calculateSelectedGame = (angle: number) => {
  const normalizedAngle = angle % 360;
  const anglePerItem = 360 / games.value.length;
  // 计算选中的索引（指针位置在顶部，所以需要调整角度计算）
  const selectedIndex = Math.floor((360 - normalizedAngle) / anglePerItem) % games.value.length;
  return games.value[selectedIndex];
};

// 旋转轮盘
const spinRoulette = () => {
  if (games.value.length < 2) {
    ElMessage.warning('请至少添加2个游戏选项');
    return;
  }

  isSpinning.value = true;
  const newAngle = generateRandomAngle();
  rotationAngle.value = newAngle;

  // 动画结束后显示结果
  setTimeout(() => {
    isSpinning.value = false;
    selectedGame.value = calculateSelectedGame(newAngle);
    resultVisible.value = true;
  }, 5000); // 与transition动画时间一致
};

// 页面加载时检查游戏列表
onMounted(() => {
  if (games.value.length === 0) {
    ElMessage.info('请在代码中添加游戏选项');
  }
});
</script>

<style scoped lang="scss">

.roulette-container {
  width: 100%;
  margin: 0;
  padding: 20px;
  text-align: center;
  border: 1px solid #333;
}

.title {
  color: #333;
  margin-bottom: 40px;
}

.roulette-wrapper {
  position: relative;
  width: 400px;
  height: 400px;
  margin: 0 auto 40px;
}

.roulette {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 50%;
  border: 2px solid #333;
  overflow: hidden;
}

.roulette-item {
  width: 100%;
	height: 100%;
	position: absolute;
	left: 50%;
	top: -50%;
  transform-origin: 0% 100%;  
}
.game-item{
  font-size: 20;
}

/* 轮盘扇区颜色交替 */
.roulette-item:nth-child(even) {
  background-color: #409eff;
}

.roulette-item:nth-child(odd) {
  background-color: #67c23a;
}

/* 指针样式 */
.pointer {
  position: absolute;
  top: -20px;
  left: 50%;
  width: 30px;
  height: 30px;
  background-color: #f56c6c;
  clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
  transform: translateX(-50%);
  z-index: 10;
}

.start-button {
  padding: 12px 30px;
  font-size: 18px;
}

.result-content {
  text-align: center;
  padding: 20px 0;
}

.result-content h2 {
  color: #409eff;
  font-size: 28px;
  margin: 20px 0;
}
</style>