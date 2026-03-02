<script setup>
const props = defineProps({
    info: {}
})

const emit = defineEmits(['reset','undo', 'flip'])
</script>

<template>
    <ul class="list-group">
        <li class="list-group-item">
            <strong class="mb-1 d-block">Material Count:</strong>
            <div class="d-flex justify-content-between">
                <div class="d-flex align-items-center">
                    <span class="me-1">♔</span>
                    <span>White:</span>
                    <span id="whiteMaterial" class="ms-1">{{info.wMat}}</span>
                </div>
                <div class="d-flex align-items-center">
                    <span class="me-1">♚</span>
                    <span>Black:</span>
                    <span id="blackMaterial" class="ms-1">{{info.bMat}}</span>
                </div>
            </div>
        </li>

        <li class="list-group-item justify-content-between">
            <strong>Status:</strong>
            <span id="gameStatus">{{info.gameStatus}}</span>
        </li>

        <li class="list-group-item">
            <strong>Legal Moves:</strong>
            <span id="legalMoveCount">{{info.moveCount}}</span>
        </li>

        <li class="list-group-item">
            <div class="d-flex justify-content-between align-items-center">
                <label class="form-check-label" for="autoqueenSwitch">
                    <strong>Auto Queen</strong>
                </label>
                <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" role="switch" id="autoQueenSwitch" checked>
                </div>
            </div>
        </li>

        <li class="list-group-item">

            <div class="d-flex flex-row gap-3 mt-2 justify-content-end">
                    <div style="
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    border: 2px solid #333; 
                    border-radius: 100%; 
                    padding: 2px; ">   
                    <strong>C_v2.4</strong>
            </div>
                <button @click="emit('flip')" class="btn btn-outline-primary">Flip Board</button>
                <button @click="emit('reset')" class="btn btn-danger">🔁 Reset</button>
                <button @click="emit('undo')" class="btn btn-outline-primary">↩️ Undo</button>
            </div>
        </li>
    </ul>
</template>
