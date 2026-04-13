import request from './request'

/**
 * 上传音频
 * @param {FormData} formData - 包含音频文件和信息的 FormData 对象
 * @returns {Promise}
 */
export function uploadAudio(formData) {
  return request({
    url: '/audio/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取音频列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getAudioList(params = {}) {
  return request({
    url: '/audio/list',
    method: 'get',
    params
  })
}

/**
 * 获取单个音频详情
 * @param {number} id - 音频 ID
 * @returns {Promise}
 */
export function getAudioDetail(id) {
  return request({
    url: `/audio/${id}`,
    method: 'get'
  })
}

/**
 * 评价音频
 * @param {number} id - 音频 ID
 * @param {boolean} rated - 是否点赞
 * @returns {Promise}
 */
export function rateAudio(id, rated) {
  return request({
    url: `/audio/${id}/rate`,
    method: 'post',
    data: { rated }
  })
}

/**
 * 添加评论
 * @param {number} id - 音频 ID
 * @param {string} content - 评论内容
 * @returns {Promise}
 */
export function addComment(id, content) {
  return request({
    url: `/audio/${id}/comment`,
    method: 'post',
    data: { content }
  })
}

/**
 * 删除音频
 * @param {number} id - 音频 ID
 * @returns {Promise}
 */
export function deleteAudio(id) {
  return request({
    url: `/audio/${id}`,
    method: 'delete'
  })
}

/**
 * 获取用户自己的音频列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getMyAudios(params = {}) {
  return request({
    url: '/audio/my',
    method: 'get',
    params
  })
}
