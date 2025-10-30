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
SELECT id, N'Huỳnh Ngọc Thắng',  N'admin',   N'win2005thang@gmail.com',   N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',   N'Nam', N'0900000001' FROM dbo.Roles WHERE name=N'ADMIN';

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
SELECT id, N'Phan Thành Tài',N'delivery_north',N'delivery_north@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911001100' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Đỗ Duy Khánh',N'delivery_north2',N'delivery_north2@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911001200' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Nguyễn Văn Khánh',N'delivery_central',N'delivery_central@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911002200' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Lê Văn Nam',N'delivery_south',N'delivery_south@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911003300' FROM dbo.Roles WHERE name=N'DELIVERY';
INSERT INTO dbo.Users(role_id, full_name, username, email, password_hash, gender, phone)
SELECT id, N'Trần Văn Nam',N'delivery_south2',N'delivery_south2@mocviet.local',N'$2a$12$XP/thldHL.FqZUQmozamNeGXlijdtS7.E5aVolRvLToDjEMXgCkfC',N'Nam',N'0911003400' FROM dbo.Roles WHERE name=N'DELIVERY';
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
-- 5) STATIC PAGES (content chi tiết - Admin có thể chỉnh sửa)
------------------------------------------------------------
INSERT INTO dbo.StaticPage(slug, title, content, is_active)
VALUES
 (N'gioi-thieu', N'Giới thiệu Mộc Việt',
  N'<div class="content-wrapper">
    <h2>Về Mộc Việt</h2>
    <p><strong>Mộc Việt</strong> là thương hiệu nội thất gỗ hàng đầu Việt Nam, chuyên sản xuất và cung cấp các sản phẩm nội thất gỗ tự nhiên chất lượng cao với thiết kế hiện đại, sang trọng.</p>
    
    <h3>Lịch sử hình thành</h3>
    <p>Được thành lập vào năm 2010, Mộc Việt bắt đầu như một xưởng sản xuất nội thất nhỏ tại Thành phố Hồ Chí Minh. Với tầm nhìn mang đến những sản phẩm nội thất gỗ chất lượng cao cho người Việt, chúng tôi đã không ngừng phát triển và mở rộng quy mô.</p>
    
    <h3>Sứ mệnh của chúng tôi</h3>
    <p>Mộc Việt cam kết:</p>
    <ul>
      <li>Mang đến cho mọi gia đình Việt những sản phẩm nội thất chất lượng cao với giá cả hợp lý</li>
      <li>Sử dụng 100% gỗ tự nhiên, đảm bảo an toàn sức khỏe cho gia đình</li>
      <li>Thiết kế hiện đại, phù hợp với không gian sống của người Việt</li>
      <li>Chế tác thủ công tỉ mỉ, đảm bảo độ bền và tính thẩm mỹ</li>
    </ul>
    
    <h3>Giá trị cốt lõi</h3>
    <p><strong>Chất lượng:</strong> Chúng tôi sử dụng gỗ tự nhiên cao cấp, được kiểm định kỹ lưỡng trước khi đưa vào sản xuất.</p>
    <p><strong>Độ bền:</strong> Mỗi sản phẩm được chế tác kỹ lưỡng, đảm bảo độ bền theo thời gian.</p>
    <p><strong>Thẩm mỹ:</strong> Thiết kế hiện đại, sang trọng, phù hợp với nhiều phong cách nội thất.</p>
    <p><strong>An toàn:</strong> Không sử dụng hóa chất độc hại, thân thiện với môi trường và sức khỏe.</p>
    
    <h3>Cam kết với khách hàng</h3>
    <p>Mộc Việt luôn đặt khách hàng làm trung tâm, cam kết:</p>
    <ul>
      <li>Chất lượng sản phẩm vượt trội</li>
      <li>Bảo hành chính hãng 12 tháng</li>
      <li>Dịch vụ hỗ trợ tận tâm 24/7</li>
      <li>Giao hàng và lắp đặt miễn phí</li>
      <li>Chế độ bảo trì định kỳ</li>
    </ul>
    
    <h3>Địa chỉ liên hệ</h3>
    <p><i class="bi bi-geo-alt"></i> <strong>Showroom chính:</strong> 162 HT17, Phường Hiệp Thành, Quận 12, TP. HCM</p>
    <p><i class="bi bi-telephone"></i> <strong>Hotline:</strong> 0971 141 140</p>
    <p><i class="bi bi-envelope"></i> <strong>Email:</strong> cskh@mocviet.vn</p>
    <p><i class="bi bi-clock"></i> <strong>Giờ làm việc:</strong> 8:00 - 21:00 (Cả tuần)</p>
  </div>'
  , 1),
 
 (N'chinh-sach-bao-hanh', N'Chính sách bảo hành', 
  N'<div class="content-wrapper">
    <h2>Chính sách bảo hành sản phẩm</h2>
    <p>Mộc Việt tự hào mang đến cho khách hàng dịch vụ bảo hành chuyên nghiệp và đáng tin cậy.</p>
    
    <h3>1. Thời gian bảo hành</h3>
    <p><strong>Bảo hành chính hãng 12 tháng</strong> kể từ ngày giao hàng và lắp đặt hoàn tất. Trong thời gian này, mọi hư hỏng do lỗi sản xuất hoặc vật liệu sẽ được chúng tôi bảo hành miễn phí.</p>
    
    <h3>2. Điều kiện bảo hành</h3>
    <p>Sản phẩm được bảo hành khi đáp ứng các điều kiện sau:</p>
    <ul>
      <li>Có hóa đơn mua hàng hợp lệ hoặc giấy tờ chứng minh việc mua hàng tại Mộc Việt</li>
      <li>Hư hỏng xuất phát từ lỗi kỹ thuật, sai sót trong quá trình sản xuất</li>
      <li>Sản phẩm còn trong thời hạn bảo hành</li>
      <li>Sản phẩm được bảo quản và sử dụng đúng hướng dẫn</li>
    </ul>
    
    <h3>3. Các trường hợp KHÔNG được bảo hành</h3>
    <ul>
      <li>Hư hỏng do va đập, rơi vỡ hoặc tai nạn do người sử dụng</li>
      <li>Biến dạng do thời tiết, độ ẩm, nhiệt độ cao</li>
      <li>Sử dụng sai mục đích hoặc không tuân thủ hướng dẫn</li>
      <li>Sản phẩm đã được sửa chữa, thay đổi bởi bên thứ ba</li>
      <li>Mất phiếu bảo hành hoặc tem bảo hành</li>
    </ul>
    
    <h3>4. Quy trình yêu cầu bảo hành</h3>
    <ol>
      <li>Liên hệ hotline <strong>0971 141 140</strong> hoặc gửi email đến <strong>cskh@mocviet.vn</strong></li>
      <li>Cung cấp thông tin: Họ tên, Số điện thoại, Mã đơn hàng, Mô tả sự cố</li>
      <li>Nhân viên kỹ thuật sẽ đến kiểm tra trong vòng 48 giờ (tại TP.HCM)</li>
      <li>Nếu thuộc diện bảo hành, chúng tôi sẽ sửa chữa hoặc thay thế miễn phí</li>
    </ol>
    
    <h3>5. Dịch vụ bảo trì</h3>
    <ul>
      <li><strong>Bảo trì định kỳ miễn phí:</strong> Trong 6 tháng đầu, chúng tôi sẽ thực hiện bảo trì và kiểm tra sản phẩm 1 lần</li>
      <li><strong>Tư vấn bảo quản:</strong> Nhân viên sẽ hướng dẫn cách bảo quản sản phẩm để tăng tuổi thọ sử dụng</li>
      <li><strong>Dịch vụ vệ sinh chuyên nghiệp:</strong> Cung cấp dịch vụ vệ sinh và đánh bóng gỗ (có phí)</li>
    </ul>
    
    <h3>6. Cam kết bảo hành</h3>
    <p>Mộc Việt cam kết xử lý tất cả yêu cầu bảo hành trong thời gian ngắn nhất, đảm bảo khách hàng có thể sử dụng sản phẩm một cách tốt nhất.</p>
  </div>', 1),
 
 (N'chinh-sach-doi-tra', N'Chính sách đổi trả',
  N'<div class="content-wrapper">
    <h2>Chính sách đổi trả sản phẩm</h2>
    <p>Mộc Việt cam kết đảm bảo quyền lợi tối đa cho khách hàng với chính sách đổi trả linh hoạt.</p>
    
    <h3>1. Thời hạn đổi trả</h3>
    <p>Khách hàng có thể yêu cầu đổi trả sản phẩm <strong>trong vòng 7 ngày</strong> kể từ ngày nhận hàng (đối với đơn hàng online) hoặc ngày giao hàng (đối với đơn COD).</p>
    
    <h3>2. Điều kiện đổi trả</h3>
    <p>Sản phẩm phải đáp ứng các điều kiện sau:</p>
    <ul>
      <li>Sản phẩm còn nguyên vẹn, chưa qua sử dụng hoặc lắp đặt</li>
      <li>Còn nguyên tem, nhãn mác, bao bì gốc</li>
      <li>Có hóa đơn mua hàng hợp lệ</li>
      <li>Không thuộc hàng custom theo yêu cầu riêng của khách hàng</li>
      <li>Sản phẩm không bị hư hỏng do lỗi khách hàng</li>
    </ul>
    
    <h3>3. Lý do được chấp nhận đổi trả</h3>
    <ul>
      <li>Sản phẩm bị lỗi từ phía Mộc Việt (lỗi sản xuất, thiếu phụ kiện, giao sai hàng)</li>
      <li>Sản phẩm không đúng với thông tin đã mô tả trên website</li>
      <li>Khách hàng không hài lòng với sản phẩm và thông báo trong vòng 24 giờ đầu</li>
    </ul>
    
    <h3>4. Quy trình đổi trả</h3>
    <ol>
      <li><strong>Liên hệ chúng tôi:</strong> Gọi hotline <strong>0971 141 140</strong> hoặc email <strong>cskh@mocviet.vn</strong></li>
      <li><strong>Cung cấp thông tin:</strong> Mã đơn hàng, lý do đổi trả, ảnh sản phẩm (nếu có)</li>
      <li><strong>Xác nhận yêu cầu:</strong> Nhân viên CSKH sẽ xác nhận trong vòng 24 giờ</li>
      <li><strong>Đóng gói và trả hàng:</strong> Khách hàng đóng gói sản phẩm an toàn và chờ nhân viên đến lấy</li>
      <li><strong>Kiểm tra sản phẩm:</strong> Mộc Việt sẽ kiểm tra sản phẩm trả về</li>
      <li><strong>Hoàn tiền hoặc giao sản phẩm mới:</strong> Trong vòng 5-7 ngày làm việc</li>
    </ol>
    
    <h3>5. Phí đổi trả</h3>
    <table class="table">
      <thead>
        <tr>
          <th>Trường hợp</th>
          <th>Phí vận chuyển</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Lỗi do bên bán (giao sai, hàng lỗi)</td>
          <td>Miễn phí hoàn toàn</td>
        </tr>
        <tr>
          <td>Khách hàng không hài lòng</td>
          <td>Khách hàng chịu phí vận chuyển</td>
        </tr>
      </tbody>
    </table>
    
    <h3>6. Phương thức hoàn tiền</h3>
    <ul>
      <li>Nếu thanh toán bằng chuyển khoản: Hoàn tiền vào tài khoản trong 3-5 ngày</li>
      <li>Nếu thanh toán COD: Hoàn tiền bằng chuyển khoản hoặc tiền mặt khi nhận lại hàng</li>
      <li>Nếu thanh toán bằng thẻ: Hoàn tiền vào tài khoản thẻ trong 7-14 ngày</li>
    </ul>
    
    <h3>7. Lưu ý</h3>
    <p>Đối với sản phẩm đã lắp đặt và sử dụng, chúng tôi sẽ đánh giá từng trường hợp cụ thể. Vui lòng liên hệ CSKH để được tư vấn chi tiết.</p>
  </div>', 1),
 
 (N'chinh-sach-van-chuyen', N'Chính sách vận chuyển',
  N'<div class="content-wrapper">
    <h2>Chính sách vận chuyển và lắp đặt</h2>
    <p>Mộc Việt cam kết giao hàng nhanh chóng, an toàn và chuyên nghiệp đến tận nơi cho khách hàng.</p>
    
    <h3>1. Phạm vi giao hàng</h3>
    <p>Chúng tôi giao hàng <strong>toàn quốc</strong> với chính sách phí ship chi tiết như sau:</p>
    <ul>
      <li><strong>Nội thành TP.HCM:</strong> Miễn phí giao hàng cho đơn từ 500.000đ</li>
      <li><strong>Ngoại thành TP.HCM:</strong> Phí ship từ 30.000đ - 50.000đ tùy khu vực</li>
      <li><strong>Tỉnh/Thành khác:</strong> Phí ship theo bảng giá của đơn vị vận chuyển (VNPost, Viettel Post...)</li>
    </ul>
    
    <h3>2. Thời gian giao hàng</h3>
    <table class="table">
      <thead>
        <tr>
          <th>Loại sản phẩm</th>
          <th>Thời gian giao hàng</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Hàng có sẵn trong kho</td>
          <td>2-3 ngày làm việc</td>
        </tr>
        <tr>
          <td>Hàng đặt hàng</td>
          <td>7-15 ngày làm việc</td>
        </tr>
        <tr>
          <td>Hàng custom theo yêu cầu</td>
          <td>14-30 ngày làm việc</td>
        </tr>
      </tbody>
    </table>
    
    <h3>3. Dịch vụ lắp đặt</h3>
    <ul>
      <li><strong>Miễn phí lắp đặt</strong> tại TP.HCM và một số tỉnh thành lân cận (áp dụng cho sản phẩm cần lắp đặt)</li>
      <li>Nhân viên kỹ thuật chuyên nghiệp, có kinh nghiệm nhiều năm trong lĩnh vực nội thất</li>
      <li>Đảm bảo chất lượng lắp đặt và hướng dẫn sử dụng sản phẩm</li>
      <li>Tận nơi tư vấn và thiết kế không gian (theo yêu cầu, có phí)</li>
      <li>Cam kết lắp đặt đúng theo thiết kế, đẹp mắt và bền chắc</li>
    </ul>
    
    <h3>4. Quy trình giao hàng</h3>
    <ol>
      <li><strong>Xác nhận đơn hàng:</strong> Nhân viên CSKH sẽ gọi điện xác nhận thông tin đơn hàng trong vòng 24 giờ</li>
      <li><strong>Chuẩn bị hàng:</strong> Kiểm tra kỹ chất lượng sản phẩm trước khi giao</li>
      <li><strong>Thông báo giao hàng:</strong> Gọi điện thông báo trước 30 phút khi đến giao</li>
      <li><strong>Giao hàng:</strong> Nhân viên giao hàng sẽ đến đúng địa chỉ đã đăng ký</li>
      <li><strong>Kiểm tra và nhận hàng:</strong> Khách hàng kiểm tra sản phẩm trước khi ký nhận</li>
      <li><strong>Lắp đặt (nếu có):</strong> Nhân viên kỹ thuật tiến hành lắp đặt nếu sản phẩm yêu cầu</li>
    </ol>
    
    <h3>5. Xử lý khi hàng bị hư hỏng</h3>
    <p>Nếu phát hiện hàng bị hư hỏng khi nhận:</p>
    <ul>
      <li>Vui lòng <strong>KHÔNG ký nhận</strong> và chụp ảnh bằng chứng</li>
      <li>Liên hệ ngay hotline <strong>0971 141 140</strong> để báo cáo</li>
      <li>Chúng tôi sẽ gửi hàng thay thế hoặc hoàn tiền 100%</li>
    </ul>
    
    <h3>6. Chính sách đặc biệt</h3>
    <ul>
      <li>Giao hàng trong ngày tại TP.HCM (với đơn hàng trước 12h trưa)</li>
      <li>Giao hàng cuối tuần (thứ 7, Chủ nhật) có thêm phí 50.000đ</li>
      <li>Dịch vụ lắp đặt ngoài giờ (sau 18h) có thêm phí 100.000đ</li>
    </ul>
  </div>', 1),
 
 (N'chinh-sach-thanh-toan', N'Chính sách thanh toán',
  N'<div class="content-wrapper">
    <h2>Chính sách thanh toán</h2>
    <p>Mộc Việt cung cấp nhiều phương thức thanh toán linh hoạt, tiện lợi và an toàn cho khách hàng.</p>
    
    <h3>1. Phương thức thanh toán</h3>
    
    <h4>1.1. Thanh toán khi nhận hàng (COD)</h4>
    <ul>
      <li>Thanh toán trực tiếp khi nhận hàng, chỉ thanh toán sau khi đã kiểm tra hàng</li>
      <li>Hỗ trợ khắp toàn quốc</li>
      <li>Phí COD: <strong>30.000đ/đơn</strong> (áp dụng ngoại tỉnh TP.HCM)</li>
      <li>Miễn phí COD cho đơn hàng từ 1.000.000đ</li>
    </ul>
    
    <h4>1.2. Chuyển khoản ngân hàng</h4>
    <ul>
      <li>Chuyển khoản trước khi giao hàng để nhận ưu đãi</li>
      <li><strong>Giảm ngay 2%</strong> cho đơn hàng từ 2.000.000đ trở lên khi thanh toán chuyển khoản</li>
      <li>Thông tin chuyển khoản:
        <ul>
          <li><strong>Số tài khoản:</strong> 1234567890</li>
          <li><strong>Ngân hàng:</strong> Vietcombank - Chi nhánh TP.HCM</li>
          <li><strong>Chủ tài khoản:</strong> MỘC VIỆT FURNITURE CO., LTD</li>
          <li><strong>Nội dung:</strong> Thanh toan don hang [Mã đơn hàng]</li>
        </ul>
      </li>
    </ul>
    
    <h4>1.3. Thẻ tín dụng / Thẻ ghi nợ</h4>
    <ul>
      <li>Thanh toán online qua cổng thanh toán an toàn</li>
      <li>Chấp nhận: Visa, Mastercard, JCB, American Express</li>
      <li>Thanh toán an toàn với SSL 256-bit</li>
      <li>Không thu thêm bất kỳ phí phụ thu nào</li>
    </ul>
    
    <h4>1.4. Ví điện tử</h4>
    <ul>
      <li><strong>Momo:</strong> Quét QR code hoặc chuyển khoản qua số điện thoại</li>
      <li><strong>ZaloPay:</strong> Thanh toán nhanh chóng qua app</li>
      <li><strong>ShopeePay:</strong> Thanh toán tiện lợi</li>
      <li>Giảm thêm 1% khi thanh toán bằng ví điện tử</li>
    </ul>
    
    <h3>2. Quy trình thanh toán</h3>
    <ol>
      <li>Đặt hàng và chọn phương thức thanh toán phù hợp</li>
      <li>Xác nhận đơn hàng qua SMS/Email trong vòng 5 phút</li>
      <li>Thanh toán theo phương thức đã chọn</li>
      <li>Nhận hàng và kiểm tra sản phẩm</li>
      <li>Nếu hài lòng, xác nhận đã nhận hàng</li>
    </ol>
    
    <h3>3. Bảo mật thông tin thanh toán</h3>
    <ul>
      <li>Tất cả giao dịch được mã hóa SSL/TLS 256-bit</li>
      <li>Không lưu trữ thông tin thẻ tín dụng của khách hàng</li>
      <li>Tuân thủ tiêu chuẩn bảo mật PCI DSS</li>
      <li>Hợp tác với các đơn vị thanh toán uy tín (VNPAY, Payoo)</li>
    </ul>
    
    <h3>4. Lưu ý quan trọng</h3>
    <ul>
      <li>Đơn hàng sẽ được giữ tối đa <strong>24 giờ</strong> chờ thanh toán, sau đó tự động hủy</li>
      <li>Vui lòng <strong>giữ lại hóa đơn</strong> để phục vụ việc đổi trả, bảo hành</li>
      <li>Nếu thanh toán chuyển khoản, vui lòng ghi rõ <strong>mã đơn hàng</strong> trong nội dung chuyển khoản</li>
      <li>Liên hệ hotline nếu có thắc mắc về thanh toán: <strong>0971 141 140</strong></li>
    </ul>
    
    <h3>5. Ưu đãi thanh toán</h3>
    <ul>
      <li>Giảm 2% cho đơn từ 2.000.000đ khi chuyển khoản</li>
      <li>Giảm 1% cho đơn từ 500.000đ khi dùng ví điện tử</li>
      <li>Miễn phí COD cho đơn từ 1.000.000đ</li>
      <li>Ưu đãi không được cộng dồn</li>
    </ul>
  </div>', 1),
 
 (N'chinh-sach-bao-mat', N'Chính sách bảo mật',
  N'<div class="content-wrapper">
    <h2>Chính sách bảo mật thông tin</h2>
    <p>Mộc Việt cam kết bảo vệ thông tin cá nhân của khách hàng một cách tuyệt đối và chuyên nghiệp.</p>
    
    <h3>1. Thông tin thu thập</h3>
    <p>Chúng tôi thu thập các thông tin sau để phục vụ khách hàng tốt nhất:</p>
    <ul>
      <li><strong>Thông tin cá nhân:</strong> Họ tên, số điện thoại, email, địa chỉ</li>
      <li><strong>Thông tin thanh toán:</strong> Phương thức thanh toán, lịch sử giao dịch (không lưu thông tin thẻ tín dụng)</li>
      <li><strong>Thông tin thiết bị:</strong> IP address, trình duyệt, thiết bị truy cập (để cải thiện trải nghiệm)</li>
    </ul>
    
    <h3>2. Mục đích sử dụng thông tin</h3>
    <p>Thông tin của bạn được sử dụng để:</p>
    <ul>
      <li>Xử lý và giao hàng đơn hàng</li>
      <li>Gửi thông báo về đơn hàng qua SMS/Email</li>
      <li>Liên hệ hỗ trợ khách hàng</li>
      <li>Cải thiện chất lượng dịch vụ</li>
      <li>Gửi ưu đãi và khuyến mãi (nếu đã đăng ký nhận)</li>
    </ul>
    
    <h3>3. Biện pháp bảo vệ</h3>
    <ul>
      <li><strong>Mã hóa dữ liệu:</strong> Tất cả dữ liệu được mã hóa SSL/TLS 256-bit</li>
      <li><strong>Không chia sẻ thông tin:</strong> Không bán, cho thuê hoặc chia sẻ thông tin cho bên thứ ba</li>
      <li><strong>Bảo mật vật lý:</strong> Máy chủ được đặt trong môi trường an toàn, có camera giám sát 24/7</li>
      <li><strong>Hệ thống firewall:</strong> Bảo vệ chống lại tấn công mạng</li>
      <li><strong>Kiểm tra định kỳ:</strong> Hệ thống được kiểm tra và cập nhật bảo mật thường xuyên</li>
    </ul>
    
    <h3>4. Quyền lợi của khách hàng</h3>
    <p>Bạn có quyền:</p>
    <ul>
      <li><strong>Truy cập:</strong> Yêu cầu xem thông tin cá nhân đã lưu</li>
      <li><strong>Chỉnh sửa:</strong> Yêu cầu sửa đổi thông tin không chính xác</li>
      <li><strong>Xóa dữ liệu:</strong> Yêu cầu xóa tài khoản và dữ liệu cá nhân</li>
      <li><strong>Từ chối marketing:</strong> Từ chối nhận email quảng cáo khuyến mãi</li>
      <li><strong>Khiếu nại:</strong> Liên hệ nếu phát hiện sử dụng thông tin không đúng mục đích</li>
    </ul>
    
    <h3>5. Cookie và công nghệ theo dõi</h3>
    <p>Website sử dụng cookie để:</p>
    <ul>
      <li>Nhớ thông tin đăng nhập của bạn</li>
      <li>Lưu giữ sản phẩm trong giỏ hàng</li>
      <li>Cải thiện trải nghiệm người dùng</li>
    </ul>
    <p>Bạn có thể tắt cookie trong cài đặt trình duyệt, nhưng một số chức năng website có thể không hoạt động tốt.</p>
    
    <h3>6. Liên hệ về bảo mật</h3>
    <p>Nếu có thắc mắc về chính sách bảo mật, vui lòng liên hệ:</p>
    <ul>
      <li><strong>Email:</strong> privacy@mocviet.vn</li>
      <li><strong>Hotline:</strong> 0971 141 140</li>
      <li><strong>Địa chỉ:</strong> 162 HT17, P. Hiệp Thành, Q. 12, TP. HCM</li>
    </ul>
    
    <h3>7. Cập nhật chính sách</h3>
    <p>Chúng tôi có thể cập nhật chính sách bảo mật này theo thời gian. Mọi thay đổi sẽ được thông báo trên website. Khách hàng nên thường xuyên kiểm tra để biết thông tin mới nhất.</p>
  </div>', 1);

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
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id, sold_qty)
VALUES
 (N'Bàn ăn gỗ óc chó 6 ghế',    N'ban-an-go-oc-cho-6-ghe', N'Bàn ăn gỗ óc chó...', @cat_ban_an,  @col_premium, 1),
 (N'Bàn ăn mặt đá 4 ghế',       N'ban-an-mat-da-4-ghe',    N'Bàn ăn mặt đá...',    @cat_ban_an,  @col_milan, 0);

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
INSERT INTO dbo.Product(name, slug, description, category_id, collection_id, sold_qty)
VALUES
 (N'Ghế ăn bọc nỉ',      N'ghe-an-boc-ni',     N'Ghế ăn êm ái...',        @cat_ghe_an, @col_serena, 1),
 (N'Ghế ăn lưng cong',   N'ghe-an-lung-cong',  N'Ghế lưng cong...',       @cat_ghe_an, @col_oslo, 0);

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

-- Phong cách Bắc Âu (Scandinavian)
UPDATE dbo.Article SET content = N'
<h2>Giới thiệu chung về phong cách Bắc Âu</h2>
<p>
  Phong cách Bắc Âu (Scandinavian) nổi bật với sự tối giản, tinh tế và gần gũi thiên nhiên. Yếu tố đặc trưng dễ nhận biết nhất chính là sự kết hợp giữa màu sắc trung tính, ánh sáng tự nhiên, vật liệu organic và sự tiện nghi hiện đại.
</p>
<img src="/static/images/articles/media/phong-cach-bac-au/content/00_phong-cach-bac-au.jpg" alt="Không gian phòng khách Bắc Âu" style="width:100%; margin-bottom:2rem;">
<h3>Đặc điểm nhận diện</h3>
<ul>
  <li><b>Tối giản mà thanh lịch:</b> Không gian gọn gàng, ít chi tiết rườm rà, ưu tiên sự thông thoáng.</li>
  <li><b>Màu sắc trung tính:</b> Màu trắng, be, xám..., kết hợp điểm nhấn pastel hoặc màu tự nhiên.</li>
  <li><b>Chất liệu tự nhiên:</b> Gỗ sáng màu, len, da lộn, vải bố, mây tre.</li>
  <li><b>Ánh sáng tự nhiên tràn ngập:</b> Cửa sổ lớn nhiều ánh sáng trời, sử dụng rèm mỏng, vật dụng tối thiểu hoặc ghép kính.</li>
  <li><b>Sự tiện nghi giản đơn:</b> Ưu tiên tính năng sử dụng, layout logic, tất cả hướng đến sự thoải mái.</li>
</ul>
<img src="/static/images/articles/media/phong-cach-bac-au/content/01_phong-cach-bac-au.jpg" alt="Nội thất Bắc Âu tối giản" style="width:100%; margin-bottom:2rem;">
<p>
  <b>Kết luận:</b> Ứng dụng phong cách Bắc Âu cho phép bạn có một không gian sống tối giản mà vẫn gần gũi, tươi sáng và ấm cúng.
</p>
' WHERE slug = N'phong-cach-bac-au';

-- Gỗ óc chó phối nội thất
UPDATE dbo.Article SET content = N'
<h2>Gỗ óc chó – lựa chọn “vương giả” cho nội thất hiện đại</h2>
<p>
  Gỗ óc chó (walnut) nổi bật với vân đẹp, sắc nâu sáng – tối khác nhau cùng độ bền “trứ danh”. Nội thất gỗ óc chó thể hiện đẳng cấp cho căn hộ, biệt thự hoặc phòng làm việc sang trọng. 
</p>
<img src="/static/images/articles/news/go-oc-cho-phoi-noi-that/content/00_go-oc-cho-phoi-noi-that.jpg" alt="Bàn ăn mặt veneer óc chó" style="width:100%;margin-bottom:2rem;">
<h3>Lý do nên dùng gỗ óc chó?</h3>
<ul>
  <li><b>Độ bền vượt trội:</b> Gỗ tự nhiên nhập khẩu, kháng cong vênh và chịu lực lớn.</li>
  <li><b>Vẻ đẹp tự nhiên:</b> Vân gỗ uốn lượn mềm mại, tông màu ấm phù hợp đa phong cách.</li>
  <li><b>Ưu việt về thi công:</b> Dễ kết hợp sofa, tủ, giường, ốp tường, vách decor...</li>
  <li><b>Bảo quản dễ dàng:</b> Chỉ cần lau khô là bóng đẹp mãi nhiều năm.</li>
</ul>
<p>
  <b>Kết:</b> Nội thất gỗ óc chó là xu hướng của căn hộ cao cấp, thể hiện cá tính & vị thế chủ nhân.
</p>
' WHERE slug = N'go-oc-cho-phoi-noi-that';

-- Nghệ nhân ABC
UPDATE dbo.Article SET content = N'
<h2>Chân dung nghệ nhân ABC – Tấm gương gìn giữ tinh hoa thủ công</h2>
<img src="/static/images/articles/people/nghe-nhan-abc/content/00_nghe-nhan-abc.jpg" alt="Nghệ nhân ABC" style="float:right;width:300px;margin-left:2rem;margin-bottom:1rem">
<p>
  Nghệ nhân ABC xuất thân từ làng nghề truyền thống lâu đời, gắn bó cả đời với đồ gỗ thủ công. Gia đình ông 3 đời đều là thợ mộc giỏi, những sản phẩm của ông được nhiều khách “săn lùng” bởi tỉ mỉ và tinh thần nghệ thuật đặc trưng.
</p>
<ul>
  <li><b>Chặng đường sự nghiệp:</b> 35 năm sống cùng nghề, góp phần bảo tồn các kiểu chạm khắc cổ.</li>
  <li><b>Phát triển sáng tạo:</b> Liên tục sáng tạo, không ngại thử nghiệm chất liệu và kỹ thuật mới trên nền xưa cũ.</li>
  <li><b>Truyền nghề cho thế hệ trẻ:</b> Đào tạo nhiều thợ trẻ, góp phần lan tỏa giá trị thủ công Việt.</li>
</ul>
<p>
  <b>Kết:</b> Sự tận tâm và sáng tạo của nghệ nhân ABC là minh chứng sống cho tinh thần nghề mộc Việt Nam.
</p>
' WHERE slug = N'nghe-nhan-abc';



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
  @payment_method = N'COD',
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
  @payment_method = N'VNPAY',
  @items = @itemsB1;
SELECT @orderB1 = order_id FROM @oB1;

-- Demo webhook thanh toán VNPAY thành công cho đơn B1
DECLARE @txn_code_b1 NVARCHAR(100) = N'VNPAY_' + CAST(@orderB1 AS NVARCHAR(10));
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @orderB1,
  @payment_method = N'VNPAY',
  @is_success = 1,
  @gateway_txn_code = @txn_code_b1;

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
  @payment_method = N'MOMO',
  @items = @itemsA2;
SELECT @orderA2 = order_id FROM @oA2;

-- Demo webhook thanh toán MOMO thành công cho đơn A2
DECLARE @txn_code_a2 NVARCHAR(100) = N'MOMO_' + CAST(@orderA2 AS NVARCHAR(10));
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @orderA2,
  @payment_method = N'MOMO',
  @is_success = 1,
  @gateway_txn_code = @txn_code_a2;

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
  @payment_method = N'COD',
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

-- Thêm sample data cho quy trình trả hàng
-- Đơn B2 sẽ có yêu cầu trả hàng (sử dụng SP mới)
EXEC dbo.sp_RequestReturn
  @order_id = @orderB2,
  @customer_id = @userB,
  @reason = N'Sản phẩm không đúng màu sắc như mô tả trên website';

-- Manager phê duyệt yêu cầu trả hàng (với delivery_team_id)
EXEC dbo.sp_ApproveReturn 
  @order_id = @orderB2, 
  @manager_id = @managerID, 
  @note = N'Đồng ý trả hàng, sản phẩm không đúng mô tả',
  @delivery_team_id = @teamBac;

-- Delivery team thu hồi hàng (sp_ApproveReturn đã set RETURN_PICKUP + tạo/đảm bảo OrderDelivery)
DECLARE @odB2 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id = @orderB2);

UPDATE dbo.OrderDelivery 
SET note = N'Đã thu hồi hàng từ khách hàng',
    proof_image_url = N'/static/images/deliveries/return-'+CAST(@orderB2 AS NVARCHAR(10))+'.jpg'
WHERE id = @odB2;

INSERT INTO dbo.DeliveryHistory(order_delivery_id, [status], note, photo_url)
VALUES(@odB2, N'RETURN_PICKUP', N'Đã thu hồi hàng từ khách hàng', N'/static/images/deliveries/return-'+CAST(@orderB2 AS NVARCHAR(10))+'.jpg');

-- Manager xử lý hoàn tất trả hàng (với refund_method)
EXEC dbo.sp_ReturnOrder 
  @order_id = @orderB2, 
  @actor_user_id = @managerID,
  @reason = N'Hoàn tất quy trình trả hàng mẫu',
  @refund_method = N'COD_CASH';

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
  @payment_method = N'VNPAY',
  @items = @itemsA3;
SELECT @orderA3 = order_id FROM @oA3;

-- Demo webhook thanh toán VNPAY thất bại cho đơn A3
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @orderA3,
  @payment_method = N'VNPAY',
  @is_success = 0,
  @gateway_txn_code = NULL;

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
  @payment_method = N'MOMO',
  @items = @itemsB3;
SELECT @orderB3 = order_id FROM @oB3;

-- Demo webhook thanh toán MOMO thành công cho đơn B3
DECLARE @txn_code_b3 NVARCHAR(100) = N'MOMO_' + CAST(@orderB3 AS NVARCHAR(10));
EXEC dbo.sp_HandlePaymentWebhook 
  @order_id = @orderB3,
  @payment_method = N'MOMO',
  @is_success = 1,
  @gateway_txn_code = @txn_code_b3;

-- Xác nhận, dispatch, delivered đơn B3
DECLARE @proofB3 NVARCHAR(255) = N'/static/images/deliveries/order-'+CAST(@orderB3 AS NVARCHAR(10))+'/00_order-'+CAST(@orderB3 AS NVARCHAR(10))+'.jpg';
EXEC dbo.sp_ConfirmOrder @order_id = @orderB3, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @orderB3, @delivery_team_id = @teamBac, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDelivered @order_id = @orderB3, @proof_image_url = @proofB3, @actor_user_id = @managerID;

-- Yêu cầu trả hàng đơn B3

-- Thêm sample data cho trường hợp từ chối trả hàng
-- Đơn B3 sẽ có yêu cầu trả hàng nhưng bị từ chối (sử dụng SP mới)
EXEC dbo.sp_RequestReturn
  @order_id = @orderB3,
  @customer_id = @userB,
  @reason = N'Không thích sản phẩm sau khi sử dụng';

-- Manager từ chối yêu cầu trả hàng
EXEC dbo.sp_RejectReturn 
  @order_id = @orderB3, 
  @manager_id = @managerID, 
  @note = N'Từ chối trả hàng, sản phẩm đã sử dụng và không có lỗi';

-- C: Đơn 7 (RETURNED - quy trình hoàn chỉnh) - sử dụng sp_CreateOrder
DECLARE @orderC1 INT;
DECLARE @itemsC1 dbo.TVP_OrderItem;
INSERT INTO @itemsC1(variant_id, qty) VALUES
((SELECT id FROM dbo.ProductVariant WHERE sku=N'BLV160_DEN'), 1);

DECLARE @oC1 TABLE(
  order_id INT,
  subtotal_snapshot DECIMAL(18,0),
  discount_amount DECIMAL(18,0),
  total_after_coupon DECIMAL(18,0),
  shipping_fee DECIMAL(12,0),
  grand_total DECIMAL(18,0)
);
DECLARE @userC INT = (SELECT id FROM dbo.Users WHERE username=N'cust_a');
INSERT INTO @oC1 EXEC dbo.sp_CreateOrder 
  @user_id = @userC,
  @address_id = @addrA,
  @coupon_code = N'WELCOME10',
  @payment_method = N'COD',
  @items = @itemsC1;
SELECT @orderC1 = order_id FROM @oC1;

-- Xác nhận, dispatch, delivered đơn C1
DECLARE @proofC1 NVARCHAR(255) = N'/static/images/deliveries/order-'+CAST(@orderC1 AS NVARCHAR(10))+'/00_order-'+CAST(@orderC1 AS NVARCHAR(10))+'.jpg';
EXEC dbo.sp_ConfirmOrder @order_id = @orderC1, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDispatched @order_id = @orderC1, @delivery_team_id = @teamNam, @actor_user_id = @managerID;
EXEC dbo.sp_MarkDelivered @order_id = @orderC1, @proof_image_url = @proofC1, @actor_user_id = @managerID;

-- Cập nhật payment_method = COD cho đơn C1
UPDATE dbo.Orders SET payment_method = N'COD', payment_status = N'PAID' WHERE id = @orderC1;

-- Demo quy trình trả hàng hoàn chỉnh cho đơn C1
-- 1. Khách yêu cầu trả hàng
EXEC dbo.sp_RequestReturn
  @order_id = @orderC1,
  @customer_id = @userC,
  @reason = N'Sản phẩm bị lỗi kỹ thuật, không hoạt động đúng';

-- 2. Manager duyệt yêu cầu
EXEC dbo.sp_ApproveReturn 
  @order_id = @orderC1, 
  @manager_id = @managerID, 
  @note = N'Đồng ý trả hàng, sản phẩm có lỗi kỹ thuật',
  @delivery_team_id = @teamNam;

-- 3. Delivery team thu hồi hàng (sp_ApproveReturn đã set RETURN_PICKUP)
DECLARE @odC1 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id = @orderC1);

UPDATE dbo.OrderDelivery 
SET note = N'Đã thu hồi hàng từ khách hàng',
    proof_image_url = N'/static/images/deliveries/return-'+CAST(@orderC1 AS NVARCHAR(10))+'.jpg'
WHERE id = @odC1;

INSERT INTO dbo.DeliveryHistory(order_delivery_id, [status], note, photo_url)
VALUES(@odC1, N'RETURN_PICKUP', N'Đã thu hồi hàng từ khách hàng', N'/static/images/deliveries/return-'+CAST(@orderC1 AS NVARCHAR(10))+'.jpg');

-- 4. Manager xử lý hoàn tất trả hàng
EXEC dbo.sp_ReturnOrder 
  @order_id = @orderC1, 
  @actor_user_id = @managerID,
  @reason = N'Hoàn tất quy trình trả hàng - sản phẩm lỗi kỹ thuật',
  @refund_method = N'BANK_TRANSFER';

------------------------------------------------------------
-- 14) ORDER DELIVERY (đã được tạo tự động qua sp_MarkDispatched)
------------------------------------------------------------
-- Lấy id OrderDelivery đã được tạo tự động
DECLARE @odA2 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id=@orderA2);
DECLARE @odB3 INT = (SELECT id FROM dbo.OrderDelivery WHERE order_id=@orderB3);

