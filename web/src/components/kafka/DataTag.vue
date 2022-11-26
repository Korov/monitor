<template>
  <div class="dataTag" :title="title">
    <span class="left">{{ left }}</span>
    <span class="right">{{ right }}</span>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DataTag',
  props: ['left', 'right', 'title'],
})
</script>

<style scoped>
.dataTag {
  margin: 0 4px 1px 0;
  /*margin-right: 4px;*/
  display: inline-block;
  white-space: nowrap;
}

.dataTag span {
  display: inline-block;
  font-size: 12px;
  line-height: 12px;
  color: cornsilk;
  /*overflow: hidden;*/
}

.left {
  padding: 4px 4px 4px 6px;
  background-color: #40485b;
  border-radius: 4px 0 0 4px;
}

.right {
  padding: 4px 6px 4px 4px;
  background-color: #409eff;
  border-radius: 0 4px 4px 0;
}
</style>
