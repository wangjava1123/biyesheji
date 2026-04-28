<template>
  <div class="latest-page">
    <section class="latest-hero">
      <div class="hero-head">
        <BackBtn></BackBtn>
        <div class="hero-copy">
          <div class="hero-kicker">Latest Releases</div>
          <div class="hero-title">最新发布作品</div>
          <div class="hero-desc">
            这里统一查看已经公开发布的作品列表。页面继续保留局部滚动加载，但高度口径改为和创作者中心一致，避免移动端出现截断和空白。
          </div>
        </div>
      </div>
      <div class="hero-tags">
        <span class="hero-chip">仅公开作品</span>
        <span class="hero-chip">按发布时间排序</span>
        <span class="hero-chip">支持继续收听与分享</span>
      </div>
    </section>

    <section class="latest-panel">
      <div class="panel-head">
        <div class="panel-title">最近公开上线</div>
        <div class="panel-desc">用于首页预览之外的完整浏览入口，继续承接公开作品发现流。</div>
      </div>
      <div class="data-list">
        <LatestList></LatestList>
      </div>
    </section>
  </div>
</template>

<script setup>
import LatestList from "./LatestList.vue";
</script>

<style lang="scss" scoped>
.latest-page {
  height: calc(100dvh - 125px);
  min-height: calc(100dvh - 125px);
  display: flex;
  flex-direction: column;
  gap: 16px;
  color: #fff;
}

.latest-hero,
.latest-panel {
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.latest-hero {
  padding: 20px 22px;
  background:
    radial-gradient(circle at top left, rgba(123, 157, 255, 0.24), transparent 34%),
    linear-gradient(135deg, rgba(13, 17, 36, 0.96), rgba(18, 27, 52, 0.92));
}

.hero-head {
  display: flex;
  align-items: flex-start;
  gap: 14px;
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
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.3;
}

.hero-desc {
  margin-top: 10px;
  max-width: 720px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.72);
}

.hero-tags {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.84);
  font-size: 12px;
}

.latest-panel {
  flex: 1;
  min-height: 0;
  padding: 18px 18px 14px;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.035);
}

.panel-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 14px;
  padding: 0 4px 14px;
}

.panel-title {
  font-size: 24px;
  line-height: 1.3;
  font-weight: 700;
}

.panel-desc {
  max-width: 480px;
  line-height: 1.7;
  text-align: right;
  color: rgba(255, 255, 255, 0.68);
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
  .latest-page {
    height: calc(100dvh - 145px);
    min-height: calc(100dvh - 145px);
  }

  .panel-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .panel-desc {
    text-align: left;
  }
}

@media (max-width: 600px) {
  .latest-page {
    height: calc(100dvh - 176px);
    min-height: calc(100dvh - 176px);
    gap: 12px;
  }

  .latest-hero,
  .latest-panel {
    border-radius: 20px;
  }

  .latest-hero {
    padding: 16px;
  }

  .hero-head {
    flex-direction: column;
  }

  .hero-title {
    font-size: 24px;
  }

  .latest-panel {
    padding: 14px 12px 12px;
  }

  .panel-head {
    padding: 0 2px 10px;
  }

  .panel-title {
    font-size: 22px;
  }
}
</style>
