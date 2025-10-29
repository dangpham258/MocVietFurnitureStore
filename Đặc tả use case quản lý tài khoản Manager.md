**3.2. Biểu đồ Use case (Use case Diagram) - Quản lý tài khoản Manager**

**3.2.1. Cập nhật hồ sơ cá nhân**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ACC-UpdateProfile                              |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Cập nhật hồ sơ cá nhân                                |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager cập nhật thông tin cá nhân trong hệ |
|              | thống                                                 |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                       |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                 |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE         |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, thông tin cá nhân được cập nhật, |
| conditions** |     hệ thống ghi log                                  |
|              |                                                       |
|              | -   Nếu thất bại, thông tin không thay đổi, hiển thị  |
|              |     thông báo lỗi.                                   |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tài khoản"        |
| Flow**       |                                                       |
|              | 2. Hệ thống hiển thị form với thông tin hiện tại     |
|              |                                                       |
|              | 3. Manager chỉnh sửa thông tin: họ tên, email, số    |
|              |     điện thoại, giới tính, ngày sinh                 |
|              |                                                       |
|              | 4. Manager nhấn "Lưu thay đổi"                       |
|              |                                                       |
|              | 5. Hệ thống validate dữ liệu đầu vào                 |
|              |                                                       |
|              | 6. Hệ thống cập nhật thông tin vào database          |
|              |                                                       |
|              | 7. Hệ thống ghi log hoạt động                        |
|              |                                                       |
|              | 8. Hiển thị thông báo "Cập nhật thành công"          |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager chỉ cập nhật một số trường thông tin      |
| lternative** |                                                       |
|              | 2a. Manager hủy thay đổi → quay về trang trước      |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Email đã tồn tại                                  |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Email này đã được sử    |
|              |     dụng"                                             |
|              |                                                       |
|              | 1d. Số điện thoại không hợp lệ                       |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Số điện thoại không     |
|              |     đúng định dạng"                                   |
|              |                                                       |
|              | 1e. Ngày sinh không hợp lệ                           |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Ngày sinh phải trước    |
|              |     ngày hiện tại"                                    |
|              |                                                       |
|              | 1f. Mất kết nối database                             |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi hệ thống, vui lòng  |
|              |     thử lại"                                          |
|              |                                                       |
|              | 1g. Session hết hạn                                  |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Phiên đăng nhập đã hết  |
|              |     hạn"                                              |
+--------------+-------------------------------------------------------+

**3.2.2. Đổi mật khẩu**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ACC-ChangePassword                            |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Đổi mật khẩu                                          |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager thay đổi mật khẩu tài khoản để bảo   |
|              | mật                                                    |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                       |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống                 |
| conditions** | 2. Tài khoản Manager đang ở trạng thái ACTIVE         |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, mật khẩu được cập nhật, session  |
| conditions** |     vẫn hoạt động                                     |
|              |                                                       |
|              | -   Nếu thất bại, mật khẩu không thay đổi, hiển thị   |
|              |     thông báo lỗi.                                   |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Đổi mật khẩu"              |
| Flow**       |                                                       |
|              | 2. Manager nhập mật khẩu hiện tại                    |
|              |                                                       |
|              | 3. Manager nhập mật khẩu mới                         |
|              |                                                       |
|              | 4. Manager xác nhận mật khẩu mới                     |
|              |                                                       |
|              | 5. Manager nhấn "Đổi mật khẩu"                       |
|              |                                                       |
|              | 6. Hệ thống validate mật khẩu hiện tại               |
|              |                                                       |
|              | 7. Hệ thống validate mật khẩu mới                    |
|              |                                                       |
|              | 8. Hệ thống hash mật khẩu mới bằng Bcrypt             |
|              |                                                       |
|              | 9. Hệ thống cập nhật mật khẩu trong database         |
|              |                                                       |
|              | 10. Hệ thống ghi log hoạt động                       |
|              |                                                       |
|              | 11. Hiển thị thông báo "Đổi mật khẩu thành công"     |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager hủy thay đổi → quay về trang trước       |
| lternative** |                                                       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Mật khẩu hiện tại sai                            |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Mật khẩu hiện tại       |
|              |     không đúng"                                       |
|              |                                                       |
|              | 1d. Mật khẩu mới quá ngắn                            |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Mật khẩu phải có ít     |
|              |     nhất 8 ký tự"                                    |
|              |                                                       |
|              | 1e. Mật khẩu mới không khớp                          |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Xác nhận mật khẩu       |
|              |     không khớp"                                       |
|              |                                                       |
|              | 1f. Mật khẩu mới giống mật khẩu cũ                   |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Mật khẩu mới phải khác  |
|              |     mật khẩu hiện tại"                              |
|              |                                                       |
|              | 1g. Mất kết nối database                             |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Lỗi hệ thống, vui lòng  |
|              |     thử lại"                                          |
|              |                                                       |
|              | 1h. Session hết hạn                                  |
|              |                                                       |
|              | 2h. Hệ thống hiện thông báo "Phiên đăng nhập đã hết  |
|              |     hạn"                                              |
+--------------+-------------------------------------------------------+

**3.2.3. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Email:** Format hợp lệ, không trùng với tài khoản khác
- **Số điện thoại:** 10-11 số, bắt đầu bằng 0
- **Họ tên:** 2-50 ký tự, không chứa ký tự đặc biệt
- **Ngày sinh:** Trước ngày hiện tại, tuổi từ 18-100
- **Mật khẩu:** Tối thiểu 8 ký tự, có chữ hoa, chữ thường, số

**Bảo mật:**
- **CSRF Protection:** Token validation cho mọi request
- **Rate Limiting:** Tối đa 5 lần đổi mật khẩu/giờ
- **Audit Trail:** Ghi log mọi thay đổi thông tin
- **Session Security:** Không làm mất session khi đổi mật khẩu
- **Input Sanitization:** Làm sạch dữ liệu đầu vào

**Tiêu chí chấp nhận:**
1. Manager có thể cập nhật tất cả thông tin cá nhân
2. Hệ thống validate đầy đủ dữ liệu đầu vào
3. Email không được trùng với tài khoản khác
4. Mật khẩu được hash an toàn
5. Ghi log đầy đủ hoạt động
6. Thông báo lỗi rõ ràng và hữu ích
7. Session không bị mất khi đổi mật khẩu
