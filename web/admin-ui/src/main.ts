import ElementPlus from "element-plus";
import 'element-plus/dist/index.css'
import App from './App.vue'
// import '~/styles/index.scss'
// import 'uno.css'
import { createApp } from "vue";
import router from './router'
// 仓库
import pinia from  './store'
// 创建应用
const app = createApp(App);
app.use(ElementPlus);
// 启用路由
app.use(router)
// 使用仓库
app.use(pinia)
app.mount("#app");


