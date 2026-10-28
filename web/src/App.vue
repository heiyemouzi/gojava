<template>
  <el-container style="height: 100vh">
    <el-header v-if="isLogin" style="background:#fff; border-bottom:1px solid #eee; display:flex; align-items:center">
      <strong style="margin-right:24px">gojava SaaS</strong>
      <el-menu mode="horizontal" :default-active="$route.path" router style="border:none; flex:1">
        <el-menu-item index="/order/create">创建订单</el-menu-item>
        <el-menu-item index="/order/list">我的订单</el-menu-item>
        <el-menu-item index="/product/list">商品库存</el-menu-item>
        <el-menu-item index="/tenant/list">租户管理</el-menu-item>
        <el-menu-item index="/tenant/users">用户管理</el-menu-item>
      </el-menu>
      <span v-if="username" style="margin-right:12px">{{ username }}</span>
      <el-button size="small" @click="logout">退出</el-button>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isLogin = computed(() => route.path !== '/login')
const username = computed(() => userStore.username)

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>
