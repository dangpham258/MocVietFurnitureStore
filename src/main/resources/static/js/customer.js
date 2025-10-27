// ========================================
// MAIN CUSTOMER APPLICATION CLASS
// ========================================
class CustomerApp {
    constructor() {
        this.cartManager = new CustomerCart();
        this.headerManager = new CustomerHeader();
        this.init();
    }
    
    init() {
        this.cartManager.init();
        this.headerManager.init();
    }
}

// ========================================
// CART MANAGEMENT CLASS
// ========================================
class CustomerCart {
    constructor() {
        this.isInitialized = false;
    }

    init() {
        if (this.isInitialized) return;
        
        this.bindEvents();
        this.updateCartTotal();
        this.storeQuantityPreviousValues();
        this.isInitialized = true;
    }

    bindEvents() {
        // Select all checkbox
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', (e) => {
                this.toggleAllItems(e.target.checked);
            });
        }

        // Individual item checkboxes
        document.querySelectorAll('.item-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', () => {
                this.updateCartTotal();
                this.updateSelectAllState();
            });
        });

        // Quantity input changes
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.addEventListener('change', (e) => {
                const cartItemId = this.getCartItemIdFromElement(e.target);
                this.updateQuantity(cartItemId, e.target.value);
            });
        });
    }

    storeQuantityPreviousValues() {
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.dataset.previousValue = input.value;
            input.addEventListener('focus', function() {
                this.dataset.previousValue = this.value;
            });
        });
    }

    toggleAllItems(checked) {
        document.querySelectorAll('.item-checkbox').forEach(checkbox => {
            if (!checkbox.disabled) {
                checkbox.checked = checked;
            }
        });
        this.updateCartTotal();
    }

    updateSelectAllState() {
        const selectAllCheckbox = document.getElementById('selectAll');
        const itemCheckboxes = document.querySelectorAll('.item-checkbox:not(:disabled)');
        const checkedItems = document.querySelectorAll('.item-checkbox:checked:not(:disabled)');
        
        if (itemCheckboxes.length === 0) {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = false;
        } else if (checkedItems.length === itemCheckboxes.length) {
            selectAllCheckbox.checked = true;
            selectAllCheckbox.indeterminate = false;
        } else if (checkedItems.length > 0) {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = true;
        } else {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = false;
        }
    }

    updateCartTotal() {
        const selectedItemIds = this.getSelectedItemIds();
        
        if (selectedItemIds.length === 0) {
            document.getElementById('subtotal').textContent = '0 ₫';
            document.getElementById('grandTotal').textContent = '0 ₫';
            return;
        }

        fetch('/customer/cart/calculate-total', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(selectedItemIds)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const formattedTotal = this.formatCurrency(data.total);
                document.getElementById('subtotal').textContent = formattedTotal;
                document.getElementById('grandTotal').textContent = formattedTotal;
            }
        })
        .catch(error => {
            console.error('Error calculating total:', error);
        });
    }

    getSelectedItemIds() {
        const selectedCheckboxes = document.querySelectorAll('.item-checkbox:checked');
        return Array.from(selectedCheckboxes).map(checkbox => {
            return parseInt(checkbox.id.replace('item-', ''));
        });
    }

    getCartItemIdFromElement(element) {
        const id = element.id;
        if (id.startsWith('qty-')) {
            return parseInt(id.replace('qty-', ''));
        }
        return null;
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount).replace('₫', '₫');
    }

    showAlert(message, type = 'info') {
        const alertContainer = document.getElementById('alertContainer');
        const alertId = 'alert-' + Date.now();
        
        const alertHtml = `
            <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show alert-custom" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        
        alertContainer.insertAdjacentHTML('beforeend', alertHtml);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            const alertElement = document.getElementById(alertId);
            if (alertElement) {
                alertElement.remove();
            }
        }, 5000);
    }

    setLoading(element, loading = true) {
        if (loading) {
            element.classList.add('loading');
            element.disabled = true;
        } else {
            element.classList.remove('loading');
            element.disabled = false;
        }
    }

    // Add to cart method (for use from product pages)
    addToCart(variantId, quantity = 1) {
        return fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `variantId=${variantId}&quantity=${quantity}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Update cart count in header
                customerApp.headerManager.updateCartCount(data.cartItemCount);
            }
            return data;
        });
    }
}

// ========================================
// HEADER MANAGEMENT CLASS
// ========================================
class CustomerHeader {
    constructor() {
        this.isInitialized = false;
    }

