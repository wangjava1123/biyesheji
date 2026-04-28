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
      <div class="intro-panel">
        <div class="intro-title">全部积分流水</div>
        <div class="intro-meta">
          <span class="intro-chip">充值</span>
          <span class="intro-chip">消费</span>
          <span class="intro-chip">退款</span>
          <span class="intro-order">时间倒序</span>
        </div>
      </div>

      <div class="summary-panel">
        <div class="summary-card">
          <div class="summary-label">当前余额</div>
          <div class="summary-value">{{ userInfoStore.userInfo.integral || 0 }}</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">已加载</div>
          <div class="summary-value">{{ dataSource.list?.length || 0 }}</div>
        </div>
        <div class="summary-card summary-card-expense">
          <div class="summary-label">本页支出</div>
          <div class="summary-value">-{{ expenseTotal }}</div>
        </div>
        <div class="summary-card summary-card-income">
          <div class="summary-label">本页入账</div>
          <div class="summary-value">+{{ incomeTotal }}</div>
        </div>
      </div>

      <DataLoadMoreList
        :dataSource="dataSource"
        :loading="loading"
        @loadData="loadRecord"
      >
        <template #default="{ data }">
          <div :class="['record-item', `record-item-${getRecordTone(data)}`]">
            <div class="record-head">
              <div class="record-main">
                <div class="record-title-row">
                  <div class="record-type">{{ data.recordTypeName }}</div>
                  <div class="record-time">{{ data.createTime }}</div>
                </div>
                <div class="record-detail">{{ getRecordDetailText(data) }}</div>
              </div>
              <div class="record-side">
                <div class="record-change">{{ formatChangeIntegral(data.changeIntegral) }}</div>
                <span :class="['record-flow', `record-flow-${getRecordTone(data)}`]">
                  {{ getRecordFlowLabel(data) }}
                </span>
              </div>
            </div>

            <div class="record-meta-row">
              <span class="record-badge">{{ getBusinessLabel(data) }}</span>
              <span class="record-badge" v-if="data.amount != null">
                金额 {{ formatAmount(data.amount) }}
              </span>
            </div>

            <div class="record-business" v-if="data.businessId">
              <span class="record-business-label">{{ getBusinessKey(data) }}</span>
              <code class="record-business-id">{{ data.businessId }}</code>
            </div>
          </div>
        </template>
      </DataLoadMoreList>
    </div>
  </Dialog>
</template>

<script setup>
import { ref, getCurrentInstance, computed } from "vue";
import { useUserInfoStore } from "@/stores/userInfoStore";

const { proxy } = getCurrentInstance();
const userInfoStore = useUserInfoStore();

const REFUND_RECORD_TYPES = [0, 6, 8];
const DETAIL_TEXT_MAP = {
  0: "创作失败，积分已自动退回",
  1: "创作提交成功后扣分",
  2: "充值完成后自动入账",
  3: "管理员手动补发积分",
  4: "管理员手动扣减积分",
  5: "智能扩写成功后扣分",
  6: "扩写失败，积分已自动退回",
  7: "AI 封面生成成功后扣分",
  8: "封面生成失败，积分已自动退回",
};
const BUSINESS_KEY_MAP = {
  0: "创作单号",
  1: "创作单号",
  2: "订单号",
  5: "扩写记录号",
  6: "扩写记录号",
  7: "封面记录号",
  8: "封面记录号",
};
const BUSINESS_LABEL_MAP = {
  0: "创作退款",
  1: "创作消费",
  2: "充值",
  3: "管理员加分",
  4: "管理员扣分",
  5: "扩写消费",
  6: "扩写退款",
  7: "AI 封面消费",
  8: "AI 封面退款",
};

const dialogConfig = ref({
  show: false,
  title: "积分记录",
});
const dialogWidth = computed(() => {
  return window.innerWidth > 960 ? "820px" : "92%";
});

const dataSource = ref({});
const loading = ref(false);
const dataList = ref([]);

const expenseTotal = computed(() => {
  return (dataSource.value.list || []).reduce((total, item) => {
    const changeIntegral = Number(item.changeIntegral || 0);
    return changeIntegral < 0 ? total + Math.abs(changeIntegral) : total;
  }, 0);
});

const incomeTotal = computed(() => {
  return (dataSource.value.list || []).reduce((total, item) => {
    const changeIntegral = Number(item.changeIntegral || 0);
    return changeIntegral > 0 ? total + changeIntegral : total;
  }, 0);
});

const isRefundRecord = (data) => {
  return REFUND_RECORD_TYPES.includes(Number(data.recordType));
};

const getRecordTone = (data) => {
  if (isRefundRecord(data)) {
    return "refund";
  }
  return Number(data.changeIntegral || 0) >= 0 ? "income" : "expense";
};

const getRecordFlowLabel = (data) => {
  if (isRefundRecord(data)) {
    return "退款";
  }
  return Number(data.changeIntegral || 0) >= 0 ? "入账" : "支出";
};

