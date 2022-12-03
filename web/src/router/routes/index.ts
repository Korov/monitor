import type { RouteRecordRaw } from 'vue-router'

/**
 * 路由配置
 * @description 所有路由都在这里集中管理
 */
const routes: RouteRecordRaw[] = [
  /**
   * 首页
   */
  {
    path: '/',
    name: 'home',
    meta: {
      title: 'Home',
    },
    redirect: {
      name: 'config',
    },
    children: [
      {
        path: 'kafka/config',
        name: 'config',
        component: () => import('@/components/kafka/Config.vue'),
      },
      {
        path: 'kafka/manager',
        name: 'manager',
        component: () => import('@/components/kafka/Manager.vue'),
      },
      {
        path: 'kafka/producer',
        name: 'producer',
        component: () => import('@/components/kafka/operate/Produce.vue'),
      },
      {
        path: 'kafka/consumer',
        name: 'consumer',
        component: () => import('@/components/kafka/operate/Consume.vue'),
      },
    ],
  },
  /**
   * 子路由示例
   */
  /*{
    path: '/foo',
    name: 'foo',
    component: () =>
      import(/!* webpackChunkName: "foo" *!/ '@cp/TransferStation.vue'),
    meta: {
      title: 'Foo',
    },
    redirect: {
      name: 'bar',
    },
    children: [
      {
        path: 'bar',
        name: 'bar',
        component: () =>
          import(/!* webpackChunkName: "bar" *!/ '@views/foo/bar.vue'),
        meta: {
          title: 'Bar',
        },
      },
    ],
  },*/
]

export default routes
