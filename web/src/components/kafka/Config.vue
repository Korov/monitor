<template>
  <div>
    <el-table :data="sources" border stripe class="config">
      <el-table-column label="集群名称" prop="name"></el-table-column>
      <el-table-column label="地址" prop="broker"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
            >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button
      style="margin-top: 5px"
      type="primary"
      @click="dialogFormVisible = true"
      >添加环境</el-button
    >

    <el-dialog v-model="dialogFormVisible" title="添加kafka地址" width="600px">
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input clearable v-model="name"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input clearable v-model="broker"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialogFooter">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="add()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'

export default defineComponent({
  name: 'ConfigPage',
  setup() {
    let sources: never[] = []
    function getAllSource() {
      apiClient
        .get('/kafka/query')
        .then((response) => {
          sources = response.data.data
        })
        .catch((error) => {
          ElMessage(`查询所有kafka环境失败，${error.message}`)
        })
    }
    getAllSource()
    return {
      sources,
    }
  },
})
</script>

<style scoped lang="scss">
.config {
  border-radius: 5px;
}

.dialogFooter {
  text-align: right;
}
</style>
