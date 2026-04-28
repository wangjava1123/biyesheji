<template>
  <div class="create-shell">
    <div class="create-tab">
      <el-tabs v-model="formData.musicType">
        <el-tab-pane label="歌曲" :name="0"></el-tab-pane>
        <el-tab-pane label="纯音乐" :name="1"></el-tab-pane>
      </el-tabs>
    </div>
    <div class="create-intro">
      <div class="intro-copy">
        <div class="intro-kicker">AIGC 创作流程</div>
        <div class="intro-title">{{ createHeading }}</div>
        <div class="intro-desc">
          先用一句话整理创作意图，再确认提示词、模型和生成模式。当前来源会随着作品一并记录到后续封面、发布和统计链路。
        </div>
      </div>
      <div class="intro-grid">
        <div class="intro-card" v-for="item in introCards" :key="item.label">
          <div class="intro-card-label">{{ item.label }}</div>
          <div class="intro-card-value">{{ item.value }}</div>
          <div class="intro-card-desc">{{ item.desc }}</div>
        </div>
      </div>
    </div>
  </div>
  <div class="create-form">
    <el-form :model="formData" :rules="rules" ref="formDataRef" label-width="80px" @submit.prevent>
      <div class="mode-strip">
        <Switch :data="[
            { label: '简单模式', value: 0 },
            { label: '高级模式', value: 1 },
          ]" v-model="formData.modeType"></Switch>
        <div class="mode-strip-tip">
          {{ modeTipText }}
        </div>
      </div>
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
          <div class="assist-tip">扩写后会自动回填专业级音乐提示词，并同步生成与音乐语义闭环的封面提示词。</div>
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
            <div class="assist-card assist-card-wide" v-if="assistHighlightItems.length > 0">
              <div class="assist-card-title">结构化建议</div>
              <div class="assist-highlight-grid">
                <div class="assist-highlight" v-for="item in assistHighlightItems" :key="item.label">
                  <span class="assist-highlight-label">{{ item.label }}</span>
                  <span class="assist-highlight-value">{{ item.value }}</span>
                </div>
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
      <div class="model-panel">
        <div class="part-title">选择模型</div>
        <div class="model-select">
          <el-radio-group v-model="formData.model" class="model-select" size="large">
            <el-radio-button v-for="model in music_models" :value="model.dictCode" :label="model.dictCode">
            </el-radio-button>
          </el-radio-group>
          <div class="model-tips">{{ currentModel?.dictDesc || '系统会按照当前音乐类型提供可用模型。' }}</div>
        </div>
      </div>
      <div class="summary-panel">
        <div class="summary-head">
          <div class="summary-title">本次提交摘要</div>
          <div class="summary-tip">
            提交后会进入右侧最近创作列表，后续还可以继续补封面、发布状态和作品信息。
          </div>
        </div>
        <div class="summary-grid">
          <div class="summary-card" v-for="item in submitSummaryItems" :key="item.label">
            <div class="summary-card-label">{{ item.label }}</div>
            <div class="summary-card-value">{{ item.value }}</div>
          </div>
          <div class="summary-card summary-card-wide" v-if="summaryRawPrompt">
            <div class="summary-card-title">一句话需求</div>
            <div class="summary-card-content">{{ summaryRawPrompt }}</div>
          </div>
          <div class="summary-card summary-card-wide">
            <div class="summary-card-title">最终音乐提示词</div>
            <div class="summary-card-content">{{ summaryPromptText }}</div>
          </div>
        </div>
      </div>
      <div class="submit-btn" @click="createMusic">
        <el-icon class="is-loading" v-if="creating">
          <Loading style="width: 1em; height: 1em" />
        </el-icon>
        <span v-else>创作音乐</span>
      </div>
      <div class="submit-note">
        当前将使用 {{ createPromptSourceText }}，模型为 {{ currentModel?.dictCode || formData.model || '待选择' }}。
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

const createHeading = computed(() => {
  return formData.value.musicType === 0
    ? '把一句灵感扩展成完整歌曲'
    : '把一句灵感扩展成纯音乐氛围'
})

