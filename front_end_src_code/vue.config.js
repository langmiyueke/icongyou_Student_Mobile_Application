const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
})

// Vue项目vue.config.js
module.exports = {
  devServer: {
    port: 8080,        // 前端默认端口
    proxy: {
      '/api': {
        target: 'http://localhost:3000',  // 后端端口
        changeOrigin: true
      }
    }
  }
}