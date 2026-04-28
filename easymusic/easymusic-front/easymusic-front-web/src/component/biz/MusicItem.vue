<template>
  <div class="music-item">
    <div class="cover">
      <template v-if="data.musicStatus === 1">
        <Cover :cover="data.cover" :width="100" @click="playMusic(false)"></Cover>
        <PlayBtn :data="data" @playList="playList"></PlayBtn>
        <div class="upload-cover" @click="uploadCover" v-if="isOwner">上传封面</div>
      </template>
      <div class="creating" v-else-if="data.musicStatus === 0">
        <img :src="proxy.Utils.getLocalResource('img/loading.gif')" />
      </div>
      <div class="failed" v-else>生成失败</div>
    </div>

    <div class="music-info">
      <div
        :class="['music-title', data.musicStatus !== 1 ? 'music-title-creating' : '']"
        @click="playMusic(true)"
      >
        {{ data.musicTitle || "作品生成中..." }}
      </div>

      <div class="publish-row" v-if="isOwner && data.musicStatus === 1">
        <span :class="['publish-tag', `publish-tag-${publishStatusValue}`]">
          {{ publishStatusText }}
        </span>
        <span class="publish-time" v-if="data.publishStatus === 1 && data.publishTime">
          发布于 {{ proxy.Utils.formatDate(data.publishTime) }}
        </span>
      </div>

      <div class="cover-row" v-if="isOwner && data.musicStatus === 1">
        <span :class="['cover-tag', `cover-tag-${coverSourceValue}`]">
          {{ coverSourceText }}
        </span>
        <span class="cover-count">AI 封面 {{ data.coverGenerateCount || 0 }} 次</span>
      </div>

      <div class="prompt-row" v-if="isOwner && promptPreview">
        <span :class="['prompt-tag', `prompt-tag-${promptSourceValue}`]">
          {{ promptSourceText }}
        </span>
        <span class="prompt-preview">{{ promptPreview }}</span>
      </div>

      <div class="lyrics" v-if="data.musicType === 0">
        {{ musicLyrics || "--" }}
      </div>
      <div class="lyrics" v-else-if="data.musicStatus === 1">纯音乐，请欣赏</div>

      <div class="stat-row" v-if="data.musicStatus === 1">
        <span class="stat-item">
          <i class="iconfont icon-play"></i>
          {{ data.playCount || 0 }}
        </span>
        <span class="stat-item">
          <i :class="['iconfont', data.doGood ? 'icon-good-solid' : 'icon-good']"></i>
          {{ data.goodCount || 0 }}
        </span>
      </div>

      <div class="time">
        {{ proxy.Utils.seconds2Min(data.duration) || "--" }} · 创建于
        {{ proxy.Utils.formatDate(data.createTime) }}
      </div>
    </div>

    <div class="op-panel" v-if="canPublicInteract">
      <div class="op-btn op-btn-icon">
        <ActionGood :data="data"></ActionGood>
      </div>
      <div class="op-btn op-btn-icon">
        <ActionShare :data="data"></ActionShare>
      </div>
    </div>

    <div class="op-panel manage-panel" v-if="isOwner">
      <template v-if="data.musicStatus === 0">
        <span class="pending-text">等待生成完成</span>
      </template>
      <template v-else-if="data.musicStatus === 1">
        <div class="op-btn" @click="renameMusic">重命名</div>
        <div class="op-btn" @click="continueCreate" v-if="data.creationId">继续创作</div>
        <div class="op-btn" @click="openAICoverDialog">AI 封面</div>
        <div class="op-btn" @click="changePublishStatus(1)" v-if="data.publishStatus !== 1">
          发布
        </div>
        <div class="op-btn" @click="changePublishStatus(2)" v-if="data.publishStatus === 1">
          隐藏
        </div>
        <div class="op-btn" @click="changePublishStatus(0)" v-if="data.publishStatus !== 0">
          草稿
        </div>
        <div class="op-btn op-btn-danger" @click="delMusic">删除</div>
      </template>
      <div class="op-btn op-btn-danger" @click="delMusic" v-else>删除</div>
    </div>
  </div>

  <ImageCoverCut ref="imageCoverCutRef" :cutWidth="200" :scale="1" @cutImage="updateCover">
  </ImageCoverCut>

  <MusicTitleUpdate ref="musicTitleUpdateRef" @update="updateTitle"></MusicTitleUpdate>

  <AICoverDialog ref="aiCoverDialogRef" @updated="applyCoverChange"></AICoverDialog>
