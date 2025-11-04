<template>
  <div class="agent-center">
    <HeaderBar :currentName="'代理中心'" :border_bottom="false" />

    <div class="page-body">
      <div class="agent-banner">
        <div class="banner-text">
          <div class="title-cn">代理连接一切</div>
          <div class="title-en">Agent connects everything</div>
        </div>
      </div>

      <!-- 功能入口列表 -->
      <van-cell-group inset>
        <van-cell title="推荐信息" is-link @click="go('/plug/recommend')" />
        <van-cell title="我的收益" is-link @click="go('/plug/income')" />
        <van-cell title="下级报表" is-link @click="go('/plug/report')" />
        <van-cell title="添加会员" is-link @click="goSignUp()" />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user/index'
const router = useRouter()
const userStore = useUserStore()

const go = (path) => {
  router.push(path)
}

const goSignUp = () => {
  const inviteCode = userStore.userInfo?.user?.activeCode || ''
  router.push(`/sign-up?invite_code=${inviteCode}`)
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
      font-size: 22px;
      font-weight: 700;
      color: #223468;
      margin-bottom: 6px;
      text-shadow: 0 2px 10px #e6edff;
    }
    .title-en {
      font-size: 14px;
      margin-top: 6px;
      color: #7490D8;
      letter-spacing: 0.02em;
    }
  }
}

/* 主题：dark 覆盖 */
[theme='dark'] .agent-banner {
  background: linear-gradient(135deg, #0b1220 0%, #121a2b 100%);
  color: #fff;
  box-shadow: 0 4px 21px 0 rgba(11, 18, 32, 0.28);
}
[theme='dark'] .agent-banner .title-cn {
  color: #fff;
  text-shadow: 0 2px 16px rgba(33, 89, 237, 0.22);
}
[theme='dark'] .agent-banner .title-en {
  color: #C3CCE5;
}

/* 主题：light 覆盖（确保与原内联一致的投影效果） */
[theme='light'] .agent-banner {
  color: #233454;
  box-shadow: 0 4px 17px 0 rgba(74, 93, 154, 0.09);
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