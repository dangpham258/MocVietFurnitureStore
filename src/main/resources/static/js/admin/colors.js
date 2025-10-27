// Colors Management System
class ColorsManagement {
    constructor() {
        this.colors = [];
        this.filteredColors = [];
        this.apiEndpoint = '/admin/colors/api';
        
        this.init();
    }
    
    init() {
        this.loadColors();
        this.bindEvents();
    }
    
    /**
     * Load danh sách colors từ API
     */
    async loadColors() {
        try {
            const response = await fetch(`${this.apiEndpoint}/list`);
            const data = await response.json();
            this.colors = data;
            this.applyFilters();
        } catch (error) {
            console.error('Error loading colors:', error);
            this.showNotification('Không thể tải danh sách màu sắc', 'danger');
        }
    }
    
    /**
     * Apply filters
     */
    applyFilters() {
        let filtered = [...this.colors];
        
        // Search filter
        const searchTerm = document.getElementById('searchInput')?.value.toLowerCase() || '';
        if (searchTerm) {
            filtered = filtered.filter(color => 
                (color.name && color.name.toLowerCase().includes(searchTerm)) ||
                (color.slug && color.slug.toLowerCase().includes(searchTerm))
            );
        }
        
        // Status filter
        const statusFilter = document.getElementById('filterStatus')?.value || '';
        if (statusFilter === 'active') {
            filtered = filtered.filter(color => color.isActive);
        } else if (statusFilter === 'inactive') {
            filtered = filtered.filter(color => !color.isActive);
        }
        
        this.filteredColors = filtered;
        this.renderColors();
        this.updateStats();
    }
    
    /**
     * Render colors
     */
    renderColors() {
        const grid = document.getElementById('colorsGrid');
        
        if (!grid) return;
        
        if (this.filteredColors.length === 0) {
            grid.innerHTML = '<div class="col-12"><p class="text-center text-muted py-5">Không có dữ liệu</p></div>';
            return;
        }
        
        grid.innerHTML = this.filteredColors.map(color => this.createColorCard(color)).join('');
        
        // Update count badge
        const countBadge = document.getElementById('colorsCount');
        if (countBadge) {
            countBadge.textContent = `${this.filteredColors.length} màu`;
        }
    }
    
