<template>
  <div class="audio-gallery-container">
    <div class="gallery-header">
      <h1>方言音频库</h1>
      <p>聆听各地乡音，感受方言魅力</p>
      <button class="upload-btn" @click="goToUpload">
        <span>上传音频</span>
      </button>
    </div>

    <div class="gallery-filters">
      <div class="filter-group">
        <label>城市筛选：</label>
        <select v-model="selectedCity">
          <option value="">全部城市</option>
          <option v-for="city in cities" :key="city.code" :value="city.code">
            {{ city.name }}
          </option>
        </select>
      </div>
    </div>

    <div class="audio-list" v-if="loading">
      <div class="loading-spinner">加载中...</div>
    </div>

    <div class="audio-list" v-else-if="filteredAudios.length === 0">
      <div class="empty-state">
        <p>暂无音频</p>
        <p class="empty-hint">成为第一个上传音频的人吧！</p>
      </div>
    </div>

    <div class="audio-list" v-else>
      <div v-for="audio in filteredAudios" :key="audio.id" class="audio-card">
        <div class="audio-info">
          <div class="audio-icon">🎧</div>
          <div class="audio-details">
            <h3 class="audio-title">{{ audio.title }}</h3>
            <div class="audio-meta">
              <span class="audio-city">{{ getCityName(audio.city) }}</span>
              <span class="audio-dialect">{{ audio.dialectType }}</span>
              <span class="audio-uploader">上传者：{{ audio.uploader }}</span>
            </div>
            <p class="audio-description" v-if="audio.description">{{ audio.description }}</p>
          </div>
        </div>
        
        <div class="audio-actions">
          <audio :src="audio.audioUrl" controls class="audio-player"></audio>
          <div class="action-buttons">
            <button 
              class="rate-btn" 
              @click="toggleRating(audio)"
              :class="{ rated: audio.userRated }"
            >
              <span class="star">{{ audio.userRated ? '⭐' : '☆' }}</span>
              <span class="count">{{ audio.rating || 0 }}</span>
            </button>
            <button class="comment-btn" @click="toggleComment(audio)">
              {{ audio.comments?.length || 0 }}
            </button>
          </div>
        </div>

        <div v-if="audio.showComment" class="comment-section">
          <div class="comment-list">
            <div v-for="comment in audio.comments" :key="comment.id" class="comment-item">
              <div class="comment-author">{{ comment.user }}</div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>
          <div class="comment-input">
            <input 
              v-model="audio.newComment"
              type="text" 
              placeholder="写下你的评价..."
              @keyup.enter="submitComment(audio)"
            />
            <button @click="submitComment(audio)">发送</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAudioList, addComment, rateAudio } from '@/api/audio'

const router = useRouter()
const userStore = useUserStore()

const cities = [
  { code: 'fuzhou', name: '福州' },
  { code: 'xiamen', name: '厦门' },
  { code: 'quanzhou', name: '泉州' },
  { code: 'zhangzhou', name: '漳州' },
  { code: 'putian', name: '莆田' },
  { code: 'longyan', name: '龙岩' },
  { code: 'nanping', name: '南平' },
  { code: 'ningde', name: '宁德' },
  { code: 'sanming', name: '三明' }
]

const loading = ref(true)
const audios = ref([])
const selectedCity = ref('')

const filteredAudios = computed(() => {
  if (!selectedCity.value) return audios.value
  return audios.value.filter(audio => audio.city === selectedCity.value)
})

const getCityName = (code) => {
  const city = cities.find(c => c.code === code)
  return city ? city.name : code
}

const goToUpload = () => {
  if (!userStore.isLoggedIn) {    alert('请先登录后再上传音频')
    return
  }
  router.push('/audio-upload')
}

