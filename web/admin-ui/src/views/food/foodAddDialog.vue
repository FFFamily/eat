<template>
  <el-dialog
    v-model="dialogVisible"
    title="添加食物"
    @close="handleClose"
  >
    <template #content>
      <el-form :model="newFood" @submit.prevent="createFood">
        <el-form-item label="食物名称">
          <el-input v-model="newFood.name" placeholder="请输入食物名称"></el-input>
        </el-form-item>
        <el-form-item label="食物价格">
          <el-input v-model.number="newFood.price" type="number" placeholder="请输入食物价格"></el-input>
        </el-form-item>
      </el-form>
    </template>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button type="primary" @click="createFood">添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits,watch } from 'vue';

// 定义 v-model 绑定的属性和事件
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
});

const emits = defineEmits(['update:modelValue', 'add']);

// 对话框显示状态
let dialogVisible = ref(false);
// 监听父组件传递的 modelValue 属性变化
watch(() => props.modelValue, (newValue) => {
    debugger
  dialogVisible.value = newValue;
});


// 新食物对象
const newFood = ref({ name: '', price: 0 });

// 创建食物
const createFood = () => {
  if (newFood.value.name && newFood.value.price > 0) {
    // 向父组件发送添加成功的消息，并传递新食物对象
    emits('add', { ...newFood.value });
    newFood.value = { name: '', price: 0 };
    closeDialog();
  }
};

// 关闭对话框并通知父组件更新状态
const closeDialog = () => {
  dialogVisible.value = false;
  emits('update:modelValue', false);
};


// 处理对话框关闭事件
const handleClose = () => {
  closeDialog();
};
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>