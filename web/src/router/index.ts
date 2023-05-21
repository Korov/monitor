import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { websiteTitle } from '@/config'
import { addDynamicMenuAndRoutes } from '@/main'
import { ref } from 'vue'

let constantRoutes: RouteRecordRaw[] = [
  {
    // path: '/404',
    path: '/:pathMatch(.*)*', // 防止浏览器刷新时路由未找到警告提示: vue-router.mjs:35 [Vue Router warn]: No match found for location with path "/xxx"
    component: () => import('@/components/kafka/Config.vue'),
  },
]

let isRefreshed = ref(false)

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
})

// 确保每次刷新之后还是原来地界面
router.beforeEach((to, from, next) => {
  if (to.name == undefined) {
    addDynamicMenuAndRoutes()
    next({ ...to, replace: true })
  } else if (!router.hasRoute(to.name)) {
    addDynamicMenuAndRoutes()
    next({ ...to, replace: true })
  } else {
    next()
  }
})

router.afterEach((to) => {
  const { title } = to.meta
  document.title = title ? `${title} - ${websiteTitle}` : websiteTitle
})

// 动态增加路由
/*router.addRoute("demo", {
  path: `/demo`,
  name: "demo",
  redirect: {
    name: "aaa"
  },
  component: () => import(`@/index.vue`),
  children: []
});*/

export default router
