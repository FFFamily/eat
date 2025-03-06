<template>
  <div>
    <!-- <h1>食物管理</h1> -->
    <el-button type="primary" @click="openAddDialog">添加食物</el-button>
    <el-table :data="foods" stripe>
      <el-table-column prop="name" label="食物名称"></el-table-column>
      <el-table-column label="操作" fixed="right" min-width="120" width="200">
        <template #default="scope">
          <el-button link type="primary" size="small" @click.prevent="deleteRow(scope.$index)">
            Remove
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 使用 v-model 绑定 dialogVisible -->
    <FoodAddDialog v-model="dialogVisible" @close="handleDialogClose" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import FoodAddDialog from './foodAddDialog.vue';
import { getAllFoods } from '~/api/food'
// 存储食物列表
const foods = ref();
// 控制对话框显示状态
const dialogVisible = ref(false);
// 从后端获取食物列表
onMounted(() => {
  getAllFoods().then(res => {
    foods.value = res.data
  })
});
// 打开添加食物对话框
const openAddDialog = () => {
  dialogVisible.value = true;
};
const handleDialogClose = () => {
  getAllFoods().then(res => {
    foods.value = res.data
  })
};

</script>

<style scoped>
/* 可以在这里添加样式 */
</style>