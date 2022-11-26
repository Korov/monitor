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
    <el-button style="margin-top: 5px" type="primary" @click="showDialog(true)"
      >添加环境</el-button
    >

    <el-dialog
      v-model:visible="dialogFormVisible"
      title="添加kafka地址"
      width="600px"
    >
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input clearable v-model="configName"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input clearable v-model="configBroker"></el-input>
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
    let configBroker = '127.0.0.1:9092'
    let configName = ''
    let dialogFormVisible = false
    let warning = false

    function getAllSource() {
      apiClient
        .get('/kafka/query')
        .then((response) => {
          sources = response.data.data
          console.log(sources)
        })
        .catch((error) => {
          ElMessage(`查询所有kafka环境失败，${error.message}`)
        })
    }

    getAllSource()

    function deleteSource(id: number) {
      apiClient
        .delete('/kafka/delete?id=' + id)
        .then((response) => {
          console.log(response)
          ElMessage.success('删除kafka环境成功')
          getAllSource()
        })
        .catch((error) => {
          ElMessage.error('删除kafka环境失败' + error.message)
        })
    }

    function handleDelete(index: any, row: any) {
      deleteSource(row.id)
    }

    function add() {
      apiClient
        .post('/kafka/add', { name: configName, broker: configBroker })
        .then((response) => {
          console.log(response)
          warning = true
          getAllSource()
        })
        .catch((error) => {
          ElMessage.error('添加kafka环境失败' + error.message)
        })
    }

    function showDialog(value: boolean) {
      dialogFormVisible = value
      console.log(`click show dialog ${dialogFormVisible}`)
    }

    return {
      sources,
      handleDelete,
      add,
      configBroker,
      configName,
      dialogFormVisible,
      warning,
      showDialog,
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
