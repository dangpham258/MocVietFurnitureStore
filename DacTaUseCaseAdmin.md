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

---

## UC_005 - Quản lý mã giảm giá (Manage coupons)

Use case ID UC_005
Name Manage coupons
Goal Quản lý mã giảm giá trong hệ thống (Xem danh sách, tìm kiếm, thêm/sửa mã giảm giá, bật/tắt hiển thị mã giảm giá)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách mã giảm giá được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý mã giảm giá (/admin/coupons) 2. Hệ thống hiển thị danh sách mã giảm giá với thông tin: Mã, % giảm giá, Thời gian hiệu lực, Ngưỡng tối thiểu, Trạng thái (Đang hiệu lực/Sắp có hiệu lực/Đã hết hạn/Đã vô hiệu hóa) 3. Hệ thống hiển thị thống kê dựa trên bộ lọc: Tổng mã, Đang kích hoạt, Sắp có hiệu lực, Đã hết hạn 4. Có thể tìm kiếm theo mã hoặc % giảm giá 5. Có thể lọc theo trạng thái (Tất cả, Đang kích hoạt, Đã vô hiệu hóa) 6. Có thể lọc theo hiệu lực (Tất cả, Đang hiệu lực, Sắp có hiệu lực, Đã hết hạn) 7. Click "Tạo mã mới" để tạo mã giảm giá mới 8. Modal hiển thị form nhập: Mã giảm giá (bắt buộc), % Giảm giá (bắt buộc, 1-100%), Ngày bắt đầu (bắt buộc), Ngày kết thúc (bắt buộc), Giá trị đơn hàng tối thiểu (VNĐ), Checkbox "Kích hoạt ngay" 9. Nhập đầy đủ thông tin bắt buộc và click "Tạo mã giảm giá" 10. Hệ thống kiểm tra mã giảm giá không trùng 11. Hệ thống kiểm tra % giảm giá từ 0.01% đến 100% 12. Hệ thống kiểm tra ngày kết thúc phải sau ngày bắt đầu 13. Nếu hợp lệ, tạo mã giảm giá mới và hiển thị thông báo thành công 14. Cập nhật danh sách mã giảm giá và đóng modal 15. Click "Sửa" trên một mã giảm giá để cập nhật 16. Modal hiển thị thông tin hiện tại (mã không thể thay đổi, hiển thị readonly) 17. Sửa đổi thông tin và click "Cập nhật" 18. Hệ thống kiểm tra % giảm giá từ 0.01% đến 100% (nếu thay đổi) 19. Hệ thống kiểm tra ngày kết thúc phải sau ngày bắt đầu (nếu thay đổi) 20. Nếu hợp lệ, cập nhật mã giảm giá và hiển thị thông báo thành công 21. Cập nhật danh sách mã giảm giá và đóng modal 22. Click nút khóa/mở khóa để thay đổi trạng thái hiển thị của mã giảm giá 23. Hệ thống có phân trang 5 mã/trang
Alternative 5a. Bấm nút "Reset" để reset bộ lọc 6a. Xóa tất cả bộ lọc và hiển thị danh sách đầy đủ 5b. Click số trang để phân trang 6b. Hiển thị trang tương ứng
Exception 10a. Mã giảm giá đã tồn tại 11a. Hiển thị thông báo "Mã giảm giá đã tồn tại" 12a. Giữ modal mở để sửa 11b. % giảm giá không hợp lệ (< 0.01 hoặc > 100) 12b. Hiển thị thông báo "% giảm giá phải từ 0.01% đến 100%" 13b. Yêu cầu nhập lại % giảm giá hợp lệ 12c. Ngày kết thúc trước ngày bắt đầu 13c. Hiển thị thông báo "Ngày kết thúc phải sau ngày bắt đầu" 14c. Yêu cầu nhập lại ngày tháng hợp lệ 21a. Lỗi hệ thống 22a. Hiển thị thông báo "Có lỗi xảy ra" 23a. Cho phép thử lại

Bảng 3-5. Use case Quản lý mã giảm giá

---

## UC_006 - Quản lý phí vận chuyển (Manage shipping fees)

