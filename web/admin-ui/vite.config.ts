import path from 'node:path'
import Vue from '@vitejs/plugin-vue'

import Unocss from 'unocss/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Components from 'unplugin-vue-components/vite'
import VueRouter from 'unplugin-vue-router/vite'

import { defineConfig } from 'vite'

export default defineConfig({
  resolve: {
    alias: {
      '~/': `${path.resolve(__dirname, 'src')}/`,
    },
  },
  build:{
    sourcemap: false,
    chunkSizeWarningLimit: 1500,//加大限制的大小将500kb改成1500kb或者更大
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return id.toString().split('node_modules/')[1].split('/')[0].toString();
          }
        }
      }
    }
  },
  // server:{
    // proxy:{
    //   "/api":{
    //     target: 'http://127.0.0.1:8080',
    //     changeOrigin: true,
    //     rewrite: (path) => {
    //       console.log('Rewriting path:', path);
    //       path.replace(/^\/api/, '')
    //     }
    //   }
    // }
  // },
  // scss 全局变量配置
  css: {
    preprocessorOptions: {
      scss: {
        // javascriptEnabled: true,
        additionalData: `@use "~/styles/variables.scss" as *;`,
        // additionalData: `@use "~/styles/element/index.scss" as *;`,
        // api: 'modern-compiler',
      },
    },
  },

  plugins: [
    Vue(),
    // https://github.com/posva/unplugin-vue-router
    VueRouter({
      extensions: ['.vue', '.md'],
      dts: 'src/typed-router.d.ts',
    }),
    Components({
      extensions: ['vue', 'md'],
      include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
      resolvers: [
        ElementPlusResolver({
          importStyle: 'sass',
        }),
      ],
      dts: 'src/components.d.ts',
    }),
    Unocss(),
  ],
  ssr: {
    noExternal: ['element-plus'],
  },
})
