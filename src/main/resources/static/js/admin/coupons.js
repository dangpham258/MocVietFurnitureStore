// ========================================
// COUPONS MANAGEMENT JAVASCRIPT
// ========================================

console.log('Coupons Management JS loaded successfully!');

class CouponsManagement {
    constructor() {
        this.apiEndpoint = '/admin/coupons/api';
        this.coupons = [];
        this.filteredCoupons = [];
        this.currentPage = 1;
        this.itemsPerPage = 5; // TEMP: Giảm xuống để test phân trang
        
        this.init();
    }
    
    init() {
        this.loadCoupons();
        this.bindEvents();
    }
    
    /**
     * Load all coupons from API
     */
    async loadCoupons() {
        try {
            const response = await fetch(`${this.apiEndpoint}/list`);
            if (!response.ok) throw new Error('Failed to load coupons');
            
            this.coupons = await response.json();
            this.filteredCoupons = [...this.coupons];
            
            this.applyFilters();
            this.renderCoupons();
            this.updateStats();
        } catch (error) {
            console.error('Error loading coupons:', error);
            this.showNotification('Không thể tải danh sách mã giảm giá', 'danger');
        }
    }
    
    /**
     * Bind event listeners
     */
    bindEvents() {
        // Search input
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', () => this.applyFilters());
        }
        
        // Status filter
        const statusFilter = document.getElementById('filterStatus');
        if (statusFilter) {
            statusFilter.addEventListener('change', () => this.applyFilters());
        }
        
