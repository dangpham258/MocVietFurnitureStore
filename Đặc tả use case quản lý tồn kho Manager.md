**3.2. Biểu đồ Use case (Use case Diagram) - Quản lý tồn kho Manager**

**3.2.1. Xem cảnh báo tồn kho**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-INV-ViewStockAlerts                         |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem cảnh báo tồn kho                                |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách sản phẩm có tồn kho |
|              | thấp hoặc hết hàng                                  |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách cảnh báo   |
| conditions** |     tồn kho                                         |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tồn kho"        |
| Flow**       |                                                       |
|              | 2. Hệ thống hiển thị dashboard tồn kho:            |
|              |     - Tổng số sản phẩm hết hàng (stock_qty = 0)   |
|              |     - Tổng số sản phẩm tồn kho thấp (1-5)        |
|              |     - Tổng số sản phẩm có tồn kho               |
|              |                                                       |
|              | 3. Manager chọn tab "Cảnh báo tồn kho"            |
|              |                                                       |
|              | 4. Hệ thống hiển thị danh sách cảnh báo:         |
|              |     - Sản phẩm hết hàng (stock_qty = 0)          |
|              |     - Sản phẩm tồn kho thấp (1-5)               |
|              |     - Sắp xếp theo mức độ ưu tiên               |
|              |                                                       |
|              | 5. Hệ thống hiển thị thông tin cho mỗi sản phẩm: |
|              |     - Tên sản phẩm                               |
|              |     - SKU                                       |
|              |     - Màu sắc và loại                           |
|              |     - Số lượng tồn kho hiện tại                  |
|              |     - Trạng thái (Hết hàng/Tồn kho thấp)        |
|              |     - Thời gian cập nhật cuối                    |
|              |                                                       |
|              | 6. Manager có thể:                              |
|              |     - Lọc theo loại cảnh báo                    |
|              |     - Tìm kiếm theo tên sản phẩm/SKU            |
|              |     - Sắp xếp theo mức độ ưu tiên               |
|              |     - Xem chi tiết sản phẩm                     |
|              |                                                       |
|              | 7. Manager có thể cập nhật tồn kho trực tiếp     |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết sản phẩm         |
| lternative** |                                                       |
|              | 2a. Manager có thể cập nhật tồn kho từ danh sách |
|              |                                                       |
|              | 3a. Manager có thể xuất báo cáo tồn kho         |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có sản phẩm nào cần cảnh báo           |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có cảnh báo nào"   |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.2. Cập nhật số lượng tồn kho**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-INV-UpdateStock                             |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Cập nhật số lượng tồn kho                          |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager cập nhật số lượng tồn kho cho     |
|              | sản phẩm                                           |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Sản phẩm tồn tại và đang hoạt động              |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, stock_qty được cập nhật, hệ   |
| conditions** |     thống ghi log và kiểm tra cảnh báo             |
|              |                                                       |
|              | -   Nếu thất bại, tồn kho không thay đổi, hiển    |
|              |     thị thông báo lỗi.                            |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tồn kho"        |
| Flow**       |                                                       |
|              | 2. Manager chọn sản phẩm cần cập nhật tồn kho     |
|              |                                                       |
|              | 3. Manager nhấn nút "Cập nhật tồn kho"            |
|              |                                                       |
|              | 4. Hệ thống hiển thị form cập nhật:              |
|              |     - Thông tin sản phẩm hiện tại                |
|              |     - Số lượng tồn kho hiện tại                   |
|              |     - Trường nhập số lượng mới                   |
|              |     - Ghi chú (tùy chọn)                         |
|              |                                                       |
|              | 5. Manager nhập số lượng tồn kho mới              |
|              |                                                       |
|              | 6. Manager nhập ghi chú (nếu cần)                |
|              |                                                       |
|              | 7. Manager nhấn "Xác nhận cập nhật"              |
|              |                                                       |
|              | 8. Hệ thống validate dữ liệu:                    |
|              |     - Kiểm tra số lượng >= 0                     |
|              |     - Kiểm tra sản phẩm còn hoạt động            |
|              |     - Kiểm tra quyền cập nhật                   |
|              |                                                       |
|              | 9. Hệ thống cập nhật ProductVariant:             |
|              |     - Cập nhật stock_qty = số lượng mới          |
|              |     - Cập nhật updated_at = GETDATE()            |
|              |                                                       |
|              | 10. Trigger TR_ProductVariant_StockAlerts chạy:   |
|              |     - Kiểm tra mức tồn kho mới                   |
|              |     - Tạo thông báo nếu tồn kho thấp (1-5)       |
|              |     - Tạo thông báo nếu hết hàng (0)             |
|              |     - Tạo thông báo nếu có hàng trở lại          |
|              |                                                       |
|              | 11. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 12. Hiển thị thông báo "Cập nhật thành công"     |
|              |                                                       |
|              | 13. Redirect về trang quản lý tồn kho           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể cập nhật hàng loạt nhiều sản   |
| lternative** |     phẩm cùng lúc                                |
|              |                                                       |
|              | 2a. Manager có thể cập nhật từ danh sách cảnh báo |
|              |                                                       |
|              | 3a. Manager có thể nhập hàng mới (tăng tồn kho)  |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Số lượng tồn kho < 0                          |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Số lượng tồn kho    |
|              |     không hợp lệ"                                 |
|              |                                                       |
|              | 1d. Sản phẩm không tồn tại hoặc không hoạt động  |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Sản phẩm không     |
|              |     tồn tại hoặc đã ngừng hoạt động"            |
|              |                                                       |
|              | 1e. Không có quyền cập nhật                     |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Không có quyền     |
|              |     cập nhật tồn kho"                            |
|              |                                                       |
|              | 1f. Mất kết nối database                         |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.3. Xem báo cáo tồn kho**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-INV-ViewStockReport                         |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem báo cáo tồn kho                                |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem báo cáo tổng quan về tình     |
|              | trạng tồn kho của toàn bộ sản phẩm                |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE       |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị báo cáo tồn kho      |
| conditions** |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tồn kho"        |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Báo cáo tồn kho"             |
|              |                                                       |
|              | 3. Hệ thống hiển thị báo cáo tổng quan:          |
|              |     - Tổng số sản phẩm                           |
|              |     - Số sản phẩm có tồn kho                     |
|              |     - Số sản phẩm hết hàng                       |
|              |     - Số sản phẩm tồn kho thấp                   |
|              |     - Tổng giá trị tồn kho                       |
|              |                                                       |
|              | 4. Hệ thống hiển thị biểu đồ thống kê:           |
|              |     - Biểu đồ phân bố tồn kho theo danh mục       |
|              |     - Biểu đồ xu hướng tồn kho theo thời gian     |
|              |     - Top sản phẩm có tồn kho cao nhất           |
|              |     - Top sản phẩm hết hàng nhiều nhất          |
|              |                                                       |
|              | 5. Hệ thống hiển thị danh sách chi tiết:         |
|              |     - Tất cả sản phẩm và biến thể                |
|              |     - Số lượng tồn kho                           |
|              |     - Giá trị tồn kho (stock_qty × price)        |
|              |     - Trạng thái hoạt động                       |
|              |     - Thời gian cập nhật cuối                    |
|              |                                                       |
|              | 6. Manager có thể:                              |
|              |     - Lọc theo danh mục sản phẩm                 |
|              |     - Lọc theo mức tồn kho                      |
|              |     - Tìm kiếm theo tên sản phẩm/SKU            |
|              |     - Sắp xếp theo tiêu chí khác                 |
|              |     - Xuất báo cáo ra Excel/PDF                 |
|              |                                                       |
|              | 7. Manager có thể xem chi tiết sản phẩm          |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết sản phẩm         |
| lternative** |                                                       |
|              | 2a. Manager có thể cập nhật tồn kho từ báo cáo   |
|              |                                                       |
|              | 3a. Manager có thể lọc theo khoảng thời gian     |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có dữ liệu tồn kho                     |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có dữ liệu"         |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.4. Quản lý sản phẩm tồn kho thấp**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-INV-ManageLowStock                          |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Quản lý sản phẩm tồn kho thấp                     |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager quản lý và xử lý các sản phẩm có  |
|              | tồn kho thấp hoặc hết hàng                        |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Có sản phẩm có tồn kho thấp (≤ 5) hoặc hết hàng |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, sản phẩm được xử lý phù hợp,  |
| conditions** |     tồn kho được cập nhật hoặc sản phẩm được ẩn   |
|              |                                                       |
|              | -   Nếu thất bại, không có thay đổi nào, hiển thị  |
|              |     thông báo lỗi.                                 |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tồn kho"        |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Sản phẩm tồn kho thấp"       |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách sản phẩm:          |
|              |     - Sản phẩm hết hàng (stock_qty = 0)          |
|              |     - Sản phẩm tồn kho thấp (1-5)               |
|              |     - Sắp xếp theo mức độ ưu tiên               |
|              |                                                       |
|              | 4. Manager chọn sản phẩm cần xử lý               |
|              |                                                       |
|              | 5. Hệ thống hiển thị thông tin chi tiết:         |
|              |     - Thông tin sản phẩm đầy đủ                 |
|              |     - Lịch sử thay đổi tồn kho                  |
|              |     - Số lượng đã bán trong tháng               |
|              |     - Dự báo hết hàng                            |
|              |                                                       |
|              | 6. Manager chọn hành động xử lý:                |
|              |     - Cập nhật tồn kho (nhập hàng mới)          |
|              |     - Ẩn sản phẩm tạm thời                      |
|              |     - Đánh dấu hết hàng                          |
|              |     - Tạo đơn nhập hàng                          |
|              |                                                       |
|              | 7. Nếu chọn "Cập nhật tồn kho":                 |
|              |     - Hiển thị form nhập số lượng mới           |
|              |     - Manager nhập số lượng và ghi chú           |
|              |     - Hệ thống cập nhật stock_qty               |
|              |     - Trigger tạo thông báo nếu cần              |
|              |                                                       |
|              | 8. Nếu chọn "Ẩn sản phẩm":                      |
|              |     - Hiển thị popup xác nhận                   |
|              |     - Manager nhập lý do ẩn sản phẩm            |
|              |     - Hệ thống cập nhật is_active = 0           |
|              |     - Sản phẩm không hiển thị trên website      |
|              |                                                       |
|              | 9. Nếu chọn "Đánh dấu hết hàng":                 |
|              |     - Hệ thống cập nhật stock_qty = 0           |
|              |     - Tạo thông báo "Hết hàng"                 |
|              |     - Cập nhật trạng thái sản phẩm              |
|              |                                                       |
|              | 10. Nếu chọn "Tạo đơn nhập hàng":                |
|              |     - Chuyển đến trang tạo đơn nhập hàng        |
|              |     - Pre-fill thông tin sản phẩm              |
|              |                                                       |
|              | 11. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 12. Hiển thị thông báo "Xử lý thành công"       |
|              |                                                       |
|              | 13. Redirect về trang quản lý tồn kho           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xử lý hàng loạt nhiều sản phẩm |
| lternative** |     cùng lúc                                      |
|              |                                                       |
|              | 2a. Manager có thể tạo kế hoạch nhập hàng        |
|              |                                                       |
|              | 3a. Manager có thể thiết lập cảnh báo tự động    |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Sản phẩm không tồn tại                        |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Sản phẩm không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1d. Không có quyền xử lý sản phẩm               |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Không có quyền     |
|              |     xử lý sản phẩm"                              |
|              |                                                       |
|              | 1e. Lỗi cập nhật database                        |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi cập nhật dữ    |
|              |     liệu"                                         |
|              |                                                       |
|              | 1f. Mất kết nối database                         |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.5. Quản lý sản phẩm đã ẩn**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-INV-ManageHiddenProducts                    |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Quản lý sản phẩm đã ẩn                              |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem và khôi phục các sản phẩm đã   |
|              | được ẩn tạm thời (is_active = 0)                   |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Có sản phẩm đã được ẩn (is_active = 0)          |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách sản phẩm   |
| conditions** |     đã ẩn và cho phép khôi phục                    |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tồn kho"        |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Sản phẩm đã ẩn"             |
|              |                                                       |
|              | 3. Hệ thống hiển thị thống kê tổng quan:         |
|              |     - Tổng số sản phẩm đã ẩn                     |
|              |     - Số sản phẩm hết hàng đã ẩn                |
|              |     - Số sản phẩm tồn kho thấp đã ẩn            |
|              |                                                       |
|              | 4. Hệ thống hiển thị danh sách sản phẩm đã ẩn:   |
|              |     - Tên sản phẩm và SKU                       |
|              |     - Màu sắc và loại                           |
|              |     - Giá bán và số lượng tồn kho               |
|              |     - Trạng thái (Đã ẩn)                       |
|              |     - Thời gian ẩn                              |
|              |                                                       |
|              | 5. Manager có thể:                              |
|              |     - Lọc theo danh mục sản phẩm                 |
|              |     - Lọc theo mức tồn kho                      |
|              |     - Tìm kiếm theo tên sản phẩm/SKU            |
|              |     - Xem chi tiết sản phẩm                     |
|              |                                                       |
|              | 6. Manager chọn hành động:                     |
|              |     - Cập nhật tồn kho                          |
|              |     - Hiện sản phẩm (khôi phục is_active = 1)   |
|              |     - Xem sản phẩm trên website                |
|              |                                                       |
|              | 7. Nếu chọn "Hiện sản phẩm":                   |
|              |     - Hiển thị modal xác nhận                   |
|              |     - Manager xác nhận khôi phục                |
|              |     - Hệ thống cập nhật is_active = 1           |
|              |     - Sản phẩm hiển thị trở lại trên website    |
|              |                                                       |
|              | 8. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 9. Hiển thị thông báo "Khôi phục thành công"    |
|              |                                                       |
|              | 10. Redirect về trang sản phẩm đã ẩn           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể khôi phục hàng loạt nhiều sản  |
| lternative** |     phẩm cùng lúc                              |
|              |                                                       |
|              | 2a. Manager có thể lọc theo lý do ẩn sản phẩm   |
|              |                                                       |
|              | 3a. Manager có thể xem lịch sử ẩn/hiện sản phẩm |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có sản phẩm nào đã ẩn                  |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có sản phẩm đã ẩn" |
|              |                                                       |
|              | 1d. Sản phẩm không tồn tại                      |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Sản phẩm không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1e. Lỗi cập nhật database                        |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi cập nhật dữ    |
|              |     liệu"                                         |
+--------------+-------------------------------------------------------+

