**3.2. Biểu đồ Use case (Use case Diagram) - Quản lý đơn hàng Manager**

**3.2.1. Xác nhận đơn hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-ConfirmOrder                              |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xác nhận đơn hàng                                    |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xác nhận đơn hàng từ trạng thái     |
|              | PENDING sang CONFIRMED                                |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                      |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                |
| conditions** | 2. Tài khoản Manager có role MANAGER                 |
|              | 3. Đơn hàng đang ở trạng thái PENDING                |
|              |    (KHÔNG cần validate payment_status)               |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, đơn hàng chuyển sang CONFIRMED, |
| conditions** |     hệ thống ghi log và thông báo cho customer       |
|              |                                                       |
|              | -   Nếu thất bại, đơn hàng vẫn ở PENDING, hiển thị  |
|              |     thông báo lỗi.                                   |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"        |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Chờ xác nhận" từ navigation    |
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đơn hàng PENDING:    |
|              |     - Đơn hàng có status = 'PENDING'               |
|              |     - Sắp xếp theo thời gian tạo (mới nhất)        |
|              |                                                       |
|              | 4. Manager chọn đơn hàng cần xác nhận               |
|              |                                                       |
|              | 5. Hệ thống hiển thị thông tin chi tiết đơn hàng:   |
|              |     - Thông tin khách hàng                         |
|              |     - Địa chỉ giao hàng                             |
|              |     - Danh sách sản phẩm và số lượng               |
|              |     - Phương thức thanh toán                       |
|              |     - Trạng thái thanh toán                        |
|              |     - Tổng giá trị đơn hàng                         |
|              |                                                       |
|              | 6. Manager nhấn nút "Xác nhận đơn hàng"            |
|              |                                                       |
|              | 7. Hệ thống validate điều kiện:                    |
|              |     - Kiểm tra đơn hàng ở trạng thái PENDING       |
|              |     - GHI CHÚ: Manager có thể xác nhận đơn TRƯỚC   |
|              |       khi khách thanh toán (để giữ hàng)          |
|              |     - Webhook thanh toán sẽ cập nhật payment_status |
|              |       sau đó                                       |
|              |                                                       |
|              | 8. Hệ thống gọi stored procedure sp_ConfirmOrder:   |
|              |     - Cập nhật Orders.status = 'CONFIRMED'         |
|              |     - Ghi OrderStatusHistory với note "Xác nhận đơn" |
|              |                                                       |
|              | 9. Hệ thống gửi thông báo cho customer:            |
|              |     - Thông báo "Đơn hàng đã được xác nhận"        |
|              |     - Thông tin chi tiết đơn hàng                 |
|              |                                                       |
|              | 10. Hệ thống ghi log hoạt động                     |
|              |                                                       |
|              | 11. Hiển thị thông báo "Xác nhận thành công"      |
|              |                                                       |
|              | 12. Redirect về trang quản lý đơn hàng            |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xác nhận hàng loạt nhiều đơn    |
| lternative** |     cùng lúc                                      |
|              |                                                       |
|              | 2a. Manager có thể thêm ghi chú khi xác nhận       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Đơn hàng không ở trạng thái PENDING            |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Chỉ xác nhận được    |
|              |     đơn hàng ở trạng thái PENDING"                |
|              |                                                       |
|              | 1d. Mất kết nối database                         |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Lỗi hệ thống, vui    |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.2. Hủy đơn hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-CancelOrder                              |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Hủy đơn hàng                                       |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager hủy đơn hàng của customer và hoàn  |
|              | lại tồn kho                                        |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Đơn hàng đang ở trạng thái PENDING               |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, đơn hàng chuyển sang CANCELLED,|
| conditions** |     hoàn lại tồn kho, hoàn tiền (nếu đã thanh toán)|
|              |     và thông báo cho customer                       |
|              |                                                       |
|              | -   Nếu thất bại, đơn hàng vẫn ở PENDING, hiển thị |
|              |     thông báo lỗi.                                 |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Chờ xác nhận" từ navigation    |
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Manager tìm kiếm đơn hàng cần hủy               |
|              |                                                       |
|              | 4. Manager chọn đơn hàng có trạng thái PENDING     |
|              |                                                       |
|              | 5. Manager nhấn nút "Hủy đơn hàng"                |
|              |                                                       |
|              | 6. Hệ thống hiển thị popup xác nhận:              |
|              |     - Thông tin đơn hàng                           |
|              |     - Lý do hủy (bắt buộc nhập)                   |
|              |     - Cảnh báo về việc hoàn tiền                   |
|              |                                                       |
|              | 7. Manager nhập lý do hủy đơn                     |
|              |                                                       |
|              | 8. Manager nhấn "Xác nhận hủy đơn"                |
|              |                                                       |
|              | 9. Hệ thống validate điều kiện:                   |
|              |     - Kiểm tra đơn hàng ở trạng thái PENDING      |
|              |     - Kiểm tra lý do hủy không rỗng               |
|              |                                                       |
|              | 10. Hệ thống gọi stored procedure sp_CancelOrder:   |
|              |     - Kiểm tra payment_method và payment_status    |
|              |     - Nếu online đã PAID: thực hiện refund        |
|              |     - Cộng lại tồn kho cho các variant            |
|              |     - Cập nhật Orders.status = 'CANCELLED'        |
|              |     - Ghi OrderStatusHistory với lý do hủy         |
|              |                                                       |
|              | 11. Hệ thống gửi thông báo cho customer:          |
|              |     - Thông báo "Đơn hàng đã bị hủy"             |
|              |     - Lý do hủy từ manager                        |
|              |                                                       |
|              | 12. Hệ thống ghi log hoạt động                    |
|              |                                                       |
|              | 13. Hiển thị thông báo "Hủy đơn thành công"      |
|              |                                                       |
|              | 14. Redirect về trang quản lý đơn hàng           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể hủy hàng loạt nhiều đơn         |
| lternative** |     cùng lúc                                      |
|              |                                                       |
|              | 2a. Manager có thể hủy đơn từ danh sách           |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Đơn hàng không ở trạng thái PENDING           |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Chỉ hủy được đơn     |
|              |     PENDING"                                       |
|              |                                                       |
|              | 1d. Không nhập lý do hủy                         |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Vui lòng nhập lý do  |
|              |     hủy đơn"                                       |
|              |                                                       |
|              | 1e. Lỗi refund cho đơn online đã thanh toán       |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi hoàn tiền, vui   |
|              |     lòng liên hệ kỹ thuật"                       |
|              |                                                       |
|              | 1f. Mất kết nối database                         |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui    |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.3. Xem danh sách đơn hàng đang giao**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-ViewInDelivery                           |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem danh sách đơn hàng đang giao                   |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách đơn hàng đang trong |
|              | quá trình giao hàng (CONFIRMED, DISPATCHED)         |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE       |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách đơn hàng   |
| conditions** |     đang giao                                     |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Đang giao" từ navigation       |
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đơn hàng:          |
|              |     - Đơn hàng có status = 'CONFIRMED' hoặc        |
|              |       'DISPATCHED'                                |
|              |     - Sắp xếp theo thời gian cập nhật (mới nhất)  |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi đơn:      |
|              |     - Mã đơn hàng                                  |
|              |     - Tên khách hàng                              |
|              |     - Địa chỉ giao hàng                           |
|              |     - Tổng giá trị                                |
|              |     - Trạng thái giao hàng                        |
|              |     - Phương thức thanh toán                      |
|              |     - Trạng thái thanh toán                       |
|              |                                                       |
|              | 5. Manager có thể:                               |
|              |     - Lọc theo khoảng thời gian                   |
|              |     - Tìm kiếm theo mã đơn/tên khách              |
|              |     - Sắp xếp theo: Ngày tạo, Ngày cập nhật,     |
|              |       Tổng tiền, Mã đơn                          |
|              |     - Chuyển nhanh giữa các tab quản lý          |
|              |                                                       |
|              | 6. Manager có thể xem chi tiết đơn hàng           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết đơn hàng          |
| lternative** |                                                       |
|              | 2a. Manager có thể xem lịch sử trạng thái đơn     |
|              |                                                       |
|              | 3a. Manager có thể xem thông tin giao hàng        |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có đơn hàng đang giao                  |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có đơn hàng nào"    |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.4. Xem danh sách đơn hàng hoàn thành**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-ViewCompleted                           |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem danh sách đơn hàng hoàn thành                  |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách đơn hàng đã giao     |
|              | thành công (DELIVERED)                              |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE       |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách đơn hàng   |
| conditions** |     hoàn thành                                     |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Đã hoàn thành" từ navigation  |
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đơn hàng:          |
|              |     - Đơn hàng có status = 'DELIVERED'            |
|              |     - Các đơn đã từng yêu cầu trả hàng nhưng bị   |
|              |       từ chối (return_status = 'REJECTED') vẫn    |
|              |       hiển thị trong danh sách này                |
|              |     - Sắp xếp theo thời gian giao (mới nhất)      |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi đơn:      |
|              |     - Mã đơn hàng                                  |
|              |     - Tên khách hàng                              |
|              |     - Địa chỉ giao hàng                           |
|              |     - Tổng giá trị                                |
|              |     - Thời gian giao                              |
|              |     - Phương thức thanh toán                      |
|              |     - Trạng thái thanh toán                       |
|              |                                                       |
|              | 5. Manager có thể:                               |
|              |     - Lọc theo khoảng thời gian                   |
|              |     - Tìm kiếm theo mã đơn/tên khách              |
|              |     - Sắp xếp theo: Ngày tạo, Ngày cập nhật,     |
|              |       Tổng tiền, Mã đơn                          |
|              |     - Chuyển nhanh giữa các tab quản lý          |
|              |                                                       |
|              | 6. Manager có thể xem chi tiết đơn hàng           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết đơn hàng          |
| lternative** |                                                       |
|              | 2a. Manager có thể xem lịch sử trạng thái đơn     |
|              |                                                       |
|              | 3a. Manager có thể xem thông tin giao hàng        |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có đơn hàng hoàn thành                 |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có đơn hàng nào"    |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.5. Xem danh sách đơn hàng đã hủy**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-ViewCancelled                           |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem danh sách đơn hàng đã hủy                     |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách đơn hàng đã bị hủy  |
|              | (CANCELLED)                                        |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE       |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách đơn hàng   |
| conditions** |     đã hủy                                        |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Đã hủy" từ navigation         |
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đơn hàng:          |
|              |     - Đơn hàng có status = 'CANCELLED'            |
|              |     - Sắp xếp theo thời gian hủy (mới nhất)       |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi đơn:      |
|              |     - Mã đơn hàng                                  |
|              |     - Tên khách hàng                              |
|              |     - Địa chỉ giao hàng                           |
|              |     - Tổng giá trị                                |
|              |     - Thời gian hủy                               |
|              |     - Lý do hủy                                    |
|              |     - Phương thức thanh toán                      |
|              |     - Trạng thái hoàn tiền                       |
|              |                                                       |
|              | 5. Manager có thể:                               |
|              |     - Lọc theo khoảng thời gian                   |
|              |     - Tìm kiếm theo mã đơn/tên khách              |
|              |     - Sắp xếp theo: Ngày tạo, Ngày cập nhật,     |
|              |       Tổng tiền, Mã đơn                          |
|              |     - Chuyển nhanh giữa các tab quản lý          |
|              |                                                       |
|              | 6. Manager có thể xem chi tiết đơn hàng           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết đơn hàng          |
| lternative** |                                                       |
|              | 2a. Manager có thể xem lịch sử trạng thái đơn     |
|              |                                                       |
|              | 3a. Manager có thể xem thông tin hoàn tiền       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có đơn hàng đã hủy                     |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có đơn hàng nào"    |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.6. Duyệt yêu cầu trả hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-ApproveReturn                           |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Duyệt yêu cầu trả hàng                             |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager duyệt yêu cầu trả hàng của        |
|              | customer và phân công đội thu hồi                  |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Đơn hàng có return_status = 'REQUESTED'          |
|              | 4. Đơn hàng có status = 'DELIVERED'                 |
|              | 5. Trong thời hạn 30 ngày sau giao hàng            |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, return_status = 'APPROVED',    |
| conditions** |     OrderDelivery chuyển sang RETURN_PICKUP,       |
|              |     thông báo cho customer và đội giao             |
|              |                                                       |
|              | -   Nếu thất bại, return_status không thay đổi,    |
|              |     hiển thị thông báo lỗi.                       |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Yêu cầu hoàn trả" từ navigation|
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách yêu cầu:          |
|              |     - Đơn hàng có return_status = 'REQUESTED'     |
|              |     - Sắp xếp theo thời gian yêu cầu (mới nhất)   |
|              |                                                       |
|              | 4. Manager chọn yêu cầu cần duyệt                 |
|              |                                                       |
|              | 5. Hệ thống hiển thị thông tin chi tiết:         |
|              |     - Thông tin đơn hàng                          |
|              |     - Lý do trả hàng từ customer                  |
|              |     - Thời gian giao hàng                         |
|              |     - Số ngày từ khi giao                        |
|              |     - Danh sách sản phẩm cần trả                  |
|              |                                                       |
|              | 6. Manager nhấn nút "Duyệt trả hàng"             |
|              |                                                       |
|              | 7. Hệ thống hiển thị form duyệt:                 |
|              |     - Ghi chú duyệt (tùy chọn)                   |
|              |     - Chọn đội giao thu hồi (nếu chưa có)        |
|              |                                                       |
|              | 8. Manager nhập thông tin và nhấn "Xác nhận"     |
|              |                                                       |
|              | 9. Hệ thống validate điều kiện:                  |
|              |     - Kiểm tra return_status = 'REQUESTED'        |
|              |     - Kiểm tra đơn hàng đã DELIVERED              |
|              |     - Kiểm tra trong thời hạn 30 ngày             |
|              |                                                       |
|              | 10. Hệ thống gọi stored procedure sp_ApproveReturn:|
|              |     - Cập nhật return_status = 'APPROVED'         |
|              |     - Thêm ghi chú vào return_note               |
|              |     - Tạo/cập nhật OrderDelivery = 'RETURN_PICKUP' |
|              |     - Ghi DeliveryHistory                        |
|              |     - Ghi OrderStatusHistory                      |
|              |                                                       |
|              | 11. Hệ thống gửi thông báo cho customer:         |
|              |     - Thông báo "Yêu cầu trả hàng được duyệt"    |
|              |     - Thông tin đội giao sẽ liên hệ              |
|              |                                                       |
|              | 12. Hệ thống gửi thông báo cho đội giao:         |
|              |     - Thông báo "Yêu cầu thu hồi"               |
|              |     - Thông tin đơn hàng cần thu hồi            |
|              |                                                       |
|              | 13. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 14. Hiển thị thông báo "Duyệt thành công"       |
|              |                                                       |
|              | 15. Redirect về trang yêu cầu trả hàng          |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể duyệt hàng loạt nhiều yêu cầu  |
| lternative** |     cùng lúc                                      |
|              |                                                       |
|              | 2a. Manager có thể chọn đội giao khác nếu cần     |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Yêu cầu không ở trạng thái REQUESTED          |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Yêu cầu không hợp    |
|              |     lệ để duyệt"                                  |
|              |                                                       |
|              | 1d. Đơn hàng chưa được giao (không DELIVERED)    |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Đơn hàng chưa được  |
|              |     giao, không thể duyệt trả"                   |
|              |                                                       |
|              | 1e. Quá thời hạn 30 ngày sau giao hàng           |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Quá thời hạn trả    |
|              |     hàng (30 ngày)"                               |
|              |                                                       |
|              | 1f. Không có đội giao phù hợp                   |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Không có đội giao   |
|              |     phù hợp, vui lòng tạo đội giao trước"       |
|              |                                                       |
|              | 1g. Mất kết nối database                         |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.7. Từ chối yêu cầu trả hàng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-RejectReturn                            |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Từ chối yêu cầu trả hàng                           |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager từ chối yêu cầu trả hàng của      |
|              | customer với lý do cụ thể                          |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Đơn hàng có return_status = 'REQUESTED'          |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, return_status = 'REJECTED',    |
| conditions** |     thông báo cho customer với lý do từ chối       |
|              |                                                       |
|              | -   Nếu thất bại, return_status không thay đổi,    |
|              |     hiển thị thông báo lỗi.                       |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Yêu cầu hoàn trả" từ navigation|
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Manager chọn yêu cầu cần từ chối               |
|              |                                                       |
|              | 4. Manager nhấn nút "Từ chối trả hàng"            |
|              |                                                       |
|              | 5. Hệ thống hiển thị form từ chối:                |
|              |     - Lý do từ chối (bắt buộc nhập)              |
|              |     - Ghi chú bổ sung (tùy chọn)                 |
|              |                                                       |
|              | 6. Manager nhập lý do từ chối                     |
|              |                                                       |
|              | 7. Manager nhấn "Xác nhận từ chối"               |
|              |                                                       |
|              | 8. Hệ thống validate điều kiện:                  |
|              |     - Kiểm tra return_status = 'REQUESTED'        |
|              |     - Kiểm tra lý do từ chối không rỗng          |
|              |                                                       |
|              | 9. Hệ thống gọi stored procedure sp_RejectReturn: |
|              |     - Cập nhật return_status = 'REJECTED'         |
|              |     - Thêm lý do vào return_note                 |
|              |     - Ghi OrderStatusHistory                      |
|              |                                                       |
|              | 10. Hệ thống gửi thông báo cho customer:         |
|              |     - Thông báo "Yêu cầu trả hàng bị từ chối"    |
|              |     - Lý do từ chối từ manager                   |
|              |                                                       |
|              | 11. Hệ thống ghi log hoạt động                   |
|              |                                                       |
|              | 12. Hiển thị thông báo "Từ chối thành công"      |
|              |                                                       |
|              | 13. Redirect về trang yêu cầu trả hàng          |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể từ chối hàng loạt nhiều yêu cầu|
| lternative** |     cùng lúc                                      |
|              |                                                       |
|              | 2a. Manager có thể xem lại yêu cầu trước khi từ chối|
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Yêu cầu không ở trạng thái REQUESTED          |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Yêu cầu không hợp    |
|              |     lệ để từ chối"                                |
|              |                                                       |
|              | 1d. Không nhập lý do từ chối                     |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Vui lòng nhập lý do  |
|              |     từ chối"                                      |
|              |                                                       |
|              | 1e. Mất kết nối database                         |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi hệ thống, vui    |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.8. Xem danh sách đơn hàng đã hoàn trả**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ORD-ViewReturned                           |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem danh sách đơn hàng đã hoàn trả                |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách đơn hàng đã hoàn   |
|              | trả thành công (return_status = 'PROCESSED')       |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE       |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách đơn hàng   |
| conditions** |     đã hoàn trả                                   |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý đơn hàng"       |
| Flow**       |                                                       |
|              | 2. Manager chọn tab "Đã hoàn trả" từ navigation     |
|              |    tabs: Chờ xác nhận, Đang giao, Đã hoàn thành,     |
|              |    Yêu cầu hoàn trả, Đã hoàn trả, Đã hủy             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách đơn hàng:          |
|              |     - Đơn hàng có status = 'RETURNED' và          |
|              |       return_status = 'PROCESSED'                 |
|              |     - Sắp xếp theo thời gian hoàn trả (mới nhất)  |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi đơn:      |
|              |     - Mã đơn hàng                                  |
|              |     - Tên khách hàng                              |
|              |     - Địa chỉ giao hàng                           |
|              |     - Tổng giá trị                                |
|              |     - Thời gian hoàn trả                          |
|              |     - Lý do trả hàng                              |
|              |     - Phương thức thanh toán                      |
|              |     - Trạng thái hoàn tiền                       |
|              |                                                       |
|              | 5. Manager có thể:                               |
|              |     - Lọc theo khoảng thời gian                   |
|              |     - Tìm kiếm theo mã đơn/tên khách              |
|              |     - Sắp xếp theo: Ngày tạo, Ngày cập nhật,     |
|              |       Tổng tiền, Mã đơn                          |
|              |     - Chuyển nhanh giữa các tab quản lý          |
|              |                                                       |
|              | 6. Manager có thể xem chi tiết đơn hàng           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết đơn hàng          |
| lternative** |                                                       |
|              | 2a. Manager có thể xem lịch sử trạng thái đơn     |
|              |                                                       |
|              | 3a. Manager có thể xem thông tin hoàn tiền       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có đơn hàng đã hoàn trả               |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Không có đơn hàng nào"    |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
+--------------+-------------------------------------------------------+

