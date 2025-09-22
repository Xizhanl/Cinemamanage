import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css';
import axios from "axios";
Vue.prototype.$axios=axios;
Vue.config.productionTip = false

Vue.prototype.$httpUrl='http://localhost:9999'//可以用request.js处理后端地址接口
Vue.use(ElementUI);
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
