-- =====================================================
-- MOC VIET - DỮ LIỆU MẪU CHO CHỨC NĂNG THU HỒI HÀNG
-- =====================================================
-- Script này tạo dữ liệu mẫu để test chức năng thu hồi hàng (return pickup)
-- Chạy sau khi đã có dữ liệu cơ bản từ MocViet_Database_Sample.sql

USE MocViet;
GO

PRINT N'🚀 Bắt đầu tạo dữ liệu mẫu cho chức năng thu hồi hàng...';

BEGIN TRANSACTION;
SET NOCOUNT ON;

-- =====================================================
-- 1. TẠO ĐƠN HÀNG MẪU CHO THU HỒI
-- =====================================================

-- Lấy các ID cần thiết
DECLARE @userA INT = (SELECT id FROM dbo.Users WHERE username = N'cust_a');
DECLARE @userB INT = (SELECT id FROM dbo.Users WHERE username = N'cust_b');
DECLARE @managerID INT = (SELECT id FROM dbo.Users WHERE username = N'manager');
DECLARE @teamNam INT = (SELECT id FROM dbo.DeliveryTeam WHERE name = N'Đội Giao Khu Vực Nam');
DECLARE @teamBac INT = (SELECT id FROM dbo.DeliveryTeam WHERE name = N'Đội Giao Khu Vực Bắc');

-- Lấy địa chỉ
DECLARE @addrA INT = (SELECT id FROM dbo.Address WHERE user_id = @userA);
DECLARE @addrB INT = (SELECT id FROM dbo.Address WHERE user_id = @userB);

-- Lấy một số SKU có sẵn
DECLARE @sku1 INT = (SELECT id FROM dbo.ProductVariant WHERE sku = N'BANOC6_DEN');
DECLARE @sku2 INT = (SELECT id FROM dbo.ProductVariant WHERE sku = N'SO3_XAM');
DECLARE @sku3 INT = (SELECT id FROM dbo.ProductVariant WHERE sku = N'GN1M6_NAU');

-- =====================================================
-- ĐƠN HÀNG 1: ĐÃ GIAO THÀNH CÔNG - CHỜ THU HỒI
-- =====================================================

PRINT N'📦 Tạo đơn hàng #1 - Đã giao thành công, chờ thu hồi...';

DECLARE @order1 INT;
DECLARE @items1 dbo.TVP_OrderItem;
INSERT INTO @items1(variant_id, qty) VALUES(@sku1, 1);

DECLARE @o1 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);

INSERT INTO @o1 EXEC dbo.sp_CreateOrder 
  @user_id = @userA,
  @address_id = @addrA,
  @coupon_code = N'WELCOME10',
  @payment_method = N'COD',
  @items = @items1;

SELECT @order1 = order_id FROM @o1;

-- Thanh toán thành công
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @order1,
  @payment_method = N'COD',
  @is_success = 1,
  @gateway_txn_code = N'COD_' + CAST(@order1 AS NVARCHAR(10));

-- Xác nhận và giao hàng thành công
EXEC dbo.sp_ConfirmOrder @order_id = @order1, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @order1, @delivery_team_id = @teamNam, @actor_user_id = @managerID;

DECLARE @proof1 NVARCHAR(255) = N'/static/images/deliveries/order-' + CAST(@order1 AS NVARCHAR(10)) + N'/delivered.jpg';
EXEC dbo.sp_MarkDelivered @order_id = @order1, @proof_image_url = @proof1, @actor_user_id = @managerID;

-- Cập nhật payment_status = PAID (COD)
UPDATE dbo.Orders SET payment_status = N'PAID' WHERE id = @order1;

-- Khách hàng yêu cầu trả hàng
EXEC dbo.sp_RequestReturn
  @order_id = @order1,
  @customer_id = @userA,
  @reason = N'Sản phẩm không đúng màu sắc như mô tả trên website. Tôi đặt màu đen nhưng nhận được màu nâu.';

-- Manager phê duyệt yêu cầu trả hàng
EXEC dbo.sp_ApproveReturn 
  @order_id = @order1, 
  @manager_id = @managerID, 
  @note = N'Đồng ý trả hàng, sản phẩm không đúng mô tả màu sắc',
  @delivery_team_id = @teamNam;

