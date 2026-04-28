<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    width="840px"
    :showCancel="false"
    @close="closeDialog"
  >
    <div class="cover-dialog">
      <div class="summary-panel">
        <div class="summary-cover">
          <Cover :cover="previewCover" :width="148" borderRadius="14px"></Cover>
        </div>
        <div class="summary-info">
          <div class="summary-title">{{ musicInfo.musicTitle || "未命名作品" }}</div>
          <div class="summary-tags">
            <span class="meta-tag">{{ coverSourceText }}</span>
            <span class="meta-tag">AI 封面 {{ musicInfo.coverGenerateCount || 0 }} 次</span>
            <span class="meta-tag">按系统配置扣减积分</span>
          </div>
          <div class="summary-desc">
            当前会先加载系统默认封面提示词。你可以直接编辑“封面提示词”和“视觉风格”后再点击生成。
          </div>
          <div class="summary-tip" v-if="promptPreview.sourceDesc">
            {{ promptPreview.sourceDesc }}
          </div>
          <div class="summary-actions">
            <el-button :loading="promptLoading" @click="loadPromptPreview">刷新默认词</el-button>
            <el-button @click="restoreDefaultPrompt" :disabled="promptLoading">恢复默认</el-button>
            <el-button type="primary" :loading="generating" @click="generateCover">
              立即生成
            </el-button>
          </div>
        </div>
      </div>

      <div class="editor-panel">
        <div class="editor-head">
          <div class="editor-title">封面提示词</div>
          <div class="editor-subtitle">这里就是点击“立即生成”时真正传给图片 AI 的文本，你可以自行修改。</div>
        </div>
        <el-input
          v-model="promptForm.prompt"
          type="textarea"
          :rows="5"
          maxlength="1000"
          show-word-limit
          resize="none"
          placeholder="请输入封面提示词"
        />

        <div class="editor-head style-head">
          <div class="editor-title">视觉风格</div>
          <div class="editor-subtitle">可选。比如“电影感插画”“赛博霓虹”“写实摄影”等。</div>
        </div>
        <el-input
          v-model="promptForm.style"
          maxlength="200"
          placeholder="请输入视觉风格"
        />
      </div>

      <div class="record-panel">
        <div class="record-header">
          <span>最近生成记录</span>
          <span class="record-header-tip">只展示当前作品最近 6 条封面记录</span>
        </div>
        <div v-if="recordLoading" class="record-empty">正在加载记录...</div>
        <div v-else-if="records.length === 0" class="record-empty">
          暂无 AI 封面生成记录
        </div>
        <div v-else class="record-list">
          <div class="record-item" v-for="record in records" :key="record.coverId">
            <div class="record-cover">
              <Cover
                :cover="record.coverUrl || musicInfo.cover"
                :width="88"
                borderRadius="10px"
              ></Cover>
            </div>
            <div class="record-main">
              <div class="record-top">
                <span :class="['status-tag', `status-${record.status}`]">
                  {{ getStatusText(record.status) }}
                </span>
                <span class="record-time">{{ record.createTime || "--" }}</span>
              </div>
              <div class="record-meta">
                <span>模型：{{ record.model || "--" }}</span>
                <span>风格：{{ record.style || "--" }}</span>
                <span>积分：{{ record.integral ?? 0 }}</span>
              </div>
              <div class="record-prompt" v-if="record.prompt">
                {{ record.prompt }}
              </div>
              <div class="record-fail" v-if="record.failReason">
                失败原因：{{ record.failReason }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Dialog>
</template>

<script setup>
import { computed, getCurrentInstance, ref } from "vue";
import { useUserInfoStore } from "@/stores/userInfoStore";

const { proxy } = getCurrentInstance();
const userInfoStore = useUserInfoStore();

const emits = defineEmits(["updated"]);

const dialogConfig = ref({
  show: false,
  title: "AI 生成封面",
});

const musicInfo = ref({});
const records = ref([]);
const generating = ref(false);
const recordLoading = ref(false);
const promptLoading = ref(false);

const promptPreview = ref({
  title: "",
  prompt: "",
  style: "",
  sourceDesc: "",
});

const promptForm = ref({
  prompt: "",
  style: "",
});

const coverSourceMap = {
  0: "当前封面：手动上传",
  1: "当前封面：AI 生成",
};

const previewCover = computed(() => {
  if (musicInfo.value.cover) {
    return musicInfo.value.cover;
  }
  if (records.value.length > 0 && records.value[0].coverUrl) {
    return withTimestamp(records.value[0].coverUrl);
  }
  return "";
});

const coverSourceText = computed(() => {
  const source = musicInfo.value.coverSource;
  return coverSourceMap[source] || "当前封面：未设置";
});

const withTimestamp = (cover) => {
  if (!cover) {
    return cover;
  }
  return `${cover}${cover.includes("?") ? "&" : "?"}t=${Date.now()}`;
};

const getStatusText = (status) => {
  const statusMap = {
    0: "生成中",
    1: "生成成功",
    2: "生成失败",
  };
  return statusMap[status] || "未知状态";
};

const closeDialog = () => {
  dialogConfig.value.show = false;
};

const restoreDefaultPrompt = () => {
  promptForm.value = {
    prompt: promptPreview.value.prompt || "",
    style: promptPreview.value.style || "",
  };
};

const loadPromptPreview = async () => {
  if (!musicInfo.value.musicId) {
    return;
  }
  promptLoading.value = true;
  const result = await proxy.Request({
    url: proxy.Api.musicCoverPromptPreview,
    showLoading: false,
    params: {
      musicId: musicInfo.value.musicId,
    },
  });
  promptLoading.value = false;
  if (!result) {
    return;
  }
  promptPreview.value = result.data || {};
  restoreDefaultPrompt();
};

const loadRecords = async () => {
  if (!musicInfo.value.musicId) {
    return;
  }
  recordLoading.value = true;
  const result = await proxy.Request({
    url: proxy.Api.musicCoverRecords,
    showLoading: false,
    params: {
      musicId: musicInfo.value.musicId,
      limit: 6,
    },
  });
  recordLoading.value = false;
  if (!result) {
    return;
  }
  records.value = result.data || [];
};

const generateCover = async () => {
  if (!musicInfo.value.musicId || generating.value) {
    return;
  }
  generating.value = true;
  const result = await proxy.Request({
    url: proxy.Api.musicCoverGenerate,
    showLoading: false,
    timeout: 120 * 1000,
    params: {
      musicId: musicInfo.value.musicId,
      prompt: promptForm.value.prompt?.trim() || "",
      style: promptForm.value.style?.trim() || "",
    },
  });
  await loadRecords();
  generating.value = false;
  if (!result) {
    return;
  }
  const nextCover = withTimestamp(result.data.coverUrl);
  const nextCount = (musicInfo.value.coverGenerateCount || 0) + 1;
  musicInfo.value = {
    ...musicInfo.value,
    cover: nextCover,
    coverSource: 1,
    coverGenerateCount: nextCount,
  };
  userInfoStore.updateLastReloadTime();
  emits("updated", {
    cover: nextCover,
    coverSource: 1,
    coverGenerateCount: nextCount,
    lastCoverRecord: result.data,
  });
  proxy.Message.success("AI 封面生成成功");
};

const show = async (music) => {
  musicInfo.value = { ...music };
  records.value = [];
  promptPreview.value = {
    title: "",
    prompt: "",
    style: "",
    sourceDesc: "",
  };
  promptForm.value = {
    prompt: "",
    style: "",
  };
  dialogConfig.value.show = true;
  await Promise.all([loadPromptPreview(), loadRecords()]);
};

defineExpose({
  show,
});
</script>

<style lang="scss" scoped>
.cover-dialog {
  color: #fff;
}

.summary-panel {
  display: flex;
  gap: 18px;
  padding: 6px 2px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.summary-cover {
  flex-shrink: 0;
}

.summary-info {
  flex: 1;
  min-width: 0;
}

.summary-title {
  font-size: 22px;
  font-weight: 600;
  line-height: 1.3;
}

.summary-tags {
  margin-top: 12px;
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

.summary-desc {
  margin-top: 14px;
  color: rgba(255, 255, 255, 0.72);
  line-height: 1.7;
}

.summary-tip {
  margin-top: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.7;
  font-size: 13px;
}

.summary-actions {
  margin-top: 18px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.editor-panel {
  margin-top: 18px;
  padding: 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
}

.editor-head {
  margin-bottom: 10px;
}

.style-head {
  margin-top: 18px;
}

.editor-title {
  font-size: 15px;
  font-weight: 600;
}

.editor-subtitle {
  margin-top: 6px;
  color: rgba(255, 255, 255, 0.62);
  font-size: 12px;
  line-height: 1.6;
}

.record-panel {
  padding-top: 18px;
}

.record-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 15px;
  font-weight: 600;
}

.record-header-tip {
  color: rgba(255, 255, 255, 0.55);
  font-size: 12px;
  font-weight: 400;
}

.record-empty {
  margin-top: 16px;
  padding: 26px 12px;
  text-align: center;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.65);
}

.record-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item {
  display: flex;
  gap: 14px;
  padding: 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
}

.record-cover {
  flex-shrink: 0;
}

.record-main {
  flex: 1;
  min-width: 0;
}

.record-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.status-0 {
  background: rgba(64, 158, 255, 0.18);
  color: #9bccff;
}

.status-1 {
  background: rgba(103, 194, 58, 0.18);
  color: #9fe26e;
}

.status-2 {
  background: rgba(245, 108, 108, 0.18);
  color: #ff9e9e;
}

.record-time {
  color: rgba(255, 255, 255, 0.58);
  font-size: 12px;
}

.record-meta {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
}

.record-prompt,
.record-fail {
  margin-top: 10px;
  line-height: 1.7;
  font-size: 13px;
  word-break: break-word;
}

.record-prompt {
  color: rgba(255, 255, 255, 0.86);
}

.record-fail {
  color: #ffb3b3;
}

@media (max-width: 640px) {
  .summary-panel {
    flex-direction: column;
  }

  .record-item {
    flex-direction: column;
  }

  .record-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
