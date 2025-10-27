# 💳 QUY TRÌNH THANH TOÁN & HỦY ĐƠN - HỆ THỐNG MỘC VIỆT

## 🎯 Tổng quan

Hệ thống thanh toán hỗ trợ 3 phương thức: **COD**, **VNPAY**, **MoMo** với quy trình "thanh toán xong ngay" - không có trạng thái REFUND_PENDING. Tất cả refund được thực hiện đồng bộ khi hủy đơn.

---

## 📱 1. LUỒNG NGƯỜI DÙNG (FRONTEND UX)

### 1.1. Tạo đơn ở trang Checkout

**Bước 1: Chọn thông tin**

- User chọn địa chỉ giao hàng
- Chọn phương thức thanh toán: **COD** / **VNPAY** / **MoMo**
- Áp dụng mã coupon (nếu có)
- Xem lại tổng tiền và phí vận chuyển

**Bước 2: Đặt hàng**

- Bấm **"Đặt hàng"** → FE gọi `POST /orders`
- BE gọi `sp_CreateOrder` → tạo đơn **PENDING** + `payment_status=UNPAID`
- Trừ tồn kho, ghi `OrderItems`

**Bước 3: Hiển thị kết quả**

- **COD**: Hiển thị "Chờ xác nhận" + nút **"Hủy đơn"**
- **VNPAY/MoMo**: Auto-redirect sang cổng thanh toán + mở trang "Đang chờ thanh toán..." (polling)

### 1.2. Thanh toán Online (VNPAY/MoMo)

**Luồng thanh toán:**

1. User thanh toán tại cổng VNPAY/MoMo
2. **Web return URL (FE)** nhận `vnp_ResponseCode`/`momoResult`
3. **Webhook (BE)** gọi `sp_HandlePaymentWebhook(@order_id, @method, @is_success)`:
   - ✅ **Thành công**: Giữ **PENDING**, set `payment_status=PAID`
   - ❌ **Thất bại**: Giữ **PENDING**, `payment_status=UNPAID`

**Hiển thị kết quả:**

- **Thành công**: Banner "Thanh toán thành công, đợi xác nhận"
- **Thất bại**: "Thanh toán chưa thành công" + nút **"Thử thanh toán lại"** / **"Hủy đơn"**

### 1.3. Xác nhận đơn (phía Manager)

**Quy trình xác nhận:**

1. Manager bấm **"Xác nhận"** trên dashboard
2. BE gọi `sp_ConfirmOrder` → đơn chuyển **CONFIRMED**
3. **Nút "Hủy đơn" ở FE biến mất** (không hủy được nữa)
4. Trigger gửi thông báo: "Đơn đã được xác nhận"

### 1.4. Hủy đơn (trước khi xác nhận)

**Điều kiện:** Chỉ khi `status=PENDING`

**Luồng hủy đơn:**

1. FE hiển thị nút **"Hủy đơn"** trên trang đơn hàng PENDING
2. Modal xác nhận: _"Sau khi hủy sẽ hoàn kho. Nếu đã thanh toán online sẽ hoàn tiền ngay."_
3. Gọi `POST /orders/{id}/cancel` → BE gọi `sp_CancelOrder`
4. **Logic trong SP:**
   - Nếu `payment_method in (VNPAY/MOMO)` **và** `payment_status=PAID` → set `payment_status=REFUNDED`
   - Hoàn kho, `status=CANCELLED`, log `OrderStatusHistory`
5. FE cập nhật UI: trạng thái **"Đã hủy"**, ẩn mọi hành động

### 1.5. COD được thu tiền khi giao

**Quy trình giao hàng:**

1. **CONFIRMED** → **DISPATCHED** (`sp_MarkDispatched`)
2. **DISPATCHED** → **DELIVERED** (`sp_MarkDelivered`)
3. **Trong `sp_MarkDelivered`**: Nếu `payment_method=COD` & `payment_status=UNPAID` → auto set **PAID**

**Hiển thị:** "Đã giao thành công – Đã thu COD"

### 1.6. Auto-cancel đơn online chưa thanh toán

**Batch job (mỗi 5 phút):**

- Gọi `sp_AutoCancelUnpaidOnline(@expire_minutes=15)`
- Hủy các đơn `PENDING` + `payment_method in (VNPAY/MOMO)` + `UNPAID` quá hạn
- Kết quả: **CANCELLED**, hoàn kho

