import request from '@/utils/request'

export function getAssetList() {
  return request({
    url: '/assets',
    method: 'get'
  })
}

export function getAssetDetail(id) {
  return request({
    url: `/assets/${id}`,
    method: 'get'
  })
}

export function saveAsset(data) {
  return request({
    url: '/assets',
    method: 'post',
    data
  })
}

export function deleteAsset(id) {
  return request({
    url: `/assets/${id}`,
    method: 'delete'
  })
}

export function submitOperation(data) {
  return request({
    url: '/assets/operations',
    method: 'post',
    data
  })
}

export function getOperations() {
  return request({
    url: '/assets/operations',
    method: 'get'
  })
}

export function getOperationDetail(id) {
  return request({
    url: `/assets/operations/${id}`,
    method: 'get'
  })
}

export function submitPurchase(data) {
  return request({
    url: '/assets/purchase',
    method: 'post',
    data
  })
}

export function submitReceive(data) {
  return request({
    url: '/assets/receive',
    method: 'post',
    data
  })
}

export function submitReturn(data) {
  return request({
    url: '/assets/return',
    method: 'post',
    data
  })
}

export function submitWriteOff(data) {
  return request({
    url: '/assets/write-off',
    method: 'post',
    data
  })
}