Use case ID UC_006
Name Manage shipping fees
Goal Quản lý phí vận chuyển theo miền và mapping tỉnh/thành vào miền (Xem danh sách, cập nhật phí vận chuyển, thêm/xóa tỉnh/thành khỏi miền)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, thông tin phí vận chuyển được cập nhật hoặc mapping tỉnh/thành được thay đổi - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý phí vận chuyển (/admin/shipping) 2. Hệ thống hiển thị danh sách các miền với thông tin: Tên miền, Phí vận chuyển hiện tại, Số lượng tỉnh/thành 3. Hệ thống hiển thị thống kê: Tổng miền, Tổng tỉnh/thành, Phí TB/đơn hàng 4. Các miền được hiển thị dưới dạng card, mỗi card hiển thị: Tên miền, Phí vận chuyển với badge, Nút "Sửa" phí, Danh sách các tỉnh/thành thuộc miền (dạng grid), Nút "Thêm tỉnh/thành" và nút X cho mỗi tỉnh/thành để xóa 5. Click nút "Sửa" (biểu tượng bút chì) trên một miền để cập nhật phí vận chuyển 6. Modal hiển thị form cập nhật: Miền (readonly), Phí vận chuyển (VNĐ, bắt buộc, min=0, step=1000, làm tròn theo phần nghìn) 7. Nhập phí vận chuyển mới và click "Cập nhật" 8. Hệ thống kiểm tra phí vận chuyển không âm 9. Nếu hợp lệ, cập nhật phí vận chuyển cho miền và hiển thị thông báo thành công 10. Cập nhật danh sách miền và đóng modal 11. Click nút "Thêm tỉnh/thành" trên một miền để thêm tỉnh/thành vào miền 12. Modal hiển thị form nhập: Miền (readonly), Tên tỉnh/thành (bắt buộc, lưu ý phân biệt hoa/thường) 13. Nhập tên tỉnh/thành và click "Thêm" 14. Hệ thống kiểm tra tỉnh/thành chưa được map vào miền khác (province_name unique) 15. Nếu hợp lệ, thêm tỉnh/thành vào miền và hiển thị thông báo thành công 16. Cập nhật danh sách miền và đóng modal 17. Click nút X (biểu tượng xóa) trên một tỉnh/thành để xóa khỏi miền 18. Modal xác nhận hiển thị: Tên tỉnh/thành và miền, Cảnh báo "Các đơn hàng tương lai đến địa chỉ này sẽ không thể tính phí vận chuyển" 19. Click "Xóa" để xác nhận 20. Hệ thống xóa mapping tỉnh/thành khỏi miền 21. Hiển thị thông báo thành công 22. Cập nhật danh sách miền và đóng modal
Alternative 2a. Nếu một miền chưa có tỉnh/thành nào 3a. Hiển thị "Chưa có tỉnh/thành nào" với biểu tượng inbox rỗng 4a. Nút "Thêm tỉnh/thành" vẫn hoạt động
Exception 8a. Phí vận chuyển âm 9a. Hiển thị thông báo "Phí vận chuyển không được âm" 10a. Yêu cầu nhập lại 14a. Tỉnh/thành đã được map vào miền khác 15a. Hiển thị thông báo "Tỉnh/thành đã được map vào miền khác" 16a. Giữ modal mở để sửa 20a. Lỗi hệ thống 21a. Hiển thị thông báo "Có lỗi xảy ra" 22a. Cho phép thử lại

Bảng 3-6. Use case Quản lý phí vận chuyển

---

## UC_007 - Quản lý đội giao hàng (Manage delivery teams)

