<template>
  <div class="trade-history">
    <!-- 成交记录头部 -->
    <div class="history-header">
      <div class="header-item">时间</div>
      <div class="header-item">价格({{ coinInfo.baseCoin?.toUpperCase() || 'USDT' }})</div>
      <div class="header-item">数量({{ coinInfo.coin?.toUpperCase() || 'BTC' }})</div>
      <div class="header-item">方向</div>
    </div>

    <!-- 成交记录列表 -->
    <div class="history-list" ref="historyList">
      <div 
        v-for="(trade, index) in tradeList" 
        :key="index"
        class="trade-item"
        :class="trade.side === 'buy' ? 'buy-item' : 'sell-item'"
      >
        <div class="time">{{ formatTime(trade.timestamp) }}</div>
        <div class="price" :class="trade.side === 'buy' ? 'buy-price' : 'sell-price'">
          {{ formatPrice(trade.price) }}
        </div>
        <div class="amount">{{ formatAmount(trade.amount) }}</div>
        <div class="side" :class="trade.side === 'buy' ? 'buy-side' : 'sell-side'">
          {{ trade.side === 'buy' ? '买' : '卖' }}
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-info">
      <div class="stats-item">
        <span class="label">24h成交量:</span>
        <span class="value">{{ formatAmount(stats.volume24h) }}</span>
      </div>
      <div class="stats-item">
        <span class="label">24h成交额:</span>
        <span class="value">{{ formatPrice(stats.turnover24h) }}</span>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="loading" class="loading">
      <van-loading size="16px">加载中...</van-loading>
    </div>
    
    <div v-if="!hasMore && tradeList.length > 0" class="no-more">
      没有更多数据了
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { showLoadingToast, closeToast } from 'vant'

const props = defineProps({
  coinInfo: {
    type: Object,
    default: () => ({})
  }
})

// 成交记录数据
const tradeList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const historyList = ref(null)

// 统计数据
const stats = ref({
  volume24h: 0,
  turnover24h: 0
})

// 格式化函数
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

const formatPrice = (price) => {
  return parseFloat(price).toFixed(2)
}

const formatAmount = (amount) => {
  if (amount >= 1000000) {
    return (amount / 1000000).toFixed(2) + 'M'
  } else if (amount >= 1000) {
    return (amount / 1000).toFixed(2) + 'K'
  }
  return parseFloat(amount).toFixed(4)
}

// 生成模拟成交记录
const generateTradeHistory = (count = 20) => {
  const basePrice = Math.random() * 10000 + 45000
  const now = Date.now()
  const newTrades = []
  
  for (let i = 0; i < count; i++) {
    const side = Math.random() > 0.5 ? 'buy' : 'sell'
    const priceVariation = (Math.random() - 0.5) * basePrice * 0.01
    const price = basePrice + priceVariation
    const amount = Math.random() * 10 + 0.01
    const timestamp = now - (i * Math.random() * 60000) // 随机时间间隔
    
    newTrades.push({
      timestamp,
      price,
      amount,
      side,
      value: price * amount
    })
  }
  
  return newTrades.sort((a, b) => b.timestamp - a.timestamp)
}

