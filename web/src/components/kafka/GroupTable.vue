<template>
  <div>
    <vxe-table border resizable :span-method="mergeRowMethod" :data="data">
      <vxe-column field="topic" title="topic" align="center"></vxe-column>
      <vxe-column field="partition" title="分区号" align="center"></vxe-column>
      <vxe-column field="endOffset" title="末尾offset" align="center"></vxe-column>
      <vxe-column field="offset" title="消费偏移量" align="center"></vxe-column>
      <vxe-column field="lag" title="未消费消息条数" align="center"></vxe-column>
    </vxe-table>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'GroupTable',
  props: ['data'],
  setup() {
    function mergeRowMethod({ row, _rowIndex, column, visibleData }: any) {
      const fields = ['topic']
      const cellValue = row[column.property]
      if (cellValue && fields.includes(column.property)) {
        const prevRow = visibleData[_rowIndex - 1]
        let nextRow = visibleData[_rowIndex + 1]
        if (prevRow && prevRow[column.property] === cellValue) {
          return { rowspan: 0, colspan: 0 }
        } else {
          let countRowspan = 1
          while (nextRow && nextRow[column.property] === cellValue) {
            nextRow = visibleData[++countRowspan + _rowIndex]
          }
          if (countRowspan > 1) {
            return { rowspan: countRowspan, colspan: 1 }
          }
        }
      }
    }

    return {
      mergeRowMethod,
    }
  },
})
</script>

<style scoped></style>
