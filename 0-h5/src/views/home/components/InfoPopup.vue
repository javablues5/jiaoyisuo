<!-- 站内信弹窗 -->
<script setup>
import { showToast } from 'vant'
import { haveRead } from '@/api/info'
import { _t18 } from '@/utils/public'

const props = defineProps({
  data: {
    type: Array,
    default: []
  },
  show: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['closeBtn'])
const closeBtn = () => {
  emit('closeBtn')
  readMsgs()
}
const readMsgs = () => {
  let str = ''
  props.data.forEach((item, index) => {
    if (item.type == '1') {
      str += item.id + ','
    }
  })
  let params = `ids=${str}`
  if (str.length > 0) {
    haveRead(params).then((res) => {
      if (res.code == '200') {
        showToast(res.msg)
      } else {
        showToast(res.msg)
      }
    })
  }
}

/**
 * 安全清洗富文本：白名单标签/属性，移除事件与危险协议。
 */
const sanitizeHtml = (dirty) => {
  if (!dirty || typeof dirty !== 'string') return ''
  const ALLOWED_TAGS = new Set(['p', 'b', 'i', 'strong', 'em', 'ul', 'ol', 'li', 'br', 'img', 'a', 'span', 'div'])
  const ALLOWED_ATTRS = new Set(['href', 'src', 'alt', 'title', 'target', 'rel'])
  const SAFE_URL = (url) => {
    if (!url || typeof url !== 'string') return ''
    const lowered = url.trim().toLowerCase()
    if (lowered.startsWith('javascript:')) return ''
    if (lowered.startsWith('data:') && !lowered.startsWith('data:image/')) return ''
    if (
      lowered.startsWith('http://') ||
      lowered.startsWith('https://') ||
      lowered.startsWith('mailto:') ||
      lowered.startsWith('tel:') ||
      lowered.startsWith('blob:') ||
      lowered.startsWith('data:image/')
    ) {
      return url
    }
    return ''
  }
  const container = document.createElement('div')
  container.innerHTML = dirty
  const walk = (node) => {
    if (node.nodeType === 1) {
      const tag = node.tagName.toLowerCase()
      if (!ALLOWED_TAGS.has(tag)) {
        node.parentNode && node.parentNode.removeChild(node)
        return
      }
      ;[...node.attributes].forEach((attr) => {
        const name = attr.name.toLowerCase()
        if (name.startsWith('on') || name === 'style') {
          node.removeAttribute(attr.name)
          return
        }
        if (!ALLOWED_ATTRS.has(name)) {
          node.removeAttribute(attr.name)
          return
        }
        if (name === 'href' || name === 'src') {
          const safe = SAFE_URL(attr.value)
          if (!safe) node.removeAttribute(attr.name)
          else node.setAttribute(attr.name, safe)
        }
        if (name === 'target') {
          node.setAttribute('rel', 'noopener noreferrer')
        }
      })
    }
    let child = node.firstChild
    while (child) {
      const next = child.nextSibling
      walk(child)
      child = next
    }
  }
  walk(container)
  return container.innerHTML
}
</script>
<template>
  <div>
    <van-popup v-model:show="props.show" :close-on-click-overlay="false" close round>
      <div class="popup-box">
        <div class="head">
          <div class="title fw-bold">
            <!-- 通知 -->
            {{ _t18('notify') }}
          </div>
          <!-- type 2全站消息 1指定用户消息 -->
        </div>

        <div class="conn">
          <div v-for="item in data" :key="item.id">
            <div class="title fw-bold">{{ item.title }}</div>
            <div class="txt" v-html="sanitizeHtml(item.content)"></div>
          </div>
        </div>
        <div class="btnBox" @click="closeBtn">
          <p class="fw-bold">{{ _t18('btnConfirm') }}</p>
        </div>
      </div>
    </van-popup>
  </div>
</template>
<style lang="scss" scoped>
:deep(.van-popup) {
  background-color: var(--ex-home-notice-bgcolor);
}
.popup-box {
  width: 340px;
  font-size: 14px;
  overflow: hidden;

  .head {
    position: relative;
    .title {
      text-align: center;
      padding: 20px 0;
      font-size: 20px;
    }
    .close {
      position: absolute;
      top: 20px;
      right: 15px;
      .guanbi {
        font-size: 20px;
      }
    }
  }
  .conn {
    padding: 15px 15px 0;
    min-height: 200px;
    height: auto;
    max-height: 50vh;
    overflow: auto;
    border-top: 1px solid var(--ex-border-color6);
    & > div {
      padding-bottom: 30px;
    }
    .title {
      font-size: 16px;
      margin-bottom: 10px;
    }
    .txt {
      word-break: break-all;
      line-height: 1.5;
      font-size: 14px;
    }
  }
  .btnBox {
    padding: 15px 20px;
    background-color: var(--ex-div-bgColor1);
    text-align: center;
    p {
      color: var(--ex-font-color);
      font-size: 16px;
    }
  }
}
</style>
