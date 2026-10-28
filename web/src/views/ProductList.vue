<template>
  <h2>商品库存 / 价格</h2>
  <el-table :data="list" border>
    <el-table-column prop="id" label="ID" width="80" />
    <el-table-column prop="sku" label="SKU" width="160" />
    <el-table-column prop="name" label="商品名" />
    <el-table-column label="单价" width="120">
      <template #default="{ row }">{{ (row.priceCents/100).toFixed(2) }} 元</template>
    </el-table-column>
    <el-table-column prop="stock" label="库存" width="100">
      <template #default="{ row }">
        <el-tag :type="row.stock > 0 ? '' : 'danger'">{{ row.stock }}</el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const list = ref([])
onMounted(async () => { list.value = await request.get('/product/list') })
</script>
