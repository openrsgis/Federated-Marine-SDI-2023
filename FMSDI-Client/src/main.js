import Vue from 'vue'
import App from './App.vue'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import router from './router'
import UrlFactory from './utils/UrlFactory'
import vueBus from './utils/vueBus'
import { VueAxios } from './utils/axios'
import { FormModel } from 'ant-design-vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import i18n from './locales'
global.jQuery = require('jquery')
var $ = global.jQuery
window.$ = $

Vue.use(ElementUI)
Vue.config.productionTip = false
Vue.use(Antd)
Vue.use(vueBus)
Vue.use(FormModel)
Vue.use(VueAxios)
Vue.prototype.$urlFactory = new UrlFactory()
new Vue({
  i18n,
  render: h => h(App),
  router, //引用router
  el: '#app',
  components: { App },
  template: '<App>/'
}).$mount('#app')

