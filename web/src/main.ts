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
import VueBlocksTree from 'vue3-blocks-tree'
import 'vue3-blocks-tree/dist/vue3-blocks-tree.css';
import { de } from 'element-plus/es/locale'

let defaultoptions = { treeName: 'blocks-tree' }

const app = createApp(Home)
  .use(createPinia()) // 启用 Pinia
  .use(router)
  .use(ElMessage)
  .use(ElementPlus)
  .use(VXETable)
  .use(VueBlocksTree, defaultoptions)
  .use(VueAxios, axios)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')

const modules = import.meta.glob('@/components/**/*.vue')

const routerStoreInfo = routerStore()

export function addDynamicMenuAndRoutes() {
  routerStoreInfo.getRouters().forEach((store) => {
    if (store.children !== null) {
      store.children.forEach((childrenNode) => {
        router.addRoute(childrenNode.name, {
          name: childrenNode.name,
          path: `${store.path}${childrenNode.path}`,
          component: modules[`.${childrenNode.component}`],
        })
      })
    } else {
      router.addRoute(store.name, {
        name: store.name,
        path: store.path,
        meta: store.meta || { title: 'default' },
        redirect: store.redirect || { name: 'config' },
      })
    }
  })
}

addDynamicMenuAndRoutes()



