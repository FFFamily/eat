<template>
  <div class="diet-calendar">
    <el-calendar ref="calendar">
      <template #header="{ date }">
        <span>{{ date }}</span>
        <el-button-group>
          <el-button size="small" @click="selectDate('prev-month')">
            上个月
          </el-button>
          <el-button size="small" @click="selectDate('next-month')">
            下个月
          </el-button>
        </el-button-group>
      </template>
      <template #date-cell="{ data }">
      
        <!-- 在日期单元格中添加图标 -->
        <div @click="handleDateClick(data.day)">
          <span class="day-number">{{ data.day.split('-')[2] }}</span>
          <el-icon><DishDot /></el-icon>
          <div v-if="dietData[data.day]" class="diet-info">
            <el-icon><DishDot /></el-icon>
            <!-- <p 
              v-for="(meal, mealIndex) in dietData[data.day].slice(0, 3)" 
              :key="mealIndex"
              :data-extra="dietData[data.day].length > 5 ? dietData[data.day].length - 5 : null"
            >
              {{ meal }}
            </p> -->
          </div>
        </div>
      </template>
    </el-calendar>

    <el-drawer v-model="drawerVisible" :direction="'btt'" :with-header="false" size="40%">
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
import { ref, Ref, onMounted } from 'vue';
import dayjs from 'dayjs';
import { getEatHistoryList } from '~/api/user/userFoodHistoryApi'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import {
  DishDot
} from '@element-plus/icons-vue'
// 定义 dietData 的类型
type DietData = Record<string, any[]>;
// 定义 dietData 并明确类型
const dietData: DietData = {};
// 使用 ref 定义 currentDate，明确类型为 dayjs.Dayjs
// const currentDate: Ref<dayjs.Dayjs> = ref(dayjs());
const calendar = ref<CalendarInstance>()
const drawerVisible = ref(false);
const selectedDate = ref('');
onMounted(() => {
  getEatHistoryList().then(res => {
    const history = res.data;
    Object.keys(history).forEach(key => {
      const date = dayjs(key).format('YYYY-MM-DD');
      dietData[date] = history[key].map((_: any) => _.foodName);
    })
    console.log(dietData)
  })
});
// 在setup脚本中添加：
const hasDietRecord = (date: string) => {
  return dietData[date] && dietData[date].length > 0;
};



const handleDateClick = (date: string) => {
  selectedDate.value = date;
  drawerVisible.value = true;
};
const selectDate = (val: CalendarDateType) => {
  if (!calendar.value) return
  calendar.value.selectDate(val)
}

</script>

<style scoped>
.diet-info {
  margin-top: 4px;
  max-height: 90px; /* 固定高度容纳3行 */
  overflow: hidden;
  position: relative;
  line-height: 1.2;
}

.diet-info p {
  margin: 2px 0;
  padding: 2px;
  background: #f0f0f0;
  border-radius: 2px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  max-width: 95%;
  font-size: 10px; /* 调小字体 */
  margin: 1px 0;  /* 减小行间距 */
  padding: 1px 2px;
}

/* 添加超出数量提示 */
.diet-info p:last-child::after {
  content: "+" attr(data-extra);
  position: absolute;
  right: 2px;
  bottom: 0;
  background: #f0f0f0;
  padding: 0 2px;
  border-radius: 2px;
}

/* 调整日历行高 */
.diet-calendar >>> .el-calendar-table__row {
  height: 16vh;
}

.diet-calendar >>> .el-calendar-day {
  padding: 2px !important;
}

/* 调整日历表格布局 */
.diet-calendar >>> .el-calendar-table thead {
  display: none; /* 隐藏星期标题 */
}

.diet-calendar >>> .el-calendar-table__row {
  height: 16vh; /* 根据视口高度分配行高 */
}

.diet-calendar >>> .el-calendar-day {
  display: flex;
  flex-direction: column;
  padding: 2px !important;
  height: 100%;
}
</style>

<style scoped>
/* 新增图标样式 */
.diet-icon {
  width: 18px;
  height: 18px;
  position: absolute;
  top: 2px;
  right: 2px;
}

.day-number {
  position: relative;
  z-index: 1;
}

/* 调整原有饮食信息位置 */
.diet-info {
  margin-top: 18px; /* 增加顶部间距 */
}
</style>