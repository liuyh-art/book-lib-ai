<template>
    <div class="user-page">
        <!-- 搜索栏 -->
        <el-card shadow="never" class="search-card">
            <el-form :model="queryForm" inline>
                <el-form-item label="用户名">
                    <el-input v-model="queryForm.username" placeholder="请输入" clearable />
                </el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="queryForm.role" placeholder="全部" clearable style="width:120px">
                        <el-option label="管理员" :value="1" />
                        <el-option label="普通读者" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryForm.status" placeholder="全部" clearable style="width:120px">
                        <el-option label="启用" :value="1" />
                        <el-option label="禁用" :value="0" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch" :icon="Search">查询</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 操作栏 -->
        <el-card shadow="never" class="table-card">
            <div class="table-toolbar">
                <el-button type="primary" @click="openAddDialog" :icon="Plus">新增用户</el-button>
            </div>
            <el-table :data="tableData" border stripe v-loading="loading" style="width:100%">
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="username" label="用户名" min-width="120" />
                <el-table-column prop="realName" label="真实姓名" min-width="120" />
                <el-table-column prop="email" label="邮箱" min-width="160" />
                <el-table-column prop="phone" label="手机号" width="130" />
                <el-table-column label="角色" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.role === 1 ? 'danger' : 'primary'" size="small">
                            {{ row.role === 1 ? '管理员' : '读者' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="80" align="center">
                    <template #default="{ row }">
                        <el-switch :model-value="row.status === 1" disabled />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
                        <el-popconfirm title="确认删除该用户？" @confirm="handleDelete(row.id)">
                            <template #reference>
                                <el-button type="danger" link size="small">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-wrap">
                <el-pagination
                    v-model:current-page="queryForm.pageNum"
                    v-model:page-size="queryForm.pageSize"
                    :total="total"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next"
                    @size-change="fetchData"
                    @current-change="fetchData"
                />
            </div>
        </el-card>

        <!-- 新增/编辑弹窗 -->
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px" :close-on-click-modal="false">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="留空则不修改" show-password />
                </el-form-item>
                <el-form-item label="真实姓名" prop="realName">
                    <el-input v-model="form.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="角色" prop="role">
                    <el-select v-model="form.role" placeholder="请选择角色" style="width:100%">
                        <el-option label="管理员" :value="1" />
                        <el-option label="普通读者" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listUsers, addUser, updateUser, deleteUser } from '@/api/user'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const queryForm = reactive({
    username: '',
    role: undefined,
    status: undefined,
    pageNum: 1,
    pageSize: 10
})

const form = reactive({
    id: undefined,
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    role: 2,
    status: 1
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

async function fetchData() {
    loading.value = true
    try {
        const res = await listUsers(queryForm)
        if (res.code === 200) {
            tableData.value = res.data.list
            total.value = res.data.total
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

function handleSearch() {
    queryForm.pageNum = 1
    fetchData()
}

function handleReset() {
    queryForm.username = ''
    queryForm.role = undefined
    queryForm.status = undefined
    handleSearch()
}

function openAddDialog() {
    isEdit.value = false
    form.id = undefined
    form.username = ''
    form.password = ''
    form.realName = ''
    form.email = ''
    form.phone = ''
    form.role = 2
    form.status = 1
    dialogVisible.value = true
}

function openEditDialog(row) {
    isEdit.value = true
    form.id = row.id
    form.username = row.username
    form.password = ''
    form.realName = row.realName
    form.email = row.email
    form.phone = row.phone
    form.role = row.role
    form.status = row.status
    dialogVisible.value = true
}

async function handleSubmit() {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    submitLoading.value = true
    try {
        const api = isEdit.value ? updateUser : addUser
        const res = await api(form)
        if (res.code === 200) {
            ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
            dialogVisible.value = false
            fetchData()
        }
    } catch (e) {
        console.error(e)
    } finally {
        submitLoading.value = false
    }
}

async function handleDelete(id) {
    try {
        const res = await deleteUser(id)
        if (res.code === 200) {
            ElMessage.success('删除成功')
            fetchData()
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => fetchData())
</script>

<style scoped>
.user-page {
    max-width: 1200px;
    margin: 0 auto;
}
.search-card, .table-card {
    border-radius: 12px;
    margin-bottom: 16px;
}
.table-toolbar {
    margin-bottom: 16px;
}
.pagination-wrap {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
}
</style>
