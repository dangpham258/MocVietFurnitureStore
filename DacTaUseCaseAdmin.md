# ĐẶC TẢ USE CASE - QUẢN LÝ TÀI KHOẢN ADMIN

## UC_001 - Quản lý thông tin tài khoản bản thân (Manage account profile)

Use case ID UC_001
Name Manage account profile
Goal Quản lý thông tin cá nhân và mật khẩu của quản trị viên
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống
Post-conditions - Nếu thành công, thông tin tài khoản hoặc mật khẩu được cập nhật - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý tài khoản (/admin/profile) 2. Chọn tab "Thông tin cá nhân" hoặc "Đổi mật khẩu" 3. Nếu cập nhật thông tin: nhập email, họ tên, số điện thoại, giới tính, ngày sinh 4. Nếu đổi mật khẩu: nhập mật khẩu hiện tại, mật khẩu mới, xác nhận mật khẩu 5. Bấm nút "Cập nhật thông tin" hoặc "Đổi mật khẩu" 6. Hệ thống kiểm tra dữ liệu 7. Nếu hợp lệ, gửi mã OTP đến email 8. Hiển thị modal xác thực OTP 9. Nhập mã OTP 6 chữ số 10. Bấm nút "Xác thực OTP" 11. Hệ thống xác thực mã OTP 12. Nếu OTP hợp lệ, cập nhật thông tin hoặc mật khẩu 13. Hiển thị thông báo thành công 14. Nếu cập nhật thông tin: reload trang sau 3 giây 15. Nếu đổi mật khẩu: chuyển đến trang đăng nhập sau 2 giây
Alternative 5a. Bấm nút "Hủy" trong modal OTP
6a. Hiển thị thông báo "Đã hủy xác thực"
7a. Ẩn modal và reset form
8a. Hủy OTP trong hệ thống
5b. OTP hết hạn hoặc nhập sai
6b. Hiển thị thông báo "Mã OTP không hợp lệ hoặc đã hết hạn"
7b. Vô hiệu hóa tất cả mã OTP cũ
8b. Đóng modal
Exception 9a. Dữ liệu không hợp lệ
10a. Hiển thị thông báo lỗi tương ứng
9b. Lỗi hệ thống
10b. Hiển thị thông báo "Có lỗi xảy ra"

Bảng 3-1. Use case Quản lý thông tin tài khoản bản thân

---

## UC_002 - Quản lý tài khoản hệ thống (Manage system users)

Use case ID UC_002
Name Manage system users
Goal Quản lý tài khoản người dùng trong hệ thống (Tìm kiếm, xem danh sách, xem chi tiết, khóa/mở khóa tài khoản, tạo tài khoản mới cho MANAGER và DELIVERY)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách User được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý Users (/admin/users) 2. Hệ thống hiển thị danh sách tất cả Users với thông tin: ID, Tên, Email, Số điện thoại, Vai trò, Trạng thái 3. Hệ thống hiển thị thống kê: Tổng số Users, Còn hoạt động, MANAGER, DELIVERY 4. Có thể tìm kiếm theo tên, username, email, số điện thoại 5. Có thể lọc theo vai trò (Tất cả, MANAGER, DELIVERY, ADMIN, CUSTOMER) 6. Có thể lọc theo trạng thái (Tất cả, Còn hoạt động, Đã khóa) 7. Click "Xem" để xem chi tiết User (modal hiển thị: ID, Username, Email, Họ và tên, Số điện thoại, Giới tính, Ngày sinh, Vai trò, Ngày tạo, Trạng thái) 8. Click "Khóa/Mở khóa" để thay đổi trạng thái User (không áp dụng cho ADMIN) 9. Click "Thêm User" để tạo tài khoản mới 10. Modal hiển thị 3 tab: Tab 1 "Thông tin cơ bản" (Username, Email, Họ và tên, Số điện thoại, Ngày sinh, Giới tính), Tab 2 "Mật khẩu" (Mật khẩu, Xác nhận mật khẩu với nút hiển thị/ẩn), Tab 3 "Vai trò & Trạng thái" (Vai trò: MANAGER/DELIVERY, Checkbox kích hoạt ngay) 11. Nhập đầy đủ thông tin bắt buộc và click "Thêm User" 12. Hệ thống kiểm tra Username và Email không trùng 13. Hệ thống kiểm tra mật khẩu đủ mạnh (tối thiểu 8 ký tự, có chữ hoa, chữ thường và số) 14. Nếu hợp lệ, tạo User mới và hiển thị thông báo thành công 15. Cập nhật danh sách Users và đóng modal
Alternative 5a. Bấm nút "Reset Filters" để reset bộ lọc 6a. Xóa tất cả bộ lọc 7a. Hiển thị danh sách đầy đủ 5b. Click số trang để phân trang 6b. Hiển thị trang tương ứng
Exception 11a. Username hoặc Email đã tồn tại 12a. Hiển thị thông báo "Username đã tồn tại" hoặc "Email đã tồn tại" 13a. Giữ modal mở để sửa 11b. Mật khẩu không đủ mạnh 12b. Hiển thị lỗi validation chi tiết 13b. Yêu cầu nhập mật khẩu theo yêu cầu (tối thiểu 8 ký tự, có chữ hoa, chữ thường và số) 11c. Lỗi hệ thống 12c. Hiển thị thông báo "Có lỗi xảy ra" 13c. Cho phép thử lại

