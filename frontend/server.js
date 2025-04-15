const express = require("express");
const path = require("path");
const WebSocket = require("ws");
const http = require("http");

const app = express();
const port = 3002;
const BACKEND_WS_URL = "ws://localhost:3000"; // Your Java server

// Serve static files from public/
app.use(express.static(path.join(__dirname, "public")));

//const privateKey = fs.readFileSync('/etc/letsencrypt/live/judelieb.com/privkey.pem', 'utf8');
//const certificate = fs.readFileSync('/etc/letsencrypt/live/judelieb.com/fullchain.pem', 'utf8');
//const credentials = { key: privateKey, cert: certificate };

// Create and start HTTP server
const server = http.createServer(app);

server.listen(port, () => {
  console.log(`Live at http://localhost:${port}`);
});

// Attach WebSocket server to HTTP server
const wss = new WebSocket.Server({ server });

wss.on("connection", (clientSocket) => {
  console.log("Browser client connected.");

  // Connect to Java backend
  const backendSocket = new WebSocket(BACKEND_WS_URL);

  // Optional: queue messages while backend is connecting
  const queuedMessages = [];

  // === FORWARD: client ➡️ backend ===
  clientSocket.on("message", (message) => {
    const msgStr = message.toString();
    if (backendSocket.readyState === WebSocket.OPEN) {
      backendSocket.send(msgStr);
    } else {
      queuedMessages.push(msgStr);
    }
  });

  // === FORWARD: backend client ===
  backendSocket.on("message", (message) => {
    const msgStr = message.toString();
    console.log("From backend to client:", msgStr);

    if (clientSocket.readyState === WebSocket.OPEN) {
      clientSocket.send(msgStr);
    }
  });

  // Once backend is ready, flush queued messages
  backendSocket.on("open", () => {
    console.log("Connected to Java backend.");
    for (const msg of queuedMessages) {
      backendSocket.send(msg);
    }
    queuedMessages.length = 0;
  });

  // === HANDLE CLOSING ===
  clientSocket.on("close", () => {
    console.log("Browser client disconnected.");
    backendSocket.close();
  });

  backendSocket.on("close", () => {
    console.log("Java backend disconnected.");
    if (clientSocket.readyState === WebSocket.OPEN) {
      clientSocket.close();
    }
  });

  // === ERROR HANDLING ===
  clientSocket.on("error", (err) => {
    console.error("Client error:", err);
  });

  backendSocket.on("error", (err) => {
    console.error("Backend error:", err);
    if (clientSocket.readyState === WebSocket.OPEN) {
      clientSocket.send(
        JSON.stringify({
          desc: "error",
          info: "Failed to connect to backend WebSocket.",
        })
      );
    }
  });
});
