<template>
  <div class="create-body">
    <section class="create-panel">
      <Create></Create>
    </section>
    <section class="music-panel">
      <div class="music-panel-head">
        <div>
          <div class="music-panel-kicker">创作动态</div>
          <div class="music-panel-title">最近创作与生成进度</div>
        </div>
        <div class="music-panel-desc">
          生成中的作品会自动刷新，完成后可以继续整理封面、发布状态和作品信息。
        </div>
      </div>
      <MusicList></MusicList>
    </section>
  </div>
</template>

<script setup>
import MusicList from './MusicList.vue'
import Create from './Create.vue'
import Player from '@/component/common/Player.vue'
import { ref, reactive, getCurrentInstance, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
</script>

<style lang="scss" scoped>
.create-body {
  display: grid;
  grid-template-columns: minmax(360px, 620px) minmax(0, 1fr);
  min-height: calc(100dvh - 70px);
  background:
    radial-gradient(circle at top right, rgba(255, 123, 84, 0.14), transparent 28%),
    linear-gradient(180deg, #080612 0%, #090714 100%);
  .create-panel {
    min-width: 0;
    padding: 18px 18px 20px;
    background: linear-gradient(180deg, rgba(14, 1, 35, 0.98), rgba(18, 7, 42, 0.96));
    border-right: 1px solid rgba(255, 255, 255, 0.06);
  }
  .music-panel {
    min-width: 0;
    min-height: calc(100dvh - 70px);
    display: flex;
    flex-direction: column;
    padding: 20px 14px 0;
    .music-panel-head {
      display: flex;
      align-items: flex-end;
      justify-content: space-between;
      gap: 16px;
      flex-wrap: wrap;
      padding: 0 10px 14px;
    }
    .music-panel-kicker {
      color: rgba(255, 255, 255, 0.5);
      font-size: 12px;
      letter-spacing: 0.14em;
      text-transform: uppercase;
    }
    .music-panel-title {
      margin-top: 8px;
      font-size: 26px;
      line-height: 1.3;
      font-weight: 700;
      color: #fff;
    }
    .music-panel-desc {
      max-width: 520px;
      color: rgba(255, 255, 255, 0.66);
      line-height: 1.7;
      text-align: right;
    }
    :deep(.load-more-data-list) {
      flex: 1;
      min-height: 0;
      padding-top: 0;
    }
  }
}

@media (max-width: 1100px) {
  .create-body {
    grid-template-columns: 1fr;
    .create-panel {
      border-right: none;
      border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    }
    .music-panel {
      min-height: clamp(360px, 54dvh, 620px);
      padding-top: 16px;
      .music-panel-desc {
        text-align: left;
      }
    }
  }
}

@media (max-width: 720px) {
  .create-body {
    .create-panel {
      padding: 14px 12px 18px;
    }
    .music-panel {
      padding: 14px 8px 0;
      .music-panel-head {
        padding: 0 6px 12px;
      }
      .music-panel-title {
        font-size: 22px;
      }
    }
  }
}
</style>
