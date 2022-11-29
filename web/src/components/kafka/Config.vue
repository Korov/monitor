<template>
  <div>
    <el-table :data="sources" border stripe class="config">
      <el-table-column label="集群名称" prop="name"></el-table-column>
      <el-table-column label="地址" prop="broker"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除 </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button style="margin-top: 5px" type="primary" @click="dialogFormVisible = true">添加环境</el-button>

    <el-dialog v-model="dialogFormVisible" title="添加kafka地址" width="600px">
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
import { defineComponent, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import { Config } from '@/types'

export default defineComponent({
  name: 'ConfigPage',
  setup() {
    let sources = ref<Config[]>([])
    let configBroker = ref('127.0.0.1:9092')
    let configName = ref('')
    let dialogFormVisible = ref(false)
    let warning = false

    function getAllSource() {
      apiClient
        .get('/kafka/query')
        .then((response) => {
          sources.value = response.data.data
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
        .post('/kafka/add', {
          name: configName.value,
          broker: configBroker.value,
        })
        .then((response) => {
          console.log(response)
          warning = true
          ElMessage.success('添加kafka环境成功')
          getAllSource()
          dialogFormVisible.value = false
          configName.value = ''
        })
        .catch((error) => {
          ElMessage.error('添加kafka环境失败' + error.message)
        })
    }

    return {
      sources,
      handleDelete,
      add,
      configBroker,
      configName,
      dialogFormVisible,
      warning,
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
