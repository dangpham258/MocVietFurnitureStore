/* =========================================================
   Database: MocViet
   Schema cho hệ thống bán hàng nội thất Mộc Việt
   
   THỐNG KÊ:
   - 34 Bảng (Tables)
   - 11 Stored Procedures (SP)
   - 23 Triggers

   
   CẤU TRÚC:
   Phần 0: Drop objects nếu đã tồn tại (để chạy lại script)
   Phần 1: Các bảng (34 bảng)
   Phần 2: Các constrains và ràng buộc dữ liệu khác
   Phần 3: Các chỉ mục (indexes) để tối ưu hiệu suất
   Phần 4: Các stored procedures và user defined types
   Phần 5: Các triggers để tự động hóa và validate dữ liệu
   Phần 6: Hệ thống thông báo (Notifications)
   ========================================================= */

-- Tạo database nếu chưa tồn tại
IF DB_ID(N'MocViet') IS NULL
BEGIN
  CREATE DATABASE MocViet;
END;
GO

USE MocViet;
GO

-- =========================================================
-- PHẦN 0: DROP ALL TABLES AND PROCEDURES 
-- (để có thể chạy lại script nhiều lần)
-- =========================================================
-- Drop Triggers
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Product_CategoryCollection_Validate')
    DROP TRIGGER dbo.TR_Product_CategoryCollection_Validate;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_TouchUpdatedAt')
    DROP TRIGGER dbo.TR_Orders_TouchUpdatedAt;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Cart_TouchUpdatedAt')
    DROP TRIGGER dbo.TR_Cart_TouchUpdatedAt;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_OrderDelivery_TouchUpdatedAt')
    DROP TRIGGER dbo.TR_OrderDelivery_TouchUpdatedAt;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_DeliveryTeam_ValidateUserRole')
    DROP TRIGGER dbo.TR_DeliveryTeam_ValidateUserRole;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_OrderDelivery_ValidateZone')
    DROP TRIGGER dbo.TR_OrderDelivery_ValidateZone;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_ProductVariant_AutoRoundPrice')
    DROP TRIGGER dbo.TR_ProductVariant_AutoRoundPrice;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_ShippingFee_AutoRoundBaseFee')
    DROP TRIGGER dbo.TR_ShippingFee_AutoRoundBaseFee;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_PV_ValidateActiveRefs')
    DROP TRIGGER dbo.TR_PV_ValidateActiveRefs;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Review_UpdateProductRating')
    DROP TRIGGER dbo.TR_Review_UpdateProductRating;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_NotifyNewOrder')
    DROP TRIGGER dbo.TR_Orders_NotifyNewOrder;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_ProductVariant_NotifyWishlistBackInStock')
    DROP TRIGGER dbo.TR_ProductVariant_NotifyWishlistBackInStock;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Review_NotifyManagerResponse')
    DROP TRIGGER dbo.TR_Review_NotifyManagerResponse;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_NotifyManagerNewOrder')
    DROP TRIGGER dbo.TR_Orders_NotifyManagerNewOrder;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_OrderDelivery_NotifyDeliveryTeam')
    DROP TRIGGER dbo.TR_OrderDelivery_NotifyDeliveryTeam;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_NotifyStatusChange')
    DROP TRIGGER dbo.TR_Orders_NotifyStatusChange;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Review_NotifyNew')
    DROP TRIGGER dbo.TR_Review_NotifyNew;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_ProductVariant_StockAlerts')
    DROP TRIGGER dbo.TR_ProductVariant_StockAlerts;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Message_NotifyManager')
    DROP TRIGGER dbo.TR_Message_NotifyManager;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Article_NotifyNew')
    DROP TRIGGER dbo.TR_Article_NotifyNew;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_NotifyReturnRequest')
    DROP TRIGGER dbo.TR_Orders_NotifyReturnRequest;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_NotifyCustomerReturnRejected')
    DROP TRIGGER dbo.TR_Orders_NotifyCustomerReturnRejected;
GO

IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Orders_NotifyCustomerReturnApproved')
    DROP TRIGGER dbo.TR_Orders_NotifyCustomerReturnApproved;
GO

-- Drop Stored Procedures
IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_CreateOrder')
    DROP PROCEDURE dbo.sp_CreateOrder;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_CancelOrder')
    DROP PROCEDURE dbo.sp_CancelOrder;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_ReturnOrder')
    DROP PROCEDURE dbo.sp_ReturnOrder;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_ConfirmOrder')
    DROP PROCEDURE dbo.sp_ConfirmOrder;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_MarkDispatched')
    DROP PROCEDURE dbo.sp_MarkDispatched;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_MarkDelivered')
    DROP PROCEDURE dbo.sp_MarkDelivered;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_ApproveReturn')
    DROP PROCEDURE dbo.sp_ApproveReturn;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_RejectReturn')
    DROP PROCEDURE dbo.sp_RejectReturn;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_RequestReturn')
    DROP PROCEDURE dbo.sp_RequestReturn;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_HandlePaymentWebhook')
    DROP PROCEDURE dbo.sp_HandlePaymentWebhook;
GO

IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_AutoCancelUnpaidOnline')
    DROP PROCEDURE dbo.sp_AutoCancelUnpaidOnline;
GO

-- Drop User Defined Types
IF EXISTS (SELECT * FROM sys.types WHERE name = 'TVP_OrderItem')
    DROP TYPE dbo.TVP_OrderItem;
GO

-- Drop Tables (theo thứ tự ngược để tránh foreign key constraints)

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'SocialLink')
    DROP TABLE dbo.SocialLink;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Showroom')
    DROP TABLE dbo.Showroom;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'StaticPage')
    DROP TABLE dbo.StaticPage;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Banner')
    DROP TABLE dbo.Banner;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Message')
    DROP TABLE dbo.Message;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Conversation')
    DROP TABLE dbo.Conversation;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'UserNotification')
    DROP TABLE dbo.UserNotification;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Viewed')
    DROP TABLE dbo.Viewed;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Wishlist')
    DROP TABLE dbo.Wishlist;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'DeliveryHistory')
    DROP TABLE dbo.DeliveryHistory;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'OrderDelivery')
    DROP TABLE dbo.OrderDelivery;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'DeliveryTeamZone')
    DROP TABLE dbo.DeliveryTeamZone;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'DeliveryTeam')
    DROP TABLE dbo.DeliveryTeam;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ProvinceZone')
    DROP TABLE dbo.ProvinceZone;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ShippingFee')
    DROP TABLE dbo.ShippingFee;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ShippingZone')
    DROP TABLE dbo.ShippingZone;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Review')
    DROP TABLE dbo.Review;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'OrderStatusHistory')
    DROP TABLE dbo.OrderStatusHistory;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'OrderItems')
    DROP TABLE dbo.OrderItems;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Orders')
    DROP TABLE dbo.Orders;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Coupon')
    DROP TABLE dbo.Coupon;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'CartItem')
    DROP TABLE dbo.CartItem;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Cart')
    DROP TABLE dbo.Cart;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ArticleImage')
    DROP TABLE dbo.ArticleImage;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Article')
    DROP TABLE dbo.Article;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ProductImage')
    DROP TABLE dbo.ProductImage;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ProductVariant')
    DROP TABLE dbo.ProductVariant;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Color')
    DROP TABLE dbo.Color;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Product')
    DROP TABLE dbo.Product;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Category')
    DROP TABLE dbo.Category;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Address')
    DROP TABLE dbo.Address;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'OTP')
    DROP TABLE dbo.OTP;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Users')
    DROP TABLE dbo.Users;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Roles')
    DROP TABLE dbo.Roles;
GO







-- =========================================================
-- PHẦN 1: CÁC BẢNG TRONG CƠ SỞ DỮ LIỆU
-- =========================================================

-- Bảng Roles (1 user : 1 role)
CREATE TABLE dbo.Roles (
    id           INT IDENTITY(1,1) PRIMARY KEY,           -- PK
    name         NVARCHAR(30) NOT NULL UNIQUE,            -- Tên role duy nhất: CUSTOMER / MANAGER / ADMIN / DELIVERY
    description  NVARCHAR(200) NULL                       -- Mô tả ngắn
);

-- Bảng Users (có role_id trực tiếp)
CREATE TABLE dbo.Users (
    id             INT IDENTITY(1,1) PRIMARY KEY,         -- PK
    role_id        INT NOT NULL,                          -- FK -> Roles (1 user = 1 role)
    full_name      NVARCHAR(120) NOT NULL,                -- Họ và tên
    username       NVARCHAR(60)  NOT NULL UNIQUE,         -- Username duy nhất (login)
    email          NVARCHAR(120) NOT NULL UNIQUE,         -- Email duy nhất
    password_hash  NVARCHAR(255) NOT NULL,                -- Mật khẩu hash (Bcrypt)
    gender         NVARCHAR(10)  NULL,                    -- Nam/Nữ/Khác
    dob            DATE          NULL,                    -- Ngày sinh
    phone          NVARCHAR(20)  NULL,                    -- SĐT
    is_active      BIT NOT NULL DEFAULT 1,                -- Khóa/mở
    created_at     DATETIME NOT NULL DEFAULT GETDATE(),   -- Tạo lúc
    CONSTRAINT FK_Users_Role FOREIGN KEY (role_id) REFERENCES dbo.Roles(id) ON DELETE NO ACTION
);

-- Bảng OTP: Mã OTP cho đăng ký / quên mật khẩu
CREATE TABLE dbo.OTP (
    id          INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    user_id     INT NOT NULL,                               -- FK -> Users
    code        NVARCHAR(10) NOT NULL,                      -- Mã OTP
    purpose     NVARCHAR(30) NOT NULL,                      -- 'REGISTER' | 'RESET_PASSWORD'
    is_used     BIT NOT NULL DEFAULT 0,                     -- Đã dùng?
    expires_at  DATETIME NOT NULL,                          -- Hết hạn
    created_at  DATETIME NOT NULL DEFAULT GETDATE(),        -- Tạo lúc
    CONSTRAINT FK_OTP_User FOREIGN KEY (user_id) REFERENCES dbo.Users(id) ON DELETE CASCADE
);

-- Bảng Address: Nhiều địa chỉ giao hàng cho mỗi customer
CREATE TABLE dbo.Address (
    id            INT IDENTITY(1,1) PRIMARY KEY,            -- PK
    user_id       INT NOT NULL,                             -- FK -> Users
    receiver_name NVARCHAR(120) NOT NULL,                   -- Tên người nhận
    phone         NVARCHAR(20)  NOT NULL,                   -- SĐT
    address_line  NVARCHAR(255) NOT NULL,                   -- Địa chỉ chi tiết
    city          NVARCHAR(100) NOT NULL,                   -- Tỉnh/Thành
    district      NVARCHAR(100) NULL,                       -- Quận/Huyện
    is_default    BIT NOT NULL DEFAULT 0,                   -- Mặc định?
    created_at    DATETIME NOT NULL DEFAULT GETDATE(),      -- Tạo lúc
    CONSTRAINT FK_Address_User FOREIGN KEY (user_id) REFERENCES dbo.Users(id) ON DELETE CASCADE
);

-- Bảng Category: Gom cả DANH MỤC & COLLECTION; route: /collections/:slug
CREATE TABLE dbo.Category (
    id         INT IDENTITY(1,1) PRIMARY KEY,               -- PK
    parent_id  INT NULL,                                    -- Cha (cho CATEGORY con)
    name       NVARCHAR(120) NOT NULL,                      -- Tên hiển thị
    slug       NVARCHAR(160) NOT NULL UNIQUE,               -- Slug duy nhất
    [type]     NVARCHAR(20)  NOT NULL CHECK ([type] IN (N'CATEGORY', N'COLLECTION')), -- 'CATEGORY' | 'COLLECTION'
    is_active  BIT NOT NULL DEFAULT 1,                      -- Ẩn/hiện
    created_at DATETIME NOT NULL DEFAULT GETDATE(),         -- Ngày tạo
    CONSTRAINT FK_Category_Parent FOREIGN KEY (parent_id) REFERENCES dbo.Category(id) ON DELETE NO ACTION
);