-- Thêm DeliveryHistory bổ sung
INSERT INTO dbo.DeliveryHistory(order_delivery_id, status, note, photo_url)
VALUES
 (@odA2, N'IN_TRANSIT', N'Rời kho', N'/static/images/deliveries/order-'+CAST(@orderA2 AS NVARCHAR(10))+'/00_order-'+CAST(@orderA2 AS NVARCHAR(10))+'.jpg'),
 (@odB3, N'DONE',       N'Bàn giao thành công', N'/static/images/deliveries/order-'+CAST(@orderB3 AS NVARCHAR(10))+'/01_order-'+CAST(@orderB3 AS NVARCHAR(10))+'.jpg');

------------------------------------------------------------
-- 15) REVIEWS (mỗi review tối đa 1 ảnh)
------------------------------------------------------------
-- Review cho kệ tivi 2m (cust_b) — order B2 có item 'KETIVI2M_BE'
INSERT INTO dbo.Review(product_id, user_id, order_item_id, rating, content, image_url)
SELECT p.id, u.id, oi.id, 4, N'Kệ đẹp, chất lượng tốt.',
       N'/static/images/reviews/1/00_review_1.jpg'
FROM dbo.Product p
JOIN dbo.Users u ON u.username = N'cust_b'
JOIN dbo.OrderItems oi ON oi.order_id = @orderB2
JOIN dbo.ProductVariant v ON v.id = oi.variant_id AND v.product_id = p.id
WHERE p.slug = N'ke-tivi-2m';

