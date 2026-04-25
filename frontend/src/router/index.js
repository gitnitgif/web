/**
 * Vue Router 路由配置文件
 * 用途：定义应用的所有路由规则和导航守卫
 * 关联：
 * - 被 main.js 引入并注册
 * - 控制页面跳转和权限验证
 * - 所有视图组件通过路由懒加载
 */
import { createRouter, createWebHistory } from 'vue-router'

// 路由配置数组
const routes = [
  {
    path: '/',              // 根路径，直接访问首页
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/home',          // 首页（福建省地图）
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/profile',       // 个人信息页面
    name: 'Profile',
    component: () => import('@/views/Profile.vue')
  },
  {
    path: '/admin',         // 管理员后台页面
    name: 'Admin',
    component: () => import('@/views/Admin.vue')
  },
  {
    path: '/city/:cityCode',  // 城市详情页（动态路由）
    name: 'CityDetail',
    component: () => import('@/views/CityDetail.vue')
  },
  {
    path: '/learning',      // 方言学习页面
    name: 'Learning',
    component: () => import('@/views/Learning.vue')
  },
  {
    path: '/folk',          // 方言资源页面（歌曲、童谣等）
    name: 'FolkResource',
    component: () => import('@/views/FolkResource.vue')
  },
  {
    path: '/community',     // 交流互动页面（评论）
    name: 'Community',
    component: () => import('@/views/Community.vue')
  },
  {
    path: '/audio-upload',  // 音频上传页面
    name: 'AudioUpload',
    component: () => import('@/views/AudioUpload.vue')
  },
  {
    path: '/audio-gallery', // 音频展示页面
    name: 'AudioGallery',
    component: () => import('@/views/AudioGallery.vue')
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),  // 使用 HTML5 History 模式
  routes
})

/**
 * 全局前置导航守卫
 * 用途：权限控制，根据用户角色进行路由拦截
 * @param {Object} to - 目标路由
 * @param {Object} from - 来源路由
 * @param {Function} next - 继续执行的回调
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')  // 获取登录 token
  const role = localStorage.getItem('role') || 'USER'  // 获取用户角色
  
  // 访问管理员页面
  if (to.path === '/admin') {
    if (!token) {
      next('/home')   // 未登录，跳转到首页
    } else if (role !== 'ADMIN') {
      next('/home')   // 非管理员，跳转到首页
    } else {
      next()          // 管理员，允许访问
    }
  } 
  // 访问注册页面
  else if (to.path === '/register') {
    if (token) {
      // 已登录用户根据角色跳转
      if (role === 'ADMIN') {
        next('/admin')
      } else {
        next('/home')
      }
    } else {
      next()  // 未登录，允许访问注册页
    }
  } 
  else {
    next()  // 其他路由，直接访问
  }
})

export default router
