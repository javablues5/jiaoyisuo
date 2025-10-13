<template>
  <div class="order-book">
    <!-- 订单簿头部 -->
    <div class="order-book-header">
      <div class="header-item">价格({{ coinInfo.baseCoin?.toUpperCase() || 'USDT' }})</div>
      <div class="header-item">数量({{ coinInfo.coin?.toUpperCase() || 'BTC' }})</div>
      <div class="header-item">累计</div>
    </div>

    <!-- 卖单区域 -->
    <div class="sell-orders">
      <div 
        v-for="(order, index) in sellOrders" 
        :key="'sell-' + index"
        class="order-item sell-item"
        :style="{ background: `linear-gradient(to left, rgba(239, 68, 68, 0.1) ${order.percentage}%, transparent ${order.percentage}%)` }"
      >
        <div class="price sell-price">{{ formatPrice(order.price) }}</div>
        <div class="amount">{{ formatAmount(order.amount) }}</div>
        <div class="total">{{ formatAmount(order.total) }}</div>
      </div>
    </div>

    <!-- 当前价格 -->
    <div class="current-price" :class="priceChangeClass">
      <div class="price-value">{{ formatPrice(currentPrice) }}</div>
      <div class="price-change">{{ priceChange > 0 ? '+' : '' }}{{ formatPrice(priceChange) }}</div>
    </div>

    <!-- 买单区域 -->
    <div class="buy-orders">
      <div 
        v-for="(order, index) in buyOrders" 
        :key="'buy-' + index"
        class="order-item buy-item"
        :style="{ background: `linear-gradient(to left, rgba(34, 197, 94, 0.1) ${order.percentage}%, transparent ${order.percentage}%)` }"
      >
        <div class="price buy-price">{{ formatPrice(order.price) }}</div>
        <div class="amount">{{ formatAmount(order.amount) }}</div>
        <div class="total">{{ formatAmount(order.total) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useTradeStore } from '@/store/trade'
import PubSub from 'pubsub-js'

const props = defineProps({
  coinInfo: {
    type: Object,
    default: () => ({})
  }
})

const tradeStore = useTradeStore()

// 订单簿数据
const buyOrders = ref([])
const sellOrders = ref([])
const currentPrice = ref(0)
const priceChange = ref(0)

// 价格变化样式
const priceChangeClass = computed(() => {
  if (priceChange.value > 0) return 'price-up'
  if (priceChange.value < 0) return 'price-down'
  return ''
})

// 格式化价格
const formatPrice = (price) => {
  return parseFloat(price).toFixed(4)
}

// 格式化数量
const formatAmount = (amount) => {
  return parseFloat(amount).toFixed(2)
}

// 生成模拟订单簿数据
const generateOrderBookData = () => {
  const basePrice = currentPrice.value || 50000
  const buyOrdersData = []
  const sellOrdersData = []
  
  // 生成买单（价格递减）
  for (let i = 0; i < 10; i++) {
    const price = basePrice - (i + 1) * (basePrice * 0.0001)
    const amount = Math.random() * 10 + 0.1
    const total = buyOrdersData.reduce((sum, order) => sum + order.amount, 0) + amount
    buyOrdersData.push({
      price,
      amount,
      total
    })
  }
  
  // 生成卖单（价格递增）
  for (let i = 0; i < 10; i++) {
    const price = basePrice + (i + 1) * (basePrice * 0.0001)
    const amount = Math.random() * 10 + 0.1
    const total = sellOrdersData.reduce((sum, order) => sum + order.amount, 0) + amount
    sellOrdersData.push({
      price,
      amount,
      total
    })
  }
  
  // 计算百分比用于背景显示
  const maxBuyTotal = Math.max(...buyOrdersData.map(o => o.total))
  const maxSellTotal = Math.max(...sellOrdersData.map(o => o.total))
  
  buyOrders.value = buyOrdersData.map(order => ({
    ...order,
    percentage: (order.total / maxBuyTotal) * 100
  }))
  
  sellOrders.value = sellOrdersData.map(order => ({
    ...order,
    percentage: (order.total / maxSellTotal) * 100
  }))
}

// 监听价格更新
const handlePriceUpdate = () => {
  // 这里可以接收WebSocket价格数据
  // 暂时使用模拟数据
  currentPrice.value = Math.random() * 10000 + 45000
  priceChange.value = (Math.random() - 0.5) * 1000
  generateOrderBookData()
}

// 定时更新数据
let updateInterval = null

onMounted(() => {
  handlePriceUpdate()
  updateInterval = setInterval(handlePriceUpdate, 2000)
})

onUnmounted(() => {
  if (updateInterval) {
    clearInterval(updateInterval)
  }
})

// 监听币种变化
watch(() => props.coinInfo, () => {
  handlePriceUpdate()
}, { deep: true })
</script>

<style lang="scss" scoped>
.order-book {
  padding: 15px;
  background: var(--ex-home-list-bgcolor);
  
  .order-book-header {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid var(--ex-border-color);
    margin-bottom: 10px;
    
    .header-item {
      flex: 1;
      text-align: center;
      font-size: 12px;
      color: var(--ex-passive-font-color);
    }
  }
  
  .sell-orders, .buy-orders {
    margin-bottom: 10px;
  }
  
  .order-item {
    display: flex;
    justify-content: space-between;
    padding: 4px 0;
    font-size: 12px;
    position: relative;
    
    .price, .amount, .total {
      flex: 1;
      text-align: center;
    }
    
    .sell-price {
      color: #ef4444;
    }
    
    .buy-price {
      color: #22c55e;
    }
    
    .amount, .total {
      color: var(--ex-default-font-color);
    }
  }
  
  .current-price {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    margin: 15px 0;
    background: var(--ex-div-bgColor);
    border-radius: 8px;
    
    .price-value {
      font-size: 16px;
      font-weight: bold;
    }
    
    .price-change {
      font-size: 12px;
    }
    
    &.price-up {
      .price-value, .price-change {
        color: #22c55e;
      }
    }
    
    &.price-down {
      .price-value, .price-change {
        color: #ef4444;
      }
    }
  }
}
</style>