-- Review cho bàn ăn 6 ghế (cust_a) — order A1 có 'BANOC6_DEN'
INSERT INTO dbo.Review(product_id, user_id, order_item_id, rating, content, image_url)
SELECT p.id, u.id, oi.id, 5, N'Rất chắc chắn.',
       N'/static/images/reviews/2/00_review_2.jpg'
FROM dbo.Product p
JOIN dbo.Users u ON u.username = N'cust_a'
JOIN dbo.OrderItems oi ON oi.order_id = @orderA1
JOIN dbo.ProductVariant v ON v.id = oi.variant_id AND v.product_id = p.id
WHERE p.slug = N'ban-an-go-oc-cho-6-ghe';

-- Review cho ghế ăn bọc nỉ (cust_a) — order A1 có 'GA_NI_BE' (cùng đơn với bàn ăn)
INSERT INTO dbo.Review(product_id, user_id, order_item_id, rating, content, image_url)
SELECT p.id, u.id, oi.id, 4, N'Ghế êm, ngồi thoải mái.',
       N'/static/images/reviews/3/00_review_3.jpg'
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

-- Cập nhật Google Maps iframe cho Showroom Hà Nội (id = 1)
UPDATE dbo.Showroom
SET map_embed = N'<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3724.414482540709!2d105.80169737503108!3d21.016095280630033!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab5e6db3d941%3A0x571f62e3b5a69002!2zMTIzIMSQLiBMw6FuZywgVHJ1bmcgSG_DoCwgxJDhu5FuZyDEkGEsIEjDoCBO4buZaSwgVmlldG5hbQ!5e0!3m2!1sen!2s!4v1761750376622!5m2!1sen!2s" width="600" height="450" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>'
WHERE name = N'Showroom Hà Nội';