-- Bảng Product: Thông tin chung; mỗi sp thuộc 1 danh mục con (bắt buộc) + 1 collection (tùy chọn)
CREATE TABLE dbo.Product (
    id            INT IDENTITY(1,1) PRIMARY KEY,            -- PK
    name          NVARCHAR(160) NOT NULL,                   -- Tên sản phẩm
    slug          NVARCHAR(180) NOT NULL UNIQUE,            -- Slug duy nhất /products/:slug
    description   NVARCHAR(MAX) NULL,                       -- Mô tả
    views         INT NOT NULL DEFAULT 0,                   -- Lượt xem (Top nổi bật)
    sold_qty      INT NOT NULL DEFAULT 0,                   -- Tổng số lượng đã bán
    avg_rating    DECIMAL(2,1) NULL,                        -- Điểm đánh giá trung bình
    total_reviews INT NOT NULL DEFAULT 0,                   -- Tổng số đánh giá
    is_active     BIT NOT NULL DEFAULT 1,                   -- Ẩn/hiện sản phẩm
    created_at    DATETIME NOT NULL DEFAULT GETDATE(),      -- Ngày tạo
    category_id   INT NOT NULL,                             -- FK -> Category (CATEGORY, phải là lá)
    collection_id INT NULL,                                 -- FK -> Category (COLLECTION, có thể NULL)
    CONSTRAINT FK_Product_Category   FOREIGN KEY (category_id)   REFERENCES dbo.Category(id) ON DELETE NO ACTION,
    CONSTRAINT FK_Product_Collection FOREIGN KEY (collection_id) REFERENCES dbo.Category(id) ON DELETE SET NULL
);

-- Bảng Color: Màu sắc sản phẩm
CREATE TABLE dbo.Color (
    id        INT IDENTITY(1,1) PRIMARY KEY,           -- PK
    name      NVARCHAR(80)  NOT NULL UNIQUE,           -- 'Nâu', 'Xanh', 'Đen', 'Trắng', 'Be', 'Xám'
    slug      NVARCHAR(100) NOT NULL UNIQUE,           -- 'nau', 'xanh', 'den', 'trang', 'be', 'xam'
    hex       CHAR(7) NULL,                            -- '#654321' ... (tùy, có thể NULL)
    is_active BIT NOT NULL DEFAULT 1                   -- Ẩn/hiện màu trong filter
);

-- Bảng ProductVariant: Lưu biến thể theo: product_id × color_id × type_name
CREATE TABLE dbo.ProductVariant (
    id               INT IDENTITY(1,1) PRIMARY KEY,           -- PK
    product_id       INT NOT NULL,                            -- FK -> Product
    color_id         INT NOT NULL,                            -- FK -> Color (bắt buộc chọn màu)
    type_name        NVARCHAR(80) NOT NULL,                   -- Loại (vd: '4-ghe', '6-ghe', '1m2', '1m6'…)
    sku              NVARCHAR(80) NOT NULL UNIQUE,            -- SKU duy nhất

    price            DECIMAL(15,0) NOT NULL CHECK (price >= 0), -- Giá gốc
    discount_percent INT NOT NULL DEFAULT 0 CHECK (discount_percent BETWEEN 0 AND 100), -- % giảm (0–100)
    stock_qty        INT NOT NULL DEFAULT 0 CHECK (stock_qty >= 0), -- Tồn kho
    promotion_type   NVARCHAR(20) NULL CHECK (promotion_type IN (N'SALE', N'OUTLET')), -- SALE = ưu đãi, OUTLET = thanh lý
    is_active        BIT NOT NULL DEFAULT 1,                  -- Ẩn/hiện biến thể

    CONSTRAINT FK_PV_Product FOREIGN KEY (product_id) REFERENCES dbo.Product(id) ON DELETE CASCADE,
    CONSTRAINT FK_PV_Color FOREIGN KEY (color_id) REFERENCES dbo.Color(id)   ON DELETE NO ACTION,
    CONSTRAINT UQ_PV_Combo UNIQUE (product_id, color_id, type_name)  -- Không trùng biến thể trong 1 sản phẩm
);

-- Column sale_price của bảng ProductVariant: Tự động tính dựa trên price & discount_percent
ALTER TABLE dbo.ProductVariant ADD sale_price AS (
  CAST(
    ROUND(
      CASE WHEN discount_percent = 0
           THEN price
           ELSE price * (100 - discount_percent) / 100.0
      END, -3  -- làm tròn bậc nghìn
    ) AS DECIMAL(15,0)
  )
) PERSISTED

-- Bảng ProductImage: Ảnh đổi theo Màu, ẢNH BẮT BUỘC PHẢI GẮN VỚI MỘT MÀU
CREATE TABLE dbo.ProductImage (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    color_id   INT NOT NULL,               -- Ảnh gắn với một Màu cụ thể
    url        NVARCHAR(255) NOT NULL,
    CONSTRAINT FK_PI_Product FOREIGN KEY (product_id) REFERENCES dbo.Product(id) ON DELETE CASCADE,
    CONSTRAINT FK_PI_Color   FOREIGN KEY (color_id)   REFERENCES dbo.Color(id)   ON DELETE NO ACTION
);

-- Bảng Article: Bài viết; nhóm bằng article_type thay vì bảng N–N
CREATE TABLE dbo.Article (
    id           INT IDENTITY(1,1) PRIMARY KEY,            -- PK
    title        NVARCHAR(300) NOT NULL,                   -- Tiêu đề
    slug         NVARCHAR(300) NOT NULL UNIQUE,            -- Slug duy nhất
    article_type NVARCHAR(20)  NOT NULL DEFAULT N'NEWS' CHECK (article_type IN (N'MEDIA', N'NEWS', N'PEOPLE')), -- MEDIA/NEWS/PEOPLE
    summary      NVARCHAR(500) NULL,                       -- Tóm tắt (~25 từ)
    content      NVARCHAR(MAX) NULL,                       -- Nội dung
    thumbnail    NVARCHAR(255) NULL,                       -- Ảnh chính (cover)
    author       NVARCHAR(100) NULL,                       -- Tác giả
    views        INT NOT NULL DEFAULT 0,                   -- Lượt xem
    is_featured  BIT NOT NULL DEFAULT 0,                   -- (tùy) ghim
    [status]     BIT NOT NULL DEFAULT 1,                   -- 1=hiển thị
    published_at DATETIME NULL,                            -- Dùng sort “mới nhất”
    created_at   DATETIME NOT NULL DEFAULT GETDATE(),      -- Tạo lúc
    linked_product_id INT NULL,                            -- 1 sản phẩm liên quan (nếu có)
    CONSTRAINT FK_Article_Product FOREIGN KEY (linked_product_id) REFERENCES dbo.Product(id) ON DELETE SET NULL
);

-- Bảng ArticleImage: Ảnh trong nội dung bài viết
CREATE TABLE dbo.ArticleImage (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    article_id INT NOT NULL,                               -- FK -> Article
    url        NVARCHAR(255) NOT NULL,                     -- URL ảnh
    caption    NVARCHAR(255) NULL,                         -- Chú thích
    CONSTRAINT FK_ArticleImage_Article FOREIGN KEY (article_id) REFERENCES dbo.Article(id) ON DELETE CASCADE
);

-- Bảng Cart: 1–1 với customer (đăng nhập mới có giỏ)
CREATE TABLE dbo.Cart (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    user_id    INT NOT NULL UNIQUE,                        -- 1 user = 1 cart
    created_at DATETIME NOT NULL DEFAULT GETDATE(),        -- Tạo lúc
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),        -- Cập nhật
    CONSTRAINT FK_Cart_User FOREIGN KEY (user_id) REFERENCES dbo.Users(id) ON DELETE CASCADE
);

-- Bảng CartItem: Sản phẩm trong giỏ (theo SKU)
CREATE TABLE dbo.CartItem (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    cart_id    INT NOT NULL,                               -- FK -> Cart
    variant_id INT NOT NULL,                               -- FK -> ProductVariant
    qty        INT NOT NULL CHECK (qty > 0), -- Số lượng
    CONSTRAINT FK_CI_Cart    FOREIGN KEY (cart_id)    REFERENCES dbo.Cart(id)           ON DELETE CASCADE,
    CONSTRAINT FK_CI_Variant FOREIGN KEY (variant_id) REFERENCES dbo.ProductVariant(id) ON DELETE NO ACTION,
    CONSTRAINT UQ_CartItem UNIQUE (cart_id, variant_id)    -- Không trùng SKU trong cùng giỏ
);

-- Bảng Coupon: Mã giảm giá % với ngưỡng tối thiểu
CREATE TABLE dbo.Coupon (
    code             NVARCHAR(50) NOT NULL PRIMARY KEY,  -- Mã duy nhất
    discount_percent DECIMAL(5,2) NOT NULL CHECK (discount_percent > 0 AND discount_percent <= 100), -- % giảm
    start_date       DATETIME NOT NULL,                         -- Bắt đầu
    end_date         DATETIME NOT NULL,                         -- Kết thúc
    active           BIT NOT NULL DEFAULT 1,                    -- Bật/tắt
    min_order_amount DECIMAL(15,0) NOT NULL DEFAULT 0          -- Ngưỡng tối thiểu (sau sale, chưa ship)
);

-- Bảng Orders: Đơn hàng (không lưu total_amount)
CREATE TABLE dbo.Orders (
    id             INT IDENTITY(1,1) PRIMARY KEY,          -- PK
    user_id        INT NOT NULL,                           -- FK -> Users
    address_id     INT NOT NULL,                           -- Địa chỉ dùng khi đặt (snapshot) - BẮT BUỘC
    [status]       NVARCHAR(20) NOT NULL CHECK ([status] IN (N'PENDING', N'CONFIRMED', N'DISPATCHED', N'DELIVERED', N'CANCELLED', N'RETURNED')), -- Trạng thái đơn
    payment_method NVARCHAR(20) NULL CHECK (payment_method IN (N'COD', N'VNPAY', N'MOMO')), -- COD/VNPAY/MOMO
    payment_status NVARCHAR(20) NULL CHECK (payment_status IN (N'UNPAID', N'PAID', N'REFUNDED')), -- UNPAID/PAID/REFUNDED
    coupon_code    NVARCHAR(50) NULL,                      -- Coupon áp dụng
    shipping_fee   DECIMAL(12,0) NOT NULL DEFAULT 0,       -- Phí vận chuyển tính theo miền
    return_status  NVARCHAR(20) NULL CHECK (return_status IN (N'REQUESTED', N'APPROVED', N'REJECTED', N'PROCESSED')), -- Trạng thái yêu cầu trả hàng
    return_reason  NVARCHAR(500) NULL,                     -- Lý do trả hàng từ customer
    return_note    NVARCHAR(500) NULL,                     -- Ghi chú từ manager
    created_at     DATETIME NOT NULL DEFAULT GETDATE(),    -- Tạo lúc
    updated_at     DATETIME NOT NULL DEFAULT GETDATE(),    -- Cập nhật
    CONSTRAINT FK_Orders_User    FOREIGN KEY (user_id)    REFERENCES dbo.Users(id)   ON DELETE NO ACTION,
    CONSTRAINT FK_Orders_Address FOREIGN KEY (address_id) REFERENCES dbo.Address(id) ON DELETE NO ACTION,
    CONSTRAINT FK_Orders_Coupon   FOREIGN KEY (coupon_code) REFERENCES dbo.Coupon(code) ON DELETE SET NULL
);

-- Bảng OrderItems: Dòng hàng; gắn SKU + đơn giá tại thời điểm mua
CREATE TABLE dbo.OrderItems (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    order_id   INT NOT NULL,                               -- FK -> Orders
    variant_id INT NOT NULL,                               -- FK -> ProductVariant
    qty        INT NOT NULL CHECK (qty > 0), -- Số lượng
    unit_price DECIMAL(15,0) NOT NULL CHECK (unit_price >= 0), -- Giá tại thời điểm đặt
    CONSTRAINT FK_OI_Order   FOREIGN KEY (order_id)   REFERENCES dbo.Orders(id)         ON DELETE CASCADE,
    CONSTRAINT FK_OI_Variant FOREIGN KEY (variant_id) REFERENCES dbo.ProductVariant(id) ON DELETE NO ACTION
);

-- Bảng OrderStatusHistory: Lịch sử thay đổi trạng thái đơn
CREATE TABLE dbo.OrderStatusHistory (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    order_id   INT NOT NULL,                               -- FK -> Orders
    status     NVARCHAR(20) NOT NULL CHECK (status IN (N'PENDING', N'CONFIRMED', N'DISPATCHED', N'DELIVERED', N'CANCELLED', N'RETURNED')), -- Trạng thái mới
    note       NVARCHAR(500) NULL,                         -- Ghi chú
    changed_by INT NULL,                                   -- User thao tác (Manager/Admin) hoặc NULL = hệ thống
    changed_at DATETIME NOT NULL DEFAULT GETDATE(),        -- Thời điểm đổi
    CONSTRAINT FK_OSH_Order FOREIGN KEY (order_id) REFERENCES dbo.Orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_OSH_User  FOREIGN KEY (changed_by) REFERENCES dbo.Users(id) ON DELETE SET NULL
);

