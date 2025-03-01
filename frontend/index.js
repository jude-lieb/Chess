WebSocket = require("ws")
const socket = new WebSocket("ws://localhost:3000");

socket.onopen = () => {
    console.log("Connected to Java WebSocket!");
    socket.send("Player moved to E5");
};

socket.onmessage = (event) => {
    console.log("Server says:", event.data);
};