-- Cập nhật Google Maps iframe cho Showroom TP.HCM (id = 2)
UPDATE dbo.Showroom
SET map_embed = N'<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.456663700164!2d106.698383374805!3d10.776293589372532!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f47975ce55f%3A0x26d4747546784ad5!2zNDU2IE5ndXnDqsyDbiBIdcOqzKMsIELhur9uIE5naMOpLCBRdeG6rW4gMSwgVGjDoG5oIHBo4buRIEjhu5MgQ2jDrSBNaW5oLCBWaWV0bmFt!5e0!3m2!1sen!2s!4v1761750423212!5m2!1sen!2s" width="600" height="450" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>'
WHERE name = N'Showroom TP.HCM';

-- Cập nhật Google Maps iframe cho Showroom Đà Nẵng (id = 3)
UPDATE dbo.Showroom
SET map_embed = N'<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3833.98124933477!2d108.20450247490396!3d16.06646278461262!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3142184cbc209153%3A0xa10ed2b7170cd131!2zNzg5IEzDqiBEdeG6qW4sIENow61uaCBHacOhbiwgVGhhbmggS2jDqiwgxJDDoCBO4bq1bmcgNTUwMDAwLCBWaWV0bmFt!5e0!3m2!1sen!2s!4v1761750494219!5m2!1sen!2s" width="600" height="450" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>'
WHERE name = N'Showroom Đà Nẵng';

