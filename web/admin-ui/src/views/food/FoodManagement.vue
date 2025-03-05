<template>
    <div>
      <h1>食物管理</h1>
      <el-button type="primary" @click="openAddDialog">添加食物</el-button>
      <el-table :data="foods" stripe>
        <el-table-column prop="name" label="食物名称"></el-table-column>
        <el-table-column prop="price" label="食物价格">
          <template #default="{ row }">
            ${{ row.price }}
          </template>
        </el-table-column>
      </el-table>
      <!-- 使用 v-model 绑定 dialogVisible -->
      <FoodAddDialog v-model="dialogVisible" @add="handleAddFood" />
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import FoodAddDialog from './foodAddDialog.vue';
  
  // 存储食物列表
  const foods = ref([
    { name: '汉堡', price: 10 },
    { name: '披萨', price: 20 },
  ]);
  // 控制对话框显示状态
  const dialogVisible = ref(false);
  // 打开添加食物对话框
  const openAddDialog = () => {
    dialogVisible.value = true;
  };
  // 处理添加食物事件
  const handleAddFood = (newFood) => {
    foods.value.push(newFood);
  };
  </script>
  
  <style scoped>
  /* 可以在这里添加样式 */
  </style>