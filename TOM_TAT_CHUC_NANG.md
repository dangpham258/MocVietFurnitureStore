# 📝 TÓM TẮT CHỨC NĂNG ĐẶT HÀNG

## ✅ ĐÃ HOÀN THÀNH

### Backend
1. **DTOs**: CreateOrderRequest, CreateOrderResponse, CheckoutSummaryDTO
2. **Repository**: CouponRepository, ShippingFeeRepository (với methods cần thiết)
3. **Service**: OrderServiceImpl.createOrder() với validation đầy đủ
4. **Controller**: CheckoutController với 4 endpoints
5. **Shipping Fee**: Tính phí theo province zone
6. **Coupon**: Validate coupon code với điều kiện

### Frontend  
1. **checkout.html**: Trang thanh toán đầy đủ
2. **order-confirmation.html**: Trang xác nhận đơn hàng
3. **JS**: Xử lý payment flow, coupon, shipping fee

### Luồng hoạt động
✅ Tạo đơn hàng (COD/VNPAY/MoMo)  
✅ Hủy đơn hàng (chỉ PENDING)  
✅ Tính phí ship theo zone  
✅ Validate và áp dụng coupon  
✅ Validation stock availability  

## 🚀 CÁCH CHẠY

### 1. Database Setup

```sql
-- Chạy file schema
source MocViet_Database_Schema.sql

-- Chạy file sample data (đã có đầy đủ dữ liệu: shipping, coupon, products, orders, users, ...)
source MocViet_Database_Sample.sql
```

**Lưu ý**: File `MocViet_Database_Sample.sql` đã bao gồm:
- ✅ ShippingZone & ShippingFee (3 miền)
- ✅ ProvinceZone (34 tỉnh/thành)  
- ✅ Coupon (WELCOME10, VIP20, BF30)
- ✅ Address mẫu cho customers
- ✅ Products, Cart, Orders, Reviews, etc.

### 2. Start Application

```bash
mvn spring-boot:run
```

### 3. Test Flow

#### Test Case 1: Đặt hàng COD thành công

1. **Login**: http://localhost:8080/login
2. **Thêm sản phẩm vào giỏ**: Browse products → Add to cart
3. **Vào giỏ hàng**: http://localhost:8080/customer/cart
4. **Chọn sản phẩm** → Click **"Thanh toán"**
5. **Checkout**: 
   - Chọn địa chỉ giao hàng
   - Chọn COD (mặc định)
   - Nhập coupon (optional): `WELCOME10`
   - Review tổng tiền
6. **Click "Đặt hàng"**
7. **Verify**: 
   - Redirect to `/customer/orders/{orderId}`
   - Order status = PENDING
   - Payment status = UNPAID
   - Stock đã được trừ

#### Test Case 2: Hủy đơn

1. Xem order detail page
2. Click **"Hủy đơn"**
3. Confirm
4. **Verify**: 
   - Status = CANCELLED
   - Stock đã được hoàn lại

#### Test Case 3: Validate coupon

1. Vào checkout page
2. Nhập coupon: `FLASH20`
3. Click "Áp dụng"
4. **Verify**: Discount hiển thị đúng 20%

## 📋 ENDPOINTS

### Checkout
- `GET /customer/checkout?selectedItemIds=1,2,3` - Trang checkout
- `GET /customer/checkout/shipping?addressId=1` - Tính phí ship
- `GET /customer/checkout/coupon/validate?code=WELCOME10&subtotal=1000000` - Validate coupon
- `POST /customer/checkout/create` - Tạo đơn hàng

### Orders
- `GET /customer/orders` - Danh sách đơn hàng
- `GET /customer/orders/{id}` - Chi tiết đơn hàng
- `POST /customer/orders/{id}/cancel?reason=...` - Hủy đơn

## 📊 DATABASE

### Bảng quan trọng
- `Orders`: Thông tin đơn hàng
- `OrderItems`: Chi tiết sản phẩm trong đơn
- `OrderStatusHistory`: Lịch sử trạng thái
- `Address`: Địa chỉ giao hàng
- `Coupon`: Mã giảm giá
- `ProvinceZone`: Map tỉnh → zone
- `ShippingFee`: Phí ship theo zone
- `ProductVariant`: Tồn kho

### Stored Procedures
- `sp_CreateOrder`: Tạo đơn hàng (đã có trong schema)
- `sp_CancelOrder`: Hủy đơn hàng
- `sp_AutoCancelUnpaidOnline`: Auto-cancel đơn timeout
- `sp_HandlePaymentWebhook`: Xử lý webhook thanh toán

## ⚠️ LƯU Ý

### 1. Dữ liệu
- Phải có ít nhất 1 địa chỉ giao hàng cho customer
- Province trong Address phải được map vào ProvinceZone
- ShippingFee phải có cho mỗi zone

### 2. Payment Integration
- VNPAY/MoMo hiện chỉ có placeholder
- Cần config API key riêng để tích hợp
- Xem file `HUONG_DAN_DAT_HANG.md` section 3

### 3. Security
- Routes `/customer/**` yêu cầu authentication
- Chỉ chủ đơn mới hủy được đơn
- Validate address thuộc về current user

## 🔧 CONFIG CẦN THIẾT

### application.properties
```properties
# Database
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MocViet
spring.datasource.username=sa
spring.datasource.password=yourpassword

# Session
server.session.timeout=30m

# Thymeleaf
spring.thymeleaf.cache=false
```

### Security Config
- Customer chỉ truy cập được `/customer/**`
- Cần login để checkout

## 📈 CÁC TÍNH NĂNG MỞ RỘNG

### Có thể thêm sau:
1. **VNPAY Integration**: 
   - Tạo `PaymentService`
   - Generate payment URL
   - Handle webhook
   
2. **MoMo Integration**:
   - Similar to VNPAY
   
3. **Email Notifications**:
   - Gửi email khi tạo đơn
   - Gửi email khi thay đổi trạng thái
   
4. **Order Tracking**:
   - Real-time tracking
   - SMS notifications
   
5. **Multiple Shipping Methods**:
   - Standard / Express
   - Tính phí theo method

## 🐛 TROUBLESHOOTING

### Lỗi: "Tỉnh/thành chưa được map vào miền giao hàng"
**Giải pháp**: Chạy `seed_checkout_data.sql` để insert ProvinceZone

### Lỗi: "Không tìm thấy phí vận chuyển"
**Giải pháp**: Insert ShippingFee cho mỗi zone

### Lỗi: "Giỏ hàng trống"
**Giải pháp**: Check xem CartItem có được fetch đúng không

### Lỗi: "Tồn kho không đủ"
**Giải pháp**: Check ProductVariant.stock_qty

## 📞 SUPPORT

Nếu có vấn đề, kiểm tra:
1. Database connection
2. Spring Security config
3. Data trong các bảng: ProvinceZone, ShippingFee, Coupon
4. Log trong console

---

**Version**: 1.0  
**Last Updated**: 2024  
**Author**: AI Assistant

