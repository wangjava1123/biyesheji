<template>
  <div class="cover-record-page">
    <div class="summary-grid">
      <div class="summary-card" v-for="item in summaryCards" :key="item.label">
        <div class="summary-label">{{ item.label }}</div>
        <div class="summary-value">{{ item.value }}</div>
        <div class="summary-desc">{{ item.desc }}</div>
      </div>
    </div>

    <div class="top-panel">
      <el-form :model="searchForm" @submit.prevent>
        <el-row :gutter="10">
          <el-col :span="5">
            <el-form-item label="用户昵称">
              <el-input clearable placeholder="输入用户昵称" v-model="searchForm.nickNameFuzzy"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="作品标题">
              <el-input clearable placeholder="输入作品标题" v-model="searchForm.musicTitleFuzzy"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="任务状态">
              <el-select clearable placeholder="请选择任务状态" v-model="searchForm.status">
                <el-option :value="0" label="生成中"></el-option>
                <el-option :value="1" label="成功"></el-option>
                <el-option :value="2" label="失败"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="发布状态">
              <el-select clearable placeholder="请选择发布状态" v-model="searchForm.publishStatus">
                <el-option :value="0" label="草稿"></el-option>
                <el-option :value="1" label="已发布"></el-option>
                <el-option :value="2" label="已隐藏"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" class="action-group">
            <el-button type="primary" @click="loadDataList(true)">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <Table :columns="columns" :fetch="loadDataList" :dataSource="tableData" :options="tableOptions"
      :extHeight="tableOptions.extHeight">
      <template #slotCover="{ row }">
        <div class="cover-cell">
          <Cover :cover="row.coverUrl || row.currentCover" :width="74" :scale="1"></Cover>
          <div class="cover-meta">
            <div>{{ row.coverUrl ? "本次生成结果" : "当前作品封面" }}</div>
            <div class="muted">{{ coverSourceLabel(row.coverSource) }}</div>
          </div>
        </div>
      </template>

      <template #slotMusicInfo="{ row }">
        <div class="music-info-cell">
          <div class="title">{{ row.musicTitle || "未命名作品" }}</div>
          <div class="meta">作者：{{ row.nickName || "--" }}</div>
          <div class="meta">作品ID：{{ row.musicId }}</div>
        </div>
      </template>

      <template #slotStatus="{ row }">
        <div class="status-cell">
          <el-tag :type="coverStatusType(row.status)" effect="light">{{ coverStatusLabel(row.status) }}</el-tag>
          <div class="meta">模型：{{ row.model || "--" }}</div>
        </div>
      </template>

      <template #slotPublish="{ row }">
        <div class="status-cell">
          <el-tag :type="publishStatusType(row.publishStatus)" effect="light">{{ publishStatusLabel(row.publishStatus) }}</el-tag>
          <div class="meta">作品状态：{{ musicStatusLabel(row.musicStatus) }}</div>
        </div>
      </template>

      <template #slotPrompt="{ row }">
        <div class="line-clamp" :title="row.prompt || ''">{{ row.prompt || "--" }}</div>
      </template>

      <template #slotFailReason="{ row }">
        <div class="line-clamp muted" :title="row.failReason || ''">{{ row.failReason || "--" }}</div>
      </template>

      <template #slotTime="{ row }">
        <div class="time-cell">
          <div>创建：{{ row.createTime || "--" }}</div>
          <div class="muted">完成：{{ row.finishTime || "--" }}</div>
        </div>
      </template>
    </Table>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, ref } from "vue";

const { proxy } = getCurrentInstance();

const columns = [
  {
    label: "封面预览",
    prop: "coverUrl",
    width: 190,
    scopedSlots: "slotCover",
  },
  {
    label: "作品信息",
    prop: "musicTitle",
    width: 220,
    scopedSlots: "slotMusicInfo",
  },
  {
    label: "任务状态",
    prop: "status",
    width: 150,
    scopedSlots: "slotStatus",
  },
  {
    label: "发布信息",
    prop: "publishStatus",
    width: 150,
    scopedSlots: "slotPublish",
  },
  {
    label: "提示词",
    prop: "prompt",
    scopedSlots: "slotPrompt",
  },
  {
    label: "失败原因",
    prop: "failReason",
    width: 220,
    scopedSlots: "slotFailReason",
  },
  {
    label: "积分",
    prop: "integral",
    width: 80,
  },
  {
    label: "时间",
    prop: "createTime",
    width: 180,
    scopedSlots: "slotTime",
  },
];

