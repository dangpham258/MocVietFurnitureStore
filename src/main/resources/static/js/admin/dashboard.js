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
            // Don't call renderRevenueChart() here - let inline script trigger it
            // This ensures window.revenueChartData is already set
            this.startAutoRefresh();
        }

        startAutoRefresh() {
            // Auto-refresh every 1 minute
            this.refreshInterval = setInterval(() => {
                this.refreshDashboard();
            }, 60000); // 60000ms = 1 minute
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
            // Update delicate stats
            const totalUsersElement = document.querySelector('.card-body h5');
            if (totalUsersElement && document.querySelector('.card-body small').textContent.includes('Users')) {
                totalUsersElement.textContent = stats.totalUsers;
            }

            // Update all stat cards
            const cards = document.querySelectorAll('.card-body');
            if (cards.length >= 4) {
                cards[0].querySelector('h5').textContent = stats.totalUsers;
                cards[1].querySelector('h5').textContent = stats.totalCategories;
                cards[2].querySelector('h5').textContent = stats.totalCoupons;
                cards[3].querySelector('h5').textContent = this.formatNumber(stats.revenueThisMonth);
            }

            // Update order counts
            const orderCounts = document.querySelectorAll('.card-body strong');
            if (orderCounts.length >= 3) {
                orderCounts[0].textContent = stats.ordersToday;
                orderCounts[1].textContent = stats.ordersThisWeek;
                orderCounts[2].textContent = stats.ordersThisMonth;
            }

            // Update chart
            if (stats.revenueChart && this.revenueChart) {
                this.revenueChart.data.labels = stats.revenueChart.map(d => d.date);
                this.revenueChart.data.datasets[0].data = stats.revenueChart.map(d => d.revenue);
                this.revenueChart.update();
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
                ctx.parentElement.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-graph-up fs-1 opacity-25 mb-2"></i><p>Chưa có dữ liệu doanh thu</p></div>';
                return;
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

        formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(amount).replace('₫', 'đ');
        }
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.dashboardManagement = new DashboardManagement();
        });
    } else {
        window.dashboardManagement = new DashboardManagement();
    }
})();

