// Manager Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Form validation enhancement
    const forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // Password strength indicator
    const newPasswordField = document.getElementById('newPassword');
    if (newPasswordField) {
        newPasswordField.addEventListener('input', function() {
            const password = this.value;
            const strength = calculatePasswordStrength(password);
            updatePasswordStrengthIndicator(strength);
        });
    }

    // Confirm password validation
    const confirmPasswordField = document.getElementById('confirmPassword');
    if (confirmPasswordField && newPasswordField) {
        confirmPasswordField.addEventListener('input', function() {
            const password = newPasswordField.value;
            const confirmPassword = this.value;
            
            if (confirmPassword && password !== confirmPassword) {
                this.setCustomValidity('Mật khẩu xác nhận không khớp');
            } else {
                this.setCustomValidity('');
            }
        });
    }
});

// Password strength calculation
function calculatePasswordStrength(password) {
    let strength = 0;
    
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    
    return strength;
}

// Update password strength indicator
function updatePasswordStrengthIndicator(strength) {
    const indicator = document.getElementById('passwordStrength');
    if (!indicator) return;
    
    const strengthText = ['Rất yếu', 'Yếu', 'Trung bình', 'Mạnh', 'Rất mạnh'];
    const strengthColors = ['#dc3545', '#fd7e14', '#ffc107', '#20c997', '#198754'];
    
    indicator.textContent = strengthText[strength - 1] || '';
    indicator.style.color = strengthColors[strength - 1] || '#6c757d';
}

// Toggle password visibility
function togglePassword(fieldId) {
    const field = document.getElementById(fieldId);
    const icon = document.getElementById(fieldId + 'Icon');
    
    if (field.type === 'password') {
        field.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        field.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

// Loading state for forms
function setLoadingState(form, loading = true) {
    const submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        if (loading) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang xử lý...';
        } else {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-save me-2"></i>Lưu thay đổi';
        }
    }
}

// Enhanced form submission
function submitForm(form, url, data) {
    setLoadingState(form, true);
    
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.success) {
            showAlert('success', result.message);
            if (result.redirect) {
                window.location.href = result.redirect;
            }
        } else {
            showAlert('error', result.message);
        }
    })
    .catch(error => {
        showAlert('error', 'Có lỗi xảy ra, vui lòng thử lại');
        console.error('Error:', error);
    })
    .finally(() => {
        setLoadingState(form, false);
    });
}

// Show alert message
function showAlert(type, message) {
    const alertContainer = document.querySelector('.container-fluid');
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('afterbegin', alertHtml);
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 5000);
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// Format datetime
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Confirm dialog
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Search functionality
function initializeSearch(inputSelector, resultsSelector) {
    const searchInput = document.querySelector(inputSelector);
    const resultsContainer = document.querySelector(resultsSelector);
    
    if (!searchInput || !resultsContainer) return;
    
    const debouncedSearch = debounce(function(query) {
        if (query.length < 2) {
            resultsContainer.innerHTML = '';
            return;
        }
        
        // Implement search logic here
        console.log('Searching for:', query);
    }, 300);
    
    searchInput.addEventListener('input', function() {
        debouncedSearch(this.value);
    });
}

// Table sorting
function initializeTableSorting(tableSelector) {
    const table = document.querySelector(tableSelector);
    if (!table) return;
    
    const headers = table.querySelectorAll('th[data-sort]');
    headers.forEach(header => {
        header.style.cursor = 'pointer';
        header.addEventListener('click', function() {
            const column = this.dataset.sort;
            const order = this.dataset.order === 'asc' ? 'desc' : 'asc';
            
            // Update all headers
            headers.forEach(h => {
                h.dataset.order = '';
                h.classList.remove('sort-asc', 'sort-desc');
            });
            
            // Update current header
            this.dataset.order = order;
            this.classList.add(order === 'asc' ? 'sort-asc' : 'sort-desc');
            
            // Implement sorting logic here
            console.log('Sort by:', column, order);
        });
    });
}

// Product Management Functions
function initializeProductManagement() {
    // Auto-calculate sale price
    const priceInput = document.getElementById('price');
    const discountInput = document.getElementById('discountPercent');
    const salePriceInput = document.getElementById('salePrice');
    
    if (priceInput && discountInput && salePriceInput) {
        function calculateSalePrice() {
            const price = parseFloat(priceInput.value) || 0;
            const discount = parseInt(discountInput.value) || 0;
            
            if (price > 0) {
                const discountAmount = price * (discount / 100);
                const salePrice = price - discountAmount;
                salePriceInput.value = new Intl.NumberFormat('vi-VN').format(Math.round(salePrice));
            } else {
                salePriceInput.value = '';
            }
        }
        
        priceInput.addEventListener('input', calculateSalePrice);
        discountInput.addEventListener('input', calculateSalePrice);
    }
    
    // SKU auto-generation
    const nameInput = document.getElementById('name');
    const skuInput = document.getElementById('sku');
    
    if (nameInput && skuInput) {
        nameInput.addEventListener('input', function() {
            if (!skuInput.value) {
                const sku = generateSKU(this.value);
                skuInput.value = sku;
            }
        });
    }
    
    // Stock level indicators
    updateStockIndicators();
}

