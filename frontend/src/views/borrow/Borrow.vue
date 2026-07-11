<template>
    <div class="borrow-page">
        <!-- 借还书操作卡片（读者专用） -->
        <el-row :gutter="20" v-if="!userStore.isAdmin">
            <el-col :span="24">
                <el-card shadow="never" class="action-card">
                    <template #header>
                        <span>借书 / 还书</span>
                    </template>
                    <el-form :model="borrowForm" inline>
                        <el-form-item label="选择图书">
                            <el-select v-model="borrowForm.bookId" placeholder="请搜索并选择图书" filterable remote
                                :remote-method="searchBooks" style="width:300px">
                                <el-option v-for="b in bookOptions" :key="b.id" :label="`${b.bookName} (${b.author}) - 库存:${b.stock}`" :value="b.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleBorrow" :icon="Plus">借书</el-button>
                            <el-button type="success" @click="handleReturn" :icon="Upload">还书</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-col>
        </el-row>

        <!-- 借阅记录列表 -->
        <el-card shadow="never" class="table-card">
            <template #header>
                <div class="card-header">
                    <span>借阅记录</span>
                    <div>
                        <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width:130px" @change="fetchData">
                            <el-option label="借出中" :value="0" />
                            <el-option label="已归还" :value="1" />
                        </el-select>
                    </div>
                </div>
            </template>
            <el-table :data="tableData" border stripe v-loading="loading">
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column label="图书名称" min-width="150" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.bookName }}</template>
                </el-table-column>
                <el-table-column prop="author" label="作者" width="120" />
                <el-table-column prop="isbn" label="ISBN" width="130" />
                <el-table-column label="借阅人" width="120" v-if="userStore.isAdmin">
                    <template #default="{ row }">{{ row.realName || row.userName }}</template>
                </el-table-column>
                <el-table-column prop="borrowTime" label="借书时间" width="160" />
                <el-table-column prop="returnTime" label="还书时间" width="160">
                    <template #default="{ row }">{{ row.returnTime || '-' }}</template>
                </el-table-column>
                <el-table-column label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 0 ? 'warning' : 'success'" size="small">
                            {{ row.status === 0 ? '借出中' : '已归还' }}
                        </el-tag>
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
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrowBook, returnBook, myBorrowRecords, pageBorrowRecords } from '@/api/borrow'
import { listAllBooks } from '@/api/book'
import { Search, Plus, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const bookOptions = ref([])
const allBooksCache = ref([])

const borrowForm = reactive({
    bookId: undefined
})

const queryForm = reactive({
    status: undefined,
    pageNum: 1,
    pageSize: 10
})

async function fetchData() {
    loading.value = true
    try {
        const api = userStore.isAdmin ? pageBorrowRecords : myBorrowRecords
        const res = await api(queryForm)
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

async function loadAllBooks() {
    try {
        const res = await listAllBooks()
        if (res.code === 200) {
            allBooksCache.value = res.data
            bookOptions.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

function searchBooks(query) {
    if (query) {
        bookOptions.value = allBooksCache.value.filter(b =>
            b.bookName.includes(query) || b.author.includes(query)
        )
    } else {
        bookOptions.value = allBooksCache.value
    }
}

async function handleBorrow() {
    if (!borrowForm.bookId) {
        ElMessage.warning('请先选择图书')
        return
    }
    try {
        const res = await borrowBook({ bookId: borrowForm.bookId })
        if (res.code === 200) {
            ElMessage.success('借书成功')
            borrowForm.bookId = undefined
            fetchData()
            loadAllBooks()
        }
    } catch (e) {
        console.error(e)
    }
}

async function handleReturn() {
    if (!borrowForm.bookId) {
        ElMessage.warning('请先选择要归还的图书')
        return
    }
    try {
        const res = await returnBook({ bookId: borrowForm.bookId })
        if (res.code === 200) {
            ElMessage.success('还书成功')
            borrowForm.bookId = undefined
            fetchData()
            loadAllBooks()
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => {
    fetchData()
    if (!userStore.isAdmin) {
        loadAllBooks()
    }
})
</script>

<style scoped>
.borrow-page {
    max-width: 1200px;
    margin: 0 auto;
}
.action-card, .table-card {
    border-radius: 12px;
    margin-bottom: 16px;
}
.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
}
.pagination-wrap {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
}
</style>
