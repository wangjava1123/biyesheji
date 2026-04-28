<template>
  <div class="music-detail-body">
    <BackBtn></BackBtn>
    <div class="music-panel">
      <div class="music-cover">
        <div
          :class="[
            'music-cover-bg',
            musicPlayStore.playing ? 'music-cover-bg-playing' : '',
          ]"
        ></div>
        <div class="cover">
          <Cover :width="150" :cover="musicInfo.cover" borderRadius="75px"></Cover>
        </div>
      </div>

      <div class="music-info">
        <div class="music-title">{{ musicInfo.musicTitle || "未命名作品" }}</div>
        <div class="user-info">{{ musicInfo.nickName || "--" }}</div>

        <div class="meta-panel">
          <span class="meta-tag" v-if="isOwner">
            {{ publishStatusText }}
          </span>
          <span class="meta-tag">
            {{ coverSourceText }}
          </span>
          <span class="meta-tag">AI 封面 {{ musicInfo.coverGenerateCount || 0 }} 次</span>
          <span class="meta-tag" v-if="isOwner && musicInfo.publishStatus === 1 && musicInfo.publishTime">
            发布于 {{ musicInfo.publishTime }}
          </span>
        </div>

        <div class="stats-panel">
          <div class="stats-card">
            <div class="stats-label">累计播放</div>
            <div class="stats-value">{{ musicInfo.playCount || 0 }}</div>
          </div>
          <div class="stats-card">
            <div class="stats-label">累计获赞</div>
            <div class="stats-value">{{ musicInfo.goodCount || 0 }}</div>
          </div>
          <div class="stats-card">
            <div class="stats-label">创建时间</div>
            <div class="stats-value stats-value-time">
              {{ musicInfo.createTime ? proxy.Utils.formatDate(musicInfo.createTime) : '--' }}
            </div>
          </div>
        </div>

        <div class="action-panel">
          <div
            :class="[
              'op-item play-btn iconfont',
              musicPlayStore.playing ? 'icon-pause' : 'icon-play',
            ]"
            @click="playMusic"
          ></div>
          <div class="op-item" v-if="canPublicInteract">
            <ActionGood :data="musicInfo"></ActionGood>
          </div>
          <div class="op-item" v-if="canPublicInteract">
            <ActionShare :data="musicInfo"></ActionShare>
          </div>
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="createSame">
              做同款
            </el-button>
            <el-button size="large" @click="continueCreate" v-if="isOwner && musicInfo.creationId">
              继续创作
            </el-button>
            <el-button size="large" @click="openAICoverDialog" v-if="isOwner && musicInfo.musicStatus === 1">
              AI 生成封面
            </el-button>
          </div>
        </div>

        <div class="owner-panel" v-if="isOwner">
          <div class="owner-head">
            <div>
              <div class="owner-kicker">作者侧管理</div>
              <div class="owner-title">继续完善标题、封面和发布状态</div>
            </div>
            <div class="owner-status">{{ ownerStatusHint }}</div>
          </div>
          <div class="owner-actions">
            <el-button plain size="large" @click="renameMusic" v-if="musicInfo.musicStatus === 1">
              修改标题
            </el-button>
            <el-button plain size="large" @click="uploadCover" v-if="musicInfo.musicStatus === 1">
              上传封面
            </el-button>
            <el-button plain size="large" @click="openAICoverDialog" v-if="musicInfo.musicStatus === 1">
              AI 重做封面
            </el-button>
            <el-button type="success" plain size="large" @click="changePublishStatus(1)"
              v-if="musicInfo.musicStatus === 1 && musicInfo.publishStatus !== 1">
              发布作品
            </el-button>
            <el-button type="warning" plain size="large" @click="changePublishStatus(2)"
              v-if="musicInfo.musicStatus === 1 && musicInfo.publishStatus === 1">
              隐藏作品
            </el-button>
            <el-button plain size="large" @click="changePublishStatus(0)"
              v-if="musicInfo.musicStatus === 1 && musicInfo.publishStatus !== 0">
              设为草稿
            </el-button>
          </div>
        </div>

        <div class="creation-panel" v-if="isOwner && (musicInfo.originPrompt || musicInfo.creationPrompt)">
          <div class="creation-head">
            <div class="creation-title">创作摘要</div>
            <div class="creation-tags">
              <span class="meta-tag">{{ promptSourceText }}</span>
              <span class="meta-tag" v-if="musicInfo.creationModeType !== undefined && musicInfo.creationModeType !== null">
                {{ creationModeText }}
              </span>
              <span class="meta-tag" v-if="musicInfo.creationModel">
                模型 {{ musicInfo.creationModel }}
              </span>
            </div>
          </div>
          <div class="creation-grid">
            <div class="creation-card" v-if="musicInfo.originPrompt">
              <div class="creation-card-title">一句话需求</div>
              <div class="creation-card-content">{{ musicInfo.originPrompt }}</div>
            </div>
            <div class="creation-card creation-card-wide" v-if="musicInfo.creationPrompt">
              <div class="creation-card-title">最终音乐提示词</div>
              <div class="creation-card-content">{{ musicInfo.creationPrompt }}</div>
            </div>
          </div>
        </div>

        <div class="lyrics-panel" v-if="musicInfo.musicType === 0">
          <div class="lyrics-title">歌词</div>
          <div
            :class="[
              'lyrics-item',
              musicPlayStore.currentPlayTime >= item.start &&
              musicPlayStore.currentPlayTime <= item.end
                ? 'active'
                : '',
            ]"
            v-for="item in musicInfo.lyrics"
            :key="`${item.start}-${item.end}`"
          >
            {{ item.text }}
          </div>
        </div>
        <div v-else class="lyrics-panel">纯音乐，请欣赏。</div>
      </div>
    </div>

    <ImageCoverCut ref="imageCoverCutRef" :cutWidth="200" :scale="1" @cutImage="updateCover"></ImageCoverCut>
    <MusicTitleUpdate ref="musicTitleUpdateRef" @update="updateTitle"></MusicTitleUpdate>
    <AICoverDialog ref="aiCoverDialogRef" @updated="applyCoverChange"></AICoverDialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import ActionShare from "@/component/biz/ActionShare.vue";
