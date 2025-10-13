<template>
  <div class="depth-chart">
    <!-- 深度图头部信息 -->
    <div class="depth-header">
      <div class="depth-info">
        <div class="info-item">
          <span class="label">最新价:</span>
          <span class="value" :class="priceChangeClass">{{ formatPrice(currentPrice) }}</span>
        </div>
        <div class="info-item">
          <span class="label">24h涨跌:</span>
          <span class="value" :class="priceChangeClass">{{ priceChange > 0 ? '+' : '' }}{{ formatPercentage(priceChangePercent) }}%</span>
        </div>
      </div>
    </div>

    <!-- 深度图表容器 -->
    <div class="chart-container" ref="chartContainer">
      <canvas 
        ref="chartCanvas" 
        :width="canvasWidth" 
        :height="canvasHeight"
        @touchstart="handleTouchStart"
        @touchmove="handleTouchMove"
        @touchend="handleTouchEnd"
      ></canvas>
      
      <!-- 十字线信息 -->
      <div v-if="crosshairInfo" class="crosshair-info" :style="crosshairStyle">
        <div class="info-box">
          <div>价格: {{ formatPrice(crosshairInfo.price) }}</div>
          <div>深度: {{ formatAmount(crosshairInfo.depth) }}</div>
        </div>
      </div>
    </div>

    <!-- 图例 -->
    <div class="chart-legend">
      <div class="legend-item">
        <span class="legend-color buy-color"></span>
        <span class="legend-text">买单深度</span>
      </div>
      <div class="legend-item">
        <span class="legend-color sell-color"></span>
        <span class="legend-text">卖单深度</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'

const props = defineProps({
  coinInfo: {
    type: Object,
    default: () => ({})
  }
})

// 图表相关
const chartContainer = ref(null)
const chartCanvas = ref(null)
const canvasWidth = ref(350)
const canvasHeight = ref(200)

// 价格数据
const currentPrice = ref(50000)
const priceChange = ref(0)
const priceChangePercent = ref(0)

// 深度数据
const buyDepthData = ref([])
const sellDepthData = ref([])

// 十字线
const crosshairInfo = ref(null)
const crosshairStyle = ref({})

// 价格变化样式
const priceChangeClass = computed(() => {
  if (priceChange.value > 0) return 'price-up'
  if (priceChange.value < 0) return 'price-down'
  return ''
})

// 格式化函数
const formatPrice = (price) => parseFloat(price).toFixed(2)
const formatAmount = (amount) => parseFloat(amount).toFixed(4)
const formatPercentage = (percent) => parseFloat(percent).toFixed(2)

// 生成模拟深度数据
const generateDepthData = () => {
  const basePrice = currentPrice.value
  const buyData = []
  const sellData = []
  
  // 生成买单深度数据
  let cumulativeBuy = 0
  for (let i = 50; i >= 1; i--) {
    const price = basePrice - (i * basePrice * 0.0001)
    const volume = Math.random() * 100 + 10
    cumulativeBuy += volume
    buyData.push({ price, volume, cumulative: cumulativeBuy })
  }
  
  // 生成卖单深度数据
  let cumulativeSell = 0
  for (let i = 1; i <= 50; i++) {
    const price = basePrice + (i * basePrice * 0.0001)
    const volume = Math.random() * 100 + 10
    cumulativeSell += volume
    sellData.push({ price, volume, cumulative: cumulativeSell })
  }
  
  buyDepthData.value = buyData
  sellDepthData.value = sellData
}

// 绘制深度图
const drawDepthChart = () => {
  const canvas = chartCanvas.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  const { width, height } = canvas
  
  // 清除画布
  ctx.clearRect(0, 0, width, height)
  
  // 设置画布样式
  ctx.lineWidth = 2
  
  // 计算绘图区域
  const padding = 20
  const chartWidth = width - padding * 2
  const chartHeight = height - padding * 2
  
  // 合并所有数据点来计算范围
  const allData = [...buyDepthData.value, ...sellDepthData.value]
  if (allData.length === 0) return
  
  const minPrice = Math.min(...allData.map(d => d.price))
  const maxPrice = Math.max(...allData.map(d => d.price))
  const maxDepth = Math.max(...allData.map(d => d.cumulative))
  
  // 价格和深度的缩放函数
  const priceToX = (price) => padding + ((price - minPrice) / (maxPrice - minPrice)) * chartWidth
  const depthToY = (depth) => height - padding - (depth / maxDepth) * chartHeight
  
  // 绘制买单深度（绿色）
  if (buyDepthData.value.length > 0) {
    ctx.strokeStyle = '#22c55e'
    ctx.fillStyle = 'rgba(34, 197, 94, 0.2)'
    
    ctx.beginPath()
    ctx.moveTo(priceToX(buyDepthData.value[0].price), depthToY(0))
    
    buyDepthData.value.forEach((point, index) => {
      const x = priceToX(point.price)
      const y = depthToY(point.cumulative)
      
      if (index === 0) {
        ctx.lineTo(x, y)
      } else {
        // 创建阶梯效果
        const prevX = priceToX(buyDepthData.value[index - 1].price)
        ctx.lineTo(prevX, y)
        ctx.lineTo(x, y)
      }
    })
    
    // 填充区域
    const lastBuyPoint = buyDepthData.value[buyDepthData.value.length - 1]
    ctx.lineTo(priceToX(lastBuyPoint.price), depthToY(0))
    ctx.closePath()
    ctx.fill()
    ctx.stroke()
  }
  
  // 绘制卖单深度（红色）
  if (sellDepthData.value.length > 0) {
    ctx.strokeStyle = '#ef4444'
    ctx.fillStyle = 'rgba(239, 68, 68, 0.2)'
    
    ctx.beginPath()
    ctx.moveTo(priceToX(sellDepthData.value[0].price), depthToY(0))
    
    sellDepthData.value.forEach((point, index) => {
      const x = priceToX(point.price)
      const y = depthToY(point.cumulative)
      
      if (index === 0) {
        ctx.lineTo(x, y)
      } else {
        // 创建阶梯效果
        const prevX = priceToX(sellDepthData.value[index - 1].price)
        ctx.lineTo(prevX, y)
        ctx.lineTo(x, y)
      }
    })
    
    // 填充区域
    const lastSellPoint = sellDepthData.value[sellDepthData.value.length - 1]
    ctx.lineTo(priceToX(lastSellPoint.price), depthToY(0))
    ctx.closePath()
    ctx.fill()
    ctx.stroke()
  }
  
  // 绘制当前价格线
  ctx.strokeStyle = '#6b7280'
  ctx.setLineDash([5, 5])
  ctx.beginPath()
  const currentPriceX = priceToX(currentPrice.value)
  ctx.moveTo(currentPriceX, padding)
  ctx.lineTo(currentPriceX, height - padding)
  ctx.stroke()
  ctx.setLineDash([])
}

