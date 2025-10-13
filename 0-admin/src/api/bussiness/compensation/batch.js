import request from '@/utils/request'

// 查询补偿批次列表
export function listBatch(query) {
  return request({
    url: '/bussiness/compensation/batch/list',
    method: 'get',
    params: query
  })
}

// 查询补偿批次详细
export function getBatch(id) {
  return request({
    url: '/bussiness/compensation/batch/' + id,
    method: 'get'
  })
}

// 新增补偿批次
export function addBatch(data) {
  return request({
    url: '/bussiness/compensation/batch',
    method: 'post',
    data: data
  })
}

// 修改补偿批次
export function updateBatch(data) {
  return request({
    url: '/bussiness/compensation/batch',
    method: 'put',
    data: data
  })
}

// 删除补偿批次
export function delBatch(id) {
  return request({
    url: '/bussiness/compensation/batch/' + id,
    method: 'delete'
  })
}

// 搜索订单预览
export function searchPreview(params) {
  return request({
    url: '/bussiness/compensation/batch/search/preview',
    method: 'get',
    params: params
  })
}

// 创建补偿批次
export function createBatch(data) {
  return request({
    url: '/bussiness/compensation/batch/create',
    method: 'post',
    params: data
  })
}

// 执行补偿批次
export function executeBatch(id) {
  return request({
    url: '/bussiness/compensation/batch/execute/' + id,
    method: 'post'
  })
}

// 取消补偿批次
export function cancelBatch(id) {
  return request({
    url: '/bussiness/compensation/batch/cancel/' + id,
    method: 'post'
  })
}
