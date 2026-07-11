<template>
    <div class="profile-page">
        <el-row :gutter="20">
            <!-- 个人信息卡片 -->
            <el-col :xs="24" :lg="8">
                <el-card shadow="never" class="profile-card">
                    <div class="avatar-wrap">
                        <el-avatar :size="80" icon="UserFilled" />
                    </div>
                    <h3 class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h3>
                    <p class="user-role">{{ userStore.roleName }}</p>
                    <el-divider />
                    <div class="info-item">
                        <span class="label">用户名：</span>
                        <span>{{ userStore.userInfo?.username }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">邮箱：</span>
                        <span>{{ userStore.userInfo?.email || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">手机号：</span>
                        <span>{{ userStore.userInfo?.phone || '-' }}</span>
                    </div>
                </el-card>
            </el-col>

            <!-- 修改密码 -->
            <el-col :xs="24" :lg="16">
                <el-card shadow="never" class="password-card">
                    <template #header>
                        <span>修改密码</span>
                    </template>
                    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:400px">
                        <el-form-item label="原密码" prop="oldPassword">
                            <el-input v-model="form.oldPassword" type="password" placeholder="请输入原密码" show-password />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" show-password />
                        </el-form-item>
                        <el-form-item label="确认新密码" prop="confirmPassword">
                            <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认修改</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>

                <!-- 我的借阅记录摘要 -->
                <el-card shadow="never" class="records-card" style="margin-top:16px">
                    <template #header>
                        <el-button text @click="$router.push('/borrow')">查看全部记录 ></el-button>
                    </template>
                    <el-empty description="暂无借阅记录" v-if="!recentRecords.length" />
                    <el-timeline v-else>
                        <el-timeline-item
                            v-for="r in recentRecords" :key="r.id"
                            :timestamp="r.borrowTime"
                            :type="r.status === 0 ? 'warning' : 'success'"
                            :hollow="true"
                        >
                            {{ r.bookName }} - {{ r.status === 0 ? '借出中' : '已归还' }}
                        </el-timeline-item>
                    </el-timeline>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updatePassword } from '@/api/user'
import { myBorrowRecords } from '@/api/borrow'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const formRef = ref(null)
const submitLoading = ref(false)
const recentRecords = ref([])

const form = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
    if (value !== form.newPassword) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 50, message: '密码长度6-50个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validateConfirm, trigger: 'blur' }
    ]
}

async function handleSubmit() {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    submitLoading.value = true
    try {
        const res = await updatePassword({
            oldPassword: form.oldPassword,
            newPassword: form.newPassword
        })
        if (res.code === 200) {
            ElMessage.success('密码修改成功，请重新登录')
            userStore.logout()
            window.location.href = '/login'
        }
    } catch (e) {
        console.error(e)
    } finally {
        submitLoading.value = false
    }
}

async function loadRecentRecords() {
    try {
        const res = await myBorrowRecords({ pageNum: 1, pageSize: 5 })
        if (res.code === 200) {
            recentRecords.value = res.data.list
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => {
    loadRecentRecords()
})
</script>

<style scoped>
.profile-page {
    max-width: 1200px;
    margin: 0 auto;
}
.profile-card {
    border-radius: 12px;
    text-align: center;
    padding: 20px 0;
}
.avatar-wrap {
    margin-bottom: 16px;
}
.user-name {
    font-size: 20px;
    margin-bottom: 4px;
}
.user-role {
    color: #909399;
    font-size: 14px;
}
.info-item {
    text-align: left;
    padding: 8px 0;
    font-size: 14px;
}
.info-item .label {
    color: #909399;
    margin-right: 8px;
}
.password-card {
    border-radius: 12px;
}
.records-card {
    border-radius: 12px;
}
</style>
