import axios, { AxiosInstance } from 'axios'
import { backendHost } from '../vite.config'

const apiClient: AxiosInstance = axios.create({
  baseURL: `http://${backendHost}:18091/api`,
  headers: {
    Accept: 'application/json',
    'Content-type': 'application/json'
    // 'Access-Control-Allow-Origin': '*',
  }
})
export default apiClient