Use case ID UC*007
Name Manage delivery teams
Goal Quản lý đội giao hàng, phân công khu vực phục vụ cho từng đội (Tạo, xem, sửa, bật/tắt đội, quản lý khu vực phục vụ)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, thông tin đội giao hàng được cập nhật hoặc khu vực phục vụ được thay đổi - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý đội giao hàng (/admin/delivery-teams) 2. Hệ thống hiển thị danh sách các đội với thông tin: Tên đội, SĐT, User phụ trách (tên và email), Số khu vực phục vụ, Trạng thái (Đang hoạt động/Đã tạm dừng) 3. Hệ thống hiển thị thống kê: Tổng đội, Đang hoạt động, Tổng khu vực phục vụ 4. Các đội được hiển thị dưới dạng card grid (3 cột), mỗi card hiển thị: Header với tên đội và badge trạng thái, Thông tin: SĐT, User phụ trách (tên + email), Số khu vực, Preview 3 zones đầu tiên với badge "+N" nếu có nhiều hơn, Footer với 3 nút: "Zones" (quản lý khu vực), "Sửa" (bút chì), "Bật/Tắt" (toggle) 5. Click nút "Tạo đội mới" để tạo đội mới 6. Modal hiển thị form nhập: Tên đội (bắt buộc), Số điện thoại (tùy chọn), User phụ trách * (chỉ hiển thị users có role DELIVERY và chưa được gán vào đội khác), Checkbox "Kích hoạt ngay" 7. Nhập đầy đủ thông tin bắt buộc và click "Tạo đội" 8. Hệ thống kiểm tra user phải có vai trò DELIVERY 9. Hệ thống kiểm tra user chưa được gán vào đội khác (user*id unique) 10. Nếu hợp lệ, tạo đội mới và hiển thị thông báo thành công 11. Cập nhật danh sách đội và đóng modal 12. Click nút "Sửa" (biểu tượng bút chì) trên một đội để cập nhật 13. Modal hiển thị form cập nhật: Tên đội (bắt buộc), Số điện thoại, User phụ trách * (hiển thị tất cả users có role DELIVERY), Checkbox "Đang hoạt động" 14. Sửa đổi thông tin và click "Cập nhật" 15. Hệ thống kiểm tra user phải có vai trò DELIVERY (nếu thay đổi user) 16. Hệ thống kiểm tra user mới chưa được gán vào đội khác (nếu thay đổi user) 17. Nếu hợp lệ, cập nhật đội và hiển thị thông báo thành công 18. Cập nhật danh sách đội và đóng modal 19. Click nút "Bật/Tắt" (toggle) trên một đội để thay đổi trạng thái 20. Hệ thống cập nhật trạng thái (active/inactive) 21. Hiển thị thông báo thành công 22. Cập nhật danh sách đội 23. Click nút "Zones" trên một đội để quản lý khu vực phục vụ 24. Modal hiển thị: Tên đội, Dropdown chọn zone để thêm kèm nút "Thêm", Danh sách zones hiện tại (dạng grid với nút X để xóa từng zone) 25. Chọn zone từ dropdown và click "Thêm" 26. Hệ thống kiểm tra đội chưa phục vụ zone này 27. Nếu hợp lệ, thêm zone cho đội và hiển thị thông báo thành công 28. Reload modal để hiển thị zone mới 29. Click nút X trên một zone để xóa 30. Hệ thống xác nhận và xóa zone khỏi đội 31. Hiển thị thông báo thành công 32. Reload modal để cập nhật danh sách zones
Alternative 4a. Nếu một đội chưa có khu vực nào 5a. Hiển thị "Chưa có khu vực" với biểu tượng inbox 6a. Nút "Thêm" vẫn hoạt động
Exception 9a. User không có vai trò DELIVERY 10a. Hiển thị thông báo "User phải có vai trò DELIVERY" 11a. Yêu cầu chọn user khác 9b. User đã được gán vào đội khác 10b. Hiển thị thông báo "User đã được gán vào đội khác" 11b. Yêu cầu chọn user khác 26a. Đội đã phục vụ zone này 27a. Hiển thị thông báo "Đội đã phục vụ khu vực này" 28a. Giữ modal mở 31a. Lỗi hệ thống 32a. Hiển thị thông báo "Có lỗi xảy ra" 33a. Cho phép thử lại

Bảng 3-7. Use case Quản lý đội giao hàng

---

## UC_008 - Quản lý liên kết mạng xã hội (Manage social links)

Use case ID UC_008
Name Manage social links
Goal Quản lý liên kết mạng xã hội (Facebook, Zalo, Youtube) - Cập nhật URL và bật/tắt hiển thị
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, liên kết mạng xã hội được cập nhật hoặc trạng thái hiển thị được thay đổi - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý liên kết mạng xã hội (/admin/social-links) 2. Hệ thống hiển thị 3 card tương ứng với 3 nền tảng: Facebook (primary), Zalo (info), Youtube (danger) 3. Mỗi card hiển thị: Header với icon và tên nền tảng + badge trạng thái (Đang hiển thị/Đã ẩn), URL hiện tại, Nút "Cập nhật link" (outline), Nút "Truy cập liên kết" (filled, chỉ hiển thị khi có URL), Footer với thông tin trạng thái 4. Nếu chưa có URL, hiển thị "Chưa cập nhật" và nút "Truy cập liên kết" bị ẩn 5. Click nút "Cập nhật link" trên một card 6. Modal hiển thị form cập nhật: Nền tảng (readonly), URL (bắt buộc, type=url), Checkbox "Hiển thị trên website" (on/off) 7. Nhập URL mới hoặc sửa URL hiện tại, chọn trạng thái hiển thị, click "Cập nhật" 8. Hệ thống kiểm tra URL hợp lệ (format URL) 9. Nếu liên kết đã tồn tại: cập nhật URL và trạng thái cho platform tương ứng 10. Nếu liên kết chưa tồn tại: tạo liên kết mới cho platform tương ứng 11. Hiển thị thông báo "Cập nhật liên kết thành công" 12. Cập nhật card tương ứng (URL, trạng thái, badge) và đóng modal 13. Click nút "Truy cập liên kết" để mở liên kết trong tab mới (chỉ khi có URL) 14. Hiển thị thông báo "Cập nhật liên kết thành công" khi cập nhật thành công
Alternative 6a. Nếu liên kết đã có ID (đã tồn tại trong DB): gọi API PUT 6b. Nếu liên kết chưa có ID (chưa tồn tại): gọi API POST
Exception 7a. URL không hợp lệ 8a. Hiển thị thông báo "Vui lòng nhập URL hợp lệ" 9a. Giữ modal mở để sửa 8b. Lỗi hệ thống 9b. Hiển thị thông báo "Có lỗi xảy ra" 10b. Cho phép thử lại 8c. Platform đã tồn tại (khi tạo mới) 9c. Hiển thị thông báo "Platform đã tồn tại" 10c. Giữ modal mở để sửa

