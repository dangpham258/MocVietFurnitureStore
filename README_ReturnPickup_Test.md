# 🚚 HƯỚNG DẪN TEST CHỨC NĂNG THU HỒI HÀNG

## 📋 Tổng quan
Chức năng thu hồi hàng (Return Pickup) cho phép đội giao hàng thu hồi sản phẩm từ khách hàng và hoàn tiền tại chỗ.

## 🗂️ Files đã tạo
- `MocViet_ReturnPickup_Sample.sql` - Script tạo nhiều đơn hàng mẫu để test
- `MocViet_Quick_Return_Test.sql` - Script tạo nhanh 1 đơn hàng để test

## 🚀 Cách chạy test

### Bước 1: Chuẩn bị dữ liệu
```sql
-- Chạy script cơ bản trước (nếu chưa có)
-- MocViet_Database_Sample.sql

-- Sau đó chạy script tạo dữ liệu thu hồi
-- MocViet_Quick_Return_Test.sql (nhanh)
-- HOẶC
-- MocViet_ReturnPickup_Sample.sql (đầy đủ)
```

### Bước 2: Đăng nhập hệ thống
- **URL:** http://localhost:8080/delivery
- **Username:** `delivery_south` hoặc `delivery_north`
- **Password:** `demo123`

### Bước 3: Test chức năng thu hồi

#### 3.1. Xem danh sách đơn cần thu hồi
- Vào Dashboard sẽ thấy đơn hàng với badge **"Cần thu hồi"**
- Thông tin hiển thị: ID đơn, khách hàng, số điện thoại, địa chỉ

#### 3.2. Xem chi tiết đơn hàng
- Click nút **"Chi tiết"** trên đơn hàng
- Xem thông tin khách hàng, sản phẩm, lịch sử giao hàng

#### 3.3. Xử lý thu hồi hàng
- Chọn **phương thức hoàn tiền** (bắt buộc):
  - ✅ Tiền mặt (COD_CASH)
  - ✅ Chuyển khoản (BANK_TRANSFER)  
  - ✅ VNPAY
  - ✅ MOMO
- Nhập **ghi chú** (tùy chọn): "Đã thu hồi hàng, hoàn tiền..."
- Nhập **link ảnh** (tùy chọn): URL ảnh thu hồi
- Click **"XÁC NHẬN ĐÃ THU HỒI"**

#### 3.4. Kiểm tra kết quả
- Đơn hàng sẽ chuyển về Dashboard với trạng thái **"Đã hoàn thành"**
- Thông báo thành công: "Xử lý thu hồi và hoàn tiền thành công"

## 🔍 Kiểm tra dữ liệu

### SQL để xem đơn hàng thu hồi
```sql
SELECT 
    o.id as OrderID,
    o.status as OrderStatus,
    o.return_status as ReturnStatus,
    o.payment_status as PaymentStatus,
    u.full_name as CustomerName,
    dt.name as DeliveryTeam,
    od.status as DeliveryStatus
FROM dbo.Orders o
JOIN dbo.Users u ON u.id = o.user_id
JOIN dbo.OrderDelivery od ON od.order_id = o.id
JOIN dbo.DeliveryTeam dt ON dt.id = od.delivery_team_id
WHERE o.return_status = N'APPROVED' 
  AND od.status = N'RETURN_PICKUP'
ORDER BY o.id;
```

### SQL để xem lịch sử thu hồi
```sql
SELECT 
    o.id as OrderID,
    osh.status as OrderStatus,
    osh.note as Note,
    osh.changed_at as ChangedAt,
    u.full_name as ChangedBy
FROM dbo.Orders o
JOIN dbo.OrderStatusHistory osh ON osh.order_id = o.id
LEFT JOIN dbo.Users u ON u.id = osh.changed_by
WHERE o.return_status = N'PROCESSED'
ORDER BY o.id, osh.changed_at;
```

