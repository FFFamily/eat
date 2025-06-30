<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">用户登录</h2>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();
const loginForm = ref({
  username: '',
  password: ''
});

const loginRules = ref({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

const loginFormRef = ref<InstanceType<typeof ElForm>>();

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  try {
    await loginFormRef.value.validate();
    // 这里添加登录API调用
    ElMessage.success('登录成功');
    router.push('/');
  } catch (error) {
    ElMessage.error('请填写正确的登录信息');
  }
};
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.login-form {
  margin-bottom: 1rem;
}

.login-btn {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 1rem;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}
</style>