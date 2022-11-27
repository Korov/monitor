<template>
  <div>
    <div style="display: flex">
      <kafkaSelect @kafka_change="kafkaChange"></kafkaSelect>

      <el-input
        placeholder="搜索group"
        v-model="keyword"
        style="width: 250px; margin-left: 5px"
        clearable
        @keyup.enter.native="searchGroup"
      >
        <el-button icon="el-icon-search" @click="searchGroup"></el-button>
      </el-input>
    </div>
    <el-table :data="tableData" stripe border>
      <el-table-column prop="name" label="group名称"></el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" circle type="primary" @click="getGroupDetail(scope.row.name)">
            <i class="iconfont icon-detail"></i>
          </el-button>
          <el-popconfirm title="确定删除吗？" @onConfirm="deleteConfirm(scope.row.name)" v-if="!scope.row.internal">
            <el-button size="small" circle type="danger" style="margin-left: 5px" :disabled="!auth.remove">
              <i class="el-icon-delete"></i>
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="group消费偏移量详情" v-model="dialogTableVisible">
      <group-table :data="detail"></group-table>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'Group',
})
</script>

<style scoped></style>
