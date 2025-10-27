import { socketDict } from '@/config/dict'
import PubSub from 'pubsub-js'
export default class WssWebSocket {
  // ws 最终链接
  url = ''

  /**
   * socket 实例
   */
  ws = null

  // 已连接
  WS_READY_STATE_OPEN = 1

  constructor(userId) {
    // 初始化
    console.log('🚀 初始化雷达 WebSocket, userId:', userId)
    let baseUrl = __config._BASE_WSS
    this.url = `${baseUrl}/webSocket/ld/${userId}`
    console.log('📡 雷达 WebSocket URL:', this.url)
    this.init()
  }

  // 初始化socket
  init() {
    this.ws = new WebSocket(this.url)
    this.ws.onopen = this.onOpen
    this.ws.onmessage = this.onMessage
    this.ws.onclose = this.onClose
    this.ws.onerror = this.onError
  }

  /**
   * 发送
   */
  send = (data) => {
    if (typeof data === 'string') {
      this.ws.send(data)
    }
  }

  /**
   * 已连接
   */
  onOpen = () => {
    console.log('✅ 雷达 WebSocket 连接成功')
  }

  /**
   * 响应数据
   */
  onMessage = (e) => {
    // console.log('响应数据LADAR', e.data)
    let data = JSON.parse(e.data)
    if ([socketDict.LADAR].includes(data.type)) {
      PubSub.publish(data.type, data)
    }
  }

  /**
   * 关闭
   */
  close = () => {
    this.ws?.close()
  }

  /**
   * 连接异常
   */
  onError = (error) => {
    console.error('❌ 雷达 WebSocket 连接错误:', error)
    this.reconnect()
  }

  /**
   * 连接关闭
   */
  onClose = () => {
    console.log('🔒 雷达 WebSocket 连接已关闭')
  }

  /**
   * 重新连接
   */
  reconnect = () => {
    setTimeout(() => this.init(), 3 * 1000)
  }
}
