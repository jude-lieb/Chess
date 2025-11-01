const deployed = window.location.hostname === "judelieb.com";
const socket = new WebSocket(deployed ? "wss://judelieb.com" : "ws://localhost");
const grid = document.querySelector("#board")

const turnDisplay = document.getElementById('turnDisplay')
const wMat = document.getElementById('whiteMaterial')
const bMat = document.getElementById('blackMaterial')
const gameStatus = document.getElementById('gameStatus')
const moveCount = document.getElementById('legalMoveCount')
const autoQueen = document.getElementById('autoQueenSwitch')

let options = []
let board = []
let mode = true
// let x = 0
// let y = 0

const images = [
  "blank.jpg","wp.png","wn.png","wb.png","wr.png","wq.png","wk.png",
  "bp.png","bn.png","bb.png","br.png","bq.png","bk.png",
]

function handleJSON(data) {
  if(data.desc === "boardState") {
    board = data.squares;
    //Loading board images
    for(let i = 0; i < 64; i++) {
      const img = grid.children[i].querySelector("img");
      img.src = "/images/" + images[board[i]];
    }

    options = data.options
    gameStatus.innerText = data.status
    turnDisplay.innerText = data.turn
    wMat.innerText = data.wMat
    bMat.innerText = data.bMat
    moveCount.innerText = data.moveCount
  }
}

//Generating board
for(let i = 0; i < 64; i++) {
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
  clearOutlines()
}

function clearOutlines() {
  for(square of grid.children) {
    square.classList.remove("red-outline")
    square.classList.remove("green-outline")
  }
}

function reset() {
  mode = true
  options = []
  socket.send(JSON.stringify({ desc: "reset" }))
  clearOutlines()
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
    const crd = y * 8 + x
    approved = false

    grid.children[crd].classList.remove("green-outline");
    if(options[crd]) {
      for (let i = 0; i < options[crd].length; i++) {
        grid.children[options[crd][i]].classList.remove("red-outline");
        if(clickedIndex === options[crd][i]) approved = true
      }
    }

    if(!approved) {
      mode = true
      return
    }

    let promote = 0;
    const fromImg = grid.children[crd].querySelector("img");
    const toImg = grid.children[clickedIndex].querySelector("img");
    const fromSrc = fromImg.src;

    // Decide if this is a promotion move
    const isWhitePawn = board[crd] === 1;
    const isBlackPawn = board[crd] === 7;
    const isPromotion = (isWhitePawn && clickedIndex < 8) || (isBlackPawn && clickedIndex > 55);

    toImg.src = fromSrc;
    fromImg.src = "/images/blank.jpg";

    if(isPromotion) {
      if(autoQueen.checked) {
        promote = isWhitePawn ? 5 : (isBlackPawn ? 11 : 0);
        mode = true
        socket.send(JSON.stringify({desc: "move request", crd: [y, x, row, col, promote]}))
        return
      }
      
      const modalEl = document.getElementById('promotionModal');
      const modal = new bootstrap.Modal(modalEl);
      
      // Store the destination coordinates in variables that won't change
      const destRow = row;
      const destCol = col;
      const destIndex = clickedIndex;
      
      let choiceMade = false;

      // Clear any existing event listeners
      const buttons = Array.from(modalEl.querySelectorAll('.promo-btn'));
      buttons.forEach(btn => {
        // Remove any existing click listeners
        btn.replaceWith(btn.cloneNode(true));
      });

      // Get fresh references after cloning
      const freshButtons = Array.from(modalEl.querySelectorAll('.promo-btn'));
      freshButtons.forEach(b => b.disabled = false);

      const handleModalClose = () => {
        if (!choiceMade) {
          console.log("Modal closed without selection, using default");
          mode = true;
          // Use the stored destination coordinates
          socket.send(JSON.stringify({desc: "move request", crd: [y, x, destRow, destCol, 0]}))
        }
        modalEl.removeEventListener('hidden.bs.modal', handleModalClose);
        // Clean up button event listeners
        freshButtons.forEach(btn => {
          btn.onclick = null;
        });
      };

      modalEl.addEventListener('hidden.bs.modal', handleModalClose);

      freshButtons.forEach(btn => {
        btn.onclick = (e) => {
          choiceMade = true;
          freshButtons.forEach(b => b.disabled = true);

          const piece = btn.dataset.piece;
          let promote = 0;
          
          if(isWhitePawn) {
            if (piece === 'q') promote = 5;
            else if (piece === 'r') promote = 4;
            else if (piece === 'b') promote = 3;
            else if (piece === 'n') promote = 2;
          } else if (isBlackPawn) {
            if (piece === 'q') promote = 11;
            else if (piece === 'r') promote = 10;
            else if (piece === 'b') promote = 9;
            else if (piece === 'n') promote = 8;
          }

          modalEl.removeEventListener('hidden.bs.modal', handleModalClose);
          modal.hide();

          mode = true;
          // Use the stored destination coordinates
          socket.send(JSON.stringify({desc: "move request", crd: [y, x, destRow, destCol, promote]}))
        };
      });
      
      modal.show();
      return;
    } else {
      mode = true;
      socket.send(JSON.stringify({desc: "move request", crd: [y, x, row, col, promote]}))
    } 
  }
}
