<template>
    <header class="page-header">
        <h1 class="page-title">Your Car Your Way Support Chat</h1>
    </header>

    <div class="chat-wrapper">
        <div class="chat-container">
            <div v-if="conv" class="chat-box">
                <div class="role-toggle">
                    <label>
                        <input type="radio" value="user" v-model="role" />
                        Utilisateur
                    </label>
                    <label>
                        <input type="radio" value="support" v-model="role" />
                        Support
                    </label>
                    <span class="copy-link">
                        <button @click="copyLink" class="copy-btn">
                            Copier le lien de partage
                        </button>
                    </span>
                </div>

                <ul ref="msgList" class="messages">
                    <li v-for="m in conv.messages" :key="m.timestamp"
                        :class="['message', m.sender === 'user' ? 'message-user' : 'message-support']">
                        <div class="message-content">
                            <div class="message-sender">
                                {{ m.sender === 'user' ? 'Vous' : 'Support' }}
                            </div>
                            <p class="message-text">{{ m.content }}</p>
                            <div class="timestamp">{{ new Date(m.timestamp).toLocaleString() }}</div>
                        </div>
                    </li>
                </ul>

                <div class="input-group">
                    <input v-model="draft" placeholder="Écrire un message..." @keyup.enter="send"
                        class="message-input" />
                    <button @click="send" :disabled="!draft" class="send-btn">Envoyer</button>
                </div>
            </div>
            <div v-else class="no-conv">
                <div>
                    <input v-model="joinId" placeholder="ID de conversation" />
                    <button @click="joinConv" :disabled="!joinId || isLoading">Rejoindre</button>
                </div>
                <button class="start-btn" @click="newConv" :disabled="isLoading">
                    <span v-if="isLoading">Création...</span>
                    <span v-else>Démarrer une conversation</span>
                </button>
            </div>
        </div>
    </div>

    <div class="fab" @click="newConv" title="Nouvelle conversation">
        <span class="icon">🧑‍💻</span>
        <span class="badge">?</span>
    </div>
</template>

<script setup>
import api from '../services/api'
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'

const conv = ref(null)
const draft = ref('')
const role = ref('user')
const isLoading = ref(false)
const isSending = ref(false)
const error = ref(null)
const msgList = ref(null)
const updateURL = (id) => {
    const url = new URL(window.location)
    url.searchParams.set('convId', id)
    window.history.replaceState({}, '', url)
}
let poller

const copyLink = () => {
    navigator.clipboard.writeText(window.location.href)
        .then(() => alert('Lien copié !'))
        .catch(() => alert('Impossible de copier le lien'))
}

const joinId = ref('')
const joinConv = async () => {
    isLoading.value = true
    error.value = null
    try {
        const { data } = await api.getConversation(joinId.value)
        conv.value = data
        localStorage.setItem('chatConvId', data.id)
        startPolling(data.id)
        scrollToBottom()
    } catch {
        error.value = "Impossible de rejoindre cette conversation."
    } finally {
        isLoading.value = false
    }
}




const scrollToBottom = () => {
    nextTick(() => {
        const el = msgList.value
        if (el) {
            el.scrollTop = el.scrollHeight
        }
    })
}

watch(
    () => conv.value?.messages?.length,
    () => {
        scrollToBottom()
    }
)

onMounted(async () => {
    const savedId = localStorage.getItem('chatConvId')
    if (savedId) {
        isLoading.value = true
        try {
            const { data } = await api.getConversation(savedId)
            conv.value = data
            startPolling(savedId)
            scrollToBottom()
        } catch { }
        finally { isLoading.value = false }
    }
})

const newConv = async () => {
    isLoading.value = true
    error.value = null
    try {
        const { data } = await api.createConversation()
        conv.value = data
        localStorage.setItem('chatConvId', data.id)
        startPolling(data.id)
        scrollToBottom()
    } catch (e) {
        error.value = "Impossible de créer la conversation."
    } finally {
        isLoading.value = false
    }
}

