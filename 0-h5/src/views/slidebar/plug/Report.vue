<template>
  <div class="page-wrap">
    <HeaderBar 
      :currentName="'代理报表'" 
      :cuttentRight="cuttentRight"
      @showPopup="showFilterPopup = true"
    />
    
    <div class="content">
      <!-- 头部统计区域 -->
      <div class="stats-grid">
        <div class="stats-item" v-for="(item, index) in statsList" :key="index">
          <div class="stats-value">{{ item.value }}</div>
          <div class="stats-title">{{ item.title }}</div>
        </div>
      </div>
      <div class="report-tip">
        数据每 1 分钟自动刷新，团队数据仅供参考，实际以系统结算为准
      </div>
      
      <!-- 图表区域 -->
      <div class="charts-section">
        <!-- 折线图：团队充值/提现趋势 -->
        <TrendLineChart
          title="团队充值/提现趋势（近7天）"
          :data="trendChartData"
          :legend="trendLegend"
        />
        
        <!-- 柱状图：每日新增人数 -->
        <BarChart
          title="每日新增人数（近7天）"
          :data="memberChartData"
        />
      </div>
      
      <!-- 下拉刷新和上拉加载列表 -->
      <van-pull-refresh
        v-model="refreshing"
        @refresh="onRefresh"
        :loading-text="_t18('loading')"
        :loosing-text="_t18('release_refresh')"
      >
        <!-- 加载中动画 -->
        <van-loading v-if="showLoading" />
        <!-- 数据列表 -->
        <div v-else>
          <van-list
            v-if="list.length > 0"
            v-model:loading="loading"
            :finished="finished"
            :finished-text="_t18('no_more_data')"
            :loading-text="_t18('loading')"
            @load="onLoad"
          >
            <van-cell v-for="(item, index) in list" :key="index">
              <div class="list-item">
                <!-- 顶部：成员昵称和状态 -->
                <div class="item-header">
                  <div class="member-name">{{ item.nickname || item.username || `用户${item.id || index + 1}` }}</div>
                  <div class="status-badge" :class="getStatusClass(item.status)">
                    {{ getStatusText(item.status) }}
                  </div>
                </div>
                
                <!-- 信息列表 -->
                <div class="item-info">
                  <div class="info-row">
                    <div class="info-label">注册时间</div>
                    <div class="info-value">{{ formatRegisterTime(item.registerTime) }}</div>
                  </div>
                  <div class="info-row">
                    <div class="info-label">充值金额</div>
                    <div class="info-value fw-num">{{ formatAmount(item.rechargeAmount || 0) }} USDT</div>
                  </div>
                  <div class="info-row">
                    <div class="info-label">提现金额</div>
                    <div class="info-value fw-num">{{ formatAmount(item.withdrawAmount || 0) }} USDT</div>
                  </div>
                  <div class="info-row">
                    <div class="info-label">收益总额</div>
                    <div class="info-value fw-num highlight">{{ formatAmount(item.totalProfit || 0) }} USDT</div>
                  </div>
                </div>
              </div>
            </van-cell>
          </van-list>
          <!-- 数据为空 -->
          <Nodata v-else />
        </div>
      </van-pull-refresh>
    </div>

    <!-- 筛选弹窗 -->
    <van-popup
      v-model:show="showFilterPopup"
      position="bottom"
      round
      :style="{ height: '60%' }"
    >
      <div class="filter-popup">
        <div class="filter-header">
          <div class="filter-title">筛选</div>
          <svg-load name="guanbi" class="close-icon" @click="showFilterPopup = false" />
        </div>
        <div class="filter-content">
          <div class="filter-section" v-for="(section, index) in filterSections" :key="index">
            <div class="section-title">{{ section.title }}</div>
            <div class="section-items">
              <div
                class="filter-item"
                v-for="(item, itemIndex) in section.items"
                :key="itemIndex"
                :class="{ active: item.active }"
                @click="toggleFilter(section, item, index)"
              >
                {{ item.label }}
              </div>
            </div>
            <!-- 自定义日期范围选择器 -->
            <div v-if="index === 0 && dateFilter === 'custom'" class="date-range-picker">
              <div class="date-input-group">
                <div class="date-input-item">
                  <div class="date-label">开始日期</div>
                  <div class="date-input" @click="showStartDatePicker = true">
                    {{ dateRange[0] || '请选择' }}
                  </div>
                </div>
                <div class="date-input-item">
                  <div class="date-label">结束日期</div>
                  <div class="date-input" @click="showEndDatePicker = true">
                    {{ dateRange[1] || '请选择' }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 日期选择器 -->
        <van-popup v-model:show="showStartDatePicker" position="bottom">
          <van-date-picker
            v-model="currentStartDate"
            title="选择开始日期"
            :cancel-button-text="'取消'"
            :confirm-button-text="'确认'"
            @confirm="confirmStartDate"
            @cancel="showStartDatePicker = false"
            :columns-type="['year', 'month', 'day']"
          />
        </van-popup>
        
        <van-popup v-model:show="showEndDatePicker" position="bottom">
          <van-date-picker
            v-model="currentEndDate"
            title="选择结束日期"
            :cancel-button-text="'取消'"
            :confirm-button-text="'确认'"
            @confirm="confirmEndDate"
            @cancel="showEndDatePicker = false"
            :columns-type="['year', 'month', 'day']"
          />
        </van-popup>
        <div class="filter-footer">
          <div class="footer-btn reset-btn" @click="resetFilter">重置</div>
          <div class="footer-btn confirm-btn" @click="confirmFilter">确认</div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import HeaderBar from '@/components/HeaderBar/index.vue'
import Nodata from '@/components/Nodata/index.vue'
import TrendLineChart from './components/TrendLineChart.vue'
import BarChart from './components/BarChart.vue'
import { _t18, _timeFormat } from '@/utils/public'
import { _toFixed } from '@/utils/decimal'
import { getAgentInfo } from '@/api/plug.js'

// HeaderBar 右侧筛选图标配置
// 如果 'shaixuan' 图标不存在，可以尝试使用其他图标如 'sousuo'（搜索）或其他合适图标
const cuttentRight = ref({
  iconRight: [
    {
      name:"筛选",
      iconName: '', // 筛选图标名称，可根据项目实际图标调整
      clickTo: '' // 空字符串会触发 showPopup 事件
    }
  ]
})

// 统计数据
const statsList = ref([
  { title: '团队余额', value: '0.00' },
  { title: '团队充值', value: '0.00' },
  { title: '团队提款', value: '0.00' },
  { title: '团队返点', value: '0.00' },
  { title: '团队人数', value: '0' },
  { title: '今日新增人数', value: '0' }
])

// 从推广中心的团队核心数据填充统计区域
const populateStatsFromTeamCore = (teamCore) => {
  const oneCount = teamCore?.oneCount || 0
  const twoCount = teamCore?.twoCount || 0
  const threeCount = teamCore?.threeCount || 0
  const totalMembers = oneCount + twoCount + threeCount
  const sumAmount = Number(teamCore?.sumAmount || 0)

  // 与 plug1.vue 保持一致的推导字段
  const todayProfit = sumAmount * 0.08
  const todayInflow = sumAmount * 0.15
  const todayOutflow = sumAmount * 0.07

  const setStat = (title, val) => {
    const item = statsList.value.find((i) => i.title === title)
    if (item) item.value = val
  }

  setStat('团队余额', _toFixed(sumAmount, 2))
  setStat('团队充值', _toFixed(todayInflow, 2))
  setStat('团队提款', _toFixed(todayOutflow, 2))
  setStat('团队返点', _toFixed(todayProfit, 2))
  setStat('团队人数', String(totalMembers))
  // 今日新增人数：接口未提供，保持 0，待有真实字段再替换
}

const fetchAndPopulateStats = async () => {
  try {
    const res = await getAgentInfo()
    if (res && (res.code === 200 || res.code === '200')) {
      populateStatsFromTeamCore(res.data)
    }
  } catch (e) {
    // 静默失败，保留默认值
  }
}

// 筛选弹窗
const showFilterPopup = ref(false)

// 筛选选项 - 日期和活跃状态
const filterSections = ref([
  {
    title: '日期筛选',
    items: [
      { label: '全部', value: 'all', active: true },
      { label: '今日', value: 'today', active: false },
      { label: '本周', value: 'week', active: false },
      { label: '本月', value: 'month', active: false },
      { label: '自定义', value: 'custom', active: false }
    ]
  },
  {
    title: '活跃状态',
    items: [
      { label: '全部', value: 'all', active: true },
      { label: '活跃', value: 'active', active: false },
      { label: '不活跃', value: 'inactive', active: false }
    ]
  }
])

// 日期筛选值
const dateFilter = ref('all')
const activeStatusFilter = ref('all')

// 自定义日期范围
const dateRange = ref(['', ''])

// 图表数据 - 折线图
const trendChartData = ref({
  dates: [],
  datasets: []
})

// 图表数据 - 柱状图
const memberChartData = ref({
  dates: [],
  values: []
})

// 折线图图例
const trendLegend = ref([
  { label: '充值', color: '#3CBC8C' },
  { label: '提现', color: '#EF4444' }
])

// 初始化图表数据（近7天）
const initChartData = () => {
  const dates = []
  const recharge = []
  const withdraw = []
  const counts = []
  
  const now = new Date()
  for (let i = 6; i >= 0; i--) {
    const date = new Date(now)
    date.setDate(date.getDate() - i)
    const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
    dates.push(dateStr)
    
    // 生成模拟数据
    recharge.push(Math.random() * 50000 + 10000)
    withdraw.push(Math.random() * 30000 + 5000)
    counts.push(Math.floor(Math.random() * 20 + 5))
  }
  
  // 折线图数据格式
  trendChartData.value = {
    dates,
    datasets: [
      {
        values: recharge,
        color: '#3CBC8C',
        label: '充值'
      },
      {
        values: withdraw,
        color: '#EF4444',
        label: '提现'
      }
    ]
  }
  
  // 柱状图数据格式
  memberChartData.value = {
    dates,
    values: counts
  }
}

// 列表数据
const list = ref([])
const refreshing = ref(false)
const showLoading = ref(false)
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// 下拉刷新
const onRefresh = () => {
  refreshing.value = false
  showLoading.value = true
  finished.value = false
  pageNum.value = 1
  loading.value = true
  list.value = []
  getList()
}

// 上拉加载
const onLoad = () => {
  if (finished.value) return
  loading.value = true
  getList()
}

// 获取列表数据
const getList = () => {
  // TODO: 调用实际API获取数据
  // 模拟数据
  setTimeout(() => {
    if (pageNum.value === 1) {
      list.value = []
    }
    
    // 模拟加载数据
    const newData = Array.from({ length: pageSize.value }, (_, i) => {
      const id = pageNum.value * pageSize.value + i
      const now = new Date()
      now.setDate(now.getDate() - Math.floor(Math.random() * 30)) // 随机30天内的注册时间
      
      return {
        id: id,
        username: `user${String(id).padStart(6, '0')}`,
        nickname: `用户${id}`,
        registerTime: now.getTime(),
        rechargeAmount: Math.random() * 10000,
        withdrawAmount: Math.random() * 5000,
        totalProfit: Math.random() * 5000 - 1000, // 可能有负数
        status: Math.random() > 0.5 ? 'active' : 'inactive' // 活跃或不活跃
      }
    })
    
    // 模拟数据为空的情况
    if (pageNum.value > 3) {
      finished.value = true
      list.value = []
    } else {
      list.value = [...list.value, ...newData]
      pageNum.value++
    }
    
    loading.value = false
    showLoading.value = false
  }, 500)
}

// 日期选择器状态
const showStartDatePicker = ref(false)
const showEndDatePicker = ref(false)

// 获取当前日期
const getCurrentDate = () => {
  const now = new Date()
  const year = String(now.getFullYear())
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return [year, month, day]
}

const currentStartDate = ref(getCurrentDate())
const currentEndDate = ref(getCurrentDate())

// 格式化日期
const formatDate = (dateArray) => {
  if (!dateArray || dateArray.length < 3) return ''
  const [year, month, day] = dateArray
  return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
}

// 确认开始日期
const confirmStartDate = () => {
  dateRange.value[0] = formatDate(currentStartDate.value)
  showStartDatePicker.value = false
}

// 确认结束日期
const confirmEndDate = () => {
  dateRange.value[1] = formatDate(currentEndDate.value)
  showEndDatePicker.value = false
}

// 切换筛选选项
const toggleFilter = (section, item, sectionIndex) => {
  section.items.forEach(i => {
    i.active = false
  })
  item.active = true
  
  // 更新对应的筛选值
  if (sectionIndex === 0) {
    dateFilter.value = item.value
    if (item.value !== 'custom') {
      dateRange.value = ['', '']
    }
  } else if (sectionIndex === 1) {
    activeStatusFilter.value = item.value
  }
}

// 重置筛选
const resetFilter = () => {
  filterSections.value.forEach((section, sectionIndex) => {
    section.items.forEach(item => {
      item.active = item.value === 'all'
    })
    
    if (sectionIndex === 0) {
      dateFilter.value = 'all'
      dateRange.value = ['', '']
    } else if (sectionIndex === 1) {
      activeStatusFilter.value = 'all'
    }
  })
}

// 确认筛选
const confirmFilter = () => {
  showFilterPopup.value = false
  // 重置列表并重新加载
  onRefresh()
  // TODO: 这里可以根据 dateFilter 和 activeStatusFilter 以及 dateRange 的值来筛选数据
  console.log('筛选条件:', {
    dateFilter: dateFilter.value,
    activeStatusFilter: activeStatusFilter.value,
    dateRange: dateRange.value
  })
}

// 格式化金额
const formatAmount = (amount) => {
  if (amount === null || amount === undefined) return '0.00'
  return _toFixed(amount, 2)
}

// 格式化注册时间
const formatRegisterTime = (timestamp) => {
  if (!timestamp) return '-'
  return _timeFormat(timestamp, 'YYYY-MM-DD HH:mm', true)
}

// 获取状态文本
const getStatusText = (status) => {
  if (status === 'active') return '活跃'
  if (status === 'inactive') return '不活跃'
  return '未知'
}

// 获取状态样式类
const getStatusClass = (status) => {
  if (status === 'active') return 'status-active'
  if (status === 'inactive') return 'status-inactive'
  return ''
}

onMounted(() => {
  initChartData()
  getList()
  fetchAndPopulateStats()
})
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  background: var(--ex-default-background-color);
}

