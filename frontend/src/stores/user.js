import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const phone = ref(localStorage.getItem('phone') || '')
  const email = ref(localStorage.getItem('email') || '')
  const role = ref(localStorage.getItem('role') || 'USER')
  const isLoggedIn = ref(!!token.value)

  const loginAction = async (loginForm) => {
    const res = await login(loginForm)
    if (res.code === 200) {
      token.value = String(res.data.id)
      userId.value = String(res.data.id)
      username.value = res.data.username
      phone.value = res.data.phone || ''
      email.value = res.data.email || ''
      role.value = res.data.role || 'USER'
      isLoggedIn.value = true
      localStorage.setItem('token', String(res.data.id))
      localStorage.setItem('userId', String(res.data.id))
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('phone', res.data.phone || '')
      localStorage.setItem('email', res.data.email || '')
      localStorage.setItem('role', res.data.role || 'USER')
      return { success: true, role: res.data.role || 'USER' }
    } else {
      return { success: false, message: res.message }
    }
  }

  const registerAction = async (registerForm) => {
    const res = await register(registerForm)
    if (res.code === 200) {
      return { success: true, message: res.message }
    } else {
      return { success: false, message: res.message }
    }
  }

  const updateUserInfo = (userInfo) => {
    username.value = userInfo.username
    phone.value = userInfo.phone
    email.value = userInfo.email
    localStorage.setItem('username', userInfo.username)
    localStorage.setItem('phone', userInfo.phone || '')
    localStorage.setItem('email', userInfo.email || '')
  }

  const logout = () => {
    token.value = ''
    userId.value = ''
    username.value = ''
    phone.value = ''
    email.value = ''
    role.value = 'USER'
    isLoggedIn.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('phone')
    localStorage.removeItem('email')
    localStorage.removeItem('role')
  }

  return {
    token,
    userId,
    username,
    phone,
    email,
    role,
    isLoggedIn,
    loginAction,
    registerAction,
    updateUserInfo,
    logout
  }
})
