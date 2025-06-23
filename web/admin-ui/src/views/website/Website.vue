<template>
    <div class="website-container">
        <!-- 导航栏 -->
        <nav class="navbar">
            <div class="logo">图图小破站</div>
            <div class="nav-links">
                <a href="#home">首页</a>
                <a href="#about">关于</a>
                <a href="#features">特点</a>
                <a href="#contact">联系</a>
            </div>
        </nav>
        <div class="website-info">
            <el-carousel height="500px">
                <!-- 修改轮播项内容 -->
                <el-carousel-item class="carousel-item">
                    <video class="full-video" preload="auto" x-webkit-airplay="true" airplay="allow" webkit-playsinline="true" playsinline="true" webkit-controls="no" muted="true" loop="loop" autoplay="autoplay" src="https://ld5.v.netease.com/2025/0421/565974084ca9d42d3ffee9bd2c72efcaqt.mp4"></video>
                </el-carousel-item>
            </el-carousel>
            
            <section class="eat" id="home">
                <h1>智能餐饮管理系统</h1>
                <p>让餐饮管理更简单、更高效</p>
                <button class="cta-button">立即体验</button>
            </section>

            <section class="play_what_page" id="home">
                <h1>智能游戏选择系统</h1>
                <p>让每晚玩什么游戏不再折磨你</p>
                <button class="cta-button">立即体验</button>
            </section>
            <section class="about" id="about">
                <h2>关于我们</h2>
                <p>专注于为餐饮行业提供一站式数字化解决方案，涵盖订单管理、库存追踪、客户营销等核心场景。</p>
            </section>

            <section class="features" id="features">
                <h2>核心特点</h2>
                <div class="feature-grid">
                    <div class="feature-card">
                        <h3>智能订单</h3>
                        <p>自动聚合多平台订单，减少人工录入错误</p>
                    </div>
                    <div class="feature-card">
                        <h3>实时库存</h3>
                        <p>原料消耗与采购需求智能联动，降低损耗</p>
                    </div>
                    <div class="feature-card">
                        <h3>客户营销</h3>
                        <p>会员体系+精准营销模板，提升复购率</p>
                    </div>
                </div>
            </section>

            <section class="contact" id="contact">
                <h2>联系我们</h2>
                <div class="contact-info">
                    <p>电话：010-12345678</p>
                    <p>邮箱：contact@example.com</p>
                    <p>地址：北京市朝阳区XX路XX号</p>
                </div>
            </section>
        </div>
    </div>
</template>

<script setup lang="ts">
// 可根据需要添加逻辑（如获取项目数据）
</script>

<style scoped lang="scss">
.logo {
    margin-left: 10%;
    font-size: 1.5rem;
    line-height: 2rem;
    font-weight: 700;
    color: rgb(30, 64, 175);
    text-decoration: inherit;
}

.website-container {
    max-width: 100%;

    /* 添加背景图片样式 */
}

.website-info {
    max-width: 100%;
    margin: 0 auto;
    /* 移除原有的背景色 */
    /* background-color: #e5e7eb; */
    /* 添加半透明背景层提高文字可读性 */
}

.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 0;
    border-bottom: 2px solid #e5e7eb;
    background-color: white;
}

.nav-links a {
    margin-left: 2rem;
    margin-right: 2rem;
    color: #374151;
    text-decoration: none;
}

.eat {
    text-align: center;
    padding: 4rem 0;
}
.play_what_page{
    background-image: url();
}

.cta-button {
    background: #2563eb;
    color: white;
    padding: 0.75rem 1.5rem;
    border-radius: 0.375rem;
    border: none;
    margin-top: 2rem;
    cursor: pointer;
}

.about,
.features,
.contact {
    padding: 3rem 0;
}

.feature-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
    margin-top: 2rem;
}

.feature-card {
    padding: 1.5rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.375rem;
}

.el-carousel__item h3 {
    color: #475669;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
    text-align: center;
}

.el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
    background-color: #d3dce6;
}

/* 轮播项样式 */
.carousel-item {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden; /* 隐藏超出容器的部分 */
}

/* 视频样式 */
.full-video {
    width: 100%;
    height: 100%;
    /* 如需完整显示视频（可能有黑边），使用 object-fit: contain; */
}
</style>