<template>
  <div class="home-container">
    <!-- 固定导航栏 -->
    <nav class="fixed-navbar">
      <div class="navbar-content">
        <h1 class="navbar-title">方言传承</h1>
        <div class="user-info">
          <template v-if="isLoggedIn">
            <div class="user-dropdown" @mouseenter="showUserMenu = true" @mouseleave="showUserMenu = false">
              <img src="/default.png" alt="用户" class="user-avatar" />
              
              <!-- 用户下拉菜单 -->
              <div v-if="showUserMenu" class="user-menu">
                <div class="user-menu-header">
                  <div class="user-menu-avatar">
                    <img src="/default.png" alt="用户" />
                  </div>
                  <div class="user-menu-info">
                    <div class="user-menu-name">{{ username }}</div>
                    <div class="user-menu-role">{{ userRole === 'ADMIN' ? '管理员' : '普通用户' }}</div>
                  </div>
                </div>
                
                <div class="user-menu-divider"></div>
                
                <div class="user-menu-item" @click="goToProfile">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="8" r="4"/>
                    <path d="M20 21a8 8 0 1 0-16 0"/>
                  </svg>
                  <span>个人信息</span>
                </div>
                
                <div v-if="userRole === 'ADMIN'" class="user-menu-item" @click="goToAdmin">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                    <path d="M3 9h18"/>
                    <path d="M9 21V9"/>
                  </svg>
                  <span>管理后台</span>
                </div>
                
                <div class="user-menu-divider"></div>
                
                <div class="user-menu-item logout" @click="handleLogout">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                    <polyline points="16 17 21 12 16 7"/>
                    <line x1="21" y1="12" x2="9" y2="12"/>
                  </svg>
                  <span>退出登录</span>
                </div>
              </div>
            </div>
            <button v-if="userRole === 'ADMIN'" @click="goToAdmin" class="admin-btn">管理后台</button>
          </template>
          <template v-else>
            <button @click="showAuthModal = true" class="auth-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="8" r="4"/>
                <path d="M20 21a8 8 0 1 0-16 0"/>
              </svg>
            </button>
          </template>
        </div>
      </div>
    </nav>

    <!-- 第一屏：背景图界面 -->
    <section class="hero-section" id="heroSection">

      
      <div class="hero-background">
        <img src="/back.png" alt="背景" class="hero-image" />
      </div>
      <div class="hero-content">
        <p class="hero-subtitle">传承方言文化<br>保护语言多样性</p>
      </div>
    </section>

    <!-- 第二屏：地图界面 -->
    <section class="map-interface" id="mapSection">
      <div class="map-container">
        <h2 class="section-title">福建省方言分布地图</h2>
        <p class="section-subtitle">鼠标悬停查看各城市方言特色</p>
        <div class="map-wrapper" v-html="svgContent"></div>
        
        <!-- 悬停提示框 -->
        <div v-if="hoveredCity" class="map-tooltip" :style="tooltipStyle">
          <div class="tooltip-title">{{ getCityTitle(hoveredCity) }}</div>
          <div class="tooltip-desc">{{ getCityDesc(hoveredCity) }}</div>
          <div class="tooltip-highlight">{{ getCityHighlight(hoveredCity) }}</div>
        </div>
      </div>
    </section>


    <!-- 第五屏：方言资源展示 - 无限滚动卡片 -->
    <section class="folk-section" id="folkSection">
      <!-- 资源详情弹窗 -->
      <div v-if="showResourceDetail" class="resource-modal" @click="closeResourceDetail">
        <div class="resource-modal-content" @click.stop>
          <button class="modal-close-btn" @click="closeResourceDetail">×</button>
          <div class="resource-detail">
            <div class="resource-header" :style="{ backgroundImage: `url(${selectedCityResource?.bgImage})` }">
              <h2>{{ selectedCityResource?.name }}</h2>
              <p>{{ selectedCityResource?.dialect }}</p>
            </div>
            <div class="resource-body">
              <h3>{{ selectedCityResource?.resource }}</h3>
              <div class="resource-intro">
                <p>这是{{ selectedCityResource?.name }}地区的特色方言资源，包含了丰富的文化内涵。</p>
                <p>通过学习和保护这些珍贵的方言资源，我们可以更好地传承和弘扬地方文化。</p>
              </div>
              <div class="resource-actions">
                <button class="resource-btn primary" @click="playAudio(selectedCityResource)">
                  <svg v-if="!isPlaying" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="5 3 19 12 5 21 5 3"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="6" y="4" width="4" height="16"/>
                    <rect x="14" y="4" width="4" height="16"/>
                  </svg>
                  <span>{{ isPlaying ? '暂停播放' : '播放音频' }}</span>
                </button>
                <button class="resource-btn" @click="downloadResource(selectedCityResource)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" y1="15" x2="12" y2="3"/>
                  </svg>
                  <span>下载资源</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="gallery">
        <ul class="cards">
          <li class="card" v-for="(city, index) in allCityCards" :key="index" :data-city="city.code" @click="showResource(city.code)">
            <div class="card-inner">
              <div class="card-bg" :style="{ backgroundImage: `url(${city.bgImage})` }"></div>
              <div class="card-content">
                <div class="card-city">{{ city.name }}</div>
                <div class="card-dialect">{{ city.dialect }}</div>
                <div class="card-resource">{{ city.resource }}</div>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <!-- 控制按钮 -->
      <div class="controls">
        <button class="control-btn" @click="scrollPrev">
          <span>Prev</span>
        </button>
        <button class="control-btn" @click="scrollNext">
          <span>Next</span>
        </button>
      </div>
    </section>
    <!-- 第四屏：方言学习 -->
    <section class="learning-section" id="learningSection">
      <div class="learning-full-container">
        <div class="learning-header">
          <h2 class="section-title">方言学习</h2>
          <p class="section-desc">系统学习福建各地方言，从基础词汇到日常对话</p>
        </div>

        <!-- 学习卡片展示 -->
        <div class="learning-cards-container">
          <div class="learning-card" v-for="item in learningCards" :key="item.id">
            <div class="card-top">
              <span class="badge category">{{ item.category }}</span>
              <span class="badge level">{{ item.level }}</span>
            </div>
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-text">{{ item.text }}</p>
            <div class="card-bottom">
              <span class="location">{{ item.city }}</span>
              <button class="card-play-btn" @click="playLearningAudio(item)" :class="{ playing: isLearningPlaying(item) }">
                <svg v-if="isLearningPlaying(item)" viewBox="0 0 24 24" fill="currentColor" stroke="none" width="18" height="18">
                  <rect x="6" y="4" width="4" height="16"/>
                  <rect x="14" y="4" width="4" height="16"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="currentColor" stroke="none" width="18" height="18">
                  <polygon points="5 3 19 12 5 21 5 3"/>
                </svg>
                <span>{{ isLearningPlaying(item) ? '暂停' : '播放' }}</span>
              </button>
            </div>
          </div>
        </div>

        <div class="learning-footer">
          <button class="btn-more" @click="goToLearning">
            进入完整学习系统 →
          </button>
        </div>
      </div>
    </section>

    <!-- 第七屏：方言音频库 -->
    <section class="audio-section" id="audioSection">
      <div class="audio-full-container">
        <div class="audio-header">
          <div class="header-content">
            <h1 class="audio-title">方言音频库</h1>
            <p class="audio-subtitle">聆听乡音 · 分享故事 · 传承文化</p>
          </div>
          <button class="upload-btn" @click="goToAudioUpload">
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

        <div class="audio-list" v-if="audioLoading">
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
              <div class="audio-details">
                <h3 class="audio-title">{{ audio.title }}</h3>
                <div class="audio-meta">
                  <span class="audio-tag">{{ getCityName(audio.city) }}</span>
                  <span class="audio-tag">{{ audio.dialectType }}</span>
                  <span class="audio-tag">上传者：{{ audio.uploader }}</span>
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
                  <span class="star">{{ audio.userRated ? '' : '☆' }}</span>
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
    </section>

    <!-- 第六屏：在线测试展示 -->
    <section class="quiz-section" id="quizSection">
      <!-- 介绍界面 -->
      <div v-if="!showQuizInterface && !isTransitioning" class="quiz-intro-content">
        <div class="intro-header">
          <h2 ref="quizTitle" class="intro-title">方言水平测试</h2>
          <p ref="quizSubtitle" class="intro-subtitle">探索方言魅力 · 传承语言文化 · 测试你的方言能力</p>
        </div>
        
        <div class="intro-features">
          <div class="feature-card">
            <h3>城市选择</h3>
            <p>福建九地市方言全覆盖</p>
          </div>
          <div class="feature-card">
            <h3>灵活题量</h3>
            <p>5/10/20 题自由选择</p>
          </div>
          <div class="feature-card">
            <h3>水平评估</h3>
            <p>专业评级系统</p>
          </div>
        </div>
        
        <button ref="quizStartBtn" class="start-quiz-btn" @click="startQuizWithAnimation">
          <span class="btn-text">立即开始测试</span>
        </button>
        
        <div class="intro-stats">
          <div class="stat-item">
            <span class="stat-number">9</span>
            <span class="stat-label">城市</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">500+</span>
            <span class="stat-label">题库</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">4</span>
            <span class="stat-label">等级</span>
          </div>
        </div>
      </div>

      <!-- 过渡动画层 -->
      <div v-if="isTransitioning" class="transition-overlay">
        <div class="loading-content">
          <div class="loading-spinner"></div>
          <div class="loading-text">正在加载题目...</div>
        </div>
        <svg class="transition-svg" viewBox="0 0 100 100" preserveAspectRatio="xMidYMin slice">
          <defs>
            <linearGradient id="transitionGrad" x1="0" y1="0" x2="99" y2="99" gradientUnits="userSpaceOnUse">
              <stop offset="0.2" stop-color="rgb(255, 135, 9)" />
              <stop offset="0.7" stop-color="rgb(247, 189, 248)" />
            </linearGradient>
          </defs>
          <path ref="pathRef" class="transition-path" stroke="url(#transitionGrad)" fill="url(#transitionGrad)" stroke-width="2px" vector-effect="non-scaling-stroke" d="M 0 100 V 100 Q 50 100 100 100 V 100 z"/>
        </svg>
      </div>

      <!-- 答题界面 -->
      <div v-if="showQuizInterface" class="quiz-fullscreen">
        <!-- 选择城市 -->
        <div v-if="!quizStarted" class="setup-section">
          <div class="setup-card">
            <div class="setup-header">

              <h3>测试设置</h3>
              <p>选择你要测试的城市和题量</p>
            </div>
            
            <div class="form-group">
              <label class="form-label">
                选择城市：
              </label>
              <div class="city-selector">
                <select v-model="selectedCity" class="custom-select">
                  <option value="">请选择城市</option>
                  <option v-for="city in cities" :key="city.code" :value="city.code">
                    {{ city.name }}
                  </option>
                </select>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">

                题目数量：
              </label>
              <div class="question-count-selector">
                <button 
                  v-for="count in [5, 10, 20]" 
                  :key="count"
                  :class="['count-btn', { active: questionCount === count.toString() }]"
                  @click="questionCount = count.toString()"
                >
                  {{ count }}题
                </button>
              </div>
            </div>

            <button 
              @click="startQuiz" 
              class="start-quiz-btn"
              :disabled="!selectedCity"
            >
              <span class="btn-text">开始测试</span>
            </button>

            <!-- 历史记录 -->
            <div v-if="isLoggedIn && records.length > 0" class="records-section">
              <h4 class="records-title">我的测试记录</h4>
              <div class="records-list">
                <div v-for="record in records" :key="record.id" class="record-item">
                  <div class="record-header">
                    <span class="record-city">{{ getCityName(record.cityCode) }}</span>
                    <span class="record-score">{{ record.score }}分</span>
                  </div>
                  <div class="record-body">
                    <div class="record-stat">
                      <span class="stat-label">正确率</span>
                      <span class="stat-value">{{ Math.round((record.correctCount / record.totalQuestions) * 100) }}%</span>
                    </div>
                    <div class="record-stat">
                      <span class="stat-label">答对</span>
                      <span class="stat-value">{{ record.correctCount }}/{{ record.totalQuestions }}</span>
                    </div>
                    <span class="record-time">{{ formatTime(record.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 答题界面 -->
        <div v-else-if="!showResult" class="quiz-content-wrapper">
          <div class="quiz-header-simple">
            <button @click="showQuizInterface = false" class="back-to-intro-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              <span>退出测试</span>
            </button>
            <div class="quiz-info">
              <span class="quiz-city-label">{{ getCityName(selectedCity) }}</span>
              <span class="quiz-progress-text">{{ currentQuestionIndex + 1 }}/{{ quizzes.length }}</span>
            </div>
          </div>

          <div class="quiz-progress">
            <div class="progress-bar">
              <div class="progress" :style="{ width: progressPercent + '%' }"></div>
            </div>
          </div>

          <div class="question-container">
            <Transition name="slide-fade" mode="out-in">
              <div :key="currentQuestionIndex" class="question-card" v-if="currentQuiz && currentQuiz.id">
                <div class="question-header">
                  <div class="question-meta">
                    <span class="question-category">{{ currentQuiz.category || '未知' }}</span>
                    <span class="question-difficulty">{{ ''.repeat(currentQuiz.difficulty || 1) }}</span>
                  </div>
                  <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
                </div>
                
                <h4 class="question-title">{{ currentQuiz.question || '加载中...' }}</h4>
                
                <div class="options">
                  <div 
                    v-for="option in currentQuizOptions" 
                    :key="option.key"
                    :class="['option-item', { 
                      selected: userAnswers[currentQuiz.id] === option.key,
                      correct: userAnswers[currentQuiz.id] && currentQuiz.answer === option.key,
                      wrong: userAnswers[currentQuiz.id] && userAnswers[currentQuiz.id] === option.key && currentQuiz.answer !== option.key,
                      locked: userAnswers[currentQuiz.id]
                    }]"
                    @click="selectAnswer(currentQuiz.id, option.key)"
                  >
                    <span class="option-key">{{ option.key }}</span>
                    <span class="option-text">{{ option.value }}</span>
                    <span v-if="userAnswers[currentQuiz.id] && currentQuiz.answer === option.key" class="status-icon">✓</span>
                    <span v-if="userAnswers[currentQuiz.id] && userAnswers[currentQuiz.id] === option.key && currentQuiz.answer !== option.key" class="status-icon">✕</span>
                  </div>
                </div>

                <div v-if="userAnswers[currentQuiz.id]" class="explanation">
                  <div :class="['result-tip', userAnswers[currentQuiz.id] === currentQuiz.answer ? 'correct' : 'wrong']">
                    <span class="tip-icon">{{ userAnswers[currentQuiz.id] === currentQuiz.answer ? '✓' : '✕' }}</span>
                    <span class="tip-text">{{ userAnswers[currentQuiz.id] === currentQuiz.answer ? '回答正确！' : '回答错误！' }}</span>
                  </div>
                  <div class="explanation-text">
                    <strong>解析：</strong>{{ currentQuiz.explanation || '暂无解析' }}
                  </div>
                </div>
              </div>
            </Transition>
          </div>

          <div class="quiz-actions">
            <button @click="prevQuestion" :disabled="currentQuestionIndex === 0" class="action-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              <span>上一题</span>
            </button>
            <button @click="nextQuestion" class="action-btn primary">
              <span>{{ currentQuestionIndex === quizzes.length - 1 ? '提交试卷' : '下一题' }}</span>
              <svg v-if="currentQuestionIndex !== quizzes.length - 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 结果展示 -->
        <div v-if="showResult" class="result-section">
          <div ref="resultCard" class="result-card">
            <div class="result-header">

              <h3>测试完成！</h3>
            </div>
            
            <div class="result-score">
              <div class="score-circle">
                <svg viewBox="0 0 100 100" class="score-svg">
                  <circle class="score-bg" cx="50" cy="50" r="45"></circle>
                  <circle class="score-fill" cx="50" cy="50" r="45" 
                    :stroke-dasharray="283" 
                    :stroke-dashoffset="283 * (1 - result.score / 100)"></circle>
                </svg>
                <div class="score-number">{{ result.score }}</div>
              </div>
              <div class="score-label">总分</div>
            </div>
            
            <div class="result-detail">
              <div class="detail-item">
                <div class="detail-icon">✓</div>
                <span class="detail-label">正确题数</span>
                <span class="detail-value">{{ result.correctCount }}/{{ result.totalQuestions }}</span>
              </div>
              <div class="detail-item">

                <span class="detail-label">准确率</span>
                <span class="detail-value">{{ result.accuracy }}%</span>
              </div>
              <div class="detail-item">

                <span class="detail-label">水平等级</span>
                <span class="detail-value level">{{ result.level }}</span>
              </div>
            </div>
            
            <div class="result-message">
              <div class="message-icon">{{ getResultMessageIcon(result.level) }}</div>
              <p>{{ getResultMessage(result.level) }}</p>
            </div>
            
            <div class="result-actions">
              <button @click="showQuizInterface = false" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 12"/>
                  <path d="M3 3v9h9"/>
                </svg>
                <span>返回主页</span>
              </button>
              <button @click="restartQuiz" class="action-btn primary">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 4v6h6"/>
                  <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/>
                </svg>
                <span>再测一次</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 登录注册弹窗 -->
    <div v-if="showAuthModal" class="modal-overlay" @click="closeAuthModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ authMode === 'login' ? '用户登录' : '用户注册' }}</h2>
          <button class="close-btn" @click="closeAuthModal">×</button>
        </div>
        <div class="modal-body">
          <form v-if="authMode === 'login'" @submit.prevent="handleLogin">
            <div class="form-group">
              <label>用户名</label>
              <input 
                v-model="loginForm.username" 
                type="text" 
                placeholder="请输入用户名"
                required
              />
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
                required
              />
            </div>
            <div class="form-error" v-if="loginError">{{ loginError }}</div>
            <button type="submit" class="submit-btn">登录</button>
          </form>
          
          <form v-else @submit.prevent="handleRegister">
            <div class="form-group">
              <label>用户名</label>
              <input 
                v-model="registerForm.username" 
                type="text" 
                placeholder="请输入用户名"
                required
              />
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码"
                required
              />
            </div>
            <div class="form-group">
              <label>确认密码</label>
              <input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                required
              />
            </div>
            <div class="form-error" v-if="registerError">{{ registerError }}</div>
            <button type="submit" class="submit-btn">注册</button>
          </form>
          
          <div class="modal-footer">
            <span v-if="authMode === 'login'">
              还没有账号？<a href="#" @click.prevent="switchToRegister">立即注册</a>
            </span>
            <span v-else>
              已有账号？<a href="#" @click.prevent="switchToLogin">立即登录</a>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { gsap } from 'gsap'
import { MorphSVGPlugin } from 'gsap/MorphSVGPlugin'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getRandomQuizzes, submitQuiz, getRecords } from '@/api/quiz'
import { getAudioList, rateAudio, addComment } from '@/api/audio'
import { getAllResources } from '@/api/resource'

