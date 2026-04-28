<template>
  <div class="product-item">
    <div class="cover">
      <Cover :cover="data.cover" borderRadius="0px"></Cover>
    </div>
    <div class="product-info">
      <div class="product-badge">积分套餐</div>
      <div class="product-name">{{ data.productName }}</div>
      <div class="price-row">
        <div class="price">¥ {{ proxy.Utils.convert2Amount(data.price) }}</div>
        <div class="integral">{{ data.integral }} 积分</div>
      </div>
      <div class="product-description">{{ data.productDescription }}</div>
    </div>
    <div class="buy-footer">
      <div class="unit-tip">适合补充创作和 AI 封面消耗</div>
      <div class="buy-btn" @click="buy">立即购买</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
import { useUserInfoStore } from '@/stores/userInfoStore'
const userInfoStore = useUserInfoStore()

const props = defineProps({
  data: {
    type: Object,
    default: {},
  },
})

const emits = defineEmits(['pay'])
const buy = () => {
  if (!userInfoStore.checkLogin()) {
    return
  }
  emits('pay', props.data)
}
</script>

<style lang="scss" scoped>
.product-item {
  min-width: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 22px;
  overflow: hidden;
  color: #fff;
  background:
    linear-gradient(180deg, rgba(39, 32, 83, 0.96), rgba(18, 18, 32, 0.94));
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
  .cover {
    aspect-ratio: 16 / 10;
    overflow: hidden;
  }
  .product-info {
    padding: 18px 18px 10px;
    flex: 1;
    .product-badge {
      display: inline-flex;
      align-items: center;
      min-height: 28px;
      padding: 0 10px;
      border-radius: 999px;
      background: rgba(255, 255, 255, 0.1);
      color: rgba(255, 255, 255, 0.74);
      font-size: 12px;
    }
    .product-name {
      margin-top: 14px;
      font-size: 22px;
      background: linear-gradient(104deg, #f6b1ff, #ecd3ff 53%, #ea8cff);
      background-clip: text;
      -webkit-text-fill-color: transparent;
      font-weight: bold;
      line-height: 1.3;
    }
    .price-row {
      margin-top: 18px;
      display: flex;
      align-items: flex-end;
      justify-content: space-between;
      gap: 12px;
      flex-wrap: wrap;
    }
    .price {
      font-size: 28px;
      line-height: 1;
      font-weight: 700;
      color: #ffd36d;
    }
    .integral {
      line-height: 1.4;
      font-weight: 700;
      background: linear-gradient(105deg, #4fdeff, #e675ff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .product-description {
      margin-top: 14px;
      font-size: 14px;
      line-height: 1.75;
      color: rgba(255, 255, 255, 0.68);
    }
  }

  .buy-footer {
    padding: 0 18px 18px;
  }

  .unit-tip {
    color: rgba(255, 255, 255, 0.5);
    font-size: 12px;
    line-height: 1.6;
  }

  .buy-btn {
    margin-top: 14px;
    text-align: center;
    padding: 12px 16px;
    border-radius: 999px;
    background: var(--btnBg);
    box-shadow: var(--btnShadow);
    cursor: pointer;
    font-weight: 600;
  }
  &:hover {
    transform: translateY(-3px);
    border-color: rgba(255, 176, 140, 0.5);
    box-shadow: 0 14px 30px rgba(0, 0, 0, 0.24);
  }
}

@media (max-width: 640px) {
  .product-item {
    border-radius: 20px;
  }

  .product-info {
    .product-name {
      font-size: 20px;
    }

    .price {
      font-size: 24px;
    }
  }
}
</style>