.content {
  padding: 12px;
}

.report-tip {
  margin: 12px 0;
  color: var(--ex-passive-font-color);
  font-size: 13px;
  text-align: center;
  line-height: 1.5;
}

// 图表区域
.charts-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 16px;
}

// 统计网格布局 - 每行三个
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
  
  .stats-item {
    background: var(--ex-passive-background-color);
    border-radius: 8px;
    padding: 16px 12px;
    text-align: center;
    
    .stats-value {
      font-size: 18px;
      font-weight: bold;
      color: var(--ex-default-font-color);
      margin-bottom: 8px;
    }
    
    .stats-title {
      font-size: 12px;
      color: var(--ex-passive-font-color);
    }
  }
}

// 列表项样式
:deep(.van-cell) {
  background: var(--ex-passive-background-color) !important;
  color: var(--ex-default-font-color) !important;
  margin-bottom: 8px;
  border-radius: 8px;
}

:deep(.van-cell::after) {
  border-bottom: none !important;
}

.list-item {
  padding: 16px;
  
  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--ex-border-color);
    
    .member-name {
      font-size: 16px;
      font-weight: bold;
      color: var(--ex-default-font-color);
    }
    
    .status-badge {
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      
      &.status-active {
        background: rgba(60, 188, 140, 0.1);
        color: #3CBC8C;
      }
      
      &.status-inactive {
        background: rgba(153, 153, 153, 0.1);
        color: #999999;
      }
    }
  }
  
  .item-info {
    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      
      .info-label {
        font-size: 14px;
        color: var(--ex-passive-font-color);
      }
      
      .info-value {
        font-size: 14px;
        color: var(--ex-default-font-color);
        
        &.highlight {
          color: var(--ex-primary-color, #3CBC8C);
          font-weight: bold;
        }
      }
    }
  }
}

