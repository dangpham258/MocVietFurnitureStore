**3.2. Biểu đồ Use case (Use case Diagram) - Quản lý tin tức Manager**

**3.2.1. Tạo bài viết mới**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ART-CreatePost                            |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Tạo bài viết mới                                   |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager tạo bài viết mới với nội dung,    |
|              | ảnh và thông tin đầy đủ                            |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, bài viết được tạo và lưu vào  |
| conditions** |     database                                        |
|              |                                                       |
|              | -   Nếu thất bại, không có bài viết nào được tạo,  |
|              |     hiển thị thông báo lỗi.                        |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tin tức"        |
| Flow**       |                                                       |
|              | 2. Manager nhấn nút "Tạo bài viết mới"            |
|              |                                                       |
|              | 3. Hệ thống hiển thị form tạo bài viết:           |
|              |     - Trường nhập tiêu đề                         |
|              |     - Dropdown chọn loại bài viết (MEDIA/NEWS/    |
|              |       PEOPLE)                                     |
|              |     - Trường nhập tóm tắt                         |
|              |     - Rich text editor nhập nội dung (TinyMCE)   |
|              |     - Upload ảnh thumbnail                        |
|              |     - Upload ảnh nội dung (nhiều ảnh)            |
|              |     - Chọn sản phẩm liên quan (tùy chọn)          |
|              |     - Checkbox "Bài viết nổi bật"                |
|              |     - Checkbox "Xuất bản ngay" (nếu không chọn   |
|              |       sẽ lưu nháp)                               |
|              |                                                       |
|              | 4. Manager nhập thông tin bài viết:              |
|              |     - Tiêu đề bài viết                           |
|              |     - Chọn loại bài viết                         |
|              |     - Viết tóm tắt ngắn gọn                     |
|              |     - Viết nội dung chi tiết với TinyMCE          |
|              |                                                       |
|              | 5. Manager upload ảnh:                          |
|              |     - Upload ảnh thumbnail (bắt buộc)            |
|              |     - Upload ảnh nội dung (tùy chọn)            |
|              |     - Hệ thống tự động tạo slug từ tiêu đề       |
|              |                                                       |
|              | 6. Manager nhấn "Tạo bài viết"                 |
|              |                                                       |
|              | 7. Hệ thống validate dữ liệu:                    |
|              |     - Kiểm tra tiêu đề không rỗng                |
|              |     - Kiểm tra slug duy nhất                     |
|              |     - Kiểm tra loại bài viết hợp lệ              |
|              |     - Kiểm tra ảnh thumbnail tồn tại             |
|              |     - Kiểm tra định dạng ảnh hợp lệ              |
|              |                                                       |
|              | 8. Hệ thống tạo slug tự động:                    |
|              |     - Chuyển tiêu đề thành slug                  |
|              |     - Kiểm tra slug không trùng                 |
|              |     - Thêm số thứ tự nếu trùng                  |
|              |                                                       |
|              | 9. Hệ thống lưu ảnh theo cấu trúc:              |
|              |     - Thumbnail: /static/images/articles/<type>/  |
|              |       <slug>/thumbnail/00_<slug>.jpg            |
|              |     - Content: /static/images/articles/<type>/    |
|              |       <slug>/content/00_<slug>.jpg              |
|              |                                                       |
|              | 10. Hệ thống lưu vào database:                 |
|              |     - INSERT vào bảng Article                   |
|              |     - INSERT vào bảng ArticleImage              |
|              |     - Cập nhật published_at nếu xuất bản      |
|              |     - Set author = username của Manager        |
|              |                                                       |
|              | 11. Hệ thống ghi log hoạt động                 |
|              |                                                       |
|              | 12. Hiển thị thông báo "Tạo bài viết thành công" |
|              |                                                       |
|              | 13. Redirect về trang quản lý tin tức          |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể lưu nháp để chỉnh sửa sau     |
| lternative** |                                                       |
|              | 2a. Manager có thể chọn sản phẩm liên quan       |
|              |                                                       |
|              | 3a. Manager có thể đặt bài viết nổi bật          |
|              |                                                       |
|              | 4a. Manager có thể upload nhiều ảnh nội dung     |
|              |                                                       |
|              | 5a. Manager có thể sử dụng TinyMCE editor       |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Tiêu đề bài viết rỗng                        |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Vui lòng nhập tiêu  |
|              |     đề bài viết"                                  |
|              |                                                       |
|              | 1d. Slug đã tồn tại                              |
|              |                                                       |
|              | 2d. Hệ thống tự động tạo slug mới                |
|              |                                                       |
|              | 1e. Ảnh thumbnail không hợp lệ                   |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Ảnh thumbnail      |
|              |     không hợp lệ"                                 |
|              |                                                       |
|              | 1f. Lỗi upload ảnh                              |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi upload ảnh"     |
|              |                                                       |
|              | 1g. Mất kết nối database                         |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.2. Chỉnh sửa bài viết**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ART-EditPost                               |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Chỉnh sửa bài viết                                |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager chỉnh sửa bài viết đã tạo        |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Bài viết tồn tại và có thể chỉnh sửa            |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, bài viết được cập nhật với     |
| conditions** |     thông tin mới                                  |
|              |                                                       |
|              | -   Nếu thất bại, bài viết không thay đổi, hiển    |
|              |     thị thông báo lỗi.                            |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tin tức"        |
| Flow**       |                                                       |
|              | 2. Manager chọn bài viết cần chỉnh sửa            |
|              |                                                       |
|              | 3. Manager nhấn nút "Chỉnh sửa"                  |
|              |                                                       |
|              | 4. Hệ thống kiểm tra quyền sở hữu bài viết       |
|              |                                                       |
|              | 5. Hệ thống hiển thị form chỉnh sửa với dữ liệu   |
|              |     hiện tại:                                      |
|              |     - Tiêu đề bài viết                           |
|              |     - Loại bài viết                             |
|              |     - Tóm tắt                                   |
|              |     - Nội dung (TinyMCE editor)                 |
|              |     - Ảnh thumbnail hiện tại                    |
|              |     - Danh sách ảnh nội dung hiện tại           |
|              |     - Sản phẩm liên quan                         |
|              |     - Trạng thái nổi bật                        |
|              |     - Trạng thái xuất bản                       |
|              |                                                       |
|              | 6. Manager chỉnh sửa thông tin:                 |
|              |     - Sửa tiêu đề (nếu cần)                     |
|              |     - Thay đổi loại bài viết (nếu cần)         |
|              |     - Cập nhật tóm tắt                          |
|              |     - Chỉnh sửa nội dung với TinyMCE            |
|              |                                                       |
|              | 7. Manager cập nhật ảnh:                        |
|              |     - Thay đổi ảnh thumbnail                     |
|              |     - Thêm/xóa ảnh nội dung                     |
|              |     - Checkbox "Xóa ảnh cũ" trước khi upload     |
|              |     - Cập nhật chú thích ảnh                    |
|              |                                                       |
|              | 8. Manager nhấn "Cập nhật bài viết"            |
|              |                                                       |
|              | 9. Hệ thống validate dữ liệu:                   |
|              |     - Kiểm tra tiêu đề không rỗng               |
|              |     - Kiểm tra slug duy nhất (nếu đổi tiêu đề)  |
|              |     - Kiểm tra loại bài viết hợp lệ             |
|              |     - Kiểm tra ảnh thumbnail tồn tại            |
|              |                                                       |
|              | 10. Hệ thống cập nhật slug nếu cần:             |
|              |     - Tạo slug mới từ tiêu đề mới               |
|              |     - Kiểm tra slug không trùng                 |
|              |                                                       |
|              | 11. Hệ thống cập nhật ảnh:                     |
|              |     - Lưu ảnh mới theo cấu trúc thư mục         |
|              |     - Xóa ảnh cũ nếu được chọn                  |
|              |     - Cập nhật URL trong database               |
|              |                                                       |
|              | 12. Hệ thống cập nhật database:                |
|              |     - UPDATE bảng Article                       |
|              |     - UPDATE/DELETE/INSERT bảng ArticleImage     |
|              |     - Cập nhật published_at nếu chuyển từ nháp  |
|              |                                                       |
|              | 13. Hệ thống ghi log hoạt động                 |
|              |                                                       |
|              | 14. Hiển thị thông báo "Cập nhật thành công"   |
|              |                                                       |
|              | 15. Redirect về trang chi tiết bài viết        |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem preview bài viết          |
| lternative** |                                                       |
|              | 2a. Manager có thể lưu nháp để chỉnh sửa sau    |
|              |                                                       |
|              | 3a. Manager có thể thay đổi trạng thái xuất bản   |
|              |                                                       |
|              | 4a. Manager có thể sử dụng TinyMCE editor        |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Bài viết không tồn tại                       |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Bài viết không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1d. Không có quyền chỉnh sửa bài viết           |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Không có quyền     |
|              |     chỉnh sửa bài viết này"                     |
|              |                                                       |
|              | 1e. Slug mới đã tồn tại                          |
|              |                                                       |
|              | 2e. Hệ thống tự động tạo slug mới                |
|              |                                                       |
|              | 1f. Lỗi cập nhật database                       |
|              |                                                       |
|              | 2f. Hệ thống hiện thông báo "Lỗi cập nhật dữ   |
|              |     liệu"                                         |
|              |                                                       |
|              | 1g. Mất kết nối database                         |
|              |                                                       |
|              | 2g. Hệ thống hiện thông báo "Lỗi hệ thống, vui   |
|              |     lòng thử lại"                                  |
+--------------+-------------------------------------------------------+

