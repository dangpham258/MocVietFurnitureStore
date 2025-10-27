// ========================================
// BANNER MANAGEMENT JAVASCRIPT
// ========================================

console.log('Banner Management JS loaded successfully!');

class BannersManagement {
    constructor() {
        this.apiEndpoint = '/admin/banners/api';
        this.banners = [];
        this.currentEditingIndex = -1;
        
        this.init();
    }
    
    init() {
        this.loadBanners();
        this.setupUploadArea();
        this.setupEditImageInput();
        this.setupFilenamePreview();
    }
    
    /**
     * Setup filename preview
     */
    setupFilenamePreview() {
        // Add modal
        const bannerTitleInput = document.getElementById('bannerTitle');
        if (bannerTitleInput) {
            bannerTitleInput.addEventListener('input', () => {
                this.updateFilenamePreview();
            });
        }
        
        // Edit modal
        const editBannerTitleInput = document.getElementById('editBannerTitle');
        const editBannerOrderInput = document.getElementById('editBannerOrderNumber');
        if (editBannerTitleInput) {
            editBannerTitleInput.addEventListener('input', () => {
                this.updateEditFilenamePreview();
            });
        }
        if (editBannerOrderInput) {
            editBannerOrderInput.addEventListener('input', () => {
                this.updateEditFilenamePreview();
            });
        }
    }
    
    /**
     * Update filename preview (Add modal)
     */
    updateFilenamePreview() {
        const titleInput = document.getElementById('bannerTitle');
        const orderInput = document.getElementById('bannerOrderNumber');
        const previewSpan = document.getElementById('filenamePreview');
        
        if (!titleInput || !orderInput || !previewSpan) return;
        
        const title = titleInput.value.trim();
        const orderInputValue = orderInput.value.trim();
        
        if (!title && !orderInputValue) {
            previewSpan.textContent = 'chưa có';
            previewSpan.className = 'fw-bold text-muted';
            return;
        }
        
        const orderNumber = orderInputValue ? orderInputValue.padStart(2, '0') : '00';
        
        if (!title) {
            previewSpan.textContent = 'chưa có';
            previewSpan.className = 'fw-bold text-muted';
            return;
        }
        
        // Sanitize title similar to backend
        const sanitized = this.sanitizeTitle(title);
        const filename = `${orderNumber}_${sanitized}.jpg`;
        
        previewSpan.textContent = filename;
        previewSpan.className = 'fw-bold text-primary';
    }
    
    /**
     * Update filename preview (Edit modal)
     */
    updateEditFilenamePreview() {
        const titleInput = document.getElementById('editBannerTitle');
        const orderInput = document.getElementById('editBannerOrderNumber');
        const previewSpan = document.getElementById('editFilenamePreview');
        
        if (!titleInput || !orderInput || !previewSpan) return;
        
        const title = titleInput.value.trim();
        const orderInputValue = orderInput.value.trim();
        
        if (!title && !orderInputValue) {
            previewSpan.textContent = 'giữ nguyên';
            previewSpan.className = 'fw-bold text-muted';
            return;
        }
        
        const orderNumber = orderInputValue ? orderInputValue.padStart(2, '0') : '';
        
        if (!title) {
            previewSpan.textContent = 'giữ nguyên';
            previewSpan.className = 'fw-bold text-muted';
            return;
        }
        
        // Sanitize title similar to backend
        const sanitized = this.sanitizeTitle(title);
        const filename = orderNumber ? `${orderNumber}_${sanitized}.jpg` : `${sanitized}.jpg`;
        
        previewSpan.textContent = filename;
        previewSpan.className = 'fw-bold text-primary';
    }
    
    /**
     * Sanitize title (same logic as backend)
     * Convert Vietnamese characters to ASCII
     */
    sanitizeTitle(title) {
        // First remove Vietnamese accents/diacritics
        const withoutAccents = this.removeVietnameseAccents(title.trim());
        
        return withoutAccents.toLowerCase()
                   .replace(/\s+/g, '-')
                   .replace(/[^a-z0-9\-]/g, '')
                   .replace(/-+/g, '-')
                   .replace(/^-|-$/g, '');
    }
    
