<script setup>
import Panel from './Panel.vue'
import Board from './Board.vue'
import {ref, computed} from 'vue'

const deployed = window.location.hostname === "judelieb.com"
const socket = new WebSocket(deployed ? "wss://judelieb.com" : "ws://localhost")

const info = ref({
  wMat: null,
  bMat: null,
  moveCount: null,
  gameStatus: '',
})

let options = ref([])
let board = ref([])
let outlines = ref([])
let isFlipped = ref(false)
let mode = true
let crd = -1

socket.onopen = () => {console.log("Connected to Java WebSocket!")}
socket.onclose = () => {console.log("WebSocket connection closed.")}
socket.onerror = (error) => {console.log("WebSocket Error:", error)}

socket.onmessage = (event) => {
  try {
    const data = JSON.parse(event.data)
    if(data.desc === "boardState") {
      board.value = data.squares
      options.value = data.options
      info.value.gameStatus = data.status
      info.value.moveCount = data.moveCount
      info.value.wMat = data.wMat
      info.value.bMat = data.bMat
    }
  } catch (error) {
    console.log("Error handling server input", error)
  }
}

//Preventing websocket closure
setInterval(() => {
  if (socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify({ desc: "ping" }))
  }
}, 60000)

function undo() {
  outlines.value = []
  socket.send(JSON.stringify({ desc: "undo" }))
}

function reset() {
  mode = true
  options.value = []
  outlines.value = []
  socket.send(JSON.stringify({ desc: "reset" }))
}

function handleSelect(selected) {
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
    outlines.value = []
    let approved = false

    if(options.value[crd]) {
      for (let i = 0; i < options.value[crd].length; i++) {
        if(selected === options.value[crd][i]) approved = true
      }
    }

    let promote = 0
    const isWhitePawn = board[crd] === 1;
    const isBlackPawn = board[crd] === 7;
    const isPromotion = (isWhitePawn && clickedIndex < 8) || (isBlackPawn && clickedIndex > 55);

    if(isPromotion) {
      promote = isWhitePawn ? 5 : (isBlackPawn ? 11 : 0)
    }

    mode = true
    if(approved === true) {
      socket.send(JSON.stringify({desc: "move request", crd: [Math.floor(crd / 8), crd % 8, Math.floor(selected / 8), selected % 8, promote]}))
    }
  }
}
</script>

<template>
  <div class="container">
    <main class="d-flex flex-column flex-md-row align-items-center align-items-md-start justify-content-center gap-3 w-100">
        <Board @select="handleSelect" :board :outlines :isFlipped></Board>
        <Panel @reset="reset" @undo="undo" @flip="isFlipped = !isFlipped" :info></Panel>
    </main>
  </div>
</template>