**FE:** Nếu user mở trang đơn đã bị job hủy → thấy trạng thái **"Đã hủy (quá hạn thanh toán)"**

---

## 🎛️ 2. QUY TẮC HIỂN THỊ NÚT & TRẠNG THÁI TRÊN FE

| Trạng thái đơn | payment_method | payment_status  | Nút khả dụng (Customer)          | Ghi chú                              |
| -------------- | -------------- | --------------- | -------------------------------- | ------------------------------------ |
| **PENDING**    | COD            | UNPAID          | **Hủy đơn**                      | Cho tới khi manager xác nhận         |
| **PENDING**    | VNPAY/MOMO     | UNPAID          | **Thanh toán lại** • **Hủy đơn** | Có thể bật countdown                 |
| **PENDING**    | VNPAY/MOMO     | PAID            | **Hủy đơn**                      | Hủy sẽ **REFUND** ngay               |
| **CONFIRMED**  | Any            | Any             | _(không có)_                     | Hết quyền hủy                        |
| **DISPATCHED** | Any            | Any             | _(không có)_                     | Hết quyền hủy                        |
| **DELIVERED**  | COD            | PAID            | _(không có)_                     | COD được set PAID khi giao           |
| **DELIVERED**  | VNPAY/MOMO     | PAID            | _(không có)_                     | Nếu muốn trả hàng → quy trình return |
| **CANCELLED**  | Any            | UNPAID/REFUNDED | _(không có)_                     | Đơn đã khép lại                      |

---

## 🔌 3. API GỢI Ý (KHỚP SP HIỆN CÓ)

### 3.1. Tạo đơn

**`POST /orders`**

```json
{
  "user_id": 123,
  "address_id": 45,
  "coupon_code": "WELCOME10",
  "payment_method": "VNPAY", // "COD" | "VNPAY" | "MOMO"
  "items": [{ "variant_id": 1001, "qty": 2 }]
}
```

**Response:**

```json
{
  "order_id": 55021,
  "grand_total": 2500000,
  "payment_url": "https://sandbox.vnpayment.vn/...", // Nếu online
  "status": "PENDING",
  "payment_status": "UNPAID"
}
```

**BE Logic:** Gọi `sp_CreateOrder` → trả về `order_id`, `grand_total`, `payment_url` (nếu online)

### 3.2. Webhook thanh toán

**`POST /payments/webhook/vnpay`** (hoặc `/momo`)

**BE Logic:**

1. Parse và xác thực chữ ký
2. Map `order_id` từ gateway response
3. Gọi `sp_HandlePaymentWebhook(@order_id, 'VNPAY'|'MOMO', @is_success, @txnCode)`

### 3.3. Hủy đơn

**`POST /orders/{id}/cancel`**

```json
{
  "reason": "Không cần nữa"
}
```

**Response:**

```json
{
  "status": "CANCELLED",
  "payment_status": "REFUNDED", // hoặc "UNPAID"
  "message": "Đơn hàng đã được hủy"
}
```

**BE Logic:** Gọi `sp_CancelOrder(@order_id, @actor_user_id, @reason)`

### 3.4. Auto-cancel job

**CRON/SQL Agent:** Gọi định kỳ `sp_AutoCancelUnpaidOnline 15`

---

## 🎨 4. UX CHI TIẾT (COMPONENT)

### 4.1. Trang chi tiết đơn (OrderDetail)

**Header:**

- Mã đơn #55021 • Badge trạng thái (PENDING/CONFIRMED/...)

**Timeline:**

- Lấy từ `OrderStatusHistory` → hiển thị lịch sử thay đổi trạng thái

**Box thanh toán:**

- `payment_method`, `payment_status`
- Nếu `PENDING & online & UNPAID`: nút **"Thanh toán lại"** (link gateway), đồng hồ đếm ngược 15'

**Action area:**

- Hiện **"Hủy đơn"** khi `status=PENDING`
- Ẩn toàn bộ action khi `status not in (PENDING)`

### 4.2. Modal Hủy đơn

**Nội dung:**

> "Hủy đơn #55021? Hệ thống sẽ hoàn kho. Nếu đã thanh toán online sẽ hoàn tiền ngay."

**Actions:**

- **"Xác nhận"** → call API → toast kết quả
- **"Hủy"** → đóng modal

### 4.3. Toasts/Notifications

**Dựa vào triggers đã có:**

