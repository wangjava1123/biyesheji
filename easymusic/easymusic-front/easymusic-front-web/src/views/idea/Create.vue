<template>
  <div class="create-tab">
    <el-tabs v-model="formData.musicType">
      <el-tab-pane label="歌曲" :name="0"></el-tab-pane>
      <el-tab-pane label="纯音乐" :name="1"></el-tab-pane>
    </el-tabs>
  </div>
  <div class="create-form">
    <el-form :model="formData" :rules="rules" ref="formDataRef" label-width="80px" @submit.prevent>
      <Switch :data="[
          { label: '简单模式', value: 0 },
          { label: '高级模式', value: 1 },
        ]" v-model="formData.modeType"></Switch>
      <div class="assist-panel">
        <div class="part-title">一句话描述需求</div>
        <el-input
          clearable
          placeholder="例如：做一首深夜城市感的女声流行歌，前半段克制，副歌要有爆发力"
          v-model="assistForm.rawPrompt"
          type="textarea"
          :rows="4"
          resize="none"
          :maxlength="300"
          show-word-limit
        >
        </el-input>
        <div class="assist-actions">
          <div class="assist-btn" @click="generatePromptAssist">
            <el-icon class="is-loading" v-if="assistLoading">
              <Loading style="width: 1em; height: 1em" />
            </el-icon>
            <span v-else>智能扩写</span>
          </div>
          <div class="assist-tip">扩写后会自动回填音乐提示词，并记录增强来源。</div>
        </div>
        <div class="assist-result" v-if="promptAssistResult">
          <div class="assist-result-head">
            <div class="assist-result-title">结构化结果</div>
            <div class="assist-result-meta">{{ assistSourceText }}</div>
          </div>
          <div class="assist-result-grid">
            <div class="assist-card assist-card-wide">
              <div class="assist-card-title">音乐提示词</div>
              <div class="assist-card-content">{{ promptAssistResult.musicPrompt }}</div>
            </div>
            <div class="assist-card assist-card-wide">
              <div class="assist-card-title">封面提示词</div>
              <div class="assist-card-content">{{ promptAssistResult.imagePrompt }}</div>
            </div>
            <div class="assist-card">
              <div class="assist-card-title">推荐标题</div>
              <div class="assist-chip-list">
                <span class="assist-chip" v-for="title in promptAssistResult.titleSuggestions" :key="title">{{ title }}</span>
              </div>
            </div>
            <div class="assist-card">
              <div class="assist-card-title">推荐标签</div>
              <div class="assist-chip-list">
                <span class="assist-chip" v-for="tag in promptAssistResult.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!--input输入-->
      <template v-if="formData.modeType == 0">
        <div class="input-panel">
          <el-input clearable placeholder="请输入你的想法" v-model="formData.prompt" type="textarea" :rows="8" resize="none"
            :maxlength="500" show-word-limit>
          </el-input>
          <div class="change-btn" @click="getPrompt">
            <div class="iconfont icon-magic">变变变</div>
          </div>
        </div>
      </template>
      <template v-else>
        <div :class="[
            'advanced-panel',
            formData.musicType === 1 ? 'advanced-panel-line' : '',
          ]">
          <div class="lyric-panel">
            <div class="input-panel">
              <el-input clearable placeholder="请输入提示，或者标题" v-model="formData.prompt" type="textarea" :rows="5"
                resize="none" :maxlength="500" show-word-limit>
              </el-input>
              <div class="change-btn" @click="getPrompt">
                <div class="iconfont icon-magic">变变变</div>
              </div>
            </div>
            <div class="input-panel lyric-input" v-if="formData.musicType === 0">
              <el-input clearable placeholder="请输入歌词" v-model="formData.lyrics" type="textarea" resize="none"
                :maxlength="3000" show-word-limit>
              </el-input>
            </div>
          </div>
          <div class="setting-panel">
            <div class="part-title">曲风</div>
            <TabSelect :data="sysSetting[SYS_SETTING_KEY.music_grenre.key]" v-model="formData.musicGener"></TabSelect>
            <div class="part-title">情绪</div>
            <TabSelect :data="sysSetting[SYS_SETTING_KEY.music_emotion.key]" v-model="formData.musicEmotion">
            </TabSelect>
            <template v-if="formData.musicType === 0">
              <div class="part-title">人声</div>
              <TabSelect :multiple="false" :data="sysSetting[SYS_SETTING_KEY.music_sex.key]"
                v-model="formData.musicSex"></TabSelect>
            </template>
          </div>
        </div>
      </template>
      <div class="part-title">选择模型</div>
      <!-- 下拉框 -->
      <div class="model-select">
        <el-radio-group v-model="formData.model" class="model-select" size="large">
          <el-radio-button v-for="model in music_models" :value="model.dictCode" :label="model.dictCode">
          </el-radio-button>
        </el-radio-group>
        <div class="model-tips">{{ currentModel?.dictDesc }}</div>
      </div>
      <div class="submit-btn" @click="createMusic">
        <el-icon class="is-loading" v-if="creating">
          <Loading style="width: 1em; height: 1em" />
        </el-icon>
        <span v-else>创作音乐</span>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import Switch from '@/component/common/Switch.vue'
