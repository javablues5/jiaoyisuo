<template>
    <div class="radar">
        <HeaderBar :currentName="_t18('radar_title')" :border_bottom="false"></HeaderBar>
        <ul class="content">
            <li class="item" v-for="item in uniqueDataList" :key="item.name" @click="linkTo(item)">
                <div class="top">
                    <div class="top-left">
                        <img src="../../assets/defi/wallet.png" alt="" class="bi">
                        <span>{{ item.name }}</span>
                        <span :style="{ color: item.percentage < 0 ? '#972830' : '#3cbd8e' }">{{ item.percentage
                            }}%</span>
                    </div>
                    <span class="btn">{{ _t18('radar_price_fluctuation') }}</span>
                </div>
                <span class="tip">{{ _t18('radar_tip_prefix') }}{{ formatTimeTip(item.time) }}{{ item.percentage > 0 ? _t18('radar_rise') : _t18('radar_fall') }}{{ item.percentage
                }}%</span>
                <span class="time">{{ formatRelativeTime(item.nowTime) }}</span>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { initRadarWebSocket } from '@/plugin/socket'
import PubSub from 'pubsub-js'
import { socketDict } from '@/config/dict'
import dayjs from 'dayjs'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '@/store/index.js'
import { _t18 } from '@/utils/public'
import { getRadarList } from '@/api/radar'
const mainStroe = useMainStore()
const dataList = ref([])

// 首次加载交易雷达列表
const initRadarList = async () => {
    try {
        const res = await getRadarList({
            pageNum: 1,
            pageSize: 50,
            orderByColumn: 'nowTime',
            isAsc: 'desc'
        })
        const list = res?.rows 
        if (Array.isArray(list) && list.length) {
            dataList.value.push(...list)
        }
    } catch (e) {
        // 忽略错误，页面继续依赖 websocket 数据
    }
}
/**
 * 用户信息改变监听回调事件
 */
const event_userInfoChange = (e) => {
    // console.log('🔄 收到用户信息变化事件:', e.detail?.user)
    initRadarWebSocket(e.detail?.user.userId)
}

const router = useRouter()
const linkTo = (item) => {
    const symbol = item.name.split('/')[0].toLowerCase()
    mainStroe.setTradeStatus(Number(0))
    router.push(`/trade?symbol=${symbol}`)
}



/**
 * 根据time字符串返回友好的时间描述
 * @param {string} time - 如"1m", "1s", "1h"
 * @returns {string}
 */
function formatTimeTip(time) {
    if (!time || typeof time !== 'string') return ''
    const unitMap = {
        's': _t18('radar_seconds_ago'),
        'm': _t18('radar_minutes_ago'),
        'h': _t18('radar_hours_ago')
    }
    const match = time.match(/^(\d+)([smh])$/)
    if (!match) return ''
    let num = match[1]
    const unit = match[2]
    // 避免num为undefined/null/非数字字符串时转NaN
    if (!/^\d+$/.test(num)) return ''
    num = Number(num)
    // 特殊处理：避免 NaN
    if (isNaN(num)) return ''
    return `${num}${unitMap[unit] || ''}`
}


/**
 * 根据时间戳返回“刚刚”或“x分钟前”
 * @param {number|string|Date} timestamp
 * @returns {string}
 */
function formatRelativeTime(timestamp) {
    const now = dayjs()
    const t = dayjs(timestamp)
    const diffMinute = now.diff(t, 'minute')
    if (diffMinute < 1) {
        return _t18('radar_just_now')
    }
    return `${diffMinute}${_t18('radar_minutes_ago_relative')}`
}


/**
 * 计算属性：根据 dataList.value 的 name 字段去重，返回最新的去重数组
 */
const uniqueDataList = computed(() => {
    const map = new Map()
    // 逆序保证最新数据保留在去重后的数组中
    for (let i = dataList.value.length - 1; i >= 0; i--) {
        const item = dataList.value[i]
        if (!map.has(item.name)) {
            map.set(item.name, item)
        }
    }
    // 保证原顺序为最新的数据在前
    return Array.from(map.values()).reverse()
})

/**
 * 雷达订阅Key
 */
const radarKey = ref('')
/**
 * 雷达通知
 */
const onRadarMessage = () => {
    radarKey.value = PubSub.subscribe(socketDict.LADAR, (key, data) => {
        dataList.value.push(data)
    })
}
onRadarMessage()

onMounted(() => {
    document.addEventListener('event_userInfoChange', event_userInfoChange)
    // 首次加载交易雷达列表
    initRadarList()
})

onUnmounted(() => {
    document.removeEventListener('event_userInfoChange', event_userInfoChange)
    radarKey.value && PubSub.unsubscribe(radarKey.value)
})


</script>

<style lang="scss" scoped>
.radar {
    display: flex;
    flex-direction: column;
    height: 100vh;

    .content {
        flex: 1;
        overflow-y: auto;
        padding: 0 16px 16px 16px;
        gap: 12px;
        display: flex;
        flex-direction: column;
        scrollbar-width: thin;
        scrollbar-color: #141414;

        &::-webkit-scrollbar {
            width: 3px;
            background: #1a1a1a;
        }

        &::-webkit-scrollbar-thumb {
            background: #141414;
            border-radius: 3px;
        }

        .item {
            display: flex;
            flex-direction: column;
            background-color: #141414;
            border-radius: 8px;
            padding: 16px;
            gap: 10px;

            .top {
                display: flex;
                justify-content: space-between;

                .bi {
                    height: 30px;
                    margin-right: 10px;
                }

                .btn {
                    display: inline-block;
                    color: #fff;
                    background-color: #2970ff;
                    border-radius: 8px;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    padding: 0 6px;
                    height: 24px;
                }

                .top-left {
                    display: flex;
                    gap: 8px;
                    align-items: center;
                    font-size: 14px;
                }
            }

            .time {
                color: #4f4b43
            }

        }

    }
}
</style>