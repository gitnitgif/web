<template>
  <div class="admin-layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="logo">
        <h2>🎵 方言保护</h2>
        <p>管理平台</p>
      </div>
      <nav class="menu">
        <div class="menu-item active" @click="activeMenu = 'dashboard'">
          <span class="icon">📊</span>
          <span>控制台</span>
        </div>
        <div class="menu-item" @click="activeMenu = 'audio-review'">
          <span class="icon">🎧</span>
          <span>音频审核</span>
        </div>
        <div class="menu-item" @click="activeMenu = 'resource-manage'">
          <span class="icon">📚</span>
          <span>资源管理</span>
        </div>
        <div class="menu-item" @click="activeMenu = 'user-manage'">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </div>
        <div class="menu-item" @click="activeMenu = 'system'">
          <span class="icon">⚙️</span>
          <span>系统设置</span>
        </div>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <div class="breadcrumb">
          <span>首页</span>
          <span>/</span>
          <span>管理后台</span>
        </div>
        <div class="user-menu">
          <span class="welcome">晚上好，{{ username }}</span>
          <button @click="handleLogout" class="logout-btn">退出登录</button>
        </div>
      </header>

      <!-- 内容区 -->
      <div class="content">
        <!-- 控制台 -->
        <div v-show="activeMenu === 'dashboard'" class="dashboard">
          <div class="welcome-card">
            <div class="avatar">👋</div>
            <div class="text">
              <h3>晚上好，{{ username }}，愿你天黑有灯，下雨有伞。</h3>
              <p>福建方言保护平台管理后台，共同守护方言文化</p>
            </div>
          </div>

          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">📊</div>
              <div class="stat-info">
                <div class="stat-value">{{ dbStatus.userCount || 0 }}</div>
                <div class="stat-label">用户总数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🎵</div>
              <div class="stat-info">
                <div class="stat-value">{{ audioStats.pending }}</div>
                <div class="stat-label">待审核音频</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">✅</div>
              <div class="stat-info">
                <div class="stat-value">{{ audioStats.approved }}</div>
                <div class="stat-label">已通过音频</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">❌</div>
              <div class="stat-info">
                <div class="stat-value">{{ audioStats.rejected }}</div>
                <div class="stat-label">已拒绝音频</div>
              </div>
            </div>
          </div>

          <div class="info-grid">
            <!-- 新增用户 -->
            <div class="info-card">
              <div class="card-header">
                <h3>� 新增用户</h3>
              </div>
              <div class="info-content">
                <div class="info-row">
                  <span class="label">今日新增</span>
                  <span class="value highlight">{{ todayNewUsers }}</span>
                </div>
                <div class="info-row">
                  <span class="label">本周新增</span>
                  <span class="value">{{ weekNewUsers }}</span>
                </div>
                <div class="info-row">
                  <span class="label">本月新增</span>
                  <span class="value">{{ monthNewUsers }}</span>
                </div>
                <div class="info-row">
                  <span class="label">总用户数</span>
                  <span class="value">{{ dbStatus.userCount || 0 }}</span>
                </div>
              </div>
            </div>

            <!-- 新增音频 -->
            <div class="info-card">
              <div class="card-header">
                <h3>🎵 新增音频</h3>
              </div>
              <div class="info-content">
                <div class="info-row">
                  <span class="label">今日新增</span>
                  <span class="value highlight">{{ todayNewAudios }}</span>
                </div>
                <div class="info-row">
                  <span class="label">本周新增</span>
                  <span class="value">{{ weekNewAudios }}</span>
                </div>
                <div class="info-row">
                  <span class="label">本月新增</span>
                  <span class="value">{{ monthNewAudios }}</span>
                </div>
                <div class="info-row">
                  <span class="label">总音频数</span>
                  <span class="value">{{ totalAudios }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 音频审核 -->
        <div v-show="activeMenu === 'audio-review'" class="audio-review">
          <div class="page-header">
            <h2>🎵 音频审核管理</h2>
            <p>管理用户上传的方言音频，进行审核操作</p>
          </div>

          <div class="tabs">
            <button 
              :class="['tab-btn', { active: audioTab === 'pending' }]"
              @click="audioTab = 'pending'"
            >
              待审核 ({{ audioStats.pending }})
            </button>
            <button 
              :class="['tab-btn', { active: audioTab === 'approved' }]"
              @click="audioTab = 'approved'"
            >
              已通过 ({{ audioStats.approved }})
            </button>
            <button 
              :class="['tab-btn', { active: audioTab === 'rejected' }]"
              @click="audioTab = 'rejected'"
            >
              已拒绝 ({{ audioStats.rejected }})
            </button>
          </div>

          <div class="audio-list" v-if="audioLoading">
            <div class="loading">加载中...</div>
          </div>

          <div class="audio-list" v-else-if="audios.length === 0">
            <div class="empty">暂无{{ audioTab === 'pending' ? '待审核' : audioTab === 'approved' ? '已通过' : '已拒绝' }}的音频</div>
          </div>

          <div class="audio-list" v-else>
            <div v-for="audio in audios" :key="audio.id" class="audio-item">
              <div class="audio-header">
                <div class="audio-title">
                  <h3>{{ audio.title }}</h3>
                  <span :class="['status-badge', audio.status]">{{ getStatusText(audio.status) }}</span>
                </div>
                <div class="audio-meta">
                  <span>📍 {{ getCityName(audio.city) }}</span>
                  <span>🎤 {{ audio.dialectType }}</span>
                  <span>👤 {{ audio.uploader }}</span>
                </div>
              </div>
              <p class="audio-desc">{{ audio.description || '无描述' }}</p>
              <audio :src="audio.audioUrl" controls class="audio-player"></audio>
              
              <div v-if="audio.status === 'pending'" class="audio-actions">
                <button class="btn-approve" @click="approveAudio(audio)">✅ 通过</button>
                <button class="btn-reject" @click="rejectAudio(audio)">❌ 拒绝</button>
              </div>
              
              <div v-if="audio.status === 'rejected'" class="rejection-reason">
                <strong>拒绝原因：</strong>{{ audio.rejectReason || '无' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 资源管理 -->
        <div v-show="activeMenu === 'resource-manage'" class="resource-manage">
          <div class="page-header">
            <h2>📚 方言资源管理</h2>
            <p>管理各城市的方言资源，配置音频和下载链接</p>
          </div>

          <button @click="showAddResource" class="add-btn">➕ 添加资源</button>
          <button @click="loadResources" class="refresh-btn">🔄 刷新列表</button>

          <div class="resource-list">
            <div v-for="city in cityResources" :key="city.id" class="resource-item">
              <div class="resource-header">
                <div class="resource-info">
                  <h3>{{ city.name }} - {{ city.resource }}</h3>
                  <p>{{ city.dialect }}</p>
                </div>
                <div class="resource-status">
                  <span :class="['status-badge', city.audioUrl ? 'success' : 'warning']">
                    {{ city.audioUrl ? '✅ 有音频' : '⚠️ 无音频' }}
                  </span>
                  <button @click="deleteResourceConfirm(city)" class="delete-resource-btn">🗑️ 删除</button>
                </div>
              </div>
              
              <div class="resource-form">
                <div class="form-row">
                  <label>城市代码：</label>
                  <input 
                    type="text" 
                    v-model="city.code" 
                    placeholder="如：fuzhou"
                    class="form-input"
                    readonly
                    title="城市代码不可修改"
                  />
                </div>
                <div class="form-row">
                  <label>资源名称：</label>
                  <input 
                    type="text" 
                    v-model="city.resource" 
                    placeholder="如：福州歌谣"
                    class="form-input"
                  />
                </div>
                <div class="form-row">
                  <label>音频 URL：</label>
                  <input 
                    type="text" 
                    v-model="city.audioUrl" 
                    placeholder="支持本地路径或网络 URL"
                    class="form-input"
                  />
                  <small class="form-tip">示例：/uploads/audio/fuzhou.mp3 或 https://music.163.com/song/media/outer/url?id=xxx.mp3</small>
                </div>
                <div class="form-row">
                  <label>下载 URL：</label>
                  <input 
                    type="text" 
                    v-model="city.downloadUrl" 
                    placeholder="支持本地路径或网络 URL"
                    class="form-input"
                  />
                  <small class="form-tip">示例：/uploads/audio/fuzhou.mp3 或 https://example.com/audio.mp3</small>
                </div>
                <div class="form-row">
                  <label>背景图片：</label>
                  <input 
                    type="text" 
                    v-model="city.bgImage" 
                    placeholder="支持本地路径或网络 URL"
                    class="form-input"
                  />
                  <small class="form-tip">示例：/fuzhou.png 或 https://example.com/image.jpg</small>
                </div>
                <div class="form-actions">
                  <button @click="previewAudio(city)" class="preview-btn">🔊 试听</button>
                  <button @click="saveResource(city)" class="save-btn">💾 保存</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-show="activeMenu === 'user-manage'" class="user-manage">
          <div class="page-header">
            <h2>👥 用户管理</h2>
            <p>管理系统用户，包括删除用户和重置密码</p>
          </div>

          <button @click="loadUsers" class="refresh-btn">🔄 刷新用户列表</button>

          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>手机号</th>
                  <th>邮箱</th>
                  <th>注册时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in userList" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.phone || '-' }}</td>
                  <td>{{ user.email || '-' }}</td>
                  <td>{{ formatDate(user.createTime) }}</td>
                  <td>
                    <button @click="showResetPasswordModal(user)" class="action-btn reset-btn">重置密码</button>
                    <button @click="handleDeleteUser(user.id)" class="action-btn delete-btn">删除</button>
                  </td>
                </tr>
                <tr v-if="userList.length === 0">
                  <td colspan="6" class="no-data">暂无用户数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 系统设置 -->
        <div v-show="activeMenu === 'system'" class="system-settings">
          <div class="page-header">
            <h2>🔧 调试工具</h2>
            <p>系统调试和测试工具</p>
          </div>

          <div class="debug-tools">
            <button @click="testApi" class="debug-btn">测试 API 连接</button>
            <button @click="clearCache" class="debug-btn">清除本地缓存</button>
            <button @click="showErrorTest" class="debug-btn">测试错误提示</button>
          </div>
          <div v-if="debugMessage" class="debug-message">
            {{ debugMessage }}
          </div>
        </div>
      </div>
    </main>

    <!-- 重置密码弹窗 -->
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="closePasswordModal">
      <div class="modal">
        <h3>重置密码</h3>
        <p>用户：{{ selectedUser?.username }}</p>
        <input 
          type="password" 
          v-model="newPassword" 
          placeholder="请输入新密码"
          class="modal-input"
        />
        <div class="modal-actions">
          <button @click="closePasswordModal" class="cancel-btn">取消</button>
          <button @click="handleResetPassword" class="confirm-btn">确认重置</button>
        </div>
      </div>
    </div>

    <!-- 添加资源弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal large">
        <h3>添加方言资源</h3>
        <div class="modal-form">
            <div class="form-row">
              <label>城市代码：</label>
              <input 
                type="text" 
                v-model="newResource.code" 
                placeholder="如：fuzhou"
                class="modal-input"
              />
            </div>
            <div class="form-row">
              <label>城市名称：</label>
              <input 
                type="text" 
                v-model="newResource.name" 
                placeholder="如：福州"
                class="modal-input"
              />
            </div>
            <div class="form-row">
              <label>方言类型：</label>
              <input 
                type="text" 
                v-model="newResource.dialect" 
                placeholder="如：福州话"
                class="modal-input"
              />
            </div>
            <div class="form-row">
              <label>资源名称：</label>
              <input 
                type="text" 
                v-model="newResource.resource" 
                placeholder="如：福州歌谣"
                class="modal-input"
              />
            </div>
            <div class="form-row">
              <label>资源描述：</label>
              <textarea 
                v-model="newResource.description" 
                placeholder="请输入资源描述"
                class="modal-input textarea"
              ></textarea>
            </div>
            <div class="form-row">
              <label>音频 URL：</label>
              <input 
                type="text" 
                v-model="newResource.audioUrl" 
                placeholder="支持本地路径或网络 URL"
                class="modal-input"
              />
              <small class="form-tip">示例：/uploads/audio/fuzhou.mp3 或 https://music.163.com/song/media/outer/url?id=xxx.mp3</small>
            </div>
            <div class="form-row">
              <label>下载 URL：</label>
              <input 
                type="text" 
                v-model="newResource.downloadUrl" 
                placeholder="支持本地路径或网络 URL"
                class="modal-input"
              />
              <small class="form-tip">示例：/uploads/audio/fuzhou.mp3 或 https://example.com/audio.mp3</small>
            </div>
            <div class="form-row">
              <label>背景图片：</label>
              <input 
                type="text" 
                v-model="newResource.bgImage" 
                placeholder="支持本地路径或网络 URL"
                class="modal-input"
              />
              <small class="form-tip">示例：/fuzhou.png 或 https://example.com/image.jpg</small>
            </div>
          </div>
        <div class="modal-actions">
          <button @click="showAddModal = false" class="cancel-btn">取消</button>
          <button @click="saveNewResource" class="confirm-btn">确认添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAllUsers, deleteUser, resetPassword, getSystemInfo, getDatabaseStatus, getAudioReviewList, approveAudio as apiApproveAudio, rejectAudio as apiRejectAudio } from '@/api/admin'
import { getAllResources, saveResource as apiSaveResource, updateResource as apiUpdateResource, deleteResource as apiDeleteResource } from '@/api/resource'

const router = useRouter()
const userStore = useUserStore()
const username = ref(userStore.username || '管理员')
const hasPermission = computed(() => userStore.role === 'ADMIN')

const activeMenu = ref('dashboard')
const showPasswordModal = ref(false)
const selectedUser = ref(null)
const newPassword = ref('')
const userList = ref([])
const systemInfo = ref({})
const dbStatus = ref({})
const debugMessage = ref('')

// 音频审核相关
const audios = ref([])
const audioTab = ref('pending')
const audioLoading = ref(false)
const audioStats = ref({
  pending: 0,
  approved: 0,
  rejected: 0
})

// 资源管理相关
const cityResources = ref([])
const showAddModal = ref(false)
const editingResource = ref(null)
const newResource = ref({
  code: '',
  name: '',
  dialect: '',
  resource: '',
  description: '',
  audioUrl: '',
  downloadUrl: '',
  bgImage: '',
  enabled: 1
})

// 加载资源列表
const loadResources = async () => {
  try {
    const res = await getAllResources()
    if (res.code === 200) {
      cityResources.value = res.data
    }
  } catch (error) {
    console.error('加载资源列表失败:', error)
  }
}

// 试听音频
const previewAudio = (city) => {
  if (!city.audioUrl) {
    alert('请先填写音频 URL')
    return
  }
  
  try {
    const audio = new Audio(city.audioUrl)
    audio.play()
      .then(() => {
        console.log('音频开始播放:', city.name)
        // 显示播放提示
        const originalText = city.name
        city.name = originalText + ' 🔊'
        setTimeout(() => {
          city.name = originalText
        }, 2000)
      })
      .catch(error => {
        console.error('播放失败:', error)
        let errorMsg = '播放失败'
        if (error.name === 'NotAllowedError') {
          errorMsg = '浏览器阻止自动播放，请点击按钮后试听'
        } else if (error.name === 'NotSupportedError') {
          errorMsg = '不支持的音频格式'
        } else if (error.name === 'AbortError') {
          errorMsg = '播放被中断'
        } else {
          errorMsg = '请检查 URL 是否正确或网络是否畅通'
        }
        alert(errorMsg)
      })
  } catch (error) {
    console.error('音频加载失败:', error)
    alert('无法加载音频：' + error.message)
  }
}

// 保存资源（新增或更新）
const saveResource = async (city) => {
  try {
    const res = await apiUpdateResource(city)
    if (res.code === 200) {
      alert(`${city.name} 资源已保存！`)
      loadResources()
    } else {
      alert('保存失败：' + res.message)
    }
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败：' + error.message)
  }
}

// 删除资源
const deleteResourceConfirm = async (city) => {
  if (!confirm(`确定要删除 ${city.name} 的资源吗？`)) return
  
  try {
    const res = await apiDeleteResource(city.id)
    if (res.code === 200) {
      alert('删除成功')
      loadResources()
    } else {
      alert('删除失败：' + res.message)
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败：' + error.message)
  }
}

// 添加资源
const showAddResource = () => {
  editingResource.value = null
  newResource.value = {
    code: '',
    name: '',
    dialect: '',
    resource: '',
    description: '',
    audioUrl: '',
    downloadUrl: '',
    bgImage: '',
    enabled: 1
  }
  showAddModal.value = true
}

const saveNewResource = async () => {
  if (!newResource.value.code || !newResource.value.name) {
    alert('请填写必填项')
    return
  }
  
  try {
    const res = await apiSaveResource(newResource.value)
    if (res.code === 200) {
      alert('添加成功')
      showAddModal.value = false
      loadResources()
    } else {
      alert('添加失败：' + res.message)
    }
  } catch (error) {
    console.error('添加失败:', error)
    alert('添加失败：' + error.message)
  }
}

// 新增统计数据
const todayNewUsers = ref(0)
const weekNewUsers = ref(0)
const monthNewUsers = ref(0)
const todayNewAudios = ref(0)
const weekNewAudios = ref(0)
const monthNewAudios = ref(0)
const totalAudios = ref(0)

const cities = [
  { code: 'fuzhou', name: '福州市', dialect: '福州话' },
  { code: 'xiamen', name: '厦门市', dialect: '闽南语' },
  { code: 'quanzhou', name: '泉州市', dialect: '泉州话' },
  { code: 'zhangzhou', name: '漳州市', dialect: '漳州话' },
  { code: 'putian', name: '莆田市', dialect: '莆仙话' },
  { code: 'longyan', name: '龙岩市', dialect: '客家话' },
  { code: 'sanming', name: '三明市', dialect: '闽南语' },
  { code: 'nanping', name: '南平市', dialect: '闽南语' },
  { code: 'ningde', name: '宁德市', dialect: '闽南语' }
]

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadSystemInfo = async () => {
  try {
    const res = await getSystemInfo()
    if (res.code === 200) {
      systemInfo.value = res.data
    }
  } catch (error) {
    console.error('获取系统信息失败:', error)
  }
}

const loadDatabaseStatus = async () => {
  try {
    const res = await getDatabaseStatus()
    if (res.code === 200) {
      dbStatus.value = res.data
      // 更新音频统计
      audioStats.value.pending = res.data.pendingCount || 0
      audioStats.value.approved = res.data.approvedCount || 0
      audioStats.value.rejected = res.data.rejectedCount || 0
      
      // 模拟新增用户数据（实际应该从后端获取）
      todayNewUsers.value = Math.floor(Math.random() * 10)
      weekNewUsers.value = Math.floor(Math.random() * 50)
      monthNewUsers.value = Math.floor(Math.random() * 200)
      
      // 模拟新增音频数据
      todayNewAudios.value = Math.floor(Math.random() * 15)
      weekNewAudios.value = Math.floor(Math.random() * 80)
      monthNewAudios.value = Math.floor(Math.random() * 300)
      totalAudios.value = (audioStats.value.pending || 0) + (audioStats.value.approved || 0) + (audioStats.value.rejected || 0)
    }
  } catch (error) {
    console.error('获取数据库状态失败:', error)
  }
}

const loadUsers = async () => {
  try {
    const res = await getAllUsers()
    if (res.code === 200) {
      userList.value = res.data
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    alert('获取用户列表失败：' + error.message)
  }
}

const showResetPasswordModal = (user) => {
  selectedUser.value = user
  newPassword.value = ''
  showPasswordModal.value = true
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  selectedUser.value = null
  newPassword.value = ''
}

const handleResetPassword = async () => {
  if (!newPassword.value) {
    alert('请输入新密码')
    return
  }
  
  try {
    const res = await resetPassword({
      userId: selectedUser.value.id,
      newPassword: newPassword.value
    })
    
    if (res.code === 200) {
      alert('密码重置成功')
      closePasswordModal()
    } else {
      alert('密码重置失败：' + res.message)
    }
  } catch (error) {
    console.error('密码重置失败:', error)
    alert('密码重置失败：' + error.message)
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/home')
}

const getCityName = (code) => {
  const city = cities.find(c => c.code === code)
  return city ? city.name : code
}

const getStatusText = (status) => {
  const statusMap = { pending: '待审核', approved: '已通过', rejected: '已拒绝' }
  return statusMap[status] || status
}

const loadAudioReview = async () => {
  audioLoading.value = true
  try {
    const res = await getAudioReviewList(audioTab.value)
    const data = res.data || []
    audios.value = data
  } catch (error) {
    console.error('加载音频列表失败:', error)
    audios.value = []
  } finally {
    audioLoading.value = false
  }
}

watch(audioTab, () => {
  loadAudioReview()
})

const approveAudio = async (audio) => {
  if (!confirm(`确认通过音频"${audio.title}"吗？`)) return
  
  try {
    const res = await apiApproveAudio(audio.id)
    if (res.code === 200) {
      alert('已通过')
      audio.status = 'approved'
      audioStats.value.pending--
      audioStats.value.approved++
    } else {
      alert('审核失败：' + res.message)
    }
  } catch (error) {
    console.error('审核失败:', error)
    alert('审核失败：' + error.message)
  }
}

const rejectAudio = async (audio) => {
  const reason = prompt('请输入拒绝原因：', '')
  if (!reason) return
  
  try {
    const res = await apiRejectAudio(audio.id, reason)
    if (res.code === 200) {
      alert('已拒绝')
      audio.status = 'rejected'
      audio.rejectReason = reason
      audioStats.value.pending--
      audioStats.value.rejected++
    } else {
      alert('审核失败：' + res.message)
    }
  } catch (error) {
    console.error('审核失败:', error)
    alert('审核失败：' + error.message)
  }
}

const testApi = async () => {
  try {
    const res = await getSystemInfo()
    if (res.code === 200) {
      debugMessage.value = '✅ API 连接正常！\n系统：' + (res.data.osName || '未知') + '\nJava: ' + (res.data.javaVersion || '未知')
    } else {
      debugMessage.value = '❌ API 返回异常：' + res.message
    }
  } catch (error) {
    debugMessage.value = '❌ API 连接失败：' + error.message
  }
}

const clearCache = () => {
  localStorage.clear()
  sessionStorage.clear()
  debugMessage.value = '✅ 缓存已清除'
}

const showErrorTest = () => {
  throw new Error('这是一个测试错误')
}

onMounted(() => {
  loadSystemInfo()
  loadDatabaseStatus()
  loadUsers()
  loadAudioReview()
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* 左侧导航栏 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1e3a8a 0%, #3b82f6 100%);
  color: white;
  padding: 20px 0;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.logo p {
  margin: 5px 0 0;
  font-size: 12px;
  opacity: 0.8;
}

.menu {
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
  opacity: 0.8;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  opacity: 1;
}

.menu-item.active {
  background: rgba(255, 255, 255, 0.2);
  opacity: 1;
  border-left: 4px solid white;
}

.menu-item .icon {
  font-size: 20px;
  margin-right: 12px;
}

/* 主内容区 */
.main-content {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

/* 顶部栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome {
  color: #333;
  font-size: 14px;
}

.logout-btn {
  padding: 8px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #ff7875;
}

/* 内容区 */
.content {
  padding: 30px;
  flex: 1;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: white;
  margin-bottom: 30px;
}

.avatar {
  font-size: 48px;
}

.welcome-card h3 {
  margin: 0 0 10px;
  font-size: 20px;
  font-weight: 500;
}

.welcome-card p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  font-size: 36px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  border-radius: 12px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1e3a8a;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.info-content {
  padding: 24px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #666;
  font-size: 14px;
}

.info-row .value {
  color: #333;
  font-weight: 500;
}

.info-row .value.highlight {
  color: #3b82f6;
  font-weight: 600;
  font-size: 18px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.success {
  background: #e6f7e6;
  color: #52c41a;
}

.status-badge.danger {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 标签页 */
.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.tab-btn {
  padding: 10px 24px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.tab-btn:hover {
  color: #3b82f6;
  border-color: #3b82f6;
}

.tab-btn.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

/* 音频列表 */
.audio-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audio-item {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.audio-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.audio-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.audio-title h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.audio-meta {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.audio-desc {
  color: #666;
  font-size: 14px;
  margin: 16px 0;
  line-height: 1.6;
}

.audio-player {
  width: 100%;
  margin: 16px 0;
}

.audio-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.btn-approve,
.btn-reject {
  padding: 8px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-approve {
  background: #52c41a;
  color: white;
}

.btn-approve:hover {
  background: #73d13d;
}

.btn-reject {
  background: #ff4d4f;
  color: white;
}

.btn-reject:hover {
  background: #ff7875;
}

.rejection-reason {
  margin-top: 16px;
  padding: 12px 16px;
  background: #fff1f0;
  border-radius: 6px;
  color: #ff4d4f;
  font-size: 14px;
}

/* 表格 */
.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 16px 24px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.data-table td {
  color: #666;
  font-size: 14px;
}

.data-table tr:hover {
  background: #f5f5f5;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  margin-right: 8px;
  transition: all 0.3s;
}

.reset-btn {
  background: #1890ff;
  color: white;
}

.reset-btn:hover {
  background: #40a9ff;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

.delete-btn:hover {
  background: #ff7875;
}

.refresh-btn {
  padding: 10px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.refresh-btn:hover {
  background: #2563eb;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 40px;
}

/* 资源管理 */
.resource-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.resource-item {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.resource-info h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #333;
}

.resource-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.resource-status {
  display: flex;
  gap: 8px;
  align-items: center;
}

.delete-resource-btn {
  padding: 6px 12px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.delete-resource-btn:hover {
  background: #ff7875;
}

.add-btn,
.refresh-btn {
  padding: 10px 20px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 12px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.add-btn:hover,
.refresh-btn:hover {
  background: #73d13d;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.status-badge.success {
  background: #e6f7e6;
  color: #52c41a;
}

.status-badge.warning {
  background: #fff7e6;
  color: #fa8c16;
}

.resource-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-row label {
  min-width: 100px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.form-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-tip {
  display: block;
  margin-top: 6px;
  color: #999;
  font-size: 12px;
  line-height: 1.4;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.preview-btn,
.save-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.preview-btn {
  background: #1890ff;
  color: white;
}

.preview-btn:hover {
  background: #40a9ff;
}

.save-btn {
  background: #52c41a;
  color: white;
}

.save-btn:hover {
  background: #73d13d;
}

/* 调试工具 */
.debug-tools {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.debug-btn {
  padding: 10px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.debug-btn:hover {
  background: #2563eb;
}

.debug-message {
  margin-top: 20px;
  padding: 16px;
  background: #f0f2f5;
  border-radius: 8px;
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 400px;
  max-width: 90%;
}

.modal.large {
  width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #333;
}

.modal p {
  margin: 0 0 16px;
  color: #666;
  font-size: 14px;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.modal-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.modal-input.textarea {
  min-height: 80px;
  resize: vertical;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn,
.confirm-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.cancel-btn:hover {
  background: #d9d9d9;
}

.confirm-btn {
  background: #3b82f6;
  color: white;
}

.confirm-btn:hover {
  background: #2563eb;
}

/* 空状态 */
.empty {
  text-align: center;
  color: #999;
  padding: 60px 20px;
  font-size: 14px;
}

.loading {
  text-align: center;
  color: #999;
  padding: 60px 20px;
}
</style>
