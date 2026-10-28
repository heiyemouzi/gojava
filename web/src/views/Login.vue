<template>
  <div style="max-width:380px; margin:100px auto">
    <el-card>
      <h2 style="text-align:center">gojava SaaS 登录</h2>
      <el-form :model="form" @submit.prevent="onLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="onLogin">登 录</el-button>
      </el-form>
      <p style="color:#999; font-size:12px; margin-top:12px">默认账号 admin / Admin@123</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const form = reactive({ username: 'admin', password: 'Admin@123' })
const loading = ref(false)
const router = useRouter()
const userStore = useUserStore()

async function onLogin() {
  loading.value = true
  try {
    const data = await request.post('/auth/login', form)
    userStore.setLogin(data.accessToken, form.username)
    ElMessage.success('登录成功')
    router.push('/order/create')
  } finally {
    loading.value = false
  }
}
</script>
