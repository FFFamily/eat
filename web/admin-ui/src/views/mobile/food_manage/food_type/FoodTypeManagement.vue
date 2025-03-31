<template>
  <div>
    <Header @refresh="handleRefresh"/>
    <!-- <el-button type="primary" >添加食物</el-button> -->
    <el-table :data="foodTypeList" stripe>
      <el-table-column prop="name" label="食物类型"></el-table-column>
      <el-table-column label="操作" fixed="right" min-width="120" width="57">
        <template #default="scope">
          <el-button  @click.prevent="deleteRow(scope.row)" type="danger" :icon="Delete" circle />
        </template>
      </el-table-column>
    </el-table>
    
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getFoodTypeList,delFoodType } from '~/api/food/foodTypeApi';
import { Delete } from '@element-plus/icons-vue';
import Header from './Header.vue';
// 存储食物列表
const foodTypeList = ref([]);
// 从后端获取食物列表
onMounted(() => {
  getList()
});

const handleRefresh = () => {
  getList()
}

const deleteRow = (foodType) => {
  delFoodType(foodType.id).then(res => {
    ElMessage({
      message: '删除成功',
      type: 'success',
    });
    getList()
  })
  
}

const getList = () => {
  getFoodTypeList().then(res => {
    foodTypeList.value = res.data
  })
}

</script>

<style scoped>
/* 可以在这里添加样式 */
</style>