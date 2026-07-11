<template>
    <el-container class="layout-container">
        <!-- 侧边栏 -->
        <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
            <div class="logo" @click="router.push('/home')">
                <el-icon :size="28"><Reading /></el-icon>
                <span v-show="!isCollapse">图书管理系统</span>
            </div>
            <el-menu
                :default-active="activeMenu"
                :collapse="isCollapse"
                :router="true"
                background-color="#001529"
                text-color="#ffffffa6"
                active-text-color="#fff"
                class="menu"
            >
                <el-menu-item index="/home">
                    <el-icon><DataAnalysis /></el-icon>
                    <template #title>首页看板</template>
                </el-menu-item>
                <el-menu-item index="/user" v-if="userStore.isAdmin">
                    <el-icon><User /></el-icon>
                    <template #title>用户管理</template>
                </el-menu-item>
                <el-menu-item index="/category" v-if="userStore.isAdmin">
                    <el-icon><Collection /></el-icon>
                    <template #title>分类管理</template>
                </el-menu-item>
                <el-menu-item index="/book" v-if="userStore.isAdmin">
                    <el-icon><Reading /></el-icon>
                    <template #title>图书管理</template>
                </el-menu-item>
                <el-menu-item index="/borrow">
                    <el-icon><List /></el-icon>
                    <template #title>借阅管理</template>
                </el-menu-item>
                <el-menu-item index="/profile">
                    <el-icon><Avatar /></el-icon>
                    <template #title>个人中心</template>
                </el-menu-item>
            </el-menu>
        </el-aside>

        <el-container>
            <!-- 顶部 -->
            <el-header class="header">
                <div class="header-left">
                    <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="20">
                        <Fold v-if="!isCollapse" />
                        <Expand v-else />
                    </el-icon>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
                    </el-breadcrumb>
                </div>
                <div class="header-right">
                    <el-dropdown trigger="click">
                        <span class="user-info">
                            <el-avatar :size="32" icon="UserFilled" />
                            <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
                            <span class="role-tag">{{ userStore.roleName }}</span>
                            <el-icon><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="router.push('/profile')">
                                    <el-icon><Avatar /></el-icon>个人中心
                                </el-dropdown-item>
                                <el-dropdown-item divided @click="handleLogout">
                                    <el-icon><SwitchButton /></el-icon>退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>

            <!-- 主体内容 -->
            <el-main class="main">
                <router-view />
            </el-main>
        </el-container>
    </el-container>

    <!-- AI 智能助手 -->
    <AIConsultant />
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import AIConsultant from '@/components/AIConsultant/index.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

function handleLogout() {
    ElMessageBox.confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        userStore.logout()
        router.push('/login')
    }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
    height: 100vh;
}
.aside {
    background-color: #001529;
    transition: width 0.3s;
    overflow-x: hidden;
}
.logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    gap: 10px;
    border-bottom: 1px solid rgba(255,255,255,0.1);
}
.menu {
    border-right: none;
}
.header {
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.08);
    z-index: 10;
    height: 60px;
}
.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}
.collapse-btn {
    cursor: pointer;
    font-size: 20px;
}
.header-right {
    display: flex;
    align-items: center;
}
.user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
}
.username {
    font-size: 14px;
    color: #333;
}
.role-tag {
    font-size: 12px;
    color: #409eff;
    background: #ecf5ff;
    padding: 2px 8px;
    border-radius: 4px;
}
.main {
    background: #f0f2f5;
    padding: 20px;
    overflow-y: auto;
}
</style>
