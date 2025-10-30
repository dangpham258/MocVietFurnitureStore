// ========================================
// MANAGER CHAT INTERFACE
// WebSocket Client cho Manager
// ========================================

class ManagerChat {
    constructor() {
        this.stompClient = null;
        this.isConnected = false;
        this.currentConversationId = null;
        this.conversations = [];
        this.currentSubscription = null; // subscription cho conversation hiện tại
        this._globalSubscription = null; // subscription kênh chung
        this._refreshTimer = null; // timer auto refresh danh sách
        this._connecting = false; // tránh connect lặp
        this.init();
    }

    init() {
        console.log('[ManagerChat] init');
        this.connect();
        this.loadConversations();
        this.setupEventListeners();
        this.startAutoRefresh();
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Nút gửi tin nhắn
        const sendBtn = document.getElementById('manager-chat-send');
        if (sendBtn) {
            sendBtn.addEventListener('click', () => this.sendMessage());
        }

        // Input message - gửi khi nhấn Enter
        const messageInput = document.getElementById('manager-chat-input');
        if (messageInput) {
            messageInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            });
        }

        // Nút đóng conversation
        const closeBtn = document.getElementById('manager-chat-close-conversation');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeConversation());
        }
    }

    /**
     * Kết nối WebSocket
     */
    connect() {
        if (this.isConnected || this._connecting) {
            return;
        }
        this._connecting = true;

        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = null;

        this.stompClient.connect({}, 
            (frame) => {
                console.log('Manager WebSocket Connected: ' + frame);
                this.isConnected = true;
                this._connecting = false;
                this.updateConnectionStatus(true);
                if (window.showNotification) {
                    window.showNotification('Đã kết nối hệ thống chat', 'success', 3000);
                }
                // Kết nối xong, làm mới danh sách hội thoại
                this.loadConversations();

                // Subscribe kênh chung để nhận event tin nhắn mới và cập nhật danh sách ngay lập tức
                if (this._globalSubscription) { try { this._globalSubscription.unsubscribe(); } catch (e) {} }
                this._globalSubscription = this.stompClient.subscribe('/topic/manager/conversations', (message) => {
                    try {
                        const chatMessage = JSON.parse(message.body);
                        this.updateConversationsOnNewMessage(chatMessage);
                    } catch (e) {
                    this.loadConversations();
                    }
                });

                // Nếu đang xem một conversation, resubscribe vào kênh của conversation đó
                if (this.currentConversationId) {
                    this.subscribeToConversation(this.currentConversationId);
                }
            },
            (error) => {
                console.error('Manager WebSocket Connection Error:', error);
                this._connecting = false;
                this.updateConnectionStatus(false);
                setTimeout(() => this.connect(), 5000);
            }
        );
    }

    /**
     * Cập nhật trạng thái kết nối
     */
    updateConnectionStatus(connected) {
        const statusElement = document.getElementById('manager-chat-status');
        if (statusElement) {
            statusElement.textContent = connected ? 'Đã kết nối' : 'Mất kết nối';
            statusElement.className = connected ? 'text-success' : 'text-danger';
        }

        // Bật/tắt input gửi theo trạng thái kết nối và đã chọn conversation hay chưa
        const sendBtn = document.getElementById('manager-chat-send');
        const inputEl = document.getElementById('manager-chat-input');
        const canSend = connected && !!this.currentConversationId;
        if (sendBtn) sendBtn.disabled = !canSend;
        if (inputEl) inputEl.disabled = !canSend;
    }

    /**
     * Load danh sách conversations
     */
    async loadConversations() {
        try {
            const response = await fetch('/manager/messages/api/list');
            if (!response.ok) {
                throw new Error('Failed to load conversations: ' + response.status);
            }
            this.conversations = await response.json();
            this.renderConversations();
        } catch (error) {
            console.error('Error loading conversations:', error);
            const container = document.getElementById('manager-conversations-list');
            if (container) {
                container.innerHTML = '<div class="text-center text-danger p-3">Không tải được danh sách. Thử lại sau.</div>';
            }
            if (window.showNotification) {
                window.showNotification('Không thể tải danh sách cuộc hội thoại (' + (error?.message || 'Lỗi không xác định') + ')', 'danger', 4000);
            }
        }
    }

    /**
     * Render danh sách conversations
     */
    renderConversations() {
        const container = document.getElementById('manager-conversations-list');
        if (!container) {
            return;
        }

        if (this.conversations.length === 0) {
            container.innerHTML = '<div class="text-center text-muted p-3">Chưa có cuộc hội thoại nào</div>';
            return;
        }

        container.innerHTML = this.conversations.map(conv => {
            const timeToShow = conv.lastMessage && conv.lastMessage.createdAt ? conv.lastMessage.createdAt : conv.createdAt;
            const preview = conv.lastMessage ? (conv.lastMessage.content || 'Đã gửi ảnh') : 'Chưa có tin nhắn';
            return `
            <div class="list-group-item list-group-item-action cursor-pointer ${conv.id === this.currentConversationId ? 'active' : ''}"
                 onclick="window.managerChat.selectConversation(${conv.id})">
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">${conv.userName || conv.guestName || 'Khách hàng'}</h6>
                    <small>${this.formatTime(timeToShow)}</small>
                </div>
                <p class="mb-1 text-muted small">${preview}</p>
                ${conv.unreadCount > 0 ? `<span class="badge bg-danger">${conv.unreadCount}</span>` : ''}
            </div>
        `}).join('');
    }

    /**
     * Chọn conversation để xem
     */
    async selectConversation(conversationId) {
        this.currentConversationId = conversationId;
        await this.loadMessages(conversationId);
        this.subscribeToConversation(conversationId);
        this.renderConversations(); // Re-render để highlight active

        // Sau khi chọn conversation, nếu đã kết nối thì cho phép gửi
        const sendBtn = document.getElementById('manager-chat-send');
        const inputEl = document.getElementById('manager-chat-input');
        const canSend = this.isConnected && !!this.currentConversationId;
        if (sendBtn) sendBtn.disabled = !canSend;
        if (inputEl) inputEl.disabled = !canSend;
    }

    /**
     * Load tin nhắn của conversation
     */
    async loadMessages(conversationId) {
        try {
            const response = await fetch(`/manager/messages/api/${conversationId}/messages`);
            if (!response.ok) {
                throw new Error('Failed to load messages');
            }
            const messages = await response.json();
            this.renderMessages(messages);
        } catch (error) {
            console.error('Error loading messages:', error);
        }
    }

    /**
     * Render tin nhắn
     */
    renderMessages(messages) {
        const container = document.getElementById('manager-chat-messages');
        if (!container) {
            return;
        }

        if (messages.length === 0) {
            container.innerHTML = '<div class="text-center text-muted p-3">Chưa có tin nhắn nào</div>';
            return;
        }

        container.innerHTML = messages.map(msg => this.renderMessage(msg)).join('');
        
        // Scroll to bottom
        container.scrollTop = container.scrollHeight;
    }

    /**
     * Render một tin nhắn
     */
    renderMessage(message) {
        const isManager = message.senderType === 'MANAGER';
        const alignClass = isManager ? 'text-end' : '';
        const bubbleClass = isManager ? 'bg-primary text-white' : 'bg-light';

        return `
            <div class="mb-3 ${alignClass}">
                <div class="d-inline-block p-2 rounded ${bubbleClass}" style="max-width: 70%;">
                    <div class="fw-bold small">Tên: ${message.senderName || 'Khách hàng'}</div>
                    <div>${message.content || ''}</div>
                    ${message.attachmentUrl ? `<img src="${message.attachmentUrl}" class="img-thumbnail mt-2" style="max-width: 200px;">` : ''}
                    <div class="small mt-1 opacity-75">${this.formatTime(message.createdAt)}</div>
                </div>
            </div>
        `;
    }

    /**
     * Subscribe vào conversation channel
     */
    subscribeToConversation(conversationId) {
        if (!this.isConnected || !conversationId) {
            return;
        }

        // Unsubscribe từ conversation cũ nếu có
        if (this.currentSubscription) { try { this.currentSubscription.unsubscribe(); } catch (e) {} }

        // Subscribe conversation mới
        this.currentSubscription = this.stompClient.subscribe(
            `/topic/conversation/${conversationId}`, 
            (message) => {
                const chatMessage = JSON.parse(message.body);
                this.addMessageToUI(chatMessage);
                
                // Nếu conversation chưa được chọn, reload danh sách để cập nhật badge
                if (chatMessage.conversationId !== this.currentConversationId) {
                    this.updateConversationsOnNewMessage(chatMessage);
                }
            }
        );
    }

    /**
     * Cập nhật danh sách hội thoại khi có tin nhắn mới: đưa lên đầu, cập nhật preview và thời gian
     */
    updateConversationsOnNewMessage(chatMessage) {
        if (!chatMessage || !Array.isArray(this.conversations)) {
            this.loadConversations();
            return;
        }
        const idx = this.conversations.findIndex(c => c.id === chatMessage.conversationId);
        if (idx >= 0) {
            const conv = this.conversations[idx];
            conv.lastMessage = {
                content: chatMessage.content,
                createdAt: chatMessage.createdAt,
                senderType: chatMessage.senderType,
                senderName: chatMessage.senderName,
                attachmentUrl: chatMessage.attachmentUrl
            };
            // Tăng unread nếu không phải là manager gửi và conversation không phải đang mở
            if (chatMessage.senderType !== 'MANAGER' && conv.id !== this.currentConversationId) {
                conv.unreadCount = (conv.unreadCount || 0) + 1;
            }
            // Đưa lên đầu danh sách
            this.conversations.splice(idx, 1);
            this.conversations.unshift(conv);
        } else {
            // Nếu chưa có trong danh sách, thêm mới đơn giản và đưa lên đầu (fallback)
            this.conversations.unshift({
                id: chatMessage.conversationId,
                userName: chatMessage.senderType === 'CUSTOMER' ? chatMessage.senderName : undefined,
                guestName: chatMessage.senderType === 'GUEST' ? chatMessage.senderName : undefined,
                unreadCount: 1,
                createdAt: chatMessage.createdAt,
                lastMessage: {
                    content: chatMessage.content,
                    createdAt: chatMessage.createdAt,
                    senderType: chatMessage.senderType,
                    senderName: chatMessage.senderName,
                    attachmentUrl: chatMessage.attachmentUrl
                }
            });
        }
        this.renderConversations();

        // Hiển thị notification cho tin nhắn từ khách/GUEST/CUSTOMER
        try {
            if (chatMessage.senderType !== 'MANAGER') {
                this.showNotification(chatMessage);
            }
        } catch (_) {}
    }

    /**
     * Tự động làm mới danh sách hội thoại định kỳ
     */
    startAutoRefresh() {
        if (this._refreshTimer) return;
        this._refreshTimer = setInterval(() => {
            this.loadConversations();
        }, 10000);
    }

    /**
     * Thêm tin nhắn mới vào UI
     */
    addMessageToUI(message) {
        // Nếu đang xem conversation này, thêm message vào
        if (message.conversationId === this.currentConversationId) {
            const container = document.getElementById('manager-chat-messages');
            if (container) {
                const messageHTML = this.renderMessage(message);
                container.insertAdjacentHTML('beforeend', messageHTML);
                container.scrollTop = container.scrollHeight;
            }
        } else {
            // Nếu không, reload danh sách để cập nhật thông tin
            this.loadConversations();
            
            // Hiển thị notification nếu có tin nhắn mới từ conversation khác
            if (message.senderType !== 'MANAGER') {
                this.showNotification(message);
            }
        }
    }

    /**
     * Hiển thị notification khi có tin nhắn mới
     */
    showNotification(message) {
        // Sử dụng notification system nếu có
        if (window.showNotification) {
            const senderName = message.senderName || 'Khách hàng';
            const content = message.content ? 
                (message.content.length > 60 ? message.content.substring(0, 60) + '...' : message.content) : 
                'Đã gửi một tin nhắn';
            
            window.showNotification(
                `<strong>${senderName}</strong>: ${content}`,
                'info',
                5000
            );
        } else if (window.notificationSystem) {
            const senderName = message.senderName || 'Khách hàng';
            const content = message.content ? 
                (message.content.length > 60 ? message.content.substring(0, 60) + '...' : message.content) : 
                'Đã gửi một tin nhắn';
            
            window.notificationSystem.info(
                `<strong>${senderName}</strong>: ${content}`,
                5000
            );
        }
    }

    /**
     * Gửi tin nhắn
     */
    sendMessage() {
        if (!this.isConnected || !this.currentConversationId) {
            if (window.showNotification) {
                window.showNotification('Vui lòng chọn một cuộc hội thoại để gửi tin nhắn.', 'warning', 4000);
            }
            return;
        }

        const messageInput = document.getElementById('manager-chat-input');
        const content = messageInput.value.trim();

        if (!content) {
            return;
        }

        const messageDTO = {
            conversationId: this.currentConversationId,
            content: content,
            attachmentUrl: null
        };

        this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(messageDTO));

        // Clear input
        messageInput.value = '';
    }

    /**
     * Đóng conversation
     */
    async closeConversation() {
        if (!this.currentConversationId) {
            return;
        }

        if (!confirm('Bạn có chắc muốn đóng cuộc hội thoại này?')) {
            return;
        }

        try {
            const response = await fetch(`/manager/messages/${this.currentConversationId}/close`, {
                method: 'POST'
            });

            if (!response.ok) {
                throw new Error('Failed to close conversation');
            }

            // Reload conversations
            await this.loadConversations();
            
            // Clear current conversation
            this.currentConversationId = null;
            document.getElementById('manager-chat-messages').innerHTML = '<div class="text-center text-muted p-3">Chọn một cuộc hội thoại để xem</div>';
            
            // Unsubscribe
            if (this.currentSubscription) {
                this.currentSubscription.unsubscribe();
                this.currentSubscription = null;
            }
        } catch (error) {
            console.error('Error closing conversation:', error);
            if (window.showNotification) {
                window.showNotification('Có lỗi xảy ra khi đóng cuộc hội thoại.', 'danger', 4000);
            }
        }
    }

    /**
     * Format thời gian
     */
    formatTime(dateTime) {
        if (!dateTime) return '';
        const date = new Date(dateTime);
        const now = new Date();
        const diff = now - date;
        
        if (diff < 60000) return 'Vừa xong';
        if (diff < 3600000) return `${Math.floor(diff / 60000)} phút trước`;
        if (diff < 86400000) return `${Math.floor(diff / 3600000)} giờ trước`;
        return date.toLocaleDateString('vi-VN');
    }

    /**
     * Ngắt kết nối
     */
    disconnect() {
        if (this.stompClient && this.isConnected) {
            // Hủy subscription kênh chung
            if (this._globalSubscription) { try { this._globalSubscription.unsubscribe(); } catch (e) {} this._globalSubscription = null; }
            // Hủy subscription conversation hiện tại
            if (this.currentSubscription) { try { this.currentSubscription.unsubscribe(); } catch (e) {} this.currentSubscription = null; }
            // Dừng auto refresh
            if (this._refreshTimer) { clearInterval(this._refreshTimer); this._refreshTimer = null; }
            this.stompClient.disconnect();
            this.isConnected = false;
            this._connecting = false;
        }

        // Ngắt kết nối thì khoá gửi tin
        const sendBtn = document.getElementById('manager-chat-send');
        const inputEl = document.getElementById('manager-chat-input');
        if (sendBtn) sendBtn.disabled = true;
        if (inputEl) inputEl.disabled = true;
    }
}

// Initialize when DOM is ready
if (typeof window !== 'undefined') {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.managerChat = new ManagerChat();
        });
    } else {
        window.managerChat = new ManagerChat();
    }
}
