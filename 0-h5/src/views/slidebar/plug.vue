<template>
  <div class="agent-center">
    <HeaderBar :currentName="'代理中心'" :border_bottom="false" />

    <div class="page-body">
      <!-- 顶部横幅：根据主题切换样式，高190px，100%宽度，文案中英文 -->
      <div
        :class="['agent-banner', { dark: __theme === 'dark' }]"
        :style="__theme === 'dark'
          ? { color: '#fff', boxShadow: '0 4px 21px 0 rgba(11,18,32,0.28)' }
          : { color: '#233454', boxShadow: '0 4px 17px 0 rgba(74,93,154,0.09)' }
        "
      >
        <div class="banner-text">
          <div
            class="title-cn"
            :style="{
              fontWeight: 700,
              fontSize: '22px',
              color: __theme === 'dark' ? '#fff' : '#223468',
              textShadow: __theme === 'dark' ? '0 2px 16px rgba(33,89,237,0.22)' : '0 2px 10px #e6edff'
            }"
          >代理连接一切</div>
          <div
            class="title-en"
            :style="{
              marginTop: '6px',
              fontSize: '14px',
              color: __theme === 'dark' ? '#C3CCE5' : '#7490D8',
              letterSpacing: '0.02em'
            }"
          >Agent connects everything</div>
        </div>
      </div>

      <!-- 功能入口列表 -->
      <van-cell-group inset>
        <van-cell title="推荐信息" is-link @click="go('/plug/recommend')" />
        <van-cell title="我的收益" is-link @click="go('/plug/income')" />
        <van-cell title="下级报表" is-link @click="go('/plug/report')" />
        <van-cell title="添加会员" is-link @click="go('/plug/add-member')" />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const go = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.agent-center {
  min-height: 100vh;
  background: var(--ex-default-background-color);
}

.agent-banner {
  position: relative;
  width: auto;
  height: 190px;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(135deg, #e8f0ff 0%, #f3f6ff 100%);
  margin-bottom: 12px;
  margin: 0 12px 12px;

  &.dark {
    background: linear-gradient(135deg, #0b1220 0%, #121a2b 100%);
  }

  .banner-text {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 0 16px;

    .title-cn {
      font-size: 20px;
      font-weight: 700;
      color: var(--ex-font-color6);
      margin-bottom: 6px;
    }
    .title-en {
      font-size: 12px;
      color: var(--ex-passive-font-color);
    }
  }
}

::v-deep(.van-cell-group--inset) {
  margin: 0;
  background: transparent;
}

::v-deep(.van-cell) {
  background: var(--ex-passive-background-color);
  color: var(--ex-default-font-color);
}
</style>