<template>
  <div class="not-found-container">
    <div class="error-content">
      <div class="error-code">404</div>
      <h1 class="error-title">页面未找到</h1>
      <p class="error-message">您访问的页面不存在或已被移除</p>
      <el-button 
        type="primary" 
        round 
        @click="$router.push('/')"
        class="back-btn"
      >
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  /* background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); */
}

.error-content {
  text-align: center;
  padding: 40px;
  /* background: white; */
  border-radius: 16px;
  /* box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); */
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  color: #409eff;
  line-height: 1;
  margin-bottom: 20px;
}

.error-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 16px;
}

.error-message {
  font-size: 16px;
  color: #666;
  margin-bottom: 32px;
}

.back-btn {
  padding: 12px 32px;
  font-size: 16px;
}
</style>