// Categories Management System
class CategoriesManagement {
    constructor() {
        this.categories = [];
        this.filteredCategories = [];
        this.apiEndpoint = '/admin/categories/api';
        
        this.init();
    }
    
    init() {
        this.loadCategories();
        this.bindEvents();
    }
    
    /**
     * Load danh sách categories từ API
     */
    async loadCategories() {
        try {
            const response = await fetch(`${this.apiEndpoint}/list`);
            const data = await response.json();
            this.categories = data;
            this.applyFilters();
        } catch (error) {
            console.error('Error loading categories:', error);
            this.showNotification('Không thể tải danh sách danh mục', 'danger');
        }
    }
    
    /**
     * Apply filters
     */
    applyFilters() {
        let filtered = [...this.categories];
        
        // Search filter
        const searchTerm = document.getElementById('searchInput')?.value.toLowerCase() || '';
        if (searchTerm) {
            filtered = filtered.filter(cat => 
                (cat.name && cat.name.toLowerCase().includes(searchTerm)) ||
                (cat.slug && cat.slug.toLowerCase().includes(searchTerm))
            );
        }
        
        // Type filter
        const typeFilter = document.getElementById('filterType')?.value || '';
        if (typeFilter) {
            filtered = filtered.filter(cat => cat.type === typeFilter);
        }
        
        // Status filter
        const statusFilter = document.getElementById('filterStatus')?.value || '';
        if (statusFilter === 'active') {
            filtered = filtered.filter(cat => cat.isActive);
        } else if (statusFilter === 'inactive') {
            filtered = filtered.filter(cat => !cat.isActive);
        }
        
        this.filteredCategories = filtered;
        this.renderCategories();
        this.updateStats();
    }
    
    /**
     * Render categories tree and collections
     */
    renderCategories() {
        // Separate categories and collections
        const categories = this.filteredCategories.filter(cat => cat.type === 'CATEGORY');
        const collections = this.filteredCategories.filter(cat => cat.type === 'COLLECTION');
        
        // Render Categories tree
        const categoriesTree = document.getElementById('categoriesTree');
        if (categoriesTree) {
            if (categories.length === 0) {
                categoriesTree.innerHTML = '<p class="text-center text-muted py-5">Không có danh mục</p>';
            } else {
                const treeStructure = this.buildTreeStructureFromCategories(categories);
                categoriesTree.innerHTML = this.renderTreeNodes(treeStructure, 0);
            }
        }
        
        // Render Collections list
        const collectionsList = document.getElementById('collectionsList');
        if (collectionsList) {
            if (collections.length === 0) {
                collectionsList.innerHTML = '<p class="text-center text-muted py-5">Không có bộ sưu tập</p>';
            } else {
                collectionsList.innerHTML = this.renderCollectionsList(collections);
            }
        }
    }
    
    /**
     * Build tree structure from categories only
     */
    buildTreeStructureFromCategories(categories) {
        const map = new Map();
        const roots = [];
        
        // Create map of all categories
        categories.forEach(cat => {
            map.set(cat.id, { ...cat, children: [] });
        });
        
        // Build tree
        categories.forEach(cat => {
            const node = map.get(cat.id);
            if (cat.parentId) {
                const parent = map.get(cat.parentId);
                if (parent) {
                    parent.children.push(node);
                } else {
                    // Nếu parent không có trong danh sách, đặt vào roots
                    roots.push(node);
                }
            } else {
                roots.push(node);
            }
        });
        
        return roots;
    }
    