const getRecordDetailText = (data) => {
  return DETAIL_TEXT_MAP[data.recordType] || "积分发生变动后自动记录";
};

const getBusinessLabel = (data) => {
  return BUSINESS_LABEL_MAP[data.recordType] || "积分流水";
};

const getBusinessKey = (data) => {
  return BUSINESS_KEY_MAP[data.recordType] || "业务号";
};

const formatChangeIntegral = (integral) => {
  const value = Number(integral || 0);
  return `${value > 0 ? "+" : ""}${value}`;
};

const formatAmount = (amount) => {
  const value = Number(amount || 0);
  return `￥${value.toFixed(2)}`;
};

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
  userInfoStore.updateLastReloadTime();
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

  .intro-panel {
    padding: 14px 16px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
  }

  .intro-title {
    font-size: 18px;
    line-height: 1.2;
    font-weight: 700;
    color: #fff;
  }

  .intro-meta {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .intro-chip,
  .intro-order {
    display: inline-flex;
    align-items: center;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 12px;
    line-height: 1.4;
    font-weight: 600;
  }

  .intro-chip {
    background: rgba(255, 255, 255, 0.09);
    color: rgba(255, 255, 255, 0.9);
  }

  .intro-order {
    background: rgba(245, 195, 82, 0.14);
    color: #ffdd82;
  }

  .summary-panel {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 10px;
    margin: 14px 0;
  }

  .summary-card {
    padding: 14px 16px;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.07);
  }

  .summary-card-expense {
    border-color: rgba(255, 118, 118, 0.18);
  }

  .summary-card-income {
    border-color: rgba(99, 201, 154, 0.18);
  }

  .summary-label {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.54);
  }

  .summary-value {
    margin-top: 8px;
    font-size: 24px;
    line-height: 1.1;
    font-weight: 700;
    color: #fff;
  }

  .record-item {
    padding: 16px;
    color: #fff;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.04);
    transition: border-color 0.2s ease, background 0.2s ease;
  }

  .record-item-income {
    border-color: rgba(245, 195, 82, 0.2);
  }

  .record-item-expense {
    border-color: rgba(243, 110, 110, 0.18);
  }

  .record-item-refund {
    border-color: rgba(98, 196, 155, 0.22);
  }

  .record-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 14px;
  }

  .record-main {
    min-width: 0;
    flex: 1;
  }

  .record-title-row {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 12px;
  }

  .record-type {
    font-size: 16px;
    line-height: 1.35;
    font-weight: 700;
  }

  .record-time {
    flex-shrink: 0;
    color: rgba(255, 255, 255, 0.48);
    font-size: 12px;
    white-space: nowrap;
  }

  .record-detail {
    margin-top: 8px;
    color: rgba(255, 255, 255, 0.7);
    font-size: 13px;
    line-height: 1.6;
  }

  .record-side {
    min-width: 84px;
    text-align: right;
    flex-shrink: 0;
  }

  .record-change {
    font-size: 24px;
    line-height: 1;
    font-weight: 700;
  }

  .record-flow {
    display: inline-flex;
    align-items: center;
    margin-top: 10px;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 600;
  }

  .record-flow-expense {
    background: rgba(246, 100, 100, 0.14);
    color: #ffabab;
  }

  .record-flow-income {
    background: rgba(244, 198, 74, 0.15);
    color: #ffd873;
  }

  .record-flow-refund {
    background: rgba(84, 196, 146, 0.16);
    color: #83f0bf;
  }

  .record-meta-row {
    margin-top: 12px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .record-badge {
    display: inline-flex;
    align-items: center;
    padding: 4px 10px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.06);
    color: rgba(255, 255, 255, 0.76);
    font-size: 12px;
    line-height: 1.4;
  }

  .record-business {
    margin-top: 12px;
    display: flex;
    align-items: center;
    gap: 10px;
    min-width: 0;
  }

  .record-business-label {
    color: rgba(255, 255, 255, 0.48);
    font-size: 12px;
    flex-shrink: 0;
  }

  .record-business-id {
    display: inline-block;
    min-width: 0;
    padding: 4px 8px;
    border-radius: 10px;
    background: rgba(0, 0, 0, 0.2);
    color: rgba(255, 255, 255, 0.86);
    font-size: 12px;
    word-break: break-all;
    font-family: Consolas, Monaco, monospace;
  }
}

@media (max-width: 640px) {
  .data-list {
    padding: 10px;

    .summary-panel {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .record-head {
      flex-direction: column;
    }

    .record-title-row {
      flex-direction: column;
      align-items: flex-start;
      gap: 6px;
    }

    .record-side {
      text-align: left;
      min-width: 0;
    }

    .record-business {
      flex-direction: column;
      align-items: flex-start;
      gap: 6px;
    }
  }
}
</style>