PRINT N'✅ Đơn hàng #' + CAST(@order1 AS NVARCHAR(10)) + N' đã sẵn sàng để thu hồi (RETURN_PICKUP)';

-- =====================================================
-- ĐƠN HÀNG 2: ĐÃ GIAO THÀNH CÔNG - CHỜ THU HỒI
-- =====================================================

PRINT N'📦 Tạo đơn hàng #2 - Đã giao thành công, chờ thu hồi...';

DECLARE @order2 INT;
DECLARE @items2 dbo.TVP_OrderItem;
INSERT INTO @items2(variant_id, qty) VALUES(@sku2, 1);

DECLARE @o2 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);

INSERT INTO @o2 EXEC dbo.sp_CreateOrder 
  @user_id = @userB,
  @address_id = @addrB,
  @coupon_code = NULL,
  @payment_method = N'VNPAY',
  @items = @items2;

SELECT @order2 = order_id FROM @o2;

-- Thanh toán VNPAY thành công
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @order2,
  @payment_method = N'VNPAY',
  @is_success = 1,
  @gateway_txn_code = N'VNPAY_' + CAST(@order2 AS NVARCHAR(10));

-- Xác nhận và giao hàng thành công
EXEC dbo.sp_ConfirmOrder @order_id = @order2, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @order2, @delivery_team_id = @teamBac, @actor_user_id = @managerID;

DECLARE @proof2 NVARCHAR(255) = N'/static/images/deliveries/order-' + CAST(@order2 AS NVARCHAR(10)) + N'/delivered.jpg';
EXEC dbo.sp_MarkDelivered @order_id = @order2, @proof_image_url = @proof2, @actor_user_id = @managerID;

-- Khách hàng yêu cầu trả hàng
EXEC dbo.sp_RequestReturn
  @order_id = @order2,
  @customer_id = @userB,
  @reason = N'Sản phẩm bị lỗi kỹ thuật, ghế không thể điều chỉnh độ cao như quảng cáo.';

-- Manager phê duyệt yêu cầu trả hàng
EXEC dbo.sp_ApproveReturn 
  @order_id = @order2, 
  @manager_id = @managerID, 
  @note = N'Đồng ý trả hàng, sản phẩm có lỗi kỹ thuật',
  @delivery_team_id = @teamBac;

PRINT N'✅ Đơn hàng #' + CAST(@order2 AS NVARCHAR(10)) + N' đã sẵn sàng để thu hồi (RETURN_PICKUP)';

-- =====================================================
-- ĐƠN HÀNG 3: ĐÃ GIAO THÀNH CÔNG - CHỜ THU HỒI
-- =====================================================

PRINT N'📦 Tạo đơn hàng #3 - Đã giao thành công, chờ thu hồi...';

DECLARE @order3 INT;
DECLARE @items3 dbo.TVP_OrderItem;
INSERT INTO @items3(variant_id, qty) VALUES(@sku3, 1);

DECLARE @o3 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);

INSERT INTO @o3 EXEC dbo.sp_CreateOrder 
  @user_id = @userA,
  @address_id = @addrA,
  @coupon_code = N'VIP20',
  @payment_method = N'MOMO',
  @items = @items3;

SELECT @order3 = order_id FROM @o3;

-- Thanh toán MOMO thành công
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @order3,
  @payment_method = N'MOMO',
  @is_success = 1,
  @gateway_txn_code = N'MOMO_' + CAST(@order3 AS NVARCHAR(10));

-- Xác nhận và giao hàng thành công
EXEC dbo.sp_ConfirmOrder @order_id = @order3, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @order3, @delivery_team_id = @teamNam, @actor_user_id = @managerID;

DECLARE @proof3 NVARCHAR(255) = N'/static/images/deliveries/order-' + CAST(@order3 AS NVARCHAR(10)) + N'/delivered.jpg';
EXEC dbo.sp_MarkDelivered @order_id = @order3, @proof_image_url = @proof3, @actor_user_id = @managerID;

-- Khách hàng yêu cầu trả hàng
EXEC dbo.sp_RequestReturn
  @order_id = @order3,
  @customer_id = @userA,
  @reason = N'Kích thước sản phẩm không phù hợp với không gian phòng ngủ của tôi.';

