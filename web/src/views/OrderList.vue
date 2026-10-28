<template>
  <h2>我的订单</h2>
  <el-table :data="list" border>
    <el-table-column prop="orderNo" label="订单号" width="220" />
    <el-table-column label="总额" width="120">
      <template #default="{ row }">{{ (row.totalCents/100).toFixed(2) }} 元</template>
    </el-table-column>
    <el-table-column prop="status" label="状态" width="120">
      <template #default="{ row }">
        <el-tag :type="row.status === 'CREATED' ? 'success' : 'info'">
          {{ row.status === 'CREATED' ? '已创建' : '已取消' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="createdAt" label="下单时间" />
    <el-table-column label="操作" width="120">
      <template #default="{ row }">
        <el-button v-if="row.status === 'CREATED'" size="small" type="danger" @click="cancel(row)">取消</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'

const list = ref([])
async function load() { list.value = await request.get('/order/list') }
onMounted(load)

async function cancel(row) {
  await ElMessageBox.confirm(`确认取消订单 ${row.orderNo}？`, '提示', { type: 'warning' })
  await request.post('/order/cancel', null, { params: { orderNo: row.orderNo } })
  ElMessage.success('已取消')
  load()
}
</script>
