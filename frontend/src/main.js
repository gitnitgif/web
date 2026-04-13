/**
 * Vue 3 应用入口文件
 * 用途：创建并配置 Vue 应用实例
 * 关联：
 * - 引入 App.vue 根组件
 * - 引入 Pinia 状态管理
 * - 引入 Vue Router 路由
 * - 引入全局样式
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import {gsap} from 'gsap'
import App from './App.vue'
import router from './router'
import './assets/main.css'

// 创建 Vue 应用实例
const app = createApp(App)

// 创建 Pinia 状态管理实例
const pinia = createPinia()

// 使用插件
app.use(pinia)      // 注册状态管理
app.use(router)     // 注册路由
app.use(gsap)       // 注册 gsap 插件

// 挂载到 #app 元素
app.mount('#app')