// 注册 ScrollTrigger 插件
gsap.registerPlugin(ScrollTrigger, MorphSVGPlugin)

// 获取 SVG 路径
const pathRef = ref(null)
let tl = null

// 动画路径
const start = 'M 0 100 V 50 Q 50 0 100 50 V 100 z'
const end = 'M 0 100 V 0 Q 50 0 100 0 V 100 z'

// 初始化 GSAP 动画时间轴
const initTimeline = () => {
  if (!pathRef.value) {
    console.error(' pathRef 未找到')
    return false
  }

  console.log(' 创建 GSAP 时间轴')
  
  try {
    tl = gsap.timeline({
      paused: true,
      onComplete: () => {
        console.log(' Timeline 动画完成回调触发！')
        isTransitioning.value = false
        showQuizInterface.value = true
        console.log(' 状态更新 - isTransitioning:', isTransitioning.value, 'showQuizInterface:', showQuizInterface.value)
      }
    })
    
    tl.to(pathRef.value, {
      morphSVG: {
        shape: start,
        shapeIndex: 0,
        type: 'cubic',
        precision: 4
      },
      duration: 0.8,
      ease: 'power2.inOut',
      onStart: () => console.log('第一阶段开始'),
      onComplete: () => console.log('第一阶段完成')
    })
    
    tl.to(pathRef.value, {
      morphSVG: {
        shape: end,
        shapeIndex: 0,
        type: 'cubic',
        precision: 4
      },
      duration: 0.8,
      ease: 'power2.inOut',
      onStart: () => console.log('第二阶段开始'),
      onComplete: () => console.log('第二阶段完成')
    })
    
    console.log('GSAP 时间轴创建成功，总时长:', tl.duration().toFixed(2), '秒')
    console.log('时间轴状态 - paused:', tl.paused(), 'reversed:', tl.reversed())
    return true
    
  } catch (error) {
    console.error('GSAP 动画创建失败:', error)
    return false
  }
}