-- Bảng Review: Chỉ customer đã mua (link OrderItems) mới được đánh giá; 1 ảnh
CREATE TABLE dbo.Review (
    id               INT IDENTITY(1,1) PRIMARY KEY,           -- PK
    product_id       INT NOT NULL,                            -- Sản phẩm được đánh giá
    user_id          INT NOT NULL,                            -- Chủ review
    order_item_id    INT NOT NULL UNIQUE,                                -- Dòng hàng đã mua (xác thực)
    rating           INT NOT NULL CHECK (rating >= 1 AND rating <= 5), -- 1–5 sao
    content          NVARCHAR(1000) NULL,                     -- Nội dung
    image_url        NVARCHAR(255) NULL,                      -- Ảnh (JPG/PNG ≤ 2MB – check ở app)
    is_hidden        BIT NOT NULL DEFAULT 0,                  -- Ẩn/hiện
    manager_response NVARCHAR(1000) NULL,                    -- Phản hồi của manager
    manager_id       INT NULL,                                -- Manager trả lời
    response_at      DATETIME NULL,                           -- Thời điểm trả lời
    created_at       DATETIME NOT NULL DEFAULT GETDATE(),     -- Tạo lúc
    CONSTRAINT FK_Review_Product   FOREIGN KEY (product_id)    REFERENCES dbo.Product(id)     ON DELETE CASCADE,
    CONSTRAINT FK_Review_User      FOREIGN KEY (user_id)       REFERENCES dbo.Users(id)       ON DELETE CASCADE,
    CONSTRAINT FK_Review_OrderItem FOREIGN KEY (order_item_id) REFERENCES dbo.OrderItems(id)  ON DELETE CASCADE,
    CONSTRAINT FK_Review_Manager   FOREIGN KEY (manager_id)    REFERENCES dbo.Users(id)       ON DELETE NO ACTION
);

-- Bảng ShippingZone: Ba miền giao hàng
CREATE TABLE dbo.ShippingZone (
    id   INT IDENTITY(1,1) PRIMARY KEY,                   -- PK
    name NVARCHAR(50) NOT NULL,                           -- Tên miền
    slug NVARCHAR(50) NOT NULL UNIQUE                     -- Slug duy nhất
);

-- Bảng ShippingFee: Phí vận chuyển
CREATE TABLE dbo.ShippingFee (
    id         INT IDENTITY(1,1) PRIMARY KEY,         -- PK
    zone_id    INT NOT NULL UNIQUE,                    -- FK -> ShippingZone (mỗi miền đúng 1 dòng)
    base_fee   DECIMAL(12,0) NOT NULL DEFAULT 0,      -- Phí ship cố định theo miền
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),   -- Lần cập nhật gần nhất
    CONSTRAINT FK_ShippingFee_Zone FOREIGN KEY (zone_id) REFERENCES dbo.ShippingZone(id) ON DELETE CASCADE
);

-- Bảng ProvinceZone: Map tỉnh/thành → miền
CREATE TABLE dbo.ProvinceZone (
    id            INT IDENTITY(1,1) PRIMARY KEY,           -- PK
    province_name NVARCHAR(100) NOT NULL UNIQUE,           -- Tên tỉnh/thành (duy nhất)
    zone_id       INT NOT NULL,                            -- FK -> ShippingZone
    CONSTRAINT FK_ProvinceZone_Zone FOREIGN KEY (zone_id) REFERENCES dbo.ShippingZone(id) ON DELETE CASCADE
);

-- Bảng DeliveryTeam: Đội giao/lắp đặt
CREATE TABLE dbo.DeliveryTeam (
    id        INT IDENTITY(1,1) PRIMARY KEY,               -- PK
    name      NVARCHAR(120) NOT NULL,                      -- Tên đội
    phone     NVARCHAR(20)  NULL,                          -- SĐT
    is_active BIT NOT NULL DEFAULT 1,                      -- Hoạt động?
    user_id   INT NOT NULL UNIQUE,                            -- User đại diện (role=DELIVERY), 1-1
    CONSTRAINT FK_DeliveryTeam_User FOREIGN KEY(user_id) REFERENCES dbo.Users(id) ON DELETE NO ACTION
);

-- Bảng DeliveryTeamZone: N–N đội ↔ miền
CREATE TABLE dbo.DeliveryTeamZone (
    id               INT IDENTITY(1,1) PRIMARY KEY,        -- PK
    delivery_team_id INT NOT NULL,                         -- FK -> DeliveryTeam
    zone_id          INT NOT NULL,                         -- FK -> ShippingZone
    CONSTRAINT FK_DTZ_Team FOREIGN KEY (delivery_team_id) REFERENCES dbo.DeliveryTeam(id) ON DELETE CASCADE,
    CONSTRAINT FK_DTZ_Zone FOREIGN KEY (zone_id) REFERENCES dbo.ShippingZone(id)  ON DELETE CASCADE,
    CONSTRAINT UQ_DTZ UNIQUE (delivery_team_id, zone_id)   -- Không trùng cặp
);


-- Bảng OrderDelivery: Gán đơn cho đội giao & theo dõi trạng thái
CREATE TABLE dbo.OrderDelivery (
    id               INT IDENTITY(1,1) PRIMARY KEY,        -- PK
    order_id         INT NOT NULL UNIQUE,                  -- FK -> Orders, 1–1: mỗi đơn chỉ có 1 OrderDelivery
    delivery_team_id INT NOT NULL,                         -- FK -> DeliveryTeam
    status           NVARCHAR(20) NOT NULL DEFAULT N'RECEIVED' CHECK (status IN (N'RECEIVED', N'IN_TRANSIT', N'DONE', N'RETURN_PICKUP')), -- Trạng thái giao nhận
    proof_image_url  NVARCHAR(255) NULL,                   -- Ảnh bàn giao
    note             NVARCHAR(500) NULL,                   -- Ghi chú
    updated_at       DATETIME NOT NULL DEFAULT GETDATE(),  -- Lần cập nhật
    CONSTRAINT FK_OD_Order FOREIGN KEY (order_id) REFERENCES dbo.Orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_OD_Team  FOREIGN KEY (delivery_team_id) REFERENCES dbo.DeliveryTeam(id) ON DELETE CASCADE
);

-- Bảng DeliveryHistory: Nhật ký trạng thái giao nhận
CREATE TABLE dbo.DeliveryHistory (
    id                INT IDENTITY(1,1) PRIMARY KEY,       -- PK
    order_delivery_id INT NOT NULL,                        -- FK -> OrderDelivery
    status            NVARCHAR(20) NOT NULL CHECK (status IN (N'RECEIVED', N'IN_TRANSIT', N'DONE', N'RETURN_PICKUP')), -- Trạng thái tại thời điểm đó
    note              NVARCHAR(500) NULL,                  -- Ghi chú
    photo_url         NVARCHAR(255) NULL,                  -- Ảnh minh họa
    changed_at        DATETIME NOT NULL DEFAULT GETDATE(), -- Thời điểm log
    CONSTRAINT FK_DH_OD FOREIGN KEY (order_delivery_id) REFERENCES dbo.OrderDelivery(id) ON DELETE CASCADE
);

-- Bảng Wishlist: Yêu thích
CREATE TABLE dbo.Wishlist (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    user_id    INT NOT NULL,                               -- FK -> Users
    product_id INT NOT NULL,                               -- FK -> Product
    created_at DATETIME NOT NULL DEFAULT GETDATE(),        -- Thời điểm thêm
    CONSTRAINT FK_WL_User    FOREIGN KEY (user_id)    REFERENCES dbo.Users(id)    ON DELETE CASCADE,
    CONSTRAINT FK_WL_Product FOREIGN KEY (product_id) REFERENCES dbo.Product(id)  ON DELETE CASCADE,
    CONSTRAINT UQ_Wishlist UNIQUE (user_id, product_id)    -- Không trùng
);

-- Bảng Viewed: Lịch sử xem (giới hạn 20 ở tầng app)
CREATE TABLE dbo.Viewed (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    user_id    INT NOT NULL,                               -- FK -> Users
    product_id INT NOT NULL,                               -- FK -> Product
    viewed_at  DATETIME NOT NULL DEFAULT GETDATE(),        -- Thời điểm xem
    CONSTRAINT FK_V_User FOREIGN KEY (user_id) REFERENCES dbo.Users(id)    ON DELETE CASCADE,
    CONSTRAINT FK_V_Product FOREIGN KEY (product_id) REFERENCES dbo.Product(id)  ON DELETE CASCADE
);

-- Bảng UserNotification: Thông báo thay đổi trạng thái/hồi đáp review…
CREATE TABLE dbo.UserNotification (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    user_id    INT NOT NULL,                               -- FK -> Users
    title      NVARCHAR(200) NOT NULL,                     -- Tiêu đề
    message    NVARCHAR(1000) NULL,                        -- Nội dung
    is_read    BIT NOT NULL DEFAULT 0,                     -- Đã đọc?
    created_at DATETIME NOT NULL DEFAULT GETDATE(),        -- Tạo lúc
    CONSTRAINT FK_UN_User FOREIGN KEY (user_id) REFERENCES dbo.Users(id) ON DELETE CASCADE
);

-- Bảng Conversation: 1 cuộc hội thoại; guest có thể để trống user_id
CREATE TABLE dbo.Conversation (
    id          INT IDENTITY(1,1) PRIMARY KEY,             -- PK
    user_id     INT NULL,                                  -- FK -> Users (NULL nếu guest)
    guest_name  NVARCHAR(120) NULL,                        -- Tên guest
    guest_email NVARCHAR(120) NULL,                        -- Email guest
    status      NVARCHAR(20) NOT NULL DEFAULT N'OPEN',     -- OPEN/CLOSED
    created_at  DATETIME NOT NULL DEFAULT GETDATE(),       -- Tạo lúc
    closed_at   DATETIME NULL,                             -- Đóng khi xong
    CONSTRAINT FK_Conv_User FOREIGN KEY (user_id) REFERENCES dbo.Users(id) ON DELETE SET NULL
);

-- Bảng Message: Tin nhắn trong hội thoại
CREATE TABLE dbo.Message (
    id               INT IDENTITY(1,1) PRIMARY KEY,        -- PK
    conversation_id  INT NOT NULL,                         -- FK -> Conversation
    sender_type      NVARCHAR(20) NOT NULL CHECK (sender_type IN (N'GUEST', N'CUSTOMER', N'MANAGER')), -- 'GUEST' | 'CUSTOMER' | 'MANAGER'
    sender_id        INT NULL,                             -- FK -> Users (nếu CUSTOMER/MANAGER)
    content          NVARCHAR(MAX) NULL,                   -- Nội dung
    attachment_url   NVARCHAR(255) NULL,                   -- File/ảnh (tùy)
    created_at       DATETIME NOT NULL DEFAULT GETDATE(),  -- Gửi lúc
    CONSTRAINT FK_Msg_Conv FOREIGN KEY (conversation_id) REFERENCES dbo.Conversation(id) ON DELETE CASCADE,
    CONSTRAINT FK_Msg_User FOREIGN KEY (sender_id) REFERENCES dbo.Users(id)        ON DELETE SET NULL
);

-- Bảng Banner: Carousel trang chủ
CREATE TABLE dbo.Banner (
    id         INT IDENTITY(1,1) PRIMARY KEY,               -- PK
    title      NVARCHAR(160) NULL,                          -- Tiêu đề ngắn
    image_url  NVARCHAR(255) NOT NULL,                      -- Ảnh banner
    link_url   NVARCHAR(255) NULL,                          -- Liên kết
    is_active  BIT NOT NULL DEFAULT 1,                       -- Bật/Tắt
    created_at DATETIME NOT NULL DEFAULT GETDATE()         -- Tạo lúc
);

-- Bảng StaticPage: Trang tĩnh (Chính sách/Giới thiệu/…)
CREATE TABLE dbo.StaticPage (
    id         INT IDENTITY(1,1) PRIMARY KEY,               -- PK
    slug       NVARCHAR(120) NOT NULL UNIQUE,               -- 'chinh-sach-ban-hang', …
    title      NVARCHAR(200) NOT NULL,                      -- Tiêu đề
    content    NVARCHAR(MAX) NULL,                          -- Nội dung
    is_active  BIT NOT NULL DEFAULT 1,                       -- Hiển thị?
    updated_at DATETIME NOT NULL DEFAULT GETDATE()         -- Cập nhật
);

-- Bảng Showroom: Địa điểm cửa hàng
CREATE TABLE dbo.Showroom (
    id         INT IDENTITY(1,1) PRIMARY KEY,              -- PK
    name       NVARCHAR(120) NOT NULL,                     -- Tên showroom
    address    NVARCHAR(255) NOT NULL,                     -- Địa chỉ
    city       NVARCHAR(100) NOT NULL,                     -- Tỉnh/Thành
    district   NVARCHAR(100) NULL,                         -- Quận/Huyện
    email      NVARCHAR(120) NULL,                         -- Email
    phone      NVARCHAR(20)  NULL,                         -- SĐT
    open_hours NVARCHAR(120) NULL,                         -- Giờ mở cửa
    map_embed  NVARCHAR(MAX) NULL,                         -- Google Map iframe
    is_active  BIT NOT NULL DEFAULT 1,                      -- Hiển thị?
    created_at DATETIME NOT NULL DEFAULT GETDATE()         -- Tạo lúc
);


