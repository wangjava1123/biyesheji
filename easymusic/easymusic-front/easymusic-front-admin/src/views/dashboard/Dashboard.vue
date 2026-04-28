<template>
  <div class="dashboard-page" v-loading="loading">
    <section class="hero-card">
      <div>
        <div class="eyebrow">EasyMusic Admin Dashboard</div>
        <h1>平台统计看板</h1>
        <p>
          聚合当前用户、创作、作品、封面、积分和充值口径，用于毕业设计阶段展示与后续联调前核对。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" @click="loadDashboard">刷新数据</el-button>
        <div class="hero-tip">当前为实时聚合版本，未接入日汇总表。</div>
      </div>
    </section>

    <section class="summary-grid">
      <article class="summary-card" v-for="item in summaryCards" :key="item.label">
        <div class="summary-label">{{ item.label }}</div>
        <div class="summary-value">{{ item.value }}</div>
        <div class="summary-desc">{{ item.desc }}</div>
      </article>
    </section>

    <section class="dashboard-grid">
      <article class="panel panel-span-7">
        <div class="panel-header">
          <div>
            <h3>平台全局概览</h3>
            <p>看总量、发布口径和用户活跃度。</p>
          </div>
        </div>
        <div class="metric-grid">
          <div class="metric-item" v-for="item in overviewItems" :key="item.label">
            <div class="metric-label">{{ item.label }}</div>
            <div class="metric-value">{{ item.value }}</div>
            <div class="metric-desc">{{ item.desc }}</div>
          </div>
        </div>
      </article>

      <article class="panel panel-span-5">
        <div class="panel-header">
          <div>
            <h3>成功率与消耗</h3>
            <p>看音乐生成、封面生成和积分收支节奏。</p>
          </div>
        </div>
        <div class="progress-group">
          <div class="progress-row">
            <div class="progress-text">
              <span>音乐生成成功率</span>
              <strong>{{ formatPercent(dashboardData.musicSuccessRate) }}</strong>
            </div>
            <el-progress :percentage="safePercent(dashboardData.musicSuccessRate)" :stroke-width="10" :show-text="false" />
          </div>
          <div class="progress-row">
            <div class="progress-text">
              <span>封面生成成功率</span>
              <strong>{{ formatPercent(dashboardData.coverSuccessRate) }}</strong>
            </div>
            <el-progress :percentage="safePercent(dashboardData.coverSuccessRate)" :stroke-width="10" :show-text="false" color="#14b8a6" />
          </div>
        </div>
        <div class="stat-pills">
          <div class="stat-pill">
            <span>总创作</span>
            <strong>{{ formatCount(dashboardData.totalCreationCount) }}</strong>
          </div>
          <div class="stat-pill">
            <span>成功作品</span>
            <strong>{{ formatCount(dashboardData.successMusicCount) }}</strong>
          </div>
          <div class="stat-pill">
            <span>封面任务</span>
            <strong>{{ formatCount(dashboardData.totalCoverTaskCount) }}</strong>
          </div>
          <div class="stat-pill">
            <span>今日封面</span>
            <strong>{{ formatCount(dashboardData.todayCoverTaskCount) }}</strong>
          </div>
        </div>
      </article>

      <article class="panel panel-span-6">
        <div class="panel-header">
          <div>
            <h3>作品状态分布</h3>
            <p>同时看生成状态和发布状态是否均衡。</p>
          </div>
        </div>
        <div class="distribution-section">
          <div class="distribution-title">生成状态</div>
          <div class="distribution-list">
            <div class="distribution-item" v-for="item in musicStatusItems" :key="item.label">
              <div class="distribution-head">
                <span>{{ item.label }}</span>
                <strong>{{ formatCount(item.value) }}</strong>
              </div>
              <div class="distribution-bar">
                <span :style="{ width: `${item.percent}%`, background: item.color }"></span>
              </div>
            </div>
          </div>
        </div>
        <div class="distribution-section">
          <div class="distribution-title">发布状态</div>
          <div class="distribution-list">
            <div class="distribution-item" v-for="item in publishStatusItems" :key="item.label">
              <div class="distribution-head">
                <span>{{ item.label }}</span>
                <strong>{{ formatCount(item.value) }}</strong>
              </div>
              <div class="distribution-bar">
                <span :style="{ width: `${item.percent}%`, background: item.color }"></span>
              </div>
            </div>
          </div>
        </div>
      </article>

      <article class="panel panel-span-6">
        <div class="panel-header">
          <div>
            <h3>来源结构</h3>
            <p>看提示词增强和封面来源是否逐步转向 AIGC 主线。</p>
          </div>
        </div>
        <div class="source-grid">
          <div class="source-card">
            <div class="distribution-title">提示词来源</div>
            <div class="distribution-list">
              <div class="distribution-item" v-for="item in promptSourceItems" :key="item.label">
                <div class="distribution-head">
                  <span>{{ item.label }}</span>
                  <strong>{{ formatCount(item.value) }}</strong>
                </div>
                <div class="distribution-bar">
                  <span :style="{ width: `${item.percent}%`, background: item.color }"></span>
                </div>
              </div>
            </div>
          </div>
          <div class="source-card">
            <div class="distribution-title">封面来源</div>
            <div class="distribution-list">
              <div class="distribution-item" v-for="item in coverSourceItems" :key="item.label">
                <div class="distribution-head">
                  <span>{{ item.label }}</span>
                  <strong>{{ formatCount(item.value) }}</strong>
                </div>
                <div class="distribution-bar">
                  <span :style="{ width: `${item.percent}%`, background: item.color }"></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </article>

      <article class="panel panel-span-4">
        <div class="panel-header">
          <div>
            <h3>热门模型</h3>
            <p>按使用次数排序，并带成功率。</p>
          </div>
        </div>
        <div v-if="topModels.length" class="rank-list">
          <div class="rank-row" v-for="(item, index) in topModels" :key="`${item.modelName}-${index}`">
            <div class="rank-index">{{ index + 1 }}</div>
            <div class="rank-body">
              <div class="rank-title">{{ item.modelName || "未设置" }}</div>
              <div class="rank-meta">使用 {{ formatCount(item.totalCount) }} 次 · 成功 {{ formatPercent(item.successRate) }}</div>
            </div>
            <div class="rank-value">{{ formatCount(item.successCount) }}</div>
          </div>
        </div>
        <NoData v-else msg="暂无模型数据"></NoData>
      </article>

      <article class="panel panel-span-4">
        <div class="panel-header">
          <div>
            <h3>热门作品</h3>
            <p>按获赞和播放综合排序，仅统计已发布作品。</p>
          </div>
        </div>
        <div v-if="hotMusics.length" class="music-list">
          <div class="music-row" v-for="item in hotMusics" :key="item.musicId">
            <Cover :cover="item.cover" :width="62" :scale="1"></Cover>
            <div class="music-body">
              <div class="music-title">{{ item.musicTitle || "未命名作品" }}</div>
              <div class="music-meta">{{ item.nickName || "--" }}</div>
              <div class="music-stats">
                <span>播放 {{ formatCount(item.playCount) }}</span>
                <span>获赞 {{ formatCount(item.goodCount) }}</span>
              </div>
            </div>
          </div>
        </div>
        <NoData v-else msg="暂无作品排行"></NoData>
      </article>

      <article class="panel panel-span-4">
        <div class="panel-header">
          <div>
            <h3>热门创作者</h3>
            <p>按已发布作品的总获赞和总播放排序。</p>
          </div>
        </div>
        <div v-if="hotCreators.length" class="creator-list">
          <div class="creator-row" v-for="item in hotCreators" :key="item.userId">
            <Cover :cover="item.avatar" :width="44" borderRadius="50%"></Cover>
            <div class="creator-body">
              <div class="creator-title">{{ item.nickName || item.userId }}</div>
              <div class="creator-meta">作品 {{ formatCount(item.musicCount) }}</div>
            </div>
            <div class="creator-side">
              <div>赞 {{ formatCount(item.totalGoodCount) }}</div>
              <div>播 {{ formatCount(item.totalPlayCount) }}</div>
            </div>
          </div>
        </div>
        <NoData v-else msg="暂无创作者排行"></NoData>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, ref } from "vue";