const startQuizWithAnimation = async () => {
  console.log('点击开始按钮')
  console.log('当前状态 - isTransitioning:', isTransitioning.value, 'showQuizInterface:', showQuizInterface.value)
  
  isTransitioning.value = true

  await nextTick()
  
  console.log('DOM 更新完成，准备播放动画')
  
  if (tl) {
    console.log('播放动画')
    console.log('播放前状态 - paused:', tl.paused(), 'reversed:', tl.reversed(), 'progress:', tl.progress())
    
  
    tl.restart()
    
    console.log('动画已重启')
    console.log('播放后状态 - paused:', tl.paused(), 'reversed:', tl.reversed())
  } else {
    console.error('时间轴 tl 为 null')
    // 如果 tl 不存在，直接显示答题界面
    setTimeout(() => {
      isTransitioning.value = false
      showQuizInterface.value = true
      console.log('使用备用方案，直接显示答题界面')
    }, 500)
  }
}

const router = useRouter()
const userStore = useUserStore()


const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username || '用户')
const userRole = computed(() => userStore.role || 'USER')
const userId = computed(() => userStore.userId || 0)
const hoveredCity = ref(null)
const svgContent = ref('') 
const tooltipPos = ref({ left: '0px', top: '0px' }) 

// 图层引用
const uiLayer = ref(null)
const heroMask = ref(null)

// 弹窗控制
const showAuthModal = ref(false)
const authMode = ref('login') // 'login' 或 'register'
const showUserMenu = ref(false) // 用户菜单显示状态
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', confirmPassword: '' })
const loginError = ref('')
const registerError = ref('')

// 在线测试相关
const quizTitle = ref(null)
const quizSubtitle = ref(null)
const quizStartBtn = ref(null)

// Quiz 相关变量
const showQuizInterface = ref(false)
const isTransitioning = ref(false)
const quizStarted = ref(false)
const selectedCity = ref('')
const questionCount = ref(10)
const quizzes = ref([])
const userAnswers = ref({})
const currentQuestionIndex = ref(0)
const showResult = ref(false)
const result = ref({
  score: 0,
  correctCount: 0,
  totalQuestions: 0,
  accuracy: 0,
  level: ''
})
const records = ref([])
const resultCard = ref(null)

// 资源详情弹窗相关
const showResourceDetail = ref(false)
const selectedCityResource = ref(null)
const isPlaying = ref(false)
const currentAudio = ref(null)
const cityResources = ref([])

// 学习卡片音频播放相关
const learningAudios = ref({})

// 播放学习卡片音频（切换播放/暂停）
const playLearningAudio = (item) => {
  if (!item || !item.audio) {
    alert('该卡片暂无音频')
    return
  }
  
  // 如果当前卡片正在播放，则暂停
  if (learningAudios.value[item.id]) {
    learningAudios.value[item.id].pause()
    delete learningAudios.value[item.id]
    return
  }
  
  // 先停止其他所有正在播放的音频
  Object.keys(learningAudios.value).forEach(key => {
    if (learningAudios.value[key]) {
      learningAudios.value[key].pause()
      delete learningAudios.value[key]
    }
  })
  
  const audio = new Audio(item.audio)
  
  // 先绑定事件，再播放
  audio.addEventListener('ended', () => {
    delete learningAudios.value[item.id]
  })
  
  audio.addEventListener('error', (error) => {
    console.error('音频加载失败:', error)
    console.error('音频 URL:', item.audio)
    let errorMsg = '无法加载音频'
    if (audio.error) {
      if (audio.error.code === 4) {
        errorMsg = '不支持的音频格式'
      } else if (audio.error.code === 3) {
        errorMsg = '音频解码失败'
      } else if (audio.error.code === 2) {
        errorMsg = '网络错误，请检查网络连接'
      } else if (audio.error.code === 1) {
        errorMsg = '音频加载被中止'
      }
    }
    alert(errorMsg + '\nURL: ' + item.audio)
    delete learningAudios.value[item.id]
  })
  
  audio.play()
    .then(() => {
      learningAudios.value[item.id] = audio
    })
    .catch(error => {
      console.error('播放失败:', error)
      console.error('音频 URL:', item.audio)
      let errorMsg = '播放失败'
      if (error.name === 'NotAllowedError') {
        errorMsg = '浏览器阻止自动播放，请点击播放按钮'
      } else if (error.name === 'NotSupportedError') {
        errorMsg = '不支持的音频格式'
      } else {
        errorMsg = '无法加载音频，请检查网络或 URL 是否正确'
      }
      alert(errorMsg + '\nURL: ' + item.audio)
      delete learningAudios.value[item.id]
    })
}

// 检查学习卡片是否正在播放
const isLearningPlaying = (item) => {
  const audio = learningAudios.value[item.id]
  if (audio) {
    // 如果音频已被暂停或删除，则返回 false
    if (audio.paused || audio.ended) {
      delete learningAudios.value[item.id]
      return false
    }
    return true
  }
  return false
}

// 学习资源相关
const currentCategory = ref('all')
const currentDifficulty = ref('all')
const learningResources = ref([])

// 加载资源列表
const loadCityResources = async () => {
  try {
    const res = await getAllResources()
    if (res.code === 200 && res.data && res.data.length > 0) {
      cityResources.value = res.data
    } else {
      // 如果 API 没有数据，使用默认数据
      cityResources.value = [
        { code: 'fuzhou', name: '福州', dialect: '福州话', resource: '福州歌谣', bgImage: '/fuzhou.png', audioUrl: '', downloadUrl: '' },
        { code: 'xiamen', name: '厦门', dialect: '闽南语', resource: '闽南童谣', bgImage: '/xiamen.png', audioUrl: '', downloadUrl: '' },
        { code: 'quanzhou', name: '泉州', dialect: '泉州话', resource: '高甲戏', bgImage: '/quanzhou.png', audioUrl: '', downloadUrl: '' },
        { code: 'zhangzhou', name: '漳州', dialect: '漳州话', resource: '漳州谚语', bgImage: '/zhangzhou.png', audioUrl: '', downloadUrl: '' },
        { code: 'putian', name: '莆田', dialect: '莆仙话', resource: '莆仙戏', bgImage: '/putian.png', audioUrl: '', downloadUrl: '' },
        { code: 'longyan', name: '龙岩', dialect: '客家话', resource: '客家山歌', bgImage: '/longyan.png', audioUrl: '', downloadUrl: '' },
        { code: 'sanming', name: '三明', dialect: '闽中话', resource: '三明故事', bgImage: '/sanming.png', audioUrl: '', downloadUrl: '' },
        { code: 'nanping', name: '南平', dialect: '闽北话', resource: '闽北故事', bgImage: '/nanping.png', audioUrl: '', downloadUrl: '' },
        { code: 'ningde', name: '宁德', dialect: '闽东话', resource: '宁德渔歌', bgImage: '/ningde.png', audioUrl: '', downloadUrl: '' }
      ]
    }
  } catch (error) {
    console.error('加载资源失败:', error)
    // 加载失败时使用默认数据
    cityResources.value = [
      { code: 'fuzhou', name: '福州', dialect: '福州话', resource: '福州歌谣', bgImage: '/fuzhou.png', audioUrl: '', downloadUrl: '' },
      { code: 'xiamen', name: '厦门', dialect: '闽南语', resource: '闽南童谣', bgImage: '/xiamen.png', audioUrl: '', downloadUrl: '' },
      { code: 'quanzhou', name: '泉州', dialect: '泉州话', resource: '高甲戏', bgImage: '/quanzhou.png', audioUrl: '', downloadUrl: '' },
      { code: 'zhangzhou', name: '漳州', dialect: '漳州话', resource: '漳州谚语', bgImage: '/zhangzhou.png', audioUrl: '', downloadUrl: '' },
      { code: 'putian', name: '莆田', dialect: '莆仙话', resource: '莆仙戏', bgImage: '/putian.png', audioUrl: '', downloadUrl: '' },
      { code: 'longyan', name: '龙岩', dialect: '客家话', resource: '客家山歌', bgImage: '/longyan.png', audioUrl: '', downloadUrl: '' },
      { code: 'sanming', name: '三明', dialect: '闽中话', resource: '三明故事', bgImage: '/sanming.png', audioUrl: '', downloadUrl: '' },
      { code: 'nanping', name: '南平', dialect: '闽北话', resource: '闽北故事', bgImage: '/nanping.png', audioUrl: '', downloadUrl: '' },
      { code: 'ningde', name: '宁德', dialect: '闽东话', resource: '宁德渔歌', bgImage: '/ningde.png', audioUrl: '', downloadUrl: '' }
    ]
  }
}

// 播放/暂停音频（切换）
const playAudio = (resource) => {
  if (!resource || !resource.audioUrl) {
    alert('该资源暂无音频')
    return
  }
  
  // 如果正在播放当前资源，则暂停
  if (isPlaying.value && currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
    isPlaying.value = false
    return
  }
  
  // 如果有其他音频在播放，先停止
  if (currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
  }
  
  const audio = new Audio(resource.audioUrl)
  audio.play()
    .then(() => {
      currentAudio.value = audio
      isPlaying.value = true
      
      audio.addEventListener('ended', () => {
        isPlaying.value = false
        currentAudio.value = null
      })
      
      audio.addEventListener('error', (error) => {
        console.error('播放失败:', error)
        let errorMsg = '播放失败'
        if (error.target.error && error.target.error.code === 4) {
          errorMsg = '不支持的音频格式'
        } else {
          errorMsg = '无法加载音频，请检查网络或 URL 是否正确'
        }
        alert(errorMsg)
        isPlaying.value = false
        currentAudio.value = null
      })
    })
    .catch(error => {
      console.error('播放失败:', error)
      let errorMsg = '播放失败'
      if (error.name === 'NotAllowedError') {
        errorMsg = '浏览器阻止自动播放，请点击播放按钮'
      } else if (error.name === 'NotSupportedError') {
        errorMsg = '不支持的音频格式'
      } else {
        errorMsg = '无法加载音频，请检查网络或 URL 是否正确'
      }
      alert(errorMsg)
      isPlaying.value = false
      currentAudio.value = null
    })
}

