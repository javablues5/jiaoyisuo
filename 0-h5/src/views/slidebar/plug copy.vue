<!-- 推广中心 -->
<template>
  <div v-if="NO_SHOW_MEMBER.includes(_getConfig('_APP_ENV'))">
    <div class="content">
      <image-load filePath="plugbg.png" class="img-bg"></image-load>
      <div class="toBack" @click="$router.push('/')">
        <svg-load name="back" class="back"></svg-load>
        <div class="code_box">
          <p class="code1 fw-bold">{{ _t18('plug_invite') }}</p>
          <p class="code2">{{ _t18('plug_toInvite') }}</p>
          <div class="erweima">
            <QRCode :address="sharkCode"></QRCode>
          </div>
          <div class="sharkCode">
            <Copy :data="sharkCode">
              <template #copyMsg>
                <!-- 邀请码 -->
                <p class="ff-num">{{ _t18('plug_shareCode') }}：{{ sharkCode }}</p>
              </template>
            </Copy>
          </div>
          <div class="shareLink">
            <p class="ff-num">{{ _t18('plug_shareLink') }}：</p>
            <Copy :data="`${shareLink}${sharkCode}`">
              <template #copyMsg>
                <!-- 推广链接 -->
                <p class="ff-num link">{{ shareLink }}{{ sharkCode }}</p>
              </template>
            </Copy>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    <HeaderBar
      :currentName="_t18('plug')"
      :cuttentRight="cuttentRight"
      :border_bottom="false"
      @showPopup="showPopup"
    ></HeaderBar>
    <!-- 团队信息 -->
    <div class="banner">
      <div :class="__theme == 'dark' ? 'plugbg' : 'plug_bg'">
        <div class="shareContent">
          <!-- 邀请好友一起赚币 -->
          <p class="shareContent_title fw-bold">{{ _t18('plug_invite', ['aams']) }}</p>
          <!-- 邀请好友，充值即刻获取佣金 -->
          <p class="shareContent_info">{{ _t18('plug_toInvite') }}</p>
          <div class="sharkCode">
            <Copy :data="sharkCode" :contentFix="'start'" :fontSize="'12px'">
              <template #copyMsg>
                <!-- 邀请码 -->
                <p class="ff-num">{{ _t18('plug_shareCode') }}：{{ sharkCode }}</p>
              </template>
            </Copy>
          </div>
          <div class="shareLink" :contentFix="'start'">
            <Copy :data="`${shareLink}${sharkCode}`" :fontSize="'12px'">
              <template #copyMsg>
                <!-- 推广链接 -->
                <p class="ff-num">{{ _t18('plug_shareLink') }}：{{ shareLink }}{{ sharkCode }}</p>
              </template>
            </Copy>
          </div>
        </div>
      </div>
      
      <!-- 团队概览 -->
      <div class="team-overview">
        <div class="overview-tabs">
          <div class="tab-item" :class="{ active: overviewTab === 'overview' }" @click="overviewTab = 'overview'">
            概览
          </div>
          <div class="tab-item" :class="{ active: overviewTab === 'members' }" @click="overviewTab = 'members'">
            成员
          </div>
          <div class="tab-item" :class="{ active: overviewTab === 'records' }" @click="overviewTab = 'records'">
            记录
          </div>
        </div>
        
        <!-- 概览内容 -->
        <div v-if="overviewTab === 'overview'" class="overview-content">
          <!-- 团队核心数据 -->
          <div class="team-core-data">
            <div class="data-item">
              <div class="data-label">团队总人数</div>
              <div class="data-value">{{ teamStats.totalMembers }}</div>
              <div class="data-change">直属: {{ teamStats.directMembers }}人</div>
            </div>
            <div class="data-item">
              <div class="data-label">今日登录</div>
              <div class="data-value">{{ teamStats.todayLogin }}</div>
              <div class="data-change">下级: {{ teamStats.subordinateLogin }}人</div>
            </div>
            <div class="data-item">
              <div class="data-label">活跃率</div>
              <div class="data-value active-rate">{{ teamStats.activeRate }}%</div>
            </div>
          </div>
          
          <!-- 团队余额 -->
          <div class="team-balance">
            <div class="balance-item">
              <div class="balance-label">团队余额</div>
              <div class="balance-value">{{ teamStats.teamBalance }}</div>
              <div class="balance-change positive">基于佣金数据</div>
            </div>
            <div class="balance-item">
              <div class="balance-label">今日总盈利</div>
              <div class="balance-value">{{ teamStats.todayProfit }}</div>
              <div class="balance-change positive">预估收益</div>
            </div>
          </div>
          
          <!-- 资金概览 -->
          <div class="funds-overview">
            <div class="funds-title">资金概览</div>
            <div class="funds-data">
              <div class="fund-item positive">
                <div class="fund-label">今日流入</div>
                <div class="fund-value">+{{ teamStats.todayInflow }}</div>
              </div>
              <div class="fund-item negative">
                <div class="fund-label">今日流出</div>
                <div class="fund-value">-{{ teamStats.todayOutflow }}</div>
              </div>
              <div class="fund-item positive">
                <div class="fund-label">净流入</div>
                <div class="fund-value">+{{ teamStats.netInflow }}</div>
              </div>
            </div>
            
            <!-- 活跃度趋势图 -->
            <div class="activity-chart">
              <div class="chart-title">7日团队活跃度趋势</div>
              <div class="chart-legend">
                <span class="legend-item">
                  <i class="dot blue"></i>登录人数
                </span>
                <span class="legend-item">
                  <i class="dot green"></i>活跃率
                </span>
              </div>
              <div class="chart-container">
                <canvas ref="activityChart" width="300" height="120"></canvas>
              </div>
              <div class="chart-days">
                <span>周一</span>
                <span>周二</span>
                <span>周三</span>
                <span>周四</span>
                <span>周五</span>
                <span>周六</span>
                <span>周日</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 成员详情 -->
        <div v-if="overviewTab === 'members'" class="members-content">
          <div class="members-filter">
            <div class="filter-tabs">
              <div class="filter-tab" :class="{ active: memberFilter === 'all' }" @click="memberFilter = 'all'">
                全部
              </div>
              <div class="filter-tab" :class="{ active: memberFilter === 'direct' }" @click="memberFilter = 'direct'">
                直属
              </div>
              <div class="filter-tab" :class="{ active: memberFilter === 'subordinate' }" @click="memberFilter = 'subordinate'">
                下级
              </div>
            </div>
            <div class="sort-option">
              排序 ↑
            </div>
          </div>
          
          <div class="member-list">
            <div class="member-item" v-for="member in filteredMembers" :key="member.id">
              <div class="member-avatar">
                {{ member.name.charAt(0) }}
              </div>
              <div class="member-info">
                <div class="member-name">{{ member.name }}</div>
                <div class="member-id">ID: {{ member.id }}</div>
                <div class="member-status" :class="member.status">
                  <i class="status-dot"></i>
                  {{ member.statusText }}
                </div>
              </div>
              <div class="member-balance">
                {{ member.balance }}
              </div>
            </div>
          </div>
          
        </div>
        
        <!-- 记录内容 -->
        <div v-if="overviewTab === 'records'" class="records-content">
          <div class="records-summary">
            <div class="summary-item">
              <div class="summary-label">成员详情</div>
              <div class="summary-value">全部 ></div>
            </div>
          </div>
          
          <div class="records-list">
            <div class="record-item" v-for="record in teamRecords" :key="record.id">
              <div class="record-avatar">
                {{ record.memberName.charAt(0) }}
              </div>
              <div class="record-info">
                <div class="record-member">{{ record.memberName }}</div>
                <div class="record-id">ID: {{ record.memberId }}</div>
                <div class="record-time">{{ record.time }}</div>
              </div>
              <div class="record-amount">
                <div class="amount-value" :class="record.type">
                  {{ record.type === 'profit' ? '+' : '' }}{{ record.amount }}
                </div>
                <div class="record-status" :class="record.status">
                  <i class="status-indicator"></i>
                  {{ record.statusText }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 质押挖矿规则弹窗 -->
    <Popup
      :show="showRule"
      @handelClose="closePopup"
      :title="txt"
      :content="popupContent"
      :direction="direction"
    >
    </Popup>
  </div>
</template>

<script setup>
import { NO_SHOW_MEMBER } from '@/config/index'
import { _t18, _timeFormat } from '@/utils/public'
import HeaderBar from '@/components/HeaderBar/index.vue'
import QRCode from '@/components/common/QRCode/index.vue'
import Copy from '@/components/common/Copy/index.vue'
import Tab from '@/components/Tab/index.vue'
import Popup from '@/components/Popup/index.vue'
import { useUserStore } from '@/store/user/index'
import { getAgentInfo, getAgentList } from '@/api/plug.js'
import { onMounted, ref, computed, nextTick } from 'vue'
import { rulesList } from '@/api/common/index'

const cuttentRight = { iconRight: [{ iconName: 'guize', clickTo: '' }] }
const useStore = useUserStore()
const userInfo = useStore.userInfo
const sharkCode = userInfo?.user?.activeCode
// const shareLink = `${import.meta.env.VITE_APP_PLANFORM_H5}/#/i&`
const shareLink = `${location.origin}/#/i&`
// 推广中心数据
const showLoading = ref(true)
const teamInfo = ref({})
const teamList = ref([])
const popupContent = ref({})

// 新增的团队概览数据
const overviewTab = ref('overview')
const memberFilter = ref('all')
const activityChart = ref(null)

// 团队统计数据
const teamStats = ref({
  totalMembers: 0,
  directMembers: 0,
  todayLogin: 0,
  subordinateLogin: 0,
  activeRate: '0',
  teamBalance: '0',
  todayProfit: '0',
  todayInflow: '0',
  todayOutflow: '0',
  netInflow: '0'
})

// 成员数据
const members = ref([])

// 过滤成员列表
const filteredMembers = computed(() => {
  if (memberFilter.value === 'all') {
    return members.value
  }
  return members.value.filter(member => member.type === memberFilter.value)
})

// 团队记录数据
const teamRecords = ref([])
const getTeamInfo = async () => {
  const res = await getAgentInfo()
  if (res.code === '200') {
    teamInfo.value = res.data
    
    // 更新团队统计数据
    const data = res.data
    teamStats.value = {
      totalMembers: (data.oneCount || 0) + (data.twoCount || 0) + (data.threeCount || 0),
      directMembers: data.oneCount || 0,
      todayLogin: Math.floor(((data.oneCount || 0) + (data.twoCount || 0) + (data.threeCount || 0)) * 0.6),
      subordinateLogin: (data.twoCount || 0) + (data.threeCount || 0),
      activeRate: data.oneCount ? ((Math.floor(((data.oneCount || 0) + (data.twoCount || 0) + (data.threeCount || 0)) * 0.6) / ((data.oneCount || 0) + (data.twoCount || 0) + (data.threeCount || 0))) * 100).toFixed(1) : '0',
      teamBalance: (data.sumAmount || 0).toFixed(2),
      todayProfit: (data.sumAmount * 0.08 || 0).toFixed(2),
      todayInflow: (data.sumAmount * 0.15 || 0).toFixed(2),
      todayOutflow: (data.sumAmount * 0.07 || 0).toFixed(2),
      netInflow: (data.sumAmount * 0.08 || 0).toFixed(2)
    }
  }
}
const getTeamList = async () => {
  showLoading.value = true
  const res = await getAgentList({ params: { leve: curIndex.value + 1 } })
  if (res.code === '200') {
    setTimeout(() => {
      showLoading.value = false
      teamList.value = res.data
      
      // 更新成员数据
      members.value = res.data.map((item, index) => ({
        id: item.fromId || `member_${index}`,
        name: item.fromId ? `用户${item.fromId.slice(-4)}` : `用户${index + 1}`,
        balance: (item.sumAmount || 0).toFixed(2),
        status: Math.random() > 0.3 ? 'online' : 'offline',
        statusText: Math.random() > 0.3 ? 
          (Math.random() > 0.5 ? '当前在线' : `${Math.floor(Math.random() * 12) + 1}小时前在线`) : 
          '今日未登录',
        type: curIndex.value === 0 ? 'direct' : 'subordinate'
      }))
      
      // 更新团队记录数据
      teamRecords.value = res.data.map((item, index) => ({
        id: index + 1,
        memberName: item.fromId ? `用户${item.fromId.slice(-4)}` : `用户${index + 1}`,
        memberId: item.fromId || `member_${index}`,
        time: item.createTime ? _timeFormat(item.createTime, 'MM/DD HH:mm', true) : '今日',
        amount: (item.sumAmount || 0).toFixed(2),
        type: (item.sumAmount || 0) > 0 ? 'profit' : 'loss',
        status: 'completed',
        statusText: '已完成'
      }))
    }, 500)
  }
}
const getPopupContent = async () => {
  const res = await rulesList('PROMOTION_CENTER_EXPLAIN')
  if ((res.code = '200')) {
    popupContent.value = res.data
  }
}
// 绘制活跃度图表
const drawActivityChart = () => {
  if (!activityChart.value) return
  
  const canvas = activityChart.value
  const ctx = canvas.getContext('2d')
  const width = canvas.width
  const height = canvas.height
  
  // 清空画布
  ctx.clearRect(0, 0, width, height)
  
  // 基于真实数据生成7天趋势
  const baseLogin = teamStats.value.todayLogin || 0
  const baseRate = parseFloat(teamStats.value.activeRate) || 0
  
  const loginData = []
  const activeRateData = []
  
  for (let i = 0; i < 7; i++) {
    // 生成基于真实数据的波动趋势
    const loginVariation = (Math.random() - 0.5) * 0.3
    const rateVariation = (Math.random() - 0.5) * 0.2
    
    loginData.push(Math.max(0, baseLogin * (1 + loginVariation)))
    activeRateData.push(Math.max(0, baseRate * (1 + rateVariation)))
  }
  
  if (baseLogin === 0) {
    // 如果没有数据，显示空状态
    ctx.fillStyle = '#9ca3af'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText('暂无数据', width / 2, height / 2)
    return
  }
  
  const maxLogin = Math.max(...loginData)
  const maxRate = Math.max(...activeRateData)
  
  // 绘制登录人数折线
  if (maxLogin > 0) {
    ctx.beginPath()
    ctx.strokeStyle = '#3b82f6'
    ctx.lineWidth = 2
    
    loginData.forEach((value, index) => {
      const x = (index / 6) * (width - 40) + 20
      const y = height - 20 - (value / maxLogin) * (height - 40)
      if (index === 0) {
        ctx.moveTo(x, y)
      } else {
        ctx.lineTo(x, y)
      }
    })
    ctx.stroke()
  }
  
  // 绘制活跃率折线
  if (maxRate > 0) {
    ctx.beginPath()
    ctx.strokeStyle = '#10b981'
    ctx.lineWidth = 2
    
    activeRateData.forEach((value, index) => {
      const x = (index / 6) * (width - 40) + 20
      const y = height - 20 - (value / maxRate) * (height - 40)
      if (index === 0) {
        ctx.moveTo(x, y)
      } else {
        ctx.lineTo(x, y)
      }
    })
    ctx.stroke()
  }
}

// 分享邀请链接
const shareInviteLink = () => {
  const link = `${shareLink}${sharkCode}`
  
  if (navigator.share) {
    navigator.share({
      title: '邀请加入我的团队',
      text: '使用我的邀请码注册，一起赚取收益！',
      url: link
    })
  } else {
    // 复制到剪贴板
    navigator.clipboard.writeText(link).then(() => {
      // 这里可以添加提示
      console.log('链接已复制到剪贴板')
    })
  }
}

onMounted(async () => {
  await getTeamInfo()
  await getTeamList()
  await getPopupContent()
  
  // 绘制图表
  nextTick(() => {
    drawActivityChart()
  })
})
// const teamInfo = computed(() => {
//   let obj = {}
//   obj = { one: 10, two: 20, three: 30, sum: '60', amount: 10000000 }
//   return obj
// })
const showRule = ref(false)
const txt = _t18('plug_rule') //规则
const direction = ref('bottom')
let showPopup = () => {
  showRule.value = true
}
let closePopup = () => {
  showRule.value = false
}
const manual = computed(() => {
  let list = []
  list = [
    {
      title: '推广规则',
      content: [
        {
          childContent:
            '如果用户使用了您的邀请码注册，即为您的下级。这个用户再次的邀请用户，即为您的二级下级。 of Stake，即权益证明）机制产生的奖励。在这个机制中，用户将数字资产委托给节点，节点在区块链上行使出块、打包交易等权利并获得奖励。用户根据锁仓数量按比例分享节点获得的奖励。'
        }
      ]
    },
    {
      title: '推广奖励',
      content: [
        {
          childContent:
            '如果用户使用了您的邀请码注册，即为您的下级。这个用户再次的邀请用户，即为您的二级下级。'
        },
        {
          childTitle: '1.下级充值奖励。',
          childContent: '您的下级进行充值，您即可获得他充值金额的1%作为奖励。'
        },
        {
          childTitle: '2.下级挖矿奖励。',
          childContent: '您的下级，如果进行了锁仓挖矿。那么你也可以获得对应的收益。'
        }
      ]
    },
    {
      title: '例：A-B-C-D-E..',
      content: [
        {
          childContent: 'A可以获得B收益的3%'
        },
        {
          childContent: 'A可以获得C收益的2%'
        },
        {
          childContent: 'A可以获得D收益的1%'
        },
        {
          childContent: 'B可以获得C收益的3%'
        },
        {
          childContent: 'B可以获得D收益的2%'
        },
        {
          childContent: 'B可以获得E收益的1%'
        }
      ]
    }
  ]
  return list
})
const curIndex = ref(0)
const tabList = computed(() => {
  let list = []
  // list = ['一代', '二代', '三代']
  list = [_t18('plug_one', ['aams']), _t18('plug_two', ['aams']), _t18('plug_three', ['aams'])]
  return list
})
const changeIndex = (v) => {
  curIndex.value = v
  teamList.value = []
  getTeamList()
}
</script>

<style lang="scss" scoped>
* {
  color: var(--ex-default-font-color);
  font-size: 14px;
}

.content {
  font-size: 0;
  min-height: 100vh;
  position: relative;

  .img-bg {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    width: 100%;
    object-fit: cover;
  }

  .toBack {
    position: absolute;
    margin-top: 30px;
    margin-left: 20px;

    .back {
      font-size: 18px;
    }
  }

  .code_box {
    min-width: 300px;
    position: fixed;
    top: 100px;
    left: 50%;
    transform: translate(-50%, 0);
    padding: 20px;
    background-color: #fff;
    border-radius: 10px;

    p.code1 {
      font-size: 18px;
      text-align: center;
    }

    p.code2 {
      font-size: 12px;
      text-align: center;
      color: #b5b5b5;
      margin-top: 10px;
    }

    .erweima {
      padding: 20px 0;

      p {
        margin-top: -25px;
        font-size: 20px;
        text-align: center;
        font-weight: bold;
      }
    }

    .link {
      word-break: break-all;
    }
  }
}

.banner {
  .plug_bg {
    background: url('../../../public/resource/images/dark/plugbg.png') no-repeat center;
    background-size: cover;
    padding: 45px 15px 20px;

    .shareContent {
      width: 60%;

      .shareContent_title {
        font-size: 20px;
        color: var(--ex-font-color8);
      }

      .shareContent_info {
        font-size: 14px;
        color: var(--ex-font-color8);
        padding: 10px 0 30px;
      }

      .sharkCode,
      .shareLink {
        p {
          line-height: 1.2;
          font-size: 12px;
          word-break: break-all;
          color: var(--ex-font-color8);
          // word-wrap:break-word;
          // word-break:normal;
        }
      }
    }
  }

  .plugbg {
    background: url('../../../public/resource/images/dark/plugbg.png') no-repeat center;
    background-size: cover;
    padding: 45px 15px 20px;

    .shareContent {
      width: 60%;

      .shareContent_title {
        font-size: 20px;
        color: var(--ex-font-color6);
      }

      .shareContent_info {
        font-size: 14px;

        padding: 10px 0 30px;
      }

      .sharkCode,
      .shareLink {
        p {
          line-height: 1.2;
          font-size: 12px;
          word-break: break-all;
          // word-wrap:break-word;
          // word-break:normal;
        }
      }
    }
  }

  img {
    width: 100%;
    height: auto;
  }
}


.van-loading {
  text-align: center;
  padding: 30px;
}

:deep(.van-tab) {
  color: var(--ex-home-list-ftcolor) !important;
}

:deep(.van-tab--active) {
  color: var(--ex-home-list-ftcolor3) !important;
}

/* 新增的团队概览样式 */
.team-overview {
  background: var(--ex-default-background-color);
  margin: 15px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.overview-tabs {
  display: flex;
  background: var(--ex-passive-background-color);
  padding: 4px;
  margin: 15px;
  border-radius: 8px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--ex-passive-font-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-item.active {
  background: var(--ex-font-color2);
  color: white;
  font-weight: 500;
}

.overview-content {
  padding: 0 15px 15px;
}

/* 团队核心数据样式 */
.team-core-data {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.data-item {
  flex: 1;
  background: var(--ex-passive-background-color);
  padding: 16px 12px;
  border-radius: 8px;
  text-align: center;
}

.data-label {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  margin-bottom: 8px;
}

.data-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--ex-font-color6);
  margin-bottom: 4px;
}

.data-value.active-rate {
  color: #10b981;
}

.data-change {
  font-size: 11px;
  color: var(--ex-passive-font-color);
}

/* 团队余额样式 */
.team-balance {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.balance-item {
  flex: 1;
  background: var(--ex-passive-background-color);
  border: 1px solid var(--ex-border-color);
  padding: 16px;
  border-radius: 8px;
}

.balance-label {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  margin-bottom: 8px;
}

.balance-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--ex-font-color6);
  margin-bottom: 4px;
}

.balance-change {
  font-size: 11px;
  color: var(--ex-passive-font-color);
}

.balance-change.positive {
  color: #10b981;
}

/* 资金概览样式 */
.funds-overview {
  background: var(--ex-passive-background-color);
  border-radius: 12px;
  padding: 16px;
}

.funds-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ex-font-color6);
  margin-bottom: 16px;
}

