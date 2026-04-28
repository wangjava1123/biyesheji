<template>
  <div class="music-item">
    <div class="cover">
      <Cover :cover="data.cover" :width="200"></Cover>
      <div class="opbtn opbtn-good">
        <ActionGood :data="data"></ActionGood>
      </div>
      <div class="opbtn opbtn-share">
        <ActionShare :data="data"></ActionShare>
      </div>
      <div class="play-info">
        <div class="iconfont icon-play">{{ data.playCount }}</div>
        <div class="iconfont icon-time">
          {{ proxy.Utils.seconds2Min(data.duration) }}
        </div>
      </div>
      <PlayBtn :data="data" @playList="playList"></PlayBtn>
    </div>
    <div class="music-info">
      <div class="music-title" @click="playMusic(true)">
        {{ data.musicTitle }}
      </div>
      <div class="music-prompt">{{ data.prompt }}</div>
      <div class="user-info">
        <div class="user-avatar">
          <Avatar :width="30" :avatar="data.avatar"></Avatar>
        </div>
        <router-link class="user-name" :to="`/user/${data.userId}`">{{
          data.nickName
        }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import ActionShare from "@/component/biz/ActionShare.vue";
import ActionGood from "@/component/biz/ActionGood.vue";
import {
  ref,
  reactive,
  getCurrentInstance,
  nextTick,
  onMounted,
  watch,
} from "vue";
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
  width: clamp(220px, 22vw, 260px);
  padding-right: 16px;
  position: relative;
  cursor: pointer;
  .cover {
    width: 100%;
    overflow: hidden;
    position: relative;
    border-radius: 18px;
    background: linear-gradient(180deg, rgba(49, 39, 74, 0.9), rgba(24, 19, 38, 0.96));
    &:hover {
      .opbtn {
        display: flex;
      }
      .play-info {
        display: none;
      }
      img {
        transform: scale(1.2);
      }
    }
    img {
      max-width: 100%;
      transition: transform 0.3s ease-in-out;
    }
    .opbtn {
      cursor: pointer;
      position: absolute;
      z-index: 5;
      bottom: 10px;
      justify-content: space-between;
      color: #fff;
      align-items: center;
      border-radius: 50%;
      overflow: hidden;
      background: #0003;
      width: 40px;
      height: 40px;
      display: none;
    }
    .opbtn-good {
      left: 10px;
    }
    .opbtn-share {
      right: 10px;
    }
    .play-info {
      position: absolute;
      width: 100%;
      bottom: 10px;
      color: #fff;
      display: flex;
      .iconfont {
        margin-left: 15px;
        font-size: 13px;
        &::before {
          margin-right: 4px;
        }
      }
    }
  }
  .music-info {
    margin-top: 12px;
    padding: 0 4px;
    color: #ffff;
    .music-title {
      color: #fff;
      font-size: 17px;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
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
      gap: 8px;
      margin-top: 10px;
      .user-avatar {
        flex-shrink: 0;
      }
      .user-name {
        opacity: 0.7;
        font-size: 14px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        text-decoration: none;
        color: #fff;
      }
    }
  }
}

@media (max-width: 600px) {
  .music-item {
    width: min(78vw, 300px);
    padding-right: 12px;
    .cover {
      border-radius: 16px;
      .opbtn {
        display: flex;
        width: 36px;
        height: 36px;
        background: rgba(10, 10, 18, 0.48);
      }
      .play-info {
        bottom: 54px;
      }
    }
    .music-info {
      margin-top: 10px;
      .music-title {
        font-size: 16px;
      }
      .music-prompt {
        -webkit-line-clamp: 3;
      }
    }
  }
}
</style>
