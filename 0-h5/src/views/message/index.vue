<template>
    <div class="message">
        <HeaderBar :currentName="_t18('message')" :border_bottom="false"></HeaderBar>
        <van-tabs v-model:active="activeName" title-active-color="#edc96d" color="#edc96d"
            title-inactive-color="#5d626d">
            <van-tab v-for="item in tabs" :key="item.value" :title="item.label" :name="item.name">
                <div class="message-content">
                    <van-pull-refresh v-model="refreshing" :key="item.name" @refresh="onRefresh"
                        :loading-text="_t18('loading')" :loosing-text="_t18('release_refresh')">
                        <van-loading v-if="showLoading" />
                        <template v-else>
                            <van-list v-if="messageList.length > 0" v-model:loading="loading" :finished="finished"
                                :finished-text="_t18('no_more_data')" :loading-text="_t18('loading')" @load="onLoad">
                                <div class="item" v-for="item in messageList" :key="item.id">
                                    <div class="top">
                                        <div class="left">
                                            <span class="title">{{ item.title }}</span>
                                            <span class="date">{{ item.createTime }}</span>
                                        </div>
                                    </div>
                                    <div class="bottom" @click="toggleExpand(item)">
                                        <span>{{ _t18('view_details') }}</span>
                                        <van-icon :name="item.expanded ? 'arrow-up' : 'arrow-down'" />
                                    </div>
                                    <div class="detail" v-show="item.expanded">
                                        <div v-html="sanitizeHtml(item.content?.replace(/\n/g, '<br>'))"></div>
                                    </div>
                                    <div class="badge" v-if="item.status === 0"></div>
                                </div>
                            </van-list>
                            <div class="empty-data" v-else>
                                <van-empty description="暂无数据" />
                            </div>
                        </template>
                    </van-pull-refresh>
                </div>
            </van-tab>
        </van-tabs>

    </div>
</template>
<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import { getMessages, haveRead } from '@/api/info.js'
import { onMounted, computed, ref, watch } from 'vue'
import { useUserStore } from '@/store/user/index'
import { _t18 } from '@/utils/public'
import { sanitizeHtml } from '@/utils/sanitizeHtml'

const tabs = computed(() => [ { label: _t18('all'), value: 1, name: "b" },{ label: _t18('unread'), value: 0, name: 'a' },])
const userStore = useUserStore()
const tokenStatus = ref(userStore.isSign)
const activeName = ref('b')
// 分页与加载状态
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const showLoading = ref(true)

const messageList = ref([])

const readMsgs = (item) => {
    if (item.id) {
        let params = `ids=${item.id}`
        haveRead(params).then((res) => {
            if (res.code == 200) {
                item.status = 1
            }
        })
    }
}
async function toggleExpand(item) {
    item.expanded = !item.expanded;
    if (item.status === 0) {
        readMsgs(item)
    }
}

const resetAndFetch = () => {
    // 重置分页状态并加载
    pageNum.value = 1
    total.value = 0
    finished.value = false
    loading.value = true
    showLoading.value = true
    messageList.value = []
    getInfoList()
}

const getInfoList = async () => {
    if (!tokenStatus.value) return
    const res = await getMessages({ pageNum: pageNum.value, pageSize: pageSize.value, orderByColumn: "createTime", status: activeName.value === 'a' ? 0 : null })
    if (res.code == '200') {
        // 结束下拉与加载动画
        if (showLoading.value) showLoading.value = false
        if (refreshing.value) refreshing.value = false
        loading.value = false

        const rows = Array.isArray(res.rows) ? res.rows : []
        total.value = Number(res.total || 0)
        messageList.value = messageList.value.concat(rows)

        if (messageList.value.length >= total.value || rows.length < pageSize.value) {
            finished.value = true
        }
        pageNum.value++
    } else {
        // 异常兜底
        finished.value = true
        loading.value = false
        showLoading.value = false
        refreshing.value = false
    }
}

const onLoad = () => {
    if (finished.value || loading.value) return
    loading.value = true
    getInfoList()
}

const onRefresh = () => {
    refreshing.value = true
    resetAndFetch()
}

// Tab 切换重置
watch(activeName, () => {
    resetAndFetch()
})

onMounted(() => resetAndFetch())

</script>

<style lang="scss" scoped>
.message {
    height: 100vh;
    --van-tabs-nav-background: var(--ex-home-list-bgcolor);
    display: flex;
    flex-direction: column;

    :deep(.van-tabs__nav) {
        background: #1f232a;
        box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.12);
        /* 添加阴影效果 */
    }

    :deep(.van-tabs) {
        box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.12);
        /* 添加阴影效果 */
    }

    .message-content {
        display: flex;
        flex-direction: column;
        padding: 10px;
        gap: 10px;
        flex: 1;
        padding-bottom: 60px;
        overflow-y: auto;
        height: 100%;

        .item {
            background-color: var(--ex-home-list-bgcolor);
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            position: relative;
            margin-bottom: 10px;

            .badge {
                position: absolute;
                top: 10px;
                right: 10px;
                height: 7px;
                width: 7px;
                border-radius: 50%;
                background-color: #ee0a24;
            }

            .top {
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #443f3f;
                padding: 12px;

                .left {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;

                    .title {
                        color: #fff;
                    }

                    .date {
                        color: #999999;
                    }
                }
            }

            .bottom {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 12px;
                color: #999999;
            }

            .detail {
                padding: 0 12px 12px;
                color: #fff;
            }
        }
    }
}
</style>