-- Manager phê duyệt yêu cầu trả hàng
EXEC dbo.sp_ApproveReturn 
  @order_id = @order3, 
  @manager_id = @managerID, 
  @note = N'Đồng ý trả hàng, kích thước không phù hợp',
  @delivery_team_id = @teamNam;

PRINT N'✅ Đơn hàng #' + CAST(@order3 AS NVARCHAR(10)) + N' đã sẵn sàng để thu hồi (RETURN_PICKUP)';

-- =====================================================
-- 2. TẠO THÔNG BÁO CHO DELIVERY TEAM
-- =====================================================

PRINT N'🔔 Tạo thông báo cho đội giao hàng...';

-- Thông báo cho đội giao Nam
DECLARE @deliveryUserNam INT = (SELECT id FROM dbo.Users WHERE username = N'delivery_south');
INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
VALUES
(@deliveryUserNam, N'Có đơn hàng cần thu hồi', N'Đơn hàng #' + CAST(@order1 AS NVARCHAR(10)) + N' cần thu hồi từ khách hàng Trần Khách A', 0),
(@deliveryUserNam, N'Có đơn hàng cần thu hồi', N'Đơn hàng #' + CAST(@order3 AS NVARCHAR(10)) + N' cần thu hồi từ khách hàng Trần Khách A', 0);

-- Thông báo cho đội giao Bắc
DECLARE @deliveryUserBac INT = (SELECT id FROM dbo.Users WHERE username = N'delivery_north');
INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
VALUES
(@deliveryUserBac, N'Có đơn hàng cần thu hồi', N'Đơn hàng #' + CAST(@order2 AS NVARCHAR(10)) + N' cần thu hồi từ khách hàng Đỗ Khách B', 0);

-- =====================================================
-- 3. HIỂN THỊ THÔNG TIN ĐỂ TEST
-- =====================================================

PRINT N'📋 Thông tin các đơn hàng sẵn sàng để thu hồi:';

SELECT 
    N'Đơn hàng sẵn sàng thu hồi' as Loai,
    o.id as OrderID,
    o.status as OrderStatus,
    o.return_status as ReturnStatus,
    o.payment_method as PaymentMethod,
    o.payment_status as PaymentStatus,
    u.full_name as CustomerName,
    u.phone as CustomerPhone,
    dt.name as DeliveryTeam,
    od.status as DeliveryStatus,
    od.note as DeliveryNote
FROM dbo.Orders o
JOIN dbo.Users u ON u.id = o.user_id
JOIN dbo.OrderDelivery od ON od.order_id = o.id
JOIN dbo.DeliveryTeam dt ON dt.id = od.delivery_team_id
WHERE o.return_status = N'APPROVED' 
  AND od.status = N'RETURN_PICKUP'
ORDER BY o.id;

PRINT N'📱 Thông tin đăng nhập để test:';
PRINT N'   👤 Delivery Team Nam: username=delivery_south, password=demo123';
PRINT N'   👤 Delivery Team Bắc: username=delivery_north, password=demo123';
PRINT N'   👤 Manager: username=manager, password=demo123';

PRINT N'🔗 URL để test:';
PRINT N'   📱 Delivery Dashboard: http://localhost:8080/delivery';
PRINT N'   📱 Chi tiết đơn hàng: http://localhost:8080/delivery/orders/{orderId}';
PRINT N'   📱 API thông báo: http://localhost:8080/delivery/api/notifications';

COMMIT TRANSACTION;

PRINT N'🎉 Hoàn thành tạo dữ liệu mẫu cho chức năng thu hồi hàng!';
PRINT N'';
PRINT N'📝 HƯỚNG DẪN TEST:';
PRINT N'1. Đăng nhập với tài khoản delivery team';
PRINT N'2. Vào Dashboard sẽ thấy các đơn hàng có trạng thái "Cần thu hồi"';
PRINT N'3. Click "Chi tiết" để xem thông tin đơn hàng';
PRINT N'4. Chọn phương thức hoàn tiền và nhập ghi chú';
PRINT N'5. Click "XÁC NHẬN ĐÃ THU HỒI" để hoàn tất';
PRINT N'';
PRINT N'✅ Chức năng thu hồi hàng đã sẵn sàng để test!';
