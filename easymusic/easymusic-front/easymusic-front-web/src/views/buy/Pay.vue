<template>
  <Dialog :show="dialogConfig.show" :title="dialogConfig.title" :buttons="dialogConfig.buttons" :width="dialogWidth"
    :showCancel="false" @close="closePay">
    <div class="pay-panel">
      <div class="step-panel">
        <div class="step-copy">
          <div class="step-title">确认订单并完成积分充值</div>
          <div class="step-desc">保留扫码支付和支付码两种路径，小屏下帮助说明改为点击查看。</div>
        </div>
        <el-steps :space="stepSpace" :active="currentStep" finish-status="success" align-center>
          <el-step title="确认订单" />
          <el-step title="扫码支付" />
          <el-step title="购买成功" />
        </el-steps>
      </div>

      <template v-if="currentStep == 1">
        <div class="product-info-panel">
          <div class="title-info">订单详情</div>
          <div class="product-info">
            <div class="product-cover">
              <Cover :cover="productInfo.cover" :width="100"></Cover>
            </div>
            <div class="product-name-panel">
              <div class="product-name">{{ productInfo.productName }}</div>
              <div class="sku-name">充值积分:{{ productInfo.integral }}</div>
            </div>
            <div class="price">
              ￥<span>{{ proxy.Utils.convert2Amount(productInfo.price) }}</span>
            </div>
          </div>
        </div>
        <el-form class="pay-form" :rules="rules" :model="formData" ref="formDataRef" label-width="95px" @submit.prevent>
          <el-form-item label="支付方式：" prop="payType">
            <div class="pay-method">
              <el-radio-group v-model="formData.payType" @change="payTypeChange">
                <el-radio :value="1">微信支付(推荐)</el-radio>
                <el-radio :value="0">支付码支付</el-radio>
              </el-radio-group>
              <el-popover placement="right" :width="220" :trigger="helpPopoverTrigger">
                <template #reference>
                  <div class="no-pay-tips">没有微信支付?</div>
                </template>
                <template #default>
                  <div class="show-qrcode">
                    <img :src="proxy.Utils.getLocalResource('img/qrcode.png')" :style="{ width: '200px' }" />
                    <div :style="{ 'text-align': 'left' }">
                      <div class="info">1、微信扫码联系管理员</div>
                      <div class="info">2、备注商品信息</div>
                      <div class="info">3、管理员会给你解决</div>
                    </div>
                  </div>
                </template>
              </el-popover>
            </div>
          </el-form-item>
          <template v-if="formData.payType == 0">
            <el-form-item label="支付码：" prop="payCode">
              <div class="form-item">
                <div class="input">
                  <el-input size="large" placeholder="输入支付码" v-model.trim="formData.payCode" :maxLength="8"></el-input>
                </div>
                <div class="input-tips">输入付款码</div>
              </div>
            </el-form-item>
            <el-form-item label="验证码：" prop="checkCode">
              <div class="form-item">
                <div class="input">
                  <el-input size="large" placeholder="输入图片验证码" v-model.trim="formData.checkCode"
                    @keyup.enter="payCodePay"></el-input>
                </div>
                <img :src="checkCodeUrl" @click="changeCheckCode" class="check-code" />
              </div>
            </el-form-item>
          </template>
        </el-form>
        <div class="pay-btn-panel">
          <div class="pay-btn" @click="getPayQrcode" v-if="formData.payType == 1">
            提交订单
          </div>
          <div class="pay-btn" @click="payCodePay" v-if="formData.payType == 0">
            立即购买
          </div>
        </div>
      </template>

      <!--获取支付信息-->
      <div class="step2" v-if="currentStep == 2">
        <div class="amount-panel">
          应付金额：￥<span class="amount">{{
            proxy.Utils.convert2Amount(productInfo.price)
          }}</span>
        </div>
        <div class="qrcode">
          <QrcodeVue :value="payInfo.payUrl" :size="180"></QrcodeVue>
          <div class="pay-remind">
            <div>
              支付完成后，页面在5秒钟后会跳转，如果未跳转，请点击下方
              “我已经支付”。
            </div>
            <div></div>
          </div>
          <div class="pay-info">
            <div class="iconfont icon-wxpay">
              <span class="text">微信扫码支付</span>
            </div>
            <div class="have-pay" @click="havePay">我已经支付？</div>
          </div>
        </div>
      </div>
      <div class="step3" v-if="currentStep == 3">
        <div class="iconfont icon-ok">恭喜你，支付成功</div>
        <div class="go-order-panel">
          <div class="go-btn" @click="showMyOrder">查看订单</div>
        </div>
      </div>
    </div>
  </Dialog>
</template>

