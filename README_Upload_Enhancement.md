# 📸 HƯỚNG DẪN CHỨC NĂNG UPLOAD ẢNH BÀN GIAO

## 🎯 Tổng quan
Đã cải thiện chức năng upload ảnh bàn giao từ việc nhập URL thành upload file trực tiếp, giúp delivery team dễ dàng chụp và upload ảnh ngay tại hiện trường.

## 🔧 Các thay đổi đã thực hiện

### 1. **DeliveryUpdateRequestDTO** - Hỗ trợ MultipartFile
- ✅ Thêm trường `MultipartFile proofImageFile` để upload file
- ✅ Giữ lại trường `String proofImageUrl` để tương thích với code cũ
- ✅ Import `org.springframework.web.multipart.MultipartFile`

### 2. **DeliveryFileUploadService** - Service xử lý upload
- ✅ Upload ảnh bàn giao: `uploadDeliveryProofImage()`
- ✅ Upload ảnh thu hồi: `uploadReturnProofImage()`
- ✅ Validate file: kích thước, định dạng (JPG, PNG, GIF, WebP)
- ✅ Tạo tên file unique với timestamp và UUID
- ✅ Tổ chức thư mục theo orderId: `/static/images/deliveries/order-{id}/`

### 3. **DeliveryServiceImpl** - Tích hợp upload service
- ✅ Cập nhật `confirmDelivery()` để xử lý upload ảnh bàn giao
- ✅ Cập nhật `processReturnPickup()` để xử lý upload ảnh thu hồi
- ✅ Xử lý lỗi upload và fallback về URL cũ
- ✅ Inject `DeliveryFileUploadService`

### 4. **UI Template** - Giao diện upload
- ✅ Thêm `enctype="multipart/form-data"` cho form
- ✅ Thêm input file cho upload ảnh bàn giao
- ✅ Thêm input file cho upload ảnh thu hồi
- ✅ Giữ lại input URL để tương thích
- ✅ Hiển thị hướng dẫn định dạng và kích thước file

### 5. **Cấu hình** - Properties
- ✅ `application-delivery-upload.properties`
- ✅ Cấu hình đường dẫn upload: `/static/images/deliveries`
- ✅ Giới hạn kích thước file: 5MB
- ✅ Cấu hình Spring Boot multipart

## 🚀 Cách sử dụng

### **Upload ảnh bàn giao:**
1. **Chọn file ảnh** từ thiết bị (điện thoại/máy tính)
2. **Hoặc nhập URL** ảnh (tương thích với code cũ)
3. **Nhập ghi chú** (tùy chọn)
4. **Click "XÁC NHẬN ĐÃ GIAO"**

### **Upload ảnh thu hồi:**
1. **Chọn phương thức hoàn tiền** (bắt buộc)
2. **Chọn file ảnh** thu hồi từ thiết bị
3. **Hoặc nhập URL** ảnh (tương thích)
4. **Nhập ghi chú** (tùy chọn)
5. **Click "XÁC NHẬN ĐÃ THU HỒI"**

## 📁 Cấu trúc thư mục upload

```
/static/images/deliveries/
├── order-123/
│   ├── 20241029_143022_a1b2c3d4.jpg  (ảnh bàn giao)
│   └── 20241029_143055_e5f6g7h8.jpg  (ảnh bàn giao khác)
├── return-123/
│   └── 20241029_144012_i9j0k1l2.jpg  (ảnh thu hồi)
└── order-124/
    └── 20241029_150030_m3n4o5p6.jpg
```

## 🔍 Validation và bảo mật

### **Validation file upload:**
- ✅ **Định dạng:** JPG, JPEG, PNG, GIF, WebP
- ✅ **Kích thước:** Tối đa 5MB
- ✅ **Tên file:** Không được null hoặc rỗng
- ✅ **Tên file unique:** Timestamp + UUID để tránh trùng lặp

### **Xử lý lỗi:**
- ✅ **File quá lớn:** "File quá lớn. Kích thước tối đa: 5MB"
- ✅ **Định dạng không hỗ trợ:** "Định dạng file không được hỗ trợ"
- ✅ **Lỗi upload:** "Lỗi upload ảnh: {chi tiết lỗi}"
- ✅ **Fallback:** Nếu upload lỗi, vẫn có thể dùng URL

## 🎨 UI/UX Improvements

