<template>
  <div>
    <el-button type="primary" @click="dialogVisible = true">新增</el-button>
    <el-button type="primary" @click="dialogVisible = true">添加食物类型</el-button>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="date" label="Date" width="180" />
      <el-table-column prop="name" label="Name" width="180" />
      <el-table-column prop="address" label="Address" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import {ref,reactive,onMounted} from 'vue'
import {getAllFoods} from '~/api/food'
// 列表
const tableData = reactive([])
// 添加数据实体
const food = ref({
  name:'',
  foodTypeId:'',
  status:''
})
const dialogVisible = ref(false)

onMounted(() => {
  getAllFoods().then((res) => {
    console.log(res)
  })
})

const addFood = () => {
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