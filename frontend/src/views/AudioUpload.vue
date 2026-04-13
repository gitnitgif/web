<template>
  <div class="audio-upload-container">
    <div class="upload-header">
      <h1>️ 上传方言音频</h1>
      <p>分享您的方言声音，传承地方文化</p>
    </div>

    <div class="upload-form">
      <div class="form-group">
        <label>音频标题</label>
        <input 
          v-model="uploadForm.title" 
          type="text" 
          placeholder="请输入音频标题"
          required
        />
      </div>

      <div class="form-group">
        <label>选择城市</label>
        <select v-model="uploadForm.city" required>
          <option value="">请选择城市</option>
          <option v-for="city in cities" :key="city.code" :value="city.code">
            {{ city.name }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label>方言类型</label>
        <input 
          v-model="uploadForm.dialectType" 
          type="text" 
          placeholder="例如：福州话、闽南语等"
          required
        />
      </div>

      <div class="form-group">
        <label>音频描述</label>
        <textarea 
          v-model="uploadForm.description" 
          placeholder="请描述音频内容（可选）"
          rows="4"
        ></textarea>
      </div>

      <div class="form-group">
        <label>上传音频文件</label>
        <div class="upload-area" 
             @dragover.prevent 
             @drop.prevent="handleDrop"
             @click="triggerFileInput">
          <input 
            ref="fileInput" 
            type="file" 
            accept=".mp3,audio/mpeg" 
            @change="handleFileChange"
            style="display: none"
          />
          <div v-if="!uploadForm.file" class="upload-placeholder">
            <p>点击或拖拽上传 MP3 文件</p>
            <p class="upload-hint">文件大小不超过 10MB</p>
          </div>
          <div v-else class="file-info">
            <div class="file-details">
              <div class="file-name">{{ uploadForm.file.name }}</div>
              <div class="file-size">{{ formatFileSize(uploadForm.file.size) }}</div>
            </div>
            <button class="remove-btn" @click.stop="removeFile">×</button>
          </div>
        </div>
      </div>

      <div class="form-error" v-if="uploadError">{{ uploadError }}</div>

      <button class="submit-btn" @click="submitUpload" :disabled="isUploading">
        {{ isUploading ? '上传中...' : '提交审核' }}
      </button>
    </div>

    <div class="upload-tips">
      <h3>上传须知</h3>
      <ul>
        <li>仅支持 MP3 格式音频文件</li>
        <li>文件大小不超过 10MB</li>
        <li>上传的音频需要管理员审核后才能展示</li>
        <li>请确保上传的内容符合法律法规</li>
        <li>鼓励上传清晰、有代表性的方言录音</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { uploadAudio } from '@/api/audio'

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

const uploadForm = ref({
  title: '',
  city: '',
  dialectType: '',
  description: '',
  file: null
})

const fileInput = ref(null)
const isUploading = ref(false)
const uploadError = ref('')

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  validateAndSetFile(file)
}

const handleDrop = (event) => {
  const file = event.dataTransfer.files[0]
  validateAndSetFile(file)
}

const validateAndSetFile = (file) => {
  if (!file) return
  
  if (file.type !== 'audio/mpeg' && !file.name.endsWith('.mp3')) {
    uploadError.value = '仅支持 MP3 格式文件'
    return
  }
  
  if (file.size > 10 * 1024 * 1024) {
    uploadError.value = '文件大小不能超过 10MB'
    return
  }
  
  uploadError.value = ''
  uploadForm.value.file = file
}

const removeFile = () => {
  uploadForm.value.file = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

const submitUpload = async () => {
  uploadError.value = ''
  
  if (!uploadForm.value.title) {
    uploadError.value = '请输入音频标题'
    return
  }
  
  if (!uploadForm.value.city) {
    uploadError.value = '请选择城市'
    return
  }
  
  if (!uploadForm.value.dialectType) {
    uploadError.value = '请输入方言类型'
    return
  }
  
  if (!uploadForm.value.file) {
    uploadError.value = '请上传音频文件'
    return
  }
  
  isUploading.value = true
  
  try {
    const formData = new FormData()
    formData.append('title', uploadForm.value.title)
    formData.append('cityCode', uploadForm.value.city)
    formData.append('dialectType', uploadForm.value.dialectType)
    formData.append('description', uploadForm.value.description)
    formData.append('file', uploadForm.value.file)
    formData.append('uploader', userStore.username)
    formData.append('uploaderId', userStore.userId)
    
    await uploadAudio(formData)
    
    alert('音频上传成功！请等待管理员审核。')
    router.push('/audio-gallery')
  } catch (error) {
    console.error('上传失败:', error)
    alert('上传失败：' + (error.response?.data?.message || error.message || '请稍后重试'))
  } finally {
    isUploading.value = false
  }
}
</script>

<style scoped>
.audio-upload-container {
  min-height: 100vh;
  padding: 100px 20px 40px;
  background-image: url('/background1.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.upload-header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.upload-header h1 {
  font-size: 36px;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.upload-header p {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
}

.upload-form {
  max-width: 600px;
  margin: 0 auto 40px;
  background: white;
  padding: 40px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.upload-area {
  border: 2px dashed #ccc;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f9f9f9;
}

.upload-area:hover {
  border-color: #667eea;
  background: #f0f4ff;
}

.upload-placeholder {
  color: #666;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.upload-hint {
  font-size: 14px;
  color: #999;
  margin-top: 8px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.file-icon {
  font-size: 36px;
}

.file-details {
  flex: 1;
  text-align: left;
}

.file-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.file-size {
  font-size: 14px;
  color: #666;
}

.remove-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #ff4444;
  color: white;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-btn:hover {
  background: #cc0000;
  transform: scale(1.1);
}

.form-error {
  color: #ff4444;
  font-size: 14px;
  margin-bottom: 16px;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.upload-tips {
  max-width: 600px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  padding: 30px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
}

.upload-tips h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 16px 0;
}

.upload-tips ul {
  margin: 0;
  padding-left: 24px;
}

.upload-tips li {
  font-size: 15px;
  color: #666;
  line-height: 2;
}
</style>