**3.2.3. Xem danh sách bài viết đã đăng**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ART-ListMyPosts                            |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem danh sách bài viết đã đăng                    |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem danh sách tất cả bài viết    |
|              | đã tạo và quản lý chúng                           |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị danh sách bài viết   |
| conditions** |     với đầy đủ thông tin                           |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tin tức"        |
| Flow**       |                                                       |
|              | 2. Hệ thống hiển thị dashboard tin tức:          |
|              |     - Tổng số bài viết                           |
|              |     - Số bài viết đã xuất bản                   |
|              |     - Số bài viết nháp                          |
|              |     - Tổng lượt xem                             |
|              |                                                       |
|              | 3. Hệ thống hiển thị danh sách bài viết:         |
|              |     - Tất cả bài viết đã tạo                    |
|              |     - Sắp xếp theo thời gian (mới nhất)          |
|              |                                                       |
|              | 4. Hệ thống hiển thị thông tin cho mỗi bài viết: |
|              |     - Tiêu đề bài viết                          |
|              |     - Loại bài viết (MEDIA/NEWS/PEOPLE)        |
|              |     - Tóm tắt                                   |
|              |     - Ảnh thumbnail                              |
|              |     - Trạng thái (Hiển thị/Nháp)               |
|              |     - Lượt xem                                  |
|              |     - Thời gian tạo                             |
|              |     - Thời gian xuất bản                        |
|              |     - Sản phẩm liên quan                        |
|              |                                                       |
|              | 5. Manager có thể:                              |
|              |     - Lọc theo loại bài viết                    |
|              |     - Lọc theo trạng thái (Xuất bản/Nháp)       |
|              |     - Tìm kiếm theo tiêu đề, tóm tắt            |
|              |     - Sắp xếp theo: Ngày tạo, Ngày xuất bản,    |
|              |       Lượt xem                                  |
|              |     - Phân trang                                |
|              |                                                       |
|              | 6. Manager có thể thực hiện hành động:          |
|              |     - Xem chi tiết bài viết                     |
|              |     - Chỉnh sửa bài viết                       |
|              |                                                       |
|              | 7. Manager có thể xem thống kê dashboard:     |
|              |     - Tổng số bài viết                         |
|              |     - Số bài viết đã xuất bản                 |
|              |     - Số bài viết nháp                         |
|              |     - Tổng lượt xem                           |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể xem chi tiết bài viết         |
| lternative** |                                                       |
|              | 2a. Manager có thể chỉnh sửa bài viết từ danh sách |
|              |                                                       |
|              | 3a. Manager có thể tạo bài viết mới từ danh sách |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Không có bài viết nào                       |
|              |                                                       |
|              | 2c. Hệ thống hiển thị "Chưa có bài viết nào"     |
|              |                                                       |
|              | 1d. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2d. Hệ thống hiển thị "Lỗi tải dữ liệu"          |
|              |                                                       |
|              | 1e. Lỗi phân trang                              |
|              |                                                       |
|              | 2e. Hệ thống hiển thị "Lỗi phân trang"           |
+--------------+-------------------------------------------------------+

