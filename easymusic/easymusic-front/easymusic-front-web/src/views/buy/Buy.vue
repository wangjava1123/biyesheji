<template>
  <div class="buy-page">
    <section class="buy-hero">
      <div class="hero-copy">
        <div class="hero-kicker">积分中心</div>
        <div class="hero-title">为创作、封面和发布流程准备可用积分</div>
        <div class="hero-desc">
          充值后可继续用于音乐生成、AI 封面和后续作品管理。这里保留套餐选择与积分记录查看，便于答辩演示完整消费链路。
        </div>
      </div>
      <div class="hero-side">
        <div class="hero-chip">当前积分 {{ userInfoStore.userInfo.integral || 0 }}</div>
        <div class="hero-chip">可选套餐 {{ productList.length }}</div>
        <div class="hero-link iconfont icon-narrow-right" @click="showIntegralRecord">
          查看积分记录
        </div>
      </div>
    </section>

    <section class="product-section">
      <div class="section-head">
        <div>
          <div class="section-title">推荐充值套餐</div>
          <div class="section-desc">优先展示积分与金额的对应关系，移动端自动改为单列卡片。</div>
        </div>
      </div>
      <div class="product-list">
        <ProductItem v-for="item in productList" :key="item.productId" :data="item" @pay="pay"></ProductItem>
      </div>
    </section>
  </div>
  <Pay ref="payRef" @showMyOrder="showIntegralRecord"></Pay>

  <IntegralRecord ref="integralRecordRef"></IntegralRecord>
</template>

<script setup>
import IntegralRecord from '@/views/my/IntegralRecord.vue'
import Pay from './Pay.vue'
import ProductItem from './ProductItem.vue'
import { ref, reactive, getCurrentInstance, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
import { useUserInfoStore } from '@/stores/userInfoStore'
const userInfoStore = useUserInfoStore()

const productList = ref([])

const loadProduct = async () => {
  let result = await proxy.Request({
    url: proxy.Api.loadProduct,
    params: {},
  })
  if (!result) {
    return
  }
  productList.value = result.data
}

const payRef = ref()
const pay = (data) => {
  payRef.value.pay({ ...data })
}

const integralRecordRef = ref()
const showIntegralRecord = () => {
  integralRecordRef.value.show()
}
onMounted(() => {
  loadProduct()
})
</script>

<style lang="scss" scoped>
.buy-page {
  color: #fff;
  padding: 18px 18px 24px;
}

.buy-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 22px 24px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top left, rgba(255, 183, 77, 0.26), transparent 34%),
    linear-gradient(135deg, rgba(17, 19, 40, 0.96), rgba(30, 25, 60, 0.92));
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.hero-kicker {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.56);
}

.hero-title {
  margin-top: 10px;
  font-size: 30px;
  line-height: 1.3;
  font-weight: 700;
}

.hero-desc {
  margin-top: 12px;
  max-width: 620px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.72);
}

.hero-side {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  justify-content: flex-end;
  gap: 10px;
  max-width: 340px;
}

.hero-chip,
.hero-link {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.88);
  font-size: 12px;
}

.hero-link {
  cursor: pointer;
  gap: 8px;
  background: rgba(255, 123, 84, 0.18);
  color: #ffd4bc;
}

.product-section {
  margin-top: 18px;
  padding: 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.section-head {
  padding: 0 4px 12px;
}

.section-title {
  font-size: 24px;
  line-height: 1.3;
  font-weight: 700;
}

.section-desc {
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.66);
  line-height: 1.7;
}

.product-list {
  margin-top: 4px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

@media (max-width: 960px) {
  .buy-page {
    padding: 14px 12px 20px;
  }

  .buy-hero {
    flex-direction: column;
    padding: 18px;
  }

  .hero-side {
    justify-content: flex-start;
    max-width: none;
  }

  .product-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .buy-hero,
  .product-section {
    border-radius: 20px;
  }

  .hero-title {
    font-size: 24px;
  }

  .section-title {
    font-size: 22px;
  }

  .product-list {
    grid-template-columns: 1fr;
  }
}
</style>
