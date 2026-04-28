<template>
  <div class="user-home-page">
    <section class="user-hero">
      <div class="hero-head">
        <BackBtn></BackBtn>
        <div class="hero-user">
          <Avatar :avatar="userInfo.avatar" :width="96"></Avatar>
          <div class="hero-copy">
            <div class="hero-kicker">Creator Profile</div>
            <div class="hero-title">{{ userInfo.nickName || "创作者主页" }}</div>
            <div class="hero-desc">
              这里展示该创作者已经公开发布的作品。页面只承接公开内容，和首页、最新页保持同一可见性口径。
            </div>
          </div>
        </div>
      </div>
      <div class="hero-stats">
        <span class="hero-chip">公开作品 {{ userInfo.musicCount || 0 }}</span>
        <span class="hero-chip">累计获赞 {{ userInfo.goodCount || 0 }}</span>
        <span class="hero-chip">已加载 {{ loadedCount }}</span>
        <span class="hero-chip">AI 扩写 {{ aiPromptCount }}</span>
      </div>
    </section>

    <div class="overview-grid">
      <div v-for="card in summaryCards" :key="card.label" class="overview-card">
        <div class="overview-label">{{ card.label }}</div>
        <div class="overview-value">{{ card.value }}</div>
        <div class="overview-desc">{{ card.desc }}</div>
      </div>
    </div>

    <section class="works-panel">
      <div class="panel-head">
        <div>
          <div class="panel-title">已发布作品列表</div>
          <div class="panel-desc">滚动加载继续保留在局部容器内，避免用户主页和全局布局互相抢滚动。</div>
        </div>
        <div class="panel-side">
          <div class="panel-chip">AI 封面 {{ aiCoverCount }}</div>
          <div class="panel-refresh iconfont icon-refresh" @click="loadMusicList(true)"></div>
        </div>
      </div>
      <div class="data-list">
        <DataLoadMoreList :dataSource="dataSource" :loading="loading" @loadData="loadMusicList">
          <template #default="{ data }">
            <MusicItem :data="data" @reload="reloadMusic" @playList="playList"></MusicItem>
          </template>
        </DataLoadMoreList>
      </div>
    </section>
  </div>
</template>

<script setup>
import MusicItem from "@/component/biz/MusicItem.vue";
import { computed, getCurrentInstance, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useMusicPlayStore } from "@/stores/musicPlay.js";

const { proxy } = getCurrentInstance();
const route = useRoute();
const musicPlayStore = useMusicPlayStore();

const userInfo = ref({});
const dataSource = ref({
  pageNo: 0,
  pageTotal: 0,
  totalCount: 0,
  list: [],
});
const loading = ref(false);
const dataList = ref([]);

const loadedCount = computed(() => dataSource.value.list?.length || 0);
const aiPromptCount = computed(() => {
  return (dataSource.value.list || []).filter((item) => item.promptSourceType === 1).length;
});
const aiCoverCount = computed(() => {
  return (dataSource.value.list || []).reduce((sum, item) => sum + (item.coverGenerateCount || 0), 0);
});
const totalPlayCount = computed(() => {
  return (dataSource.value.list || []).reduce((sum, item) => sum + (item.playCount || 0), 0);
});
const totalDuration = computed(() => {
  return (dataSource.value.list || []).reduce((sum, item) => sum + (item.duration || 0), 0);
});

const summaryCards = computed(() => {
  return [
    {
      label: "公开作品",
      value: userInfo.value.musicCount || 0,
      desc: "创作者当前已公开展示的作品数量",
    },
    {
      label: "累计获赞",
      value: userInfo.value.goodCount || 0,
      desc: "公开作品在平台内累计获得的点赞总量",
    },
    {
      label: "已加载播放",
      value: totalPlayCount.value,
      desc: "当前列表中作品累积的播放次数",
    },
    {
      label: "已加载时长",
      value: proxy.Utils.seconds2Min(totalDuration.value || 0),
      desc: "按当前已加载作品汇总的播放时长",
    },
  ];
});

const getUserInfo = async () => {
  const result = await proxy.Request({
    url: proxy.Api.getUserInfo,
    params: {
      userId: route.params.userId,
    },
  });
  if (!result) {
    return;
  }
  userInfo.value = result.data || {};
};

