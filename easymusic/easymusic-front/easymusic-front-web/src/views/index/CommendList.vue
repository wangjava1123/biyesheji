<template>
  <div class="commend-list">
    <div class="swiper-panel" ref="swipePanelRef">
      <div
        class="swiper-wraper"
        :style="{ transform: `translate3d(${xOffset}px, 0px, 0px)` }"
      >
        <MusicCommendItem
          v-for="item in commendList"
          :key="item.musicId"
          :data="item"
          class="hot-item"
          @playList="playList"
        ></MusicCommendItem>
      </div>
    </div>
  </div>
</template>

<script setup>
import MusicCommendItem from "@/component/biz/MusicCommendItem.vue";
import { ref, getCurrentInstance, nextTick, onMounted, onUnmounted, watch } from "vue";
import { useMusicPlayStore } from "@/stores/musicPlay.js";

const { proxy } = getCurrentInstance();
const musicPlayStore = useMusicPlayStore();

const commendList = ref([]);
const xOffset = ref(0);
const parentWidth = ref(0);
const swipePanelRef = ref();

const loadCommend = async () => {
  const result = await proxy.Request({
    url: proxy.Api.loadCommendMusic,
  });
  if (!result) {
    return;
  }
  commendList.value = result.data || [];
};

const playList = () => {
  musicPlayStore.savePlayList(commendList.value);
};

const emit = defineEmits(["disableType"]);

const getSwiperState = () => {
  const itemNodes = document.getElementsByClassName("hot-item");
  if (!itemNodes.length) {
    return {
      itemWidth: 0,
      wrapWidth: 0,
    };
  }
  const itemWidth = itemNodes[0].clientWidth;
  return {
    itemWidth,
    wrapWidth: itemNodes.length * itemWidth,
  };
};

const updateDisableType = () => {
  const { wrapWidth } = getSwiperState();
  if (!wrapWidth || wrapWidth <= parentWidth.value || xOffset.value >= 0) {
    emit("disableType", 1);
    return;
  }
  if (-xOffset.value + parentWidth.value >= wrapWidth) {
    emit("disableType", -1);
    return;
  }
  emit("disableType", null);
};

const change = (type) => {
  const { itemWidth, wrapWidth } = getSwiperState();
  if (!itemWidth || !wrapWidth) {
    return;
  }
  if (xOffset.value === 0 && type === 1) {
    return;
  }
  if (type === -1 && -xOffset.value + parentWidth.value >= wrapWidth) {
    return;
  }
  xOffset.value = xOffset.value + type * itemWidth;
  updateDisableType();
};

const init = async () => {
  await nextTick();
  if (!swipePanelRef.value) {
    return;
  }
  parentWidth.value = swipePanelRef.value.clientWidth;
  const { itemWidth, wrapWidth } = getSwiperState();
  if (itemWidth && wrapWidth) {
    const maxOffset = Math.min(0, parentWidth.value - wrapWidth);
    if (xOffset.value < maxOffset) {
      xOffset.value = maxOffset;
    }
  }
  updateDisableType();
};

onMounted(() => {
  loadCommend();
  init();
  window.addEventListener("resize", init);
});

onUnmounted(() => {
  window.removeEventListener("resize", init);
});

watch(commendList, async () => {
  await nextTick();
  xOffset.value = 0;
  init();
});

defineExpose({
  change,
});
</script>

<style lang="scss" scoped>
.commend-list {
  margin: 6px 0 0;
}

.swiper-panel {
  overflow: hidden;
}

.swiper-wraper {
  display: flex;
  transition: transform 0.6s ease;
}
</style>
