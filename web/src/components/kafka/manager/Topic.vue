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
      <el-button size="small" style="margin-bottom: 5px" type="primary" @click="dialogFormVisible = true"
        >创建topic
      </el-button>
    </div>
    <div>
      <el-table :data="tableData" border max-height="650" size="small" stripe class="tableData">
        <el-table-column label="topic名称" prop="name"></el-table-column>
        <el-table-column label="类型">
          <template #default="scope">
            <div v-if="scope.row.internal">系统topic</div>
            <div v-else>用户topic</div>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button round size="small" type="info" @click="getTopicDetail(scope.row.name)"> TopicDetail </el-button>
            <el-popconfirm v-if="!scope.row.internal" title="确定删除吗？" @confirm="deleteConfirm(scope.row.name)">
              <template #reference>
                <el-button round size="small" type="danger">Delete</el-button>
              </template>
            </el-popconfirm>
            <el-button round size="small" type="info" @click="getGroupByTopic(scope.row.name)"> Consumer </el-button>
          </template>
        </el-table-column>
      </el-table>
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
        <el-collapse-item v-for="item in groups" :key="item.value" :name="item.value" :title="item.value">
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
import { defineComponent, ref } from 'vue'
import { ElMessage } from 'element-plus'
import apiClient from '@/http-common'
import { Topic } from '@/types'
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
    let keyword = ''
    let sourceId: number = 0
    let tableData: any = null
    let topic: Topic = {
      name: '',
      partition: 0,
      replica: 0,
    }
    let topics: Topic[] = []
    let selectedTopic: string = ''
    let partitions: any[] = []
    let dialogTableVisible = ref(false)
    let dialogFormVisible = ref(false)
    let topicDetail: any = undefined
    let groups: any[] = []
    let groupVisible: boolean = true
    let groupDetail = undefined

    function setKeyword(value: string) {
      keyword = value
    }

    function getTopics() {
      if (sourceId == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      let url = `/kafka/topic/query?sourceId=${sourceId}`
      if (keyword != null) {
        url = `${url}&keyword=${keyword}`
      }
      apiClient
        .get(url)
        .then((response) => {
          if (response.data.code) {
            tableData = response.data.data
          } else {
            ElMessage.error(response.data.message)
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有topic失败' + error.message)
        })
    }

    function getAllTopics() {
      if (sourceId == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      apiClient
        .get(`/kafka/topic/query?sourceId=${sourceId}`)
        .then((response) => {
          if (response.data.code) {
            topics = response.data.data
            console.log(tableData)
          } else {
            ElMessage.error(response.data.message)
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有topic失败' + error.message)
        })
    }

    function kafkaChange(value: number) {
      sourceId = value
      getTopics()
    }

    function addTopic() {
      if (sourceId == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (topic.name === null) {
        ElMessage.error('请选择输入Topic名称')
        return
      }
      apiClient
        .post(`/kafka/topic/create`, {
          sourceId: sourceId,
          topic: topic.name,
          partition: topic.partition,
          replica: topic.replica,
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
      if (sourceId == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      apiClient
        .delete(`/kafka/topic/delete?sourceId=${sourceId}&topic=${name}`)
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
      selectedTopic = topic
      apiClient
        .get(`/kafka/topic/detail/query?sourceId=${sourceId}&topic=${selectedTopic}`)
        .then((response) => {
          partitions = response.data.data.partitions
          topicDetail = response.data.data
          dialogTableVisible.value = true
        })
        .catch((error) => {
          ElMessage.error('查询topic分区详情失败' + error.message)
        })
    }

    function getGroupByTopic(value: string) {
      if (sourceId == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (value == null) {
        ElMessage.error('请选择输入Topic名称')
        return
      }
      selectedTopic = value
      apiClient
        .get(`/kafka/consumer/query?sourceId=${sourceId}&topic=${selectedTopic}`)
        .then((response) => {
          groups = response.data.data
          groupVisible = true
        })
        .catch((error) => {
          ElMessage.error('失败' + error.message)
        })
    }

    function handleChange(group: string) {
      if (sourceId == null) {
        ElMessage.error('请选择Kafka环境')
        return
      }
      if (group == null) {
        ElMessage.error('请选择输入Topic名称')
        return
      }

      apiClient
        .get(`/kafka/consumer/detail?sourceId=${sourceId}&group=${group}`)
        .then((response) => {
          if (response.data.code) {
            groupDetail = response.data.data
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

.dialogFooter {
  text-align: right;
}

.tableData {
  padding: 5px 5px 0 5px;
}
</style>
