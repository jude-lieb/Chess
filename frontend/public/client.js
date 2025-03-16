const socket = new WebSocket("ws://localhost:3000")

socket.onopen = () => {
    console.log("Connected to Java WebSocket!")
    //socket.send("Test Move")
};

socket.onmessage = (event) => {
    console.log("Server says:", event.data)
}

socket.onerror = (error) => {
    console.log("WebSocket Error:", error)
}

socket.onclose = () => {
    console.log("WebSocket connection closed.")
}

const grid = document.querySelector('#board');

for (let i = 0; i < 64; i++) {
    const img = document.createElement('img');
    img.src = "/images/blank.jpg";
    img.className = "grid-item";
    img.row = Math.floor(i / 8)
    img.col = i % 8
    img.onclick = () => {
        socket.send("sup" + img.row + " " + img.col)
    }
    grid.appendChild(img);
}

