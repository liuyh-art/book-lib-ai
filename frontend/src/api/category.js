import request from './request'

export function listCategories(params) {
    return request.get('/category/list', { params })
}

export function pageCategories(params) {
    return request.get('/category/page', { params })
}

export function getCategory(id) {
    return request.get(`/category/${id}`)
}

export function addCategory(data) {
    return request.post('/category', data)
}

export function updateCategory(data) {
    return request.put('/category', data)
}

export function deleteCategory(id) {
    return request.delete(`/category/${id}`)
}
