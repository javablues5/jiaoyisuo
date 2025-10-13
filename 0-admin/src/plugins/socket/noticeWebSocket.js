import store from "@/store";
import { _playAudio } from "@/plugins/audio/index.js";

export default class NoticeWebSocket {
  /**
   * 心跳定时器
   */
  heartbeatTimer = null;

  url = "";
  /**
   * 用户id
   */
  userId = "";
  /**
   * 等待发送列表
   */
  waitSendList = [];
  /**
   * socket 实例
   */
  ws = null;
  // 已连接
  WS_READY_STATE_OPEN = 1;
  constructor(userId) {
    let baseUrl = "wss://adminapi.probitus.vip"
      // || __config._BASE_WSS;
    this.userId = userId;
    this.url = baseUrl + `/webSocket/notice/${this.userId}/${+new Date()}`;
    this.init();
  }

  init() {
    this.ws = new WebSocket(this.url);
    this.ws.onopen = this.onOpen;
    this.ws.onmessage = this.onMessage;
    this.ws.onclose = this.onClose;
    this.ws.onerror = this.onError;
  }

  /**
   * 发送
   */
  send = (data) => {
    try {
      if (typeof data === "string") {
        data = JSON.parse(data);
      }
      data = JSON.stringify(data);
    } catch (error) {
      data = "";
    }
    if (data) {
      const isWsReady = this.ws?.readyState === this.WS_READY_STATE_OPEN;

      if (isWsReady) {
        this.ws.send(data);
        this.waitSendList = this.waitSendList.filter((elem) => elem != data);
      } else {
        if (!this.waitSendList.includes(data)) {
          this.waitSendList.push(data);
        }
      }
    }
  };
  heartbeat;
  /**
   * 已连接
   */
  onOpen = () => {
    this.send({ message: "ping" });
    this.waitSendList.forEach((text) => {
      this.send(text);
    });
  };
  /**
   * 心跳
   */
  heartbeat = () => {
    this.heartbeatTimer && clearTimeout(this.heartbeatTimer);
    this.heartbeatTimer = setTimeout(() => {
      this.send({ message: "ping" });
    }, 3 * 1000);
  };
  /**
   * 响应数据
   */
  onMessage = (e) => {
    let data = JSON.parse(e.data);
    if (data.message == "ping") {
      // 心跳
      this.heartbeat();
    } else {
      console.log("subscribeNotice", data);

      if (data?.verified >= 0) {
        store.commit("SET_VERIFIEDNUM", data.verified);
        // 认证通知
        // if (data.verified != store.getters.withdrawNum) {
        if (data?.verified) {
          let src = localStorage.getItem("notice3");
          src &&
            _playAudio.play({
              src,
            });
        }

        // }
      }
      if (data?.recharge >= 0) {
        store.commit("SET_RECHARGENUM", data.recharge);
        // 充值通知

        // if (data.recharge != store.getters.withdrawNum) {
        if (data?.recharge) {
          let src = localStorage.getItem("notice1");
          src &&
            _playAudio.play({
              src,
            });
        }

        // }
      }
      if (data?.withdraw) {
        store.commit("SET_WITHDRAWNUM", data.withdraw);
        // 提现通知
        // if (data.withdraw != store.getters.withdrawNum) {
        if (data?.withdraw) {
          let src = localStorage.getItem("notice2");
          src &&
            _playAudio.play({
              src,
            });
        }

        // }
      }
    }
  };

  /**
   * 关闭
   */
  close = () => {
    this.heartbeatTimer && clearTimeout(this.heartbeatTimer);
    this.ws?.close();
  };

  /**
   * 连接异常
   */
  onError = () => {
    this.heartbeatTimer && clearTimeout(this.heartbeatTimer);
    this.reconnect();
  };

  /**
   * 连接关闭
   */
  onClose = () => {
    this.heartbeatTimer && clearTimeout(this.heartbeatTimer);
  };

  /**
   * 重新连接
   */
  reconnect = () => {
    setTimeout(this.init.apply(this), 1 * 1000);
  };
}
