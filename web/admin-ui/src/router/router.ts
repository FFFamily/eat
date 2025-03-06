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
        path: '/food',
        component: () => import('~/views/food/index.vue'),
        name: 'Food',
        children: [
            {
                path: 'foodType',
                component: () => import('~/views/food/foodType/index.vue'),
                name: 'FoodType',
            }
        ],
    },
    {
        path: '/mobile',
        component: () => import('~/views/mobile/index.vue'),
        name: 'MobileHome'
    },
    {
        path: '/404',
        component: () => import('~/views/404/index.vue'),
        name: '404',
    },

    {
        path: '/:pathMatch(.*)*',
        // 重定向
        redirect: '/404',
        name: 'NotFound',
    },
]