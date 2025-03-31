<template>
  <div class="diet-management">
    <el-button type="primary" @click="handleAdd">新增饮食方式</el-button>

    <el-table :data="dietList" border style="width: 100%">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="formTitle">
      <el-form :model="editForm" :rules="rules" ref="formRef">
        <el-form-item label="名称" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="editForm.code" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import type { FormInstance } from 'element-plus'
import { getAllFoodDietStyleList, addFoodDietStyle,delFoodDietStyle,updateFoodDietStyle } from '~/api/food/foodDietStyleApi'

interface Diet {
  id?: string
  name: string
  code: string
}

const dietList = ref<Diet[]>([])
const dialogVisible = ref(false)
const formTitle = ref('新增饮食方式')
const editForm = reactive<Diet>({ name: '', code: '' })

const rules = reactive({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }]
})

const formRef = ref<FormInstance>()

// 初始化加载数据
const fetchData = async () => {
  // 这里替换为实际API调用
  getAllFoodDietStyleList().then((res) => {
    dietList.value = res.data
  })
}

const handleAdd = () => {
  formTitle.value = '新增饮食方式'
  Object.assign(editForm, { name: '', code: '' })
  dialogVisible.value = true
}

const handleEdit = (row: Diet) => {
  formTitle.value = '编辑饮食方式'
  Object.assign(editForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row:any) => {
  delFoodDietStyle(row.id).then((res) => {
    fetchData()
  })
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (editForm.id) {
    await updateFoodDietStyle(editForm)
  }else{
    await addFoodDietStyle(editForm)
  }
  Object.assign(editForm, { name: '', code: '' })
  dialogVisible.value = false
  fetchData()
}

fetchData()
</script>