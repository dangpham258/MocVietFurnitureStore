# Manager Dashboard - Mộc Việt

## Tổng quan
Module quản lý dành cho Manager, bao gồm:
- Quản lý tài khoản cá nhân
- Phân công đội giao hàng
- Quản lý đơn hàng (xác nhận, hủy, trả hàng)
- **Quản lý tồn kho** (cảnh báo, cập nhật, báo cáo)
- **Quản lý tin tức** (tạo, sửa, ẩn/hiện bài viết)

## Cấu trúc thư mục

```
src/main/java/mocviet/
├── controller/manager/
│   ├── ManagerController.java               # Controller chính cho manager
│   ├── DeliveryAssignmentController.java    # Controller phân công đội giao hàng
│   ├── OrderManagementController.java       # Controller quản lý đơn hàng
│   ├── InventoryManagementController.java   # Controller quản lý tồn kho
│   └── ArticleManagementController.java     # Controller quản lý tin tức
├── dto/manager/
│   ├── UpdateProfileRequest.java            # DTO cho cập nhật profile
│   ├── ChangePasswordRequest.java           # DTO cho đổi mật khẩu
│   ├── AssignDeliveryTeamRequest.java       # DTO phân công đội giao hàng
│   ├── ChangeDeliveryTeamRequest.java       # DTO thay đổi đội giao hàng
│   ├── DeliveryTeamDTO.java                 # DTO thông tin đội giao hàng
│   ├── OrderDeliveryDTO.java                # DTO thông tin giao hàng
│   ├── PendingOrderDTO.java                 # DTO đơn hàng cần phân công
│   ├── ZoneDTO.java                         # DTO thông tin khu vực
│   ├── OrderManagementDTO.java              # DTO chi tiết đơn hàng
│   ├── OrderListDTO.java                    # DTO danh sách đơn hàng
│   ├── ReturnRequestDTO.java                # DTO yêu cầu trả hàng
│   ├── OrderActionRequest.java              # DTO request actions
│   ├── StockAlertDTO.java                   # DTO cảnh báo tồn kho
│   ├── StockReportDTO.java                  # DTO báo cáo tồn kho
│   ├── StockSummaryDTO.java                 # DTO tổng quan tồn kho
│   ├── UpdateStockRequest.java              # DTO cập nhật tồn kho
│   ├── LowStockProductDTO.java              # DTO sản phẩm tồn kho thấp
│   ├── CreateArticleRequest.java            # DTO tạo bài viết mới
│   ├── UpdateArticleRequest.java            # DTO cập nhật bài viết
│   ├── ArticleListDTO.java                  # DTO danh sách bài viết
│   ├── ArticleDetailDTO.java                # DTO chi tiết bài viết
│   └── ArticleDashboardDTO.java             # DTO thống kê bài viết
├── service/manager/
│   ├── ManagerAccountService.java           # Service xử lý logic quản lý tài khoản
│   ├── DeliveryAssignmentService.java       # Service xử lý logic phân công
│   ├── OrderManagementService.java          # Service quản lý đơn hàng
│   ├── OrderStoredProcedureService.java     # Service tích hợp stored procedures
│   ├── InventoryManagementService.java      # Service quản lý tồn kho
│   ├── ArticleService.java                  # Service quản lý bài viết
│   └── ArticleImageService.java             # Service quản lý ảnh bài viết
└── repository/
    ├── OrdersRepository.java                # Repository đơn hàng
    ├── OrderDeliveryRepository.java         # Repository giao hàng
    ├── DeliveryTeamRepository.java          # Repository đội giao hàng
    ├── DeliveryTeamZoneRepository.java      # Repository đội-khu vực
    ├── ProvinceZoneRepository.java          # Repository tỉnh-khu vực
    ├── ShippingZoneRepository.java          # Repository khu vực
    ├── DeliveryHistoryRepository.java       # Repository lịch sử giao hàng
    ├── OrderStatusHistoryRepository.java    # Repository lịch sử trạng thái
    ├── ProductVariantRepository.java        # Repository biến thể sản phẩm
    ├── ProductRepository.java               # Repository sản phẩm
    ├── ArticleRepository.java               # Repository bài viết
    └── ArticleImageRepository.java          # Repository ảnh bài viết

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
│   ├── orders/
│   │   ├── pending.html               # Danh sách đơn chờ xác nhận
│   │   ├── pending-detail.html        # Chi tiết đơn chờ xác nhận
│   │   ├── completed.html             # Danh sách đơn hoàn thành
│   │   ├── completed-detail.html      # Chi tiết đơn hoàn thành
│   │   ├── returns.html               # Danh sách yêu cầu trả hàng
│   │   └── return-detail.html         # Chi tiết yêu cầu trả hàng
│   ├── inventory/
│   │   ├── alerts.html                # Cảnh báo tồn kho
│   │   ├── update-stock.html          # Cập nhật tồn kho
│   │   ├── report.html                # Báo cáo tồn kho
│   │   └── low-stock.html             # Quản lý sản phẩm tồn kho thấp
│   └── articles/
│       ├── article_list.html          # Danh sách bài viết
│       ├── article_create.html        # Tạo bài viết mới
│       ├── article_edit.html          # Chỉnh sửa bài viết
│       └── article_detail.html        # Chi tiết bài viết
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

### 6. Quản lý tồn kho (`/manager/inventory/*`)

#### 6.1. UC-MGR-INV-ViewStockAlerts - Xem cảnh báo tồn kho
**Route:** `/manager/inventory/alerts`

- **Dashboard tổng quan:**
  - Tổng số sản phẩm (variants)
  - Số sản phẩm hết hàng (stock_qty = 0)
  - Số sản phẩm tồn kho thấp (1-5)
  - Tổng giá trị tồn kho

- **Danh sách cảnh báo:**
  - Sắp xếp theo mức độ ưu tiên (hết hàng → tồn kho thấp)
  - Lọc theo loại cảnh báo (Hết hàng / Tồn kho thấp)
  - Tìm kiếm theo tên sản phẩm hoặc SKU
  - Hiển thị thông tin màu sắc và loại của từng variant

#### 6.2. UC-MGR-INV-UpdateStock - Cập nhật số lượng tồn kho
**Route:** `/manager/inventory/update/{variantId}`

- **Chức năng:**
  - Form cập nhật số lượng tồn kho
  - Preview real-time khi thay đổi số lượng
  - Xem trước thay đổi (tồn kho cũ → mới)
  - Nhập ghi chú lý do cập nhật (tùy chọn)
  - Validation: số lượng >= 0, sản phẩm active
  - Ghi log mọi thay đổi tồn kho

- **Trigger tự động:**
  - `TR_ProductVariant_StockAlerts` tự động tạo thông báo khi:
    - Tồn kho thấp (1-5) và đang giảm
    - Hết hàng (0)
    - Có hàng trở lại (từ 0 lên > 0)

#### 6.3. UC-MGR-INV-ViewStockReport - Xem báo cáo tồn kho
**Route:** `/manager/inventory/report`

- **Báo cáo tổng quan:**
  - Biểu đồ thống kê tồn kho
  - Danh sách chi tiết tất cả sản phẩm và biến thể
  - Tính giá trị tồn kho (stock_qty × sale_price)

- **Lọc và tìm kiếm:**
  - Lọc theo mức tồn kho (Hết hàng / Thấp / Vừa / Tốt)
  - Lọc theo danh mục sản phẩm
  - Tìm kiếm theo tên sản phẩm/SKU
  - Xuất Excel (đang phát triển)

#### 6.4. UC-MGR-INV-ManageLowStock - Quản lý sản phẩm tồn kho thấp
**Route:** `/manager/inventory/low-stock`

- **Danh sách ưu tiên:**
  - Sản phẩm có tồn kho <= 5
  - Sắp xếp theo mức độ ưu tiên xử lý
  - Priority system (1-10):
    - Priority 1 (Đỏ) - Hết hàng (stock_qty = 0)
    - Priority 2 (Cam) - Dự kiến hết trong 3 ngày
    - Priority 3 (Vàng) - Dự kiến hết trong 7 ngày
    - Priority 10 (Xám) - Tồn kho ổn định

- **Thống kê & Dự báo:**
  - Số lượng đã bán trong 30 ngày qua
  - Trung bình bán mỗi ngày
  - Dự báo số ngày còn lại trước khi hết hàng
  - Công thức: `stock_qty / avg_daily_sales`

- **Hành động:**
  - Cập nhật tồn kho nhanh
  - Ẩn/Hiện sản phẩm tạm thời
  - Xem sản phẩm trên website
  - Tạo đơn nhập hàng (đang phát triển)
  - Xem lịch sử thay đổi (đang phát triển)

### 7. Quản lý tin tức (`/manager/articles/*`)

#### 7.1. UC-MGR-ART-ListMyPosts - Xem danh sách bài viết
**Route:** `/manager/articles`

- **Dashboard tổng quan:**
  - Tổng số bài viết
  - Số bài viết đã xuất bản
  - Số bài viết nháp
  - Tổng lượt xem

- **Danh sách bài viết:**
  - Xem tất cả bài viết của chính mình
  - Lọc theo loại bài viết (MEDIA/NEWS/PEOPLE)
  - Lọc theo trạng thái (Đã xuất bản/Nháp)
  - Tìm kiếm theo tiêu đề, tóm tắt
  - Sắp xếp theo: Ngày tạo, Ngày xuất bản, Lượt xem
  - Phân trang
  - Actions: Xem, Sửa, Ẩn/Hiện

#### 7.2. UC-MGR-ART-CreatePost - Tạo bài viết mới
**Route:** `/manager/articles/create`

- **Các trường:**
  - **Tiêu đề** (bắt buộc, max 300 ký tự)
  - **Loại bài viết** (bắt buộc): MEDIA, NEWS, PEOPLE
  - **Tóm tắt** (tùy chọn, max 500 ký tự)
  - **Nội dung** (Rich text editor với TinyMCE)
  - **Ảnh thumbnail** (bắt buộc, JPG/PNG/WEBP, max 2MB)
  - **Ảnh nội dung** (tùy chọn, nhiều ảnh, max 2MB mỗi ảnh)
  - **Sản phẩm liên quan** (tùy chọn - chọn từ dropdown sản phẩm active)
  - **Nổi bật** (checkbox)
  - **Xuất bản** (checkbox - nếu không chọn sẽ lưu nháp)

- **Quy tắc:**
  - Slug tự động tạo từ tiêu đề (Vietnamese-friendly)
  - Slug đảm bảo duy nhất (thêm số nếu trùng)
  - Ảnh lưu theo cấu trúc: `/static/images/articles/<type>/<slug>/thumbnail/` và `/content/`
  - Set `published_at` nếu status = true
  - Tác giả tự động lấy từ user đang đăng nhập

#### 7.3. UC-MGR-ART-EditPost - Chỉnh sửa bài viết
**Route:** `/manager/articles/{id}/edit`

- **Chức năng:**
  - Cập nhật tất cả thông tin
  - Thay đổi thumbnail
  - Thêm/xóa ảnh nội dung
  - Checkbox "Xóa ảnh cũ" trước khi upload ảnh mới
  - Nếu đổi tiêu đề, slug tự động cập nhật
  - Chỉ tác giả mới được chỉnh sửa

- **Validation:**
  - Kiểm tra quyền sở hữu (author phải trùng với user đăng nhập)
  - Validate định dạng và kích thước file
  - Slug uniqueness

#### 7.4. UC-MGR-ART-ViewDetail - Xem chi tiết bài viết
**Route:** `/manager/articles/{id}`

- **Hiển thị:**
  - Tất cả thông tin của bài viết
  - Metadata (tác giả, ngày tạo, ngày xuất bản, lượt xem)
  - Thumbnail và danh sách ảnh nội dung
  - Sản phẩm liên quan (nếu có)
  - Trạng thái: Nổi bật, Xuất bản/Nháp

- **Cấu trúc lưu trữ ảnh:**
  - Thumbnail: `/static/images/articles/<type>/<slug>/thumbnail/00_<slug>.jpg`
  - Content: `/static/images/articles/<type>/<slug>/content/00_<slug>.jpg`, `01_<slug>.jpg`, ...

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
| GET | `/manager/inventory/alerts` | Trang cảnh báo tồn kho | MANAGER |
| GET | `/manager/inventory/update/{id}` | Form cập nhật tồn kho | MANAGER |
| POST | `/manager/inventory/update` | Submit cập nhật tồn kho | MANAGER |
| POST | `/manager/inventory/quick-update` | API AJAX cập nhật nhanh | MANAGER |
| GET | `/manager/inventory/report` | Trang báo cáo tồn kho | MANAGER |
| GET | `/manager/inventory/low-stock` | Trang quản lý tồn kho thấp | MANAGER |
| POST | `/manager/inventory/hide/{id}` | Ẩn sản phẩm | MANAGER |
| POST | `/manager/inventory/show/{id}` | Hiện sản phẩm | MANAGER |

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

### 7. Quản lý tồn kho
```
URL: http://localhost:8080/manager/inventory/alerts
URL: http://localhost:8080/manager/inventory/report
URL: http://localhost:8080/manager/inventory/low-stock
```

### 8. Quản lý tin tức
```
URL: http://localhost:8080/manager/articles
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

### Quản lý tồn kho
- **Số lượng tồn kho:** Phải >= 0, kiểm tra constraint CHECK (stock_qty >= 0)
- **Sản phẩm hoạt động:** Chỉ cập nhật sản phẩm có is_active = 1
- **Quyền hạn:** Chỉ role MANAGER mới được quản lý tồn kho
- **Trigger cảnh báo:** Tự động tạo thông báo khi tồn kho thấp (1-5) hoặc hết hàng (0)
- **Dedupe thông báo:** Chống spam thông báo trong 12 giờ
- **Audit Trail:** Ghi log mọi thay đổi tồn kho
- **Ẩn sản phẩm:** Sản phẩm ẩn không hiển thị trên website nhưng không ảnh hưởng đơn đã tạo

### Quản lý tin tức
- **Tiêu đề:** Bắt buộc, tối đa 300 ký tự
- **Slug:** Tự động tạo từ tiêu đề, duy nhất (thêm số nếu trùng)
- **Loại bài viết:** Bắt buộc, chỉ nhận MEDIA/NEWS/PEOPLE
- **Tóm tắt:** Tùy chọn, tối đa 500 ký tự
- **Ảnh thumbnail:** Bắt buộc khi tạo mới, JPG/PNG/WEBP, max 2MB
- **Ảnh nội dung:** Tùy chọn, nhiều ảnh, JPG/PNG/WEBP, max 2MB mỗi ảnh
- **Quyền sở hữu:** Chỉ tác giả mới được sửa bài viết của mình
- **Sản phẩm liên quan:** Chỉ chọn từ sản phẩm active
- **Trạng thái:** true = Xuất bản, false = Nháp
- **Lưu ý:** Manager không có quyền ẩn bài viết (chỉ Admin mới có quyền này)

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
- `ProductVariant` - Biến thể sản phẩm (lưu tồn kho)
- `Product` - Thông tin sản phẩm
- `Color` - Màu sắc sản phẩm
- `Category` - Danh mục sản phẩm
- `OrderItems` - Dùng để tính số lượng đã bán
- `Article` - Bài viết tin tức
- `ArticleImage` - Ảnh bài viết

### Stored Procedures & Triggers
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
- `TR_ProductVariant_StockAlerts` - Tự động tạo cảnh báo tồn kho
- `TR_OrderDelivery_NotifyDeliveryTeam` - Thông báo cho đội giao hàng
- `TR_ProductVariant_NotifyWishlistBackInStock` - Thông báo khách hàng khi có hàng trở lại
- `TR_Article_NotifyNew` - Thông báo khi có bài viết mới được xuất bản

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

### Inventory Management Features
- **Dự báo hết hàng:**
  - Tính dựa trên lịch sử bán 30 ngày qua
  - Ước tính số ngày còn lại: `stock_qty / avg_daily_sales`
  - Cảnh báo ưu tiên nếu dự kiến hết trong 3-7 ngày

- **Priority System:**
  - Priority 1 (Đỏ) - Hết hàng (stock_qty = 0)
  - Priority 2 (Cam) - Dự kiến hết trong 3 ngày
  - Priority 3 (Vàng) - Dự kiến hết trong 7 ngày
  - Priority 10 (Xám) - Tồn kho ổn định

- **Real-time Preview:**
  - Form cập nhật tồn kho có preview real-time
  - Hiển thị thay đổi: Cũ → Mới
  - JavaScript update ngay lập tức

- **Auto Stock Alerts:**
  - Trigger tự động tạo thông báo khi tồn kho thay đổi
  - Dedupe 12 giờ để tránh spam
  - Thông báo Manager, Admin và khách hàng (wishlist)

## Logging & Audit

### 1. Activity Logging
- Ghi log mọi thay đổi thông tin cá nhân
- Ghi log đổi mật khẩu
- Ghi log phân công đội giao hàng
- Ghi log xác nhận/hủy đơn hàng
- Ghi log duyệt/từ chối trả hàng
- **Ghi log cập nhật tồn kho** (manager_id, variant_id, old_qty, new_qty, note)
- **Ghi log ẩn/hiện sản phẩm** (manager_id, variant_id, action, reason)
- **Ghi log tạo/sửa/xóa bài viết** (manager_id, article_id, action, changes)
- Sử dụng Spring AOP (có thể implement thêm)

### 2. Security Logging
- Log failed login attempts
- Log password change attempts
- Log profile update attempts
- Log delivery assignment changes
- Log order management actions
- Log return request processing
- **Log stock update actions** (thời gian, user, sản phẩm, thay đổi)
- **Log product visibility changes** (thời gian, user, sản phẩm, lý do)
- **Log article management actions** (thời gian, user, bài viết, action)

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
- **"Số lượng tồn kho phải >= 0"** - Validation tồn kho không hợp lệ
- **"Không thể cập nhật tồn kho cho sản phẩm đã vô hiệu hóa"** - Sản phẩm không active
- **"Không tìm thấy biến thể sản phẩm"** - Variant ID không tồn tại

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
- **Xuất báo cáo Excel/PDF cho tồn kho**
- **Tạo đơn nhập hàng tự động**
- **Lịch sử thay đổi tồn kho chi tiết**
- **Biểu đồ xu hướng tồn kho**
- **Cảnh báo email khi tồn kho thấp**
- **Tích hợp với hệ thống ERP/WMS**
- **Bulk update tồn kho (upload Excel)**
- **Stock forecasting với ML**
- **Quản lý tags cho bài viết**
- **SEO optimization cho bài viết**
- **Scheduled publishing (đăng bài theo lịch)**
- **Draft auto-save**
- **Version control cho bài viết**

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
