import request from './request'

export const getAllResources = () => {
  return request({
    url: '/folk/list',
    method: 'get'
  })
}

export const getResourcesByCity = (cityCode) => {
  return request({
    url: `/folk/city/${cityCode}`,
    method: 'get'
  })
}

export const getResourcesByType = (type) => {
  return request({
    url: `/folk/type/${type}`,
    method: 'get'
  })
}

export const getResourceById = (id) => {
  return request({
    url: `/folk/${id}`,
    method: 'get'
  })
}

export const addResource = (data) => {
  return request({
    url: '/folk/add',
    method: 'post',
    data
  })
}

export const updateResource = (data) => {
  return request({
    url: '/folk/update',
    method: 'put',
    data
  })
}

export const deleteResource = (id) => {
  return request({
    url: `/folk/delete/${id}`,
    method: 'delete'
  })
}
