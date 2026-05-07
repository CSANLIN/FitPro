import request from '@/utils/request'

export const paymentApi = {
  createOrder: (data) => request.post('/payment/create', data),
  mockPay: (orderNo) => request.post(`/payment/mock-pay/${orderNo}`),
  getStatus: (orderNo) => request.get(`/payment/status/${orderNo}`)
}