Bảng 3-2. Use case Quản lý tài khoản hệ thống

---

## UC_003 - Quản lý màu sắc (Manage colors)

Use case ID UC_003
Name Manage colors
Goal Quản lý màu sắc sản phẩm trong hệ thống (Xem danh sách, tìm kiếm, thêm/sửa màu sắc, bật/tắt hiển thị màu)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách màu sắc được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý màu sắc (/admin/colors) 2. Hệ thống hiển thị danh sách màu sắc với thông tin: Tên màu, Slug, Mã màu HEX, Trạng thái (Đang kích hoạt/Đã vô hiệu hóa) 3. Hệ thống hiển thị thống kê: Tổng số màu sắc, Đang kích hoạt, Đã vô hiệu hóa 4. Click "Thêm màu sắc" để tạo màu mới 5. Modal hiển thị form nhập: Tên màu (bắt buộc), Slug (bắt buộc), Mã màu HEX (tùy chọn), Checkbox "Kích hoạt ngay" 6. Nhập đầy đủ thông tin bắt buộc, có thể nhập mã HEX hoặc không, chọn trạng thái kích hoạt 7. Click "Thêm màu" 8. Hệ thống kiểm tra tên màu không trùng 9. Hệ thống kiểm tra slug không trùng 10. Nếu hợp lệ, tạo màu sắc mới và hiển thị thông báo thành công 11. Cập nhật danh sách màu sắc và đóng modal 12. Có thể tìm kiếm màu sắc theo tên hoặc slug 13. Có thể lọc theo trạng thái (Tất cả, Đang kích hoạt, Đã vô hiệu hóa) 14. Click "Sửa" để cập nhật màu sắc 15. Modal hiển thị thông tin hiện tại (nếu màu đã có ảnh sản phẩm, trường Slug bị vô hiệu hóa với lý do: "Không thể chỉnh sửa (đã có ảnh sản phẩm)") 16. Sửa đổi thông tin và click "Cập nhật" 17. Hệ thống kiểm tra tên màu và slug không trùng (nếu thay đổi) 18. Nếu màu sắc đã có ảnh sản phẩm, không cho phép thay đổi slug 19. Nếu hợp lệ, cập nhật màu sắc và hiển thị thông báo thành công 20. Cập nhật danh sách màu sắc và đóng modal 21. Click nút khóa/mở khóa để thay đổi trạng thái hiển thị của màu
Alternative 13a. Bấm nút "Reset" để reset bộ lọc 14a. Xóa tất cả bộ lọc và hiển thị danh sách đầy đủ
Exception 9a. Tên màu hoặc Slug đã tồn tại 10a. Hiển thị thông báo "Tên màu đã tồn tại" hoặc "Slug đã tồn tại" 11a. Giữ modal mở để sửa 18a. Cố gắng thay đổi slug khi màu đã có ảnh sản phẩm 19a. Hiển thị thông báo "Không thể thay đổi slug khi màu sắc đã có ảnh sản phẩm" 20a. Slug field tự động bị vô hiệu hóa khi open modal edit 21a. Lỗi hệ thống 22a. Hiển thị thông báo "Có lỗi xảy ra" 23a. Cho phép thử lại