**3.2.4. Xem chi tiết bài viết**

+--------------+-------------------------------------------------------+
| **Use case   | UC-MGR-ART-ViewDetail                            |
| ID**         |                                                       |
+==============+=======================================================+
| **Name**     | Xem chi tiết bài viết                            |
+--------------+-------------------------------------------------------+
| **Goal**     | Cho phép Manager xem chi tiết đầy đủ của bài viết |
|              | đã tạo                                            |
+--------------+-------------------------------------------------------+
| **Actors**   | Manager (chính)                                    |
+--------------+-------------------------------------------------------+
| **Pre-       | 1. Manager đã đăng nhập vào hệ thống               |
| conditions** | 2. Tài khoản Manager có role MANAGER                |
|              | 3. Bài viết tồn tại và thuộc về Manager           |
+--------------+-------------------------------------------------------+
| **Post-      | -   Nếu thành công, hiển thị chi tiết bài viết    |
| conditions** |     đầy đủ                                         |
|              |                                                       |
|              | -   Nếu thất bại, hiển thị thông báo lỗi.          |
+--------------+-------------------------------------------------------+
| **Main       | 1. Manager truy cập trang "Quản lý tin tức"        |
| Flow**       |                                                       |
|              | 2. Manager chọn bài viết cần xem chi tiết         |
|              |                                                       |
|              | 3. Manager nhấn nút "Xem chi tiết"               |
|              |                                                       |
|              | 4. Hệ thống kiểm tra quyền sở hữu bài viết       |
|              |                                                       |
|              | 5. Hệ thống hiển thị chi tiết bài viết:          |
|              |     - Tiêu đề bài viết                           |
|              |     - Loại bài viết                             |
|              |     - Tóm tắt                                   |
|              |     - Nội dung đầy đủ (HTML)                     |
|              |     - Ảnh thumbnail                              |
|              |     - Danh sách ảnh nội dung với chú thích      |
|              |     - Sản phẩm liên quan (nếu có)              |
|              |     - Trạng thái nổi bật                        |
|              |     - Trạng thái xuất bản                       |
|              |     - Metadata: Tác giả, Ngày tạo, Ngày xuất bản, |
|              |       Lượt xem                                  |
|              |                                                       |
|              | 6. Manager có thể thực hiện hành động:          |
|              |     - Chỉnh sửa bài viết                       |
|              |     - Quay lại danh sách                        |
+--------------+-------------------------------------------------------+
| **A          | 1a. Manager có thể chỉnh sửa bài viết từ trang    |
| lternative** |     chi tiết                                      |
|              |                                                       |
|              | 2a. Manager có thể quay lại danh sách bài viết    |
+--------------+-------------------------------------------------------+
| **Exception**| 1c. Bài viết không tồn tại                       |
|              |                                                       |
|              | 2c. Hệ thống hiện thông báo "Bài viết không     |
|              |     tồn tại"                                      |
|              |                                                       |
|              | 1d. Không có quyền xem bài viết                 |
|              |                                                       |
|              | 2d. Hệ thống hiện thông báo "Không có quyền     |
|              |     xem bài viết này"                            |
|              |                                                       |
|              | 1e. Lỗi truy vấn database                        |
|              |                                                       |
|              | 2e. Hệ thống hiện thông báo "Lỗi tải dữ liệu"   |
+--------------+-------------------------------------------------------+

