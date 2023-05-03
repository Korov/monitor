<template>
  <div style="display: flex; margin-top: 5px">
    <el-input v-model="zkHost" clearable placeholder="Please input ZK Host" />
    <el-button type="primary" @click="queryZkTree()">Query</el-button>
  </div>
  <el-tree :data="allNode" :props="defaultProps" />
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import apiClient from "@/http-common";
import { ElMessage } from "element-plus";

interface Tree {
  label: string;
  children?: Tree[];
}

export default defineComponent({
  name: "ZKTree",
  setup() {
    const defaultProps = {
      children: "children",
      label: "label"
    };

    let zkHost = ref<String>("localhost:2183");

    let allNode = ref<Tree[]>();

    function queryZkTree() {
      apiClient
        .get(`/zookeeper/tree?host=${zkHost.value}`)
        .then((response) => {
          if (response.data.code) {
            let rootTree: Tree = {
              label: response.data.data.path,
              children: new Array<Tree>()
            };
            if (response.data.data.childNodes != null && response.data.data.childNodes.length > 0) {
              extractChildNode(rootTree, response.data.data.childNodes);
            }
            allNode.value = [rootTree]
          } else {
            ElMessage.error(response.data.message);
          }
        })
        .catch((error) => {
          ElMessage.error("查询所有zookeeper node失败" + error.message);
        });
    }

    function extractChildNode(tree: Tree, childNodes: any[]) {
      let allChildTree: Tree[] = new Array<Tree>();
      childNodes.forEach((childNode) => {
        let childTree: Tree = {
          label: childNode.path,
          children: new Array<Tree>()
        };
        allChildTree.push(childTree);
        if (childNode.childNodes != null && childNode.childNodes.length > 0) {
          extractChildNode(childTree, childNode.childNodes);
        }
      });
      tree.children = tree.children?.concat(allChildTree);
    }

    return {
      allNode,
      defaultProps,
      zkHost,
      queryZkTree
    };
  }
});
</script>

<style scoped lang="scss">

</style>
