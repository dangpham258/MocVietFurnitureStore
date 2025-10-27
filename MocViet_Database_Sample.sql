USE MocViet;
GO

-- Kiểm tra và xóa data cũ nếu có
IF EXISTS (SELECT 1 FROM dbo.Roles WHERE name = N'ADMIN')
BEGIN
    PRINT N'⚠️ Data đã tồn tại. Đang xóa data cũ...';
    
    -- Xóa theo thứ tự dependency
    DELETE FROM dbo.DeliveryHistory;
    DELETE FROM dbo.OrderDelivery;
    DELETE FROM dbo.OrderStatusHistory;
    DELETE FROM dbo.OrderItems;
    DELETE FROM dbo.Orders;
    DELETE FROM dbo.CartItem;
    DELETE FROM dbo.Cart;
    DELETE FROM dbo.Wishlist;
    DELETE FROM dbo.Viewed;
    DELETE FROM dbo.Review;
    DELETE FROM dbo.UserNotification;
    DELETE FROM dbo.Message;
    DELETE FROM dbo.Conversation;
    DELETE FROM dbo.OTP;
    DELETE FROM dbo.Address;
    DELETE FROM dbo.Showroom;
    DELETE FROM dbo.SocialLink;
    DELETE FROM dbo.ArticleImage;
    DELETE FROM dbo.Article;
    DELETE FROM dbo.ProductImage;
    DELETE FROM dbo.ProductVariant;
    DELETE FROM dbo.Product;
    DELETE FROM dbo.DeliveryTeamZone;
    DELETE FROM dbo.DeliveryTeam;
    DELETE FROM dbo.ProvinceZone;
    DELETE FROM dbo.ShippingFee;
    DELETE FROM dbo.ShippingZone;
    DELETE FROM dbo.Coupon;
    DELETE FROM dbo.StaticPage;
    DELETE FROM dbo.Banner;
    DELETE FROM dbo.Color;
    DELETE FROM dbo.Category;
    DELETE FROM dbo.Users;
    DELETE FROM dbo.Roles;
    
    PRINT N'✅ Đã xóa data cũ.';
END

BEGIN TRANSACTION;
SET NOCOUNT ON;

------------------------------------------------------------
-- 0) ROLES
------------------------------------------------------------
INSERT INTO dbo.Roles(name, description) VALUES
 (N'ADMIN',   N'Quản trị hệ thống'),
 (N'MANAGER', N'Quản lý bán hàng'),
 (N'DELIVERY',N'Đội ngũ vận chuyển và lắp đặt'),
 (N'CUSTOMER',N'Khách hàng');

------------------------------------------------------------
-- 1) USERS (mật khẩu demo: hash với https://bcrypt-generator.com/ rounds = 12)
------------------------------------------------------------
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Nguyễn Admin',  N'admin',   N'admin@mocviet.vn',   N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',   N'Nam', N'0900000001' FROM dbo.Roles WHERE name=N'ADMIN';

INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Lê Quản lý',    N'manager', N'manager@mocviet.vn', N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC', N'Nam', N'0900000002' FROM dbo.Roles WHERE name=N'MANAGER';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Trần Quản lý 2',N'manager2',N'manager2@mocviet.vn',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nữ', N'0900000003' FROM dbo.Roles WHERE name=N'MANAGER';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Phạm Quản lý 3',N'manager3',N'manager3@mocviet.vn',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0900000004' FROM dbo.Roles WHERE name=N'MANAGER';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Hoàng Quản lý 4',N'manager4',N'manager4@mocviet.vn',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nữ',N'0900000005' FROM dbo.Roles WHERE name=N'MANAGER';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)

SELECT id, N'Phạm Giao nhận',N'delivery',N'delivery@mocviet.vn',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0900000006' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đội Giao Khu Vực Bắc',N'delivery_north',N'delivery_north@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911001100' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đội Giao Khu Vực Bắc 2',N'delivery_north2',N'delivery_north2@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911001200' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đội Giao Khu Vực Trung',N'delivery_central',N'delivery_central@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911002200' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đội Giao Khu Vực Nam',N'delivery_south',N'delivery_south@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911003300' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đội Giao Khu Vực Nam 2',N'delivery_south2',N'delivery_south2@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911003400' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)

SELECT id, N'Trần Khách A',  N'cust_a',  N'cust_a@mocviet.vn',  N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',       N'Nữ', N'0900000007' FROM dbo.Roles WHERE name=N'CUSTOMER';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đỗ Khách B',    N'cust_b',  N'cust_b@mocviet.vn',  N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',       N'Nam',N'0900000008' FROM dbo.Roles WHERE name=N'CUSTOMER';

------------------------------------------------------------
-- 2) CATEGORY (PARENTS + LEAVES theo đúng mô tả của bạn)
------------------------------------------------------------
-- Parents
INSERT INTO dbo.Category(parent_id, name, slug, [type], is_active) VALUES
 (NULL,N'Phòng khách',N'phong-khach',N'CATEGORY',1),
 (NULL,N'Phòng ăn',   N'phong-an',   N'CATEGORY',1),
 (NULL,N'Phòng ngủ',  N'phong-ngu',  N'CATEGORY',1),
 (NULL,N'Phòng làm việc',N'phong-lam-viec',N'CATEGORY',1);

-- Leaves
INSERT INTO dbo.Category(parent_id, name, slug, [type], is_active) VALUES
((SELECT id FROM dbo.Category WHERE slug=N'phong-ngu'),      N'Combo phòng ngủ',      N'combo-phong-ngu',            N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-ngu'),      N'Tủ quần áo',            N'tu-quan-ao',                 N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-ngu'),      N'Giường ngủ',            N'giuong-ngu',                 N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-ngu'),      N'Tủ đầu giường',         N'tu-dau-giuong',              N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-ngu'),      N'Bàn trang điểm',        N'ban-trang-diem',             N'CATEGORY',1),

((SELECT id FROM dbo.Category WHERE slug=N'phong-khach'),    N'Ghế sofa',              N'ghe-sofa',                   N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-khach'),    N'Bàn sofa - Bàn cafe - Bàn trà', N'ban-sofa-ban-cafe-ban-tra',  N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-khach'),    N'Tủ kệ tivi',            N'tu-ke-tivi',                 N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-khach'),    N'Tủ giày - Tủ trang trí',N'tu-giay-tu-trang-tri',       N'CATEGORY',1),

((SELECT id FROM dbo.Category WHERE slug=N'phong-an'),       N'Bàn ăn',                N'ban-an',                     N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-an'),       N'Ghế ăn',                N'ghe-an',                     N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-an'),       N'Bộ bàn ăn',             N'bo-ban-an',                  N'CATEGORY',1),

