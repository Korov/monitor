<template>
  <div>
    <div style="display: flex; margin-top: 5px">
      <div class="label">group:</div>
      <el-select
        v-model="group"
        class="inputTable"
        filterable
        allow-create
        @focus="getGroupByTopic"
        placeholder="请选择group"
      >
        <el-option v-for="item in groups" :key="item" :label="item" :value="item"></el-option>
      </el-select>
      <div class="label label2">partition:</div>
      <el-select
        v-model="partition"
        class="inputTable"
        filterable
        @focus="getTopicDetail"
        @change="setPartition(partition)"
        placeholder="请选择partition"
      >
        <el-option v-for="item in partitions" :key="item" :label="item.toString()" :value="item"></el-option>
      </el-select>
      <div class="label label2">offset:</div>
      <el-input
        v-model="offset"
        :disabled="disabled"
        placeholder="请输入offset"
        class="inputTable"
        style="width: 200px"
        clearable
      ></el-input>
    </div>

    <div>
      <el-radio v-model="mode" label="earliest" :disabled="disabled">历史消息(earliest)</el-radio>
      <el-radio v-model="mode" label="latest" :disabled="disabled">最新消息(latest)</el-radio>
    </div>

    <div style="margin: 10px 0; display: flex">
      <span style="line-height: 30px; color: #000;">高亮显示关键字：</span>

      <el-input
        v-model="keyword"
        placeholder="请输入关键字"
        style="width: 300px;"
        clearable
      ></el-input>
    </div>

    <div class="frame">
      <div class="left">
        <div class="stopBar">
          <el-icon v-if="!on">
            <VideoPlay @click="start" />
          </el-icon>
          <el-icon v-if="on">
            <VideoPause @click="stop" />
          </el-icon>
          <el-icon>
            <Message @click="send" />
          </el-icon>
        </div>
        <div class="loadBar">
          <el-icon v-if="on">
            <Loading />
          </el-icon>
        </div>
      </div>
      <div class="right" ref="frame">
        <el-table :data="message" height="100%" stripe>
          <el-table-column prop="topic" label="topic" :width="100"></el-table-column>
          <el-table-column prop="partition" label="partition" :width="90"></el-table-column>
          <el-table-column prop="offset" label="offset" :width="90"></el-table-column>
          <el-table-column prop="key" label="key" :width="100"></el-table-column>
          <el-table-column prop="message" label="value"></el-table-column>
        </el-table>
      </div>
    </div>
    <div>
      <DataTag :right="consumeCount" left="已消费消息条数"></DataTag>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import { ElMessage } from 'element-plus'
import apiClient from '@/http-common'
import DataTag from '@cp/kafka/DataTag.vue'
import { Message } from '@/types'

