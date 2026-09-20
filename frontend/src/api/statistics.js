import request from '@/utils/request'

export function getDashboard() {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

export function getAssetDetails() {
  return request({
    url: '/statistics/asset-details',
    method: 'get'
  })
}

export function getApprovalDetails() {
  return request({
    url: '/statistics/approval-details',
    method: 'get'
  })
}
