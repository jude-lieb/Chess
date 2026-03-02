import express from "express"
import WebSocket, { WebSocketServer } from "ws"
import https from "https"
import http from "http"
import fs from "fs"
import dotenv from "dotenv"

dotenv.config()

const deployed = process.env.DEPLOYED === "true"
const app = express()
const port = deployed ? 443 : 80
const BACKEND_WS_URL = "ws://localhost:3000"

const privateKey = deployed ? fs.readFileSync("/home/jude/certs/origin.key", "utf8") : null
const certificate = deployed ? fs.readFileSync("/home/jude/certs/origin.crt", "utf8"): null

const credentials = deployed ? { key: privateKey, cert: certificate } : null

const server = deployed ? https.createServer(credentials, app) : http.createServer(app)

server.listen(port, () => {
  console.log(
    deployed
      ? `Live at https://localhost:${port}`
      : `Live at http://localhost:${port}`
  )
})

const wss = new WebSocketServer({ server })

wss.on("connection", (clientSocket) => {
  const backendSocket = new WebSocket(BACKEND_WS_URL)
  const queuedMessages = []

  // Forward client → backend
  clientSocket.on("message", (message) => {
    const msgStr = message.toString()

    if (backendSocket.readyState === WebSocket.OPEN) {
      backendSocket.send(msgStr)
    } else {
      queuedMessages.push(msgStr)
    }
  })

  // Forward backend → client
  backendSocket.on("message", (message) => {
    const msgStr = message.toString()
    if (!deployed) console.log("Backend message:", msgStr)
    if (clientSocket.readyState === WebSocket.OPEN) {clientSocket.send(msgStr)}
  })

  // Flush queue when backend connects
  backendSocket.on("open", () => {
    for (const msg of queuedMessages) {
      backendSocket.send(msg)
    }
    queuedMessages.length = 0
  })

  clientSocket.on("close", () => { backendSocket.close() })

  backendSocket.on("close", () => {
    if (clientSocket.readyState === WebSocket.OPEN) clientSocket.close()
  })

  clientSocket.on("error", (err) => {
    console.error("Client error:", err)
  })

  backendSocket.on("error", (err) => {
    console.error("Backend error:", err)

    if (clientSocket.readyState === WebSocket.OPEN) {
      clientSocket.send(
        JSON.stringify({
          desc: "error",
          info: "Failed to connect to backend WebSocket.",
        })
      )
    }
  })
})