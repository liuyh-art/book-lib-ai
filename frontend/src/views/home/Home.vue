<template>
    <div class="home">
        <!-- 欢迎横幅 -->
        <el-row :gutter="20" class="welcome-row">
            <el-col :span="24">
                <el-card class="welcome-card" shadow="never">
                    <div class="welcome-content">
                        <div>
                            <h2>欢迎回来，{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h2>
                            <p>今天是 {{ currentDate }}，您有 <strong>{{ stats.borrowedBooks }}</strong> 本书正在借阅中</p>
                        </div>
                        <el-icon :size="80" color="#409eff"><Reading /></el-icon>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-row">
            <el-col :xs="12" :sm="8" :md="4" v-for="item in statCards" :key="item.label">
                <el-card shadow="hover" class="stat-card">
                    <div class="stat-content">
                        <el-icon :size="32" :color="item.color"><component :is="item.icon" /></el-icon>
                        <div class="stat-info">
                            <span class="stat-value">{{ item.value }}</span>
                            <span class="stat-label">{{ item.label }}</span>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 借阅趋势与快捷操作 -->
        <el-row :gutter="20">
            <el-col :xs="24" :lg="16">
                <el-card shadow="never" class="chart-card">
                    <template #header>
                        <span>借阅趋势（近7日）</span>
                    </template>
                    <div ref="chartRef" style="height: 300px;"></div>
                </el-card>
            </el-col>
            <el-col :xs="24" :lg="8">
                <el-card shadow="never" class="quick-card">
                    <template #header>
                        <span>快捷操作</span>
                    </template>
                    <div class="quick-actions">
                        <el-button type="primary" @click="router.push('/borrow')" :icon="Plus">借书</el-button>
                        <el-button type="success" @click="router.push('/borrow')" :icon="Upload">还书</el-button>
                        <el-button type="warning" @click="router.push('/book')" :icon="Search" v-if="userStore.isAdmin">图书管理</el-button>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getDashboardStats } from '@/api/dashboard'
import { Plus, Upload, Search } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()

const currentDate = ref('')
const chartRef = ref(null)
let chartInstance = null

const stats = reactive({
    totalBooks: 0,
    totalUsers: 0,
    borrowedBooks: 0,
    totalCategories: 0,
    todayBorrows: 0,
    todayReturns: 0
})

const statCards = computed(() => [
    { label: '图书总量', value: stats.totalBooks, icon: 'Reading', color: '#409eff' },
    { label: '分类数量', value: stats.totalCategories, icon: 'Collection', color: '#67c23a' },
    { label: '用户数量', value: stats.totalUsers, icon: 'User', color: '#e6a23c' },
    { label: '借阅中', value: stats.borrowedBooks, icon: 'List', color: '#f56c6c' },
    { label: '今日借书', value: stats.todayBorrows, icon: 'Top', color: '#909399' },
    { label: '今日还书', value: stats.todayReturns, icon: 'Bottom', color: '#b37feb' }
])

async function fetchStats() {
    try {
        const res = await getDashboardStats()
        if (res.code === 200) {
            Object.assign(stats, res.data)
        }
    } catch (e) {
        console.error('获取统计数据失败', e)
    }
}

function initChart() {
    if (!chartRef.value) return
    chartInstance = echarts.init(chartRef.value)
    const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
        yAxis: { type: 'value' },
        series: [{
            name: '借书量',
            type: 'line',
            smooth: true,
            data: [3, 5, 2, 8, 6, 10, 4],
            areaStyle: { opacity: 0.3 },
            itemStyle: { color: '#409eff' }
        }, {
            name: '还书量',
            type: 'line',
            smooth: true,
            data: [2, 4, 3, 6, 5, 8, 3],
            areaStyle: { opacity: 0.3 },
            itemStyle: { color: '#67c23a' }
        }]
    }
    chartInstance.setOption(option)
}

function updateDate() {
    const now = new Date()
    const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
    currentDate.value = now.toLocaleDateString('zh-CN', options)
}

onMounted(() => {
    updateDate()
    fetchStats()
    setTimeout(initChart, 100)
    window.addEventListener('resize', () => chartInstance?.resize())
})

onUnmounted(() => {
    window.removeEventListener('resize', () => chartInstance?.resize())
    chartInstance?.dispose()
})
</script>

<style scoped>
.home {
    max-width: 1200px;
    margin: 0 auto;
}
.welcome-row {
    margin-bottom: 20px;
}
.welcome-card {
    border-radius: 12px;
}
.welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.welcome-content h2 {
    font-size: 22px;
    margin-bottom: 8px;
}
.welcome-content p {
    color: #666;
    font-size: 14px;
}
.stats-row {
    margin-bottom: 20px;
}
.stat-card {
    border-radius: 12px;
    cursor: pointer;
    transition: transform 0.2s;
}
.stat-card:hover {
    transform: translateY(-4px);
}
.stat-content {
    display: flex;
    align-items: center;
    gap: 12px;
}
.stat-info {
    display: flex;
    flex-direction: column;
}
.stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
}
.stat-label {
    font-size: 12px;
    color: #999;
    margin-top: 2px;
}
.chart-card, .quick-card {
    border-radius: 12px;
    margin-bottom: 20px;
}
.quick-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.quick-actions .el-button {
    width: 100%;
}
</style>
