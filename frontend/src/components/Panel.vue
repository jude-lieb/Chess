<script setup>
import {ref} from 'vue'
const props = defineProps({
    info: {}
})

const isMulti = ref(false)

const emit = defineEmits(['newGame','undo', 'autoQueen', 'changeColor','game-type'])
</script>

<template>
    <ul class="list-group">
        <li class="list-group-item">
            <div class="d-flex justify-content-between align-items-center">
                <label class="form-check-label">
                    <strong>Multiplayer</strong>
                </label>
                <div class="form-check form-switch">
                    <input @click="emit('game-type')" :checked="info.isMulti" class="form-check-input" type="checkbox" role="switch">
                </div>
                
            </div>
        </li>
        <li class="list-group-item">
            <div class="d-flex">
                <strong class="me-1">Material: </strong>
                <span class="me-1">♔</span>
                <span>White</span>
                <span id="whiteMaterial" class="ms-1"> {{info.wMat}}</span>
                <span class="me-1">♚</span>
                <span>Black</span>
                <span id="blackMaterial" class="ms-1"> {{info.bMat}}</span>
            </div>
        </li>

        <li class="list-group-item d-flex justify-content-between">
            <strong>Status:</strong> {{info.gameStatus}}
        </li>

        <li class="list-group-item d-flex">
            <strong>Legal Moves:</strong> {{info.moveCount}}
        </li>

        <li class="list-group-item">
            <div class="d-flex justify-content-between align-items-center">
                <label class="form-check-label">
                    <strong>Auto Queen</strong>
                </label>
                <div class="form-check form-switch">
                    <input @click="emit('autoQueen')" :checked="info.autoQueen" class="form-check-input" type="checkbox" role="switch">
                </div>
            </div>
            <div v-if="!info.isMulti" class="d-flex justify-content-between align-items-center">
                <label class="form-check-label">
                    <strong>Play as black</strong>
                </label>
                <div class="form-check form-switch">
                    <input @click="emit('changeColor')" :checked="!info.player" class="form-check-input" type="checkbox" role="switch">
                </div>
            </div>
        </li>

        <li class="list-group-item">
            <div class="d-flex flex-row gap-3">
                <button @click="emit('newGame')" class="btn btn-danger">🔁 New</button>
                <button v-if="!info.isMulti" @click="emit('undo')" class="btn btn-primary">↩️ Undo</button>
                <span v-if="info.isLoading" class="spinner-border" role="status" aria-hidden="true"></span>
            </div>
        </li>
    </ul>
</template>
