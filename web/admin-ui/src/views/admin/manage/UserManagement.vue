<template>
  <div>
    <!-- 用户列表 -->
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>
      <el-table :data="users" stripe>
        <el-table-column prop="id" label="ID"></el-table-column>
        <el-table-column prop="name" label="用户名"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加用户表单 -->
    <el-dialog v-model="userAddDialogVisible" title="Tips" width="500" :before-close="handleClose">
      <el-form :model="newUser" @submit.prevent="addUser">
        <el-form-item label="用户名">
          <el-input v-model="newUser.name" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="newUser.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">添加用户</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref,onMounted } from 'vue';
const userAddDialogVisible = ref(false);
// 模拟用户数据
const users = ref([]);
// 新用户数据
const newUser = ref({
  name: '',
  email: ''
});
onMounted(()=>{
  getUsers()
})
const getUserList = () => {
  
}
// 添加用户
const addUser = () => {
  const newId = users.value.length > 0 ? users.value[users.value.length - 1].id + 1 : 1;
  const newUserData = {
    id: newId,
    ...newUser.value
  };
  users.value.push(newUserData);
  newUser.value = {
    name: '',
    email: ''
  };
};

// 删除用户
const deleteUser = (id) => {
  users.value = users.value.filter(user => user.id !== id);
};
</script>

<style scoped>
/* 可根据需要添加自定义样式 */
</style>