// Nettoyage au démontage
onUnmounted(() => clearInterval(poller))

// Démarrage / arrêt du polling
function startPolling(convId) {
    updateURL(convId)
    poller = setInterval(async () => {
        const { data } = await api.getConversation(conv.value.id)
        conv.value = data
        scrollToBottom()
    }, 2000)
}

const send = async () => {
    if (!draft.value || !conv.value) return
    isSending.value = true
    error.value = null
    try {
        await api.addMessage(conv.value.id, { sender: role.value, content: draft.value })
        const { data } = await api.getConversation(conv.value.id)
        conv.value = data
        // ← ajoutez ceci
        scrollToBottom()
        draft.value = ''
    } catch (e) {
        error.value = "Échec de l'envoi du message."
    } finally {
        isSending.value = false
    }
}

</script>

<style scoped>
/* Constants */
:root {
    --header-height: 60px;
}

/* Header */
.page-header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: var(--header-height);
    background-color: #004aad;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 100;
}

.page-title {
    color: #fff;
    font-size: 1.5rem;
    margin: 0;
}

/* Wrapper absolute full-space below header */
.chat-wrapper {
    position: absolute;
    top: var(--header-height);
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

/* Chat container sizing mobile-first */
.chat-container {
    width: 90vw;
    max-width: 600px;
    height: 80vh;
    display: flex;
    flex-direction: column;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    margin-bottom: 10vh;
}

.chat-box {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
}

/* Desktop adjustment */
@media (min-width: 768px) {
    .chat-container {
        width: 40vw;
    }
}

.no-conv {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #666;
    text-align: center;
    padding: 1rem;
}

.role-toggle {
    padding: 0.5rem 1rem;
    background: #f9f9f9;
    display: flex;
    gap: 1rem;
}

.messages {
    flex: 1;
    list-style: none;
    padding: 1rem;
    margin: 0;
    overflow-y: auto;
    background: #fafafa;
}

.message {
    max-width: 80%;
    margin-bottom: 1rem;
    display: flex;
}

.message-user {
    justify-content: flex-end;
    margin-left: auto;
}

.message-support {
    justify-content: flex-start;
    margin-right: auto;
}

.message-content {
    background: #e0e0e0;
    padding: 0.75rem;
    border-radius: 6px;
    display: flex;
    flex-direction: column;
}

.message-user .message-content {
    background: #cfe9ff;
}

.message-text {
    margin: 0 0 0.5rem 0;
}

.timestamp {
    font-size: 0.75rem;
    color: #666;
    text-align: right;
}

.input-group {
    display: flex;
    padding: 0.5rem;
    border-top: 1px solid #ddd;
    background: #fff;
}

.message-input {
    flex: 1;
    padding: 0.75rem;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.send-btn {
    margin-left: 0.5rem;
    background: #004aad;
    border: none;
    color: #fff;
    padding: 0 1rem;
    border-radius: 4px;
    cursor: pointer;
}

.send-btn:disabled {
    background: #ccc;
    cursor: not-allowed;
}

/* Floating Action Button */
.fab {
    position: fixed;
    bottom: 20px;
    right: 20px;
    width: 60px;
    height: 60px;
    background-color: #004aad;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    cursor: pointer;
    animation: pulse 2s infinite;
    z-index: 101;
}

.fab .icon {
    font-size: 1.5rem;
}

.fab .badge {
    position: absolute;
    top: 8px;
    right: 8px;
    background: red;
    color: #fff;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
}

.message-sender {
    font-weight: 500;
}

.copy-link {
    padding: 0.5rem 1rem;
    text-align: right;
}

.copy-btn {
    background: #004aad;
    color: #fff;
    border: none;
    padding: 0.25rem 0.75rem;
    border-radius: 4px;
    cursor: pointer;
}

.copy-btn:hover {
    background: #003580;
}

@keyframes pulse {
    0% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.1);
    }

    100% {
        transform: scale(1);
    }
}
</style>