    /**
     * Render collections list (no tree structure)
     */
    renderCollectionsList(collections) {
        return collections.map(collection => {
            const statusBadge = collection.isActive 
                ? '<span class="badge bg-success">Đang kích hoạt</span>' 
                : '<span class="badge bg-danger">Đã vô hiệu hóa</span>';
            
            return `
                <div class="d-flex align-items-center py-2 border-bottom">
                    <div class="flex-grow-1">
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <i class="bi bi-folder2"></i>
                            <strong>${this.escapeHtml(collection.name)}</strong>
                            ${statusBadge}
                        </div>
                        <small class="text-muted ms-4">
                            <i class="bi bi-link-45deg"></i> ${this.escapeHtml(collection.slug)}
                        </small>
                    </div>
                    <div class="actions">
                        <button class="btn btn-sm btn-outline-primary" onclick="editCategory(${collection.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-${collection.isActive ? 'warning' : 'success'}" 
                                onclick="toggleCategoryStatus(${collection.id})">
                            <i class="bi bi-${collection.isActive ? 'lock' : 'unlock'}"></i>
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }
    
    
    /**
     * Render tree nodes recursively
     */
    renderTreeNodes(nodes, level) {
        if (!nodes || nodes.length === 0) return '';
        
        return nodes.map(node => {
            const indent = level * 30;
            const typeBadge = node.type === 'COLLECTION' 
                ? '<span class="badge bg-warning">COLLECTION</span>' 
                : '<span class="badge bg-primary">CATEGORY</span>';
            const statusBadge = node.isActive 
                ? '<span class="badge bg-success">Đang kích hoạt</span>' 
                : '<span class="badge bg-danger">Đã vô hiệu hóa</span>';
            const hasChildren = node.children && node.children.length > 0;
            
            let html = `
                <div class="d-flex align-items-center py-2 border-bottom">
                    <div style="padding-left: ${indent}px;" class="flex-grow-1">
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <i class="bi bi-${hasChildren ? 'folder' : 'folder2'}-${node.isActive ? 'fill' : ''}"></i>
                            <strong>${this.escapeHtml(node.name)}</strong>
                            ${typeBadge}
                            ${statusBadge}
                        </div>
                        <small class="text-muted ms-4">
                            <i class="bi bi-link-45deg"></i> ${this.escapeHtml(node.slug)}
                        </small>
                    </div>
                    <div class="actions">
                        <button class="btn btn-sm btn-outline-primary" onclick="editCategory(${node.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-${node.isActive ? 'warning' : 'success'}" 
                                onclick="toggleCategoryStatus(${node.id})">
                            <i class="bi bi-${node.isActive ? 'lock' : 'unlock'}"></i>
                        </button>
                    </div>
                </div>
            `;
            
            // Render children
            if (hasChildren) {
                html += this.renderTreeNodes(node.children, level + 1);
            }
            
            return html;
        }).join('');
    }
    
    /**
     * Update stats
     */
    updateStats() {
        const totalCategories = this.filteredCategories.length;
        const activeCategories = this.filteredCategories.filter(c => c.isActive).length;
        const categoryType = this.filteredCategories.filter(c => c.type === 'CATEGORY' && !c.parentId).length;
        const collectionType = this.filteredCategories.filter(c => c.type === 'COLLECTION').length;
        
        const totalCard = document.getElementById('totalCategories');
        const activeCard = document.getElementById('activeCategories');
        const categoryCard = document.getElementById('categoryTypeCount');
        const collectionCard = document.getElementById('collectionType');
        
        if (totalCard) totalCard.textContent = totalCategories;
        if (activeCard) activeCard.textContent = activeCategories;
        if (categoryCard) categoryCard.textContent = categoryType;
        if (collectionCard) collectionCard.textContent = collectionType;
    }
    
    /**
     * Create category
     */
    async createCategory() {
        const typeElement = document.getElementById('categoryType');
        const parentIdElement = document.getElementById('parentCategoryId');
        const nameElement = document.getElementById('categoryName');
        const slugElement = document.getElementById('categorySlug');
        const isActiveElement = document.getElementById('categoryIsActive');
        
        const type = typeElement.value;
        const parentId = parentIdElement ? parentIdElement.value : '';
        const name = nameElement.value;
        const slug = slugElement.value;
        const isActive = isActiveElement ? isActiveElement.checked : true;
        
        // Validate required fields
        if (!type || !name || !slug) {
            this.showNotification('Vui lòng nhập đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        // Build request body
        const requestBody = {
            type,
            name,
            slug,
            isActive
        };
        
        // Add parentId only if it has a value
        if (parentId) {
            requestBody.parentId = parentId;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Tạo danh mục thành công', 'success');
                const modalElement = document.getElementById('addCategoryModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                document.getElementById('addCategoryForm').reset();
                this.loadCategories();
            } else {
                let errorMsg = result.message || 'Tạo danh mục thất bại';
                if (result.errors) {
                    errorMsg = Object.values(result.errors).join(', ');
                }
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error creating category:', error);
            this.showNotification('Không thể tạo danh mục', 'danger');
        }
    }
    
    /**
     * Edit category
     */
    async editCategory(id) {
        const category = this.categories.find(c => c.id === id);
        if (!category) {
            this.showNotification('Không tìm thấy danh mục', 'danger');
            return;
        }
        
        document.getElementById('editCategoryId').value = category.id;
        document.getElementById('editCategoryType').value = category.type;
        document.getElementById('editCategoryName').value = category.name;
        document.getElementById('editCategorySlug').value = category.slug;
        document.getElementById('editCategoryIsActive').checked = category.isActive;
        
        // Load parent categories for selection - CHỈ HIỂN THỊ CHO DANH MỤC CẤP 2
        const parentSelect = document.getElementById('editParentCategoryId');
        const parentGroup = document.getElementById('editParentCategoryGroup');
        
        // Ẩn parent group mặc định, chỉ hiển thị khi cần
        if (parentGroup) {
            parentGroup.style.display = 'none';
        }
        
        if (parentSelect && parentGroup && category.type === 'CATEGORY') {
            // Nếu là danh mục cấp 2 (có parent_id) -> Hiển thị dropdown
            if (category.parentId) {
                parentGroup.style.display = 'block';
                const parentCategories = this.categories.filter(c => 
                    c.type === 'CATEGORY' && c.isActive && !c.parentId && c.id !== category.id
                );
                parentSelect.innerHTML = '<option value="">Không có (Danh mục cấp 1)</option>';
                parentCategories.forEach(cat => {
                    const option = document.createElement('option');
                    option.value = cat.id;
                    option.textContent = cat.name;
                    if (category.parentId === cat.id) {
                        option.selected = true;
                    }
                    parentSelect.appendChild(option);
                });
            }
        }
        
        // Disable slug field if category has products
        const slugInput = document.getElementById('editCategorySlug');
        const slugHelpText = slugInput.parentElement.querySelector('.form-text');
        if (category.hasProducts) {
            slugInput.disabled = true;
            slugInput.title = 'Không thể thay đổi slug khi danh mục đã có sản phẩm';
            if (slugHelpText) {
                slugHelpText.textContent = 'Không thể chỉnh sửa (đã có sản phẩm)';
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
        
        const modal = new bootstrap.Modal(document.getElementById('editCategoryModal'));
        modal.show();
    }
    
    /**
     * Update category
     */
    async updateCategory() {
        const id = document.getElementById('editCategoryId').value;
        const name = document.getElementById('editCategoryName').value;
        const slug = document.getElementById('editCategorySlug').value;
        const parentId = document.getElementById('editParentCategoryId').value;
        const isActive = document.getElementById('editCategoryIsActive').checked;
        
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
                    parentId: parentId || null,
                    isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật danh mục thành công', 'success');
                const modalElement = document.getElementById('editCategoryModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadCategories();
            } else {
                let errorMsg = result.message || 'Cập nhật danh mục thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error updating category:', error);
            this.showNotification('Không thể cập nhật danh mục', 'danger');
        }
    }
    
    /**
     * Toggle category status
     */
    async toggleCategoryStatus(id) {
        if (!confirm('Bạn có chắc muốn thay đổi trạng thái danh mục này?')) {
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}/toggle-status`, {
                method: 'POST'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success) {
                this.showNotification('Cập nhật trạng thái thành công', 'success');
                this.loadCategories();
            } else {
                this.showNotification(result.message || 'Cập nhật trạng thái thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error toggling category status:', error);
            this.showNotification('Không thể cập nhật trạng thái', 'danger');
        }
    }
    
    /**
     * Reset filters
     */
    resetFilters() {
        document.getElementById('searchInput').value = '';
        document.getElementById('filterType').value = '';
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
        
        // Type filter
        const typeFilter = document.getElementById('filterType');
        if (typeFilter) {
            typeFilter.addEventListener('change', () => this.applyFilters());
        }
        
        // Status filter
        const statusFilter = document.getElementById('filterStatus');
        if (statusFilter) {
            statusFilter.addEventListener('change', () => this.applyFilters());
        }
        
        // Show/hide parent category based on type
        const categoryType = document.getElementById('categoryType');
        const parentGroup = document.getElementById('parentCategoryGroup');
        if (categoryType && parentGroup) {
            categoryType.addEventListener('change', () => {
                if (categoryType.value === 'CATEGORY') {
                    parentGroup.style.display = 'block';
                    this.loadParentCategories();
                } else {
                    parentGroup.style.display = 'none';
                }
            });
        }
        
        // Auto-generate slug from name
        const categoryName = document.getElementById('categoryName');
        const categorySlug = document.getElementById('categorySlug');
        if (categoryName && categorySlug) {
            categoryName.addEventListener('input', function() {
                if (!categorySlug.dataset.edited) {
                    const slug = categoriesManagement.sanitizeTitle(this.value);
                    categorySlug.value = slug;
                }
            });
            
            categorySlug.addEventListener('input', function() {
                this.dataset.edited = 'true';
            });
        }
    }
    
    /**
     * Load parent categories for dropdown - CHỈ LOAD DANH MỤC CẤP 1 (parent_id = NULL)
     */
    loadParentCategories() {
        const parentSelect = document.getElementById('parentCategoryId');
        if (!parentSelect) return;
        
        const categoryType = document.getElementById('categoryType').value;
        if (categoryType === 'CATEGORY') {
            // CHỈ load các danh mục CẤP 1 (parent_id = null) và active
            const parentCategories = this.categories.filter(c => 
                c.type === 'CATEGORY' && c.isActive && !c.parentId
            );
            
            parentSelect.innerHTML = '<option value="">Không có (Danh mục cấp 1)</option>';
            parentCategories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = cat.name;
                parentSelect.appendChild(option);
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
let categoriesManagement;

function createCategory() {
    if (categoriesManagement) {
        categoriesManagement.createCategory();
    }
}

function editCategory(id) {
    if (categoriesManagement) {
        categoriesManagement.editCategory(id);
    }
}

function updateCategory() {
    if (categoriesManagement) {
        categoriesManagement.updateCategory();
    }
}

function toggleCategoryStatus(id) {
    if (categoriesManagement) {
        categoriesManagement.toggleCategoryStatus(id);
    }
}

function resetFilters() {
    if (categoriesManagement) {
        categoriesManagement.resetFilters();
    }
}

// Function to initialize categories management
function initializeCategoriesManagement() {
    if (window.location.pathname.includes('/admin/categories')) {
        if (categoriesManagement) {
            delete categoriesManagement;
        }
        
        try {
            categoriesManagement = new CategoriesManagement();
        } catch (error) {
            console.error('Error initializing CategoriesManagement:', error);
        }
    }
}

// Initialize when DOM is loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeCategoriesManagement);
} else {
    initializeCategoriesManagement();
}

// Export for global access
window.CategoriesManagement = CategoriesManagement;
window.initializeCategoriesManagement = initializeCategoriesManagement;