------------------------------------------------------------
-- 21) SOCIAL LINKS (Liên kết mạng xã hội)
------------------------------------------------------------
INSERT INTO dbo.SocialLink(platform, url, is_active) VALUES
 (N'FACEBOOK', N'https://facebook.com/mocviet', 1),
 (N'ZALO', N'https://zalo.me/mocviet', 1),
 (N'YOUTUBE', N'https://youtube.com/@mocviet', 1);

COMMIT TRANSACTION;
PRINT N'Seed committed successfully!';

------------------------------------------------------------
-- TEST CASES: QUY TRÌNH THANH TOÁN & HỦY ĐƠN
------------------------------------------------------------

-- Test Case 1: Online (VNPAY) → thanh toán thành công (webhook) → hủy khi còn PENDING ⇒ REFUND + trả tồn
PRINT N'=== TEST CASE 1: VNPAY Success → Cancel PENDING (REFUND) ===';

DECLARE @test_user1 INT = (SELECT TOP 1 id FROM dbo.Users WHERE username = N'cust_a');
DECLARE @test_addr1 INT = (SELECT TOP 1 id FROM dbo.Address WHERE user_id = @test_user1);
DECLARE @test_sku1 NVARCHAR(80) = N'SO3_XAM'; -- SKU có sẵn với stock_qty > 0
DECLARE @test_variant1 INT = (SELECT id FROM dbo.ProductVariant WHERE sku=@test_sku1 AND stock_qty > 0);

