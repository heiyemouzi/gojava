<template>
  <h2>创建订单</h2>
  <el-table :data="items" border style="margin-bottom:16px">
    <el-table-column prop="name" label="商品" />
    <el-table-column prop="sku" label="SKU" width="160" />
    <el-table-column label="单价" width="120">
      <template #default="{ row }">{{ (row.priceCents/100).toFixed(2) }} 元</template>
    </el-table-column>
    <el-table-column prop="stock" label="库存" width="100" />
    <el-table-column label="数量" width="180">
      <template #default="{ row }">
        <el-input-number v-model="row.qty" :min="1" :max="row.stock" size="small" />
      </template>
    </el-table-column>
  </el-table>

  <el-alert v-if="totalCents" :title="`合计：${(totalCents/100).toFixed(2)} 元`" type="success" style="margin-bottom:12px" />
  <el-button type="primary" :loading="submitting" @click="submit">提交订单</el-button>
  <el-result v-if="result" icon="success" :title="`下单成功 ${result.orderNo}`" :sub-title="`总额 ${(result.totalCents/100).toFixed(2)} 元`">
    <template #extra>
      <el-button type="primary" @click="$router.push('/order/list')">查看我的订单</el-button>
      <el-button @click="reset">再下一单</el-button>
    </template>
  </el-result>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../api/request'

const products = ref([])
const submitting = ref(false)
const result = ref(null)

onMounted(async () => {
  products.value = await request.get('/product/list')
  products.value.forEach(p => { p.qty = 1 })
})

const totalCents = computed(() =>
  products.value.reduce((s, p) => s + p.priceCents * (p.qty || 0), 0)
)

async function submit() {
  submitting.value = true
  try {
    const items = products.value
      .filter(p => p.qty > 0)
      .map(p => ({ productId: p.id, qty: p.qty }))
    result.value = await request.post('/order/create', { items })
  } finally {
    submitting.value = false
  }
}

function reset() {
  result.value = null
  products.value.forEach(p => { p.qty = 1 })
}
</script>