const musicTypeText = computed(() => {
  return formData.value.musicType === 0 ? '歌曲' : '纯音乐'
})

const modeTypeText = computed(() => {
  return formData.value.modeType === 0 ? '简单模式' : '高级模式'
})

const createPromptSourceText = computed(() => {
  return formData.value.promptSourceType === 1 ? 'AI 扩写回填' : '手动输入 / 灵感库'
})

const currentModelText = computed(() => {
  return currentModel.value?.dictCode || formData.value.model || '待选择'
})

const modeTipText = computed(() => {
  return formData.value.modeType === 0
    ? '简单模式适合快速开始，会优先围绕一句话需求和最终提示词完成创作。'
    : '高级模式适合细化控制，可继续设置歌词、曲风、情绪、人声与模型。'
})

const assistStatusText = computed(() => {
  if (!promptAssistResult.value) {
    return '未扩写'
  }
  const titleCount = promptAssistResult.value.titleSuggestions?.length || 0
  const tagCount = promptAssistResult.value.tags?.length || 0
  return `标题 ${titleCount} · 标签 ${tagCount}`
})

const introCards = computed(() => {
  return [
    {
      label: '创作类型',
      value: musicTypeText.value,
      desc: formData.value.musicType === 0 ? '适合歌词和人声作品' : '适合氛围、配乐与纯演奏',
    },
    {
      label: '生成模式',
      value: modeTypeText.value,
      desc: formData.value.modeType === 0 ? '更快进入生成' : '可进一步细化控制',
    },
    {
      label: '提示词来源',
      value: createPromptSourceText.value,
      desc: '当前结果会带入作品创作记录',
    },
    {
      label: '结构化结果',
      value: assistStatusText.value,
      desc: promptAssistResult.value ? assistSourceText.value : '先扩写再确认细节',
    },
  ]
})

const assistHighlightItems = computed(() => {
  if (!promptAssistResult.value) {
    return []
  }
  return [
    {
      label: '曲风',
      value: promptAssistResult.value.musicGenre || '未指定',
    },
    {
      label: '情绪',
      value: promptAssistResult.value.musicEmotion || '未指定',
    },
    {
      label: '人声',
      value: promptAssistResult.value.musicSex || (formData.value.musicType === 1 ? '纯音乐' : '未指定'),
    },
    {
      label: '视觉风格',
      value: promptAssistResult.value.visualStyle || '未指定',
    },
  ]
})

const currentGenreText = computed(() => {
  return formData.value.musicGener || promptAssistResult.value?.musicGenre || '待补充'
})

const currentEmotionText = computed(() => {
  return formData.value.musicEmotion || promptAssistResult.value?.musicEmotion || '待补充'
})

const currentSexText = computed(() => {
  if (formData.value.musicType === 1) {
    return '纯音乐'
  }
  return formData.value.musicSex || promptAssistResult.value?.musicSex || '待补充'
})

const summaryRawPrompt = computed(() => {
  return assistForm.value.rawPrompt || formData.value.originPrompt || ''
})

const summaryPromptText = computed(() => {
  return formData.value.prompt || '还没有可提交的音乐提示词，请先填写或使用智能扩写。'
})

const submitSummaryItems = computed(() => {
  return [
    {
      label: '创作类型',
      value: musicTypeText.value,
    },
    {
      label: '生成模式',
      value: modeTypeText.value,
    },
    {
      label: '提示词来源',
      value: createPromptSourceText.value,
    },
    {
      label: '当前模型',
      value: currentModelText.value,
    },
    {
      label: '曲风',
      value: currentGenreText.value,
    },
    {
      label: '情绪',
      value: currentEmotionText.value,
    },
    {
      label: '人声',
      value: currentSexText.value,
    },
    {
      label: '结构化结果',
      value: assistStatusText.value,
    },
  ]
})