    init() {
        if (this.isInitialized) return;
        
        this.updateCartCountInHeader();
        this.isInitialized = true;
    }

    updateCartCountInHeader() {
        fetch('/api/cart/count')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    this.updateCartCount(data.count);
                }
            })
            .catch(error => {
                console.error('Error updating cart count:', error);
            });
    }

    updateCartCount(count) {
        const cartCountElements = document.querySelectorAll('.cart-count');
        cartCountElements.forEach(element => {
            element.textContent = count;
            element.style.display = count > 0 ? 'inline' : 'none';
        });
    }
}

// ========================================
// GLOBAL FUNCTIONS FOR HTML ONCLICK EVENTS
// ========================================

// Quantity management functions - Make them globally available
window.increaseQuantity = function(cartItemId) {
    const input = document.getElementById(`qty-${cartItemId}`);
    const currentValue = parseInt(input.value);
    const maxValue = parseInt(input.max);
    
    if (currentValue < maxValue) {
        input.value = currentValue + 1;
        updateQuantity(cartItemId, input.value);
    }
};

window.decreaseQuantity = function(cartItemId) {
    const input = document.getElementById(`qty-${cartItemId}`);
    const currentValue = parseInt(input.value);
    
    if (currentValue > 1) {
        input.value = currentValue - 1;
        updateQuantity(cartItemId, input.value);
    }
};

window.updateQuantity = function(cartItemId, quantity) {
    const quantityInt = parseInt(quantity);
    
    if (quantityInt <= 0) {
        removeItem(cartItemId);
        return;
    }

    const cartItemElement = document.getElementById(`cart-item-${cartItemId}`);
    const updateBtn = cartItemElement.querySelector('.btn-quantity');
    
    customerApp.cartManager.setLoading(updateBtn, true);

    fetch('/customer/cart/update-quantity', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `cartItemId=${cartItemId}&quantity=${quantityInt}`
    })
    .then(response => response.json())
    .then(data => {
        customerApp.cartManager.setLoading(updateBtn, false);
        
        if (data.success) {
            // Update total price for this item
            updateItemTotal(cartItemId);
            customerApp.cartManager.updateCartTotal();
            
            // Show stock errors if any
            if (data.stockErrors) {
                updateStockErrors(data.stockErrors);
            }
        } else {
            customerApp.cartManager.showAlert(data.message || 'Có lỗi xảy ra khi cập nhật số lượng', 'danger');
            // Revert input value
            const input = document.getElementById(`qty-${cartItemId}`);
            input.value = input.dataset.previousValue || 1;
        }
    })
    .catch(error => {
        customerApp.cartManager.setLoading(updateBtn, false);
        customerApp.cartManager.showAlert('Có lỗi xảy ra. Vui lòng thử lại sau', 'danger');
        console.error('Error updating quantity:', error);
    });
};

window.updateItemTotal = function(cartItemId) {
    const input = document.getElementById(`qty-${cartItemId}`);
    const quantity = parseInt(input.value);
    
    // Get price from the price display
    const priceElement = document.querySelector(`#cart-item-${cartItemId} .price-info span`);
    const priceText = priceElement.textContent.replace(/[^\d]/g, '');
    const price = parseInt(priceText);
    
    const total = price * quantity;
    const totalElement = document.getElementById(`total-${cartItemId}`);
    totalElement.textContent = customerApp.cartManager.formatCurrency(total);
};

window.removeItem = function(cartItemId) {
    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }

    const cartItemElement = document.getElementById(`cart-item-${cartItemId}`);
    const removeBtn = cartItemElement.querySelector('.btn-remove');
    
    customerApp.cartManager.setLoading(removeBtn, true);

    fetch('/customer/cart/remove', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `cartItemId=${cartItemId}`
    })
    .then(response => response.json())
    .then(data => {
        customerApp.cartManager.setLoading(removeBtn, false);
        
        if (data.success) {
            cartItemElement.remove();
            customerApp.cartManager.updateCartTotal();
            customerApp.cartManager.updateSelectAllState();
            customerApp.cartManager.showAlert(data.message || 'Đã xóa sản phẩm khỏi giỏ hàng', 'success');
            
            // Update cart count in header
            customerApp.headerManager.updateCartCount(data.cartItemCount);
            
            // Check if cart is empty
            const remainingItems = document.querySelectorAll('.cart-item');
            if (remainingItems.length === 0) {
                location.reload(); // Reload to show empty cart message
            }
        } else {
            customerApp.cartManager.showAlert(data.message || 'Không thể xóa sản phẩm khỏi giỏ hàng', 'danger');
        }
    })
    .catch(error => {
        customerApp.cartManager.setLoading(removeBtn, false);
        customerApp.cartManager.showAlert('Có lỗi xảy ra. Vui lòng thử lại sau', 'danger');
        console.error('Error removing item:', error);
    });
};