((SELECT id FROM dbo.Category WHERE slug=N'phong-lam-viec'), N'Bàn làm việc',          N'ban-lam-viec',               N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-lam-viec'), N'Ghế văn phòng',         N'ghe-van-phong',              N'CATEGORY',1),
((SELECT id FROM dbo.Category WHERE slug=N'phong-lam-viec'), N'Tủ và kệ',              N'tu-va-ke',                   N'CATEGORY',1);

-- Collections
INSERT INTO dbo.Category(parent_id, name, slug, [type], is_active) VALUES
 (NULL,N'PREMIUM', N'premium', N'COLLECTION',1),
 (NULL,N'SCARLET', N'scarlet', N'COLLECTION',1),
 (NULL,N'SERENA',  N'serena',  N'COLLECTION',1),
 (NULL,N'PLANK',   N'plank',   N'COLLECTION',1),
 (NULL,N'KLINE',   N'kline',   N'COLLECTION',1),
 (NULL,N'DALUMD',  N'dalumd',  N'COLLECTION',1),
 (NULL,N'HOBRO',   N'hobro',   N'COLLECTION',1),
 (NULL,N'VLINE',   N'vline',   N'COLLECTION',1),
 (NULL,N'VIENNA',  N'vienna',  N'COLLECTION',1),
 (NULL,N'KOSTER',  N'koster',  N'COLLECTION',1),
 (NULL,N'NARVIK',  N'narvik',  N'COLLECTION',1),
 (NULL,N'OSLO',    N'oslo',    N'COLLECTION',1),
 (NULL,N'MILAN',   N'milan',   N'COLLECTION',1),
 (NULL,N'FYN',     N'fyn',     N'COLLECTION',1);

------------------------------------------------------------
-- 3) COLORS
------------------------------------------------------------
INSERT INTO dbo.Color(name, slug, hex, is_active) VALUES
 (N'Nâu',   N'nau',   '#8B5A2B',1),
 (N'Xanh',  N'xanh',  '#3A7BD5',1),
 (N'Đen',   N'den',   '#000000',1),
 (N'Trắng', N'trang', '#FFFFFF',1),
 (N'Be',    N'be',    '#F5F5DC',1),
 (N'Xám',   N'xam',   '#808080',1);

------------------------------------------------------------
-- 4) BANNERS
------------------------------------------------------------
INSERT INTO dbo.Banner(title, image_url, link_url, is_active) VALUES
 (N'Home Hero',      N'/static/images/banners/00_home-hero.jpg', N'/', 1),
 (N'Hè siêu sale',   N'/static/images/banners/01_he-sieu-sale.jpg', N'/collections/premium', 1),
 (N'Black Friday',   N'/static/images/banners/02_black-friday.jpg', N'/bo-suu-tap', 1);

------------------------------------------------------------
-- 5) STATIC PAGES (content ngắn + css/ảnh)
------------------------------------------------------------
INSERT INTO dbo.StaticPage(slug, title, content, is_active)
VALUES
 (N'chinh-sach-bao-hanh', N'Chính sách bảo hành',
  N'<link rel="stylesheet" href="/static/css/pages/chinh-sach-bao-hanh.css" />' +
  N'<h2>Bảo hành Mộc Việt</h2><p>Chi tiết...</p>' +
  N'<img src="/static/images/pages/chinh-sach-bao-hanh/00_chinh-sach-bao-hanh.jpg" />', 1),
 (N'gioi-thieu', N'Giới thiệu Mộc Việt',
  N'<h2>Về chúng tôi</h2><p>Mộc Việt...</p>' +
  N'<img src="/static/images/pages/gioi-thieu/00_gioi-thieu.jpg" />', 1),
 (N'chinh-sach-ban-hang', N'Chính sách bán hàng',
  N'<h2>Chính sách bán hàng</h2><p>Quy định về việc mua bán sản phẩm...</p>', 1),
 (N'chinh-sach-doi-tra', N'Chính sách đổi trả',
  N'<h2>Chính sách đổi trả</h2><p>Quy định về việc đổi trả sản phẩm...</p>', 1),
 (N'chinh-sach-van-chuyen', N'Chính sách vận chuyển',
  N'<h2>Chính sách vận chuyển</h2><p>Thông tin về phí ship và thời gian giao hàng...</p>', 1),
 (N'chinh-sach-thanh-toan', N'Chính sách thanh toán',
  N'<h2>Chính sách thanh toán</h2><p>Các phương thức thanh toán được chấp nhận...</p>', 1),
 (N'dieu-khoan-su-dung', N'Điều khoản sử dụng',
  N'<h2>Điều khoản sử dụng</h2><p>Quy định sử dụng website...</p>', 1),
 (N'chinh-sach-bao-mat', N'Chính sách bảo mật',
  N'<h2>Chính sách bảo mật</h2><p>Bảo vệ thông tin cá nhân...</p>', 1),
 (N'lien-he', N'Liên hệ',
  N'<h2>Liên hệ với chúng tôi</h2><p>Thông tin liên hệ...</p>', 1),
 (N'huong-dan-mua-hang', N'Hướng dẫn mua hàng',
  N'<h2>Hướng dẫn mua hàng</h2><p>Các bước đặt hàng...</p>', 1);

------------------------------------------------------------
-- 6) COUPONS
------------------------------------------------------------
INSERT INTO dbo.Coupon(code, discount_percent, start_date, end_date, active, min_order_amount) VALUES
 (N'WELCOME10', 10, DATEADD(DAY,-7,GETDATE()), DATEADD(DAY,30,GETDATE()), 1, 1000000),
 (N'VIP20',     20, DATEADD(DAY,-1,GETDATE()), DATEADD(DAY,60,GETDATE()), 1, 3000000),
 (N'BF30',      30, DATEADD(DAY,-10,GETDATE()), DATEADD(DAY,10,GETDATE()), 1, 5000000);

------------------------------------------------------------
-- 7) SHIPPING ZONES / FEES / PROVINCES
------------------------------------------------------------
INSERT INTO dbo.ShippingZone(name, slug) VALUES
 (N'Bắc', N'mien-bac'), (N'Trung', N'mien-trung'), (N'Nam', N'mien-nam');

INSERT INTO dbo.ShippingFee(zone_id, base_fee)
SELECT id, 150000 FROM dbo.ShippingZone WHERE slug=N'mien-bac';
INSERT INTO dbo.ShippingFee(zone_id, base_fee)
SELECT id, 130000 FROM dbo.ShippingZone WHERE slug=N'mien-trung';
INSERT INTO dbo.ShippingFee(zone_id, base_fee)
SELECT id, 100000 FROM dbo.ShippingZone WHERE slug=N'mien-nam';

