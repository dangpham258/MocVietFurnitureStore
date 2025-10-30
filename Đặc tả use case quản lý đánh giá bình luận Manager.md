**3.2. Biểu đồ Use case (Use case Diagram) - Quản lý đánh giá/bình luận Manager**

**3.2.1. Xem tất cả đánh giá**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-REV-ViewAllReviews                         |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem tất cả đánh giá                                |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách tất cả đánh giá của |
|              | khách hàng về sản phẩm                             |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách đánh giá   |
| conditions** |     với đầy đủ thông tin                           |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đánh giá"       |
| Flow**       |                                                       |
|              | 2. Hệ thống hiển thị dashboard đánh giá:          |
|              |     - Tổng số đánh giá                           |
|              |     - Số đánh giá chưa trả lời                   |
|              |     - Đánh giá trung bình hệ thống               |
|              |     - Đánh giá mới trong tuần                    |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đánh giá:         |
|              |     - Tất cả đánh giá từ khách hàng             |
|              |     - Sắp xếp theo thời gian (mới nhất)          |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi đánh giá: |
|              |     - Tên khách hàng                            |
|              |     - Tên sản phẩm                              |
|              |     - Số sao đánh giá (1-5)                     |
|              |     - Nội dung đánh giá                         |
|              |     - Ảnh đánh giá (nếu có)                     |
|              |     - Thời gian đánh giá                        |
|              |     - Trạng thái (Hiển thị/Ẩn)                |
|              |     - Phản hồi của manager (nếu có)            |
|              |                                                       |
|              | 5. Manager có thể:                              |
|              |     - Lọc theo sản phẩm                        |
|              |     - Lọc theo số sao                           |
|              |     - Lọc theo trạng thái (Hiển thị/Ẩn)        |
|              |     - Lọc theo đánh giá chưa trả lời            |
|              |     - Tìm kiếm theo tên sản phẩm/khách hàng     |
|              |     - Sắp xếp theo tiêu chí khác                |
|              |                                                       |
|              | 6. Manager có thể xem chi tiết đánh giá        |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết đánh giá        |
| lternative** |                                                       |
|              | 2a. Manager có thể trả lời đánh giá từ danh sách |
|              |                                                       |
|              | 3a. Manager có thể ẩn/hiện đánh giá từ danh sách |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có đánh giá nào                       |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có đánh giá nào"   |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.2. Trả lời đánh giá của khách hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-REV-RespondToReview                        |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Trả lời đánh giá của khách hàng                   |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager trả lời phản hồi cho đánh giá    |
|              | của khách hàng                                    |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Đánh giá tồn tại và chưa có phản hồi           |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, phản hồi được lưu và thông báo |
| conditions** |     cho khách hàng                                 |
|              |                                                       |
|              | -   Nếu thất bại, không có thay đổi nào, hiển thị  |
|              |     thông báo lỗi.                                 |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đánh giá"       |
| Flow**       |                                                       |
|              | 2. Manager chọn đánh giá cần trả lời              |
|              |                                                       |
|              | 3. Manager nhấn nút "Trả lời đánh giá"           |
|              |                                                       |
|              | 4. Hệ thống hiển thị form trả lời:               |
|              |     - Thông tin đánh giá gốc                     |
|              |     - Tên khách hàng                             |
|              |     - Nội dung đánh giá                          |
|              |     - Số sao đánh giá                           |
|              |     - Trường nhập phản hồi                       |
|              |                                                       |
|              | 5. Manager nhập nội dung phản hồi                |
|              |                                                       |
|              | 6. Manager nhấn "Gửi phản hồi"                 |
|              |                                                       |
|              | 7. Hệ thống validate dữ liệu:                   |
|              |     - Kiểm tra nội dung phản hồi không rỗng     |
|              |     - Kiểm tra độ dài phản hồi <= 1000 ký tự     |
|              |     - Kiểm tra đánh giá chưa có phản hồi        |
|              |                                                       |
|              | 8. Hệ thống cập nhật bảng Review:               |
|              |     - Cập nhật manager_response                  |
|              |     - Cập nhật manager_id                        |
|              |     - Cập nhật response_at = GETDATE()          |
|              |                                                       |
|              | 9. Trigger TR_Review_NotifyManagerResponse chạy:  |
|              |     - Tạo thông báo cho khách hàng              |
|              |     - Thông báo "Có phản hồi từ cửa hàng"       |
|              |                                                       |
|              | 10. Hệ thống ghi log hoạt động                  |
|              |                                                       |
|              | 11. Hiển thị thông báo "Trả lời thành công"     |
|              |                                                       |
|              | 12. Redirect về trang quản lý đánh giá         |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể trả lời hàng loạt nhiều đánh  |
| lternative** |     giá cùng lúc                                 |
|              |                                                       |
|              | 2a. Manager có thể sửa phản hồi đã gửi          |
|              |                                                       |
|              | 3a. Manager có thể xóa phản hồi                 |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Đánh giá không tồn tại                        |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Đánh giá không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1d. Đánh giá đã có phản hồi                      |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Đánh giá đã có     |
|              |     phản hồi"                                      |
|              |                                                       |
|              | 1e. Nội dung phản hồi rỗng                       |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Vui lòng nhập nội  |
|              |     dung phản hồi"                                |
|              |                                                       |
|              | 1f. Nội dung phản hồi quá dài                    |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Nội dung phản hồi  |
|              |     quá dài (tối đa 1000 ký tự)"               |
|              |                                                       |
|              | 1g. Mất kết nối database                         |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.3. Ẩn/hiện đánh giá**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-REV-HideShowReview                         |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Ẩn/hiện đánh giá                                  |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager ẩn hoặc hiện đánh giá không phù  |
|              | hợp hoặc không mong muốn                         |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Đánh giá tồn tại                               |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, trạng thái đánh giá được thay  |
| conditions** |     đổi và rating sản phẩm được cập nhật           |
|              |                                                       |
|              | -   Nếu thất bại, không có thay đổi nào, hiển thị  |
|              |     thông báo lỗi.                                 |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đánh giá"       |
| Flow**       |                                                       |
|              | 2. Manager chọn đánh giá cần ẩn/hiện              |
|              |                                                       |
|              | 3. Manager nhấn nút "Ẩn đánh giá" hoặc "Hiện    |
|              |     đánh giá"                                      |
|              |                                                       |
|              | 4. Hệ thống hiển thị popup xác nhận:            |
|              |     - Thông tin đánh giá                         |
|              |     - Lý do ẩn/hiện (tùy chọn)                  |
|              |     - Cảnh báo về việc thay đổi rating          |
|              |                                                       |
|              | 5. Manager nhập lý do (nếu cần)                 |
|              |                                                       |
|              | 6. Manager nhấn "Xác nhận"                      |
|              |                                                       |
|              | 7. Hệ thống validate điều kiện:                 |
|              |     - Kiểm tra đánh giá tồn tại                 |
|              |     - Kiểm tra quyền thay đổi trạng thái        |
|              |                                                       |
|              | 8. Hệ thống cập nhật bảng Review:              |
|              |     - Cập nhật is_hidden = 1 (ẩn) hoặc 0 (hiện) |
|              |                                                       |
|              | 9. Trigger TR_Review_UpdateProductRating chạy:   |
|              |     - Tính lại avg_rating cho sản phẩm         |
|              |     - Cập nhật total_reviews                    |
|              |     - Chỉ tính các đánh giá is_hidden = 0       |
|              |                                                       |
|              | 10. Hệ thống ghi log hoạt động                  |
|              |                                                       |
|              | 11. Hiển thị thông báo "Thay đổi thành công"    |
|              |                                                       |
|              | 12. Redirect về trang quản lý đánh giá         |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể ẩn/hiện hàng loạt nhiều đánh  |
| lternative** |     giá cùng lúc                                 |
|              |                                                       |
|              | 2a. Manager có thể xem lịch sử ẩn/hiện đánh giá  |
|              |                                                       |
|              | 3a. Manager có thể khôi phục đánh giá đã ẩn      |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Đánh giá không tồn tại                        |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Đánh giá không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1d. Không có quyền thay đổi trạng thái           |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Không có quyền     |
|              |     thay đổi trạng thái đánh giá"               |
|              |                                                       |
|              | 1e. Lỗi cập nhật rating sản phẩm                |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi cập nhật rating |
|              |     sản phẩm"                                     |
|              |                                                       |
|              | 1f. Mất kết nối database                         |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.4. Quản lý cảnh báo đánh giá mới**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-REV-ManageReviewAlerts                     |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Quản lý cảnh báo đánh giá mới                     |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem và xử lý các cảnh báo đánh   |
|              | giá mới từ khách hàng                             |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Có đánh giá mới chưa được xử lý                |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, cảnh báo được xử lý và đánh   |
| conditions** |     giá được quản lý phù hợp                      |
|              |                                                       |
|              | -   Nếu thất bại, không có thay đổi nào, hiển thị  |
|              |     thông báo lỗi.                                 |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đánh giá"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Cảnh báo đánh giá mới"       |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách cảnh báo:         |
|              |     - Đánh giá mới chưa trả lời                  |
|              |     - Đánh giá có rating thấp (1-2 sao)          |
|              |     - Đánh giá có nội dung tiêu cực               |
|              |     - Sắp xếp theo mức độ ưu tiên               |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cảnh báo:         |
|              |     - Loại cảnh báo                             |
|              |     - Thông tin đánh giá                        |
|              |     - Tên khách hàng                            |
|              |     - Tên sản phẩm                              |
|              |     - Thời gian tạo cảnh báo                    |
|              |     - Mức độ ưu tiên                            |
|              |                                                       |
|              | 5. Manager chọn hành động xử lý:                |
|              |     - Trả lời đánh giá                          |
|              |     - Ẩn đánh giá                               |
|              |     - Đánh dấu đã xem                          |
|              |     - Xem chi tiết đánh giá                     |
|              |                                                       |
|              | 6. Nếu chọn "Trả lời đánh giá":                 |
|              |     - Chuyển đến form trả lời                  |
|              |     - Pre-fill thông tin đánh giá              |
|              |                                                       |
|              | 7. Nếu chọn "Ẩn đánh giá":                     |
|              |     - Hiển thị popup xác nhận                  |
|              |     - Manager nhập lý do ẩn                   |
|              |     - Hệ thống cập nhật is_hidden = 1          |
|              |                                                       |
|              | 8. Nếu chọn "Đánh dấu đã xem":                 |
|              |     - Cập nhật trạng thái cảnh báo             |
|              |     - Không hiển thị trong danh sách cảnh báo  |
|              |                                                       |
|              | 9. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 10. Hiển thị thông báo "Xử lý thành công"       |
|              |                                                       |
|              | 11. Redirect về trang cảnh báo đánh giá         |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xử lý hàng loạt nhiều cảnh báo |
| lternative** |     cùng lúc                                      |
|              |                                                       |
|              | 2a. Manager có thể thiết lập quy tắc tự động      |
|              |                                                       |
|              | 3a. Manager có thể xem lịch sử xử lý cảnh báo    |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có cảnh báo nào                        |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có cảnh báo nào"    |
|              |                                                       |
|              | 1d. Cảnh báo không tồn tại                       |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Cảnh báo không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1e. Lỗi cập nhật trạng thái cảnh báo            |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi cập nhật trạng  |
|              |     thái cảnh báo"                                |
|              |                                                       |
|              | 1f. Mất kết nối database                         |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.5. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Đánh giá hợp lệ:** Chỉ customer đã mua sản phẩm mới được đánh giá (link OrderItems)
- **Rating:** Phải từ 1-5 sao, kiểm tra CHECK constraint
- **Nội dung phản hồi:** Tối đa 1000 ký tự
- **Ảnh đánh giá:** Phải ở /static/images/reviews/, tối đa 2MB
- **Trạng thái:** is_hidden = 0 (hiển thị) hoặc 1 (ẩn)
- **Trigger rating:** Tự động cập nhật avg_rating và total_reviews khi có thay đổi

