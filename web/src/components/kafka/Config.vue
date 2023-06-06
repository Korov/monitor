<template>
  <div>
    <el-table :data="sources" border stripe class="config">
      <el-table-column :label="$t('kafka.clusterName')" prop="name"></el-table-column>
      <el-table-column :label="$t('kafka.address')" prop="broker"></el-table-column>
      <el-table-column :label="$t('kafka.operation')">
        <template #default="scope">
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">
            {{ t(`kafka.delete`) }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button style="margin-top: 5px" type="primary" @click="dialogFormVisible = true">
      {{ t(`kafka.addEnvironment`) }}
    </el-button>

    <el-dialog v-model="dialogFormVisible" :title="$t('kafka.addKafkaAddress')" width="600px">
      <el-form label-width="80px">
        <el-form-item :label="$t('kafka.name')">
          <el-input clearable v-model="configName"></el-input>
        </el-form-item>
        <el-form-item :label="$t('kafka.address')">
          <el-input clearable v-model="configBroker"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialogFooter">
        <el-button @click="dialogFormVisible = false">{{ t(`kafka.cancel`) }}</el-button>
        <el-button type="primary" @click="add()">{{ t(`kafka.confirm`) }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import { Config } from '@/types'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'ConfigPage',
  setup() {
    let sources = ref<Config[]>([])
    let configBroker = ref('127.0.0.1:9092')
    let configName = ref('')
    let dialogFormVisible = ref(false)
    let warning = false

    const { t } = useI18n({ useScope: 'global' })

    function getAllSource() {
      apiClient
        .get('/kafka/query')
        .then((response) => {
          sources.value = response.data.data
        })
        .catch((error) => {
          ElMessage(`${t('kafka.queryError')}${error.message}`)
        })
    }

    getAllSource()

    function deleteSource(id: number) {
      apiClient
        .delete('/kafka/delete?id=' + id)
        .then((response) => {
          console.log(response)
          ElMessage.success(`${t('kafka.deleteSuccess')}`)
          getAllSource()
        })
        .catch((error) => {
          ElMessage.error(`${t('kafka.deleteFail')}` + error.message)
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
          ElMessage.success(`${t('kafka.addKafkaSuccess')}`)
          getAllSource()
          dialogFormVisible.value = false
          configName.value = ''
        })
        .catch((error) => {
          ElMessage.error(`${t('kafka.addKafkaSuccess')}` + error.message)
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
      t,
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
