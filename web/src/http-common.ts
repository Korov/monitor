import axios, { AxiosInstance } from 'axios'

const apiClient: AxiosInstance = axios.create({
  baseURL: 'http://172.21.227.18:18091/api',
  headers: {
    Accept: 'application/json',
    'Content-type': 'application/json',
    // 'Access-Control-Allow-Origin': '*',
  },
})
export default apiClient
