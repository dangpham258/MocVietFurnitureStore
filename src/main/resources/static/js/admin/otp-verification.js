// ========================================
// OTP VERIFICATION JAVASCRIPT - MỘC VIỆT FURNITURE STORE
// ========================================
// File này chứa logic OTP verification cho admin profile

console.log('OTP Verification JS loaded successfully!');

// ========================================
// OTP VERIFICATION SYSTEM
// ========================================

class OTPVerification {
    constructor() {
        this.otpModal = null;
        this.currentForm = null;
        this.currentFormData = null;
        this.originalFormData = null; // Lưu trữ dữ liệu ban đầu để so sánh
        this.otpEndpoint = '';
        this.submitEndpoint = '';
        this.isProcessing = false;
        
        this.init();
    }
    
    init() {
        this.createOTPModal();
        this.bindEvents();
    }
    
    /**
     * Helper method để hiển thị notification với fallback
     */
    showNotification(message, type = 'info') {
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
        } else if (typeof showNotification === 'function') {
            showNotification(message, type);
        } else if (typeof window.showNotification === 'function') {
            window.showNotification(message, type);
        } else {
            console.log(`Notification [${type.toUpperCase()}]: ${message}`);
        }
    }
    
    /**
     * Tạo OTP Modal
     */
    createOTPModal() {
        const modalHTML = `
            <div class="modal fade" id="otpVerificationModal" tabindex="-1" aria-labelledby="otpVerificationModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header bg-primary text-white">
                            <h5 class="modal-title" id="otpVerificationModalLabel">
                                <i class="bi bi-shield-check me-2"></i>
                                Xác thực OTP
                            </h5>
                        </div>
                        <div class="modal-body">
                            <div class="text-center mb-4">
                                <div class="otp-icon mb-3">
                                    <i class="bi bi-envelope-check text-primary" style="font-size: 3rem;"></i>
                                </div>
                                <h6 class="mb-2">Mã OTP đã được gửi đến email của bạn</h6>
                                <p class="text-muted small mb-0">Vui lòng kiểm tra email và nhập mã OTP để tiếp tục</p>
                            </div>
                            
                            <form id="otpForm" novalidate>
                                <div class="mb-3">
                                    <label for="otpCode" class="form-label">Mã OTP *</label>
                                    <input type="text" 
                                           class="form-control text-center otp-input" 
                                           id="otpCode" 
                                           name="otpCode" 
                                           maxlength="6" 
                                           pattern="[0-9]{6}" 
                                           placeholder="000000"
                                           required>
                                    <div class="invalid-feedback">Vui lòng nhập mã OTP 6 chữ số</div>
                                    <div class="form-text text-center">
                                        <small class="text-muted">Mã OTP có hiệu lực trong <span id="otpTimer" class="text-danger fw-bold">5:00</span></small>
                                    </div>
                                </div>
                                
                                <div class="d-flex justify-content-between align-items-center">
                                    <button type="button" class="btn btn-outline-secondary btn-sm" id="resendOtpBtn">
                                        <i class="bi bi-arrow-clockwise me-1"></i>
                                        Gửi lại OTP
                                    </button>
                                    <button type="button" class="btn btn-outline-danger btn-sm" id="cancelOtpBtn">
                                        <i class="bi bi-x-circle me-1"></i>
                                        Hủy
                                    </button>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" id="verifyOtpBtn">
                                <i class="bi bi-check-lg me-1"></i>
                                Xác thực OTP
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Thêm modal vào body
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        this.otpModal = new bootstrap.Modal(document.getElementById('otpVerificationModal'));
    }
    
    /**
     * Bind events
     */
    bindEvents() {
        // Gửi form profile - Force override
        const profileForm = document.querySelector('form[action*="/admin/profile/update"]');
        if (profileForm) {
            // Xóa tất cả các event listeners hiện có bằng cách clone
            const newProfileForm = profileForm.cloneNode(true);
            profileForm.parentNode.replaceChild(newProfileForm, profileForm);
            
            // Thêm event listener với capture=true để đảm bảo nó chạy đầu tiên
            newProfileForm.addEventListener('submit', (e) => this.handleProfileSubmit(e), true);
            console.log('Profile form event listener bound (OTP override)');
        }
        
        // Gửi form password - Force override
        const passwordForm = document.querySelector('form[action*="/admin/profile/change-password"]');
        if (passwordForm) {
            // Xóa tất cả các event listeners hiện có bằng cách clone
            const newPasswordForm = passwordForm.cloneNode(true);
            passwordForm.parentNode.replaceChild(newPasswordForm, passwordForm);
            
            // Thêm event listener với capture=true để đảm bảo nó chạy đầu tiên
            newPasswordForm.addEventListener('submit', (e) => this.handlePasswordSubmit(e), true);
            console.log('Password form event listener bound (OTP override)');
        }
        
        // Event form OTP sẽ được bind khi modal được tạo
        this.bindOTPEvents();
    }
    
    /**
     * Bind event form OTP modal
     */
    bindOTPEvents() {
        // Chờ modal được tạo
        setTimeout(() => {
            const verifyBtn = document.getElementById('verifyOtpBtn');
            const resendBtn = document.getElementById('resendOtpBtn');
            const cancelBtn = document.getElementById('cancelOtpBtn');
            const otpInput = document.getElementById('otpCode');
            
            if (verifyBtn) verifyBtn.addEventListener('click', () => this.verifyOTP());
            if (resendBtn) resendBtn.addEventListener('click', () => this.resendOTP());
            if (cancelBtn) cancelBtn.addEventListener('click', () => this.cancelOTP());
            if (otpInput) {
                otpInput.addEventListener('input', (e) => this.formatOTPInput(e));
                otpInput.addEventListener('keypress', (e) => this.handleOTPKeypress(e));
            }
        }, 100);
    }
    
    /**
     * Xử lý gửi form profile
     */
    async handleProfileSubmit(event) {
        console.log('OTP: Profile form submit intercepted!');
        event.preventDefault();
        event.stopPropagation();
        
        if (this.isProcessing) {
            console.log('OTP: Already processing, ignoring');
            return;
        }
        
        const form = event.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        
        console.log('OTP: Form data:', data);
        
        // Chuyển đổi chuỗi ngày thành định dạng chuẩn nếu có
        if (data.dob) {
            const date = new Date(data.dob);
            data.dob = date.toISOString().split('T')[0];
        }
        
        // Loại bỏ trường OTP khi gửi request send-otp
        delete data.otpCode;
        
        // Kiểm tra xem có thay đổi gì không (so sánh dữ liệu ban đầu và dữ liệu mới)
        if (this.originalFormData) {
            const hasChanges = this.hasChanges(data, this.originalFormData);
            
            if (!hasChanges) {
                console.log('OTP: No changes detected');
                this.showNotification('Thông tin chưa được chỉnh sửa', 'warning');
                return;
            }
        }
        
        // Hiển thị thông báo đang xử lý
        this.showNotification('Đang xử lý thông tin...', 'info');
        
        // Bỏ qua validation HTML5, để backend DTO handle
        console.log('OTP: Form data collected, sending to backend for validation');
        
        // Lưu dữ liệu form và hiển thị modal OTP
        this.currentForm = form;
        this.currentFormData = data;
        this.otpEndpoint = '/admin/profile/send-otp';
        this.submitEndpoint = '/admin/profile/update';
        
        await this.showOTPModal('profile');
    }
    
    /**
     * So sánh dữ liệu mới và dữ liệu ban đầu để kiểm tra có thay đổi không
     */
    hasChanges(newData, originalData) {
        // So sánh tất cả các trường trừ username (không thể thay đổi)
        const fieldsToCompare = ['email', 'fullName', 'phone', 'gender', 'dob'];
        
        for (const field of fieldsToCompare) {
            const newValue = newData[field] || '';
            const oldValue = originalData[field] || '';
            
            // So sánh giá trị đã được normalize (trim whitespace)
            if (newValue.trim() !== oldValue.trim()) {
                console.log(`Change detected in ${field}: "${oldValue}" → "${newValue}"`);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Xử lý gửi form password
     */
    async handlePasswordSubmit(event) {
        event.preventDefault();
        
        if (this.isProcessing) return;
        
        // Hiển thị thông báo đang xử lý
        this.showNotification('Đang xử lý mật khẩu...', 'info');
        
        const form = event.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        
        console.log('OTP: Password form data:', data);
        
        // Loại bỏ trường OTP khi gửi request send-password-otp
        delete data.otpCode;
        
        // Bỏ qua validation HTML5, để backend DTO handle
        console.log('OTP: Password form data collected, sending to backend for validation');
        
        // Validate password confirmation (chỉ kiểm tra cơ bản)
        if (data.newPassword !== data.confirmPassword) {
            console.log('OTP: Password confirmation mismatch');
            
            // Highlight trường password xác nhận
            const confirmPasswordField = form.querySelector('input[name="confirmPassword"]');
            if (confirmPasswordField) {
                confirmPasswordField.classList.add('is-invalid');
                confirmPasswordField.setCustomValidity('Mật khẩu xác nhận không khớp');
            }
            
            this.showNotification('Mật khẩu xác nhận không khớp', 'danger');
            return;
        }
        
        console.log('OTP: Password form data collected, sending to backend for validation');
        
        // Lưu dữ liệu form và hiển thị modal OTP
        this.currentForm = form;
        this.currentFormData = data;
        this.otpEndpoint = '/admin/profile/send-password-otp';
        this.submitEndpoint = '/admin/profile/change-password';
        
        await this.showOTPModal('password');
    }
    
    /**
     * Hiển thị modal OTP và gửi OTP
     */
    async showOTPModal(type) {
        try {
            this.isProcessing = true;
            
            // Reset form trước (làm mới form)
            this.resetOTPForm();
            
            // Lấy CSRF token
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            };
            
            if (csrfToken && csrfHeader && csrfToken !== '' && csrfHeader !== '') {
                headers[csrfHeader] = csrfToken;
            }
            
            // Gửi OTP với dữ liệu form để backend validate
            const response = await fetch(this.otpEndpoint, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(this.currentFormData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                // Chỉ hiển thị modal khi validation thành công (khi mã OTP hợp lệ)
                this.otpModal.show();
                
                this.showNotification(result.message, 'success');
                this.startOTPTimer();
            } else {
                // Validation failed - không hiển thị modal
                this.showNotification(result.message, 'danger');
                // Không cần hide modal vì chưa hiển thị
            }
            
        } catch (error) {
            console.error('Error sending OTP:', error);
            this.showNotification('Không thể gửi mã OTP', 'danger');
            this.otpModal.hide();
        } finally {
            this.isProcessing = false;
        }
    }
    
    /**
     * Xác thực OTP và gửi form
     */
    async verifyOTP() {
        const otpCode = document.getElementById('otpCode').value.trim();
        
        if (!otpCode || otpCode.length !== 6) {
            document.getElementById('otpCode').classList.add('is-invalid');
            return;
        }
        
        try {
            this.isProcessing = true;
            
            // Thêm OTP vào dữ liệu form
            this.currentFormData.otpCode = otpCode;
            
            // Lấy CSRF token
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            };
            
            if (csrfToken && csrfHeader && csrfToken !== '' && csrfHeader !== '') {
                headers[csrfHeader] = csrfToken;
            }
            
            // Thêm OTP vào form data trước khi gửi
            const submitData = { ...this.currentFormData };
            submitData.otpCode = otpCode;
            
            // Gửi form với OTP
            const response = await fetch(this.submitEndpoint, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(submitData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                // Hiển thị thông báo với fallback
                this.showNotification(result.message, 'success');
                
                this.otpModal.hide();
                
                // Xử lý chuyển hướng cho đổi mật khẩu
                if (result.redirect) {
                    setTimeout(() => {
                        window.location.href = result.redirect;
                    }, 2000);
                } else {
                    // Reload page cho cập nhật profile - delay để user thấy thông báo
                    setTimeout(() => {
                        window.location.reload();
                    }, 3000); // 3000ms (3 giây)
                }
            } else {
                // Xử lý lỗi validation từ DTO
                this.showNotification(result.message, 'danger');
                
                // Highlight trường có lỗi nếu có
                if (result.errors) {
                    Object.keys(result.errors).forEach(fieldName => {
                        const field = this.currentForm.querySelector(`[name="${fieldName}"]`);
                        if (field) {
                            field.classList.add('is-invalid');
                            field.setCustomValidity(result.errors[fieldName]);
                        }
                    });
                }
                
                // Highlight trường OTP và hủy timer
                document.getElementById('otpCode').classList.add('is-invalid');
                if (this.otpTimer) {
                    clearInterval(this.otpTimer);
                    this.otpTimer = null;
                }
                
                // Nếu OTP sai hoặc hết hạn, đóng modal và reset form (làm mới form)
                if (result.message.includes('không hợp lệ') || result.message.includes('hết hạn')) {
                    this.otpModal.hide();
                    this.resetOTPForm();
                    this.currentForm = null;
                    this.currentFormData = null;
                    
                    // Thông báo cho user cần gửi OTP mới
                    setTimeout(() => {
                        this.showNotification('Mã OTP đã bị hủy. Vui lòng thử lại để nhận mã OTP mới.', 'warning');
                    }, 500);
                }
            }
            
        } catch (error) {
            console.error('Error verifying OTP:', error);
            this.showNotification('Có lỗi xảy ra khi xác thực OTP', 'danger');
        } finally {
            this.isProcessing = false;
        }
    }
    
    /**
     * Gửi lại OTP
     */
    async resendOTP() {
        if (this.isProcessing) return;
        
        try {
            this.isProcessing = true;
            
            // Lấy CSRF token
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            };
            
            if (csrfToken && csrfHeader && csrfToken !== '' && csrfHeader !== '') {
                headers[csrfHeader] = csrfToken;
            }
            
            const response = await fetch(this.otpEndpoint, {
                method: 'POST',
                headers: headers
            });
            
            const result = await response.json();
            
            if (result.success) {
                this.showNotification(result.message, 'success');
                this.resetOTPForm();
                this.startOTPTimer();
            } else {
                this.showNotification(result.message, 'danger');
            }
            
        } catch (error) {
            console.error('Error resending OTP:', error);
            this.showNotification('Không thể gửi lại mã OTP', 'danger');
        } finally {
            this.isProcessing = false;
        }
    }
    
    /**
     * Hủy xác thực OTP
     */
    async cancelOTP() {
        console.log('OTP verification cancelled by user');
        
        // Xóa timer nếu có
        if (this.otpTimer) {
            clearInterval(this.otpTimer);
            this.otpTimer = null;
        }
        
        // Gọi API để xóa OTP cũ trong database (xóa OTP cũ trong database)
        await this.cancelOTPInBackend();
        
        this.otpModal.hide();
        this.resetOTPForm();
        
        // Xóa dữ liệu form để force user phải nhập lại
        this.currentForm = null;
        this.currentFormData = null;
        
        // Hiển thị thông báo cho user biết OTP đã bị hủy
        this.showNotification('OTP đã được hủy. Khi bạn thử lại, mã OTP mới sẽ được gửi.', 'info');
    }
    
    /**
     * Gọi API để hủy OTP trong backend
     */
    async cancelOTPInBackend() {
        try {
            // Get CSRF token
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            };
            
            if (csrfToken && csrfHeader && csrfToken !== '' && csrfHeader !== '') {
                headers[csrfHeader] = csrfToken;
            }
            
            // Xác định endpoint dựa trên loại form
            let cancelEndpoint = '/admin/profile/cancel-otp';
            if (this.currentForm && this.currentForm.action.includes('/admin/profile/change-password')) {
                cancelEndpoint = '/admin/profile/cancel-password-otp';
            }
            
            // Gọi API để hủy OTP
            const response = await fetch(cancelEndpoint, {
                method: 'POST',
                headers: headers
            });
            
            if (response.ok) {
                console.log('OTP cancelled in backend');
            } else {
                console.warn('Failed to cancel OTP in backend');
            }
            
        } catch (error) {
            console.error('Error cancelling OTP in backend:', error);
        }
    }
    
    /**
     * Reset OTP form
     */
    resetOTPForm() {
        document.getElementById('otpCode').value = '';
        document.getElementById('otpCode').classList.remove('is-invalid', 'is-valid');
        document.getElementById('otpVerificationModalLabel').innerHTML = `
            <i class="bi bi-shield-check me-2"></i>
            Xác thực OTP
        `;
    }
    
    /**
     * Format OTP input (chỉ cho phép nhập số)
     */
    formatOTPInput(event) {
        const input = event.target;
        input.value = input.value.replace(/[^0-9]/g, '');
        
        if (input.value.length === 6) {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
        } else {
            input.classList.remove('is-valid');
        }
    }
    
    /**
     * Xử lý nhập keypress OTP input
     */
    handleOTPKeypress(event) {
        // Only allow numbers
        if (!/[0-9]/.test(event.key) && !['Backspace', 'Delete', 'Tab', 'Enter'].includes(event.key)) {
            event.preventDefault();
        }
        
        // Tự động gửi khi nhập 6 chữ số
        if (event.key === 'Enter' && event.target.value.length === 6) {
            this.verifyOTP();
        }
    }
    
    /**
     * Bắt đầu timer OTP countdown
     */
    startOTPTimer() {
        let timeLeft = 300; // 300 giây (5 phút)
        const timerElement = document.getElementById('otpTimer');
        const resendBtn = document.getElementById('resendOtpBtn');
        
        resendBtn.disabled = true;
        
        const timer = setInterval(() => {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            
            timerElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
            
            if (timeLeft <= 0) {
                clearInterval(timer);
                timerElement.textContent = 'Hết hạn';
                resendBtn.disabled = false;
                resendBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-1"></i>Gửi lại OTP';
            }
            
            timeLeft--;
        }, 1000);
        
        // Lưu tham chiếu timer để dọn dẹp
        this.otpTimer = timer;
    }
}

// ========================================
// INITIALIZE OTP VERIFICATION
// ========================================

// Hàm global để force override gửi form
window.forceOTPOverride = function() {
    console.log('Force OTP Override called');
    
    // Gửi form profile - Force override
    const profileForm = document.querySelector('form[action*="/admin/profile/update"]');
    if (profileForm) {
        // Xóa tất cả các event listeners hiện có bằng cách clone
        const newProfileForm = profileForm.cloneNode(true);
        profileForm.parentNode.replaceChild(newProfileForm, profileForm);
        
        // Thêm event listener với capture=true để đảm bảo nó chạy đầu tiên
        newProfileForm.addEventListener('submit', (e) => {
            console.log('FORCE OVERRIDE: Profile form submit intercepted!');
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();
            
            // Gọi OTP handler nếu có
            if (window.otpVerificationInstance) {
                window.otpVerificationInstance.handleProfileSubmit(e);
            }
        }, true);
        
        console.log('FORCE OVERRIDE: Profile form event listener bound');
    }
    
    // Gửi form password - Force override
    const passwordForm = document.querySelector('form[action*="/admin/profile/change-password"]');
    if (passwordForm) {
        // Xóa tất cả các event listeners hiện có bằng cách clone
        const newPasswordForm = passwordForm.cloneNode(true);
        passwordForm.parentNode.replaceChild(newPasswordForm, passwordForm);
        
        // Thêm event listener với capture=true để đảm bảo nó chạy đầu tiên
        newPasswordForm.addEventListener('submit', (e) => {
            console.log('FORCE OVERRIDE: Password form submit intercepted!');
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();
            
            // Gọi OTP handler nếu có
            if (window.otpVerificationInstance) {
                window.otpVerificationInstance.handlePasswordSubmit(e);
            }
        }, true);
        
        console.log('FORCE OVERRIDE: Password form event listener bound');
    }
};

// Khởi tạo khi DOM đã tải xong
document.addEventListener('DOMContentLoaded', function() {
    // Chỉ khởi tạo trên trang profile
    if (window.location.pathname.includes('/admin/profile')) {
        // Chờ một chút để đảm bảo admin.js đã tải xong đầu tiên
        setTimeout(() => {
            window.otpVerificationInstance = new OTPVerification();
            console.log('OTP Verification system initialized');
            
            // Lưu trữ dữ liệu ban đầu của form profile
            const profileForm = document.querySelector('form[action*="/admin/profile/update"]');
            if (profileForm) {
                const formData = new FormData(profileForm);
                const data = Object.fromEntries(formData.entries());
                
                // Chuyển đổi chuỗi ngày thành định dạng chuẩn nếu có
                if (data.dob) {
                    const date = new Date(data.dob);
                    data.dob = date.toISOString().split('T')[0];
                }
                
                // Lưu dữ liệu ban đầu (dữ liệu ban đầu của form profile)
                window.otpVerificationInstance.originalFormData = data;
                console.log('Original form data saved:', data);
            }
            
            // Force override sau khi khởi tạo
            setTimeout(() => {
                window.forceOTPOverride();
            }, 100);
        }, 200);
    }
});

// Export để sử dụng trong admin.js
window.OTPVerification = OTPVerification;
