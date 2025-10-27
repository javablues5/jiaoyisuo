/**
 * 配置字典
 */

/**
 * 存储 key
 */
const getStorageDict = () => {
  let tempList = [
    // token
    'TOKEN',
    // 语言
    'LANGUAGE',
    // 语言ID
    'LANGUAGE_ID',
    // 用户信息
    'USER_INFO',
    // 主题
    'THEME',
    // 资产眼睛
    'EYES',
    // 关闭窗口时长
    'CLOSE_WINDOW_TIME',
    // 测试平台
    'TEST_PLATFORM'
  ]
  let tempStorageDict = {}
  tempList.forEach((key) => {
    // tempStorageDict[key] = `${import.meta.env.VITE_APP_ENV.toLocaleUpperCase()}_${key}`
    tempStorageDict[key] = `${import.meta.env.VITE_APP_ENV}_${key}`
  })
  Object.freeze(tempStorageDict)
  return tempStorageDict
}
/**
 * 存储 字典
 */
export const storageDict = getStorageDict()
/**
 *  socket key
 */
const getSocketDict = () => {
  // kLine部分
  // 这个数组用于定义和 socket 通信时涉及到的所有消息类型 key（如订阅、取消订阅、行情明细、K线、成交、心跳、结算、持仓、用户状态、自有币等）
  // 后续会转为对象，作为 socket 消息通信中的事件类型字典，方便安全地引用和对比 socket 消息类型
  let kLineTempList = [
    'subscribe',     // 订阅操作
    'unsubscribe',   // 取消订阅操作
    'DETAIL',        // 行情明细
    'KLINE',         // K线图数据
    'TRADE',         // 成交记录
    'HEARTBEAT',     // 心跳包
    'SETTLEMENT',    // 结算推送
    'POSITION',      // 持仓推送
    'USER_STATUS',   // 用户状态变化
    'OWNCOIN' ,       // 自有币推送
    'LADAR'          //雷达推送
  ]

  let tempList = [...kLineTempList]
  let tempSocketDict = {}
  tempList.forEach((key) => {
    tempSocketDict[key] = key
  })
  Object.freeze(tempSocketDict)
  return tempSocketDict
}
/**
 * socket 字典
 */
export const socketDict = getSocketDict()