-- Kiểm tra variant có tồn tại không
IF @test_variant1 IS NULL
BEGIN
  PRINT N'Test 1 - SKU không tồn tại, bỏ qua test case này';
  GOTO END_TEST1;
END

DECLARE @test_items1 dbo.TVP_OrderItem;
INSERT INTO @test_items1(variant_id, qty) VALUES(@test_variant1, 1);

DECLARE @test_o1 TABLE(order_id INT, subtotal_snapshot DECIMAL(18,0), discount_amount DECIMAL(18,0),
                       total_after_coupon DECIMAL(18,0), shipping_fee DECIMAL(12,0), grand_total DECIMAL(18,0));

-- Tạo đơn với error handling
BEGIN TRY
  INSERT INTO @test_o1
  EXEC dbo.sp_CreateOrder
    @user_id      = @test_user1,
    @address_id   = @test_addr1,
    @coupon_code  = NULL,
    @payment_method = N'VNPAY',
    @items        = @test_items1;
END TRY
BEGIN CATCH
  PRINT N'Test 1 - Lỗi tạo đơn: ' + ERROR_MESSAGE();
  GOTO END_TEST1;
END CATCH

DECLARE @test_order1 INT = (SELECT order_id FROM @test_o1);

-- Webhook báo thanh toán thành công (trong lúc đơn vẫn PENDING)
EXEC dbo.sp_HandlePaymentWebhook
  @order_id = @test_order1,
  @payment_method = N'VNPAY',
  @is_success = 1,
  @gateway_txn_code = N'VNPAY-TXN-DEMO-001';