-- Bảng SocialLink: Liên kết mạng xã hội
CREATE TABLE dbo.SocialLink (
    id         INT IDENTITY(1,1) PRIMARY KEY,             -- PK
    platform   NVARCHAR(50) NOT NULL UNIQUE,              -- 'FACEBOOK', 'ZALO', 'YOUTUBE'
    url        NVARCHAR(255) NOT NULL,                     -- Link đến trang
    is_active  BIT NOT NULL DEFAULT 1,                     -- Hiển thị?
    created_at DATETIME NOT NULL DEFAULT GETDATE()        -- Tạo lúc
);







-- =========================================================
-- PHẦN 2: CÁC CONSTRAINTS VÀ RÀNG BUỘC DỮ LIỆU KHÁC
-- =========================================================

-- Coupon: Ngày bắt đầu phải nhỏ hơn ngày kết thúc
ALTER TABLE dbo.Coupon
ADD CONSTRAINT CK_Coupon_Dates CHECK (start_date < end_date);

-- Article.thumbnail phải ở /static/images/articles/
ALTER TABLE dbo.Article
ADD CONSTRAINT CK_Article_Thumbnail_StaticRoot
CHECK (thumbnail IS NULL OR thumbnail LIKE N'/static/images/articles/%');

-- Review.image_url phải ở /static/images/reviews/
ALTER TABLE dbo.Review
ADD CONSTRAINT CK_Review_Image_StaticRoot
CHECK (image_url IS NULL OR image_url LIKE N'/static/images/reviews/%');

-- OrderDelivery.proof_image_url phải ở /static/images/deliveries/
ALTER TABLE dbo.OrderDelivery
ADD CONSTRAINT CK_OrderDelivery_Proof_StaticRoot
CHECK (proof_image_url IS NULL OR proof_image_url LIKE N'/static/images/deliveries/%');

-- Message.attachment_url phải ở /static/images/messages/
ALTER TABLE dbo.Message
ADD CONSTRAINT CK_Message_Attachment_StaticRoot
CHECK (attachment_url IS NULL OR attachment_url LIKE N'/static/images/messages/%');

-- ProductImage.url phải ở /static/images/products/
ALTER TABLE dbo.ProductImage
ADD CONSTRAINT CK_ProductImage_StaticRoot
CHECK (url LIKE N'/static/images/products/%');

-- ArticleImage.url phải ở /static/images/articles/
ALTER TABLE dbo.ArticleImage
ADD CONSTRAINT CK_ArticleImage_StaticRoot
CHECK (url LIKE N'/static/images/articles/%');

-- Banner.image_url phải ở /static/images/banners/
ALTER TABLE dbo.Banner
ADD CONSTRAINT CK_Banner_Image_StaticRoot
CHECK (image_url LIKE N'/static/images/banners/%');

-- Slug validation: lowercase + chỉ chữ cái, số, gạch ngang
ALTER TABLE dbo.Category
ADD CONSTRAINT CK_Category_Slug_Clean
CHECK (slug = LOWER(slug) AND slug COLLATE Latin1_General_100_BIN2 NOT LIKE '%[^a-z0-9-]%');

ALTER TABLE dbo.Product
ADD CONSTRAINT CK_Product_Slug_Clean
CHECK (slug = LOWER(slug) AND slug COLLATE Latin1_General_100_BIN2 NOT LIKE '%[^a-z0-9-]%');

ALTER TABLE dbo.Article
ADD CONSTRAINT CK_Article_Slug_Clean
CHECK (slug = LOWER(slug) AND slug COLLATE Latin1_General_100_BIN2 NOT LIKE '%[^a-z0-9-]%');

ALTER TABLE dbo.Color
ADD CONSTRAINT CK_Color_Slug_Clean
CHECK (slug = LOWER(slug) AND slug COLLATE Latin1_General_100_BIN2 NOT LIKE '%[^a-z0-9-]%');








-- =========================================================
-- PHẦN 3: CÁC CHỈ MỤC (INDEXES) ĐỂ TỐI ƯU HIỆU SUẤT
-- =========================================================
CREATE INDEX IX_Product_Category ON dbo.Product(category_id);
CREATE INDEX IX_Product_Collection ON dbo.Product(collection_id);
CREATE INDEX IX_ProductVariant_Product ON dbo.ProductVariant(product_id);
CREATE INDEX IX_ProductImage_Product ON dbo.ProductImage(product_id);
CREATE INDEX IX_OrderItems_Variant ON dbo.OrderItems(variant_id);
CREATE INDEX IX_Review_Product ON dbo.Review(product_id);
CREATE INDEX IX_Viewed_UserTime ON dbo.Viewed(user_id, viewed_at DESC);
CREATE INDEX IX_UserNotification_User ON dbo.UserNotification(user_id, is_read);
CREATE INDEX IX_OrderStatusHistory_Order ON dbo.OrderStatusHistory(order_id, changed_at DESC);
CREATE INDEX IX_PV_ProductColorType ON dbo.ProductVariant(product_id, color_id, type_name);
CREATE INDEX IX_OrderItems_Order ON dbo.OrderItems(order_id) INCLUDE(variant_id, qty, unit_price);
CREATE INDEX IX_ProductImage_ProductColor ON dbo.ProductImage(product_id, color_id);
CREATE INDEX IX_Coupon_ActiveTime ON dbo.Coupon(active, start_date, end_date) INCLUDE(discount_percent, min_order_amount);
CREATE INDEX IX_ProductVariant_Active ON dbo.ProductVariant(product_id, color_id, type_name) INCLUDE (price, sale_price, stock_qty, is_active) WHERE is_active = 1;
CREATE INDEX IX_Article_VisibleLatest ON dbo.Article(published_at DESC) INCLUDE (title, slug, thumbnail) WHERE [status] = 1;
CREATE INDEX IX_Orders_User_CreatedAt ON dbo.Orders(user_id, created_at DESC);
CREATE INDEX IX_Address_User ON dbo.Address(user_id); -- List địa chỉ theo customer
CREATE INDEX IX_OTP_UserPurposeUnused ON dbo.OTP(user_id, purpose, is_used) WHERE is_used = 0; -- Check OTP còn hiệu lực
CREATE INDEX IX_DeliveryHistory_Timeline ON dbo.DeliveryHistory(order_delivery_id, changed_at DESC); -- Timeline theo đơn giao
CREATE INDEX IX_ProvinceZone_Province ON dbo.ProvinceZone(province_name) INCLUDE(zone_id);
CREATE INDEX IX_DeliveryTeamZone_TeamZone ON dbo.DeliveryTeamZone(delivery_team_id, zone_id);
CREATE INDEX IX_OrderDelivery_Team ON dbo.OrderDelivery(delivery_team_id) INCLUDE(order_id, status, updated_at);
CREATE INDEX IX_Orders_Status_CreatedAt ON dbo.Orders([status], created_at DESC);
CREATE INDEX IX_ArticleImage_Article ON dbo.ArticleImage(article_id);
CREATE INDEX IX_Review_Product_Visible_ForAgg ON dbo.Review(product_id) INCLUDE (rating) WHERE is_hidden = 0;
CREATE INDEX IX_Users_RoleActive ON dbo.Users(role_id) INCLUDE(is_active, full_name, email);
CREATE INDEX IX_Roles_Name ON dbo.Roles(name) INCLUDE(id);
CREATE INDEX IX_Product_CategoryActiveSold ON dbo.Product(category_id) INCLUDE(is_active, sold_qty, avg_rating, created_at);
CREATE INDEX IX_PV_ByProduct_ForDetail ON dbo.ProductVariant(product_id) INCLUDE(color_id, type_name, sale_price, stock_qty) WHERE is_active=1;
CREATE INDEX IX_UserNotification_Dedupe ON dbo.UserNotification(user_id, title, created_at DESC);
CREATE INDEX IX_Orders_Return_Status ON dbo.Orders(return_status, [status]) INCLUDE(user_id, payment_status, created_at);






-- =========================================================
-- PHẦN 4: CÁC STORED PROCEDURES VÀ USER DEFINED TYPES
-- =========================================================

-- Kiểu bảng input cho các dòng hàng
CREATE TYPE dbo.TVP_OrderItem AS TABLE (
  variant_id INT NOT NULL,
  qty        INT NOT NULL CHECK (qty > 0)
);
GO


-- Stored Procedure: Tạo đơn hàng mới với validation đầy đủ
CREATE OR ALTER PROCEDURE dbo.sp_CreateOrder
  @user_id      INT,
  @address_id   INT = NULL,
  @coupon_code  NVARCHAR(50) = NULL,
  @payment_method NVARCHAR(20) = N'COD',  -- COD | VNPAY | MOMO
  @items        dbo.TVP_OrderItem READONLY
AS
BEGIN
  SET NOCOUNT ON;
  SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  BEGIN TRAN;

  -- 0) Validate cơ bản
  IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE id = @user_id AND is_active = 1)
  BEGIN RAISERROR (N'Customer không hợp lệ hoặc bị khóa.',16,1); ROLLBACK TRAN; RETURN; END

  IF NOT EXISTS (SELECT 1 FROM dbo.Address WHERE id = @address_id AND user_id = @user_id)
  BEGIN RAISERROR (N'Địa chỉ không thuộc về customer.',16,1); ROLLBACK TRAN; RETURN; END

  IF @payment_method NOT IN (N'COD', N'VNPAY', N'MOMO')
  BEGIN RAISERROR (N'payment_method không hợp lệ. Chỉ chấp nhận COD, VNPAY, MOMO.',16,1); ROLLBACK TRAN; RETURN; END

  IF NOT EXISTS (SELECT 1 FROM @items)
  BEGIN RAISERROR (N'Giỏ hàng trống.',16,1); ROLLBACK TRAN; RETURN; END

  -- 1) Gộp trùng SKU và validate tồn + trạng thái
  DECLARE @temp_items TABLE (variant_id INT, qty INT, sale_price DECIMAL(15,0), stock_qty INT, is_active BIT);
  
  INSERT INTO @temp_items
  SELECT ia.variant_id, ia.qty, pv.sale_price, pv.stock_qty, pv.is_active
  FROM (
    SELECT variant_id, SUM(qty) AS qty
    FROM @items
    GROUP BY variant_id
  ) ia
  JOIN dbo.ProductVariant pv WITH (UPDLOCK, ROWLOCK) ON pv.id = ia.variant_id;

  -- 2) Validate SKU tồn tại
  IF (SELECT COUNT(*) FROM @temp_items) <> (SELECT COUNT(DISTINCT variant_id) FROM @items)
  BEGIN
    RAISERROR (N'Một hoặc nhiều SKU không tồn tại.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- 3) Validate tồn + trạng thái
  IF EXISTS (SELECT 1 FROM @temp_items WHERE stock_qty < qty)
  BEGIN RAISERROR (N'Tồn kho không đủ cho một hoặc nhiều biến thể.',16,1); ROLLBACK TRAN; RETURN; END

  IF EXISTS (SELECT 1 FROM @temp_items WHERE is_active = 0)
  BEGIN RAISERROR (N'Một hoặc nhiều biến thể không ở trạng thái active.',16,1); ROLLBACK TRAN; RETURN; END

  -- 4) Tính subtotal (VND nguyên)
  DECLARE @subtotal DECIMAL(19,0) =
    (SELECT SUM(CONVERT(DECIMAL(19,0), sale_price) * CONVERT(DECIMAL(19,0), qty))
    FROM @temp_items);

  IF @subtotal IS NULL OR @subtotal < 0
  BEGIN RAISERROR (N'Lỗi tính tiền giỏ hàng.',16,1); ROLLBACK TRAN; RETURN; END

  -- 5) Validate coupon (nếu có) và tính discount
  DECLARE @discount_amount DECIMAL(19,0) = 0;

  IF @coupon_code IS NOT NULL
  BEGIN
    DECLARE @coupon_percent DECIMAL(5,2);

    SELECT @coupon_percent = c.discount_percent
    FROM dbo.Coupon c
    WHERE c.code = @coupon_code
      AND c.active = 1
      AND GETDATE() BETWEEN c.start_date AND c.end_date
      AND @subtotal >= c.min_order_amount;

    IF @coupon_percent IS NULL
    BEGIN
      RAISERROR (N'Coupon không hợp lệ/không còn hiệu lực hoặc chưa đạt ngưỡng.',16,1);
      ROLLBACK TRAN; RETURN;
    END

    -- Làm tròn về VND nguyên (đổi 0 -> -3 nếu muốn 1.000đ)
    SET @discount_amount = CONVERT(DECIMAL(19,0),
  ROUND(@subtotal * (@coupon_percent / 100.0), -3)  -- bậc nghìn
);

  END

  DECLARE @total_after_coupon DECIMAL(19,0) = @subtotal - @discount_amount;
  IF @total_after_coupon < 0 SET @total_after_coupon = 0;

  -- 5.1) Validate tỉnh/thành phải được map vào zone
  IF NOT EXISTS (
    SELECT 1
    FROM dbo.Address a
    JOIN dbo.ProvinceZone pz ON pz.province_name = a.city
    WHERE a.id = @address_id
  )
  BEGIN
    RAISERROR (N'Tỉnh/thành chưa được map vào miền giao hàng.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- 5.2) Tính phí vận chuyển theo miền
  DECLARE @shipping_fee DECIMAL(12,0);
  SELECT @shipping_fee = sf.base_fee
  FROM dbo.Address a
  JOIN dbo.ProvinceZone pz ON pz.province_name = a.city
  JOIN dbo.ShippingFee sf ON sf.zone_id = pz.zone_id
  WHERE a.id = @address_id;

  DECLARE @grand_total DECIMAL(19,0) = @total_after_coupon + @shipping_fee;

  -- 6) Tạo đơn
  INSERT dbo.Orders(user_id, address_id, [status], payment_method, payment_status, coupon_code, shipping_fee)
  VALUES (@user_id, @address_id, N'PENDING', NULL, N'UNPAID', @coupon_code, @shipping_fee);

  DECLARE @order_id INT = SCOPE_IDENTITY();

  -- 7) Trừ tồn an toàn
  UPDATE pv
     SET pv.stock_qty = pv.stock_qty - ti.qty
  FROM dbo.ProductVariant pv WITH (UPDLOCK, ROWLOCK)
  JOIN @temp_items ti ON ti.variant_id = pv.id
  WHERE pv.stock_qty >= ti.qty AND pv.is_active = 1;

  IF @@ROWCOUNT <> (SELECT COUNT(*) FROM @temp_items)
  BEGIN
    RAISERROR (N'Tồn kho không đủ hoặc SKU không hợp lệ/không còn active.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- 8) Ghi dòng hàng (snapshot unit_price = sale_price hiện tại)
  INSERT dbo.OrderItems(order_id, variant_id, qty, unit_price)
  SELECT @order_id, ti.variant_id, ti.qty, ti.sale_price
  FROM @temp_items ti;

  -- 9) Log trạng thái ban đầu
  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES (@order_id, N'PENDING', N'Tạo đơn hàng', NULL);

  -- 8) Cập nhật payment_method và payment_status
  UPDATE o
     SET payment_method = @payment_method,
         payment_status = N'UNPAID'
  FROM dbo.Orders o
  WHERE o.id = @order_id;

  COMMIT TRAN;

  SELECT @order_id AS order_id,
         @subtotal AS subtotal_snapshot,
         @discount_amount AS discount_amount,
         @total_after_coupon AS total_after_coupon,
         @shipping_fee AS shipping_fee,
         @grand_total AS grand_total;