INSERT INTO dbo.ProvinceZone(province_name, zone_id) VALUES
-- Miền Bắc (15 tỉnh/thành)
 (N'Bắc Ninh', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Cao Bằng', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Điện Biên', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Hà Nội', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Hải Phòng', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Hưng Yên', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Lai Châu', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Lạng Sơn', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Lào Cai', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Ninh Bình', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Phú Thọ', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Quảng Ninh', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Sơn La', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Thái Nguyên', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),
 (N'Tuyên Quang', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac')),

-- Miền Trung (11 tỉnh/thành)
 (N'Đà Nẵng', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Đắk Lắk', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Gia Lai', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Hà Tĩnh', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Huế', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Khánh Hòa', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Lâm Đồng', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Nghệ An', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Quảng Ngãi', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Quảng Trị', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),
 (N'Thanh Hóa', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung')),

-- Miền Nam (8 tỉnh/thành)
 (N'An Giang', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'Cà Mau', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'Cần Thơ', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'Đồng Nai', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'Đồng Tháp', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'TP.HCM', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'Tây Ninh', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam')),
 (N'Vĩnh Long', (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam'));

------------------------------------------------------------
-- 8) DELIVERY TEAMS + ZONES
------------------------------------------------------------
INSERT INTO dbo.DeliveryTeam(name, phone, user_id) VALUES
 (N'Đội Giao Khu Vực Bắc', N'0911001100', (SELECT id FROM dbo.Users WHERE username=N'delivery_north')), 
 (N'Đội Giao Khu Vực Bắc 2', N'0911001200', (SELECT id FROM dbo.Users WHERE username=N'delivery_north2')),
 (N'Đội Giao Khu Vực Trung', N'0911002200', (SELECT id FROM dbo.Users WHERE username=N'delivery_central')), 
 (N'Đội Giao Khu Vực Nam', N'0911003300', (SELECT id FROM dbo.Users WHERE username=N'delivery_south')),
 (N'Đội Giao Khu Vực Nam 2', N'0911003400', (SELECT id FROM dbo.Users WHERE username=N'delivery_south2'));

INSERT INTO dbo.DeliveryTeamZone(delivery_team_id, zone_id)
SELECT (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Bắc'),
       (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac');
INSERT INTO dbo.DeliveryTeamZone(delivery_team_id, zone_id)
SELECT (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Bắc 2'),
       (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-bac');
INSERT INTO dbo.DeliveryTeamZone(delivery_team_id, zone_id)
SELECT (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Trung'),
       (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-trung');
INSERT INTO dbo.DeliveryTeamZone(delivery_team_id, zone_id)
SELECT (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Nam'),
       (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam');
INSERT INTO dbo.DeliveryTeamZone(delivery_team_id, zone_id)
SELECT (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Nam 2'),
       (SELECT id FROM dbo.ShippingZone WHERE slug=N'mien-nam');

------------------------------------------------------------
-- 9) PRODUCTS (mỗi nhóm 2–3 SP), COLLECTION gán ngẫu nhiên
------------------------------------------------------------
-- Helper: lấy id category nhanh (VIEW tạm dùng SELECT)
-- Phòng ăn
DECLARE @cat_ban_an INT = (SELECT id FROM dbo.Category WHERE slug=N'ban-an');
DECLARE @cat_ghe_an INT = (SELECT id FROM dbo.Category WHERE slug=N'ghe-an');
DECLARE @cat_bo_ban_an INT = (SELECT id FROM dbo.Category WHERE slug=N'bo-ban-an');

-- Phòng khách
DECLARE @cat_ghe_sofa INT = (SELECT id FROM dbo.Category WHERE slug=N'ghe-sofa');
DECLARE @cat_ban_sofa INT = (SELECT id FROM dbo.Category WHERE slug=N'ban-sofa-ban-cafe-ban-tra');
DECLARE @cat_tu_ke_tivi INT = (SELECT id FROM dbo.Category WHERE slug=N'tu-ke-tivi');
DECLARE @cat_tu_giay INT = (SELECT id FROM dbo.Category WHERE slug=N'tu-giay-tu-trang-tri');

-- Phòng ngủ
DECLARE @cat_giuong INT = (SELECT id FROM dbo.Category WHERE slug=N'giuong-ngu');
DECLARE @cat_tu_quan_ao INT = (SELECT id FROM dbo.Category WHERE slug=N'tu-quan-ao');
DECLARE @cat_tu_dau_giuong INT = (SELECT id FROM dbo.Category WHERE slug=N'tu-dau-giuong');
DECLARE @cat_ban_trang_diem INT = (SELECT id FROM dbo.Category WHERE slug=N'ban-trang-diem');
DECLARE @cat_combo_phong_ngu INT = (SELECT id FROM dbo.Category WHERE slug=N'combo-phong-ngu');

-- Phòng làm việc
DECLARE @cat_ban_lv INT = (SELECT id FROM dbo.Category WHERE slug=N'ban-lam-viec');
DECLARE @cat_ghe_vp INT = (SELECT id FROM dbo.Category WHERE slug=N'ghe-van-phong');
DECLARE @cat_tu_va_ke INT = (SELECT id FROM dbo.Category WHERE slug=N'tu-va-ke');

-- Collections
DECLARE @col_premium INT = (SELECT id FROM dbo.Category WHERE slug=N'premium' AND [type]=N'COLLECTION');
DECLARE @col_oslo    INT = (SELECT id FROM dbo.Category WHERE slug=N'oslo' AND [type]=N'COLLECTION');
DECLARE @col_milan   INT = (SELECT id FROM dbo.Category WHERE slug=N'milan' AND [type]=N'COLLECTION');
DECLARE @col_serena  INT = (SELECT id FROM dbo.Category WHERE slug=N'serena' AND [type]=N'COLLECTION');
DECLARE @col_koster  INT = (SELECT id FROM dbo.Category WHERE slug=N'koster' AND [type]=N'COLLECTION');

-- Colors
DECLARE @c_den  INT = (SELECT id FROM dbo.Color WHERE slug=N'den');
DECLARE @c_nau  INT = (SELECT id FROM dbo.Color WHERE slug=N'nau');
DECLARE @c_be   INT = (SELECT id FROM dbo.Color WHERE slug=N'be');
DECLARE @c_trang INT = (SELECT id FROM dbo.Color WHERE slug=N'trang');
DECLARE @c_xam  INT = (SELECT id FROM dbo.Color WHERE slug=N'xam');
DECLARE @c_xanh INT = (SELECT id FROM dbo.Color WHERE slug=N'xanh');

-- === Phòng ăn: Bàn ăn ===
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id)
VALUES
 (N'Bàn ăn gỗ óc chó 6 ghế',    N'ban-an-go-oc-cho-6-ghe', N'Bàn ăn gỗ óc chó...', @cat_ban_an,  @col_premium),
 (N'Bàn ăn mặt đá 4 ghế',       N'ban-an-mat-da-4-ghe',    N'Bàn ăn mặt đá...',    @cat_ban_an,  @col_milan);

-- Variants (type_name: 4-ghe/6-ghe/8-ghe)
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_den, N'6-ghe',  N'BANOC6_DEN', 12990000, 10, 50, N'SALE' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau, N'6-ghe',  N'BANOC6_NAU', 12990000,  5, 40, N'SALE' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be,  N'6-ghe',  N'BANOC6_BE',  12490000,  0, 60, N'OUTLET' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_trang, N'4-ghe', N'BANDA4_TRANG',  8990000,  5, 80, N'SALE' FROM dbo.Product p WHERE slug=N'ban-an-mat-da-4-ghe';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_xam,   N'4-ghe', N'BANDA4_XAM',    8990000,  0, 70, N'OUTLET' FROM dbo.Product p WHERE slug=N'ban-an-mat-da-4-ghe';

-- Images (mỗi màu thư mục riêng)
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_den, N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/den/00_ban-an-go-oc-cho-6-ghe_den.jpg' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_den, N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/den/01_ban-an-go-oc-cho-6-ghe_den.jpg' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau, N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/nau/00_ban-an-go-oc-cho-6-ghe_nau.jpg' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,  N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/be/00_ban-an-go-oc-cho-6-ghe_be.jpg' FROM dbo.Product p WHERE slug=N'ban-an-go-oc-cho-6-ghe';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_trang, N'/static/images/products/phong-an/ban-an/ban-an-mat-da-4-ghe/trang/00_ban-an-mat-da-4-ghe_trang.jpg' FROM dbo.Product p WHERE slug=N'ban-an-mat-da-4-ghe';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_xam,   N'/static/images/products/phong-an/ban-an/ban-an-mat-da-4-ghe/xam/00_ban-an-mat-da-4-ghe_xam.jpg' FROM dbo.Product p WHERE slug=N'ban-an-mat-da-4-ghe';

-- === Phòng ăn: Ghế ăn ===
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id)
VALUES
 (N'Ghế ăn bọc nỉ',      N'ghe-an-boc-ni',     N'Ghế ăn êm ái...',        @cat_ghe_an, @col_serena),
 (N'Ghế ăn lưng cong',   N'ghe-an-lung-cong',  N'Ghế lưng cong...',       @cat_ghe_an, @col_oslo);

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be,  N'standard', N'GA_NI_BE',   1290000, 0, 120, N'OUTLET' FROM dbo.Product p WHERE slug=N'ghe-an-boc-ni';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_xanh,N'standard', N'GA_NI_XANH', 1390000,10,  80, N'SALE' FROM dbo.Product p WHERE slug=N'ghe-an-boc-ni';

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_den, N'standard', N'GA_CONG_DEN',  1590000, 5, 60, N'SALE' FROM dbo.Product p WHERE slug=N'ghe-an-lung-cong';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_trang,N'standard', N'GA_CONG_TRANG',1590000, 0, 50, N'OUTLET' FROM dbo.Product p WHERE slug=N'ghe-an-lung-cong';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,    N'/static/images/products/phong-an/ghe-an/ghe-an-boc-ni/be/00_ghe-an-boc-ni_be.jpg' FROM dbo.Product p WHERE slug=N'ghe-an-boc-ni';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_xanh,  N'/static/images/products/phong-an/ghe-an/ghe-an-boc-ni/xanh/00_ghe-an-boc-ni_xanh.jpg' FROM dbo.Product p WHERE slug=N'ghe-an-boc-ni';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_den,   N'/static/images/products/phong-an/ghe-an/ghe-an-lung-cong/den/00_ghe-an-lung-cong_den.jpg' FROM dbo.Product p WHERE slug=N'ghe-an-lung-cong';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_trang, N'/static/images/products/phong-an/ghe-an/ghe-an-lung-cong/trang/00_ghe-an-lung-cong_trang.jpg' FROM dbo.Product p WHERE slug=N'ghe-an-lung-cong';

-- === Phòng khách: Sofa / Bàn sofa / Tủ kệ / Tủ giày ===
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id) VALUES
 (N'Sofa vải 3 chỗ',     N'sofa-vai-3-cho', N'Sofa 3 chỗ...', @cat_ghe_sofa, @col_koster),
 (N'Sofa góc chữ L',     N'sofa-goc-chu-l', N'Sofa góc...',   @cat_ghe_sofa, @col_premium),
 (N'Bàn sofa gỗ tròn',   N'ban-sofa-go-tron', N'Bàn tròn...', @cat_ban_sofa, @col_oslo),
 (N'Kệ tivi 2m',         N'ke-tivi-2m', N'Kệ tivi...', @cat_tu_ke_tivi, @col_serena),
 (N'Tủ giày cánh lật',   N'tu-giay-canh-lat', N'Tủ giày...', @cat_tu_giay, @col_oslo);

-- Variants (sofa: 2-cho / 3-cho / goc-L)
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_xam, N'3-cho', N'SO3_XAM', 11990000, 10, 25, N'SALE' FROM dbo.Product p WHERE slug=N'sofa-vai-3-cho';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_trang,N'3-cho', N'SO3_TRANG',12490000,  0, 20, NULL FROM dbo.Product p WHERE slug=N'sofa-vai-3-cho';

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_den, N'goc-L', N'SOL_DEN', 16990000, 15, 15, N'SALE' FROM dbo.Product p WHERE slug=N'sofa-goc-chu-l';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau, N'goc-L', N'SOL_NAU', 16990000,  5, 10, N'SALE' FROM dbo.Product p WHERE slug=N'sofa-goc-chu-l';

-- Bàn sofa
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau, N'60cm', N'BS_TRON60_NAU', 2490000, 0, 70, NULL FROM dbo.Product p WHERE slug=N'ban-sofa-go-tron';

-- Kệ tivi
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be, N'2m', N'KETIVI2M_BE', 3990000, 0, 30, NULL FROM dbo.Product p WHERE slug=N'ke-tivi-2m';

-- Tủ giày
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau, N'90cm', N'TUGIAY90_NAU', 2990000, 0, 50, NULL FROM dbo.Product p WHERE slug=N'tu-giay-canh-lat';

-- Images
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_xam,  N'/static/images/products/phong-khach/ghe-sofa/sofa-vai-3-cho/xam/00_sofa-vai-3-cho_xam.jpg' FROM dbo.Product p WHERE slug=N'sofa-vai-3-cho';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_trang,N'/static/images/products/phong-khach/ghe-sofa/sofa-vai-3-cho/trang/00_sofa-vai-3-cho_trang.jpg' FROM dbo.Product p WHERE slug=N'sofa-vai-3-cho';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_den,  N'/static/images/products/phong-khach/ghe-sofa/sofa-goc-chu-l/den/00_sofa-goc-chu-l_den.jpg' FROM dbo.Product p WHERE slug=N'sofa-goc-chu-l';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau,  N'/static/images/products/phong-khach/ghe-sofa/sofa-goc-chu-l/nau/00_sofa-goc-chu-l_nau.jpg' FROM dbo.Product p WHERE slug=N'sofa-goc-chu-l';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau, N'/static/images/products/phong-khach/ban-sofa-ban-cafe-ban-tra/ban-sofa-go-tron/nau/00_ban-sofa-go-tron_nau.jpg' FROM dbo.Product p WHERE slug=N'ban-sofa-go-tron';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,  N'/static/images/products/phong-khach/tu-ke-tivi/ke-tivi-2m/be/00_ke-tivi-2m_be.jpg' FROM dbo.Product p WHERE slug=N'ke-tivi-2m';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau, N'/static/images/products/phong-khach/tu-giay-tu-trang-tri/tu-giay-canh-lat/nau/00_tu-giay-canh-lat_nau.jpg' FROM dbo.Product p WHERE slug=N'tu-giay-canh-lat';

-- === Phòng ngủ: Giường / Tủ / Táp / Bàn trang điểm / Combo ===
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id) VALUES
 (N'Giường gỗ 1m6', N'giuong-go-1m6', N'Giường 1m6...', @cat_giuong, @col_premium),
 (N'Giường bọc nệm 1m8', N'giuong-boc-nem-1m8', N'Giường 1m8...', @cat_giuong, @col_serena),
 (N'Tủ quần áo 3 cánh', N'tu-quan-ao-3-canh', N'Tủ 3 cánh...', @cat_tu_quan_ao, @col_oslo),
 (N'Tủ đầu giường 2 hộc', N'tu-dau-giuong-2-hoc', N'Táp 2 hộc...', @cat_tu_dau_giuong, @col_oslo),
 (N'Bàn trang điểm Nordic', N'ban-trang-diem-nordic', N'Bàn trang điểm...', @cat_ban_trang_diem, @col_milan),
 (N'Combo phòng ngủ Oslo', N'combo-phong-ngu-oslo', N'Combo giường+tủ+táp...', @cat_combo_phong_ngu, @col_oslo);

-- Variants
-- Giường 1m6: type 1m6, màu nâu / be
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau, N'1m6', N'GN1M6_NAU', 8990000,  5, 40, N'SALE' FROM dbo.Product p WHERE slug=N'giuong-go-1m6';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be,  N'1m6', N'GN1M6_BE',  8990000, 10, 35, N'SALE' FROM dbo.Product p WHERE slug=N'giuong-go-1m6';

-- Giường bọc nệm 1m8: màu xám / trắng
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_xam,   N'1m8', N'GN1M8_XAM',   10990000, 0, 25, N'OUTLET' FROM dbo.Product p WHERE slug=N'giuong-boc-nem-1m8';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_trang, N'1m8', N'GN1M8_TRANG', 10990000, 5, 20, N'SALE' FROM dbo.Product p WHERE slug=N'giuong-boc-nem-1m8';

-- Tủ quần áo: 2m, màu trắng / nâu
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_trang, N'2m', N'TQA3C_TRANG', 7990000, 0, 30, N'OUTLET' FROM dbo.Product p WHERE slug=N'tu-quan-ao-3-canh';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau,   N'2m', N'TQA3C_NAU',   7990000,10, 20, N'SALE' FROM dbo.Product p WHERE slug=N'tu-quan-ao-3-canh';

-- Táp đầu giường: màu nâu
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau, N'std', N'TAP2_NAU', 1290000, 0, 60, N'OUTLET' FROM dbo.Product p WHERE slug=N'tu-dau-giuong-2-hoc';

-- Bàn trang điểm: 100cm, be/trắng
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be,   N'100cm', N'BTD100_BE',   3490000, 0, 40, N'OUTLET' FROM dbo.Product p WHERE slug=N'ban-trang-diem-nordic';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_trang,N'100cm', N'BTD100_TRANG',3490000, 5, 30, N'SALE' FROM dbo.Product p WHERE slug=N'ban-trang-diem-nordic';

-- Combo: dùng type_name theo size giường 1m6/1m8, màu be
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be, N'combo-1m6', N'COMBO_OSLO_16_BE', 15990000, 10, 10, N'SALE' FROM dbo.Product p WHERE slug=N'combo-phong-ngu-oslo';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be, N'combo-1m8', N'COMBO_OSLO_18_BE', 17990000, 15,  8, N'SALE' FROM dbo.Product p WHERE slug=N'combo-phong-ngu-oslo';

-- Images
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau,  N'/static/images/products/phong-ngu/giuong-ngu/giuong-go-1m6/nau/00_giuong-go-1m6_nau.jpg' FROM dbo.Product p WHERE slug=N'giuong-go-1m6';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,   N'/static/images/products/phong-ngu/giuong-ngu/giuong-go-1m6/be/00_giuong-go-1m6_be.jpg' FROM dbo.Product p WHERE slug=N'giuong-go-1m6';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_xam,  N'/static/images/products/phong-ngu/giuong-ngu/giuong-boc-nem-1m8/xam/00_giuong-boc-nem-1m8_xam.jpg' FROM dbo.Product p WHERE slug=N'giuong-boc-nem-1m8';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_trang,N'/static/images/products/phong-ngu/giuong-ngu/giuong-boc-nem-1m8/trang/00_giuong-boc-nem-1m8_trang.jpg' FROM dbo.Product p WHERE slug=N'giuong-boc-nem-1m8';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_trang, N'/static/images/products/phong-ngu/tu-quan-ao/tu-quan-ao-3-canh/trang/00_tu-quan-ao-3-canh_trang.jpg' FROM dbo.Product p WHERE slug=N'tu-quan-ao-3-canh';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau,   N'/static/images/products/phong-ngu/tu-quan-ao/tu-quan-ao-3-canh/nau/00_tu-quan-ao-3-canh_nau.jpg' FROM dbo.Product p WHERE slug=N'tu-quan-ao-3-canh';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau,   N'/static/images/products/phong-ngu/tu-dau-giuong/tu-dau-giuong-2-hoc/nau/00_tu-dau-giuong-2-hoc_nau.jpg' FROM dbo.Product p WHERE slug=N'tu-dau-giuong-2-hoc';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,    N'/static/images/products/phong-ngu/ban-trang-diem/ban-trang-diem-nordic/be/00_ban-trang-diem-nordic_be.jpg' FROM dbo.Product p WHERE slug=N'ban-trang-diem-nordic';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_trang, N'/static/images/products/phong-ngu/ban-trang-diem/ban-trang-diem-nordic/trang/00_ban-trang-diem-nordic_trang.jpg' FROM dbo.Product p WHERE slug=N'ban-trang-diem-nordic';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,    N'/static/images/products/phong-ngu/combo-phong-ngu/combo-phong-ngu-oslo/be/00_combo-phong-ngu-oslo_be.jpg' FROM dbo.Product p WHERE slug=N'combo-phong-ngu-oslo';

-- === Phòng làm việc: Bàn / Ghế / Tủ & kệ ===
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id) VALUES
 (N'Bàn làm việc 120cm', N'ban-lam-viec-120', N'Bàn 120cm...', @cat_ban_lv, @col_oslo),
 (N'Bàn làm việc 160cm', N'ban-lam-viec-160', N'Bàn 160cm...', @cat_ban_lv, @col_premium),
 (N'Ghế công thái học', N'ghe-cong-thai-hoc', N'Ghế ergonomic...', @cat_ghe_vp, @col_koster),
 (N'Kệ sách 5 tầng',    N'ke-sach-5-tang', N'Kệ 5 tầng...', @cat_tu_va_ke, @col_serena);

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_be,   N'120cm', N'BLV120_BE',  2990000, 0, 70, N'OUTLET' FROM dbo.Product p WHERE slug=N'ban-lam-viec-120';
INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_den,  N'160cm', N'BLV160_DEN', 3990000, 5, 50, N'SALE' FROM dbo.Product p WHERE slug=N'ban-lam-viec-160';

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_xam,  N'std',   N'GCTH_XAM',   4590000, 0, 40, N'OUTLET' FROM dbo.Product p WHERE slug=N'ghe-cong-thai-hoc';

INSERT INTO dbo.ProductVariant(product_id, color_id, type_name, sku, price, discount_percent, stock_qty, promotion_type)
SELECT p.id, @c_nau,  N'5-tang',N'KESACH5_NAU', 2190000, 0, 60, N'OUTLET' FROM dbo.Product p WHERE slug=N'ke-sach-5-tang';

INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_be,  N'/static/images/products/phong-lam-viec/ban-lam-viec/ban-lam-viec-120/be/00_ban-lam-viec-120_be.jpg' FROM dbo.Product p WHERE slug=N'ban-lam-viec-120';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_den, N'/static/images/products/phong-lam-viec/ban-lam-viec/ban-lam-viec-160/den/00_ban-lam-viec-160_den.jpg' FROM dbo.Product p WHERE slug=N'ban-lam-viec-160';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_xam, N'/static/images/products/phong-lam-viec/ghe-van-phong/ghe-cong-thai-hoc/xam/00_ghe-cong-thai-hoc_xam.jpg' FROM dbo.Product p WHERE slug=N'ghe-cong-thai-hoc';
INSERT INTO dbo.ProductImage(product_id, color_id, url)
SELECT p.id, @c_nau, N'/static/images/products/phong-lam-viec/tu-va-ke/ke-sach-5-tang/nau/00_ke-sach-5-tang_nau.jpg' FROM dbo.Product p WHERE slug=N'ke-sach-5-tang';

------------------------------------------------------------
-- 10) ARTICLES (media/news/people) + ArticleImage
------------------------------------------------------------
INSERT INTO dbo.Article(title, slug, article_type, summary, thumbnail, [status], published_at)
VALUES
 (N'Phong cách Bắc Âu', N'phong-cach-bac-au', N'MEDIA', N'Giới thiệu style...', 
  N'/static/images/articles/media/phong-cach-bac-au/thumbnail/00_phong-cach-bac-au.jpg', 1, GETDATE()),
 (N'Gỗ óc chó phối nội thất', N'go-oc-cho-phoi-noi-that', N'NEWS', N'Xu hướng vật liệu...', 
  N'/static/images/articles/news/go-oc-cho-phoi-noi-that/thumbnail/00_go-oc-cho-phoi-noi-that.jpg', 1, GETDATE()),
 (N'Nghệ nhân ABC', N'nghe-nhan-abc', N'PEOPLE', N'Câu chuyện thương hiệu...', 
  N'/static/images/articles/people/nghe-nhan-abc/thumbnail/00_nghe-nhan-abc.jpg', 1, GETDATE());

-- Lấy id bài
DECLARE @a_media  INT = (SELECT id FROM dbo.Article WHERE slug=N'phong-cach-bac-au');
DECLARE @a_news   INT = (SELECT id FROM dbo.Article WHERE slug=N'go-oc-cho-phoi-noi-that');
DECLARE @a_people INT = (SELECT id FROM dbo.Article WHERE slug=N'nghe-nhan-abc');

INSERT INTO dbo.ArticleImage(article_id, url, caption) VALUES
 (@a_media,  N'/static/images/articles/media/phong-cach-bac-au/content/00_phong-cach-bac-au.jpg', N'Phòng khách tông sáng'),
 (@a_media,  N'/static/images/articles/media/phong-cach-bac-au/content/01_phong-cach-bac-au.jpg', N'Bố cục tối giản'),
 (@a_news,   N'/static/images/articles/news/go-oc-cho-phoi-noi-that/content/00_go-oc-cho-phoi-noi-that.jpg', N'Veneer óc chó'),
 (@a_people, N'/static/images/articles/people/nghe-nhan-abc/content/00_nghe-nhan-abc.jpg', N'Thợ mộc lành nghề');

------------------------------------------------------------
-- 11) ADDRESS (phải có trước Orders)
------------------------------------------------------------
INSERT INTO dbo.Address(user_id, receiver_name, phone, address_line, city, district, is_default)
VALUES
((SELECT id FROM dbo.Users WHERE username=N'cust_a'), N'Trần Khách A', N'0900000007', N'12 Nguyễn Văn A', N'TP.HCM', N'Quận 1', 1),
((SELECT id FROM dbo.Users WHERE username=N'cust_b'), N'Đỗ Khách B',   N'0900000008', N'34 Trần Văn B',   N'Hà Nội', N'Cầu Giấy', 1);

-- Lấy address_id
DECLARE @addrA INT = (SELECT id FROM dbo.Address WHERE user_id=(SELECT id FROM dbo.Users WHERE username=N'cust_a'));
DECLARE @addrB INT = (SELECT id FROM dbo.Address WHERE user_id=(SELECT id FROM dbo.Users WHERE username=N'cust_b'));

------------------------------------------------------------
-- 12) CARTS & ITEMS
------------------------------------------------------------
INSERT INTO dbo.Cart(user_id) 
SELECT id FROM dbo.Users WHERE username=N'cust_a';
INSERT INTO dbo.Cart(user_id) 
SELECT id FROM dbo.Users WHERE username=N'cust_b';

-- Cart A: 1 bàn ăn + 4 ghế
DECLARE @cartA INT = (SELECT id FROM dbo.Cart WHERE user_id=(SELECT id FROM dbo.Users WHERE username=N'cust_a'));
INSERT INTO dbo.CartItem(cart_id, variant_id, qty)
VALUES
 (@cartA, (SELECT id FROM dbo.ProductVariant WHERE sku=N'BANOC6_DEN'), 1),
 (@cartA, (SELECT id FROM dbo.ProductVariant WHERE sku=N'GA_NI_BE'), 4);

-- Cart B: sofa + bàn sofa
DECLARE @cartB INT = (SELECT id FROM dbo.Cart WHERE user_id=(SELECT id FROM dbo.Users WHERE username=N'cust_b'));
INSERT INTO dbo.CartItem(cart_id, variant_id, qty)
VALUES
 (@cartB, (SELECT id FROM dbo.ProductVariant WHERE sku=N'SO3_XAM'), 1),
 (@cartB, (SELECT id FROM dbo.ProductVariant WHERE sku=N'BS_TRON60_NAU'), 1);

------------------------------------------------------------
-- 13) ORDERS + ITEMS (sử dụng sp_CreateOrder)
------------------------------------------------------------
-- A: Đơn 1 (PENDING) - sử dụng sp_CreateOrder
DECLARE @orderA1 INT;
DECLARE @itemsA1 dbo.TVP_OrderItem;
INSERT INTO @itemsA1(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'BANOC6_DEN'), 1),
((SELECT id FROM dbo.ProductVariant WHERE sku=N'GA_NI_BE'), 4);

DECLARE @oA1 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
DECLARE @userA INT = (SELECT id FROM dbo.Users WHERE username=N'cust_a');
INSERT INTO @oA1 EXEC dbo.sp_CreateOrder 
  @user_id = @userA,
  @address_id = @addrA,
  @coupon_code = N'WELCOME10',
  @items = @itemsA1;
SELECT @orderA1 = order_id FROM @oA1;

-- B: Đơn 2 (CONFIRMED) - sử dụng sp_CreateOrder
DECLARE @orderB1 INT;
DECLARE @itemsB1 dbo.TVP_OrderItem;
INSERT INTO @itemsB1(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'SO3_XAM'), 1),
((SELECT id FROM dbo.ProductVariant WHERE sku=N'BS_TRON60_NAU'), 1);

DECLARE @oB1 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
DECLARE @userB INT = (SELECT id FROM dbo.Users WHERE username=N'cust_b');
INSERT INTO @oB1 EXEC dbo.sp_CreateOrder 
  @user_id = @userB,
  @address_id = @addrB,
  @coupon_code = NULL,
  @items = @itemsB1;
SELECT @orderB1 = order_id FROM @oB1;

-- Xác nhận đơn B1
DECLARE @managerID INT = (SELECT id FROM dbo.Users WHERE username=N'manager');
EXEC dbo.sp_ConfirmOrder @order_id = @orderB1, @actor_user_id = @managerID;

-- A: Đơn 3 (DISPATCHED) - sử dụng sp_CreateOrder
DECLARE @orderA2 INT;
DECLARE @itemsA2 dbo.TVP_OrderItem;
INSERT INTO @itemsA2(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'SOL_DEN'), 1);

DECLARE @oA2 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
INSERT INTO @oA2 EXEC dbo.sp_CreateOrder 
  @user_id = @userA,
  @address_id = @addrA,
  @coupon_code = N'VIP20',
  @items = @itemsA2;
SELECT @orderA2 = order_id FROM @oA2;

-- Xác nhận và dispatch đơn A2
DECLARE @teamNam INT = (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Nam');
EXEC dbo.sp_ConfirmOrder @order_id = @orderA2, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @orderA2, @delivery_team_id = @teamNam, @actor_user_id = @managerID;

-- B: Đơn 4 (DELIVERED) - sử dụng sp_CreateOrder
DECLARE @orderB2 INT;
DECLARE @itemsB2 dbo.TVP_OrderItem;
INSERT INTO @itemsB2(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'KETIVI2M_BE'), 1);

DECLARE @oB2 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
INSERT INTO @oB2 EXEC dbo.sp_CreateOrder 
  @user_id = @userB,
  @address_id = @addrB,
  @coupon_code = NULL,
  @items = @itemsB2;
SELECT @orderB2 = order_id FROM @oB2;

-- Xác nhận, dispatch và delivered đơn B2
DECLARE @teamBac INT = (SELECT id FROM dbo.DeliveryTeam WHERE name=N'Đội Giao Khu Vực Bắc');
DECLARE @proofB2 NVARCHAR(255) = N'/static/images/deliveries/order-'+CAST(@orderB2 AS NVARCHAR(10))+'/00_order-'+CAST(@orderB2 AS NVARCHAR(10))+'.jpg';
EXEC dbo.sp_ConfirmOrder @order_id = @orderB2, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @orderB2, @delivery_team_id = @teamBac, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDelivered @order_id = @orderB2, @proof_image_url = @proofB2, @actor_user_id = @managerID;

-- Cập nhật payment_status = PAID cho đơn B2
UPDATE dbo.Orders SET payment_method = N'VNPAY', payment_status = N'PAID' WHERE id = @orderB2;

-- A: Đơn 5 (CANCELLED) - sử dụng sp_CreateOrder
DECLARE @orderA3 INT;
DECLARE @itemsA3 dbo.TVP_OrderItem;
INSERT INTO @itemsA3(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'TQA3C_TRANG'), 1);

DECLARE @oA3 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
INSERT INTO @oA3 EXEC dbo.sp_CreateOrder 
  @user_id = @userA,
  @address_id = @addrA,
  @coupon_code = NULL,
  @items = @itemsA3;
SELECT @orderA3 = order_id FROM @oA3;

-- Hủy đơn A3
EXEC dbo.sp_CancelOrder @order_id = @orderA3, @actor_user_id = @managerID, @reason = N'Hủy theo yêu cầu khách hàng';

-- B: Đơn 6 (RETURNED) - sử dụng sp_CreateOrder
DECLARE @orderB3 INT;
DECLARE @itemsB3 dbo.TVP_OrderItem;
INSERT INTO @itemsB3(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'GN1M8_TRANG'), 1);

