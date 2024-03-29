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
import routerStore from '@/stores/routers'
import i18n from '@/locales/i18n'

const app = createApp(Home)
  .use(createPinia()) // 启用 Pinia
  .use(router)
  .use(ElMessage)
  .use(ElementPlus)
  .use(VXETable)
  .use(VueAxios, axios)
  .use(i18n)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')

const modules = import.meta.glob('@/components/**/*.vue')

const routerStoreInfo = routerStore()

export function addDynamicMenuAndRoutes() {
  routerStoreInfo.getRouters().forEach((routerInfo) => {
    if (routerInfo.children !== null) {
      routerInfo.children.forEach((childrenRouter) => {
        router.addRoute(childrenRouter.name, {
          name: childrenRouter.name,
          path: `${routerInfo.path}${childrenRouter.path}`,
          component: modules[`${childrenRouter.component}`]
        })
      })
    } else {
      router.addRoute(routerInfo.name, {
        name: routerInfo.name,
        path: routerInfo.path,
        meta: routerInfo.meta || { title: 'default' },
        redirect: routerInfo.redirect || { name: 'config' }
      })
    }
  })
}

addDynamicMenuAndRoutes()