### **Trước (URL input):**
```html
<input type="text" placeholder="/static/images/deliveries/order-2/00_order-2.jpg">
<div class="form-text">URL phải bắt đầu bằng /static/images/deliveries/</div>
```

### **Sau (File upload + URL fallback):**
```html
<input type="file" accept="image/*">
<div class="form-text">Chấp nhận: JPG, PNG, GIF, WebP. Kích thước tối đa: 5MB</div>

<input type="text" placeholder="/static/images/deliveries/order-2/00_order-2.jpg">
<div class="form-text">Hoặc nhập link ảnh (tùy chọn)</div>
```

## 🔄 Luồng xử lý

### **Upload ảnh bàn giao:**
1. User chọn file → `MultipartFile proofImageFile`
2. `DeliveryFileUploadService.uploadDeliveryProofImage()`
3. Validate file → Tạo thư mục → Upload file
4. Trả về URL: `/static/images/deliveries/order-{id}/{filename}`
5. Gọi `sp_MarkDelivered` với `proof_image_url`
6. Cập nhật Orders.status = DELIVERED, OrderDelivery.status = DONE

### **Upload ảnh thu hồi:**
1. User chọn file → `MultipartFile proofImageFile`
2. `DeliveryFileUploadService.uploadReturnProofImage()`
3. Validate file → Tạo thư mục → Upload file
4. Trả về URL: `/static/images/deliveries/return-{id}/{filename}`
5. Gọi `sp_ReturnOrder` với các tham số
6. Cập nhật OrderDelivery.proof_image_url và note

## 🧪 Test Cases

### **Test Case 1: Upload ảnh bàn giao thành công**
- Chọn file JPG 2MB → Upload thành công
- Kết quả: Ảnh lưu trong `/static/images/deliveries/order-{id}/`
- URL trả về: `/static/images/deliveries/order-{id}/{timestamp}_{uuid}.jpg`

### **Test Case 2: Upload file quá lớn**
- Chọn file PNG 8MB → Lỗi validation
- Thông báo: "File quá lớn. Kích thước tối đa: 5MB"

### **Test Case 3: Upload file không hỗ trợ**
- Chọn file PDF → Lỗi validation
- Thông báo: "Định dạng file không được hỗ trợ"

### **Test Case 4: Fallback URL**
- Không chọn file, nhập URL → Sử dụng URL
- Tương thích với code cũ

## 📱 Mobile-friendly

### **Tối ưu cho điện thoại:**
- ✅ Input file hỗ trợ camera trực tiếp
- ✅ Accept="image/*" mở camera/gallery
- ✅ Responsive design
- ✅ Touch-friendly buttons

### **Workflow trên mobile:**
1. **Chụp ảnh** trực tiếp từ camera
2. **Hoặc chọn** từ gallery
3. **Upload ngay** không cần chuyển file
4. **Xác nhận** giao hàng/thu hồi

## 🔧 Cấu hình Production

### **application.properties:**
```properties
# Upload configuration
app.upload.path=/var/www/static/images/deliveries
app.upload.max-size=5242880

# Spring Boot multipart
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true
```

### **Nginx configuration:**
```nginx
location /static/images/deliveries/ {
    alias /var/www/static/images/deliveries/;
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## ✅ Checklist hoàn thành

- [x] Cập nhật DeliveryUpdateRequestDTO với MultipartFile
- [x] Tạo DeliveryFileUploadService
- [x] Cập nhật DeliveryServiceImpl tích hợp upload
- [x] Cập nhật UI template với file input
- [x] Thêm validation file upload
- [x] Tạo cấu hình properties
- [x] Hỗ trợ fallback URL
- [x] Tối ưu cho mobile
- [x] Xử lý lỗi đầy đủ
- [x] Tổ chức thư mục theo orderId

## 🎉 Kết quả

**Chức năng upload ảnh bàn giao đã được cải thiện hoàn toàn:**
- ✅ **Dễ sử dụng:** Upload trực tiếp từ camera/gallery
- ✅ **An toàn:** Validation đầy đủ
- ✅ **Linh hoạt:** Hỗ trợ cả file upload và URL
- ✅ **Tương thích:** Không phá vỡ code cũ
- ✅ **Mobile-friendly:** Tối ưu cho điện thoại
- ✅ **Tổ chức tốt:** Thư mục theo orderId

**Delivery team giờ có thể chụp và upload ảnh ngay tại hiện trường! 📸🚚**
