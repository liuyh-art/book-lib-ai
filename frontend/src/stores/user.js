import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

    const isLoggedIn = computed(() => !!token.value)
    const isAdmin = computed(() => userInfo.value?.role === 1)
    const roleName = computed(() => userInfo.value?.role === 1 ? '管理员' : '普通读者')

    function setToken(newToken) {
        token.value = newToken
        localStorage.setItem('token', newToken)
    }

    function setUserInfo(info) {
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
    }

    function logout() {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
    }

    async function fetchUserInfo() {
        try {
            const res = await getCurrentUser()
            if (res.code === 200) {
                setUserInfo(res.data)
            }
        } catch (e) {
            console.error('获取用户信息失败', e)
        }
    }

    return {
        token, userInfo, isLoggedIn, isAdmin, roleName,
        setToken, setUserInfo, logout, fetchUserInfo
    }
})
