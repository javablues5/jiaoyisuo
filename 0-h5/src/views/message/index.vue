<template>
    <div class="message">
        <HeaderBar currentName="信息" :border_bottom="false"></HeaderBar>
        <van-tabs v-model:active="active" title-active-color="#edc96d" color="#edc96d" title-inactive-color="#5d626d">
            <van-tab v-for="item in tabs" :key="item.value" :title="item.label" dot>

            </van-tab>
        </van-tabs>
        <div class="message-content">
            <div class="item" v-for="item in messageList" :key="item.id">
                <div class="top">
                    <div class="left">
                        <span class="title">{{ item.title }}</span>
                        <!-- <span class="date">{{ currentType === 3 ? item.publish_time : item.create_time
                                }}</span> -->
                    </div>
                </div>
                <div class="bottom" @click="toggleExpand(item)">
                    <span>查看详情</span>
                    <van-icon :name="item.expanded ? 'arrow-up' : 'arrow-down'" />
                </div>
                <div class="detail" v-show="item.expanded">
                    <div v-html="item.content?.replace(/\n/g, '<br>')"></div>
                </div>
                <div class="badge" v-if="item.is_read === '0'"></div>
            </div>
        </div>
    </div>
</template>
<script setup>
import HeaderBar from '@/components/HeaderBar/index.vue'
import { getInfo } from '@/api/info.js'
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user/index'
const tabs = [{ label: "未读", value: 0 }, { label: "全部", value: 1 }]
const userStore = useUserStore()
const tokenStatus = ref(userStore.isSign)
const messageList = ref([])
async function toggleExpand(item) {
    item.expanded = !item.expanded;
    if (item.is_read === '0') {
        await upDateReadMsg({ id: item.id });
        item.is_read = '1';
        fechUnreadMsgCount();
    }
}

const getInfoList = async () => {
    if (tokenStatus.value) {
        let res = await getInfo()
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
    --van-tabs-nav-background: #333;
    .message-content {
        display: flex;
        flex-direction: column;
        padding: 10px;
        gap: 10px;
        flex: 1;
        padding-bottom: 60px;
        overflow-y: auto;

        .item {
            background-color: #fff;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            margin-bottom: 10px;
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
                border-bottom: 1px solid #e4e4e4;
                padding: 12px;

                .left {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;

                    .title {
                        color: #000;
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
            }
        }
    }
}
</style>