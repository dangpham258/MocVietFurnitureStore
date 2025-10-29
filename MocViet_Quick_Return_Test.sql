-- =====================================================
-- MOC VIET - SCRIPT NHANH TẠO DỮ LIỆU THU HỒI HÀNG
-- =====================================================
-- Script này tạo nhanh 1 đơn hàng để test chức năng thu hồi hàng
-- Chạy sau khi đã có dữ liệu cơ bản

USE MocViet;
GO

PRINT N'🚀 Tạo nhanh đơn hàng để test thu hồi...';

BEGIN TRANSACTION;

-- Lấy các ID cần thiết
DECLARE @userA INT = (SELECT TOP 1 id FROM dbo.Users WHERE username = N'cust_a');
DECLARE @managerID INT = (SELECT TOP 1 id FROM dbo.Users WHERE username = N'manager');
DECLARE @teamNam INT = (SELECT TOP 1 id FROM dbo.DeliveryTeam WHERE name = N'Đội Giao Khu Vực Nam');
DECLARE @addrA INT = (SELECT TOP 1 id FROM dbo.Address WHERE user_id = @userA);
DECLARE @sku1 INT = (SELECT TOP 1 id FROM dbo.ProductVariant WHERE sku = N'BANOC6_DEN');

-- Tạo đơn hàng mới
DECLARE @orderNew INT;
DECLARE @itemsNew dbo.TVP_OrderItem;
INSERT INTO @itemsNew(variant_id, qty) VALUES(@sku1, 1);

DECLARE @oNew TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);

INSERT INTO @oNew EXEC dbo.sp_CreateOrder 
  @user_id = @userA,
  @address_id = @addrA,
  @coupon_code = N'WELCOME10',
  @payment_method = N'COD',
  @items = @itemsNew;

SELECT @orderNew = order_id FROM @oNew;

-- Thanh toán thành công
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @orderNew,
  @payment_method = N'COD',
  @is_success = 1,
  @gateway_txn_code = N'COD_' + CAST(@orderNew AS NVARCHAR(10));

-- Xác nhận và giao hàng thành công
EXEC dbo.sp_ConfirmOrder @order_id = @orderNew, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @orderNew, @delivery_team_id = @teamNam, @actor_user_id = @managerID;

DECLARE @proofNew NVARCHAR(255) = N'/static/images/deliveries/order-' + CAST(@orderNew AS NVARCHAR(10)) + N'/delivered.jpg';
EXEC dbo.sp_MarkDelivered @order_id = @orderNew, @proof_image_url = @proofNew, @actor_user_id = @managerID;

-- Cập nhật payment_status = PAID (COD)
UPDATE dbo.Orders SET payment_status = N'PAID' WHERE id = @orderNew;

-- Khách hàng yêu cầu trả hàng
EXEC dbo.sp_RequestReturn
  @order_id = @orderNew,
  @customer_id = @userA,
  @reason = N'Sản phẩm không đúng màu sắc như mô tả trên website.';

-- Manager phê duyệt yêu cầu trả hàng
EXEC dbo.sp_ApproveReturn 
  @order_id = @orderNew, 
  @manager_id = @managerID, 
  @note = N'Đồng ý trả hàng, sản phẩm không đúng mô tả',
  @delivery_team_id = @teamNam;

-- Tạo thông báo cho delivery team
DECLARE @deliveryUserNam INT = (SELECT id FROM dbo.Users WHERE username = N'delivery_south');
INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
VALUES
(@deliveryUserNam, N'Có đơn hàng cần thu hồi', N'Đơn hàng #' + CAST(@orderNew AS NVARCHAR(10)) + N' cần thu hồi từ khách hàng Trần Khách A', 0);

COMMIT TRANSACTION;

PRINT N'✅ Đã tạo đơn hàng #' + CAST(@orderNew AS NVARCHAR(10)) + N' sẵn sàng để thu hồi!';
PRINT N'';
PRINT N'📱 Đăng nhập để test:';
PRINT N'   👤 Username: delivery_south';
PRINT N'   🔑 Password: demo123';
PRINT N'   🔗 URL: http://localhost:8080/delivery';
PRINT N'';
PRINT N'🎯 Đơn hàng sẽ hiển thị với trạng thái "Cần thu hồi" trong dashboard!';