const { proxy } = getCurrentInstance();

const createEmptyDashboard = () => ({
  totalUserCount: 0,
  todayNewUserCount: 0,
  activeCreatorCount: 0,
  totalCreationCount: 0,
  todayCreationCount: 0,
  totalMusicCount: 0,
  successMusicCount: 0,
  todaySuccessMusicCount: 0,
  creatingMusicCount: 0,
  failedMusicCount: 0,
  publishedMusicCount: 0,
  draftMusicCount: 0,
  hiddenMusicCount: 0,
  totalPlayCount: 0,
  totalGoodCount: 0,
  totalCoverTaskCount: 0,
  successCoverTaskCount: 0,
  todayCoverTaskCount: 0,
  aiPromptCount: 0,
  manualPromptCount: 0,
  aiCoverMusicCount: 0,
  manualCoverMusicCount: 0,
  todayIntegralConsume: 0,
  todayRechargeAmount: 0,
  musicSuccessRate: 0,
  coverSuccessRate: 0,
  topModels: [],
  hotMusics: [],
  hotCreators: [],
});

const loading = ref(false);
const dashboardData = ref(createEmptyDashboard());

const summaryCards = computed(() => [
  {
    label: "今日创作数",
    value: formatCount(dashboardData.value.todayCreationCount),
    desc: `今日成功 ${formatCount(dashboardData.value.todaySuccessMusicCount)} 首`,
  },
  {
    label: "音乐成功率",
    value: formatPercent(dashboardData.value.musicSuccessRate),
    desc: `累计成功 ${formatCount(dashboardData.value.successMusicCount)} / ${formatCount(dashboardData.value.totalMusicCount)}`,
  },
  {
    label: "封面成功率",
    value: formatPercent(dashboardData.value.coverSuccessRate),
    desc: `累计封面任务 ${formatCount(dashboardData.value.totalCoverTaskCount)} 个`,
  },
  {
    label: "今日积分消耗",
    value: formatCount(dashboardData.value.todayIntegralConsume),
    desc: "音乐、封面和提示词增强的扣减总量",
  },
  {
    label: "今日充值金额",
    value: formatMoney(dashboardData.value.todayRechargeAmount),
    desc: "仅统计已支付订单",
  },
  {
    label: "活跃创作者",
    value: formatCount(dashboardData.value.activeCreatorCount),
    desc: `今日新增用户 ${formatCount(dashboardData.value.todayNewUserCount)} 人`,
  },
]);

