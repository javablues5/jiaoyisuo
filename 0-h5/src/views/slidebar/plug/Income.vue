<template>
  <div class="page-wrap">
    <HeaderBar :currentName="'我的收益'" />
    <div class="content">
      <div class="card">这里展示我的收益统计与记录占位。</div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  background: var(--ex-default-background-color);
}
.content {
  padding: 12px;
}
.card {
  background: var(--ex-passive-background-color);
  color: var(--ex-default-font-color);
  border-radius: 8px;
  padding: 16px;
}
</style>




