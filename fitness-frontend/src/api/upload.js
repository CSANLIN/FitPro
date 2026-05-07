import request from '@/utils/request'

export const uploadApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/files/upload', formData)
  }
}
