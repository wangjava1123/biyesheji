<template>
  <div class="creator-page">
    <div class="creator-hero">
      <div class="hero-copy">
        <div class="hero-kicker">{{ tabType === 0 ? '创作者中心' : '喜欢收藏' }}</div>
        <div class="hero-title">{{ pageTitle }}</div>
        <div class="hero-desc">{{ pageDesc }}</div>
      </div>
      <div class="hero-side">
        <div class="hero-stats">
          <span class="hero-chip">当前筛选 {{ dataSource.totalCount || 0 }} 条</span>
          <span class="hero-chip">已加载 {{ loadedCount }} 条</span>
          <span class="hero-chip" v-if="tabType === 0">已加载 AI 增强 {{ aiPromptCount }} 条</span>
          <span class="hero-chip" v-if="tabType === 0">已加载 AI 封面 {{ aiCoverCount }} 次</span>
        </div>
        <div class="hero-refresh iconfont icon-refresh" @click="loadMusicList(true)"></div>
      </div>
    </div>

    <div class="toolbar-panel">
      <Switch
        :data="[
          { label: '我的作品', value: 0 },
          { label: '我喜欢的作品', value: 1 },
        ]"
        v-model="tabType"
        @change="changeTab"
      ></Switch>
      <div class="filter-row" v-if="tabType === 0">
        <div
          v-for="item in workFilters"
          :key="item.key"
          :class="['filter-chip', activeFilterKey === item.key ? 'filter-chip-active' : '']"
          @click="changeFilter(item.key)"
        >
          {{ item.label }}
        </div>
      </div>
    </div>

    <div class="data-list">
      <DataLoadMoreList :dataSource="dataSource" :loading="loading" @loadData="loadMusicList">
        <template #default="{ data }">
          <MusicItem :data="data" @reload="reloadMusic" @playList="playList"></MusicItem>
        </template>
      </DataLoadMoreList>
    </div>
  </div>
</template>

<script setup>
import MusicItem from '@/component/biz/MusicItem.vue'
import { computed, getCurrentInstance, ref } from 'vue'
import { useMusicPlayStore } from '@/stores/musicPlay.js'

const { proxy } = getCurrentInstance()
const musicPlayStore = useMusicPlayStore()

const tabType = ref(0)
const activeFilterKey = ref('all')

const workFilters = [
  { key: 'all', label: '全部作品' },
  { key: 'creating', label: '生成中', musicStatus: 0 },
  { key: 'draft', label: '草稿', musicStatus: 1, publishStatus: 0 },
  { key: 'published', label: '已发布', publishStatus: 1 },
  { key: 'hidden', label: '已隐藏', publishStatus: 2 },
  { key: 'failed', label: '失败', musicStatus: 2 },
]

const dataSource = ref({
  pageNo: 0,
  pageTotal: 0,
  totalCount: 0,
  list: [],
})
const loading = ref(false)
const dataList = ref([])

const currentFilter = computed(() => {
  return workFilters.find((item) => item.key === activeFilterKey.value) || workFilters[0]
})

const pageTitle = computed(() => {
  return tabType.value === 0 ? '管理你的草稿、发布和 AI 封面' : '回看你点赞过的已发布作品'
})

const pageDesc = computed(() => {
  if (tabType.value === 1) {
    return '这里保留的是你公开点赞过的作品列表，方便回听和分享。'
  }
  return '围绕创作状态、提示词来源和封面生成记录，持续整理你的作品流。'
})

const loadedCount = computed(() => dataSource.value.list?.length || 0)
const aiPromptCount = computed(() => {
  return (dataSource.value.list || []).filter((item) => item.promptSourceType === 1).length
})
const aiCoverCount = computed(() => {
  return (dataSource.value.list || []).reduce((sum, item) => sum + (item.coverGenerateCount || 0), 0)
})

const resetPaging = () => {
  dataList.value = []
  dataSource.value = {
    pageNo: 0,
    pageTotal: 0,
    totalCount: 0,
    list: [],
  }
}

const buildParams = (pageNo) => {
  const params = {
    pageNo,
    queryLikeMusic: tabType.value === 1,
  }
  if (tabType.value === 0) {
    if (currentFilter.value.publishStatus !== undefined) {
      params.publishStatus = currentFilter.value.publishStatus
    }
    if (currentFilter.value.musicStatus !== undefined) {
      params.musicStatus = currentFilter.value.musicStatus
    }
  }
  return params
}

const loadMusicList = async (refresh) => {
  if (refresh) {
    resetPaging()
  }
  if (
    dataSource.value.pageNo > 0 &&
    dataSource.value.pageNo === dataSource.value.pageTotal &&
    !refresh
  ) {
    return
  }
  loading.value = true
  const pageNo = (dataSource.value.pageNo || 0) + 1
  const result = await proxy.Request({
    url: proxy.Api.loadMyMusic,
    showLoading: false,
    params: buildParams(pageNo),
  })
  loading.value = false
  if (!result) {
    return
  }
  if (result.data.pageNo === 1) {
    dataList.value = result.data.list
  } else {
    dataList.value = dataList.value.concat(result.data.list)
  }
  result.data.list = dataList.value
  dataSource.value = result.data
}

const changeTab = () => {
  activeFilterKey.value = 'all'
  loadMusicList(true)
}

const changeFilter = (filterKey) => {
  if (activeFilterKey.value === filterKey) {
    return
  }
  activeFilterKey.value = filterKey
  loadMusicList(true)
}

const playList = () => {
  musicPlayStore.savePlayList(dataSource.value.list || [])
}

const reloadMusic = () => {
  loadMusicList(true)
}
</script>

<style lang="scss" scoped>
.creator-page {
  height: calc(100vh - 125px);
  display: flex;
  flex-direction: column;
  gap: 16px;
  color: #fff;
}

.creator-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 20px 22px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top left, rgba(255, 123, 84, 0.24), transparent 34%),
    linear-gradient(135deg, rgba(18, 20, 42, 0.96), rgba(28, 34, 64, 0.92));
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hero-copy {
  flex: 1;
  min-width: 0;
}

.hero-kicker {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.56);
}

.hero-title {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1.3;
  font-weight: 700;
}

.hero-desc {
  margin-top: 12px;
  max-width: 620px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.72);
}

.hero-side {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.hero-stats {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
  max-width: 320px;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.86);
}

.hero-refresh {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.1);
  transition: background 0.2s ease;
}

.hero-refresh:hover {
  background: rgba(255, 255, 255, 0.18);
}

.toolbar-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-chip {
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-chip:hover,
.filter-chip-active {
  background: rgba(255, 123, 84, 0.2);
  color: #ffd0b5;
}

.data-list {
  flex: 1;
  min-height: 0;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

@media (max-width: 900px) {
  .creator-page {
    height: calc(100vh - 145px);
  }

  .creator-hero {
    flex-direction: column;
  }

  .hero-side,
  .hero-stats {
    width: 100%;
    justify-content: flex-start;
    max-width: none;
  }
}

@media (max-width: 600px) {
  .creator-page {
    height: calc(100vh - 176px);
    gap: 12px;
  }

  .creator-hero {
    padding: 16px;
    border-radius: 20px;
  }

  .hero-title {
    font-size: 24px;
  }

  .toolbar-panel {
    align-items: flex-start;
  }
}
</style>