import ActionGood from "@/component/biz/ActionGood.vue";
import AICoverDialog from "@/component/biz/AICoverDialog.vue";
import ImageCoverCut from "@/component/common/ImageCoverCut.vue";
import { useMusicPlayStore } from "@/stores/musicPlay.js";
import { useUserInfoStore } from "@/stores/userInfoStore";
import { mitter } from "@/eventbus/eventBus.js";
import MusicTitleUpdate from "@/component/biz/MusicTitleUpdate.vue";

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
const musicPlayStore = useMusicPlayStore();
const userInfoStore = useUserInfoStore();

const currentMusicId = ref(route.params.musicId);
const musicInfo = ref({});

const publishStatusMap = {
  0: "草稿作品",
  1: "已发布作品",
  2: "已隐藏作品",
};

const coverSourceMap = {
  0: "当前封面：手动上传",
  1: "当前封面：AI 生成",
};

const promptSourceMap = {
  0: "创作方式：手写提示词",
  1: "创作方式：AI 增强",
};

const modeTypeMap = {
  0: "简单模式",
  1: "高级模式",
};

const isOwner = computed(() => {
  return userInfoStore.userInfo.userId === musicInfo.value.userId;
});

const publishStatusText = computed(() => {
  return publishStatusMap[musicInfo.value.publishStatus ?? 0] || "草稿作品";
});

const coverSourceText = computed(() => {
  return coverSourceMap[musicInfo.value.coverSource ?? 0] || "当前封面：未设置";
});

const promptSourceText = computed(() => {
  return promptSourceMap[musicInfo.value.promptSourceType ?? 0] || "创作方式：手写提示词";
});