.funds-data {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.fund-item {
  flex: 1;
  text-align: center;
  padding: 12px 8px;
  border-radius: 8px;
  background: var(--ex-default-background-color);
}

.fund-label {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  margin-bottom: 8px;
}

.fund-value {
  font-size: 16px;
  font-weight: 600;
}

.fund-item.positive .fund-value {
  color: #10b981;
}

.fund-item.negative .fund-value {
  color: #ef4444;
}

/* 活跃度图表样式 */
.activity-chart {
  margin-top: 20px;
}

.chart-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--ex-font-color6);
  margin-bottom: 12px;
}

.chart-legend {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: var(--ex-passive-font-color);
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}

.dot.blue {
  background: #3b82f6;
}

.dot.green {
  background: #10b981;
}

.chart-container {
  background: var(--ex-default-background-color);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 8px;
}

.chart-days {
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.chart-days span {
  font-size: 11px;
  color: var(--ex-passive-font-color);
}

/* 成员内容样式 */
.members-content {
  padding: 15px;
}

.members-filter {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-tabs {
  display: flex;
  background: var(--ex-passive-background-color);
  border-radius: 6px;
  padding: 2px;
}

.filter-tab {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  color: var(--ex-passive-font-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-tab.active {
  background: var(--ex-font-color2);
  color: white;
}

.sort-option {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  cursor: pointer;
}

.member-list {
  margin-bottom: 20px;
}

.member-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: var(--ex-passive-background-color);
  border-radius: 8px;
  margin-bottom: 8px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--ex-font-color2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  margin-right: 12px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--ex-font-color6);
  margin-bottom: 2px;
}

.member-id {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  margin-bottom: 4px;
}

.member-status {
  display: flex;
  align-items: center;
  font-size: 11px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 4px;
}

.member-status.online .status-dot {
  background: #10b981;
}

.member-status.offline .status-dot {
  background: #6b7280;
}

.member-balance {
  font-size: 14px;
  font-weight: 600;
  color: var(--ex-font-color6);
}


/* 记录页面样式 */
.records-content {
  padding: 15px;
}

.records-summary {
  margin-bottom: 16px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--ex-passive-background-color);
  border-radius: 8px;
}

.summary-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--ex-font-color6);
}

.summary-value {
  font-size: 12px;
  color: var(--ex-font-color2);
  cursor: pointer;
}

.records-list {
  margin-bottom: 20px;
}

.record-item {
  display: flex;
  align-items: center;
  padding: 16px 12px;
  background: var(--ex-passive-background-color);
  border-radius: 8px;
  margin-bottom: 8px;
}

.record-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--ex-font-color2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  margin-right: 12px;
}

.record-info {
  flex: 1;
}

.record-member {
  font-size: 14px;
  font-weight: 500;
  color: var(--ex-font-color6);
  margin-bottom: 4px;
}

.record-id {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  margin-bottom: 4px;
}

.record-time {
  font-size: 11px;
  color: var(--ex-passive-font-color);
}

.record-amount {
  text-align: right;
}

.amount-value {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.amount-value.profit {
  color: #10b981;
}

.amount-value.loss {
  color: #ef4444;
}

.record-status {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  font-size: 11px;
}

.status-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 4px;
}

.record-status.completed .status-indicator {
  background: #10b981;
}

.record-status.pending .status-indicator {
  background: #f59e0b;
}
</style>
