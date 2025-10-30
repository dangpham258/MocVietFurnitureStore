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
        this.storeQuantityPreviousValues();
        this.updateCartTotal();
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
            checkbox.addEventListener('change', (e) => {
                e.stopPropagation();
                this.updateCartTotal();
                this.updateSelectAllState();
            });
        });

        // Quantity input changes
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.addEventListener('change', (e) => {
                e.stopPropagation();
                const cartItemId = this.getCartItemIdFromElement(e.target);
                if (cartItemId) {
                    this.updateQuantity(cartItemId, e.target.value);
                }
            });
        });
        
        // Quantity buttons
        document.querySelectorAll('.btn-quantity').forEach(button => {
            button.addEventListener('click', (e) => {
                e.stopPropagation();
                const idMatch = e.currentTarget.closest('.cart-item')?.id?.match(/cart-item-(\d+)/);
                const cartItemId = idMatch ? parseInt(idMatch[1]) : null;
                if (!cartItemId) return;
                // Determine increase or decrease by icon class
                if (e.currentTarget.querySelector('.fa-plus')) {
                    increaseQuantity(cartItemId);
                } else if (e.currentTarget.querySelector('.fa-minus')) {
                    decreaseQuantity(cartItemId);
                }
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
        const subtotalEl = document.getElementById('subtotal');
        const grandTotalEl = document.getElementById('grandTotal');
        
        if (!subtotalEl || !grandTotalEl) {
            return; // Elements not found, page might not have cart summary
        }
        
        if (selectedItemIds.length === 0) {
            subtotalEl.textContent = '0 ₫';
            grandTotalEl.textContent = '0 ₫';
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
                subtotalEl.textContent = formattedTotal;
                grandTotalEl.textContent = formattedTotal;
            }
        })
        .catch(error => {
            console.error('Error calculating total:', error);
        });
    }

    getSelectedItemIds() {
        const selectedCheckboxes = document.querySelectorAll('.item-checkbox:checked:not(:disabled)');
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
        if (typeof window.showNotification === 'function') {
            window.showNotification(message, type, 4000);
        } else {
            console[type === 'danger' ? 'error' : (type === 'warning' ? 'warn' : 'log')](message);
        }
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
                if (window.showNotification) window.showNotification(data.message || 'Đã thêm vào giỏ hàng', 'success', 2500);
            } else {
                if (window.showNotification) window.showNotification(data.message || 'Không thể thêm vào giỏ hàng', 'danger', 4000);
            }
            return data;
        });
    }

    updateQuantity(cartItemId, quantity) {
        const quantityInt = parseInt(quantity);
        
        if (quantityInt <= 0) {
            removeItem(cartItemId);
            return;
        }

        const cartItemElement = document.getElementById(`cart-item-${cartItemId}`);
        const updateBtn = cartItemElement?.querySelector('.btn-quantity');
        
        if (updateBtn) {
            this.setLoading(updateBtn, true);
        }

        fetch('/customer/cart/update-quantity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `cartItemId=${cartItemId}&quantity=${quantityInt}`
        })
        .then(response => response.json())
        .then(data => {
            if (updateBtn) {
                this.setLoading(updateBtn, false);
            }
            
            if (data.success) {
                // Update total price for this item
    updateItemTotal(cartItemId);
                this.updateCartTotal();
                
                // Show stock errors if any
                if (data.stockErrors) {
                    updateStockErrors(data.stockErrors);
                }
            } else {
                this.showAlert(data.message || 'Có lỗi xảy ra khi cập nhật số lượng', 'danger');
                // Revert input value
                const input = document.getElementById(`qty-${cartItemId}`);
                if (input) {
                    input.value = input.dataset.previousValue || 1;
                }
            }
        })
        .catch(error => {
            if (updateBtn) {
                this.setLoading(updateBtn, false);
            }
            this.showAlert('Có lỗi xảy ra. Vui lòng thử lại sau', 'danger');
            console.error('Error updating quantity:', error);
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
    if (!input) return;
    
    const currentValue = parseInt(input.value);
    const maxValue = parseInt(input.max);
    
    if (currentValue < maxValue) {
        input.value = currentValue + 1;
        
        // Trigger change event manually
        const event = new Event('change', { bubbles: true });
        input.dispatchEvent(event);
    }
};

window.decreaseQuantity = function(cartItemId) {
    const input = document.getElementById(`qty-${cartItemId}`);
    if (!input) return;
    
    const currentValue = parseInt(input.value);
    
    if (currentValue > 1) {
        input.value = currentValue - 1;
        
        // Trigger change event manually
        const event = new Event('change', { bubbles: true });
        input.dispatchEvent(event);
    }
};

window.updateQuantity = function(cartItemId, quantity) {
    if (customerApp && customerApp.cartManager) {
        customerApp.cartManager.updateQuantity(cartItemId, quantity);
    } else {
        // Fallback implementation
        const quantityInt = parseInt(quantity);
        
        if (quantityInt <= 0) {
            removeItem(cartItemId);
            return;
        }

        const cartItemElement = document.getElementById(`cart-item-${cartItemId}`);
        const updateBtn = cartItemElement?.querySelector('.btn-quantity');
        
        if (updateBtn) {
            updateBtn.disabled = true;
        }

        fetch('/customer/cart/update-quantity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `cartItemId=${cartItemId}&quantity=${quantityInt}`
        })
        .then(response => response.json())
        .then(data => {
            if (updateBtn) {
                updateBtn.disabled = false;
            }
            
            if (data.success) {
                // Update total price for this item
                updateItemTotal(cartItemId);
                
                // Show stock errors if any
                if (data.stockErrors) {
                    updateStockErrors(data.stockErrors);
                }
            } else {
                if (window.showNotification) window.showNotification(data.message || 'Có lỗi xảy ra khi cập nhật số lượng', 'danger', 4000);
                // Revert input value
                const input = document.getElementById(`qty-${cartItemId}`);
                if (input) {
                    input.value = input.dataset.previousValue || 1;
                }
            }
        })
        .catch(error => {
            if (updateBtn) {
                updateBtn.disabled = false;
            }
            if (window.showNotification) window.showNotification('Có lỗi xảy ra. Vui lòng thử lại sau', 'danger', 4000);
            console.error('Error updating quantity:', error);
        });
    }
};

