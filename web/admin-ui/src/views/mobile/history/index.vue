<template>
  <div class="diet-calendar">
    <el-calendar v-model="currentDate">
      <template #date-cell="{ data }">
        <div>
          <span>{{ data.day.split('-')[2] }}</span>
          <div v-if="dietData[data.day]" class="diet-info">
            <p v-for="(meal, mealIndex) in dietData[data.day]" :key="mealIndex">{{ meal }}</p>
          </div>
        </div>
      </template>
    </el-calendar>
  </div>
</template>

<script lang="ts" setup>
import { ref, Ref } from 'vue';
import dayjs from 'dayjs';

// 定义 dietData 的类型
type DietData = Record<string, string[]>;

// 使用 ref 定义 currentDate，明确类型为 dayjs.Dayjs
const currentDate: Ref<dayjs.Dayjs> = ref(dayjs());

// 定义 dietData 并明确类型
const dietData: DietData = {
  // 示例数据，可根据实际情况修改
  '2025-03-01': ['早餐：面包、牛奶', '午餐：炒饭', '晚餐：炒菜'],
  '2025-03-10': ['早餐：鸡蛋、粥', '午餐：拉面', '晚餐：寿司']
};
</script>

<style scoped>
.diet-calendar {
  font-family: Arial, sans-serif;
}

.diet-info {
  margin-top: 10px;
  display: flex;
  /* 使用 flexbox 布局 */
  flex-wrap: wrap;
  /* 允许内容换行 */
  gap: 5px;
  /* 元素之间的间距 */
}

.diet-info p {
  margin: 0;
  /* 移除段落的默认边距 */
}
</style>