DECLARE @oB3 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
INSERT INTO @oB3 EXEC dbo.sp_CreateOrder 
  @user_id = @userB,
  @address_id = @addrB,
  @coupon_code = N'WELCOME10',
  @items = @itemsB3;
SELECT @orderB3 = order_id FROM @oB3;

-- Xác nhận, dispatch, delivered và return đơn B3
DECLARE @proofB3 NVARCHAR(255) = N'/static/images/deliveries/order-'+CAST(@orderB3 AS NVARCHAR(10))+'/00_order-'+CAST(@orderB3 AS NVARCHAR(10))+'.jpg';
EXEC dbo.sp_ConfirmOrder @order_id = @orderB3, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @orderB3, @delivery_team_id = @teamBac, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDelivered @order_id = @orderB3, @proof_image_url = @proofB3, @actor_user_id = @managerID;
EXEC dbo.sp_ReturnOrder @order_id = @orderB3, @actor_user_id = @managerID, @reason = N'Khách hàng yêu cầu trả hàng';

-- Cập nhật payment_method = COD cho đơn B3
UPDATE dbo.Orders SET payment_method = N'COD' WHERE id = @orderB3;

------------------------------------------------------------
-- 14) ORDER DELIVERY (đã được tạo tự động qua sp_MarkDispatched)
------------------------------------------------------------
-- Lấy id OrderDelivery đã được tạo tự động
DECLARE @odA2 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id=@orderA2);
DECLARE @odB2 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id=@orderB2);
DECLARE @odB3 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id=@orderB3);

