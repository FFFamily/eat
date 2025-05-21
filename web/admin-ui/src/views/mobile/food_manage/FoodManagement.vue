<template>
  <div>
    <FoodManageHeader @refresh="handleRefresh"/>
    <!-- <el-button type="primary" >添加食物</el-button> -->
    <el-table :data="foods" stripe>
      <el-table-column prop="name" label="食物名称">
        <template #default="scope">
          <span @click="handleRowClick(scope.row)">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="120" width="57">
        <template #default="scope">
          <el-button  @click.prevent="deleteRow(scope.$index)" type="danger" :icon="Delete" circle />
        </template>
      </el-table-column>
    </el-table>
    <FoodAddDialog v-model="dialogVisible" :current-food="currentEditFood" @close="handleDialogClose" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import FoodAddDialog from './foodAddDialog.vue';
import { getAllFoods } from '~/api/food';
import { Delete } from '@element-plus/icons-vue';
import FoodManageHeader from './FoodManageHeader.vue';
import {deleteFood} from "~/api/food";
import { ElMessage } from 'element-plus';
// 存储食物列表
const foods = ref();
// 更新开关
const dialogVisible = ref(false);
// 当前编辑的食物
const currentEditFood = ref({});
// 从后端获取食物列表
onMounted(() => {
  getList()
});
const getList = () => {
  getAllFoods().then(res => {
    foods.value = res.data
  })
}

const handleRowClick = (row) => {
  // 处理行点击事件
  console.log('Row clicked:', row);
  dialogVisible.value = true;
  currentEditFood.value = row;
};
const handleDialogClose = () => {
  dialogVisible.value = false;
  getList()
}

const handleRefresh = () => {
  getAllFoods().then(res => {
    foods.value = res.data
  })
}

const deleteRow = (index) => {
  deleteFood(foods.value[index].id).then(res => {
    ElMessage({
      type: 'success',
      message: '删除成功'
    })
    handleRefresh()
  })
}


</script>

<style scoped>
/* 可以在这里添加样式 */
</style>