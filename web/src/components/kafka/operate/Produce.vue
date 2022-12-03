<template>
  <div>
    <h3>kafka生产消息：</h3>
    <div style="display: flex; margin-top: 5px">
      <kafkaSelect @kafka_change="kafkaChange"></kafkaSelect>
      <el-select v-model="topic" clearable filterable placeholder="选择topic" class="topicSelect">
        <el-option v-for="item in topics" :key="item.name" :label="item.name" :value="item.name"></el-option>
      </el-select>
      <el-select
        v-model="partition"
        clearable
        filterable
        placeholder="请选择partition"
        @focus="getTopicDetail"
        class="topicSelect"
      >
        <el-option v-for="item in partitions" :key="item" :label="item.toString()" :value="item"></el-option>
      </el-select>
      <el-input v-model="messageKey" class="keyInput" clearable placeholder="Please input key" />
    </div>

    <Producer :sourceId="sourceId" :topic="topic" :partition="partition" :messageKey="messageKey"></Producer>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, Ref } from 'vue'
import KafkaSelect from '@cp/kafka/KafkaSelect.vue'
import Producer from '@cp/kafka/Producer.vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import { Topic } from '@/types'

export default defineComponent({
  name: 'Produce',
  components: {
    KafkaSelect,
    Producer,
  },
  setup() {
    let sourceId = ref(0)
    let topics = ref<Topic[]>([])
    let topic = ref('')
    let partition = ref<number>()
    let partitions = ref<number[]>([])
    let messageKey = ref('')

    function kafkaChange(value: Ref<number>) {
      sourceId.value = value.value
      getTopics()
    }

    function getTopics() {
      apiClient
        .get(`/kafka/topic/query?sourceId=${sourceId.value}`)
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

    function getTopicDetail() {
      partitions.value.length = 0
      apiClient
        .get(`/kafka/topic/detail/query?sourceId=${sourceId.value}&topic=${topic.value}`)
        .then((response) => {
          for (const partitionsKey in response.data.data.partitions) {
            partitions.value.push(response.data.data.partitions[partitionsKey].partition)
          }
          console.log(partitions.value)
        })
        .catch((error) => {
          ElMessage.error('查询topic分区详情失败' + error.message)
        })
    }

    return {
      sourceId,
      topics,
      topic,
      partition,
      partitions,
      kafkaChange,
      getTopicDetail,
      messageKey,
    }
  },
})
</script>

<style scoped lang="scss">
.topicSelect {
  margin: 0 5px 0 5px;
}

.keyInput {
  margin: 0 5px 0 5px;
  width: 200px;
}
</style>
