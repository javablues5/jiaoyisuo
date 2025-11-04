<template>
  <div class="page-wrap">
    <HeaderBar :currentName="_t18('my_invest')" />
    <div class="content columnFlex">
      <!-- 汇总统计 -->
      <div class="summary">
        <div class="summary-item">
          <div class="label">{{ _t18('sum_bet_value') }}</div>
          <div class="value fw-num">{{ headerObj.sumAmount || 0 }} USDT</div>
        </div>
        <div class="summary-item">
          <div class="label">{{ _t18('earn_rewards') }}</div>
          <div class="value fw-num">{{ headerObj.commission || 0 }} USDT</div>
        </div>
        <div class="summary-item">
          <div class="label">{{ _t18('Cumulative_income') }}</div>
          <div class="value fw-num highlight">{{ headerObj.sumEarn || 0 }} USDT</div>
        </div>
        <div class="summary-item">
          <div class="label">{{ _t18('number_of_positions') }}</div>
          <div class="value fw-num">{{ headerObj.position || 0 }}</div>
        </div>
      </div>

      <!-- 交易记录 -->
      <div class="record-title">{{ _t18('transaction_record') }}</div>
      <div class="record-list">
        <van-list
          v-if="list.length > 0 && !isShow"
          v-model:loading="loading"
          :finished="finished"
          :finished-text="_t18('no_more_data')"
          :loading-text="_t18('loading')"
          @load="onLoad"
        >
          <RecordItem v-for="item in list" :key="item.id || JSON.stringify(item)" :itemObj="item" />
        </van-list>
        <Nodata v-if="list.length === 0 && isShow" />
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import RecordItem from '@/views/financial/components/recordItem.vue'
import { ref, onMounted } from 'vue'
import { investmentList, personalIncome } from '@/api/financial/index'
import { _t18 } from '@/utils/public'

const headerObj = ref({})
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(5)
const isShow = ref(false)
const loading = ref(false)
const finished = ref(false)
const total = ref(0)

const fetchSummary = async () => {
  try {
    const res = await personalIncome()
    if (res && (res.code === 200 || res.code === '200')) {
      headerObj.value = res.data || {}
    }
  } catch (e) {}
}

const fetchInvestmentPage = async () => {
  try {
    const res = await investmentList(pageNum.value, pageSize.value)
    if (res && (res.code === 200 || res.code === '200')) {
      total.value = res.total || 0
      return res.rows || []
    } else {
      finished.value = true
      return []
    }
  } catch (e) {
    finished.value = true
    return []
  }
}

const onLoad = async () => {
  loading.value = true
  const data = await fetchInvestmentPage()
  list.value = [...list.value, ...data]
  isShow.value = list.value.length === 0
  if (list.value.length >= total.value) {
    finished.value = true
  }
  pageNum.value++
  loading.value = false
}

onMounted(() => {
  fetchSummary()
  onLoad()
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
.columnFlex { display: flex; flex-direction: column; height: calc(100vh - 44px); }
.summary { padding: 10px 3px 6px; }
.summary-item { padding: 10px 9px; display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: var(--ex-passive-font-color); }
.summary-item .value { font-size: 12px; color: var(--ex-default-font-color); }
.summary-item .highlight { color: var(--ex-font-color9); }
.record-title { font-size: 16px; color: var(--ex-default-font-color); padding: 14px 6px 6px; border-top: 1px solid var(--ex-border-color); }
.record-list { flex: 1; overflow: auto; }
:deep(.van-list__finished-text) { height: 72px; display: flex; align-items: center; justify-content: center; font-size: 14px; color: var(--ex-passive-font-color); }
</style>





