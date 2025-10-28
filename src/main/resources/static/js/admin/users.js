// ========================================
// USER MANAGEMENT JAVASCRIPT
// ========================================
// File này chứa logic quản lý users cho admin

console.log('Users Management JS loaded successfully!');

class UsersManagement {
    constructor() {
        this.users = [];
        this.filteredUsers = [];
        this.currentPage = 1;
        this.pageSize = 10;
        this.apiEndpoint = '/admin/users/api';
        this.activeFilters = {
            search: '',
            role: '',
            status: ''
        };
        
        this.init();
    }
    
    init() {
        this.loadUsers();
        this.bindEvents();
    }
    
    /**
     * Load danh sách users từ API
     */
    async loadUsers() {
        try {
            const response = await fetch(`${this.apiEndpoint}/list`);
            const data = await response.json();
            this.users = data;
            this.applyFilters();
        } catch (error) {
            console.error('Error loading users:', error);
            this.showNotification('Không thể tải danh sách users', 'danger');
        }
    }
    
    applyFilters() {
        let filtered = [...this.users];
        
        // Search filter
        if (this.activeFilters.search) {
            const keyword = this.activeFilters.search.toLowerCase();
            filtered = filtered.filter(user => 
                (user.fullName && user.fullName.toLowerCase().includes(keyword)) ||
                (user.email && user.email.toLowerCase().includes(keyword)) ||
                (user.username && user.username.toLowerCase().includes(keyword)) ||
                (user.phone && user.phone.toLowerCase().includes(keyword))
            );
        }
        
        // Role filter
        if (this.activeFilters.role) {
            filtered = filtered.filter(user => user.roleName === this.activeFilters.role);
        }
        
        // Status filter
        if (this.activeFilters.status) {
            const isActive = this.activeFilters.status === 'active';
            filtered = filtered.filter(user => user.isActive === isActive);
        }
        
        this.filteredUsers = filtered;
        this.currentPage = 1;
        this.renderUsers();
        this.renderPagination();
        this.updateStats();
    }
    
    /**
     * Render danh sách users vào table
     */
    renderUsers() {
        const tbody = document.querySelector('#usersTable tbody');
        if (!tbody) return;
        
        tbody.innerHTML = '';
        
        if (this.filteredUsers.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">
                        Không có dữ liệu
                    </td>
                </tr>
            `;
            return;
        }
        
        // Logic phân trang
        const startIndex = (this.currentPage - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        const paginatedUsers = this.filteredUsers.slice(startIndex, endIndex);
        
        paginatedUsers.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>
                    <div class="d-flex align-items-center">
                        <div class="bg-primary bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 40px; height: 40px;">
                            <i class="bi bi-person-fill text-primary"></i>
                        </div>
                        <div>
                            <div class="fw-bold">${user.username}</div>
                            <small class="text-muted">${user.fullName}</small>
                        </div>
                    </div>
                </td>
                <td>${user.email}</td>
                <td><span class="badge ${this.getRoleBadgeClass(user.roleName)}">${user.roleName}</span></td>
                <td>
                    <span class="badge ${user.isActive ? 'bg-success' : 'bg-danger'}">
                        ${user.isActive ? 'Hoạt động' : 'Tạm khóa'}
                    </span>
                </td>
                <td>${this.formatDate(user.createdAt)}</td>
                <td>
                    <button class="btn btn-sm btn-outline-info me-1" onclick="usersManagement.viewUser(${user.id})" data-bs-toggle="modal" data-bs-target="#viewUserModal">
                        <i class="bi bi-eye"></i>
                    </button>
                    ${user.roleName !== 'ADMIN' ? `<button class="btn btn-sm btn-outline-warning" onclick="usersManagement.toggleStatus(${user.id})">
                        <i class="bi bi-${user.isActive ? 'lock' : 'unlock'}"></i>
                    </button>` : ''}
                </td>
            `;
            tbody.appendChild(row);
        });
        
