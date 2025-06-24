// 常量路由
export const constantRoutes = [
    {
        path: '/website',
        name: 'Website',
        component: () => import('~/views/website/Website.vue'),
        meta: { title: '系统官网' }
    },
    {
        path: '/website1',
        name: 'Website1',
        component: () => import('~/views/website/web1.vue'),
        meta: { title: '系统官网' }
    },
    {
        path: '/playWhat',
        name: 'PlayWhat',
        component: () => import('~/views/game-roulette/index.vue'),
        meta: { title: '智能游戏选择系统' }
    },
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
                path: 'home',
                component: () => import('~/views/mobile/index.vue'),
                name: 'MobileHome',
            },
            {
                path: 'eat',
                component: () => import('~/views/mobile/food_list/index.vue'),
                name: 'EatHome',
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
                    {
                        path: 'foodHabit',
                        component: () => import('~/views/mobile/food_manage/food_habit/index.vue'),
                        name: 'FoodHabitManagement',
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