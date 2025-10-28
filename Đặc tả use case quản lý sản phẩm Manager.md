**3.2. Biểu đồ Use case (Use case Diagram) - Quản lý sản phẩm Manager**

**3.2.1. Tạo sản phẩm mới**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-PRD-CreateProduct                              |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Tạo sản phẩm mới                                       |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager tạo sản phẩm mới với thông tin cơ bản |
|              | và biến thể đầu tiên                                    |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                        |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                  |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE          |
|              | 3. Đã có ít nhất 1 danh mục sản phẩm (category)       |
|              | 4. Đã có ít nhất 1 màu sắc (color)                    |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, sản phẩm được tạo với trạng thái   |
| conditions** |     ACTIVE, hệ thống ghi log                           |
|              |                                                       |
|              | -   Nếu thất bại, không tạo sản phẩm, hiển thị thông   |
|              |     báo lỗi.                                           |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý sản phẩm"          |
| Flow**       |                                                       |
|              | 2. Manager nhấn nút "Thêm sản phẩm mới"              |
|              |                                                       |
|              | 3. Hệ thống hiển thị form tạo sản phẩm                 |
|              |                                                       |
|              | 4. Manager nhập thông tin sản phẩm:                    |
|              |     - Tên sản phẩm (bắt buộc)                         |
|              |     - Mô tả sản phẩm                                   |
|              |     - Chọn danh mục (category)                        |
|              |     - Chọn bộ sưu tập (collection, tùy chọn)          |
|              |                                                       |
|              | 5. Manager tạo biến thể đầu tiên:                     |
|              |     - Chọn màu sắc                                     |
|              |     - Nhập loại/kích thước (type_name)                |
|              |     - Nhập SKU (mã sản phẩm)                          |
|              |     - Nhập giá (price)                                |
|              |     - Nhập % giảm giá (discount_percent)              |
|              |     - Nhập số lượng tồn kho (stock_qty)               |
|              |     - Chọn loại khuyến mãi (promotion_type)           |
|              |                                                       |
|              | 6. Manager upload ảnh sản phẩm cho màu đã chọn        |
|              |     (tùy chọn, có thể upload sau)                      |
|              |                                                       |
|              | 7. Manager nhấn "Tạo sản phẩm"                        |
|              |                                                       |
|              | 8. Hệ thống validate dữ liệu đầu vào                  |
|              |                                                       |
|              | 9. Hệ thống tạo slug tự động từ tên sản phẩm          |
|              |                                                       |
|              | 10. Hệ thống lưu sản phẩm và biến thể vào database   |
|              |                                                       |
|              | 11. Hệ thống upload ảnh (nếu có)                      |
|              |                                                       |
|              | 12. Hiển thị thông báo "Tạo sản phẩm thành công"      |
|              |                                                       |
|              | 13. Redirect về trang danh sách sản phẩm              |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể thêm nhiều ảnh cho cùng 1 màu      |
| lternative** |                                                       |
|              | 2a. Manager có thể bỏ qua bộ sưu tập                 |
|              |                                                       |
|              | 3a. Manager có thể tạo thêm biến thể sau khi tạo      |
|              |     sản phẩm                                           |
|              |                                                       |
|              | 4a. Manager có thể bỏ qua upload ảnh                  |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Tên sản phẩm đã tồn tại                          |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Tên sản phẩm đã tồn tại"  |
|              |                                                       |
|              | 1d. SKU đã tồn tại                                    |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Mã SKU đã tồn tại"      |
|              |                                                       |
|              | 1e. Giá sản phẩm âm hoặc không hợp lệ                |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Giá sản phẩm phải >= 0"  |
|              |                                                       |
|              | 1f. % giảm giá không hợp lệ                          |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "% giảm giá phải từ 0-100"|
|              |                                                       |
|              | 1g. Số lượng tồn kho âm                              |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Số lượng tồn kho >= 0"   |
|              |                                                       |
|              | 1h. Không chọn danh mục                               |
|              |                                                       |
|              | 2h. Hệ thống hiện thông báo "Vui lòng chọn danh mục"  |
|              |                                                       |
|              | 1i. Không chọn màu sắc                               |
|              |                                                       |
|              | 2i. Hệ thống hiện thông báo "Vui lòng chọn màu sắc"  |
|              |                                                       |
|              | 1j. File ảnh quá lớn (>2MB)                         |
|              |                                                       |
|              | 2j. Hệ thống hiện thông báo "File quá lớn, vui lòng  |
|              |     chọn file nhỏ hơn 2MB"                            |
|              |                                                       |
|              | 1k. Lỗi upload ảnh                                  |
|              |                                                       |
|              | 2k. Hệ thống hiện thông báo "Lỗi upload ảnh" nhưng    |
|              |     vẫn tạo sản phẩm thành công                       |
|              |                                                       |
|              | 1l. Mất kết nối database                             |
|              |                                                       |
|              | 2l. Hệ thống hiện thông báo "Lỗi hệ thống, vui lòng  |
|              |     thử lại"                                           |
+--------------+-------------------------------------------------------+