-- Thêm DeliveryHistory bổ sung
INSERT INTO dbo.DeliveryHistory(order_delivery_id, status, note, photo_url)
VALUES
 (@odA2, N'IN_TRANSIT', N'Rời kho', N'/static/images/deliveries/order-'+CAST(@orderA2 AS NVARCHAR(10))+'/00_order-'+CAST(@orderA2 AS NVARCHAR(10))+'.jpg'),
 (@odB2, N'DONE',       N'Bàn giao thành công', N'/static/images/deliveries/order-'+CAST(@orderB2 AS NVARCHAR(10))+'/01_order-'+CAST(@orderB2 AS NVARCHAR(10))+'.jpg'),
 (@odB3, N'RETURN_PICKUP', N'Đang thu hồi', N'/static/images/deliveries/order-'+CAST(@orderB3 AS NVARCHAR(10))+'/02_order-'+CAST(@orderB3 AS NVARCHAR(10))+'_return.jpg');

------------------------------------------------------------
-- 15) REVIEWS (mỗi review tối đa 1 ảnh)
------------------------------------------------------------
-- Review cho kệ tivi 2m (cust_b) — order B2 có item 'KETIVI2M_BE'
INSERT INTO dbo.Review(product_id, user_id, order_item_id, rating, content, image_url)
SELECT p.id, u.id, oi.id, 4, N'Kệ đẹp, chất lượng tốt.',
       N'/static/images/reviews/3105/00_review_3105.jpg'
