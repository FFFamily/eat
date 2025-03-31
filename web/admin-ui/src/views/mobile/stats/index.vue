<template>
  <div class="stats-container">
    <el-date-picker
      v-model="dateRange"
      type="daterange"
      range-separator="-"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      @change="updateCharts"
    />
    
    <div class="chart-wrapper">
      <div ref="barChart" class="chart" style="height: 400px;"></div>
    </div>
    <div class="chart-wrapper">
      <div ref="pieChart" class="chart" style="height: 400px;"></div>
    </div>
  
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import dayjs from 'dayjs';

type DietData = Record<string, string[]>;

// 示例数据
const dietData: DietData = {
  '2025-03-01': ['早餐：面包、牛奶', '午餐：炒饭', '晚餐：炒菜'],
  '2025-03-10': ['早餐：鸡蛋、粥', '午餐：拉面', '晚餐：寿司']
};

const dateRange = ref<[Date, Date]>([new Date(2025, 2, 1), new Date(2025, 2, 31)]);
const barChart = ref<HTMLElement>();
const pieChart = ref<HTMLElement>();
let barInstance: echarts.ECharts;
let pieInstance: echarts.ECharts;

const processData = () => {
  const mealCount = {
    早餐: 0,
    午餐: 0,
    晚餐: 0
  };

  Object.values(dietData).forEach(meals => {
    meals.forEach(meal => {
      const type = meal.split('：')[0];
      // 修改为使用类型断言确保可以通过 string 类型的键访问对象
      if (mealCount[type as keyof typeof mealCount]) {
        // 使用类型断言确保可以通过 string 类型的键访问对象
        (mealCount as Record<string, number>)[type]++;
      }
    });
  });

  return {
    barData: Object.keys(mealCount).map(type => ({
      name: type,
      value:mealCount[type as keyof typeof mealCount]
    })),
    pieData: Object.entries(mealCount)
  };
};

const initCharts = () => {
  if (barChart.value && pieChart.value) {
    barInstance = echarts.init(barChart.value);
    pieInstance = echarts.init(pieChart.value);
    updateCharts();
  }
};

const updateCharts = () => {
  const { barData, pieData } = processData();

  barInstance.setOption({
    title: { text: '餐次分布统计' },
    xAxis: { type: 'category', data: barData.map(d => d.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: barData }]
  });

  pieInstance.setOption({
    title: { text: '用餐类型占比' },
    series: [{
      type: 'pie',
      data: pieData.map(([name, value]) => ({ name, value })),
      radius: '50%'
    }]
  });
};

onMounted(() => {
  initCharts();
  window.addEventListener('resize', () => {
    barInstance.resize();
    pieInstance.resize();
  });
});
</script>

<style scoped>
/* .stats-container {
  padding: 20px;
} */

.chart-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.chart {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>