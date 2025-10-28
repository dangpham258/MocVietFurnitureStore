// ========================================
// DASHBOARD MANAGEMENT JAVASCRIPT
// ========================================

console.log('Dashboard Management JS loaded successfully!');

(function() {
    'use strict';

    class DashboardManagement {
        constructor() {
            this.apiEndpoint = '/admin/dashboard/api';
            this.revenueChart = null;
            this.refreshInterval = null;
            this.init();
        }

        init() {
            // Không gọi renderRevenueChart() ở đây - để inline script trigger nó
            // Đảm bảo window.revenueChartData đã được đặt
            this.startAutoRefresh();
        }

        startAutoRefresh() {
            // Tự động refresh mỗi 1 phút
            this.refreshInterval = setInterval(() => {
                this.refreshDashboard();
            }, 60000); // 60000ms = 1 phút
        }

        stopAutoRefresh() {
            if (this.refreshInterval) {
                clearInterval(this.refreshInterval);
                this.refreshInterval = null;
            }
        }

        async refreshDashboard() {
            try {
                const response = await fetch(this.apiEndpoint);
                if (!response.ok) throw new Error('Failed to fetch dashboard data');
                
                const stats = await response.json();
                this.updateDashboardUI(stats);
            } catch (error) {
                console.error('Error refreshing dashboard:', error);
            }
        }

        updateDashboardUI(stats) {
            // Cập nhật bằng IDs nếu có (preferred, an toàn hơn)
            const idTotalUsers = document.getElementById('totalUsers');
            const idTotalCategories = document.getElementById('totalCategories');
            const idTotalCoupons = document.getElementById('totalCoupons');
            const idRevenueThisMonth = document.getElementById('revenueThisMonth');

            if (idTotalUsers) idTotalUsers.textContent = stats.totalUsers;
            if (idTotalCategories) idTotalCategories.textContent = stats.totalCategories;
            if (idTotalCoupons) idTotalCoupons.textContent = stats.totalCoupons;
            if (idRevenueThisMonth) idRevenueThisMonth.textContent = this.formatNumber(stats.revenueThisMonth) + 'đ';

            // Fallback to layout selectors cũ nếu IDs không có
            if (!idTotalUsers || !idTotalCategories || !idTotalCoupons || !idRevenueThisMonth) {
                const cards = document.querySelectorAll('.main-content .row:nth-child(2) .card-body h5');
                if (cards.length >= 4) {
                    cards[0].textContent = stats.totalUsers;
                    cards[1].textContent = stats.totalCategories;
                    cards[2].textContent = stats.totalCoupons;
                    cards[3].innerHTML = '<span>' + this.formatNumber(stats.revenueThisMonth) + '</span>đ';
                }
            }

            // Cập nhật số đơn hàng
            const idOrdersToday = document.getElementById('ordersToday');
            const idOrdersThisWeek = document.getElementById('ordersThisWeek');
            const idOrdersThisMonth = document.getElementById('ordersThisMonth');
            if (idOrdersToday) idOrdersToday.textContent = stats.ordersToday;
            if (idOrdersThisWeek) idOrdersThisWeek.textContent = stats.ordersThisWeek;
            if (idOrdersThisMonth) idOrdersThisMonth.textContent = stats.ordersThisMonth;

            // Fallback selectors cũ
            if (!idOrdersToday || !idOrdersThisWeek || !idOrdersThisMonth) {
                const orderCounts = document.querySelectorAll('.main-content .row:nth-child(3) strong');
                if (orderCounts.length >= 3) {
                    orderCounts[0].textContent = stats.ordersToday;
                    orderCounts[1].textContent = stats.ordersThisWeek;
                    orderCounts[2].textContent = stats.ordersThisMonth;
                }
            }

            // Cập nhật biểu đồ
            if (stats.revenueChart) {
                window.revenueChartData = stats.revenueChart;
                this.renderRevenueChart();
            }
        }

        formatNumber(num) {
            if (num >= 1000000) {
                return (num / 1000000).toFixed(1) + 'M';
            } else if (num >= 1000) {
                return (num / 1000).toFixed(1) + 'K';
            }
            return num.toString();
        }

        renderRevenueChart() {
            const ctx = document.getElementById('dashboardRevenueChart');
            if (!ctx) {
                return;
            }
            
            if (!window.revenueChartData) {
                return;
            }
            
            const chartData = window.revenueChartData;
            
            if (chartData.length === 0) {
                // Hiển thị trạng thái trống
                const emptyDiv = document.createElement('div');
                emptyDiv.className = 'text-center text-muted py-5';
                emptyDiv.innerHTML = '<i class="bi bi-graph-up fs-1 opacity-25 mb-2"></i><p>Chưa có dữ liệu doanh thu</p>';
                ctx.parentElement.appendChild(emptyDiv);
                return;
            }
            
            // Xóa trạng thái trống nếu có
            const emptyDiv = ctx.parentElement.querySelector('.text-center.text-muted');
            if (emptyDiv) {
                emptyDiv.remove();
            }
            
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

    }

    // Khởi tạo khi DOM đã tải xong
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.dashboardManagement = new DashboardManagement();
        });
    } else {
        window.dashboardManagement = new DashboardManagement();
    }
})();