    /**
     * Create color card HTML
     */
    createColorCard(color) {
        const hexColor = color.hex || '#cccccc';
        const statusBadge = color.isActive 
            ? '<span class="badge bg-success">Đang kích hoạt</span>' 
            : '<span class="badge bg-danger">Đã vô hiệu hóa</span>';
        
        return `
            <div class="col-md-3 col-sm-6 col-12">
                <div class="card shadow-sm h-100">
                    <div class="card-body">
                        <!-- Color Preview -->
                        <div class="d-flex justify-content-center mb-3">
                            <div class="color-preview rounded" style="width: 80px; height: 80px; background-color: ${hexColor}; border: 2px solid #dee2e6;">
                                ${!color.hex ? '<i class="bi bi-palette d-flex align-items-center justify-content-center h-100 text-muted fs-4"></i>' : ''}
                            </div>
                        </div>
                        
                        <!-- Color Info -->
                        <h6 class="card-title text-center mb-2">${this.escapeHtml(color.name)}</h6>
                        <div class="text-center mb-3">
                            ${statusBadge}
                        </div>
                        
                        <!-- Details -->
                        <div class="small text-muted mb-2">
                            <i class="bi bi-link-45deg"></i> ${this.escapeHtml(color.slug)}
                        </div>
                        ${color.hex ? `<div class="small text-muted">
                            <i class="bi bi-code-square"></i> ${this.escapeHtml(color.hex)}
                        </div>` : ''}
                        
                        <!-- Actions -->
                        <div class="d-flex gap-2 justify-content-center mt-3">
                            <button class="btn btn-sm btn-outline-primary" onclick="editColor(${color.id})">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-${color.isActive ? 'warning' : 'success'}" 
                                    onclick="toggleColorStatus(${color.id})">
                                <i class="bi bi-${color.isActive ? 'lock' : 'unlock'}"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
    
    /**
     * Update stats
     */
    updateStats() {
        const totalColors = this.filteredColors.length;
        const activeColors = this.filteredColors.filter(c => c.isActive).length;
        const inactiveColors = totalColors - activeColors;
        
        const totalCard = document.getElementById('totalColors');
        const activeCard = document.getElementById('activeColors');
        const inactiveCard = document.getElementById('inactiveColors');
        
        if (totalCard) totalCard.textContent = totalColors;
        if (activeCard) activeCard.textContent = activeColors;
        if (inactiveCard) inactiveCard.textContent = inactiveColors;
    }
    
    /**
     * Create color
     */
    async createColor() {
        const name = document.getElementById('colorName').value;
        const slug = document.getElementById('colorSlug').value;
        const hex = document.getElementById('colorHex').value;
        const isActive = document.getElementById('colorIsActive').checked;
        
        if (!name || !slug) {
            this.showNotification('Vui lòng nhập đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name,
                    slug,
                    hex: hex || null,
                    isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Tạo màu sắc thành công', 'success');
                const modalElement = document.getElementById('addColorModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                document.getElementById('addColorForm').reset();
                this.loadColors();
            } else {
                let errorMsg = result.message || 'Tạo màu sắc thất bại';
                if (result.errors) {
                    errorMsg = Object.values(result.errors).join(', ');
                }
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error creating color:', error);
            this.showNotification('Không thể tạo màu sắc', 'danger');
        }
    }
    
    /**
     * Edit color
     */
    async editColor(id) {
        const color = this.colors.find(c => c.id === id);
        if (!color) {
            this.showNotification('Không tìm thấy màu sắc', 'danger');
            return;
        }
        
        document.getElementById('editColorId').value = color.id;
        document.getElementById('editColorName').value = color.name;
        document.getElementById('editColorSlug').value = color.slug;
        document.getElementById('editColorHex').value = color.hex || '';
        document.getElementById('editColorIsActive').checked = color.isActive;
        
        // Disable slug field if color has images (slug is used in image paths)
        const slugInput = document.getElementById('editColorSlug');
        const slugHelpText = slugInput.parentElement.querySelector('.form-text');
        if (color.hasImages) {
            slugInput.disabled = true;
            slugInput.title = 'Không thể thay đổi slug khi màu sắc đã có ảnh sản phẩm';
            if (slugHelpText) {
                slugHelpText.textContent = 'Không thể chỉnh sửa (đã có ảnh sản phẩm)';
                slugHelpText.classList.add('text-danger');
            }
        } else {
            slugInput.disabled = false;
            slugInput.title = '';
            if (slugHelpText) {
                slugHelpText.textContent = '';
                slugHelpText.classList.remove('text-danger');
            }
        }
        
        const modal = new bootstrap.Modal(document.getElementById('editColorModal'));
        modal.show();
    }
    
    /**
     * Update color
     */
    async updateColor() {
        const id = document.getElementById('editColorId').value;
        const name = document.getElementById('editColorName').value;
        const slug = document.getElementById('editColorSlug').value;
        const hex = document.getElementById('editColorHex').value;
        const isActive = document.getElementById('editColorIsActive').checked;
        
        if (!name || !slug) {
            this.showNotification('Vui lòng nhập đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name,
                    slug,
                    hex: hex || null,
                    isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật màu sắc thành công', 'success');
                const modalElement = document.getElementById('editColorModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadColors();
            } else {
                let errorMsg = result.message || 'Cập nhật màu sắc thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error updating color:', error);
            this.showNotification('Không thể cập nhật màu sắc', 'danger');
        }
    }
    
    /**
     * Toggle color status
     */
    async toggleColorStatus(id) {
        if (!confirm('Bạn có chắc muốn thay đổi trạng thái màu sắc này?')) {
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}/toggle-status`, {
                method: 'POST'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success) {
                this.showNotification('Cập nhật trạng thái thành công', 'success');
                this.loadColors();
            } else {
                this.showNotification(result.message || 'Cập nhật trạng thái thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error toggling color status:', error);
            this.showNotification('Không thể cập nhật trạng thái', 'danger');
        }
    }
    
