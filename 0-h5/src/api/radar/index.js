/**
 * 交易雷达相关 api
 */
import { get } from '@/utils/request'

/**
 * 获取交易雷达列表数据
 * @data {{ pageNum: number, pageSize: number, orderByColumn?: string, isAsc?: 'asc' | 'desc' }} params
 * @returns {Promise<any>}
 */
export const getRadarList = (data) => {
  const payload = {
    pageNum: data?.pageNum ?? 1,
    pageSize: data?.pageSize ?? 10,
    orderByColumn: data?.orderByColumn || '',
    isAsc: data?.isAsc || 'asc'
  }
  return get('/api/ld/socket/list', { params: payload })
}
