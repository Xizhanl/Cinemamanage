import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'login',
        component:()=>import('../views/UserLogin.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/UserRegister.vue')
    },
    {
        path: '/UserIndex',
        name: 'userindex',
        component: () => import('../views/UserIndex.vue'),
        redirect: { name: 'userhome'},
        children:[
            {
                path: '/UserHome',
                name: 'userhome',
                component:()=>import('../views/home/UserHome.vue'),
            },
            {
                path: '/SearchMovie',
                name: 'searchmovie',
                component:()=>import('../views/SearchMovie.vue'),
            },
            {
                path: '/PersonalCenter',
                component:()=>import('../views/personalcenter/PersonalCenter.vue'),
                children: [
                    {
                        path: '', // 默认子路由
                        name: 'personalcenter',
                        redirect: 'UserInfo'
                    },
                    {
                        path: 'UserInfo',
                        name: 'userinfo',
                        component:()=>import('../views/personalcenter/UserInfo.vue'),
                    },
                    {
                        path: 'OrderInfo',
                        name: 'orderinfo',
                        component:()=>import('../views/personalcenter/OrderInfo.vue'),
                    }
                ],
            },
            {
                path: '/UserMovie',
                component:()=>import('../views/movie/UserMovie.vue'),
                children:[
                    {
                        path: '', // 默认子路由
                        name: 'usermovie',
                        redirect: 'OngoingMovie'
                    },
                    {
                    path: 'OngoingMovie',
                    name: 'ongoingmovie',
                    component:()=>import('../views/movie/OngoingMovie.vue'),
                    },
                    {
                        path: 'UpcomingMovie',
                        name: 'upcomingmovie',
                        component:()=>import('../views/movie/UpcomingMovie.vue'),
                    }
                ],
            },
            {
                path: '/UserRanking',
                name: 'userranking',
                component:()=>import('../views/ranking/UserRanking.vue'),
            },
            {
                path: '/UserAbout',
                name: 'userabout',
                component:()=>import('../views/about/UserAbout.vue'),
            },
            {
                path: '/BuyTicket',
                name: 'buyticket',
                component:()=>import('../views/about/BuyTicket.vue'),
            },
            {
                path: '/ChooseSeat',
                name: 'chooseseat',
                component:()=>import('../views/about/ChooseSeat.vue'),
            },
            {
                path: '/ConfirmOrder',
                name: 'confirmorder',
                component:()=>import('../views/about/ConfirmOrder.vue'),
            },
            {
                path: '/MovieInfo',
                name: 'movieinfo',
                component:()=>import('../views/movie/MovieInfo.vue'),
            },
            {
                path: '/DetailInfo',
                name: 'detailinfo',
                component:()=>import('../views/personalcenter/DetailInfo.vue'),
            },

        ]
    }
]

const router = new VueRouter({
    routes,
    //
    scrollBehavior() {
        return { x: 0, y: 0 }
    }
})
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
    return VueRouterPush.call(this, to).catch(err => err)
}
export default router
