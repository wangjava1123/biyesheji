<template>
  <div class="dashboard-page" v-loading="loading">
    <section class="hero-card">
      <div>
        <div class="eyebrow">EasyMusic Admin Dashboard</div>
        <h1>平台统计看板</h1>
        <p>
          聚合当前用户、创作、发布、封面、积分和充值口径，并补最近 7 日趋势，便于毕业设计答辩展示和联调前核对。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" @click="loadDashboard">刷新数据</el-button>
        <div class="hero-tip">
          最近刷新：{{ lastLoadedAt || "未刷新" }}<br />
          当前为实时聚合版本，近 7 日趋势按自然日统计。
        </div>
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
      <article class="panel panel-span-8">
        <div class="panel-header">
          <div>
            <h3>近 7 日主链路趋势</h3>
            <p>按自然日观察创作、成功、发布和封面任务的推进节奏。</p>
          </div>
        </div>
        <div class="trend-overview">
          <div class="trend-total-card" v-for="item in contentTrendSummary" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
            <small>{{ item.desc }}</small>
          </div>
        </div>
        <div class="trend-legend">
          <span v-for="item in trendLegend" :key="item.key">
            <i :style="{ background: item.color }"></i>
            {{ item.label }}
          </span>
        </div>
        <div v-if="contentTrendDays.length" class="trend-columns">
          <div class="trend-day" v-for="item in contentTrendDays" :key="item.trendDate">
            <div class="trend-bars">
              <div class="trend-bar-group" v-for="metric in item.metrics" :key="metric.key">
                <span class="trend-bar" :style="{ height: `${metric.height}%`, background: metric.color }"></span>
                <em>{{ formatCompactCount(metric.value) }}</em>
              </div>
            </div>
            <div class="trend-day-meta">
              <strong>{{ item.dateLabel }}</strong>
              <span>{{ item.weekdayLabel }}</span>
              <small>新增 {{ formatCount(item.newUserCount) }}</small>
            </div>
          </div>
        </div>
        <NoData v-else msg="暂无近 7 日趋势数据"></NoData>
      </article>

      <article class="panel panel-span-4">
        <div class="panel-header">
          <div>
            <h3>运营趋势与展示口径</h3>
            <p>补收支节奏、用户变化和答辩讲解时需要统一的统计说明。</p>
          </div>
        </div>
        <div class="ops-summary">
          <div class="ops-summary-card" v-for="item in opsTrendSummary" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
            <small>{{ item.desc }}</small>
          </div>
        </div>
        <div v-if="opsTrendDays.length" class="ops-trend-list">
          <div class="ops-trend-row" v-for="item in opsTrendDays" :key="item.trendDate">
            <div class="ops-trend-date">
              <strong>{{ item.dateLabel }}</strong>
              <span>{{ item.weekdayLabel }}</span>
            </div>
            <div class="ops-trend-lines">
              <div class="ops-trend-line">
                <div class="ops-line-head">
                  <span>积分消耗</span>
                  <strong>{{ formatCount(item.integralConsume) }}</strong>
                </div>
                <div class="ops-line-bar">
                  <span :style="{ width: `${item.integralPercent}%` }"></span>
                </div>
              </div>
              <div class="ops-trend-line">
                <div class="ops-line-head">
                  <span>充值金额</span>
                  <strong>{{ formatMoney(item.rechargeAmount) }}</strong>
                </div>
                <div class="ops-line-bar is-accent">
                  <span :style="{ width: `${item.rechargePercent}%` }"></span>
                </div>
              </div>
            </div>
            <div class="ops-trend-side">新增 {{ formatCount(item.newUserCount) }}</div>
          </div>
        </div>
        <NoData v-else msg="暂无运营趋势数据"></NoData>
        <div class="dashboard-notes">
          <div class="note-item" v-for="item in dashboardNotes" :key="item.label">
            <strong>{{ item.label }}</strong>
            <span>{{ item.desc }}</span>
          </div>
        </div>
      </article>

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
const trendLegend = [
  { key: "creationCount", label: "创作", color: "#2563eb" },
  { key: "successMusicCount", label: "成功", color: "#10b981" },
  { key: "publishCount", label: "发布", color: "#f59e0b" },
  { key: "coverTaskCount", label: "封面", color: "#8b5cf6" },
];

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
  last7DaysTrend: [],
  topModels: [],
  hotMusics: [],
  hotCreators: [],
});

const loading = ref(false);
const dashboardData = ref(createEmptyDashboard());
const lastLoadedAt = ref("");
const trendList = computed(() =>
  Array.isArray(dashboardData.value.last7DaysTrend) ? dashboardData.value.last7DaysTrend : []
);