**3.2.2. Cập nhật sản phẩm**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-PRD-UpdateProduct                              |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Cập nhật sản phẩm                                      |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager chỉnh sửa thông tin sản phẩm đã tồn   |
|              | tại                                                   |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                        |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                  |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE          |
|              | 3. Sản phẩm tồn tại trong hệ thống                     |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, thông tin sản phẩm được cập nhật,  |
| conditions** |     hệ thống ghi log                                   |
|              |                                                       |
|              | -   Nếu thất bại, thông tin không thay đổi, hiển thị   |
|              |     thông báo lỗi.                                    |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý sản phẩm"          |
| Flow**       |                                                       |
|              | 2. Manager tìm kiếm hoặc chọn sản phẩm cần sửa       |
|              |                                                       |
|              | 3. Manager nhấn nút "Chỉnh sửa"                       |
|              |                                                       |
|              | 4. Hệ thống hiển thị form với thông tin hiện tại      |
|              |                                                       |
|              | 5. Manager chỉnh sửa thông tin:                       |
|              |     - Tên sản phẩm                                     |
|              |     - Mô tả sản phẩm                                   |
|              |     - Danh mục (category)                             |
|              |     - Bộ sưu tập (collection)                         |
|              |                                                       |
|              | 6. Manager nhấn "Lưu thay đổi"                        |
|              |                                                       |
|              | 7. Hệ thống validate dữ liệu đầu vào                |
|              |                                                       |
|              | 8. Hệ thống cập nhật thông tin vào database         |
|              |                                                       |
|              | 9. Hệ thống ghi log hoạt động                         |
|              |                                                       |
|              | 10. Hiển thị thông báo "Cập nhật thành công"         |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể thay đổi danh mục                   |
| lternative** |                                                       |
|              | 2a. Manager có thể thay đổi bộ sưu tập               |
|              |                                                       |
|              | 3a. Manager có thể cập nhật ảnh sản phẩm            |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Tên sản phẩm mới đã tồn tại (khác sản phẩm hiện   |
|              |     tại)                                               |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Tên sản phẩm đã tồn tại" |
|              |                                                       |
|              | 1d. Danh mục không tồn tại                            |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Danh mục không hợp lệ"   |
|              |                                                       |
|              | 1e. Bộ sưu tập không tồn tại                          |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Bộ sưu tập không hợp lệ" |
|              |                                                       |
|              | 1f. Mất kết nối database                             |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui lòng  |
|              |     thử lại"                                           |
+--------------+-------------------------------------------------------+

**3.2.3. Kích hoạt/vô hiệu hóa sản phẩm**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-PRD-ToggleActive                               |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Kích hoạt/vô hiệu hóa sản phẩm                        |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager ẩn/hiện sản phẩm trên website        |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                       |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                 |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE         |
|              | 3. Sản phẩm tồn tại trong hệ thống                    |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, trạng thái sản phẩm được thay đổi,|
| conditions** |     hệ thống ghi log                                  |
|              |                                                       |
|              | -   Nếu thất bại, trạng thái không thay đổi, hiển thị  |
|              |     thông báo lỗi.                                   |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý sản phẩm"         |
| Flow**       |                                                       |
|              | 2. Manager tìm kiếm hoặc chọn sản phẩm               |
|              |                                                       |
|              | 3. Manager nhấn nút "Ẩn/Hiện"                        |
|              |                                                       |
|              | 4. Hệ thống hiển thị dialog xác nhận                 |
|              |                                                       |
|              | 5. Manager xác nhận thay đổi trạng thái               |
|              |                                                       |
|              | 6. Hệ thống cập nhật trạng thái is_active            |
|              |                                                       |
|              | 7. Hệ thống ghi log hoạt động                        |
|              |                                                       |
|              | 8. Hiển thị thông báo "Thay đổi trạng thái thành công"|
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể thay đổi trạng thái hàng loạt      |
| lternative** |                                                       |
|              | 2a. Manager có thể hủy thay đổi                       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Sản phẩm không tồn tại                           |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Sản phẩm không tồn tại" |
|              |                                                       |
|              | 1d. Mất kết nối database                            |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Lỗi hệ thống, vui lòng  |
|              |     thử lại"                                          |
+--------------+-------------------------------------------------------+

