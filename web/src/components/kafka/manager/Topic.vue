<template>
  <div>
    <div style="display: flex">
      <kafkaSelect @kafka_change="kafkaChange"></kafkaSelect>
      <el-select
        class="topicSelect"
        v-model="keyword"
        allow-create
        default-first-option
        filterable
        placeholder="请输入topic"
        @change="getTopics"
        @focus="getAllTopics"
      >
        <el-option v-for="item in topics" :key="item.name" :label="item.name" :value="item.name"></el-option>
      </el-select>
      <el-button size="small" class="createButton" type="primary" @click="dialogFormVisible = true">
        创建topic
      </el-button>
    </div>
    <div>
      <vxe-table :data="tableData" border max-height="650" size="small" stripe class="tableData">
        <vxe-column title="topic名称" field="name"></vxe-column>
        <vxe-column title="类型">
          <template #default="scope">
            <div v-if="scope.row.internal">系统topic</div>
            <div v-else>用户topic</div>
          </template>
        </vxe-column>
        <vxe-column title="操作">
          <template #default="scope">
            <el-button round size="small" type="info" @click="getTopicDetail(scope.row.name)"> TopicDetail</el-button>
            <el-popconfirm v-if="!scope.row.internal" title="确定删除吗？" @confirm="deleteConfirm(scope.row.name)">
              <template #reference>
                <el-button round size="small" type="danger">Delete</el-button>
              </template>
            </el-popconfirm>
            <el-button round size="small" type="info" @click="getGroupByTopic(scope.row.name)"> Consumer</el-button>
          </template>
        </vxe-column>
      </vxe-table>
    </div>

    <el-dialog v-model="dialogFormVisible" title="创建topic" width="500px">
      <el-form label-width="80px">
        <el-form-item label="topic名称">
          <el-input v-model="topic.name" clearable placeholder="请输入topic名称"></el-input>
        </el-form-item>
        <el-form-item label="分区数量">
          <el-input-number v-model="topic.partition" :min="1" label="请输入分区数量"></el-input-number>
        </el-form-item>
        <el-form-item label="副本数量">
          <el-input-number v-model="topic.replica" :min="1" label="请输入分区数量"></el-input-number>
        </el-form-item>
      </el-form>
      <div class="dialogFooter">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addTopic()">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="groupVisible" :title="'消费 ' + selectedTopic + ' 的group'">
      <div v-if="groups.length === 0">暂无数据</div>
      <el-collapse accordion @change="handleChange">
        <el-collapse-item v-for="item in groups" :key="item" :name="item" :title="item">
          <group-table :data="groupDetail"></group-table>
        </el-collapse-item>
      </el-collapse>
    </el-dialog>

    <el-dialog v-model="dialogTableVisible" :title="selectedTopic + ' 分区详情'">
      <el-table :data="partitions" border max-height="500" size="small" stripe>
        <el-table-column label="分区号" property="partition" width="80"></el-table-column>
        <el-table-column label="leader分区" property="leader">
          <template #default="scope">
            <data-tag
              :right="scope.row.leader.id"
              :title="scope.row.leader.host + ':' + scope.row.leader.port"
              left="broker"
            ></data-tag>
          </template>
        </el-table-column>
        <el-table-column label="所有副本">
          <template #default="scope">
            <data-tag
              v-for="item in scope.row.replicas"
              :key="item.id"
              :right="item.id"
              :title="item.host + ':' + item.port"
              left="broker"
            ></data-tag>
          </template>
        </el-table-column>
        <el-table-column label="isr副本">
          <template #default="scope">
            <data-tag
              v-for="item in scope.row.replicas"
              :key="item.id"
              :right="item.id"
              :title="item.host + ':' + item.port"
              left="broker"
            ></data-tag>
          </template>
        </el-table-column>
        <el-table-column label="最小偏移量" property="beginningOffset"></el-table-column>
        <el-table-column label="最大偏移量" property="endOffset"></el-table-column>
        <el-table-column label="消息数量" width="80">
          <template #default="scope">
            <span>{{ scope.row.endOffset - scope.row.beginningOffset }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from 'vue'
import { ElMessage } from 'element-plus'
import apiClient from '@/http-common'
import { Consumer, Partition, Topic } from '@/types'
import KafkaSelect from '@cp/kafka/KafkaSelect.vue'
import DataTag from '@cp/kafka/DataTag.vue'
import GroupTable from '@cp/kafka/GroupTable.vue'

export default defineComponent({
  name: 'Topic',
  components: {
    KafkaSelect,
    DataTag,
    GroupTable,
  },
  setup() {
    let keyword = ref<string>('')
    let sourceId = ref<number>()
    let tableData = ref<Topic[]>([])
    let topic = ref<Topic>({
      name: '',
      partition: 0,
      replica: 0,
      internal: false,
    })
    let topics = ref<Topic[]>([])
    let selectedTopic = ref('')
    let partitions = ref<Partition[]>()
    let dialogTableVisible = ref(false)
    let dialogFormVisible = ref(false)
    let topicDetail = ref<Topic>()
    let groups = ref<string[]>([])
    let groupVisible = ref(false)
    let groupDetail = ref<Consumer[]>([])

    function setKeyword(value: string) {
      keyword.value = value
    }

    function getTopics() {
      if (sourceId.value == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      let url = `/kafka/topic/query?sourceId=${sourceId.value}`
      if (keyword.value != null && keyword.value != '') {
        url = `${url}&keyword=${keyword.value}`
      }
      apiClient
        .get(url)
        .then((response) => {
          if (response.data.code) {
            tableData.value = response.data.data
          } else {
            ElMessage.error(response.data.message)
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有topic失败' + error.message)
        })
    }

    function getAllTopics() {
      if (sourceId.value == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
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

    function kafkaChange(value: Ref<number>) {
      sourceId.value = value.value
      getTopics()
    }

    function addTopic() {
      if (sourceId.value == null || sourceId.value <= 0) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (topic.value.name == null) {
        ElMessage.error('请选择输入Topic名称')
        return
      }
      apiClient
        .post(`/kafka/topic/create`, {
          sourceId: sourceId.value,
          topic: topic.value.name,
          partition: topic.value.partition,
          replica: topic.value.replica,
        })
        .then((response) => {
          if (response.data.code) {
            ElMessage.success('创建topic成功')
            getTopics()
          }
        })
        .catch((error) => {
          ElMessage.error('查询topic分区详情失败' + error.message)
        })
      dialogFormVisible.value = false
    }

    function deleteConfirm(name: string) {
      if (sourceId.value == null || sourceId.value <= 0) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (name == null || name == '') {
        ElMessage.error('请选择输入Topic')
        return
      }
      apiClient
        .delete(`/kafka/topic/delete?sourceId=${sourceId.value}&topic=${name}`)
        .then((response) => {
          if (response.data.code) {
            ElMessage.success('删除topic成功')
            getTopics()
          } else {
            ElMessage.success('删除topic失败' + response)
            getTopics()
          }
        })
        .catch((error) => {
          ElMessage.error('删除topic失败' + error.message)
        })
    }

    function getTopicDetail(topic: string) {
      if (sourceId.value == null || sourceId.value <= 0) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      selectedTopic.value = topic
      apiClient
        .get(`/kafka/topic/detail/query?sourceId=${sourceId.value}&topic=${selectedTopic.value}`)
        .then((response) => {
          partitions.value = response.data.data.partitions
          topicDetail.value = response.data.data
          dialogTableVisible.value = true
        })
        .catch((error) => {
          ElMessage.error('查询topic分区详情失败' + error.message)
        })
    }

    function getGroupByTopic(value: string) {
      if (sourceId.value == null || sourceId.value <= 0) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (value == null || value == '') {
        ElMessage.error('请选择输入Topic名称')
        return
      }
      console.log(`query source id:${sourceId.value}, value:${value}`)
      selectedTopic.value = value
      apiClient
        .get(`/kafka/consumer/query?sourceId=${sourceId.value}&topic=${selectedTopic.value}`)
        .then((response) => {
          groups.value = response.data.data
          groupVisible.value = true
        })
        .catch((error) => {
          groupVisible.value = false
          ElMessage.error('失败' + error.message)
        })
    }

    function handleChange(group: string) {
      if (sourceId.value == null || sourceId.value <= 0) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (group == null || group == '') {
        ElMessage.error('请选择输入Topic名称')
        return
      }

      apiClient
        .get(`/kafka/consumer/detail?sourceId=${sourceId.value}&group=${group}`)
        .then((response) => {
          if (response.data.code) {
            groupDetail.value = response.data.data
            console.log(groupDetail)
          }
        })
        .catch((error) => {
          ElMessage.error('查询group详情失败' + error.message)
        })
    }

    return {
      keyword,
      setKeyword,
      sourceId,
      getTopics,
      tableData,
      topics,
      topic,
      getAllTopics,
      kafkaChange,
      addTopic,
      deleteConfirm,
      selectedTopic,
      partitions,
      dialogTableVisible,
      topicDetail,
      getTopicDetail,
      groups,
      groupVisible,
      getGroupByTopic,
      groupDetail,
      handleChange,
      dialogFormVisible,
    }
  },
})
</script>

<style scoped lang="scss">
.topicSelect {
  margin: 0 10px 0 5px;
}

.createButton {
  margin-bottom: 5px;
  height: 30px;
}

.dialogFooter {
  text-align: right;
}

.tableData {
  padding: 5px 5px 0 5px;
}
</style>
