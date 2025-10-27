package mocviet.service.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {
    
    // Use both locations for file serving compatibility
    private static final String SRC_DIR = "src/main/resources/static/images/";
    private static final String TARGET_DIR = "target/classes/static/images/";
    private static final String BANNER_DIR = "banners/";
    
    /**
     * Upload banner image with NN and key
     * Format: NN_<key>.<ext> (e.g., 12_aa.jpg)
     * @param file The image file
     * @param nn The 2-digit order number (e.g., "12")
     * @param key The title/key for the filename
     */
    public String uploadBannerImageWithKey(MultipartFile file, String nn, String key) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Create upload directories (both src and target for compatibility)
        Path srcBannerPath = Paths.get(SRC_DIR + BANNER_DIR);
        Path targetBannerPath = Paths.get(TARGET_DIR + BANNER_DIR);
        
        Files.createDirectories(srcBannerPath);
        Files.createDirectories(targetBannerPath);
        
        // Generate filename following the pattern: NN_key.ext
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        // Extract extension safely
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Default to .jpg if no extension found
        if (extension.isEmpty()) {
            extension = ".jpg";
        }
        
        // Create key-based filename
        String keySlug = key != null && !key.isEmpty() 
            ? sanitizeKey(key) 
            : UUID.randomUUID().toString().substring(0, 8);
        
        String filename = String.format("%s_%s%s", nn, keySlug, extension);
        
        // Save file to both locations
        Path srcFilePath = srcBannerPath.resolve(filename);
        Path targetFilePath = targetBannerPath.resolve(filename);
        
        Files.copy(file.getInputStream(), srcFilePath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return URL path
        return "/static/images/banners/" + filename;
    }
    
    
    /**
     * Sanitize key to be URL-friendly (lowercase, convert spaces to dashes)
     * Vietnamese characters are preserved
     */
    private String sanitizeKey(String key) {
        // Remove Vietnamese accents first
        String withoutAccents = removeVietnameseAccents(key);
        
        return withoutAccents.trim()
                  .toLowerCase()
                  .replaceAll("\\s+", "-")  // Convert spaces to dashes
                  .replaceAll("[^a-z0-9\\-]", "")  // Remove special chars except dashes
                  .replaceAll("-+", "-")  // Replace multiple dashes with single dash
                  .replaceAll("^-|-$", "");  // Remove leading/trailing dashes
    }
    
    /**
     * Remove Vietnamese accents/diacritics
     */
    private String removeVietnameseAccents(String str) {
        if (str == null) return "";
        
        return str
            .replace("à", "a").replace("á", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
            .replace("â", "a").replace("ầ", "a").replace("ấ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a")
            .replace("ă", "a").replace("ằ", "a").replace("ắ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a")
            .replace("è", "e").replace("é", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
            .replace("ê", "e").replace("ề", "e").replace("ế", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e")
            .replace("đ", "d")
            .replace("ì", "i").replace("í", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
            .replace("ò", "o").replace("ó", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
            .replace("ô", "o").replace("ồ", "o").replace("ố", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o")
            .replace("ơ", "o").replace("ờ", "o").replace("ớ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o")
            .replace("ù", "u").replace("ú", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
            .replace("ư", "u").replace("ừ", "u").replace("ứ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u")
            .replace("ỳ", "y").replace("ý", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
            .replace("À", "A").replace("Á", "A").replace("Ả", "A").replace("Ã", "A").replace("Ạ", "A")
            .replace("Â", "A").replace("Ầ", "A").replace("Ấ", "A").replace("Ẩ", "A").replace("Ẫ", "A").replace("Ậ", "A")
            .replace("Ă", "A").replace("Ằ", "A").replace("Ắ", "A").replace("Ẳ", "A").replace("Ẵ", "A").replace("Ặ", "A")
            .replace("È", "E").replace("É", "E").replace("Ẻ", "E").replace("Ẽ", "E").replace("Ẹ", "E")
            .replace("Ê", "E").replace("Ề", "E").replace("Ế", "E").replace("Ể", "E").replace("Ễ", "E").replace("Ệ", "E")
            .replace("Đ", "D")
            .replace("Ì", "I").replace("Í", "I").replace("Ỉ", "I").replace("Ĩ", "I").replace("Ị", "I")
            .replace("Ò", "O").replace("Ó", "O").replace("Ỏ", "O").replace("Õ", "O").replace("Ọ", "O")
            .replace("Ô", "O").replace("Ồ", "O").replace("Ố", "O").replace("Ổ", "O").replace("Ỗ", "O").replace("Ộ", "O")
            .replace("Ơ", "O").replace("Ờ", "O").replace("Ớ", "O").replace("Ở", "O").replace("Ỡ", "O").replace("Ợ", "O")
            .replace("Ù", "U").replace("Ú", "U").replace("Ủ", "U").replace("Ũ", "U").replace("Ụ", "U")
            .replace("Ư", "U").replace("Ừ", "U").replace("Ứ", "U").replace("Ử", "U").replace("Ữ", "U").replace("Ự", "U")
            .replace("Ỳ", "Y").replace("Ý", "Y").replace("Ỷ", "Y").replace("Ỹ", "Y").replace("Ỵ", "Y");
    }
    
    /**
     * Delete banner image from both locations
     */
    public void deleteBannerImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        try {
            // Extract filename from URL
            int lastIndex = imageUrl.lastIndexOf("/");
            if (lastIndex == -1) {
                return;
            }
            
            String filename = imageUrl.substring(lastIndex + 1);
            
            // Delete from both locations
            Path srcFilePath = Paths.get(SRC_DIR + BANNER_DIR + filename);
            if (Files.exists(srcFilePath)) {
                Files.delete(srcFilePath);
            }
            
            Path targetFilePath = Paths.get(TARGET_DIR + BANNER_DIR + filename);
            if (Files.exists(targetFilePath)) {
                Files.delete(targetFilePath);
            }
        } catch (IOException e) {
            // Log error but don't throw (silent fail for deletion)
            System.err.println("Failed to delete banner image: " + imageUrl);
        }
    }
    
    /**
     * Check if file is an image
     */
    public boolean isImage(MultipartFile file) {
        if (file == null) {
            return false;
        }
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    /**
     * Validate file size (max 10MB)
     */
    public boolean isValidSize(MultipartFile file) {
        if (file == null) {
            return false;
        }
        long maxSize = 10 * 1024 * 1024; // 10MB
        return file.getSize() <= maxSize;
    }
}