window.proceedToCheckout = function() {
    const selectedItemIds = customerApp.cartManager.getSelectedItemIds();
    
    if (selectedItemIds.length === 0) {
        customerApp.cartManager.showAlert('Vui lòng chọn ít nhất một sản phẩm để thanh toán', 'warning');
        return;
    }

    // Check for stock errors
    const stockErrors = document.querySelectorAll('.stock-error');
    if (stockErrors.length > 0) {
        customerApp.cartManager.showAlert('Vui lòng kiểm tra lại tồn kho của các sản phẩm đã chọn', 'warning');
        return;
    }

    // Store selected items in sessionStorage for checkout page
    sessionStorage.setItem('selectedCartItems', JSON.stringify(selectedItemIds));
    
    // Redirect to checkout page
    window.location.href = '/customer/checkout';
};

window.updateStockErrors = function(stockErrors) {
    // Remove existing stock errors
    document.querySelectorAll('.stock-error').forEach(error => error.remove());
    
    // Add new stock errors
    Object.entries(stockErrors).forEach(([itemId, errorMessage]) => {
        const cartItem = document.getElementById(`cart-item-${itemId}`);
        if (cartItem) {
            const productInfo = cartItem.querySelector('.product-info');
            const errorDiv = document.createElement('div');
            errorDiv.className = 'stock-error';
            errorDiv.textContent = errorMessage;
            productInfo.appendChild(errorDiv);
            
            // Disable checkbox and quantity controls
            const checkbox = document.getElementById(`item-${itemId}`);
            const quantityInput = document.getElementById(`qty-${itemId}`);
            const quantityButtons = cartItem.querySelectorAll('.btn-quantity');
            
            if (checkbox) checkbox.disabled = true;
            if (quantityInput) quantityInput.disabled = true;
            quantityButtons.forEach(btn => btn.disabled = true);
            
            // Add out-of-stock class
            cartItem.classList.add('out-of-stock');
        }
    });
    
    customerApp.cartManager.updateSelectAllState();
};

// Global function for adding to cart from product pages
window.addToCart = function(variantId, quantity = 1) {
    return customerApp.cartManager.addToCart(variantId, quantity);
};

// ========================================
// ORDER MANAGEMENT FUNCTIONS
// ========================================

let currentOrderId = null;

// Make functions globally available
window.filterOrders = function() {
    const statusSelect = document.getElementById('statusFilter');
    if (statusSelect) {
        const status = statusSelect.value;
        window.location.href = `/customer/orders?status=${status}`;
    }
};

window.cancelOrder = function(orderId) {
    currentOrderId = orderId;
    const modal = new bootstrap.Modal(document.getElementById('cancelOrderModal'));
    modal.show();
};

window.confirmCancelOrder = function() {
    const reason = document.getElementById('cancelReason').value;
    
    fetch(`/customer/orders/${currentOrderId}/cancel`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `reason=${encodeURIComponent(reason)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        alert('Có lỗi xảy ra khi hủy đơn hàng');
    });
};

window.requestReturn = function(orderId) {
    currentOrderId = orderId;
    const modal = new bootstrap.Modal(document.getElementById('returnRequestModal'));
    modal.show();
};

window.confirmReturnRequest = function() {
    const reason = document.getElementById('returnReason').value;
    
    if (!reason.trim()) {
        alert('Vui lòng nhập lý do trả hàng');
        return;
    }
    
    fetch(`/customer/orders/${currentOrderId}/return`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `reason=${encodeURIComponent(reason)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        alert('Có lỗi xảy ra khi gửi yêu cầu trả hàng');
    });
};

window.reorderProducts = function(orderId) {
    if (confirm('Bạn có muốn thêm các sản phẩm trong đơn hàng này vào giỏ hàng không?')) {
        fetch(`/customer/orders/${orderId}/reorder`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                if (data.addedCount > 0) {
                    window.location.href = '/customer/cart';
                }
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng');
        });
    }
};

// ========================================
// INITIALIZATION
// ========================================
let customerApp;

document.addEventListener('DOMContentLoaded', function() {
    customerApp = new CustomerApp();
});
