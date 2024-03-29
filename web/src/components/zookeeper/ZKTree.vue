<template>
  <div style="display: flex; margin-top: 5px">
    <el-select class="kafkaSelect" v-model="sourceId" placeholder="选择Zookeeper环境" @change="selectZookeeper">
      <el-option v-for="source in sources" :key="source.id" :label="`${source.name}`" :value="source.id">
        <span style="float: left">{{ source.name }}</span>
        <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">{{ source.address }}</span>
      </el-option>
    </el-select>
    <el-input v-model="zkPath" clearable placeholder="请输入需要查询的路径" style="width: 10%; height: 32px"></el-input>
    <el-switch v-model="recursion" style="padding-left: 10px; padding-right: 10px" />
    <el-button type="primary" @click="queryZkTree()">Query</el-button>
  </div>

  <el-tree :data="allNode" node-key="label" :props="defaultProps" default-expand-all>
    <template #default="{ node, data }">
      <span style="width: 100%">
        <span>{{ node.label }}</span>
        <el-text
          v-if="data.content != null && data.content.length > 0"
          style="float: right; color: var(--el-text-color-secondary)"
          type="primary"
        >
          {{ data.content }}
        </el-text>
      </span>
    </template>
  </el-tree>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'
import { ZookeeperConfig } from '@/types'
import { backendHost } from '../../../vite.config'

interface Tree {
  label: string
  content: string
  children?: Tree[]
}

export default defineComponent({
  name: 'ZKTree',
  setup() {
    const defaultProps = {
      children: 'children',
      label: 'label'
    }

    let sourceId = ref<number>()
    let sources = ref<ZookeeperConfig[]>([])
    let sourceMap: Map<number, ZookeeperConfig> = new Map<number, ZookeeperConfig>()
    let zkHost = ref<String>(`${backendHost}:2183`)
    let zkPath = ref<String>('/')
    let recursion = ref<Boolean>(true)

    function getAllSource() {
      apiClient
        .get('/zookeeper/address/query')
        .then((response) => {
          sources.value = response.data.data
          for (let index in sources.value) {
            sourceMap.set(sources.value[index].id, sources.value[index])
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有zookeeper环境失败' + error.message)
        })
    }

    getAllSource()

    function selectZookeeper() {
      if (sourceId.value == null) {
        ElMessage.error('请选择zookeeper环境')
      } else {
        let zkSource = sourceMap.get(sourceId.value)
        if (zkSource != undefined) {
          zkHost.value = zkSource.address
        }
      }
    }

    let allNode = ref<Tree[]>([
      {
        label: '',
        content: '',
        children: []
      }
    ])

    function queryZkTree() {
      apiClient
        .get(`/zookeeper/tree?host=${zkHost.value}&path=${zkPath.value}&recursion=${recursion.value}`)
        .then((response) => {
          if (response.data.code) {
            let rootTree: Tree = {
              label: response.data.data.path,
              content: response.data.data.data,
              children: new Array<Tree>()
            }
            if (response.data.data.childNodes != null && response.data.data.childNodes.length > 0) {
              extractChildNode(rootTree, response.data.data.childNodes)
            }
            allNode.value = [rootTree]
          } else {
            ElMessage.error(response.data.message)
          }
        })
        .catch((error) => {
          ElMessage.error('查询所有zookeeper node失败' + error.message)
        })
    }

    function extractChildNode(tree: Tree, childNodes: any[]) {
      let allChildTree: Tree[] = new Array<Tree>()
      childNodes.forEach((childNode) => {
        let childTree: Tree = {
          label: childNode.path,
          content: childNode.data,
          children: new Array<Tree>()
        }
        allChildTree.push(childTree)
        if (childNode.childNodes != null && childNode.childNodes.length > 0) {
          extractChildNode(childTree, childNode.childNodes)
        }
      })
      tree.children = tree.children?.concat(allChildTree)
    }

    return {
      allNode,
      defaultProps,
      zkHost,
      zkPath,
      recursion,
      queryZkTree,
      selectZookeeper,
      sources,
      sourceId
    }
  }
})
</script>

<style scoped lang="scss">
.kafkaSelect {
  margin: 0 5px 10px 10px;
}
</style>
