(function() {
    'use strict';

    class ReportsManagement {
        constructor() {
            this.apiEndpoint = '/admin/reports/api';
            this.startDate = null;
            this.endDate = null;
            this.revenueChart = null;
            this.orderStatusChart = null;
            this.categoryChart = null;
            
            this.init();
        }

        init() {
            this.setupEventListeners();
            this.loadReports();
        }

        setupEventListeners() {
            // Period filter
            const periodFilter = document.getElementById('periodFilter');
            if (periodFilter) {
                periodFilter.addEventListener('change', (e) => {
                    const value = e.target.value;
                    if (value === 'custom') {
                        document.getElementById('startDateFilter').style.display = 'block';
                        document.getElementById('endDateFilter').style.display = 'block';
                    } else {
                        document.getElementById('startDateFilter').style.display = 'none';
                        document.getElementById('endDateFilter').style.display = 'none';
                        // Auto refresh when changing period
                        this.loadReports();
                    }
                });
            }
        }

        async loadReports() {
            try {
                const { startDate, endDate } = this.getDateRange();
                this.startDate = startDate;
                this.endDate = endDate;
                
                const response = await fetch(
                    `${this.apiEndpoint}?startDate=${startDate}&endDate=${endDate}`
                );
                
                if (!response.ok) throw new Error('Failed to fetch reports');
                
                const data = await response.json();
                this.renderReports(data);
            } catch (error) {
                console.error('Error loading reports:', error);
                this.showNotification('Không thể tải báo cáo', 'danger');
            }
        }

        getDateRange() {
            const period = document.getElementById('periodFilter')?.value || 'month';
            const today = new Date();
            
            switch (period) {
                case 'today':
                    return {
                        startDate: this.formatDate(today),
                        endDate: this.formatDate(today)
                    };
                case 'week':
                    const weekStart = new Date(today);
                    weekStart.setDate(today.getDate() - today.getDay());
                    return {
                        startDate: this.formatDate(weekStart),
                        endDate: this.formatDate(today)
                    };
                case 'month':
                    const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
                    return {
                        startDate: this.formatDate(monthStart),
                        endDate: this.formatDate(today)
                    };
                case 'year':
                    const yearStart = new Date(today.getFullYear(), 0, 1);
                    return {
                        startDate: this.formatDate(yearStart),
                        endDate: this.formatDate(today)
                    };
                case 'custom':
                    const startInput = document.getElementById('startDate');
                    const endInput = document.getElementById('endDate');
                    return {
                        startDate: startInput?.value || this.formatDate(new Date(today.getFullYear(), today.getMonth(), 1)),
                        endDate: endInput?.value || this.formatDate(today)
                    };
                default:
                    return {
                        startDate: this.formatDate(new Date(today.getFullYear(), today.getMonth(), 1)),
                        endDate: this.formatDate(today)
                    };
            }
        }

        formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        }

        renderReports(data) {
            this.renderStats(data.stats);
            this.renderCharts(data);
            this.renderTopProducts(data.topProducts);
            this.renderTopCustomers(data.topCustomers);
            this.renderRegionStats(data.regionStats);
        }

        renderStats(stats) {
            const container = document.getElementById('statsCards');
            if (!container) return;
            
            container.innerHTML = `
                <div class="col-md-3 mb-3">
                    <div class="card border-0 shadow-sm h-100 bg-primary text-white">
                        <div class="card-body">
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <i class="bi bi-currency-exchange fs-1 opacity-75"></i>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h6 class="mb-1 text-white-50">Tổng doanh thu</h6>
                                    <h3 class="mb-0">${this.formatCurrency(stats.totalRevenue)}</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 mb-3">
                    <div class="card border-0 shadow-sm h-100 bg-success text-white">
                        <div class="card-body">
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <i class="bi bi-cart-check fs-1 opacity-75"></i>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h6 class="mb-1 text-white-50">Tổng đơn hàng</h6>
                                    <h3 class="mb-0">${stats.totalOrders}</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 mb-3">
                    <div class="card border-0 shadow-sm h-100 bg-info text-white">
                        <div class="card-body">
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <i class="bi bi-people fs-1 opacity-75"></i>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h6 class="mb-1 text-white-50">Tổng khách hàng</h6>
                                    <h3 class="mb-0">${stats.totalCustomers}</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 mb-3">
                    <div class="card border-0 shadow-sm h-100 bg-warning text-white">
                        <div class="card-body">
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <i class="bi bi-box-seam fs-1 opacity-75"></i>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h6 class="mb-1 text-white-50">Tổng sản phẩm</h6>
                                    <h3 class="mb-0">${stats.totalProducts}</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }

        renderCharts(data) {
            this.renderRevenueChart(data.revenueChart);
            this.renderOrderStatusChart(data.orderStatusChart);
            this.renderCategoryChart(data.categoryRevenue);
        }

        renderRevenueChart(chartData) {
            const ctx = document.getElementById('revenueChart');
            if (!ctx) return;
            
            if (this.revenueChart) {
                this.revenueChart.destroy();
            }
            
            this.revenueChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: chartData.map(d => d.date),
                    datasets: [{
                        label: 'Doanh thu (VNĐ)',
                        data: chartData.map(d => d.revenue),
                        borderColor: 'rgb(13, 110, 253)',
                        backgroundColor: 'rgba(13, 110, 253, 0.1)',
                        tension: 0.4,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: true
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }

        renderOrderStatusChart(chartData) {
            const ctx = document.getElementById('orderStatusChart');
            if (!ctx) return;
            
            if (this.orderStatusChart) {
                this.orderStatusChart.destroy();
            }
            
            const colors = {
                'PENDING': 'rgb(255, 193, 7)',
                'CONFIRMED': 'rgb(13, 110, 253)',
                'DISPATCHED': 'rgb(0, 123, 255)',
                'DELIVERED': 'rgb(25, 135, 84)',
                'CANCELLED': 'rgb(220, 53, 69)',
                'RETURNED': 'rgb(253, 126, 20)'
            };
            
            this.orderStatusChart = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: chartData.map(d => d.status),
                    datasets: [{
                        data: chartData.map(d => d.count),
                        backgroundColor: chartData.map(d => colors[d.status] || 'rgb(128, 128, 128)')
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }

        renderCategoryChart(chartData) {
            const ctx = document.getElementById('categoryRevenueChart');
            if (!ctx) return;
            
            if (this.categoryChart) {
                this.categoryChart.destroy();
            }
            
            this.categoryChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: chartData.map(d => d.categoryName),
                    datasets: [{
                        label: 'Doanh thu (VNĐ)',
                        data: chartData.map(d => d.revenue),
                        backgroundColor: 'rgba(13, 110, 253, 0.8)',
                        borderColor: 'rgb(13, 110, 253)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }

        renderTopProducts(products) {
            const tbody = document.getElementById('topProductsTable');
            if (!tbody) return;
            
            if (products && products.length > 0) {
                tbody.innerHTML = products.map(p => `
                    <tr>
                        <td class="text-center">${p.rank}</td>
                        <td>${p.productName}</td>
                        <td class="text-end">${p.totalSold}</td>
                        <td class="text-end">${this.formatCurrency(p.revenue)}</td>
                    </tr>
                `).join('');
            } else {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Chưa có dữ liệu</td></tr>';
            }
        }

        renderTopCustomers(customers) {
            const tbody = document.getElementById('topCustomersTable');
            if (!tbody) return;
            
            if (customers && customers.length > 0) {
                tbody.innerHTML = customers.map(c => `
                    <tr>
                        <td class="text-center">${c.rank}</td>
                        <td>
                            <div>${c.customerName}</div>
                            <small class="text-muted">${c.customerEmail}</small>
                        </td>
                        <td class="text-end">${c.orderCount}</td>
                        <td class="text-end">${this.formatCurrency(c.totalSpent)}</td>
                    </tr>
                `).join('');
            } else {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Chưa có dữ liệu</td></tr>';
            }
        }

        renderRegionStats(stats) {
            const tbody = document.getElementById('ordersByRegionTable');
            if (!tbody) return;
            
            if (stats && stats.length > 0) {
                tbody.innerHTML = stats.map(s => `
                    <tr>
                        <td>${s.region}</td>
                        <td class="text-end">${s.orderCount}</td>
                        <td class="text-end">${this.formatCurrency(s.totalRevenue)}</td>
                        <td class="text-end">${this.formatCurrency(s.averageOrderValue)}</td>
                        <td class="text-center">
                            <span class="badge bg-primary">${s.percentage.toFixed(1)}%</span>
                        </td>
                    </tr>
                `).join('');
            } else {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Chưa có dữ liệu</td></tr>';
            }
        }

        formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(amount).replace('₫', 'đ');
        }

        showNotification(message, type) {
            if (window.notificationSystem) {
                window.notificationSystem.show(message, type);
            }
        }

        async refreshReports() {
            await this.loadReports();
            this.showNotification('Đã cập nhật báo cáo', 'success');
        }
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.reportsManagement = new ReportsManagement();
        });
    } else {
        window.reportsManagement = new ReportsManagement();
    }
})();

