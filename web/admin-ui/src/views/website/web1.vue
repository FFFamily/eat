<template>
    <div class="font-sans antialiased bg-gray-100">
      <!-- 导航栏 -->
      <header class="bg-white shadow-md py-4">
        <nav class="container mx-auto flex justify-between items-center px-4">
          <a href="javascript:void(0);" class="text-2xl font-bold text-gray-800">
            <i class="fas fa-bicycle text-blue-600 mr-2"></i>
            BikeShop
          </a>
          <ul class="flex space-x-6">
            <li>
              <a href="javascript:void(0);" class="text-gray-600 hover:text-blue-600 transition duration-300">首页</a>
            </li>
            <li>
              <a href="javascript:void(0);" class="text-gray-600 hover:text-blue-600 transition duration-300">产品</a>
            </li>
            <li>
              <a href="javascript:void(0);" class="text-gray-600 hover:text-blue-600 transition duration-300">关于我们</a>
            </li>
            <li>
              <a href="javascript:void(0);" class="text-gray-600 hover:text-blue-600 transition duration-300">联系我们</a>
            </li>
          </ul>
          <div class="flex items-center space-x-4">
            <a href="javascript:void(0);" class="text-gray-600 hover:text-blue-600 transition duration-300">
              <i class="fas fa-search"></i>
            </a>
            <a href="javascript:void(0);" class="text-gray-600 hover:text-blue-600 transition duration-300">
              <i class="fas fa-shopping-cart"></i>
            </a>
          </div>
        </nav>
      </header>
  
      <!-- 英雄区 -->
      <section class="relative bg-gray-900 text-white py-20 md:py-32 overflow-hidden">
        <img
          src="https://design.gemcoder.com/staticResource/echoAiSystemImages/85dbcba179852759f1eeeaa5ba39aca0.png"
          alt="Hero Bicycle"
          class="absolute inset-0 w-full h-full object-cover opacity-50"
        />
        <div class="container mx-auto relative z-10 text-center px-4">
          <h1 class="text-4xl md:text-6xl font-extrabold leading-tight mb-6 animate-fade-in-up">骑行，探索无限可能</h1>
          <p class="text-lg md:text-xl mb-8 max-w-2xl mx-auto animate-fade-in-up delay-200">我们提供最优质的自行车，助您征服每一段旅程。</p>
          <a
            href="javascript:void(0);"
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-full transition duration-300 transform hover:scale-105 inline-block animate-fade-in-up delay-400"
          >
            立即选购
            <i class="fas fa-arrow-right ml-2"></i>
          </a>
        </div>
      </section>
  
      <!-- 产品特色区 -->
      <section class="py-16 bg-white">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-center text-gray-800 mb-12">我们的优势</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div class="bg-gray-50 p-8 rounded-lg shadow-md text-center transform hover:scale-105 transition duration-300">
              <div class="text-blue-600 text-5xl mb-4">
                <i class="fas fa-lightbulb"></i>
              </div>
              <h3 class="text-xl font-semibold text-gray-800 mb-3">创新设计</h3>
              <p class="text-gray-600">融合最新科技与人体工程学，打造卓越骑行体验。</p>
            </div>
            <div class="bg-gray-50 p-8 rounded-lg shadow-md text-center transform hover:scale-105 transition duration-300">
              <div class="text-blue-600 text-5xl mb-4">
                <i class="fas fa-shield-alt"></i>
              </div>
              <h3 class="text-xl font-semibold text-gray-800 mb-3">品质保证</h3>
              <p class="text-gray-600">精选高强度材料，严格品控，确保每一辆车的可靠性。</p>
            </div>
            <div class="bg-gray-50 p-8 rounded-lg shadow-md text-center transform hover:scale-105 transition duration-300">
              <div class="text-blue-600 text-5xl mb-4">
                <i class="fas fa-headset"></i>
              </div>
              <h3 class="text-xl font-semibold text-gray-800 mb-3">专业服务</h3>
              <p class="text-gray-600">从选购到售后，全程提供专业、贴心的服务支持。</p>
            </div>
          </div>
        </div>
      </section>
  
      <!-- 精选产品区 -->
      <section class="py-16 bg-gray-100">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-center text-gray-800 mb-12">精选产品</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <!-- 产品卡片 1 -->
            <div class="bg-white rounded-lg shadow-md overflow-hidden transform hover:scale-105 transition duration-300">
              <img
                src="https://design.gemcoder.com/staticResource/echoAiSystemImages/b6ba96e796ca2343c064f978fd8d6ef6.png"
                alt="Mountain Bike"
                class="w-full h-48 object-cover"
              />
              <div class="p-6">
                <h3 class="text-xl font-semibold text-gray-800 mb-2">山地自行车</h3>
                <p class="text-gray-600 mb-4">征服崎岖山路，享受越野乐趣。</p>
                <div class="flex justify-between items-center">
                  <span class="text-2xl font-bold text-blue-600">¥ 3999</span>
                  <a href="javascript:void(0);" class="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-full text-sm">立即购买</a>
                </div>
              </div>
            </div>
            <!-- 产品卡片 2 -->
            <div class="bg-white rounded-lg shadow-md overflow-hidden transform hover:scale-105 transition duration-300">
              <img
                src="https://design.gemcoder.com/staticResource/echoAiSystemImages/5c8ec0c3a8ad20ce9ab81c39e8c4f1a4.png"
                alt="Road Bike"
                class="w-full h-48 object-cover"
              />
              <div class="p-6">
                <h3 class="text-xl font-semibold text-gray-800 mb-2">公路自行车</h3>
                <p class="text-gray-600 mb-4">速度与激情的完美结合，畅享疾驰。</p>
                <div class="flex justify-between items-center">
                  <span class="text-2xl font-bold text-blue-600">¥ 5999</span>
                  <a href="javascript:void(0);" class="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-full text-sm">立即购买</a>
                </div>
              </div>
            </div>
            <!-- 产品卡片 3 -->
            <div class="bg-white rounded-lg shadow-md overflow-hidden transform hover:scale-105 transition duration-300">
              <img
                src="https://design.gemcoder.com/staticResource/echoAiSystemImages/02aef6da13c0e9f8e609ee4e2a991165.png"
                alt="City Bike"
                class="w-full h-48 object-cover"
              />
              <div class="p-6">
                <h3 class="text-xl font-semibold text-gray-800 mb-2">城市通勤车</h3>
                <p class="text-gray-600 mb-4">轻便舒适，穿梭都市的理想选择。</p>
                <div class="flex justify-between items-center">
                  <span class="text-2xl font-bold text-blue-600">¥ 2499</span>
                  <a href="javascript:void(0);" class="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-full text-sm">立即购买</a>
                </div>
              </div>
            </div>
          </div>
          <div class="text-center mt-12">
            <a
              href="javascript:void(0);"
              class="bg-gray-800 hover:bg-gray-900 text-white font-bold py-3 px-8 rounded-full transition duration-300 transform hover:scale-105 inline-block"
            >
              查看更多产品
              <i class="fas fa-arrow-right ml-2"></i>
            </a>
          </div>
        </div>
      </section>
  
      <!-- 客户评价区 -->
      <section class="py-16 bg-white">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-center text-gray-800 mb-12">客户评价</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div class="bg-gray-50 p-8 rounded-lg shadow-md">
              <div class="flex items-center mb-4">
                <img
                  src="https://design.gemcoder.com/staticResource/echoAiSystemImages/38baa77e6c4e7e0ff4f77772e1c1b5bc.png"
                  alt="Customer 1"
                  class="w-16 h-16 rounded-full object-cover mr-4"
                />
                <div>
                  <p class="font-semibold text-gray-800">张先生</p>
                  <div class="text-yellow-500 text-sm">
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                  </div>
                </div>
              </div>
              <p class="text-gray-600 leading-relaxed">“购买的公路车性能卓越，骑行体验非常棒！客服也很专业，解答了我所有疑问。”</p>
            </div>
            <div class="bg-gray-50 p-8 rounded-lg shadow-md">
              <div class="flex items-center mb-4">
                <img
                  src="https://design.gemcoder.com/staticResource/echoAiSystemImages/b302886bf691d6650170d9b4cbe3dd55.png"
                  alt="Customer 2"
                  class="w-16 h-16 rounded-full object-cover mr-4"
                />
                <div>
                  <p class="font-semibold text-gray-800">李女士</p>
                  <div class="text-yellow-500 text-sm">
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star-half-alt"></i>
                  </div>
                </div>
              </div>
              <p class="text-gray-600 leading-relaxed">“山地车质量很好，外观也很酷。送货速度快，包装完好无损，非常满意！”</p>
            </div>
          </div>
        </div>
      </section>
  
      <!-- CTA 区块 -->
      <section class="bg-blue-600 text-white py-16">
        <div class="container mx-auto text-center px-4">
          <h2 class="text-3xl font-bold mb-4">准备好开始您的骑行之旅了吗？</h2>
          <p class="text-lg mb-8">立即联系我们，获取专属骑行方案！</p>
          <a
            href="javascript:void(0);"
            class="bg-white text-blue-600 hover:bg-gray-100 font-bold py-3 px-8 rounded-full transition duration-300 transform hover:scale-105 inline-block"
          >
            联系我们
            <i class="fas fa-phone-alt ml-2"></i>
          </a>
        </div>
      </section>
  
      <!-- 底部 -->
      <footer class="bg-gray-800 text-gray-300 py-10">
        <div class="container mx-auto grid grid-cols-1 md:grid-cols-3 gap-8 px-4">
          <div>
            <h3 class="text-xl font-bold text-white mb-4">BikeShop</h3>
            <p class="text-sm">致力于提供高品质自行车及卓越骑行体验。</p>
            <div class="flex space-x-4 mt-4">
              <a href="javascript:void(0);" class="text-gray-400 hover:text-white transition duration-300">
                <i class="fab fa-facebook-f"></i>
              </a>
              <a href="javascript:void(0);" class="text-gray-400 hover:text-white transition duration-300">
                <i class="fab fa-twitter"></i>
              </a>
              <a href="javascript:void(0);" class="text-gray-400 hover:text-white transition duration-300">
                <i class="fab fa-instagram"></i>
              </a>
              <a href="javascript:void(0);" class="text-gray-400 hover:text-white transition duration-300">
                <i class="fab fa-linkedin-in"></i>
              </a>
            </div>
          </div>
          <div>
            <h3 class="text-xl font-bold text-white mb-4">快速链接</h3>
            <ul class="space-y-2">
              <li><a href="javascript:void(0);" class="text-gray-300 hover:text-white transition duration-300">首页</a></li>
              <li><a href="javascript:void(0);" class="text-gray-300 hover:text-white transition duration-300">产品</a></li>
              <li><a href="javascript:void(0);" class="text-gray-300 hover:text-white transition duration-300">关于我们</a></li>
              <li><a href="javascript:void(0);" class="text-gray-300 hover:text-white transition duration-300">联系我们</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-xl font-bold text-white mb-4">联系方式</h3>
            <p class="text-sm"><i class="fas fa-map-marker-alt mr-2"></i>北京市朝阳区某某街道123号</p>
            <p class="text-sm mt-2"><i class="fas fa-phone mr-2"></i>+86 123 4567 8900</p>
            <p class="text-sm mt-2"><i class="fas fa-envelope mr-2"></i>info@bikeshop.com</p>
          </div>
        </div>
        <div class="border-t border-gray-700 mt-8 pt-8 text-center text-sm text-gray-500">© 2023 BikeShop. All rights reserved.</div>
      </footer>
    </div>
  </template>
  
  <script setup lang="ts">
  // 目前无需交互功能，保留空的script标签
  </script>
  
  <style scoped lang="scss">
  /* 保留所有原有样式 */
  /* 注意：由于使用了Tailwind CSS，大部分样式已通过class实现 */
  /* 如需添加自定义样式，可在此处添加 */
  
  /* 修复Vue单文件组件中可能的样式作用域问题 */
  :deep(.animate-fade-in-up) {
    animation: fadeInUp 0.8s ease forwards;
  }
  
  :deep(.animate-fade-in-up.delay-200) {
    animation-delay: 0.2s;
  }
  
  :deep(.animate-fade-in-up.delay-400) {
    animation-delay: 0.4s;
  }
  
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  </style>