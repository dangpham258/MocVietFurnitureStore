# 🔄 QUY TRÌNH TRẢ HÀNG - MỘC VIỆT FURNITURE

Tớ mô tả quy trình "trả hàng & hoàn tiền tại chỗ" như đang dùng 1 website bán nội thất. Mỗi vai (Khách hàng / Quản lý / Đội giao) sẽ thấy gì, bấm gì; hệ thống thay đổi trạng thái và gửi thông báo ra sao.

## 1) Khách hàng (Customer)

### Bước A — Gửi yêu cầu trả hàng

**Trang:** Lịch sử đơn hàng → Chi tiết đơn #1234  
**Điều kiện thấy nút "Yêu cầu trả hàng":** đơn đang DELIVERED và còn trong hạn 30 ngày.

**Hành động:**

- Bấm Yêu cầu trả hàng, nhập lý do (tùy chọn) → Gửi.

**Hệ thống làm (sp_RequestReturn):**

- Kiểm tra quyền sở hữu đơn, trạng thái, hạn 30 ngày.
- Cập nhật `return_status = REQUESTED`, lưu `return_reason`.
- Ghi log lịch sử trạng thái.

**Thông báo:**

- Gửi Manager: "Yêu cầu trả hàng cho đơn #1234… Lý do: …" (`TR_Orders_NotifyReturnRequest`).
- (Khách chưa nhận thông báo ở bước này—UI có thể hiện banner "Đã gửi yêu cầu, vui lòng chờ duyệt".)

---

## 2) Quản lý (Manager)

### Bước B — Duyệt yêu cầu

**Trang:** Quản trị → Trả hàng → Tab "Chờ duyệt"  
**Thấy:** đơn #1234 (status: DELIVERED, return_status: REQUESTED), có nút Duyệt / Từ chối.

### Nếu Duyệt:

- Chọn đội thu hồi (nếu đơn chưa có OrderDelivery) → Duyệt.

**Hệ thống làm (sp_ApproveReturn):**

- Cập nhật `return_status = APPROVED`, thêm ghi chú duyệt.
- Nếu chưa có giao nhận → tạo OrderDelivery cho đơn này.
- Đẩy `OrderDelivery.status = RETURN_PICKUP` (đã phân công thu hồi).
- Ghi lịch sử giao nhận.

**Thông báo:**

- **Khách hàng:** "Yêu cầu trả hàng được duyệt. Đội giao sẽ liên hệ để thu hồi." (`TR_Orders_NotifyCustomerReturnApproved`)
- **Đội giao** (user đại diện của đội): "Đơn #1234 chuyển RETURN_PICKUP." (`TR_OrderDelivery_NotifyDeliveryTeam`)

### Nếu Từ chối:

- Nhập lý do → Từ chối (`sp_RejectReturn`).
- Cập nhật `return_status = REJECTED`, log lịch sử.
- **Khách hàng nhận:** "Yêu cầu trả hàng bị từ chối. Lý do: …" (`TR_Orders_NotifyCustomerReturnRejected`)

---

## 3) Đội giao / thu hồi (Delivery Team)

### Bước C — Thu hồi tại nhà khách

**Trang:** Ứng dụng đội giao → Danh sách việc  
**Thấy:** việc "Thu hồi đơn #1234 — RETURN_PICKUP".

**Hành động tại hiện trường:**

1. Gọi khách hẹn giờ.
2. Tới nhà khách, kiểm tra hàng hóa.
3. **Hoàn tiền tại chỗ** (tiền mặt/QR theo chính sách "trả khi tới lấy").
4. Chụp ảnh biên nhận/bàn giao (nếu muốn), nhập ghi chú.
5. Bấm Hoàn tất thu hồi.

**Hệ thống làm (sp_ReturnOrder):**

- Xác minh đơn đang DELIVERED & return_status=APPROVED và OrderDelivery đang RETURN_PICKUP.
- **Cập nhật:**
  - `Orders.status = RETURNED`
  - `Orders.return_status = PROCESSED`
  - `Orders.payment_status = REFUNDED`
  - `Orders.return_note` bổ sung "PROCESSED … | REFUND_METHOD=… (DONE)"
  - **Bù tồn kho** các SKU đã mua.
  - **Đóng giao nhận:** OrderDelivery.status = DONE + log DeliveryHistory.
  - Ghi log OrderStatusHistory.

**Thông báo:**

- **Khách hàng:** "Đơn #1234 đã trả hàng & hoàn tiền xong (COD_CASH/VNPAY/MOMO)."
- (Đội giao có thể không cần thêm thông báo ở bước hoàn tất; hệ thống chỉ log.)