// 加载成交记录
const loadTradeHistory = async (isRefresh = false) => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const newTrades = generateTradeHistory()
    
    if (isRefresh) {
      tradeList.value = newTrades
    } else {
      tradeList.value = [...tradeList.value, ...newTrades]
    }
    
    // 模拟数据加载完毕
    if (tradeList.value.length >= 100) {
      hasMore.value = false
    }
    
    // 更新统计数据
    updateStats()
    
  } catch (error) {
    console.error('加载成交记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  const last24h = Date.now() - 24 * 60 * 60 * 1000
  const recent24hTrades = tradeList.value.filter(trade => trade.timestamp > last24h)
  
  stats.value.volume24h = recent24hTrades.reduce((sum, trade) => sum + trade.amount, 0)
  stats.value.turnover24h = recent24hTrades.reduce((sum, trade) => sum + trade.value, 0)
}

// 滚动加载更多
const handleScroll = () => {
  if (!historyList.value || loading.value || !hasMore.value) return
  
  const { scrollTop, scrollHeight, clientHeight } = historyList.value
  
  if (scrollTop + clientHeight >= scrollHeight - 50) {
    loadTradeHistory()
  }
}

// 自动更新最新成交
const addNewTrade = () => {
  const newTrade = generateTradeHistory(1)[0]
  newTrade.timestamp = Date.now()
  
  tradeList.value.unshift(newTrade)
  
  // 保持列表长度，移除旧数据
  if (tradeList.value.length > 200) {
    tradeList.value = tradeList.value.slice(0, 200)
  }
  
  updateStats()
}

let updateInterval = null

onMounted(async () => {
  await loadTradeHistory(true)
  
  // 添加滚动监听
  if (historyList.value) {
    historyList.value.addEventListener('scroll', handleScroll)
  }
  
  // 定时添加新成交记录
  updateInterval = setInterval(addNewTrade, Math.random() * 3000 + 2000)
})

onUnmounted(() => {
  if (historyList.value) {
    historyList.value.removeEventListener('scroll', handleScroll)
  }
  
  if (updateInterval) {
    clearInterval(updateInterval)
  }
})

// 监听币种变化
watch(() => props.coinInfo, () => {
  loadTradeHistory(true)
}, { deep: true })
</script>

<style lang="scss" scoped>
.trade-history {
  padding: 15px;
  background: var(--ex-home-list-bgcolor);
  height: 400px;
  display: flex;
  flex-direction: column;
  
  .history-header {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid var(--ex-border-color);
    margin-bottom: 10px;
    flex-shrink: 0;
    
    .header-item {
      flex: 1;
      text-align: center;
      font-size: 12px;
      color: var(--ex-passive-font-color);
      
      &:first-child {
        text-align: left;
      }
      
      &:last-child {
        text-align: right;
      }
    }
  }
  
  .history-list {
    flex: 1;
    overflow-y: auto;
    
    .trade-item {
      display: flex;
      justify-content: space-between;
      padding: 6px 0;
      font-size: 12px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.05);
      
      &:last-child {
        border-bottom: none;
      }
      
      .time {
        flex: 1;
        text-align: left;
        color: var(--ex-passive-font-color);
      }
      
      .price {
        flex: 1;
        text-align: center;
        font-weight: 500;
        
        &.buy-price {
          color: #22c55e;
        }
        
        &.sell-price {
          color: #ef4444;
        }
      }
      
      .amount {
        flex: 1;
        text-align: center;
        color: var(--ex-default-font-color);
      }
      
      .side {
        flex: 1;
        text-align: right;
        font-size: 11px;
        padding: 2px 6px;
        border-radius: 3px;
        
        &.buy-side {
          color: #22c55e;
          background: rgba(34, 197, 94, 0.1);
        }
        
        &.sell-side {
          color: #ef4444;
          background: rgba(239, 68, 68, 0.1);
        }
      }
    }
  }
  
  .stats-info {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    border-top: 1px solid var(--ex-border-color);
    margin-top: 10px;
    flex-shrink: 0;
    
    .stats-item {
      .label {
        font-size: 11px;
        color: var(--ex-passive-font-color);
      }
      
      .value {
        font-size: 12px;
        font-weight: 500;
        color: var(--ex-default-font-color);
        margin-left: 5px;
      }
    }
  }
  
  .loading, .no-more {
    text-align: center;
    padding: 10px;
    font-size: 12px;
    color: var(--ex-passive-font-color);
    flex-shrink: 0;
  }
}

// 滚动条样式
.history-list::-webkit-scrollbar {
  width: 4px;
}

.history-list::-webkit-scrollbar-track {
  background: transparent;
}

.history-list::-webkit-scrollbar-thumb {
  background: var(--ex-border-color);
  border-radius: 2px;
}
</style>