// 触摸事件处理
const handleTouchStart = (e) => {
  e.preventDefault()
  updateCrosshair(e.touches[0])
}

const handleTouchMove = (e) => {
  e.preventDefault()
  updateCrosshair(e.touches[0])
}

const handleTouchEnd = () => {
  crosshairInfo.value = null
}

// 更新十字线
const updateCrosshair = (touch) => {
  const canvas = chartCanvas.value
  if (!canvas) return
  
  const rect = canvas.getBoundingClientRect()
  const x = touch.clientX - rect.left
  const y = touch.clientY - rect.top
  
  // 计算对应的价格和深度
  const padding = 20
  const chartWidth = canvas.width - padding * 2
  const chartHeight = canvas.height - padding * 2
  
  const allData = [...buyDepthData.value, ...sellDepthData.value]
  if (allData.length === 0) return
  
  const minPrice = Math.min(...allData.map(d => d.price))
  const maxPrice = Math.max(...allData.map(d => d.price))
  const maxDepth = Math.max(...allData.map(d => d.cumulative))
  
  const price = minPrice + ((x - padding) / chartWidth) * (maxPrice - minPrice)
  const depth = maxDepth * (1 - (y - padding) / chartHeight)
  
  crosshairInfo.value = { price, depth }
  crosshairStyle.value = {
    left: `${x}px`,
    top: `${y}px`
  }
}

// 初始化画布尺寸
const initCanvas = () => {
  nextTick(() => {
    if (chartContainer.value) {
      const rect = chartContainer.value.getBoundingClientRect()
      canvasWidth.value = rect.width
      canvasHeight.value = 200
      
      nextTick(() => {
        drawDepthChart()
      })
    }
  })
}

// 更新数据
const updateData = () => {
  currentPrice.value = Math.random() * 10000 + 45000
  priceChange.value = (Math.random() - 0.5) * 1000
  priceChangePercent.value = (priceChange.value / currentPrice.value) * 100
  
  generateDepthData()
  drawDepthChart()
}

let updateInterval = null

onMounted(() => {
  initCanvas()
  updateData()
  updateInterval = setInterval(updateData, 3000)
})

onUnmounted(() => {
  if (updateInterval) {
    clearInterval(updateInterval)
  }
})

// 监听币种变化
watch(() => props.coinInfo, () => {
  updateData()
}, { deep: true })
</script>

<style lang="scss" scoped>
.depth-chart {
  padding: 15px;
  background: var(--ex-home-list-bgcolor);
  
  .depth-header {
    margin-bottom: 15px;
    
    .depth-info {
      display: flex;
      justify-content: space-between;
      
      .info-item {
        .label {
          font-size: 12px;
          color: var(--ex-passive-font-color);
        }
        
        .value {
          font-size: 14px;
          font-weight: bold;
          margin-left: 5px;
          
          &.price-up {
            color: #22c55e;
          }
          
          &.price-down {
            color: #ef4444;
          }
        }
      }
    }
  }
  
  .chart-container {
    position: relative;
    width: 100%;
    height: 200px;
    border: 1px solid var(--ex-border-color);
    border-radius: 8px;
    overflow: hidden;
    
    canvas {
      display: block;
      width: 100%;
      height: 100%;
    }
    
    .crosshair-info {
      position: absolute;
      pointer-events: none;
      z-index: 10;
      
      .info-box {
        background: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 8px;
        border-radius: 4px;
        font-size: 12px;
        white-space: nowrap;
        transform: translate(-50%, -100%);
        margin-top: -10px;
      }
    }
  }
  
  .chart-legend {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 10px;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 5px;
      
      .legend-color {
        width: 12px;
        height: 12px;
        border-radius: 2px;
        
        &.buy-color {
          background: #22c55e;
        }
        
        &.sell-color {
          background: #ef4444;
        }
      }
      
      .legend-text {
        font-size: 12px;
        color: var(--ex-default-font-color);
      }
    }
  }
}
</style>