// 下载资源
const downloadResource = (resource) => {
  if (!resource || !resource.downloadUrl) {
    alert('该资源暂无下载链接')
    return
  }
  
  const link = document.createElement('a')
  link.href = resource.downloadUrl
  link.download = `${resource.name}-${resource.resource}.mp3`
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 音频库相关
const audioLoading = ref(true)
const audios = ref([])

// 学习卡片数据
const learningCards = ref([
  {
    id: 1,
    title: '福州话问候语',
    city: '福州',
    category: '词汇',
    level: '初级',
    text: '早上好 - 早势好\n你好 - 汝好\n谢谢 - 多谢\n对不起 - 对不起\n再见 - 再见',
    audio: '/uploads/audio/fuzhou.mp3'
  },
  {
    id: 2,
    title: '福州话数字',
    city: '福州',
    category: '词汇',
    level: '初级',
    text: '一 - siok\n二 - no\n三 - sa\n四 - sei\n五 - ngo\n六 - lok\n七 - ts\'ik\n八 - pik\n九 - kau\n十 - sip',
    audio: '/uploads/audio/fuzhou.mp3'
  },
  {
    id: 3,
    title: '福州话日常用语',
    city: '福州',
    category: '句子',
    level: '中级',
    text: '你叫什么名字？- 汝叫咩名字？\n我今年二十岁 - 我今年二十岁\n你是哪里人？- 汝是底所人？\n吃饭了吗？- 食糜了未？',
    audio: '/uploads/audio/fuzhou.mp3'
  },
  {
    id: 4,
    title: '厦门话问候语',
    city: '厦门',
    category: '词汇',
    level: '初级',
    text: '你好 - 哩厚\n谢谢 - 多谢\n对不起 - 拍失礼\n再见 - 再见\n欢迎 - 欢迎',
    audio: '/uploads/audio/xiamen.mp3'
  },
  {
    id: 5,
    title: '厦门话数字',
    city: '厦门',
    category: '词汇',
    level: '初级',
    text: '一 - tsit\n二 - nng\n三 - sa\n四 - si\n五 - go\n六 - lak\n七 - ts\'it\n八 - peh\n九 - kau\n十 - tsap',
    audio: '/uploads/audio/xiamen.mp3'
  },
  {
    id: 6,
    title: '闽南语歌曲 - 爱拼才会赢',
    city: '厦门',
    category: '歌曲',
    level: '高级',
    text: '一时失志不免怨\n一时失志不免叹\n一时失志毋通挂心\n三分天注定\n七分靠打拼\n爱拼才会赢',
    audio: '/uploads/audio/xiamen.mp3'      
  },
  {
    id: 7,
    title: '泉州话特色词汇',
    city: '泉州',
    category: '词汇',
    level: '初级',
    text: '奶奶 - 妈祖\n爷爷 - 阿公\n爸爸 - 老父\n妈妈 - 老母\n哥哥 - 阿兄\n姐姐 - 阿姐',
    audio: '/uploads/audio/quanzhou.mp3'
  },
  {
    id: 8,
    title: '泉州话俗语',
    city: '泉州',
    category: '句子',
    level: '中级',
    text: '吃饭皇帝大 - 食饭皇帝大\n入乡随俗 - 入乡随俗\n一分耕耘一分收获 - 一分耕耘一分收获',
    audio: '/uploads/audio/quanzhou.mp3'
  },
  {
    id: 9,
    title: '漳州谚语',
    city: '漳州',
    category: '句子',
    level: '中级',
    text: '早起的鸟儿有虫吃\n滴水穿石\n熟能生巧',
    audio: '/uploads/audio/zhangzhou.mp3'
  },
  {
    id: 10,
    title: '莆仙戏经典唱段',
    city: '莆田',
    category: '歌曲',
    level: '高级',
    text: '莆田戏文选段\n传统艺术精华\n代代相传',
    audio: '/uploads/audio/putian.mp3'
  }
])

const filteredAudios = computed(() => {
  if (!selectedCity.value) return audios.value
  return audios.value.filter(audio => audio.city === selectedCity.value)
})

// 复制城市数组以实现无限滚动（重复 3 次）
const allCityCards = computed(() => {
  return [...cityResources.value, ...cityResources.value, ...cityResources.value]
})

// 滚动控制变量
let iteration = 0
let currentIndex = 0
let maxIndex = 0
let scrollTween = null

// 创建无缝循环动画 - CodePen 示例核心函数
const buildSeamlessLoop = (items, spacing, animateFunc) => {
  const loop = gsap.timeline({
    repeat: -1,
    paused: true
  })
  
  items.forEach((item, i) => {
    const tl = animateFunc(item)
    loop.add(tl, i * spacing)
  })
  
  return loop
}

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

const cityCodeMap = {
  '福州市': 'fuzhou',
  '莆田市': 'putian',
  '三明市': 'sanming',
  '泉州市': 'quanzhou',
  '漳州市': 'zhangzhou',
  '南平市': 'nanping',
  '龙岩市': 'longyan',
  '宁德市': 'ningde',
  '厦门市': 'xiamen'
}

// 城市颜色配置
const cityColors = {
  fuzhou: '#667eea',
  xiamen: '#f093fb',
  quanzhou: '#4facfe',
  zhangzhou: '#43e97b',
  putian: '#fa709a',
  longyan: '#fee140',
  sanming: '#30cfd0',
  nanping: '#a8edea',
  ningde: '#fdcbf1'
}

const cityInfo = {
  fuzhou: { title: '福州话', desc: '闽东语代表，市区口音', highlight: '特色：依姆、依伯、食饭' },
  xiamen: { title: '厦门话', desc: '闽南语代表方言之一', highlight: '特色：歹势、好势、虾米' },
  quanzhou: { title: '泉州话', desc: '闽南语重要分支', highlight: '特色：阿公、阿婆、食茶' },
  zhangzhou: { title: '漳州话', desc: '闽南语重要分支，语调平缓', highlight: '特色：阿兄、阿妹、食糜' },
  putian: { title: '莆田话', desc: '闽语重要分支，独特语音', highlight: '特色：阿公、阿嬷、食饭' },
  longyan: { title: '龙岩话', desc: '客家语系，独特语音特点', highlight: '特色：阿哥、阿妹、食饭' },
  sanming: { title: '三明话', desc: '闽中语，闽语分支', highlight: '特色：阿爸、阿妈、食饭' },
  nanping: { title: '南平话', desc: '闽北语，闽语分支', highlight: '特色：阿公、阿婆、食饭' },
  ningde: { title: '宁德话', desc: '闽东语，与福州话有差异', highlight: '特色：阿公、阿婆、食饭' }
}

const tooltipPositions = {
  nanping: { left: '45%', top: '25%' },
  ningde: { left: '75%', top: '22%' },
  sanming: { left: '38%', top: '42%' },
  fuzhou: { left: '72%', top: '48%' },
  longyan: { left: '25%', top: '62%' },
  putian: { left: '68%', top: '56%' },
  quanzhou: { left: '70%', top: '65%' },
  xiamen: { left: '65%', top: '78%' },
  zhangzhou: { left: '52%', top: '72%' }
}

const getCityName = (cityCode) => {
  const city = cities.find(c => c.code === cityCode)
  return city ? city.name : '未知'
}

const getCityTitle = (cityCode) => cityInfo[cityCode]?.title || ''
const getCityDesc = (cityCode) => cityInfo[cityCode]?.desc || ''
const getCityHighlight = (cityCode) => cityInfo[cityCode]?.highlight || ''

const tooltipStyle = computed(() => {
  return {
    left: tooltipPos.value.left,
    top: tooltipPos.value.top
  }
})

const progressPercent = computed(() => {
  return ((currentQuestionIndex.value + 1) / quizzes.value.length) * 100
})

const currentQuiz = computed(() => {
  if (!quizzes.value || !quizzes.value.length) return {}
  return quizzes.value[currentQuestionIndex.value] || {}
})

const currentQuizOptions = computed(() => {
  const quiz = currentQuiz.value
  if (!quiz || !quiz.id) return []
  return [
    { key: 'A', value: quiz.optionA || '' },
    { key: 'B', value: quiz.optionB || '' },
    { key: 'C', value: quiz.optionC || '' },
    { key: 'D', value: quiz.optionD || '' }
  ]
})

const switchToRegister = () => {
  authMode.value = 'register'
  loginError.value = ''
}

const switchToLogin = () => {
  authMode.value = 'login'
  registerError.value = ''
}

const closeAuthModal = () => {
  showAuthModal.value = false
  authMode.value = 'login'
  loginError.value = ''
  registerError.value = ''
  loginForm.value = { username: '', password: '' }
  registerForm.value = { username: '', password: '', confirmPassword: '' }
}

const handleLogin = async () => {
  loginError.value = ''
  const result = await userStore.loginAction(loginForm.value)
  if (result.success) {
    closeAuthModal()
    showUserMenu.value = false
  } else {
    loginError.value = result.message || '登录失败'
  }
}

const handleRegister = async () => {
  registerError.value = ''
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    registerError.value = '两次输入的密码不一致'
    return
  }
  
  const result = await userStore.registerAction(registerForm.value)
  if (result.success) {
    alert('注册成功，请登录')
    closeAuthModal()
    showAuthModal.value = true
    authMode.value = 'login'
  } else {
    registerError.value = result.message || '注册失败'
  }
}

const startQuizInterface = () => {
  showQuizInterface.value = true
  nextTick(() => {
    const quizSection = document.getElementById('quizSection')
    if (quizSection) {
      quizSection.scrollIntoView({ behavior: 'smooth' })
    }
  })
}

const startQuiz = async () => {
  if (!selectedCity.value) return
  
  // 切换前动画
  const setupCard = document.querySelector('.setup-card')
  if (setupCard) {
    gsap.to(setupCard, { scale: 0.9, opacity: 0, duration: 0.3, ease: 'power2.in' })
  }

  try {
    const res = await getRandomQuizzes(selectedCity.value, parseInt(questionCount.value))
    if (res.code === 200 && res.data && res.data.length > 0) {
      quizzes.value = res.data
      quizStarted.value = true
      userAnswers.value = {}
      currentQuestionIndex.value = 0
      showResult.value = false

      // 进入后动画
      nextTick(() => {
        const quizContent = document.querySelector('.quiz-content-wrapper')
        if (quizContent) {
          gsap.fromTo(quizContent, 
            { x: 50, opacity: 0 },
            { x: 0, opacity: 1, duration: 0.6, ease: 'power3.out' }
          )
        }
      })
    } else {
      alert('该城市暂无题目，请选择其他城市')
      if (setupCard) {
        gsap.to(setupCard, { scale: 1, opacity: 1, duration: 0.3 })
      }
    }
  } catch (error) {
    console.error('加载题目失败:', error)
    alert('加载题目失败，请稍后重试')
    if (setupCard) {
      gsap.to(setupCard, { scale: 1, opacity: 1, duration: 0.3 })
    }
  }
}

const restartQuiz = () => {
  quizStarted.value = false
  showResult.value = false
  quizzes.value = []
  userAnswers.value = {}
  currentQuestionIndex.value = 0
}

const selectAnswer = (quizId, answer) => {
  if (userAnswers.value[quizId]) {
    return
  }
  userAnswers.value[quizId] = answer
}

const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const nextQuestion = async () => {
  if (currentQuestionIndex.value < quizzes.value.length - 1) {
    currentQuestionIndex.value++
  } else {
    await submitAnswers()
  }
}

