<script setup>
import Panel from './Panel.vue'
import Board from './Board.vue'
import {ref} from 'vue'
import PromoteModal from './PromoteModal.vue'
import gameApi from '@/api/gameApi.js'

const deployed = window.location.hostname === "judelieb.com"
let socket = null

const info = ref({
  wMat: null,
  bMat: null,
  moveCount: null,
  gameStatus: 'Pregame',
  autoQueen: true,
  player: true,
  isMulti: true,
  isLoading: null,
})

const options = ref([])
const board = ref([10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4])
const outlines = ref([])
const isFlipped = ref(false)
const showModal = ref(false)
let lights = []

//Move selection states (mode = start/end, crd = start, box = end)
let mode = true
let crd = null
let box = -1
let sessionId = null
let turn = false
let started = false

function getWebSocket() {
  let newSocket = new WebSocket(deployed ? "wss://api.judelieb.com/ws" : "ws://localhost:5000/ws")

  newSocket.onopen = () => {console.log("Web socket connected.")}
  newSocket.onclose = () => {console.log("Web socket disconnected.")}
  newSocket.onerror = (error) => {console.log("Web socket error:", error)}

  newSocket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if(data.desc === "boardState") {
        if(started === false) {
          info.value.isLoading = null
        }
        if(data.highlights !== "none") {
          outlines.value = []
          lights = data.highlights
        } else {
          lights = []
        }
        resetLights()
        board.value = data.squares
        options.value = data.options
        turn = data.turn
        
        isFlipped.value = (data.flipped === sessionId)
        info.value.gameStatus = data.status
        info.value.moveCount = data.moveCount
        info.value.wMat = data.wMat
        info.value.bMat = data.bMat
      }
      if(data.desc === "sessionId") {
        sessionId = data.sessionId
        gameRequest(sessionId)
      }
      console.log(data)
      //console.log(data)
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

  return newSocket
}

function resetLights() {
  outlines.value[lights[0]] = 'yellow-highlight'
  outlines.value[lights[1]] = 'yellow-highlight'
}

function undo() {
  outlines.value = []
  socket.send(JSON.stringify({ desc: "undo" }))
}

function newGame() {
  if(socket != null && socket != undefined) socket.close()
  socket = getWebSocket()
  mode = true
  options.value = []
  outlines.value = []
  started = false
  info.value.isLoading = true
}

async function gameRequest(sessionId) {
  try {
    let res = "nothing returned"
    if(info.value.isMulti) {
      res = await gameApi.multiplayer(sessionId)
      info.value.gameStatus = res.data
    } else {
      res = await gameApi.singleplayer(sessionId, info.value.player)
    }
    console.log(res)
  } catch(e) {
    console.log('haha', e)
  }
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

function changeGameType() {
  info.value.isMulti = !info.value.isMulti
}

function handleSelect(selected) {
  if(turn !== sessionId) return

  if(mode === true) {
    
    crd = selected
    outlines.value = []
    resetLights()

    outlines.value[selected] = 'green-outline'
    if(turn !== sessionId) return
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

    //Trying to find the move selection in the pre-loaded move list
    if(options.value[crd]) {
      for (let i = 0; i < options.value[crd].length; i++) {
        if(selected === options.value[crd][i]) approved = true
      }
    }

    //Quick auto rejection for illegal move (will be double checked by server)
    if(approved === false) {
      cancelMove()
      resetLights()
      return
    }

    //Determining whether the move is a promotion attempt
    let promote = 0
    const isWhitePawn = board.value[crd] === 1
    const isBlackPawn = board.value[crd] === 7
    const isPromotion = (isWhitePawn && selected < 8) || (isBlackPawn && selected > 55)

    //Handles promotion type in request; otherwise sends normal move
    if(isPromotion) {
      if(info.value.autoQueen === true) {
        promote = isWhitePawn ? 5 : (isBlackPawn ? 11 : 0);
        socket.send(JSON.stringify({desc: "move request", crd: [crd, selected, promote]}))
        mode = true
      } else {
        showModal.value = true
      }
    } else {
      //Clearing outlines and making the move appear
      //outlines.value = []
      board.value[selected] = board.value[crd]
      board.value[crd] = 0
      mode = true
      socket.send(JSON.stringify({desc: "move request", crd: [crd, box, promote]}))
    }
  }
}

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
          @game-type="changeGameType"
          @changeColor="info.player = !info.player">
        </Panel>
        <PromoteModal :showModal @pick="handlePromote" @cancel="cancelMove"></PromoteModal>
      </main>
  </div>
</template>