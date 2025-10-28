// ========================================
// SHOWROOM MANAGEMENT JAVASCRIPT
// ========================================

console.log('Showroom Management JS loaded successfully!');

class ShowroomsManagement {
    constructor() {
        this.apiEndpoint = '/admin/showrooms/api';
        this.showrooms = [];
        this.filteredShowrooms = [];
        this.currentView = 'grid'; // 'grid' or 'list'
        
        this.init();
    }
    
    init() {
        this.loadShowrooms();
        this.bindEvents();
        this.setDefaultView();
    }
    
    /**
     * Bind events (lắng nghe sự kiện)
     */
    bindEvents() {
        // Search input với debounce
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            let timeout;
            searchInput.addEventListener('input', (e) => {
                clearTimeout(timeout);
                timeout = setTimeout(() => {
                    this.applyFilters();
                }, 500);
            });
        }
        
        // Filter dropdowns (lọc dropdown)
        const filterCity = document.getElementById('filterCity');
        const filterStatus = document.getElementById('filterStatus');
        
        if (filterCity) {
            filterCity.addEventListener('change', () => this.applyFilters());
        }
        
        if (filterStatus) {
            filterStatus.addEventListener('change', () => this.applyFilters());
        }
    }
    
    /**
     * Set default view (set view mặc định)
     */
    setDefaultView() {
        // Set view grid làm mặc định
        if (this.currentView === 'grid') {
            document.getElementById('gridViewBtn').classList.add('active');
            document.getElementById('listViewBtn').classList.remove('active');
        }
    }
    
    /**
     * Tải showrooms từ API
     */
    async loadShowrooms() {
        try {
            const response = await fetch(`${this.apiEndpoint}/list`);
            if (!response.ok) throw new Error('Failed to load showrooms');
            
            const data = await response.json();
            this.showrooms = data;
            this.applyFilters();
            this.updateStats();
        } catch (error) {
            console.error('Error loading showrooms:', error);
            this.showNotification('Không thể tải danh sách showroom', 'danger');
            this.showLoading(false);
            this.showEmptyState(true);
        }
    }
    
    /**
     * Áp dụng filters
     */
    applyFilters() {
        let filtered = [...this.showrooms];
        
        // Search filter (lọc tìm kiếm)
        const searchKeyword = document.getElementById('searchInput')?.value.toLowerCase() || '';
        if (searchKeyword) {
            filtered = filtered.filter(showroom => 
                showroom.name?.toLowerCase().includes(searchKeyword) ||
                showroom.address?.toLowerCase().includes(searchKeyword) ||
                showroom.city?.toLowerCase().includes(searchKeyword) ||
                showroom.district?.toLowerCase().includes(searchKeyword)
            );
        }
        
        // City filter (lọc thành phố)
        const filterCity = document.getElementById('filterCity')?.value || '';
        if (filterCity) {
            filtered = filtered.filter(showroom => showroom.city === filterCity);
        }
        
        // Status filter (lọc trạng thái)
        const filterStatus = document.getElementById('filterStatus')?.value || '';
        if (filterStatus) {
            const isActive = filterStatus === 'active';
            filtered = filtered.filter(showroom => showroom.isActive === isActive);
        }
        
        this.filteredShowrooms = filtered;
        this.renderShowrooms();
        this.updateStats();
        
        // Update city filter options (cập nhật options thành phố)
        this.updateCityFilterOptions();
    }
    
    /**
     * Cập nhật options thành phố dựa trên showrooms đã tải
     */
    updateCityFilterOptions() {
        const cities = [...new Set(this.showrooms.map(s => s.city).filter(Boolean))].sort();
        const filterCitySelect = document.getElementById('filterCity');
        
        if (!filterCitySelect) return;
        
        // Keep "Tất cả" option and add cities
        const currentValue = filterCitySelect.value;
        filterCitySelect.innerHTML = '<option value="">Tất cả</option>';
        
        cities.forEach(city => {
            const option = document.createElement('option');
            option.value = city;
            option.textContent = city;
            filterCitySelect.appendChild(option);
        });
        
        // Restore previous selection (khôi phục lựa chọn trước đó)
        filterCitySelect.value = currentValue;
    }
    
    /**
     * Render showrooms (render showroom)
     */
    renderShowrooms() {
        this.hideAllStates();
        
        // Kiểm tra nếu không có showroom nào
        if (this.showrooms.length === 0) {
            this.showEmptyStateWithMessage(true, 'Chưa có showroom nào', 'Hãy thêm showroom đầu tiên của bạn');
            return;
        }
        
        // Kiểm tra nếu kết quả lọc là trống
        if (this.filteredShowrooms.length === 0) {
            this.showEmptyStateWithMessage(true, 'Không có kết quả phù hợp', 'Thử thay đổi bộ lọc hoặc tìm kiếm');
            return;
        }
        
        // Hiển thị showroomContainer
        const container = document.getElementById('showroomContainer');
        if (container) {
            container.style.display = 'block';
        }
        
        if (this.currentView === 'grid') {
            this.renderGridView();
        } else {
            this.renderListView();
        }
    }
    
    /**
     * Render grid view (render view grid)
     */
    renderGridView() {
        const container = document.getElementById('showroomGrid');
        const listView = document.getElementById('showroomList');
        
        if (!container) return;
        
        // Hiển thị grid (với class row của Bootstrap)
        container.style.display = '';
        if (listView) listView.style.display = 'none';
        
        container.innerHTML = this.filteredShowrooms.map(showroom => 
            this.renderShowroomCard(showroom)
        ).join('');
    }
    
    /**
     * Render list view (render view list)
     */
    renderListView() {
        const container = document.getElementById('showroomList');
        const gridView = document.getElementById('showroomGrid');
        
        if (!container) return;
        
        container.style.display = 'block';
        if (gridView) gridView.style.display = 'none';
        
        const tableBody = `
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>Tên Showroom</th>
                        <th>Địa chỉ</th>
                        <th>Quận/Huyện</th>
                        <th>Tỉnh/Thành</th>
                        <th>Liên hệ</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    ${this.filteredShowrooms.map(showroom => this.renderShowroomRow(showroom)).join('')}
                </tbody>
            </table>
        `;
        
        container.innerHTML = tableBody;
    }
    
    /**
     * Render showroom card (view grid)
     */
    renderShowroomCard(showroom) {
        const statusBadge = showroom.isActive 
            ? '<span class="badge bg-success">Đang hoạt động</span>'
            : '<span class="badge bg-secondary">Đã tắt</span>';
        
        // Generate map preview nếu mapEmbed tồn tại
        let mapPreview = '';
        if (showroom.mapEmbed) {
            mapPreview = `
                <div class="showroom-map-preview-full">
                    ${showroom.mapEmbed}
                </div>
            `;
        } else {
            mapPreview = `
                <div class="bg-light d-flex align-items-center justify-content-center" style="height: 100%; min-height: 400px;">
                    <div class="text-center">
                        <i class="bi bi-geo-alt text-muted fs-1"></i>
                        <p class="text-muted mt-2 mb-0">Chưa có bản đồ</p>
                    </div>
                </div>
            `;
        }
        
        return `
            <div class="col-12 mb-4">
                <div class="card border-0 shadow-sm showroom-card">
                    <div class="row g-0">
                        <!-- Map Preview - Left side -->
                        <div class="col-lg-5">
                            ${mapPreview}
                        </div>
                        <!-- Showroom Info - Right side -->
                        <div class="col-lg-7">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <h5 class="card-title mb-0">${showroom.name || 'Chưa có tên'}</h5>
                                    ${statusBadge}
                                </div>
                                
                                <div class="mb-3">
                                    <small class="text-muted d-block">
                                        <i class="bi bi-geo-alt-fill me-1"></i>
                                        <strong>Địa chỉ:</strong> ${showroom.address || ''}
                                    </small>
                                    ${showroom.district ? `
                                        <small class="text-muted d-block">
                                            <i class="bi bi-geo-alt me-1"></i><strong>Quận/Huyện:</strong> ${showroom.district}
                                        </small>
                                    ` : ''}
                                    ${showroom.city ? `
                                        <small class="text-muted d-block">
                                            <i class="bi bi-geo me-1"></i><strong>Tỉnh/Thành:</strong> ${showroom.city}
                                        </small>
                                    ` : ''}
                                </div>
                                
                                <div class="row mb-3">
                                    ${showroom.phone ? `
                                        <div class="col-md-6 mb-2">
                                            <small class="text-muted d-block">
                                                <i class="bi bi-telephone me-1"></i><strong>Điện thoại:</strong> ${showroom.phone}
                                            </small>
                                        </div>
                                    ` : ''}
                                    ${showroom.email ? `
                                        <div class="col-md-6 mb-2">
                                            <small class="text-muted d-block">
                                                <i class="bi bi-envelope me-1"></i><strong>Email:</strong> ${showroom.email}
                                            </small>
                                        </div>
                                    ` : ''}
                                    ${showroom.openHours ? `
                                        <div class="col-md-6 mb-2">
                                            <small class="text-muted d-block">
                                                <i class="bi bi-clock me-1"></i><strong>Giờ mở cửa:</strong> ${showroom.openHours}
                                            </small>
                                        </div>
                                    ` : ''}
                                    <div class="col-md-6 mb-2">
                                        <small class="text-muted d-block">
                                            <i class="bi bi-calendar me-1"></i><strong>Ngày tạo:</strong> ${this.formatDate(showroom.createdAt)}
                                        </small>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="card-footer bg-white border-0">
                                <div class="d-flex justify-content-end gap-2">
                                    <button class="btn btn-sm btn-outline-info" onclick="showroomsManagement.viewShowroom(${showroom.id})" title="Xem chi tiết">
                                        <i class="bi bi-eye"></i> Xem
                                    </button>
                                    <button class="btn btn-sm btn-outline-success" onclick="showroomsManagement.editShowroom(${showroom.id})" title="Sửa">
                                        <i class="bi bi-pencil"></i> Sửa
                                    </button>
                                    <button class="btn btn-sm btn-outline-danger" onclick="showroomsManagement.deleteShowroom(${showroom.id})" title="Xóa">
                                        <i class="bi bi-trash"></i> Xóa
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
    
    /**
     * Render showroom row (view list)
     */
    renderShowroomRow(showroom) {
        const statusBadge = showroom.isActive 
            ? '<span class="badge bg-success">Đang hoạt động</span>'
            : '<span class="badge bg-secondary">Đã tắt</span>';
        
        const contactInfo = `
            ${showroom.phone ? `<small class="d-block"><i class="bi bi-telephone"></i> ${showroom.phone}</small>` : ''}
            ${showroom.email ? `<small class="d-block"><i class="bi bi-envelope"></i> ${showroom.email}</small>` : ''}
        `;
        
        return `
            <tr>
                <td>
                    <strong>${showroom.name || 'Chưa có tên'}</strong>
                    ${showroom.openHours ? `<br><small class="text-muted"><i class="bi bi-clock"></i> ${showroom.openHours}</small>` : ''}
                </td>
                <td>${showroom.address || ''}</td>
                <td>${showroom.district || '-'}</td>
                <td>${showroom.city || ''}</td>
                <td>${contactInfo}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-info" onclick="showroomsManagement.viewShowroom(${showroom.id})" title="Xem">
                            <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn btn-outline-success" onclick="showroomsManagement.editShowroom(${showroom.id})" title="Sửa">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="showroomsManagement.deleteShowroom(${showroom.id})" title="Xóa">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }
    
    /**
     * Cập nhật stats
     */
    updateStats() {
        const total = this.filteredShowrooms.length;
        const active = this.filteredShowrooms.filter(s => s.isActive).length;
        const inactive = total - active;
        const uniqueCities = new Set(this.filteredShowrooms.map(s => s.city).filter(Boolean)).size;
        
        document.getElementById('totalShowrooms').textContent = total;
        document.getElementById('activeShowrooms').textContent = active;
        document.getElementById('inactiveShowrooms').textContent = inactive;
        document.getElementById('totalCities').textContent = uniqueCities;
    }
    
    /**
     * Hiển thị modal thêm
     */
    showAddModal() {
        const modal = new bootstrap.Modal(document.getElementById('addShowroomModal'));
        modal.show();
    }
    
    /**
     * Tạo showroom
     */
    async createShowroom() {
        const name = document.getElementById('showroomName').value.trim();
        const address = document.getElementById('showroomAddress').value.trim();
        const city = document.getElementById('showroomCity').value;
        const district = document.getElementById('showroomDistrict').value.trim();
        const phone = document.getElementById('showroomPhone').value.trim();
        const email = document.getElementById('showroomEmail').value.trim();
        const openHours = document.getElementById('showroomOpenHours').value.trim();
        const mapEmbed = document.getElementById('showroomMapEmbed').value.trim();
        const isActive = document.getElementById('showroomIsActive').checked;
        
        // Validate required fields (kiểm tra các trường bắt buộc)
        if (!name) {
            this.showNotification('Vui lòng nhập tên showroom', 'warning');
            return;
        }
        
        if (!address) {
            this.showNotification('Vui lòng nhập địa chỉ chi tiết', 'warning');
            return;
        }
        
        if (!city) {
            this.showNotification('Vui lòng chọn tỉnh/thành', 'warning');
            return;
        }
        
        // Validate email nếu có
        if (email && !this.isValidEmail(email)) {
            this.showNotification('Email không hợp lệ', 'warning');
            return;
        }
        
        // Validate phone nếu có
        if (phone && !this.isValidPhone(phone)) {
            const cleaned = phone.replace(/\s/g, '');
            if (cleaned.length < 9 || cleaned.length > 20) {
                this.showNotification('Số điện thoại phải có từ 9 đến 20 ký tự', 'warning');
            } else {
                this.showNotification('Số điện thoại chỉ được chứa số, dấu +, -, (), và khoảng trắng', 'warning');
            }
            return;
        }
        
        const data = {
            name,
            address,
            city,
            district,
            phone,
            email,
            openHours,
            mapEmbed,
            isActive
        };
        
        try {
            const response = await fetch(`${this.apiEndpoint}/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Thêm showroom thành công', 'success');
                const modal = bootstrap.Modal.getInstance(document.getElementById('addShowroomModal'));
                if (modal) modal.hide();
                this.resetAddForm();
                this.loadShowrooms();
            } else {
                this.showNotification(result.message || 'Thêm showroom thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error creating showroom:', error);
            this.showNotification('Không thể thêm showroom', 'danger');
        }
    }
    
    /**
     * Sửa showroom
     */
    async editShowroom(showroomId) {
        const showroom = this.showrooms.find(s => s.id === showroomId);
        if (!showroom) {
            this.showNotification('Không tìm thấy showroom', 'danger');
            return;
        }
        
        // Populate edit modal (điền dữ liệu vào modal sửa)
        document.getElementById('editShowroomId').value = showroom.id;
        document.getElementById('editShowroomName').value = showroom.name || '';
        document.getElementById('editShowroomAddress').value = showroom.address || '';
        document.getElementById('editShowroomCity').value = showroom.city || '';
        document.getElementById('editShowroomDistrict').value = showroom.district || '';
        document.getElementById('editShowroomPhone').value = showroom.phone || '';
        document.getElementById('editShowroomEmail').value = showroom.email || '';
        document.getElementById('editShowroomOpenHours').value = showroom.openHours || '';
        document.getElementById('editShowroomMapEmbed').value = showroom.mapEmbed || '';
        document.getElementById('editShowroomIsActive').checked = showroom.isActive;
        
        const modal = new bootstrap.Modal(document.getElementById('editShowroomModal'));
        modal.show();
    }
    
    /**
     * Cập nhật showroom
     */
    async updateShowroom() {
        const showroomId = document.getElementById('editShowroomId').value;
        const name = document.getElementById('editShowroomName').value.trim();
        const address = document.getElementById('editShowroomAddress').value.trim();
        const city = document.getElementById('editShowroomCity').value;
        const district = document.getElementById('editShowroomDistrict').value.trim();
        const phone = document.getElementById('editShowroomPhone').value.trim();
        const email = document.getElementById('editShowroomEmail').value.trim();
        const openHours = document.getElementById('editShowroomOpenHours').value.trim();
        const mapEmbed = document.getElementById('editShowroomMapEmbed').value.trim();
        const isActive = document.getElementById('editShowroomIsActive').checked;
        
        // Validate required fields (kiểm tra các trường bắt buộc)
        if (!name) {
            this.showNotification('Vui lòng nhập tên showroom', 'warning');
            return;
        }
        
        if (!address) {
            this.showNotification('Vui lòng nhập địa chỉ chi tiết', 'warning');
            return;
        }
        
        if (!city) {
            this.showNotification('Vui lòng chọn tỉnh/thành', 'warning');
            return;
        }
        
        // Validate email nếu có
        if (email && !this.isValidEmail(email)) {
            this.showNotification('Email không hợp lệ', 'warning');
            return;
        }
        
        // Validate phone nếu có
        if (phone && !this.isValidPhone(phone)) {
            const cleaned = phone.replace(/\s/g, '');
            if (cleaned.length < 9 || cleaned.length > 20) {
                this.showNotification('Số điện thoại phải có từ 9 đến 20 ký tự', 'warning');
            } else {
                this.showNotification('Số điện thoại chỉ được chứa số, dấu +, -, (), và khoảng trắng', 'warning');
            }
            return;
        }
        
        const data = {
            name,
            address,
            city,
            district,
            phone,
            email,
            openHours,
            mapEmbed,
            isActive
        };
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${showroomId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật showroom thành công', 'success');
                const modal = bootstrap.Modal.getInstance(document.getElementById('editShowroomModal'));
                if (modal) modal.hide();
                this.loadShowrooms();
            } else {
                this.showNotification(result.message || 'Cập nhật showroom thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error updating showroom:', error);
            this.showNotification('Không thể cập nhật showroom', 'danger');
        }
    }
    
    /**
     * Xóa showroom
     */
    async deleteShowroom(showroomId) {
        if (!confirm('Bạn có chắc chắn muốn xóa showroom này?')) {
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${showroomId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Xóa showroom thành công', 'success');
                this.loadShowrooms();
            } else {
                this.showNotification(result.message || 'Xóa showroom thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error deleting showroom:', error);
            this.showNotification('Không thể xóa showroom', 'danger');
        }
    }
    
    /**
     * Xem chi tiết showroom
     */
    async viewShowroom(showroomId) {
        const showroom = this.showrooms.find(s => s.id === showroomId);
        if (!showroom) {
            this.showNotification('Không tìm thấy showroom', 'danger');
            return;
        }
        
        // Generate view content (tạo nội dung xem)
        const statusBadge = showroom.isActive 
            ? '<span class="badge bg-success">Đang hoạt động</span>'
            : '<span class="badge bg-secondary">Đã tắt</span>';
        
        const content = `
            <div class="row">
                <div class="col-md-4">
                    <div class="text-center mb-3">
                        <div class="bg-primary bg-opacity-10 rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 120px; height: 120px;">
                            <i class="bi bi-shop text-primary" style="font-size: 3rem;"></i>
                        </div>
                    </div>
                </div>
                <div class="col-md-8">
                    <h5 class="mb-3">${showroom.name || 'Chưa có tên'}</h5>
                    ${statusBadge}
                </div>
            </div>
            
            <hr>
            
            <div class="mb-3">
                <label class="fw-bold"><i class="bi bi-geo-alt me-1"></i>Địa chỉ:</label>
                <p class="mb-0">${showroom.address || ''}${showroom.district ? `, ${showroom.district}` : ''}${showroom.city ? `, ${showroom.city}` : ''}</p>
            </div>
            
            ${showroom.phone ? `
                <div class="mb-3">
                    <label class="fw-bold"><i class="bi bi-telephone me-1"></i>Số điện thoại:</label>
                    <p class="mb-0">${showroom.phone}</p>
                </div>
            ` : ''}
            
            ${showroom.email ? `
                <div class="mb-3">
                    <label class="fw-bold"><i class="bi bi-envelope me-1"></i>Email:</label>
                    <p class="mb-0">${showroom.email}</p>
                </div>
            ` : ''}
            
            ${showroom.openHours ? `
                <div class="mb-3">
                    <label class="fw-bold"><i class="bi bi-clock me-1"></i>Giờ mở cửa:</label>
                    <p class="mb-0">${showroom.openHours}</p>
                </div>
            ` : ''}
            
            <div class="mb-3">
                <label class="fw-bold"><i class="bi bi-calendar me-1"></i>Ngày tạo:</label>
                <p class="mb-0">${this.formatDate(showroom.createdAt)}</p>
            </div>
            
            ${showroom.mapEmbed ? `
                <div class="mb-3">
                    <label class="fw-bold"><i class="bi bi-map me-1"></i>Bản đồ:</label>
                    <div class="showroom-map-preview">
                        ${showroom.mapEmbed}
                    </div>
                </div>
            ` : ''}
        `;
        
        document.getElementById('viewShowroomContent').innerHTML = content;
        
        const modal = new bootstrap.Modal(document.getElementById('viewShowroomModal'));
        modal.show();
    }
    
    /**
     * Reset add form (reset form thêm)
     */
    resetAddForm() {
        document.getElementById('addShowroomForm').reset();
    }
    
    /**
     * Reset filters (reset filters)
     */
    resetFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('filterCity').value = '';
        document.getElementById('filterStatus').value = '';
        this.applyFilters();
    }
    
    /**
     * Hide all states (ẩn tất cả trạng thái)
     */
    hideAllStates() {
        document.getElementById('loadingState').style.display = 'none';
        document.getElementById('emptyState').style.display = 'none';
        document.getElementById('showroomContainer').style.display = 'none';
    }
    
    /**
     * Hiển thị trạng thái loading
     */
    showLoading(show) {
        document.getElementById('loadingState').style.display = show ? 'block' : 'none';
    }
    
    /**
     * Hiển thị trạng thái trống
     */
    showEmptyState(show) {
        document.getElementById('emptyState').style.display = show ? 'block' : 'none';
    }
    
    /**
     * Hiển thị trạng thái trống với message tùy chỉnh
     */
    showEmptyStateWithMessage(show, title, message) {
        const emptyState = document.getElementById('emptyState');
        const titleEl = document.getElementById('emptyStateTitle');
        const messageEl = document.getElementById('emptyStateMessage');
        
        if (show) {
            if (titleEl) titleEl.textContent = title;
            if (messageEl) messageEl.textContent = message;
        }
        
        emptyState.style.display = show ? 'block' : 'none';
    }
    
    /**
     * Validates email format (kiểm tra định dạng email)
     */
    isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
    
    /**
     * Validates phone format (kiểm tra định dạng số điện thoại)
     */
    isValidPhone(phone) {
        if (!phone || phone.trim().length === 0) {
            return true; // Cho phép trống (trường tùy chọn)
        }
        
        // Kiểm tra độ dài (9-20 ký tự sau khi xóa khoảng trắng)
        const cleaned = phone.replace(/\s/g, '');
        if (cleaned.length < 9 || cleaned.length > 20) {
            return false;
        }
        
        // Cho phép số, +, -, (), và khoảng trắng
        const re = /^[0-9+\-()\s]*$/;
        return re.test(phone);
    }
    
    /**
     * Format date (định dạng ngày)
     */
    formatDate(dateString) {
        if (!dateString) return '';
        
        // Backend đã trả về ngày định dạng (dd/MM/yyyy)
        if (dateString.includes('/')) {
            // Đã ở định dạng dd/MM/yyyy
            return dateString;
        }
        
        // Thử phân tích nếu nó ở định dạng ISO hoặc định dạng khác
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return dateString; // Trả về như là nếu không thể phân tích
        }
        
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }
    
    /**
     * Hiển thị thông báo
     */
    showNotification(message, type = 'info') {
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
        } else {
            console.log(`[${type.toUpperCase()}]: ${message}`);
        }
    }
}

// Instance global
let showroomsManagement;

// Khởi tạo khi script tải xong
function initializeShowroomsManagement() {
    if (showroomsManagement) {
        delete showroomsManagement;
    }
    showroomsManagement = new ShowroomsManagement();
}

// Functions global cho onclick handlers
function toggleView(view) {
    if (showroomsManagement) {
        showroomsManagement.currentView = view;
        showroomsManagement.renderShowrooms();
        
        // Cập nhật trạng thái button
        if (view === 'grid') {
            document.getElementById('gridViewBtn').classList.add('active');
            document.getElementById('listViewBtn').classList.remove('active');
        } else {
            document.getElementById('listViewBtn').classList.add('active');
            document.getElementById('gridViewBtn').classList.remove('active');
        }
    }
}

// Global functions for onclick handlers
function createShowroom() {
    if (showroomsManagement) {
        showroomsManagement.createShowroom();
    }
}

function updateShowroom() {
    if (showroomsManagement) {
        showroomsManagement.updateShowroom();
    }
}

function resetFilters() {
    if (showroomsManagement) {
        showroomsManagement.resetFilters();
    }
}

// Xuất ra admin.js
window.initializeShowroomsManagement = initializeShowroomsManagement;

// Khởi tạo tự động khi script tải xong
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        if (window.location.pathname.includes('/admin/showrooms')) {
            initializeShowroomsManagement();
        }
    });
} else {
    // DOM đã tải xong
    if (window.location.pathname.includes('/admin/showrooms')) {
        initializeShowroomsManagement();
    }
}

