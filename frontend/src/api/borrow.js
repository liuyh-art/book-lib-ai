import request from './request'

export function borrowBook(data) {
    return request.post('/borrow/borrow', data)
}

export function returnBook(data) {
    return request.post('/borrow/return', data)
}

export function pageBorrowRecords(params) {
    return request.get('/borrow/page', { params })
}

export function myBorrowRecords(params) {
    return request.get('/borrow/my', { params })
}
