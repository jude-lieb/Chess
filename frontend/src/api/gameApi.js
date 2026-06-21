import axios from "axios"

let source = null

if(window.location.hostname === "judelieb.com") {
    source = "https://api.judelieb.com/"
} else {
    source = "http://localhost:5000/"
}

const api = axios.create({
    baseURL: source
})

export default {
    async multiplayer(socketId) {
        const res = await api.get("/new/multiplayer", {
            params: {
                sessionId: socketId,
            }
        })
        //console.log(res)  
        return res
    },
    async singleplayer(socketId, color) {
        console.log("color", color)
        const res = await api.get("/new/singleplayer", {
            params: {
                sessionId: socketId,
                color: color
            }
        })
        //console.log(res)  
        return res
    },
}