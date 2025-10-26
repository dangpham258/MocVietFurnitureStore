// ========================================
// SHIPPING FEE MANAGEMENT JAVASCRIPT
// ========================================

console.log('Shipping Fee Management JS loaded successfully!');

class ShippingManagement {
    constructor() {
        this.apiEndpoint = '/admin/shipping/api';
        this.zones = [];
        this.provinces = [];
        this.mappings = [];
        
        this.init();
    }
    
    init() {
        this.loadData();
    }
    
    /**
     * Load all data (zones, provinces, mappings)
     */
    async loadData() {
        try {
            // Load zones with shipping fees
            const zonesResponse = await fetch(`${this.apiEndpoint}/zones`);
            if (!zonesResponse.ok) throw new Error('Failed to load zones');
            this.zones = await zonesResponse.json();
            
            // Load provinces
            const provincesResponse = await fetch(`${this.apiEndpoint}/provinces`);
            if (!provincesResponse.ok) throw new Error('Failed to load provinces');
            this.provinces = await provincesResponse.json();
            
            // Load mappings
            const mappingsResponse = await fetch(`${this.apiEndpoint}/mappings`);
            if (!mappingsResponse.ok) throw new Error('Failed to load mappings');
            this.mappings = await mappingsResponse.json();
            
            this.renderZones();
            this.updateStats();
        } catch (error) {
            console.error('Error loading data:', error);
            this.showNotification('Không thể tải dữ liệu phí vận chuyển', 'danger');
        }
    }
    
    /**
     * Render zones with provinces
     */
    renderZones() {
        const container = document.getElementById('zonesContainer');
        if (!container) return;
        
        if (this.zones.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-geo-alt text-muted" style="font-size: 3rem;"></i>
                    <p class="text-muted mt-3">Chưa có dữ liệu miền giao hàng</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = this.zones.map(zone => this.renderZoneCard(zone)).join('');
    }
    
    /**
     * Render individual zone card
     */
    renderZoneCard(zone) {
        // Get provinces for this zone
        const zoneProvinces = this.mappings
            .filter(m => m.zoneId === zone.id)
            .map(m => {
                const province = this.provinces.find(p => p.id === m.provinceId);
                return { ...m, province };
            })
            .filter(m => m.province);
        
        const feeFormatted = new Intl.NumberFormat('vi-VN').format(zone.baseFee || 0);
        
        return `
            <div class="card mb-4 shadow-sm border-0">
                <div class="card-header bg-light">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h5 class="mb-0">
                                <i class="bi bi-geo-alt-fill me-2 text-primary"></i>
                                ${this.escapeHtml(zone.name)}
                            </h5>
                        </div>
                        <div class="col-md-4 text-center">
                            <span class="badge bg-light text-dark fs-6">
                                <i class="bi bi-cash-coin me-1"></i>
                                ${feeFormatted} đ
                            </span>
                        </div>
                        <div class="col-md-2 text-end">
                            <button class="btn btn-sm btn-outline-primary" onclick="editShippingFee(${zone.id})">
                                <i class="bi bi-pencil"></i>
                            </button>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="text-muted small">
                                <i class="bi bi-list-ul me-1"></i>
                                ${zoneProvinces.length} tỉnh/thành
                            </span>
                            <button class="btn btn-sm btn-success" onclick="showAddProvinceModal(${zone.id})">
                                <i class="bi bi-plus-circle me-1"></i>Thêm tỉnh/thành
                            </button>
                        </div>
                    </div>
                    ${zoneProvinces.length > 0 ? this.renderProvincesList(zoneProvinces, zone.id) : this.renderEmptyState(zone.id)}
                </div>
            </div>
        `;
    }
    
    /**
     * Render provinces list for a zone
     */
    renderProvincesList(zoneProvinces, zoneId) {
        return `
            <div class="row g-2">
                ${zoneProvinces.map(mapping => `
                    <div class="col-md-4 col-lg-3">
                        <div class="d-flex align-items-center bg-light rounded p-2">
                            <i class="bi bi-geo me-2 text-primary"></i>
                            <span class="flex-grow-1 small">${this.escapeHtml(mapping.province.name)}</span>
                            <button class="btn btn-sm btn-link text-danger p-0 ms-2" onclick="removeProvinceFromZone(${mapping.id}, '${this.escapeHtml(mapping.province.name)}', ${zoneId})">
                                <i class="bi bi-x-circle"></i>
                            </button>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    }
    
    /**
     * Render empty state for a zone
     */
    renderEmptyState(zoneId) {
        return `
            <div class="text-center py-4 text-muted">
                <i class="bi bi-inbox fs-3 mb-2"></i>
                <p class="small mb-0">Chưa có tỉnh/thành nào</p>
            </div>
        `;
    }
    
    /**
     * Update stats
     */
    updateStats() {
        const totalZones = this.zones.length;
        const totalProvinces = this.provinces.length;
        
        // Calculate average shipping fee
        const totalFees = this.zones.reduce((sum, zone) => sum + (zone.baseFee || 0), 0);
        const avgFee = totalZones > 0 ? Math.round(totalFees / totalZones) : 0;
        const avgFeeFormatted = new Intl.NumberFormat('vi-VN').format(avgFee);
        
        document.getElementById('totalZones').textContent = totalZones;
        document.getElementById('totalProvinces').textContent = totalProvinces;
        document.getElementById('avgShippingFee').textContent = `${avgFeeFormatted} đ`;
    }
    
    /**
     * Edit shipping fee for a zone
     */
    async editShippingFee(zoneId) {
        const zone = this.zones.find(z => z.id === zoneId);
        if (!zone) {
            this.showNotification('Không tìm thấy miền', 'danger');
            return;
        }
        
        // Fill form
        document.getElementById('editZoneId').value = zone.id;
        document.getElementById('editZoneName').value = zone.name;
        document.getElementById('editBaseFee').value = zone.baseFee || 0;
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('editShippingFeeModal'));
        modal.show();
    }
    