const submitAnswers = async () => {
  const answeredCount = Object.keys(userAnswers.value).length
  if (answeredCount < quizzes.value.length) {
    if (!confirm(`还有 ${quizzes.value.length - answeredCount} 题未回答，确定要提交吗？`)) {
      return
    }
  }
  
  let correctCount = 0
  const answers = Object.keys(userAnswers.value).map(quizId => {
    const userAnswer = userAnswers.value[quizId]
    const quiz = quizzes.value.find(q => q.id.toString() === quizId)
    if (quiz && quiz.answer === userAnswer) {
      correctCount++
    }
    return {
      quizId,
      answer: userAnswer
    }
  })
  
  const totalQuestions = quizzes.value.length
  const score = Math.round((correctCount / totalQuestions) * 100)
  const accuracy = Math.round((correctCount / totalQuestions) * 100)
  
  let level = ''
  if (score >= 90) level = '方言大师'
  else if (score >= 70) level = '方言能手'
  else if (score >= 50) level = '方言初学者'
  else level = '继续加油'
  
  result.value = {
    score,
    correctCount,
    totalQuestions,
    accuracy,
    level
  }
  
  try {
    await submitQuiz({
      answers,
      cityCode: selectedCity.value,
      userId: userId.value,
      username: username.value
    })
    
    showResult.value = true
    
    // 动画展示结果
    nextTick(() => {
      if (resultCard.value) {
        gsap.fromTo(resultCard.value,
          { scale: 0.8, opacity: 0, rotationY: 45 },
          { scale: 1, opacity: 1, rotationY: 0, duration: 0.8, ease: 'back.out(1.7)' }
        )
        
        // 分数跳动动画
        const scoreNum = resultCard.value.querySelector('.score-number')
        if (scoreNum) {
          gsap.from(scoreNum, {
            innerText: 0,
            duration: 1.5,
            snap: { innerText: 1 },
            ease: 'power2.out'
          })
        }
      }
    })

    if (isLoggedIn.value) {
      await loadRecords()
    }
  } catch (error) {
    console.error('提交失败:', error)
    alert('提交失败，请稍后重试')
  }
}

const getResultMessage = (level) => {
  const messages = {
    '方言大师': '太厉害了！你对方言的掌握程度非常高！',
    '方言能手': '很不错！你已经掌握了大部分方言知识！',
    '方言初学者': '继续加油！多听多说就能进步！',
    '继续加油': '不要气馁，多加练习一定能提高！'
  }
  return messages[level] || ''
}

const getResultMessageIcon = (level) => {
  const icons = {
    '方言大师': '',
    '方言能手': '',
    '方言初学者': '',
    '继续加油': ''
  }
  return icons[level] || ''
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const loadRecords = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await getRecords(userId.value)
    if (res.code === 200) {
      records.value = res.data || []
    }
  } catch (error) {
    console.error('加载记录失败:', error)
  }
}

const goToAdmin = () => {
  router.push('/admin')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToLearning = () => {
  router.push('/learning')
}

const goToFolkResource = () => {
  router.push('/folk')
}

const goToCommunity = () => {
  router.push('/community')
}

const goToQuiz = () => {
  router.push('/quiz')
}

const goToAudioUpload = () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录再上传音频')
    return
  }
  router.push('/audio-upload')
}

const loadAudios = async () => {
  audioLoading.value = true
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
    audioLoading.value = false
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

const handleLogout = () => {
  userStore.logout()
  router.replace('/home')
}

const goToCityDetail = (cityCode) => {
  router.push(`/city/${cityCode}`)
}

// 跳转到城市资源页面
const goToCityResource = (cityCode) => {
  router.push(`/folk/${cityCode}`)
}

// 显示资源详情
const showResource = (cityCode) => {
  const city = cityResources.value.find(c => c.code === cityCode)
  if (city) {
    selectedCityResource.value = city
    showResourceDetail.value = true
    
    // 弹窗出现动画
    nextTick(() => {
      const modal = document.querySelector('.resource-modal-content')
      if (modal) {
        gsap.fromTo(modal,
          { scale: 0.8, opacity: 0, y: 50 },
          { scale: 1, opacity: 1, y: 0, duration: 0.4, ease: 'back.out(1.7)' }
        )
      }
    })
  }
}

// 关闭资源详情
const closeResourceDetail = () => {
  showResourceDetail.value = false
  selectedCityResource.value = null
}

// 从 GeoJSON 生成 SVG
const loadMapData = async () => {
  try {     //获取福建省数据
    const response = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/350000_full.json')
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const geoJson = await response.json()
    const features = geoJson.features || []
    //计算边界
    let minX = Infinity, minY = Infinity, maxX = -Infinity, maxY = -Infinity
    
    features.forEach(feature => {
      const coords = feature.geometry.coordinates
      const allPoints = flattenCoordinates(coords)
      allPoints.forEach(([lng, lat]) => {
        minX = Math.min(minX, lng)
        minY = Math.min(minY, lat)
        maxX = Math.max(maxX, lng)
        maxY = Math.max(maxY, lat)
      })
    })
    
    const width = 567
    const height = 433
    const padding = 10
    const rangeX = maxX - minX
    const rangeY = maxY - minY
    //生成 SVG 路径
    let svgPaths = ''
    let svgLabels = ''
    let processedCount = 0
    
    features.forEach((feature, featureIndex) => {
      const cityName = feature.properties.name
      const cityCode = cityCodeMap[cityName]
      
      if (!cityCode) {
        return
      }
      
      const coordinates = feature.geometry.coordinates
      let cityPathD = ''
      let polygonCount = 0
      let labelPoints = []
      
      coordinates.forEach((polygon, polygonIndex) => {
        if (Array.isArray(polygon)) {
          let polygonPathD = ''
          let polygonPoints = []
          polygon.forEach((ring, ringIndex) => {
            if (Array.isArray(ring) && ring.length > 0) {
              ring.forEach((point, pointIndex) => {
                if (Array.isArray(point) && point.length >= 2) {
                  const lng = point[0]
                  const lat = point[1]
                  
                  const x = ((lng - minX) / rangeX) * (width - padding * 2) + padding
                  const y = height - padding - ((lat - minY) / rangeY) * (height - padding * 2)
                  
                  if (pointIndex === 0) {
                    polygonPathD += `M ${x.toFixed(3)},${y.toFixed(3)} `
                  } else {
                    polygonPathD += `L ${x.toFixed(3)},${y.toFixed(3)} `
                  }
                  polygonPoints.push({ x, y })
                }
              })
              polygonPathD += 'Z '
            }
          })
          
          if (polygonPathD.length > 0) {
            cityPathD += polygonPathD
            polygonCount++
            labelPoints = labelPoints.concat(polygonPoints)
          }
        }
      })
      
      if (cityPathD.length > 0) {
        const color = cityColors[cityCode] || '#667eea'
        svgPaths += `<path d="${cityPathD.trim()}" data-city="${cityCode}" class="city-path" style="fill: ${color}"/>`
        processedCount++
      }
      
      if (labelPoints.length > 0) {
        const centerX = labelPoints.reduce((sum, p) => sum + p.x, 0) / labelPoints.length
        const centerY = labelPoints.reduce((sum, p) => sum + p.y, 0) / labelPoints.length
        
        svgLabels += `<text x="${centerX}" y="${centerY}" class="city-label" data-city="${cityCode}">${cityName}</text>`
      }
    })
    
    const finalSVG = `<svg viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
      <g class="paths-group">${svgPaths}</g>
      <g class="labels-group">${svgLabels}</g>
    </svg>`
    
    svgContent.value = finalSVG
    
    nextTick(() => {
      initMapInteraction()
    })
  } catch (error) {
    console.error('加载地图失败:', error)
  }
}

const flattenCoordinates = (coords) => {
  if (typeof coords[0][0] === 'number') {
    return coords
  }
  return coords.reduce((acc, item) => acc.concat(flattenCoordinates(item)), [])
}

const initMapInteraction = () => {
  const svgElement = document.querySelector('.map-wrapper svg')
  if (!svgElement) return
  
  const paths = svgElement.querySelectorAll('.paths-group .city-path')
  const labels = svgElement.querySelectorAll('.labels-group .city-label')
  
  // 为每个 path 添加入场动画
  gsap.set(paths, {
    scale: 0,
    opacity: 0,
    transformOrigin: 'center'
  })
  
  gsap.set(labels, {
    y: 20,
    opacity: 0
  })
  
  // 使用 ScrollTrigger 实现滚动触发动画
  const pathTimeline = gsap.timeline({
    scrollTrigger: {
      trigger: '#mapSection',
      start: 'top 80%',
      toggleActions: 'play none none none',
      once: true
    }
  })
  
  paths.forEach((path, index) => {
    pathTimeline.to(path, {
      scale: 1,
      opacity: 1,
      duration: 0.5,
      ease: 'back.out(1.7)',
      delay: index * 0.1
    }, 0)
  })
  
  // 创建 label 动画序列
  const labelTimeline = gsap.timeline({
    scrollTrigger: {
      trigger: '#mapSection',
      start: 'top 70%',
      toggleActions: 'play none none none',
      once: true
    }
  })
  
  labels.forEach((label, index) => {
    labelTimeline.to(label, {
      y: 0,
      opacity: 1,
      duration: 0.4,
      ease: 'power2.out',
      delay: index * 0.1
    }, 0)
  })
  
  // 添加交互事件
  paths.forEach(path => {
    const cityCode = path.getAttribute('data-city')
    
    path.addEventListener('mouseenter', (e) => {
      hoveredCity.value = cityCode
      
      // 悬停动画
      gsap.to(path, {
        scale: 1.05,
        duration: 0.3,
        ease: 'power2.out'
      })
      
      const container = document.querySelector('.map-container')
      if (container) {
        const rect = container.getBoundingClientRect()
        tooltipPos.value = {
          left: `${e.clientX - rect.left}px`,
          top: `${e.clientY - rect.top - 20}px`
        }
      }
    })
    
    path.addEventListener('mouseleave', () => {
      hoveredCity.value = null
      
      // 离开动画
      gsap.to(path, {
        scale: 1,
        duration: 0.3,
        ease: 'power2.out'
      })
    })
    
    path.addEventListener('click', (e) => {
      e.stopPropagation()
      goToCityDetail(cityCode)
    })
  })
  
  // 添加标签交互事件
  labels.forEach(label => {
    const cityCode = label.getAttribute('data-city')
    
    label.addEventListener('mouseenter', (e) => {
      hoveredCity.value = cityCode
      
      const container = document.querySelector('.map-container')
      if (container) {
        const rect = container.getBoundingClientRect()
        tooltipPos.value = {
          left: `${e.clientX - rect.left}px`,
          top: `${e.clientY - rect.top - 20}px`
        }
      }
    })
    
    label.addEventListener('mouseleave', () => {
      hoveredCity.value = null
    })
    
    label.addEventListener('click', (e) => {
      e.stopPropagation()
      goToCityDetail(cityCode)
    })
  })
}

onMounted(() => {
  initGSAPAnimations()
  loadMapData()
  loadAudios()
  loadCityResources()
})

// 卡片滚动控制
let currentPosition = 0
const cardWidth = 350
const cardGap = 30
const cardTotalWidth = cardWidth + cardGap
const totalCards = 9 // 9 个城市

// 实时更新卡片状态函数
const updateCardStates = () => {
  const cards = document.querySelectorAll('.card')
  const centerX = window.innerWidth / 2
  
  cards.forEach((card) => {
    const rect = card.getBoundingClientRect()
    const cardCenterX = rect.left + rect.width / 2
    const distanceFromCenter = Math.abs(centerX - cardCenterX)
    
    const scale = Math.max(0.7, 1 - (distanceFromCenter / window.innerWidth) * 0.5)
    const opacity = Math.max(0.5, 1 - (distanceFromCenter / window.innerWidth) * 0.6)
    const zIndex = Math.floor(100 - distanceFromCenter / 10)
    
    gsap.set(card, {
      scale: scale,
      opacity: opacity,
      zIndex: zIndex
    })
  })
}

// 初始化卡片位置
const initCards = () => {
  const cards = document.querySelectorAll('.card')
  const container = document.querySelector('.cards')
  
  if (!container || cards.length === 0) return
  
  // 设置容器初始位置 - 居中显示
  const initialOffset = (window.innerWidth / 2) - (cardWidth / 2) - (cardTotalWidth * 4) // 向左偏移4个卡片的宽度
  gsap.set(container, { x: initialOffset })
  
  // 立即更新一次
  updateCardStates()
}

// 向前滚动
const scrollPrev = () => {
  console.log('Prev button clicked')
  console.log('Current position:', currentPosition)
  currentPosition += cardTotalWidth
  console.log('New position:', currentPosition)
  const container = document.querySelector('.cards')
  console.log('Container found:', !!container)
  if (container) {
    console.log('Animating to:', currentPosition)
    gsap.to(container, {
      x: currentPosition,
      duration: 0.8,
      ease: 'power3.inOut',
      onUpdate: updateCardStates,
      onComplete: () => console.log('Animation completed')
    })
  }
}

// 向后滚动
const scrollNext = () => {
  console.log('Next button clicked')
  console.log('Current position:', currentPosition)
  currentPosition -= cardTotalWidth
  console.log('New position:', currentPosition)
  const container = document.querySelector('.cards')
  console.log('Container found:', !!container)
  if (container) {
    console.log('Animating to:', currentPosition)
    gsap.to(container, {
      x: currentPosition,
      duration: 0.8,
      ease: 'power3.inOut',
      onUpdate: updateCardStates,
      onComplete: () => console.log('Animation completed')
    })
  }
}

// GSAP 动画初始化
const initGSAPAnimations = () => {
  // 英雄内容和图片动画
  gsap.to('.hero-content', {
    opacity: 1,
    duration: 1.5,
    delay: 0.5
  })

  gsap.to('.hero-image', {
    scale: 1,
    duration: 2.5,
    ease: 'power2.out',
    delay: 0.3
  })

  gsap.fromTo('#mapSection',
    {
      y: 100,
      opacity: 0,
      scale: 0.95
    },
    {
      y: 0,
      opacity: 1,
      scale: 1,
      duration: 1,
      ease: 'power3.out',
      scrollTrigger: {
        trigger: '#mapSection',
        start: 'top 80%',
      }
    }
  )



  // 方言学习屏动画
  gsap.fromTo('#learningSection',
    {
      opacity: 0,
      y: 100
    },
    {
      opacity: 1,
      y: 0,
      duration: 1.5,
      ease: 'power3.out',
      scrollTrigger: {
        trigger: '#learningSection',
        start: 'top 80%',
      }
    }
  )

  gsap.fromTo('#learningSection .section-title',
    {
      opacity: 0,
      scale: 0.8
    },
    {
      opacity: 1,
      scale: 1,
      duration: 0.8,
      ease: 'back.out(1.7)',
      delay: 0.3
    }
  )

  gsap.fromTo('#learningSection .section-desc',
    {
      opacity: 0,
      x: -30
    },
    {
      opacity: 1,
      x: 0,
      duration: 0.8,
      delay: 0.5
    }
  )

  // 学习卡片容器动画
  gsap.fromTo('#learningSection .learning-cards-container',
    {
      opacity: 0,
      y: 50
    },
    {
      opacity: 1,
      y: 0,
      duration: 1,
      delay: 0.5,
      scrollTrigger: {
        trigger: '#learningSection .learning-cards-container',
        start: 'top 85%',
      }
    }
  )

  // 方言资源屏动画
  gsap.fromTo('#folkSection',
    {
      opacity: 0,
      y: 100
    },
    {
      opacity: 1,
      y: 0,
      duration: 1.5,
      ease: 'power3.out',
      scrollTrigger: {
        trigger: '#folkSection',
        start: 'top 80%',
      }
    }
  )

  // 等待 DOM 渲染后初始化
  setTimeout(() => {
    initCards()
  }, 200)

  // 在线测试屏动画
  gsap.fromTo('#quizSection',
    {
      opacity: 0,
      y: 100
    },
    {
      opacity: 1,
      y: 0,
      duration: 1.5,
      ease: 'power3.out',
      scrollTrigger: {
        trigger: '#quizSection',
        start: 'top 80%',
      }
    }
  )

  // 第七屏：音频库进场动画
  gsap.fromTo('#audioSection',
    {
      opacity: 0,
      y: 100
    },
    {
      opacity: 1,
      y: 0,
      duration: 1.5,
      ease: 'power3.out',
      scrollTrigger: {
        trigger: '#audioSection',
        start: 'top 80%',
      }
    }
  )

  // 音频库列表动画
  gsap.fromTo('#audioSection .audio-list',
    {
      opacity: 0,
      y: 50
    },
    {
      opacity: 1,
      y: 0,
      duration: 1,
      delay: 0.7,
      ease: 'back.out(1.7)',
      scrollTrigger: {
        trigger: '#audioSection .audio-list',
        start: 'top 85%',
      }
    }
  )

  // 第六屏进场动画 - 更加细腻的 Stagger 效果
  const quizTl = gsap.timeline({
    scrollTrigger: {
      trigger: '#quizSection',
      start: 'top 70%',
    }
  })

  quizTl.fromTo(quizTitle.value, 
    { opacity: 0, y: 30, scale: 0.9 },
    { opacity: 1, y: 0, scale: 1, duration: 0.8, ease: 'back.out(1.7)' }
  ).fromTo(quizSubtitle.value,
    { opacity: 0, y: 20 },
    { opacity: 1, y: 0, duration: 0.6 },
    '-=0.4'
  ).fromTo(quizStartBtn.value,
    { opacity: 0, scale: 0.5 },
    { opacity: 1, scale: 1, duration: 0.6, ease: 'back.out(2)' },
    '-=0.3'
  )
}
</script>

<style scoped>
/* 容器 */
.home-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  overflow-x: hidden;
}

