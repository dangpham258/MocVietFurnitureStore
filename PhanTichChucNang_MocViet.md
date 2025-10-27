# 3.1. PHÂN TÍCH CHỨC NĂNG HỆ THỐNG MỘC VIỆT

## 3.1.1. Phía Khách (Guest)

| STT | Chức năng             | Mô tả chi tiết                                                                                                                                                                                                     |
| --- | --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | Đăng ký               | Đăng ký tạo tài khoản với email, mật khẩu, thông tin cá nhân. Hệ thống gửi OTP xác thực qua email                                                                                                                  |
| 2   | Xem/Tìm kiếm sản phẩm | Tìm kiếm theo tên, xem danh sách theo danh mục/bộ sưu tập, lọc theo giá (min-max), màu sắc, kích thước. Sắp xếp theo: nổi bật, giá tăng/giảm, mới nhất, bán chạy                                                   |
| 3   | Xem trang chủ         | Xem banner carousel, top 10 sản phẩm nổi bật (theo views), sản phẩm mới (theo created_at), sản phẩm khuyến mãi (% discount cao), sản phẩm bán chạy (theo sold_qty), best reviews (5 sao, mỗi SP 1 review mới nhất) |
| 4   | Xem tin tức           | Xem danh sách bài viết theo loại: MEDIA (phong cách), NEWS (xu hướng), PEOPLE (nghệ nhân). Phân trang 20 bài/trang                                                                                                 |
| 5   | Xem chính sách        | Xem các trang tĩnh: chính sách bán hàng, giao hàng và lắp đặt, đổi trả, bảo hành. Nội dung HTML với CSS và hình ảnh                                                                                                |
| 6   | Liên hệ               | Gửi tin nhắn qua chatbox để được tư vấn. Có thể đính kèm hình ảnh. Tạo conversation mới                                                                                                                            |
| 7   | Xem showroom          | Xem thông tin địa chỉ các cửa hàng trưng bày: tên, địa chỉ, SĐT, email, giờ mở cửa, map embed                                                                                                                      |
| 8   | Liên kết mạng xã hội  | Truy cập Facebook, Zalo, Youtube của cửa hàng qua các link được cấu hình                                                                                                                                           |

## 3.1.2. Phía Người mua (Customer)

| STT | Chức năng                 | Mô tả chi tiết                                                                                                                                             |
| --- | ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Đăng nhập                 | Đăng nhập bằng username/email và mật khẩu. Hệ thống ghi nhớ phiên đăng nhập                                                                                |
| 2   | Đăng xuất                 | Đăng xuất khỏi hệ thống, xóa session và cookie                                                                                                             |
| 3   | Quên mật khẩu             | Nhập email → hệ thống gửi OTP → nhập OTP → tạo mật khẩu mới                                                                                                |
| 4   | Quản lý tài khoản cá nhân | Thay đổi mật khẩu, cập nhật họ tên, giới tính, ngày sinh, SĐT                                                                                              |
| 5   | Quản lý địa chỉ cá nhân   | Xem danh sách địa chỉ, thêm/sửa/xóa địa chỉ giao hàng, đặt địa chỉ mặc định. Mỗi địa chỉ có: tên người nhận, SĐT, địa chỉ chi tiết, tỉnh/thành, quận/huyện |
| 6   | Quản lý giỏ hàng          | Xem tổng quan giỏ hàng, thêm sản phẩm với số lượng, cập nhật số lượng, xóa sản phẩm. Kiểm tra tồn kho real-time                                            |
| 7   | Đặt hàng                  | Chọn địa chỉ giao hàng, áp dụng mã giảm giá (validate ngưỡng tối thiểu), chọn phương thức thanh toán (COD/VNPAY/MOMO), xác nhận đơn hàng → PENDING         |
| 8   | Quản lý đơn hàng          | Xem lịch sử đơn hàng theo thời gian, theo dõi trạng thái đơn hàng (PENDING/CONFIRMED/DISPATCHED/DELIVERED/CANCELLED/RETURNED), hủy đơn (chỉ khi PENDING)   |
| 9   | Xem chi tiết đơn hàng     | Xem thông tin chi tiết: danh sách sản phẩm với số lượng và giá tại thời điểm mua, tổng tiền, địa chỉ giao, trạng thái, lịch sử thay đổi trạng thái         |
| 10  | Đánh giá sản phẩm         | Đánh giá sản phẩm đã mua và giao thành công (DELIVERED). Rating 1-5 sao, nội dung đánh giá, đính kèm 1 hình ảnh. Mỗi lần mua có thể đánh giá riêng         |
| 11  | Quản lý yêu thích         | Thêm/xóa sản phẩm vào wishlist, xem danh sách yêu thích, nhận thông báo khi sản phẩm từ hết hàng → có hàng trở lại                                         |
| 12  | Lịch sử xem               | Xem lịch sử 20 sản phẩm đã xem gần nhất, sắp xếp theo thời gian xem                                                                                        |
| 13  | Chat hỗ trợ               | Gửi tin nhắn cho manager qua chatbox, đính kèm hình ảnh. Xem lịch sử conversation                                                                          |
| 14  | Nhận thông báo            | Nhận thông báo về: đặt hàng thành công, thay đổi trạng thái đơn hàng, sản phẩm yêu thích có hàng trở lại, phản hồi từ cửa hàng về đánh giá                 |

