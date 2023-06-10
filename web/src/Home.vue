<template>
  <div class="home">
    <el-container>
      <el-header class="title">
        <h1>Monitor</h1>
      </el-header>
      <el-container>
        <el-aside width="auto">
          <div class="navLeft">
            <div>
              <svg
                :class="{ 'is-active': isCollapse }"
                class="hamburger"
                viewBox="0 0 1024 1024"
                @click="collapseHandle()"
                xmlns="http://www.w3.org/2000/svg"
                width="64"
                height="64"
              >
                <path
                  d="M408 442h480c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8H408c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8zm-8 204c0 4.4 3.6 8 8 8h480c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8H408c-4.4 0-8 3.6-8 8v56zm504-486H120c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zm0 632H120c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zM142.4 642.1L298.7 519a8.84 8.84 0 0 0 0-13.9L142.4 381.9c-5.8-4.6-14.4-.5-14.4 6.9v246.3a8.9 8.9 0 0 0 14.4 7z"
                />
              </svg>
            </div>
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
              <el-sub-menu index="/zookeeper">
                <template #title>
                  <el-icon>
                    <Setting />
                  </el-icon>
                  <span>Zookeeper</span>
                </template>
                <el-menu-item index="/zookeeper/zkconfig">
                  <template #title>Config</template>
                </el-menu-item>
                <el-menu-item index="/zookeeper/zktree">
                  <template #title>Tree</template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </div>
        </el-aside>
        <el-main>
          <div class="navRight">
            <router-view></router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { useRoute } from 'vue-router'
import { localStorage } from '@/stores/store'

export default defineComponent({
  name: 'HomePage',
  setup() {
    const isCollapse = ref(true)
    const leftSpan = ref(2)
    const rightSpan = ref(22)
    const widthNav = ref('200px')
    const route = useRoute()
    let activePath = computed(() => {
      if (route.path === '/') {
        return '/kafka/config'
      } else {
        return route.path
      }
    })

    function initCollapse() {
      isCollapse.value = Boolean(localStorage.get('isCollapse') === 'true')
      if (isCollapse.value) {
        leftSpan.value = 2
        rightSpan.value = 24 - leftSpan.value
      } else {
        leftSpan.value = 4
        rightSpan.value = 24 - leftSpan.value
      }
    }

    initCollapse()

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
      localStorage.set('isCollapse', String(isCollapse.value))
    }

    return {
      activePath,
      isCollapse,
      leftSpan,
      rightSpan,
      collapseHandle,
      widthNav,
    }
  },
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.module.scss';

$home-height: 100vh;

.home {
  margin-right: 1px;
  padding: 0 10px;
  line-height: 10px;
  //background-color: #832b2b;
  color: #ffffff;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 700;
  height: $home-height;
}

.navLeft {
  padding: 5px 10px 5px 0;
}

.navRight {
  padding: 5px 0 0 0;
  height: calc(#{$home-height} - 110px);
}

.navBar {
  border-radius: 5px;
  height: 100%;
  border: 0;
}

.title {
  width: 100%;
  text-align: center;
  border-radius: 5px;
  background: #06b176;
}

.hamburger {
  display: inline-block;
  vertical-align: middle;
  width: 20px;
  height: 20px;
}

.hamburger.is-active {
  transform: rotate(180deg);
}
</style>