---

## 4) Khách hàng sau khi hoàn tất

**Trang:** Lịch sử đơn hàng → Chi tiết đơn #1234

- **Trạng thái hiển thị:** RETURNED
- **Nhãn thanh toán:** Đã hoàn tiền
- **Box ghi chú:** "PROCESSED: … | REFUND_METHOD=… (DONE)"
- **Thông báo đã nhận** ở trung tâm thông báo.

---

## 5) Tóm tắt luồng trạng thái dữ liệu

### Orders.status

```
PENDING → CONFIRMED → DISPATCHED → DELIVERED → RETURNED (khi hoàn tất trả)
```

### Orders.return_status

```
NULL → REQUESTED (khách gửi) → APPROVED / REJECTED (manager duyệt) → PROCESSED (khi đã xử lý & hoàn tiền)
```

### OrderDelivery.status

```
RECEIVED / IN_TRANSIT → RETURN_PICKUP (khi duyệt trả) → DONE (sau khi thu hồi xong)
```

### Orders.payment_status

```
UNPAID/PAID → REFUNDED (ngay tại bước C bằng sp_ReturnOrder)
```

---

## 6) Ai làm gì — bảng vai trò

| Vai trò        | Hành động                                                                                                                                                                                  |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Khách hàng** | • Gửi yêu cầu trả (DELIVERED trong 30 ngày)<br>• Nhận thông báo khi: được duyệt / bị từ chối / đã hoàn tất & hoàn tiền                                                                     |
| **Quản lý**    | • Xem danh sách yêu cầu, Duyệt hoặc Từ chối<br>• Chọn đội thu hồi nếu chưa có<br>• Không được đổi đội khi đã gán (ràng buộc trong logic)                                                   |
| **Đội giao**   | • Thực hiện thu hồi tại địa chỉ khách<br>• Hoàn tiền ngay tại chỗ theo phương thức chọn (COD_CASH/BANK_TRANSFER/VNPAY/MOMO)<br>• Bấm hoàn tất để hệ thống chốt trả, bù tồn, đóng giao nhận |
| **Hệ thống**   | • Tự kiểm tra điều kiện, cập nhật trạng thái, bù tồn, và gửi thông báo (triggers)<br>• Dedupe thông báo để tránh spam (một số trigger có chống lặp theo thời gian)                         |

---

## 7) Gợi ý UI (mẫu nhanh)

### Customer → Order Detail

- **Card "Tình trạng":** Delivered ✅ / Returned ✅
- **Nút Yêu cầu trả hàng** (ẩn nếu quá hạn/không đủ điều kiện)
- **Modal nhập lý do** → Gửi

### Manager → Returns

- **Bảng:** Mã đơn | Khách hàng | Ngày giao | Lý do | Trạng thái trả | Hành động
- **Drawer "Duyệt trả":** chọn Đội thu hồi (dropdown), ghi chú → Duyệt / Từ chối

### Delivery App → Tasks

- **Danh sách "RETURN_PICKUP"**
- **Chi tiết:** Địa chỉ, số điện thoại, ảnh sản phẩm, ghi chú.
- **Nút Hoàn tất thu hồi** (kèm chọn phương thức hoàn tiền)

---

## 8) Hỏi–Đáp nhanh

### Tiền có trả ngay lúc đội tới lấy hàng không?

**Có.** `sp_ReturnOrder` luôn set `payment_status='REFUNDED'` tại thời điểm thu hồi (RETURN_PICKUP → DONE).

### Nếu Manager quên gán đội?

Khi Duyệt, hệ thống bắt buộc chọn đội nếu đơn chưa có OrderDelivery. Không thể tiến hành nếu thiếu.

### Có thể đổi đội sau khi gán?

**Không.** Logics chặn đổi đội trong giai đoạn giao/thu hồi.

---

## 🎯 **ĐIỂM KHÁC BIỆT QUAN TRỌNG**

### ✅ Chuẩn "Hoàn tiền ngay khi thu hồi":

- **Luôn** `payment_status = REFUNDED` khi xử lý trả
- **Bắt buộc** đội phải đến thu hồi (`RETURN_PICKUP`) mới được xử lý
- **Thông báo duy nhất**: "đã trả hàng & hoàn tiền xong"

### ✅ Validation chặt chẽ:

- Chặn yêu cầu trả khi chưa `DELIVERED`
- Chặn xử lý trả khi chưa `APPROVED`
- **Chặn xử lý trả khi chưa `RETURN_PICKUP`** (mới)
- Chặn đổi đội sau khi đã gán thu hồi

