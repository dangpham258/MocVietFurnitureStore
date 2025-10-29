/**
 * Delivery Assignment JavaScript
 * Handles delivery team assignment functionality
 */

class DeliveryAssignment {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
        this.initTooltips();
        this.initFormValidation();
    }

    bindEvents() {
        // Team selection
        document.addEventListener('click', (e) => {
            if (e.target.closest('.team-card')) {
                this.selectTeam(e.target.closest('.team-card'));
            }
        });

        // Form submission
        document.addEventListener('submit', (e) => {
            if (e.target.matches('form[th\\:action*="assign"], form[th\\:action*="change"]')) {
                this.handleFormSubmission(e);
            }
        });

        // Auto-refresh for pending orders
        if (document.querySelector('.pending-orders-table')) {
            this.startAutoRefresh();
        }

        // Search and filter
        this.initSearchAndFilter();
    }

    selectTeam(teamCard) {
        // Remove previous selection
        document.querySelectorAll('.team-card').forEach(card => {
            card.classList.remove('selected');
        });

        // Select current team
        teamCard.classList.add('selected');
        
        // Update form
        const teamId = teamCard.querySelector('input[type="radio"]').value;
        const formSelect = document.querySelector('#deliveryTeamId, #newDeliveryTeamId');
        if (formSelect) {
            formSelect.value = teamId;
        }

        // Show team details
        this.showTeamDetails(teamId);
    }

    showTeamDetails(teamId) {
        // This would typically make an AJAX call to get team details
        console.log('Showing details for team:', teamId);
        
        // For now, just show a simple notification
        this.showNotification('Đội giao hàng đã được chọn', 'info');
    }

    handleFormSubmission(e) {
        const form = e.target;
        const formData = new FormData(form);
        
        // Validate required fields
        if (!this.validateForm(form)) {
            e.preventDefault();
            return false;
        }

        // Show loading state
        this.showLoadingState(form);
        
        // Add confirmation for critical actions
        if (form.action.includes('change')) {
            if (!confirm('Bạn có chắc chắn muốn thay đổi đội giao hàng?')) {
                e.preventDefault();
                this.hideLoadingState(form);
                return false;
            }
        }
    }

    validateForm(form) {
        const requiredFields = form.querySelectorAll('[required]');
        let isValid = true;

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                this.showFieldError(field, 'Trường này là bắt buộc');
                isValid = false;
            } else {
                this.clearFieldError(field);
            }
        });

        // Custom validation for delivery team selection
        const teamSelect = form.querySelector('#deliveryTeamId, #newDeliveryTeamId');
        if (teamSelect && !teamSelect.value) {
            this.showFieldError(teamSelect, 'Vui lòng chọn đội giao hàng');
            isValid = false;
        }

        return isValid;
    }

    showFieldError(field, message) {
        this.clearFieldError(field);
        
        field.classList.add('is-invalid');
        
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        
        field.parentNode.appendChild(errorDiv);
    }

    clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    showLoadingState(form) {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
        }
    }

    hideLoadingState(form) {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = submitBtn.getAttribute('data-original-text') || 'Xác nhận';
        }
    }

    initSearchAndFilter() {
        const searchInput = document.querySelector('#keyword');
        const zoneSelect = document.querySelector('#zoneId');
        
        if (searchInput) {
            searchInput.addEventListener('input', this.debounce(() => {
                this.performSearch();
            }, 300));
        }

        if (zoneSelect) {
            zoneSelect.addEventListener('change', () => {
                this.performFilter();
            });
        }
    }

    performSearch() {
        const keyword = document.querySelector('#keyword').value;
        const zoneId = document.querySelector('#zoneId').value;
        
        // Build URL with current parameters
        const url = new URL(window.location);
        url.searchParams.set('keyword', keyword);
        url.searchParams.set('zoneId', zoneId);
        url.searchParams.set('page', '0'); // Reset to first page
        
        // Navigate to filtered results
        window.location.href = url.toString();
    }

    performFilter() {
        this.performSearch();
    }

    startAutoRefresh() {
        // Refresh every 30 seconds for pending orders
        setInterval(() => {
            this.refreshPendingOrders();
        }, 30000);
    }

    refreshPendingOrders() {
        fetch(window.location.href, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.text())
        .then(html => {
            // Update the orders table
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const newTable = doc.querySelector('.table-responsive');
            const currentTable = document.querySelector('.table-responsive');
            
            if (newTable && currentTable) {
                currentTable.innerHTML = newTable.innerHTML;
            }
        })
        .catch(error => {
            console.error('Error refreshing orders:', error);
        });
    }

    initTooltips() {
        // Initialize Bootstrap tooltips
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    initFormValidation() {
        // Custom validation for delivery assignment forms
        const forms = document.querySelectorAll('form[th\\:action*="assign"], form[th\\:action*="change"]');
        
        forms.forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!form.checkValidity()) {
                    e.preventDefault();
                    e.stopPropagation();
                }
                form.classList.add('was-validated');
            });
        });
    }

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.innerHTML = `
            <i class="fas fa-${this.getIconForType(type)}"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(notification);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 5000);
    }

    getIconForType(type) {
        const icons = {
            'success': 'check-circle',
            'error': 'exclamation-circle',
            'warning': 'exclamation-triangle',
            'info': 'info-circle'
        };
        return icons[type] || 'info-circle';
    }

    debounce(func, wait) {
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

    // Utility methods for team management
    getTeamWorkloadClass(count) {
        if (count <= 2) return 'team-workload-low';
        if (count <= 5) return 'team-workload-medium';
        return 'team-workload-high';
    }

    getOrderPriorityClass(order) {
        // This would be determined by business logic
        const total = order.orderTotal || 0;
        if (total > 5000000) return 'order-priority-high';
        if (total > 2000000) return 'order-priority-medium';
        return 'order-priority-low';
    }

    // AJAX helper methods
    async fetchAvailableTeams(orderId) {
        try {
            const response = await fetch(`/manager/delivery/api/teams/${orderId}`);
            return await response.json();
        } catch (error) {
            console.error('Error fetching teams:', error);
            return [];
        }
    }

    async fetchOrderZone(orderId) {
        try {
            const response = await fetch(`/manager/delivery/api/zone/${orderId}`);
            return await response.text();
        } catch (error) {
            console.error('Error fetching zone:', error);
            return 'Chưa xác định';
        }
    }

    // Export functionality
    exportPendingOrders() {
        const table = document.querySelector('.table');
        if (!table) return;

        // Simple CSV export
        const rows = Array.from(table.querySelectorAll('tr'));
        const csvContent = rows.map(row => {
            const cells = Array.from(row.querySelectorAll('td, th'));
            return cells.map(cell => `"${cell.textContent.trim()}"`).join(',');
        }).join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'pending-orders.csv';
        a.click();
        window.URL.revokeObjectURL(url);
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.deliveryAssignment = new DeliveryAssignment();
});

// Global utility functions
window.refreshPage = function() {
    window.location.reload();
};

window.viewOrderDetails = function(orderId) {
    // TODO: Implement order details modal
    console.log('View order details:', orderId);
};

window.viewTeamDetails = function(teamId) {
    // TODO: Implement team details modal
    console.log('View team details:', teamId);
};

window.editTeam = function(teamId) {
    // TODO: Implement edit team functionality
    console.log('Edit team:', teamId);
};

window.viewZoneDetails = function(zoneId) {
    // TODO: Implement zone details modal
    console.log('View zone details:', zoneId);
};

window.editZone = function(zoneId) {
    // TODO: Implement edit zone functionality
    console.log('Edit zone:', zoneId);
};

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = DeliveryAssignment;
}