**3.2.4. Quản lý biến thể sản phẩm**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-VAR-ManageVariants                            |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Quản lý biến thể sản phẩm                             |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager thêm/sửa/xóa biến thể sản phẩm        |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                       |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                 |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE         |
|              | 3. Sản phẩm tồn tại trong hệ thống                    |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, biến thể được thêm/sửa/xóa, hệ    |
| conditions** |     thống ghi log                                     |
|              |                                                       |
|              | -   Nếu thất bại, không có thay đổi, hiển thị thông   |
|              |     báo lỗi.                                          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang chi tiết sản phẩm          |
| Flow**       |                                                       |
|              | 2. Manager nhấn nút "Quản lý biến thể"               |
|              |                                                       |
|              | 3. Manager thực hiện một trong các hành động:        |
|              |     a) Thêm biến thể mới:                             |
|              |         - Chọn màu sắc                                |
|              |         - Nhập loại/kích thước                        |
|              |         - Nhập SKU                                   |
|              |         - Nhập giá, % giảm giá, tồn kho              |
|              |         - Chọn loại khuyến mãi                       |
|              |     b) Sửa biến thể hiện có:                        |
|              |         - Click nút "Sửa" → Form auto-fill         |
|              |         - Chỉnh sửa thông tin biến thể               |
|              |         - Nhấn "Cập nhật"                            |
|              |     c) Xóa biến thể:                                 |
|              |         - Click nút "Xóa" → Xác nhận                |
|              |                                                       |
|              | 4. Hệ thống validate dữ liệu đầu vào                |
|              |                                                       |
|              | 5. Hệ thống cập nhật biến thể trong database       |
|              |                                                       |
|              | 6. Hệ thống ghi log hoạt động                       |
|              |                                                       |
|              | 7. Hiển thị thông báo thành công/lỗi               |
|              |                                                       |
|              | 8. Redirect về trang quản lý biến thể              |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể thêm nhiều biến thể cùng lúc       |
| lternative** |                                                       |
|              | 2a. Manager có thể upload ảnh cho từng màu          |
|              |                                                       |
|              | 3a. Manager có thể xóa ảnh hiện có                  |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Biến thể đã tồn tại (cùng product_id, color_id,    |
|              |     type_name)                                        |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Biến thể đã tồn tại"    |
|              |                                                       |
|              | 1d. SKU đã tồn tại                                    |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Mã SKU đã tồn tại"     |
|              |                                                       |
|              | 1e. Biến thể đang có trong giỏ hàng                  |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Không thể xóa biến thể  |
|              |     đã có trong giỏ hàng"                            |
|              |                                                       |
|              | 1f. Biến thể đang có trong đơn hàng                 |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Không thể xóa biến thể  |
|              |     đã có trong đơn hàng"                            |
|              |                                                       |
|              | 1g. Màu sắc không đang hoạt động                     |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Màu sắc không đang     |
|              |     hoạt động"                                        |
+--------------+-------------------------------------------------------+

