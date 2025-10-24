# Manager Dashboard - Mộc Việt

## Tổng quan
Module quản lý tài khoản Manager cho hệ thống bán hàng nội thất Mộc Việt.

## Cấu trúc thư mục

```
src/main/java/mocviet/
├── controller/manager/
│   └── ManagerController.java          # Controller chính cho manager
├── dto/manager/
│   ├── UpdateProfileRequest.java       # DTO cho cập nhật profile
│   └── ChangePasswordRequest.java      # DTO cho đổi mật khẩu
└── service/manager/
    └── ManagerAccountService.java      # Service xử lý logic quản lý tài khoản

src/main/resources/
├── templates/manager/
│   ├── fragments/
│   │   └── manager_layout.html         # Layout chung cho manager
│   ├── dashboard/
│   │   └── manager_index.html          # Dashboard chính
│   └── account/
│       ├── profile.html                # Trang quản lý tài khoản
│       └── change-password.html        # Trang đổi mật khẩu
├── static/css/
│   └── manager.css                     # CSS cho giao diện manager
└── static/js/
    └── manager.js                      # JavaScript cho manager
```

## Chức năng đã implement

### 1. Dashboard Manager (`/manager`)
- Hiển thị thống kê tổng quan
- Đơn hàng gần đây
- Thông báo hệ thống
- Quick actions

### 2. Quản lý tài khoản (`/manager/profile`)
- **Cập nhật thông tin cá nhân:**
  - Họ và tên (2-50 ký tự, chỉ chữ cái)
  - Email (format hợp lệ, không trùng)
  - Số điện thoại (10-11 số, bắt đầu bằng 0)
  - Giới tính (Nam/Nữ/Khác)
  - Ngày sinh (tuổi 18-100)

### 3. Đổi mật khẩu (`/manager/change-password`)
- **Validation mật khẩu:**
  - Mật khẩu hiện tại phải đúng
  - Mật khẩu mới tối thiểu 8 ký tự
  - Phải có chữ hoa, chữ thường và số
  - Xác nhận mật khẩu phải khớp
  - Mật khẩu mới phải khác mật khẩu cũ

## Bảo mật

### 1. Authentication & Authorization
- Chỉ user có role "MANAGER" mới truy cập được
- Sử dụng `@PreAuthorize("hasRole('MANAGER')")`
- Session management với JWT

### 2. Validation
- **Input validation:** Sử dụng Bean Validation
- **Business validation:** Kiểm tra email trùng, tuổi hợp lệ
- **Security validation:** Mật khẩu hiện tại, độ mạnh mật khẩu

### 3. Rate Limiting
- Tối đa 5 lần đổi mật khẩu/giờ (có thể implement thêm)

## API Endpoints

| Method | Endpoint | Mô tả | Quyền |
|--------|----------|-------|-------|
| GET | `/manager` | Dashboard chính | MANAGER |
| GET | `/manager/profile` | Trang quản lý tài khoản | MANAGER |
| POST | `/manager/profile/update` | Cập nhật thông tin | MANAGER |
| GET | `/manager/change-password` | Trang đổi mật khẩu | MANAGER |
| POST | `/manager/change-password` | Đổi mật khẩu | MANAGER |

## Công nghệ sử dụng

- **Backend:** Spring Boot 3.x, Spring Security, JPA/Hibernate
- **Frontend:** Thymeleaf, Bootstrap 5, Font Awesome
- **Database:** SQL Server
- **Authentication:** JWT + Session
- **Validation:** Bean Validation (Jakarta)

## Hướng dẫn sử dụng

### 1. Đăng nhập với tài khoản Manager
```
URL: http://localhost:8080/login
Role: MANAGER
```

### 2. Truy cập Dashboard
```
URL: http://localhost:8080/manager
```

### 3. Quản lý tài khoản
```
URL: http://localhost:8080/manager/profile
```

### 4. Đổi mật khẩu
```
URL: http://localhost:8080/manager/change-password
```

## Validation Rules

### UpdateProfileRequest
- **fullName:** 2-50 ký tự, chỉ chữ cái và khoảng trắng
- **email:** Format email hợp lệ, không trùng với user khác
- **phone:** 10-11 số, bắt đầu bằng 0
- **gender:** Chỉ nhận giá trị "Nam", "Nữ", "Khác"
- **dob:** Phải trước ngày hiện tại, tuổi 18-100

### ChangePasswordRequest
- **currentPassword:** Không được để trống
- **newPassword:** Tối thiểu 8 ký tự, có chữ hoa, chữ thường, số
- **confirmPassword:** Phải khớp với newPassword

## Error Handling

### 1. Validation Errors
- Hiển thị lỗi validation ngay trên form
- Sử dụng Bootstrap validation classes

### 2. Business Logic Errors
- Hiển thị thông báo lỗi bằng alert
- Redirect về trang trước với thông báo

### 3. Security Errors
- 403 Forbidden cho user không có quyền
- 401 Unauthorized cho user chưa đăng nhập

## Logging & Audit

### 1. Activity Logging
- Ghi log mọi thay đổi thông tin cá nhân
- Ghi log đổi mật khẩu
- Sử dụng Spring AOP (có thể implement thêm)

### 2. Security Logging
- Log failed login attempts
- Log password change attempts
- Log profile update attempts

## Performance

### 1. Database Optimization
- Sử dụng indexes cho các trường thường query
- Lazy loading cho các entity liên quan

### 2. Frontend Optimization
- Minify CSS/JS
- CDN cho Bootstrap và Font Awesome
- Image optimization

## Testing

### 1. Unit Tests
- Test ManagerAccountService
- Test validation logic
- Test security constraints

### 2. Integration Tests
- Test API endpoints
- Test authentication flow
- Test database operations

### 3. UI Tests
- Test form validation
- Test user interactions
- Test responsive design

## Deployment

### 1. Environment Variables
```properties
# Database
spring.datasource.url=jdbc:sqlserver://...
spring.datasource.username=...
spring.datasource.password=...

# JWT
jwt.secret=...
jwt.expiration=86400000

# Session
spring.session.timeout=30m
```

### 2. Security Configuration
- HTTPS trong production
- CORS configuration
- CSRF protection
- Rate limiting

## Troubleshooting

### 1. Common Issues
- **403 Forbidden:** Kiểm tra role của user
- **Validation errors:** Kiểm tra input format
- **Database errors:** Kiểm tra connection và constraints

### 2. Debug Mode
```properties
# Enable debug logging
logging.level.mocviet=DEBUG
spring.jpa.show-sql=true
```

## Future Enhancements

### 1. Features
- Two-factor authentication
- Password history
- Account lockout
- Email notifications

### 2. UI/UX
- Dark mode
- Mobile optimization
- Accessibility improvements
- Real-time notifications

### 3. Security
- OAuth2 integration
- SSO support
- Advanced audit logging
- Security headers