/* 固定导航栏 */
.fixed-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  background: transparent;
  backdrop-filter: none;
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
}

.navbar-title {
  font-size: 24px;
  color: white;
  font-weight: bold;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

/* 第一屏：背景图界面 */
.hero-section {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}



.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transform: scale(1.1);
}

.hero-content {
  position: absolute;
  top: 50%;
  right: 10%;
  transform: translateY(-50%);
  text-align: right;
  color: white;
  z-index: 5;
  opacity: 0;
}

.hero-subtitle {
  font-size: 24px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  line-height: 1.6;
}

/* 第二屏：地图界面 */
.map-interface {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 100px 20px;
  background: url('/background.png') no-repeat center center;
  background-size: cover;
}

.map-container {
  max-width: 1200px;
  margin: 0 auto;
  background: transparent;
  padding: 50px 40px;
  text-align: center;
}



/* 第四屏：方言学习 */
.learning-section {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 80px 20px;
  background: url('/background1.png') no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-x: hidden;
  overflow-y: visible;
  z-index: 1;
  box-sizing: border-box;
}

.learning-full-container {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  box-sizing: border-box;
}

.learning-header {
  text-align: center;
  margin-bottom: 30px;
  position: relative;
  z-index: 10;
}

.learning-header .section-title {
  font-size: 42px;
  font-weight: bold;
  color: white;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.learning-header .section-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.6;
}

/* 学习卡片容器 */
.learning-cards-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-bottom: 50px;
}

@media (max-width: 1200px) {
  .learning-cards-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .learning-cards-container {
    grid-template-columns: 1fr;
  }
}

/* 学习卡片 */
.learning-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.learning-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
}

