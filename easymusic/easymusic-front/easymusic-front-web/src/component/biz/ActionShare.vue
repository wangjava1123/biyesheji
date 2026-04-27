<template>
  <div class="iconfont icon-share" @click="copyLink"></div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
import { useUserInfoStore } from "@/stores/userInfoStore";
const userInfoStore = useUserInfoStore();

const props = defineProps({
  data: {
    type: Object,
    default: {},
  },
});

const copyLink = async () => {
  const text = import.meta.env.VITE_DOMAIN + `/play/${props.data.musicId}`;
  try {
    await navigator.clipboard.writeText(text);
    proxy.Message.success("歌曲链接已复制，快去分享吧");
  } catch (e) {
    // 降级方案：使用 textarea + execCommand
    const textarea = document.createElement("textarea");
    textarea.value = text;
    textarea.style.position = "fixed";
    textarea.style.opacity = "0";
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand("copy");
    document.body.removeChild(textarea);
    proxy.Message.success("歌曲链接已复制，快去分享吧");
  }
};
</script>

<style lang="scss" scoped>
.iconfont {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  -webkit-backdrop-filter: blur(12px);
  backdrop-filter: blur(12px);
  border-radius: 50%;
  &:hover {
    background: #3e3450;
  }
}
</style>
