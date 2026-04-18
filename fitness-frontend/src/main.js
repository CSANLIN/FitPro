import { createApp } from 'vue'

// 第三方库样式
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css' // 暗色模式支持

// 项目全局样式
import '@/styles/index.scss'

// 路由和状态管理
import router from '@/router'
import pinia from '@/stores'

// 主组件
import App from './App.vue'

// 创建应用
const app = createApp(App)

// 注册插件
app.use(router)
app.use(pinia)

// 挂载
app.mount('#app')