    /**
     * Update shipping fee
     */
    async updateShippingFee() {
        const zoneId = parseInt(document.getElementById('editZoneId').value);
        const baseFee = parseFloat(document.getElementById('editBaseFee').value);
        
        if (baseFee < 0) {
            this.showNotification('Phí vận chuyển không được âm', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/fees/${zoneId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    baseFee
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật phí vận chuyển thành công', 'success');
                const modalElement = document.getElementById('editShippingFeeModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadData();
            } else {
                let errorMsg = result.message || 'Cập nhật phí vận chuyển thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error updating shipping fee:', error);
            this.showNotification('Không thể cập nhật phí vận chuyển', 'danger');
        }
    }
    
    /**
     * Show add province modal
     */
    showAddProvinceModal(zoneId) {
        const zone = this.zones.find(z => z.id === zoneId);
        if (!zone) {
            this.showNotification('Không tìm thấy miền', 'danger');
            return;
        }
        
        document.getElementById('addProvinceZoneId').value = zone.id;
        document.getElementById('addProvinceZoneName').value = zone.name;
        document.getElementById('addProvinceName').value = '';
        
        const modal = new bootstrap.Modal(document.getElementById('addProvinceModal'));
        modal.show();
    }
    
    /**
     * Add province to zone
     */
    async addProvinceToZone() {
        const zoneId = parseInt(document.getElementById('addProvinceZoneId').value);
        const provinceName = document.getElementById('addProvinceName').value.trim();
        
        if (!provinceName) {
            this.showNotification('Vui lòng nhập tên tỉnh/thành', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/mappings`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    zoneId,
                    provinceName
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Thêm tỉnh/thành vào miền thành công', 'success');
                const modalElement = document.getElementById('addProvinceModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                document.getElementById('addProvinceForm').reset();
                this.loadData();
            } else {
                let errorMsg = result.message || 'Thêm tỉnh/thành thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error adding province:', error);
            this.showNotification('Không thể thêm tỉnh/thành', 'danger');
        }
    }
    
    /**
     * Show remove province modal
     */
    showRemoveProvinceModal(mappingId, provinceName, zoneId) {
        const zone = this.zones.find(z => z.id === zoneId);
        if (!zone) {
            this.showNotification('Không tìm thấy miền', 'danger');
            return;
        }
        
        document.getElementById('removeProvinceId').value = mappingId;
        document.getElementById('removeProvinceName').textContent = provinceName;
        document.getElementById('removeProvinceZoneName').textContent = zone.name;
        
        const modal = new bootstrap.Modal(document.getElementById('removeProvinceModal'));
        modal.show();
    }
    
    /**
     * Remove province from zone
     */
    async removeProvinceFromZone(mappingId, provinceName, zoneId) {
        this.showRemoveProvinceModal(mappingId, provinceName, zoneId);
    }
    
    /**
     * Confirm remove province
     */
    async confirmRemoveProvince() {
        const mappingId = document.getElementById('removeProvinceId').value;
        
        try {
            const response = await fetch(`${this.apiEndpoint}/mappings/${mappingId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Xóa tỉnh/thành khỏi miền thành công', 'success');
                const modalElement = document.getElementById('removeProvinceModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadData();
            } else {
                this.showNotification(result.message || 'Xóa tỉnh/thành thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error removing province:', error);
            this.showNotification('Không thể xóa tỉnh/thành', 'danger');
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
let shippingManagement;

// Initialize Shipping Management
function initializeShippingManagement() {
    if (shippingManagement) {
        delete shippingManagement;
    }
    shippingManagement = new ShippingManagement();
}

// Global functions for onclick handlers
function editShippingFee(zoneId) {
    if (shippingManagement) {
        shippingManagement.editShippingFee(zoneId);
    }
}

function updateShippingFee() {
    if (shippingManagement) {
        shippingManagement.updateShippingFee();
    }
}

function showAddProvinceModal(zoneId) {
    if (shippingManagement) {
        shippingManagement.showAddProvinceModal(zoneId);
    }
}

function addProvinceToZone() {
    if (shippingManagement) {
        shippingManagement.addProvinceToZone();
    }
}

function removeProvinceFromZone(mappingId, provinceName, zoneId) {
    if (shippingManagement) {
        shippingManagement.removeProvinceFromZone(mappingId, provinceName, zoneId);
    }
}

function confirmRemoveProvince() {
    if (shippingManagement) {
        shippingManagement.confirmRemoveProvince();
    }
}

// Export for admin.js
window.initializeShippingManagement = initializeShippingManagement;