const resetPaging = () => {
  dataList.value = [];
  dataSource.value = {
    pageNo: 0,
    pageTotal: 0,
    totalCount: 0,
    list: [],
  };
};

const loadMusicList = async (refresh) => {
  if (refresh) {
    resetPaging();
  }
  if (
    dataSource.value.pageNo > 0 &&
    dataSource.value.pageNo === dataSource.value.pageTotal
  ) {
    return;
  }
  loading.value = true;
  let pageNo = dataSource.value.pageNo || 0;
  pageNo++;
  const result = await proxy.Request({
    url: proxy.Api.loadUserMusic,
    showLoading: false,
    params: {
      pageNo,
      userId: route.params.userId,
    },
  });
  loading.value = false;
  if (!result) {
    return;
  }
  if (result.data.pageNo === 1) {
    dataList.value = result.data.list || [];
  } else {
    dataList.value = dataList.value.concat(result.data.list || []);
  }
  result.data.list = dataList.value;
  dataSource.value = result.data;
};

const playList = () => {
  musicPlayStore.savePlayList(dataSource.value.list || []);
};

const reloadMusic = () => {
  loadMusicList(true);
};

const reloadPage = async () => {
  userInfo.value = {};
  resetPaging();
  await getUserInfo();
  await loadMusicList(true);
};

onMounted(() => {
  reloadPage();
});

watch(
  () => route.params.userId,
  () => {
    reloadPage();
  }
);
</script>

<style lang="scss" scoped>
.user-home-page {
  height: calc(100dvh - 125px);
  min-height: calc(100dvh - 125px);
  display: flex;
  flex-direction: column;
  gap: 16px;
  color: #fff;
}

.user-hero,
.works-panel {
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.user-hero {
  padding: 20px 22px;
  background:
    radial-gradient(circle at top left, rgba(255, 183, 77, 0.2), transparent 34%),
    linear-gradient(135deg, rgba(16, 18, 38, 0.96), rgba(26, 29, 56, 0.92));
}

.hero-head {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.hero-user {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.hero-copy {
  min-width: 0;
}

.hero-kicker {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.58);
}

.hero-title {
  margin-top: 8px;
  font-size: 30px;
  line-height: 1.3;
  font-weight: 700;
}

.hero-desc {
  margin-top: 10px;
  max-width: 720px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.72);
}

.hero-stats {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-chip,
.panel-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.84);
  font-size: 12px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.overview-card {
  padding: 16px 18px;
  border-radius: 18px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.06), rgba(255, 255, 255, 0.03));
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.overview-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.58);
}

.overview-value {
  margin-top: 10px;
  font-size: 24px;
  line-height: 1.2;
  font-weight: 700;
}

.overview-desc {
  margin-top: 10px;
  font-size: 12px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.68);
}

.works-panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 18px 18px 14px;
  background: rgba(255, 255, 255, 0.035);
}

.panel-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding: 0 4px 14px;
}

.panel-title {
  font-size: 24px;
  line-height: 1.3;
  font-weight: 700;
}

.panel-desc {
  margin-top: 8px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.68);
}

.panel-side {
  display: flex;
  align-items: center;
  gap: 10px;
}

.panel-refresh {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.08);
}

.panel-refresh:hover {
  background: rgba(255, 255, 255, 0.16);
}

.data-list {
  flex: 1;
  min-height: 0;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

@media (max-width: 900px) {
  .user-home-page {
    height: calc(100dvh - 145px);
    min-height: calc(100dvh - 145px);
  }

  .overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .panel-head {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 600px) {
  .user-home-page {
    height: calc(100dvh - 176px);
    min-height: calc(100dvh - 176px);
    gap: 12px;
  }

  .user-hero,
  .works-panel {
    border-radius: 20px;
  }

  .user-hero {
    padding: 16px;
  }

  .hero-head,
  .hero-user {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-title {
    font-size: 24px;
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }

  .works-panel {
    padding: 14px 12px 12px;
  }

  .panel-head {
    padding: 0 2px 10px;
  }

  .panel-title {
    font-size: 22px;
  }

  .panel-side {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
