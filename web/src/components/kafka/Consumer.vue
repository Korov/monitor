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
      <span style="line-height: 40px">高亮显示关键字:</span>

      <el-input
        v-model="keyword"
        placeholder="请输入关键字"
        style="width: 300px; margin-left: 10px"
        clearable
      ></el-input>
    </div>

    <div class="frame">
      <div class="left">
        <div class="stopBar">
          <i class="gg-play-button-o" v-if="!on" @click="start" style="color: #12b812"></i>
          <i class="gg-play-stop-o" v-if="on" @click="stop" style="color: #f83b3b"></i>
        </div>
        <div class="loadBar">
          <i class="gg-loadbar-doc" v-if="on"></i>
        </div>
      </div>
      <div class="right" ref="frame">
        <el-table :data="message" height="100%" stripe>
          <el-table-column prop="topic" label="topic" :width="100"></el-table-column>
          <el-table-column prop="partition" label="partition" :width="90"></el-table-column>
          <el-table-column prop="offset" label="offset" :width="90"></el-table-column>
          <el-table-column prop="key" label="key" :width="100"></el-table-column>
          <el-table-column prop="value" label="value"></el-table-column>
        </el-table>
      </div>
    </div>
    <div>
      <data-tag :right="consumeCount" left="已消费消息条数"></data-tag>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'Consumer',
})
</script>

<style scoped></style>
