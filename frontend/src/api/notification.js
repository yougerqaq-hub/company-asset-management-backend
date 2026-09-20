import request from '@/utils/request'

export function getNotificationList() {
  return request({
    url: '/notifications',
    method: 'get'
  })
}

export function markAsRead(id) {
  return request({
    url: `/notifications/${id}/read`,
    method: 'put'
  })
}

export function createNotification(data) {
  return request({
    url: '/notifications',
    method: 'post',
    data
  })
}
