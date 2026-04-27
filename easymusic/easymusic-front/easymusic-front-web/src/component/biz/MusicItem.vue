<template>
  <div class="music-item">
    <div class="cover">
      <template v-if="data.musicStatus == 1">
        <Cover
          :cover="data.cover"
          :width="100"
          @click="playMusic(false)"
        ></Cover>
        <PlayBtn :data="data" @playList="playList"></PlayBtn>
        <div
          class="upload-cover"
          @click="uploadCover"
          v-if="userInfoStore.userInfo.userId == data.userId"
        >
          上传封面
        </div>
      </template>
      <div class="createing" v-if="data.musicStatus == 0">
        <img :src="proxy.Utils.getLocalResource('img/loading.gif')" />
      </div>
    </div>
    <div class="music-info">
      <div v-if="data.musicStatus == 2">生成失败</div>
      <div
        v-else
        :class="[
          'music-title',
          data.musicStatus != 1 ? 'music-title-creating' : '',
        ]"
        @click="playMusic(true)"
      >
        {{ data.musicTitle || "作品生成中......" }}
      </div>
      <div class="publish-row" v-if="isOwner && data.musicStatus === 1">
        <span :class="['publish-tag', `publish-tag-${publishStatusValue}`]">
          {{ publishStatusText }}
        </span>
        <span
          class="publish-time"
          v-if="data.publishStatus === 1 && data.publishTime"
        >
          发布于 {{ proxy.Utils.formatDate(data.publishTime) }}
        </span>
      </div>
      <div class="lyrics" v-if="data.musicType === 0">
        {{ musicLyrics || "--" }}
      </div>
      <div class="lyrics" v-if="data.musicType === 1">纯音乐，请欣赏</div>
      <div class="time">
        {{ proxy.Utils.seconds2Min(data.duration) || "--" }} ·
        {{ proxy.Utils.formatDate(data.createTime) }}
      </div>
    </div>
    <div class="op-panel" v-if="canPublicInteract">
      <div class="opbtn opbtn-good">
        <ActionGood :data="data"></ActionGood>
      </div>
      <div class="opbtn opbtn-share">
        <action-share :data="data"></action-share>
      </div>
    </div>
    <div class="op-panel manage-panel" v-if="isOwner">
      <template v-if="data.musicStatus === 0"> -- </template>
      <template v-else-if="data.musicStatus === 1">
        <div class="op-btn" @click="renameMusic">重命名</div>
        <div
          class="op-btn"
          @click="changePublishStatus(1)"
          v-if="data.publishStatus !== 1"
        >
          发布
        </div>
        <div
          class="op-btn"
          @click="changePublishStatus(2)"
          v-if="data.publishStatus === 1"
        >
          隐藏
        </div>
        <div
          class="op-btn"
          @click="changePublishStatus(0)"
          v-if="data.publishStatus !== 0"
        >
          草稿
        </div>
        <div class="op-btn op-btn-danger" @click="delMusic">删除</div>
      </template>
      <div class="op-btn op-btn-danger" @click="delMusic" v-else>删除</div>
    </div>
  </div>

  <ImageCoverCut
    ref="imageCoverCutRef"
    :cutWidth="200"
    :scale="1"
    @cutImage="updateCover"
  >
  </ImageCoverCut>

  <MusicTitleUpdate
    ref="musicTitleUpdateRef"
    @update="updateTitle"
  ></MusicTitleUpdate>
</template>

