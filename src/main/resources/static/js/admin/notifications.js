// ========================================
// NOTIFICATIONS MANAGEMENT JAVASCRIPT
// ========================================

console.log('Notifications Management JS loaded successfully!');

(function() {
    'use strict';

    class NotificationsManagement {
        constructor() {
            this.apiEndpoint = '/admin/notifications/api';
            this.currentNotifications = [];
            this.filteredNotifications = [];
            this.currentFilter = 'all';
            this.searchTerm = '';
            this.currentPage = 1;
            this.pageSize = 10;
            this.init();
        }

        init() {
            this.loadNotifications();
        }

        async loadNotifications() {
            try {
                const response = await fetch(this.apiEndpoint);
                if (!response.ok) throw new Error('Failed to fetch notifications');
                
                const data = await response.json();
                this.currentNotifications = data.notifications || [];
                this.filteredNotifications = [...this.currentNotifications];
            this.currentPage = 1;
            this.renderNotifications();
                this.updateStats();
            } catch (error) {
                console.error('Error loading notifications:', error);
                this.showNotification('Không thể tải thông báo', 'danger');
            }
        }

        renderNotifications() {
            const container = document.getElementById('notificationsList');
            if (!container) return;

            if (this.filteredNotifications.length === 0) {
                container.innerHTML = `
                    <div class="text-center py-5 text-muted">
                        <i class="bi bi-bell-slash fs-1 mb-3 opacity-25"></i>
                        <p class="mb-0">Chưa có thông báo nào</p>
                    </div>
                `;
                return;
            }

            // Phân trang slice
            const start = (this.currentPage - 1) * this.pageSize;
            const end = start + this.pageSize;
            const pageItems = this.filteredNotifications.slice(start, end);

            container.innerHTML = pageItems.map(notif => `
                <div class="d-flex align-items-start p-3 border-bottom ${!notif.isRead ? 'bg-light' : ''}" data-notification-id="${notif.id}">
                    <div class="flex-shrink-0 me-3">
                        <i class="bi ${this.getIconForTitle(notif.title)} ${!notif.isRead ? 'text-primary' : 'text-muted'} fs-4"></i>
                    </div>
                    <div class="flex-grow-1">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-1 ${!notif.isRead ? 'fw-bold' : ''}">
                                    ${this.escapeHtml(notif.title)}
                                    ${!notif.isRead ? '<span class="badge bg-primary ms-2">Mới</span>' : ''}
                                </h6>
                                <p class="text-muted small mb-1">${this.escapeHtml(notif.message || '')}</p>
                                <small class="text-muted">${this.formatDate(notif.createdAt)}</small>
                            </div>
                            <div class="btn-group ms-3">
                                <button class="btn btn-sm btn-outline-primary" onclick="notificationsManagement.viewNotification(${notif.id})">
                                    <i class="bi bi-eye"></i>
                                </button>
                                ${!notif.isRead ? `
                                    <button class="btn btn-sm btn-outline-success" onclick="notificationsManagement.markAsRead(${notif.id})">
                                        <i class="bi bi-check"></i>
                                    </button>
                                ` : ''}
                                <button class="btn btn-sm btn-outline-danger" onclick="notificationsManagement.deleteNotification(${notif.id})">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `).join('');

            // Render controls phân trang
            this.renderPagination();
        }

        renderPagination() {
            const container = document.getElementById('notificationsList');
            if (!container) return;
            const totalPages = Math.ceil(this.filteredNotifications.length / this.pageSize);
            if (totalPages <= 1) return;

            const pagination = document.createElement('div');
            pagination.className = 'd-flex justify-content-between align-items-center pt-3';

            const info = document.createElement('small');
            const start = (this.currentPage - 1) * this.pageSize + 1;
            const end = Math.min(this.currentPage * this.pageSize, this.filteredNotifications.length);
            info.className = 'text-muted';
            info.textContent = `Hiển thị ${start}-${end} / ${this.filteredNotifications.length}`;

            const controls = document.createElement('div');
            controls.className = 'btn-group';
            const prevBtn = document.createElement('button');
            prevBtn.className = 'btn btn-sm btn-outline-secondary';
            prevBtn.innerHTML = '<i class="bi bi-chevron-left"></i>';
            prevBtn.disabled = this.currentPage === 1;
            prevBtn.onclick = () => { this.currentPage--; this.renderNotifications(); };

            const nextBtn = document.createElement('button');
            nextBtn.className = 'btn btn-sm btn-outline-secondary';
            nextBtn.innerHTML = '<i class="bi bi-chevron-right"></i>';
            nextBtn.disabled = this.currentPage >= totalPages;
            nextBtn.onclick = () => { this.currentPage++; this.renderNotifications(); };

            controls.appendChild(prevBtn);
            controls.appendChild(nextBtn);

            pagination.appendChild(info);
            pagination.appendChild(controls);

            container.appendChild(pagination);
        }

        getIconForTitle(title) { // Lấy icon cho tiêu đề
            if (title.includes('bài viết')) return 'bi-file-text';
            if (title.includes('đơn hàng')) return 'bi-cart';
            if (title.includes('review')) return 'bi-star';
            if (title.includes('đánh giá')) return 'bi-star';
            return 'bi-bell';
        }

        formatDate(dateString) {
            const date = new Date(dateString);
            const now = new Date();
            const diffMs = now - date;
            const diffMins = Math.floor(diffMs / 60000);
            const diffHours = Math.floor(diffMs / 3600000);
            const diffDays = Math.floor(diffMs / 86400000);

            if (diffMins < 1) return 'Vừa xong';
            if (diffMins < 60) return `${diffMins} phút trước`;
            if (diffHours < 24) return `${diffHours} giờ trước`;
            if (diffDays < 7) return `${diffDays} ngày trước`;
            
            return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'long', day: 'numeric' });
        }

        updateStats() {
            const total = this.currentNotifications.length;
            const unread = this.currentNotifications.filter(n => !n.isRead).length;
            const read = total - unread;

            document.getElementById('totalNotifications').textContent = total;
            document.getElementById('unreadNotifications').textContent = unread;
            document.getElementById('readNotifications').textContent = read;
        }

        searchNotifications() {
            this.searchTerm = document.getElementById('searchInput').value.toLowerCase();
            this.applyFilters();
        }

        filterByStatus() {
            this.currentFilter = document.getElementById('statusFilter').value;
            this.applyFilters();
        }

        applyFilters() {
            this.filteredNotifications = this.currentNotifications.filter(notif => {
                const matchesSearch = !this.searchTerm || 
                    notif.title.toLowerCase().includes(this.searchTerm) ||
                    (notif.message && notif.message.toLowerCase().includes(this.searchTerm));
                
                const matchesStatus = this.currentFilter === 'all' ||
                    (this.currentFilter === 'unread' && !notif.isRead) ||
                    (this.currentFilter === 'read' && notif.isRead);

                return matchesSearch && matchesStatus;
            });

            this.currentPage = 1;
            this.renderNotifications();
        }

        resetFilters() {
            document.getElementById('searchInput').value = '';
            document.getElementById('statusFilter').value = 'all';
            this.searchTerm = '';
            this.currentFilter = 'all';
            this.applyFilters();
        }

        async viewNotification(id) {
            const notification = this.currentNotifications.find(n => n.id === id);
            if (!notification) return;

            const titleEl = document.getElementById('viewNotificationTitle');
            const titleTextEl = document.getElementById('viewTitleText');
            const messageTextEl = document.getElementById('viewMessageText');
            const createdAtTextEl = document.getElementById('viewCreatedAtText');
            const markAsReadBtn = document.querySelector('#viewNotificationModal .btn-primary');
            
            if (titleEl) titleEl.textContent = notification.title;
            if (titleTextEl) titleTextEl.textContent = notification.title;
            if (messageTextEl) messageTextEl.textContent = notification.message || 'Không có nội dung';
            if (createdAtTextEl) createdAtTextEl.textContent = this.formatDate(notification.createdAt);

            // Hiển thị/ẩn button đánh dấu đã đọc dựa trên trạng thái isRead
            if (markAsReadBtn) {
                if (notification.isRead) {
                    markAsReadBtn.style.display = 'none';
                } else {
                    markAsReadBtn.style.display = 'inline-block';
                }
            }

            // Lưu ID thông báo hiện tại để đánh dấu đã đọc
            this.currentViewingId = id;

            const modalEl = document.getElementById('viewNotificationModal');
            if (modalEl) {
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            }
        }

        async markAsRead(id) {
            try {
                const response = await fetch(`${this.apiEndpoint}/${id}/read`, {
                    method: 'PUT'
                });

                if (!response.ok) throw new Error('Failed to mark as read');

                await this.loadNotifications();
                this.showNotification('Đã đánh dấu đã đọc', 'success');
                
                // Refresh header thông báo
                if (typeof loadHeaderNotifications === 'function') {
                    loadHeaderNotifications();
                }
            } catch (error) {
                console.error('Error marking as read:', error);
                this.showNotification('Không thể đánh dấu đã đọc', 'danger');
            }
        }

        async markAsReadFromModal() {
            if (this.currentViewingId) {
                await this.markAsRead(this.currentViewingId);
                bootstrap.Modal.getInstance(document.getElementById('viewNotificationModal')).hide();
            }
        }

        async markAllAsRead() {
            try {
                const response = await fetch(`${this.apiEndpoint}/mark-all-read`, {
                    method: 'PUT'
                });

                if (!response.ok) throw new Error('Failed to mark all as read');

                await this.loadNotifications();
                this.showNotification('Đã đánh dấu tất cả đã đọc', 'success');
                
                // Refresh header thông báo
                if (typeof loadHeaderNotifications === 'function') {
                    loadHeaderNotifications();
                }
            } catch (error) {
                console.error('Error marking all as read:', error);
                this.showNotification('Không thể đánh dấu tất cả đã đọc', 'danger');
            }
        }

        async deleteNotification(id) {
            if (!confirm('Bạn có chắc chắn muốn xóa thông báo này?')) return;

            try {
                const response = await fetch(`${this.apiEndpoint}/${id}`, {
                    method: 'DELETE'
                });

                if (!response.ok) throw new Error('Failed to delete notification');

                await this.loadNotifications();
                this.showNotification('Đã xóa thông báo', 'success');
                
                // Refresh header thông báo
                if (typeof loadHeaderNotifications === 'function') {
                    loadHeaderNotifications();
                }
            } catch (error) {
                console.error('Error deleting notification:', error);
                this.showNotification('Không thể xóa thông báo', 'danger');
            }
        }

        async deleteAllRead() {
            if (!confirm('Bạn có chắc chắn muốn xóa tất cả thông báo đã đọc?')) return;

            try {
                const response = await fetch(`${this.apiEndpoint}/delete-all-read`, {
                    method: 'DELETE'
                });

                if (!response.ok) throw new Error('Failed to delete all read');

                await this.loadNotifications();
                this.showNotification('Đã xóa tất cả thông báo đã đọc', 'success');
                
                // Refresh header notifications
                if (typeof loadHeaderNotifications === 'function') {
                    loadHeaderNotifications();
                }
            } catch (error) {
                console.error('Error deleting all read:', error);
                this.showNotification('Không thể xóa thông báo', 'danger');
            }
        }

        escapeHtml(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        showNotification(message, type) {
            if (window.notificationSystem) {
                window.notificationSystem.show(message, type);
            }
        }
    }

    // Khởi tạo khi DOM đã tải xong
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            window.notificationsManagement = new NotificationsManagement();
        });
    } else {
        window.notificationsManagement = new NotificationsManagement();
    }
})();

