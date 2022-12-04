<template>
  <div>
    <h3>kafka消费消息：</h3>
    <div style="display: flex; margin-top: 5px">
      <div class="label">Kafka:</div>
      <kafkaSelect class="inputTable" @kafka_change="getTopics"></kafkaSelect>
      <div class="label">topic:</div>
      <el-select v-model="topic" clearable filterable placeholder="选择topic" class="inputTable">
        <el-option v-for="item in topics" :key="item.name" :label="item.name" :value="item.name"></el-option>
      </el-select>
    </div>

    <consumer :broker="broker" :sourceId="sourceId" :topic="topic"></consumer>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from 'vue'
import KafkaSelect from '@cp/kafka/KafkaSelect.vue'
import { Topic } from '@/types'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import Consumer from '@cp/kafka/Consumer.vue'

export default defineComponent({
  name: 'Consume',
  components: {
    Consumer,
    KafkaSelect,
  },
  setup() {
    let sourceId = ref(0)
    let topics = ref<Topic[]>([])
    let topic = ref('')
    let partition = ref<number>()
    let partitions = ref<number[]>([])
    let messageKey = ref('')
    let broker = ref('')

    function getTopics(value: Ref<number>, host: string) {
      sourceId.value = value.value
      broker.value = host
      apiClient
        .get(`/kafka/topic/query?sourceId=${value.value}`)
        .then((response) => {
          if (response.data.code) {
            topics.value = response.data.data
          } else {
            ElMessage.error(response.data.message)
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有topic失败' + error.message)
        })
    }

    return {
      sourceId,
      topics,
      topic,
      partition,
      partitions,
      messageKey,
      getTopics,
      broker,
    }
  },
})
</script>

<style scoped lang="scss">
.label {
  margin-right: 1px;
  padding: 0 10px;
  line-height: 40px;
  background-color: #06b176;
  color: #fff;
  height: 40px;
  border-radius: 3px;
  font-size: 16px;
  font-weight: 700;
}

.label:nth-child(n + 2) {
  margin-left: 10px;
}

.inputTable {
  margin: 0 5px 10px 1px;
}
</style>