        // Chỉ update stats khi load ALL users, không update khi search/filter
        // this.updateStats();
    }
    
    /**
     * Xem chi tiết user
     */
    async viewUser(userId) {
        try {
            const response = await fetch(`${this.apiEndpoint}/${userId}`);
            const user = await response.json();
            
            // Điền modal
            const modal = document.getElementById('viewUserModal');
            if (!modal) {
                console.error('Modal không tồn tại');
                return;
            }
            
            const h5 = modal.querySelector('.modal-body h5');
            const p = modal.querySelector('.modal-body p');
            const col8 = modal.querySelector('.modal-body .col-md-8');
            
            if (h5) h5.textContent = user.username;
            if (p) p.textContent = user.fullName;
            
            const detailsHtml = `
                <div class="row mb-2">
                    <div class="col-4"><strong>ID:</strong></div>
                    <div class="col-8">${user.id}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Email:</strong></div>
                    <div class="col-8">${user.email}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Số điện thoại:</strong></div>
                    <div class="col-8">${user.phone || 'N/A'}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Giới tính:</strong></div>
                    <div class="col-8">${user.gender || 'N/A'}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Ngày sinh:</strong></div>
                    <div class="col-8">${user.dob ? this.formatDate(user.dob) : 'N/A'}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Vai trò:</strong></div>
                    <div class="col-8"><span class="badge ${this.getRoleBadgeClass(user.roleName)}">${user.roleName}</span></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Trạng thái:</strong></div>
                    <div class="col-8"><span class="badge ${user.isActive ? 'bg-success' : 'bg-danger'}">${user.isActive ? 'Hoạt động' : 'Tạm khóa'}</span></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4"><strong>Ngày tạo:</strong></div>
                    <div class="col-8">${this.formatDate(user.createdAt)}</div>
                </div>
            `;
            
            if (col8) col8.innerHTML = detailsHtml;
        } catch (error) {
            console.error('Error viewing user:', error);
            this.showNotification('Không thể xem chi tiết user', 'danger');
        }
    }
    
    /**
     * Thay đổi trạng thái user (khóa/mở khóa)
     */
    async toggleStatus(userId) {
        if (!confirm('Bạn có chắc muốn thay đổi trạng thái user này?')) {
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${userId}/toggle-status`, {
                method: 'POST'
            });
            
            const result = await response.json();
            
            if (result.success) {
                this.showNotification(result.message, 'success');
                this.loadUsers();
            } else {
                this.showNotification(result.message, 'danger');
            }
        } catch (error) {
            console.error('Error toggling user status:', error);
            this.showNotification('Không thể cập nhật trạng thái', 'danger');
        }
    }
    
    /**
     * Lấy class badge vai trò
     */
    getRoleBadgeClass(roleName) {
        const classes = {
            'ADMIN': 'bg-danger',
            'MANAGER': 'bg-warning',
            'DELIVERY': 'bg-info',
            'CUSTOMER': 'bg-primary'
        };
        return classes[roleName] || 'bg-secondary';
    }
    
    /**
     * Định dạng ngày
     */
    formatDate(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    }
    
    /**
     * Tạo user
     */
    async createUser() {
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const fullName = document.getElementById('fullName').value;
        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const roleName = document.getElementById('roleId').value;
        const dob = document.getElementById('dob').value;
        const gender = document.getElementById('gender').value;
        const isActive = document.getElementById('isActive').checked;
        
        if (!username || !email || !fullName || !password || !confirmPassword || !roleName) {
            this.showNotification('Vui lòng nhập đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        if (password !== confirmPassword) {
            this.showNotification('Mật khẩu xác nhận không khớp', 'danger');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username,
                    email,
                    fullName,
                    phone: phone || null,
                    password,
                    roleName,
                    dob: dob || null,
                    gender: gender || null,
                    isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Tạo user thành công', 'success');
                const modalElement = document.getElementById('addUserModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                document.getElementById('addUserForm').reset();
                this.loadUsers();
            } else {
                // Hiển thị lỗi validation chi tiết
                let errorMsg = result.message || 'Tạo user thất bại';
                if (result.errors) {
                    const errorDetails = Object.values(result.errors).join(', ');
                    errorMsg = errorDetails;
                }
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error creating user:', error);
            this.showNotification('Không thể tạo user', 'danger');
        }
    }
    
    /**
     * Cập nhật stats
     */
    updateStats() {
        const totalUsers = this.filteredUsers.length;
        const activeUsers = this.filteredUsers.filter(u => u.isActive).length;
        const managerCount = this.filteredUsers.filter(u => u.roleName === 'MANAGER').length;
        const deliveryCount = this.filteredUsers.filter(u => u.roleName === 'DELIVERY').length;
        
        // Cập nhật stats cards
        const statsRow = document.getElementById('statsRow');
        if (!statsRow) return;
        
        const cards = statsRow.querySelectorAll('.card .card-title');
        
        if (cards.length >= 1) cards[0].textContent = totalUsers;
        if (cards.length >= 2) cards[1].textContent = activeUsers;
        if (cards.length >= 3) cards[2].textContent = managerCount;
        if (cards.length >= 4) cards[3].textContent = deliveryCount;
    }
    
    /**
     * Helper method để hiển thị thông báo
     */
    showNotification(message, type = 'info') {
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
        } else {
            console.log(`📢 Notification [${type.toUpperCase()}]: ${message}`);
        }
    }
    
    /**
     * Bind events (kết nối events)
     */
    bindEvents() {
        // Tìm kiếm
        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.activeFilters.search = e.target.value.trim();
                this.applyFilters();
            });
        }
        
        // Filter vai trò
        const roleFilter = document.getElementById('filterRole');
        if (roleFilter) {
            roleFilter.addEventListener('change', (e) => {
                this.activeFilters.role = e.target.value;
                this.applyFilters();
            });
        }
        
        // Filter trạng thái
        const statusFilter = document.getElementById('filterStatus');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                this.activeFilters.status = e.target.value;
                this.applyFilters();
            });
        }
    }
    
    /**
     * Reset tất cả filters
     */
    resetFilters() {
        this.activeFilters = { search: '', role: '', status: '' };
        const searchEl = document.querySelector('.search-input');
        if (searchEl) searchEl.value = '';
        const roleEl = document.getElementById('filterRole');
        if (roleEl) roleEl.value = '';
        const statusEl = document.getElementById('filterStatus');
        if (statusEl) statusEl.value = '';
        this.applyFilters();
    }
    
    /**
     * Render phân trang
     */
    renderPagination() {
        const pagination = document.getElementById('pagination');
        if (!pagination) return;
        
        const totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
        
        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }
        
        let paginationHtml = '';
        
        // Button Previous
        paginationHtml += `
            <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="usersManagement.goToPage(${this.currentPage - 1}); return false;">
                    <i class="bi bi-chevron-left"></i>
                </a>
            </li>
        `;
        
        // Số trang
        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= this.currentPage - 2 && i <= this.currentPage + 2)) {
                paginationHtml += `
                    <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="usersManagement.goToPage(${i}); return false;">${i}</a>
                    </li>
                `;
            } else if (i === this.currentPage - 3 || i === this.currentPage + 3) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }
        
        // Button Next
        paginationHtml += `
            <li class="page-item ${this.currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="usersManagement.goToPage(${this.currentPage + 1}); return false;">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </li>
        `;
        
        pagination.innerHTML = paginationHtml;
    }
    
    /**
     * Chuyển đến trang cụ thể
     */
    goToPage(page) {
        const totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
        if (page < 1 || page > totalPages) return;
        
        this.currentPage = page;
        this.renderUsers();
        this.renderPagination();
    }
    
}

// Function khởi tạo users management
function initializeUsersManagement() {
    if (window.location.pathname.includes('/admin/users')) {
        // Xóa instance hiện có nếu tồn tại
        if (window.usersManagement) {
            delete window.usersManagement;
        }
        
        try {
            window.usersManagement = new UsersManagement();
        } catch (error) {
            console.error('Error initializing UsersManagement:', error);
        }
    }
}

// Khởi tạo khi DOM đã tải xong
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeUsersManagement);
} else {
    // DOM đã tải xong
    initializeUsersManagement();
}

// Xuất ra global access
window.UsersManagement = UsersManagement;
window.initializeUsersManagement = initializeUsersManagement;