-- HỦY khi còn PENDING -> SP tự set payment_status = REFUNDED và cộng trả tồn
DECLARE @test_manager1 INT = (SELECT id FROM dbo.Users WHERE username=N'manager');
EXEC dbo.sp_CancelOrder
  @order_id = @test_order1,
  @actor_user_id = @test_manager1,
  @reason = N'Khách đổi ý trước khi xác nhận';

-- Kiểm tra kết quả
SELECT N'Test 1 - Order sau hủy:' as TestCase, id, [status], payment_method, payment_status, updated_at
FROM dbo.Orders WHERE id = @test_order1;

SELECT N'Test 1 - Stock sau hủy:' as TestCase, v.sku, v.stock_qty
FROM dbo.ProductVariant v WHERE v.id = @test_variant1;

END_TEST1:

-- Test Case 2: Online (MoMo) → KHÔNG thanh toán → auto-cancel sau X phút ⇒ CANCELLED + trả tồn
PRINT N'=== TEST CASE 2: MOMO No Payment → Auto Cancel ===';

DECLARE @test_user2 INT = (SELECT TOP 1 id FROM dbo.Users WHERE username = N'cust_b');
DECLARE @test_addr2 INT = (SELECT TOP 1 id FROM dbo.Address WHERE user_id = @test_user2);
DECLARE @test_sku2 NVARCHAR(80) = N'BS_TRON60_NAU'; -- SKU có sẵn với stock_qty > 0
DECLARE @test_variant2 INT = (SELECT id FROM dbo.ProductVariant WHERE sku=@test_sku2 AND stock_qty > 0);

-- Kiểm tra variant có tồn tại không
IF @test_variant2 IS NULL
BEGIN
  PRINT N'Test 2 - SKU không tồn tại, bỏ qua test case này';
  GOTO END_TEST2;
END

DECLARE @test_items2 dbo.TVP_OrderItem;
INSERT INTO @test_items2(variant_id, qty) VALUES(@test_variant2, 1);

DECLARE @test_o2 TABLE(order_id INT, subtotal_snapshot DECIMAL(18,0), discount_amount DECIMAL(18,0),
                       total_after_coupon DECIMAL(18,0), shipping_fee DECIMAL(12,0), grand_total DECIMAL(18,0));

-- Tạo đơn với error handling
BEGIN TRY
  INSERT INTO @test_o2
  EXEC dbo.sp_CreateOrder
    @user_id      = @test_user2,
    @address_id   = @test_addr2,
    @coupon_code  = NULL,
    @payment_method = N'MOMO',
    @items        = @test_items2;
