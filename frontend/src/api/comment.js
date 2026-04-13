import request from './request'

export const getComments = (resourceType, resourceId) => {
  return request({
    url: '/comment/list',
    method: 'get',
    params: { resourceType, resourceId }
  })
}

export const getCommentById = (id) => {
  return request({
    url: `/comment/${id}`,
    method: 'get'
  })
}

export const addComment = (data) => {
  return request({
    url: '/comment/add',
    method: 'post',
    data
  })
}

export const deleteComment = (id) => {
  return request({
    url: `/comment/delete/${id}`,
    method: 'delete'
  })
}

export const likeComment = (id) => {
  return request({
    url: `/comment/like/${id}`,
    method: 'post'
  })
}
