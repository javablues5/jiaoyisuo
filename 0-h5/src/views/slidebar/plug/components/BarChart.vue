<template>
  <div class="chart-card">
    <div class="chart-title">{{ title }}</div>
    <div class="chart-container">
      <canvas ref="chartCanvas" :width="width" :height="height"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '柱状图'
  },
  data: {
    type: Object,
    required: true,
    default: () => ({
      dates: [],
      values: [] // 数值数组
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
  color: {
    type: String,
    default: '#3CBC8C'
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
  
  const { dates, values } = props.data
  if (!dates || dates.length === 0 || !values || values.length === 0) {
    ctx.fillStyle = '#999'
    ctx.font = '14px Arial'
    ctx.textAlign = 'center'
    ctx.fillText('暂无数据', width / 2, height / 2)
    return
  }
  
  const padding = { top: 20, right: 20, bottom: 40, left: 50 }
  const chartWidth = width - padding.left - padding.right
  const chartHeight = height - padding.top - padding.bottom
  
  // 计算最大值
  const maxValue = Math.max(...values, 1) * 1.2
  
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
    ctx.fillText(Math.floor(value).toString(), padding.left - 10, y + 4)
  }
  
  // 绘制X轴标签
  ctx.textAlign = 'center'
  dates.forEach((date, index) => {
    const x = padding.left + (chartWidth / dates.length) * index + (chartWidth / dates.length) / 2
    ctx.fillText(date, x, height - padding.bottom + 20)
  })
  
  // 绘制柱状图
  const barWidth = chartWidth / dates.length * 0.6
  const barGap = (chartWidth / dates.length - barWidth) / 2
  
  values.forEach((value, index) => {
    const x = padding.left + (chartWidth / dates.length) * index + barGap
    const barHeight = (value / maxValue) * chartHeight
    const y = padding.top + chartHeight - barHeight
    
    // 绘制柱体
    ctx.fillStyle = props.color
    ctx.fillRect(x, y, barWidth, barHeight)
    
    // 绘制数值
    ctx.fillStyle = '#fff'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText(value.toString(), x + barWidth / 2, y - 5)
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
}
</style>

