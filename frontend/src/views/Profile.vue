<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>福建方言保护平台</h1>
      <div class="user-menu">
        <span>欢迎用户 {{ form.username }}</span>
        <button @click="goBack" class="back-btn">返回主页</button>
        <button v-if="userRole === 'ADMIN'" @click="goToAdmin" class="admin-btn">管理后台</button>
        <button @click="handleLogout" class="logout-btn">退出登录</button>
      </div>
    </div>

    <div class="profile-content">
      <!-- 个人信息卡片 -->
      <div class="profile-card">
        <div class="card-header">
          <h2>个人信息</h2>
        </div>
        <div class="card-body">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label>用户 ID</label>
              <input 
                v-model="form.id" 
                type="text" 
                disabled
                class="disabled-input"
              />
            </div>
            <div class="form-group">
              <label>用户名</label>
              <input 
                v-model="form.username" 
                type="text" 
                placeholder="请输入用户名"
                required
              />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input 
                v-model="form.phone" 
                type="tel" 
                placeholder="请输入手机号"
                pattern="[0-9]{11}"
              />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input 
                v-model="form.email" 
                type="email" 
                placeholder="请输入邮箱"
              />
            </div>

            <div class="form-error" v-if="error">{{ error }}</div>
            <div class="form-success" v-if="successMessage">{{ successMessage }}</div>

            <div class="form-actions">
              <button type="submit" class="submit-btn">保存修改</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 我的音频管理卡片 -->
      <div class="profile-card">
        <div class="card-header">
          <h2>我的音频</h2>
          <button @click="goToUpload" class="upload-btn">上传音频</button>
        </div>
        <div class="card-body">
          <div class="audio-stats">
            <div class="stat-item">
              <div class="stat-number">{{ audioStats.total }}</div>
              <div class="stat-label">总数</div>
            </div>
            <div class="stat-item pending">
              <div class="stat-number">{{ audioStats.pending }}</div>
              <div class="stat-label">审核中</div>
            </div>
            <div class="stat-item approved">
              <div class="stat-number">{{ audioStats.approved }}</div>
              <div class="stat-label">已通过</div>
            </div>
            <div class="stat-item rejected">
              <div class="stat-number">{{ audioStats.rejected }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>

          <div class="audio-tabs">
            <button 
              :class="['tab-btn', { active: audioTab === 'all' }]"
              @click="audioTab = 'all'"
            >
              全部
            </button>
            <button 
              :class="['tab-btn', { active: audioTab === 'pending' }]"
              @click="audioTab = 'pending'"
            >
              审核中
            </button>
            <button 
              :class="['tab-btn', { active: audioTab === 'approved' }]"
              @click="audioTab = 'approved'"
            >
              已通过
            </button>
            <button 
              :class="['tab-btn', { active: audioTab === 'rejected' }]"
              @click="audioTab = 'rejected'"
            >
              已拒绝
            </button>
          </div>

          <div class="audio-list" v-if="audioLoading">
            <div class="loading">加载中...</div>
          </div>

          <div class="audio-list" v-else-if="filteredAudios.length === 0">
            <div class="empty">暂无音频</div>
          </div>

          <div class="audio-list" v-else>
            <div v-for="audio in filteredAudios" :key="audio.id" class="audio-item">
              <div class="audio-info">

                <div class="audio-details">
                  <h3 class="audio-title">{{ audio.title }}</h3>
                  <div class="audio-meta">
                    <span :class="['status-badge', audio.status]">{{ getStatusText(audio.status) }}</span>
                    <span>{{ getCityName(audio.city) }}</span>
                    <span>{{ formatDate(audio.createdAt) }}</span>
                  </div>
                  <p class="audio-desc" v-if="audio.description">{{ audio.description }}</p>
                  <p v-if="audio.status === 'rejected'" class="rejection-reason">
                    <strong>拒绝原因：</strong>{{ audio.rejectReason }}
                  </p>
                </div>
              </div>
              <div class="audio-actions">
                <audio :src="audio.audioUrl" controls class="audio-player"></audio>
                <div class="action-buttons">
                  <button v-if="audio.status === 'pending'" @click="cancelAudio(audio)" class="btn-cancel">取消</button>
                  <button v-if="audio.status === 'approved'" @click="deleteAudioItem(audio)" class="btn-delete">删除</button>
                  <button v-if="audio.status === 'rejected'" @click="goToUpload" class="btn-reupload">重新上传</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 答题记录卡片 -->
      <div class="profile-card">
        <div class="card-header">
          <h2>答题记录</h2>
        </div>
        <div class="card-body">
          <div class="quiz-stats">
            <div class="stat-item">
              <div class="stat-number">{{ quizStats.total }}</div>
              <div class="stat-label">总次数</div>
            </div>
            <div class="stat-item best">
              <div class="stat-number">{{ quizStats.best }}</div>
              <div class="stat-label">最高分</div>
            </div>
            <div class="stat-item average">
              <div class="stat-number">{{ quizStats.average }}</div>
              <div class="stat-label">平均分</div>
            </div>
            <div class="stat-item level">
              <div class="stat-number">{{ quizStats.bestLevel }}</div>
              <div class="stat-label">最佳等级</div>
            </div>
          </div>

          <div class="quiz-tabs">
            <button 
              :class="['tab-btn', { active: quizTab === 'all' }]"
              @click="quizTab = 'all'"
            >
              全部
            </button>
            <button 
              :class="['tab-btn', { active: quizTab === 'excellent' }]"
              @click="quizTab = 'excellent'"
            >
              优秀 (≥90)
            </button>
            <button 
              :class="['tab-btn', { active: quizTab === 'good' }]"
              @click="quizTab = 'good'"
            >
              良好 (≥70)
            </button>
            <button 
              :class="['tab-btn', { active: quizTab === 'pass' }]"
              @click="quizTab = 'pass'"
            >
              及格 (≥50)
            </button>
          </div>

          <div class="quiz-list" v-if="quizLoading">
            <div class="loading">加载中...</div>
          </div>

          <div class="quiz-list" v-else-if="filteredQuizzes.length === 0">
            <div class="empty">暂无答题记录</div>
          </div>

          <div class="quiz-list" v-else>
            <div v-for="record in filteredQuizzes" :key="record.id" class="quiz-item">
              <div class="quiz-header">
                <div class="quiz-city">{{ getCityName(record.cityCode) }}方言测试</div>
                <div class="quiz-date">{{ formatTime(record.createdAt) }}</div>
              </div>
              <div class="quiz-body">
                <div class="quiz-score" :class="getScoreClass(record.score)">
                  {{ record.score }}
                  <span class="score-label">分</span>
                </div>
                <div class="quiz-details">
                  <div class="quiz-detail-item">
                    <span class="detail-label">正确率</span>
                    <span class="detail-value">{{ record.accuracy }}%</span>
                  </div>
                  <div class="quiz-detail-item">
                    <span class="detail-label">等级</span>
                    <span :class="['detail-value', 'level-badge', getLevelClass(record.level)]">{{ record.level }}</span>
                  </div>
                  <div class="quiz-detail-item">
                    <span class="detail-label">题目数</span>
                    <span class="detail-value">{{ record.totalQuestions }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updateUserInfo } from '@/api/auth'
import { getMyAudios, deleteAudio as apiDeleteAudio } from '@/api/audio'
import { getRecords } from '@/api/quiz'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userRole = computed(() => userStore.role || 'USER')

const form = ref({
  id: '',
  username: '',
  phone: '',
  email: '',
  role: 'USER'
})

const error = ref('')
const successMessage = ref('')

// 音频管理相关
const audioTab = ref('all')
const audioLoading = ref(true)
const audios = ref([])

// 答题记录相关
const quizTab = ref('all')
const quizLoading = ref(true)
const quizRecords = ref([])

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

const audioStats = computed(() => {
  return {
    total: audios.value.length,
    pending: audios.value.filter(a => a.status === 'pending').length,
    approved: audios.value.filter(a => a.status === 'approved').length,
    rejected: audios.value.filter(a => a.status === 'rejected').length
  }
})

const quizStats = computed(() => {
  if (!quizRecords.value.length) {
    return { total: 0, best: 0, average: 0, bestLevel: '-' }
  }
  
  const scores = quizRecords.value.map(r => r.score)
  const total = quizRecords.value.length
  const best = Math.max(...scores)
  const average = Math.round(scores.reduce((a, b) => a + b, 0) / total)
  const bestRecord = quizRecords.value.find(r => r.score === best)
  const bestLevel = bestRecord ? bestRecord.level : '-'
  
  return { total, best, average, bestLevel }
})

const filteredQuizzes = computed(() => {
  if (quizTab.value === 'all') return quizRecords.value
  
  return quizRecords.value.filter(record => {
    if (quizTab.value === 'excellent') return record.score >= 90
    if (quizTab.value === 'good') return record.score >= 70
    if (quizTab.value === 'pass') return record.score >= 50
    return true
  })
})

const filteredAudios = computed(() => {
  if (audioTab.value === 'all') return audios.value
  return audios.value.filter(audio => audio.status === audioTab.value)
})

onMounted(() => {
  // 初始化表单数据
  form.value.id = userStore.userId
  form.value.username = userStore.username
  form.value.phone = userStore.phone || ''
  form.value.email = userStore.email || ''
  
  // 加载用户音频
  loadMyAudios()
  
  // 加载答题记录
  loadQuizRecords()
})

const handleSubmit = async () => {
  error.value = ''
  successMessage.value = ''
  
  try {
    const data = {
      id: form.value.id,
      username: form.value.username,
      phone: form.value.phone,
      email: form.value.email
    }
    
    const result = await updateUserInfo(data)
    
    if (result.code === 200) {
      successMessage.value = '保存成功！'
      // 更新 store 中的用户信息
      userStore.updateUserInfo({
        username: form.value.username,
        phone: form.value.phone,
        email: form.value.email
      })
      
      // 3 秒后清除成功消息
      setTimeout(() => {
        successMessage.value = ''
      }, 3000)
    } else {
      error.value = result.message || '保存失败'
    }
  } catch (err) {
    error.value = '保存失败，请稍后重试'
    console.error('更新用户信息失败:', err)
  }
}

const getCityName = (code) => {
  const city = cities.find(c => c.code === code)
  return city ? city.name : code
}

const getStatusText = (status) => {
  const statusMap = { pending: '审核中', approved: '已通过', rejected: '已拒绝' }
  return statusMap[status] || status
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const loadQuizRecords = async () => {
  quizLoading.value = true
  try {
    const response = await getRecords(userStore.userId)
    if (response.code === 200) {
      quizRecords.value = response.data || []
    }
  } catch (error) {
    console.error('加载答题记录失败:', error)
    // 使用模拟数据
    console.log('使用模拟数据展示')
    quizRecords.value = [
      {
        id: 1,
        cityCode: 'fuzhou',
        score: 95,
        accuracy: 95,
        level: '方言大师',
        totalQuestions: 20,
        createdAt: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        cityCode: 'xiamen',
        score: 85,
        accuracy: 85,
        level: '方言能手',
        totalQuestions: 20,
        createdAt: '2024-01-16 14:20:00'
      },
      {
        id: 3,
        cityCode: 'quanzhou',
        score: 72,
        accuracy: 72,
        level: '方言能手',
        totalQuestions: 20,
        createdAt: '2024-01-17 09:15:00'
      },
      {
        id: 4,
        cityCode: 'zhangzhou',
        score: 60,
        accuracy: 60,
        level: '方言初学者',
        totalQuestions: 20,
        createdAt: '2024-01-18 16:45:00'
      }
    ]
  } finally {
    quizLoading.value = false
  }
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 70) return 'score-good'
  if (score >= 50) return 'score-pass'
  return 'score-fail'
}

const getLevelClass = (level) => {
  const levelMap = {
    '方言大师': 'level-master',
    '方言能手': 'level-expert',
    '方言初学者': 'level-beginner',
    '继续加油': 'level-learner'
  }
  return levelMap[level] || ''
}

const loadMyAudios = async () => {
  audioLoading.value = true
  try {
    const response = await getMyAudios({ uploaderId: userStore.userId })
    const data = response.data || []
    audios.value = data
  } catch (error) {
    console.error('加载音频失败:', error)
    // 使用模拟数据
    console.log('使用模拟数据展示')
    audios.value = [
      {
        id: 1,
        title: '我上传的福州歌谣',
        city: 'fuzhou',
        dialectType: '福州话',
        description: '传统福州歌谣',
        audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3',
        status: 'approved',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        title: '闽南语童谣',
        city: 'xiamen',
        dialectType: '闽南语',
        description: '小时候唱的童谣',
        audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3',
        status: 'pending',
        createdAt: '2024-01-18 14:20:00'
      },
      {
        id: 3,
        title: '泉州故事',
        city: 'quanzhou',
        dialectType: '泉州话',
        description: '泉州民间故事',
        audioUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3',
        status: 'rejected',
        rejectReason: '内容不清晰',
        createdAt: '2024-01-16 09:15:00'
      }
    ]
  } finally {
    audioLoading.value = false
  }
}

const cancelAudio = async (audio) => {
  if (!confirm(`确定要取消音频 "${audio.title}" 吗？`)) return
  
  try {
    await apiDeleteAudio(audio.id)
    audios.value = audios.value.filter(a => a.id !== audio.id)
    alert('已取消')
  } catch (error) {
    console.error('取消失败:', error)
    // 模拟取消
    console.log('模拟取消')
    audios.value = audios.value.filter(a => a.id !== audio.id)
    alert('已取消！（模拟）')
  }
}

const deleteAudioItem = async (audio) => {
  if (!confirm(`确定要删除音频 "${audio.title}" 吗？`)) return
  
  try {
    await apiDeleteAudio(audio.id)
    audios.value = audios.value.filter(a => a.id !== audio.id)
    alert('已删除')
  } catch (error) {
    console.error('删除失败:', error)
    // 模拟删除
    console.log('模拟删除')
    audios.value = audios.value.filter(a => a.id !== audio.id)
    alert('已删除！（模拟）')
  }
}

const goToUpload = () => {
  router.push('/audio-upload')
}

const goBack = () => {
  router.push('/home')
}

const goToAdmin = () => {
  router.push('/admin')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/home')
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-image: url('/background1.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.profile-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.profile-header h1 {
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
.admin-btn,
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
  color: #000000;
}

.back-btn:hover {
  transform: translateY(-2px);
}

.admin-btn {
  background: #28a745;
  color: white;
}

.admin-btn:hover {
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

.profile-content {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  padding: 25px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  color: white;
  margin: 0;
  font-size: 24px;
}

.card-header .upload-btn {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.card-header .upload-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.card-body {
  padding: 40px;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #000000;
}

.disabled-input {
  background: #f5f5f5;
  cursor: not-allowed;
  color: #999;
}

.form-error {
  color: #dc3545;
  font-size: 13px;
  margin-bottom: 15px;
  padding: 8px 12px;
  background: #fff5f5;
  border-radius: 4px;
}

.form-success {
  color: #28a745;
  font-size: 13px;
  margin-bottom: 15px;
  padding: 8px 12px;
  background: #f0fff4;
  border-radius: 4px;
}

.form-actions {
  margin-top: 30px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

/* 音频管理样式 */
.audio-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #000000;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-item.pending .stat-number {
  color: #ffc107;
}

.stat-item.approved .stat-number {
  color: #28a745;
}

.stat-item.rejected .stat-number {
  color: #dc3545;
}

.audio-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.audio-tabs .tab-btn {
  flex: 1;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  color: #666;
  background: #f5f5f5;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.audio-tabs .tab-btn.active {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border-color: transparent;
}

.audio-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audio-list .loading,
.audio-list .empty {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 16px;
}

.audio-item {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  border: 2px solid #e0e0e0;
}

.audio-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.audio-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.audio-details {
  flex: 1;
}

.audio-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.audio-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.status-badge {
  padding: 4px 12px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 12px;
  background: #f5f5f5;
  color: #666;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.approved {
  background: #d4edda;
  color: #155724;
}

.status-badge.rejected {
  background: #f8d7da;
  color: #721c24;
}

.audio-desc {
  margin: 12px 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

.rejection-reason {
  padding: 12px;
  background: #f8d7da;
  border-radius: 8px;
  border-left: 4px solid #dc3545;
  color: #721c24;
  margin: 12px 0;
}

.audio-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.audio-player {
  width: 100%;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel,
.btn-delete,
.btn-reupload {
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background: #5a6268;
}

.btn-delete {
  background: #dc3545;
  color: white;
}

.btn-delete:hover {
  background: #c82333;
}

.btn-reupload {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
}

.btn-reupload:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* 答题记录样式 */
.quiz-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.quiz-stats .stat-item {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
}

.quiz-stats .stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #000000;
  margin-bottom: 8px;
}

.quiz-stats .stat-label {
  font-size: 14px;
  color: #666;
}

.quiz-stats .stat-item.best .stat-number {
  color: #28a745;
}

.quiz-stats .stat-item.average .stat-number {
  color: #17a2b8;
}

.quiz-stats .stat-item.level .stat-number {
  color: #ffc107;
  font-size: 24px;
}

.quiz-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.quiz-tabs .tab-btn {
  flex: 1;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  color: #666;
  background: #f5f5f5;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quiz-tabs .tab-btn.active {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border-color: transparent;
}

.quiz-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.quiz-list .loading,
.quiz-list .empty {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 16px;
}

.quiz-item {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
}

.quiz-item:hover {
  border-color: #000000;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.15);
}

.quiz-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e0e0;
}

.quiz-city {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.quiz-date {
  font-size: 13px;
  color: #999;
}

.quiz-body {
  display: flex;
  align-items: center;
  gap: 24px;
}

.quiz-score {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 28px;
  flex-shrink: 0;
  position: relative;
}

.quiz-score.score-excellent {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.quiz-score.score-good {
  background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
  color: white;
}

.quiz-score.score-pass {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
  color: white;
}

.quiz-score.score-fail {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  color: white;
}

.score-label {
  font-size: 12px;
  font-weight: 400;
  opacity: 0.9;
}

.quiz-details {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.quiz-detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-label {
  font-size: 13px;
  color: #666;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.level-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px !important;
  display: inline-block;
  text-align: center;
}

.level-badge.level-master {
  background: #ffc107;
  color: #856404;
}

.level-badge.level-expert {
  background: #17a2b8;
  color: white;
}

.level-badge.level-beginner {
  background: #28a745;
  color: white;
}

.level-badge.level-learner {
  background: #6c757d;
  color: white;
}
</style>
