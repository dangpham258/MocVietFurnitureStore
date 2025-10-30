# MocVietFurnitureStore

<p align="center">
  <img src="src/main/resources/static/images/logo/MVlogo.jpg" alt="Logo Mộc Việt" width="120">
</p>

## 🏠 Website Bán Đồ Nội Thất Mộc Việt

**Thông tin đề tài:**
- Xây dựng website bán đồ nội thất "Mộc Việt" bằng Spring Boot, Thymeleaf, Bootstrap, JPA, SQLServer, JWT.
- Hỗ trợ các vai trò: Khách hàng (CUSTOMER), Quản lý (MANAGER), Quản trị viên (ADMIN), Nhân viên giao hàng (DELIVERY).

---

![Trang chủ Mộc Việt](src/main/resources/static/images/logo/TheBackground.jpg)

---

## 🌟 Tính Năng Chính
- Duyệt/bộ lọc sản phẩm, chi tiết sản phẩm, phân loại (Phòng khách, phòng ngủ, bếp...)
- Giỏ hàng, đặt và thanh toán đơn hàng (COD, tích hợp placeholder VNPAY/MoMo)
- Tính phí ship theo tỉnh/thành/miền (Shipping Zone)
- Áp dụng & quản lý mã giảm giá (Coupon)
- Đánh giá bình luận sản phẩm
- Theo dõi, lịch sử đơn hàng, hủy đơn, auto-cancel
- Quản lý kho, tồn kho sản phẩm
- Quản trị sản phẩm, bài viết, tin tức, người dùng, phân công đội giao hàng
- Thông báo (notification), chatbox real-time, wishlist, quản lý hồ sơ
- Đăng ký, đăng nhập, xác thực JWT, phân quyền truy cập

---

