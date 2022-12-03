<template>
  <div>
    <el-input
      v-model="message"
      :autosize="{ minRows: 6, maxRows: 15 }"
      placeholder="上键：向上翻输入历史(最多保存10条最近输入记录); 下键：向下翻输入历史; ctrl+enter: 发送消息"
      rows="6"
      show-word-limit
      size="default"
      type="textarea"
      @keyup.enter="keyDown"
      @keyup.up="scrollUpHistory"
      @keyup.down="scrollDownHistory"
    >
    </el-input>
    <el-button style="margin: 5px 0" type="primary" @click="produce"><i class="iconfont icon-Send"></i> 发送</el-button>
    <div class="frame">
      <div class="left">
        <i class="el-icon-delete" @click="clear"></i>
      </div>
      <div ref="history" class="right">
        <div v-for="item in messages" :key="item" class="history">
          <div class="index">
            <div class="index-c">
              <i v-if="item.success" class="el-icon-circle-check success"></i>
              <i v-else class="el-icon-circle-close fail"></i>
              <i v-if="!item.batch" class="iconfont icon-single" title="单条消息"></i>
              <i v-else class="iconfont icon-multi" title="批量多条消息"></i>
            </div>
          </div>
          <div class="content">{{ item.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'Producer',
  setup() {
    let message = ref('')
    let messages = ref<string[]>([])
    let historyMessages = ref<string[]>([])
    let maxHistorySize = ref(10)
    let cursor = ref(-1)
    let batch = ref(false)

    function produce() {
      messages.value.push(message.value)
      historyMessages.value.push(message.value)
      cursor.value++
      message.value = ''
    }

    function keyDown(e: any) {
      if (e.ctrlKey && e.keyCode === 13) {
        produce()
      } else {
        //用户点击了enter触发
      }
    }

    function scrollUpHistory() {
      message.value = historyMessages.value[cursor.value]
      cursor.value--
      if (cursor.value < 0) {
        cursor.value = historyMessages.value.length - 1
      }
    }

    function scrollDownHistory() {
      cursor.value++
      if (cursor.value >= historyMessages.value.length) {
        cursor.value = 0
      }
      message.value = historyMessages.value[cursor.value]
    }

    function clear() {
      console.log('clear message')
      messages.value.length = 0
    }

    function scroll() {
      console.log('scroll')
    }

    function processHistory(message: string) {
      console.log(message)
      /*history.value.push(message)
      if (history.value.length > maxHistorySize.value) {
        history.value = history.value.slice(-maxHistorySize.value)
      }
      cursor.value = history.value.length - 1*/
    }

    return {
      message,
      messages,
      historyMessages,
      maxHistorySize,
      cursor,
      batch,
      keyDown,
      scrollUpHistory,
      scrollDownHistory,
      clear,
      scroll,
      processHistory,
      produce,
    }
  },
})
</script>

<style scoped></style>