FROM dbo.Product p
JOIN dbo.Users u ON u.username = N'cust_b'
JOIN dbo.OrderItems oi ON oi.order_id = @orderB2
JOIN dbo.ProductVariant v ON v.id = oi.variant_id AND v.product_id = p.id
WHERE p.slug = N'ke-tivi-2m';

-- Review cho bàn ăn 6 ghế (cust_a) — order A1 có 'BANOC6_DEN'
INSERT INTO dbo.Review(product_id, user_id, order_item_id, rating, content, image_url)
SELECT p.id, u.id, oi.id, 5, N'Rất chắc chắn.',
       N'/static/images/reviews/3106/00_review_3106.jpg'
FROM dbo.Product p
JOIN dbo.Users u ON u.username = N'cust_a'
JOIN dbo.OrderItems oi ON oi.order_id = @orderA1
JOIN dbo.ProductVariant v ON v.id = oi.variant_id AND v.product_id = p.id
WHERE p.slug = N'ban-an-go-oc-cho-6-ghe';

-- Review cho ghế ăn bọc nỉ (cust_a) — order A1 có 'GA_NI_BE' (cùng đơn với bàn ăn)
INSERT INTO dbo.Review(product_id, user_id, order_item_id, rating, content, image_url)
SELECT p.id, u.id, oi.id, 4, N'Ghế êm, ngồi thoải mái.',
       N'/static/images/reviews/3107/00_review_3107.jpg'
