<template>
  <div>
    <kafkaSelect @kafka_change="kafkaChange"></kafkaSelect>

    <el-table :data="tableData" border stripe class="tableData">
      <el-table-column label="broker id" prop="id">
        <template #default="scope">
          <span style="margin-right: 5px">{{ scope.row.id }}</span>
          <el-tag v-if="scope.row.controller" effect="dark" size="small" type="danger">controller</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="host" prop="host"></el-table-column>
      <el-table-column label="端口" prop="port"></el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import KafkaSelect from '@cp/kafka/KafkaSelect.vue'

export default defineComponent({
  name: 'Cluster',
  components: {
    KafkaSelect,
  },
  setup() {
    let tableData = ref(null)

    function kafkaChange(sourceId: Ref<number>) {
      console.log('source id:' + sourceId.value)
      getCluster(sourceId)
    }

    function getCluster(value: Ref<number>) {
      apiClient
        .post('/kafka/cluster/info', { sourceId: value.value })
        .then((response) => {
          console.log(response.data.data)
          if (response.data.success) tableData.value = response.data.data
          else ElMessage.error(response.data.message)
        })
        .catch((error) => {
          ElMessage.error('查询集群信息失败' + error.message)
        })
    }

    return {
      tableData,
      kafkaChange,
    }
  },
})
</script>

<style scoped lang="scss">
.tableData {
  padding: 5px 5px 0 5px;
}
</style>
