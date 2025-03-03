// 常量路由
export const constantRoutes = [
    {
        path: '/login',
        component: () => import('~/views/login/index.vue'),
        name: 'Login',// 路由命名
    },
    {
        path: '/',
        component: () => import('~/views/home/index.vue'),
        name: 'Home',
    },
    {
        path: '/404',
        component: () => import('~/views/404/index.vue'),
        name: '404',
    },
    {
        path: '/food/foodType',
        component: () => import('~/views/food/foodType/index.vue'),
        name: 'FoodType',
    },
    {
        path: '/:pathMatch(.*)*',
        // 重定向
        redirect: '/404',
        name: 'NotFound',
    },
]