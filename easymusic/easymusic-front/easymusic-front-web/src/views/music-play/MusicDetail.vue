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
            <el-button size="large" @click="openAICoverDialog" v-if="isOwner && musicInfo.musicStatus === 1">
              AI 生成封面
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

    <AICoverDialog ref="aiCoverDialogRef" @updated="applyCoverChange"></AICoverDialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import ActionShare from "@/component/biz/ActionShare.vue";
import ActionGood from "@/component/biz/ActionGood.vue";
import AICoverDialog from "@/component/biz/AICoverDialog.vue";
import { useMusicPlayStore } from "@/stores/musicPlay.js";
import { useUserInfoStore } from "@/stores/userInfoStore";
import { mitter } from "@/eventbus/eventBus.js";

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

const playMusic = () => {
  if (musicPlayStore.currentMusic?.musicId === musicInfo.value.musicId) {
    mitter.emit("togglePlay");
    return;
  }
  musicPlayStore.play({ ...musicInfo.value });
};

const createSame = () => {
  router.push(`/idea/${musicInfo.value.creationId}`);
};

const aiCoverDialogRef = ref();
const openAICoverDialog = () => {
  aiCoverDialogRef.value.show(musicInfo.value);
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
  padding: 10px 16px 88px 10px;
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
  }

  .music-cover {
    margin: 0 auto;
  }

  .meta-panel,
  .action-panel,
  .action-buttons,
  .creation-tags {
    justify-content: center;
  }

  .creation-grid {
    grid-template-columns: 1fr;
  }

  .creation-card-wide {
    grid-column: span 1;
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
