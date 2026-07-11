import request from './request'

/**
 * 发送AI咨询消息
 * @param {string} message 用户消息
 * @param {string} conversationId 会话ID（可选）
 * @returns {Promise}
 */
export function sendMessage(message, conversationId) {
  return request({
    url: '/ai/chat',
    method: 'get',
    params: {
      question: message,
      conversationId:conversationId
    }
    //,
    // data: {
    //   message,
    //   conversationId
    // }
  })
}
