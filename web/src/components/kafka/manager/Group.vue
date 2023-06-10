<template>
  <div>
    <div style="display: flex">
      <kafkaSelect @kafka_change="kafkaChange"></kafkaSelect>

      <el-input placeholder="搜索group" v-model="keyword" class="searchInput" clearable @change="searchGroup">
        <el-button icon="el-icon-search" @click="searchGroup"></el-button>
      </el-input>
    </div>
    <el-table :data="tableData" stripe border>
      <el-table-column prop="name" label="group名称"></el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" @click="getGroupDetail(scope.row.name)">详情</el-button>
          <!--          <el-popconfirm title="确定删除吗？" @confirm="deleteConfirm(scope.row.name)">-->
          <el-button type="danger" @click="getGroupDetail(scope.row.name)" style="margin-left: 5px">删除</el-button>
          <!--          </el-popconfirm>-->
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="group消费偏移量详情" v-model="dialogTableVisible">
      <group-table :data="detail"></group-table>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from 'vue'
import GroupTable from '@cp/kafka/GroupTable.vue'
import KafkaSelect from '@cp/kafka/KafkaSelect.vue'
import { ElMessage } from 'element-plus'
import apiClient from '@/http-common'

export default defineComponent({
  name: 'Group',
  emits: ['kafka_change'],
  components: {
    GroupTable,
    KafkaSelect,
  },
  setup() {
    interface Group {
      name: string
      internal: boolean
    }

    let detail = ref()
    let dialogTableVisible = ref(false)
    let keyword = ref('')
    let tableData = ref<Group[]>([])

    function searchGroup(sourceId: Ref<number>) {
      if (sourceId.value == null) {
        ElMessage.error('请选择Kafka环境')
      }
      apiClient
        .get(`/kafka/consumer/query?sourceId=${sourceId.value}`)
        .then((response) => {
          let groupNames: string[] = response.data.data
          for (let i = 0; i < groupNames.length; i++) {
            tableData.value.push({
              name: groupNames[i],
              internal: false,
            })
          }
        })
        .catch((error) => {
          ElMessage.error('失败' + error.message)
        })
    }

    function kafkaChange(sourceId: Ref<number>) {
      searchGroup(sourceId)
    }

    function getGroupDetail(value: string) {
      console.log(value)
    }

    function deleteConfirm(value: string) {
      console.log(value)
    }

    return {
      detail,
      dialogTableVisible,
      keyword,
      searchGroup,
      kafkaChange,
      tableData,
      getGroupDetail,
      deleteConfirm,
    }
  },
})
</script>

<style lang="scss" scoped>
.searchInput {
  width: 250px;
  height: 30px;
  margin-left: 5px;
}
</style>
