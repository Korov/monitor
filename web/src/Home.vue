<template>
  <div class="home">
    <el-row>
      <div class="title">
        <h1>Monitor</h1>
      </div>
    </el-row>
    <el-row>
      <el-col :span="leftSpan" class="navLeft">
        <el-menu :default-active="activePath" :collapse="isCollapse" class="navBar" router>
          <el-sub-menu index="/kafka">
            <template #title>
              <el-icon>
                <Message />
              </el-icon>
              <span>Kafka</span>
            </template>
            <el-menu-item index="/kafka/config">
              <template #title>config</template>
            </el-menu-item>
            <el-menu-item index="/kafka/manager">
              <template #title>manager</template>
            </el-menu-item>
            <el-sub-menu index="/kafka/producer">
              <template #title>operate</template>
              <el-menu-item index="/kafka/producer">
                <template #title>produce</template>
              </el-menu-item>
              <el-menu-item index="/kafka/consumer">
                <template #title>consume</template>
              </el-menu-item>
            </el-sub-menu>
          </el-sub-menu>
        </el-menu>
      </el-col>
      <el-col :span="rightSpan" class="navRight">
        <el-row>
          <div>
            <el-button @click="collapseHandle()">Default</el-button>
          </div>
        </el-row>
        <el-row>
          <div>
            <router-view></router-view>
          </div>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import { ElRow, ElCol, ElMenu, ElSubMenu, ElIcon, ElMenuItem, ElRadioGroup, ElRadioButton } from 'element-plus'
import { computed, defineComponent, ref } from 'vue'
import { RouterView, useRoute } from 'vue-router'

export default defineComponent({
  name: 'HomePage',
  setup() {
    const isCollapse = ref(true)
    const leftSpan = ref(4)
    const rightSpan = ref(20)
    const route = useRoute()
    let activePath = computed(() => route.path)

    function collapseHandle() {
      if (isCollapse.value) {
        isCollapse.value = !isCollapse.value
        leftSpan.value = 4
        rightSpan.value = 24 - leftSpan.value
      } else {
        isCollapse.value = !isCollapse.value
        leftSpan.value = 2
        rightSpan.value = 24 - leftSpan.value
      }
    }

    return {
      activePath,
      isCollapse,
      leftSpan,
      rightSpan,
      collapseHandle
    }
  },
})
</script>

<style lang="scss" scoped>
.home {
  margin-right: 1px;
  padding: 0 10px;
  line-height: 10px;
  //background-color: #832b2b;
  color: #ffffff;
  height: max-content;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 700;
}

.navLeft {
  padding: 5px 10px 5px 0;
  height: 600px;
}

.navRight {
  padding: 5px 0 0 0;
  height: 400px;
}

.navBar {
  border-radius: 5px;
  height: 100%;
}

.title {
  width: 100%;
  text-align: center;
  border-radius: 5px;
  background: #06b176;
}
</style>
