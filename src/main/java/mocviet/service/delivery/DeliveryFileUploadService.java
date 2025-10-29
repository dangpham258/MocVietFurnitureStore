package mocviet.service.delivery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DeliveryFileUploadService {

    @Value("${app.upload.path:/static/images/deliveries}")
    private String uploadPath;

    @Value("${app.upload.max-size:5242880}") // 5MB default
    private long maxFileSize;

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * Upload ảnh bàn giao cho đơn hàng
     * @param file File ảnh cần upload
     * @param orderId ID đơn hàng
     * @return URL đường dẫn ảnh đã upload
     * @throws IOException nếu có lỗi khi upload
     */
    public String uploadDeliveryProofImage(MultipartFile file, Integer orderId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validate file
        validateFile(file);

        // Tạo thư mục theo orderId
        String orderFolder = "order-" + orderId;
        Path targetDir = Paths.get(uploadPath, orderFolder);
        
        // Tạo thư mục nếu chưa có
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // Upload file
        Path targetPath = targetDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL đường dẫn
        return "/static/images/deliveries/" + orderFolder + "/" + uniqueFilename;
    }

    /**
     * Upload ảnh thu hồi hàng
     * @param file File ảnh cần upload
     * @param orderId ID đơn hàng
     * @return URL đường dẫn ảnh đã upload
     * @throws IOException nếu có lỗi khi upload
     */
    public String uploadReturnProofImage(MultipartFile file, Integer orderId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validate file
        validateFile(file);

        // Tạo thư mục theo orderId
        String orderFolder = "return-" + orderId;
        Path targetDir = Paths.get(uploadPath, orderFolder);
        
        // Tạo thư mục nếu chưa có
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // Upload file
        Path targetPath = targetDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL đường dẫn
        return "/static/images/deliveries/" + orderFolder + "/" + uniqueFilename;
    }

    /**
     * Validate file upload
     */
    private void validateFile(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File quá lớn. Kích thước tối đa: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean isValidExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new IllegalArgumentException("Định dạng file không được hỗ trợ. Chỉ chấp nhận: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /**
     * Lấy extension của file
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