FROM dbo.Product p
JOIN dbo.Users u ON u.username = N'cust_a'
JOIN dbo.OrderItems oi ON oi.order_id = @orderA1
JOIN dbo.ProductVariant v ON v.id = oi.variant_id AND v.product_id = p.id
WHERE p.slug = N'ghe-an-boc-ni';

------------------------------------------------------------
-- 16) WISHLIST + VIEWED
------------------------------------------------------------
INSERT INTO dbo.Wishlist(user_id, product_id)
SELECT (SELECT id FROM dbo.Users WHERE username=N'cust_a'),
       (SELECT id FROM dbo.Product WHERE slug=N'sofa-goc-chu-l');

INSERT INTO dbo.Viewed(user_id, product_id) VALUES
((SELECT id FROM dbo.Users WHERE username=N'cust_a'), (SELECT id FROM dbo.Product WHERE slug=N'sofa-vai-3-cho')),
((SELECT id FROM dbo.Users WHERE username=N'cust_b'), (SELECT id FROM dbo.Product WHERE slug=N'giuong-go-1m6'));

------------------------------------------------------------
-- 17) NOTIFICATIONS
------------------------------------------------------------
INSERT INTO dbo.UserNotification(user_id, title, message)
VALUES
((SELECT id FROM dbo.Users WHERE username=N'cust_a'), N'Đơn hàng PENDING', N'Đơn của bạn đã tạo'),
((SELECT id FROM dbo.Users WHERE username=N'cust_b'), N'Giao hàng thành công', N'Đơn đã giao');