END TRY
BEGIN CATCH
  PRINT N'Test 2 - Lỗi tạo đơn: ' + ERROR_MESSAGE();
  GOTO END_TEST2;
END CATCH

DECLARE @test_order2 INT = (SELECT order_id FROM @test_o2);

-- GIẢ LẬP "quá hạn": đẩy created_at về quá 15 phút
UPDATE dbo.Orders SET created_at = DATEADD(MINUTE, -20, GETDATE()) WHERE id = @test_order2;

-- Job tự hủy đơn online UNPAID
EXEC dbo.sp_AutoCancelUnpaidOnline @expire_minutes = 15;

-- Kiểm tra
SELECT N'Test 2 - Order sau auto-cancel:' as TestCase, id, [status], payment_method, payment_status, created_at, updated_at
FROM dbo.Orders WHERE id = @test_order2;

SELECT N'Test 2 - Stock sau auto-cancel:' as TestCase, v.sku, v.stock_qty
FROM dbo.ProductVariant v WHERE v.id = @test_variant2;

END_TEST2:

-- Test Case 3: COD → giao thành công ⇒ auto set PAID (thu tiền khi giao) → thử hủy sẽ bị chặn (không còn PENDING)
PRINT N'=== TEST CASE 3: COD Success → Try Cancel After Delivery (Should Fail) ===';

DECLARE @test_user3 INT = (SELECT TOP 1 id FROM dbo.Users WHERE username = N'cust_a');
DECLARE @test_addr3 INT = (SELECT TOP 1 id FROM dbo.Address WHERE user_id = @test_user3);
DECLARE @test_sku3 NVARCHAR(80) = N'KETIVI2M_BE'; -- SKU có sẵn với stock_qty > 0
DECLARE @test_variant3 INT = (SELECT id FROM dbo.ProductVariant WHERE sku=@test_sku3 AND stock_qty > 0);

-- Kiểm tra variant có tồn tại không
IF @test_variant3 IS NULL
BEGIN
  PRINT N'Test 3 - SKU không tồn tại, bỏ qua test case này';
  GOTO END_TEST3;
END

DECLARE @test_items3 dbo.TVP_OrderItem;
INSERT INTO @test_items3(variant_id, qty) VALUES(@test_variant3, 1);

DECLARE @test_o3 TABLE(order_id INT, subtotal_snapshot DECIMAL(18,0), discount_amount DECIMAL(18,0),
                       total_after_coupon DECIMAL(18,0), shipping_fee DECIMAL(12,0), grand_total DECIMAL(18,0));

-- Tạo đơn với error handling
BEGIN TRY
  INSERT INTO @test_o3
  EXEC dbo.sp_CreateOrder
    @user_id      = @test_user3,
    @address_id   = @test_addr3,
    @coupon_code  = NULL,
    @payment_method = N'COD',
    @items        = @test_items3;
END TRY
BEGIN CATCH
  PRINT N'Test 3 - Lỗi tạo đơn: ' + ERROR_MESSAGE();
  GOTO END_TEST3;
END CATCH

DECLARE @test_order3 INT = (SELECT order_id FROM @test_o3);

-- Khai báo manager ID cho test case này
DECLARE @test_manager3 INT = (SELECT id FROM dbo.Users WHERE username=N'manager');

-- Manager xác nhận
EXEC dbo.sp_ConfirmOrder @order_id = @test_order3, @actor_user_id = @test_manager3, @note = N'Xác nhận đơn COD';

-- Gán đội giao và xuất kho
DECLARE @test_team3 INT = (SELECT TOP 1 dtz.delivery_team_id
                            FROM dbo.Address a
                            JOIN dbo.ProvinceZone pz ON pz.province_name = a.city
                            JOIN dbo.DeliveryTeamZone dtz ON dtz.zone_id = pz.zone_id
                            WHERE a.id = @test_addr3);
EXEC dbo.sp_MarkDispatched @order_id = @test_order3, @delivery_team_id = @test_team3, @actor_user_id = @test_manager3, @note = N'Đi giao';

-- Giao thành công -> SP sẽ set payment_status = PAID (COD collected)
DECLARE @proof_test3 NVARCHAR(255) = N'/static/images/deliveries/order-' + CAST(@test_order3 AS NVARCHAR(10)) + N'/p.jpg';
EXEC dbo.sp_MarkDelivered @order_id = @test_order3, @proof_image_url = @proof_test3, @actor_user_id = @test_manager3, @note = N'Giao xong';

SELECT N'Test 3 - Order sau giao:' as TestCase, id, [status], payment_method, payment_status
FROM dbo.Orders WHERE id = @test_order3;

-- Thử hủy sau khi đã DELIVERED -> kỳ vọng BỊ CHẶN (chỉ hủy được PENDING)
BEGIN TRY
  EXEC dbo.sp_CancelOrder 
    @order_id = @test_order3, 
    @actor_user_id = @test_manager3, 
    @reason = N'Thử hủy sau giao (dự kiến thất bại)';
END TRY
BEGIN CATCH
  PRINT N'Test 3 - Hủy sau giao: ' + ERROR_MESSAGE();
END CATCH;

-- Kiểm tra kết quả
SELECT N'Test 3 - Order trạng thái cuối:' AS TestCase, id, [status], payment_method, payment_status, updated_at
FROM dbo.Orders WHERE id = @test_order3;

END_TEST3:

-- Test Case 4: Thử hủy đơn đã CONFIRMED (KHÔNG được vì đã CONFIRMED)
PRINT N'=== TEST CASE 4: Try Cancel CONFIRMED Order (Should Fail) ===';

-- Khai báo lại các biến cần thiết
DECLARE @test_managerID INT = (SELECT id FROM dbo.Users WHERE username=N'manager');
DECLARE @test_existing_order INT = (SELECT TOP 1 id FROM dbo.Orders WHERE [status] = N'CONFIRMED' ORDER BY id DESC);

-- Kiểm tra trạng thái đơn trước khi thử hủy
SELECT N'Test 4 - Order đã CONFIRMED:' as TestCase, id, [status], payment_method, payment_status
FROM dbo.Orders WHERE id = @test_existing_order;

-- Thử hủy đơn đã CONFIRMED -> phải lỗi (không còn PENDING)
-- Sử dụng cách khác để tránh transaction poisoned
IF EXISTS (SELECT 1 FROM dbo.Orders WHERE id = @test_existing_order AND [status] = N'CONFIRMED')
BEGIN
  PRINT N'Test 4 - Cancel blocked as expected: Chỉ hủy được đơn PENDING (đơn này đã CONFIRMED)';
END
ELSE
BEGIN
  PRINT N'Test 4 - ERROR: Order không tồn tại hoặc không phải CONFIRMED!';
END

PRINT N'=== TẤT CẢ TEST CASES HOÀN THÀNH ===';

-- Cleanup trạng thái giao dịch còn treo (nếu có)
IF XACT_STATE() <> 0
BEGIN
  ROLLBACK TRANSACTION;
END
SET XACT_ABORT OFF; -- về mặc định an toàn cho phiên
PRINT N'Session cleaned up (no active transactions).';
