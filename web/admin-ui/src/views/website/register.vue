<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="register-title">用户注册</h2>
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请确认密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" @click="handleRegister">注册</el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
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
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
});

const registerRules = ref({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

const registerFormRef = ref<InstanceType<typeof ElForm>>();

const handleRegister = async () => {
  if (!registerFormRef.value) return;
  try {
    await registerFormRef.value.validate();
    // 这里添加注册API调用
    ElMessage.success('注册成功');
    router.push('/login');
  } catch (error) {
    ElMessage.error('请填写正确的注册信息');
  }
};
</script>

<style scoped lang="scss">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.register-card {
  width: 400px;
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.register-form {
  margin-bottom: 1rem;
}

.register-btn {
  width: 100%;
}

.login-link {
  text-align: center;
  margin-top: 1rem;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}
</style>