.card-top {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.badge {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.badge.category {
  background: linear-gradient(135deg, #000000, #000000);
  color: white;
}

.badge.level {
  background: #f0f0f0;
  color: #666;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  line-height: 1.4;
}

.card-text {
  font-size: 13px;
  color: #666;
  line-height: 1.7;
  white-space: pre-line;
  flex-grow: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 6;
  -webkit-box-orient: vertical;
  max-height: 140px;
  margin-bottom: 20px;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.location {
  font-size: 13px;
  color: #888;
}

.card-play-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.card-play-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

.card-play-btn.playing {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.card-play-btn svg {
  width: 16px;
  height: 16px;
}

.learning-footer {
  text-align: center;
  margin-top: 40px;
}

.btn-more {
  padding: 15px 50px;
  background: white;
  color: #000000;
  border: 2px solid white;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.btn-more:hover {
  background: white;
  color: #000000;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.3);
}

/* 筛选区域 */
.filter-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  justify-content: center;
  align-items: center;
  margin-bottom: 40px;
  padding: 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.filter-group label {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

.filter-btn {
  padding: 8px 20px;
  border: none;
  background: #f0f0f0;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  background: #e0e0e0;
  transform: translateY(-1px);
}

.filter-btn.active {
  background: linear-gradient(135deg, #000000);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 资源卡片列表 */
.resource-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  width: 100%;
  box-sizing: border-box;
  align-items: stretch;
}

@media (max-width: 1200px) {
  .resource-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .resource-list {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}

.resource-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.resource-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.card-image-placeholder {
  position: relative;
  width: 100%;
  height: 180px;
  min-height: 180px;
  background: linear-gradient(to bottom, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.6));
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
}

.category-badge {
  padding: 6px 16px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.difficulty-badge {
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.3);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.card-content {
  padding: 20px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  position: relative;
  z-index: 1;
}

.resource-title {
  font-size: 17px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
  line-height: 1.3;
  flex-shrink: 0;
}

.resource-content {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  flex-grow: 1;
  white-space: pre-line;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 5;
  -webkit-box-orient: vertical;
  max-height: 120px;
  min-height: 0;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
  flex-shrink: 0;
  gap: 10px;
}

.resource-city {
  font-size: 12px;
  color: #888;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.learn-btn {
  padding: 8px 24px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.learn-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.learning-footer {
  text-align: center;
  margin-top: 30px;
}

.more-btn {
  padding: 15px 50px;
  background: white;
  color: #000000;
  border: 2px solid white;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.more-btn:hover {
  background: white;
  color: #000000;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.3);
}

/* 第五屏：方言资源 */
.folk-section {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 100px 20px;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 第五屏：方言资源 - 无限滚动卡片 (3D 透视效果) */
.folk-section {
  position: relative;
  width: 100%;
  min-height: 100vh;
  background: url('/wuding.png') no-repeat center center;
  background-size: cover;
  overflow: hidden;
  perspective: 1000px;
}

/* 画廊容器 */
.gallery {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

/* 卡片列表 */
.cards {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  gap: 20px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translateY(-50%);
}

/* 单个卡片 */
.card {
  flex-shrink: 0;
  width: 350px;  /* 更大的卡片宽度 */
  height: 500px; /* 更大的卡片高度 */
  position: relative;
  transform-style: preserve-3d;
  cursor: pointer;
}

.card:hover {
  transform: scale(1.02);
}

.card-inner {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  cursor: pointer;
  background: #fff;
}

.card:hover .card-inner {
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.4);
  transform: scale(1.05);
}

/* 卡片背景 */
.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  filter: brightness(0.7);
  transition: all 0.3s ease;
}

.card:hover .card-bg {
  filter: brightness(0.5);
}

/* 卡片内容 */
.card-content {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 30px 20px;
  color: white;
  text-align: center;
  z-index: 10;
}

.card-city {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.card-dialect {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  opacity: 0.9;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.5);
}

.card-resource {
  font-size: 16px;
  opacity: 0.8;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.5);
}

/* 控制按钮 */
.controls {
  position: absolute;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 20px;
  z-index: 200;
}

.control-btn {
  padding: 15px 40px;
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-radius: 30px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.8);
  transform: scale(1.05);
}

.control-btn span {
  pointer-events: none;
}

/* 资源详情弹窗 */
.resource-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.3s ease;
}

.resource-modal-content {
  position: relative;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  background: white;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.4s ease;
}

.modal-close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  font-size: 28px;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  z-index: 10;
}

.modal-close-btn:hover {
  background: white;
  color: #000000;
  transform: scale(1.1);
}

.resource-detail {
  overflow: hidden;
}

.resource-header {
  height: 250px;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 40px;
  color: white;
}

.resource-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
}

.resource-header h2 {
  position: relative;
  z-index: 1;
  font-size: 42px;
  font-weight: 800;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.resource-header p {
  position: relative;
  z-index: 1;
  font-size: 20px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.5);
}

.resource-body {
  padding: 40px;
}

.resource-body h3 {
  font-size: 28px;
  color: #333;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.resource-intro {
  margin-bottom: 30px;
}

.resource-intro p {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 15px;
}

.resource-actions {
  display: flex;
  gap: 20px;
}

.resource-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.resource-btn svg {
  width: 20px;
  height: 20px;
}

.resource-btn:hover {
  border-color: #000000;
  color: #000000;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.resource-btn.primary {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  border-color: transparent;
  color: white;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
}

.resource-btn.primary:hover {
  box-shadow: 0 8px 30px rgb(0, 0, 0);
  transform: translateY(-3px);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 第七屏：方言音频库 */
.audio-section {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 100px 20px 40px;
  background: url('/background1.png') no-repeat center center;
  background-size: cover;
}

.audio-full-container {
  max-width: 1200px;
  margin: 0 auto;
}

.audio-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-content {
  flex: 1;
}

.badge-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.audio-title {
  font-size: 42px;
  color: #333;
  margin: 0 0 10px 0;
}

.audio-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0;
}

.upload-btn {
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.6);
}

.gallery-filters {
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
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

.empty-icon {
  font-size: 72px;
  margin-bottom: 20px;
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

.audio-tag {
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
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

/* 第六屏：在线测试 */
.quiz-section {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 100px 20px;
  background: url('/background1.png') no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 第六屏介绍内容 */
.quiz-intro-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 40px;
  width: 100%;
  max-width: 1000px;
  animation: fadeInUp 0.8s ease;
  padding: 40px 20px;
}

.intro-header {
  text-align: center;
  margin-bottom: 20px;
}

.badge-icon {
  font-size: 64px;
  margin-bottom: 20px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.intro-title {
  font-size: 48px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 800;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
}

.intro-subtitle {
  font-size: 18px;
  color: #666;
  margin-bottom: 0;
  line-height: 1.8;
}

.intro-features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  width: 100%;
  margin: 20px 0;
}

.feature-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 30px 20px;
  border-radius: 20px;
  text-align: center;
  border: 2px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(0, 0, 0, 0.3);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.feature-card h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 700;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.intro-stats {
  display: flex;
  gap: 60px;
  margin-top: 20px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 题目切换动画 */
.slide-fade-enter-active {
  transition: all 0.4s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from {
  transform: translateX(30px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-30px);
  opacity: 0;
}

.question-container {
  min-height: 400px;
  position: relative;
}

/* 结果分数跳动 */
.score-number {
  font-size: 72px;
  font-weight: 800;
  color: #667eea;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.start-quiz-btn {
  padding: 18px 50px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border: none;
  border-radius: 40px;
  font-size: 22px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
  position: relative;
  overflow: hidden;
}

.start-quiz-btn::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(45deg) translateY(-100%);
  transition: transform 0.6s;
}

.start-quiz-btn:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.6);
}

.start-quiz-btn:hover::after {
  transform: rotate(45deg) translateY(100%);
}

.start-quiz-btn:active {
  transform: translateY(-2px) scale(0.98);
}

/* 调整原来的动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.quiz-intro-image {
  width: 100%;
  height: 100%;
  position: absolute;
}

.transition-path {
  opacity: 0.9;
}

.transition-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10001;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(0, 0, 0, 0.2);
  border-top-color: #000000;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 20px;
  font-weight: 600;
  color: #000000;
  animation: pulse 1.5s ease infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.section-content {
  max-width: 1200px;
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.section-content.reverse {
  grid-template-columns: 1fr 1fr;
  direction: rtl;
}

.section-content.reverse > * {
  direction: ltr;
}

.section-text {
  padding: 40px;
}

.section-title {
  font-size: 42px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-desc {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
  line-height: 1.8;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin-bottom: 40px;
}

.feature-list li {
  font-size: 16px;
  color: #555;
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.feature-list li:hover {
  color: #000000;
  transform: translateX(10px);
}

.feature-list li:last-child {
  border-bottom: none;
}

.start-btn {
  padding: 15px 40px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.4);
}

.start-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.6);
}

.section-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-placeholder {
  width: 100%;
  max-width: 500px;
  height: 400px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.image-placeholder span {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
  font-weight: 600;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

/* 答题界面 */
.quiz-fullscreen {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  animation: fadeIn 0.5s ease;
  position: relative;
  z-index: 10;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.setup-section {
  padding: 60px 40px;
}

.setup-card {
  max-width: 700px;
  margin: 0 auto;
  background: white;
  padding: 50px 40px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.setup-header {
  text-align: center;
  margin-bottom: 40px;
}

.setup-icon {
  font-size: 56px;
  margin-bottom: 15px;
  animation: rotate 3s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.setup-header h3 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 700;
}

.setup-header p {
  font-size: 15px;
  color: #666;
  margin: 0;
}

.form-group {
  margin-bottom: 30px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #555;
  font-weight: 600;
  margin-bottom: 12px;
  font-size: 15px;
}

.label-icon {
  font-size: 18px;
}

.city-selector {
  width: 100%;
}

.custom-select {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
}

.custom-select:focus {
  outline: none;
  border-color: #000000;
  box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.1);
}

.question-count-selector {
  display: flex;
  gap: 15px;
}

.count-btn {
  flex: 1;
  padding: 14px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: white;
  color: #666;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.count-btn:hover {
  border-color: #000000;
  background: rgba(0, 0, 0, 0.05);
}

.count-btn.active {
  border-color: #000000;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.records-section {
  margin-top: 40px;
  text-align: left;
}

.records-title {
  color: #333;
  font-size: 20px;
  margin-bottom: 20px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 350px;
  overflow-y: auto;
  padding: 10px;
  background: rgba(249, 249, 249, 0.5);
  border-radius: 12px;
}

.records-list::-webkit-scrollbar {
  width: 6px;
}

.records-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.records-list::-webkit-scrollbar-thumb {
  background: #000000;
  border-radius: 3px;
}

.record-item {
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.record-item:hover {
  border-color: #000000;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
  transform: translateX(5px);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.record-city {
  font-weight: 700;
  color: #000000;
  font-size: 16px;
}

.record-score {
  font-weight: 700;
  color: #28a745;
  font-size: 20px;
}

.record-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.record-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.record-stat .stat-label {
  font-size: 12px;
  color: #999;
}

.record-stat .stat-value {
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.record-time {
  color: #999;
  font-size: 13px;
  margin-left: auto;
}

.quiz-content-wrapper {
  padding: 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.quiz-header-simple {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.back-to-intro-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.back-to-intro-btn:hover {
  background: #e0e0e0;
  transform: translateX(-3px);
}

.back-to-intro-btn svg {
  width: 18px;
  height: 18px;
}

.quiz-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quiz-city-label {
  font-size: 18px;
  font-weight: 700;
  color: #000000;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.quiz-progress-text {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.quiz-progress {
  margin-bottom: 30px;
}

.progress-bar {
  height: 10px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 10px;
}

.question-card {
  margin-bottom: 30px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.question-meta {
  display: flex;
  gap: 15px;
  align-items: center;
}

.question-category {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.3);
}

.question-difficulty {
  font-size: 16px;
  letter-spacing: 2px;
}

.question-number {
  font-size: 15px;
  color: #666;
  font-weight: 600;
  padding: 6px 16px;
  background: #f5f5f5;
  border-radius: 20px;
}

.question-title {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 30px 0;
  line-height: 1.6;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  padding: 18px 24px;
  background: #f9f9f9;
  border-radius: 12px;
  border: 2px solid #e0e0e0;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 15px;
  position: relative;
  overflow: hidden;
}

.option-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #000000 0%, #000000        100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.option-item:hover:not(.locked) {
  border-color: #000000;
  background: #f5f5ff;
  transform: translateX(5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.option-item:hover:not(.locked)::before {
  opacity: 1;
}

.option-item.selected {
  border-color: #000000;
  background: #e8e8ff;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.option-item.selected::before {
  opacity: 1;
}

.option-item.correct {
  border-color: #28a745;
  background: #d4edda;
  box-shadow: 0 5px 20px rgba(40, 167, 69, 0.2);
}

.option-item.correct::before {
  background: #28a745;
  opacity: 1;
}

.option-item.wrong {
  border-color: #dc3545;
  background: #f8d7da;
  box-shadow: 0 5px 20px rgba(220, 53, 69, 0.2);
}

.option-item.wrong::before {
  background: #dc3545;
  opacity: 1;
}

.option-item.locked {
  cursor: not-allowed;
  opacity: 0.7;
}

.status-icon {
  margin-left: auto;
  font-size: 20px;
  font-weight: 700;
  color: #28a745;
}

.option-item.wrong .status-icon {
  color: #dc3545;
}

.option-key {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  font-weight: 700;
  color: #000000;
  font-size: 15px;
  background: rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.option-item.selected .option-key {
  background: linear-gradient(135deg, #000000 0%, #764ba2 100%);
  color: white;
}

.option-item.correct .option-key {
  background: #28a745;
  color: white;
}

.option-item.wrong .option-key {
  background: #dc3545;
  color: white;
}

.option-text {
  color: #333;
  font-size: 16px;
  flex: 1;
  line-height: 1.5;
}

.explanation {
  margin-top: 25px;
  padding: 25px;
  background: linear-gradient(135deg, #f9f9f9 0%, #f0f0f0 100%);
  border-radius: 12px;
  border-left: 4px solid #000000;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.result-tip {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 17px;
  margin-bottom: 15px;
  padding: 12px 16px;
  border-radius: 8px;
}

.result-tip.correct {
  color: #28a745;
  background: rgba(40, 167, 69, 0.1);
}

.result-tip.wrong {
  color: #dc3545;
  background: rgba(220, 53, 69, 0.1);
}

.tip-icon {
  font-size: 20px;
  font-weight: 700;
}

.tip-text {
  flex: 1;
}

.explanation-text {
  color: #666;
  line-height: 1.7;
  font-size: 15px;
  padding-left: 15px;
  border-left: 3px solid #ddd;
  margin-left: 10px;
}

.quiz-actions {
  display: flex;
  gap: 20px;
  margin-top: 30px;
  justify-content: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 35px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn svg {
  width: 18px;
  height: 18px;
}

.action-btn:hover:not(:disabled) {
  background: #e0e0e0;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.action-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  color: white;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
}

.action-btn.primary:hover:not(:disabled) {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5);
  transform: translateY(-3px);
}

.result-section {
  padding: 60px 40px;
}

.result-card {
  max-width: 700px;
  margin: 0 auto;
  background: white;
  padding: 50px 40px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.result-header {
  text-align: center;
  margin-bottom: 30px;
}

.result-icon {
  font-size: 64px;
  margin-bottom: 15px;
  animation: celebration 1s ease infinite;
}

@keyframes celebration {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.1) rotate(10deg);
  }
}

.result-card h3 {
  color: #333;
  font-size: 32px;
  margin: 0;
  font-weight: 700;
}

.result-score {
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-circle {
  position: relative;
  width: 180px;
  height: 180px;
  margin-bottom: 20px;
}

.score-svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.score-bg {
  fill: none;
  stroke: #f0f0f0;
  stroke-width: 8;
}

.score-fill {
  fill: none;
  stroke: url(#scoreGradient);
  stroke-width: 8;
  stroke-linecap: round;
  transition: stroke-dashoffset 1.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.score-number {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 48px;
  font-weight: 800;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.score-label {
  color: #666;
  font-size: 16px;
  margin: 0;
  font-weight: 500;
}

.result-detail {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 35px;
}

.detail-item {
  padding: 25px 20px;
  background: linear-gradient(135deg, #f9f9f9 0%, #f0f0f0 100%);
  border-radius: 16px;
  text-align: center;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.detail-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.detail-icon {
  font-size: 32px;
  margin-bottom: 10px;
  display: block;
}

.detail-label {
  display: block;
  color: #666;
  font-size: 13px;
  margin-bottom: 8px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  display: block;
  color: #333;
  font-size: 20px;
  font-weight: 700;
}

.detail-value.level {
  color: #667eea;
  font-size: 18px;
  background: linear-gradient(135deg, #000000 0%, #000000 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.result-message {
  color: #666;
  font-size: 16px;
  line-height: 1.7;
  padding: 25px;
  background: linear-gradient(135deg, #f9f9f9 0%, #f0f0f0 100%);
  border-radius: 16px;
  margin-bottom: 25px;
  border: 2px solid rgba(102, 126, 234, 0.1);
}

.message-icon {
  font-size: 40px;
  margin-bottom: 10px;
  display: block;
}

.result-message p {
  margin: 0;
  font-weight: 500;
}

.result-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

/* 用户信息和图标按钮样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-dropdown {
  position: relative;
  display: inline-block;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid rgba(255, 255, 255, 0.6);
  object-fit: cover;
}

.user-avatar:hover {
  border-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.3);
}

.user-menu {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 260px;
  background: rgba(255, 255, 255, 0.986);
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  overflow: hidden;
  animation: menuFadeIn 0.2s ease;
}

@keyframes menuFadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-menu-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  gap: 12px;
}

.user-menu-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.user-menu-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-menu-info {
  flex: 1;
  overflow: hidden;
}

.user-menu-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-menu-role {
  font-size: 12px;
  color: #888;
}

.user-menu-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 0 20px;
}

.user-menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.2s;
  color: #333;
}

.user-menu-item:hover {
  background: #f8f9fa;
}

.user-menu-item svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  flex-shrink: 0;
}

.user-menu-item span {
  font-size: 14px;
  font-weight: 500;
}

.user-menu-item.logout {
  color: #ff4757;
}

.user-menu-item.logout:hover {
  background: #fff5f5;
}

.icon-btn {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  text-decoration: none;
  padding: 0;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.08);
  box-shadow: 0 4px 20px rgba(255, 255, 255, 0.3);
  color: white;
}

.icon-btn svg {
  width: 22px;
  height: 22px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.auth-btn {
  padding: 8px 10px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.auth-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(255, 255, 255, 0.3);
}

.auth-btn svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.admin-btn {
  padding: 8px 20px;
  background: rgba(76, 209, 55, 0.8);
  backdrop-filter: blur(10px);
  color: white;
  border: 2px solid rgba(76, 209, 55, 0.5);
  border-radius: 20px;
  font-weight: 600;
  transition: all 0.3s;
  cursor: pointer;
  font-size: 14px;
}

.admin-btn:hover {
  background: rgba(76, 209, 55, 0.9);
  border-color: rgba(76, 209, 55, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(76, 209, 55, 0.3);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: modalFadeIn 0.3s ease;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 32px;
  color: #999;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
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

.form-error {
  color: #dc3545;
  font-size: 13px;
  margin-bottom: 15px;
  padding: 8px 12px;
  background: #fff5f5;
  border-radius: 4px;
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
  box-shadow: 0 5px 15px rgba(252, 252, 252, 0.4);
}

.modal-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
  color: #666;
  font-size: 14px;
}

.modal-footer a {
  color: #000000;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.modal-footer a:hover {
  text-decoration: underline;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modalSlideIn {
  from {
    transform: translateY(-30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 地图样式 */
.map-wrapper svg {
  width: 100%;
  height: auto;
  display: block;
  background: #f0f8ff;
}

:deep(.city-path) {
  stroke: #ffffff;
  stroke-width: 0.5;
  transition: all 0.3s ease;
  cursor: pointer;
}

:deep(.city-path:hover),
:deep(.city-path.active) {
  stroke: #ffd700;
  stroke-width: 2;
  filter: drop-shadow(0 0 8px rgba(118, 75, 162, 0.5));
}

:deep(.city-label) {
  fill: #ffffff;
  font-size: 12px;
  font-weight: 600;
  text-anchor: middle;
  dominant-baseline: middle;
  pointer-events: none;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
}

.map-tooltip {
  position: absolute;
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
  min-width: 180px;
  max-width: 220px;
  z-index: 100;
  animation: tooltipFadeIn 0.3s ease;
  pointer-events: none;
}

.tooltip-title {
  color: #000000;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 2px solid #000000;
  padding-bottom: 5px;
}

.tooltip-desc {
  color: #666;
  font-size: 13px;
  margin-bottom: 5px;
  line-height: 1.4;
}

.tooltip-highlight {
  color: #764ba2;
  font-size: 13px;
  font-weight: 600;
  background: #f0f0f0;
  padding: 3px 6px;
  border-radius: 3px;
  display: inline-block;
  margin-top: 8px;
}

@keyframes tooltipFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .intro-features {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .intro-stats {
    gap: 40px;
  }
  
  .result-detail {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .quiz-intro-content {
    padding: 20px 10px;
    gap: 30px;
  }
  
  .intro-title {
    font-size: 32px;
  }
  
  .intro-subtitle {
    font-size: 15px;
  }
  
  .intro-features {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-card {
    padding: 25px 20px;
  }
  
  .intro-stats {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .setup-card {
    padding: 30px 20px;
  }
  
  .setup-header h3 {
    font-size: 24px;
  }
  
  .question-count-selector {
    flex-direction: column;
  }
  
  .count-btn {
    width: 100%;
  }
  
  .quiz-content-wrapper {
    padding: 25px 20px;
  }
  
  .question-title {
    font-size: 18px;
  }
  
  .option-item {
    padding: 15px 18px;
  }
  
  .option-key {
    width: 28px;
    height: 28px;
    font-size: 14px;
  }
  
  .option-text {
    font-size: 15px;
  }
  
  .quiz-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .action-btn {
    width: 100%;
    justify-content: center;
    padding: 14px 25px;
  }
  
  .result-card {
    padding: 30px 20px;
  }
  
  .result-detail {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .detail-item {
    padding: 20px 15px;
  }
  
  .score-circle {
    width: 140px;
    height: 140px;
  }
  
  .score-number {
    font-size: 36px;
  }
  
  .records-list {
    max-height: 250px;
  }
}

@media (max-width: 480px) {
  .badge-icon {
    font-size: 48px;
  }
  
  .intro-title {
    font-size: 26px;
  }
  
  .intro-subtitle {
    font-size: 14px;
  }
  
  .feature-icon {
    font-size: 40px;
  }
  
  .feature-card h3 {
    font-size: 18px;
  }
  
  .setup-icon {
    font-size: 48px;
  }
  
  .result-icon {
    font-size: 48px;
  }
  
  .result-card h3 {
    font-size: 24px;
  }
}
</style>
