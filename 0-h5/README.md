# H5 项目说明文档

## 全站公告功能说明

### 主要文件位置

#### 1. 首页滚动公告栏
- **文件路径**: `src/views/home/components/FrontPage/menu.vue`
- **功能**: 在首页显示滚动公告栏
- **实现方式**: 使用 `van-notice-bar` 组件展示公告内容
- **接口调用**: `noticeList('ROLL_NOTICE')`
- **关键代码位置**: 第 15-19 行（模板），第 136-143 行（逻辑）

#### 2. 公告 API 接口
- **文件路径**: `src/api/common/index.js`
- **接口说明**:
  - `noticeList(key)` - 单参数公告列表接口
    - 参数说明：
      - `INFORMATION_NOTICE` - 公告信息
      - `ACTIVITY_NOTICE` - 活动公告
      - `ROLL_NOTICE` - 首页滚动公告
  - `publiceNotice(key, modelKey)` - 双参数公告列表接口
  - `noticeDetail(noticeId)` - 公告详情接口
- **关键代码位置**: 第 65-79 行

#### 3. 公告详情页
- **文件路径**: `src/views/home/BroadcastDetails.vue`
- **功能**: 显示公告的详细内容
- **路由**: `/broadcastDetails?id={noticeId}`

#### 4. 站内信弹窗（全站消息）
- **文件路径**: `src/views/home/components/InfoPopup.vue`
- **功能**: 显示全站消息弹窗
- **消息类型**:
  - `type=2` - 全站消息
  - `type=1` - 指定用户消息

#### 5. 首页 Header 中的活动公告
- **文件路径**: `src/views/home/components/FrontPage/header.vue`
- **功能**: 首页轮播图区域的活动公告
- **接口调用**: `publiceNotice('ACTIVITY_NOTICE', 'HOME_ACTIVITY ')`

### 公告类型 Key 说明

常用的公告 key 类型：
- `ROLL_NOTICE` - 首页滚动公告
- `ACTIVITY_NOTICE` - 活动公告
- `INFORMATION_NOTICE` - 公告信息
- `POP_UPS_NOTICE` - 弹窗公告
- `REGISTER_WELFARE` - 注册福利公告
- `DEFI_POP_UPS_NOTICE` - DeFi 弹窗公告
- `HOME_ACTIVITY` - 首页活动公告
- `FINANCE_ACTIVITY` - 理财活动公告

### 使用示例

#### 获取首页滚动公告
```javascript
import { noticeList } from '@/api/common/index'

const res = await noticeList('ROLL_NOTICE')
if (res.code === 200 && res.data.length > 0 && res.data[0].status == '0') {
  currentNotice.value = res.data[0].noticeContent
}
```

#### 获取活动公告
```javascript
import { publiceNotice } from '@/api/common/index'

const res = await publiceNotice('ACTIVITY_NOTICE', 'HOME_ACTIVITY ')
if (res.code === 200) {
  // 处理公告数据
}
```

#### 获取公告详情
```javascript
import { noticeDetail } from '@/api/common/index'

const res = await noticeDetail(noticeId)
if (res.code === 200) {
  currentName.value = res.data.noticeTitle
  currentHtml.value = res.data.noticeContent
}
```

### 注意事项

1. 公告状态判断：`status == '0'` 表示启用状态
2. 公告内容需要使用 `sanitizeHtml` 进行 HTML 安全处理
3. 弹窗公告需要检查数据是否存在再显示
4. 全站消息通过事件监听 `event_userInfoChange` 触发显示

