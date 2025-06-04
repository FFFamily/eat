<template>
  <div class="diet-history-page">
    <!-- 日期筛选 -->
    <el-date-picker v-model="filterDate" type="month" placeholder="选择日期" value-format="YYYY-MM-DD HH:mm:ss"
      style="margin: 15px; width: 90%" @change="handleDateChange" />

    <!-- 饮食记录列表 -->
    <div class="record-list">
      <el-card v-for="(record, index) in dietRecords" :key="index" class="record-card">
        <div class="record-header">
          <span class="record-date">{{ record.date }}</span>
          <span class="record-meal-type">{{ record.mealType }}</span>
        </div>
        <div class="record-content">
          <p class="food-name">{{ record.foodName }}</p>
        </div>
      </el-card>
    </div>

    <!-- 无记录提示 -->
    <div v-if="dietRecords.length === 0" class="no-record">
      暂无该日期的饮食记录
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getQueryEatHistoryList } from '~/api/user/userFoodHistoryApi'; 
// 筛选日期
const filterDate = ref<string>('');
// 饮食记录列表
const dietRecords = ref<any[]>([]);

// 获取饮食历史数据
const fetchDietRecords = async (date: any) => {
  const res = await getQueryEatHistoryList(date);
  dietRecords.value = res.data.map((item: any) => ({
    date: item.createTime, // 格式化为"2024-05-20"
    mealType: item.mealType, // 早餐/午餐/晚餐
    foodName: item.foodName,
    foodType: item.foodType,
    calorie: item.calorie // 可选热量数据
  }));
};

// 日期筛选回调
const handleDateChange = (date: string) => {
  console.log('选择的日期：', date);
  fetchDietRecords(date);
};

// 初始化加载今日数据
onMounted(() => {
  const today = new Date();
  fetchDietRecords(today);
});
</script>

<style scoped lang="scss">
.diet-history-page {
  padding-bottom: 60px;
  /* 避免被底部导航遮挡 */
}

.record-list {
  padding: 0 15px;
}

.record-card {
  margin-bottom: 12px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.record-content {
  .food-name {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 4px;
  }

  .food-type {
    color: #999;
    font-size: 13px;
    margin-bottom: 8px;
  }
}

.no-record {
  text-align: center;
  color: #999;
  margin-top: 20px;
  font-size: 14px;
}
</style>