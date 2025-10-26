// ========================================
// SOCIAL LINKS MANAGEMENT JAVASCRIPT
// ========================================

console.log('Social Links Management JS loaded successfully!');

class SocialLinksManagement {
    constructor() {
        this.apiEndpoint = '/admin/social-links/api';
        this.links = [];
        
        this.init();
    }
    
    init() {
        this.loadSocialLinks();
    }
    
    /**
     * Load social links
     */
    async loadSocialLinks() {
        try {
            const response = await fetch(`${this.apiEndpoint}/links`);
            if (!response.ok) throw new Error('Failed to load social links');
            this.links = await response.json();
            
            this.renderSocialLinks();
        } catch (error) {
            console.error('Error loading social links:', error);
            this.showNotification('Không thể tải liên kết mạng xã hội', 'danger');
        }
    }
    
    /**
     * Render social links
     */
    renderSocialLinks() {
        const container = document.getElementById('socialLinksContainer');
        if (!container) return;
        
        // Expected platforms
        const expectedPlatforms = ['FACEBOOK', 'ZALO', 'YOUTUBE'];
        const platformConfig = {
            'FACEBOOK': { icon: 'bi-facebook', color: 'primary', name: 'Facebook' },
            'ZALO': { icon: 'bi-chat-dots', color: 'info', name: 'Zalo' },
            'YOUTUBE': { icon: 'bi-youtube', color: 'danger', name: 'Youtube' }
        };
        
        container.innerHTML = expectedPlatforms.map(platform => {
            const link = this.links.find(l => l.platform === platform);
            const config = platformConfig[platform];
            const url = link ? link.url : '';
            const isActive = link ? link.isActive : false;
            const linkId = link ? link.id : null;
            
            return this.renderLinkCard(platform, config, url, isActive, linkId);
        }).join('');
    }
    
    /**
     * Render individual link card
     */
    renderLinkCard(platform, config, url, isActive, linkId) {
        const statusBadge = isActive 
            ? '<span class="badge bg-success">Đang hiển thị</span>'
            : '<span class="badge bg-secondary">Đã ẩn</span>';
        
        const urlDisplay = url || '<span class="text-muted">Chưa cập nhật</span>';
        
        return `
            <div class="col-md-4 mb-3">
                <div class="card h-100 shadow-sm border-0">
                    <div class="card-header bg-${config.color} bg-opacity-10 d-flex justify-content-between align-items-center">
                        <h6 class="mb-0">
                            <i class="bi ${config.icon} text-${config.color} me-2"></i>
                            ${config.name}
                        </h6>
                        ${statusBadge}
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <label class="form-label small text-muted mb-1">URL hiện tại:</label>
                            <p class="mb-0 small">${urlDisplay}</p>
                        </div>
                        <div class="d-grid gap-2">
                            <button class="btn btn-outline-${config.color}" onclick="editSocialLink('${platform}', '${config.name}')">
                                <i class="bi bi-pencil me-1"></i>Cập nhật link
                            </button>
                            ${url ? `<a href="${url}" target="_blank" class="btn btn-${config.color}">
                                <i class="bi bi-box-arrow-up-right me-1"></i>Truy cập liên kết
                            </a>` : ''}
                        </div>
                    </div>
                    <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center">
                        <small class="text-muted">
                            <i class="bi bi-info-circle me-1"></i>
                            ${isActive ? 'Đang hiển thị trên website' : 'Đã ẩn khỏi website'}
                        </small>
                        ${linkId ? `<input type="hidden" id="linkId_${platform}" value="${linkId}">` : ''}
                        <input type="hidden" id="linkPlatform_${platform}" value="${platform}">
                    </div>
                </div>
            </div>
        `;
    }
    
    /**
     * Edit social link
     */
    async editSocialLink(platform, platformName) {
        const link = this.links.find(l => l.platform === platform);
        
        // Fill form (store platform enum in hidden field)
        document.getElementById('editLinkPlatform').setAttribute('data-platform', platform);
        document.getElementById('editLinkPlatform').value = platformName;
        
        if (link) {
            document.getElementById('editLinkId').value = link.id;
            document.getElementById('editLinkUrl').value = link.url;
            document.getElementById('editLinkActive').checked = link.isActive;
        } else {
            document.getElementById('editLinkId').value = '';
            document.getElementById('editLinkUrl').value = '';
            document.getElementById('editLinkActive').checked = true;
        }
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('editSocialLinkModal'));
        modal.show();
    }
    
    /**
     * Update social link
     */
    async updateSocialLink() {
        const linkId = document.getElementById('editLinkId').value;
        const url = document.getElementById('editLinkUrl').value.trim();
        const isActive = document.getElementById('editLinkActive').checked;
        
        if (!url) {
            this.showNotification('Vui lòng nhập URL', 'warning');
            return;
        }
        
        try {
            let response;
            
            if (linkId) {
                // Update existing link
                response = await fetch(`${this.apiEndpoint}/links/${linkId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ url, isActive })
                });
            } else {
                // Create new link
                const platformEnum = document.getElementById('editLinkPlatform').getAttribute('data-platform');
                
                response = await fetch(`${this.apiEndpoint}/links`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ 
                        platform: platformEnum,
                        url,
                        isActive
                    })
                });
            }
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật liên kết thành công', 'success');
                const modalElement = document.getElementById('editSocialLinkModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadSocialLinks();
            } else {
                let errorMsg = result.message || 'Cập nhật liên kết thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error updating social link:', error);
            this.showNotification('Không thể cập nhật liên kết', 'danger');
        }
    }
    
    /**
     * Helper: Show notification
     */
    showNotification(message, type = 'info') {
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
        } else {
            console.log(`[${type.toUpperCase()}]: ${message}`);
        }
    }
}

// Global instance
let socialLinksManagement;

// Initialize Social Links Management
function initializeSocialLinksManagement() {
    if (socialLinksManagement) {
        delete socialLinksManagement;
    }
    socialLinksManagement = new SocialLinksManagement();
}

// Global functions for onclick handlers
function editSocialLink(platform, platformName) {
    if (socialLinksManagement) {
        socialLinksManagement.editSocialLink(platform, platformName);
    }
}

function updateSocialLink() {
    if (socialLinksManagement) {
        socialLinksManagement.updateSocialLink();
    }
}

// Export for admin.js
window.initializeSocialLinksManagement = initializeSocialLinksManagement;

