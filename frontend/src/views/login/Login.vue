<template>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <el-icon :size="40" color="#409eff"><Reading /></el-icon>
                <h2>图书管理系统</h2>
                <p>企业级图书借阅管理平台</p>
            </div>
            <el-tabs v-model="activeTab" class="login-tabs">
                <el-tab-pane label="登录" name="login">
                    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0" size="large">
                        <el-form-item prop="username">
                            <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" :loading="loading" class="submit-btn" @click="handleLogin">登 录</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="注册" name="register">
                    <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="0" size="large">
                        <el-form-item prop="username">
                            <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
                        </el-form-item>
                        <el-form-item prop="realName">
                            <el-input v-model="registerForm.realName" placeholder="真实姓名（选填）" prefix-icon="EditPen" />
                        </el-form-item>
                        <el-form-item prop="email">
                            <el-input v-model="registerForm.email" placeholder="邮箱（选填）" prefix-icon="Message" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="success" :loading="loading" class="submit-btn" @click="handleRegister">注 册</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { login as loginApi, register as registerApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)
const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({ username: 'admin', password: '123456' })
const registerForm = reactive({ username: '', password: '', realName: '', email: '' })

const loginRules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const registerRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 50, message: '密码长度6-50个字符', trigger: 'blur' }
    ]
}

async function handleLogin() {
    const valid = await loginFormRef.value.validate().catch(() => false)
    if (!valid) return
    loading.value = true
    try {
        const res = await loginApi(loginForm)
        if (res.code === 200) {
            userStore.setToken(res.data.token)
            userStore.setUserInfo(res.data.userInfo)
            ElMessage.success('登录成功')
            router.push('/home')
        }
    } catch (e) {
        console.error('登录失败', e)
    } finally {
        loading.value = false
    }
}

async function handleRegister() {
    const valid = await registerFormRef.value.validate().catch(() => false)
    if (!valid) return
    loading.value = true
    try {
        const res = await registerApi(registerForm)
        if (res.code === 200) {
            ElMessage.success('注册成功，请登录')
            activeTab.value = 'login'
        }
    } catch (e) {
        console.error('注册失败', e)
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-container {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
    width: 420px;
    padding: 40px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.login-header {
    text-align: center;
    margin-bottom: 30px;
}
.login-header h2 {
    margin-top: 10px;
    font-size: 24px;
    color: #1a1a2e;
}
.login-header p {
    color: #999;
    font-size: 14px;
    margin-top: 6px;
}
.login-tabs {
    margin-top: 10px;
}
.submit-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
}
</style>
