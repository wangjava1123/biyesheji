<template>
  <div class="layout">
    <div class="left-body">
      <LeftSide></LeftSide>
    </div>
    <div class="right">
      <router-view></router-view>
      <GlobalPlayer></GlobalPlayer>
    </div>
  </div>
  <LoginAndRegister></LoginAndRegister>
  <!--移动端显示顶部-->
  <TopPanel></TopPanel>
</template>

<script setup>
import TopPanel from './TopPanel.vue'
import LoginAndRegister from './login/LoginAndRegister.vue'
import GlobalPlayer from '@/views/player/GlobalPlayer.vue'
import LeftSide from './LeftSide.vue'
import {
  ref,
  reactive,
  getCurrentInstance,
  nextTick,
  onMounted,
  watch,
} from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
import { useUserInfoStore } from '@/stores/userInfoStore'
const userInfoStore = useUserInfoStore()

const getLoginInfo = async () => {
  let result = await proxy.Request({
    url: proxy.Api.getLoginInfo,
    showLoading: false,
    params: {},
  })
  if (!result) {
    return
  }
  userInfoStore.setLoginInfo(result.data || {})
}

watch(
  () => userInfoStore.lastReloadTime,
  (newVal, oldVal) => {
    getLoginInfo()
  },
  { immediate: true, deep: true }
)
</script>

<style lang="scss" scoped>
.layout {
  height: 100dvh;
  min-height: 100dvh;
  .left-body {
    position: fixed;
    inset: 0 auto 0 0;
    height: 100dvh;
  }
  .right {
    min-height: 100dvh;
    padding-left: 200px;
    overflow: auto;
    padding-bottom: 86px;
    background:
      radial-gradient(circle at top right, rgba(255, 123, 84, 0.08), transparent 24%),
      linear-gradient(180deg, #070512 0%, #0b0718 100%);
  }
}

@media (max-width: 500px) {
  .layout {
    height: 100dvh;
    min-height: 100dvh;
    padding-top: 62px;
    .left-body {
      display: none;
    }
    .right {
      padding-left: 0px;
      min-height: calc(100dvh - 62px);
      padding-bottom: 92px;
    }
  }
}
</style>