END
GO


-- Stored Procedure: Xử lý webhook thanh toán (VNPAY/MoMo)
CREATE OR ALTER PROCEDURE dbo.sp_HandlePaymentWebhook
  @order_id INT,
  @payment_method NVARCHAR(20), -- 'VNPAY' | 'MOMO'
  @is_success BIT,
  @gateway_txn_code NVARCHAR(100) = NULL -- nếu muốn log vào OrderStatusHistory
AS
BEGIN
  SET NOCOUNT ON;

  IF @is_success = 1
  BEGIN
    -- Cập nhật payment_status cho đơn PENDING hoặc CONFIRMED
  UPDATE dbo.Orders 
        SET payment_method  = @payment_method,
            payment_status  = N'PAID',
            updated_at      = GETDATE()
      WHERE id = @order_id AND status IN (N'PENDING', N'CONFIRMED');

    -- Log thành công
    INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
    VALUES(@order_id, N'PENDING', CONCAT(N'Payment PAID via ', @payment_method, N' (webhook). Txn=', ISNULL(@gateway_txn_code,N'')));

    -- Cảnh báo nếu đơn không match điều kiện (đã CANCELLED hoặc DELIVERED)
  IF @@ROWCOUNT = 0
  BEGIN
      DECLARE @current_status NVARCHAR(20);
      SELECT @current_status = status FROM dbo.Orders WHERE id = @order_id;
      
      INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
      VALUES(@order_id, @current_status, CONCAT(N'WARNING: Payment webhook received but order status is ', @current_status, N'. Payment not updated.'));
    END
  END
  ELSE
  BEGIN
    -- thất bại: giữ UNPAID, có thể ghi log
    INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
    VALUES(@order_id, N'PENDING', CONCAT(@payment_method, N' payment failed.'));
  END
END
GO


-- Stored Procedure: Hủy đơn hàng và bù lại tồn kho
CREATE OR ALTER PROCEDURE dbo.sp_CancelOrder
  @order_id INT,
  @actor_user_id INT = NULL,           -- người thực hiện (manager), có thể NULL = hệ thống
  @reason NVARCHAR(500) = NULL
AS
BEGIN
  SET NOCOUNT ON;
  SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  BEGIN TRAN;

  -- Chỉ hủy khi PENDING (đảm bảo chưa giao/không double restock)
  IF NOT EXISTS (SELECT 1 FROM dbo.Orders WHERE id = @order_id AND [status] = N'PENDING')
  BEGIN
    RAISERROR(N'Chỉ hủy được đơn PENDING', 16, 1);
    ROLLBACK TRAN; RETURN; 
  END

  DECLARE @pm NVARCHAR(20), @ps NVARCHAR(20);
  SELECT @pm = payment_method, @ps = payment_status FROM dbo.Orders WHERE id = @order_id;

  -- Nếu online đã PAID -> thực hiện refund ngay rồi set REFUNDED
  IF @pm IN (N'VNPAY', N'MOMO') AND @ps = N'PAID'
  BEGIN
    -- TODO: gọi API refund đồng bộ tại đây (qua service layer). Ở DB chỉ log:
    INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
    VALUES(@order_id, N'PENDING', CONCAT(@pm, N' refund executed on cancel.'));

    UPDATE dbo.Orders
       SET payment_status = N'REFUNDED',
           updated_at = GETDATE()
     WHERE id = @order_id;
  END

  -- Cộng tồn lại các variant
  UPDATE v
     SET v.stock_qty = v.stock_qty + oi.qty
  FROM dbo.OrderItems oi
  JOIN dbo.ProductVariant v ON v.id = oi.variant_id
  WHERE oi.order_id = @order_id;

  -- Hủy đơn
  UPDATE dbo.Orders
     SET [status] = N'CANCELLED',
         updated_at = GETDATE()
   WHERE id = @order_id;

  INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
  VALUES(@order_id, N'CANCELLED', ISNULL(@reason,N'Cancel by user/manager'));

  COMMIT TRAN;
END
GO






/* ==========================================
   1) Xác nhận đơn: PENDING -> CONFIRMED
   ========================================== */
-- Stored Procedure: Xác nhận đơn hàng (PENDING → CONFIRMED)
CREATE OR ALTER PROCEDURE dbo.sp_ConfirmOrder
  @order_id INT,
  @actor_user_id INT = NULL,           -- ai thực hiện (manager), có thể NULL = hệ thống
  @note NVARCHAR(500) = NULL
