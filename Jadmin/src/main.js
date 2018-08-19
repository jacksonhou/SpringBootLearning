import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'

import i18n from './lang' // Internationalization
import './icons' // icon
import './errorLog' // error log
import './permission' // permission control
// import './mock' // simulation data
import {
  hasPermission
} from './utils/hasPermission'

import * as filters from './filters' // global filters
import $ from 'jquery'
// import layer from 'vue-layer'

Vue.use(Element, {
  size: 'small', // set element-ui default size (medium / small / mini)
  // i18n: (key, value) => i18n.t(key, value),
  locale
})

// 全局的常量
Vue.prototype.hasPermission = hasPermission

// https://www.npmjs.com/package/vue-layer
// Vue.prototype.$layer = layer(Vue);
// Vue.prototype.$layer = layer(Vue, {
//     msgtime: 3,//目前只有一项，即msg方法的默认消失时间，单位：秒
// });

// register global utility filters.
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
})
