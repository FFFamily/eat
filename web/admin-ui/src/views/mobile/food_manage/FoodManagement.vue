<template>
  <div>
    <FoodManageHeader @refresh="handleRefresh"/>
    <!-- <el-button type="primary" >添加食物</el-button> -->
    <el-table :data="foods" stripe>
      <el-table-column prop="name" label="食物名称"></el-table-column>
      <el-table-column label="操作" fixed="right" min-width="120" width="57">
        <template #default="scope">
          <el-button  @click.prevent="deleteRow(scope.$index)" type="danger" :icon="Delete" circle />
        </template>
      </el-table-column>
    </el-table>
    
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

import { getAllFoods } from '~/api/food';
import { Delete } from '@element-plus/icons-vue';
import FoodManageHeader from './FoodManageHeader.vue';
// 存储食物列表
const foods = ref();
// 从后端获取食物列表
onMounted(() => {
  getAllFoods().then(res => {
    foods.value = res.data
  })
});

const handleRefresh = () => {
  getAllFoods().then(res => {
    foods.value = res.data
  })
}



</script>

<style scoped>
/* 可以在这里添加样式 */
</style>