**3.2.5. Cập nhật giá sản phẩm**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-PRC-UpdatePrice                               |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Cập nhật giá sản phẩm                                 |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager thay đổi giá và % giảm giá của biến  |
|              | thể sản phẩm                                          |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                       |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE         |
|              | 3. Biến thể sản phẩm tồn tại                         |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, giá được cập nhật, hệ thống tính  |
| conditions** |     lại sale_price và ghi log                       |
|              |                                                       |
|              | -   Nếu thất bại, giá không thay đổi, hiển thị thông  |
|              |     báo lỗi.                                          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý sản phẩm"         |
| Flow**       |                                                       |
|              | 2. Manager chọn sản phẩm và biến thể cần sửa giá     |
|              |                                                       |
|              | 3. Manager nhập thông tin giá mới:                   |
|              |     - Giá gốc (price)                                |
|              |     - % giảm giá (discount_percent)                  |
|              |     - Loại khuyến mãi (promotion_type)              |
|              |                                                       |
|              | 4. Hệ thống hiển thị giá bán cuối cùng (sale_price)  |
|              |                                                       |
|              | 5. Manager xác nhận thay đổi giá                    |
|              |                                                       |
|              | 6. Hệ thống validate dữ liệu đầu vào                |
|              |                                                       |
|              | 7. Hệ thống cập nhật price và discount_percent      |
|              |                                                       |
|              | 8. Hệ thống tự động tính lại sale_price              |
|              |                                                       |
|              | 9. Hệ thống ghi log hoạt động                       |
|              |                                                       |
|              | 10. Hiển thị thông báo "Cập nhật giá thành công"    |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể cập nhật giá hàng loạt           |
| lternative** |                                                       |
|              | 2a. Manager có thể áp dụng % giảm giá cho nhiều biến  |
|              |     thể                                                |
|              |                                                       |
|              | 3a. Manager có thể thiết lập khuyến mãi đặc biệt     |
|              |     (SALE/OUTLET)                                      |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Giá sản phẩm âm                                   |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Giá sản phẩm phải >= 0" |
|              |                                                       |
|              | 1d. % giảm giá không hợp lệ                          |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "% giảm giá phải từ 0-100"|
|              |                                                       |
|              | 1e. Biến thể không tồn tại                           |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Biến thể không tồn tại" |
|              |                                                       |
|              | 1f. Mất kết nối database                            |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui lòng  |
|              |     thử lại"                                          |
+--------------+-------------------------------------------------------+

**3.2.7. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Tên sản phẩm:** 2-160 ký tự, không trùng lặp
- **SKU:** 3-80 ký tự, duy nhất trong hệ thống
- **Giá:** >= 0, tối đa 15 chữ số
- **% giảm giá:** 0-100
- **Tồn kho:** >= 0
- **Danh mục:** Phải tồn tại và đang ACTIVE (chỉ danh mục lá)
- **Màu sắc:** Phải tồn tại và đang ACTIVE
- **File upload:** Tối đa 2MB, định dạng JPG/PNG

**Bảo mật:**
- **CSRF Protection:** Token validation cho mọi request
- **Rate Limiting:** Tối đa 50 thao tác sản phẩm/phút
- **Audit Trail:** Ghi log mọi thay đổi sản phẩm
- **Input Sanitization:** Làm sạch dữ liệu đầu vào
- **File Upload Security:** Validate định dạng và kích thước ảnh
- **Database Constraints:** Kiểm tra foreign key và unique constraints

**Tính năng đặc biệt:**
- **Auto Slug Generation:** Tự động tạo slug từ tên sản phẩm
- **Sale Price Calculation:** Tự động tính giá bán cuối cùng
- **Auto-fill Form:** Form tự động điền dữ liệu khi sửa biến thể
- **Image Management:** Upload/xóa ảnh theo màu sắc
- **Constraint Validation:** Không cho xóa biến thể đã có trong giỏ hàng/đơn hàng
- **Navigation Flow:** Danh sách → Chi tiết → Quản lý biến thể

**Tiêu chí chấp nhận:**
1. Manager có thể tạo sản phẩm với đầy đủ thông tin và biến thể đầu tiên
2. Manager có thể quản lý nhiều biến thể cho 1 sản phẩm
3. Hệ thống validate đầy đủ dữ liệu đầu vào
4. Tồn kho được cập nhật real-time
5. Giá bán được tính tự động
6. Form auto-fill khi sửa biến thể
7. Không thể xóa biến thể đã có trong giỏ hàng/đơn hàng
8. Upload ảnh với validation kích thước
9. Navigation flow logic và user-friendly
10. Thông báo lỗi rõ ràng và hữu ích
