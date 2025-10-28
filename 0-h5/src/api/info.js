/**
 * 站内信api
 */

import { post, get } from '@/utils/request'

// 查询弹窗通知消息
export const getInfo = () => post('/api/mail/listByUserId')

// 查询站内信消息（分页）
// 支持 { pageNum, pageSize } 参数
export const getMessages = (data) => {
  const payload = {
    pageNum: data?.pageNum ?? 1,
    pageSize: data?.pageSize ?? 10,
    orderByColumn: data?.orderByColumn || '',
    isAsc: data?.isAsc || 'asc',
    status: data?.status 
  }
  // 与其他分页接口保持一致，使用 GET 查询参数
  return get('/api/mail/list', { params: payload })
}

//借贷订单
// export const getLoan = (params) => post('/api/mail/updateMail', params)

//消息已读
export const haveRead = (params) => post(`/api/mail/updateMail?${params}`)
