**3.2. Biểu đồ Use case (Use case Diagram) - Phân công đội giao hàng Manager**

**3.2.1. Phân công đội giao hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-DVL-AssignDeliveryTeam                        |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Phân công đội giao hàng                              |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager phân công đơn hàng cho đội giao hàng |
|              | phù hợp theo khu vực địa chỉ giao                    |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                      |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                |
| conditions** | 2. Tài khoản Manager có role MANAGER                 |
|              | 3. Đơn hàng đang ở trạng thái CONFIRMED              |
|              | 4. Đơn hàng chưa có OrderDelivery record            |
|              | 5. Địa chỉ giao hàng đã được map vào zone            |
|              | 6. Có ít nhất 1 đội giao hàng hoạt động trong zone   |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, đơn hàng được phân công cho đội  |
| conditions** |     giao, trạng thái chuyển thành DISPATCHED, hệ     |
|              |     thống ghi log và thông báo cho đội giao          |
|              |                                                       |
|              | -   Nếu thất bại, đơn hàng không được phân công,     |
|              |     hiển thị thông báo lỗi.                         |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"        |
| Flow**       |                                                       |
|              | 2. Manager tìm kiếm đơn hàng cần phân công          |
|              |                                                       |
|              | 3. Manager chọn đơn hàng có trạng thái CONFIRMED    |
|              |                                                       |
|              | 4. Manager nhấn nút "Phân công đội giao"             |
|              |                                                       |
|              | 5. Hệ thống hiển thị thông tin đơn hàng:            |
|              |     - Thông tin khách hàng                         |
|              |     - Địa chỉ giao hàng                             |
|              |     - Danh sách sản phẩm                           |
|              |     - Tổng giá trị đơn hàng                         |
|              |                                                       |
|              | 6. Hệ thống tự động gợi ý đội giao phù hợp:        |
|              |     - Xác định zone từ địa chỉ giao hàng           |
|              |     - Hiển thị danh sách đội giao trong zone        |
|              |     - Chỉ hiển thị đội giao hoạt động              |
|              |                                                       |
|              | 7. Manager chọn đội giao hàng:                     |
|              |     - Chọn từ danh sách đội giao trong zone        |
|              |                                                       |
|              | 8. Manager nhập thông tin bổ sung (tùy chọn):      |
|              |     - Ghi chú đặc biệt                             |
|              |                                                       |
|              | 9. Manager nhấn "Xác nhận phân công"               |
|              |                                                       |
|              | 10. Hệ thống validate thông tin:                   |
|              |     - Kiểm tra đơn hàng ở trạng thái CONFIRMED     |
|              |     - Kiểm tra đơn hàng chưa có OrderDelivery       |
|              |     - Kiểm tra đội giao có hoạt động               |
|              |     - Kiểm tra đội giao có phụ trách zone           |
|              |                                                       |
|              | 11. Hệ thống tạo OrderDelivery record:             |
|              |     - Gán delivery_team_id                        |
|              |     - Set status = 'IN_TRANSIT'                   |
|              |     - Lưu ghi chú                                 |
|              |                                                       |
|              | 12. Hệ thống cập nhật trạng thái đơn hàng:         |
|              |     - Orders.status = 'DISPATCHED'                |
|              |     - Ghi OrderStatusHistory                       |
|              |                                                       |
|              | 13. Hệ thống tạo DeliveryHistory:                  |
|              |     - Log trạng thái 'IN_TRANSIT'                |
|              |     - Ghi chú phân công                           |
|              |                                                       |
|              | 14. Hệ thống ghi log hoạt động                    |
|              |                                                       |
|              | 15. Hiển thị thông báo "Phân công thành công"     |
|              |                                                       |
|              | 16. Redirect về trang quản lý đơn hàng           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể thay đổi đội giao nếu chưa      |
| lternative** |     bắt đầu giao hàng                              |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Đơn hàng không ở trạng thái CONFIRMED           |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Chỉ phân công đơn      |
|              |     đã xác nhận"                                   |
|              |                                                       |
|              | 1d. Không có đội giao phù hợp trong zone           |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Không có đội giao     |
|              |     phù hợp cho khu vực này"                      |
|              |                                                       |
|              | 1e. Địa chỉ giao hàng chưa được map vào zone       |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Địa chỉ chưa được    |
|              |     map vào khu vực giao hàng"                    |
|              |                                                       |
|              | 1f. Đơn hàng đã được phân công trước đó            |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Đơn hàng đã được      |
|              |     phân công"                                     |
|              |                                                       |
|              | 1g. Đội giao được chọn không hoạt động             |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Đội giao không       |
|              |     hoạt động"                                     |
|              |                                                       |
|              | 1h. Đội giao không phụ trách zone của địa chỉ      |
|              |                                                       |
|              | 2h. Hệ thống hiện thông báo "Đội giao không phụ   |
|              |     trách khu vực này"                            |
|              |                                                       |
|              | 1i. Mất kết nối database                         |
|              |                                                       |
|              | 2i. Hệ thống hiện thông báo "Lỗi hệ thống, vui    |
|              |     lòng thử lại"                                  |
|              |                                                       |
|              | 1j. Lỗi gửi thông báo cho đội giao               |
|              |                                                       |
|              | 2j. Hệ thống vẫn phân công thành công nhưng báo    |
|              |     "Lỗi gửi thông báo"                           |
+--------------+-------------------------------------------------------+

