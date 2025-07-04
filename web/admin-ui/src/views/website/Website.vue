<template>
  <div class="home-container">
    <header class="header">
      <div class="title">nCalendar</div>
      <div class="subtitle">日历提醒同步系统</div>
      <div class="desc">一个集成 Notion 模板、日历与本地提醒的自动化时间管理工具</div>
      <div class="actions">
        <el-button type="primary">登录/注册</el-button>
        <el-button>快速入门</el-button>
      </div>
    </header>
    <section class="about-me">
  <h2>关于本站</h2>
  <p>
    你好！我是图图，这里是我的个人官网。本站致力于分享高效的时间管理工具、前端开发经验与生活感悟。<br>
    你可以在这里体验我的开源项目、了解我的技术栈，也欢迎通过下方方式与我联系交流！
  </p>
  <div class="contact-links">
    <a href="mailto:your@email.com">邮箱</a>
    <a href="https://github.com/yourname" target="_blank">GitHub</a>
    <a href="https://yourblog.com" target="_blank">博客</a>
  </div>
</section>
    <section class="features">
      <div
        class="feature-card"
        v-for="item in features"
        :key="item.title"
        @click="goFeature(item.route)"
      >
        <div class="icon">{{ item.icon }}</div>
        <div class="feature-title">{{ item.title }}</div>
        <div class="feature-desc">{{ item.desc }}</div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const features = [
  { icon: '🚀', title: '配置简单', desc: '使用 Notion 公共模板集成，快速完成配置流程。', route: '/setup' },
  { icon: '🎯', title: '精准提醒', desc: '自动同步到期信息，提供本地提醒。', route: '/reminder' },
  { icon: '😁', title: '数据隐私', desc: 'SaaS 架构，保障用户数据安全。', route: '/privacy' },
  { icon: '🧠', title: '智能联动', desc: '从 Notion 到日历的自动桥接。', route: '/smart-link' },
  { icon: '❤️', title: '个性定制', desc: '模板驱动，样式自定义。', route: '/custom' },
  { icon: '🔄', title: '双向同步', desc: '支持本地日历修改同步回 Notion。', route: '/sync' }
]

function goFeature(route: string) {
  router.push(route)
}
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.header {
  text-align: center;
  margin-top: 80px;
  .title {
    font-size: 2.8rem;
    font-weight: bold;
    color: #2563eb;
  }
  .subtitle {
    font-size: 1.5rem;
    margin-top: 1rem;
    color: #222;
  }
  .desc {
    color: #888;
    margin-top: 0.5rem;
    font-size: 1.1rem;
  }
  .actions {
    margin-top: 2rem;
    button {
      margin: 0 0.5rem;
    }
  }
}
.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 2rem;
  margin: 60px 0 0 0;
  width: 80%;
  .feature-card {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 12px #0001;
    padding: 2rem 1.5rem;
    text-align: center;
    .icon {
      font-size: 2.2rem;
      margin-bottom: 1rem;
    }
    .feature-title {
      font-weight: 600;
      font-size: 1.2rem;
      margin-bottom: 0.5rem;
    }
    .feature-desc {
      color: #666;
      font-size: 1rem;
    }
    transition: transform 0.2s cubic-bezier(.34,1.56,.64,1);
    cursor: pointer;
    &:hover {
      transform: scale(1.07);
      box-shadow: 0 6px 24px #0002;
    }
  }
}
.about-me {
  margin: 60px auto 0 auto;
  max-width: 700px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #0001;
  padding: 2.5rem 2rem;
  text-align: center;
  h2 {
    font-size: 1.7rem;
    color: #2563eb;
    margin-bottom: 1rem;
  }
  p {
    color: #444;
    font-size: 1.1rem;
    line-height: 2;
    margin-bottom: 1.5rem;
  }
  .contact-links {
    a {
      display: inline-block;
      margin: 0 1rem;
      color: #2563eb;
      text-decoration: none;
      font-weight: 500;
      transition: color 0.2s;
      &:hover {
        color: #1e40af;
        text-decoration: underline;
      }
    }
  }
}
</style>