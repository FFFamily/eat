<template>
  <div class="login_container">
  <!-- <div> -->
    <el-row>
      <el-col :span="12" :xs="0"></el-col>
      <el-col :span="12" :xs="18">
        <el-card class="login_form">
          <h1>今天吃什么</h1>
          <el-form :model="loginForm" :rules="rules" ref="loginForms">
            <el-form-item prop="username">
              <el-input :prefix-icon="User" v-model="loginForm.username" clearable placeholder="Username"
                size="large"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input type="password" :prefix-icon="Lock" show-password v-model="loginForm.password" size="large"
                placeholder="Password" clearable></el-input>
            </el-form-item>
          </el-form>
          <div class="button-container">
            <el-button :loading="loading" class="login-btn" type="primary" size="default" @click="login">
              登录
            </el-button>
            <el-button :loading="loading" class="register-btn" type="primary" size="default" @click="login">
              注册
            </el-button>
          </div >
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup lang="ts">
import { User, Lock, Warning } from '@element-plus/icons-vue'
import { Ref, onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElNotification,ElMessage } from 'element-plus'
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
    $router.push({ path: redirect || '/' })
    loading.value = false
  } catch (error) {
    loading.value = false
  }
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
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: aliceblue;
  background-size: cover;
  position: fixed;
  .login_form {
    position: relative;
    width: 55%;
    top: 25vh;
    left: 10vw;
    padding: 10px;
    background: transparent;
    h1 {
      background: linear-gradient(to right, blue, rgb(35, 60, 70));
      background-clip: text;
      -webkit-text-fill-color: transparent;
      font-size: 40px;
      text-align: center;
      font-weight: 700;
      margin-bottom: 40px;
      margin-top: -10px;
    }

    // .login_btn {
    //   width: 100%;
    // }
  }
}

.el-card {
  box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
}

:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
}
.button-container{
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
  // margin-top: 10px;
}
.login-btn, .register-btn {
  flex: 1; /* 等宽分布 */
  min-width: 100%;
  // padding: 12px 20px;
  // width: 100%;
  margin-left: 0;
}
@media screen and (max-width: 768px) {
  .login_container {
    display: flex;
    justify-content: center;
    align-items: center;
    // padding: 20px;
    box-sizing: border-box;

    .login_form {
      width: 100%;
      max-width: 400px;
      padding: 40px;
      background-color: aqua;
      // background: rgba(255, 255, 255, 0.8);
      border-radius: 10px;
      position: static;
      top: auto;
      left: auto;

      h1 {
        font-size: 32px;
        margin-bottom: 30px;
        margin-top: 0;
      }
      // .button-container {
      //   flex-direction: column; /* 移动端垂直排列 */
      //   gap: 10px;
      // }
    }
  }
}
</style>