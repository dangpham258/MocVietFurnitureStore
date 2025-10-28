# Chức năng Quản lý Đánh giá/Bình luận - Manager

## Tổng quan
Chức năng quản lý đánh giá/bình luận cho Manager được implement đầy đủ theo đặc tả use case, bao gồm 4 use cases chính.

## Use Cases đã implement

### 1. UC-MGR-REV-ViewAllReviews: Xem tất cả đánh giá
**Route:** `/manager/reviews/all`

**Chức năng:**
- Dashboard thống kê đầy đủ:
  - Tổng số đánh giá
  - Số đánh giá chưa trả lời
  - Đánh giá trung bình hệ thống
  - Đánh giá mới trong tuần
- Danh sách đánh giá với pagination (15 items/page)
- Filter đa điều kiện:
  - Tìm kiếm theo tên sản phẩm/khách hàng
  - Lọc theo sản phẩm
  - Lọc theo số sao (1-5)
  - Lọc theo trạng thái (Hiển thị/Ẩn)
  - Lọc đánh giá chưa trả lời
- Hiển thị đầy đủ thông tin:
  - Tên khách hàng
  - Tên sản phẩm (có link)
  - Số sao đánh giá
  - Nội dung đánh giá
  - Ảnh đánh giá (nếu có)
  - Thời gian đánh giá
  - Trạng thái (Hiển thị/Ẩn)
  - Phản hồi của manager (nếu có)

### 2. UC-MGR-REV-RespondToReview: Trả lời đánh giá
**Route:** `/manager/reviews/{id}/respond`

**Chức năng:**
- Form trả lời đánh giá với validation:
  - Không được để trống
  - Tối đa 1000 ký tự
  - Character counter real-time
- Hiển thị đầy đủ thông tin đánh giá gốc
- Tự động tạo thông báo cho khách hàng (qua Trigger TR_Review_NotifyManagerResponse)
- Hỗ trợ sửa/xóa phản hồi đã gửi

### 3. UC-MGR-REV-HideShowReview: Ẩn/hiện đánh giá
**Route:** `/manager/reviews/{id}/toggle-visibility`

**Chức năng:**
- Toggle ẩn/hiện đánh giá đơn lẻ
- Tự động cập nhật rating sản phẩm (qua Trigger TR_Review_UpdateProductRating)
- Hỗ trợ bulk operations (ẩn/hiện hàng loạt)
- Hiển thị badge trạng thái rõ ràng

### 4. UC-MGR-REV-ManageReviewAlerts: Quản lý cảnh báo đánh giá mới
**Route:** `/manager/reviews/alerts`

**Chức năng:**
- Dashboard thống kê cảnh báo
- Danh sách cảnh báo theo mức độ ưu tiên:
  - **HIGH Priority:**
    - Đánh giá rating thấp (1-2 sao)
    - Nội dung tiêu cực
  - **MEDIUM Priority:**
    - Đánh giá mới chưa trả lời
- Phân loại alerts:
  - `LOW_RATING`: Rating 1-2 sao
  - `NEGATIVE_CONTENT`: Nội dung có từ khóa tiêu cực
  - `NEW_REVIEW`: Đánh giá mới
- Quick actions: Trả lời ngay, xem chi tiết

## Kiến trúc Code

### DTOs (`src/main/java/mocviet/dto/manager/`)
- `ReviewDTO`: DTO cho review entity
- `ReviewStatsDTO`: DTO cho thống kê dashboard
- `ReviewFilterDTO`: DTO cho filter/search
- `ReviewResponseRequest`: DTO cho request trả lời
- `ReviewAlertDTO`: DTO cho cảnh báo

### Repository (`src/main/java/mocviet/repository/ReviewRepository.java`)
Đã mở rộng với các query methods:
- `findAllByOrderByCreatedAtDesc()`: Lấy tất cả reviews
- `countByManagerResponseIsNull()`: Đếm chưa trả lời
- `findByKeywordAndFilters()`: Filter phức tạp
- `findNewUnansweredReviews()`: Lấy alerts
- `calculateAverageSystemRating()`: Tính rating TB

### Service (`src/main/java/mocviet/service/manager/ReviewManagementService.java`)
Methods chính:
- `getReviewStats()`: Lấy thống kê
- `getReviews()`: Lấy danh sách với filter
- `respondToReview()`: Trả lời đánh giá
- `toggleReviewVisibility()`: Ẩn/hiện
- `getReviewAlerts()`: Lấy cảnh báo

### Controller (`src/main/java/mocviet/controller/manager/ReviewManagementController.java`)
Endpoints:
- `GET /manager/reviews/all`: Xem tất cả
- `GET /manager/reviews/{id}/respond`: Form trả lời
- `POST /manager/reviews/{id}/respond`: Gửi phản hồi
- `POST /manager/reviews/{id}/toggle-visibility`: Ẩn/hiện
- `GET /manager/reviews/alerts`: Cảnh báo

### Templates (`src/main/resources/templates/manager/reviews/`)
- `all-reviews.html`: Trang danh sách chính
- `respond-form.html`: Form trả lời
- `alerts.html`: Trang cảnh báo

### CSS (`src/main/resources/static/css/manager.css`)
Đã thêm styling cho:
- Review table
- Rating stars
- Manager response box
- Alert items
- Filter panel
- Responsive design

## Tích hợp Database

### Triggers tự động
- `TR_Review_UpdateProductRating`: Auto update rating sản phẩm khi ẩn/hiện review
- `TR_Review_NotifyManagerResponse`: Auto thông báo customer khi manager trả lời

### Validation
- Rating: 1-5 sao (CHECK constraint)
- Manager response: Max 1000 ký tự
- Image URL: Phải trong `/static/images/reviews/`
- Only verified purchases can review (link OrderItems)

## Navigation
Menu đã được thêm vào sidebar:
```
Quản lý đánh giá
  ├── Tất cả đánh giá
  └── Cảnh báo đánh giá
```

## Testing
Để test chức năng:
1. Đăng nhập với role MANAGER
2. Truy cập `/manager/reviews/all`
3. Test các filter/search
4. Thử trả lời đánh giá
5. Test ẩn/hiện đánh giá
6. Kiểm tra alerts tại `/manager/reviews/alerts`

## Notes
- Tất cả operations đều có transaction management
- Pagination mặc định: 15 items/page
- Real-time character counter khi nhập phản hồi
- Responsive design cho mobile
- Error handling đầy đủ với flash messages

