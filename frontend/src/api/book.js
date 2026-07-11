import request from './request'

export function pageBooks(params) {
    return request.get('/book/page', { params })
}

export function listAllBooks() {
    return request.get('/book/list')
}

export function getBook(id) {
    return request.get(`/book/${id}`)
}

export function addBook(data) {
    return request.post('/book', data)
}

export function updateBook(data) {
    return request.put('/book', data)
}

export function deleteBook(id) {
    return request.delete(`/book/${id}`)
}