<script setup>
import QrcodeVue from 'qrcode.vue'
import { ref, reactive, getCurrentInstance, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()

import { useUserInfoStore } from '@/stores/userInfoStore'
const userInfoStore = useUserInfoStore()

const dialogConfig = ref({
  show: false,
  title: '购买',
})
const dialogWidth = computed(() => {
  return window.innerWidth > 680 ? '535px' : '92%'
})
const stepSpace = computed(() => {
  return window.innerWidth > 680 ? 160 : 90
})
const helpPopoverTrigger = computed(() => {
  return window.innerWidth > 720 ? 'hover' : 'click'
})
const currentStep = ref(1)

const productInfo = ref({})
const pay = async (data) => {
  dialogConfig.value.show = true
  currentStep.value = 1
  productInfo.value = data
  await nextTick()
  formDataRef.value.resetFields()
  formData.value = {
    payType: 1,
  }
}

const formData = ref({})
const formDataRef = ref()
const rules = {
  payType: [{ required: true, message: '请选择支付方式' }],
  payCode: [{ required: true, message: '请输入支付码' }],
  checkCode: [{ required: true, message: '请输入图片验证码' }],
}

const checkCodeUrl = ref(null)
const checkCodeKey = ref()
const changeCheckCode = async () => {
  let result = await proxy.Request({
    url: proxy.Api.checkCode,
    showLoading: false,
  })
  if (!result) {
    return
  }
  checkCodeUrl.value = result.data.checkCode
  checkCodeKey.value = result.data.checkCodeKey
}

const payTypeChange = (e) => {
  if (e === 1) {
    return
  }
  changeCheckCode()
}

//获取支付二维码
const payInfo = ref({})
const getPayQrcode = async () => {
  let result = await proxy.Request({
    url: proxy.Api.getPayInfo,
    params: {
      productId: productInfo.value.productId,
      payType: formData.value.payType,
    },
  })
  if (!result) {
    return
  }
  currentStep.value = 2
  payInfo.value = result.data
  startTimer()
}

//校验支付结果
const checkPayInfo = async () => {
  const orderId = payInfo.value.orderId
  if (!orderId) {
    return
  }
  let result = await proxy.Request({
    showLoading: false,
    url: proxy.Api.checkPayOrder,
    params: {
      orderId: orderId,
    },
  })
  if (!result) {
    return
  }
  if (result.data != null && result.data) {
    cleanTimer()
    currentStep.value = 3
    //重新加载积分
    userInfoStore.updateLastReloadTime()
  }
}

//已经支付
const havePay = async () => {
  const orderId = payInfo.value.orderId
  if (!orderId) {
    proxy.Message.error('未获取到支付信息')
    return
  }
  let result = await proxy.Request({
    url: proxy.Api.havePay,
    params: {
      orderId: orderId,
    },
  })
  if (!result) {
    return
  }
  if (result.data != null && result.data) {
    cleanTimer()
    currentStep.value = 3
    //重新加载积分
    userInfoStore.updateLastReloadTime()
  } else {
    proxy.Message.error('未查询到已支付订单,请等30秒后再试')
  }
}

let timmer = ref(null)
const startTimer = () => {
  timmer.value = setInterval(() => {
    checkPayInfo()
  }, 5000)
}

const cleanTimer = () => {
  if (timmer.value !== null) {
    clearInterval(timmer.value)
    timmer.value = null
  }
}
const closePay = () => {
  dialogConfig.value.show = false
  cleanTimer()
}

//支付码支付
const payCodePay = () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    let result = await proxy.Request({
      url: proxy.Api.buyByPayCode,
      params: {
        checkCodeKey: checkCodeKey.value,
        checkCode: formData.value.checkCode,
        payCode: formData.value.payCode,
        productId: productInfo.value.productId,
      },
      errorCallback: () => {
        changeCheckCode()
      },
    })
    if (!result) {
      return
    }
    currentStep.value = 3
    //重新加载积分
    userInfoStore.updateLastReloadTime()
  })
}

const emits = defineEmits(['showMyOrder'])
const showMyOrder = () => {
  dialogConfig.value.show = false
  emits('showMyOrder')
}

defineExpose({
  pay,
})
</script>