import TabSelect from '@/component/common/TabSelect.vue'
import {
  ref,
  reactive,
  getCurrentInstance,
  nextTick,
  onMounted,
  computed,
  onUnmounted,
} from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
import { useUserInfoStore } from '@/stores/userInfoStore'
const userInfoStore = useUserInfoStore()

import { mitter } from '@/eventbus/eventBus.js'

const SYS_SETTING_KEY = {
  //曲风
  music_grenre: {
    key: 'music_grenre',
    valueKey: 'dictCode',
  },
  //情绪
  music_emotion: {
    key: 'music_emotion',
    valueKey: 'dictCode',
  },
  //人声
  music_sex: {
    key: 'music_sex',
    valueKey: 'dictCode',
  },
  //音乐提示词
  music_prompt: {
    key: 'music_prompt',
    valueKey: 'dictCode',
  },
  //纯音乐提示词
  music_prompt_pure: {
    key: 'music_prompt_pure',
    valueKey: 'dictCode',
  },
  //音乐模型
  music_model: {
    key: 'music_model',
  },
  //纯音乐模型
  music_model_pure: {
    key: 'music_model_pure',
  },
}

const assistForm = ref({
  rawPrompt: '',
})
const promptAssistResult = ref(null)
const assistLoading = ref(false)

const resetPromptSource = () => {
  formData.value.originPrompt = ''
  formData.value.promptRecordId = ''
  formData.value.promptSourceType = 0
}

const getPrompt = () => {
  let prompts = []
  if (formData.value.musicType == 0) {
    prompts = sysSetting.value[SYS_SETTING_KEY.music_prompt.key]
  } else if (formData.value.musicType == 1) {
    prompts = sysSetting.value[SYS_SETTING_KEY.music_prompt_pure.key]
  }
  if (prompts == null) {
    return
  }
  formData.value.prompt = prompts[Math.floor(Math.random() * prompts.length)]
  resetPromptSource()
}

const music_models = computed(() => {
  const models =
    formData.value.musicType == 0
      ? sysSetting.value[SYS_SETTING_KEY.music_model.key]
      : sysSetting.value[SYS_SETTING_KEY.music_model_pure.key]
  if (models && models.length > 0 && !formData.value.model) {
    formData.value.model = models[0].dictCode
  }
  return models
})

const sysSetting = ref({})
const loadSysSetting = async () => {
  let result = await proxy.Request({
    url: proxy.Api.loadSysDict,
  })
  if (!result) {
    return
  }
  for (let key in result.data) {
    if (SYS_SETTING_KEY[key].valueKey) {
      result.data[key] = result.data[key].map((item) => {
        return item[SYS_SETTING_KEY[key].valueKey]
      })
    }
  }
  sysSetting.value = result.data
  if (route.params.creationId) {
    return
  }
  getPrompt()
}

const currentModel = computed(() => {
  if (music_models.value) {
    return music_models.value.find((item) => {
      return item.dictCode == formData.value.model
    })
  } else {
    return {}
  }
})

