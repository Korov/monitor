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
    let sourceId = ref<number>()
    let sources = ref<Config[]>([])
    let sourceMap: Map<number, Config> = new Map<number, Config>()

    function getAllSource() {
      apiClient
        .get('/kafka/query')
        .then((response) => {
          sources.value = response.data.data
          for (let index in sources.value) {
            sourceMap.set(sources.value[index].id, sources.value[index])
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有kafka环境失败' + error.message)
        })
    }
    getAllSource()

    function selectKafka() {
      if (sourceId.value != null) {
        let source = sourceMap.get(sourceId.value)
        console.log(`select kafka:`)
        if (source?.broker == undefined) {
          emit('kafka_change', sourceId, '')
        } else {
          console.log(source.broker)
          emit('kafka_change', sourceId, source.broker)
        }
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
