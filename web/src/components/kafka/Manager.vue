<template>
  <div>
    <el-tabs v-model="activeName" type="card" class="tabCard">
      <el-tab-pane label="topic管理" name="topic">
        <topic></topic>
      </el-tab-pane>
      <el-tab-pane label="集群管理" name="cluster">
        <cluster></cluster>
      </el-tab-pane>
      <el-tab-pane label="group管理" name="group">
        <group></group>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import Topic from '@cp/kafka/manager/Topic.vue'
import Cluster from '@cp/kafka/manager/Cluster.vue'
import Group from '@cp/kafka/manager/Group.vue'

export default defineComponent({
  name: 'Manager',
  components: {
    Topic,
    Cluster,
    Group,
  },
  setup() {
    let activeName = ref<string>('topic')

    return {
      activeName,
    }
  },
})
</script>

<style scoped lang="scss">
.tabCard {
  background-color: #ffffff;
  height: 400px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 600;
}
</style>