## 3.1.3. Phía Quản lý cửa hàng (Manager)

| STT | Chức năng                  | Mô tả                                                                                          |
| --- | -------------------------- | ---------------------------------------------------------------------------------------------- |
| 1   | Quản lý tài khoản bản thân | Cập nhật hồ sơ hoặc đổi mật khẩu tài khoản bản thân                                            |
| 2   | Quản lý sản phẩm           | Thêm/sửa/vô hiệu hóa sản phẩm, quản lý biến thể (màu sắc, kích thước), cập nhật giá và tồn kho |
| 3   | Quản lý đơn hàng           | Xác nhận đơn (PENDING→CONFIRMED), hủy đơn của customer, xem các đơn hàng đã hoàn thành         |
| 4   | Phân công đội giao hàng    | Phân công đơn hàng cho đội giao hàng phù hợp theo khu vực                                      |
| 5   | Quản lý tin tức            | Thêm/sửa/ẩn bài viết của chính mình, xem danh sách bài viết đã đăng                            |
| 6   | Quản lý đánh giá/bình luận | Xem tất cả đánh giá, trả lời đánh giá của khách hàng, ẩn/hiện đánh giá                         |
| 7   | Trả lời tin nhắn           | Trả lời các tin nhắn từ chatbox của guest và customer                                          |
| 8   | Quản lý tồn kho            | Nhận thông báo khi tồn kho thấp hoặc hết hàng, cập nhật số lượng tồn                           |
| 9   | Báo cáo cơ bản             | Doanh thu theo ngày/tháng, sản phẩm bán chạy, đơn hàng theo trạng thái                         |

## 3.1.4. Phía Admin

| STT | Chức năng                      | Mô tả                                                                                      |
| --- | ------------------------------ | ------------------------------------------------------------------------------------------ |
| 1   | Quản lý tài khoản bản thân     | Cập nhật hồ sơ hoặc đổi mật khẩu tài khoản bản thân                                        |
| 2   | Quản lý tài khoản hệ thống     | Tìm kiếm, xem danh sách tất cả User, xem chi tiết thông tin User, khóa/mở khóa tài khoản   |
| 3   | Quản lý danh mục sản phẩm      | Thêm/sửa/vô hiệu hóa danh mục cấp 1, 2, quản lý bộ sưu tập (Collections)                   |
| 4   | Quản lý màu sắc                | Thêm/sửa/vô hiệu hóa màu sắc sản phẩm                                                      |
| 5   | Quản lý chi phí vận chuyển     | Cập nhật phí vận chuyển theo khu vực, quản lý mapping tỉnh/thành vào zone                  |
| 6   | Quản lý đội giao hàng          | Tạo/quản lý đội giao hàng, phân công đội theo khu vực                                      |
| 7   | Quản lý mã giảm giá            | Thêm/sửa/vô hiệu hóa mã giảm giá, thiết lập giá trị đơn hàng tối thiểu, thời gian hiệu lực |
| 8   | Quản lý banner                 | Thêm/sửa/xóa banner trang chủ                                                              |
| 9   | Quản lý trang tĩnh             | Thêm/sửa/xóa các trang chính sách, giới thiệu                                              |
| 10  | Quản lý showroom               | Thêm/sửa/xóa thông tin cửa hàng trưng bày                                                  |
| 11  | Quản lý liên kết mạng xã hội   | Cập nhật link Facebook, Zalo, Youtube                                                      |
| 12  | Báo cáo và thống kê doanh thu  | Doanh thu theo ngày/tuần/tháng/năm, theo danh mục sản phẩm                                 |
| 13  | Báo cáo và thống kê đơn hàng   | Tổng số đơn hàng theo trạng thái, giá trị đơn hàng trung bình, tỷ lệ hủy đơn/trả hàng      |
| 14  | Báo cáo và thống kê khách hàng | Số lượng khách hàng mới, khách hàng mua nhiều nhất (top 10)                                |
| 15  | Báo cáo và thống kê sản phẩm   | Sản phẩm bán chạy nhất, tồn kho nhiều, đánh giá cao nhất, lượt xem cao nhất                |
| 16  | Nhận thông báo                 | Nhận thông báo khi có bài viết mới từ manager                                              |

