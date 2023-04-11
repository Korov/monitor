import { defineStore } from 'pinia'
import { Router } from '@/types'

const routerStore = defineStore('routerStore', {
  state: (): { routers: Router[] } => ({
    routers: [
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
            component: '/components/kafka/Config.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: 'kafka/manager',
            name: 'manager',
            component: '/components/kafka/Manager.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: 'kafka/producer',
            name: 'producer',
            component: '/components/kafka/operate/Produce.vue',
            meta: null,
            redirect: null,
            children: null,
          },
          {
            path: 'kafka/consumer',
            name: 'consumer',
            component: '/components/kafka/operate/Consume.vue',
            meta: null,
            redirect: null,
            children: null,
          },
        ],
      },
    ],
  }),

  actions: {
    getRouters(): Router[] {
      return this.routers
    },
  },
})

export default routerStore