</template>

<script setup>
import { computed, getCurrentInstance, ref } from "vue";
import { useRouter } from "vue-router";
import ActionGood from "@/component/biz/ActionGood.vue";
import ActionShare from "@/component/biz/ActionShare.vue";
import AICoverDialog from "@/component/biz/AICoverDialog.vue";
import ImageCoverCut from "@/component/common/ImageCoverCut.vue";
import PlayBtn from "@/component/common/PlayBtn.vue";
import { useMusicPlayStore } from "@/stores/musicPlay.js";
import { useUserInfoStore } from "@/stores/userInfoStore";
import MusicTitleUpdate from "./MusicTitleUpdate.vue";

const { proxy } = getCurrentInstance();
const router = useRouter();
const musicPlayStore = useMusicPlayStore();
const userInfoStore = useUserInfoStore();

const props = defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
});

const emits = defineEmits(["playList", "reload"]);

const publishStatusMap = {
  0: "草稿",
  1: "已发布",
  2: "已隐藏",
};

const coverSourceMap = {
  0: "手动封面",
  1: "AI 封面",
};

const promptSourceMap = {
  0: "手写提示词",
  1: "AI 增强",
};

const isOwner = computed(() => {
  return userInfoStore.userInfo.userId === props.data.userId;
});

const publishStatusValue = computed(() => props.data.publishStatus ?? 0);
const publishStatusText = computed(() => {
  return publishStatusMap[publishStatusValue.value] || "草稿";
});

const coverSourceValue = computed(() => props.data.coverSource ?? 0);
const coverSourceText = computed(() => {
  return coverSourceMap[coverSourceValue.value] || "未设置封面";
});

const promptSourceValue = computed(() => props.data.promptSourceType ?? 0);
const promptSourceText = computed(() => {
  return promptSourceMap[promptSourceValue.value] || "手写提示词";
});

const promptPreview = computed(() => {
  return props.data.originPrompt || props.data.creationPrompt || "";
});

const canPublicInteract = computed(() => {
  return props.data.musicStatus === 1 && publishStatusValue.value === 1;
});

const musicLyrics = computed(() => {
  if (!props.data.lyrics) {
    return "";
  }
  if (Array.isArray(props.data.lyrics)) {
    return props.data.lyrics.map((item) => item.text).join(" ");
  }
  try {
    const lyricsArray = JSON.parse(props.data.lyrics);
    return lyricsArray.map((item) => item.text).join(" ");
  } catch (error) {
    return props.data.lyrics;
  }
});

const syncCurrentMusic = (patchData) => {
  if (musicPlayStore.currentMusic?.musicId !== props.data.musicId) {
    return;
  }
  musicPlayStore.currentMusic = {
    ...musicPlayStore.currentMusic,
    ...patchData,
  };
};

