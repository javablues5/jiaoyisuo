<template>
    <div class="follow">
        <HeaderBar currentName="跟单计划" :border_bottom="false"></HeaderBar>
        <van-tabs v-model:active="activeName" title-active-color="#edc96d" color="#edc96d" background="#1f232a" sticky
            title-inactive-color="#5d626d">
            <van-tab v-for="item in tabs" :key="item.value" :title="item.label" :name="item.name">
                <template v-if="activeName === 'a'">
                    <ul class="nav-type">
                        <li v-for="(nav, index) in types" :key="nav.value" class="navItem"
                            :class="{ active: currentIndex === index }" @click="handletype(nav, index)">{{ nav.label }}
                        </li>
                    </ul>
                    <ul class="content">
                        <li class="content-item">
                            <div class="top">
                                <div class="title-box">
                                    <span class="name">量化交易传家</span>
                                    <span class="info">高级策略师｜全品种交易</span>
                                </div>
                                <div class="percentage" style="color:#2fbe85">+32.6%</div>
                            </div>
                            <div class="bottom">
                                <div>
                                    <span class="label">跟随人数：</span>
                                    <span class="value">1238</span>
                                </div>
                                <div>
                                    <span class="label">成功率：</span>
                                    <span class="value">86.7%</span>
                                </div>
                                <div>
                                    <span class="label">最大回撤：</span>
                                    <span class="value">-7.5%</span>
                                </div>
                            </div>
                            <van-button type="primary">立即跟单</van-button>
                        </li>
                    </ul>
                </template>
            </van-tab>
        </van-tabs>
    </div>
</template>

<script setup>
import { name } from 'pubsub-js'

const activeName = ref('a')
const tabs = computed(() => [{ label: '跟单计划', value: 0, name: 'a' }, { label: '交易信号', value: 1, name: "b" }])
const types = computed(() => [{ label: '全部品种', value: 0 }, { label: '高收益率', value: 2 }, { label: '低风险', value: 3 }, { label: '人气最高', value: 4 }])
const currentIndex = ref(0)
const handletype = (item, index) => {
    currentIndex.value = index
}

</script>

<style lang="scss" scoped>
.follow {
    display: flex;
    flex-direction: column;
    height: 100vh;

    :deep(.van-tabs__nav) {
        box-shadow: 0 4px 20px 0 rgba(32, 29, 54, 0.25), 0 1.5px 8px 0 rgba(0, 0, 0, 0.18);
        padding-left: 30px;
    }

    :deep(.van-tabs) {
        box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.12);
    }

    :deep(.van-tab) {
        flex: inherit;
        margin-right: 20px;
    }

    .nav-type {
        display: flex;
        gap: 10px;
        padding: 16px;

        .navItem {
            border: 1px solid #2e2e2e;
            padding: 10px 4px;
            border-right: 8px;
            color: #565f6d;
        }

        .active {
            color: #ebecef;
        }
    }

    .content {
        padding: 0 16px;
        display: flex;
        flex-direction: column;
        gap: 10px;
        color: #ebecef;

        .content-item {
            border: 1px solid #2c323e;
            background-color: #202630;
            padding: 16px 10px;
            border-radius: 8px;
            gap: 10px;
            display: flex;
            flex-direction: column;

            .top {
                display: flex;
                justify-content: space-between;

                .title-box {
                    display: flex;
                    flex-direction: column;
                    gap: 5px;

                    .name {
                        font-size: 18px;
                        font-weight: bold;
                    }

                    .info {
                        color: #565f6d;
                    }
                }

                .percentage {
                    font-size: 16px;
                }

            }

            .bottom {
                display: flex;
                justify-content: space-between;

                .label {
                    color: #565f6d;
                }

            }
        }
    }

}
</style>