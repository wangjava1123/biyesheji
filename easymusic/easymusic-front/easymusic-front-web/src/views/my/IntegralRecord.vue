<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    :width="dialogWidth"
    :showCancel="false"
    @close="dialogConfig.show = false"
    :padding="2"
  >
    <div class="data-list" v-if="dialogConfig.show">
      <div class="summary-panel">
        <div class="summary-card">
          <div class="summary-label">当前积分</div>
          <div class="summary-value">{{ userInfoStore.userInfo.integral || 0 }}</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">已加载记录</div>
          <div class="summary-value">{{ dataSource.list?.length || 0 }}</div>
        </div>
      </div>
      <DataLoadMoreList
        :dataSource="dataSource"
        :loading="loading"
        @loadData="loadRecord"
        layoutType="grid"
        :gridCount="gridCount"
      >
        <template #default="{ data, index }">
          <div :class="['record-item', `record-item-${data.recordType}`]">
            <div class="record-type">{{ data.recordTypeName }}</div>
            <div class="record-row">
              <span class="record-label">积分</span>
              <span class="record-value">{{ data.changeIntegral }}</span>
            </div>
            <div class="record-row" v-if="data.recordType == 2">
              <span class="record-label">金额</span>
              <span class="record-value">{{ data.amount }}</span>
            </div>
            <div class="record-meta">时间：{{ data.createTime }}</div>
            <div class="record-meta" v-if="data.recordType == 2">订单号：{{ data.businessId }}</div>
          </div>
        </template>
      </DataLoadMoreList>
    </div>
  </Dialog>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
import { useUserInfoStore } from "@/stores/userInfoStore";
const userInfoStore = useUserInfoStore();

const dialogConfig = ref({
  show: false,
  title: "积分记录",
});
const dialogWidth = computed(() => {
  return window.innerWidth > 960 ? "800px" : "92%"
});
const gridCount = computed(() => {
  return window.innerWidth > 760 ? 2 : 1
});

const dataSource = ref({});
const loading = ref(false);
const dataList = ref([]);
const loadRecord = async () => {
  if (
    Object.keys(dataSource.value).length > 0 &&
    dataSource.value.pageNo == dataSource.value.pageTotal
  ) {
    return;
  }
  loading.value = true;
  let pageNo = dataSource.value.pageNo || 0;
  pageNo++;
  let result = await proxy.Request({
    url: proxy.Api.integralRecords,
    showLoading: false,
    params: {
      pageNo,
    },
  });
  loading.value = false;
  if (!result) {
    return;
  }
  if (result.data.pageNo == 1) {
    dataList.value = result.data.list;
  } else {
    dataList.value = dataList.value.concat(result.data.list);
  }
  result.data.list = dataList.value;
  dataSource.value = result.data;
};

const show = () => {
  if (!userInfoStore.checkLogin()) {
    return;
  }
  dialogConfig.value.show = true;
  dataSource.value = {
    pageNo: 0,
    pageTotal: 0,
    totalCount: 0,
    list: [],
  };
  dataList.value = [];
};

defineExpose({
  show,
});
</script>

<style lang="scss" scoped>
.data-list {
  height: min(62dvh, 560px);
  overflow: auto;
  padding: 14px;
  .summary-panel {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
    margin-bottom: 14px;
  }
  .summary-card {
    padding: 14px 16px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.06);
  }
  .summary-label {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.56);
  }
  .summary-value {
    margin-top: 8px;
    font-size: 24px;
    line-height: 1.2;
    font-weight: 700;
    color: #fff;
  }
  .record-item {
    padding: 14px;
    color: #fff;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.04);
    .record-type {
      font-size: 16px;
      line-height: 1.35;
      font-weight: 700;
    }
    .record-row {
      margin-top: 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
    }
    .record-label {
      color: rgba(255, 255, 255, 0.56);
      font-size: 12px;
    }
    .record-value {
      font-weight: 700;
      color: #fff;
    }
    .record-meta {
      margin-top: 10px;
      color: rgba(255, 255, 255, 0.66);
      line-height: 1.6;
    }
  }
  .record-item-2 {
    background:
      linear-gradient(180deg, rgba(52, 140, 84, 0.18), rgba(255, 255, 255, 0.04));
  }
}

@media (max-width: 640px) {
  .data-list {
    padding: 10px;
    .summary-panel {
      grid-template-columns: 1fr;
    }
  }
}
</style>