const overviewItems = computed(() => [
  {
    label: "平台用户",
    value: formatCount(dashboardData.value.totalUserCount),
    desc: "当前累计注册用户",
  },
  {
    label: "总创作次数",
    value: formatCount(dashboardData.value.totalCreationCount),
    desc: "music_creation 累计记录",
  },
  {
    label: "成功作品",
    value: formatCount(dashboardData.value.successMusicCount),
    desc: "music_info 成功生成记录",
  },
  {
    label: "已发布作品",
    value: formatCount(dashboardData.value.publishedMusicCount),
    desc: `草稿 ${formatCount(dashboardData.value.draftMusicCount)} · 隐藏 ${formatCount(dashboardData.value.hiddenMusicCount)}`,
  },
  {
    label: "总播放量",
    value: formatCount(dashboardData.value.totalPlayCount),
    desc: "全站公开口径累计播放",
  },
  {
    label: "总获赞量",
    value: formatCount(dashboardData.value.totalGoodCount),
    desc: "点赞聚合字段累计值",
  },
  {
    label: "生成中作品",
    value: formatCount(dashboardData.value.creatingMusicCount),
    desc: `失败作品 ${formatCount(dashboardData.value.failedMusicCount)} 首`,
  },
  {
    label: "AI 封面作品",
    value: formatCount(dashboardData.value.aiCoverMusicCount),
    desc: `手动封面 ${formatCount(dashboardData.value.manualCoverMusicCount)} 首`,
  },
]);

const musicStatusItems = computed(() =>
  buildDistribution(
    [
      { label: "生成成功", value: dashboardData.value.successMusicCount, color: "#2563eb" },
      { label: "生成中", value: dashboardData.value.creatingMusicCount, color: "#f59e0b" },
      { label: "生成失败", value: dashboardData.value.failedMusicCount, color: "#ef4444" },
    ],
    dashboardData.value.totalMusicCount
  )
);

const publishStatusItems = computed(() =>
  buildDistribution(
    [
      { label: "草稿", value: dashboardData.value.draftMusicCount, color: "#94a3b8" },
      { label: "已发布", value: dashboardData.value.publishedMusicCount, color: "#10b981" },
      { label: "已隐藏", value: dashboardData.value.hiddenMusicCount, color: "#f97316" },
    ],
    dashboardData.value.totalMusicCount
  )
);

const promptSourceItems = computed(() =>
  buildDistribution(
    [
      { label: "AI 增强", value: dashboardData.value.aiPromptCount, color: "#2563eb" },
      { label: "手写 Prompt", value: dashboardData.value.manualPromptCount, color: "#64748b" },
    ],
    dashboardData.value.aiPromptCount + dashboardData.value.manualPromptCount
  )
);

const coverSourceItems = computed(() =>
  buildDistribution(
    [
      { label: "AI 封面", value: dashboardData.value.aiCoverMusicCount, color: "#14b8a6" },
      { label: "手动封面", value: dashboardData.value.manualCoverMusicCount, color: "#8b5cf6" },
    ],
    dashboardData.value.aiCoverMusicCount + dashboardData.value.manualCoverMusicCount
  )
);

const topModels = computed(() => dashboardData.value.topModels || []);
const hotMusics = computed(() => dashboardData.value.hotMusics || []);
const hotCreators = computed(() => dashboardData.value.hotCreators || []);

const loadDashboard = async () => {
  loading.value = true;
  const result = await proxy.Request({
    url: proxy.Api.loadDashboard,
    showLoading: false,
  });
  loading.value = false;
  if (!result) {
    return;
  }
  dashboardData.value = Object.assign(createEmptyDashboard(), result.data || {});
};

