import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('@/views/layout/Layout.vue'),
        meta: { requiresAuth: true },
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/home/Home.vue'),
                meta: { title: '首页看板', icon: 'DataAnalysis' }
            },
            {
                path: 'user',
                name: 'User',
                component: () => import('@/views/user/User.vue'),
                meta: { title: '用户管理', icon: 'User', roles: [1] }
            },
            {
                path: 'category',
                name: 'Category',
                component: () => import('@/views/category/Category.vue'),
                meta: { title: '分类管理', icon: 'Collection', roles: [1] }
            },
            {
                path: 'book',
                name: 'Book',
                component: () => import('@/views/book/Book.vue'),
                meta: { title: '图书管理', icon: 'Reading', roles: [1] }
            },
            {
                path: 'borrow',
                name: 'Borrow',
                component: () => import('@/views/borrow/Borrow.vue'),
                meta: { title: '借阅管理', icon: 'List' }
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/profile/Profile.vue'),
                meta: { title: '个人中心', icon: 'Avatar' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    if (to.meta.requiresAuth === false) {
        next()
        return
    }

    if (!userStore.isLoggedIn) {
        ElMessage.warning('请先登录')
        next('/login')
        return
    }

    // 角色权限校验
    if (to.meta.roles && Array.isArray(to.meta.roles)) {
        if (!to.meta.roles.includes(userStore.userInfo?.role)) {
            ElMessage.error('无权限访问')
            next('/home')
            return
        }
    }

    next()
})

export default router
