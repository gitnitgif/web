import request from './request'

// 获取所有资源
export const getAllResources = () => {
  return request({
    url: '/admin/resource/list',
    method: 'get'
  })
}

// 获取单个资源
export const getResource = (code) => {
  return request({
    url: `/admin/resource/${code}`,
    method: 'get'
  })
}

// 保存资源
export const saveResource = (data) => {
  return request({
    url: '/admin/resource/save',
    method: 'post',
    data
  })
}

// 更新资源
export const updateResource = (data) => {
  return request({
    url: '/admin/resource/update',
    method: 'post',
    data
  })
}

// 删除资源
export const deleteResource = (id) => {
  return request({
    url: `/admin/resource/delete/${id}`,
    method: 'post'
  })
}