AS
BEGIN
  SET NOCOUNT ON;
  SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  BEGIN TRAN;

  -- Chỉ cho phép khi đơn đang PENDING
  UPDATE dbo.Orders
    SET [status] = N'CONFIRMED'
  WHERE id = @order_id AND [status] = N'PENDING';

  IF @@ROWCOUNT = 0
  BEGIN
    RAISERROR (N'Chỉ xác nhận đơn ở trạng thái PENDING.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES (@order_id, N'CONFIRMED', ISNULL(@note, N'Xác nhận đơn'), @actor_user_id);

  COMMIT TRAN;
END
GO


/* ==========================================================
   2) Xuất kho/đi giao: CONFIRMED -> DISPATCHED
      - Có thể gán đội giao ngay lúc dispatch.
      - Nếu chưa có OrderDelivery thì bắt buộc truyền @delivery_team_id.
   ========================================================== */
-- Stored Procedure: Đánh dấu đơn đã xuất kho/đi giao (CONFIRMED → DISPATCHED)
CREATE OR ALTER PROCEDURE dbo.sp_MarkDispatched
  @order_id INT,
  @delivery_team_id INT = NULL,        -- nếu lần đầu tạo OrderDelivery thì BẮT BUỘC != NULL
  @actor_user_id INT = NULL,
  @note NVARCHAR(500) = NULL
AS
BEGIN
  SET NOCOUNT ON;
  SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  BEGIN TRAN;

  -- Chỉ cho phép khi đơn đang CONFIRMED
  UPDATE dbo.Orders
    SET [status] = N'DISPATCHED'
  WHERE id = @order_id AND [status] = N'CONFIRMED';

  IF @@ROWCOUNT = 0
  BEGIN
    RAISERROR (N'Chỉ đánh dấu DISPATCHED khi đơn đang CONFIRMED.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- Kiểm tra OrderDelivery hiện có?
  DECLARE @od_id INT, @existing_team_id INT;

  SELECT @od_id = od.id,
         @existing_team_id = od.delivery_team_id
  FROM dbo.OrderDelivery od WITH (UPDLOCK, ROWLOCK)
  WHERE od.order_id = @order_id;

  IF @od_id IS NULL
  BEGIN
    -- Chưa có OrderDelivery => cần đội giao
    IF @delivery_team_id IS NULL
    BEGIN
      RAISERROR (N'Chưa có bản ghi giao nhận. Cần cung cấp delivery_team_id.',16,1);
      ROLLBACK TRAN; RETURN;
    END

    INSERT dbo.OrderDelivery(order_id, delivery_team_id, [status], note)
    VALUES (@order_id, @delivery_team_id, N'IN_TRANSIT', ISNULL(@note, N'Đi giao'));

    SET @od_id = SCOPE_IDENTITY();

    INSERT dbo.DeliveryHistory(order_delivery_id, [status], note)
    VALUES (@od_id, N'IN_TRANSIT', ISNULL(@note, N'Đi giao'));
  END
  ELSE
  BEGIN
    -- Đã có OrderDelivery: KHÔNG cho đổi đội nữa
    IF @delivery_team_id IS NOT NULL AND @delivery_team_id <> @existing_team_id
    BEGIN
      RAISERROR (N'Đơn đã gán đội giao, không được đổi đội.',16,1);
      ROLLBACK TRAN; RETURN;
    END

    UPDATE dbo.OrderDelivery
      SET [status] = N'IN_TRANSIT',
          note = ISNULL(@note, note)
    WHERE id = @od_id;

    INSERT dbo.DeliveryHistory(order_delivery_id, [status], note)
    VALUES (@od_id, N'IN_TRANSIT', ISNULL(@note, N'Đi giao'));
  END


  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES (@order_id, N'DISPATCHED', ISNULL(@note, N'Xuất kho/đi giao'), @actor_user_id);

  COMMIT TRAN;
END
GO


/* ==========================================================
   3) Giao thành công: DISPATCHED -> DELIVERED
      - Cập nhật OrderDelivery = DONE, lưu hình bàn giao (nếu có)
      - Log DeliveryHistory & OrderStatusHistory
   ========================================================== */
-- Stored Procedure: Đánh dấu đơn đã giao thành công (DISPATCHED → DELIVERED)
CREATE OR ALTER PROCEDURE dbo.sp_MarkDelivered
  @order_id INT,
  @proof_image_url NVARCHAR(255) = NULL,
  @actor_user_id INT = NULL,
  @note NVARCHAR(500) = NULL
AS
BEGIN
  SET NOCOUNT ON;
  SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  BEGIN TRAN;

  -- Chỉ cho phép khi đơn đang DISPATCHED
  UPDATE dbo.Orders
    SET [status] = N'DELIVERED'
  WHERE id = @order_id AND [status] = N'DISPATCHED';

  IF @@ROWCOUNT = 0
  BEGIN
    RAISERROR (N'Chỉ đánh dấu DELIVERED khi đơn đang DISPATCHED.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- Lấy OrderDelivery
  DECLARE @od_id INT;
  SELECT @od_id = od.id
  FROM dbo.OrderDelivery od WITH (UPDLOCK, ROWLOCK)
  WHERE od.order_id = @order_id;

  IF @od_id IS NULL
  BEGIN
    RAISERROR (N'Không tìm thấy thông tin giao nhận cho đơn này.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- Cập nhật OrderDelivery -> DONE
  UPDATE dbo.OrderDelivery
    SET [status] = N'DONE',
        proof_image_url = COALESCE(@proof_image_url, proof_image_url),
        note = ISNULL(@note, note)
  WHERE id = @od_id;

  INSERT dbo.DeliveryHistory(order_delivery_id, [status], note, photo_url)
  VALUES (@od_id, N'DONE', ISNULL(@note, N'Giao thành công'), @proof_image_url);

  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES (@order_id, N'DELIVERED', ISNULL(@note, N'Giao thành công'), @actor_user_id);

  -- Xử lý COD: nếu payment_method = COD và payment_status = UNPAID thì set PAID
  IF EXISTS (SELECT 1 FROM dbo.Orders WHERE id = @order_id AND payment_method = N'COD' AND payment_status = N'UNPAID')
  BEGIN
    UPDATE dbo.Orders
       SET payment_status = N'PAID',
           updated_at = GETDATE()
     WHERE id = @order_id;
    INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
    VALUES(@order_id, N'DELIVERED', N'COD collected, set payment_status = PAID.');
  END

  COMMIT TRAN;
END
GO


-- Stored Procedure: Auto-cancel đơn online chưa thanh toán (job)
CREATE OR ALTER PROCEDURE dbo.sp_AutoCancelUnpaidOnline
  @expire_minutes INT = 15
AS
BEGIN
  SET NOCOUNT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  ;WITH cte AS (
    SELECT id
    FROM dbo.Orders
    WHERE [status] = N'PENDING'
      AND payment_method IN (N'VNPAY', N'MOMO')
      AND payment_status = N'UNPAID'
      AND created_at < DATEADD(MINUTE, -@expire_minutes, GETDATE())
  )
  SELECT * INTO #to_cancel FROM cte;

  DECLARE @oid INT;
  DECLARE c CURSOR LOCAL FAST_FORWARD FOR SELECT id FROM #to_cancel;
  OPEN c; FETCH NEXT FROM c INTO @oid;
  WHILE @@FETCH_STATUS = 0
  BEGIN
    -- Re-validate điều kiện trước khi hủy để tránh race condition
    IF EXISTS (
      SELECT 1 FROM dbo.Orders 
      WHERE id = @oid 
        AND [status] = N'PENDING'
        AND payment_method IN (N'VNPAY', N'MOMO')
        AND payment_status = N'UNPAID'
        AND created_at < DATEADD(MINUTE, -@expire_minutes, GETDATE())
    )
    BEGIN
      -- hoàn kho
      UPDATE v
         SET v.stock_qty = v.stock_qty + oi.qty
      FROM dbo.OrderItems oi
      JOIN dbo.ProductVariant v ON v.id = oi.variant_id
      WHERE oi.order_id = @oid;

      -- hủy
      UPDATE dbo.Orders
         SET [status] = N'CANCELLED',
             updated_at = GETDATE()
       WHERE id = @oid;

      INSERT INTO dbo.OrderStatusHistory(order_id, [status], note)
      VALUES(@oid, N'CANCELLED', N'Auto-cancel unpaid online after timeout.');
    END

    FETCH NEXT FROM c INTO @oid;
  END
  CLOSE c; DEALLOCATE c;

  DROP TABLE #to_cancel;
END
GO


-- =========================================================
-- CÁC STORED PROCEDURES CHO QUY TRÌNH TRẢ HÀNG
-- =========================================================

-- Stored Procedure: Khách hàng gửi yêu cầu trả hàng
CREATE OR ALTER PROCEDURE dbo.sp_RequestReturn
  @order_id INT,
  @customer_id INT,                 -- bảo vệ: chỉ chủ đơn mới request
  @reason NVARCHAR(500) = NULL
AS
BEGIN
  SET NOCOUNT ON; SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
  BEGIN TRAN;

  -- 1) Đơn phải thuộc về customer, đã DELIVERED, chưa có kết quả trả hàng
  IF NOT EXISTS (
    SELECT 1
    FROM dbo.Orders o
    WHERE o.id = @order_id
      AND o.user_id = @customer_id
      AND o.[status] = N'DELIVERED'
      AND (o.return_status IS NULL OR o.return_status NOT IN (N'REJECTED', N'PROCESSED'))
  )
  BEGIN
    RAISERROR(N'Đơn không hợp lệ để yêu cầu trả.',16,1); ROLLBACK TRAN; RETURN;
  END

  -- 2) Không cho gửi lại nếu đang REQUESTED/APPROVED
  IF EXISTS (
    SELECT 1 FROM dbo.Orders WHERE id=@order_id AND return_status IN (N'REQUESTED', N'APPROVED')
  )
  BEGIN
    RAISERROR(N'Đơn đã gửi yêu cầu/được duyệt trước đó.',16,1); ROLLBACK TRAN; RETURN;
  END

  -- 3) Kiểm tra thời hạn 30 ngày sau giao hàng - theo chính sách
  IF EXISTS (
    SELECT 1
    FROM dbo.Orders o
    OUTER APPLY (
       SELECT MAX(changed_at) AS delivered_at
       FROM dbo.OrderStatusHistory
       WHERE order_id = o.id AND [status] = N'DELIVERED'
    ) h
    WHERE o.id = @order_id
      AND DATEDIFF(DAY, h.delivered_at, GETDATE()) > 30
  )
  BEGIN RAISERROR(N'Quá thời hạn yêu cầu trả (30 ngày sau giao).',16,1); ROLLBACK; RETURN; END

  -- 4) Ghi trạng thái + lý do
  UPDATE dbo.Orders
    SET return_status = N'REQUESTED',
        return_reason = LEFT(ISNULL(@reason,N'Không có lý do'), 500),
        return_note   = NULL  -- để trống cho manager ghi chú sau
  WHERE id = @order_id;

  -- 5) Log (giữ nguyên status DELIVERED, chỉ log sự kiện)
  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES(@order_id, N'DELIVERED', N'YÊU CẦU TRẢ HÀNG: ' + ISNULL(@reason,N''), @customer_id);

  COMMIT TRAN;
END
GO

-- Stored Procedure: Duyệt trả hàng, đẩy trạng thái giao nhận sang RETURN_PICKUP
CREATE OR ALTER PROCEDURE dbo.sp_ApproveReturn
  @order_id INT,
  @manager_id INT,
  @note NVARCHAR(500) = NULL,
  @delivery_team_id INT = NULL   -- nếu đơn chưa có OrderDelivery thì cần truyền
AS
BEGIN
  SET NOCOUNT ON; SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
  BEGIN TRAN;

  UPDATE dbo.Orders
    SET return_status = N'APPROVED',
        return_note   = CONCAT(ISNULL(return_note,N''), 
                               CASE WHEN return_note IS NULL THEN N'' ELSE N' | ' END,
                               N'APPROVED: ', ISNULL(@note,N'')) 
  WHERE id=@order_id AND return_status=N'REQUESTED' AND [status]=N'DELIVERED';

  IF @@ROWCOUNT=0
  BEGIN RAISERROR(N'Chỉ phê duyệt đơn REQUESTED và đã DELIVERED.',16,1); ROLLBACK TRAN; RETURN; END;

  DECLARE @od_id INT, @team_id INT;
  SELECT @od_id=id,@team_id=delivery_team_id FROM dbo.OrderDelivery WITH(UPDLOCK,ROWLOCK) WHERE order_id=@order_id;

  IF @od_id IS NULL
  BEGIN
    IF @delivery_team_id IS NULL
    BEGIN RAISERROR(N'Chưa có OrderDelivery, cần delivery_team_id.',16,1); ROLLBACK TRAN; RETURN; END;

    INSERT dbo.OrderDelivery(order_id, delivery_team_id, [status], note)
    VALUES(@order_id, @delivery_team_id, N'RETURN_PICKUP', N'Phê duyệt trả hàng - thu hồi');
    SET @od_id = SCOPE_IDENTITY();

    INSERT dbo.DeliveryHistory(order_delivery_id, [status], note)
    VALUES(@od_id, N'RETURN_PICKUP', N'Phê duyệt - thu hồi');
  END
  ELSE
  BEGIN
    -- KHÔNG cho đổi đội giao ở giai đoạn thu hồi
    IF @delivery_team_id IS NOT NULL AND @delivery_team_id <> @team_id
    BEGIN
      RAISERROR(N'Đơn đã gán đội thu hồi, không được đổi đội.',16,1);
    ROLLBACK TRAN; RETURN;
  END

    UPDATE dbo.OrderDelivery SET [status]=N'RETURN_PICKUP', note=ISNULL(@note, note) WHERE id=@od_id AND [status]<>N'RETURN_PICKUP';
    IF @@ROWCOUNT>0
      INSERT dbo.DeliveryHistory(order_delivery_id, [status], note)
      VALUES(@od_id, N'RETURN_PICKUP', ISNULL(@note,N'Phê duyệt - thu hồi'));
  END

  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES(@order_id, N'DELIVERED', N'RETURN_STATUS=APPROVED: '+ISNULL(@note,N''), @manager_id);

  COMMIT TRAN;
END
GO

-- Stored Procedure: Xử lý trả hàng và hoàn tiền tại chỗ
CREATE OR ALTER PROCEDURE dbo.sp_ReturnOrder
  @order_id INT,
  @actor_user_id INT = NULL,
  @reason NVARCHAR(500) = NULL,
  @refund_method NVARCHAR(20) = N'COD_CASH'  -- COD_CASH | BANK_TRANSFER | VNPAY | MOMO
AS
BEGIN
  SET NOCOUNT ON; SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
  BEGIN TRAN;

  -- 0) Yêu cầu đơn đang DELIVERED + APPROVED
  IF NOT EXISTS (
    SELECT 1 FROM dbo.Orders 
    WHERE id=@order_id AND [status]=N'DELIVERED' AND return_status=N'APPROVED'
  )
  BEGIN RAISERROR(N'Chỉ xử lý khi đơn APPROVED và đang DELIVERED.',16,1); ROLLBACK TRAN; RETURN; END;

  -- 1) Phải có OrderDelivery ở trạng thái RETURN_PICKUP (đã tới thu hồi)
  DECLARE @od_id INT, @od_status NVARCHAR(20);
  SELECT @od_id=id, @od_status=[status] FROM dbo.OrderDelivery WITH (UPDLOCK, ROWLOCK) WHERE order_id=@order_id;
  IF @od_id IS NULL OR @od_status <> N'RETURN_PICKUP'
  BEGIN RAISERROR(N'Chỉ xử lý trả khi giao nhận đang RETURN_PICKUP (đã tới thu hồi).',16,1); ROLLBACK TRAN; RETURN; END;

  -- 2) Cập nhật đơn: trả hàng & HOÀN TIỀN NGAY
  UPDATE dbo.Orders
     SET [status]       = N'RETURNED',
         return_status  = N'PROCESSED',
         return_note    = CONCAT(ISNULL(return_note,N''), 
                                 CASE WHEN return_note IS NULL THEN N'' ELSE N' | ' END,
                                 N'PROCESSED: ', ISNULL(@reason,N''), N' | REFUND_METHOD=', @refund_method, N' (DONE)'),
         payment_status = N'REFUNDED'
   WHERE id=@order_id;

  -- 3) Bù tồn
  UPDATE pv
     SET pv.stock_qty = pv.stock_qty + oi.qty
  FROM dbo.ProductVariant pv WITH(UPDLOCK,ROWLOCK)
  JOIN dbo.OrderItems oi ON oi.variant_id = pv.id
  WHERE oi.order_id = @order_id;

  -- 4) Đóng giao nhận
  UPDATE dbo.OrderDelivery SET [status]=N'DONE', note=ISNULL(@reason, note) WHERE id=@od_id;
  INSERT dbo.DeliveryHistory(order_delivery_id, [status], note)
  VALUES(@od_id, N'DONE', ISNULL(@reason, N'Hoàn tất trả & thu hồi'));

  -- 5) Log
  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES(@order_id, N'RETURNED', ISNULL(@reason,N'Xử lý trả hàng & bù tồn'), @actor_user_id);

  -- 6) Thông báo cho khách (chỉ một dòng, không còn nhánh pending)
  INSERT dbo.UserNotification(user_id, title, message, is_read)
  SELECT o.user_id,
         N'Đơn hàng đã được trả',
         N'Đơn hàng #' + CAST(o.id AS NVARCHAR(20)) + N' đã trả hàng & hoàn tiền xong (' + @refund_method + N').',
         0
  FROM dbo.Orders o
  WHERE o.id = @order_id;

  COMMIT TRAN;
END
GO