------------------------------------------------------------
-- 18) CONVERSATION + MESSAGES (đính kèm ảnh)
------------------------------------------------------------
INSERT INTO dbo.Conversation(user_id, status) 
VALUES((SELECT id FROM dbo.Users WHERE username=N'cust_a'), N'OPEN');
DECLARE @convA INT = SCOPE_IDENTITY();

INSERT INTO dbo.Message(conversation_id, sender_type, sender_id, content, attachment_url)
VALUES
(@convA, N'CUSTOMER', (SELECT id FROM dbo.Users WHERE username=N'cust_a'),
 N'Cho mình hỏi kích thước bàn?', N'/static/images/messages/conversation-'+CAST(@convA AS NVARCHAR(10))+'/00_message-1.jpg'),
(@convA, N'MANAGER',  (SELECT id FROM dbo.Users WHERE username=N'manager'),
 N'Dạ bàn có size 120/160.', NULL);

------------------------------------------------------------
-- 19) OTP DEMO
------------------------------------------------------------
INSERT INTO dbo.OTP(user_id, code, purpose, is_used, expires_at)
VALUES
((SELECT id FROM dbo.Users WHERE username=N'cust_a'), N'123456', N'REGISTER', 0, DATEADD(HOUR,2,GETDATE())),
((SELECT id FROM dbo.Users WHERE username=N'cust_b'), N'987654', N'RESET_PASSWORD', 0, DATEADD(HOUR,1,GETDATE()));

------------------------------------------------------------
-- 20) SHOWROOM (Cửa hàng trưng bày)
------------------------------------------------------------
INSERT INTO dbo.Showroom(name, address, city, district, phone, email, open_hours, is_active) VALUES
 (N'Showroom Hà Nội', N'123 Đường Láng, Đống Đa', N'Hà Nội', N'Đống Đa', N'024.1234.5678', N'hn@mocviet.vn', N'8:00 - 22:00', 1),
 (N'Showroom TP.HCM', N'456 Nguyễn Huệ, Quận 1', N'TP.HCM', N'Quận 1', N'028.8765.4321', N'hcm@mocviet.vn', N'8:00 - 22:00', 1),
 (N'Showroom Đà Nẵng', N'789 Lê Duẩn, Hải Châu', N'Đà Nẵng', N'Hải Châu', N'0236.1111.2222', N'dn@mocviet.vn', N'8:00 - 22:00', 1);

------------------------------------------------------------
-- 21) SOCIAL LINKS (Liên kết mạng xã hội)
------------------------------------------------------------
INSERT INTO dbo.SocialLink(platform, url, is_active) VALUES
 (N'FACEBOOK', N'https://facebook.com/mocviet', 1),
 (N'ZALO', N'https://zalo.me/mocviet', 1),
 (N'YOUTUBE', N'https://youtube.com/@mocviet', 1);

COMMIT TRANSACTION;
PRINT N'Transaction committed successfully!';
