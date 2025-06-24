import {createRouter, createWebHashHistory, createWebHistory} from 'vue-router'
import {constantRoutes} from './router'
import { useUserStore } from '~/store/modules/user';
import { ElMessage } from 'element-plus'
import { de } from 'element-plus/es/locales.mjs';
let router = createRouter({
    //路由模式: hash
    history: createWebHistory(),
    routes: [
        ...constantRoutes
    ],
    // 滚动行为
    scrollBehavior: () => ({ left: 0,top:0  }),
})
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    const token = userStore.token;

    if (to.path === '/login' || to.path === '/register') {
        next()
    } else {
        if (token) {
            next()
        } else {
            next('/login')
        }
    }
})
export default router;