Bảng 3-8. Use case Quản lý liên kết mạng xã hội

---

## UC_009 - Quản lý banner (Manage banners)

Use case ID UC_009
Name Manage banners
Goal Quản lý banner trang chủ - Carousel (Xem danh sách, thêm/sửa banner, xóa banner, bật/tắt hiển thị, thiết lập thứ tự hiển thị)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách banner được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý banner (/admin/banners) 2. Hệ thống hiển thị danh sách banner dưới dạng grid, mỗi card hiển thị: Ảnh banner, Badge số thứ tự (STT), Tiêu đề banner, Liên kết (nếu có), Trạng thái (Đang hiển thị/Đã tắt), Nút "Truy cập URL" (nếu có liên kết), Nút "Sửa", Nút "Xóa", Ngày tạo 3. Hệ thống hiển thị thống kê: Tổng banner, Đang hiển thị, Đã tắt, Có liên kết 4. Banner được sắp xếp theo số thứ tự (00-99), banner mới nhất hiển thị cuối 5. Click "Thêm Banner" để tạo banner mới 6. Modal hiển thị form nhập: Số thứ tự (00-99, bắt buộc), Tiêu đề (bắt buộc, max 160 ký tự), Ảnh banner (bắt buộc, JPG/PNG/WebP, max 10MB), Liên kết (tùy chọn, URL), Checkbox "Kích hoạt ngay" 7. Khi nhập tiêu đề và số thứ tự, hiển thị preview tên file: NN_title.jpg (tự động chuyển tiếng Việt có dấu thành không dấu) 8. Upload ảnh bằng drag-drop hoặc click chọn file, validate ngay khi chọn (max 10MB) 9. Nhập đầy đủ thông tin bắt buộc và click "Thêm Banner" 10. Hệ thống kiểm tra số thứ tự không trùng với banner khác 11. Hệ thống kiểm tra tiêu đề không trống 12. Nếu hợp lệ, tạo banner mới và hiển thị thông báo thành công 13. Cập nhật danh sách banner và đóng modal, form tự động reset 14. Click "Sửa" trên một banner để cập nhật 15. Modal hiển thị: Ảnh hiện tại, Nút upload ảnh mới (tùy chọn), Số thứ tự hiện tại, Tiêu đề hiện tại, Liên kết hiện tại, Checkbox trạng thái 16. Sửa đổi thông tin và click "Cập nhật" 17. Nếu đổi số thứ tự mà không upload ảnh mới, hệ thống tự động rename file ảnh trên server 18. Hệ thống kiểm tra số thứ tự mới không trùng (nếu thay đổi) 19. Nếu hợp lệ, cập nhật banner và hiển thị thông báo thành công 20. Cập nhật danh sách banner và đóng modal, form tự động reset 21. Click "Xóa" trên một banner 22. Modal xác nhận hiển thị: Tiêu đề banner, Cảnh báo "Banner và ảnh sẽ bị xóa vĩnh viễn" 23. Click "Xóa" để xác nhận 24. Hệ thống xóa banner và ảnh khỏi server (cả 2 thư mục src và target) 25. Hiển thị thông báo thành công 26. Cập nhật danh sách banner và đóng modal 27. Click "Truy cập URL" để mở liên kết trong tab mới (chỉ khi có liên kết) 28. Hệ thống tự động thêm http:// vào URL nếu không có protocol
Alternative 8a. Người dùng click vào file input nhưng không chọn file 9a. Preview ảnh tự động ẩn đi, validation vẫn bắt buộc upload ảnh 14a. Người dùng click vào file input edit nhưng không chọn file 15a. Preview ảnh tự động khôi phục về ảnh cũ
Exception 10a. Số thứ tự đã được sử dụng 11a. Hiển thị thông báo "Số thứ tự XX đã được sử dụng bởi banner 'Tên'" 12a. Giữ modal mở để sửa 11b. Tiêu đề trống 12b. Hiển thị thông báo "Vui lòng nhập tiêu đề banner" 13b. Yêu cầu nhập tiêu đề 11c. Ảnh vượt quá 10MB 12c. Hiển thị thông báo "Kích thước ảnh vượt quá 10MB" ngay khi chọn file 13c. Xóa file đã chọn 18a. Số thứ tự mới đã được sử dụng bởi banner khác 19a. Hiển thị thông báo "Số thứ tự XX đã được sử dụng bởi banner 'Tên'" 20a. Giữ modal mở để sửa 24a. Lỗi hệ thống 25a. Hiển thị thông báo "Có lỗi xảy ra" 26a. Cho phép thử lại

