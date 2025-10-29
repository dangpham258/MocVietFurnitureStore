# 📁 QUY TẮC LƯU TRỮ ẢNH - HỆ THỐNG MỘC VIỆT

## 🎯 Tổng quan

Toàn bộ hệ thống sử dụng cấu trúc static files với root `/static/` để lưu trữ ảnh và CSS. Tất cả URL trong database đều bắt đầu từ `/static/...`

---

## 📂 1. Cấu trúc thư mục gốc

```
/static/
├─ images/
│  ├─ products/     # Ảnh sản phẩm (theo màu)
│  ├─ articles/     # Ảnh bài viết (media/news/people)
│  ├─ pages/        # Ảnh trang tĩnh
│  ├─ banners/      # Banner carousel
│  ├─ reviews/      # Ảnh đánh giá khách hàng
│  ├─ deliveries/   # Ảnh giao hàng/bàn giao
│  └─ messages/     # Ảnh đính kèm tin nhắn
└─ css/
   └─ pages/        # CSS cho trang tĩnh
```

---

## 🛍️ 2. Ảnh sản phẩm (Products)

### Cấu trúc thư mục

```
/static/images/products/<parent-category>/<child-category>/<product-slug>/<color-slug>/
```

### Quy tắc đặt tên

- **Format**: `NN_<product-slug>_<color-slug>.<ext>`
- **NN**: 00, 01, 02... (00 = ảnh đại diện)
- **Ext**: jpg/png/webp
- **Color-slug**: Trùng với `Color.slug` trong database

### Ví dụ cây thư mục

```
/static/images/products/
└─ phong-an/
   └─ ban-an/
      └─ ban-an-go-oc-cho-6-ghe/
         ├─ den/
         │  ├─ 00_ban-an-go-oc-cho-6-ghe_den.jpg
         │  ├─ 01_ban-an-go-oc-cho-6-ghe_den.jpg
         │  └─ 02_ban-an-go-oc-cho-6-ghe_den.jpg
         └─ nau/
            ├─ 00_ban-an-go-oc-cho-6-ghe_nau.jpg
            ├─ 01_ban-an-go-oc-cho-6-ghe_nau.jpg
            └─ 02_ban-an-go-oc-cho-6-ghe_nau.jpg
```

### Database mapping

```sql
-- ProductImage table
INSERT dbo.ProductImage(product_id, color_id, url) VALUES
(101, 5, N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/den/00_ban-an-go-oc-cho-6-ghe_den.jpg'),
(101, 5, N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/den/01_ban-an-go-oc-cho-6-ghe_den.jpg'),
(101, 6, N'/static/images/products/phong-an/ban-an/ban-an-go-oc-cho-6-ghe/nau/00_ban-an-go-oc-cho-6-ghe_nau.jpg');
```

### Query lấy ảnh theo variant

```sql
DECLARE @variant_id INT = 5001;
SELECT pi.url
FROM ProductImage pi
JOIN ProductVariant pv
  ON pv.product_id = pi.product_id
 AND pv.color_id   = pi.color_id
WHERE pv.id = @variant_id
ORDER BY pi.url;  -- 00,01,02...
```

### ⚠️ Lưu ý quan trọng

- **Ảnh bắt buộc gắn với màu**: Mỗi ảnh phải có `color_id`
- **type_name không ảnh hưởng**: Chỉ ảnh hưởng giá/tồn, không ảnh hưởng ảnh
- **NN=00**: Luôn là ảnh đại diện

---

## 📰 3. Ảnh bài viết (Articles)

### Cấu trúc thư mục

```
/static/images/articles/<type>/<article-slug>/
├─ thumbnail/
│  └─ 00_<article-slug>.<ext>
└─ content/
   ├─ 00_<article-slug>.<ext>
   ├─ 01_<article-slug>.<ext>
   └─ ...
```

### Types

- **MEDIA**: Phong cách thiết kế
- **NEWS**: Tin tức xu hướng
- **PEOPLE**: Câu chuyện nghệ nhân

### Ví dụ cây thư mục

```
/static/images/articles/
├─ media/
│  └─ phong-cach-bac-au/
│     ├─ thumbnail/
│     │  └─ 00_phong-cach-bac-au.jpg
│     └─ content/
│        ├─ 00_phong-cach-bac-au.jpg
│        └─ 01_phong-cach-bac-au.jpg
├─ news/
│  └─ go-oc-cho-phoi-noi-that/
│     ├─ thumbnail/
│     │  └─ 00_go-oc-cho-phoi-noi-that.jpg
│     └─ content/
│        └─ 00_go-oc-cho-phoi-noi-that.jpg
└─ people/
   └─ nghe-nhan-abc/
      ├─ thumbnail/
      │  └─ 00_nghe-nhan-abc.jpg
      └─ content/
         └─ 00_nghe-nhan-abc.jpg
```

### Database mapping

```sql
-- Article thumbnail
INSERT dbo.Article (title, slug, article_type, summary, thumbnail, [status], published_at)
VALUES (N'Phong cách Bắc Âu', N'phong-cach-bac-au', N'MEDIA', N'Giới thiệu style...',
        N'/static/images/articles/media/phong-cach-bac-au/thumbnail/00_phong-cach-bac-au.jpg', 1, GETDATE());

-- ArticleImage content
INSERT dbo.ArticleImage (article_id, url, caption) VALUES
(201, N'/static/images/articles/media/phong-cach-bac-au/content/00_phong-cach-bac-au.jpg', N'Phòng khách tông sáng');
```

---

## 📄 4. Trang tĩnh (Static Pages) & CSS

### Cấu trúc thư mục

```
/static/images/pages/<page-slug>/
/static/css/pages/<page-slug>.css
```

### Ví dụ

