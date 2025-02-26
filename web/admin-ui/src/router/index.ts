import {createRouter, createWebHashHistory} from 'vue-router'
import {constantRoutes} from './router'
let router = createRouter({
    //路由模式: hash
    history: createWebHashHistory(),
    routes: [
        ...constantRoutes
    ],
    // 滚动行为
    scrollBehavior: () => ({ left: 0,top:0  }),
})

export default router;