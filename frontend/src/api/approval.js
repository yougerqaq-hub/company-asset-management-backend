import request from '@/utils/request'

export function getApprovalList() {
  return request({
    url: '/approvals',
    method: 'get'
  })
}

export function getApprovalDetail(id) {
  return request({
    url: `/approvals/${id}`,
    method: 'get'
  })
}

export function approve(id, data) {
  return request({
    url: `/approvals/${id}/approve`,
    method: 'post',
    data
  })
}

export function reject(id, data) {
  return request({
    url: `/approvals/${id}/reject`,
    method: 'post',
    data
  })
}
