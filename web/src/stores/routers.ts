import { defineStore } from 'pinia'

export const navStore = defineStore('navInfo', {
  state: () => ({
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
        component: '/components/kafka/Config.vue',
      },
      {
        path: 'kafka/manager',
        name: 'manager',
        component: '/components/kafka/Manager.vue',
      },
      {
        path: 'kafka/producer',
        name: 'producer',
        component: '/components/kafka/operate/Produce.vue',
      },
      {
        path: 'kafka/consumer',
        name: 'consumer',
        component: '/components/kafka/operate/Consume.vue',
      },
    ],
  }),
  getters: {
    routers: (state) => state,
  },
  actions: {},
})