**3.2.5. Chi tiết Validation và Bảo mật**

**Validation Rules:**
- **Tiêu đề bài viết:** Bắt buộc, tối đa 300 ký tự
- **Slug:** Tự động tạo từ tiêu đề, phải duy nhất
- **Loại bài viết:** MEDIA/NEWS/PEOPLE, kiểm tra CHECK constraint
- **Tóm tắt:** Tối đa 500 ký tự
- **Nội dung:** Không giới hạn, hỗ trợ HTML
- **Ảnh thumbnail:** Bắt buộc, định dạng jpg/png/webp, tối đa 2MB
- **Ảnh nội dung:** Tùy chọn, định dạng jpg/png/webp, tối đa 2MB mỗi ảnh
- **Sản phẩm liên quan:** Tùy chọn, phải tồn tại trong database

**Bảo mật:**
- **Permission Check:** Chỉ role MANAGER mới được quản lý tin tức
- **Audit Trail:** Ghi log mọi thay đổi bài viết
- **Data Validation:** Kiểm tra dữ liệu đầu vào nghiêm ngặt
- **File Upload Security:** Kiểm tra định dạng và kích thước file

**Tính năng đặc biệt:**
- **Auto Slug Generation:** Tự động tạo slug từ tiêu đề (Vietnamese-friendly)
- **Image Management:** Quản lý ảnh theo cấu trúc thư mục chuẩn
- **Rich Text Editor:** TinyMCE editor với đầy đủ tính năng
- **Ownership Control:** Manager chỉ quản lý bài viết của chính mình
- **SEO Optimization:** Slug thân thiện với SEO
- **Related Products:** Liên kết với sản phẩm liên quan
- **Featured Posts:** Đánh dấu bài viết nổi bật
- **View Tracking:** Theo dõi lượt xem bài viết
- **Draft System:** Hỗ trợ lưu nháp và xuất bản
- **Image Preview:** Xem trước ảnh trước khi upload
- **Bulk Image Upload:** Upload nhiều ảnh nội dung cùng lúc
- **Responsive Design:** Giao diện thân thiện trên mọi thiết bị