Bảng 3-9. Use case Quản lý banner

---

## UC_010 - Quản lý showroom (Manage showrooms)

Use case ID UC_010
Name Manage showrooms
Goal Quản lý thông tin các cửa hàng trưng bày (Xem danh sách, thêm/sửa/xóa showroom, xem chi tiết, bật/tắt hiển thị, tìm kiếm và lọc)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách showroom được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý showroom (/admin/showrooms) 2. Hệ thống hiển thị danh sách showroom dưới dạng grid full-width, mỗi card hiển thị: Bản đồ Google Maps (40% bên trái), Thông tin showroom (60% bên phải): Tên showroom, Badge trạng thái, Địa chỉ chi tiết, Quận/Huyện, Tỉnh/Thành, Điện thoại, Email, Giờ mở cửa, Ngày tạo, Nút "Xem", "Sửa", "Xóa" 3. Hệ thống hiển thị thống kê dựa trên bộ lọc: Tổng showroom, Đang hoạt động, Đã tắt, Số tỉnh/thành 4. Có thể tìm kiếm theo tên, địa chỉ, tỉnh/thành, quận/huyện 5. Có thể lọc theo tỉnh/thành (dropdown tự động cập nhật danh sách từ dữ liệu) 6. Có thể lọc theo trạng thái (Tất cả, Đang hoạt động, Đã tắt) 7. Có 2 chế độ xem: Grid (full-width card với map + info) và List (table dạng bảng) 8. Click "Thêm Showroom" để tạo showroom mới 9. Modal hiển thị form nhập: Tên showroom (bắt buộc, max 120 ký tự), Địa chỉ chi tiết (bắt buộc, max 255 ký tự), Tỉnh/Thành \* (bắt buộc, dropdown 63 tỉnh/thành), Quận/Huyện (tùy chọn, max 100 ký tự), Số điện thoại (tùy chọn, 9-20 ký tự, chỉ số +, -, (), khoảng trắng), Email (tùy chọn, format email hợp lệ), Giờ mở cửa (tùy chọn, max 120 ký tự), Mã nhúng Google Map (tùy chọn, iframe code), Checkbox "Kích hoạt ngay" 10. Nhập đầy đủ thông tin bắt buộc và click "Thêm Showroom" 11. Hệ thống kiểm tra tên showroom không trùng (không phân biệt hoa/thường) 12. Hệ thống validate số điện thoại (nếu có): chỉ chứa số, +, -, (), và khoảng trắng, độ dài 9-20 ký tự 13. Hệ thống validate email (nếu có): format email hợp lệ 14. Nếu hợp lệ, tạo showroom mới và hiển thị thông báo thành công 15. Cập nhật danh sách showroom và đóng modal, form tự động reset 16. Click "Xem" trên một showroom để xem chi tiết 17. Modal hiển thị thông tin đầy đủ: Tên showroom, Badge trạng thái, Địa chỉ đầy đủ, Số điện thoại, Email, Giờ mở cửa, Ngày tạo, Bản đồ (nếu có) 18. Click "Sửa" trên một showroom để cập nhật 19. Modal hiển thị form cập nhật với tất cả thông tin hiện tại 20. Sửa đổi thông tin và click "Cập nhật" 21. Hệ thống kiểm tra tên showroom không trùng (nếu thay đổi, không phân biệt hoa/thường) 22. Hệ thống validate số điện thoại và email (nếu có) 23. Nếu hợp lệ, cập nhật showroom và hiển thị thông báo thành công 24. Cập nhật danh sách showroom và đóng modal 25. Click "Xóa" trên một showroom 26. Modal xác nhận hiển thị: "Bạn có chắc chắn muốn xóa showroom này?" 27. Click "Xóa" để xác nhận 28. Hệ thống xóa showroom 29. Hiển thị thông báo thành công 30. Cập nhật danh sách showroom và đóng modal
Alternative 2a. Nếu chưa có showroom nào 3a. Hiển thị empty state "Chưa có showroom nào" + nút "Thêm Showroom" 2b. Nếu filter không có kết quả 3b. Hiển thị empty state "Không có kết quả phù hợp" + gợi ý thay đổi bộ lọc 5a. Bấm nút "Reset" để reset bộ lọc 6a. Xóa tất cả bộ lọc và hiển thị danh sách đầy đủ 7a. Toggle giữa Grid view và List view 8a. Hiển thị danh sách tương ứng với view đã chọn
Exception 11a. Tên showroom đã tồn tại 12a. Hiển thị thông báo "Tên showroom đã tồn tại" 13a. Giữ modal mở để sửa 12b. Số điện thoại không hợp lệ (chứa ký tự không cho phép) 13b. Hiển thị thông báo "Số điện thoại không hợp lệ (chỉ được chứa số, dấu +, -, (), và khoảng trắng)" 14b. Yêu cầu nhập lại 12c. Số điện thoại có độ dài không hợp lệ (< 9 hoặc > 20) 13c. Hiển thị thông báo "Số điện thoại phải có từ 9 đến 20 ký tự" 14c. Yêu cầu nhập lại 12d. Email không hợp lệ 13d. Hiển thị thông báo "Email không hợp lệ" 14d. Yêu cầu nhập lại 21a. Tên showroom mới đã tồn tại 22a. Hiển thị thông báo "Tên showroom đã tồn tại" 23a. Giữ modal mở để sửa 28a. Lỗi hệ thống 29a. Hiển thị thông báo "Có lỗi xảy ra" 30a. Cho phép thử lại

