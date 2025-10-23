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
            sidebar.classList.toggle('collapsed');
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
                // Chuyển sang mobile: xóa class desktop, reset hamburger
                sidebar.classList.remove('collapsed');
                if (sidebarToggle) {
                    sidebarToggle.style.display = '';
                }
                // Đảm bảo logic mobile hoạt động
                const isOpen = sidebar.classList.contains('show');
                toggleHamburgerVisibility(isOpen);
            } else {
                // Chuyển sang desktop: xóa class mobile, reset hamburger
                sidebar.classList.remove('show');
                if (sidebarToggle) {
                    sidebarToggle.style.display = '';
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
        if (window.innerWidth <= 991) {
            // Mobile: sidebar đóng ban đầu
            sidebar.classList.remove('show');
            if (sidebarToggle) {
                sidebarToggle.style.display = 'block'; // Hiện hamburger header
            }
        } else {
            // Desktop: sidebar mở ban đầu
            sidebar.classList.remove('collapsed');
            // CSS sẽ tự động điều khiển hamburger visibility
        }
    }

    console.log('Admin panel initialized successfully!');
});