<template>
  <div class="top-panel">
    <div class="iconfont icon-list" @click="showDrawerHandler"></div>
    <div class="title">EasyMusic</div>
  </div>

  <el-drawer v-model="showDrawer" direction="ltr" header-class="left-side-header" body-class="left-side-body"
    size="200px">
    <LeftSide></LeftSide>
  </el-drawer>
</template>

<script setup>
import LeftSide from './LeftSide.vue'
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()

const showDrawer = ref(false)
const showDrawerHandler = () => {
  showDrawer.value = true
}
</script>

<style lang="scss" scoped>
.top-panel {
  display: none;
}
@media (max-width: 500px) {
  .top-panel {
    display: flex;
    background: rgba(11, 10, 24, 0.86);
    border: 1px solid rgba(255, 255, 255, 0.08);
    box-shadow: 0 14px 30px rgba(0, 0, 0, 0.28);
    backdrop-filter: blur(18px);
    border-radius: 16px;
    position: fixed;
    top: 8px;
    left: 8px;
    right: 8px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 12px 14px;
    z-index: 20;
    .icon-list {
      color: #fff;
      font-size: 22px;
    }
    .title {
      color: #fff;
      margin-left: 12px;
      font-size: 14px;
      letter-spacing: 0.12em;
      text-transform: uppercase;
    }
  }
}
</style>
