import request from './request'

export const getAllResources = () => {
  return request({
    url: '/dialect/list',
    method: 'get'
  })
}

export const getResourcesByCity = (cityCode) => {
  return request({
    url: `/dialect/city/${cityCode}`,
    method: 'get'
  })
}

export const getResourcesByCategory = (category) => {
  return request({
    url: `/dialect/category/${category}`,
    method: 'get'
  })
}

export const getResourcesByDifficulty = (difficulty) => {
  return request({
    url: `/dialect/difficulty/${difficulty}`,
    method: 'get'
  })
}

export const getResourceById = (id) => {
  return request({
    url: `/dialect/${id}`,
    method: 'get'
  })
}

export const addResource = (data) => {
  return request({
    url: '/dialect/add',
    method: 'post',
    data
  })
}

export const updateResource = (data) => {
  return request({
    url: '/dialect/update',
    method: 'put',
    data
  })
}

export const deleteResource = (id) => {
  return request({
    url: `/dialect/delete/${id}`,
    method: 'delete'
  })
}