const assistSourceText = computed(() => {
  if (!promptAssistResult.value) {
    return ''
  }
  return promptAssistResult.value.responseSource === 'remote'
    ? `来源：外部 AI · ${promptAssistResult.value.model || ''}`
    : `来源：本地回退 · ${promptAssistResult.value.model || ''}`
})

const formData = ref({
  modeType: 0,
  musicType: 0,
  promptSourceType: 0,
})
const formDataRef = ref()
const rules = {
  title: [{ required: true, message: '请输入内容' }],
}

const applyAssistResult = (result) => {
  promptAssistResult.value = result
  formData.value.prompt = result.musicPrompt
  formData.value.originPrompt = result.rawPrompt
  formData.value.promptRecordId = result.recordId
  formData.value.promptSourceType = 1
  if (!formData.value.musicGener && result.musicGenre) {
    formData.value.musicGener = result.musicGenre
  }
  if (!formData.value.musicEmotion && result.musicEmotion) {
    formData.value.musicEmotion = result.musicEmotion
  }
  if (formData.value.musicType === 0 && !formData.value.musicSex && result.musicSex && result.musicSex !== '纯音乐') {
    formData.value.musicSex = result.musicSex
  }
}

const generatePromptAssist = async () => {
  if (!userInfoStore.checkLogin()) {
    return
  }
  if (!assistForm.value.rawPrompt) {
    proxy.Message.warning('请输入一句话需求')
    return
  }
  if (assistLoading.value) {
    return
  }
  assistLoading.value = true
  const result = await proxy.Request({
    url: proxy.Api.promptAssistGenerate,
    params: {
      rawPrompt: assistForm.value.rawPrompt,
      bizType: 'MUSIC_CREATE',
    },
    showLoading: false,
  })
  assistLoading.value = false
  if (!result) {
    return
  }
  applyAssistResult(result.data)
  proxy.Message.success('扩写结果已回填到创作表单')
}

const creating = ref(false)
const createMusic = async () => {
  if (!userInfoStore.checkLogin()) {
    return
  }

  if (creating.value) {
    return
  }
  if (!formData.value.prompt) {
    proxy.Message.warning('请输入提示词')
    return
  }
  creating.value = true
  let result = await proxy.Request({
    url: proxy.Api.createMusic,
    params: { ...formData.value },
    showLoading: false,
  })
  creating.value = false
  if (!result) {
    return
  }
  mitter.emit('newMusic', result.data)

  //重新加载积分
  userInfoStore.updateLastReloadTime()

  proxy.Alert({
    message: '创建成功音乐创作中....',
  })
}

const getCreation = async () => {
  const creationId = route.params.creationId
  if (!creationId) {
    return
  }

  let result = await proxy.Request({
    url: proxy.Api.getCreation,
    params: {
      creationId,
    },
  })
  if (!result) {
    return
  }
  //初始化设置
  if (result.data.modeType == 1) {
    result.data = { ...result.data, ...JSON.parse(result.data.settings) }
  }
  formData.value = result.data
  assistForm.value.rawPrompt = result.data.originPrompt || ''
}

onMounted(() => {
  loadSysSetting()
  getCreation()
})
</script>

