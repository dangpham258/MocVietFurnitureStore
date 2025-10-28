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
            // Update all stat cards (4 cards in first row)
            const cards = document.querySelectorAll('.main-content .row:nth-child(2) .card-body h5');
            if (cards.length >= 4) {
                cards[0].textContent = stats.totalUsers;
                cards[1].textContent = stats.totalCategories;
                cards[2].textContent = stats.totalCoupons;
                cards[3].innerHTML = '<span>' + this.formatNumber(stats.revenueThisMonth) + '</span>đ';
            }

            // Update order counts (in the right card)
            const orderCounts = document.querySelectorAll('.main-content .row:nth-child(3) strong');
            if (orderCounts.length >= 3) {
                orderCounts[0].textContent = stats.ordersToday;
                orderCounts[1].textContent = stats.ordersThisWeek;
                orderCounts[2].textContent = stats.ordersThisMonth;
            }

            // Update chart
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
                // Show empty state
                const emptyDiv = document.createElement('div');
                emptyDiv.className = 'text-center text-muted py-5';
                emptyDiv.innerHTML = '<i class="bi bi-graph-up fs-1 opacity-25 mb-2"></i><p>Chưa có dữ liệu doanh thu</p>';
                ctx.parentElement.appendChild(emptyDiv);
                return;
            }
            
            // Remove empty state if exists
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

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.dashboardManagement = new DashboardManagement();
        });
    } else {
        window.dashboardManagement = new DashboardManagement();
    }
})();