**Quy trình nghiệp vụ:**
1. **Tạo bài viết:** Manager tạo bài viết với nội dung và ảnh
2. **Lưu nháp:** Có thể lưu nháp để chỉnh sửa sau
3. **Xuất bản:** Bài viết được xuất bản và hiển thị trên website
4. **Quản lý:** Chỉnh sửa bài viết theo nhu cầu
5. **Thống kê:** Theo dõi lượt xem và hiệu suất bài viết

**Cấu trúc lưu trữ ảnh:**
- **Thumbnail:** `/static/images/articles/<type>/<slug>/thumbnail/00_<slug>.jpg`
- **Content Images:** `/static/images/articles/<type>/<slug>/content/00_<slug>.jpg`
- **Types:** MEDIA (phong cách), NEWS (tin tức), PEOPLE (nghệ nhân)

**Tiêu chí chấp nhận:**
1. Manager có thể tạo bài viết mới với đầy đủ thông tin
2. Manager có thể chỉnh sửa bài viết đã tạo
3. Manager có thể xem danh sách tất cả bài viết đã tạo
4. Manager có thể xem chi tiết bài viết đã tạo
5. Hệ thống tự động tạo slug từ tiêu đề bài viết (Vietnamese-friendly)
6. Ảnh được lưu theo cấu trúc thư mục chuẩn
7. Validation đầy đủ dữ liệu đầu vào
8. TinyMCE rich text editor hoạt động đầy đủ
9. Hỗ trợ upload nhiều ảnh nội dung với preview
10. Dashboard hiển thị thống kê tin tức trực quan
11. Tìm kiếm và lọc bài viết theo nhiều tiêu chí
12. Liên kết với sản phẩm liên quan
13. Đánh dấu bài viết nổi bật
14. Theo dõi lượt xem bài viết
15. Responsive design cho mọi thiết bị
16. Error handling robust với fallback values
17. Audit trail đầy đủ cho compliance
18. Performance optimization cho large dataset
19. SEO-friendly URL structure
20. Manager chỉ có thể quản lý bài viết của chính mình
21. Hỗ trợ lưu nháp và xuất bản
22. Kiểm tra quyền sở hữu nghiêm ngặt
23. Upload ảnh với validation định dạng và kích thước
24. Phân trang và sắp xếp linh hoạt