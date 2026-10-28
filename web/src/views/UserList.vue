<template>
  <h2>用户管理</h2>
  <el-space style="margin-bottom:12px">
    <span>租户：</span>
    <el-select v-model="tenantId" @change="load" style="width:200px">
      <el-option v-for="t in tenants" :key="t.tenantId" :label="t.name" :value="t.tenantId" />
    </el-select>
    <el-button type="primary" @click="openCreate">新建用户</el-button>
  </el-space>

  <el-table :data="list" border>
    <el-table-column prop="id" label="ID" width="60" />
    <el-table-column prop="username" label="登录名" width="140" />
    <el-table-column prop="realName" label="姓名" width="140" />
    <el-table-column prop="roles" label="角色" />
    <el-table-column prop="status" label="状态" width="100">
      <template #default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? '正常' : '禁用' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="180">
      <template #default="{ row }">
        <el-button size="small" @click="toggle(row)">
          {{ row.status === 1 ? '禁用' : '启用' }}
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog v-model="dialog" title="新建用户" width="420px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="登录名"><el-input v-model="form.username" /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="初始密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.roles">
          <el-option label="user" value="user" />
          <el-option label="admin" value="admin" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialog = false">取消</el-button>
      <el-button type="primary" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const tenants = ref([])
const list = ref([])
const tenantId = ref('default')
const dialog = ref(false)
const form = reactive({ username: '', realName: '', password: '', roles: 'user' })

async function loadTenants() {
  tenants.value = await request.get('/tenant/tenant/list')
}
async function load() {
  list.value = await request.get('/tenant/user/list', { params: { tenantId: tenantId.value } })
}
onMounted(async () => { await loadTenants(); await load() })

function openCreate() {
  Object.assign(form, { username: '', realName: '', password: '', roles: 'user' })
  dialog.value = true
}
async function submit() {
  await request.post('/tenant/user/create', { ...form, tenantId: tenantId.value })
  ElMessage.success('已创建')
  dialog.value = false
  load()
}
async function toggle(row) {
  const next = row.status === 1 ? 0 : 1
  await request.post(`/tenant/user/${row.id}/status`, null, { params: { status: next } })
  ElMessage.success('已更新')
  load()
}
</script>