// Generate SKU from product name
function generateSKU(name) {
    return name
        .toUpperCase()
        .replace(/[^A-Z0-9\s]/g, '')
        .replace(/\s+/g, '-')
        .substring(0, 20);
}

// Update stock level indicators
function updateStockIndicators() {
    const stockElements = document.querySelectorAll('[data-stock-qty]');
    stockElements.forEach(function(element) {
        const stockQty = parseInt(element.dataset.stockQty);
        if (stockQty <= 0) {
            element.classList.add('stock-danger');
            element.classList.remove('stock-warning', 'stock-success');
        } else if (stockQty <= 5) {
            element.classList.add('stock-warning');
            element.classList.remove('stock-danger', 'stock-success');
        } else {
            element.classList.add('stock-success');
            element.classList.remove('stock-warning', 'stock-danger');
        }
    });
}

// Stock management functions
function updateStock(variantId, productName, currentStock) {
    document.getElementById('productName').value = productName;
    document.getElementById('stockQty').value = currentStock;
    
    // Update form action URL
    const form = document.querySelector('#stockModal form');
    form.action = '/manager/products/variants/' + variantId + '/stock';
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('stockModal'));
    modal.show();
}

// Price management functions
function updatePrice(variantId, currentPrice, currentDiscount) {
    document.getElementById('price').value = currentPrice;
    document.getElementById('discountPercent').value = currentDiscount;
    
    // Update form action URL
    const form = document.querySelector('#priceModal form');
    form.action = '/manager/products/variants/' + variantId + '/price';
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('priceModal'));
    modal.show();
}

// Variant management functions
function deleteVariant(variantId, variantName) {
    if (confirm('Bạn có chắc chắn muốn xóa biến thể "' + variantName + '"?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/manager/products/variants/' + variantId + '/delete';
        document.body.appendChild(form);
        form.submit();
    }
}

// Search and filter functions
function performSearch() {
    const keyword = document.getElementById('keyword').value;
    const categoryId = document.getElementById('categoryId').value;
    const sortBy = document.getElementById('sortBy').value;
    const sortDir = document.getElementById('sortDir').value;
    
    const url = new URL(window.location);
    url.searchParams.set('keyword', keyword);
    url.searchParams.set('categoryId', categoryId);
    url.searchParams.set('sortBy', sortBy);
    url.searchParams.set('sortDir', sortDir);
    url.searchParams.set('page', '0');
    
    window.location.href = url.toString();
}

// Toggle product status
function toggleProductStatus(productId, currentStatus) {
    const action = currentStatus ? 'Ẩn' : 'Hiện';
    if (confirm('Bạn có chắc chắn muốn ' + action + ' sản phẩm này?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/manager/products/' + productId + '/toggle';
        document.body.appendChild(form);
        form.submit();
    }
}

// Bulk actions
function selectAllProducts() {
    const checkboxes = document.querySelectorAll('input[name="productIds"]');
    const selectAllCheckbox = document.getElementById('selectAll');
    
    checkboxes.forEach(function(checkbox) {
        checkbox.checked = selectAllCheckbox.checked;
    });
}

function bulkAction(action) {
    const selectedProducts = document.querySelectorAll('input[name="productIds"]:checked');
    
    if (selectedProducts.length === 0) {
        alert('Vui lòng chọn ít nhất một sản phẩm');
        return;
    }
    
    if (confirm('Bạn có chắc chắn muốn thực hiện hành động "' + action + '" cho ' + selectedProducts.length + ' sản phẩm?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/manager/products/bulk/' + action;
        
        selectedProducts.forEach(function(checkbox) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'productIds';
            input.value = checkbox.value;
            form.appendChild(input);
        });
        
        document.body.appendChild(form);
        form.submit();
    }
}

// Initialize product management on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeProductManagement();
});

// Export functions to global scope
window.togglePassword = togglePassword;
window.showAlert = showAlert;
window.formatCurrency = formatCurrency;
window.formatDate = formatDate;
window.formatDateTime = formatDateTime;
window.confirmAction = confirmAction;
window.updateStock = updateStock;
window.updatePrice = updatePrice;
window.deleteVariant = deleteVariant;
window.performSearch = performSearch;
window.toggleProductStatus = toggleProductStatus;
window.selectAllProducts = selectAllProducts;
window.bulkAction = bulkAction;
