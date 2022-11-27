<template>
  <el-select class="kafkaSelect" v-model="sourceId" placeholder="选择kafka环境" @change="selectKafka">
    <el-option v-for="source in sources" :key="source.id" :label="source.name" :value="source.id"></el-option>
  </el-select>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import { Config } from '@/types'

export default defineComponent({
  name: 'KafkaSelect',
  emits: ['kafka_change'],
  setup(props, { emit }) {
    let sourceId: number = 0
    let sources = ref<Config[]>([])

    function getAllSource() {
      apiClient
        .get('/kafka/query')
        .then((response) => {
          console.log('all sources' + response.data.data)
          sources.value = response.data.data
        })
        .catch((error) => {
          ElMessage.error('查询所有kafka环境失败' + error.message)
        })
    }
    getAllSource()

    function selectKafka() {
      if (sourceId != null) {
        emit('kafka_change', sourceId)
      }
    }

    return {
      sourceId,
      sources,
      selectKafka,
    }
  },
})
</script>

<style scoped lang="scss">
.kafkaSelect {
  margin: 0 5px 10px 10px;
}
</style>
