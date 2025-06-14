// 常量路由
export const constantRoutes = [
    {
        path: '/login',
        component: () => import('~/views/login/index.vue'),
        name: 'Login',// 路由命名
    },
    {
        path: '/register',
        component: () => import('~/views/login/register.vue'),
        name: 'MobileRegister',// 路由命名
    },
    {
        path: '/',
        component: () => import('~/views/home/index.vue'),
        name: 'Home',
    },
    {
        path: '/admin',
        children: [
            {
                path: '',
                component: () => import('~/views/admin/index.vue'),
                name: 'AdminHome',
            }
        ],
    },
    {
        path: '/mobile',
        children: [
            {
                path: 'eat',
                component: () => import('~/views/mobile/food_list/index.vue'),
                name: 'MobileHome',
            },
            {
                path: 'foodManage',
                component: () => import('~/views/mobile/food_manage/index.vue'),
                name: 'FoodManage',
            },
            {
                path: 'foodTypeManage',
                component: () => import('~/views/mobile/food_manage/food_type/FoodTypeManagement.vue'),
                name: 'foodTypeManage',
            },
            {
                path: '/history',
                component: () => import('~/views/mobile/history/index.vue'),
                name: 'History',    
            },
            {
                path: 'manage',
                children: [
                    {
                        path: 'foodHistory',
                        component: () => import('~/views/mobile/food_manage/food_history/index.vue'),
                        name: 'FoodHistoryManagement',
                    },
                    {
                        path: 'foodTag',
                        component: () => import('~/views/mobile/food_manage/food_tag/index.vue'),
                        name: 'FoodTagManagement',
                    },
                ]
            }
        ],
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