const normalizeMusicSex = (value, musicType = formData.value.musicType) => {
  if (musicType === 1) {
    return ''
  }
  const text = `${value || ''}`.trim()
  if (!text) {
    return ''
  }
  const lower = text.toLowerCase()
  if (text.includes('合唱') || text.includes('对唱') || lower.includes('choir') || lower.includes('choral') || lower.includes('duet')) {
    return '合唱'
  }
  if (text.includes('童声') || lower.includes('child') || lower.includes('kid')) {
    return '童声'
  }
  if (text.includes('男') || lower.includes('male') || lower.includes('tenor') || lower.includes('baritone')) {
    return '男声'
  }
  if (text.includes('女') || lower.includes('female') || lower.includes('soprano') || lower.includes('alto')) {
    return '女声'
  }
  if (text.includes('纯音乐') || text.includes('配乐') || lower.includes('instrumental')) {
    return ''
  }
  return text.length <= 5 ? text : ''
}

const applyAssistResult = (result) => {
  promptAssistResult.value = result
  formData.value.prompt = result.musicPrompt
  formData.value.originPrompt = result.rawPrompt
  formData.value.promptRecordId = result.recordId
  formData.value.promptSourceType = 1
  if (result.musicGenre) {
    formData.value.musicGener = result.musicGenre
  }
  if (result.musicEmotion) {
    formData.value.musicEmotion = result.musicEmotion
  }
  const normalizedMusicSex = normalizeMusicSex(result.musicSex, formData.value.musicType)
  if (formData.value.musicType === 0 && normalizedMusicSex) {
    formData.value.musicSex = normalizedMusicSex
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
      musicType: formData.value.musicType,
      modeType: formData.value.modeType,
      musicGenre: formData.value.musicGener || '',
      musicEmotion: formData.value.musicEmotion || '',
      musicSex: normalizeMusicSex(formData.value.musicSex, formData.value.musicType),
    },
    showLoading: false,
    timeout: 60 * 1000,
  })
  assistLoading.value = false
  if (!result) {
    return
  }
  applyAssistResult(result.data)
  userInfoStore.updateLastReloadTime()
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
  const submitParams = {
    ...formData.value,
    musicSex: normalizeMusicSex(formData.value.musicSex, formData.value.musicType),
  }
  let result = await proxy.Request({
    url: proxy.Api.createMusic,
    params: submitParams,
    showLoading: false,
    timeout: 120 * 1000,
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
  result.data.musicSex = normalizeMusicSex(result.data.musicSex, result.data.musicType)
  formData.value = result.data
  assistForm.value.rawPrompt = result.data.originPrompt || ''
}

onMounted(() => {
  loadSysSetting()
  getCreation()
})
</script>

<style lang="scss" scoped>
.create-shell {
  color: #fff;
}

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

.create-intro {
  margin-top: 14px;
  padding: 18px;
  border-radius: 22px;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(280px, 0.85fr);
  gap: 16px;
  background:
    radial-gradient(circle at top left, rgba(255, 123, 84, 0.28), transparent 32%),
    linear-gradient(135deg, rgba(18, 20, 42, 0.96), rgba(25, 30, 54, 0.92));
  border: 1px solid rgba(255, 255, 255, 0.08);
  .intro-copy {
    min-width: 0;
  }
  .intro-kicker {
    color: rgba(255, 255, 255, 0.54);
    font-size: 12px;
    letter-spacing: 0.16em;
    text-transform: uppercase;
  }
  .intro-title {
    margin-top: 10px;
    font-size: 28px;
    line-height: 1.35;
    font-weight: 700;
  }
  .intro-desc {
    margin-top: 12px;
    line-height: 1.75;
    color: rgba(255, 255, 255, 0.72);
    max-width: 560px;
  }
  .intro-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
  .intro-card {
    padding: 14px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.07);
  }
  .intro-card-label {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.58);
  }
  .intro-card-value {
    margin-top: 8px;
    font-size: 18px;
    font-weight: 700;
    color: #fff;
  }
  .intro-card-desc {
    margin-top: 8px;
    font-size: 12px;
    line-height: 1.6;
    color: rgba(255, 255, 255, 0.68);
  }
}

