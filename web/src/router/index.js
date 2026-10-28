import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/order/create', component: () => import('../views/OrderCreate.vue') },
  { path: '/order/list', component: () => import('../views/OrderList.vue') },
  { path: '/product/list', component: () => import('../views/ProductList.vue') },
  { path: '/tenant/list', component: () => import('../views/TenantList.vue') },
  { path: '/tenant/users', component: () => import('../views/UserList.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/order/create' }
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) return '/login'
})

export default router
