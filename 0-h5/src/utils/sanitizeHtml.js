// 通用富文本清洗：白名单标签/属性，移除事件与危险协议
export function sanitizeHtml(dirty) {
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