        // Validity filter
        const validityFilter = document.getElementById('filterValidity');
        if (validityFilter) {
            validityFilter.addEventListener('change', () => this.applyFilters());
        }
    }
    
    /**
     * Apply filters to coupons list
     */
    applyFilters() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const statusFilter = document.getElementById('filterStatus').value;
        const validityFilter = document.getElementById('filterValidity').value;
        
        this.filteredCoupons = this.coupons.filter(coupon => {
            // Search filter
            const matchesSearch = !searchTerm || 
                coupon.code.toLowerCase().includes(searchTerm) ||
                coupon.discountPercent.toString().includes(searchTerm);
            
            // Status filter
            const matchesStatus = !statusFilter || 
                (statusFilter === 'active' && coupon.active) ||
                (statusFilter === 'inactive' && !coupon.active);
            
            // Validity filter
            let matchesValidity = true;
            if (validityFilter) {
                const now = new Date();
                const startDate = new Date(coupon.startDate);
                const endDate = new Date(coupon.endDate);
                
                if (validityFilter === 'valid') {
                    matchesValidity = now >= startDate && now <= endDate;
                } else if (validityFilter === 'upcoming') {
                    matchesValidity = now < startDate;
                } else if (validityFilter === 'expired') {
                    matchesValidity = now > endDate;
                }
            }
            
            return matchesSearch && matchesStatus && matchesValidity;
        });
        
        this.currentPage = 1;
        this.renderCoupons();
        this.updateStats();
    }
    
    /**
     * Render coupons list
     */
    renderCoupons() {
        const container = document.getElementById('couponsContainer');
        if (!container) return;
        
        if (this.filteredCoupons.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-ticket-perforated text-muted" style="font-size: 3rem;"></i>
                    <p class="text-muted mt-3">Không có mã giảm giá nào</p>
                </div>
            `;
            return;
        }
        
        // Paginate
        const start = (this.currentPage - 1) * this.itemsPerPage;
        const end = start + this.itemsPerPage;
        const paginatedCoupons = this.filteredCoupons.slice(start, end);
        
        // Clear container first
        container.innerHTML = '';
        
        // Render coupon cards
        paginatedCoupons.forEach(coupon => {
            container.insertAdjacentHTML('beforeend', this.renderCouponCard(coupon));
        });
        
        // Add pagination
        this.renderPagination();
    }
    
    /**
     * Render individual coupon card
     */
    renderCouponCard(coupon) {
        const now = new Date();
        const startDate = new Date(coupon.startDate);
        const endDate = new Date(coupon.endDate);
        
        let statusBadge = '';
        let statusClass = '';
        
        if (!coupon.active) {
            statusBadge = '<span class="badge bg-secondary">Đã vô hiệu hóa</span>';
            statusClass = 'opacity-50';
        } else if (now < startDate) {
            statusBadge = '<span class="badge bg-info">Sắp có hiệu lực</span>';
        } else if (now > endDate) {
            statusBadge = '<span class="badge bg-danger">Đã hết hạn</span>';
        } else {
            statusBadge = '<span class="badge bg-success">Đang hiệu lực</span>';
        }
        
        const minOrderFormatted = new Intl.NumberFormat('vi-VN').format(coupon.minOrderAmount);
        
        return `
            <div class="card mb-3 ${statusClass}">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-md-3">
                            <div class="d-flex align-items-center">
                                <div class="bg-primary bg-opacity-10 rounded p-2 me-3">
                                    <i class="bi bi-ticket-perforated text-primary fs-4"></i>
                                </div>
                                <div>
                                    <h5 class="mb-0">${this.escapeHtml(coupon.code)}</h5>
                                    <small class="text-muted">Mã giảm giá</small>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2 text-center">
                            <div class="text-primary fs-4 fw-bold">${coupon.discountPercent}%</div>
                            <small class="text-muted">Giảm giá</small>
                        </div>
                        <div class="col-md-3">
                            <small class="text-muted d-block">Thời gian</small>
                            <div class="small">
                                <i class="bi bi-calendar-event"></i> ${this.formatDate(coupon.startDate)}
                            </div>
                            <div class="small">
                                <i class="bi bi-calendar-x"></i> ${this.formatDate(coupon.endDate)}
                            </div>
                        </div>
                        <div class="col-md-2 text-center">
                            <small class="text-muted d-block">Ngưỡng tối thiểu</small>
                            <strong>${minOrderFormatted} đ</strong>
                        </div>
                        <div class="col-md-2 text-center">
                            ${statusBadge}
                        </div>
                    </div>
                    <div class="row mt-3 pt-3 border-top">
                        <div class="col-12 text-end">
                            <button class="btn btn-sm btn-outline-warning me-2" onclick="editCoupon('${coupon.code}')">
                                <i class="bi bi-pencil"></i> Sửa
                            </button>
                            <button class="btn btn-sm btn-outline-${coupon.active ? 'danger' : 'success'}" 
                                    onclick="toggleCouponStatus('${coupon.code}')">
                                <i class="bi bi-${coupon.active ? 'lock' : 'unlock'}"></i> 
                                ${coupon.active ? 'Vô hiệu hóa' : 'Kích hoạt'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
    
    /**
     * Render pagination
     */
    renderPagination() {
        const totalPages = Math.ceil(this.filteredCoupons.length / this.itemsPerPage);
        if (totalPages <= 1) return;
        
        const paginationHTML = `
            <div class="d-flex justify-content-center align-items-center mt-4">
                <nav>
                    <ul class="pagination mb-0">
                        <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                            <a class="page-link" href="#" onclick="goToPage(${this.currentPage - 1}); return false;">
                                <i class="bi bi-chevron-left"></i>
                            </a>
                        </li>
                        ${Array.from({ length: totalPages }, (_, i) => i + 1).map(page => `
                            <li class="page-item ${page === this.currentPage ? 'active' : ''}">
                                <a class="page-link" href="#" onclick="goToPage(${page}); return false;">
                                    ${page}
                                </a>
                            </li>
                        `).join('')}
                        <li class="page-item ${this.currentPage === totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="#" onclick="goToPage(${this.currentPage + 1}); return false;">
                                <i class="bi bi-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        `;
        
        document.getElementById('couponsContainer').insertAdjacentHTML('beforeend', paginationHTML);
    }
    
    /**
     * Update stats
     */
    updateStats() {
        const now = new Date();
        
        const totalCoupons = this.filteredCoupons.length;
        const activeCoupons = this.filteredCoupons.filter(c => c.active).length;
        
        const upcomingCoupons = this.filteredCoupons.filter(c => {
            const startDate = new Date(c.startDate);
            return c.active && now < startDate;
        }).length;
        
        const expiredCoupons = this.filteredCoupons.filter(c => {
            const endDate = new Date(c.endDate);
            return c.active && now > endDate;
        }).length;
        
        document.getElementById('totalCoupons').textContent = totalCoupons;
        document.getElementById('activeCoupons').textContent = activeCoupons;
        document.getElementById('upcomingCoupons').textContent = upcomingCoupons;
        document.getElementById('expiredCoupons').textContent = expiredCoupons;
    }
    
    /**
     * Create coupon
     */
    async createCoupon() {
        const code = document.getElementById('couponCode').value;
        const discountPercent = parseFloat(document.getElementById('discountPercent').value);
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const minOrderAmount = parseFloat(document.getElementById('minOrderAmount').value) || 0;
        const isActive = document.getElementById('couponIsActive').checked;
        
        // Validate
        if (!code || !discountPercent || !startDate || !endDate) {
            this.showNotification('Vui lòng nhập đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        if (discountPercent < 1 || discountPercent > 100) {
            this.showNotification('% giảm giá phải từ 1% đến 100%', 'warning');
            return;
        }
        
        if (new Date(startDate) >= new Date(endDate)) {
            this.showNotification('Ngày kết thúc phải sau ngày bắt đầu', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    code,
                    discountPercent,
                    startDate,
                    endDate,
                    minOrderAmount,
                    active: isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Tạo mã giảm giá thành công', 'success');
                const modalElement = document.getElementById('addCouponModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                document.getElementById('addCouponForm').reset();
                this.loadCoupons();
            } else {
                let errorMsg = result.message || 'Tạo mã giảm giá thất bại';
                if (result.errors) {
                    errorMsg = Object.values(result.errors).join(', ');
                }
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error creating coupon:', error);
            this.showNotification('Không thể tạo mã giảm giá', 'danger');
        }
    }
    
    /**
     * Edit coupon
     */
    async editCoupon(code) {
        const coupon = this.coupons.find(c => c.code === code);
        if (!coupon) {
            this.showNotification('Không tìm thấy mã giảm giá', 'danger');
            return;
        }
        
        // Fill form
        document.getElementById('editCouponCode').value = coupon.code;
        document.getElementById('editCouponCodeDisplay').value = coupon.code;
        document.getElementById('editDiscountPercent').value = coupon.discountPercent;
        document.getElementById('editStartDate').value = this.formatDateInput(coupon.startDate);
        document.getElementById('editEndDate').value = this.formatDateInput(coupon.endDate);
        document.getElementById('editMinOrderAmount').value = coupon.minOrderAmount;
        document.getElementById('editCouponIsActive').checked = coupon.active;
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('editCouponModal'));
        modal.show();
    }
    
    /**
     * Update coupon
     */
    async updateCoupon() {
        const code = document.getElementById('editCouponCode').value;
        const discountPercent = parseFloat(document.getElementById('editDiscountPercent').value);
        const startDate = document.getElementById('editStartDate').value;
        const endDate = document.getElementById('editEndDate').value;
        const minOrderAmount = parseFloat(document.getElementById('editMinOrderAmount').value) || 0;
        const isActive = document.getElementById('editCouponIsActive').checked;
        
        // Validate
        if (!discountPercent || !startDate || !endDate) {
            this.showNotification('Vui lòng nhập đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        if (discountPercent < 1 || discountPercent > 100) {
            this.showNotification('% giảm giá phải từ 1% đến 100%', 'warning');
            return;
        }
        
        if (new Date(startDate) >= new Date(endDate)) {
            this.showNotification('Ngày kết thúc phải sau ngày bắt đầu', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${code}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    discountPercent,
                    startDate,
                    endDate,
                    minOrderAmount,
                    active: isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật mã giảm giá thành công', 'success');
                const modalElement = document.getElementById('editCouponModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadCoupons();
            } else {
                let errorMsg = result.message || 'Cập nhật mã giảm giá thất bại';
                if (result.errors) {
                    errorMsg = Object.values(result.errors).join(', ');
                }
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error updating coupon:', error);
            this.showNotification('Không thể cập nhật mã giảm giá', 'danger');
        }
    }
    
    /**
     * Toggle coupon status
     */
    async toggleCouponStatus(code) {
        try {
            const response = await fetch(`${this.apiEndpoint}/${code}/toggle-status`, {
                method: 'POST'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật trạng thái thành công', 'success');
                this.loadCoupons();
            } else {
                this.showNotification(result.message || 'Cập nhật trạng thái thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error toggling coupon status:', error);
            this.showNotification('Không thể cập nhật trạng thái', 'danger');
        }
    }
    
    /**
     * Reset filters
     */
    resetFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('filterStatus').value = '';
        document.getElementById('filterValidity').value = '';
        this.applyFilters();
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
    
    /**
     * Helper: Format date
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
    
    /**
     * Helper: Format date for input
     */
    formatDateInput(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }
    
    /**
     * Helper: Escape HTML
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Global instance
let couponsManagement;

// Initialize Coupons Management
function initializeCouponsManagement() {
    if (couponsManagement) {
        delete couponsManagement;
    }
    couponsManagement = new CouponsManagement();
}

// Global functions for onclick handlers
function createCoupon() {
    if (couponsManagement) {
        couponsManagement.createCoupon();
    }
}

function updateCoupon() {
    if (couponsManagement) {
        couponsManagement.updateCoupon();
    }
}

function editCoupon(code) {
    if (couponsManagement) {
        couponsManagement.editCoupon(code);
    }
}

function toggleCouponStatus(code) {
    if (couponsManagement) {
        couponsManagement.toggleCouponStatus(code);
    }
}

function resetFilters() {
    if (couponsManagement) {
        couponsManagement.resetFilters();
    }
}

function goToPage(page) {
    if (couponsManagement) {
        couponsManagement.currentPage = page;
        couponsManagement.renderCoupons();
    }
}

// Export for admin.js
window.initializeCouponsManagement = initializeCouponsManagement;

