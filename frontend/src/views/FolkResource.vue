<template>
  <div class="folk-container">
    <div class="folk-header">
      <h1>福建方言保护平台</h1>
      <div class="user-menu">
        <template v-if="isLoggedIn">
          <span>欢迎用户 {{ username }}</span>
          <button @click="goBack" class="back-btn">返回主页</button>
          <button @click="handleLogout" class="logout-btn">退出登录</button>
        </template>
        <template v-else>
          <button @click="goToLogin" class="login-btn">登录</button>
          <button @click="goToRegister" class="register-btn">注册</button>
        </template>
      </div>
    </div>

    <div class="folk-content">
      <!-- 分类筛选 -->
      <div class="filter-section">
        <div class="filter-group">
          <label>类型：</label>
          <button 
            :class="['filter-btn', { active: currentType === 'all' }]"
            @click="filterByType('all')"
          >
            全部
          </button>
          <button 
            :class="['filter-btn', { active: currentType === '歌曲' }]"
            @click="filterByType('歌曲')"
          >
            歌曲
          </button>
          <button 
            :class="['filter-btn', { active: currentType === '童谣' }]"
            @click="filterByType('童谣')"
          >
            童谣
          </button>
          <button 
            :class="['filter-btn', { active: currentType === '故事' }]"
            @click="filterByType('故事')"
          >
            故事
          </button>
          <button 
            :class="['filter-btn', { active: currentType === '谚语' }]"
            @click="filterByType('谚语')"
          >
            谚语
          </button>
        </div>

        <div class="filter-group">
          <label>城市：</label>
          <button 
            :class="['filter-btn', { active: currentCity === 'all' }]"
            @click="filterByCity('all')"
          >
            全部
          </button>
          <button 
            v-for="city in cities" 
            :key="city.code"
            :class="['filter-btn', { active: currentCity === city.code }]"
            @click="filterByCity(city.code)"
          >
            {{ city.name }}
          </button>
        </div>
      </div>

      <!-- 资源列表 -->
      <div class="resource-list">
        <div v-for="resource in filteredResources" :key="resource.id" class="resource-card">
          <div class="resource-header">
            <span class="resource-type">{{ resource.type }}</span>
            <span class="resource-city">{{ getCityName(resource.cityCode) }}</span>
          </div>
          
          <h3 class="resource-title">{{ resource.title }}</h3>
          
          <div v-if="resource.author" class="resource-author">
            {{ resource.author }}
          </div>
          
          <p class="resource-description">{{ resource.description }}</p>
          
          <div class="resource-content-preview">
            {{ getPreview(resource.content) }}
          </div>
          
          <div class="resource-footer">
            <span class="resource-views">{{ resource.viewCount }} 次查看</span>
          </div>
          
          <div class="resource-actions">
            <button @click="playAudio(resource)" class="action-btn" v-if="resource.audioUrl">
              播放
            </button>
            <button @click="viewDetail(resource)" class="action-btn primary">
              查看详情
            </button>
          </div>
        </div>

        <div v-if="filteredResources.length === 0" class="empty-state">
          <p>暂无资源</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAllResources } from '@/api/folk'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username || '用户')

const resources = ref([])
const currentType = ref('all')
const currentCity = ref('all')

const cities = [
  { code: 'fuzhou', name: '福州' },
  { code: 'xiamen', name: '厦门' },
  { code: 'quanzhou', name: '泉州' },
  { code: 'zhangzhou', name: '漳州' },
  { code: 'putian', name: '莆田' },
  { code: 'longyan', name: '龙岩' },
  { code: 'sanming', name: '三明' },
  { code: 'nanping', name: '南平' },
  { code: 'ningde', name: '宁德' }
]

const getCityName = (cityCode) => {
  const city = cities.find(c => c.code === cityCode)
  return city ? city.name : '未知'
}

const getPreview = (content) => {
  if (!content) return ''
  const lines = content.split('\n')
  return lines.slice(0, 3).join('\n') + (lines.length > 3 ? '...' : '')
}

const filteredResources = computed(() => {
  return resources.value.filter(resource => {
    const typeMatch = currentType.value === 'all' || resource.type === currentType.value
    const cityMatch = currentCity.value === 'all' || resource.cityCode === currentCity.value
    return typeMatch && cityMatch
  })
})

onMounted(async () => {
  await loadResources()
})

const loadResources = async () => {
  try {
    const result = await getAllResources()
    if (result.code === 200) {
      resources.value = result.data || []
    }
  } catch (error) {
    console.error('加载资源失败:', error)
  }
}

const filterByType = (type) => {
  currentType.value = type
}

const filterByCity = (city) => {
  currentCity.value = city
}

const playAudio = (resource) => {
  alert('播放音频：' + resource.title)
}

const viewDetail = (resource) => {
  router.push(`/folk/${resource.id}`)
}

const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.folk-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.folk-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.folk-header h1 {
  color: white;
  font-size: 24px;
  margin: 0;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
}

.user-menu span {
  font-size: 14px;
}

.back-btn,
.login-btn,
.register-btn,
.logout-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 5px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.back-btn {
  background: white;
  color: #667eea;
}

.back-btn:hover {
  transform: translateY(-2px);
}

.login-btn {
  background: white;
  color: #667eea;
}

.login-btn:hover {
  transform: translateY(-2px);
}

.register-btn {
  background: #28a745;
  color: white;
}

.register-btn:hover {
  transform: translateY(-2px);
  background: #218838;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.logout-btn:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.3);
}

.folk-content {
  padding: 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.filter-section {
  background: white;
  padding: 25px 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.filter-group:last-child {
  margin-bottom: 0;
}

.filter-group label {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.filter-btn {
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  background: #e0e0e0;
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

.resource-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 25px;
}

.resource-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.resource-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.resource-type {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.resource-city {
  font-size: 13px;
  color: #999;
}

.resource-title {
  color: #333;
  font-size: 20px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.resource-author {
  color: #667eea;
  font-size: 14px;
  margin-bottom: 10px;
  font-weight: 500;
}

.resource-description {
  color: #666;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 15px;
}

.resource-content-preview {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
  background: #f9f9f9;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 15px;
  white-space: pre-line;
  max-height: 120px;
  overflow-y: auto;
}

.resource-footer {
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
  margin-bottom: 15px;
  font-size: 13px;
  color: #999;
}

.resource-views {
  display: flex;
  align-items: center;
  gap: 5px;
}

.resource-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
  padding: 10px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e0e0e0;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.action-btn.primary:hover {
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.4);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  color: #999;
  font-size: 16px;
}
</style>
