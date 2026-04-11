<script setup>
import Panel from './Panel.vue'
import Board from './Board.vue'
import {ref} from 'vue'
import PromoteModal from './PromoteModal.vue'

const deployed = window.location.hostname === "judelieb.com"
let socket = null

const info = ref({
  wMat: null,
  bMat: null,
  moveCount: null,
  gameStatus: '',
  autoQueen: true,
  player: true
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

function getWebSocket() {
  let newSocket = new WebSocket(deployed ? "wss://api.judelieb.com/ws" : "ws://localhost:5000/ws")

  newSocket.onopen = () => {
    console.log("Web socket connected.")
    newSocket.send(JSON.stringify({ desc: "new", player: info.value.player }))
  }
  newSocket.onclose = () => {console.log("Web socket disconnected.")}
  newSocket.onerror = (error) => {console.log("Web socket error:", error)}

  newSocket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if(data.desc === "boardState") {
        board.value = data.squares
        options.value = data.options
        isFlipped.value = !data.turn
        info.value.gameStatus = data.status
        info.value.moveCount = data.moveCount
        info.value.wMat = data.wMat
        info.value.bMat = data.bMat
      }
    } catch (error) {
      console.log("Error handling server input", error)
    }
  }
  return newSocket
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

function newGame() {
  socket.close()
  socket = getWebSocket()
  mode = true
  options.value = []
  outlines.value = []
}

function handlePromote(type) {
  let promote = type
  if(board.value[crd] > 6) promote = type + 6
  socket.send(JSON.stringify({desc: "move request", crd: [crd, box, promote]}))
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

    board.value[selected] = board.value[crd]
    board.value[crd] = 0
    outlines.value = []

    let promote = 0
    const isWhitePawn = board.value[crd] === 1
    const isBlackPawn = board.value[crd] === 7
    const isPromotion = (isWhitePawn && selected < 8) || (isBlackPawn && selected > 55)

    if(isPromotion) {
      if(info.value.autoQueen === true) {
        promote = isWhitePawn ? 5 : (isBlackPawn ? 11 : 0);
        socket.send(JSON.stringify({desc: "move request", crd: [crd, selected, promote]}))
        mode = true
      } else {
        showModal.value = true
      }
    } else {
      mode = true
      socket.send(JSON.stringify({desc: "move request", crd: [crd, box, promote]}))
    }
  }
}

socket = getWebSocket()

</script>

<template>
  <div class="container">
    <main class="d-flex flex-column flex-md-row align-items-center align-items-md-start justify-content-center gap-3 w-100">
        <Board @select="handleSelect" :board :outlines :isFlipped></Board>
        <Panel 
          :info
          @newGame="newGame" 
          @undo="undo" 
          @auto-queen="info.autoQueen = !info.autoQueen"
          @changeColor="info.player = !info.player">
        </Panel>
        <PromoteModal :showModal @pick="handlePromote"></PromoteModal>
      </main>
  </div>
</template>