<style lang="scss" scoped>
.pay-panel {
  color: #fff;
  .step-panel {
    margin-bottom: 12px;
    .step-copy {
      margin-bottom: 18px;
    }
    .step-title {
      font-size: 20px;
      font-weight: 700;
      line-height: 1.35;
      text-align: center;
    }
    .step-desc {
      margin-top: 8px;
      text-align: center;
      color: rgba(255, 255, 255, 0.64);
      line-height: 1.65;
    }
    :deep(.el-step__title.is-process) {
      color: #fff;
    }
    :deep(.el-step__title) {
      font-size: 13px;
    }
  }
  .product-info-panel {
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 18px;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.04);
    .title-info {
      padding: 12px 14px;
      background: rgba(255, 123, 84, 0.18);
      color: #ffd4bc;
      font-weight: 600;
    }
    .product-info {
      display: flex;
      align-items: center;
      gap: 14px;
      padding: 16px;
      .product-cover {
        border-radius: 12px;
        overflow: hidden;
      }
      .product-name-panel {
        flex: 1;
        width: 0;
        .product-name {
          font-size: 18px;
          line-height: 1.35;
          font-weight: 700;
        }
        .sku-name {
          margin-top: 8px;
          font-size: 13px;
          color: rgba(255, 255, 255, 0.68);
        }
      }
      .price {
        color: #ffd36d;
        font-weight: 700;
        span {
          font-size: 28px;
        }
      }
    }
  }
  .pay-form {
    margin-top: 16px;
    :deep(.el-form-item) {
      align-items: center;
      .el-form-item__label,
      .el-radio__label {
        color: #fff;
      }
      .el-input__wrapper {
        background-color: #fff0 !important;
        box-shadow: none !important;
        box-shadow: initial !important;
        border: 1px solid rgba(255, 255, 255, 0.3) !important;
        border-radius: 8px;
        .el-input__inner {
          color: #fff;
        }
      }
    }
    .form-item {
      display: flex;
      align-items: center;
      width: 100%;
      .input {
        flex: 1;
        margin-right: 10px;
      }
      .input-tips {
        color: rgba(255, 255, 255, 0.56);
        font-size: 12px;
      }
      .check-code {
        cursor: pointer;
        border-radius: 10px;
      }
    }
    .pay-method {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      flex-wrap: wrap;
      .no-pay-tips {
        cursor: pointer;
        font-size: 13px;
        color: #ffbf98;
      }
    }
  }
  .pay-btn-panel {
    margin-top: 18px;
    display: flex;
    justify-content: center;
    .pay-btn {
      min-width: 180px;
      text-align: center;
      padding: 12px 18px;
      background: var(--btnBg);
      box-shadow: var(--btnShadow);
      border-radius: 999px;
      cursor: pointer;
      font-weight: 600;
      &:hover {
        opacity: 0.92;
      }
    }
  }

  .step2 {
    text-align: center;
    .amount-panel {
      color: #ffd36d;
      font-size: 18px;
      margin-top: 18px;
      .amount {
        font-size: 30px;
        font-weight: 700;
      }
    }
    .qrcode {
      margin: 20px auto 10px;
      padding: 18px 16px;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.04);
      border: 1px solid rgba(255, 255, 255, 0.06);
      .pay-remind {
        margin-top: 12px;
        text-align: center;
        color: rgba(255, 255, 255, 0.66);
        line-height: 1.7;
      }
      .pay-info {
        margin-top: 14px;
        display: flex;
        gap: 14px;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
      }
      .icon-wxpay {
        display: flex;
        align-items: center;
        justify-content: center;
        color: #22ac38;
        font-size: 20px;
        .text {
          color: #aab4bf;
          margin-left: 5px;
          font-size: 14px;
        }
      }
      .have-pay {
        cursor: pointer;
        color: #fff;
        padding: 8px 16px;
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.08);
      }
    }
  }

  .step3 {
    .icon-ok {
      text-align: center;
      color: #22ac38;
      font-size: 20px;
      margin: 56px 0px 24px;
    }
    .icon-ok::before {
      margin-right: 10px;
    }
    .go-order-panel {
      margin: 20px 0px 20px 0px;
      text-align: center;
      .go-btn {
        background: linear-gradient(90deg, #22ac38, #39c95d);
        color: #fff;
        line-height: 48px;
        text-align: center;
        margin: 0px auto;
        display: inline-block;
        padding: 0px 60px;
        cursor: pointer;
        border-radius: 999px;
        &:hover {
          opacity: 0.8;
        }
      }
    }
  }
}

@media (max-width: 640px) {
  .pay-panel {
    .step-panel {
      .step-title {
        font-size: 18px;
      }
    }

    .product-info-panel {
      .product-info {
        flex-wrap: wrap;
        justify-content: center;
        text-align: center;
      }
    }

    .pay-form {
      .form-item {
        flex-wrap: wrap;
        gap: 10px;

        .input {
          width: 100%;
          margin-right: 0;
        }
      }
    }

    .pay-btn-panel {
      .pay-btn {
        width: 100%;
      }
    }

    .step2 {
      .qrcode {
        padding: 16px 12px;
      }
    }

    .step3 {
      .go-order-panel {
        .go-btn {
          width: 100%;
          padding: 0;
        }
      }
    }
  }
}
</style>
