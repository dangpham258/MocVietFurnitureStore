# 📋 HƯỚNG DẪN CHỨC NĂNG ĐẶT HÀNG

## ✅ NHỮNG GÌ ĐÃ HOÀN THÀNH

### 1. Backend Services & Controllers

#### ✅ DTOs (Data Transfer Objects)
- **`CreateOrderRequest.java`**: Request tạo đơn hàng với addressId, couponCode, paymentMethod, items
- **`CreateOrderResponse.java`**: Response trả về orderId, status, payment info
- **`CheckoutSummaryDTO.java`**: Dữ liệu checkout summary cho frontend

#### ✅ Repository mới
- **`CouponRepository.java`**: Validate mã giảm giá với điều kiện thời gian và số tiền
- **`ShippingFeeRepository.java`**: Tính phí vận chuyển theo zone
- Cập nhật **`ProvinceZoneRepository.java`**: Thêm method findZoneIdByProvinceName

#### ✅ Service Implementation
- **`OrderServiceImpl.createOrder()`**: Logic tạo đơn hàng đầy đủ:
  - Validate address và payment method
  - Group items theo variant (tránh trùng)
  - Validate stock availability
  - Tính subtotal, discount (theo coupon), shipping fee
  - Tạo order và order items
  - Trừ tồn kho
  - Ghi status history
  - Return order info

#### ✅ Controller
- **`CheckoutController.java`**:
  - `GET /customer/checkout` - Trang checkout
  - `GET /customer/checkout/shipping` - Tính phí ship (placeholder)
  - `GET /customer/checkout/coupon/validate` - Validate coupon (placeholder)
  - `POST /customer/checkout/create` - Tạo đơn hàng

### 2. Frontend Pages

#### ✅ Trang Checkout (`checkout.html`)
- Form chọn địa chỉ giao hàng (list from user addresses)
- Chọn phương thức thanh toán (COD/VNPAY/MoMo)
- Nhập mã coupon
- Tóm tắt đơn hàng (danh sách sản phẩm, subtotal, discount, shipping, total)
- Nút "Đặt hàng" với validation đầy đủ

#### ✅ Trang Order Confirmation (`order-confirmation.html`)
- Hiển thị thông báo đặt hàng thành công
- Chi tiết đơn hàng (items, address, payment info)
- Nút "Hủy đơn" (chỉ khi PENDING)
- Link xem tất cả đơn hàng

### 3. Luồng hoạt động

#### Luồng chính (COD):
1. User chọn sản phẩm trong giỏ → Click "Thanh toán"
2. Redirect đến `/customer/checkout` với items đã chọn
3. User chọn địa chỉ, payment method, coupon (nếu có)
4. Click "Đặt hàng" → POST `/customer/checkout/create`
5. Backend tạo order (status=PENDING, payment_status=UNPAID)
6. Redirect đến `/customer/orders/{orderId}` với thông báo success

#### Luồng hủy đơn:
1. User xem order detail → Click "Hủy đơn"
2. Confirm modal → POST `/customer/orders/{id}/cancel`
3. Backend gọi logic hủy (nếu đã PAID thì refund, hoàn kho)
4. Status → CANCELLED

## ⚠️ NHỮNG GÌ CẦN HOÀN THIỆN

### 1. **Tính phí vận chuyển (SHIPPING_FEE)**

**File**: `CheckoutController.calculateShipping()`

Hiện tại đang return placeholder:
```java
response.put("shippingFee", BigDecimal.valueOf(50000));
```

**Cần implement**:
```java
@GetMapping("/shipping")
public ResponseEntity<Map<String, Object>> calculateShipping(@RequestParam Integer addressId) {
    // 1. Get address
    Address address = addressRepository.findById(addressId).orElse(null);
    
    // 2. Get zoneId từ province
    Integer zoneId = provinceZoneRepository.findZoneIdByProvinceName(address.getCity())
        .orElse(null);
    
    // 3. Get shipping fee
    ShippingFee shippingFee = shippingFeeRepository.findByZoneId(zoneId).orElse(null);
    
    // 4. Return
    response.put("shippingFee", shippingFee.getBaseFee());
    return ResponseEntity.ok(response);
}
```

**Lưu ý**: Cần có dữ liệu trong bảng:
- `ProvinceZone` (map tỉnh → zone)
- `ShippingFee` (fee theo zone)

### 2. **Validate Coupon**

**File**: `CheckoutController.validateCoupon()`

Hiện tại:
```java
response.put("success", false);
response.put("message", "Mã giảm giá không tồn tại");
```

**Cần implement**:
```java
@GetMapping("/coupon/validate")
public ResponseEntity<Map<String, Object>> validateCoupon(
    @RequestParam String code, 
    @RequestParam BigDecimal subtotal) {
    
    Coupon coupon = couponRepository.findValidCoupon(
        code, 
        LocalDateTime.now(), 
        subtotal
    ).orElse(null);
    
    if (coupon != null) {
        BigDecimal discountPercent = coupon.getDiscountPercent();
        BigDecimal discountAmount = subtotal.multiply(discountPercent.divide(BigDecimal.valueOf(100)))
            .setScale(-3, RoundingMode.HALF_UP);
        
        response.put("success", true);
        response.put("discountAmount", discountAmount);
        response.put("message", "Áp dụng mã giảm giá thành công");
    } else {
        response.put("success", false);
        response.put("message", "Mã giảm giá không hợp lệ");
    }
    
    return ResponseEntity.ok(response);
}
```

### 3. **Tích hợp VNPAY/MoMo Payment**

#### A. Tạo Payment Service

**Tạo file**: `src/main/java/mocviet/service/PaymentService.java`