.create-form {
  color: #fff;
  margin-top: 16px;
  .mode-strip {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    flex-wrap: wrap;
  }
  .mode-strip-tip {
    max-width: 320px;
    color: rgba(255, 255, 255, 0.68);
    font-size: 13px;
    line-height: 1.6;
  }
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
    .assist-highlight-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 10px;
    }
    .assist-highlight {
      padding: 10px 12px;
      border-radius: 12px;
      background: rgba(255, 255, 255, 0.04);
      display: flex;
      flex-direction: column;
      gap: 6px;
    }
    .assist-highlight-label {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.56);
    }
    .assist-highlight-value {
      color: var(--hiText);
      line-height: 1.5;
      word-break: break-word;
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
    display: grid;
    grid-template-columns: minmax(280px, 340px) minmax(0, 1fr);
    gap: 14px;
    .lyric-panel {
      background: #29244d;
      border-radius: 12px;
      overflow: hidden;
      min-width: 0;
      .lyric-input {
        border-radius: 0px;
        border-top: 1px solid var(--text);
        :deep(.el-textarea__inner) {
          min-height: 240px;
          color: var(--hiText);
        }
      }
    }
    .setting-panel {
      padding: 4px 16px 14px;
      min-width: 0;
      border-radius: 12px;
      background: rgba(255, 255, 255, 0.04);
      border: 1px solid rgba(255, 255, 255, 0.06);
    }
  }
  .advanced-panel-line {
    grid-template-columns: 1fr;
    .lyric-panel {
      width: 100%;
    }
    .setting-panel {
      width: 100%;
    }
  }
  .part-title {
    line-height: 40px;
    font-weight: 600;
  }

  .model-panel {
    margin-top: 8px;
    padding: 0 16px 16px;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.06);
  }

  .summary-panel {
    margin-top: 16px;
    padding: 16px;
    border-radius: 16px;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.03));
    border: 1px solid rgba(255, 255, 255, 0.06);
  }

  .summary-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 14px;
    flex-wrap: wrap;
  }

  .summary-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--hiText);
  }

  .summary-tip {
    max-width: 420px;
    color: rgba(255, 255, 255, 0.68);
    font-size: 13px;
    line-height: 1.7;
  }

  .summary-grid {
    margin-top: 14px;
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 10px;
  }

  .summary-card {
    padding: 12px;
    border-radius: 14px;
    background: rgba(7, 8, 20, 0.34);
    border: 1px solid rgba(255, 255, 255, 0.05);
  }

  .summary-card-wide {
    grid-column: span 4;
  }

  .summary-card-label,
  .summary-card-title {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.56);
  }

  .summary-card-value {
    margin-top: 8px;
    color: var(--hiText);
    line-height: 1.45;
    word-break: break-word;
  }

  .summary-card-content {
    margin-top: 10px;
    color: rgba(255, 255, 255, 0.82);
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-word;
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
    box-shadow: var(--btnShadow);
    &:hover {
      opacity: 0.9;
    }
  }
  .submit-note {
    margin-top: 10px;
    color: rgba(255, 255, 255, 0.64);
    text-align: center;
    font-size: 12px;
    line-height: 1.6;
  }
}

@media (max-width: 960px) {
  .create-intro {
    grid-template-columns: 1fr;
  }

  .create-form {
    .summary-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    .summary-card-wide {
      grid-column: span 2;
    }
    .advanced-panel {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 500px) {
  .create-intro {
    padding: 16px;
    border-radius: 18px;
    .intro-title {
      font-size: 24px;
    }
    .intro-grid {
      grid-template-columns: 1fr;
    }
  }
  .create-form {
    .mode-strip-tip {
      max-width: none;
    }
    .summary-panel {
      padding: 14px;
    }
    .summary-grid {
      grid-template-columns: 1fr;
    }
    .summary-card-wide {
      grid-column: span 1;
    }
    .assist-panel {
      .assist-result-grid {
        grid-template-columns: 1fr;
      }
      .assist-card-wide {
        grid-column: span 1;
      }
      .assist-highlight-grid {
        grid-template-columns: 1fr;
      }
    }
    .advanced-panel {
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
