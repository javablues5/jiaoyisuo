<template>
  <div class="chart-card">
    <div class="chart-title">{{ title }}</div>
    <div class="chart-container">
      <canvas ref="chartCanvas" :width="width" :height="height"></canvas>
    </div>
    <div class="chart-legend" v-if="legend">
      <div class="legend-item" v-for="(item, index) in legend" :key="index">
        <span class="legend-color" :style="{ background: item.color }"></span>
        <span class="legend-text">{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick, onUnmounted } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '趋势图'
  },
  data: {
    type: Object,
    required: true,
    default: () => ({
      dates: [],
      datasets: [] // [{ values: [], color: '#3CBC8C', label: '充值' }]
    })
  },
  width: {
    type: Number,
    default: 340
  },
  height: {
    type: Number,
    default: 180
  },
  legend: {
    type: Array,
    default: () => []
  }
})

const chartCanvas = ref(null)

const drawChart = () => {
  if (!chartCanvas.value) return
  
  const canvas = chartCanvas.value
  const ctx = canvas.getContext('2d')
  const width = canvas.width
  const height = canvas.height
  
  // 清空画布
  ctx.clearRect(0, 0, width, height)
  
  const { dates, datasets } = props.data
  if (!dates || dates.length === 0 || !datasets || datasets.length === 0) {
    ctx.fillStyle = '#999'
    ctx.font = '14px Arial'
    ctx.textAlign = 'center'
    ctx.fillText('暂无数据', width / 2, height / 2)
    return
  }
  
  const padding = { top: 20, right: 20, bottom: 40, left: 50 }
  const chartWidth = width - padding.left - padding.right
  const chartHeight = height - padding.top - padding.bottom
  
  // 计算最大值（所有数据集的最大值）
  let maxValue = 0
  datasets.forEach(dataset => {
    if (dataset.values && dataset.values.length > 0) {
      const datasetMax = Math.max(...dataset.values)
      if (datasetMax > maxValue) {
        maxValue = datasetMax
      }
    }
  })
  maxValue = maxValue * 1.1
  const minValue = 0
  
  // 绘制网格线
  ctx.strokeStyle = 'rgba(128, 128, 128, 0.2)'
  ctx.lineWidth = 1
  for (let i = 0; i <= 4; i++) {
    const y = padding.top + (chartHeight / 4) * i
    ctx.beginPath()
    ctx.moveTo(padding.left, y)
    ctx.lineTo(width - padding.right, y)
    ctx.stroke()
  }
  
  // 绘制Y轴标签
  ctx.fillStyle = '#999'
  ctx.font = '12px Arial'
  ctx.textAlign = 'right'
  for (let i = 0; i <= 4; i++) {
    const value = maxValue - (maxValue / 4) * i
    const y = padding.top + (chartHeight / 4) * i
    let label = ''
    if (value >= 1000) {
      label = (value / 1000).toFixed(0) + 'k'
    } else {
      label = value.toFixed(0)
    }
    ctx.fillText(label, padding.left - 10, y + 4)
  }
  
  // 绘制X轴标签
  ctx.textAlign = 'center'
  dates.forEach((date, index) => {
    const x = padding.left + (chartWidth / (dates.length - 1)) * index
    ctx.fillText(date, x, height - padding.bottom + 20)
  })
  
  // 坐标转换函数
  const xToValue = (index) => padding.left + (chartWidth / (dates.length - 1)) * index
  const yToValue = (value) => padding.top + chartHeight - ((value - minValue) / (maxValue - minValue)) * chartHeight
  
  // 绘制每条折线
  datasets.forEach(dataset => {
    if (!dataset.values || dataset.values.length === 0) return
    
    const color = dataset.color || '#3CBC8C'
    
    // 绘制折线
    ctx.strokeStyle = color
    ctx.lineWidth = 2
    ctx.beginPath()
    dataset.values.forEach((value, index) => {
      const x = xToValue(index)
      const y = yToValue(value)
      if (index === 0) {
        ctx.moveTo(x, y)
      } else {
        ctx.lineTo(x, y)
      }
    })
    ctx.stroke()
    
    // 绘制数据点
    ctx.fillStyle = color
    dataset.values.forEach((value, index) => {
      const x = xToValue(index)
      const y = yToValue(value)
      ctx.beginPath()
      ctx.arc(x, y, 3, 0, Math.PI * 2)
      ctx.fill()
    })
  })
}

// 监听数据变化
watch(() => props.data, () => {
  nextTick(() => {
    drawChart()
  })
}, { deep: true })

onMounted(() => {
  nextTick(() => {
    drawChart()
  })
})
</script>

<style lang="scss" scoped>
.chart-card {
  background: var(--ex-passive-background-color);
  border-radius: 8px;
  padding: 16px 0;
  
  .chart-title {
    font-size: 14px;
    font-weight: bold;
    color: var(--ex-default-font-color);
    margin-bottom: 12px;
    text-align: center;
  }
  
  .chart-container {
    width: 100%;
    display: flex;
    justify-content: center;
    margin-bottom: 12px;
    
    canvas {
      max-width: 100%;
      height: auto;
    }
  }
  
  .chart-legend {
    display: flex;
    justify-content: center;
    gap: 24px;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      
      .legend-color {
        width: 12px;
        height: 12px;
        border-radius: 2px;
      }
      
      .legend-text {
        font-size: 12px;
        color: var(--ex-passive-font-color);
      }
    }
  }
}
</style>

