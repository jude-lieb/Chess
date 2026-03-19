<script setup>
import Panel from './Panel.vue'
import Board from './Board.vue'
import {ref} from 'vue'
import PromoteModal from './PromoteModal.vue'
import publicApi from '@/api/publicApi'

// const deployed = window.location.hostname === "judelieb.com"
// const socket = new WebSocket(deployed ? "wss://judelieb.com" : "ws://localhost")

const info = ref({
  wMat: null,
  bMat: null,
  moveCount: null,
  gameStatus: '',
  autoQueen: true
})

const options = ref([])
const board = ref([])
const outlines = ref([])
const isFlipped = ref(false)
const showModal = ref(false)

//Move selection states (mode = start/end, crd = start, box = end)
let mode = true
let crd = -1
let box = -1

async function newGame() {
  mode = true
  options.value = []
  outlines.value = []
  try {
    const response = await publicApi.newGame()
    console.log(response)
    localStorage.setItem('gameId', response.data)
    load()
  } catch (error) {
    console.log("Error handling server input", error)
  }
}

async function move(move) {
  try {
    const response = await publicApi.move(move)
    console.log(response)
    load()
  } catch(e) {
    console.log(e)
  } 
}

async function load() {
  try {
    const response = await publicApi.getBoard()
    let data = response.data
    console.log(data)

    if(data.desc === "boardState") {
      board.value = data.squares
      options.value = data.options
      info.value.gameStatus = data.status
      info.value.moveCount = data.moveCount
      info.value.wMat = data.wMat
      info.value.bMat = data.bMat
    }
  } catch(e) {
    console.log(e)
  } 
}

async function undo() {
  outlines.value = []
  try {
    const response = await publicApi.undoMove()
    console.log(response)
    load()
  } catch (error) {
    console.log("Error handling server input", error)
  }
}

function handlePromote(type) {
  let promote = type
  if(board.value[crd] > 6) promote = type + 6
  move({desc: "move request", crd: [crd, box, promote]})
  mode = true
  showModal.value = false
}

function cancelMove() {
  mode = true
  crd = -1
  box = -1
  outlines.value = []
  showModal.value = false
}

function handleSelect(selected) {
  console.log(mode)
  if (mode === true) {
    crd = selected
    outlines.value[selected] = 'green-outline'
    if (options.value[selected] !== undefined && options.value[selected] !== null) {
      for (let i = 0; i < options.value[selected].length; i++) {
        outlines.value[options.value[selected][i]] = 'red-outline'
      }
    }
    mode = false
  } else {
    box = selected
    outlines.value = []
    let approved = false

    if(options.value[crd]) {
      for (let i = 0; i < options.value[crd].length; i++) {
        if(selected === options.value[crd][i]) approved = true
      }
    }

    if(approved === false) {
      cancelMove()
      return
    }

    let promote = 0
    const isWhitePawn = board.value[crd] === 1;
    const isBlackPawn = board.value[crd] === 7;
    const isPromotion = (isWhitePawn && selected < 8) || (isBlackPawn && selected > 55);

    if(isPromotion) {
      if(info.value.autoQueen === true) {
        promote = isWhitePawn ? 5 : (isBlackPawn ? 11 : 0);
        move({desc: "move request", crd: [crd, selected, promote]})
        mode = true
      } else {
        showModal.value = true
      }
    } else {
      mode = true
      move({desc: "move request", crd: [crd, box, promote]})
    }
  }
}

newGame()

</script>

<template>
  <div class="container">
    <main class="d-flex flex-column flex-md-row align-items-center align-items-md-start justify-content-center gap-3 w-100">
        <Board @select="handleSelect" :board :outlines :isFlipped></Board>
        <Panel 
          :info
          @reset="newGame" 
          @undo="undo" 
          @flip="isFlipped = !isFlipped" 
          @auto-queen="info.autoQueen = !info.autoQueen">
        </Panel>
        <PromoteModal :showModal @pick="handlePromote"></PromoteModal>
      </main>
  </div>
</template>