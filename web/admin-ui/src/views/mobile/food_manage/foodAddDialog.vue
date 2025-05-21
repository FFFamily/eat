<template>
  <el-dialog v-model="dialogVisible" width="90%">
    <el-form :model="newFood" @submit.prevent="createFood">
      <el-form-item label="食物名称">
        <el-input style="width: 270px;" v-model="newFood.name" placeholder="请输入食物名称"></el-input>
      </el-form-item>
      <el-form-item label="食物类型">
        <el-select v-model="newFood.foodTypeId" placeholder="请输入饮食方式" style="width: 270px">
          <el-option v-for="item in foodDietStyleList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="饮食方式">
        <el-select v-model="newFood.foodDietStyleList" multiple placeholder="请输入饮食方式" style="width: 270px">
          <el-option v-for="item in foodDietStyleList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button v-if="newFood.id === undefined" type="primary" @click="createFood">添加</el-button>
      <el-button v-else type="primary" @click="handlerUpdateFood">更新</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';
import { addFood, updateFood } from '~/api/food';
import { ElMessage } from 'element-plus'
import { getAllFoodDietStyleList } from '~/api/food/foodDietStyleApi';
// 父组件传递的 modelValue 属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  currentFood: {
    type: Object,
    default: () => ({})
  }
});
const emits = defineEmits(['update:modelValue']);
const newFood = ref({
  name: ''
});
// 对话框显示状态
let dialogVisible = ref(false);
// 饮食方式列表
let foodDietStyleList = ref([]);
// 监听父组件传递的 modelValue 属性变化
watch(() => props.modelValue, (newValue) => {
  dialogVisible.value = newValue;
  newFood.value = { ...props.currentFood }
  getAllFoodDietStyleList().then(res => {
    foodDietStyleList.value = res.data
  })
});
// 创建食物
const createFood = () => {
    // 新增食物
    addFood(newFood.value).then(res => {
      ElMessage({ message: '创建成功', type: 'success', })
    })
    closeDialog();
};

const handlerUpdateFood = () => {
  updateFood(newFood.value).then(res => {
    ElMessage({ message: '更新成功', type:'success', })
  })
  closeDialog();
}
// 关闭对话框并通知父组件更新状态
const closeDialog = () => {
  dialogVisible.value = false;
};
// 处理对话框关闭事件
const handleClose = () => {
  closeDialog();
};
</script>

<style scoped>
::v-deep .el-input__inner {
  font-size: 16px !important;
}
</style>