// ========================================
// ADMIN PANEL JAVASCRIPT - MỘC VIỆT FURNITURE STORE
// ========================================
// File này chứa tất cả JavaScript cho admin panel
// Bao gồm: sidebar toggle, form validation, search, modal, etc.

    
// ========================================
// KHỞI TẠO CÁC TÍNH NĂNG ADMIN
// ========================================

// Khởi tạo sidebar ngay lập tức TRƯỚC KHI DOM LOAD
(function() {
    // ĐỌC LOCALSTORAGE NGAY LẬP TỨC
    const sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
    
    // TẠO STYLE TAG ĐỂ ÁP DỤNG NGAY LẬP TỨC
    const style = document.createElement('style');
    style.id = 'sidebar-style'; // THÊM ID ĐỂ CÓ THỂ UPDATE
    style.textContent = `
        .admin-sidebar {
            transition: none !important;
        }
        .content-area {
            transition: none !important;
        }
        ${sidebarCollapsed ? `
            .admin-sidebar {
                left: -250px !important;
            }
            .content-area {
                margin-left: 0 !important;
            }
            .sidebar-toggle {
                display: block !important;
            }
        ` : `
            .admin-sidebar {
                left: 0px !important;
            }
            .content-area {
                margin-left: 250px !important;
            }
            .sidebar-toggle {
                display: none !important;
            }
        `}
        
        /* MOBILE: FORCE ĐÓNG MẶC ĐỊNH */
        @media (max-width: 991px) {
            .admin-sidebar {
                left: -250px !important;
            }
            .content-area {
                margin-left: 0 !important;
            }
            .sidebar-toggle {
                display: block !important;
            }
        }
    `;
    
    // THÊM STYLE VÀO HEAD NGAY LẬP TỨC
    document.head.appendChild(style);
    
    // KHÔNG XÓA STYLE TAG NỮA - ĐỂ NÓ LUÔN ÁP DỤNG
})();

// Hàm update header và breadcrumb
function updateHeaderAndBreadcrumb(url) {
    // Update header title based on URL
    const headerTitle = document.querySelector('.admin-header h4');
    const headerDescription = document.querySelector('.admin-header small');
    
    if (headerTitle && headerDescription) {
    switch(url) {
        case '/admin':
        case '/admin/':
        case '/admin/dashboard':
        case '/admin/dashboard/':
        case '/admin/dashboard/home':
                headerTitle.textContent = 'Dashboard Admin';
                headerDescription.textContent = 'Tổng quan hệ thống Mộc Việt';
            break;
        case '/admin/users':
                headerTitle.textContent = 'Quản lý Users';
                headerDescription.textContent = 'Quản lý tài khoản hệ thống';
            break;
        case '/admin/colors':
                headerTitle.textContent = 'Quản lý màu sắc';
                headerDescription.textContent = 'Quản lý màu sắc sản phẩm';
            break;
        case '/admin/categories':
                headerTitle.textContent = 'Quản lý danh mục';
                headerDescription.textContent = 'Quản lý danh mục sản phẩm';
            break;
        case '/admin/coupons':
                headerTitle.textContent = 'Quản lý mã giảm giá';
                headerDescription.textContent = 'Quản lý mã giảm giá và khuyến mãi';
            break;
        case '/admin/shipping':
                headerTitle.textContent = 'Quản lý phí vận chuyển';
                headerDescription.textContent = 'Quản lý phí vận chuyển';
            break;
        case '/admin/delivery-teams':
                headerTitle.textContent = 'Quản lý đội giao hàng';
                headerDescription.textContent = 'Quản lý đội giao hàng';
            break;
        case '/admin/banners':
                headerTitle.textContent = 'Quản lý banner';
                headerDescription.textContent = 'Quản lý banner quảng cáo';
            break;
        case '/admin/pages':
                headerTitle.textContent = 'Quản lý trang tĩnh';
                headerDescription.textContent = 'Quản lý trang tĩnh';
            break;
        case '/admin/showrooms':
                headerTitle.textContent = 'Quản lý showroom';
                headerDescription.textContent = 'Quản lý showroom';
            break;
        case '/admin/social-links':
                headerTitle.textContent = 'Quản lý liên kết MXH';
                headerDescription.textContent = 'Quản lý liên kết mạng xã hội';
            break;
        case '/admin/reports':
                headerTitle.textContent = 'Báo cáo & Thống kê';
                headerDescription.textContent = 'Báo cáo và thống kê hệ thống';
            break;
        case '/admin/notifications':
                headerTitle.textContent = 'Thông báo';
                headerDescription.textContent = 'Quản lý và theo dõi thông báo hệ thống';
            break;
        case '/admin/profile':
                headerTitle.textContent = 'Thông tin cá nhân';
                headerDescription.textContent = 'Cập nhật thông tin cá nhân và cài đặt tài khoản';
            break;
            default:
                headerTitle.textContent = 'Admin Panel';
                headerDescription.textContent = 'Quản trị hệ thống Mộc Việt';
        }
    }
    
    // Update breadcrumb
    const breadcrumbActive = document.querySelector('.admin-breadcrumb .breadcrumb-item.active');
    if (breadcrumbActive) {
        breadcrumbActive.textContent = headerTitle ? headerTitle.textContent : 'Admin Panel';
    }
}

