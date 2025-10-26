# Manager Dashboard - Mộc Việt

## Tổng quan
Module quản lý tài khoản Manager, phân công đội giao hàng và quản lý đơn hàng cho hệ thống bán hàng nội thất Mộc Việt.

## Cấu trúc thư mục

```
src/main/java/mocviet/
├── controller/manager/
│   ├── ManagerController.java          # Controller chính cho manager
│   ├── DeliveryAssignmentController.java    # Controller phân công đội giao hàng
│   └── OrderManagementController.java       # Controller quản lý đơn hàng
├── dto/manager/
│   ├── UpdateProfileRequest.java       # DTO cho cập nhật profile
│   ├── ChangePasswordRequest.java      # DTO cho đổi mật khẩu
│   ├── AssignDeliveryTeamRequest.java       # DTO phân công đội giao hàng
│   ├── ChangeDeliveryTeamRequest.java       # DTO thay đổi đội giao hàng
│   ├── DeliveryTeamDTO.java                 # DTO thông tin đội giao hàng
│   ├── OrderDeliveryDTO.java                # DTO thông tin giao hàng
│   ├── PendingOrderDTO.java                 # DTO đơn hàng cần phân công
│   ├── ZoneDTO.java                         # DTO thông tin khu vực
│   ├── OrderManagementDTO.java              # DTO chi tiết đơn hàng
│   ├── OrderListDTO.java                    # DTO danh sách đơn hàng
│   ├── ReturnRequestDTO.java                # DTO yêu cầu trả hàng
│   └── OrderActionRequest.java              # DTO request actions
├── service/manager/
│   ├── ManagerAccountService.java      # Service xử lý logic quản lý tài khoản
│   ├── DeliveryAssignmentService.java       # Service xử lý logic phân công
│   ├── OrderManagementService.java          # Service quản lý đơn hàng
│   └── OrderStoredProcedureService.java     # Service tích hợp stored procedures
└── repository/
    ├── OrdersRepository.java                # Repository đơn hàng
    ├── OrderDeliveryRepository.java         # Repository giao hàng
    ├── DeliveryTeamRepository.java          # Repository đội giao hàng
    ├── DeliveryTeamZoneRepository.java      # Repository đội-khu vực
    ├── ProvinceZoneRepository.java          # Repository tỉnh-khu vực
    ├── ShippingZoneRepository.java          # Repository khu vực
    ├── DeliveryHistoryRepository.java       # Repository lịch sử giao hàng
    └── OrderStatusHistoryRepository.java    # Repository lịch sử trạng thái

src/main/resources/
├── templates/manager/
│   ├── fragments/
│   │   ├── manager_layout.html         # Layout chung cho manager
│   │   ├── manager_sidebar.html        # Sidebar cho manager
│   │   └── manager_navbar.html         # Navbar cho manager
│   ├── dashboard/
│   │   └── manager_index.html          # Dashboard chính
│   ├── account/
│   │   ├── profile.html                # Trang quản lý tài khoản
│   │   └── change-password.html        # Trang đổi mật khẩu
│   ├── delivery/
│   │   ├── pending_orders.html        # Danh sách đơn cần phân công
│   │   ├── assign_team.html           # Phân công đội giao hàng
│   │   ├── change_team.html           # Thay đổi đội giao hàng
│   │   ├── teams.html                 # Quản lý đội giao hàng
│   │   └── zones.html                 # Quản lý khu vực giao hàng
│   └── orders/
│       ├── pending.html               # Danh sách đơn chờ xác nhận
│       ├── pending-detail.html        # Chi tiết đơn chờ xác nhận
│       ├── completed.html             # Danh sách đơn hoàn thành
│       ├── completed-detail.html      # Chi tiết đơn hoàn thành
│       ├── returns.html               # Danh sách yêu cầu trả hàng
│       └── return-detail.html         # Chi tiết yêu cầu trả hàng
├── static/css/
│   └── manager.css                     # CSS cho giao diện manager (đã cập nhật)
└── static/js/
    ├── manager.js                      # JavaScript cho manager
    └── delivery-assignment.js         # JavaScript cho phân công đội giao hàng
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

### 4. Phân công đội giao hàng (`/manager/delivery/*`)
- **Xem danh sách đơn hàng cần phân công** (`/manager/delivery/pending`)
  - Hiển thị đơn hàng CONFIRMED chưa được phân công
  - Lọc theo khu vực, tìm kiếm, sắp xếp
  - Phân trang và auto-refresh

- **Phân công đội giao hàng** (`/manager/delivery/assign/{orderId}`)
  - Tự động gợi ý đội giao phù hợp theo khu vực
  - Load balancing (ưu tiên đội ít đơn đang xử lý)
  - Validation đầy đủ theo đặc tả
  - Ghi chú đặc biệt và thông tin liên hệ

- **Thay đổi đội giao hàng** (`/manager/delivery/change/{orderId}`)
  - Chỉ cho phép khi chưa bắt đầu giao hàng
  - Lý do thay đổi bắt buộc
  - Thông báo cho đội giao cũ và mới

- **Quản lý đội giao hàng** (`/manager/delivery/teams`)
  - Xem thông tin tất cả đội giao hàng
  - Số lượng đơn đang xử lý
  - Khu vực phụ trách

- **Quản lý khu vực giao hàng** (`/manager/delivery/zones`)
  - Thông tin các khu vực giao hàng
  - Phí vận chuyển và tỉnh/thành phụ trách

### 5. Quản lý đơn hàng (`/manager/orders/*`)
- **Xác nhận đơn hàng** (UC-MGR-ORD-ConfirmOrder)
  - Xác nhận đơn hàng PENDING đã thanh toán
  - Sử dụng stored procedure `sp_ConfirmOrder`
  - Thông báo cho khách hàng
  - Ghi log hoạt động

- **Hủy đơn hàng** (UC-MGR-ORD-CancelOrder)
  - Hủy đơn hàng PENDING
  - Sử dụng stored procedure `sp_CancelOrder`
  - Hoàn tiền tự động cho đơn online
  - Hoàn lại tồn kho
  - Thông báo cho khách hàng

- **Xem đơn hàng hoàn thành** (UC-MGR-ORD-ViewCompleted)
  - Danh sách đơn DELIVERED
  - Filters theo thời gian, keyword
  - Export Excel
  - Chi tiết đơn hàng đầy đủ

- **Duyệt yêu cầu trả hàng** (UC-MGR-ORD-ApproveReturn)
  - Duyệt yêu cầu REQUESTED trong thời hạn 30 ngày
  - Sử dụng stored procedure `sp_ApproveReturn`
  - Phân công đội thu hồi
  - Thông báo cho khách hàng và đội giao

- **Từ chối yêu cầu trả hàng** (UC-MGR-ORD-RejectReturn)
  - Từ chối yêu cầu REQUESTED
  - Sử dụng stored procedure `sp_RejectReturn`
  - Thông báo lý do từ chối cho khách hàng

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
| GET | `/manager/delivery/pending` | Danh sách đơn cần phân công | MANAGER |
| GET | `/manager/delivery/assign/{orderId}` | Form phân công đội giao | MANAGER |
| POST | `/manager/delivery/assign` | Xử lý phân công đội giao | MANAGER |
| GET | `/manager/delivery/change/{orderId}` | Form thay đổi đội giao | MANAGER |
| POST | `/manager/delivery/change` | Xử lý thay đổi đội giao | MANAGER |
| GET | `/manager/delivery/teams` | Quản lý đội giao hàng | MANAGER |
| GET | `/manager/delivery/zones` | Quản lý khu vực giao hàng | MANAGER |
| GET | `/manager/delivery/api/teams/{orderId}` | Lấy danh sách đội giao phù hợp | MANAGER |
| GET | `/manager/delivery/api/zone/{orderId}` | Lấy khu vực của đơn hàng | MANAGER |
| GET | `/manager/orders/pending` | Danh sách đơn chờ xác nhận | MANAGER |
| GET | `/manager/orders/pending/{id}` | Chi tiết đơn chờ xác nhận | MANAGER |
| POST | `/manager/orders/pending/{id}/confirm` | Xác nhận đơn hàng | MANAGER |
| POST | `/manager/orders/pending/{id}/cancel` | Hủy đơn hàng | MANAGER |
| POST | `/manager/orders/pending/bulk-confirm` | Xác nhận hàng loạt | MANAGER |
| POST | `/manager/orders/pending/bulk-cancel` | Hủy hàng loạt | MANAGER |
| GET | `/manager/orders/completed` | Danh sách đơn hoàn thành | MANAGER |
| GET | `/manager/orders/completed/{id}` | Chi tiết đơn hoàn thành | MANAGER |
| GET | `/manager/orders/returns` | Danh sách yêu cầu trả hàng | MANAGER |
| GET | `/manager/orders/returns/{id}` | Chi tiết yêu cầu trả hàng | MANAGER |
| POST | `/manager/orders/returns/{id}/approve` | Duyệt yêu cầu trả hàng | MANAGER |
| POST | `/manager/orders/returns/{id}/reject` | Từ chối yêu cầu trả hàng | MANAGER |
| GET | `/manager/orders/api/{id}` | Lấy chi tiết đơn hàng (JSON) | MANAGER |
| GET | `/manager/orders/api/returns/{id}` | Lấy chi tiết yêu cầu trả hàng (JSON) | MANAGER |

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

### 5. Phân công đội giao hàng
```
URL: http://localhost:8080/manager/delivery/pending
```

### 6. Quản lý đơn hàng
```
URL: http://localhost:8080/manager/orders/pending
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

### Phân công đội giao hàng
- **Đơn hàng phải ở trạng thái CONFIRMED**
- **Đơn hàng chưa được phân công**
- **Đội giao hàng phải hoạt động**
- **Đội giao hàng phải phụ trách khu vực của địa chỉ giao hàng**
- **Địa chỉ giao hàng phải được map vào khu vực**

### Thay đổi đội giao hàng
- **Đơn hàng đã được phân công**
- **Đơn hàng chưa bắt đầu giao hàng (status = RECEIVED)**
- **Đội giao hàng mới phải hoạt động**
- **Đội giao hàng mới phải phụ trách khu vực**
- **Lý do thay đổi là bắt buộc**

### Quản lý đơn hàng
- **Trạng thái đơn hàng:** Chỉ xác nhận đơn PENDING, chỉ hủy đơn PENDING
- **Thanh toán:** Đơn online phải đã thanh toán mới xác nhận được
- **Trả hàng:** Chỉ duyệt/từ chối yêu cầu REQUESTED
- **Thời hạn trả hàng:** Trong vòng 30 ngày sau giao hàng
- **Đội giao:** Phải có đội giao phù hợp để thu hồi
- **Lý do:** Bắt buộc nhập lý do khi hủy đơn hoặc từ chối trả hàng

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

## Database Integration

### Entities sử dụng
- `Orders` - Đơn hàng
- `OrderDelivery` - Phân công giao hàng
- `DeliveryTeam` - Đội giao hàng
- `DeliveryTeamZone` - Đội giao hàng - Khu vực
- `ProvinceZone` - Tỉnh - Khu vực
- `ShippingZone` - Khu vực giao hàng
- `DeliveryHistory` - Lịch sử giao hàng
- `OrderStatusHistory` - Lịch sử trạng thái đơn hàng

### Stored Procedures sử dụng
- `sp_MarkDispatched` - Đánh dấu đơn đã xuất kho
- `sp_ConfirmOrder` - Xác nhận đơn hàng
- `sp_CancelOrder` - Hủy đơn hàng và hoàn kho
- `sp_ApproveReturn` - Duyệt yêu cầu trả hàng
- `sp_RejectReturn` - Từ chối yêu cầu trả hàng
- `sp_RequestReturn` - Khách hàng yêu cầu trả hàng
- `sp_ReturnOrder` - Xử lý trả hàng và hoàn tiền
- `sp_MarkDelivered` - Đánh dấu đơn đã giao thành công
- `sp_HandlePaymentWebhook` - Xử lý webhook thanh toán
- `sp_AutoCancelUnpaidOnline` - Tự động hủy đơn online chưa thanh toán
- Triggers tự động gửi thông báo cho đội giao hàng

## Tính năng đặc biệt

### Auto Zone Detection
- Tự động xác định khu vực từ địa chỉ giao hàng
- Validation khu vực có đội giao hàng

### Load Balancing
- Ưu tiên đội giao hàng ít đơn đang xử lý nhất
- Hiển thị số lượng đơn đang xử lý

### Smart Suggestions
- Gợi ý đội giao hàng phù hợp nhất
- Sắp xếp theo workload

### Real-time Notifications
- Thông báo ngay cho đội giao hàng khi được phân công
- Thông báo khi thay đổi đội giao hàng

### Change Tracking
- Theo dõi lịch sử thay đổi đội giao hàng
- Ghi log đầy đủ mọi thao tác

### Bulk Operations
- Xác nhận nhiều đơn hàng cùng lúc
- Hủy nhiều đơn hàng cùng lúc
- Giao diện checkbox để chọn đơn hàng

### Advanced Filtering
- Tìm kiếm theo mã đơn hàng, tên khách hàng
- Lọc theo khoảng thời gian
- Sắp xếp theo nhiều tiêu chí
- Phân trang với nhiều tùy chọn

### Smart Notifications
- Cảnh báo đơn hàng sắp hết hạn trả hàng
- Thông báo đơn hàng quá thời hạn trả hàng
- Hiển thị trạng thái thanh toán rõ ràng

## Logging & Audit

### 1. Activity Logging
- Ghi log mọi thay đổi thông tin cá nhân
- Ghi log đổi mật khẩu
- Ghi log phân công đội giao hàng
- Ghi log xác nhận/hủy đơn hàng
- Ghi log duyệt/từ chối trả hàng
- Sử dụng Spring AOP (có thể implement thêm)

### 2. Security Logging
- Log failed login attempts
- Log password change attempts
- Log profile update attempts
- Log delivery assignment changes
- Log order management actions
- Log return request processing

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
- **"Chỉ xác nhận được đơn hàng ở trạng thái PENDING"** - Đơn hàng đã được xử lý
- **"Đơn hàng online chưa thanh toán"** - Cần kiểm tra trạng thái thanh toán
- **"Quá thời hạn trả hàng (30 ngày)"** - Yêu cầu trả hàng đã hết hạn
- **"Không có đội giao phù hợp"** - Cần tạo đội giao hàng trước

### 2. Debug Mode
```properties
# Enable debug logging
logging.level.mocviet=DEBUG
spring.jpa.show-sql=true
```

### 3. Giải pháp
- Kiểm tra trạng thái đơn hàng hiện tại
- Xác nhận thông tin thanh toán
- Kiểm tra thời gian giao hàng
- Tạo/cấu hình đội giao hàng

## Future Enhancements

### 1. Features
- Two-factor authentication
- Password history
- Account lockout
- Email notifications
- Bulk assignment (phân công hàng loạt)
- Auto-assignment mode
- Advanced filtering
- Team performance analytics
- Delivery route optimization
- Mobile app integration

### 2. UI/UX
- Dark mode
- Mobile optimization
- Accessibility improvements
- Real-time notifications
- Advanced filtering
- Export functionality
- Real-time updates với WebSocket

### 3. Security
- OAuth2 integration
- SSO support
- Advanced audit logging
- Security headers
- API versioning
- GraphQL support

### 4. API Improvements
- RESTful API đầy đủ
- WebSocket cho real-time updates
- GraphQL support
- API versioning
