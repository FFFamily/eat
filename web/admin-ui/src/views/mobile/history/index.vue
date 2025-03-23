<template>
  <div class="diet-calendar">
    <!-- <el-calendar v-model="currentDate">
      <template #date-cell="{ data }">
        <div @click="handleDateClick(data.day)">
          <span>{{ data.day.split('-')[2] }}</span>
          <div v-if="dietData[data.day]" class="diet-info">
            <p v-for="(meal, mealIndex) in dietData[data.day]" :key="mealIndex">{{ meal }}</p>
          </div>
        </div>
      </template>
    </el-calendar> -->

    <el-drawer
      v-model="drawerVisible"
      :direction="'btt'"
      :with-header="false"
      size="40%">
      <div class="drawer-content">
        <h3>{{ selectedDate }} 饮食记录</h3>
        <div v-if="dietData[selectedDate]" class="diet-details">
          <p v-for="(meal, index) in dietData[selectedDate]" :key="index">{{ meal }}</p>
        </div>
        <p v-else class="no-data">当天没有饮食记录</p>
      </div>
    </el-drawer>
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

const drawerVisible = ref(false);
const selectedDate = ref('');

const handleDateClick = (date: string) => {
  selectedDate.value = date;
  drawerVisible.value = true;
};
</script>

<style scoped>
.drawer-content {
  padding: 20px;
}

.diet-details {
  margin-top: 15px;
  max-height: 300px;
  overflow-y: auto;
}

.no-data {
  color: #999;
  text-align: center;
  margin-top: 20px;
}

.diet-calendar {
  font-family: Arial, sans-serif;
}

.diet-info {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  gap: 5px;
  max-height: 120px;
  overflow-y: auto;
  padding-right: 8px;
}

.diet-info p {
  margin: 0;
  font-size: 12px;
  line-height: 1.4;
  padding: 2px 5px;
  background: #f5f7fa;
  border-radius: 3px;
  white-space: nowrap;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  word-break: break-all;
}
</style>