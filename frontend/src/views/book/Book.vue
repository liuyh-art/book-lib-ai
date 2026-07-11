<template>
    <div class="book-page">
        <el-card shadow="never" class="search-card">
            <el-form :model="queryForm" inline>
                <el-form-item label="书名">
                    <el-input v-model="queryForm.bookName" placeholder="请输入" clearable style="width:180px" />
                </el-form-item>
                <el-form-item label="作者">
                    <el-input v-model="queryForm.author" placeholder="请输入" clearable style="width:150px" />
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="queryForm.categoryId" placeholder="全部" clearable style="width:150px">
                        <el-option v-for="c in categoryList" :key="c.id" :label="c.categoryName" :value="c.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryForm.status" placeholder="全部" clearable style="width:120px">
                        <el-option label="上架" :value="1" />
                        <el-option label="下架" :value="0" />
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
                <el-button type="primary" @click="openAddDialog" :icon="Plus">新增图书</el-button>
            </div>
            <el-table :data="tableData" border stripe v-loading="loading">
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="bookName" label="书名" min-width="160" show-overflow-tooltip />
                <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />
                <el-table-column prop="publisher" label="出版社" width="140" show-overflow-tooltip />
                <el-table-column prop="isbn" label="ISBN" width="140" />
                <el-table-column prop="categoryName" label="分类" width="100" />
                <el-table-column label="库存" width="100" align="center">
                    <template #default="{ row }">
                        <span :class="{ 'stock-low': row.stock <= 3 }">{{ row.stock }} / {{ row.totalStock }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                            {{ row.status === 1 ? '上架' : '下架' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="160" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
                        <el-popconfirm title="确认删除该图书？" @confirm="handleDelete(row.id)">
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

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑图书' : '新增图书'" width="650px" :close-on-click-modal="false">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="书名" prop="bookName">
                            <el-input v-model="form.bookName" placeholder="请输入书名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="作者" prop="author">
                            <el-input v-model="form.author" placeholder="请输入作者" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="出版社" prop="publisher">
                            <el-input v-model="form.publisher" placeholder="请输入出版社" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="ISBN" prop="isbn">
                            <el-input v-model="form.isbn" placeholder="请输入ISBN" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="分类" prop="categoryId">
                            <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
                                <el-option v-for="c in categoryList" :key="c.id" :label="c.categoryName" :value="c.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="状态" prop="status">
                            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="库存" prop="stock">
                            <el-input-number v-model="form.stock" :min="0" :max="99999" style="width:100%" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="总库存" prop="totalStock">
                            <el-input-number v-model="form.totalStock" :min="0" :max="99999" style="width:100%" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="简介" prop="description">
                    <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入图书简介" />
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
import { pageBooks, addBook, updateBook, deleteBook } from '@/api/book'
import { listCategories } from '@/api/category'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const categoryList = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const queryForm = reactive({
    bookName: '',
    author: '',
    categoryId: undefined,
    status: undefined,
    pageNum: 1,
    pageSize: 10
})

const form = reactive({
    id: undefined,
    bookName: '',
    author: '',
    publisher: '',
    isbn: '',
    categoryId: undefined,
    stock: 1,
    totalStock: 1,
    description: '',
    cover: '',
    status: 1
})

const rules = {
    bookName: [{ required: true, message: '请输入书名', trigger: 'blur' }],
    categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
    totalStock: [{ required: true, message: '请输入总库存', trigger: 'blur' }]
}

async function loadCategories() {
    try {
        const res = await listCategories({ status: 1 })
        if (res.code === 200) {
            categoryList.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

async function fetchData() {
    loading.value = true
    try {
        const res = await pageBooks(queryForm)
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
    queryForm.bookName = ''
    queryForm.author = ''
    queryForm.categoryId = undefined
    queryForm.status = undefined
    handleSearch()
}

function openAddDialog() {
    isEdit.value = false
    form.id = undefined
    form.bookName = ''
    form.author = ''
    form.publisher = ''
    form.isbn = ''
    form.categoryId = undefined
    form.stock = 1
    form.totalStock = 1
    form.description = ''
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
        const api = isEdit.value ? updateBook : addBook
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
        const res = await deleteBook(id)
        if (res.code === 200) {
            ElMessage.success('删除成功')
            fetchData()
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => {
    loadCategories()
    fetchData()
})
</script>

<style scoped>
.book-page {
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
.stock-low {
    color: #f56c6c;
    font-weight: bold;
}
</style>
