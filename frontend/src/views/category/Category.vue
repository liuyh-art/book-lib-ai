<template>
    <div class="category-page">
        <el-card shadow="never" class="search-card">
            <el-form :model="queryForm" inline>
                <el-form-item label="分类名称">
                    <el-input v-model="queryForm.categoryName" placeholder="请输入" clearable />
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

        <el-card shadow="never" class="table-card">
            <div class="table-toolbar">
                <el-button type="primary" @click="openAddDialog" :icon="Plus">新增分类</el-button>
            </div>
            <el-table :data="tableData" border stripe v-loading="loading">
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="categoryName" label="分类名称" min-width="150" />
                <el-table-column prop="sort" label="排序号" width="100" align="center" />
                <el-table-column label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                            {{ row.status === 1 ? '启用' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" />
                <el-table-column prop="updateTime" label="更新时间" width="170" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
                        <el-popconfirm title="确认删除该分类？" @confirm="handleDelete(row.id)">
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

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="450px" :close-on-click-modal="false">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="分类名称" prop="categoryName">
                    <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
                </el-form-item>
                <el-form-item label="排序号" prop="sort">
                    <el-input-number v-model="form.sort" :min="0" :max="999" style="width:100%" />
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
import { pageCategories, addCategory, updateCategory, deleteCategory } from '@/api/category'
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
    categoryName: '',
    status: undefined,
    pageNum: 1,
    pageSize: 10
})

const form = reactive({
    id: undefined,
    categoryName: '',
    sort: 0,
    status: 1
})

const rules = {
    categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

async function fetchData() {
    loading.value = true
    try {
        const res = await pageCategories(queryForm)
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
    queryForm.categoryName = ''
    queryForm.status = undefined
    handleSearch()
}

function openAddDialog() {
    isEdit.value = false
    form.id = undefined
    form.categoryName = ''
    form.sort = 0
    form.status = 1
    dialogVisible.value = true
}

function openEditDialog(row) {
    isEdit.value = true
    Object.assign(form, row)
    dialogVisible.value = true
}

async function handleSubmit() {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    submitLoading.value = true
    try {
        const api = isEdit.value ? updateCategory : addCategory
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
        const res = await deleteCategory(id)
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
.category-page {
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
