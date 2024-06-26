import { defineStore } from 'pinia'
import { Router } from '@/types'

const routerStore = defineStore('routerStore', {
  state: (): { myRouters: Router[] } => ({
    myRouters: [
      {
        path: '/',
        name: 'home',
        meta: {
          title: 'Home',
        },
        redirect: {
          name: 'config',
        },
        component: null,
        children: [
          {
            path: 'kafka/config',
            name: 'config',
            component: '/src/components/kafka/Config.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: 'kafka/manager',
            name: 'manager',
            component: '/src/components/kafka/Manager.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: 'kafka/producer',
            name: 'producer',
            component: '/src/components/kafka/operate/Produce.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: 'kafka/consumer',
            name: 'consumer',
            component: '/src/components/kafka/operate/Consume.vue',
            meta: null,
            redirect: null,
            children: null,
          },
        ],
      },
      {
        path: '/zookeeper',
        name: 'zookeeper',
        meta: {
          title: 'Zookeeper',
        },
        redirect: {
          name: 'zktree',
        },
        component: null,
        children: [
          {
            path: '/zkconfig',
            name: 'zkconfig',
            component: '/src/components/zookeeper/ZkConfig.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: '/zktree',
            name: 'zktree',
            component: '/src/components/zookeeper/ZKTree.vue',
            meta: null,
            redirect: null,
            children: null,
          },
        ],
      },
    ],
  }),

  actions: {
    getMyRouters(): Router[] {
      return this.myRouters
    },
  },
})

export default routerStore
