/**
 * 交易雷达相关 api
 */
import { get } from '@/utils/request'

/**
 * 获取交易雷达列表数据
 * @param {{ pageNum: number, pageSize: number, orderByColumn?: string, isAsc?: 'asc' | 'desc' }} params
 * @returns {Promise<any>}
 */
export const getRadarList = (params) => {
  const payload = {
    pageNum: params?.pageNum ?? 1,
    pageSize: params?.pageSize ?? 10,
    orderByColumn: params?.orderByColumn || '',
    isAsc: params?.isAsc || 'asc'
  }
  return get('/api/ld/socket/list', payload)
}