**3.2.2. Xem danh sách đơn hàng cần phân công**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-DVL-ViewPendingOrders                        |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem danh sách đơn hàng cần phân công                 |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách đơn hàng đã xác nhận  |
|              | nhưng chưa được phân công giao hàng                  |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                      |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE         |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách đơn hàng     |
| conditions** |     cần phân công                                    |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.           |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"        |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Cần phân công"                 |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đơn hàng:           |
|              |     - Đơn hàng có status = 'CONFIRMED'            |
|              |     - Chưa có OrderDelivery record                 |
|              |     - Sắp xếp theo thời gian tạo (mới nhất)        |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi đơn:       |
|              |     - Mã đơn hàng                                   |
|              |     - Tên khách hàng                               |
|              |     - Địa chỉ giao hàng                            |
|              |     - Tổng giá trị                                 |
|              |     - Thời gian tạo                                |
|              |     - Zone giao hàng                               |
|              |                                                       |
|              | 5. Manager có thể:                                |
|              |     - Lọc theo zone                                 |
|              |     - Tìm kiếm theo mã đơn/tên khách               |
|              |     - Sắp xếp theo tiêu chí khác                   |
|              |                                                       |
|              | 6. Manager có thể chọn đơn để phân công            |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết đơn hàng           |
| lternative** |                                                       |
|              | 2a. Manager có thể phân công trực tiếp từ danh sách |
|              |                                                       |
|              | 3a. Manager có thể xuất danh sách ra Excel        |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có đơn hàng cần phân công               |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có đơn hàng nào"     |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"           |
+--------------+-------------------------------------------------------+