// Hàm update active menu
function updateActiveMenu(url) {
    const navLinks = document.querySelectorAll('.admin-sidebar .nav-link');
    navLinks.forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');
        
        // Logic chính xác: chỉ match exact
        if (href) {
            // Chỉ match exact
            if (url === href) {
                link.classList.add('active');
            }
            // Trường hợp đặc biệt: Dashboard (/admin) phải match dashboard URLs
            else if (href === '/admin' && (url === '/admin' || url === '/admin/' || url === '/admin/dashboard' || url === '/admin/dashboard/' || url === '/admin/dashboard/home')) {
                link.classList.add('active');
            }
        }
    });
}

// Hàm update style tag khi toggle sidebar
function updateSidebarStyle(isCollapsed) {
    const existingStyle = document.querySelector('#sidebar-style');
    if (existingStyle) {
        existingStyle.textContent = `
            .admin-sidebar {
                transition: none !important;
            }
            .content-area {
                transition: none !important;
            }
            ${isCollapsed ? `
                .admin-sidebar {
                    left: -250px !important;
                }
                .content-area {
                    margin-left: 0 !important;
                }
                .sidebar-toggle {
                    display: block !important;
                }
            ` : `
                .admin-sidebar {
                    left: 0px !important;
                }
                .content-area {
                    margin-left: 250px !important;
                }
                .sidebar-toggle {
                    display: none !important;
                }
            `}
            
            /* MOBILE: FORCE ĐÓNG MẶC ĐỊNH */
            @media (max-width: 991px) {
                .admin-sidebar {
                    left: -250px !important;
                }
                .content-area {
                    margin-left: 0 !important;
                }
                .sidebar-toggle {
                    display: block !important;
                }
            }
        `;
    }
}

// Khởi tạo sidebar ngay lập tức khi DOM load
document.addEventListener('DOMContentLoaded', function() {
    // CẬP NHẬT HEADER NGAY LẬP TỨC - KHÔNG CHỜ
    const currentUrl = window.location.pathname;
    updateHeaderAndBreadcrumb(currentUrl);
    updateActiveMenu(currentUrl);
    
    // CHỜ MỘT CHÚT ĐỂ ĐẢM BẢO MỌI THỨ LOAD ỔN ĐỊNH
    setTimeout(() => {
        // Khởi tạo sidebar trước khi các tính năng khác
        initializeSidebar();
        
        // Khởi tạo các tính năng khác
        initializeBootstrapComponents();
    }, 150); // CHỜ 150MS ĐỂ TRÁNH GIỰT
});

// Hàm khởi tạo sidebar
function initializeSidebar() {
    const sidebar = document.querySelector('.admin-sidebar');
    const sidebarToggle = document.querySelector('.sidebar-toggle');
    
    if (!sidebar) return;
    
    // TẮT TRANSITION NGAY LẬP TỨC ĐỂ TRÁNH FLASH
    sidebar.classList.add('no-transition');
    
    if (window.innerWidth <= 991) {
        // Mobile: Đóng mặc định
        sidebar.classList.remove('show', 'open', 'collapsed');
        if (sidebarToggle) {
            sidebarToggle.style.display = 'block';
        }
    } else {
        // Desktop: Kiểm tra localStorage và áp dụng ngay
        const sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
        
        if (sidebarCollapsed) {
            // Đóng sidebar - THÊM CLASS COLLAPSED NGAY LẬP TỨC
            sidebar.classList.add('collapsed');
            sidebar.classList.remove('open');
        } else {
            // Mở sidebar - ĐẢM BẢO KHÔNG CÓ CLASS COLLAPSED
            sidebar.classList.remove('collapsed');
            sidebar.classList.add('open');
        }
    }
    
    // BẬT LẠI TRANSITION SAU KHI ĐÃ SET TRẠNG THÁI
    setTimeout(() => {
        sidebar.classList.remove('no-transition');
    }, 100);
}

