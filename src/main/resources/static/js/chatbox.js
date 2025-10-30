// ========================================
// PUBLIC CHATBOX - WebSocket Client
// Cho Guest và Customer
// ========================================

class Chatbox {
    constructor() {
        this.stompClient = null;
        this.conversationId = null;
        this.isConnected = false;
        this.guestName = '';
        this.guestEmail = '';
        this.userId = null; // NULL nếu là guest, có giá trị nếu là customer
        this._eventsBound = false; // tránh bind trùng event
        this._docClickBound = false; // tránh bind trùng click ngoài
        this._connecting = false; // tránh connect lặp
        this._fetchedConv = false; // chỉ gọi API lấy conversationId 1 lần
        this._subscription = null; // giữ subscription hiện tại
        this._currentTopic = null; // topic đang subscribe
        this._messagesTimer = null; // timer auto refresh
        this._emailSubscription = null; // subscription tạm theo email
        this.init();
    }

    /**
     * Prompt người dùng nhập URL ảnh để đính kèm
     */
    attachImageUrl() {
        const url = window.prompt('Nhập URL ảnh để đính kèm:');
        if (!url) return;
        try {
            const trimmed = url.trim();
            if (!/^https?:\/\//i.test(trimmed)) {
                if (window.showNotification) window.showNotification('URL không hợp lệ (cần bắt đầu bằng http/https).', 'warning', 4000);
                return;
            }
            this._pendingAttachmentUrl = trimmed;
            if (window.showNotification) window.showNotification('Đã thêm ảnh đính kèm. Gửi tin để gửi ảnh.', 'info', 3000);
        } catch (_) {}
    }

    init() {
        this.setupEventListeners();
        this.checkUserAuth();
        this.setupChatboxToggle();
    }

    /**
     * Kiểm tra xem user có đăng nhập không
     */
    checkUserAuth() {
        // 1) Thử lấy từ DOM nếu server đã render sẵn
        const userIdElement = document.getElementById('current-user-id');
        if (userIdElement) {
            this.userId = userIdElement.textContent || userIdElement.value;
            return;
        }
        // 2) Fallback gọi API /api/auth/me để kiểm tra đăng nhập
        try {
            fetch('/api/auth/me', { credentials: 'include' })
                .then(res => res.ok ? res.json() : null)
                .then(me => {
                    if (me && me.id) {
                        // Chỉ chấp nhận id số để dùng làm senderId
                        this.userId = me.id;
                        // Enable input gửi nếu UI đã tạo
                        const sendBtn = document.getElementById('chatbox-send');
                        const messageInput = document.getElementById('chatbox-message-input');
                        if (sendBtn) sendBtn.disabled = false;
                        if (messageInput) messageInput.disabled = false;
                        const guestInfoDiv = document.getElementById('chatbox-guest-info');
                        if (guestInfoDiv) guestInfoDiv.style.display = 'none';
                    }
                })
                .catch(() => {});
        } catch (_) {}
    }

