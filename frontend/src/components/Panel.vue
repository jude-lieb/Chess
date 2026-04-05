<script setup>
const props = defineProps({
    info: {}
})

const emit = defineEmits(['newGame','undo', 'autoQueen'])
</script>

<template>
    <ul class="list-group">
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

        <li class="list-group-item justify-content-between">
            <strong>Status:</strong> {{info.gameStatus}}
        </li>

        <li class="list-group-item">
            <strong>Legal Moves:</strong> {{info.moveCount}}
        </li>

        <li class="list-group-item">
            <div class="d-flex justify-content-between align-items-center">
                <label class="form-check-label" for="autoqueenSwitch">
                    <strong>Auto Queen</strong>
                </label>
                <div class="form-check form-switch">
                    <input @click="emit('autoQueen')" :checked="info.autoQueen" class="form-check-input" type="checkbox" role="switch">
                </div>
            </div>
        </li>

        <li class="list-group-item">
            <div class="d-flex flex-row gap-3 mt-2">
                <button @click="emit('newGame')" class="btn btn-danger">🔁 New</button>
                <button @click="emit('undo')" class="btn btn-primary">↩️ Undo</button>
            </div>
        </li>
    </ul>
</template>
