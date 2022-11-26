<template>
  <div class="home">
    <el-row>
      <div class="title">
        <h1>Monitor</h1>
      </div>
    </el-row>
    <el-row>
      <el-col :span="4" class="navLeft">
        <el-menu :default-active="activePath" class="navBar" router>
          <el-sub-menu index="/kafka">
            <template #title>
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
      <el-col :span="20" class="navRight">
        <div>
          <router-view></router-view>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useRoute } from 'vue-router'

export default defineComponent({
  name: 'HomePage',
  setup() {
    const route = useRoute()
    let activePath = computed(() => route.path)

    return {
      activePath,
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
