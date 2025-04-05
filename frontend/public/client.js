const socket = new WebSocket("ws://localhost:3000")
let game = "new"
const grid = document.querySelector('#board');

const images = ["blank.jpg", "wp.png", "wn.png","wb.png","wr.png","wq.png",
    "wk.png", "bp.png","bn.png","bb.png","br.png","bq.png", "bk.png"]

socket.onopen = () => {
    console.log("Connected to Java WebSocket!")
    //socket.send("Test Move")
};

socket.onmessage = (event) => {
    try {
        const jsonData = JSON.parse(event.data);
        //console.log("Received JSON:", jsonData);
        handleJSON(jsonData)
    } catch (error) {
        console.log("Error handling server input");
    }
}

socket.onerror = (error) => {
    console.log("WebSocket Error:", error)
}

socket.onclose = () => {
    console.log("WebSocket connection closed.")
}

function handleJSON(data) {
    if(data.desc === "boardState" && game === "new") {
        loadBoard(data.squares)
    }
    if(data.desc === "select") {
        grid.children[(8 * data.square[0]) + data.square[1]].classList.add('red-outline')
    }
    if(data.desc === "deselect") {
        grid.children[(8 * data.square[0]) + data.square[1]].classList.remove('red-outline')
    }
    if(data.desc === "text") {
        console.log(data.info)
    }
}

function loadBoard(set) {
    for(let i = 0; i < 64; i++) {
        grid.children[i].src = "/images/" + images[set[i]];
    }
}

for (let i = 0; i < 64; i++) {
    const img = document.createElement('img');
    img.src = "/images/blank.jpg";
    img.className = "grid-item";
    img.row = Math.floor(i / 8)
    img.col = i % 8
    img.onclick = () => {
        socket.send(JSON.stringify({desc: "coordinate" , crd: [img.row, img.col]}));
    }
    grid.appendChild(img);
}


