<template>
  <div style="display: flex; margin-top: 5px">
    <el-input v-model="zkHost" clearable placeholder="Please input ZK Host" />
    <el-button type="primary" @click="queryZkTree()">Query</el-button>
  </div>
  <el-tree :data="allNode" :props="defaultProps" />
  <div>
    <blocks-tree :data="treeData" :horizontal="treeOrientation == '1'" :collapsable="true"></blocks-tree>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue'
import apiClient from '@/http-common'
import { ElMessage } from 'element-plus'

interface Tree {
  label: string
  children?: Tree[]
}

export default defineComponent({
  name: 'ZKTree',
  setup() {
    const defaultProps = {
      children: 'children',
      label: 'label',
    }

    let zkHost = ref<String>('localhost:2183')

    let allNode = ref<Tree[]>([
      {
        label: '',
        children: [],
      },
    ])

    let treeOrientation = ref('0')
    let treeData = reactive({
      label: 'root',
      expand: true,
      some_id: 1,
      children: [
        { label: 'child 1', some_id: 2 },
        { label: 'child 2', some_id: 3 },
        {
          label: 'subparent 1',
          some_id: 4,
          expand: false,
          children: [
            { label: 'subchild 1', some_id: 5 },
            {
              label: 'subchild 2',
              some_id: 6,
              expand: false,
              children: [
                { label: 'subchild 11', some_id: 7 },
                { label: 'subchild 22', some_id: 8 },
              ],
            },
          ],
        },
      ],
    })

    function queryZkTree() {
      apiClient
        .get(`/zookeeper/tree?host=${zkHost.value}`)
        .then((response) => {
          if (response.data.code) {
            let rootTree: Tree = {
              label: response.data.data.path,
              children: new Array<Tree>(),
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
          children: new Array<Tree>(),
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
      treeOrientation,
      treeData,
      defaultProps,
      zkHost,
      queryZkTree,
    }
  },
})
</script>

<style scoped lang="scss"></style>