```java
@Service
public class PaymentService {
    
    public String generateVnpayPaymentUrl(CreateOrderResponse orderResponse) {
        // TODO: Implement VNPay integration
        // - Tạo payment URL với VNPay API
        // - Lưu transaction code
        return "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...";
    }
    
    public String generateMomoPaymentUrl(CreateOrderResponse orderResponse) {
        // TODO: Implement MoMo integration
        return "https://test-payment.momo.vn/v2/gateway/api/create";
    }
}
```

#### B. Cập nhật CheckoutController

Trong `createOrder()`:
```java
if ("VNPAY".equals(request.getPaymentMethod())) {
    response.setPaymentUrl(paymentService.generateVnpayPaymentUrl(response));
    // Redirect to payment URL
    return ResponseEntity.ok(response); // FE sẽ redirect
}

if ("MOMO".equals(request.getPaymentMethod())) {
    response.setPaymentUrl(paymentService.generateMomoPaymentUrl(response));
    return ResponseEntity.ok(response);
}
```

#### C. Tạo Webhook Handler

**File**: `src/main/java/mocviet/controller/PaymentWebhookController.java`

```java
@RestController
@RequestMapping("/payments/webhook")
public class PaymentWebhookController {
    
    @PostMapping("/vnpay")
    public ResponseEntity<String> handleVnpayWebhook(@RequestBody Map<String, String> params) {
        // Parse VNPay response
        String orderId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        
        boolean isSuccess = "00".equals(responseCode);
        
        // Gọi stored procedure
        // sp_HandlePaymentWebhook(orderId, 'VNPAY', isSuccess)
        
        return ResponseEntity.ok("OK");
    }
}
```

#### D. Cập nhật FE cho Payment Flow

**File**: `checkout.html` - trong function `createOrder()`

```javascript
.then(data => {
    if (data.paymentUrl) {
        // Redirect to payment gateway
        window.location.href = data.paymentUrl;
    } else {
        // COD - redirect to order detail
        window.location.href = `/customer/orders/${data.orderId}`;
    }
})
```

### 4. **Cấu hình dữ liệu**

#### A. Cần seed dữ liệu vào database

```sql
-- 1. Insert ProvinceZone
INSERT INTO ProvinceZone (province_name, zone_id) VALUES
('Hà Nội', 1),
('Hồ Chí Minh', 2),
('Đà Nẵng', 2),
...

-- 2. Insert ShippingFee
INSERT INTO ShippingFee (zone_id, base_fee) VALUES
(1, 30000), -- Miền Bắc
(2, 35000), -- Miền Nam
(3, 40000); -- Miền Trung

-- 3. Insert Coupon (optional)
INSERT INTO Coupon (code, discount_percent, start_date, end_date, active, min_order_amount) VALUES
('WELCOME10', 10.00, '2024-01-01', '2024-12-31', 1, 500000);
```

#### B. Security Config

Đảm bảo routes được bảo vệ trong `SecurityConfig.java`:
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/customer/checkout/**").authenticated()
    .requestMatchers("/customer/orders/**").authenticated()
    ...
)
```

## 🧪 TESTING

### Test Accounts

Từ file `MocViet_Database_Sample.sql`, bạn có các accounts sẵn:

**Customers**:
- `cust_a` / password: `123456` - Trần Khách A
- `cust_b` / password: `123456` - Đỗ Khách B

**Manager**: `manager` / password: `123456`

Tất cả passwords đều: `123456` (hash trong DB)

### Test Case 1: Đặt hàng COD thành công
1. Login as customer
2. Thêm sản phẩm vào giỏ
3. Click "Thanh toán" → Chọn địa chỉ → Chọn COD
4. Click "Đặt hàng"
5. ✅ Verify: Order tạo với status=PENDING, payment_status=UNPAID
6. ✅ Verify: Stock đã được trừ
7. ✅ Verify: Redirect đến order detail page

### Test Case 2: Đặt hàng với coupon
1. Login → Cart → Checkout
2. Nhập mã coupon valid → Click "Áp dụng"
3. Verify: Discount được tính đúng
4. Click "Đặt hàng"
5. ✅ Verify: Order tạo với coupon applied

### Test Case 3: Hủy đơn
1. Xem order detail của đơn PENDING
2. Click "Hủy đơn" → Confirm
3. ✅ Verify: Order status = CANCELLED
4. ✅ Verify: Stock được cộng lại
5. ✅ Verify: Nếu đã PAID thì payment_status = REFUNDED

### Test Case 4: Validate stock
1. Thêm sản phẩm vào cart
2. Giả lập hết hàng trước khi checkout
3. Click "Thanh toán"
4. ✅ Verify: Hiện thông báo "Tồn kho không đủ"

## 📝 GHI CHÚ QUAN TRỌNG

1. **Database**: Cần chạy script tạo stored procedures từ `MocViet_Database_Schema.sql`
2. **ProvinceZone**: Phải có dữ liệu mapping tỉnh/thành vào zone
3. **ShippingFee**: Phải có phí ship cho từng zone
4. **Payment**: VNPAY/MoMo cần config API key riêng (không có trong project này)
5. **Auto-cancel**: Job `sp_AutoCancelUnpaidOnline` chạy mỗi 5 phút (cần config SQL Server Agent)

## 🎯 NEXT STEPS

1. ✅ Config database với dữ liệu ProvinceZone và ShippingFee
2. ⏳ Implement full shipping fee calculation
3. ⏳ Implement full coupon validation
4. ⏳ Integrate VNPAY/MoMo payment gateway (cần config riêng)
5. ⏳ Test end-to-end flow
6. ⏳ Implement order history và tracking

---

**Người phát triển**: AI Assistant
**Ngày**: 2024
**Version**: 1.0