- Tạo/gửi `UserNotification` khi `status` đổi (Confirm/Dispatch/Delivered/Cancelled)
- FE có bell hoặc inbox: poll hoặc websocket

---

## ⚠️ 5. CÁC CASE BIÊN & BẢO VỆ

### 5.1. Double click "Đặt hàng"

- **FE:** Disable nút sau khi click
- **BE:** Dùng transaction/unique key (SKU trong CartItem đã có; order tạo 1 lần)

### 5.2. Hủy trễ

- Nếu user bấm **"Hủy"** nhưng manager vừa **CONFIRMED** xong
- `sp_CancelOrder` sẽ báo lỗi: _"Chỉ hủy được đơn PENDING"_
- **FE:** Hiện toast lỗi, reload đổi UI

### 5.3. Online PAID nhưng PENDING

- Cho phép **"Hủy"** → SP tự set `REFUNDED` (đúng yêu cầu)

### 5.4. Thiếu webhook

- **FE return:** Báo "đang xử lý thanh toán"
- **BE:** Có retry webhook hoặc nút **"Đồng bộ trạng thái"**
- **FE:** Gọi `GET /orders/{id}/sync-payment` → BE tự check gateway

### 5.5. Auto-cancel

- **FE:** Xử lý "đếm ngược 15'" là cosmetic
- **Quyết định:** Ở job `sp_AutoCancelUnpaidOnline`

### 5.6. Xóa nút đúng quyền

- Chỉ hiển thị **"Hủy"** cho **chủ đơn**
- **BE:** Vẫn kiểm tra quyền ở API

---

## 🔗 6. MAPPING SANG SP HIỆN TẠI

| Chức năng                  | Stored Procedure            | Mô tả                          |
| -------------------------- | --------------------------- | ------------------------------ |
| **Đặt hàng**               | `sp_CreateOrder`            | Tạo đơn PENDING + UNPAID       |
| **Webhook**                | `sp_HandlePaymentWebhook`   | Xử lý kết quả thanh toán       |
| **Hủy đơn (frontend)**     | `sp_CancelOrder`            | Chỉ PENDING, tự refund nếu cần |
| **Job hủy quá hạn online** | `sp_AutoCancelUnpaidOnline` | Auto-cancel đơn timeout        |

---

## 🚀 7. HƯỚNG DẪN TRIỂN KHAI

### 7.1. Spring Boot Integration

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        // Gọi sp_CreateOrder
        // Trả payment_url nếu online
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CancelResponse> cancelOrder(@PathVariable Long id, @RequestBody CancelRequest request) {
        // Gọi sp_CancelOrder
    }
}

@RestController
@RequestMapping("/payments/webhook")
public class PaymentWebhookController {

    @PostMapping("/vnpay")
    public ResponseEntity<String> handleVnpayWebhook(@RequestBody VnpayWebhookRequest request) {
        // Gọi sp_HandlePaymentWebhook
    }
}
```

### 7.2. Scheduler cho Auto-cancel

```java
@Component
public class AutoCancelScheduler {

    @Scheduled(fixedRate = 300000) // 5 phút
    public void autoCancelUnpaidOrders() {
        jdbcTemplate.execute("EXEC dbo.sp_AutoCancelUnpaidOnline @expire_minutes = 15");
    }
}
```

### 7.3. SQL Agent Job (Alternative)

```sql
-- Tạo job chạy mỗi 5 phút
EXEC msdb.dbo.sp_add_job @job_name = N'AutoCancelUnpaidOnline';
EXEC msdb.dbo.sp_add_jobstep
    @job_name = N'AutoCancelUnpaidOnline',
    @command = N'EXEC dbo.sp_AutoCancelUnpaidOnline @expire_minutes = 15;';
```

---

## ✅ KẾT LUẬN

Quy trình thanh toán **"Cách A"** đã được thiết kế hoàn chỉnh với:

- ✅ **3 phương thức thanh toán**: COD, VNPAY, MoMo
- ✅ **Thanh toán xong ngay**: Không có REFUND_PENDING
- ✅ **Auto-cancel timeout**: Giải phóng tồn kho
- ✅ **UX rõ ràng**: Nút và trạng thái theo từng giai đoạn
- ✅ **API đầy đủ**: Mapping với SP hiện có
- ✅ **Xử lý case biên**: Double click, hủy trễ, thiếu webhook

**Hệ thống sẵn sàng triển khai!** 🎉
