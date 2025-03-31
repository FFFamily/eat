<template>
  <el-dialog v-model="dialogVisible" width="90%">
    <el-form :model="newFoodType" @submit.prevent="createFoodType">
      <el-form-item label="食物类别名称">
        <el-input v-model="newFoodType.name" placeholder="食物类别名称"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="createFoodType">添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';
import { ElMessage } from 'element-plus'
import { getFoodTypeList ,addFoodType} from '~/api/food/foodTypeApi';
// 父组件传递的 modelValue 属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
});
const emits = defineEmits(['update:modelValue']);
const newFoodType = ref({
  name: ''
});
// 对话框显示状态
let dialogVisible = ref(false);
// 饮食方式列表
let foodTypeList = ref([]);
// 监听父组件传递的 modelValue 属性变化
watch(() => props.modelValue, (newValue) => {
  dialogVisible.value = newValue;
  getFoodTypeList().then(res => {
    foodTypeList.value = res.data
  })
});
// 创建食物
const createFoodType = () => {
  addFoodType(newFoodType.value).then(res => {
    ElMessage({ message: '创建成功', type: 'success', })
    closeDialog();
  })
};
// 关闭对话框并通知父组件更新状态
const closeDialog = () => {
  dialogVisible.value = false;
};
// 处理对话框关闭事件
const handleClose = () => {
  closeDialog();
};
</script>

<style scoped></style>