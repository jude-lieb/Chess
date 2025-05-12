//const socket = new WebSocket("wss://judelieb.com");
const socket = new WebSocket("ws://localhost:3002")
const grid = document.querySelector("#board")
const display = document.getElementById('messageBox')

let options = []
let mode = true
let x = 0
let y = 0

const images = [
  "blank.jpg","wp.png","wn.png","wb.png","wr.png","wq.png","wk.png",
  "bp.png","bn.png","bb.png","br.png","bq.png","bk.png",
]

socket.onopen = () => {
  console.log("Connected to Java WebSocket!");
}

socket.onmessage = (event) => {
  try {
    const jsonData = JSON.parse(event.data)
    //console.log("Received JSON:", jsonData);
    handleJSON(jsonData)
  } catch (error) {
    console.log("Error handling server input")
    console.log(error)
  }
}

socket.onerror = (error) => {
  console.log("WebSocket Error:", error)
}

socket.onclose = () => {
  console.log("WebSocket connection closed.")
}

//Preventing websocket closure
setInterval(() => {
    if (socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({ desc: "ping" }))
    }
}, 60000)

function undo() {
  socket.send(JSON.stringify({ desc: "undo" }))
}

function promote() {
  socket.send(JSON.stringify({ desc: "promote" }))
}

function reset() {
  display.innerHTML = " "
  mode = true
  options = []
  socket.send(JSON.stringify({ desc: "reset" }))
  for(square of grid.children) {
    square.classList.remove("red-outline")
    square.classList.remove("green-outline")
  }
}

function handleJSON(data) {
  if(data.desc === "boardState") {
    loadBoard(data.squares)
  }

  if(data.desc === "status") {
    display.innerHTML = data.status
  }

  if(data.desc === "promote") {
    document.getElementById(
      "promoteBtn"
    ).innerHTML = `Promote Toggle ${data.value}`
  }

  if(data.desc === "loadSelect") {
    //console.log(data.options)
    options = data.options
  }
}

function loadBoard(set) {
  for (let i = 0; i < 64; i++) {
    grid.children[i].src = "/images/" + images[set[i]]
  }
}

for (let i = 0; i < 64; i++) {
  const img = document.createElement("img")
  img.src = "/images/blank.jpg"
  img.className = "grid-item"
  img.draggable = false
  img.row = Math.floor(i / 8)
  img.col = i % 8
  img.onclick = () => {
    handleClick(img.row, img.col)
  }
  grid.appendChild(img)
}


function handleClick(row, col) {
  if(mode == true) {
    y = row
    x = col
    let init = (row * 8) + col
    grid.children[init].classList.add("green-outline")
    if(options[init] != undefined && options[init] != null) {
      for(let i = 0; i < options[init].length; i++) {
        grid.children[options[init][i]].classList.add("red-outline")
      } 
    }
    mode = false
  } else {
    let crd = (y * 8) + x
    grid.children[crd].classList.remove("green-outline")
    if(options[crd] != undefined && options[crd] != null) {
      for(let i = 0; i < options[crd].length; i++) {
        grid.children[options[crd][i]].classList.remove("red-outline")
      } 
    }
    mode = true
    socket.send(JSON.stringify({ desc: "move", crd: [y, x, row, col] }))
  }
}