const getTrendPoint = (offset) => {
  const list = trendList.value;
  const index = list.length - 1 - offset;
  if (index < 0 || !list[index]) {
    return {};
  }
  return list[index];
};

const todayTrend = computed(() => getTrendPoint(0));
const yesterdayTrend = computed(() => getTrendPoint(1));

const sevenDayTotals = computed(() => ({
  newUserCount: sumTrendField("newUserCount"),
  creationCount: sumTrendField("creationCount"),
  successMusicCount: sumTrendField("successMusicCount"),
  publishCount: sumTrendField("publishCount"),
  coverTaskCount: sumTrendField("coverTaskCount"),
  integralConsume: sumTrendField("integralConsume"),
  rechargeAmount: trendList.value.reduce((sum, item) => sum + Number(item.rechargeAmount || 0), 0),
}));

const summaryCards = computed(() => [
  {
    label: "今日创作数",
    value: formatCount(dashboardData.value.todayCreationCount),
    desc: `${formatDeltaCount(dashboardData.value.todayCreationCount, todayTrend.value.creationCount, yesterdayTrend.value.creationCount)} · 今日成功 ${formatCount(dashboardData.value.todaySuccessMusicCount)} 首`,
  },
  {
    label: "音乐成功率",
    value: formatPercent(dashboardData.value.musicSuccessRate),
    desc: `近 7 日成功 ${formatCount(sevenDayTotals.value.successMusicCount)} / 创作 ${formatCount(sevenDayTotals.value.creationCount)}`,
  },
  {
    label: "封面成功率",
    value: formatPercent(dashboardData.value.coverSuccessRate),
    desc: `近 7 日封面任务 ${formatCount(sevenDayTotals.value.coverTaskCount)} 个`,
  },
  {
    label: "今日积分消耗",
    value: formatCount(dashboardData.value.todayIntegralConsume),
    desc: `${formatDeltaCount(dashboardData.value.todayIntegralConsume, todayTrend.value.integralConsume, yesterdayTrend.value.integralConsume)} · 近 7 日累计 ${formatCount(sevenDayTotals.value.integralConsume)}`,
  },
  {
    label: "今日充值金额",
    value: formatMoney(dashboardData.value.todayRechargeAmount),
    desc: `${formatDeltaMoney(dashboardData.value.todayRechargeAmount, todayTrend.value.rechargeAmount, yesterdayTrend.value.rechargeAmount)} · 近 7 日累计 ${formatMoney(sevenDayTotals.value.rechargeAmount)}`,
  },
  {
    label: "活跃创作者",
    value: formatCount(dashboardData.value.activeCreatorCount),
    desc: `近 7 日新增用户 ${formatCount(sevenDayTotals.value.newUserCount)} 人`,
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

const contentTrendSummary = computed(() => [
  {
    label: "7 日创作",
    value: formatCount(sevenDayTotals.value.creationCount),
    desc: `日均 ${formatCount(calculateAverage(sevenDayTotals.value.creationCount, trendList.value.length))} 次`,
  },
  {
    label: "7 日成功",
    value: formatCount(sevenDayTotals.value.successMusicCount),
    desc: `成功率 ${formatPercent(calculateRateValue(sevenDayTotals.value.successMusicCount, sevenDayTotals.value.creationCount))}`,
  },
  {
    label: "7 日发布",
    value: formatCount(sevenDayTotals.value.publishCount),
    desc: `发布转化 ${formatPercent(calculateRateValue(sevenDayTotals.value.publishCount, sevenDayTotals.value.successMusicCount))}`,
  },
  {
    label: "7 日封面",
    value: formatCount(sevenDayTotals.value.coverTaskCount),
    desc: `日均 ${formatCount(calculateAverage(sevenDayTotals.value.coverTaskCount, trendList.value.length))} 次`,
  },
]);

const contentTrendDays = computed(() => {
  const metricMaxMap = trendLegend.reduce((acc, item) => {
    acc[item.key] = Math.max(
      1,
      ...trendList.value.map((trend) => Number(trend[item.key] || 0))
    );
    return acc;
  }, {});
  return trendList.value.map((item) => ({
    trendDate: item.trendDate,
    dateLabel: formatDateLabel(item.trendDate),
    weekdayLabel: formatWeekday(item.trendDate),
    newUserCount: Number(item.newUserCount || 0),
    metrics: trendLegend.map((metric) => ({
      ...metric,
      value: Number(item[metric.key] || 0),
      height: calculateScale(item[metric.key], metricMaxMap[metric.key]),
    })),
  }));
});

const opsTrendSummary = computed(() => [
  {
    label: "7 日新增用户",
    value: formatCount(sevenDayTotals.value.newUserCount),
    desc: `日均 ${formatCount(calculateAverage(sevenDayTotals.value.newUserCount, trendList.value.length))} 人`,
  },
  {
    label: "7 日积分消耗",
    value: formatCount(sevenDayTotals.value.integralConsume),
    desc: "含音乐生成、提示词增强和 AI 封面扣减",
  },
  {
    label: "7 日充值金额",
    value: formatMoney(sevenDayTotals.value.rechargeAmount),
    desc: "仅统计已支付充值订单",
  },
]);

const opsTrendDays = computed(() => {
  const maxIntegralConsume = Math.max(
    1,
    ...trendList.value.map((item) => Number(item.integralConsume || 0))
  );
  const maxRechargeAmount = Math.max(
    1,
    ...trendList.value.map((item) => Number(item.rechargeAmount || 0))
  );
  return trendList.value.map((item) => ({
    trendDate: item.trendDate,
    dateLabel: formatDateLabel(item.trendDate),
    weekdayLabel: formatWeekday(item.trendDate),
    newUserCount: Number(item.newUserCount || 0),
    integralConsume: Number(item.integralConsume || 0),
    rechargeAmount: Number(item.rechargeAmount || 0),
    integralPercent: calculateScale(item.integralConsume, maxIntegralConsume),
    rechargePercent: calculateScale(item.rechargeAmount, maxRechargeAmount),
  }));
});

const dashboardNotes = computed(() => [
  {
    label: "统计口径",
    desc: "总览为实时聚合；近 7 日趋势按自然日统计，不依赖日汇总表。",
  },
  {
    label: "公开范围",
    desc: "热门作品和热门创作者仅统计“生成成功 + 已发布”的公开作品。",
  },
  {
    label: "积分范围",
    desc: "积分消耗包含音乐生成、提示词增强和 AI 封面三类扣减。",
  },
  {
    label: "展示顺序",
    desc: "建议答辩时先讲主链路闭环，再看趋势、来源结构和排行榜。",
  },
]);

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
  lastLoadedAt.value = formatDateTime(new Date());
};

const buildDistribution = (items, total) => {
  const safeTotal = Number(total || 0);
  return items.map((item) => ({
    ...item,
    percent: safeTotal > 0 ? Number(((Number(item.value || 0) / safeTotal) * 100).toFixed(1)) : 0,
  }));
};

const formatCount = (value) => Number(value || 0).toLocaleString("zh-CN");

const formatCompactCount = (value) => {
  const count = Number(value || 0);
  if (count >= 10000) {
    return `${(count / 10000).toFixed(count % 10000 === 0 ? 0 : 1)}w`;
  }
  if (count >= 1000) {
    return `${(count / 1000).toFixed(count % 1000 === 0 ? 0 : 1)}k`;
  }
  return `${count}`;
};

const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`;

const formatPercent = (value) => `${Number(value || 0).toFixed(1)}%`;

const formatDeltaCount = (todayValue, currentValue, previousValue) => {
  const safeToday = Number(todayValue ?? currentValue ?? 0);
  const safePrevious = Number(previousValue || 0);
  const delta = safeToday - safePrevious;
  if (delta === 0) {
    return "较昨日持平";
  }
  return `较昨日 ${delta > 0 ? "+" : ""}${formatCount(delta)}`;
};

const formatDeltaMoney = (todayValue, currentValue, previousValue) => {
  const safeToday = Number(todayValue ?? currentValue ?? 0);
  const safePrevious = Number(previousValue || 0);
  const delta = safeToday - safePrevious;
  if (delta === 0) {
    return "较昨日持平";
  }
  return `较昨日 ${delta > 0 ? "+" : "-"}¥${Math.abs(delta).toFixed(2)}`;
};

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

const sumTrendField = (field) =>
  trendList.value.reduce((sum, item) => sum + Number(item[field] || 0), 0);

const calculateAverage = (total, count) => {
  if (!count) {
    return 0;
  }
  return Number((Number(total || 0) / count).toFixed(1));
};

const calculateRateValue = (successCount, totalCount) => {
  const total = Number(totalCount || 0);
  if (!total) {
    return 0;
  }
  return Number(((Number(successCount || 0) * 100) / total).toFixed(1));
};

const calculateScale = (value, maxValue) => {
  const safeMax = Number(maxValue || 0);
  if (!safeMax) {
    return 0;
  }
  const percent = Number(((Number(value || 0) / safeMax) * 100).toFixed(1));
  if (percent <= 0) {
    return 6;
  }
  return Math.max(12, Math.min(100, percent));
};

const formatDateLabel = (value) => {
  if (!value || value.length < 10) {
    return "--";
  }
  return value.slice(5).replace("-", "/");
};

const formatWeekday = (value) => {
  if (!value) {
    return "--";
  }
  const date = new Date(`${value}T00:00:00`);
  const weekMap = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  const weekIndex = date.getDay();
  return Number.isNaN(weekIndex) ? "--" : weekMap[weekIndex];
};

const formatDateTime = (date) => {
  const target = date instanceof Date ? date : new Date(date);
  if (Number.isNaN(target.getTime())) {
    return "";
  }
  const pad = (value) => `${value}`.padStart(2, "0");
  return `${target.getFullYear()}-${pad(target.getMonth() + 1)}-${pad(target.getDate())} ${pad(target.getHours())}:${pad(target.getMinutes())}:${pad(target.getSeconds())}`;
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

.panel-span-8 {
  grid-column: span 8;
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

.trend-overview,
.ops-summary {
  display: grid;
  gap: 12px;
}

.trend-overview {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.ops-summary {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.trend-total-card,
.ops-summary-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #edf2fb;

  span {
    font-size: 12px;
    color: #64748b;
  }

  strong {
    font-size: 24px;
    color: #0f172a;
  }

  small {
    color: #94a3b8;
    line-height: 1.5;
  }
}

.trend-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  font-size: 12px;
  color: #64748b;

  span {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  i {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 999px;
  }
}

.trend-columns {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 12px;
}

.trend-day {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px 10px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #edf2fb;
}

.trend-bars {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  align-items: end;
  gap: 8px;
  min-height: 180px;
}

.trend-bar-group {
  display: flex;
  min-height: 180px;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;

  em {
    font-size: 11px;
    font-style: normal;
    color: #475569;
  }
}

.trend-bar {
  width: 100%;
  min-height: 12px;
  border-radius: 999px 999px 8px 8px;
  box-shadow: inset 0 -2px 0 rgba(15, 23, 42, 0.08);
}

.trend-day-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  text-align: center;

  strong {
    font-size: 14px;
    color: #0f172a;
  }

  span,
  small {
    color: #64748b;
  }

  small {
    font-size: 12px;
  }
}

.ops-trend-list,
.dashboard-notes {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ops-trend-row,
.note-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #edf2fb;
}

.ops-trend-date {
  display: flex;
  min-width: 68px;
  flex-direction: column;
  gap: 4px;

  strong {
    font-size: 15px;
    color: #0f172a;
  }

  span {
    font-size: 12px;
    color: #64748b;
  }
}

.ops-trend-lines {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 10px;
}

.ops-trend-line {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.ops-line-head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 12px;
  color: #475569;

  strong {
    font-size: 12px;
    color: #0f172a;
  }
}

.ops-line-bar {
  height: 8px;
  overflow: hidden;
  border-radius: 999px;
  background: #dbeafe;

  span {
    display: block;
    height: 100%;
    border-radius: inherit;
    background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%);
  }
}

.ops-line-bar.is-accent {
  background: #d1fae5;

  span {
    background: linear-gradient(90deg, #14b8a6 0%, #34d399 100%);
  }
}

.ops-trend-side {
  min-width: 54px;
  font-size: 12px;
  line-height: 1.6;
  text-align: right;
  color: #475569;
}

.note-item {
  flex-direction: column;

  strong {
    font-size: 13px;
    color: #0f172a;
  }

  span {
    font-size: 12px;
    line-height: 1.6;
    color: #64748b;
  }
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

  .panel-span-8,
  .panel-span-7,
  .panel-span-6,
  .panel-span-5,
  .panel-span-4 {
    grid-column: span 6;
  }

  .metric-grid,
  .trend-overview,
  .ops-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .trend-columns {
    grid-template-columns: repeat(4, minmax(0, 1fr));
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
  .stat-pills,
  .trend-overview,
  .ops-summary,
  .trend-columns {
    grid-template-columns: 1fr;
  }

  .panel-span-8,
  .panel-span-7,
  .panel-span-6,
  .panel-span-5,
  .panel-span-4 {
    grid-column: span 12;
  }

  .trend-bars,
  .trend-bar-group {
    min-height: 140px;
  }

  .ops-trend-row {
    flex-direction: column;
  }

  .ops-trend-side {
    min-width: auto;
    text-align: left;
  }
}
</style>