**3.2.9. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Trạng thái đơn hàng:** Chỉ xác nhận đơn PENDING, chỉ hủy đơn PENDING
- **Thanh toán:** Manager có thể xác nhận đơn TRƯỚC khi khách thanh toán (để giữ hàng), webhook thanh toán sẽ cập nhật payment_status sau
- **Trả hàng:** Chỉ duyệt/từ chối yêu cầu REQUESTED
- **Thời hạn trả hàng:** Trong vòng 30 ngày sau giao hàng
- **Đội giao:** Phải có đội giao phù hợp để thu hồi
- **Lý do:** Bắt buộc nhập lý do khi hủy đơn hoặc từ chối trả hàng
 - **Danh sách hoàn trả:** Hiển thị đơn có status = 'RETURNED' và return_status = 'PROCESSED'

**Bảo mật:**
- **Permission Check:** Chỉ role MANAGER mới được quản lý đơn hàng
- **Audit Trail:** Ghi log mọi thay đổi trạng thái đơn hàng
- **Status Validation:** Kiểm tra trạng thái đơn hàng nghiêm ngặt
- **Payment Validation:** Kiểm tra trạng thái thanh toán
- **Return Policy:** Tuân thủ chính sách trả hàng 30 ngày

**Tính năng đặc biệt:**
- **Auto Refund:** Tự động hoàn tiền khi hủy đơn online đã thanh toán
- **Stock Management:** Tự động hoàn lại tồn kho khi hủy đơn
- **Notification System:** Thông báo tự động cho customer và đội giao
- **Return Tracking:** Theo dõi quy trình trả hàng từ yêu cầu đến hoàn tất
- **Bulk Operations:** Xử lý hàng loạt nhiều đơn hàng

