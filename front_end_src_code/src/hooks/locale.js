import { ref } from 'vue'

// 可写且持久化的 locale 钩子：读取 localStorage（若存在），否则默认 zh-CN
const STORAGE_KEY = 'app_locale'
const initial = (typeof window !== 'undefined' && localStorage.getItem(STORAGE_KEY)) || 'zh-CN'
const currentLocale = ref(initial)

export default function useLocale() {
  const setLocale = (l) => {
    if (!l) return
    currentLocale.value = l
    try {
      localStorage.setItem(STORAGE_KEY, l)
    } catch (e) {
      console.warn('Failed to persist locale', e)
    }
  }

  return { currentLocale, setLocale }
}
