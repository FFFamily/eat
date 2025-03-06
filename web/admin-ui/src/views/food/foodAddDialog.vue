<template>
  <el-dialog v-model="dialogVisible" title="添加食物" >
    <el-form :model="newFood" @submit.prevent="createFood">
      <el-form-item label="食物名称">
        <el-input v-model="newFood.name" placeholder="请输入食物名称"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button type="primary" @click="createFood">添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';
import { addFood } from '~/api/food';
import { ElMessage } from 'element-plus'
// 父组件传递的 modelValue 属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
});
const emits = defineEmits(['update:modelValue']);
const newFood = ref({
  name: ''
});
// 对话框显示状态
let dialogVisible = ref(false);
// 监听父组件传递的 modelValue 属性变化
watch(() => props.modelValue, (newValue) => {
  dialogVisible.value = newValue;
});
// 创建食物
const createFood = () => {
    addFood(newFood.value).then(res => {
      ElMessage({message: '创建成功',type: 'success',})
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

<style scoped>
/* 可以在这里添加样式 */
</style>