Bảng 3-10. Use case Quản lý showroom

---

## UC_011 - Quản lý trang tĩnh (Manage static pages)

Use case ID UC_011
Name Manage static pages
Goal Quản lý các trang tĩnh của website (Xem danh sách, thêm/sửa/xóa trang, xem chi tiết, bật/tắt hiển thị, tìm kiếm)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, danh sách trang tĩnh được hiển thị hoặc thao tác quản lý được thực hiện - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý trang tĩnh (/admin/pages) 2. Hệ thống hiển thị danh sách các trang tĩnh với thông tin: Slug, Tiêu đề, Nội dung preview, Trạng thái, Ngày cập nhật, Thao tác (Truy cập trang, Xem, Sửa, Xóa) 3. Hệ thống hiển thị thống kê dựa trên bộ lọc: Tổng trang, Đang hoạt động, Đã tắt 4. Có thể tìm kiếm theo slug hoặc tiêu đề 5. Có thể lọc theo trạng thái (Tất cả, Đang hoạt động, Đã tắt) 6. Có thể sắp xếp theo: Mới nhất, Cũ nhất, A-Z, Z-A 7. Click nút "Truy cập trang" để mở trang tĩnh ở tab mới 8. Click "Thêm Trang Tĩnh" để tạo trang mới 9. Modal hiển thị form nhập: Slug (bắt buộc, max 120 ký tự, không được bắt đầu với: login, register, logout, admin, manager, delivery, customer, profile, orders, cart, wishlist, api, auth, css, js, images, dashboard, home), Tiêu đề (bắt buộc, max 200 ký tự), Nội dung HTML (chỉ hiển thị khi chỉnh sửa), Checkbox "Kích hoạt ngay" 10. Nhập đầy đủ thông tin bắt buộc và click "Thêm Trang" 11. Hệ thống kiểm tra slug không trùng (không phân biệt hoa/thường) 12. Hệ thống kiểm tra slug không phải reserved path 13. Nếu hợp lệ, tạo trang tĩnh mới và hiển thị thông báo thành công 14. Cập nhật danh sách trang tĩnh và đóng modal, form tự động reset 15. Click "Xem" trên một trang tĩnh để xem chi tiết 16. Modal hiển thị thông tin đầy đủ: Slug, Tiêu đề, Nội dung HTML (với CSS styling), Trạng thái, Ngày cập nhật 17. Click "Sửa" trên một trang tĩnh để cập nhật 18. Modal hiển thị form cập nhật với tất cả thông tin hiện tại kể cả Quill Editor cho nội dung HTML 19. Sửa đổi thông tin và click "Cập nhật" 20. Hệ thống kiểm tra slug không trùng (nếu thay đổi, không phân biệt hoa/thường) 21. Hệ thống kiểm tra slug không phải reserved path 22. Nếu hợp lệ, cập nhật trang tĩnh và hiển thị thông báo thành công 23. Cập nhật danh sách trang tĩnh và đóng modal 24. Click "Xóa" trên một trang tĩnh 25. Modal xác nhận hiển thị: "Bạn có chắc chắn muốn xóa trang tĩnh này?" 26. Click "Xóa" để xác nhận 27. Hệ thống xóa trang tĩnh 28. Hiển thị thông báo thành công 29. Cập nhật danh sách trang tĩnh và đóng modal
Alternative 2a. Nếu chưa có trang tĩnh nào 3a. Hiển thị empty state "Chưa có trang tĩnh nào" + nút "Thêm Trang Tĩnh" 2b. Nếu filter không có kết quả 3b. Hiển thị empty state "Không tìm thấy trang tĩnh phù hợp" + gợi ý thay đổi bộ lọc 5a. Bấm nút "Reset Filters" để reset bộ lọc 6a. Xóa tất cả bộ lọc và hiển thị danh sách đầy đủ
Exception 11a. Slug đã tồn tại 12a. Hiển thị thông báo "Slug đã tồn tại" 13a. Giữ modal mở để sửa 12b. Slug không được phép (là reserved path) 13b. Hiển thị thông báo "Slug không được bắt đầu với: [reserved_path]" 14b. Yêu cầu nhập lại 20a. Slug mới đã tồn tại 21a. Hiển thị thông báo "Slug đã tồn tại" 22a. Giữ modal mở để sửa 21b. Slug mới không được phép (là reserved path) 22b. Hiển thị thông báo "Slug không được bắt đầu với: [reserved_path]" 23b. Yêu cầu nhập lại 27a. Lỗi hệ thống 28a. Hiển thị thông báo "Có lỗi xảy ra" 29a. Cho phép thử lại