<script setup>
import ActionGood from "@/component/biz/ActionGood.vue";
import ActionShare from "@/component/biz/ActionShare.vue";
import MusicTitleUpdate from "./MusicTitleUpdate.vue";
import ImageCoverCut from "@/component/common/ImageCoverCut.vue";
import PlayBtn from "@/component/common/PlayBtn.vue";
import { ref, reactive, getCurrentInstance, nextTick, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
import { useMusicPlayStore } from "@/stores/musicPlay.js";
const musicPlayStore = useMusicPlayStore();

import { useUserInfoStore } from "@/stores/userInfoStore";
const userInfoStore = useUserInfoStore();

const props = defineProps({
  data: {
    type: Object,
    default: {},
  },
});

const emits = defineEmits(["playList", "reload"]);
const publishStatusMap = {
  0: "草稿",
  1: "已发布",
  2: "已隐藏",
};
const isOwner = computed(() => {
  return userInfoStore.userInfo.userId == props.data.userId;
});
const publishStatusValue = computed(() => props.data.publishStatus ?? 0);
const publishStatusText = computed(() => {
  return publishStatusMap[publishStatusValue.value] || "草稿";
});
const canPublicInteract = computed(() => {
  return props.data.musicStatus === 1 && publishStatusValue.value === 1;
});
const playMusic = (jumpDetail) => {
  if (props.data.musicStatus != 1) {
    return;
  }
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
const musicLyrics = computed(() => {
  if (!props.data.lyrics) {
    return "";
  }
  const lyricsArray = JSON.parse(props.data.lyrics);
  const lyricsTextArray = lyricsArray.map((item) => {
    return item.text;
  });
  return lyricsTextArray.join(" ");
});

//上传封面
const imageCoverCutRef = ref();
const uploadCover = () => {
  imageCoverCutRef.value.show();
};

const updateCover = async (file) => {
  let result = await proxy.Request({
    url: proxy.Api.uploadMusicCover,
    params: {
      cover: file,
      musicId: props.data.musicId,
    },
  });
  if (!result) {
    return;
  }
  props.data.cover = result.data;
};

const delMusic = () => {
  proxy.Confirm({
    message: `确定要删除歌曲[${props.data.musicTitle}]吗?`,
    okfun: async () => {
      let result = await proxy.Request({
        url: proxy.Api.delMusic,
        params: {
          musicId: props.data.musicId,
        },
      });
      if (!result) {
        return;
      }
      emits("reload");
    },
  });
};

const musicTitleUpdateRef = ref();
const renameMusic = () => {
  musicTitleUpdateRef.value.show(props.data);
};
const updateTitle = (title) => {
  props.data.musicTitle = title;
};

const changePublishStatus = (publishStatus) => {
  const actionText = publishStatusMap[publishStatus] || "更新";
  proxy.Confirm({
    message: `确定将作品[${props.data.musicTitle}]设为${actionText}吗?`,
    okfun: async () => {
      let result = await proxy.Request({
        url: proxy.Api.changePublishStatus,
        params: {
          musicId: props.data.musicId,
          publishStatus,
        },
      });
      if (!result) {
        return;
      }
      props.data.publishStatus = publishStatus;
      if (publishStatus === 1) {
        props.data.publishTime = new Date();
      } else if (publishStatus === 0) {
        props.data.publishTime = null;
      }
      if (musicPlayStore.currentMusic?.musicId === props.data.musicId) {
        musicPlayStore.currentMusic.publishStatus = publishStatus;
        musicPlayStore.currentMusic.publishTime = props.data.publishTime;
      }
      proxy.Message.success("状态更新成功");
    },
  });
};
</script>

<style lang="scss" scoped>
.music-item {
  margin: 10px;
  padding: 10px;
  margin-bottom: 10px;
  border-width: 1px;
  border-style: solid;
  border-color: hsla(0, 0%, 100%, 0.2);
  border-radius: 10px;
  color: #fff;
  display: flex;
  align-items: center;
  overflow: hidden;
  .cover {
    position: relative;
    .upload-cover {
      width: 100%;
      position: absolute;
      left: 0px;
      bottom: 0px;
      background: rgba(0, 0, 0, 0.5);
      z-index: 1;
      color: #fff;
      text-align: center;
      cursor: pointer;
      padding: 3px 0px;
      font-size: 13px;
    }
    .createing {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100px;
      height: 100px;
      background: #1f212d;
      border-radius: 5px;
      img {
        width: 20px;
      }
    }
  }
  .music-info {
    flex: 1;
    width: 0;
    margin-left: 10px;
    .music-title {
      display: inline-block;
      font-size: 20px;
      cursor: pointer;
      &:hover {
        color: var(--activeText);
      }
    }
    .music-title-creating {
      cursor: not-allowed;
      color: var(--text);
      &:hover {
        color: var(--text);
      }
    }
    .lyrics {
      margin-top: 10px;
      color: var(--text);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 12px;
    }
    .publish-row {
      margin-top: 8px;
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
      .publish-tag {
        padding: 2px 8px;
        border-radius: 999px;
        font-size: 12px;
        line-height: 18px;
      }
      .publish-tag-0 {
        background: rgba(255, 255, 255, 0.12);
        color: #d9d9d9;
      }
      .publish-tag-1 {
        background: rgba(103, 194, 58, 0.18);
        color: #7dd857;
      }
      .publish-tag-2 {
        background: rgba(230, 162, 60, 0.18);
        color: #f4b24d;
      }
      .publish-time {
        font-size: 12px;
        color: var(--text);
      }
    }
    .time {
      margin-top: 5px;
      font-size: 13px;
      color: var(--text);
    }
  }
  .op-panel {
    width: 80px;
    margin-left: 10px;
    display: flex;
    font-size: 14px;
    justify-content: space-between;
    align-items: center;
    .op-btn {
      cursor: pointer;
    }
    .op-btn-danger {
      color: #f56c6c;
    }
  }
  .manage-panel {
    width: 220px;
    justify-content: flex-start;
    gap: 8px;
    flex-wrap: wrap;
  }
}
</style>
