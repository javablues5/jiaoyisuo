import request from '@/utils/request'

// 查询补偿详细记录列表
export function listDetail(query) {
  return request({
    url: '/bussiness/compensation/detail/list',
    method: 'get',
    params: query
  })
}

// 根据批次ID查询补偿详细记录
export function listDetailByBatchId(batchId, query) {
  return request({
    url: '/bussiness/compensation/detail/batch/' + batchId,
    method: 'get',
    params: query
  })
}

// 查询补偿详细记录详细
export function getDetail(id) {
  return request({
    url: '/bussiness/compensation/detail/' + id,
    method: 'get'
  })
}

// 新增补偿详细记录
export function addDetail(data) {
  return request({
    url: '/bussiness/compensation/detail',
    method: 'post',
    data: data
  })
}

// 修改补偿详细记录
export function updateDetail(data) {
  return request({
    url: '/bussiness/compensation/detail',
    method: 'put',
    data: data
  })
}

// 删除补偿详细记录
export function delDetail(id) {
  return request({
    url: '/bussiness/compensation/detail/' + id,
    method: 'delete'
  })
}

// 获取补偿统计信息
export function getStatistics(batchId) {
  return request({
    url: '/bussiness/compensation/detail/statistics/' + batchId,
    method: 'get'
  })
}

// 重试失败的补偿
export function retryFailedCompensation(detailIds) {
  return request({
    url: '/bussiness/compensation/detail/retry',
    method: 'post',
    data: detailIds
  })
}
