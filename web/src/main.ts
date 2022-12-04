import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Home from '@/Home.vue'
import router from '@/router'
import ElementPlus, { ElMessage } from 'element-plus'
import 'element-plus/dist/index.css'
import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 全局样式
import '@less/global.less'
import VueAxios from 'vue-axios'
import axios from 'axios'

const app = createApp(Home)
  .use(createPinia()) // 启用 Pinia
  .use(router)
  .use(ElMessage)
  .use(ElementPlus)
  .use(VXETable)
  .use(VueAxios, axios)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