-- Stored Procedure: Từ chối yêu cầu trả hàng
CREATE OR ALTER PROCEDURE dbo.sp_RejectReturn
  @order_id INT,
  @manager_id INT,
  @note NVARCHAR(500) = NULL
AS
BEGIN
  SET NOCOUNT ON; SET XACT_ABORT ON;
  SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
  BEGIN TRAN;

  UPDATE dbo.Orders
    SET return_status = N'REJECTED',
        return_note   = CONCAT(ISNULL(return_note,N''), 
                               CASE WHEN return_note IS NULL THEN N'' ELSE N' | ' END,
                               N'REJECTED: ', ISNULL(@note,N''))
  WHERE id=@order_id AND return_status=N'REQUESTED';

  IF @@ROWCOUNT=0
  BEGIN RAISERROR(N'Chỉ từ chối đơn ở trạng thái REQUESTED.',16,1); ROLLBACK TRAN; RETURN; END;

  INSERT dbo.OrderStatusHistory(order_id, [status], note, changed_by)
  VALUES(@order_id, N'DELIVERED', N'RETURN_STATUS=REJECTED: '+ISNULL(@note,N''), @manager_id);

  COMMIT TRAN;
END
GO











-- =========================================================
-- PHẦN 5: CÁC TRIGGERS ĐỂ TỰ ĐỘNG HÓA VÀ VALIDATE DỮ LIỆU
-- =========================================================

-- Trigger: Validate DeliveryTeam.user_id phải có role DELIVERY và active
GO
CREATE TRIGGER dbo.TR_DeliveryTeam_ValidateUserRole
ON dbo.DeliveryTeam
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Users u ON u.id = i.user_id
    JOIN dbo.Roles r ON r.id = u.role_id
    WHERE u.is_active = 0 OR r.name <> N'DELIVERY'
  )
  BEGIN
    RAISERROR(N'user gán cho DeliveryTeam phải có role DELIVERY và đang active.',16,1);
    ROLLBACK TRAN; RETURN;
  END
END
GO

-- Trigger: Validate OrderDelivery zone - chỉ gán đội đúng miền của địa chỉ giao
GO
CREATE TRIGGER dbo.TR_OrderDelivery_ValidateZone
ON dbo.OrderDelivery
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- 1) Bắt buộc có địa chỉ giao
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Orders o ON o.id = i.order_id
    WHERE o.address_id IS NULL
  )
  BEGIN
    RAISERROR (N'Đơn hàng phải có địa chỉ giao trước khi gán đội.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- 2) Tỉnh/thành phải được map vào zone
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Orders o ON o.id = i.order_id
    JOIN dbo.Address a ON a.id = o.address_id
    LEFT JOIN dbo.ProvinceZone pz ON pz.province_name = a.city
    WHERE pz.zone_id IS NULL
  )
  BEGIN
    RAISERROR (N'Tỉnh/thành chưa được map vào miền giao hàng.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- 3) Đội giao phải thuộc đúng miền
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Orders o ON o.id = i.order_id
    JOIN dbo.Address a ON a.id = o.address_id
    JOIN dbo.ProvinceZone pz ON pz.province_name = a.city
    LEFT JOIN dbo.DeliveryTeamZone dtz
      ON dtz.delivery_team_id = i.delivery_team_id
     AND dtz.zone_id = pz.zone_id
    WHERE dtz.id IS NULL
  )
  BEGIN
    RAISERROR (N'Đội giao không thuộc miền (zone) của địa chỉ giao hàng.',16,1);
    ROLLBACK TRAN; RETURN;
  END
END
GO

-- Trigger: Tự động cập nhật updated_at khi Orders được update
GO
CREATE OR ALTER TRIGGER dbo.TR_Orders_TouchUpdatedAt
ON dbo.Orders AFTER UPDATE AS
BEGIN
  SET NOCOUNT ON;
  IF UPDATE(updated_at) RETURN; -- tránh lặp
  UPDATE o SET updated_at = GETDATE()
  FROM dbo.Orders o JOIN inserted i ON o.id = i.id;
END;
GO

-- Trigger: Tự động cập nhật updated_at khi Cart được update
GO
CREATE OR ALTER TRIGGER dbo.TR_Cart_TouchUpdatedAt
ON dbo.Cart AFTER UPDATE AS
BEGIN
  SET NOCOUNT ON;
  IF UPDATE(updated_at) RETURN; -- tránh lặp
  UPDATE c SET updated_at = GETDATE()
  FROM dbo.Cart c JOIN inserted i ON c.id = i.id;
END;
GO

-- Trigger: Tự động cập nhật updated_at khi OrderDelivery được update
GO
CREATE OR ALTER TRIGGER dbo.TR_OrderDelivery_TouchUpdatedAt
ON dbo.OrderDelivery AFTER UPDATE AS
BEGIN
  SET NOCOUNT ON;
  IF UPDATE(updated_at) RETURN; -- tránh lặp
  UPDATE d SET updated_at = GETDATE()
  FROM dbo.OrderDelivery d JOIN inserted i ON d.id = i.id;
END;
GO


-- Trigger: Tự động làm tròn giá ProductVariant về bậc nghìn
GO
CREATE OR ALTER TRIGGER dbo.TR_ProductVariant_AutoRoundPrice
ON dbo.ProductVariant
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- Tránh vòng lặp do UPDATE trong trigger
  IF TRIGGER_NESTLEVEL() > 1 RETURN;

  -- Chỉ làm tròn những dòng vừa tác động và chưa bậc nghìn
  UPDATE pv
    SET pv.price = ROUND(pv.price, -3) -- Làm tròn bậc nghìn
  FROM dbo.ProductVariant pv
  JOIN inserted i ON i.id = pv.id
  WHERE pv.price <> ROUND(pv.price, -3);
END
GO

-- Trigger: Tự động làm tròn phí ship ShippingFee về bậc nghìn
GO
CREATE OR ALTER TRIGGER dbo.TR_ShippingFee_AutoRoundBaseFee
ON dbo.ShippingFee
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- Tránh vòng lặp do UPDATE trong trigger
  IF TRIGGER_NESTLEVEL() > 1 RETURN;

  UPDATE sf
    SET sf.base_fee = ROUND(sf.base_fee, -3) -- Làm tròn bậc nghìn
  FROM dbo.ShippingFee sf
  JOIN inserted i ON i.id = sf.id
  WHERE sf.base_fee <> ROUND(sf.base_fee, -3);
END
GO

-- Trigger: Validate Product phải thuộc Category đang hoạt động và là danh mục lá
GO
CREATE OR ALTER TRIGGER dbo.TR_Product_CategoryCollection_Validate
ON dbo.Product
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- category_id phải là CATEGORY & còn active
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Category c ON c.id = i.category_id
    WHERE c.[type] <> N'CATEGORY' OR c.is_active = 0
  )
  BEGIN
    RAISERROR (N'category_id phải là Category đang hoạt động.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- category_id phải là lá
  IF EXISTS (
    SELECT 1
    FROM inserted i
    WHERE EXISTS (SELECT 1 FROM dbo.Category ch WHERE ch.parent_id = i.category_id AND ch.is_active = 1)
  )
  BEGIN
    RAISERROR (N'category_id phải là danh mục lá.',16,1);
    ROLLBACK TRAN; RETURN;
  END

  -- collection_id (nếu có) phải là COLLECTION & còn active
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Category c ON c.id = i.collection_id
    WHERE i.collection_id IS NOT NULL
      AND (c.[type] <> N'COLLECTION' OR c.is_active = 0)
  )
  BEGIN
    RAISERROR (N'collection_id phải là Collection đang hoạt động.',16,1);
    ROLLBACK TRAN; RETURN;
  END
END
GO

-- Trigger: Validate ProductVariant phải thuộc Product và Color đang hoạt động
GO
CREATE OR ALTER TRIGGER dbo.TR_PV_ValidateActiveRefs
ON dbo.ProductVariant
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  IF EXISTS (
    SELECT 1
    FROM inserted i
    JOIN dbo.Product p ON p.id = i.product_id
    JOIN dbo.Color   c ON c.id = i.color_id
    WHERE p.is_active = 0 OR c.is_active = 0
  )
  BEGIN
    RAISERROR (N'Product hoặc Color đã vô hiệu hóa.',16,1);
    ROLLBACK TRAN; RETURN;
  END
END;
GO

-- Trigger: Tự động cập nhật avg_rating và total_reviews khi có review mới/cập nhật/xóa
GO
CREATE OR ALTER TRIGGER dbo.TR_Review_UpdateProductRating
ON dbo.Review
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Lấy danh sách product_id bị ảnh hưởng
  DECLARE @affected_products TABLE (product_id INT);
  
  INSERT INTO @affected_products (product_id)
  SELECT DISTINCT product_id FROM inserted
  UNION
  SELECT DISTINCT product_id FROM deleted;
  
  -- Cập nhật rating cho từng sản phẩm
  UPDATE p
  SET 
    avg_rating   = CAST(ROUND(ISNULL(rs.avg_rating, 0), 1) AS DECIMAL(2,1)),
    total_reviews = ISNULL(rs.total_count, 0)
  FROM dbo.Product p
  JOIN @affected_products ap ON ap.product_id = p.id
  LEFT JOIN (
    SELECT 
      product_id,
      AVG(CAST(rating AS DECIMAL(4,2))) AS avg_rating,
      COUNT(*) AS total_count
    FROM dbo.Review
    WHERE is_hidden = 0
    GROUP BY product_id
  ) AS rs ON rs.product_id = p.id;
END;
GO







-- ========================================================
-- Phần 6: Hệ thống thông báo (Notifications)
-- ========================================================

-- CHO CUSTOMER

-- Trigger: Tự động tạo thông báo khi đơn hàng đổi trạng thái
GO
CREATE OR ALTER TRIGGER dbo.TR_Orders_NotifyStatusChange
ON dbo.Orders
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Chỉ xử lý khi status thay đổi
  IF NOT UPDATE([status]) RETURN;
  
  -- Insert thông báo cho các đơn có status mới
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    i.user_id,
    CASE i.[status]
      WHEN N'CONFIRMED' THEN N'Đơn hàng đã được xác nhận'
      WHEN N'DISPATCHED' THEN N'Đơn hàng đã xuất kho'
      WHEN N'DELIVERED' THEN N'Đơn hàng đã giao thành công'
      WHEN N'CANCELLED' THEN N'Đơn hàng đã bị hủy'
      ELSE N'Cập nhật trạng thái đơn hàng'
    END,
    CASE i.[status]
      WHEN N'CONFIRMED' THEN N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã được xác nhận và đang chuẩn bị giao.'
      WHEN N'DISPATCHED' THEN N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã xuất kho và đang trên đường giao.'
      WHEN N'DELIVERED' THEN N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã giao thành công. Cảm ơn bạn!'
      WHEN N'CANCELLED' THEN N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã bị hủy.'
      ELSE N'Trạng thái đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã được cập nhật.'
    END,
    0 -- chưa đọc
  FROM inserted i
  JOIN deleted d ON i.id = d.id
  WHERE i.[status] <> d.[status]  -- chỉ khi status thực sự thay đổi
    AND i.[status] IN (N'CONFIRMED', N'DISPATCHED', N'DELIVERED', N'CANCELLED'); -- ĐÃ BỎ N'RETURNED'
END;
GO


-- Trigger: Thông báo khi tạo đơn hàng thành công cho customer
GO
CREATE OR ALTER TRIGGER dbo.TR_Orders_NotifyNewOrder
ON dbo.Orders
AFTER INSERT
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Thông báo cho customer khi tạo đơn thành công
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    i.user_id,
    N'Đặt hàng thành công',
    N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã được tạo thành công. Vui lòng chờ xác nhận.',
    0
  FROM inserted i
  WHERE i.[status] = N'PENDING';
END;
GO

-- Trigger: Thông báo khi sản phẩm yêu thích có hàng trở lại
GO
CREATE OR ALTER TRIGGER dbo.TR_ProductVariant_NotifyWishlistBackInStock
ON dbo.ProductVariant
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  IF NOT UPDATE(stock_qty) RETURN;

  -- Thông báo cho customer khi sản phẩm yêu thích có hàng trở lại
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    w.user_id,
    N'Sản phẩm có hàng trở lại',
    N'Sản phẩm "' + p.name + N'" trong danh sách yêu thích đã có hàng trở lại (' + CAST(i.stock_qty AS NVARCHAR(10)) + N' sản phẩm).',
    0
  FROM inserted i
  JOIN deleted d ON d.id = i.id
  JOIN dbo.Product p ON p.id = i.product_id
  JOIN dbo.Wishlist w ON w.product_id = p.id
  WHERE i.stock_qty > 0 
    AND d.stock_qty = 0  -- từ 0 lên > 0
    AND i.is_active = 1
    AND NOT EXISTS (
      SELECT 1 FROM dbo.UserNotification n
      WHERE n.user_id = w.user_id
        AND n.title = N'Sản phẩm có hàng trở lại'
        AND n.message LIKE N'%' + p.name + N'%'
        AND n.created_at >= DATEADD(HOUR,-24,GETDATE())  -- chống spam 24h
    );
