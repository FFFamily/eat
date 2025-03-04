<template>
  <div>
    <el-button type="primary" @click="openAddFoodTypeDialog">添加食物类型</el-button>
    <el-table :data="foodTypeList" style="width: 100%">
      <el-table-column prop="name" label="名称" width="180" />
      <el-table-column prop="status" label="状态" width="180" />
    </el-table>
    <FoodTypeDialog v-model:dialog-visible="dialogVisible"></FoodTypeDialog>
  </div>
</template>

<script setup>

import {ref,onMounted} from 'vue'
import {getFoodTypeList} from '~/api/food/foodTypeApi'
import FoodTypeDialog from './foodTypeDialog.vue'
// 列表
let foodTypeList = ref([])
// 添加数据实体
const food = ref({
  name:'',
  foodTypeId:'',
  status:''
})
const dialogVisible = ref(false)

function openAddFoodTypeDialog(){
  dialogVisible.value = true
}

onMounted(() => {
  getFoodTypeList().then((res) => {
    foodTypeList.value = res.data
    console.log(foodTypeList)
  })
})

const addFoodType = () => {
  dialogVisible.value = true
  console.log(food.value)
}
const handleClose = () => {

}

</script>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>