Bảng 3-3. Use case Quản lý màu sắc

---

## UC_004 - Quản lý danh mục (Manage categories)

Use case ID UC_004
Name Manage categories
Goal Quản lý danh mục sản phẩm và bộ sưu tập trong hệ thống (Xem danh sách, tìm kiếm, thêm/sửa danh mục, bật/tắt hiển thị danh mục)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách danh mục được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý danh mục (/admin/categories) 2. Hệ thống hiển thị 2 tab: "Danh mục sản phẩm" (hiển thị cây danh mục) và "Bộ sưu tập" (hiển thị danh sách phẳng) 3. Hệ thống hiển thị thống kê dựa trên bộ lọc: Tổng danh mục, Đang kích hoạt, Danh mục cấp 1, Bộ sưu tập 4. Có thể tìm kiếm theo tên hoặc slug 5. Có thể lọc theo loại (Tất cả, Danh mục, Bộ sưu tập) 6. Có thể lọc theo trạng thái (Tất cả, Đang kích hoạt, Đã vô hiệu hóa) 7. Click "Thêm danh mục" để tạo danh mục mới 8. Modal hiển thị form nhập: Loại (Danh mục/Bộ sưu tập - bắt buộc), Danh mục cha (chỉ hiển thị khi chọn loại "Danh mục", cho phép tạo danh mục cấp 2), Tên danh mục (bắt buộc), Slug (bắt buộc, tự động tạo từ tên), Checkbox "Kích hoạt ngay" 9. Nhập đầy đủ thông tin và click "Thêm danh mục" 10. Hệ thống kiểm tra tên danh mục không trùng 11. Hệ thống kiểm tra slug không trùng 12. Nếu hợp lệ, tạo danh mục mới và hiển thị thông báo thành công 13. Cập nhật danh sách danh mục và đóng modal 14. Click "Sửa" trên một danh mục để cập nhật 15. Modal hiển thị thông tin hiện tại (nếu là danh mục cấp 1, ẩn dropdown "Danh mục cha"; nếu danh mục đã có sản phẩm, Slug bị vô hiệu hóa cho CATEGORY) 16. Sửa đổi thông tin và click "Cập nhật" 17. Hệ thống kiểm tra tên danh mục và slug không trùng (nếu thay đổi) 18. Nếu danh mục là loại CATEGORY và đã có sản phẩm hoặc có danh mục con có sản phẩm, không cho phép thay đổi slug (slug dùng trong đường dẫn ảnh) 19. Nếu danh mục là loại COLLECTION, cho phép thay đổi slug (không dùng trong đường dẫn ảnh) 20. Nếu hợp lệ, cập nhật danh mục và hiển thị thông báo thành công 21. Cập nhật danh sách danh mục và đóng modal 22. Click nút khóa/mở khóa để thay đổi trạng thái hiển thị của danh mục
Alternative 5a. Bấm nút "Đặt lại" để reset bộ lọc 6a. Xóa tất cả bộ lọc và hiển thị danh sách đầy đủ
Exception 11a. Tên danh mục hoặc Slug đã tồn tại 12a. Hiển thị thông báo "Tên danh mục đã tồn tại" hoặc "Slug đã tồn tại" 13a. Giữ modal mở để sửa 18a. Cố gắng thay đổi slug của danh mục CATEGORY khi đã có sản phẩm hoặc có danh mục con có sản phẩm 19a. Hiển thị thông báo "Không thể thay đổi slug khi danh mục đã có sản phẩm" hoặc "Không thể thay đổi slug khi có danh mục con đã có sản phẩm" 20a. Slug field tự động bị vô hiệu hóa trong modal edit nếu là CATEGORY có sản phẩm 21a. Lỗi hệ thống 22a. Hiển thị thông báo "Có lỗi xảy ra" 23a. Cho phép thử lại

Bảng 3-4. Use case Quản lý danh mục
