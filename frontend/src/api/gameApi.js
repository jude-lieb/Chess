import axios from "axios"

const api = axios.create({
    baseURL: 'http://localhost:5000/'
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