**Quy trình nghiệp vụ:**
1. **Xác nhận đơn:** PENDING → CONFIRMED (không cần validate payment_status)
2. **Hủy đơn:** PENDING → CANCELLED (hoàn tiền + hoàn kho)
3. **Trả hàng:** REQUESTED → APPROVED/REJECTED → PROCESSED
4. **Validation nghiêm ngặt:** Kiểm tra trạng thái đơn hàng
5. **Thông báo tức thì:** Gửi thông báo cho các bên liên quan
6. **Sắp xếp thông minh:** Hỗ trợ sort theo tổng tiền (in-memory)

**Navigation Tabs:**
- **Chờ xác nhận:** Đơn hàng có status = 'PENDING'
- **Đang giao:** Đơn hàng có status = 'CONFIRMED' hoặc 'DISPATCHED'
- **Đã hoàn thành:** Đơn hàng có status = 'DELIVERED'
- **Yêu cầu hoàn trả:** Đơn hàng có return_status = 'REQUESTED'
- **Đã hoàn trả:** Đơn hàng có return_status = 'PROCESSED'
  (và Orders.status = 'RETURNED')
- **Đã hủy:** Đơn hàng có status = 'CANCELLED'

**Tiêu chí chấp nhận:**
1. Manager có thể xác nhận đơn hàng PENDING (không cần chờ thanh toán)
2. Manager có thể hủy đơn hàng và hoàn tiền/hoàn kho
3. Manager có thể xem danh sách đơn hàng theo từng trạng thái
4. Manager có thể duyệt/từ chối yêu cầu trả hàng
5. Validation đầy đủ trạng thái đơn hàng
6. Thông báo tự động cho customer và đội giao
7. Ghi log đầy đủ mọi thao tác
8. Tuân thủ chính sách trả hàng 30 ngày
9. Hỗ trợ sắp xếp theo tổng tiền, ngày tạo, ngày cập nhật
10. Không thể hủy đơn đã xác nhận
11. Navigation nhanh giữa các tab quản lý đơn hàng
12. Auto-expand sidebar menu khi ở trang quản lý đơn hàng
13. Hỗ trợ tìm kiếm và lọc trong tất cả các tab
14. Hiển thị thông tin chi tiết cho từng loại đơn hàng