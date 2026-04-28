<template>
  <div class="music-item">
    <div class="cover">
      <Cover :cover="data.cover" borderRadius="50%"></Cover>
      <PlayBtn :data="data" @playList="playList"></PlayBtn>
    </div>
    <div class="music-info">
      <div class="music-title" @click="playMusic(true)">
        {{ data.musicTitle }}
      </div>
      <div class="music-prompt">{{ data.prompt }}</div>
      <div class="user-info">
        <div class="user-avatar">
          <router-link :to="`/user/${data.userId}`">
            <Avatar :avatar="data.avatar" :width="20"></Avatar>
          </router-link>
        </div>
        <router-link :to="`/user/${data.userId}`" class="user-name">{{
          data.nickName
        }}</router-link>
        <div class="iconfont icon-play">{{ data.playCount }}</div>
        <div class="iconfont icon-time">
          {{ proxy.Utils.seconds2Min(data.duration) }}
        </div>
      </div>
    </div>
    <div class="op-panel">
      <div class="opbtn opbtn-good">
        <ActionGood :data="data"></ActionGood>
      </div>
      <div class="opbtn opbtn-share">
        <ActionShare :data="data"></ActionShare>
      </div>
    </div>
  </div>
</template>

<script setup>
import ActionShare from "@/component/biz/ActionShare.vue";
import ActionGood from "@/component/biz/ActionGood.vue";
import { ref, reactive, getCurrentInstance, nextTick, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
import { useMusicPlayStore } from "@/stores/musicPlay.js";
const musicPlayStore = useMusicPlayStore();

const props = defineProps({
  data: {
    type: Object,
    default: {},
  },
});
const emits = defineEmits(["playList"]);
const playMusic = (jumpDetail) => {
  emits("playList");
  musicPlayStore.play({ ...props.data });
  if (!jumpDetail) {
    return;
  }
  router.push(`/play/${props.data.musicId}`);
};

const playList = () => {
  emits("playList");
};
</script>

<style lang="scss" scoped>
.music-item {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
  min-width: 0;
  padding: 14px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.04);
  .cover {
    flex-shrink: 0;
    background: linear-gradient(180deg, rgba(48, 39, 72, 0.9), rgba(27, 21, 42, 0.96));
    border-radius: 16px;
    width: 96px;
    height: 96px;
    padding: 10px;
    cursor: pointer;
    position: relative;
    img {
      max-width: 100%;
      border-radius: 50%;
    }
  }
  .music-info {
    flex: 1;
    width: 0;
    min-width: 0;
    color: #ffff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    .music-title {
      display: inline-block;
      color: #fff;
      font-size: 17px;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: pointer;
      &:hover {
        color: #95aefc;
      }
    }
    .music-prompt {
      font-size: 12px;
      font-weight: 500;
      opacity: 0.65;
      margin-top: 6px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
    }
    .user-info {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 10px;
      .user-avatar {
        opacity: 1;
      }
      .user-name {
        font-size: 14px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        opacity: 0.7;
        text-decoration: none;
        color: #fff;
      }
      .iconfont {
        opacity: 0.6;
        &::before {
          margin-right: 4px;
        }
      }
      .icon-play {
        font-size: 12px;
        margin-right: 10px;
      }
      .icon-time {
        font-size: 14px;
      }
    }
  }
  .op-panel {
    display: flex;
    flex-direction: column;
    gap: 16px;
    color: #fff;
    align-items: center;
    width: 36px;
    .opbtn {
      cursor: pointer;
    }
  }
}
@media (max-width: 500px) {
  .music-item {
    gap: 10px;
    padding: 12px;
    border-radius: 18px;
    .cover {
      width: 84px;
      height: 84px;
      border-radius: 14px;
    }
    .music-info {
      .music-title {
        font-size: 16px;
      }
      .music-prompt {
        -webkit-line-clamp: 3;
      }
    }
    .op-panel {
      width: 32px;
      gap: 12px;
    }
  }
}
</style>