window.updateItemTotal = function(cartItemId) {
    const input = document.getElementById(`qty-${cartItemId}`);
    if (!input) return;
    
    const quantity = parseInt(input.value);
    
    // Get price from the price display
    const priceElement = document.querySelector(`#cart-item-${cartItemId} .price-info span`);
    if (!priceElement) return;
    const dataUnit = priceElement.getAttribute('data-unit');
    const price = dataUnit ? parseInt(dataUnit) : parseInt(priceElement.textContent.replace(/[^\d]/g, ''));
    
    const total = price * quantity;
    const totalElement = document.getElementById(`total-${cartItemId}`);
    if (totalElement) {
        if (customerApp && customerApp.cartManager) {
            totalElement.textContent = customerApp.cartManager.formatCurrency(total);
        } else {
            // Fallback formatting
            totalElement.textContent = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(total).replace('₫', '₫');
        }
    }
};

window.removeItem = function(cartItemId) {
    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }

    const cartItemElement = document.getElementById(`cart-item-${cartItemId}`);
    if (!cartItemElement) return;
    
    const removeBtn = cartItemElement.querySelector('.btn-remove');
    
    if (customerApp && customerApp.cartManager) {
        customerApp.cartManager.setLoading(removeBtn, true);
    } else if (removeBtn) {
        removeBtn.disabled = true;
    }

    fetch('/customer/cart/remove', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `cartItemId=${cartItemId}`
    })
    .then(response => response.json())
    .then(data => {
        if (customerApp && customerApp.cartManager) {
            customerApp.cartManager.setLoading(removeBtn, false);
        } else if (removeBtn) {
            removeBtn.disabled = false;
        }
        
        if (data.success) {
            // Toast luôn hiển thị
            if (window.showNotification) window.showNotification(data.message || 'Đã xóa sản phẩm khỏi giỏ hàng', 'success', 2500);

            cartItemElement.remove();
            
            if (customerApp && customerApp.cartManager) {
                customerApp.cartManager.updateCartTotal();
                customerApp.cartManager.updateSelectAllState();
                
                // Update cart count in header
                if (customerApp.headerManager) {
                    customerApp.headerManager.updateCartCount(data.cartItemCount);
                }
            }
            
            // Check if cart is empty
            const remainingItems = document.querySelectorAll('.cart-item');
            if (remainingItems.length === 0) {
                // Trì hoãn một chút để người dùng thấy toast
                setTimeout(() => location.reload(), 800);
            }
        } else {
            if (customerApp && customerApp.cartManager) {
                customerApp.cartManager.showAlert(data.message || 'Không thể xóa sản phẩm khỏi giỏ hàng', 'danger');
            } else if (window.showNotification) {
                window.showNotification(data.message || 'Không thể xóa sản phẩm khỏi giỏ hàng', 'danger', 4000);
            }
        }
    })
    .catch(error => {
        if (customerApp && customerApp.cartManager) {
            customerApp.cartManager.setLoading(removeBtn, false);
            customerApp.cartManager.showAlert('Có lỗi xảy ra. Vui lòng thử lại sau', 'danger');
        } else if (removeBtn) {
            removeBtn.disabled = false;
        }
        if (window.showNotification) window.showNotification('Có lỗi xảy ra. Vui lòng thử lại sau', 'danger', 4000);
        console.error('Error removing item:', error);
    });
};