// 筛选弹窗样式
.filter-popup {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--ex-default-background-color);
  
  .filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 15px;
    border-bottom: 1px solid var(--ex-border-color);
    
    .filter-title {
      font-size: 18px;
      font-weight: bold;
      color: var(--ex-default-font-color);
    }
    
    .close-icon {
      width: 24px;
      height: 24px;
      cursor: pointer;
    }
  }
  
  .filter-content {
    flex: 1;
    overflow-y: auto;
    padding: 15px;
    
    .filter-section {
      margin-bottom: 24px;
      
      .section-title {
        font-size: 16px;
        font-weight: bold;
        color: var(--ex-default-font-color);
        margin-bottom: 12px;
      }
      
      .section-items {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        
        .filter-item {
          padding: 8px 16px;
          background: var(--ex-passive-background-color);
          border-radius: 20px;
          font-size: 14px;
          color: var(--ex-default-font-color);
          cursor: pointer;
          border: 1px solid transparent;
          transition: all 0.3s;
          
          &.active {
            background: var(--ex-primary-color, #3CBC8C);
            color: #fff;
            border-color: var(--ex-primary-color, #3CBC8C);
          }
        }
      }
      
      // 自定义日期范围选择器
      .date-range-picker {
        margin-top: 16px;
        
        .date-input-group {
          display: flex;
          gap: 12px;
          
          .date-input-item {
            flex: 1;
            
            .date-label {
              font-size: 12px;
              color: var(--ex-passive-font-color);
              margin-bottom: 8px;
            }
            
            .date-input {
              padding: 10px 12px;
              background: var(--ex-passive-background-color);
              border-radius: 8px;
              font-size: 14px;
              color: var(--ex-default-font-color);
              cursor: pointer;
              border: 1px solid var(--ex-border-color);
            }
          }
        }
      }
    }
  }
  
  .filter-footer {
    display: flex;
    gap: 12px;
    padding: 15px;
    border-top: 1px solid var(--ex-border-color);
    
    .footer-btn {
      flex: 1;
      height: 44px;
      line-height: 44px;
      text-align: center;
      border-radius: 8px;
      font-size: 16px;
      cursor: pointer;
      
      &.reset-btn {
        background: var(--ex-passive-background-color);
        color: var(--ex-default-font-color);
      }
      
      &.confirm-btn {
        background: var(--ex-primary-color, #3CBC8C);
        color: #fff;
      }
    }
  }
}
</style>