## 3.1.5. Phía Delivery (Đội ngũ vận chuyển)

| STT | Chức năng                     | Mô tả                                                                         |
| --- | ----------------------------- | ----------------------------------------------------------------------------- |
| 1   | Quản lý tài khoản bản thân    | Cập nhật hồ sơ hoặc đổi mật khẩu tài khoản bản thân                           |
| 2   | Quản lý đơn hàng được giao    | Xem danh sách đơn hàng được phân công cho đội của mình                        |
| 3   | Xem chi tiết đơn hàng         | Xem thông tin sản phẩm, địa chỉ giao, thông tin khách hàng, ghi chú           |
| 4   | Cập nhật trạng thái giao hàng | Cập nhật trạng thái: RECEIVED → IN_TRANSIT → DONE                             |
| 5   | Thu hồi hàng                  | Cập nhật trạng thái RETURN_PICKUP khi cần thu hồi hàng                        |
| 6   | Xác nhận giao hàng            | Chụp ảnh bàn giao, ghi chú vấn đề phát sinh                                   |
| 7   | Liên hệ khách hàng            | Xem số điện thoại khách hàng để liên hệ                                       |
| 8   | Báo cáo và thống kê           | Xem lịch sử đơn hàng đã giao, thống kê số đơn hoàn thành theo ngày/tuần/tháng |
| 9   | Nhận thông báo                | Nhận thông báo khi được phân công đơn mới, yêu cầu thu hồi                    |

## 3.1.6. Hệ thống thông báo tự động

| STT | Loại thông báo               | Người nhận | Điều kiện kích hoạt                          |
| --- | ---------------------------- | ---------- | -------------------------------------------- |
| 1   | Đặt hàng thành công          | Customer   | Khi tạo đơn hàng mới                         |
| 2   | Thay đổi trạng thái đơn hàng | Customer   | Khi đơn hàng chuyển trạng thái               |
| 3   | Sản phẩm có hàng trở lại     | Customer   | Khi sản phẩm yêu thích từ hết hàng → có hàng |
| 4   | Phản hồi từ cửa hàng         | Customer   | Khi manager trả lời đánh giá                 |
| 5   | Đơn hàng mới cần xử lý       | Manager    | Khi có đơn hàng mới                          |
| 6   | Đánh giá mới                 | Manager    | Khi có đánh giá mới từ khách hàng            |
| 7   | Tồn kho thấp/Hết hàng        | Manager    | Khi tồn kho xuống thấp hoặc hết              |
| 8   | Tin nhắn mới từ khách        | Manager    | Khi có tin nhắn từ guest/customer            |
| 9   | Bài viết mới                 | Admin      | Khi manager tạo bài viết mới                 |
| 10  | Phân công đơn giao           | Delivery   | Khi được phân công đơn hàng mới              |
| 11  | Yêu cầu thu hồi              | Delivery   | Khi đơn hàng cần thu hồi                     |

## 3.1.7. Quy trình nghiệp vụ chính

### 3.1.7.1. Quy trình đặt hàng và thanh toán

**Bước 1: Khách hàng duyệt sản phẩm**

- Xem danh sách sản phẩm theo danh mục/bộ sưu tập
- Lọc theo giá, màu sắc, kích thước
- Xem chi tiết sản phẩm: hình ảnh, mô tả, giá, tồn kho
- Thêm vào giỏ hàng với số lượng mong muốn

**Bước 2: Quản lý giỏ hàng**

- Xem tổng quan giỏ hàng
- Cập nhật số lượng sản phẩm
- Xóa sản phẩm khỏi giỏ
- Kiểm tra tồn kho real-time

**Bước 3: Thanh toán**

- Chọn địa chỉ giao hàng (hoặc thêm mới)
- Áp dụng mã giảm giá (nếu có)
- Chọn phương thức thanh toán: COD/VNPAY/MOMO
- Xác nhận đơn hàng → Trạng thái PENDING

**Bước 4: Xử lý đơn hàng**

- Manager nhận thông báo đơn mới
- Kiểm tra tồn kho và xác nhận đơn → CONFIRMED
- Chuẩn bị hàng và phân công đội giao → DISPATCHED
- Delivery nhận hàng và giao → DELIVERED

