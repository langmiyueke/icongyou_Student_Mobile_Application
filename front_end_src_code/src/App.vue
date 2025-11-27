<template>
  <div id="app">
    <!-- 渲染路由视图 -->
    <router-view />
  </div>
</template>

<script>
import { onMounted } from 'vue'
import router from './router'

export default {
  name: 'App',
  setup () {
    onMounted(() => {
      // 启动后直接跳转到登录页（如果当前不是登录页）
      try {
        const current = router.currentRoute && router.currentRoute.value && router.currentRoute.value.path
        if (current !== '/login') {
          router.replace('/login')
        }
      } catch (e) {
        console.warn('Redirect to login failed', e)
      }
    })

    return {}
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 全局样式调整 */
.el-header {
  padding: 0 20px;
}

.el-main {
  padding: 20px;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

</style>