const buildDistribution = (items, total) => {
  const safeTotal = Number(total || 0);
  return items.map((item) => ({
    ...item,
    percent: safeTotal > 0 ? Number(((Number(item.value || 0) / safeTotal) * 100).toFixed(1)) : 0,
  }));
};

const formatCount = (value) => Number(value || 0).toLocaleString("zh-CN");

const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`;

const formatPercent = (value) => `${Number(value || 0).toFixed(1)}%`;

const safePercent = (value) => {
  const percent = Number(value || 0);
  if (percent < 0) {
    return 0;
  }
  if (percent > 100) {
    return 100;
  }
  return percent;
};

onMounted(() => {
  loadDashboard();
});
</script>

<style lang="scss" scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: calc(100vh - 20px);
  padding-bottom: 12px;
  background:
    radial-gradient(circle at top right, rgba(37, 99, 235, 0.08), transparent 28%),
    linear-gradient(180deg, #f7faff 0%, #f3f6fb 100%);
}

.hero-card,
.panel,
.summary-card {
  border: 1px solid #e6edf8;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
}

.hero-card {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 26px;
  border-radius: 20px;
  background: linear-gradient(135deg, #0f172a 0%, #1d4ed8 55%, #38bdf8 100%);
  color: #ffffff;

  h1 {
    margin: 8px 0 10px;
    font-size: 30px;
    line-height: 1.1;
  }

  p {
    max-width: 720px;
    margin: 0;
    line-height: 1.6;
    color: rgba(255, 255, 255, 0.82);
  }
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.hero-actions {
  display: flex;
  min-width: 220px;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
}

.hero-tip {
  max-width: 220px;
  font-size: 12px;
  line-height: 1.6;
  text-align: right;
  color: rgba(255, 255, 255, 0.7);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 12px;
}

.summary-card {
  padding: 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.summary-label,
.metric-label,
.distribution-title,
.panel-header p,
.music-meta,
.rank-meta,
.creator-meta {
  color: #64748b;
}

.summary-label,
.metric-label {
  font-size: 13px;
}

.summary-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  color: #0f172a;
}

.summary-desc,
.metric-desc {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.5;
  color: #94a3b8;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 16px;
}

.panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
}

.panel-span-7 {
  grid-column: span 7;
}

.panel-span-6 {
  grid-column: span 6;
}

.panel-span-5 {
  grid-column: span 5;
}

.panel-span-4 {
  grid-column: span 4;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;

  h3 {
    margin: 0;
    font-size: 18px;
    color: #0f172a;
  }

  p {
    margin: 6px 0 0;
    font-size: 13px;
    line-height: 1.5;
  }
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.metric-item {
  padding: 16px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #edf2fb;
}

.metric-value {
  margin-top: 10px;
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
}

.progress-group {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.progress-row {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #0f172a;
}

.stat-pills {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.stat-pill {
  padding: 14px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #edf2fb;

  span {
    display: block;
    font-size: 12px;
    color: #64748b;
  }

  strong {
    display: block;
    margin-top: 8px;
    font-size: 20px;
    color: #0f172a;
  }
}

.distribution-section,
.source-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.source-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.distribution-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.distribution-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #0f172a;
}

.distribution-bar {
  height: 10px;
  overflow: hidden;
  border-radius: 999px;
  background: #e8eef8;

  span {
    display: block;
    height: 100%;
    border-radius: inherit;
  }
}

.rank-list,
.music-list,
.creator-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-row,
.music-row,
.creator-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #edf2fb;
}

.rank-index {
  display: flex;
  height: 36px;
  width: 36px;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 700;
}

.rank-body,
.music-body,
.creator-body {
  flex: 1;
  min-width: 0;
}

.rank-title,
.music-title,
.creator-title {
  overflow: hidden;
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rank-value {
  font-size: 18px;
  font-weight: 700;
  color: #1d4ed8;
}

.music-stats {
  display: flex;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
  color: #475569;
}

.creator-side {
  text-align: right;
  font-size: 12px;
  line-height: 1.6;
  color: #475569;
}

@media (max-width: 1500px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .panel-span-7,
  .panel-span-6,
  .panel-span-5,
  .panel-span-4 {
    grid-column: span 6;
  }

  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .hero-card {
    flex-direction: column;
  }

  .hero-actions {
    align-items: flex-start;
  }

  .hero-tip {
    max-width: none;
    text-align: left;
  }

  .summary-grid,
  .dashboard-grid,
  .source-grid,
  .metric-grid,
  .stat-pills {
    grid-template-columns: 1fr;
  }

  .panel-span-7,
  .panel-span-6,
  .panel-span-5,
  .panel-span-4 {
    grid-column: span 12;
  }
}
</style>
