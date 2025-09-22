import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component:()=>import('../components/Login.vue')
  },
  {
    path: '/Index',
    name: 'index',
    component:()=>import('../components/Index.vue'),
    redirect: '/Home',
    children:[
      {
        path: '/Home',
        name: 'home',
        component:()=>import('../components/Home.vue'),
      },
      {
        path: '/self',
        name: 'Self',
        component: () => import('../components/Self.vue') // 确保路径正确
      },
      {
        path: '/Cinema',
        name: 'cinema',
        component:()=>import('../components/cinema/Cinema.vue'),
      },
      {
        path: '/MovieManage',
        name: 'moviemanage',
        component:()=>import('../components/movie/MovieManage.vue')
      },
      {
        path: '/MovieTypes',
        name: 'movietypes',
        component:()=>import('../components/movie/MovieTypes.vue')
      },
      {
        path: '/Hall',
        name: 'hall',
        component:()=>import('../components/hall/Hall.vue'),
      },
      {
        path: '/Session',
        name: 'session',
        component:()=>import('../components/hall/Session.vue'),
      },
      {
        path: '/Order',
        name: 'order',
        component:()=>import('../components/order/Order.vue'),
      },
      {
        path: '/UserManage',
        name: 'usermanage',
        component:()=>import('../components/user/UserManage.vue'),
      },

      {
        path: '/Math',
        name: 'math',
        component:()=>import('../components/math/Math.vue')
      }
    ]
  },


]

const router = new VueRouter({
  mode:'history',
  routes
})

const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
  return VueRouterPush.call(this, to).catch(err => err)
}
export default router
