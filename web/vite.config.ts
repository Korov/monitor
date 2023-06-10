import { defineConfig } from 'vite'
import path from 'path'
import vue from '@vitejs/plugin-vue'
import components from 'unplugin-vue-components/vite'
import AutoImport from 'unplugin-auto-import/vite'
import banner from 'vite-plugin-banner'
import pkg from './package.json'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

const resolve = (dir: string): string => path.resolve(__dirname, dir)

// https://vitejs.dev/config/
export default defineConfig({
  /**
   * 如果生产部署和开发路径不一样，可以在这里动态配置
   * @see https://cn.vitejs.dev/config/#base
   */
  base: '/',

  /**
   * 本地开发服务，也可以配置接口代理
   * @see https://cn.vitejs.dev/config/#server-proxy
   */
  server: {
    port: 18091,
    proxy: {
      '^/api': {
        target: 'http://172.21.227.18:8091',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
  build: {
    chunkSizeWarningLimit: 2048,
  },

  resolve: {
    /**
     * 配置目录别名
     * @see https://cn.vitejs.dev/config/#resolve-alias
     */
    alias: {
      // 兼容webpack的习惯
      '@': resolve('src'),
      '@img': resolve('src/assets/img'),
      '@less': resolve('src/assets/less'),
      '@libs': resolve('src/libs'),
      '@cp': resolve('src/components'),
      '@views': resolve('src/views'),
      // 兼容webpack的静态资源
      '~@': resolve('src'),
      '~@img': resolve('src/assets/img'),
      '~@less': resolve('src/assets/less'),
      '~@libs': resolve('src/libs'),
      '~@cp': resolve('src/components'),
      '~@views': resolve('src/views'),
    },
  },

  css: {
    

    /**
     * 预处理器选项可以在这里配置
     * @see https://cn.vitejs.dev/config/#css-preprocessoroptions
     */
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
        modifyVars: {
          'primary-color': '#1890ff',
          hack: `true; @import '@less/config.less'`,
        },
      },
    },
  },

  plugins: [
    vue(),

    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),

    /**
     * 自动导入组件，不用每次都 import
     * @see https://github.com/antfu/unplugin-vue-components#configuration
     */
    components({
      dirs: [resolve('src/components')],
      extensions: ['vue', 'ts'],
      deep: true,
      dts: false,
      resolvers: [ElementPlusResolver()],
    }),

    /**
     * 版权注释
     * @see https://github.com/chengpeiquan/vite-plugin-banner#advanced-usage
     */
    banner(
      `/**\n * name: ${pkg.name}\n * version: v${pkg.version}\n * description: v${pkg.description}\n * author: ${pkg.author}\n */`
    ),
  ],
})