    /**
     * Reset filters
     */
    resetFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('filterStatus').value = '';
        this.applyFilters();
    }
    
    /**
     * Bind events
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
        
        // Auto-generate slug from name
        const colorName = document.getElementById('colorName');
        const colorSlug = document.getElementById('colorSlug');
        if (colorName && colorSlug) {
            colorName.addEventListener('input', function() {
                if (!colorSlug.dataset.edited) {
                    const slug = colorsManagement.sanitizeTitle(this.value);
                    colorSlug.value = slug;
                }
            });
            
            colorSlug.addEventListener('input', function() {
                this.dataset.edited = 'true';
            });
        }
    }
    
    /**
     * Sanitize title to slug (same logic as banners)
     */
    sanitizeTitle(title) {
        if (!title) return "";
        
        // Remove Vietnamese accents first
        const withoutAccents = this.removeVietnameseAccents(title);
        
        return withoutAccents
            .toLowerCase()
            .replace(/[^a-z0-9\s-]/g, '')
            .replace(/\s+/g, '-')
            .replace(/-+/g, '-')
            .replace(/^-|-$/g, '');
    }
    
    /**
     * Remove Vietnamese accents/diacritics
     */
    removeVietnameseAccents(str) {
        if (!str) return "";
        
        return str
            .replace(/à/g, "a").replace(/á/g, "a").replace(/ả/g, "a").replace(/ã/g, "a").replace(/ạ/g, "a")
            .replace(/â/g, "a").replace(/ầ/g, "a").replace(/ấ/g, "a").replace(/ẩ/g, "a").replace(/ẫ/g, "a").replace(/ậ/g, "a")
            .replace(/ă/g, "a").replace(/ằ/g, "a").replace(/ắ/g, "a").replace(/ẳ/g, "a").replace(/ẵ/g, "a").replace(/ặ/g, "a")
            .replace(/è/g, "e").replace(/é/g, "e").replace(/ẻ/g, "e").replace(/ẽ/g, "e").replace(/ẹ/g, "e")
            .replace(/ê/g, "e").replace(/ề/g, "e").replace(/ế/g, "e").replace(/ể/g, "e").replace(/ễ/g, "e").replace(/ệ/g, "e")
            .replace(/đ/g, "d")
            .replace(/ì/g, "i").replace(/í/g, "i").replace(/ỉ/g, "i").replace(/ĩ/g, "i").replace(/ị/g, "i")
            .replace(/ò/g, "o").replace(/ó/g, "o").replace(/ỏ/g, "o").replace(/õ/g, "o").replace(/ọ/g, "o")
            .replace(/ô/g, "o").replace(/ồ/g, "o").replace(/ố/g, "o").replace(/ổ/g, "o").replace(/ỗ/g, "o").replace(/ộ/g, "o")
            .replace(/ơ/g, "o").replace(/ờ/g, "o").replace(/ớ/g, "o").replace(/ở/g, "o").replace(/ỡ/g, "o").replace(/ợ/g, "o")
            .replace(/ù/g, "u").replace(/ú/g, "u").replace(/ủ/g, "u").replace(/ũ/g, "u").replace(/ụ/g, "u")
            .replace(/ư/g, "u").replace(/ừ/g, "u").replace(/ứ/g, "u").replace(/ử/g, "u").replace(/ữ/g, "u").replace(/ự/g, "u")
            .replace(/ỳ/g, "y").replace(/ý/g, "y").replace(/ỷ/g, "y").replace(/ỹ/g, "y").replace(/ỵ/g, "y")
            .replace(/À/g, "A").replace(/Á/g, "A").replace(/Ả/g, "A").replace(/Ã/g, "A").replace(/Ạ/g, "A")
            .replace(/Â/g, "A").replace(/Ầ/g, "A").replace(/Ấ/g, "A").replace(/Ẩ/g, "A").replace(/Ẫ/g, "A").replace(/Ậ/g, "A")
            .replace(/Ă/g, "A").replace(/Ằ/g, "A").replace(/Ắ/g, "A").replace(/Ẳ/g, "A").replace(/Ẵ/g, "A").replace(/Ặ/g, "A")
            .replace(/È/g, "E").replace(/É/g, "E").replace(/Ẻ/g, "E").replace(/Ẽ/g, "E").replace(/Ẹ/g, "E")
            .replace(/Ê/g, "E").replace(/Ề/g, "E").replace(/Ế/g, "E").replace(/Ể/g, "E").replace(/Ễ/g, "E").replace(/Ệ/g, "E")
            .replace(/Đ/g, "D")
            .replace(/Ì/g, "I").replace(/Í/g, "I").replace(/Ỉ/g, "I").replace(/Ĩ/g, "I").replace(/Ị/g, "I")
            .replace(/Ò/g, "O").replace(/Ó/g, "O").replace(/Ỏ/g, "O").replace(/Õ/g, "O").replace(/Ọ/g, "O")
            .replace(/Ô/g, "O").replace(/Ồ/g, "O").replace(/Ố/g, "O").replace(/Ổ/g, "O").replace(/Ỗ/g, "O").replace(/Ộ/g, "O")
            .replace(/Ơ/g, "O").replace(/Ờ/g, "O").replace(/Ớ/g, "O").replace(/Ở/g, "O").replace(/Ỡ/g, "O").replace(/Ợ/g, "O")
            .replace(/Ù/g, "U").replace(/Ú/g, "U").replace(/Ủ/g, "U").replace(/Ũ/g, "U").replace(/Ụ/g, "U")
            .replace(/Ư/g, "U").replace(/Ừ/g, "U").replace(/Ứ/g, "U").replace(/Ử/g, "U").replace(/Ữ/g, "U").replace(/Ự/g, "U")
            .replace(/Ỳ/g, "Y").replace(/Ý/g, "Y").replace(/Ỷ/g, "Y").replace(/Ỹ/g, "Y").replace(/Ỵ/g, "Y");
    }
    
    /**
     * Show notification
     */
    showNotification(message, type) {
        if (window.showNotification) {
            window.showNotification(message, type);
        } else {
            alert(message);
        }
    }
    
    /**
     * Escape HTML
     */
    escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text ? text.replace(/[&<>"']/g, m => map[m]) : '';
    }
}

// Global functions for onclick handlers
let colorsManagement;

function createColor() {
    if (colorsManagement) {
        colorsManagement.createColor();
    }
}

function editColor(id) {
    if (colorsManagement) {
        colorsManagement.editColor(id);
    }
}

function updateColor() {
    if (colorsManagement) {
        colorsManagement.updateColor();
    }
}

function toggleColorStatus(id) {
    if (colorsManagement) {
        colorsManagement.toggleColorStatus(id);
    }
}

function resetFilters() {
    if (colorsManagement) {
        colorsManagement.resetFilters();
    }
}

// Function to initialize colors management
function initializeColorsManagement() {
    if (window.location.pathname.includes('/admin/colors')) {
        if (colorsManagement) {
            delete colorsManagement;
        }
        
        try {
            colorsManagement = new ColorsManagement();
        } catch (error) {
            console.error('Error initializing ColorsManagement:', error);
        }
    }
}

// Initialize when DOM is loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeColorsManagement);
} else {
    initializeColorsManagement();
}

// Export for global access
window.ColorsManagement = ColorsManagement;
window.initializeColorsManagement = initializeColorsManagement;

