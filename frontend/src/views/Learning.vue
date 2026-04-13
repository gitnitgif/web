<template>
  <div class="learning-container">
    <div class="learning-header">
      <h1>福建方言保护平台</h1>
      <div class="user-menu">
        <button @click="goBack" class="back-btn">返回主页</button>
      </div>
    </div>

    <div class="learning-content">
      <!-- 分类筛选 -->
      <div class="filter-section">
        <div class="filter-group">
          <label>分类：</label>
          <button 
            :class="['filter-btn', { active: currentCategory === 'all' }]"
            @click="filterByCategory('all')"
          >
            全部
          </button>
          <button 
            :class="['filter-btn', { active: currentCategory === '词汇' }]"
            @click="filterByCategory('词汇')"
          >
            词汇
          </button>
          <button 
            :class="['filter-btn', { active: currentCategory === '句子' }]"
            @click="filterByCategory('句子')"
          >
            句子
          </button>
          <button 
            :class="['filter-btn', { active: currentCategory === '发音' }]"
            @click="filterByCategory('发音')"
          >
            发音
          </button>
          <button 
            :class="['filter-btn', { active: currentCategory === '歌曲' }]"
            @click="filterByCategory('歌曲')"
          >
            歌曲
          </button>
          <button 
            :class="['filter-btn', { active: currentCategory === '故事' }]"
            @click="filterByCategory('故事')"
          >
            故事
          </button>
        </div>

        <div class="filter-group">
          <label>难度：</label>
          <button 
            :class="['filter-btn', { active: currentDifficulty === 'all' }]"
            @click="filterByDifficulty('all')"
          >
            全部
          </button>
          <button 
            :class="['filter-btn', { active: currentDifficulty === '初级' }]"
            @click="filterByDifficulty('初级')"
          >
            初级
          </button>
          <button 
            :class="['filter-btn', { active: currentDifficulty === '中级' }]"
            @click="filterByDifficulty('中级')"
          >
            中级
          </button>
          <button 
            :class="['filter-btn', { active: currentDifficulty === '高级' }]"
            @click="filterByDifficulty('高级')"
          >
            高级
          </button>
        </div>
      </div>

      <!-- 资源列表 -->
      <div class="resource-list">
        <div v-for="resource in filteredResources" :key="resource.id" class="resource-card">
          <div class="resource-header">
            <span class="resource-category">{{ resource.category }}</span>
            <span class="resource-difficulty">{{ resource.difficulty }}</span>
          </div>
          <h3 class="resource-title">{{ resource.title }}</h3>
          <p class="resource-content">{{ resource.content }}</p>
          
          <div class="resource-footer">
            <span class="resource-city">📍 {{ getCityName(resource.cityCode) }}</span>
            <span class="resource-views">👁 {{ resource.viewCount }} 次查看</span>
          </div>
          
          <div class="resource-actions">
            <button @click="playAudio(resource)" class="action-btn" v-if="resource.audioUrl">
              🔊 播放音频
            </button>
          </div>
        </div>

        <div v-if="filteredResources.length === 0" class="empty-state">
          <p>暂无学习资源</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAllResources } from '@/api/dialect'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username || '用户')

const resources = ref([])
const currentCategory = ref('all')
const currentDifficulty = ref('all')

const cityMap = {
  fuzhou: '福州',
  xiamen: '厦门',
  quanzhou: '泉州',
  zhangzhou: '漳州',
  putian: '莆田',
  longyan: '龙岩',
  sanming: '三明',
  nanping: '南平',
  ningde: '宁德'
}

const getCityName = (cityCode) => {
  return cityMap[cityCode] || '未知'
}

const filteredResources = computed(() => {
  return resources.value.filter(resource => {
    const categoryMatch = currentCategory.value === 'all' || resource.category === currentCategory.value
    const difficultyMatch = currentDifficulty.value === 'all' || resource.difficulty === currentDifficulty.value
    return categoryMatch && difficultyMatch
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

const filterByCategory = (category) => {
  currentCategory.value = category
}

const filterByDifficulty = (difficulty) => {
  currentDifficulty.value = difficulty
}

const playAudio = (resource) => {
  // TODO: 实现音频播放
  alert('播放音频：' + resource.title)
}

const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.learning-container {
  min-height: 100vh;
  background: url('/background1.png') no-repeat center center;
  background-size: cover;
}

.learning-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.learning-header h1 {
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

.learning-content {
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
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
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

.resource-category {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.resource-difficulty {
  background: #f5f5f5;
  color: #666;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.resource-title {
  color: #333;
  font-size: 18px;
  margin: 0 0 12px 0;
  font-weight: 600;
}

.resource-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 20px;
  max-height: 100px;
  overflow-y: auto;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
  margin-bottom: 15px;
  font-size: 13px;
  color: #999;
}

.resource-city {
  display: flex;
  align-items: center;
  gap: 5px;
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