Bảng 3-11. Use case Quản lý trang tĩnh

---

## UC_012 - Báo cáo và thống kê (Reports and statistics)

Use case ID UC_012
Name Reports and statistics
Goal Xem báo cáo và thống kê tổng quan về doanh thu, đơn hàng, khách hàng và sản phẩm với các bộ lọc thời gian
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, các báo cáo và biểu đồ được hiển thị - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang báo cáo (/admin/reports) 2. Hệ thống hiển thị 4 cards thống kê tổng quan: Tổng doanh thu (tính từ đơn DELIVERED trong khoảng thời gian), Tổng đơn hàng (tất cả đơn trong khoảng thời gian), Tổng khách hàng (distinct users), Tổng sản phẩm (tất cả sản phẩm trong hệ thống) 3. Hệ thống hiển thị bộ lọc thời gian: Hôm nay, Tuần này, Tháng này (mặc định), Năm nay, Tùy chỉnh (hiển thị 2 input date) 4. Mặc định hiển thị dữ liệu Tháng này (từ ngày 1 đến hôm nay) 5. Chọn bộ lọc thời gian từ dropdown 6. Nếu chọn "Tùy chỉnh": hiển thị 2 input "Từ ngày" và "Đến ngày", user nhập hoặc chọn ngày 7. Bấm nút "Cập nhật" để tải lại báo cáo 8. Hệ thống gọi API để lấy dữ liệu thống kê theo khoảng thời gian đã chọn 9. Hệ thống hiển thị biểu đồ doanh thu theo thời gian (line chart, chỉ đơn DELIVERED, nhóm theo ngày) 10. Hệ thống hiển thị biểu đồ phân bố trạng thái đơn hàng (doughnut chart, tất cả trạng thái trong khoảng thời gian) 11. Hệ thống hiển thị biểu đồ doanh thu theo danh mục cấp 2 (bar chart, tất cả danh mục có doanh thu > 0, chỉ đơn DELIVERED, sắp xếp theo doanh thu giảm dần) 12. Hệ thống hiển thị bảng Top 10 sản phẩm bán chạy nhất (rank, tên sản phẩm, tổng số lượng bán, tổng doanh thu, chỉ đơn DELIVERED) 13. Hệ thống hiển thị bảng Top 10 khách hàng mua nhiều nhất (rank, tên, email, số đơn, tổng chi, chỉ đơn DELIVERED) 14. Hệ thống hiển thị bảng đơn hàng theo khu vực (khu vực (city), số đơn, tổng doanh thu, trung bình/đơn, tỷ lệ %, chỉ đơn DELIVERED, sắp xếp theo doanh thu giảm dần) 15. Tất cả biểu đồ và bảng tự động cập nhật khi thay đổi bộ lọc thời gian
Alternative 5a. Khi thay đổi bộ lọc (không phải "Tùy chỉnh"): tự động load lại dữ liệu mà không cần bấm "Cập nhật" 6a. Chỉ khi chọn "Tùy chỉnh" mới cần bấm "Cập nhật" sau khi chọn ngày 9a. Nếu không có dữ liệu trong khoảng thời gian: hiển thị thông báo "Chưa có dữ liệu" cho các biểu đồ và bảng tương ứng
Exception 8a. Lỗi khi tải dữ liệu từ API 9a. Hiển thị thông báo "Không thể tải báo cáo" 10a. Cho phép thử lại bằng cách chọn bộ lọc khác hoặc bấm "Cập nhật" 8b. Khoảng thời gian không hợp lệ (ngày bắt đầu sau ngày kết thúc) 9b. Hiển thị thông báo "Khoảng thời gian không hợp lệ" 10b. Yêu cầu chọn lại ngày 8c. Lỗi hệ thống khi tính toán thống kê 9c. Hiển thị thông báo "Có lỗi xảy ra khi tính toán báo cáo" 10c. Cho phép thử lại

