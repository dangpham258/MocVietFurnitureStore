// ========================================
// ADMIN PANEL JAVASCRIPT - MỘC VIỆT FURNITURE STORE
// ========================================
// File này chứa tất cả JavaScript cho admin panel
// Bao gồm: sidebar toggle, form validation, search, modal, etc.

    console.log('Admin JS loaded successfully!');
    

// ========================================
// KHỞI TẠO CÁC TÍNH NĂNG ADMIN
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    
    // ========================================
    // 1. KHỞI TẠO BOOTSTRAP COMPONENTS
    // ========================================
    
    // Khởi tạo tooltips (chú thích khi hover)
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Khởi tạo popovers (thông tin popup)
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
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
        if (window.innerWidth <= 991) {
            // Mobile: sử dụng class 'show'
            sidebar.classList.toggle('show');
        } else {
            // Desktop: sử dụng class 'collapsed'
            sidebar.classList.add('user-interacting'); // BẬT TRANSITION
            sidebar.classList.toggle('collapsed');
            // Lưu trạng thái sidebar vào localStorage
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed);
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
                !sidebarToggle.contains(event.target)) {
                sidebar.classList.remove('show');
                toggleHamburgerVisibility(false); // Hiện lại hamburger header
            }
        }
    });

        // Xử lý khi thay đổi kích thước màn hình
        window.addEventListener('resize', function() {
            const sidebar = document.querySelector('.admin-sidebar');
            if (sidebar) {
                if (window.innerWidth <= 991) {
                    // Chuyển sang mobile: DÙNG CLASS `show` - sidebar đóng mặc định
                    sidebar.classList.remove('show', 'collapsed'); // ĐẢM BẢO ĐÓNG
                    if (sidebarToggle) {
                        sidebarToggle.style.display = 'block';
                    }
                } else {
                    // Chuyển sang desktop: khôi phục trạng thái từ localStorage
                    sidebar.classList.remove('show');
                    sidebar.style.left = ''; // RESET INLINE STYLE
                    if (sidebarToggle) {
                        sidebarToggle.style.display = '';
                    }
                    // Khôi phục trạng thái sidebar từ localStorage
                    const sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
                    if (sidebarCollapsed) {
                        sidebar.classList.add('collapsed');
                    } else {
                        sidebar.classList.remove('collapsed');
                    }
                }
            }
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
    // 6. VALIDATION FORM
    // ========================================
    
    // Xử lý validation cho các form có class 'needs-validation'
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

    // ========================================
    // 7. TÍNH NĂNG BẢNG DỮ LIỆU
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
    // 8. CẢI THIỆN MODAL
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
    // 9. TÍNH NĂNG TÌM KIẾM
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
    // 10. XÁC NHẬN XÓA
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
    // 11. TRẠNG THÁI LOADING
    // ========================================
    
    // Hiển thị loading khi submit form
    const submitButtons = document.querySelectorAll('.btn-submit');
    submitButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            const form = this.closest('form');
            if (form && form.checkValidity()) {
                this.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';
                this.disabled = true;
            }
        });
    });

    // ========================================
    // 12. KHỞI TẠO TRẠNG THÁI BAN ĐẦU
    // ========================================
    
    // Thiết lập trạng thái ban đầu của sidebar
    if (sidebar) {
        // FORCE SIDEBAR ĐÓNG NGAY TỪ ĐẦU ĐỂ TRÁNH ANIMATION
        sidebar.style.left = '-250px';
        sidebar.style.transition = 'none';
        
        if (window.innerWidth <= 991) {
            // Mobile: DÙNG CLASS `show` - sidebar đóng mặc định
            sidebar.classList.remove('show', 'collapsed'); // ĐẢM BẢO ĐÓNG
            if (sidebarToggle) {
                sidebarToggle.style.display = 'block'; // Hiện hamburger header
            }
        } else {
            // Desktop: MẶC ĐỊNH SIDEBAR MỞ (không collapsed)
            // Chỉ đóng nếu localStorage có giá trị 'true'
            const sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
            
            if (sidebarCollapsed) {
                sidebar.classList.add('collapsed');
            } else {
                sidebar.classList.remove('collapsed');
            }
            // Mặc định: không có class collapsed = sidebar mở
            // CSS sẽ tự động điều khiển hamburger visibility
        }
        
        // THÊM CLASS INITIALIZED CHO CONTENT AREA TRƯỚC
        const contentArea = document.querySelector('.content-area');
        if (contentArea) {
            contentArea.classList.add('content-initialized');
        }
        
        // THÊM CLASS INITIALIZED ĐỂ CSS HOẠT ĐỘNG
        sidebar.classList.add('sidebar-initialized');
        
        // ĐỢI 1 FRAME ĐỂ CSS ÁP DỤNG HOÀN TOÀN
        requestAnimationFrame(() => {
            sidebar.style.left = '';
            sidebar.style.transition = '';
            
            // BẬT TRANSITION CHO CONTENT AREA SAU KHI SETUP XONG
            if (contentArea) {
                contentArea.style.transition = 'margin-left 0.3s ease';
            }
        });
    }

    // ========================================
    // 13. AJAX NAVIGATION - KHÔNG RELOAD SIDEBAR
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
            // Parse HTML và extract content
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const newContent = doc.querySelector('.main-content');
            
            if (newContent && mainContent) {
                // Update content
                mainContent.innerHTML = newContent.innerHTML;
                
                // Update page title
                const newTitle = doc.querySelector('title');
                if (newTitle) {
                    document.title = newTitle.textContent;
                }
                
                // Update active menu
                updateActiveMenu(url);
                
                // Update header and breadcrumb
                updateHeaderAndBreadcrumb(url);
                
                // Re-initialize components
                initializePageComponents();
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
                // Exact match only
                if (url === href) {
                    link.classList.add('active');
                }
                // Special case: Dashboard (/admin) should match dashboard URLs
                else if (href === '/admin' && (url === '/admin' || url === '/admin/' || url === '/admin/dashboard' || url === '/admin/dashboard/' || url === '/admin/dashboard/home')) {
                    link.classList.add('active');
                }
            }
        });
    }
    
    // Hàm khởi tạo components cho trang mới
    function initializePageComponents() {
        // Re-initialize tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
        
        // Re-initialize modals
        var modalTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="modal"]'));
        modalTriggerList.forEach(function(modalTriggerEl) {
            new bootstrap.Modal(modalTriggerEl);
        });
        
        // Re-initialize forms
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
        
        // Re-initialize search inputs
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
    }
    
    // Intercept sidebar navigation clicks
    const sidebarLinks = document.querySelectorAll('.admin-sidebar .nav-link');
    sidebarLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            const href = this.getAttribute('href');
            
            // Skip external links và logout
            if (href === '/' || href === '/logout' || href.startsWith('http')) {
                return; // Let normal navigation handle these
            }
            
            // Prevent default navigation
            event.preventDefault();
            
            // Load content via AJAX
            loadPageContent(href);
            
            // Update URL without reload
            history.pushState(null, '', href);
        });
    });
    
    // Handle browser back/forward buttons
    window.addEventListener('popstate', function(event) {
        const url = window.location.pathname;
        loadPageContent(url);
    });
    
    // PERSIST SIDEBAR STATE ACROSS NAVIGATION
    window.addEventListener('beforeunload', function() {
        if (sidebar) {
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed);
        }
    });

    console.log('Admin panel initialized successfully!');
});