END;
GO


-- Trigger: Thông báo khi manager trả lời review cho customer
GO
CREATE OR ALTER TRIGGER dbo.TR_Review_NotifyManagerResponse
ON dbo.Review
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  IF NOT UPDATE(manager_response) RETURN;

  -- Thông báo cho customer khi manager trả lời review
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    i.user_id,
    N'Có phản hồi từ cửa hàng',
    N'Cửa hàng đã phản hồi review của bạn cho sản phẩm "' + p.name + N'".',
    0
  FROM inserted i
  JOIN deleted d ON d.id = i.id
  JOIN dbo.Product p ON p.id = i.product_id
  WHERE i.manager_response IS NOT NULL 
    AND d.manager_response IS NULL;  -- từ NULL thành có response
END;
GO


-- Trigger: Thông báo cho customer khi yêu cầu trả hàng bị từ chối
GO
CREATE OR ALTER TRIGGER dbo.TR_Orders_NotifyCustomerReturnRejected
ON dbo.Orders
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Chỉ xử lý khi return_status thay đổi thành REJECTED
  IF NOT UPDATE(return_status) RETURN;
  
  -- Thông báo cho customer khi yêu cầu trả hàng bị từ chối
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    i.user_id,
    N'Yêu cầu trả hàng bị từ chối',
    N'Yêu cầu trả hàng cho đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã bị từ chối. Lý do: ' + ISNULL(i.return_note, N'Không đủ điều kiện trả hàng') + N'.',
    0
  FROM inserted i
  JOIN deleted d ON i.id = d.id
  WHERE i.return_status = N'REJECTED' 
    AND (d.return_status IS NULL OR d.return_status <> N'REJECTED');  -- chỉ khi mới REJECTED
END;
GO


-- Trigger: Thông báo cho customer khi yêu cầu trả hàng được duyệt
CREATE OR ALTER TRIGGER dbo.TR_Orders_NotifyCustomerReturnApproved
ON dbo.Orders
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  IF NOT UPDATE(return_status) RETURN;
  
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    i.user_id,
    N'Yêu cầu trả hàng được duyệt',
    N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' đã được duyệt trả. Đội giao sẽ liên hệ để thu hồi sản phẩm.',
    0
  FROM inserted i
  JOIN deleted d ON d.id = i.id
  WHERE i.return_status = N'APPROVED'
    AND (d.return_status IS NULL OR d.return_status <> N'APPROVED')
    AND i.[status] = N'DELIVERED';
END;
GO





-- CHO MANAGER

-- Trigger: Thông báo khi có đơn hàng mới cho manager
GO
CREATE OR ALTER TRIGGER dbo.TR_Orders_NotifyManagerNewOrder
ON dbo.Orders
AFTER INSERT
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Thông báo cho manager khi có đơn hàng mới
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    u.id,
    N'Có đơn hàng mới',
    N'Đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N' từ khách hàng "' + u_customer.full_name + N'" cần xử lý.',
    0
  FROM inserted i
  JOIN dbo.Users u_customer ON u_customer.id = i.user_id
  CROSS JOIN dbo.Users u
  WHERE i.[status] = N'PENDING'
    AND u.role_id IN (SELECT id FROM dbo.Roles WHERE name = N'MANAGER')
    AND u.is_active = 1;
END;
GO


-- Trigger: Thông báo khi có review mới cho sản phẩm
GO
CREATE OR ALTER TRIGGER dbo.TR_Review_NotifyNew
ON dbo.Review
AFTER INSERT
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Thông báo cho manager khi có review mới
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT u.id,
         N'Có đánh giá mới',
         N'Review #' + CAST(i.id AS NVARCHAR(20)) + N' cho "' + p.name + N'" (' + CAST(i.rating AS NVARCHAR(10)) + N'★).',
         0
  FROM inserted i
  JOIN dbo.Product p ON p.id = i.product_id
  CROSS JOIN dbo.Users u 
  WHERE u.role_id IN (SELECT id FROM dbo.Roles WHERE name = N'MANAGER')
    AND u.is_active=1;
END;
GO


-- Trigger: Thông báo khi tồn kho xuống thấp
GO
CREATE OR ALTER TRIGGER dbo.TR_ProductVariant_StockAlerts
ON dbo.ProductVariant
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  IF NOT UPDATE(stock_qty) RETURN;

  -- 1) Low stock: 1..5 và đang giảm
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT u.id, N'Tồn kho thấp',
         N'SKU ' + i.sku + N' (' + p.name + N') chỉ còn ' + CAST(i.stock_qty AS NVARCHAR(10)) + N' sp.',
         0
  FROM inserted i
  JOIN deleted d ON d.id = i.id
  JOIN dbo.Product p ON p.id = i.product_id
  CROSS JOIN dbo.Users u
  WHERE i.stock_qty <> d.stock_qty
    AND i.stock_qty BETWEEN 1 AND 5
    AND i.stock_qty < d.stock_qty
    AND i.is_active = 1
    AND u.role_id IN (SELECT id FROM dbo.Roles WHERE name IN (N'MANAGER', N'ADMIN'))
    AND u.is_active = 1
    AND NOT EXISTS (
      SELECT 1 FROM dbo.UserNotification n
      WHERE n.user_id = u.id
        AND n.title   = N'Tồn kho thấp'
        AND n.message LIKE N'%SKU ' + i.sku + N' %'
        AND n.created_at >= DATEADD(HOUR,-12,GETDATE())
    );

  -- 2) Out of stock: vừa về 0
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT u.id, N'Hết hàng',
         N'SKU ' + i.sku + N' (' + p.name + N') đã hết hàng.',
         0
  FROM inserted i
  JOIN deleted d ON d.id = i.id
  JOIN dbo.Product p ON p.id = i.product_id
  CROSS JOIN dbo.Users u
  WHERE i.stock_qty <> d.stock_qty
    AND i.stock_qty = 0
    AND d.stock_qty > 0
    AND i.is_active = 1
    AND u.role_id IN (SELECT id FROM dbo.Roles WHERE name IN (N'MANAGER', N'ADMIN'))
    AND u.is_active = 1
    AND NOT EXISTS (
      SELECT 1 FROM dbo.UserNotification n
      WHERE n.user_id = u.id
        AND n.title   = N'Hết hàng'
        AND n.message LIKE N'%SKU ' + i.sku + N' %'
        AND n.created_at >= DATEADD(HOUR,-12,GETDATE())
    );
END;
GO


-- Trigger: Thông báo tin nhắn mới từ khách hàng cho manager
GO
CREATE OR ALTER TRIGGER dbo.TR_Message_NotifyManager
ON dbo.Message
AFTER INSERT
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Thông báo cho manager khi có tin nhắn từ khách (chống spam: chỉ 1 thông báo/manager/cuộc hội thoại trong 5 phút)
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT u.id, N'Tin nhắn mới',
         N'Có tin nhắn mới trong cuộc hội thoại #' + CAST(i.conversation_id AS NVARCHAR(20)) + N'.',
         0
  FROM inserted i
  CROSS JOIN dbo.Users u 
  WHERE i.sender_type IN (N'CUSTOMER', N'GUEST')  -- chỉ tin nhắn từ khách
    AND u.role_id IN (SELECT id FROM dbo.Roles WHERE name = N'MANAGER')
    AND u.is_active = 1
    AND NOT EXISTS (
      SELECT 1 FROM dbo.UserNotification n
      WHERE n.user_id = u.id
        AND n.title   = N'Tin nhắn mới'
        AND n.message LIKE N'%cuộc hội thoại #' + CAST(i.conversation_id AS NVARCHAR(20)) + N'%.'
        AND n.created_at >= DATEADD(MINUTE,-5,GETDATE())
    );
END;
GO

-- Trigger: Thông báo khi customer yêu cầu trả hàng cho manager
GO
CREATE OR ALTER TRIGGER dbo.TR_Orders_NotifyReturnRequest
ON dbo.Orders
AFTER UPDATE
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Chỉ xử lý khi return_status thay đổi thành REQUESTED
  IF NOT UPDATE(return_status) RETURN;
  
  -- Thông báo cho manager khi customer yêu cầu trả hàng
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT 
    u.id,
    N'Yêu cầu trả hàng',
    N'Khách hàng "' + u_customer.full_name + N'" yêu cầu trả đơn hàng #' + CAST(i.id AS NVARCHAR(20)) + N'. Lý do: ' + ISNULL(i.return_reason, N'Không có lý do') + N'. Vui lòng xử lý.',
    0
  FROM inserted i
  JOIN deleted d ON i.id = d.id
  JOIN dbo.Users u_customer ON u_customer.id = i.user_id
  CROSS JOIN dbo.Users u
  WHERE i.return_status = N'REQUESTED' 
    AND (d.return_status IS NULL OR d.return_status <> N'REQUESTED')  -- chỉ khi mới REQUESTED
    AND u.role_id IN (SELECT id FROM dbo.Roles WHERE name = N'MANAGER')
    AND u.is_active = 1;
END;
GO





-- CHO ADMIN

-- Trigger: Thông báo khi có bài viết mới cho admin
GO
CREATE OR ALTER TRIGGER dbo.TR_Article_NotifyNew
ON dbo.Article
AFTER INSERT
AS
BEGIN
  SET NOCOUNT ON;
  
  -- Thông báo cho admin khi có bài viết mới
  INSERT INTO dbo.UserNotification(user_id, title, message, is_read)
  SELECT u.id,
         N'Có bài viết mới',
         N'Bài viết "' + i.title + N'" (' + i.article_type + N') đã được tạo.',
         0
  FROM inserted i
  CROSS JOIN dbo.Users u 
  WHERE u.role_id IN (SELECT id FROM dbo.Roles WHERE name = N'ADMIN')
    AND u.is_active = 1;
END;
GO





-- CHO DELIVERY

-- Trigger: Notify đội giao khi phân công/đổi trạng thái giao
GO
CREATE OR ALTER TRIGGER dbo.TR_OrderDelivery_NotifyDeliveryTeam  
ON dbo.OrderDelivery
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- A) Phân công mới (INSERT)
  INSERT dbo.UserNotification(user_id, title, message, is_read)
  SELECT dt.user_id,
         N'Phân công đơn giao',
         N'Bạn được phân công đơn #' + CAST(i.order_id AS NVARCHAR(20)) + N'. Trạng thái: ' + i.status + N'.',
         0
  FROM inserted i
  JOIN dbo.DeliveryTeam dt ON dt.id = i.delivery_team_id
  LEFT JOIN deleted d ON d.id = i.id
  WHERE d.id IS NULL  -- INSERT only
    AND NOT EXISTS (
      SELECT 1 FROM dbo.UserNotification n
      WHERE n.user_id = dt.user_id
        AND n.title   = N'Phân công đơn giao'
        AND n.message LIKE N'%đơn #' + CAST(i.order_id AS NVARCHAR(20)) + N'%'
        AND n.created_at >= DATEADD(MINUTE,-10,GETDATE())
    );

  -- B) RETURN_PICKUP (THÊM dedupe 10')
  INSERT dbo.UserNotification(user_id, title, message, is_read)
  SELECT dt.user_id,
         N'Yêu cầu thu hồi',
         N'Đơn #' + CAST(i.order_id AS NVARCHAR(20)) + N' chuyển RETURN_PICKUP.',
         0
  FROM inserted i
  JOIN dbo.DeliveryTeam dt ON dt.id = i.delivery_team_id
  JOIN deleted d ON d.id = i.id
  WHERE i.status = N'RETURN_PICKUP' 
    AND d.status <> N'RETURN_PICKUP'
    AND NOT EXISTS (
      SELECT 1 FROM dbo.UserNotification n
      WHERE n.user_id = dt.user_id
        AND n.title   = N'Yêu cầu thu hồi'
        AND n.message LIKE N'%Đơn #' + CAST(i.order_id AS NVARCHAR(20)) + N'%'
        AND n.created_at >= DATEADD(MINUTE,-10,GETDATE())
    );

  -- C) DONE
  INSERT dbo.UserNotification(user_id, title, message, is_read)
  SELECT dt.user_id,
         N'Đơn giao hoàn tất',
         N'Đơn #' + CAST(i.order_id AS NVARCHAR(20)) + N' đã DONE.',
         0
  FROM inserted i
  JOIN dbo.DeliveryTeam dt ON dt.id = i.delivery_team_id
  JOIN deleted d ON d.id = i.id
  WHERE i.status = N'DONE' 
    AND d.status <> N'DONE';
END
GO