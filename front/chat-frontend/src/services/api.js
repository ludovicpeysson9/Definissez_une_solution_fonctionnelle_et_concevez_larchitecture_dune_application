import axios from "axios";

const api = axios.create({
    baseURL: '/api',      
    timeout: 5000,
})

export default {
    createConversation: () => api.post('/conversations'),
    getConversation: (id) => api.get(`/conversations/${id}`),
    addMessage: (id, msg) => api.post(`/conversations/${id}/messages`, msg),
}