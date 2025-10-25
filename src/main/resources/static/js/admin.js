// ========================================
// ADMIN PANEL JAVASCRIPT - MỘC VIỆT FURNITURE STORE
// ========================================
// File này chứa tất cả JavaScript cho admin panel
// Bao gồm: sidebar toggle, form validation, search, modal, etc.

console.log('Admin JS loaded successfully!');

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
                !sidebarToggle.contains(event.target)) {
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
    
    // Notification stack management
    let notificationStack = [];
    
    // Tạo notification popup với animation đẹp và stack support (tối đa 3)
    function showNotification(message, type = 'info') {
        // Kiểm tra notification trùng lặp (chỉ kiểm tra type và thời gian)
        const duplicateIndex = notificationStack.findIndex(notif => {
            const typeElement = notif.querySelector('.fw-semibold');
            if (!typeElement) return false;
            
            const existingType = typeElement.textContent.trim();
            const currentTypeText = type === 'success' ? 'Thành công!' : 
                                  type === 'danger' ? 'Lỗi!' : 
                                  type === 'warning' ? 'Cảnh báo!' : 'Thông báo!';
            
            // Chỉ kiểm tra type và thời gian (trong vòng 2 giây)
            const notificationTime = notif.dataset.timestamp || 0;
            const currentTime = Date.now();
            const timeDiff = currentTime - notificationTime;
            
            return existingType === currentTypeText && timeDiff < 2000; // 2 giây
        });
        
        // Nếu trùng lặp thì không hiện
        if (duplicateIndex > -1) {
            console.log('Duplicate notification prevented:', message);
            return;
        }
        
        // Giới hạn stack tối đa 3 notifications
        if (notificationStack.length >= 3) {
            // Xóa notification cũ nhất với animation
            const oldestNotification = notificationStack.shift();
            if (oldestNotification && oldestNotification.parentNode) {
                oldestNotification.style.right = '-400px';
                oldestNotification.style.opacity = '0';
                oldestNotification.style.transform = 'translateX(20px)';
                
                setTimeout(() => {
                    if (oldestNotification.parentNode) {
                        oldestNotification.remove();
                    }
                }, 400);
            }
        }
        
        // Tạo notification element
        const notification = document.createElement('div');
        notification.className = `alert alert-${type} alert-dismissible position-fixed notification-popup`;
        
        // Calculate position based on stack
        const stackIndex = notificationStack.length;
        const topPosition = 20 + (stackIndex * 80); // 80px spacing between notifications
        
        notification.style.cssText = `
            top: ${topPosition}px;
            right: -300px;
            z-index: ${9999 + stackIndex};
            min-width: 250px;
            max-width: 300px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
            border: none;
            border-radius: 8px;
            padding: 12px 16px;
            font-weight: 500;
            transform: translateX(0);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            opacity: 0;
        `;
        
        // Icon mapping
        const iconMap = {
            'success': 'check-circle-fill',
            'danger': 'exclamation-triangle-fill',
            'warning': 'exclamation-triangle-fill',
            'info': 'info-circle-fill'
        };
        
        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="bi bi-${iconMap[type] || 'info-circle-fill'} me-3" style="font-size: 1.2rem;"></i>
                <div class="flex-grow-1">
                    <div class="fw-semibold mb-1">${type === 'success' ? 'Thành công!' : type === 'danger' ? 'Lỗi!' : type === 'warning' ? 'Cảnh báo!' : 'Thông báo!'}</div>
                    <div class="small">${message}</div>
                </div>
                <button type="button" class="btn-close btn-close-sm ms-2" data-bs-dismiss="alert" style="opacity: 0.7;"></button>
            </div>
        `;
        
        // Thêm timestamp để chống spam
        notification.dataset.timestamp = Date.now();
        
        // Thêm vào body và stack
        document.body.appendChild(notification);
        notificationStack.push(notification);
        
        // Animation slide-in
        requestAnimationFrame(() => {
            notification.style.right = '20px';
            notification.style.opacity = '1';
        });
        
        // Function to remove notification from stack
        const removeFromStack = () => {
            const index = notificationStack.indexOf(notification);
            if (index > -1) {
                notificationStack.splice(index, 1);
                // Reposition remaining notifications
                notificationStack.forEach((notif, i) => {
                    const newTop = 20 + (i * 80);
                    notif.style.top = `${newTop}px`;
                    notif.style.zIndex = `${9999 + i}`;
                });
            }
        };
        
        // Tự động ẩn sau 4 giây với animation slide-out
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.right = '-400px';
                notification.style.opacity = '0';
                notification.style.transform = 'translateX(20px)';
                
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.remove();
                        removeFromStack();
                    }
                }, 400);
            }
        }, 2222);
        
        // Close button functionality
        const closeBtn = notification.querySelector('.btn-close');
        closeBtn.addEventListener('click', () => {
            notification.style.right = '-400px';
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(20px)';
            
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                    removeFromStack();
                }
            }, 400);
        });
    }
    
    // Kiểm tra URL parameters để hiển thị notification
    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get('message');
    const messageType = urlParams.get('type') || 'info';
    
    if (message) {
        showNotification(decodeURIComponent(message), messageType);
        
        // Xóa parameters khỏi URL
        const newUrl = window.location.pathname;
        window.history.replaceState({}, document.title, newUrl);
    }

    // ========================================
    // 7. VALIDATION FORM
    // ========================================
    
    // Xử lý validation cho các form có class 'needs-validation'
    const forms = document.querySelectorAll('.needs-validation');
    
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission
            
            // Handle form submission via AJAX
            handleFormSubmission(form);
        });
    });
    
    // Function to handle form submission via AJAX
    function handleFormSubmission(form) {
        const submitButton = form.querySelector('.btn-submit');
        
        // Prevent multiple submissions using both disabled check and processing flag
        if (submitButton.disabled || submitButton.dataset.processing === 'true') {
            return;
        }
        
        // Store original text BEFORE any changes
        const originalText = submitButton.innerHTML;
        
        // Set processing flag and loading state
        submitButton.dataset.processing = 'true';
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Đang xử lý...';
        
        // Collect form data
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        
        // Convert date string to proper format if exists
        if (data.dob) {
            const date = new Date(data.dob);
            data.dob = date.toISOString().split('T')[0];
        }
        
        // Determine endpoint
        let endpoint = '';
        if (form.action.includes('/admin/profile/update')) {
            endpoint = '/admin/profile/update';
        } else if (form.action.includes('/admin/profile/change-password')) {
            endpoint = '/admin/profile/change-password';
        }
        
        // Add CSRF token
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
        
        const headers = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };
        
        if (csrfToken && csrfHeader && csrfToken !== '' && csrfHeader !== '') {
            headers[csrfHeader] = csrfToken;
        }
        
        fetch(endpoint, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data)
        })
        .then(response => {
            // Always try to parse JSON, even for error responses
            return response.json().then(data => ({
                status: response.status,
                ok: response.ok,
                data: data
            }));
        })
        .then(result => {
            if (result.ok && result.data.success) {
                showNotification(result.data.message, 'success');
                
                // Kiểm tra nếu có redirect (đổi mật khẩu)
                if (result.data.redirect) {
                    setTimeout(() => {
                        window.location.href = result.data.redirect;
                    }, 2000);
                } else {
                    // Reload page cho các trường hợp khác
                    setTimeout(() => {
                        window.location.reload();
                    }, 1500);
                }
            } else {
                // Handle both validation errors and other errors
                const message = result.data.message || 'Có lỗi xảy ra khi xử lý yêu cầu';
                showNotification(message, 'danger');
                
                // Hiển thị validation errors cho từng field
                if (result.data.errors) {
                    // Xóa tất cả validation errors cũ
                    form.querySelectorAll('.is-invalid').forEach(field => {
                        field.classList.remove('is-invalid');
                    });
                    form.querySelectorAll('.invalid-feedback').forEach(feedback => {
                        feedback.remove();
                    });
                    
                    // Hiển thị validation errors mới
                    Object.keys(result.data.errors).forEach(fieldName => {
                        const field = form.querySelector(`[name="${fieldName}"]`);
                        if (field) {
                            field.classList.add('is-invalid');
                            
                            // Tạo feedback element
                            const feedback = document.createElement('div');
                            feedback.className = 'invalid-feedback';
                            feedback.textContent = result.data.errors[fieldName];
                            
                            // Thêm feedback sau field
                            field.parentNode.appendChild(feedback);
                        }
                    });
                }
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Có lỗi xảy ra khi xử lý yêu cầu', 'danger');
        })
        .finally(() => {
            // Reset button state and clear processing flag
            submitButton.disabled = false;
            submitButton.dataset.processing = 'false';
            submitButton.innerHTML = originalText;
            
            // Additional safety timeout to ensure button is reset
            setTimeout(() => {
                if (submitButton.disabled || submitButton.dataset.processing === 'true') {
                    submitButton.disabled = false;
                    submitButton.dataset.processing = 'false';
                    submitButton.innerHTML = originalText;
                }
            }, 1000);
        });
    }

    // Real-time validation khi user nhập liệu
    forms.forEach(function(form) {
        const fields = form.querySelectorAll('.form-control, .form-select');
        fields.forEach(function(field) {
            field.addEventListener('blur', function() {
                if (form.classList.contains('was-validated')) {
                    if (this.checkValidity()) {
                        this.classList.remove('is-invalid');
                        this.classList.add('is-valid');
                    } else {
                        this.classList.remove('is-valid');
                        this.classList.add('is-invalid');
                    }
                }
            });
            
            field.addEventListener('input', function() {
                if (form.classList.contains('was-validated')) {
                    if (this.checkValidity()) {
                        this.classList.remove('is-invalid');
                        this.classList.add('is-valid');
                    } else {
                        this.classList.remove('is-valid');
                        this.classList.add('is-invalid');
                    }
                }
            });
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
    // 11. AJAX NAVIGATION - KHÔNG RELOAD SIDEBAR
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
                
                // Update active menu (luôn update)
                updateActiveMenu(url);
                
                // Update header and breadcrumb (luôn update)
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
    
    // Hàm khởi tạo components cho trang mới
    function initializePageComponents() {
        // Re-initialize tooltips
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.forEach(function (tooltipTriggerEl) {
            new bootstrap.Tooltip(tooltipTriggerEl);
        });
        
        // Re-initialize modals
        const modalTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="modal"]'));
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
        // Chỉ load khi thực sự là back/forward, không phải reload
        if (event.state !== null) {
            const url = window.location.pathname;
            loadPageContent(url);
        }
    });
    
    // PERSIST SIDEBAR STATE ACROSS NAVIGATION
    window.addEventListener('beforeunload', function() {
        const sidebar = document.querySelector('.admin-sidebar');
        if (sidebar) {
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed);
        }
    });

    console.log('Admin panel initialized successfully!');
}