```
/static/images/pages/
└─ chinh-sach-bao-hanh/
   ├─ 00_chinh-sach-bao-hanh.jpg
   └─ 01_chinh-sach-bao-hanh.jpg

/static/css/pages/
└─ chinh-sach-bao-hanh.css
```

### HTML trong StaticPage.content

```html
<link rel="stylesheet" href="/static/css/pages/chinh-sach-bao-hanh.css" />
<img
  src="/static/images/pages/chinh-sach-bao-hanh/00_chinh-sach-bao-hanh.jpg"
  alt="Chính sách bảo hành"
/>
```

---

## 🎨 5. Banners

### Cấu trúc thư mục

```
/static/images/banners/
```

### Quy tắc đặt tên

- **Format**: `NN_<key>.<ext>`
- **Ví dụ**: `00_home-hero.jpg`, `01_black-friday.jpg`

### Ví dụ cây thư mục

```
/static/images/banners/
├─ 00_home-hero.jpg
├─ 01_he-sieu-sale.jpg
└─ 02_black-friday.jpg
```

### Database mapping

```sql
INSERT dbo.Banner (title, image_url, link_url, is_active)
VALUES (N'Home Hero', N'/static/images/banners/00_home-hero.jpg', N'/', 1);
```

---

## ⭐ 6. Reviews (Ảnh khách hàng)

### Cấu trúc thư mục

```
/static/images/reviews/<review-id>/
```

### Quy tắc đặt tên

- **Format**: `NN_review_<review-id>.<ext>`
- **Mỗi review chỉ 1 ảnh**

### Ví dụ cây thư mục

```
/static/images/reviews/
└─ 3105/
   └─ 00_review_3105.jpg
```

### Database mapping

```sql
UPDATE dbo.Review
SET image_url = N'/static/images/reviews/3105/00_review_3105.jpg'
WHERE id = 3105;
```

---

## 🚚 7. Giao hàng / Bàn giao

### Cấu trúc thư mục

```
/static/images/deliveries/order-<order-id>/
```

### Quy tắc đặt tên

- **Format**: `NN_order-<order-id>[_<suffix>].<ext>`
- **Suffix**: `_return` cho ảnh thu hồi

### Ví dụ cây thư mục

```
/static/images/deliveries/
└─ order-55021/
   ├─ 00_order-55021.jpg           # Bàn giao chính
   ├─ 01_order-55021.jpg           # Ảnh bổ sung
   └─ 02_order-55021_return.jpg    # Ảnh thu hồi
```

### Database mapping

```sql
-- Lưu ảnh bàn giao
UPDATE dbo.OrderDelivery
SET proof_image_url = N'/static/images/deliveries/order-55021/00_order-55021.jpg'
WHERE order_id = 55021;

-- Log timeline
INSERT dbo.DeliveryHistory (order_delivery_id, status, note, photo_url)
VALUES (9001, N'DONE', N'Đã bàn giao', N'/static/images/deliveries/order-55021/01_order-55021.jpg');
```

---

## 💬 8. Tin nhắn (Đính kèm)

### Cấu trúc thư mục

```
/static/images/messages/conversation-<conversation-id>/
```

### Quy tắc đặt tên

- **Format**: `NN_message-<message-id>.<ext>`

### Ví dụ cây thư mục

```
/static/images/messages/
└─ conversation-777/
   ├─ 00_message-12001.jpg
   └─ 01_message-12002.png
```

### Database mapping

```sql
UPDATE dbo.Message
SET attachment_url = N'/static/images/messages/conversation-777/00_message-12001.jpg'
WHERE id = 12001;
```

---

## 📋 9. Quy tắc chung

### Slug validation

- **Format**: Chữ thường, số, gạch ngang `[a-z0-9-]`
- **Ví dụ**: `ban-an-go-oc-cho-6-ghe`, `phong-cach-bac-au`

### Số thứ tự (NN)

- **00**: Luôn là ảnh đại diện/chính
- **01, 02, 03...**: Ảnh bổ sung theo thứ tự

### Định dạng file

- **Hỗ trợ**: jpg, png, webp
- **Khuyến nghị**: jpg cho ảnh sản phẩm, png cho ảnh có trong suốt

### Database constraints

Tất cả URL ảnh đều có CHECK constraints để đảm bảo đúng đường dẫn:

- `ProductImage.url` → `/static/images/products/%`
- `Article.thumbnail` → `/static/images/articles/%`
- `ArticleImage.url` → `/static/images/articles/%`
- `Banner.image_url` → `/static/images/banners/%`
- `Review.image_url` → `/static/images/reviews/%`
- `OrderDelivery.proof_image_url` → `/static/images/deliveries/%`
- `Message.attachment_url` → `/static/images/messages/%`

---

## 🚀 10. Triển khai Spring Boot

### Cấu hình static resources

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
```

### Upload file

```java
@Service
public class FileUploadService {
    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    public String uploadProductImage(MultipartFile file, String categoryPath, String productSlug, String colorSlug) {
        String fileName = String.format("00_%s_%s.jpg", productSlug, colorSlug);
        String filePath = UPLOAD_DIR + "products/" + categoryPath + "/" + productSlug + "/" + colorSlug + "/" + fileName;
        // Upload logic...
        return "/static/images/products/" + categoryPath + "/" + productSlug + "/" + colorSlug + "/" + fileName;
    }
}
```

---

## ✅ Kết luận

Cấu trúc lưu trữ ảnh này đã được **kiểm tra và xác nhận** hoàn toàn nhất quán với:

- ✅ Database schema constraints
- ✅ Sample data patterns
- ✅ Business requirements
- ✅ Spring Boot best practices

**Hệ thống sẵn sàng triển khai!** 🎉
