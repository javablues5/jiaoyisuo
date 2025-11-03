<template>
  <div class="page-wrap">
    <HeaderBar :currentName="'推荐信息'" />
    <div class="content">
      <!-- 顶部提示 -->
      <div class="tip">
        如果想赚取丰厚的推荐佣金，请复制推荐地址并发给您的亲朋好友注册（推荐地址请用浏览器打开）
      </div>

      <!-- 信息列表 -->
      <van-cell-group inset>
        <van-cell title="我的用户名" :value="usernameDisplay" />

        <van-cell title="我的推荐码" :value="sharkCode || '-'" is-link>
          <template #right-icon>
            <van-button size="small" type="primary" plain @click.stop="copyText(sharkCode)">复制</van-button>
          </template>
        </van-cell>

        <van-cell title="推广二维码" :value="''" is-link @click="showQr = true">
          <template #right-icon>
            <van-button size="small" type="primary" @click.stop="showQr = true">推广二维码展示</van-button>
          </template>
        </van-cell>

        <van-cell title="推荐地址" :value="shareFullLink" is-link>
          <template #right-icon>
            <van-button size="small" type="primary" plain @click.stop="copyText(shareFullLink)">复制</van-button>
          </template>
        </van-cell>

        <van-cell title="加密地址" :value="encryptLink" is-link>
          <template #right-icon>
            <van-button size="small" type="primary" plain @click.stop="copyText(encryptLink)">复制</van-button>
          </template>
        </van-cell>

        <van-cell title="会员推荐人数" :value="memberTotal" />
        <van-cell title="本月推荐收益佣金" :value="monthCommission" />
      </van-cell-group>
    </div>

    <!-- 二维码弹窗 -->
    <van-popup v-model:show="showQr" round :style="{ padding: '16px' }">
      <div class="qr-box">
        <QRCode :address="sharkCode" />
        <div class="qr-link">{{ shareFullLink }}</div>
      </div>
    </van-popup>
  </div>
  
</template>

<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import QRCode from '@/components/common/QRCode/index.vue'
import { useUserStore } from '@/store/user/index'
import { getAgentInfo } from '@/api/plug.js'
import { onMounted, ref, computed } from 'vue'
import { showToast } from 'vant'

const userStore = useUserStore()
const userInfo = userStore.userInfo
const sharkCode = userInfo?.user?.activeCode
const usernameDisplay = computed(() => userInfo?.user?.loginName|| '-')

const shareLink = `${location.origin}/#/i&`
const shareFullLink = computed(() => `${shareLink}${sharkCode || ''}`)
// 暂无单独加密地址字段，临时与推荐地址一致，若后端提供则替换
const encryptLink = computed(() => shareFullLink.value)

const memberTotal = ref(0)
const monthCommission = ref('0')
const showQr = ref(false)

const copyText = async (text) => {
  if (!text) return
  try {
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.setAttribute('readonly', '')
    textarea.style.position = 'absolute'
    textarea.style.left = '-9999px'
    document.body.appendChild(textarea)
    textarea.select()
    textarea.setSelectionRange(0, text.length)
    const ok = document.execCommand('copy')
    document.body.removeChild(textarea)
    showToast(ok ? '复制成功' + text : '复制失败，请手动复制')
  } catch (err) {
    showToast('复制失败，请手动复制' + text)
  }
}

const fetchAgentInfo = async () => {
  try {
    const res = await getAgentInfo()
    if (res && (res.code === '200' || res.code === 200)) {
      const d = res.data || {}
      memberTotal.value = (d.oneCount || 0) + (d.twoCount || 0) + (d.threeCount || 0)
      monthCommission.value = (d.sumAmount || 0).toFixed ? (d.sumAmount || 0).toFixed(2) : `${d.sumAmount || 0}`
    }
  } catch (err) {
    // 忽略
  }
}

onMounted(() => {
  fetchAgentInfo()
})
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  background: var(--ex-default-background-color);
}

.tip {
  font-size: 12px;
  color: var(--ex-passive-font-color);
  background: var(--ex-passive-background-color);
  border-radius: 8px;
  padding: 10px 12px;
  line-height: 1.5;
  margin-bottom: 12px;
}
::v-deep(.van-cell-group--inset) {
  margin: 0;
  background: transparent;
}
::v-deep(.van-cell) {
  background: var(--ex-passive-background-color);
  color: var(--ex-default-font-color);
}
:deep(.van-button) {
  background: var(--ex-primary-color);
  color: var(--ex-default-font-color);
  margin-left: 10px;
}
.qr-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  .qr-link {
    margin-top: 8px;
    font-size: 12px;
    word-break: break-all;
    color: var(--ex-passive-font-color);
    max-width: 260px;
    text-align: center;
  }
}
</style>


