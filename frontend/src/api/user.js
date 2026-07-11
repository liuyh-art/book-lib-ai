import request from './request'

export function listUsers(params) {
    return request.get('/user/page', { params })
}

export function addUser(data) {
    return request.post('/user', data)
}

export function updateUser(data) {
    return request.put('/user', data)
}

export function deleteUser(id) {
    return request.delete(`/user/${id}`)
}

export function updatePassword(data) {
    return request.put('/user/password', data)
}