export default defineComponent({
  name: 'Consumer',
  components: { DataTag },
  props: {
    topic: String,
    sourceId: Number,
    broker: String,
  },
  setup(props) {
    let group = ref('')
    let groups = ref<string[]>([])
    let consumeCount = ref(0)
    let message = ref<Message[]>([])
    let keyword = ref('')
    let partition = ref(0)
    let partitions = ref<number[]>([])
    let offset = ref(-1)
    let startOffset = ref(0)
    let endOffset = ref(0)
    let disabled = ref(false)
    let mode = ref('earliest')
    let on = ref(false)
    let websocket: WebSocket
    let autoScrollToBottom = ref(false)

    function getGroupByTopic() {
      if (props.sourceId == null) {
        ElMessage.error('请选择Kafka环境')
      }
      apiClient
        .get(`/kafka/consumer/query?sourceId=${props.sourceId}`)
        .then((response) => {
          let groupNames: string[] = response.data.data
          for (let i = 0; i < groupNames.length; i++) {
            groups.value.push(groupNames[i])
          }
        })
        .catch((error) => {
          ElMessage.error('失败' + error.message)
        })
    }

    function getTopicDetail() {
      partitions.value = []
      apiClient
        .get(`/kafka/topic/detail/query?sourceId=${props.sourceId}&topic=${props.topic}`)
        .then((response) => {
          for (const partitionsKey in response.data.data.partitions) {
            partitions.value.push(response.data.data.partitions[partitionsKey].partition)
          }
        })
        .catch((error) => {
          ElMessage.error('查询topic分区详情失败' + error.message)
        })
    }

    function setPartition(partition: number) {
      console.log(partition)
      offset.value = partition
    }

    function start() {
      if (props.sourceId == null || props.topic == '' || props.topic == null) {
        ElMessage.error('请先选择kafka和topic')
      }

      if (group.value == null || group.value == '') {
        ElMessage.error('请先输入group')
      }

      consumeCount.value = 0

      if ('WebSocket' in window) {
        // let url = `ws://${address.value}/kafka/consumer/socket`
        let url = `ws://localhost:8091/kafka/consumer/socket?topic=${props.topic}&broker=${props.broker}&group=${group.value}&reset=${mode.value}&partition=${partition.value}&offset=${offset.value}`
        websocket = new WebSocket(url)
        initWebSocket()
      } else {
        on.value = false
        alert('当前浏览器 不支持')
      }
      disabled.value = true
      on.value = true
    }

    function send() {
      const data = {
        topic: props.topic,
        broker: props.broker,
        group: group.value,
        reset: mode.value,
        partition: partition.value,
        offset: offset.value,
      }
      console.log(data)
      websocket.send(JSON.stringify(data))
    }

    function stop() {
      disabled.value = false
      on.value = false
      websocket.close()
    }

    function initWebSocket() {
      // 连接错误
      websocket.onerror = () => {
        console.log('WebSocket连接发生错误   状态码：' + websocket.readyState)
        disabled.value = false
        on.value = false
      }
      // 连接成功
      websocket.onopen = () => {
        console.log('WebSocket连接成功    状态码：' + websocket.readyState)
      }
      // 收到消息的回调
      websocket.onmessage = (event) => {
        consumeCount.value++
        console.log(event.data)
        message.value.push(JSON.parse(event.data))
        console.log(message.value[message.value.length - 1])
        /*if (autoScrollToBottom.value) {
          scroll()
        }*/
      }
      // 连接关闭的回调
      websocket.onclose = (error) => {
        console.log(error)
        console.log(`WebSocket连接关闭    状态码：${websocket.readyState}, error:${error.reason}`)
        disabled.value = false
        on.value = false
      }
      // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
      window.onbeforeunload = () => {
        websocket.close()
        disabled.value = false
        on.value = false
      }
    }

    return {
      group,
      getGroupByTopic,
      groups,
      consumeCount,
      message,
      keyword,
      partition,
      partitions,
      getTopicDetail,
      setPartition,
      offset,
      endOffset,
      disabled,
      startOffset,
      mode,
      on,
      start,
      stop,
      autoScrollToBottom,
      send,
    }
  },
})
</script>

<style scoped lang="scss">
.label {
  margin-right: 1px;
  padding: 0 10px;
  line-height: 30px;
  background-color: #06b176;
  color: #fff;
  height: 30px;
  border-radius: 3px;
  font-size: 16px;
  font-weight: 700;
}

.label2:nth-child(n + 2) {
  margin-left: 10px;
}

.inputTable {
  margin: 0 5px 10px 1px;
}

.loadBar {
  margin: 5px 0 5px 2px;
}

.stopBar {
  margin: 5px 0 5px 2px;
}

.frame {
  display: flex;
  min-height: calc(100vh - 350px);
  box-shadow: 1px 1px 5px #72767b;
  margin-bottom: 5px;

  .left {
    width: 30px;
    font-size: 30px;
    border-right: #8c939d 1px solid;
    background-color: #edebeb;
    display: flex;
    flex-direction: column;

    i {
      font-size: 20px;
      text-align: center;
      font-weight: 900;
      margin: 1px;
      color: #151313;
      border-radius: 4px;
    }

    i:hover {
      background-color: #cacac6;
    }

    .active {
      background-color: #ababa7;
    }
  }

  .right {
    overflow-y: scroll;

    width: 100%;
    background-color: #fbf7f7;

    .history {
      margin: 3px;
      background-color: #fde6c0;
      display: flex;

      .index {
        width: 25px;
        background-color: #f6bf6a;
      }

      .index-c {
        width: 25px;
        font-size: 10px;
      }

      .message {
        background-color: #0fe6c0;
        padding: 0 5px;
        white-space: pre-line; //  字符串\n换行
      }

      .autoBreak {
        overflow: hidden;
        overflow-wrap: break-word;
      }
    }
  }
}
</style>
