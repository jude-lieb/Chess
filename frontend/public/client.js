const socket = new WebSocket("wss://judelieb.com");
const grid = document.querySelector("#board");

const images = [
  "blank.jpg",
  "wp.png",
  "wn.png",
  "wb.png",
  "wr.png",
  "wq.png",
  "wk.png",
  "bp.png",
  "bn.png",
  "bb.png",
  "br.png",
  "bq.png",
  "bk.png",
];

socket.onopen = () => {
  console.log("Connected to Java WebSocket!");
};

socket.onmessage = (event) => {
  try {
    const jsonData = JSON.parse(event.data);
    console.log("Received JSON:", jsonData);
    handleJSON(jsonData);
  } catch (error) {
    console.log("Error handling server input");
    console.log(event);
  }
};

socket.onerror = (error) => {
  console.log("WebSocket Error:", error);
};

socket.onclose = () => {
  console.log("WebSocket connection closed.");
};

function undo() {
  socket.send(JSON.stringify({ desc: "undo" }));
}

function promote() {
  socket.send(JSON.stringify({ desc: "promote" }));
}

function reset() {
  socket.send(JSON.stringify({ desc: "reset" }));
}

function handleJSON(data) {
  if (data.desc === "boardState") {
    loadBoard(data.squares);
  }
  if (data.desc === "select") {
    grid.children[8 * data.square[0] + data.square[1]].classList.add(
      "red-outline"
    );
  }
  if (data.desc === "deselect") {
    grid.children[8 * data.square[0] + data.square[1]].classList.remove(
      "red-outline"
    );
  }
  if (data.desc === "text") {
    console.log(data.info);
  }
  if (data.desc === "promote") {
    document.getElementById(
      "promoteBtn"
    ).innerHTML = `Promote Toggle ${data.value}`;
  }
}

function loadBoard(set) {
  for (let i = 0; i < 64; i++) {
    grid.children[i].src = "/images/" + images[set[i]];
  }
}

for (let i = 0; i < 64; i++) {
  const img = document.createElement("img");
  img.src = "/images/blank.jpg";
  img.className = "grid-item";
  img.row = Math.floor(i / 8);
  img.col = i % 8;
  img.onclick = () => {
    socket.send(
      JSON.stringify({ desc: "coordinate", crd: [img.row, img.col] })
    );
  };
  grid.appendChild(img);
}
