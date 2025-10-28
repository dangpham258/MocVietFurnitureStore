// ========================================
// PAGES MANAGEMENT JAVASCRIPT
// ========================================

console.log('Pages Management JS loaded successfully!');

class PagesManagement {
    constructor() {
        this.apiEndpoint = '/admin/pages/api';
        this.pages = [];
        this.filteredPages = [];
        
        this.searchFilter = '';
        this.statusFilter = '';
        this.sortFilter = 'updated_desc';
        
        this.init();
    }
    
    init() {
        this.loadPages();
        this.setupEventListeners();
    }
    
    
    /**
     * Thiết lập event listeners
     */
    setupEventListeners() {
        // Input tìm kiếm
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', () => {
                this.searchFilter = searchInput.value.toLowerCase();
                this.applyFilters();
            });
        }
        
        // Filter trạng thái
        const statusFilterEl = document.getElementById('statusFilter');
        if (statusFilterEl) {
            statusFilterEl.addEventListener('change', () => {
                this.statusFilter = statusFilterEl.value;
                this.applyFilters();
            });
        }
        
        // Filter sắp xếp
        const sortFilterEl = document.getElementById('sortFilter');
        if (sortFilterEl) {
            sortFilterEl.addEventListener('change', () => {
                this.sortFilter = sortFilterEl.value;
                this.applyFilters();
            });
        }
    }
    
    /**
     * Tải trang tĩnh từ API
     */
    async loadPages() {
        try {
            this.showLoading();
            
            const response = await fetch(`${this.apiEndpoint}/list`);
            if (!response.ok) {
                throw new Error('Failed to load pages');
            }
            
            this.pages = await response.json();
            this.applyFilters();
        } catch (error) {
            console.error('Error loading pages:', error);
            this.showError('Không thể tải danh sách trang tĩnh');
        }
    }
    
    /**
     * Áp dụng filters và render
     */
    applyFilters() {
        // Filter theo tìm kiếm
        let filtered = this.pages;
        
        if (this.searchFilter) {
            filtered = filtered.filter(page => 
                page.title.toLowerCase().includes(this.searchFilter) ||
                page.slug.toLowerCase().includes(this.searchFilter)
            );
        }
        
        // Filter theo trạng thái
        if (this.statusFilter !== '') {
            const isActive = this.statusFilter === 'true';
            filtered = filtered.filter(page => page.isActive === isActive);
        }
        
        // Sắp xếp
        filtered = [...filtered];
        filtered.sort((a, b) => {
            switch(this.sortFilter) {
                case 'updated_desc':
                    return new Date(b.updatedAt) - new Date(a.updatedAt);
                case 'updated_asc':
                    return new Date(a.updatedAt) - new Date(b.updatedAt);
                case 'title_asc':
                    return a.title.localeCompare(b.title);
                case 'title_desc':
                    return b.title.localeCompare(a.title);
                default:
                    return 0;
            }
        });
        
        this.filteredPages = filtered;
        this.renderPages();
        this.updateStats(this.filteredPages);
    }
    
    /**
     * Render danh sách trang tĩnh
     */
    renderPages() {
        const container = document.getElementById('pagesContainer');
        const loadingSpinner = document.getElementById('loadingSpinner');
        const emptyState = document.getElementById('emptyState');
        const noResults = document.getElementById('noResults');
        
        if (!container) return;
        
        // Ẩn loading
        if (loadingSpinner) loadingSpinner.classList.add('d-none');
        
        // Kiểm tra nếu không có trang tĩnh nào
        if (this.pages.length === 0) {
            if (emptyState) emptyState.classList.remove('d-none');
            if (noResults) noResults.classList.add('d-none');
            container.innerHTML = '';
            return;
        }
        
        // Kiểm tra nếu không có kết quả từ filter
        if (this.filteredPages.length === 0) {
            if (emptyState) emptyState.classList.add('d-none');
            if (noResults) noResults.classList.remove('d-none');
            container.innerHTML = '';
            return;
        }
        
        // Ẩn empty states
        if (emptyState) emptyState.classList.add('d-none');
        if (noResults) noResults.classList.add('d-none');
        
        // Render bảng
        container.innerHTML = `
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th style="width: 10%;">Slug</th>
                            <th style="width: 25%;">Tiêu đề</th>
                            <th style="width: 30%;">Nội dung</th>
                            <th style="width: 15%;">Trạng thái</th>
                            <th style="width: 10%;">Cập nhật</th>
                            <th style="width: 10%;" class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${this.filteredPages.map(page => this.renderPageRow(page)).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }
    
    /**
     * Render hàng trang tĩnh
     */
    renderPageRow(page) {
        const statusBadge = page.isActive 
            ? '<span class="badge bg-success">Đang hiển thị</span>' 
            : '<span class="badge bg-secondary">Đã ẩn</span>';
        
        const updatedDate = this.formatDate(page.updatedAt);
        const contentPreview = page.content 
            ? this.stripHtml(page.content).substring(0, 80) + '...' 
            : '(Chưa có nội dung)';
        
        return `
            <tr>
                <td><code class="text-primary">/${page.slug}</code></td>
                <td><strong>${page.title}</strong></td>
                <td class="text-muted small">${contentPreview}</td>
                <td>${statusBadge}</td>
                <td class="text-muted small">${updatedDate}</td>
                <td class="text-center">
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-outline-info" onclick="window.open('/${page.slug}', '_blank')" 
                                title="Truy cập trang">
                            <i class="bi bi-box-arrow-up-right"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-primary" onclick="if(window.pagesManagement){window.pagesManagement.viewPage(${page.id})}" 
                                title="Xem">
                            <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-warning" onclick="if(window.pagesManagement){window.pagesManagement.editPage(${page.id})}" 
                                title="Sửa">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="if(window.pagesManagement){window.pagesManagement.deletePage(${page.id}, '${page.title.replace(/'/g, '&apos;')}')}" 
                                title="Xóa">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }
    
    /**
     * Cập nhật stats cards
     */
    updateStats(data) {
        const totalPages = data.length;
        const activePages = data.filter(p => p.isActive).length;
        const inactivePages = totalPages - activePages;
        
        // Đếm cập nhật gần đây (trong vòng 7 ngày)
        const sevenDaysAgo = new Date();
        sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
        const recentUpdates = data.filter(p => new Date(p.updatedAt) >= sevenDaysAgo).length;
        
        const totalEl = document.getElementById('totalPages');
        const activeEl = document.getElementById('activePages');
        const inactiveEl = document.getElementById('inactivePages');
        const recentEl = document.getElementById('recentUpdates');
        
        if (totalEl) totalEl.textContent = totalPages;
        if (activeEl) activeEl.textContent = activePages;
        if (inactiveEl) inactiveEl.textContent = inactivePages;
        if (recentEl) recentEl.textContent = recentUpdates;
    }
    
    /**
     * Hiển thị loading spinner
     */
    showLoading() {
        const loadingSpinner = document.getElementById('loadingSpinner');
        const emptyState = document.getElementById('emptyState');
        const noResults = document.getElementById('noResults');
        
        if (loadingSpinner) loadingSpinner.classList.remove('d-none');
        if (emptyState) emptyState.classList.add('d-none');
        if (noResults) noResults.classList.add('d-none');
    }
    
    /**
     * Mở modal thêm trang tĩnh
     */
    async openAddPageModal() {
        const modal = new bootstrap.Modal(document.getElementById('addEditPageModal'));
        const modalTitle = document.getElementById('modalTitle');
        const form = document.getElementById('pageForm');
        const contentSection = document.getElementById('contentSection');
        
        // Xóa form
        form.reset();
        document.getElementById('pageId').value = '';
        document.getElementById('pageIsActive').checked = true;
        
        // Xóa Summernote
        if (this.summernoteEditor) {
            this.summernoteEditor.summernote('code', '');
        }
        
        // Ẩn section nội dung cho mode thêm
        if (contentSection) {
            contentSection.style.display = 'none';
        }
        
        // Cập nhật tiêu đề
        if (modalTitle) {
            modalTitle.innerHTML = '<i class="bi bi-plus-circle me-2"></i>Thêm Trang Tĩnh Mới';
        }
        
        modal.show();
    }
    
    /**
     * Xem chi tiết trang tĩnh
     */
    async viewPage(id) {
        try {
            // Xóa nội dung cũ trước
            document.getElementById('viewPageTitle').textContent = 'Đang tải...';
            document.getElementById('viewSlug').textContent = '';
            document.getElementById('viewTitle').textContent = '';
            document.getElementById('viewStatus').innerHTML = '';
            document.getElementById('viewUpdatedAt').textContent = '';
            document.getElementById('viewContent').innerHTML = '';
            
            // Hiển thị modal đầu tiên để làm cho overlay hiển thị
            const modal = new bootstrap.Modal(document.getElementById('viewPageModal'));
            modal.show();
            
            // Hiển thị loading overlay SAU khi modal được hiển thị
            const loadingOverlay = document.getElementById('viewLoadingOverlay');
            if (loadingOverlay) loadingOverlay.classList.remove('d-none');
            
            const response = await fetch(`${this.apiEndpoint}/${id}`);
            if (!response.ok) throw new Error('Failed to load page');
            
            const page = await response.json();
            
            // Điền dữ liệu vào modal xem
            document.getElementById('viewPageTitle').textContent = `Chi tiết: ${page.title}`;
            document.getElementById('viewSlug').textContent = `/${page.slug}`;
            document.getElementById('viewTitle').textContent = page.title;
            
            const statusHtml = page.isActive 
                ? '<span class="badge bg-success">Đang hiển thị</span>' 
                : '<span class="badge bg-secondary">Đã ẩn</span>';
            document.getElementById('viewStatus').innerHTML = statusHtml;
            
            document.getElementById('viewUpdatedAt').textContent = this.formatDate(page.updatedAt);
            
            const contentDiv = document.getElementById('viewContent');
            if (page.content) {
                contentDiv.innerHTML = page.content;
            } else {
                contentDiv.innerHTML = '<em class="text-muted">Chưa có nội dung</em>';
            }
            
            // Ẩn loading overlay khi hoàn tất
            if (loadingOverlay) loadingOverlay.classList.add('d-none');
        } catch (error) {
            console.error('Error loading page:', error);
            this.showNotification('Không thể tải chi tiết trang', 'danger');
            
            // Ẩn loading overlay khi có lỗi
            const loadingOverlay = document.getElementById('viewLoadingOverlay');
            if (loadingOverlay) loadingOverlay.classList.add('d-none');
        }
    }
    
    /**
     * Sửa trang tĩnh
     */
    async editPage(id) {
        try {
            // Xóa dữ liệu form cũ trước
            document.getElementById('pageId').value = '';
            document.getElementById('pageSlug').value = '';
            document.getElementById('pageTitle').value = '';
            document.getElementById('pageIsActive').checked = true;
            
            // Ẩn section nội dung ban đầu
            const contentSectionInit = document.getElementById('contentSection');
            if (contentSectionInit) {
                contentSectionInit.style.display = 'none';
            }
            
            // Hiển thị modal đầu tiên để làm cho overlay hiển thị
            const modal = new bootstrap.Modal(document.getElementById('addEditPageModal'));
            modal.show();
            
            // Hiển thị loading overlay SAU khi modal được hiển thị
            const loadingOverlay = document.getElementById('formLoadingOverlay');
            if (loadingOverlay) loadingOverlay.classList.remove('d-none');
            
            const response = await fetch(`${this.apiEndpoint}/${id}`);
            if (!response.ok) throw new Error('Failed to load page');
            
            const page = await response.json();
            
            // Điền dữ liệu vào form
            document.getElementById('pageId').value = page.id;
            document.getElementById('pageSlug').value = page.slug;
            document.getElementById('pageTitle').value = page.title;
            document.getElementById('pageIsActive').checked = page.isActive;
            
            // Hiển thị section nội dung cho mode sửa
            const contentSection = document.getElementById('contentSection');
            if (contentSection) {
                contentSection.style.display = 'block';
            }
            
            // Khởi tạo Summernote nếu chưa được khởi tạo
            if (!this.summernoteEditor) {
                const editorElement = $('#pageContent');
                if (editorElement.length && typeof $.fn.summernote !== 'undefined') {
                    // Hiển thị loading overlay
                    const loadingOverlay = document.getElementById('formLoadingOverlay');
                    if (loadingOverlay) loadingOverlay.classList.remove('d-none');
                    editorElement.summernote({
                        height: 500,
                        toolbar: [
                            ['style', ['style']],
                            ['font', ['bold', 'italic', 'underline', 'strikethrough', 'clear']],
                            ['fontname', ['fontname']],
                            ['fontsize', ['fontsize']],
                            ['color', ['color']],
                            ['para', ['ul', 'ol', 'paragraph']],
                            ['table', ['table']],
                            ['insert', ['link', 'picture', 'video']],
                            ['view', ['fullscreen', 'codeview', 'undo', 'redo']]
                        ],
                        callbacks: {
                            onImageUpload: function(files) {
                                const file = files[0];
                                const reader = new FileReader();
                                reader.onloadend = function() {
                                    const image = $('<img>').attr('src', reader.result);
                                    $('#pageContent').summernote('insertNode', image[0]);
                                };
                                reader.readAsDataURL(file);
                            }
                        }
                    });
                    this.summernoteEditor = editorElement;
                    if (page.content) {
                        editorElement.summernote('code', page.content);
                    }
                    // Ẩn loading overlay
                    if (loadingOverlay) loadingOverlay.classList.add('d-none');
                }
            } else {
                // Editor đã tồn tại, chỉ điền nội dung
                if (page.content) {
                    this.summernoteEditor.summernote('code', page.content);
                }
                // Ẩn loading overlay vì editor đã tồn tại
                const loadingOverlay = document.getElementById('formLoadingOverlay');
                if (loadingOverlay) loadingOverlay.classList.add('d-none');
            }
            
            // Cập nhật tiêu đề modal
            const modalTitle = document.getElementById('modalTitle');
            if (modalTitle) {
                modalTitle.innerHTML = '<i class="bi bi-pencil me-2"></i>Sửa Trang Tĩnh';
            }
        } catch (error) {
            console.error('Error loading page for edit:', error);
            this.showNotification('Không thể tải thông tin trang', 'danger');
            
            // Ẩn loading overlay khi có lỗi
            const loadingOverlay = document.getElementById('formLoadingOverlay');
            if (loadingOverlay) loadingOverlay.classList.add('d-none');
        }
    }
    
    /**
     * Lưu trang tĩnh (tạo hoặc cập nhật)
     */
    async savePage() {
        const form = document.getElementById('pageForm');
        const pageId = document.getElementById('pageId').value;
        const slug = document.getElementById('pageSlug').value.trim();
        const title = document.getElementById('pageTitle').value.trim();
        const isActive = document.getElementById('pageIsActive').checked;
        
        // Kiểm tra validation
        if (!slug) {
            this.showNotification('Vui lòng nhập slug', 'warning');
            return;
        }
        
        if (!title) {
            this.showNotification('Vui lòng nhập tiêu đề', 'warning');
            return;
        }
        
        // Lấy nội dung từ Summernote
        let content = '';
        if (this.summernoteEditor) {
            content = this.summernoteEditor.summernote('code');
        }
        
        const data = {
            slug: slug,
            title: title,
            content: content,
            isActive: isActive
        };
        
        try {
            // Hiển thị loading trên nút lưu
            const saveBtn = document.getElementById('saveBtn');
            const saveBtnText = document.getElementById('saveBtnText');
            const saveBtnLoading = document.getElementById('saveBtnLoading');
            const cancelBtn = document.getElementById('cancelBtn');
            
            if (saveBtn) saveBtn.disabled = true;
            if (cancelBtn) cancelBtn.disabled = true;
            if (saveBtnText) saveBtnText.classList.add('d-none');
            if (saveBtnLoading) saveBtnLoading.classList.remove('d-none');
            
            const url = pageId ? `${this.apiEndpoint}/${pageId}` : `${this.apiEndpoint}/create`;
            const method = pageId ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            // Kiểm tra nếu response là OK
            if (!response.ok) {
                const errorResult = await response.json();
                throw new Error(errorResult.message || 'Có lỗi xảy ra');
            }
            
            const result = await response.json();
            
            if (result.success) {
                this.showNotification(pageId ? 'Cập nhật trang thành công' : 'Thêm trang thành công', 'success');
                
                // Đóng modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('addEditPageModal'));
                if (modal) modal.hide();
                
                // Tải lại trang tĩnh
                this.loadPages();
            } else {
                this.showNotification(result.message || 'Có lỗi xảy ra', 'danger');
            }
        } catch (error) {
            console.error('Error saving page:', error);
            this.showNotification(error.message || 'Không thể lưu trang', 'danger');
        } finally {
            // Reset trạng thái nút
            const saveBtn = document.getElementById('saveBtn');
            const saveBtnText = document.getElementById('saveBtnText');
            const saveBtnLoading = document.getElementById('saveBtnLoading');
            const cancelBtn = document.getElementById('cancelBtn');
            
            if (saveBtn) saveBtn.disabled = false;
            if (cancelBtn) cancelBtn.disabled = false;
            if (saveBtnText) saveBtnText.classList.remove('d-none');
            if (saveBtnLoading) saveBtnLoading.classList.add('d-none');
        }
    }
    
    /**
     * Xác nhận xóa trang tĩnh
     */
    deletePage(id, title) {
        document.getElementById('deletePageId').value = id;
        document.getElementById('deletePageTitle').textContent = title;
        
        const modal = new bootstrap.Modal(document.getElementById('deletePageModal'));
        modal.show();
    }
    
    /**
     * Xác nhận xóa
     */
    async confirmDelete() {
        const pageId = document.getElementById('deletePageId').value;
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${pageId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (result.success) {
                this.showNotification('Xóa trang thành công', 'success');
                
                // Đóng modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('deletePageModal'));
                if (modal) modal.hide();
                
                // Tải lại trang tĩnh
                this.loadPages();
            } else {
                this.showNotification(result.message || 'Có lỗi xảy ra', 'danger');
            }
        } catch (error) {
            console.error('Error deleting page:', error);
            this.showNotification('Không thể xóa trang', 'danger');
        }
    }
    
    /**
     * Reset filters (làm mới filters)
     */
    resetFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('sortFilter').value = 'updated_desc';
        
        this.searchFilter = '';
        this.statusFilter = '';
        this.sortFilter = 'updated_desc';
        
        this.applyFilters();
    }
    
    /**
     * Format date helper (chuyển đổi chuỗi ngày thành định dạng chuẩn)
     */
    formatDate(dateString) {
        if (!dateString) return '';
        
        if (dateString.includes('/')) {
            return dateString;
        }
        
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return dateString;
        }
        
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }
    
    /**
     * Loại bỏ HTML tags
     */
    stripHtml(html) {
        const tmp = document.createElement('DIV');
        tmp.innerHTML = html;
        return tmp.textContent || tmp.innerText || '';
    }
    
    /**
     * Hiển thị thông báo
     */
    showNotification(message, type = 'info') {
        console.log(`[${type.toUpperCase()}] ${message}`);
        
        // Sử dụng notification system nếu có
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
        } else {
            // Fallback: hiển thị alert
            alert(message);
        }
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    showError(message) {
        this.showNotification(message, 'danger');
    }
}

// Khởi tạo instance global
let pagesManagement;

function initializePagesManagement() {
    if (!pagesManagement) {
        pagesManagement = new PagesManagement();
        // Cũng xuất ra window để dễ truy cập
        window.pagesManagement = pagesManagement;
    }
}

// Khởi tạo tự động khi script tải xong
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializePagesManagement);
} else {
    // DOM đã tải xong, khởi tạo ngay
    initializePagesManagement();
}

// Xuất functions to global scope cho onclick handlers
window.openAddPageModal = () => {
    if (window.pagesManagement) {
        window.pagesManagement.openAddPageModal();
    }
};

window.savePage = () => {
    if (window.pagesManagement) {
        window.pagesManagement.savePage();
    }
};

window.confirmDelete = () => {
    if (window.pagesManagement) {
        window.pagesManagement.confirmDelete();
    }
};

window.resetFilters = () => {
    if (window.pagesManagement) {
        window.pagesManagement.resetFilters();
    }
};
