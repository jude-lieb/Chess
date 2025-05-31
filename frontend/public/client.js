const deployed = window.location.hostname === "judelieb.com";
const socket = new WebSocket(deployed ? "wss://judelieb.com" : "ws://localhost");
const grid = document.querySelector("#board")
const display = document.getElementById('messageBox')
const promoteBtn =  document.getElementById("promoteBtn")

let options = []
let mode = true
let promoteType = 5
let x = 0
let y = 0

const images = [
  "blank.jpg","wp.png","wn.png","wb.png","wr.png","wq.png","wk.png",
  "bp.png","bn.png","bb.png","br.png","bq.png","bk.png",
]

for (let i = 0; i < 64; i++) {
  const cell = document.createElement("div");
  cell.className = "grid-item";
  cell.row = Math.floor(i / 8);
  cell.col = i % 8;
  cell.onclick = () => handleClick(cell.row, cell.col);

  const img = document.createElement("img");
  img.src = "/images/blank.jpg";
  img.draggable = false;

  cell.appendChild(img);
  grid.appendChild(cell);
}

socket.onopen = () => {
  console.log("Connected to Java WebSocket!");
}

socket.onmessage = (event) => {
  try {
    const jsonData = JSON.parse(event.data)
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
  if(promoteType < 5) {
    promoteType++;
  } else {
    promoteType = 2;
  }
  promoteBtn.innerHTML = `Promote Toggle ${promoteType}`
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
    promoteType = data.value
    promoteBtn.innerHTML = `Promote Toggle ${data.value}`
  }

  if(data.desc === "loadSelect") {
    //console.log(data.options)
    options = data.options
  }
}

function loadBoard(set) {
  for (let i = 0; i < 64; i++) {
    const img = grid.children[i].querySelector("img");
    img.src = "/images/" + images[set[i]];
  }
}


function handleClick(row, col) {
  const clickedIndex = row * 8 + col;

  if (mode === true) {
    y = row;
    x = col;
    const init = clickedIndex;
    grid.children[init].classList.add("green-outline");

    if (options[init] !== undefined && options[init] !== null) {
      for (let i = 0; i < options[init].length; i++) {
        grid.children[options[init][i]].classList.add("red-outline");
      }
    }
    mode = false;
  } else {
    const crd = y * 8 + x;
    grid.children[crd].classList.remove("green-outline");

    if (options[crd] !== undefined && options[crd] !== null) {
      for (let i = 0; i < options[crd].length; i++) {
        const targetIndex = options[crd][i];
        const targetDiv = grid.children[targetIndex];
        const fromImg = grid.children[crd].querySelector("img");
        const toImg = targetDiv.querySelector("img");

        if (targetIndex === clickedIndex) {
          toImg.src = fromImg.src;
          fromImg.src = "/images/blank.jpg";
        }
        targetDiv.classList.remove("red-outline");
      }
    }

    mode = true;
    socket.send(JSON.stringify({desc: "move request", crd: [y, x, row, col],}));
  }
}

