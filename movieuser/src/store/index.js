import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
export default new Vuex.Store({
    state: {
        selectedMovie:null,
        selectedSession:null,
        selectedOrder:null,
    },
    mutations: {
        setSelectedMovie(state,movie){
            state.selectedMovie=movie
        },
        setSelectedSession(state,session){
            state.selectedSession=session
        },
        setSelectedOrder(state,order){
            state.selectedOrder=order
        }
    },
    actions: {
        setCurrentOrder({ commit }, order) {
            commit('setSelectedOrder', order);
        }},
    getters: {}
})