**Bước 5: Hoàn tất**

- Customer nhận hàng và thanh toán (nếu COD)
- Đánh giá sản phẩm (tùy chọn)
- Đơn hàng hoàn tất

### 3.1.7.2. Quy trình quản lý tồn kho

**Tự động cập nhật tồn:**

- Trừ tồn khi đặt hàng thành công
- Cộng tồn khi hủy đơn (PENDING → CANCELLED)
- Cộng tồn khi trả hàng (DELIVERED → RETURNED)

**Cảnh báo tồn kho:**

- Tồn kho ≤ 5: Thông báo "Tồn kho thấp"
- Tồn kho = 0: Thông báo "Hết hàng"
- Tồn kho từ 0 → >0: Thông báo cho khách có trong wishlist

**Quản lý thủ công:**

- Manager nhập hàng mới: cập nhật stock_qty
- Manager điều chỉnh giá: cập nhật price, discount_percent
- Manager vô hiệu hóa sản phẩm: set is_active = 0

### 3.1.7.3. Quy trình xử lý đơn hàng

**Đơn hàng PENDING:**

- Customer có thể hủy đơn
- Manager có thể hủy đơn (với lý do)
- Manager xác nhận đơn → CONFIRMED

**Đơn hàng CONFIRMED:**

- Manager chuẩn bị hàng
- Manager phân công đội giao → DISPATCHED
- Tạo OrderDelivery với trạng thái IN_TRANSIT

**Đơn hàng DISPATCHED:**

- Delivery nhận hàng và đi giao
- Cập nhật trạng thái giao hàng
- Chụp ảnh bàn giao (nếu cần)
- Hoàn thành giao hàng → DELIVERED

**Đơn hàng DELIVERED:**

- Customer có thể yêu cầu trả hàng
- Manager xử lý trả hàng → RETURNED
- Customer có thể đánh giá sản phẩm

### 3.1.7.4. Quy trình quản lý đánh giá

**Customer đánh giá:**

- Chỉ đánh giá sản phẩm đã mua và giao thành công
- Rating từ 1-5 sao (số nguyên)
- Có thể đính kèm 1 hình ảnh
- Nội dung đánh giá (tùy chọn)
- Mỗi lần mua có thể đánh giá riêng (gắn với order_item_id cụ thể)

**Manager xử lý đánh giá:**

- Xem tất cả đánh giá mới
- Trả lời đánh giá của khách hàng
- Ẩn/hiện đánh giá không phù hợp
- Hệ thống tự động cập nhật avg_rating và total_reviews

### 3.1.7.5. Quy trình chat hỗ trợ

**Guest/Customer gửi tin nhắn:**

- Tạo conversation mới (nếu chưa có)
- Gửi tin nhắn với nội dung và đính kèm hình (tùy chọn)
- Hệ thống thông báo cho Manager

**Manager trả lời:**

- Xem danh sách conversation chưa trả lời
- Trả lời tin nhắn của khách
- Đóng conversation khi hoàn tất

### 3.1.7.6. Quy trình quản lý mã giảm giá

**Tạo mã giảm giá:**

- Admin tạo mã với % giảm giá
- Thiết lập thời gian hiệu lực
- Thiết lập giá trị đơn hàng tối thiểu
- Kích hoạt/vô hiệu hóa mã

**Sử dụng mã giảm giá:**

- Customer nhập mã khi thanh toán
- Hệ thống validate mã còn hiệu lực
- Tính toán số tiền giảm giá
- Áp dụng vào tổng tiền đơn hàng

### 3.1.7.7. Quy trình quản lý bài viết

**Manager tạo bài viết:**

- Chọn loại bài viết: MEDIA/NEWS/PEOPLE
- Viết tiêu đề, tóm tắt, nội dung
- Upload ảnh thumbnail và ảnh nội dung
- Xuất bản bài viết

**Hệ thống xử lý:**

- Thông báo cho Admin khi có bài mới
- Hiển thị bài viết trên trang tin tức
- Tính lượt xem bài viết

### 3.1.7.8. Quy trình phân công giao hàng

**Phân công đội giao:**

- Manager chọn đơn hàng cần giao
- Hệ thống tự động gợi ý đội giao theo khu vực
- Manager xác nhận phân công
- Tạo OrderDelivery với trạng thái RECEIVED

**Delivery xử lý:**

- Nhận thông báo đơn hàng mới
- Xem chi tiết đơn hàng và địa chỉ giao
- Cập nhật trạng thái: RECEIVED → IN_TRANSIT → DONE
- Chụp ảnh bàn giao và ghi chú