**3.2.3. Thay đổi đội giao hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-DVL-ChangeDeliveryTeam                       |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Thay đổi đội giao hàng                              |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager thay đổi đội giao hàng cho đơn đã   |
|              | được phân công (chỉ khi chưa bắt đầu giao)          |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                     |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                 |
|              | 3. Đơn hàng đã được phân công                       |
|              | 4. OrderDelivery status = 'IN_TRANSIT'              |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, đội giao được thay đổi, hệ     |
| conditions** |     thống ghi log và thông báo                     |
|              |                                                       |
|              | -   Nếu thất bại, đội giao không thay đổi, hiển    |
|              |     thị thông báo lỗi.                            |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager tìm kiếm đơn hàng đã phân công           |
|              |                                                       |
|              | 3. Manager chọn đơn hàng có OrderDelivery          |
|              |                                                       |
|              | 4. Manager nhấn nút "Thay đổi đội giao"           |
|              |                                                       |
|              | 5. Hệ thống hiển thị thông tin hiện tại:          |
|              |     - Đội giao hiện tại                           |
|              |     - Trạng thái giao hàng                         |
|              |     - Thời gian phân công                          |
|              |                                                       |
|              | 6. Hệ thống hiển thị danh sách đội giao khác:      |
|              |     - Các đội giao trong cùng zone                 |
|              |     - Các đội giao khác zone (nếu có quyền)       |
|              |                                                       |
|              | 7. Manager chọn đội giao mới                      |
|              |                                                       |
|              | 8. Manager nhập lý do thay đổi (bắt buộc)        |
|              |                                                       |
|              | 9. Manager nhấn "Xác nhận thay đổi"               |
|              |                                                       |
|              | 10. Hệ thống validate:                           |
|              |     - Kiểm tra đơn chưa bắt đầu giao               |
|              |     - Kiểm tra đội giao mới có hoạt động          |
|              |     - Kiểm tra đội giao mới có phụ trách zone     |
|              |                                                       |
|              | 11. Hệ thống cập nhật OrderDelivery:              |
|              |     - Thay đổi delivery_team_id                   |
|              |     - Cập nhật ghi chú                           |
|              |                                                       |
|              | 12. Hệ thống ghi DeliveryHistory:                 |
|              |     - Log thay đổi đội giao                       |
|              |     - Ghi lý do thay đổi                          |
|              |                                                       |
|              | 13. Hệ thống thông báo cho đội giao cũ:          |
|              |     - Hủy phân công đơn hàng                     |
|              |                                                       |
|              | 14. Hệ thống thông báo cho đội giao mới:          |
|              |     - Phân công đơn hàng mới                      |
|              |                                                       |
|              | 15. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 16. Hiển thị thông báo "Thay đổi thành công"     |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể hủy thay đổi                   |
| lternative** |                                                       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. OrderDelivery status không phải 'IN_TRANSIT'    |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Không thể thay đổi    |
|              |     đội giao khi đã bắt đầu giao hàng"            |
|              |                                                       |
|              | 1d. Đội giao mới không hoạt động                  |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Đội giao không       |
|              |     hoạt động"                                     |
|              |                                                       |
|              | 1e. Đội giao mới không phụ trách zone             |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Đội giao không phụ   |
|              |     trách khu vực này"                            |
|              |                                                       |
|              | 1f. Không nhập lý do thay đổi                    |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Vui lòng nhập lý do  |
|              |     thay đổi"                                      |
|              |                                                       |
|              | 1g. Mất kết nối database                         |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Lỗi hệ thống, vui    |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.4. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Trạng thái đơn hàng:** Chỉ phân công đơn CONFIRMED
- **OrderDelivery:** Đơn hàng chưa có OrderDelivery record
- **Đội giao hàng:** Phải hoạt động (isActive = true)
- **Zone validation:** Đội giao phải phụ trách zone của địa chỉ
- **Địa chỉ mapping:** Tỉnh/thành phải có trong ProvinceZone
- **Thay đổi đội:** Chỉ khi OrderDelivery status = 'IN_TRANSIT'

**Bảo mật:**
- **Permission Check:** Chỉ role MANAGER mới được phân công
- **Audit Trail:** Ghi log mọi thay đổi phân công
- **Zone Validation:** Kiểm tra đội giao có phụ trách zone
- **Status Validation:** Kiểm tra trạng thái đơn hàng và OrderDelivery

**Tính năng đặc biệt:**
- **Auto Zone Detection:** Tự động xác định zone từ địa chỉ
- **Team Filtering:** Chỉ hiển thị đội giao trong zone
- **Status Tracking:** Theo dõi trạng thái OrderDelivery
- **Change Tracking:** Theo dõi lịch sử thay đổi đội giao

**Quy trình nghiệp vụ:**
1. **Phân công thủ công:** Manager chọn đội cụ thể
2. **Thay đổi đội:** Chỉ khi OrderDelivery status = 'IN_TRANSIT'
3. **Validation nghiêm ngặt:** Kiểm tra zone và trạng thái
4. **Log tracking:** Ghi log mọi thay đổi

**Tiêu chí chấp nhận:**
1. Manager có thể phân công đơn hàng cho đội giao phù hợp
2. Hệ thống tự động gợi ý đội giao theo zone
3. Validation đầy đủ trạng thái và quyền hạn
4. Có thể thay đổi đội giao khi cần thiết
5. Ghi log đầy đủ mọi thao tác
6. Không thể phân công đơn đã được phân công
7. Không thể thay đổi đội khi đã bắt đầu giao
8. Thông báo lỗi rõ ràng và hữu ích
