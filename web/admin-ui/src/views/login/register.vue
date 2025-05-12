<template>
    <div class="register-container">
      <el-card class="register-card">
        <!-- 标题部分 -->
        <h1 class="gradient-title">注册新账号</h1>
        
        <!-- 注册表单 -->
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
          <!-- 用户名输入 -->
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              clearable
            />
          </el-form-item>
  
          <!-- 密码输入 -->
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              size="large"
              clearable
            />
          </el-form-item>
  
          <!-- 确认密码 -->
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              prefix-icon="Check"
              show-password
              size="large"
              clearable
            />
          </el-form-item>
  
          <!-- 注册按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              @click="handleRegister"
              class="register-btn"
            >
              立即注册
            </el-button>
          </el-form-item>
        </el-form>
  
        <!-- 底部提示 -->
        <div class="footer-tip">
          已有账号？<el-link type="primary" @click="goToLogin">去登录</el-link>
        </div>
      </el-card>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import type { FormInstance } from 'element-plus'
  import {register} from '~/api/user/index'

  const router = useRouter()
  const registerFormRef = ref<FormInstance>()
  
  // 表单数据
  const registerForm = reactive({
    username: '',
    password: '',
    confirmPassword: ''
  })
  
  // 验证规则
  const validatePassword = (rule: any, value: any, callback: any) => {
    if (value !== registerForm.password) {
      callback(new Error('两次输入密码不一致'))
    } else {
      callback()
    }
  }
  
  const rules = {
    username: [
      { required: true, message: '用户名不能为空', trigger: 'blur' },
      { min: 4, max: 16, message: '长度应为4-16个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '密码不能为空', trigger: 'blur' },
      { min: 6, max: 16, message: '长度应为6-16个字符', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请确认密码', trigger: 'blur' },
      { validator: validatePassword, trigger: 'blur' }
    ]
  }
  
  // 注册处理
  const handleRegister = async () => {
    try {
      await registerFormRef.value?.validate()
      // 这里添加注册API调用
      register(registerForm).then((res:any)=>{
        router.push('/login')
      })
    } catch (error) {
      console.log('表单验证失败')
    }
  }
  
  // 跳转登录
  const goToLogin = () => {
    router.push('/login')
  }
  </script>
  
  <style lang="scss" scoped>
  .register-container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  .register-card {
    width: 100%;
    // max-width: 450px;
    // padding: 40px 30px;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  }
  
  .gradient-title {
    text-align: center;
    margin-bottom: 40px;
    background: linear-gradient(45deg, #409EFF, #6c5ce7);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    font-size: 28px;
    font-weight: 600;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 8px;
  }
  
  .register-btn {
    width: 100%;
    height: 45px;
    font-size: 16px;
    border-radius: 8px;
  }
  
  .footer-tip {
    text-align: center;
    color: #666;
    margin-top: 20px;
    
    .el-link {
      vertical-align: baseline;
    }
  }
  
  @media (max-width: 768px) {
    .register-card {
      margin: 20px;
      padding: 25px 20px;
    }
    
    .gradient-title {
      font-size: 24px;
      margin-bottom: 30px;
    }
  }
  </style>