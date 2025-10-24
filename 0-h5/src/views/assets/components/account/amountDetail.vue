<template>
  <div class="assetDetails">
    <div class="title">
        <!-- 资产明细 -->
        <div class="left fw-bold">{{ _t18('asset_detail', ['ebc']) }}</div>
      </div>
    <div v-for="(item, index) in assetDetails" :key="index"  class="item">
      <div class="details">
        <div class="left">
          <svg-load v-if="item.title == 'USDT'" :name="item.icon" class="currencyIcon"></svg-load>
          <image-load :filePath="item.loge" v-else />
          <p class="fw-num">{{ item.title }}</p>
        </div>
        <div class="right">
          <div v-if="false">
            <!-- 可用 -->
            <p class="til">{{ _t18('asset_available') }}</p>
            <p class="num fw-num">
              {{ amountFormat(item.keyong) }}
            </p>
          </div>
          <div v-if="false">
            <!-- 占用 冻结-->
            <p class="til" v-if="['rxce'].includes(_getConfig('_APP_ENV'))">
              {{ _t18('asset_cold') }}
            </p>
            <p class="til" v-else>{{ _t18('asset_occupation', ['bitmake', 'ebc']) }}</p>
            <p class="num fw-num">
              {{ amountFormat(item.zhanyong, 4) }}
            </p>
          </div>

          <div>
            <!-- 折合 -->
            <!-- <p class="til">{{ _t18('asset_equivalent', ['bitmake', 'ebc']) }}(USDT)</p> -->
            <p class="num fw-num">
              ${{ amountFormat(item.zhehe) }}
            </p>
            <span class="keyong">可交易 {{ amountFormat(item.keyong) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { _hide, _t18, _numberWithCommas } from '@/utils/public.js'
import { priceFormat } from '@/utils/decimal.js'
const props = defineProps({
  assetDetails: {
    type: Object
  },
  showNum: {
    type: Boolean
  }
})

/**
 * 金额格式化
 * @param {*} amount
 * @param {*} hide
 * @param {*} decimal
 */
const amountFormat = (amount, decimal = 0) => {
  let tempVal = priceFormat(amount, decimal)
  if (props.showNum) {
    tempVal = _numberWithCommas(tempVal)
  } else {
    tempVal = _hide(tempVal)
  }
  return tempVal
}
</script>

<style lang="scss" scoped>
.assetDetails {
  padding: 0 16px 100px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  .title {
      display: flex;
      justify-content: space-between;
      .left {
        color: var(--ex-font-color6);
        font-size: 16px;
      }
      .yanjin {
        font-size: 16px;
      }
    }
  .item{
     border: 1px solid #342f4f;
     padding: 16px 16px 20px 16px;
     border-radius: 8px;

    .details {
      display: flex;
      .left {
        display: flex;
        align-items: center;

        img {
          border-radius: 100%;
          width: 20px;
          height: 20px;
          margin-right: 5px;
          border: 6px solid #374454;
        }
        .currencyIcon {
          font-size: 20px;
          margin-right: 5px;
        }
        P {
          font-size: 14px;
          color: var(--ex-font-color6);
        }
      }
      .right {
        display: flex;
        flex: 1;
        & > div {
          flex: 1;
          text-align: center;
          font-size: 16px;
          .til {
            padding: 20px 0;
            color: var(--ex-passive-font-color);
          }
          .num {
            color: var(--ex-font-color6);
            font-size: 14px;
          }
          .keyong{
            color: #a1a8ab;
            font-size: 12px;
          }
          &:first-child {
            text-align: start;
          }
          &:last-child {
            text-align: end;
          }
        }
      }
    }
  }
}
</style>