**3.2.6. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Số lượng tồn kho:** Phải >= 0, kiểm tra constraint CHECK (stock_qty >= 0)
- **Sản phẩm hoạt động:** Chỉ cập nhật sản phẩm có is_active = 1
- **Quyền hạn:** Chỉ role MANAGER mới được quản lý tồn kho
- **Trigger cảnh báo:** Tự động tạo thông báo khi tồn kho thấp (1-5) hoặc hết hàng (0)
- **Dedupe thông báo:** Chống spam thông báo trong 12 giờ
- **Sản phẩm yêu thích:** Thông báo customer khi sản phẩm có hàng trở lại

**Bảo mật:**
- **Permission Check:** Chỉ role MANAGER mới được xem cảnh báo tồn kho
- **Audit Trail:** Ghi log mọi thay đổi tồn kho
- **Data Validation:** Kiểm tra dữ liệu đầu vào nghiêm ngặt
- **Constraint Enforcement:** Sử dụng database constraints để đảm bảo tính toàn vẹn

**Tính năng đặc biệt:**
- **Auto Stock Alerts:** Trigger tự động tạo thông báo khi tồn kho thay đổi
- **Smart Notifications:** Thông báo thông minh với dedupe và chống spam
- **Wishlist Integration:** Thông báo customer khi sản phẩm yêu thích có hàng
- **Real-time Updates:** Cập nhật tồn kho real-time
- **Bulk Operations:** Xử lý hàng loạt nhiều sản phẩm
- **Stock Forecasting:** Dự báo hết hàng dựa trên lịch sử bán
- **Hidden Products Management:** Quản lý sản phẩm đã ẩn với khả năng khôi phục
- **Advanced Search:** Tìm kiếm thông minh theo tên, SKU, màu sắc, loại
- **Responsive Dashboard:** Giao diện responsive với KPI cards và thống kê trực quan
- **Modal Confirmations:** Xác nhận ẩn/hiện sản phẩm với modal đẹp mắt
- **Currency Formatting:** Hiển thị giá tiền chuẩn VNĐ (₫)
- **Pagination & Filtering:** Phân trang và lọc dữ liệu mượt mà

