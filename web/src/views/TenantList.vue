<template>
  <h2>租户管理</h2>
  <el-button type="primary" @click="dialog = true" style="margin-bottom:12px">新建租户</el-button>
  <el-table :data="list" border>
    <el-table-column prop="tenantId" label="租户ID" width="140" />
    <el-table-column prop="name" label="公司名" />
    <el-table-column prop="packageName" label="套餐" width="120">
      <template #default="{ row }">
        <el-tag>{{ row.packageName }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="status" label="状态" width="100">
      <template #default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? '正常' : '停用' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="expiredAt" label="到期日" width="140" />
  </el-table>

  <el-dialog v-model="dialog" title="新建租户" width="420px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="租户ID"><el-input v-model="form.tenantId" /></el-form-item>
      <el-form-item label="公司名"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="套餐">
        <el-select v-model="form.package">
          <el-option label="free" value="free" />
          <el-option label="pro" value="pro" />
          <el-option label="enterprise" value="enterprise" />
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

const list = ref([])
const dialog = ref(false)
const form = reactive({ tenantId: '', name: '', package: 'free' })

async function load() { list.value = await request.get('/tenant/tenant/list') }
onMounted(load)

async function submit() {
  await request.post('/tenant/tenant/create', form)
  ElMessage.success('已创建')
  dialog.value = false
  load()
}
</script>
