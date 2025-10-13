import request from '@/utils/request'

// 查询补偿策略配置列表
export function listStrategy(query) {
  return request({
    url: '/bussiness/compensation/strategy/list',
    method: 'get',
    params: query
  })
}

// 查询补偿策略配置详细
export function getStrategy(id) {
  return request({
    url: '/bussiness/compensation/strategy/' + id,
    method: 'get'
  })
}

// 新增补偿策略配置
export function addStrategy(data) {
  return request({
    url: '/bussiness/compensation/strategy',
    method: 'post',
    data: data
  })
}

// 修改补偿策略配置
export function updateStrategy(data) {
  return request({
    url: '/bussiness/compensation/strategy',
    method: 'put',
    data: data
  })
}

// 删除补偿策略配置
export function delStrategy(id) {
  return request({
    url: '/bussiness/compensation/strategy/' + id,
    method: 'delete'
  })
}

// 获取启用的策略列表
export function getEnabledStrategies() {
  return request({
    url: '/bussiness/compensation/strategy/enabled',
    method: 'get'
  })
}
