const express = require("express");
const fs = require("fs");
const path = require("path");
const WebSocket = require("ws");
const https = require("https");
const http = require("http");

const app = express();
//const port = 443;
const port = 3002;
const BACKEND_WS_URL = "ws://localhost:3000";

// const privateKey = fs.readFileSync(
//   "/etc/letsencrypt/live/judelieb.com/privkey.pem",
//   "utf8"
// );
// const certificate = fs.readFileSync(
//   "/etc/letsencrypt/live/judelieb.com/fullchain.pem",
//   "utf8"
// );
// const credentials = { key: privateKey, cert: certificate };

app.use(express.static(path.join(__dirname, "public")));

const server = http.createServer(app);
//const server = https.createServer(credentials, app);

server.listen(port, () => {
  console.log(`Live at https://localhost:${port}`);
});

const wss = new WebSocket.Server({ server });

wss.on("connection", (clientSocket) => {
  console.log("Browser client connected.");

  // Connect to Java backend
  const backendSocket = new WebSocket(BACKEND_WS_URL);
  const queuedMessages = [];

  clientSocket.on("message", (message) => {
    const msgStr = message.toString();
    if (backendSocket.readyState === WebSocket.OPEN) {
      backendSocket.send(msgStr);
    } else {
      queuedMessages.push(msgStr);
    }
  });

  backendSocket.on("message", (message) => {
    const msgStr = message.toString();
    //console.log("From backend to client:", msgStr);

    if (clientSocket.readyState === WebSocket.OPEN) {
      clientSocket.send(msgStr);
    }
  });

  backendSocket.on("open", () => {
    console.log("Connected to Java backend.");
    for (const msg of queuedMessages) {
      backendSocket.send(msg);
    }
    queuedMessages.length = 0;
  });

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
