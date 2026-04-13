import request from './request'

export const getAllUsers = () => {
  return request({
    url: '/admin/users',
    method: 'get'
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

export const resetPassword = (id, newPassword) => {
  return request({
    url: `/admin/user/reset-password/${id}`,
    method: 'post',
    data: { newPassword }
  })
}

export const getSystemInfo = () => {
  return request({
    url: '/admin/system/info',
    method: 'get'
  })
}

export const getDatabaseStatus = () => {
  return request({
    url: '/admin/database/status',
    method: 'get'
  })
}

// 音频审核相关接口
export const getAudioReviewList = (status) => {
  return request({
    url: '/admin/audio/review',
    method: 'get',
    params: { status }
  })
}

export const approveAudio = (id) => {
  return request({
    url: `/admin/audio/${id}/approve`,
    method: 'post'
  })
}

export const rejectAudio = (id, reason) => {
  return request({
    url: `/admin/audio/${id}/reject`,
    method: 'post',
    data: { reason }
  })
}
