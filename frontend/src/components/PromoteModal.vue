<script setup>
const props = defineProps({
    showModal: Boolean
})
const emit = defineEmits(['pick', 'cancel'])
</script>

<template>
    <div v-if="showModal" class="modal show d-block">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Promotion</h5>
                    <button @click="emit('cancel')" type="button" class="btn-close"></button>
                </div>

                <div class="modal-body">
                    <p>Select promotion piece</p>
                </div>

                <div class="modal-footer">
                    <button @click="emit('pick', 5)" type="button" class="btn btn-primary">Queen</button>
                    <button @click="emit('pick', 4)" type="button" class="btn btn-primary">Rook</button>
                    <button @click="emit('pick', 3)" type="button" class="btn btn-primary">Bishop</button>
                    <button @click="emit('pick', 2)" type="button" class="btn btn-primary">Knight</button>
                </div>
            </div>
        </div>
    </div>
</template>