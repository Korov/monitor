<template>
  <el-tree :data="dataModel" :props="defaultProps" />
</template>

<script lang="ts">
import { defineComponent } from "vue";

interface Tree {
  label: string;
  children?: Tree[];
}

export default defineComponent({
  name: "ZKTree",
  setup() {
    let dataModel = [
      {
        label: "Root Node",
        children: [
          {
            label: "Child Node"
          },
          {
            label: "Second Child"
          }
        ]
      }
    ];

    const defaultProps = {
      children: "children",
      label: "label"
    };

    return {
      dataModel,
      defaultProps
    };
  }
});
</script>

<style scoped lang="scss">

</style>
