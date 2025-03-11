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

    // {
    //     path: '/food',
    //     component: () => import('~/views/food/index.vue'),
    //     name: 'Food',
    // },
    {
        path: '/mobile',
        // component: () => import('~/views/mobile/index.vue'),
        // name: 'MobileHome',
        children: [
            {
                path: '',
                component: () => import('~/views/mobile/index.vue'),
                name: 'MobileHome',
            },
            {
                path: 'foodManage',
                component: () => import('~/views/mobile/food_manage/index.vue'),
                name: 'foodManage',
            }
        ],
    },
    {
        path: '/history',
        component: () => import('~/views/mobile/history/index.vue'),
        name: 'History',    
    },
    {
        path: '/stats',
        component: () => import('~/views/mobile/stats/index.vue'),
        name: 'Stats',    
    },
    {
        path: '/user',
        component: () => import('~/views/mobile/user_home/index.vue'),
        name: 'UserHome',    
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