<template>
  <div class="community-container">
    <div class="community-header">
      <h1>福建方言保护平台 - 交流互动</h1>
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

    <div class="community-content">
      <!-- 资源选择 -->
      <div class="resource-selector">
        <h3>选择评论资源</h3>
        <div class="selector-group">
          <label>资源类型：</label>
          <select v-model="selectedResourceType" @change="loadResources">
            <option value="dialect">学习资源</option>
            <option value="folk">文化资源</option>
          </select>
        </div>
        <div class="selector-group">
          <label>选择资源：</label>
          <select v-model="selectedResourceId" @change="loadComments">
            <option value="">请选择资源</option>
            <option v-for="resource in resources" :key="resource.id" :value="resource.id">
              {{ resource.title }}
            </option>
          </select>
        </div>
      </div>

      <!-- 发表评论 -->
      <div v-if="isLoggedIn" class="comment-form">
        <h3>发表评论</h3>
        <textarea 
          v-model="newComment" 
          placeholder="分享你的方言学习心得..."
          rows="4"
        ></textarea>
        <div class="form-actions">
          <button @click="submitComment" class="submit-btn" :disabled="!newComment.trim()">
            发表评论
          </button>
        </div>
      </div>

      <div v-else class="login-hint">
        <p>请先 <a href="#" @click.prevent="goToLogin">登录</a> 后发表评论</p>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <h3>评论列表 ({{ comments.length }})</h3>
        
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="comment-author">{{ comment.username }}</span>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <div class="comment-content">
            {{ comment.content }}
          </div>
          <div class="comment-footer">
            <button @click="likeComment(comment)" class="like-btn">
               {{ comment.likeCount }}
            </button>
            <button v-if="canDelete(comment)" @click="deleteCommentAction(comment.id)" class="delete-btn">
              删除
            </button>
          </div>
        </div>

        <div v-if="comments.length === 0" class="empty-state">
          <p>暂无评论，快来抢沙发吧！</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getComments, addComment, deleteComment, likeComment } from '@/api/comment'
import { getAllResources as getDialectResources } from '@/api/dialect'
import { getAllResources as getFolkResources } from '@/api/folk'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username || '用户')
const userId = computed(() => userStore.userId)

const selectedResourceType = ref('dialect')
const selectedResourceId = ref('')
const resources = ref([])
const comments = ref([])
const newComment = ref('')

onMounted(() => {
  loadResources()
})

const loadResources = async () => {
  try {
    let result
    if (selectedResourceType.value === 'dialect') {
      result = await getDialectResources()
    } else {
      result = await getFolkResources()
    }
    
    if (result.code === 200) {
      resources.value = result.data || []
    }
  } catch (error) {
    console.error('加载资源失败:', error)
  }
}

const loadComments = async () => {
  if (!selectedResourceId.value) {
    comments.value = []
    return
  }
  
  try {
    const result = await getComments(selectedResourceType.value, selectedResourceId.value)
    if (result.code === 200) {
      comments.value = result.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  try {
    const data = {
      userId: userId.value,
      username: username.value,
      content: newComment.value,
      resourceType: selectedResourceType.value,
      resourceId: selectedResourceId.value,
      parentId: null
    }
    
    const result = await addComment(data)
    if (result.code === 200) {
      newComment.value = ''
      await loadComments()
      alert('评论成功！')
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    alert('评论失败，请稍后重试')
  }
}

const likeCommentAction = async (comment) => {
  try {
    const result = await likeComment(comment.id)
    if (result.code === 200) {
      comment.likeCount++
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const deleteCommentAction = async (commentId) => {
  if (!confirm('确定要删除这条评论吗？')) return
  
  try {
    const result = await deleteComment(commentId)
    if (result.code === 200) {
      await loadComments()
      alert('删除成功')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请稍后重试')
  }
}

const canDelete = (comment) => {
  return userId.value && (String(userId.value) === String(comment.userId))
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < 7 * day) {
    return Math.floor(diff / day) + '天前'
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.community-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  padding-bottom: 40px;
}

.community-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.community-header h1 {
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
  color: #000000;
}

.back-btn:hover {
  transform: translateY(-2px);
}

.login-btn {
  background: white;
  color: #000000;
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

.community-content {
  padding: 40px;
  max-width: 1000px;
  margin: 0 auto;
}

.resource-selector {
  background: white;
  padding: 25px 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.resource-selector h3 {
  color: #333;
  margin: 0 0 20px 0;
  font-size: 18px;
}

.selector-group {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.selector-group:last-child {
  margin-bottom: 0;
}

.selector-group label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
  min-width: 100px;
}

.selector-group select {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.selector-group select:focus {
  outline: none;
  border-color: #000000;
}

.comment-form {
  background: white;
  padding: 25px 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.comment-form h3 {
  color: #333;
  margin: 0 0 15px 0;
  font-size: 18px;
}

.comment-form textarea {
  width: 100%;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
}

.comment-form textarea:focus {
  outline: none;
  border-color: #000000;
}

.form-actions {
  margin-top: 15px;
  text-align: right;
}

.submit-btn {
  padding: 10px 30px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-hint {
  background: white;
  padding: 25px 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.login-hint p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-hint a {
  color: #000000;
  text-decoration: none;
  font-weight: 600;
}

.login-hint a:hover {
  text-decoration: underline;
}

.comment-list {
  background: white;
  padding: 25px 30px;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.comment-list h3 {
  color: #333;
  margin: 0 0 20px 0;
  font-size: 18px;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-author {
  font-weight: 600;
  color: #000000;
  font-size: 14px;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  white-space: pre-wrap;
}

.comment-footer {
  display: flex;
  gap: 15px;
  align-items: center;
}

.like-btn,
.delete-btn {
  background: none;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  font-size: 13px;
  color: #666;
  border-radius: 4px;
  transition: all 0.2s;
}

.like-btn:hover {
  background: #f5f5f5;
  color: #000000;
}

.delete-btn:hover {
  background: #fff5f5;
  color: #dc3545;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}
</style>
