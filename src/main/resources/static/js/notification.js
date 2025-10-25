/**
 * Notification System
 * Hệ thống thông báo chung cho toàn bộ admin panel
 * Version: HuynhNgocThang
 */

console.log('Notification System loaded successfully!');

class NotificationSystem {
    constructor() {
        this.notifications = [];
        this.maxNotifications = 5;
        this.defaultDuration = 4000;
        this.debounceMap = new Map(); // Để ngăn spam cùng một message
        this.debounceTime = 2000; // 2 giây debounce
        this.init();
    }

    init() {
        // Tạo container cho notifications
        this.createNotificationContainer();
        console.log('✅ Notification system initialized');
    }

    /**
     * Tạo container cho notifications
     */
    createNotificationContainer() {
        if (document.getElementById('notification-container')) {
            return;
        }

        const container = document.createElement('div');
        container.id = 'notification-container';
        container.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            max-width: 400px;
            pointer-events: none;
        `;

        document.body.appendChild(container);
    }

    /**
     * Hiển thị notification với debounce để ngăn spam
     * @param {string} message - Nội dung thông báo
     * @param {string} type - Loại thông báo: success, danger, warning, info
     * @param {number} duration - Thời gian hiển thị (ms)
     */
    show(message, type = 'info', duration = this.defaultDuration) {
        // Tạo key để debounce
        const debounceKey = `${message}-${type}`;
        const now = Date.now();
        
        // Kiểm tra debounce
        if (this.debounceMap.has(debounceKey)) {
            const lastShown = this.debounceMap.get(debounceKey);
            if (now - lastShown < this.debounceTime) {
                return;
            }
        }
        
        // Cập nhật thời gian cuối cùng hiển thị
        this.debounceMap.set(debounceKey, now);
        
        // Cleanup debounce map sau 5 phút
        setTimeout(() => {
            this.debounceMap.delete(debounceKey);
        }, 300000);
        
        const notification = this.createNotification(message, type);
        this.addNotification(notification);
        
        // Auto remove sau duration
        setTimeout(() => {
            this.removeNotification(notification);
        }, duration);

        console.log(`Notification [${type.toUpperCase()}]: ${message}`);
    }

    /**
     * Force hiển thị notification (bỏ qua debounce)
     * @param {string} message - Nội dung thông báo
     * @param {string} type - Loại thông báo: success, danger, warning, info
     * @param {number} duration - Thời gian hiển thị (ms)
     */
    forceShow(message, type = 'info', duration = this.defaultDuration) {
        const notification = this.createNotification(message, type);
        this.addNotification(notification);
        
        // Auto remove sau duration
        setTimeout(() => {
            this.removeNotification(notification);
        }, duration);

        console.log(`Notification [${type.toUpperCase()}] FORCED: ${message}`);
    }

    /**
     * Tạo notification element
     */
    createNotification(message, type) {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        
        const iconMap = {
            'success': 'check-circle-fill',
            'danger': 'exclamation-triangle-fill',
            'warning': 'exclamation-triangle-fill',
            'info': 'info-circle-fill'
        };

        const titleMap = {
            'success': 'Thành công!',
            'danger': 'Lỗi!',
            'warning': 'Cảnh báo!',
            'info': 'Thông báo!'
        };

        notification.innerHTML = `
            <div class="notification-content">
                <div class="notification-header">
                    <i class="bi bi-${iconMap[type] || 'info-circle-fill'} notification-icon"></i>
                    <span class="notification-title">${titleMap[type] || 'Thông báo!'}</span>
                    <button type="button" class="notification-close" onclick="window.notificationSystem.removeNotification(this.parentElement.parentElement.parentElement)">
                        <i class="bi bi-x"></i>
                    </button>
                </div>
                <div class="notification-body">
                    ${message}
                </div>
            </div>
        `;

        // Add styles
        notification.style.cssText = `
            background: ${this.getBackgroundColor(type)};
            border: 1px solid ${this.getBorderColor(type)};
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
            margin-bottom: 10px;
            opacity: 0;
            transform: translateX(100%);
            transition: all 0.3s ease;
            pointer-events: auto;
            max-width: 350px;
        `;

        return notification;
    }

    /**
     * Thêm notification vào container
     */
    addNotification(notification) {
        const container = document.getElementById('notification-container');
        container.appendChild(notification);

        // Trigger animation
        setTimeout(() => {
            notification.style.opacity = '1';
            notification.style.transform = 'translateX(0)';
        }, 10);

        this.notifications.push(notification);

        // Giới hạn số lượng notifications
        if (this.notifications.length > this.maxNotifications) {
            const oldNotification = this.notifications.shift();
            this.removeNotification(oldNotification);
        }
    }

    /**
     * Xóa notification
     */
    removeNotification(notification) {
        if (!notification || !notification.parentNode) {
            return;
        }

        // Animation out
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';

        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
            
            // Remove from array
            const index = this.notifications.indexOf(notification);
            if (index > -1) {
                this.notifications.splice(index, 1);
            }
        }, 300);
    }

    /**
     * Lấy màu background theo type
     */
    getBackgroundColor(type) {
        const colors = {
            'success': '#d1edff',
            'danger': '#f8d7da',
            'warning': '#fff3cd',
            'info': '#d1ecf1'
        };
        return colors[type] || colors['info'];
    }

    /**
     * Lấy màu border theo type
     */
    getBorderColor(type) {
        const colors = {
            'success': '#0f5132',
            'danger': '#842029',
            'warning': '#664d03',
            'info': '#055160'
        };
        return colors[type] || colors['info'];
    }

    /**
     * Xóa tất cả notifications
     */
    clearAll() {
        this.notifications.forEach(notification => {
            this.removeNotification(notification);
        });
    }

    /**
     * Success notification
     */
    success(message, duration) {
        this.show(message, 'success', duration);
    }

    /**
     * Error notification
     */
    error(message, duration) {
        this.show(message, 'danger', duration);
    }

    /**
     * Warning notification
     */
    warning(message, duration) {
        this.show(message, 'warning', duration);
    }

    /**
     * Info notification
     */
    info(message, duration) {
        this.show(message, 'info', duration);
    }

    /**
     * Force success notification (bỏ qua debounce)
     */
    forceSuccess(message, duration) {
        this.forceShow(message, 'success', duration);
    }

    /**
     * Force error notification (bỏ qua debounce)
     */
    forceError(message, duration) {
        this.forceShow(message, 'danger', duration);
    }
}

// Initialize notification system
window.notificationSystem = new NotificationSystem();

// Global function để tương thích với code cũ
window.showNotification = function(message, type = 'info', duration = 4000) {
    window.notificationSystem.show(message, type, duration);
};