**Bảo mật:**
- **Permission Check:** Chỉ role MANAGER mới được quản lý đánh giá
- **Audit Trail:** Ghi log mọi thay đổi đánh giá và phản hồi
- **Data Validation:** Kiểm tra dữ liệu đầu vào nghiêm ngặt
- **Content Filtering:** Lọc nội dung không phù hợp

**Tính năng đặc biệt:**
- **Auto Rating Update:** Trigger tự động cập nhật rating sản phẩm
- **Smart Notifications:** Thông báo thông minh cho manager và customer
- **Review Verification:** Chỉ khách đã mua mới được đánh giá
- **Response Tracking:** Theo dõi phản hồi của manager
- **Bulk Operations:** Xử lý hàng loạt nhiều đánh giá
- **Alert Management:** Quản lý cảnh báo đánh giá mới với priority levels
- **Content Moderation:** Ẩn/hiện đánh giá không phù hợp
- **Negative Content Detection:** Tự động phát hiện nội dung tiêu cực
- **Enhanced Dashboard:** KPI cards với stats chi tiết và progress bars

**Quy trình nghiệp vụ:**
1. **Cảnh báo tự động:** Trigger tạo thông báo khi có đánh giá mới
2. **Quản lý thủ công:** Manager xem, trả lời và ẩn/hiện đánh giá
3. **Rating tự động:** Trigger cập nhật rating sản phẩm khi có thay đổi
4. **Thông báo customer:** Tự động thông báo khi manager trả lời
5. **Content moderation:** Kiểm duyệt và ẩn đánh giá không phù hợp

**Tiêu chí chấp nhận:**
1. Manager có thể xem tất cả đánh giá của khách hàng
2. Manager có thể trả lời đánh giá của khách hàng
3. Manager có thể ẩn/hiện đánh giá không phù hợp
4. Manager có thể quản lý cảnh báo đánh giá mới
5. Hệ thống tự động cập nhật rating sản phẩm
6. Thông báo được gửi đến Manager khi có đánh giá mới
7. Customer được thông báo khi manager trả lời đánh giá
8. Validation đầy đủ dữ liệu đầu vào
9. Ghi log đầy đủ mọi thao tác quản lý đánh giá
10. Hỗ trợ xử lý hàng loạt nhiều đánh giá
11. Dashboard hiển thị thống kê đánh giá trực quan
12. Tìm kiếm và lọc đánh giá theo nhiều tiêu chí
13. Chỉ khách đã mua sản phẩm mới được đánh giá
14. Trigger tự động cập nhật avg_rating và total_reviews
15. Quản lý cảnh báo đánh giá thông minh
16. Content moderation hiệu quả
17. Responsive design cho mọi thiết bị
18. Error handling robust với fallback values
19. Audit trail đầy đủ cho compliance
20. Performance optimization cho large dataset