    /**
     * Setup event listeners cho chatbox
     */
    setupEventListeners() {
        // Nút mở/đóng chatbox (có thể có sẵn trước khi tạo UI chat)
        const toggleBtn = document.getElementById('chatbox-toggle');
        if (toggleBtn) {
            toggleBtn.onclick = (e) => { e.preventDefault(); e.stopPropagation(); this.toggleChatbox(); };
        }

        // Nút gửi tin nhắn (phần tử được tạo động → bind mỗi lần gọi hàm này)
        const sendBtn = document.getElementById('chatbox-send');
        if (sendBtn) {
            sendBtn.onclick = () => this.sendMessage();
        }

        // Nút đính kèm ảnh (URL)
        const attachBtn = document.getElementById('chatbox-attach');
        if (attachBtn) {
            attachBtn.onclick = () => {
                const fileInput = document.getElementById('chatbox-attach-file');
                if (fileInput) fileInput.click();
            };
        }

        // File input change → upload
        const fileInput = document.getElementById('chatbox-attach-file');
        if (fileInput) {
            fileInput.onchange = async (e) => {
                const file = e.target.files && e.target.files[0];
                if (!file) return;
                try {
                    const form = new FormData();
                    form.append('file', file);
                    const res = await fetch('/api/chat/upload-image', { method: 'POST', body: form });
                    const data = res.ok ? await res.json() : null;
                    if (data && data.url) {
                        this._pendingAttachmentUrl = data.url;
                        if (window.showNotification) window.showNotification('Ảnh đã tải lên. Gửi để đính kèm.', 'success', 3000);
                    } else {
                        if (window.showNotification) window.showNotification('Tải ảnh thất bại.', 'danger', 3000);
                    }
                } catch (_) {
                    if (window.showNotification) window.showNotification('Tải ảnh thất bại.', 'danger', 3000);
                } finally {
                    e.target.value = '';
                }
            };
        }

        // Input message - gửi khi nhấn Enter (phần tử được tạo động)
        const messageInput = document.getElementById('chatbox-message-input');
        if (messageInput) {
            messageInput.onkeypress = (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            };
        }

        // Đóng chatbox khi click ra ngoài (bind 1 lần)
        if (!this._docClickBound) {
            document.addEventListener('click', (e) => {
                const chatbox = document.getElementById('chatbox-container');
                const toggle = document.getElementById('chatbox-toggle');
                if (!chatbox || !toggle) return;
                const clickInside = chatbox.contains(e.target);
                const clickToggle = toggle.contains(e.target);
                if (!clickInside && !clickToggle) {
                    // Không auto đóng để tránh khó chịu; giữ nguyên trạng thái
                }
            });
            this._docClickBound = true;
        }
    }

    /**
     * Setup nút toggle chatbox (button floating)
     */
    setupChatboxToggle() {
        // Tự động hiển thị nút chat nếu chưa có
        if (!document.getElementById('chatbox-toggle')) {
            const toggleBtn = document.createElement('button');
            toggleBtn.id = 'chatbox-toggle';
            toggleBtn.className = 'btn btn-primary rounded-circle position-fixed';
            toggleBtn.style.cssText = 'bottom: 20px; right: 20px; width: 60px; height: 60px; z-index: 1000; box-shadow: 0 4px 12px rgba(0,0,0,0.3);';
            toggleBtn.innerHTML = '<i class="bi bi-chat-dots"></i>';
            toggleBtn.title = 'Chat với chúng tôi';
            document.body.appendChild(toggleBtn);
            
            toggleBtn.onclick = (e) => { e.preventDefault(); e.stopPropagation(); this.toggleChatbox(); };
        }
    }

    /**
     * Mở/đóng chatbox
     */
    toggleChatbox() {
        const chatbox = document.getElementById('chatbox-container');
        if (!chatbox) {
            this.createChatboxUI();
            const created = document.getElementById('chatbox-container');
            if (created) {
                created.classList.remove('d-none');
            }
            this.connect();
            // Nếu đã nhập email trước đó, thử lấy conversation để subscribe ngay khi mở
            if (!this.userId) {
                const emailVal = document.getElementById('chatbox-guest-email')?.value || this.guestEmail;
                if (emailVal && !this.conversationId) {
                    this.guestEmail = emailVal;
                    this.tryFetchConversationIdWithRetry();
                }
            }
            return;
        }

        if (chatbox.classList.contains('d-none')) {
            chatbox.classList.remove('d-none');
            if (chatbox.style && chatbox.style.display === 'none') {
                chatbox.style.display = '';
            }
            this.connect();
            // Khi mở lại và có email mà chưa có conversationId, thử lấy lại
            if (!this.userId && !this.conversationId) {
                const emailVal = document.getElementById('chatbox-guest-email')?.value || this.guestEmail;
                if (emailVal) {
                    this.guestEmail = emailVal;
                    this.tryFetchConversationIdWithRetry();
                }
            }
        } else {
            this.closeChatbox();
        }
    }