window.proceedToCheckout = function() {
    // Get selected item IDs
    let selectedItemIds = [];
    if (customerApp && customerApp.cartManager) {
        selectedItemIds = customerApp.cartManager.getSelectedItemIds();
    } else {
        // Fallback
        const selectedCheckboxes = document.querySelectorAll('.item-checkbox:checked:not(:disabled)');
        selectedItemIds = Array.from(selectedCheckboxes).map(checkbox => {
            return parseInt(checkbox.id.replace('item-', ''));
        });
    }
    
    if (selectedItemIds.length === 0) {
        if (customerApp && customerApp.cartManager) {
            customerApp.cartManager.showAlert('Vui lòng chọn ít nhất một sản phẩm để thanh toán', 'warning');
        } else if (window.showNotification) {
            window.showNotification('Vui lòng chọn ít nhất một sản phẩm để thanh toán', 'warning', 4000);
        }
        return;
    }

    // Check for stock errors
    const stockErrors = document.querySelectorAll('.stock-error');
    if (stockErrors.length > 0) {
        if (customerApp && customerApp.cartManager) {
            customerApp.cartManager.showAlert('Vui lòng kiểm tra lại tồn kho của các sản phẩm đã chọn', 'warning');
        } else if (window.showNotification) {
            window.showNotification('Vui lòng kiểm tra lại tồn kho của các sản phẩm đã chọn', 'warning', 4000);
        }
        return;
    }

    // Build URL with selected items
    const selectedIdsParam = selectedItemIds.join(',');
    window.location.href = `/customer/checkout?selectedItemIds=${selectedIdsParam}`;
};

window.updateStockErrors = function(stockErrors) {
    // Remove existing stock errors
    document.querySelectorAll('.stock-error').forEach(error => error.remove());
    
    // Add new stock errors
    Object.entries(stockErrors).forEach(([itemId, errorMessage]) => {
        const cartItem = document.getElementById(`cart-item-${itemId}`);
        if (cartItem) {
            const productInfo = cartItem.querySelector('.product-info');
            if (productInfo) {
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
        }
    });
    
    if (customerApp && customerApp.cartManager) {
        customerApp.cartManager.updateSelectAllState();
    }
};

// Global function for adding to cart from product pages
window.addToCart = function(variantId, quantity = 1) {
    if (customerApp && customerApp.cartManager) {
        return customerApp.cartManager.addToCart(variantId, quantity);
    } else {
        // Fallback
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
                if (window.showNotification) window.showNotification(data.message || 'Đã thêm vào giỏ hàng', 'success', 2500);
            } else if (window.showNotification) {
                window.showNotification(data.message || 'Không thể thêm vào giỏ hàng', 'danger', 4000);
            }
            return data;
        });
    }
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
            if (window.showNotification) window.showNotification(data.message, 'danger', 4000);
        }
    })
    .catch(error => {
        if (window.showNotification) window.showNotification('Có lỗi xảy ra khi hủy đơn hàng', 'danger', 4000);
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
        if (window.showNotification) window.showNotification('Vui lòng nhập lý do trả hàng', 'warning', 4000);
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
            if (window.showNotification) window.showNotification(data.message, 'danger', 4000);
        }
    })
    .catch(error => {
        if (window.showNotification) window.showNotification('Có lỗi xảy ra khi gửi yêu cầu trả hàng', 'danger', 4000);
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
                if (window.showNotification) window.showNotification(data.message, 'success', 3000);
                if (data.addedCount > 0) {
                    window.location.href = '/customer/cart';
                }
            } else {
                if (window.showNotification) window.showNotification(data.message, 'danger', 4000);
            }
        })
        .catch(error => {
            if (window.showNotification) window.showNotification('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng', 'danger', 4000);
        });
    }
};

// ========================================
// INITIALIZATION
// ========================================
let customerApp;

// Function to initialize the app
function initializeCustomerApp() {
    if (window.customerApp === undefined) {
        window.customerApp = new CustomerApp();
        customerApp = window.customerApp;
    }
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeCustomerApp);
} else {
    initializeCustomerApp();
}
