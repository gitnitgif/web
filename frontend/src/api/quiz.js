import request from './request'

export const getAllQuizzes = () => {
  return request({
    url: '/quiz/list',
    method: 'get'
  })
}

export const getQuizzesByCity = (cityCode) => {
  return request({
    url: `/quiz/city/${cityCode}`,
    method: 'get'
  })
}

export const getRandomQuizzes = (cityCode, limit = 10) => {
  return request({
    url: `/quiz/random/${cityCode}`,
    method: 'get',
    params: { limit }
  })
}

export const submitQuiz = (data) => {
  return request({
    url: '/quiz/submit',
    method: 'post',
    data
  })
}

export const getRecords = (userId) => {
  return request({
    url: `/quiz/records/${userId}`,
    method: 'get'
  })
}