// Hàm khởi tạo Bootstrap components
function initializeBootstrapComponents() {
    
    // ========================================
    // 1. KHỞI TẠO BOOTSTRAP COMPONENTS
    // ========================================
    
    // Khởi tạo tooltips (chú thích khi hover)
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Khởi tạo popovers (thông tin popup)
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    const popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // ========================================
    // 2. HIỆU ỨNG HEADER KHI SCROLL
    // ========================================
    
    // Tạo hiệu ứng header nổi bật khi scroll
    const header = document.querySelector('.admin-header');
    
    if (header) {
        window.addEventListener('scroll', function() {
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
            
            // Thêm class 'scrolled' khi scroll xuống > 50px
            if (scrollTop > 50) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        });
    }

    // ========================================
    // 3. LOGIC ĐIỀU KHIỂN SIDEBAR
    // ========================================
    
    // Lấy các element cần thiết
    const sidebarToggle = document.querySelector('.sidebar-toggle'); // Hamburger ở header
    const sidebarClose = document.querySelector('.sidebar-close');     // Hamburger ở sidebar
    const sidebar = document.querySelector('.admin-sidebar');         // Sidebar chính
    
    
    // Hàm điều khiển hiển thị hamburger
    // @param {boolean} isSidebarOpen - Trạng thái sidebar (mở/đóng)
    function toggleHamburgerVisibility(isSidebarOpen) {
        if (sidebarToggle) {
            if (window.innerWidth <= 991) {
                // Mobile: JavaScript điều khiển hiển thị
                if (isSidebarOpen) {
                    sidebarToggle.style.display = 'none';  // Ẩn hamburger header
                } else {
                    sidebarToggle.style.display = 'block'; // Hiện hamburger header
                }
            } else {
                // Desktop: CSS tự động điều khiển
                // Reset inline styles để CSS hoạt động
                sidebarToggle.style.display = '';
            }
        }
    }
    
    // Hàm toggle sidebar (mở/đóng)
    function toggleSidebar() {
        const contentArea = document.querySelector('.content-area');
        
        // THÊM CLASS USER-INTERACTING ĐỂ CÓ TRANSITION MƯỢT MÀ
        sidebar.classList.add('user-interacting');
        if (contentArea) {
            contentArea.classList.add('user-interacting');
        }
        
        if (window.innerWidth <= 991) {
            // Mobile: sử dụng class 'show'
            sidebar.classList.toggle('show');
            
            // Ẩn/hiện hamburger khi sidebar mở/đóng trên mobile
            const isOpen = sidebar.classList.contains('show');
            if (sidebarToggle) {
                sidebarToggle.style.display = isOpen ? 'none' : 'block';
            }
        } else {
            // Desktop: sử dụng class 'collapsed' và 'open'
            sidebar.classList.toggle('collapsed');
            sidebar.classList.toggle('open');
            
            // Lưu trạng thái sidebar vào localStorage
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed); // true = đóng, false = mở
            
            // UPDATE STYLE TAG KHI TOGGLE
            setTimeout(() => {
                updateSidebarStyle(isCollapsed);
            }, 50); // CHỜ 50MS ĐỂ TRÁNH GIỰT KHI TOGGLE
        }
    }
    
    // ========================================
    // 4. EVENT LISTENERS CHO SIDEBAR
    // ========================================
    
    // Event listener cho hamburger ở header
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            toggleSidebar();
            // Xác định trạng thái sidebar sau khi toggle
            const isOpen = window.innerWidth <= 991 ? 
                sidebar.classList.contains('show') : 
                !sidebar.classList.contains('collapsed');
            toggleHamburgerVisibility(isOpen);
        });
    }
    
    // Event listener cho hamburger ở sidebar
    if (sidebarClose && sidebar) {
        sidebarClose.addEventListener('click', function() {
            toggleSidebar();
            // Xác định trạng thái sidebar sau khi toggle
            const isOpen = window.innerWidth <= 991 ? 
                sidebar.classList.contains('show') : 
                !sidebar.classList.contains('collapsed');
            toggleHamburgerVisibility(isOpen);
        });
    }

    // Đóng sidebar khi click bên ngoài (chỉ trên mobile)
    document.addEventListener('click', function(event) {
        if (window.innerWidth <= 991) {
            const sidebar = document.querySelector('.admin-sidebar');
            const sidebarToggle = document.querySelector('.sidebar-toggle');
            
            // Nếu sidebar đang mở và click không phải vào sidebar/toggle
            if (sidebar && sidebar.classList.contains('show') && 
                !sidebar.contains(event.target) && 
                (!sidebarToggle || !sidebarToggle.contains(event.target))) {
                sidebar.classList.remove('show');
                toggleHamburgerVisibility(false); // Hiện lại hamburger header
            }
        }
    });

        // Xử lý khi thay đổi kích thước màn hình
        window.addEventListener('resize', function() {
            // CHỜ MỘT CHÚT ĐỂ TRÁNH GIỰT KHI RESIZE
            setTimeout(() => {
            const sidebar = document.querySelector('.admin-sidebar');
                
            if (sidebar) {
                if (window.innerWidth <= 991) {
                    // Chuyển sang mobile: DÙNG CLASS `show` - sidebar đóng mặc định
                    sidebar.classList.remove('show', 'open', 'collapsed'); // ĐẢM BẢO ĐÓNG
                    if (sidebarToggle) {
                        sidebarToggle.style.display = 'block'; // Hiện hamburger trên mobile
                    }
                } else {
                    // Chuyển sang desktop: khôi phục trạng thái từ localStorage
                    sidebar.classList.remove('show');
                    if (sidebarToggle) {
                        sidebarToggle.style.display = '';
                    }
                    // Khôi phục trạng thái sidebar từ localStorage
                    const sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
                    if (sidebarCollapsed) {
                        sidebar.classList.add('collapsed'); // Đóng
                        sidebar.classList.remove('open');
                    } else {
                        sidebar.classList.remove('collapsed'); // Mở
                        sidebar.classList.add('open');
                    }
                }
            }
            }, 100); // CHỜ 100MS ĐỂ TRÁNH GIỰT KHI RESIZE
        });

    // ========================================
    // 5. TÍNH NĂNG ALERT VÀ THÔNG BÁO
    // ========================================
    
    // Tự động ẩn alert sau 5 giây
    const alerts = document.querySelectorAll('.alert[data-auto-hide]');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // ========================================
    // 6. NOTIFICATION SYSTEM
    // ========================================
    
    // Notification function - sử dụng notification system mới
    function showNotification(message, type = 'info') {
        // Sử dụng notification system mới nếu có
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
            return;
        }
        
        // Fallback cho trường hợp notification system chưa load
        console.log(`Fallback notification [${type.toUpperCase()}]: ${message}`);
    }

    // ========================================
    // 7. FORM VALIDATION & PASSWORD TOGGLE
    // ========================================
    
    // Global function for password toggle
    window.togglePassword = function(inputId, button) {
        const input = document.getElementById(inputId);
        const icon = button.querySelector('i');
        
        if (!input || !icon) {
            console.error('Input or icon not found:', inputId);
            return;
        }
        
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('bi-eye');
            icon.classList.add('bi-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('bi-eye-slash');
            icon.classList.add('bi-eye');
        }
    };
    
    // Chỉ xử lý validation cho các form KHÔNG phải profile
    const forms = document.querySelectorAll('.needs-validation');
    
    forms.forEach(function(form) {
        // Skip profile forms - they are handled by OTP verification
        if (form.action.includes('/admin/profile/update') || form.action.includes('/admin/profile/change-password')) {
            return;
        }
        
        // Validation HTML5 cơ bản
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
        
        // Validation thời gian thực
        const fields = form.querySelectorAll('.form-control, .form-select');
        fields.forEach(function(field) {
            field.addEventListener('blur', function() {
                if (form.classList.contains('was-validated')) {
                    this.classList.toggle('is-valid', this.checkValidity());
                    this.classList.toggle('is-invalid', !this.checkValidity());
                }
            });
            
            field.addEventListener('input', function() {
                if (form.classList.contains('was-validated')) {
                    this.classList.toggle('is-valid', this.checkValidity());
                    this.classList.toggle('is-invalid', !this.checkValidity());
                }
            });
        });
    });

    // ========================================
    // 8. TÍNH NĂNG BẢNG DỮ LIỆU
    // ========================================
    
    // Cải thiện bảng dữ liệu với sorting
    const dataTables = document.querySelectorAll('.data-table');
    dataTables.forEach(function(table) {
        // Thêm chức năng sắp xếp cho các cột
        const headers = table.querySelectorAll('th[data-sort]');
        headers.forEach(function(header) {
            header.style.cursor = 'pointer';
            header.addEventListener('click', function() {
                // Logic sắp xếp đơn giản (có thể nâng cấp)
                console.log('Sort by:', header.dataset.sort);
            });
        });
    });

    // ========================================
    // 9. CẢI THIỆN MODAL
    // ========================================
    
    // Tự động focus vào input đầu tiên khi modal mở
    const modals = document.querySelectorAll('.modal');
    modals.forEach(function(modal) {
        modal.addEventListener('show.bs.modal', function() {
            const firstInput = modal.querySelector('input, textarea, select');
            if (firstInput) {
                setTimeout(function() {
                    firstInput.focus();
                }, 100);
            }
        });
    });

    // ========================================
    // 10. TÍNH NĂNG TÌM KIẾM
    // ========================================
    
    // Tìm kiếm trong bảng dữ liệu
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const targetTable = document.querySelector(this.dataset.target);
            
            if (targetTable) {
                const rows = targetTable.querySelectorAll('tbody tr');
                rows.forEach(function(row) {
                    const text = row.textContent.toLowerCase();
                    if (text.includes(searchTerm)) {
                        row.style.display = '';      // Hiện row
                    } else {
                        row.style.display = 'none';  // Ẩn row
                    }
                });
            }
        });
    });

    // ========================================
    // 11. XÁC NHẬN XÓA
    // ========================================
    
    // Hiển thị dialog xác nhận trước khi xóa
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            if (!confirm('Bạn có chắc chắn muốn xóa?')) {
                event.preventDefault();
            }
        });
    });


    // ========================================
    // 12. AJAX NAVIGATION - KHÔNG RELOAD SIDEBAR
    // ========================================
    
    // Hàm load content bằng AJAX
    function loadPageContent(url) {
        // Hiển thị loading
        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            mainContent.innerHTML = `
                <div class="d-flex justify-content-center align-items-center" style="height: 300px;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <span class="ms-3">Đang tải...</span>
                </div>
            `;
        }
        
        // AJAX request
        fetch(url, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'text/html'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(html => {
            // Phân tích HTML và trích xuất nội dung
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const newContent = doc.querySelector('.main-content');
            
            if (newContent && mainContent) {
                // Cập nhật nội dung
                mainContent.innerHTML = newContent.innerHTML;
                
                // Cập nhật tiêu đề trang
                const newTitle = doc.querySelector('title');
                if (newTitle) {
                    document.title = newTitle.textContent;
                }
                
                // Cập nhật menu hoạt động (luôn cập nhật)
                updateActiveMenu(url);
                
                // Cập nhật header và breadcrumb (luôn cập nhật)
                updateHeaderAndBreadcrumb(url);
                
                // Khởi tạo lại các thành phần
                initializePageComponents();
                
                // Xử lý đặc biệt cho dashboard - kích hoạt render biểu đồ sau khi nội dung tải xong
                if (url === '/admin' || url === '/admin/' || url === '/admin/dashboard' || url === '/admin/dashboard/' || url === '/admin/dashboard/home') {
                    setTimeout(() => {
                        if (window.dashboardManagement) {
                            // Lấy dữ liệu mới từ API và render biểu đồ
                            fetch('/admin/dashboard/api')
                                .then(response => response.json())
                                .then(data => {
                                    window.revenueChartData = data.revenueChart || [];
                                    window.dashboardManagement.renderRevenueChart();
                                })
                                .catch(error => console.error('Error fetching dashboard data:', error));
                        }
                    }, 700);
                }
            }
        })
        .catch(error => {
            console.error('Error loading page:', error);
            console.error('URL:', url);
            if (mainContent) {
                mainContent.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        Lỗi tải trang: ${error.message}
                        <br><small>URL: ${url}</small>
                        <br><a href="${url}" class="alert-link">Thử lại</a>
                    </div>
                `;
            }
        });
    }
    
    // Hàm khởi tạo components cho trang mới
    function initializePageComponents() {
        // Khởi tạo lại các tooltip
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.forEach(function (tooltipTriggerEl) {
            new bootstrap.Tooltip(tooltipTriggerEl);
        });
        
        // Khởi tạo lại các modal
        const modalTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="modal"]'));
        modalTriggerList.forEach(function(modalTriggerEl) {
            new bootstrap.Modal(modalTriggerEl);
        });
        
        // Khởi tạo lại các form
        const forms = document.querySelectorAll('.needs-validation');
        forms.forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            });
        });
        
        // Khởi tạo lại các input tìm kiếm
        const searchInputs = document.querySelectorAll('.search-input');
        searchInputs.forEach(function(input) {
            input.addEventListener('input', function() {
                const searchTerm = this.value.toLowerCase();
                const targetTable = document.querySelector(this.dataset.target);
                
                if (targetTable) {
                    const rows = targetTable.querySelectorAll('tbody tr');
                    rows.forEach(function(row) {
                        const text = row.textContent.toLowerCase();
                        if (text.includes(searchTerm)) {
                            row.style.display = '';
                        } else {
                            row.style.display = 'none';
                        }
                    });
                }
            });
        });
        
        // Khởi tạo các thành phần cụ thể cho trang hiện tại dựa trên URL hiện tại
        const currentUrl = window.location.pathname;
        
        // Định nghĩa cấu hình module
        const modules = [
            {
                urlPattern: '/admin/users',
                scriptPath: '/js/admin/users.js',
                classRef: 'UsersManagement',
                instanceName: 'usersManagement',
                initFunction: 'initializeUsersManagement'
            },
            {
                urlPattern: '/admin/colors',
                scriptPath: '/js/admin/colors.js',
                classRef: 'ColorsManagement',
                instanceName: 'colorsManagement',
                initFunction: 'initializeColorsManagement'
            },
            {
                urlPattern: '/admin/categories',
                scriptPath: '/js/admin/categories.js',
                classRef: 'CategoriesManagement',
                instanceName: 'categoriesManagement',
                initFunction: 'initializeCategoriesManagement'
            },
            {
                urlPattern: '/admin/coupons',
                scriptPath: '/js/admin/coupons.js',
                classRef: 'CouponsManagement',
                instanceName: 'couponsManagement',
                initFunction: 'initializeCouponsManagement'
            },
            {
                urlPattern: '/admin/shipping',
                scriptPath: '/js/admin/shipping.js',
                classRef: 'ShippingManagement',
                instanceName: 'shippingManagement',
                initFunction: 'initializeShippingManagement'
            },
            {
                urlPattern: '/admin/delivery-teams',
                scriptPath: '/js/admin/delivery-teams.js',
                classRef: 'DeliveryTeamsManagement',
                instanceName: 'deliveryTeamsManagement',
                initFunction: 'initializeDeliveryTeamsManagement'
            },
            {
                urlPattern: '/admin/social-links',
                scriptPath: '/js/admin/social-links.js',
                classRef: 'SocialLinksManagement',
                instanceName: 'socialLinksManagement',
                initFunction: 'initializeSocialLinksManagement'
            },
            {
                urlPattern: '/admin/banners',
                scriptPath: '/js/admin/banners.js',
                classRef: 'BannersManagement',
                instanceName: 'bannersManagement',
                initFunction: 'initializeBannersManagement'
            },
            {
                urlPattern: '/admin/showrooms',
                scriptPath: '/js/admin/showrooms.js',
                classRef: 'ShowroomsManagement',
                instanceName: 'showroomsManagement',
                initFunction: 'initializeShowroomsManagement'
            },
            {
                urlPattern: '/admin/pages',
                scriptPath: '/js/admin/pages.js',
                classRef: 'PagesManagement',
                instanceName: 'pagesManagement',
                initFunction: 'initializePagesManagement'
            },
            {
                urlPattern: '/admin/reports',
                scriptPath: '/js/admin/reports.js',
                classRef: 'ReportsManagement',
                instanceName: 'reportsManagement',
                initFunction: 'initializeReportsManagement'
            },
            {
                urlPattern: '/admin/dashboard',
                scriptPath: '/js/admin/dashboard.js',
                classRef: 'DashboardManagement',
                instanceName: 'dashboardManagement',
                initFunction: 'initializeDashboardManagement'
            },
            {
                urlPattern: '/admin',
                scriptPath: '/js/admin/dashboard.js',
                classRef: 'DashboardManagement',
                instanceName: 'dashboardManagement',
                initFunction: 'initializeDashboardManagement'
            },
            {
                urlPattern: '/admin/notifications',
                scriptPath: '/js/admin/notifications.js',
                classRef: 'NotificationsManagement',
                instanceName: 'notificationsManagement',
                initFunction: 'initializeNotificationsManagement'
            }
        ];
        
        // Khởi tạo các module phù hợp với URL hiện tại
        modules.forEach(module => {
            if (currentUrl.includes(module.urlPattern)) {
                initModule(module);
            }
        });
    }
    
    /**
     * Khởi tạo một module theo cách động
     * @param {Object} module - Module configuration
     */
    function initModule(module) {
        const { scriptPath, classRef, instanceName, initFunction } = module;
        
        // Kiểm tra nếu đã khởi tạo và tồn tại
        if (window[instanceName] && typeof window[instanceName] !== 'undefined') {
            // Khởi tạo lại dữ liệu bằng cách gọi loadData, loadPages, loadBanners, etc.
            const methodsToTry = ['loadData', 'loadPages', 'loadBanners', 'loadColors', 'loadCategories', 'loadCoupons', 'loadShipping', 'loadShowrooms', 'loadUsers', 'loadSocialLinks', 'loadDeliveryTeams', 'loadReports', 'loadNotifications'];
            
            for (const methodName of methodsToTry) {
                if (typeof window[instanceName][methodName] === 'function') {
                    try {
                        window[instanceName][methodName]();
                        break;
                    } catch (error) {
                        console.error(`Error calling ${methodName} for ${classRef}:`, error);
                    }
                }
            }
            
            // Trường hợp đặc biệt: Dashboard cần render lại biểu đồ khi nội dung được tải lại
            if (instanceName === 'dashboardManagement') {
                setTimeout(() => {
                    if (window.dashboardManagement) {
                        // Lấy dữ liệu mới từ API và cập nhật biểu đồ
                        fetch('/admin/dashboard/api')
                            .then(response => response.json())
                            .then(data => {
                                window.revenueChartData = data.revenueChart || [];
                                window.dashboardManagement.renderRevenueChart();
                            })
                            .catch(error => {
                                console.error('Error fetching dashboard data:', error);
                            });
                    }
                }, 600);
            }
            
            return;
        }
        
        // Kiểm tra nếu hàm init tồn tại
        if (typeof window[initFunction] === 'function') {
            window[initFunction]();
            return;
        }
        
        // Kiểm tra nếu class tồn tại
        if (typeof window[classRef] !== 'undefined') {
            if (window[instanceName]) {
                delete window[instanceName];
            }
            try {
                window[instanceName] = new window[classRef]();
            } catch (error) {
                console.error(`Error initializing ${classRef}:`, error);
            }
            return;
        }
        
        // Tải script theo cách động nếu chưa tải
        loadScript(scriptPath, () => {
            if (typeof window[initFunction] === 'function') {
                window[initFunction]();
            } else if (typeof window[classRef] !== 'undefined') {
                if (window[instanceName]) {
                    delete window[instanceName];
                }
                try {
                    window[instanceName] = new window[classRef]();
                } catch (error) {
                    console.error(`Error initializing ${classRef}:`, error);
                }
            }
        });
    }
    
    /**
     * Tải script theo cách động
     * @param {string} src - Script source path
     * @param {Function} callback - Callback after load
     */
    function loadScript(src, callback) {
        // Kiểm tra nếu script đã được tải
        const existingScript = document.querySelector(`script[src="${src}"]`);
        if (existingScript) {
            if (callback) callback();
            return;
        }
        
        const script = document.createElement('script');
        script.src = src + '?v=' + Date.now();
        script.onload = callback;
        script.onerror = () => {
            console.error(`Failed to load ${src}`);
        };
        document.body.appendChild(script);
    }
    
    // Chặn click trên sidebar navigation
    const sidebarLinks = document.querySelectorAll('.admin-sidebar .nav-link');
    sidebarLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            const href = this.getAttribute('href');
            
            // Bỏ qua liên kết ngoài và logout
            if (href === '/' || href === '/logout' || href.startsWith('http')) {
                return; // Cho phép xử lý bằng navigation thông thường
            }
            
            // Ngăn chặn navigation mặc định
            event.preventDefault();
            
            // Cập nhật URL trước
            history.pushState({ url: href }, '', href);
            
            // Tải nội dung via AJAX
            loadPageContent(href);
        });
    });
    
    // Xử lý nút back/forward của trình duyệt
    window.addEventListener('popstate', function(event) {
        const url = window.location.pathname;
        loadPageContent(url);
    });
    
    // LƯU TRỮ TRẠNG THÁI SIDEBAR TRONG TOÀN BỘ NAVIGATION
    window.addEventListener('beforeunload', function() {
        const sidebar = document.querySelector('.admin-sidebar');
        if (sidebar) {
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed);
        }
    });

    // Khởi tạo các thành phần cụ thể cho trang đầu tiên (không phải AJAX)
    // Đảm bảo modules được khởi tạo khi trang được tải trực tiếp (F5/reload)
    initializePageComponents();
    
    // Tải thông báo header
    loadHeaderNotifications();
    
    // Tự động làm mới thông báo header mỗi 10 giây
    setInterval(loadHeaderNotifications, 10000);
}

// Theo dõi số thông báo chưa đọc để phát hiện thông báo mới
let previousUnreadCount = 0;

// Tải thông báo header
async function loadHeaderNotifications() {
    try {
        const response = await fetch('/admin/notifications/api');
        if (!response.ok) throw new Error('Failed to fetch notifications');
        
        const data = await response.json();
        const unreadCount = data.unreadCount || 0;
        const notifications = data.notifications || [];
        
        // Kiểm tra nếu có thông báo mới
        if (unreadCount > previousUnreadCount && previousUnreadCount > 0) {
            const newCount = unreadCount - previousUnreadCount;
            window.notificationSystem.show(
                `Bạn có ${newCount} thông báo mới!`,
                'success'
            );
        }
        
        previousUnreadCount = unreadCount;
        
        // Cập nhật badge
        const badge = document.getElementById('notificationBadge');
        const unreadCountEl = document.getElementById('headerUnreadCount');
        const listEl = document.getElementById('headerNotificationsList');
        
        if (badge) {
            if (unreadCount > 0) {
                badge.textContent = unreadCount > 99 ? '99+' : unreadCount;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }
        
        if (unreadCountEl) {
            unreadCountEl.textContent = unreadCount;
        }
        
        if (listEl && notifications.length > 0) {
            listEl.innerHTML = notifications.slice(0, 5).map(n => `
                <li>
                    <a class="dropdown-item ${n.isRead ? '' : 'bg-light'}" href="/admin/notifications">
                        <div class="d-flex w-100 justify-content-between">
                            <strong class="mb-1">${n.title}</strong>
                            <small>${formatDateTime(n.createdAt)}</small>
                        </div>
                        <p class="mb-1 small text-truncate">${n.message || ''}</p>
                    </a>
                </li>
            `).join('');
            
            if (notifications.length > 5) {
                listEl.innerHTML += '<li><hr class="dropdown-divider"></li>';
            }
        } else if (listEl) {
            listEl.innerHTML = '<li class="px-3 py-2 text-center text-muted"><small>Không có thông báo mới</small></li>';
        }
    } catch (error) {
        console.error('Error loading header notifications:', error);
    }
}

// Hàm hỗ trợ định dạng datetime
function formatDateTime(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    
    if (diffMins < 1) return 'Vừa xong';
    if (diffMins < 60) return `${diffMins} phút trước`;
    if (diffMins < 1440) return `${Math.floor(diffMins / 60)} giờ trước`;
    return `${Math.floor(diffMins / 1440)} ngày trước`;
}