<style lang="scss" scoped>
.create-tab {
  :deep(.el-tabs__header) {
    margin-bottom: 0px;
  }
  :deep(.el-tabs__item) {
    color: var(--text);
    font-size: 25px;
    padding-bottom: 10px;
  }
  :deep(.el-tabs__item.is-active) {
    color: var(--purple);
  }
  :deep(.el-tabs__active-bar) {
    background: var(--purple);
  }
  :deep(.el-tabs__nav-wrap) {
    &::after {
      background: #2c2c2c;
    }
  }
}
.create-form {
  color: #fff;
  .assist-panel {
    margin: 16px 0 20px;
    padding: 14px;
    border-radius: 12px;
    background: linear-gradient(135deg, rgba(41, 36, 78, 0.96), rgba(32, 29, 64, 0.96));
    border: 1px solid rgba(255, 255, 255, 0.06);
    :deep(.el-textarea__inner) {
      background: rgba(16, 14, 33, 0.35);
      box-shadow: none;
      color: var(--hiText);
      border: 1px solid rgba(255, 255, 255, 0.08);
    }
    :deep(.el-input__count) {
      background: none;
    }
    .assist-actions {
      margin-top: 12px;
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }
    .assist-btn {
      min-width: 110px;
      height: 38px;
      padding: 0 18px;
      border-radius: 999px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      cursor: pointer;
      font-weight: 600;
      color: #fff;
      background: linear-gradient(90deg, #ff7b54, #ff4d6d);
    }
    .assist-tip {
      color: var(--text);
      font-size: 13px;
    }
    .assist-result {
      margin-top: 16px;
    }
    .assist-result-head {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 12px;
    }
    .assist-result-title {
      font-size: 15px;
      font-weight: 700;
      color: var(--hiText);
    }
    .assist-result-meta {
      font-size: 12px;
      color: #ffb38a;
    }
    .assist-result-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 12px;
    }
    .assist-card {
      padding: 12px;
      border-radius: 10px;
      background: rgba(13, 12, 28, 0.4);
      border: 1px solid rgba(255, 255, 255, 0.05);
    }
    .assist-card-wide {
      grid-column: span 2;
    }
    .assist-card-title {
      margin-bottom: 8px;
      color: var(--text);
      font-size: 13px;
    }
    .assist-card-content {
      line-height: 1.7;
      color: var(--hiText);
      white-space: pre-wrap;
      word-break: break-word;
    }
    .assist-chip-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
    .assist-chip {
      display: inline-flex;
      align-items: center;
      padding: 4px 10px;
      border-radius: 999px;
      background: rgba(255, 255, 255, 0.08);
      color: var(--hiText);
      font-size: 12px;
    }
  }
  .input-panel {
    background: #29244e;
    border-radius: 5px;
    overflow: hidden;
    :deep(.el-textarea__inner) {
      background: #29244e;
      box-shadow: none;
      height: 100%;
      border-radius: 0px;
      color: var(--hiText);
    }
    :deep(.el-input__count) {
      background: none;
    }
    ::-webkit-scrollbar {
      display: none;
    }
    .change-btn {
      text-align: right;
      padding: 10px;
      color: #fff;
      cursor: pointer;
      display: flex;
      justify-content: flex-end;
      .icon-magic {
        border-radius: 5px;
        padding: 5px 10px;
        background: #3f3a60;
        font-size: 13px;
        &::before {
          margin-right: 5px;
          font-size: 16px;
        }
      }
    }
  }

  .advanced-panel {
    display: flex;
    .lyric-panel {
      width: 300px;
      background: #29244d;
      border-radius: 5px;
      overflow: hidden;
      height: 100%;
      .lyric-input {
        border-radius: 0px;
        border-top: 1px solid var(--text);
        :deep(.el-textarea__inner) {
          height: calc(100vh - 515px);
          color: var(--hiText);
        }
      }
    }
    .setting-panel {
      padding: 0px 10px 0px 10px;
      flex: 1;
      width: 0;
    }
  }
  .advanced-panel-line {
    flex-direction: column;
    .lyric-panel {
      width: 100%;
    }
    .setting-panel {
      width: 100%;
    }
  }
  .part-title {
    line-height: 40px;
  }

  .model-select {
    width: 100%;
    :deep(.el-radio-button) {
      width: 50%;
      .el-radio-button__inner {
        width: 100%;
      }
    }
    .model-tips {
      margin-top: 5px;
      font-size: 13px;
      color: var(--text);
    }
  }

  .submit-btn {
    cursor: pointer;
    text-align: center;
    line-height: 45px;
    height: 45px;
    font-weight: bold;
    font-size: 20px;
    border-radius: 30px;
    margin-top: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--btnBg);
    box-shadow: var(-btnShadow);
    &:hover {
      opacity: 0.9;
    }
  }
}

@media (max-width: 500px) {
  .create-form {
    .assist-panel {
      .assist-result-grid {
        grid-template-columns: 1fr;
      }
      .assist-card-wide {
        grid-column: span 1;
      }
    }
    .advanced-panel {
      flex-direction: column;
      .lyric-panel {
        width: 100%;
      }
      .setting-panel {
        width: 100%;
      }
    }
  }
}
</style>
