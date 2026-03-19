import axios from "axios"

const api = axios.create({
    baseURL: 'http://localhost:5000'
})

export default {
    async newGame() {
        const response = await api.get("/new")
        localStorage.setItem('gameId', response.data)
        return response
    },
    async undoMove() {
        let gameId = localStorage.getItem('gameId')
        const response = await api.get(`/undoMove/${gameId}`)
        return response
    },
    async getBoard() {
        let gameId = localStorage.getItem('gameId')
        const response = await api.get(`/board/${gameId}`)
        return response
    },
    async move(move) {
        let gameId = localStorage.getItem('gameId')

        console.log("sending", move)
        return await api.post(`/move/${gameId}`, move, {
            headers: {
                "Content-Type": "application/json"
        }})
    }
}