### 3.1.7.9. Quy trình xử lý trả hàng

**Yêu cầu trả hàng:**

- Customer liên hệ qua chat hoặc hotline
- Manager xem xét lý do trả hàng
- Quyết định chấp nhận/từ chối trả hàng

**Xử lý trả hàng:**

- Manager cập nhật đơn hàng → RETURNED
- Delivery thu hồi hàng (RETURN_PICKUP)
- Hệ thống tự động cộng lại tồn kho
- Hoàn tiền cho khách hàng (nếu đã thanh toán)

### 3.1.7.10. Quy trình báo cáo và thống kê

**Báo cáo doanh thu:**

- Doanh thu theo ngày/tuần/tháng/năm
- Doanh thu theo danh mục sản phẩm
- So sánh doanh thu các kỳ

**Báo cáo đơn hàng:**

- Tổng số đơn hàng theo trạng thái
- Giá trị đơn hàng trung bình
- Tỷ lệ hủy đơn/trả hàng
- Đơn hàng theo khu vực

**Báo cáo khách hàng:**

- Số lượng khách hàng mới
- Top khách hàng mua nhiều nhất
- Phân tích hành vi mua hàng

**Báo cáo sản phẩm:**

- Sản phẩm bán chạy nhất
- Sản phẩm tồn kho nhiều
- Sản phẩm có đánh giá cao nhất
- Sản phẩm có lượt xem cao nhất

## 3.1.8. Các tính năng bổ sung và đặc biệt

### 3.1.8.1. Hệ thống phân quyền và bảo mật

**Phân quyền theo vai trò:**

- **ADMIN**: Toàn quyền hệ thống, quản lý tài khoản, cấu hình hệ thống
- **MANAGER**: Quản lý sản phẩm, đơn hàng, tin tức, chat hỗ trợ
- **DELIVERY**: Quản lý đơn giao hàng được phân công
- **CUSTOMER**: Mua hàng, đánh giá, chat hỗ trợ

**Bảo mật:**

- Mật khẩu được hash bằng Bcrypt
- OTP xác thực qua email cho đăng ký và quên mật khẩu
- Session management và cookie security
- Input validation và SQL injection prevention

### 3.1.8.2. Hệ thống thông báo thông minh

**Chống spam thông báo:**

- Tin nhắn chat: 1 thông báo/manager/cuộc hội thoại trong 5 phút
- Tồn kho thấp: 1 thông báo/SKU trong 12 giờ
- Sản phẩm có hàng: 1 thông báo/sản phẩm trong 24 giờ
- Phân công giao: 1 thông báo/đơn trong 10 phút

**Tùy chỉnh thông báo:**

- Customer có thể bật/tắt từng loại thông báo
- Manager có thể cấu hình ngưỡng cảnh báo tồn kho
- Admin có thể cấu hình tần suất thông báo hệ thống

### 3.1.8.3. Tối ưu hiệu suất và SEO

**Caching và tối ưu:**

- Cache danh sách sản phẩm theo danh mục
- Cache thông tin banner và trang tĩnh
- Lazy loading cho hình ảnh sản phẩm
- CDN cho static files (hình ảnh, CSS, JS)

**SEO:**

- URL thân thiện: /products/slug, /collections/slug
- Meta tags động cho từng trang
- Sitemap tự động cập nhật
- Schema markup cho sản phẩm và đánh giá

### 3.1.8.4. Tích hợp thanh toán

**COD (Cash on Delivery):**

- Thanh toán khi nhận hàng
- Không cần tích hợp gateway
- Phí ship có thể tính riêng

**VNPAY:**

- Tích hợp API VNPAY
- Hỗ trợ thẻ ATM, Visa, Mastercard
- Webhook xác nhận thanh toán

**MOMO:**

- Tích hợp API MoMo
- QR code thanh toán
- Xác nhận thanh toán real-time

### 3.1.8.5. Quản lý hình ảnh và media

**Cấu trúc thư mục:**

```
/static/images/
├── products/          # Ảnh sản phẩm
├── articles/          # Ảnh bài viết
├── banners/           # Banner trang chủ
├── reviews/           # Ảnh đánh giá
├── deliveries/        # Ảnh giao hàng
├── messages/          # Ảnh chat
└── pages/             # Ảnh trang tĩnh
```

**Quy tắc upload:**

- Định dạng: JPG, PNG, WebP
- Kích thước tối đa: 2MB/ảnh
- Tự động resize và optimize
