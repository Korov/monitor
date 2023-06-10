import axios, { AxiosInstance } from 'axios'

const apiClient: AxiosInstance = axios.create({
  baseURL: 'http://localhost:18091/api',
  headers: {
    Accept: 'application/json',
    'Content-type': 'application/json',
    // 'Access-Control-Allow-Origin': '*',
  },
})
export default apiClient
