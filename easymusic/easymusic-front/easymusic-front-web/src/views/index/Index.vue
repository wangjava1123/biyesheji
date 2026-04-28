<template>
  <div class="index-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <div class="hero-kicker">AIGC MUSIC WORKFLOW</div>
        <div class="hero-title">一句话开始创作，把音乐、封面和发布串成完整闭环</div>
        <div class="hero-desc">
          当前首页继续承担“发现作品 + 进入创作”的入口角色。推荐区展示值得继续收听的作品，最新区承接公开发布内容，整体口径与作品发布状态保持一致。
        </div>
        <div class="hero-actions">
          <router-link to="/idea" class="hero-btn hero-btn-primary">开始创作</router-link>
          <router-link to="/latest" class="hero-btn hero-btn-secondary">查看最新发布</router-link>
        </div>
      </div>
      <div class="hero-side">
        <div v-for="item in workflowCards" :key="item.title" class="workflow-card">
          <div class="workflow-step">{{ item.step }}</div>
          <div class="workflow-title">{{ item.title }}</div>
          <div class="workflow-desc">{{ item.desc }}</div>
        </div>
      </div>
    </section>

    <section class="section-panel">
      <div class="section-head">
        <div class="section-copy">
          <div class="section-kicker">推荐精选</div>
          <div class="section-title">适合继续收听和拆解的热门作品</div>
          <div class="section-desc">保留横向滑动结构，但统一为更清晰的卡片节奏和更稳定的移动端交互。</div>
        </div>
        <div class="section-tools">
          <div
            :class="['iconfont icon-narrow-left', disableType === 1 ? 'tool-btn-disabled' : '']"
            class="tool-btn"
            @click="changeCommend(1)"
          ></div>
          <div
            :class="['iconfont icon-narrow-right', disableType === -1 ? 'tool-btn-disabled' : '']"
            class="tool-btn"
            @click="changeCommend(-1)"
          ></div>
        </div>
      </div>
      <CommendList ref="commendListRef" @disableType="hotChangeTypeHandler"></CommendList>
    </section>

    <section class="section-panel latest-panel">
      <div class="section-head">
        <div class="section-copy">
          <div class="section-kicker">最新发布</div>
          <div class="section-title">最近公开上线的作品</div>
          <div class="section-desc">只展示已发布作品，首页保持轻量预览，完整列表继续在“最新发布”页查看。</div>
        </div>
        <router-link to="/latest" class="more-link">
          更多
          <span class="iconfont icon-narrow-right"></span>
        </router-link>
      </div>
      <div class="latest-list">
        <LatestList :indexType="1"></LatestList>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from "vue";
import LatestList from "./LatestList.vue";
import CommendList from "./CommendList.vue";

const commendListRef = ref();
const disableType = ref(1);

const workflowCards = [
  {
    step: "01",
    title: "一句话扩写",
    desc: "把自然语言需求整理成音乐提示词、封面提示词和推荐标题。",
  },
  {
    step: "02",
    title: "生成与封面",
    desc: "音乐生成完成后继续补 AI 封面、来源记录和作品摘要信息。",
  },
  {
    step: "03",
    title: "发布与统计",
    desc: "公开展示只读取已发布作品，后台看板统一回收创作和封面数据。",
  },
];

const hotChangeTypeHandler = (type) => {
  disableType.value = type;
};

const changeCommend = (type) => {
  commendListRef.value?.change(type);
};
</script>

<style lang="scss" scoped>
.index-page {
  padding: 14px 12px 22px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  color: #fff;
}

.hero-panel,
.section-panel {
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.9fr);
  gap: 20px;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(255, 123, 84, 0.24), transparent 36%),
    linear-gradient(135deg, rgba(15, 17, 36, 0.96), rgba(25, 31, 58, 0.92));
}

.hero-copy {
  min-width: 0;
}

.hero-kicker,
.section-kicker,
.workflow-step {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.58);
}

.hero-title {
  margin-top: 12px;
  max-width: 760px;
  font-size: 34px;
  line-height: 1.25;
  font-weight: 700;
}

.hero-desc,
.section-desc,
.workflow-desc {
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.72);
}

.hero-desc {
  margin-top: 14px;
  max-width: 640px;
}

.hero-actions {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hero-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 11px 18px;
  border-radius: 999px;
  text-decoration: none;
  transition: transform 0.2s ease, opacity 0.2s ease, background 0.2s ease;
}

.hero-btn:hover {
  transform: translateY(-1px);
  opacity: 0.92;
}

.hero-btn-primary {
  color: #1b1220;
  background: linear-gradient(135deg, #ffcf7b, #ff8c5a);
}

.hero-btn-secondary {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hero-side {
  display: grid;
  gap: 12px;
}

.workflow-card {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
}

.workflow-title,
.section-title {
  margin-top: 8px;
  font-size: 24px;
  line-height: 1.3;
  font-weight: 700;
}

.workflow-desc {
  margin-top: 8px;
  font-size: 13px;
}

.section-panel {
  padding: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.025));
  backdrop-filter: blur(10px);
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.section-copy {
  min-width: 0;
}

.section-desc {
  margin-top: 10px;
  max-width: 620px;
}

.section-tools {
  display: flex;
  gap: 10px;
}

.tool-btn {
  width: 42px;
  height: 42px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  cursor: pointer;
  transition: background 0.2s ease, opacity 0.2s ease;
}

.tool-btn:hover {
  background: rgba(255, 255, 255, 0.14);
}

.tool-btn-disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.tool-btn-disabled:hover {
  background: rgba(255, 255, 255, 0.08);
}

.latest-panel {
  padding-bottom: 14px;
}

.more-link {
  flex-shrink: 0;
  margin-top: 2px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 9px 14px;
  border-radius: 999px;
  text-decoration: none;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.more-link:hover {
  background: rgba(255, 123, 84, 0.18);
}

.latest-list {
  margin-top: 14px;
}

.latest-list :deep(.load-more-data-list) {
  padding: 0;
}

@media (max-width: 1100px) {
  .hero-panel {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .index-page {
    padding: 10px 8px 18px;
    gap: 16px;
  }

  .hero-panel,
  .section-panel {
    border-radius: 20px;
  }

  .hero-panel {
    padding: 18px 16px;
  }

  .hero-title {
    font-size: 28px;
  }

  .workflow-title,
  .section-title {
    font-size: 22px;
  }

  .section-panel {
    padding: 16px;
  }

  .section-head {
    flex-direction: column;
  }

  .section-tools {
    align-self: flex-end;
  }
}

@media (max-width: 500px) {
  .hero-title {
    font-size: 24px;
  }

  .hero-actions {
    gap: 10px;
  }

  .hero-btn {
    width: 100%;
  }

  .workflow-card {
    padding: 14px 14px 16px;
  }

  .more-link {
    align-self: stretch;
    justify-content: center;
  }

  .section-tools {
    align-self: stretch;
    justify-content: flex-end;
  }
}
</style>
