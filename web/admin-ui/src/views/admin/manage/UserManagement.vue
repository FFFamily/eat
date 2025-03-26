<template>
  <div>
    <!-- 用户列表 -->
    <el-card>
      <template #header>
        <span>用户管理</span>
        <!-- 查询输入框 -->
        <el-input v-model="searchKeyword" placeholder="请输入用户名或账号查询" @keyup.enter="searchUsers"
          style="width: 200px; margin-left: 20px;"></el-input>
        <el-button type="primary" @click="searchUsers" style="margin-left: 10px;">查询</el-button>
        <el-button type="success" @click="openAddUserDialog">新增用户</el-button>
      </template>
      <el-table :data="users" stripe>
        <el-table-column prop="id" label="ID"></el-table-column>
        <el-table-column prop="nickname" label="用户名"></el-table-column>
        <el-table-column prop="username" label="账号"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" @click="openEditUserDialog(scope.row)">编辑</el-button>
            <el-button type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加用户表单 -->
    <el-dialog v-model="userAddDialogVisible" title="添加用户" width="500" :before-close="handleClose">
      <el-form :model="newUser" @submit.prevent="addUser">
        <el-form-item label="用户名">
          <el-input v-model="newUser.nickname" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="newUser.username" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">添加用户</el-button>
          <el-button @click="userAddDialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { findUserList, createUser, updateUser, removeUser } from '~/api/user/index.ts';
import { ElMessage } from 'element-plus';
// 对话框显示状态
const userAddDialogVisible = ref(false);
// 用户列表
const users = ref([]);
// 新用户数据
const newUser = ref({
  nickname: '',
  username: '',
});
// 编辑用户数据
const editUser = ref({
  id: 0,
  nickname: '',
  username: '',
  email: ''
});
// 查询关键词
const searchKeyword = ref('');

onMounted(() => {
  getUserList();
});

// 获取用户列表
const getUserList = () => {
  findUserList().then(res => {
    users.value = res.data;
  });
};

// 搜索用户
const searchUsers = () => {
  // 这里可以调用后端接口进行搜索，暂时使用前端过滤代替
  if (searchKeyword.value) {
    findUserList().then(res => {
      users.value = res.data.filter(user => {
        return user.nickname.includes(searchKeyword.value) || user.username.includes(searchKeyword.value);
      });
    });
  } else {
    getUserList();
  }
};

// 打开添加用户对话框
const openAddUserDialog = () => {
  newUser.value = {
    nickname: '',
    username: '',
    email: ''
  };
  userAddDialogVisible.value = true;
};

// 添加用户
const addUser = () => {
  addUserApi(newUser.value).then(() => {
    userAddDialogVisible.value = false;
    getUserList();
  });
};

// 打开编辑用户对话框
const openEditUserDialog = (user) => {
  console.log(user);
  newUser.value = { ...user };
  userAddDialogVisible.value = true;
};

// 更新用户
const updateUser = () => {
  updateUserApi(newUser.value).then(() => {
    userAddDialogVisible.value = false;
    getUserList();
  });
};

// 删除用户
const deleteUser = (id) => {
  console.log(id);
  if (id == 1) {
    ElMessage.error('不能删除管理员');
    return;
  }
  // deleteUserApi(id).then(() => {
  //   ElMessage.success('删除成功');
  //   getUserList();
  // });
};

// 关闭对话框
const handleClose = () => {
  userAddDialogVisible.value = false;
};
</script>

<style scoped>
/* 可根据需要添加自定义样式 */
</style>