**Quy trình nghiệp vụ:**
1. **Cảnh báo tự động:** Trigger tạo thông báo khi tồn kho thấp/hết hàng
2. **Quản lý thủ công:** Manager cập nhật tồn kho và xử lý cảnh báo
3. **Báo cáo tổng quan:** Dashboard hiển thị tình trạng tồn kho với KPI cards
4. **Xử lý sản phẩm:** Ẩn/hiện sản phẩm dựa trên tình trạng tồn kho
5. **Quản lý sản phẩm ẩn:** Xem và khôi phục sản phẩm đã ẩn
6. **Tìm kiếm và lọc:** Tìm kiếm thông minh và lọc dữ liệu
7. **Thông báo khách hàng:** Tự động thông báo khi sản phẩm có hàng trở lại
8. **Audit trail:** Ghi log đầy đủ mọi thao tác quản lý tồn kho

**Tiêu chí chấp nhận:**
1. Manager có thể xem danh sách cảnh báo tồn kho thấp/hết hàng
2. Manager có thể cập nhật số lượng tồn kho cho sản phẩm
3. Manager có thể xem báo cáo tổng quan về tình trạng tồn kho
4. Manager có thể quản lý và xử lý sản phẩm tồn kho thấp
5. Manager có thể quản lý sản phẩm đã ẩn và khôi phục chúng
6. Hệ thống tự động tạo cảnh báo khi tồn kho thay đổi
7. Thông báo được gửi đến Manager/Admin khi cần thiết
8. Customer được thông báo khi sản phẩm yêu thích có hàng trở lại
9. Validation đầy đủ dữ liệu đầu vào
10. Ghi log đầy đủ mọi thao tác cập nhật tồn kho
11. Hỗ trợ xử lý hàng loạt nhiều sản phẩm
12. Dashboard hiển thị thống kê tồn kho trực quan với KPI cards
13. Tìm kiếm thông minh theo tên, SKU, màu sắc, loại sản phẩm
14. Phân trang và lọc dữ liệu mượt mà
15. Giao diện responsive trên mọi thiết bị
16. Hiển thị giá tiền chuẩn VNĐ (₫)
17. Modal xác nhận đẹp mắt cho các thao tác quan trọng
18. Sidebar navigation với submenu mượt mà
19. Error handling robust với fallback values
20. Xuất báo cáo tồn kho ra Excel/PDF (tùy chọn)
