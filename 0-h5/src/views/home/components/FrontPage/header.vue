<!-- 首页 -->
<template>
  <div>
    <van-popup v-model:show="show" position="left" class="sidebar" @close="closeSideBar">
      <SideBar @closeSideBar="closeSideBar"></SideBar>
    </van-popup>
    <div class="carousel">
      <!-- <van-swipe :autoplay="3000" lazy-render :loop="true" :show-indicators="false">
        <van-swipe-item v-for="(item, index) in carouselList" :key="index">
          <image-load :filePath="item.imgUrl" alt="" class="carouselItem" @click="linkto(item)" />
        </van-swipe-item>
      </van-swipe> -->
      <div class="top">
        <div><Logo></Logo> <span class="title">{{ _t18('digital_currency_trading') }}</span></div>
        <div class="leftSide">
          <svg-load name="sousuo" class="rightImg" @click="$router.push('/quote')"></svg-load>
          <svg-load
            name="kefu"
            class="rightImg"
            @click="dispatchCustomEvent('event_serviceChange')"
          ></svg-load>
          <svg-load name="message" class="rightImg" @click="$router.push('/message')"></svg-load>
          <svg-load name="user" class="rightImg" @click="openSideBar"></svg-load>
        </div>
      </div>
    </div>
    <div class="listBox">
      <span class="title">{{ _t18('market_overview') }}</span>
      <div class="currentList">
        <div
          class="item centerItem"
          v-for="(item, index) in dataList.filter((it, idx) => {
            return idx < 4
          })"
          :key="index"
          @click="linkTo(item)"
        >
          <!-- <div
          :class="[
            _isRFD(
              tradeStore.allCoinPriceInfo[item.coin]?.openPrice,
              tradeStore.allCoinPriceInfo[item.coin]?.close
            ),
            'rfd-sign itemMain fw-num'
          ]"
        >
          {{ tradeStore.allCoinPriceInfo[item.coin]?.priceChangePercent }}%
        </div> -->
          <div
            :class="[
              _isRFD(
                tradeStore.allCoinPriceInfo[item.coin]?.open,
                tradeStore.allCoinPriceInfo[item.coin]?.close
              ),
              'itemFooter fw-num'
            ]"
          >
            ${{ tradeStore.allCoinPriceInfo[item.coin]?.close }}
          </div>
          <div class="itemBottom fw-num">{{ item.showSymbol }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useTradeStore } from '@/store/trade/index'
import { useMainStore } from '@/store/index.js'
import { useRouter } from 'vue-router'
import { onMounted, computed, ref } from 'vue'
import { publiceNotice } from '@/api/common/index'
import { dispatchCustomEvent } from '@/utils'
import { _t18 } from '@/utils/public'
import SideBar from '@/views/home/sidebar/index.vue'
const show = ref(false)
const openSideBar = () => {
  show.value = true
}
const closeSideBar = () => {
  show.value = false
}
const tradeStore = useTradeStore()
const mainStroe = useMainStore()
const $router = useRouter()

const dataList = computed(() => {
  // let tempFilterKey = Object.keys(tradeStore.allCoinPriceInfo)
  //   .filter((key) => tradeStore.allCoinPriceInfo[key]?.priceChangePercent > 0)
  //   .slice(0, 3)
  let tempData = []
  let tempFilterKey = mainStroe.getHomeCoinList.map((item) => {
    if (item.isOpen == 'true') {
      return item.coin
    }
  })
  // let tempData = tradeStore.secondContractCoinList.filter((elem) =>
  //   tempFilterKey.includes(elem.coin)
  // )
  tempFilterKey.forEach((elem) => {
    tradeStore.secondContractCoinList.forEach((elem2) => {
      if (elem2.coin == elem) {
        tempData.push(elem2)
      }
    })
  })
  return tempData
})

const linkTo = (item) => {
  mainStroe.setTradeStatus(Number(0))
  $router.push(`/trade?symbol=${item.coin}`)
}
const carouselList = ref([
  {
    imgUrl: '../../resource/images/light/banner.jpg'
  }
])
// 轮播图跳转
const linkto = (detail) => {
  if (detail.noticeContent && detail.noticeContent !== '<p><br></p>') {
    $router.push(`/broadcastDetails?id=${detail.noticeId}`)
  } else {
    $router.push(`${detail.detailUrl}`)
  }
}

onMounted(async () => {
  try {
    const res = await publiceNotice('ACTIVITY_NOTICE', 'HOME_ACTIVITY ')
    if (res.code === 200) {
      // carouselList.value = res.data.filter((item) => {
      //   return item.status != '1'
      // })
    }
  } catch (error) {}
})
</script>
<style lang="scss" scoped>
:deep(.sidebar) {
  margin-top: -1px;
  max-width: var(--ex-max-width);
  width: 100%;
  left: auto;
  height: 100%;
}

.carousel {
  // height: 200px;
  // background-color: #f2b80a;

  .carouselItem {
    height: 200px;
    width: 100%;
    object-fit: cover;
  }

  .top {
    height: 52px;
    background: var(--ex-home-box-background-color3);
    border-radius: 5px;
    padding: 15px 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;
    margin: 10px;
    .leftSide{
      display: flex;
      gap: 8px;
    }
    .title {
      font-size: 16px;
    }

    /* 减去两侧的间距 */
    .leftImg {
      width: 25px;
      height: 25px;
    }

    .centerImg {
      width: 56px;
      height: 14px;
      margin-left: 5px;
    }

    .rightImg {
      width: 24px;
      height: 24px;
    }
  }
}

.listBox{
  background-color: #1f232a;
  padding: 10px;
  margin: 10px;
  border: 1px solid #2e2e2e;
  border-radius: 10px;
  .title{
    font-size: 16px;
  }
}

.currentList {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr; /* 每行两列 */
  gap: 10px; /* 可根据需要设置间隔 */
  /* 减去两侧的间距 */
  box-sizing: border-box;
 
  box-shadow: 0px 3px 10px 1px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  display: grid;
 
  grid-template-columns: repeat(2fr, 2);

  .item {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: 1px solid var(--ex-home-box-border-color);
    background-color: var(--ex-home-box-background-color);
    padding: 10px;
    border-radius: 8px;

    .itemBottom {
      font-size: 13px;
      color: var(--ex-default-font-color);
      font-weight: 400;
      margin-top: 4px;
    }

    .itemMain {
      margin-top: 5px;
      display: flex;
      align-items: center;
      font-size: 14px;
      font-weight: 400;

      .itemMainImg {
        width: 24px;
        height: 24px;
        margin-right: 5px;
      }
    }

    .itemFooter {
      display: flex;
      font-size: 18px;
      margin-top: 5px;
    }
    
  }

  .centerItem {
    justify-content: center;
    align-items: center;
  }
}
</style>