## 🛠️ Technology Stack
- **Backend:** [Spring Boot](https://spring.io/projects/spring-boot) 3, [Spring Data JPA](https://spring.io/projects/spring-data-jpa) (SQLServer), Spring Security, JWT, WebSocket (STOMP)
- **Frontend:** [Thymeleaf](https://www.thymeleaf.org/) template engine, [Bootstrap 5](https://getbootstrap.com/), [Bootstrap Icons](https://icons.getbootstrap.com/)
- **Database:** Microsoft SQL Server (sử dụng các script schema & sample SQL)
- **Khác:** Spring Mail, file upload, Lombok, Validation, DevTools, layout-dialect (Thymeleaf)
- **Config nổi bật:** JWT, upload, mail, session, integration thanh toán (VNPAY/MoMo placeholders)

---

## 🧭 Cấu Trúc Dự Án Tiêu Biểu

```
MocVietFurnitureStore/
├── [src/main/java/mocviet/](src/main/java/mocviet/)                   # Backend source code
│   ├── [controller](src/main/java/mocviet/controller/)               # REST/view controllers
│   ├── [service](src/main/java/mocviet/service/)
│   ├── [repository](src/main/java/mocviet/repository/)
│   ├── [entity](src/main/java/mocviet/entity/)
│   ├── [dto](src/main/java/mocviet/dto/)
│   ├── [security](src/main/java/mocviet/security/)
│   ├── [config](src/main/java/mocviet/config/)
│   └── [MocVietFurnitureStoreApplication.java](src/main/java/mocviet/MocVietFurnitureStoreApplication.java)
│
├── [src/main/resources/templates/](src/main/resources/templates/)     # Giao diện Thymeleaf giao diện nhiều vai
│   ├── [admin](src/main/resources/templates/admin/)  
│   ├── [manager](src/main/resources/templates/manager/)
│   ├── [delivery](src/main/resources/templates/delivery/)
│   ├── [customer](src/main/resources/templates/customer/)
│   ├── [fragments](src/main/resources/templates/fragments/)
│   └── [index.html](src/main/resources/templates/index.html)
│
├── [src/main/resources/static/](src/main/resources/static/)
│   ├── [images](src/main/resources/static/images/)
│   ├── [css](src/main/resources/static/css/)
│   └── [js](src/main/resources/static/js/)
│
├── [src/main/resources/application.properties](src/main/resources/application.properties)
├── [pom.xml](pom.xml)
├── [README.md](README.md)
├── [MocViet_Database_Schema.sql](MocViet_Database_Schema.sql)
├── [MocViet_Database_Sample.sql](MocViet_Database_Sample.sql)
├── [TOM_TAT_CHUC_NANG.md](TOM_TAT_CHUC_NANG.md)   # Tổng hợp chức năng
├── [Đặc tả use case quản lý đơn hàng Manager.md](Đặc%20tả%20use%20case%20quản%20lý%20đơn%20hàng%20Manager.md)
└── ...
```

---

## :mag_right: [Xem nhanh các file đặc biệt](#)
- :notebook: [Tổng tóm tắt chức năng](TOM_TAT_CHUC_NANG.md)
- :scroll: [Hướng dẫn đặt hàng](HUONG_DAN_DAT_HANG.md)
- :floppy_disk: [Schema DB gốc](MocViet_Database_Schema.sql)
- :floppy_disk: [Seed sample DB](MocViet_Database_Sample.sql)
- :hammer: [Cấu hình ứng dụng](src/main/resources/application.properties)
- :page_with_curl: [Đặc tả usecase: Quản lý đơn hàng](Đặc%20tả%20use%20case%20quản%20lý%20đơn%20hàng%20Manager.md)
- :page_with_curl: [Đặc tả usecase: Quản lý sản phẩm](Đặc%20tả%20use%20case%20quản%20lý%20sản%20phẩm%20Manager.md)

---

## 🚦 Hướng Dẫn Cài Đặt & Chạy Dự Án
### 1️⃣ Cài đặt database
- Khởi tạo SQLServer, tạo database tên `MocViet`.
- Chạy file [`MocViet_Database_Schema.sql`](MocViet_Database_Schema.sql) tạo bảng, stored procedure...
- Chạy file [`MocViet_Database_Sample.sql`](MocViet_Database_Sample.sql) để seed dữ liệu mẫu (sản phẩm, tài khoản, coupon, fee, zone...)

### 2️⃣ Config ứng dụng
Sửa [`application.properties`](src/main/resources/application.properties):
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MocViet
spring.datasource.username=sa
spring.datasource.password=your_password
jwt.secret=... # giữ bí mật
```

### 3️⃣ Build và chạy ứng dụng
```bash
cd MocVietFurnitureStore
mvn spring-boot:run
# hoặc build: mvn clean package
```
- Truy cập: [http://localhost:8080](http://localhost:8080)

---

## 🚀 Demo Giao diện
<p align="center">
  <img src="src/main/resources/static/images/logo/TheBackground.jpg" alt="Trang chủ Mộc Việt" width="500">
</p>

---

## ⚡ Một Số Endpoint Tiêu Biểu
- `GET /customer/checkout?selectedItemIds=...` - Trang thanh toán
- `GET /customer/checkout/shipping?addressId=...` - Tính phí ship
- `GET /customer/checkout/coupon/validate?code=XXX` - Kiểm tra mã giảm giá
- `POST /customer/checkout/create` - Tạo đơn hàng
- `GET /customer/orders` - Lịch sử đơn hàng cá nhân
- `POST /customer/orders/{id}/cancel` - Hủy đơn
- `/admin/*`, `/manager/*`, `/delivery/*` - Quản trị & nội bộ

---

## 👤 Thành Viên Nhóm
- 23110203 - Phạm Trần Thiên Đăng
- 23110280 - Huỳnh Thanh Nhân
- 23110319 - Hồ Minh Tiến Thành
- 23110327 - Huỳnh Ngọc Thắng

---

## 📚 Tham Khảo & Tài Liệu Dự Án
- Đặc tả usecase (thư mục gốc):
    - [Đặc tả use case quản lý đơn hàng Manager.md](Đặc%20tả%20use%20case%20quản%20lý%20đơn%20hàng%20Manager.md)
    - [Đặc tả use case quản lý sản phẩm Manager.md](Đặc%20tả%20use%20case%20quản%20lý%20sản%20phẩm%20Manager.md)
    - [Đặc tả use case quản lý đánh giá bình luận Manager.md](Đặc%20tả%20use%20case%20quản%20lý%20đánh%20giá%20bình%20luận%20Manager.md)
    - ...
- [TOM_TAT_CHUC_NANG.md](TOM_TAT_CHUC_NANG.md) - Tổng kết chức năng, seed/test case
- [HUONG_DAN_DAT_HANG.md](HUONG_DAN_DAT_HANG.md) - Hướng dẫn ngiệp vụ đặt hàng chi tiết

---

## 🛡️ Bảo mật & Lưu ý
- Phân quyền nghiêm ngặt: ADMIN, MANAGER, CUSTOMER, DELIVERY
- JWT secret cần giữ kín, thay đổi khi deploy production
- Không để password DB/script sensitive trên Git public
- Đường `/customer/**` bắt buộc login
- Validate dữ liệu nhập, user chỉ được thao tác đơn của mình

## 🐛 Troubleshooting
- Xem log console khi lỗi
- Đảm bảo config DB đúng, port/chặn tường lửa
- Kiểm tra lại seed dữ liệu nếu bị lỗi fee/coupon/zone
- Xem thêm mục "TROUBLESHOOTING" trong [TOM_TAT_CHUC_NANG.md](TOM_TAT_CHUC_NANG.md)

---

<p align="center"><b>Version:</b> 1.0 (2024) &nbsp;|&nbsp; <b>Powered by Nhóm 5 - Mộc Việt</b></p>
