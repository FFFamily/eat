<template>
  <el-dialog v-model="dialogVisible" title="{{ currentTag.id ? '编辑标签' : '添加标签' }}" width="90%">
    <el-form :model="currentTag" ref="tagForm" :rules="rules">
      <el-form-item label="标签名称" prop="name">
        <el-input v-model="currentTag.name" placeholder="请输入标签名称"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';
import { createFoodTag } from '~/api/food/foodTagApi';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  currentTag: { type: Object, default: () => ({}) }
});

const emits = defineEmits(['update:modelValue', 'close']);
const dialogVisible = ref(false);
const tagForm = ref(null);
const currentTag = ref({});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
};

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val;
  if (val) {
    currentTag.value = { ...props.currentTag };
  }
});

watch(dialogVisible, (val) => {
  emits('update:modelValue', val);
});

const submitForm = () => {
  tagForm.value.validate((valid) => {
    if (valid) {
      if (currentTag.value.id) {
        updateTag(currentTag.value).then(() => {
          ElMessage.success('更新成功');
          dialogVisible.value = false;
          emits('close');
        });
      } else {
        createTag(currentTag.value).then(() => {
          ElMessage.success('创建成功');
          dialogVisible.value = false;
          emits('close');
        });
      }
    }
  });
};
</script>