const creationModeText = computed(() => {
  return modeTypeMap[musicInfo.value.creationModeType ?? 0] || "简单模式";
});

const canPublicInteract = computed(() => {
  return musicInfo.value.publishStatus === 1;
});

const ownerStatusHint = computed(() => {
  if (musicInfo.value.musicStatus === 0) {
    return "作品还在生成中，完成后可补封面并发布";
  }
  if (musicInfo.value.musicStatus === 2) {
    return "作品生成失败，建议回到创作页调整提示词后重试";
  }
  if (musicInfo.value.publishStatus === 1) {
    return "当前已公开展示，可继续隐藏或更新封面";
  }
  if (musicInfo.value.publishStatus === 2) {
    return "当前已隐藏，仅作者自己可见";
  }
  return "当前仍是草稿，建议确认标题和封面后再发布";
});

const normalizeLyrics = (lyrics) => {
  if (!lyrics) {
    return [];
  }
  if (Array.isArray(lyrics)) {
    return lyrics;
  }
  try {
    return JSON.parse(lyrics);
  } catch (error) {
    return [];
  }
};

const getMusicInfo = async (autoPlay) => {
  const result = await proxy.Request({
    url: proxy.Api.musicDetail,
    params: {
      musicId: currentMusicId.value,
    },
  });
  if (!result) {
    return;
  }
  result.data.lyrics = normalizeLyrics(result.data.lyrics);
  musicInfo.value = result.data;
  if (!autoPlay) {
    return;
  }
  musicPlayStore.play({ ...result.data });
};

const syncCurrentMusic = (patchData) => {
  if (musicPlayStore.currentMusic?.musicId !== musicInfo.value.musicId) {
    return;
  }
  musicPlayStore.currentMusic = {
    ...musicPlayStore.currentMusic,
    ...patchData,
  };
};

const applyCoverChange = (patchData) => {
  musicInfo.value = {
    ...musicInfo.value,
    ...patchData,
  };
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
      musicId: musicInfo.value.musicId,
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

const playMusic = () => {
  if (musicPlayStore.currentMusic?.musicId === musicInfo.value.musicId) {
    mitter.emit("togglePlay");
    return;
  }
  musicPlayStore.play({ ...musicInfo.value });
};

const createSame = () => {
  if (!musicInfo.value.creationId) {
    return;
  }
  router.push(`/idea/${musicInfo.value.creationId}`);
};

const continueCreate = () => {
  if (!musicInfo.value.creationId) {
    return;
  }
  router.push(`/idea/${musicInfo.value.creationId}`);
};

const musicTitleUpdateRef = ref();
const renameMusic = () => {
  musicTitleUpdateRef.value.show(musicInfo.value);
};

const updateTitle = (title) => {
  musicInfo.value.musicTitle = title;
  syncCurrentMusic({ musicTitle: title });
};

const aiCoverDialogRef = ref();
const openAICoverDialog = () => {
  aiCoverDialogRef.value.show(musicInfo.value);
};

const changePublishStatus = (publishStatus) => {
  const actionText = publishStatusMap[publishStatus] || "更新";
  proxy.Confirm({
    message: `确定将作品【${musicInfo.value.musicTitle || "未命名作品"}】设为${actionText}吗？`,
    okfun: async () => {
      const result = await proxy.Request({
        url: proxy.Api.changePublishStatus,
        params: {
          musicId: musicInfo.value.musicId,
          publishStatus,
        },
      });
      if (!result) {
        return;
      }
      musicInfo.value.publishStatus = publishStatus;
      if (publishStatus === 1) {
        musicInfo.value.publishTime = new Date();
      } else if (publishStatus === 0) {
        musicInfo.value.publishTime = null;
      }
      syncCurrentMusic({
        publishStatus,
        publishTime: musicInfo.value.publishTime,
      });
      proxy.Message.success("状态更新成功");
    },
  });
};

watch(
  () => route.params.musicId,
  (newVal) => {
    if (!newVal) {
      return;
    }
    currentMusicId.value = newVal;
    getMusicInfo(true);
  },
  { immediate: true }
);

watch(
  () => musicPlayStore.currentMusic.musicId,
  (newVal) => {
    if (!newVal || newVal === route.params.musicId) {
      return;
    }
    router.push(`/play/${newVal}`);
  }
);
</script>

<style lang="scss" scoped>
.music-detail-body {
  padding: 20px 0 0 20px;
}

.music-panel {
  display: flex;
  gap: 28px;
  padding: 22px 24px 88px 18px;
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(255, 123, 84, 0.16), transparent 26%),
    linear-gradient(135deg, rgba(13, 16, 34, 0.96), rgba(21, 26, 48, 0.94));
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.music-cover {
  width: 250px;
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

.music-cover-bg {
  position: absolute;
  left: 0;
  top: 0;
  width: 250px;
  height: 250px;
  background: url("../../assets/img/play_cover_bg.png") no-repeat;
}

.music-cover-bg-playing {
  animation: rotateBackground 30s linear infinite;
}

.cover {
  position: absolute;
  z-index: 2;
}

.music-info {
  flex: 1;
  min-width: 0;
  color: #fff;
}

.music-title {
  font-size: 30px;
  line-height: 1.3;
  font-weight: 600;
}

.user-info {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.72);
}

.meta-panel {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.88);
  font-size: 12px;
}

