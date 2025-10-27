import CoinWebSocket from './coinWebSocket.js'
import ClientWebSocket from './webSocket.js'
import WssWebSocket from './wssSocket.js'

export let _coinWebSocket = null
/**
 * 初始化 CoinWebSocket
 * 币币
 */
export const _initCoinWebSocket = () => {
  if (!_coinWebSocket) {
    _coinWebSocket = new CoinWebSocket()
  }
}

export let _WebSocket = null
/**
 * 初始化 _WebSocket
 * 消息通知
 */
export const initWebSocket = (userId) => {
  if (!_WebSocket && userId) {
    _WebSocket = new ClientWebSocket(userId)
  }
}


export let _RadarSocket = null
/**
 * 初始化 _RadarSocket
 * 雷达
 */
export const initRadarWebSocket = (userId) => {
  console.log('🔔 调用 initRadarWebSocket, userId:', userId, '已连接:', !!_RadarSocket)
  if (!_RadarSocket && userId) {
    _RadarSocket = new WssWebSocket(userId)
  } else if (!userId) {
    console.warn('⚠️  initRadarWebSocket 被调用但 userId 为空')
  } else {
    console.log('ℹ️  WebSocket 已存在，跳过重新初始化')
  }
}