const playMusic = (jumpDetail) => {
  if (props.data.musicStatus !== 1) {
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

const applyCoverChange = (patchData) => {
  Object.assign(props.data, patchData);
  syncCurrentMusic(patchData);
};

const imageCoverCutRef = ref();
const uploadCover = () => {
  imageCoverCutRef.value.show();
};

const updateCover = async (file) => {
  const result = await proxy.Request({
    url: proxy.Api.uploadMusicCover,
    params: {
      cover: file,
      musicId: props.data.musicId,
    },
  });
  if (!result) {
    return;
  }
  applyCoverChange({
    cover: result.data,
    coverSource: 0,
  });
};

const aiCoverDialogRef = ref();
const openAICoverDialog = () => {
  aiCoverDialogRef.value.show(props.data);
};

const delMusic = () => {
  proxy.Confirm({
    message: `确定要删除作品【${props.data.musicTitle || "未命名作品"}】吗？`,
    okfun: async () => {
      const result = await proxy.Request({
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

const continueCreate = () => {
  if (!props.data.creationId) {
    return;
  }
  router.push(`/idea/${props.data.creationId}`);
};

const updateTitle = (title) => {
  props.data.musicTitle = title;
  syncCurrentMusic({ musicTitle: title });
};

const changePublishStatus = (publishStatus) => {
  const actionText = publishStatusMap[publishStatus] || "更新";
  proxy.Confirm({
    message: `确定将作品【${props.data.musicTitle || "未命名作品"}】设为${actionText}吗？`,
    okfun: async () => {
      const result = await proxy.Request({
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
      syncCurrentMusic({
        publishStatus,
        publishTime: props.data.publishTime,
      });
      proxy.Message.success("状态更新成功");
    },
  });
};
</script>

<style lang="scss" scoped>
.music-item {
  margin: 10px;
  padding: 12px;
  border: 1px solid hsla(0, 0%, 100%, 0.18);
  border-radius: 14px;
  color: #fff;
  display: grid;
  grid-template-columns: 100px minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
}

.cover {
  position: relative;
  flex-shrink: 0;
}

.upload-cover {
  width: 100%;
  position: absolute;
  left: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.56);
  z-index: 1;
  color: #fff;
  text-align: center;
  cursor: pointer;
  padding: 4px 0;
  font-size: 12px;
}

.creating,
.failed {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  background: #1f212d;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.68);
  text-align: center;
  padding: 10px;
}

.creating img {
  width: 20px;
}

.music-info {
  flex: 1;
  width: 0;
  min-width: 0;
}

.music-title {
  display: inline-block;
  font-size: 20px;
  cursor: pointer;
  line-height: 1.4;
}

.music-title:hover {
  color: var(--activeText);
}

.music-title-creating {
  cursor: not-allowed;
  color: var(--text);
}

.music-title-creating:hover {
  color: var(--text);
}

.publish-row,
.cover-row,
.prompt-row {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.publish-tag,
.cover-tag,
.prompt-tag {
  padding: 2px 9px;
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

.cover-tag-0 {
  background: rgba(255, 255, 255, 0.12);
  color: #d9d9d9;
}

.cover-tag-1 {
  background: rgba(64, 158, 255, 0.18);
  color: #8ec5ff;
}

.prompt-tag-0 {
  background: rgba(255, 255, 255, 0.12);
  color: #d9d9d9;
}

.prompt-tag-1 {
  background: rgba(255, 123, 84, 0.18);
  color: #ffb38a;
}

.publish-time,
.cover-count {
  font-size: 12px;
  color: var(--text);
}

.prompt-preview {
  flex: 1;
  min-width: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.74);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.lyrics {
  margin-top: 10px;
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
}

.stat-row {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.72);
}

.stat-item .iconfont {
  font-size: 14px;
}

.time {
  margin-top: 6px;
  font-size: 13px;
  color: var(--text);
}

.op-panel {
  width: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.op-btn {
  cursor: pointer;
  transition: color 0.2s ease;
}

.op-btn:hover {
  color: #fff;
}

.op-btn-icon {
  display: flex;
  align-items: center;
}

.op-btn-danger {
  color: #f56c6c;
}

.manage-panel {
  width: 270px;
  justify-content: flex-start;
  flex-wrap: wrap;
}

.pending-text {
  color: var(--text);
  font-size: 12px;
}

@media (max-width: 980px) {
  .music-item {
    grid-template-columns: 100px minmax(0, 1fr);
    align-items: flex-start;
  }

  .op-panel,
  .manage-panel {
    width: auto;
    grid-column: 2;
    justify-content: flex-start;
  }
}

@media (max-width: 720px) {
  .music-item {
    grid-template-columns: 1fr;
  }

  .cover,
  .music-info,
  .op-panel,
  .manage-panel {
    grid-column: 1;
  }

  .cover {
    justify-self: start;
  }

  .op-panel,
  .manage-panel {
    width: 100%;
  }
}

@media (max-width: 520px) {
  .music-item {
    gap: 10px;
    padding: 10px;
  }

  .music-title {
    font-size: 18px;
  }

  .manage-panel {
    width: 100%;
  }
}
</style>