const searchForm = ref({});
const tableData = ref({
  pageNo: 1,
  pageSize: 15,
});
const summaryData = ref({
  totalCount: 0,
  successCount: 0,
  failCount: 0,
  creatingCount: 0,
  todayCount: 0,
  musicCount: 0,
  successRate: 0,
});
const tableOptions = ref({
  extHeight: 178,
});

const summaryCards = computed(() => {
  return [
    {
      label: "总任务数",
      value: summaryData.value.totalCount || 0,
      desc: "当前筛选范围内的 AI 封面生成记录",
    },
    {
      label: "成功率",
      value: `${summaryData.value.successRate || 0}%`,
      desc: `成功 ${summaryData.value.successCount || 0} / 失败 ${summaryData.value.failCount || 0}`,
    },
    {
      label: "生成中",
      value: summaryData.value.creatingCount || 0,
      desc: "仍在进行中的封面任务",
    },
    {
      label: "今日新增",
      value: summaryData.value.todayCount || 0,
      desc: "今天创建的封面任务数量",
    },
    {
      label: "覆盖作品",
      value: summaryData.value.musicCount || 0,
      desc: "涉及不同作品的数量",
    },
  ];
});

const loadSummary = async () => {
  const result = await proxy.Request({
    url: proxy.Api.loadMusicCoverSummary,
    params: {
      ...searchForm.value,
    },
    showLoading: false,
  });
  if (!result) {
    return;
  }
  summaryData.value = Object.assign({}, summaryData.value, result.data || {});
};

const loadDataList = async (refresh = false) => {
  if (refresh) {
    tableData.value.pageNo = 1;
  }
  const result = await proxy.Request({
    url: proxy.Api.loadMusicCoverRecordList,
    params: {
      pageNo: tableData.value.pageNo,
      pageSize: tableData.value.pageSize,
      ...searchForm.value,
    },
  });
  if (!result) {
    return;
  }
  Object.assign(tableData.value, result.data);
  await loadSummary();
};

const resetSearch = () => {
  searchForm.value = {};
  loadDataList(true);
};

const coverStatusLabel = (status) => {
  const map = {
    0: "生成中",
    1: "成功",
    2: "失败",
  };
  return map[status] || "未知";
};

const coverStatusType = (status) => {
  const map = {
    0: "warning",
    1: "success",
    2: "danger",
  };
  return map[status] || "info";
};

const publishStatusLabel = (status) => {
  const map = {
    0: "草稿",
    1: "已发布",
    2: "已隐藏",
  };
  return map[status] || "未设置";
};

const publishStatusType = (status) => {
  const map = {
    0: "info",
    1: "success",
    2: "warning",
  };
  return map[status] || "info";
};

const musicStatusLabel = (status) => {
  const map = {
    0: "生成中",
    1: "已完成",
    2: "失败",
  };
  return map[status] || "未知";
};

const coverSourceLabel = (source) => {
  const map = {
    0: "当前封面：手动上传",
    1: "当前封面：AI 生成",
  };
  return map[source] || "当前封面：未设置";
};
</script>

<style lang="scss" scoped>
.cover-record-page {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.summary-card {
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f8fbff 0%, #f2f6ff 100%);
  border: 1px solid #e4edff;
}

.summary-label {
  font-size: 13px;
  color: #6b7280;
}

.summary-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 600;
  color: #1e40af;
}

.summary-desc {
  margin-top: 6px;
  font-size: 12px;
  color: #8a94a6;
  line-height: 1.4;
}

.top-panel {
  padding: 16px 16px 4px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #edf1f7;
}

.action-group {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
}

.cover-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cover-meta,
.music-info-cell,
.status-cell,
.time-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.music-info-cell .title {
  font-weight: 600;
  color: #111827;
}

.meta,
.muted {
  font-size: 12px;
  color: #6b7280;
}

.line-clamp {
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}
</style>