### ✅ Thông báo không trùng:

- **REQUESTED** → Manager nhận thông báo
- **APPROVED** → Customer + Delivery nhận thông báo
- **RETURNED** → Chỉ SP gửi thông báo (không dùng trigger)

---

## 📋 **CÁC STORED PROCEDURES**

### **sp_RequestReturn**

- **Mục đích:** Khách hàng yêu cầu trả hàng
- **Tham số:** `@order_id`, `@user_id`, `@reason`
- **Kết quả:** `return_status` → `REQUESTED`

### **sp_ApproveReturn**

- **Mục đích:** Phê duyệt yêu cầu trả hàng
- **Tham số:** `@order_id`, `@manager_id`, `@note`, `@delivery_team_id`
- **Kết quả:** `return_status` → `APPROVED` + tạo `OrderDelivery`

### **sp_RejectReturn**

- **Mục đích:** Từ chối yêu cầu trả hàng
- **Tham số:** `@order_id`, `@manager_id`, `@note`
- **Kết quả:** `return_status` → `REJECTED`

### **sp_ReturnOrder**

- **Mục đích:** Xử lý hoàn tất trả hàng (chỉ khi đã thu hồi)
- **Tham số:** `@order_id`, `@actor_user_id`, `@reason`, `@refund_method`
- **Kết quả:** Hoàn tất trả hàng + hoàn tiền ngay + bù tồn kho

---

## 🎯 **CÁC TRIGGER TỰ ĐỘNG**

### **TR_Orders_NotifyReturnRequest**

- **Kích hoạt:** Khi `return_status` → `REQUESTED`
- **Thông báo:** Manager nhận thông báo yêu cầu trả hàng

### **TR_Orders_NotifyCustomerReturnApproved**

- **Kích hoạt:** Khi `return_status` → `APPROVED`
- **Thông báo:** Customer nhận thông báo yêu cầu được duyệt

### **TR_OrderDelivery_NotifyDeliveryTeam**

- **Kích hoạt:** Khi `OrderDelivery.status` → `RETURN_PICKUP`
- **Thông báo:** Delivery team nhận thông báo thu hồi (với deduplication)

### **TR_Orders_NotifyCustomerReturnRejected**

- **Kích hoạt:** Khi `return_status` → `REJECTED`
- **Thông báo:** Customer nhận thông báo yêu cầu trả hàng bị từ chối

---

## ⚠️ **LƯU Ý QUAN TRỌNG**

1. **Chỉ đơn hàng đã giao** (`status = 'DELIVERED'`) mới có thể trả hàng
2. **Trong vòng 30 ngày** kể từ khi giao hàng (tính từ `OrderStatusHistory`)
3. **Delivery team chỉ thu hồi** đơn hàng được phân công cho họ
4. **Phải có `RETURN_PICKUP`** mới được xử lý trả hàng (đảm bảo đã thu hồi)
5. **Luôn hoàn tiền ngay** khi xử lý trả hàng (`payment_status = REFUNDED`)
6. **Tồn kho được bù lại** khi hoàn tất trả hàng
7. **Tất cả thao tác đều được log** trong `OrderStatusHistory`
8. **Thông báo tự động** cho tất cả các bên liên quan
9. **Chặn đổi đội** sau khi đã gán thu hồi

---

## 🚫 **CÁC TRƯỜNG HỢP BỊ CHẶN**

1. **Yêu cầu trả khi chưa giao hàng** (`status ≠ 'DELIVERED'`)
2. **Yêu cầu trả quá 30 ngày** (tính từ khi giao)
3. **Xử lý trả khi chưa được duyệt** (`return_status ≠ 'APPROVED'`)
4. **Xử lý trả khi đội chưa đến thu hồi** (`OrderDelivery.status ≠ 'RETURN_PICKUP'`)
5. **Đổi đội sau khi đã gán thu hồi** (trong `sp_ApproveReturn`)

---

## 🎉 **LỢI ÍCH**

- ✅ **Kiểm soát chặt chẽ** quy trình trả hàng
- ✅ **Hoàn tiền ngay tại chỗ** khi thu hồi hàng
- ✅ **Thông báo tự động** cho tất cả các bên
- ✅ **Theo dõi đầy đủ** trạng thái và lịch sử
- ✅ **Bù tồn kho tự động** khi hoàn tất
- ✅ **Phân quyền rõ ràng** cho từng vai trò
- ✅ **Validation chặt chẽ** đảm bảo tính nhất quán
- ✅ **Không có thông báo trùng lặp**