Bảng 3-12. Use case Báo cáo và thống kê

---

## UC_013 - Quản lý thông báo (Manage notifications)

Use case ID UC_013
Name Manage notifications
Goal Quản lý thông báo hệ thống cho Admin: xem danh sách, xem chi tiết, đánh dấu đã đọc, đánh dấu tất cả đã đọc, xóa từng thông báo, xóa tất cả thông báo đã đọc; badge thông báo ở header cập nhật gần realtime (polling)
Actors Quản trị viên (Admin)
Pre-conditions Đã đăng nhập vào hệ thống với vai trò ADMIN
Post-conditions - Nếu thành công, trạng thái đọc/xóa của thông báo được cập nhật và hiển thị ngay ở header - Nếu thất bại, hiển thị thông báo lỗi
Main Flow 1. Vào trang quản lý thông báo (/admin/notifications) 2. Hệ thống hiển thị 3 thẻ thống kê: Tổng, Chưa đọc, Đã đọc 3. Có bộ lọc Trạng thái (Tất cả/Chưa đọc/Đã đọc) và ô tìm kiếm theo tiêu đề/nội dung 4. Danh sách thông báo hiển thị theo thời gian tạo mới nhất: Tiêu đề, trích nội dung, thời gian tạo, trạng thái (badge) và các nút thao tác (Xem, Đánh dấu đã đọc, Xóa) 5. Click "Xem" để mở modal chi tiết: Tiêu đề, Nội dung, Thời gian tạo, Trạng thái; trong modal có nút "Đánh dấu đã đọc" (ẩn nếu đã đọc) 6. Ở danh sách, click "Đánh dấu đã đọc" trên một thông báo chưa đọc 7. Hệ thống gọi API cập nhật trạng thái đã đọc và refresh danh sách 8. Badge số lượng chưa đọc ở header cập nhật ngay sau thao tác 9. Click "Đánh dấu tất cả đã đọc" để chuyển toàn bộ thông báo sang trạng thái đã đọc 10. Hệ thống gọi API cập nhật hàng loạt, refresh danh sách và badge header 11. Click "Xóa" để xóa một thông báo 12. Xác nhận xóa, hệ thống gọi API xóa và refresh danh sách 13. Click "Xóa tất cả đã đọc" để xóa toàn bộ thông báo đã đọc 14. Hệ thống gọi API xóa hàng loạt và refresh danh sách 15. Header thông báo (badge + dropdown 5 thông báo gần nhất) tự động refresh định kỳ (30s) và hiển thị toast khi có thông báo chưa đọc mới
Alternative 3a. Reset bộ lọc: bấm "Reset" để về mặc định 4a. Không có thông báo phù hợp: hiển thị empty state "Chưa có thông báo" 5a. Mở modal từ thông báo đã đọc: ẩn nút "Đánh dấu đã đọc" 9a. Khi điều hướng AJAX: module header vẫn polling 30s để cập nhật badge 11a. Có thể truy cập trang quản lý từ dropdown header ("Xem tất cả")
Exception 7a. Lỗi khi đánh dấu đã đọc 8a. Hiển thị "Không thể đánh dấu đã đọc" 9b. Lỗi khi đánh dấu tất cả đã đọc 10b. Hiển thị "Không thể đánh dấu tất cả đã đọc" 12a. Lỗi khi xóa thông báo 12b. Hiển thị "Không thể xóa thông báo" 14a. Lỗi khi xóa tất cả đã đọc 14b. Hiển thị "Không thể xóa tất cả đã đọc" 15a. Lỗi khi tải danh sách 15b. Hiển thị "Không thể tải thông báo"

Bảng 3-13. Use case Quản lý thông báo