    /**
     * Tạo UI chatbox nếu chưa có
     */
    createChatboxUI() {
        const chatboxHTML = `
            <div id="chatbox-container" class="position-fixed shadow-lg rounded" 
                 style="width: 350px; height: 500px; bottom: 90px; right: 20px; z-index: 1000; background: white; display: flex; flex-direction: column;">
                <!-- Header -->
                <div class="bg-primary text-white p-3 rounded-top d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="mb-0">Chat với chúng tôi</h6>
                        <small id="chatbox-status">Đang kết nối...</small>
                    </div>
                    <button type="button" class="btn btn-sm btn-light" onclick="window.chatbox.closeChatbox()">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>

                <!-- Guest Info Form (chỉ hiện khi chưa có conversation) -->
                <div id="chatbox-guest-info" class="p-3 border-bottom" style="display: none;">
                    <input type="text" id="chatbox-guest-name" class="form-control form-control-sm mb-2" 
                           placeholder="Họ và tên (bắt buộc)" required>
                    <input type="email" id="chatbox-guest-email" class="form-control form-control-sm" 
                           placeholder="Email (bắt buộc)" required>
                </div>

                <!-- Messages Area -->
                <div id="chatbox-messages" class="flex-grow-1 p-3 overflow-auto" 
                     style="max-height: 300px; background: #f8f9fa;">
                    <div class="text-center text-muted">
                        <small>Bắt đầu cuộc trò chuyện...</small>
                    </div>
                </div>

                <!-- Input Area -->
                <div class="p-3 border-top">
                    <div class="input-group">
                        <input type="text" id="chatbox-message-input" class="form-control" 
                               placeholder="Nhập tin nhắn..." disabled>
                        <button class="btn btn-outline-secondary" id="chatbox-attach" type="button" title="Đính kèm ảnh (URL)" disabled>
                            <i class="bi bi-image"></i>
                        </button>
                        <button class="btn btn-primary" id="chatbox-send" type="button" disabled>
                            <i class="bi bi-send"></i>
                        </button>
                        <input type="file" id="chatbox-attach-file" accept="image/*" style="display:none;" />
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', chatboxHTML);
        
        // Setup lại event listeners
        this.setupEventListeners();
        
        // Hiển thị form guest info nếu chưa đăng nhập
        if (!this.userId) {
            document.getElementById('chatbox-guest-info').style.display = 'block';
        } else {
            document.getElementById('chatbox-send').disabled = false;
            document.getElementById('chatbox-message-input').disabled = false;
            const attachBtn = document.getElementById('chatbox-attach');
            if (attachBtn) attachBtn.disabled = false;
        }
    }

    /**
     * Đóng chatbox
     */
    closeChatbox() {
        const chatbox = document.getElementById('chatbox-container');
        if (chatbox) {
            chatbox.classList.add('d-none');
        }
        this.disconnect();
    }

    /**
     * Kết nối WebSocket
     */
    connect() {
        if (this.isConnected || this._connecting) {
            return;
        }
        this._connecting = true;

        // Kết nối SockJS
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        // Disable logging trong production
        this.stompClient.debug = null;

        this.stompClient.connect({}, 
            (frame) => {
                console.log('WebSocket Connected: ' + frame);
                this.isConnected = true;
                this._connecting = false;
                document.getElementById('chatbox-status').textContent = 'Đã kết nối';
                document.getElementById('chatbox-send').disabled = false;
                document.getElementById('chatbox-message-input').disabled = false;
                const attachBtn = document.getElementById('chatbox-attach');
                if (attachBtn) attachBtn.disabled = false;
                if (window.showNotification) window.showNotification('Đã kết nối tới Hỗ trợ Mộc Việt', 'success', 1500);

                // Subscribe vào conversation channel và tải lại lịch sử nếu đã có conversation
                if (this.conversationId) {
                    this.subscribeToConversation(this.conversationId);
                    this.loadMessagesFromServer(this.conversationId);
                } else if (this.userId && !this._fetchedCustomerConv) {
                    // Customer: cố lấy conversation đang mở theo userId để subscribe
                    this._fetchedCustomerConv = true;
                    const tryFetchCustomerConv = (attempt = 1) => {
                        fetch(`/api/chat/conversation/customer-open?userId=${encodeURIComponent(this.userId)}`)
                            .then(res => res.ok ? res.json() : null)
                            .then(conv => {
                                if (conv && conv.id) {
                                    this.conversationId = conv.id;
                                    this.subscribeToConversation(this.conversationId);
                                    this.loadMessagesFromServer(this.conversationId);
                                    this.startMessagesAutoRefresh();
                                } else if (attempt < 5) {
                                    setTimeout(() => tryFetchCustomerConv(attempt + 1), 800);
                                }
                            })
                            .catch(() => {
                                if (attempt < 5) setTimeout(() => tryFetchCustomerConv(attempt + 1), 800);
                            });
                    };
                    tryFetchCustomerConv();
        } else if (!this.userId) {
            // Guest: nếu đã có email và chưa có conversationId, chủ động lấy ngay sau khi connect
            const emailVal = document.getElementById('chatbox-guest-email')?.value || this.guestEmail;
            if (emailVal && !this.conversationId) {
                this.guestEmail = emailVal;
                // Luôn thử lấy, không phụ thuộc _fetchedConv
                this.tryFetchConversationIdWithRetry(10, 600);
                // Đồng thời subscribe kênh dự phòng theo email để nhận tin manager ngay
                this.subscribeByEmailFallback(this.guestEmail);
            }
                }
            },
            (error) => {
                console.error('WebSocket Connection Error:', error);
                document.getElementById('chatbox-status').textContent = 'Lỗi kết nối';
                this.isConnected = false;
                this._connecting = false;
                setTimeout(() => this.connect(), 5000); // Retry sau 5 giây
            }
        );
    }

    /**
     * Subscribe vào conversation channel
     */
    subscribeToConversation(conversationId) {
        if (!this.isConnected || !conversationId) {
            return;
        }
        const topic = `/topic/conversation/${conversationId}`;
        // Nếu đã subscribe đúng topic này rồi thì bỏ qua
        if (this._subscription && this._currentTopic === topic) {
            return;
        }
        // Nếu đang có subscription khác, hủy trước khi đăng ký mới
        if (this._subscription && this._currentTopic && this._currentTopic !== topic) {
            try { this._subscription.unsubscribe(); } catch (e) {}
            this._subscription = null;
            this._currentTopic = null;
        }

        this._subscription = this.stompClient.subscribe(topic, (message) => {
            const chatMessage = JSON.parse(message.body);
            this.displayMessage(chatMessage);
        });
        this._currentTopic = topic;
    }

    /**
     * Gửi tin nhắn
     */
    async sendMessage() {
        if (!this.isConnected) {
            if (window.showNotification) {
                window.showNotification('Chưa kết nối. Vui lòng đợi...', 'warning', 4000);
            }
            return;
        }

        const messageInput = document.getElementById('chatbox-message-input');
        const content = messageInput.value.trim();

        if (!content) {
            if (window.showNotification) {
                window.showNotification('Vui lòng nhập nội dung tin nhắn.', 'warning', 3000);
            }
            return;
        }

        // Nếu là guest và chưa có thông tin
        if (!this.userId) {
            this.guestName = document.getElementById('chatbox-guest-name')?.value || '';
            this.guestEmail = document.getElementById('chatbox-guest-email')?.value || '';
            
            if (!this.guestName || !this.guestEmail) {
                if (window.showNotification) {
                    window.showNotification('Vui lòng nhập đầy đủ Họ và tên và Email.', 'warning', 4000);
                }
                return;
            }
            // Validate email format
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(this.guestEmail)) {
                if (window.showNotification) {
                    window.showNotification('Email không hợp lệ. Vui lòng kiểm tra lại.', 'warning', 4000);
                }
                return;
            }

            // Ẩn form guest info sau khi đã nhập
            const guestInfoDiv = document.getElementById('chatbox-guest-info');
            if (guestInfoDiv) {
                guestInfoDiv.style.display = 'none';
            }
            const attachBtn = document.getElementById('chatbox-attach');
            if (attachBtn) attachBtn.disabled = false;
        }

        // Kiểm tra trạng thái conversation nếu đang có conversationId
        if (this.conversationId) {
            try {
                const res = await fetch(`/api/chat/conversation/info?conversationId=${encodeURIComponent(this.conversationId)}`);
                if (res.ok) {
                    const conv = await res.json();
                    if (conv && conv.status && conv.status !== 'OPEN') {
                        // Hội thoại đã đóng → hỏi người dùng tạo hội thoại mới
                        const shouldCreateNew = window.confirm('Cuộc hội thoại này đã được đóng. Bạn có muốn mở một cuộc hội thoại mới không?');
                        if (!shouldCreateNew) {
                            return;
                        }
                        // Reset để server tạo mới
                        this.conversationId = null;
                    }
                }
            } catch (_) {}
        }

        // Gửi message qua WebSocket
        const messageDTO = {
            conversationId: this.conversationId,
            guestName: this.guestName,
            guestEmail: this.guestEmail,
            senderId: this.userId ? parseInt(this.userId) : null,
            content: content,
            attachmentUrl: this._pendingAttachmentUrl || null
        };

        this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(messageDTO));

        // Clear input
        messageInput.value = '';
        this._pendingAttachmentUrl = null;
        if (window.showNotification) {
            window.showNotification('Tin nhắn đã được gửi.', 'success', 2000);
        }

            // Optimistic UI: chỉ áp dụng khi CHƯA có conversationId (lần đầu)
        if (!this.conversationId) {
            this.displayMessage({
                conversationId: this.conversationId, // có thể null ở lần đầu
                senderType: this.userId ? 'CUSTOMER' : 'GUEST',
                senderId: this.userId ? parseInt(this.userId) : null,
                senderName: this.userId ? 'Bạn' : (this.guestName || 'Bạn'),
                content: content,
                attachmentUrl: null,
                createdAt: new Date().toISOString()
            });
        }

        // Nếu chưa có conversationId (lần đầu của guest): hỏi server để lấy id và subscribe
        if (!this.userId && !this.conversationId && this.guestEmail) {
            this._fetchedConv = true;
            const tryFetchConv = (attempt = 1) => {
                fetch(`/api/chat/conversation/open?guestEmail=${encodeURIComponent(this.guestEmail)}`)
                    .then(res => res.ok ? res.json() : null)
                    .then(conv => {
                        if (conv && conv.id) {
                            this.conversationId = conv.id;
                            this.subscribeToConversation(this.conversationId);
                            this.loadMessagesFromServer(this.conversationId);
                            this.startMessagesAutoRefresh();
                        } else if (attempt < 10) {
                            setTimeout(() => tryFetchConv(attempt + 1), 600);
                        } else {
                            // Cho phép lần gọi sau thử lại tiếp
                            this._fetchedConv = false;
                        }
                    })
                    .catch(() => {
                        if (attempt < 10) {
                            setTimeout(() => tryFetchConv(attempt + 1), 600);
                        } else {
                            this._fetchedConv = false;
                        }
                    });
            };
            tryFetchConv();
        }
    }

    /**
     * Tải lịch sử tin nhắn từ server (đồng bộ khi mở lại)
     */
    loadMessagesFromServer(conversationId) {
        if (!conversationId) return;
        fetch(`/api/chat/messages?conversationId=${encodeURIComponent(conversationId)}`)
            .then(res => res.ok ? res.json() : [])
            .then(list => {
                if (Array.isArray(list)) {
                    const messagesDiv = document.getElementById('chatbox-messages');
                    if (!messagesDiv) return;
                    messagesDiv.innerHTML = '';
                    list.forEach(m => this.displayMessage(m));
                }
            })
            .catch(() => {});
    }

    /**
     * Hiển thị tin nhắn
     */
    displayMessage(message) {
        const messagesDiv = document.getElementById('chatbox-messages');
        if (!messagesDiv) {
            return;
        }

        // Set conversationId khi nhận message đầu tiên
        if (!this.conversationId && message.conversationId) {
            this.conversationId = message.conversationId;
            this.subscribeToConversation(this.conversationId);
        }

        // Xóa placeholder nếu có
        const placeholder = messagesDiv.querySelector('.text-center.text-muted');
        if (placeholder) {
            placeholder.remove();
        }

        // Nếu là tin nhắn từ người khác (không phải của mình), hiển thị notification
        if (!this.isMyMessage(message)) {
            const chatboxContainer = document.getElementById('chatbox-container');
            const isChatboxVisible = chatboxContainer && !chatboxContainer.classList.contains('d-none');
            
            // Chỉ hiển thị notification nếu chatbox đang đóng hoặc không hiển thị
            if (!isChatboxVisible) {
                this.showNotification(message);
            }
        }

        // Tạo message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `mb-3 ${this.isMyMessage(message) ? 'text-end' : ''}`;

        const messageBubble = document.createElement('div');
        messageBubble.className = `d-inline-block p-2 rounded ${this.isMyMessage(message) ? 'bg-primary text-white' : 'bg-light'}`;
        messageBubble.style.maxWidth = '80%';

        const contentHtml = message.content ? `<div>${message.content}</div>` : '';
        const imageHtml = message.attachmentUrl ? `<img src="${message.attachmentUrl}" class="img-thumbnail mt-2" style="max-width: 220px;">` : '';
        messageBubble.innerHTML = `
            <div class="fw-bold small">Tên: ${message.senderName || 'Khách'}</div>
            ${contentHtml}
            ${imageHtml}
            <div class="small mt-1 opacity-75">${this.formatTime(message.createdAt)}</div>
        `;

        messageDiv.appendChild(messageBubble);
        messagesDiv.appendChild(messageDiv);

        // Scroll to bottom
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }

    /**
     * Hiển thị notification khi có tin nhắn mới
     */
    showNotification(message) {
        // Sử dụng notification system nếu có
        if (window.showNotification) {
            const senderName = message.senderName || 'Nhân viên tư vấn';
            const content = message.content ? 
                (message.content.length > 50 ? message.content.substring(0, 50) + '...' : message.content) : 
                'Đã gửi một tin nhắn';
            
            window.showNotification(
                `<strong>${senderName}</strong>: ${content}`,
                'info',
                5000
            );
        } else if (window.notificationSystem) {
            const senderName = message.senderName || 'Nhân viên tư vấn';
            const content = message.content ? 
                (message.content.length > 50 ? message.content.substring(0, 50) + '...' : message.content) : 
                'Đã gửi một tin nhắn';
            
            window.notificationSystem.info(
                `<strong>${senderName}</strong>: ${content}`,
                5000
            );
        }
        
        // Thêm hiệu ứng rung cho nút chat (nếu có)
        const toggleBtn = document.getElementById('chatbox-toggle');
        if (toggleBtn) {
            toggleBtn.style.animation = 'shake 0.5s';
            setTimeout(() => {
                toggleBtn.style.animation = '';
            }, 500);
        }
    }

    /**
     * Kiểm tra xem message có phải của mình không
     */
    isMyMessage(message) {
        if (this.userId) {
            return message.senderId === parseInt(this.userId);
        }
        // Guest (chưa đăng nhập): coi là "của mình" khi sender là GUEST và không có senderId
        return message.senderType === 'GUEST' && (message.senderId == null);
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
        return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    }

    /**
     * Ngắt kết nối WebSocket
     */
    disconnect() {
        if (this.stompClient && this.isConnected) {
            // Hủy subscription nếu có
            if (this._subscription) {
                try { this._subscription.unsubscribe(); } catch (e) {}
                this._subscription = null;
                this._currentTopic = null;
            }
            // Hủy subscription email fallback nếu có
            if (this._emailSubscription) {
                try { this._emailSubscription.unsubscribe(); } catch (e) {}
                this._emailSubscription = null;
            }
            // Dừng auto refresh nếu đang chạy
            if (this._messagesTimer) {
                clearInterval(this._messagesTimer);
                this._messagesTimer = null;
            }
            this.stompClient.disconnect();
            this.isConnected = false;
            const statusElement = document.getElementById('chatbox-status');
            if (statusElement) {
                statusElement.textContent = 'Đã ngắt kết nối';
            }
        }
    }

    /**
     * Subscribe kênh dự phòng theo email của guest
     */
    subscribeByEmailFallback(email) {
        if (!this.isConnected || !email) return;
        // Nếu đã subscribe theo email rồi thì bỏ qua
        if (this._emailSubscription) return;
        const topic = `/topic/guest/${email}`;
        try {
            this._emailSubscription = this.stompClient.subscribe(topic, (message) => {
                const chatMessage = JSON.parse(message.body);
                this.displayMessage(chatMessage);
                if (!this.conversationId) {
                    this.tryFetchConversationIdWithRetry(10, 600);
                }
            });
        } catch (_) {}
    }

    /**
     * Thử lấy conversationId theo email với retry
     */
    tryFetchConversationIdWithRetry(maxAttempt = 5, delayMs = 800) {
        if (!this.guestEmail) return;
        let attempt = 0;
        const run = () => {
            if (this.conversationId) return;
            attempt++;
            fetch(`/api/chat/conversation/open?guestEmail=${encodeURIComponent(this.guestEmail)}`)
                .then(res => res.ok ? res.json() : null)
                .then(conv => {
                    if (conv && conv.id) {
                        this.conversationId = conv.id;
                        this.subscribeToConversation(this.conversationId);
                        this.loadMessagesFromServer(this.conversationId);
                        this.startMessagesAutoRefresh();
                    } else if (attempt < maxAttempt) {
                        setTimeout(run, delayMs);
                    } else {
                        // Cho phép các lần gọi sau thử lại
                        this._fetchedConv = false;
                    }
                })
                .catch(() => {
                    if (attempt < maxAttempt) {
                        setTimeout(run, delayMs);
                    } else {
                        this._fetchedConv = false;
                    }
                });
        };
        run();
    }

    /**
     * Bật auto refresh messages như phương án dự phòng realtime
     */
    startMessagesAutoRefresh() {
        if (this._messagesTimer || !this.conversationId) return;
        this._messagesTimer = setInterval(() => {
            if (this.conversationId) {
                this.loadMessagesFromServer(this.conversationId);
            }
        }, 5000);
    }
}

// Add CSS animation for shake effect
if (!document.getElementById('chatbox-animations')) {
    const style = document.createElement('style');
    style.id = 'chatbox-animations';
    style.textContent = `
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
            20%, 40%, 60%, 80% { transform: translateX(5px); }
        }
    `;
    document.head.appendChild(style);
}

// Initialize khi DOM ready
if (typeof window !== 'undefined') {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.chatbox = new Chatbox();
        });
    } else {
        window.chatbox = new Chatbox();
    }
}