.action-panel {
  margin-top: 18px;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.owner-panel {
  margin-top: 20px;
  padding: 18px 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.owner-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.owner-kicker {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.54);
}

.owner-title {
  margin-top: 8px;
  font-size: 18px;
  line-height: 1.35;
  font-weight: 600;
}

.owner-status {
  max-width: 320px;
  color: rgba(255, 255, 255, 0.68);
  line-height: 1.7;
}

.owner-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.stats-panel {
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.stats-card {
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.stats-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.stats-value {
  margin-top: 8px;
  font-size: 24px;
  line-height: 1.2;
  font-weight: 700;
}

.stats-value-time {
  font-size: 15px;
  line-height: 1.5;
}

.op-item {
  cursor: pointer;
  width: 40px;
  height: 40px;
}

.iconfont {
  font-size: 25px;
}

.play-btn {
  font-size: 20px;
  background: #fff;
  border-radius: 50%;
  color: var(--purple);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.lyrics-panel {
  margin-top: 26px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.05);
}

.creation-panel {
  margin-top: 24px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.05);
}

.creation-head {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.creation-title {
  font-size: 20px;
  font-weight: 600;
}

.creation-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.creation-grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.creation-card {
  padding: 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.creation-card-wide {
  grid-column: span 2;
}

.creation-card-title {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.62);
}

.creation-card-content {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.lyrics-title {
  font-size: 20px;
  margin-bottom: 10px;
}

.lyrics-item {
  padding: 6px 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.76);
}

.lyrics-item.active {
  color: #ffd36d;
  font-size: 18px;
}

@media (max-width: 720px) {
  .music-detail-body {
    padding: 14px 10px 0;
  }

  .music-panel {
    flex-direction: column;
    text-align: center;
    gap: 18px;
    padding: 18px 14px 88px;
    border-radius: 22px;
  }

  .music-cover {
    margin: 0 auto;
    width: 220px;
    height: 220px;
  }

  .music-cover-bg {
    width: 220px;
    height: 220px;
    background-size: cover;
  }

  .meta-panel,
  .stats-panel,
  .action-panel,
  .action-buttons,
  .creation-tags,
  .owner-actions {
    justify-content: center;
  }

  .stats-panel {
    grid-template-columns: 1fr;
  }

  .creation-grid {
    grid-template-columns: 1fr;
  }

  .creation-card-wide {
    grid-column: span 1;
  }

  .owner-status {
    max-width: none;
  }

  .lyrics-panel {
    text-align: left;
  }
}

@keyframes rotateBackground {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}
</style>
