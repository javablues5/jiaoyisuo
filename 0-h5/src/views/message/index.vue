<template>
    <div class="message">
        <HeaderBar currentName="信息" :border_bottom="false"></HeaderBar>
        <van-tabs v-model:active="active" title-active-color="#edc96d" color="#edc96d" title-inactive-color="#5d626d">
            <van-tab v-for="item in tabs" :key="item.value" :title="item.label">
            </van-tab>
        </van-tabs>
        <div class="message-content">
            <div class="item" v-for="item in computedList" :key="item.id">
                <div class="top">
                    <div class="left">
                        <span class="title">{{ item.title }}</span>
                        <span class="date">{{ item.createTime
                            }}</span>
                    </div>
                </div>
                <div class="bottom" @click="toggleExpand(item)">
                    <span>查看详情</span>
                    <van-icon :name="item.expanded ? 'arrow-up' : 'arrow-down'" />
                </div>
                <div class="detail" v-show="item.expanded">
                    <div v-html="item.content?.replace(/\n/g, '<br>')"></div>
                </div>
                <div class="badge" v-if="item.status === 0"></div>
            </div>
        </div>
    </div>
</template>
<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import { getMessages, haveRead } from '@/api/info.js'
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user/index'
import { computed } from 'vue'
import { showToast } from 'vant'
const tabs = [{ label: "未读", value: 0 }, { label: "全部", value: 1 }]
const userStore = useUserStore()
const tokenStatus = ref(userStore.isSign)
const active = ref(0)

const messageList = ref([])
const computedList = computed(() => {
    if (active.value === 0) {
        return messageList.value.filter((v) => v.status === 0)
    } else {
        return messageList.value
    }

})

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

const getInfoList = async () => {
    if (tokenStatus.value) {
        let res = await getMessages()
        if (res.code == '200' && res.rows.length > 0) {
            messageList.value = res.rows
        }
    }
}


onMounted(() => getInfoList())

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