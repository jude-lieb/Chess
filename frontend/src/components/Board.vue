<script setup>
const emit = defineEmits(['select'])
const props = defineProps({
  board: Array,
  outlines: Array,
  isFlipped: Boolean
})

function getSquare(i) {
  return props.isFlipped ? 63 - i : i
}

const images = [
  "blank.jpg","wp.png","wn.png","wb.png","wr.png","wq.png","wk.png",
  "bp.png","bn.png","bb.png","br.png","bq.png","bk.png",
]
</script>

<template>
  <div id="board" class="grid-container">
    <div v-for="i in 64" class="grid-item" :key="i">
      <img :class="outlines[getSquare(i-1)]" @click="emit('select', getSquare(i-1))" :src="'/images/'+(images[board[getSquare(i-1)]] || 'blank.jpg')" draggable="false" />
    </div>
  </div>
</template>

<style scoped>
.grid-container {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    grid-template-rows: repeat(8, 1fr);
    width: min(90vmin, 90vw, 600px);
    aspect-ratio: 1 / 1;
    background-image: url("/images/board.png");
    background-size: cover;
    background-repeat: no-repeat;
}
.grid-item {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    position: relative;
}

.grid-item img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    display: block;
}

.red-outline {
    outline: 2px solid red;
    outline-offset: -4px;
}

.green-outline {
    outline: 2px solid green;
    outline-offset: -4px;
}
</style>