    /**
     * Remove Vietnamese accents/diacritics
     * Same logic as backend FileUploadService
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
     * Setup upload area
     */
    setupUploadArea() {
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('bannerImageInput');
        
        if (!uploadArea || !fileInput) return;
        
        uploadArea.addEventListener('click', () => fileInput.click());
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        });
        uploadArea.addEventListener('dragleave', () => {
            uploadArea.classList.remove('dragover');
        });
        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                // Validate file size (10MB = 10485760 bytes)
                if (files[0].size > 10485760) {
                    this.showNotification('Kích thước ảnh vượt quá 10MB!', 'danger');
                    return;
                }
                fileInput.files = files;
                this.handleImagePreview(fileInput.files[0], 'previewImg');
            }
        });
        
        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                const file = e.target.files[0];
                // Validate file size (10MB = 10485760 bytes)
                if (file.size > 10485760) {
                    this.showNotification('Kích thước ảnh vượt quá 10MB!', 'danger');
                    fileInput.value = ''; // Clear the input
                    return;
                }
                this.handleImagePreview(file, 'previewImg');
            } else {
                // User canceled file selection - hide preview
                const preview = document.getElementById('imagePreview');
                const previewImg = document.getElementById('previewImg');
                if (preview) preview.style.display = 'none';
                if (previewImg) previewImg.src = '';
            }
        });
    }
    
    /**
     * Setup edit image input
     */
    setupEditImageInput() {
        const editImageInput = document.getElementById('editBannerImageInput');
        if (!editImageInput) return;
        
        editImageInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                const file = e.target.files[0];
                // Validate file size (10MB = 10485760 bytes)
                if (file.size > 10485760) {
                    this.showNotification('Kích thước ảnh vượt quá 10MB!', 'danger');
                    editImageInput.value = ''; // Clear the input
                    return;
                }
                const reader = new FileReader();
                reader.onload = (e) => {
                    document.getElementById('currentImg').src = e.target.result;
                };
                reader.readAsDataURL(file);
            } else {
                // User canceled file selection - restore original image
                const banner = this.banners[this.currentEditingIndex];
                if (banner && document.getElementById('currentImg')) {
                    document.getElementById('currentImg').src = banner.imageUrl;
                }
            }
        });
    }
    
    /**
     * Handle image preview
     */
    handleImagePreview(file, previewId) {
        const preview = document.getElementById('imagePreview');
        const previewImg = document.getElementById(previewId);
        
        if (file && preview && previewImg) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    }
    
    /**
     * Load banners
     */
    async loadBanners() {
        try {
            const response = await fetch(`${this.apiEndpoint}/list`);
            if (!response.ok) throw new Error('Failed to load banners');
            this.banners = await response.json();
            
            this.renderBanners();
            this.updateStats();
        } catch (error) {
            console.error('Error loading banners:', error);
            this.showNotification('Không thể tải danh sách banner', 'danger');
        }
    }
    
    /**
     * Render banners
     */
    renderBanners() {
        const container = document.getElementById('bannersContainer');
        if (!container) return;
        
        if (this.banners.length === 0) {
            container.innerHTML = `
                <div class="col-12">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body text-center py-5">
                            <i class="bi bi-images fs-1 text-muted"></i>
                            <p class="text-muted mt-3 mb-0">Chưa có banner nào</p>
                        </div>
                    </div>
                </div>
            `;
            return;
        }
        
        // Display banners in the order returned by backend (sorted by order number)
        container.innerHTML = this.banners.map((banner, index) => 
            this.renderBannerCard(banner, index)
        ).join('');
    }
    
    /**
     * Render banner card
     */
    renderBannerCard(banner, index) {
        const statusBadge = banner.isActive 
            ? '<span class="badge bg-success">Đang hiển thị</span>'
            : '<span class="badge bg-secondary">Đã tắt</span>';
        
        const linkBadge = banner.linkUrl 
            ? `<span class="badge bg-info ms-1"><i class="bi bi-link-45deg"></i> Có liên kết</span>`
            : '';
        
        // Extract order number from imageUrl (e.g., "/static/images/banners/12_aa.jpg" -> "12")
        let orderNumber = 'N/A';
        if (banner.imageUrl) {
            const match = banner.imageUrl.match(/(\d{2})_/);
            if (match) {
                orderNumber = match[1];
            }
        }
        
        return `
            <div class="col-lg-3 col-md-4 col-sm-6 mb-4">
                <div class="card border-0 shadow-sm banner-card h-100">
                    <div class="position-relative">
                        <!-- Order Number Badge -->
                        <div class="position-absolute top-0 start-0 m-2" style="z-index: 10;">
                            <span class="badge bg-primary" style="font-size: 0.875rem; padding: 0.5rem 0.75rem; box-shadow: 0 2px 8px rgba(0,0,0,0.2);">STT: ${orderNumber}</span>
                        </div>
                        <div class="banner-image-wrapper">
                            <img src="${banner.imageUrl}" 
                                 class="card-img-top banner-image-preview" 
                                 alt="${banner.title || 'Banner'}"
                                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                            <div class="banner-placeholder" style="display: none; background: #f8f9fa; height: 200px; border-radius: 8px; align-items: center; justify-content: center; flex-direction: column;">
                                <i class="bi bi-image fs-1 text-muted mb-2"></i>
                                <span class="text-muted small">Không có ảnh</span>
                            </div>
                        </div>
                        <div class="position-absolute top-0 end-0 m-2">
                            ${statusBadge}
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h6 class="card-title mb-0">${banner.title || 'Chưa có tiêu đề'}</h6>
                            ${linkBadge}
                        </div>
                        ${banner.linkUrl ? `<small class="text-muted d-block"><i class="bi bi-link"></i> ${this.truncateUrl(banner.linkUrl, 40)}</small>` : ''}
                    </div>
                    <div class="card-footer bg-white border-0">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <small class="text-muted">
                                <i class="bi bi-calendar me-1"></i>
                                ${this.formatDate(banner.createdAt)}
                            </small>
                            <div>
                                ${banner.linkUrl ? `<a href="${this.normalizeUrl(banner.linkUrl)}" target="_blank" class="btn btn-sm btn-outline-primary" title="Truy cập URL">
                                    <i class="bi bi-box-arrow-up-right"></i>
                                </a>` : ''}
                                <button class="btn btn-sm btn-outline-success" onclick="editBanner(${index})" title="Sửa">
                                    <i class="bi bi-pencil"></i>
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="deleteBannerId(${banner.id})" title="Xóa">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
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
        const total = this.banners.length;
        const active = this.banners.filter(b => b.isActive).length;
        const inactive = total - active;
        const withLink = this.banners.filter(b => b.linkUrl && b.linkUrl.trim() !== '').length;
        
        document.getElementById('totalBanners').textContent = total;
        document.getElementById('activeBanners').textContent = active;
        document.getElementById('inactiveBanners').textContent = inactive;
        document.getElementById('bannersWithLink').textContent = withLink;
    }
    
    /**
     * Show edit modal
     */
    showEditModal(index) {
        const banner = this.banners[index];
        if (!banner) return;
        
        this.currentEditingIndex = index;
        
        // Extract order number from imageUrl (e.g., "/static/images/banners/00_title.jpg" -> "00")
        let orderNumber = '00';
        if (banner.imageUrl) {
            const match = banner.imageUrl.match(/(\d{2})_/);
            if (match) {
                orderNumber = match[1];
            }
        }
        
        document.getElementById('editBannerId').value = banner.id;
        document.getElementById('editBannerOrderNumber').value = orderNumber;
        document.getElementById('editBannerTitle').value = banner.title || '';
        document.getElementById('editBannerLink').value = banner.linkUrl || '';
        document.getElementById('editBannerActive').checked = banner.isActive;
        
        const currentImg = document.getElementById('currentImg');
        if (currentImg) {
            currentImg.src = banner.imageUrl;
        }
        
        const modal = new bootstrap.Modal(document.getElementById('editBannerModal'));
        modal.show();
    }
    
    /**
     * Create banner
     */
    async createBanner() {
        const imageInput = document.getElementById('bannerImageInput');
        const orderNumber = document.getElementById('bannerOrderNumber').value;
        const title = document.getElementById('bannerTitle').value.trim();
        const link = document.getElementById('bannerLink').value.trim();
        const active = document.getElementById('bannerActive').checked;
        
        // Validate image
        if (!imageInput.files || imageInput.files.length === 0) {
            this.showNotification('Vui lòng chọn ảnh banner', 'warning');
            return;
        }
        
        // Validate title (required for filename generation)
        if (!title) {
            this.showNotification('Vui lòng nhập tiêu đề banner', 'warning');
            return;
        }
        
        // Validate order number (00-99)
        const orderNum = parseInt(orderNumber);
        if (isNaN(orderNum) || orderNum < 0 || orderNum > 99) {
            this.showNotification('Số thứ tự phải từ 00 đến 99', 'warning');
            return;
        }
        
        // Check if order number already exists (for create only)
        const orderStr = orderNumber.padStart(2, '0');
        const existingBanner = this.banners.find(b => {
            if (b.imageUrl) {
                const match = b.imageUrl.match(/(\d{2})_/);
                return match && match[1] === orderStr;
            }
            return false;
        });
        if (existingBanner) {
            this.showNotification(`Số thứ tự ${orderStr} đã được sử dụng bởi banner "${existingBanner.title}"`, 'warning');
            return;
        }
        
        const formData = new FormData();
        formData.append('image', imageInput.files[0]);
        formData.append('orderNumber', orderNumber);
        formData.append('title', title);
        formData.append('linkUrl', link);
        formData.append('isActive', active);
        
        try {
            const response = await fetch(`${this.apiEndpoint}/create`, {
                method: 'POST',
                body: formData
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Thêm banner thành công', 'success');
                const modal = bootstrap.Modal.getInstance(document.getElementById('addBannerModal'));
                if (modal) modal.hide();
                this.resetAddForm();
                this.loadBanners();
            } else {
                this.showNotification(result.message || 'Thêm banner thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error creating banner:', error);
            this.showNotification('Không thể thêm banner', 'danger');
        }
    }
    
    /**
     * Update banner
     */
    async updateBanner() {
        const bannerId = document.getElementById('editBannerId').value;
        const orderNumber = document.getElementById('editBannerOrderNumber').value;
        const title = document.getElementById('editBannerTitle').value.trim();
        const link = document.getElementById('editBannerLink').value.trim();
        const active = document.getElementById('editBannerActive').checked;
        const imageInput = document.getElementById('editBannerImageInput');
        
        // Validate title (required for filename generation)
        if (!title) {
            this.showNotification('Vui lòng nhập tiêu đề banner', 'warning');
            return;
        }
        
        // Validate order number (00-99)
        const orderNum = parseInt(orderNumber);
        if (isNaN(orderNum) || orderNum < 0 || orderNum > 99) {
            this.showNotification('Số thứ tự phải từ 00 đến 99', 'warning');
            return;
        }
        
        // Check if order number already exists (for update - excluding current banner)
        const orderStr = orderNumber.padStart(2, '0');
        const existingBanner = this.banners.find(b => {
            if (b.id.toString() !== bannerId && b.imageUrl) {
                const match = b.imageUrl.match(/(\d{2})_/);
                return match && match[1] === orderStr;
            }
            return false;
        });
        if (existingBanner) {
            this.showNotification(`Số thứ tự ${orderStr} đã được sử dụng bởi banner "${existingBanner.title}"`, 'warning');
            return;
        }
        
        const formData = new FormData();
        
        if (imageInput.files && imageInput.files.length > 0) {
            formData.append('image', imageInput.files[0]);
        }
        
        formData.append('orderNumber', orderNumber);
        formData.append('title', title);
        formData.append('linkUrl', link);
        formData.append('isActive', active);
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${bannerId}`, {
                method: 'PUT',
                body: formData
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật banner thành công', 'success');
                const modal = bootstrap.Modal.getInstance(document.getElementById('editBannerModal'));
                if (modal) modal.hide();
                this.resetEditForm();
                this.loadBanners();
            } else {
                this.showNotification(result.message || 'Cập nhật banner thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error updating banner:', error);
            this.showNotification('Không thể cập nhật banner', 'danger');
        }
    }
    
    /**
     * Delete banner
     */
    async deleteBanner(bannerId) {
        if (!confirm('Bạn có chắc chắn muốn xóa banner này?')) {
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${bannerId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Xóa banner thành công', 'success');
                this.loadBanners(); // Reload from server
            } else {
                this.showNotification(result.message || 'Xóa banner thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error deleting banner:', error);
            this.showNotification('Không thể xóa banner', 'danger');
        }
    }
    
    /**
     * Reset add form
     */
    resetAddForm() {
        document.getElementById('addBannerForm').reset();
        
        // Reset specific inputs
        document.getElementById('bannerOrderNumber').value = '';
        document.getElementById('bannerTitle').value = '';
        document.getElementById('bannerLink').value = '';
        document.getElementById('bannerActive').checked = true;
        document.getElementById('bannerImageInput').value = '';
        
        // Reset image preview
        document.getElementById('imagePreview').style.display = 'none';
        document.getElementById('previewImg').src = '';
        
        // Update filename preview
        document.getElementById('filenamePreview').textContent = 'chưa có';
        document.getElementById('filenamePreview').className = 'fw-bold text-muted';
    }
    
    /**
     * Helper: Normalize URL - Add protocol if missing
     */
    normalizeUrl(url) {
        if (!url) return '';
        // If URL doesn't start with http:// or https://, add http://
        if (!url.match(/^https?:\/\//i)) {
            return 'http://' + url;
        }
        return url;
    }
    
    /**
     * Reset edit form
     */
    resetEditForm() {
        document.getElementById('editBannerForm').reset();
        const editBannerImageInput = document.getElementById('editBannerImageInput');
        if (editBannerImageInput) {
            editBannerImageInput.value = '';
        }
        const editFilenamePreview = document.getElementById('editFilenamePreview');
        if (editFilenamePreview) {
            editFilenamePreview.textContent = 'giữ nguyên';
            editFilenamePreview.className = 'fw-bold text-muted';
        }
    }
    
    /**
     * Helper: Truncate URL
     */
    truncateUrl(url, maxLength) {
        if (!url) return '';
        return url.length > maxLength ? url.substring(0, maxLength) + '...' : url;
    }
    
    /**
     * Helper: Format date
     */
    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
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
let bannersManagement;

// Initialize Banners Management
function initializeBannersManagement() {
    if (bannersManagement) {
        delete bannersManagement;
    }
    bannersManagement = new BannersManagement();
}

// Global functions for filename preview
function updateFilenamePreview() {
    if (bannersManagement) {
        bannersManagement.updateFilenamePreview();
    }
}

function updateEditFilenamePreview() {
    if (bannersManagement) {
        bannersManagement.updateEditFilenamePreview();
    }
}

// Global functions for onclick handlers
function editBanner(index) {
    if (bannersManagement) {
        bannersManagement.showEditModal(index);
    }
}

function deleteBannerId(bannerId) {
    if (bannersManagement) {
        bannersManagement.deleteBanner(bannerId);
    }
}

function createBanner() {
    if (bannersManagement) {
        bannersManagement.createBanner();
    }
}

function updateBanner() {
    if (bannersManagement) {
        bannersManagement.updateBanner();
    }
}

// Export for admin.js
window.initializeBannersManagement = initializeBannersManagement;

