<template>
  <div class="login_container">
    <el-card class="card-container">
      <el-form ref="loginForms" :model="loginForm" :rules="rules">
        <h1>用户登录</h1>
        <el-form-item label="用户名" prop="username" label-position="top">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password" label-position="top">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password
            size="large" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="login" class="login-btn">
            登录
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" size="large" @click="goToRegister" class="login-btn">
            注册新用户
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { User, Lock, Warning } from '@element-plus/icons-vue'
import { Ref, onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElNotification, ElMessage } from 'element-plus'
import { useUserStore } from '~/store/modules/user';
let $router = useRouter()
let $route = useRoute()
let loading = ref(false)

const identifyCode = ref('1234')
const identifyCodes = ref('1234567890abcdefjhijklinopqrsduvwxyz')


let useStore = useUserStore()
let loginForms = ref()

const loginForm = reactive({
  username: 'admin',
  password: '111111'
})

const validatorUsername = (rule: any, value: any, callback: any) => {
  if (value.length === 0) {
    callback(new Error('请输入账号'))
  } else {
    callback()
  }
}

const validatorPassword = (rule: any, value: any, callback: any) => {
  if (value.length === 0) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 16) {
    callback(new Error('密码应为6~16位的任意组合'))
  } else {
    callback()
  }
}

const login = async () => {
  await loginForms.value.validate()
  loading.value = true
  try {
    await useStore.userLogin(loginForm)
    let redirect: string = $route.query.redirect as string
    $router.push({ path: redirect || '/mobile' })
    loading.value = false
  } catch (error) {
    loading.value = false
  }
}

const goToRegister = () => {
  $router.push({ path: '/register' })
}

const rules = {
  username: [
    {
      trigger: 'change',
      validator: validatorUsername,
    },
  ],
  password: [
    {
      trigger: 'change',
      validator: validatorPassword,
    },
  ],
}
</script>

<style lang="scss" scoped>
.login_container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-container {
  width: 90%;
  padding: 10px;
  h1 {
    text-align: center;
    margin-bottom: 40px;
    background: linear-gradient(45deg, #409EFF, #6c5ce7);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    font-size: 28px;
    font-weight: 600;
  }
}
:deep(.el-input__wrapper) {
    border-radius: 8px;
}


.login-btn{
  flex: 1;
  border-radius: 8px;
}
</style>