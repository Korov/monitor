<template>
  <div>
    <kafkaSelect @kafka_change="kafkaChange"></kafkaSelect>

    <vxe-table :data="brokers" border stripe class="brokers">
      <vxe-column title="broker id" field="id">
        <template #default="scope">
          <span style="margin-right: 5px">{{ scope.row.id }}</span>
          <el-tag v-if="scope.row.controller" effect="dark" size="small" type="danger">controller</el-tag>
        </template>
      </vxe-column>
      <vxe-column title="host" field="host"></vxe-column>
      <vxe-column title="端口" field="port"></vxe-column>
    </vxe-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import KafkaSelect from '@cp/kafka/KafkaSelect.vue'
import { Broker } from '@/types'

export default defineComponent({
  name: 'Cluster',
  components: {
    KafkaSelect,
  },
  setup() {
    let brokers = ref<Broker[]>([])

    function kafkaChange(sourceId: Ref<number>) {
      console.log('source id:' + sourceId.value)
      getCluster(sourceId)
    }

    function getCluster(value: Ref<number>) {
      apiClient
        .post('/kafka/cluster/info', { sourceId: value.value })
        .then((response) => {
          if (response.data.code == 1) {
            brokers.value = response.data.data
            console.log(brokers)
          } else {
            ElMessage.error(response.data.message)
          }
        })
        .catch((error) => {
          ElMessage.error('查询集群信息失败' + error.message)
        })
    }

    return {
      brokers,
      kafkaChange,
    }
  },
})
</script>

<style scoped lang="scss">
.brokers {
  padding: 5px 5px 0 5px;
}
</style>