const loadAudios = async () => {
  loading.value = true
  try {
    const response = await getAudioList()
    const data = response.data || []
    audios.value = data.map(audio => ({
      ...audio,
      showComment: false,
      newComment: ''
    }))
  } catch (error) {
    console.error('加载音频失败:', error)
    // 使用模拟数据展示效果
    console.log('使用模拟数据展示')
    audios.value = [
      {
        id: 1,
        title: '福州童谣 - 真鸟囝',
        city: 'fuzhou',
        dialectType: '福州话',
        uploader: '张三',
        description: '经典的福州童谣，讲述真鸟囝的故事',
        audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3',
        rating: 15,
        userRated: false,
        comments: [],
        showComment: false,
        newComment: '',
        status: 'approved',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        title: '闽南语歌曲 - 爱拼才会赢',
        city: 'xiamen',
        dialectType: '闽南语',
        uploader: '李四',
        description: '经典的闽南语励志歌曲',
        audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3',
        rating: 28,
        userRated: false,
        comments: [
          { id: 1, user: '王五', content: '很好听！' }
        ],
        showComment: false,
        newComment: '',
        status: 'approved',
        createdAt: '2024-01-16 14:20:00'
      },
      {
        id: 3,
        title: '泉州高甲戏选段',
        city: 'quanzhou',
        dialectType: '泉州话',
        uploader: '陈六',
        description: '传统高甲戏经典选段',
        audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3',
        rating: 12,
        userRated: false,
        comments: [],
        showComment: false,
        newComment: '',
        status: 'approved',
        createdAt: '2024-01-17 09:15:00'
      }
    ]
  } finally {
    loading.value = false
  }
}

const toggleRating = async (audio) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录再评价')
    return
  }
  
  try {
    await rateAudio(audio.id, !audio.userRated)
    audio.userRated = !audio.userRated
    audio.rating = (audio.rating || 0) + (audio.userRated ? 1 : -1)
  } catch (error) {
    console.error('评价失败:', error)
    // 模拟评价
    console.log('模拟评价')
    audio.userRated = !audio.userRated
    audio.rating = (audio.rating || 0) + (audio.userRated ? 1 : -1)
  }
}

const toggleComment = (audio) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录再评论')
    return
  }
  audio.showComment = !audio.showComment
}

const submitComment = async (audio) => {
  if (!audio.newComment?.trim()) return
  
  try {
    await addComment(audio.id, audio.newComment)
    if (!audio.comments) audio.comments = []
    audio.comments.push({
      id: Date.now(),
      user: userStore.username,
      content: audio.newComment
    })
    audio.newComment = ''
  } catch (error) {
    console.error('评论失败:', error)
    // 模拟评论
    console.log('模拟评论')
    if (!audio.comments) audio.comments = []
    audio.comments.push({
      id: Date.now(),
      user: userStore.username,
      content: audio.newComment
    })
    audio.newComment = ''
  }
}

onMounted(() => {
  loadAudios()
})
</script>

<style scoped>
.audio-gallery-container {
  min-height: 100vh;
  padding: 100px 20px 40px;
  background-image: url('/background1.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.gallery-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.gallery-header h1 {
  font-size: 42px;
  color: #333;
  margin: 0 0 10px 0;
}

.gallery-header p {
  font-size: 18px;
  color: #666;
  margin: 0 0 20px 0;
}

.upload-btn {
  display: inline-block;
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.gallery-filters {
  max-width: 1200px;
  margin: 0 auto 30px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 16px;
}

.filter-group label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.filter-group select {
  flex: 1;
  padding: 12px 16px;
  font-size: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: white;
  cursor: pointer;
}

.audio-list {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.loading-spinner {
  text-align: center;
  padding: 60px 20px;
  font-size: 18px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
}

.empty-state p {
  font-size: 20px;
  color: #666;
  margin: 10px 0;
}

.empty-hint {
  font-size: 16px;
  color: #999;
}

.audio-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.audio-card:hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.audio-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.audio-icon {
  font-size: 48px;
  flex-shrink: 0;
}

.audio-details {
  flex: 1;
}

.audio-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.audio-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.audio-city,
.audio-dialect,
.audio-uploader {
  font-size: 14px;
  color: #666;
  background: #f5f5f5;
  padding: 4px 12px;
  border-radius: 20px;
}

.audio-description {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.audio-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audio-player {
  width: 100%;
  height: 40px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.rate-btn,
.comment-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.rate-btn:hover,
.comment-btn:hover {
  border-color: #000000;
  color: #000000;
}

.rate-btn.rated {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border-color: transparent;
}

.star {
  font-size: 18px;
}

.comment-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #f0f0f0;
}

.comment-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.comment-item {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 12px;
}

.comment-author {
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  font-size: 14px;
}

.comment-content {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.comment-input {
  display: flex;
  gap: 12px;
}

.comment-input input {
  flex: 1;
  padding: 12px 16px;
  font-size: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.comment-input input:focus {
  outline: none;
  border-color: #000000;
}

.comment-input button {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.comment-input button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
</style>