## 🎯 Test Cases

### Test Case 1: Thu hồi với tiền mặt
- Chọn phương thức: **Tiền mặt**
- Ghi chú: "Đã thu hồi hàng, hoàn tiền bằng tiền mặt"
- Kết quả mong đợi: Đơn chuyển thành RETURNED, payment_status = REFUNDED

### Test Case 2: Thu hồi với chuyển khoản
- Chọn phương thức: **Chuyển khoản**
- Ghi chú: "Đã thu hồi hàng, hoàn tiền qua chuyển khoản"
- Kết quả mong đợi: Đơn chuyển thành RETURNED, payment_status = REFUNDED

### Test Case 3: Thu hồi với VNPAY
- Chọn phương thức: **VNPAY**
- Ghi chú: "Đã thu hồi hàng, hoàn tiền qua VNPAY"
- Kết quả mong đợi: Đơn chuyển thành RETURNED, payment_status = REFUNDED

### Test Case 4: Thu hồi với MOMO
- Chọn phương thức: **MOMO**
- Ghi chú: "Đã thu hồi hàng, hoàn tiền qua MOMO"
- Kết quả mong đợi: Đơn chuyển thành RETURNED, payment_status = REFUNDED

## 🐛 Troubleshooting

### Lỗi thường gặp
1. **"Không thể xử lý thu hồi cho đơn ở trạng thái..."**
   - Kiểm tra đơn hàng có đúng trạng thái RETURN_PICKUP không
   - Chạy lại script tạo dữ liệu

2. **"Vui lòng chọn phương thức hoàn tiền"**
   - Đảm bảo đã chọn một trong 4 phương thức hoàn tiền

3. **"Đơn giao hàng không hợp lệ hoặc không được phân công cho bạn"**
   - Kiểm tra đăng nhập đúng tài khoản delivery team
   - Kiểm tra đơn hàng có thuộc về team đó không

### Reset dữ liệu
```sql
-- Xóa dữ liệu thu hồi để test lại
DELETE FROM dbo.DeliveryHistory WHERE status = N'RETURN_PICKUP';
DELETE FROM dbo.OrderStatusHistory WHERE status = N'RETURNED';
UPDATE dbo.Orders SET return_status = NULL, status = N'DELIVERED' WHERE return_status = N'PROCESSED';
UPDATE dbo.OrderDelivery SET status = N'DONE' WHERE status = N'RETURN_PICKUP';
```

## 📱 API Testing

### Lấy danh sách thông báo
```bash
GET http://localhost:8080/delivery/api/notifications
Authorization: Bearer {jwt_token}
```

### Đếm thông báo chưa đọc
```bash
GET http://localhost:8080/delivery/api/notifications/count
Authorization: Bearer {jwt_token}
```

### Đánh dấu đã đọc
```bash
POST http://localhost:8080/delivery/api/notifications/{id}/mark-read
Authorization: Bearer {jwt_token}
```

## ✅ Checklist Test

- [ ] Đăng nhập được với tài khoản delivery
- [ ] Thấy đơn hàng "Cần thu hồi" trong dashboard
- [ ] Xem được chi tiết đơn hàng
- [ ] Chọn được phương thức hoàn tiền
- [ ] Nhập được ghi chú và ảnh
- [ ] Submit thành công form thu hồi
- [ ] Đơn hàng chuyển về trạng thái "Đã hoàn thành"
- [ ] Thông báo thành công hiển thị
- [ ] Dữ liệu trong database được cập nhật đúng

## 🎉 Kết luận

Chức năng thu hồi hàng đã được implement đầy đủ với:
- ✅ UI/UX thân thiện
- ✅ Validation đầy đủ
- ✅ Xử lý lỗi tốt
- ✅ API RESTful
- ✅ Thông báo real-time
- ✅ Lưu trữ dữ liệu chính xác

**Chúc bạn test thành công! 🚀**
